package com.oauth.implementation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.oauth.implementation.dao.ProductsRepository;
import com.oauth.implementation.dao.UserRepository;
import com.oauth.implementation.dto.ProductDto;
import com.oauth.implementation.model.Product;

import jakarta.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/")
public class DashboardController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductsRepository productRepository;

    @GetMapping
    public String displayDashboard(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication().getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) securityContext.getAuthentication().getPrincipal();
            model.addAttribute("userDetails", user.getAttribute("name") != null ? user.getAttribute("name")
                    : user.getAttribute("login"));
        } else {
            User user = (User) securityContext.getAuthentication().getPrincipal();
            com.oauth.implementation.model.User users = userRepo.findByEmail(user.getUsername());
            model.addAttribute("userDetails", users.getName());
           
        }
        List<Product> products = productRepository.findAll(); // Ottieni tutti i prodotti
        model.addAttribute("products", products);

        return "/dashboard/products";
    }

    @GetMapping("/products")
    public String showProductList(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = (User) securityContext.getAuthentication().getPrincipal();
        com.oauth.implementation.model.User currentUser = userRepo.findByEmail(user.getUsername());

        List<Product> products = productRepository.findByUser(currentUser);
        if (products.isEmpty()) {
            System.out.println("No products found for user: " + currentUser.getEmail());
            try {
                userRepo.resetSequence();
            } catch (Exception e) {
                System.err.println("Error resetting product sequence: " + e.getMessage());
                e.printStackTrace();
                // Gestisci l'errore, ad esempio aggiungendo un messaggio al model
            }
        } else {
            for (Product product : products) {
                System.out.println("Product: " + product.getName());
            }
        }
        model.addAttribute("products", products);

        return "/dashboard";
    }

    @GetMapping("/products/create")
    public String showCreateProductForm(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "/create";
    }

    @PostMapping("/products/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
    
        if (result.hasErrors()) {
            return "/create";
        }
    
        MultipartFile imageFile = productDto.getImageFile();
        String originalFilename = imageFile.getOriginalFilename();
        String fileName = new Date().getTime() + "_" + originalFilename;
        String uploadDir = "src/main/resources/static/images/";
    
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            InputStream inputStream = imageFile.getInputStream();
            Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Could not save uploaded file: " + fileName);
            e.printStackTrace();
        }
    
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = (User) securityContext.getAuthentication().getPrincipal();
        com.oauth.implementation.model.User currentUser = userRepo.findByEmail(user.getUsername());
    
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(fileName);
        product.setCreatedAt(new Date());
        product.setUser(currentUser);
    
        productRepository.save(product);
    
        return "redirect:/dashboard/products";
    }
    
    @GetMapping("/products/edit")
    public String showEditProductForm(Model model, @RequestParam int id) {
        Product product = productRepository.findById(id).get();
        model.addAttribute("product", product);
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setCategory(product.getCategory());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());

        model.addAttribute("productDto", productDto);
        

        return "edit";
    }

  @PostMapping("/products/edit")
public String updateProduct(
    Model model, 
    @RequestParam int id, 
    @Valid @ModelAttribute ProductDto productDto, 
    BindingResult result
) {System.out.println("ID ricevuto: " + id);
    try {
        System.out.println("ID ricevuto: " + id);
        // Retrieve the product by id
        Optional<Product> optionalProduct = productRepository.findById(id);
        
        if (!optionalProduct.isPresent()) {
            // Handle the case where product with given id is not found
            throw new IllegalArgumentException("Product with id " + id + " not found");
        }
        
        Product product = optionalProduct.get();
        model.addAttribute("product", product);
        
        if (result.hasErrors()) {
            System.out.println("ID ricevuto: " + id);
            return "products/edit";
        }
        
        if (!productDto.getImageFile().isEmpty()) {
            // delete old image
            String uploadDir = "src/main/resources/static/images/";
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
            
            try {
                Files.delete(oldImagePath);
            } catch (Exception e) {
                System.out.println("Exception deleting old image: " + e.getMessage());
            }
            
            // save new image file
            MultipartFile image = productDto.getImageFile();
            Date createdAt = new Date();
            String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
            
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Exception saving new image: " + e.getMessage());
            }
            
            product.setImageFileName(storageFileName);
        }
        
        // Update product details
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        
        productRepository.save(product);
        
    } catch (Exception e) {
        System.out.println("Exception updating product: " + e.getMessage());
        e.printStackTrace(); // Print stack trace for better debugging
        // You might want to return an error page or redirect to an error page here
        return "error";
    }

    return "redirect:/dashboard/products";
}
    

    @GetMapping("products/delete")
    public String deleteProduct(@RequestParam int id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        try {
            // Delete image file
            Path image = Paths.get("src/main/resources/static/images/" + product.getImageFileName());
            Files.delete(image);

            // Delete product
            productRepository.delete(product);

        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/dashboard/products";
    }
}
