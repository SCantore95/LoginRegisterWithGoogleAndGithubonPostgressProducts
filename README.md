Description:
This application provides users with the ability to register, log in, and manage products through a web interface. Users can sign up using traditional email/password authentication or via third-party OAuth providers such as Google and GitHub. The application utilizes a PostgreSQL database for data storage and supports full CRUD (Create, Read, Update, Delete) operations for managing products.

Key Features:

User Registration and Authentication:

Email/Password Registration: Users can create an account by providing their email and a password. The application stores the user credentials securely using the BCrypt hashing algorithm.
OAuth Authentication: Users have the option to log in using their Google or GitHub accounts. This is facilitated through the OAuth 2.0 protocol, ensuring a secure and seamless login experience.
User Login:

Email/Password Login: Registered users can log in by entering their email and password.
OAuth Login: Users can authenticate using their Google or GitHub accounts.
Product Management:

Create Product: Users can add new products by providing details such as name, brand, category, price, and uploading an image.
Read Product: Users can view a list of all products, along with details such as ID, name, brand, category, price, and the date the product was created. Each product also displays its associated image.
Update Product: Users can edit the details of existing products.
Delete Product: Users can delete products, with a confirmation prompt to prevent accidental deletions.
User Interface:

Navigation Bar: The application features a navigation bar that displays a welcome message to the logged-in user and provides a logout button.
Responsive Design: The application is designed to be responsive, ensuring a seamless user experience across different devices.
Error Handling:

The application includes error handling mechanisms to manage common issues such as 404 (Not Found) errors. This ensures that users receive informative feedback when something goes wrong.
Technical Details:

Backend:

Spring Boot: The application is built using the Spring Boot framework, which provides a robust and scalable backend.
Spring Security: Handles authentication and authorization, including the integration of OAuth providers.
PostgreSQL: Serves as the relational database management system (DBMS) for storing user and product data.
Frontend:

Thymeleaf: Used for server-side rendering of HTML templates.
Bootstrap: Ensures a responsive and visually appealing design.
Database Schema:

Users Table: Stores user information including  id,name, email, password (hashed), and roles.

Products Table: Stores product details such as ID, name, brand, category, price, image file name,creation date,price.description,and user_id
Image Handling:

Static Resources: Product images are stored in a static directory accessible by the web server.
