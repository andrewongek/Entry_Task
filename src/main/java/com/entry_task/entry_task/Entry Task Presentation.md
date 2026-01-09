## Assignment

This assignment is designed for developers to build a Spring Boot application for an E-commerce Book/Game/Virtual Product Store with two roles: Admin (adds products) and User (browses, adds to favorites, searches, and buys products).

## My Objectives:

1) Create the BE for an E-Commerce Store using Spring Boot  
2) Support 3 different user types. Customer, Seller and Admins

## Core Requirements:

### Sellers

1. Authentication  
   1. Register  
   2. Login  
   3. Logout  
2. Create Products to be sold to Customers  
   1. Set product name  
   2. Set price  
   3. Set stock  
   4. Set description  
   5. Able to select multiple categories that the product belongs to. (helps filtering)  
3. View **Own** Product  
   1. View All  
      1. Have Pagination/Filtering/Sorting  
   2. View one in detail  
      1. Has more details that may be important to the seller (e.g. c\_time, m\_time)  
4. Update Product Details  
   1. Able to **update Product** Details  
5. Delete Product  
   1. Deletes the product and removes ability from being sold/viewed.   
6. View Orders by Customers  
   1. Views the Order details of sold items

### Customers

1. Authentication  
   1. Register  
   2. Login  
   3. Logout  
2. View all **Available** Products.   
   1. View **all**   
      1. Have Pagination/Filtering/Sorting  
   2. View one in detail  
      1. Has more details but not all (e.g. don’t show c\_time, m\_time)  
3. Favourite List  
   1. View All Favourites  
      1. Have Pagination/Filtering/Sorting  
   2. Able to **Add product** to Favourite List  
   3. Able to **Remove products** from Favourite List  
   4. *When products are made **not Available**, should not be in favourites list*  
4. Cart  
   1. Able to add **Available** Product to Cart  
      1. Able to **specify quantity**  
   2. Able to update quantity of product from cart  
   3. Able to **remove cart item**  
   4. Able to **View** the cart items  
   5. Able to **Checkout** based on **current** Cart  
      1. Allow **selection of cart items** to checkout  
      2. Successful checkout should **only remove** selected items

\*Not going to handle payment

### Admin

1. Authentication  
   1. Register  
   2. Login  
   3. Logout  
2. View all **Non-Deleted** Products.   
   1. View **all**   
      1. Have Pagination/Filtering/Sorting  
   2. View specific seller products  
   3. View one in detail  
      1. Same details as the seller.  
3. Create Product for Seller   
4. Change Product Status  
   1. Set Inactive to Active  
   2. Set Active to Inactive  
      1. *Similar idea to being banned*  
5. Category  
   1. Create Categories for products  
   2. Delete Categories for products  
6. Orders  
    1. View **all** orders  
       1. Have Pagination/Filtering/Sorting

## Implementation

### Software Architecture

Why Monoliths

* **Lower complexity**: A single codebase avoids unnecessary concerns such as service discovery, API gateways, and inter-service communication.

* **Faster development and iteration**: Features can be implemented end-to-end without coordinating between multiple services.

* **Easier debugging and testing**: The entire application can be run and tested locally without distributed tooling.

#### Acknowledged Trade-offs

* **Limited scalability**: Individual modules cannot be scaled independently.

* **Tighter coupling risk**: Without careful design, domain logic can become interdependent.

* **Single deployment unit**: Any change requires redeploying the application.

### API Architecture

**Architecture context**

* Project is a **monolith**, with most interactions happening **in-process**

* Network-based service-to-service communication does **not** exist

| REST (HTTP/JSON)                                     | RPC (e.g. gRPC) |
|:-----------------------------------------------------| :---- |
| Simple to implement and understand                   | Adds build, versioning, and tooling complexity |
| Easy to test and demo (Swagger, curl, **Post**man)   | Harder to debug and demonstrate  |
| Well-suited for external APIs at the system boundary | Designed for network-based service-to-service communication |

**Conclusion**

* REST is the better fit for this project due to its simplicity and clarity

* RPC would add complexity without meaningful benefits

* RPC becomes relevant only if the system is later split into true microservices

### Authorisation Framework

* For this project i want to try to implement the core IAM featues to better understand so i will use **JWT with Spring Security** is the cleanest because it achieves stateless auth with minimal infrastructure and keeps the focus on the API and business logic.
* want to implement the access-token refresh-token flow to balance security and usability.


### APIs
#### Authentication APIs
1. **POST** `/api/auth/register`
   - Role: Public
   - Registers a new user (Customer, Seller) 
   - Logic:  
     - Validate input data
       - Username must be between 3 and 30 characters
       - Username and Email must be unique
       - Username must be alphanumeric without spaces
       - Password must be at least 6 characters
       - Email must be valid format
       - Role must be either "Customer" or "Seller"
     - Encrypt password  
     - Store user in database with role
   - Request Body: {username, email, password, role}  
   - Response: Success/Failure message

2. **POST** `/api/auth/admin/register`
   - Role: Admin
   - Registers a new admin user
   - Logic:  
     - Validate input data
         - Role can be any
     - Encrypt password  
     - Store admin user in database
   - Request Body: {username, email, password, role}  
   - Response: Success/Failure message

3. **POST** `/api/auth/login`
   - Role: Public
   - Authenticates user and returns JWT tokens
   - Logic:  
     - Validate credentials
       - springframework.security.authentication
     - Generate access and refresh tokens
       - jjwt library
       - Access token valid for 15 minutes
     - Store refresh token in database
   - Request Body: {username, password}  
   - Response: {accessToken, refreshToken}

4. **POST** `/api/auth/refresh`
   - Role: Public
   - Refreshes access token using refresh token
   - Logic:  
     - Validate refresh token
       - Check token signature and expiry
       - Verify token exists in database
     - Generate new access token and refresh token
       - Invalidate old refresh token
       - Store new refresh token in database
   - Request Body: {refreshToken}  
   - Response: {accessToken}

5. **POST** `/api/auth/logout`
   - Role: Authenticated User
   - Logs out user by invalidating refresh token
   - Logic:  
     - Extract user from request token
     - Invalidate refresh token in database
     - FE to handle token removal from storage
   - Request Body: {refreshToken}  
   - Response: Success/Failure message

#### Category APIs
1. **POST** `/api/categories`
   - Role: Admin
   - Creates a new product category
   - Logic:  
     - Validate category name uniqueness
     - Store category in database
   - Request Body: {name, description}  
   - Response: Success/Failure message

2. **DELETE** `/api/categories`
   - Role: Admin
   - Deletes a product category
   - Logic:  
     - Validate category exists
     - Remove category from database
   - Request Body: {categoryId}  
   - Response: Success/Failure message

3. **GET** `/api/categories`
   - Role: Authenticated User
   - Retrieves all product categories for FE
   - Logic:  
     - Fetch categories from database
   - Response: List of categories

#### Product APIs for Sellers
1. **POST** `/api/seller/products/search` 
   - Role: Seller
   - Retrieve a paginated list of products. Allows for sorting, filtering and searches
   - Logic:  
     - Extract seller from request token
     - Apply pagination, sorting, filtering based on query params
     - Fetch products from database
   - Request Body: {keyword (Only Product Name), pagination, filter, sort)
   - Response: Paginated list of products
     - id, name, seller_id, price, stock, description, status
2. **GET** `/api/seller/products/{productId}` 
   - Role: Seller
   - Retrieve detailed information about a specific product owned by the seller
   - Logic:  
     - Extract seller from request token
     - Validate product ownership
     - Fetch product details from database
   - Response: Product details
     - id, name, seller_id, price, stock, description, categories, status, c_time, m_time
3. **POST** `/api/seller/products` 
   - Role: Seller
   - Create a new product
   - Logic:  
     - Extract seller from request token
     - Validate input data
     - Store product in database
   - Request Body: {name, price, stock, description, categories}  
   - Response: Success/Failure message
4. **DELETE** `/api/seller/products/{productId}` 
   - Role: Seller
   - Delete a product owned by the seller
   - Logic:  
     - Extract seller from request token
     - Validate product ownership
     - Update product status to "Deleted" in database
   - Response: Success/Failure message
5. **PUT** `/api/seller/products/{productId}` 
   - Role: Seller
   - Update details of a product owned by the seller
   - Logic:  
     - Extract seller from request token
     - Validate product ownership
     - Validate input data
     - Update product details in database
   - Request Body: {name, price, stock, description, categories}  
   - Response: Success/Failure message

Product APIs for Customers
1. **POST** `/api/products/search` 
   - Role: Customer
   - Retrieve a paginated list of available products. Allows for sorting, filtering and searches
   - Logic:  
     - Apply pagination, sorting, filtering based on query params
     - Fetch products with status "Active" from database
   - Request Body: {keyword (Product Name, Description), pagination, filter, sort)
   - Response: Paginated list of products
     - id, name, seller_id, price, stock, description, status
2. **POST** `/api/{sellerId}/search` 
   - Role: Customer
   - Retrieve a paginated list of available products from a specific seller. Allows for sorting, filtering and searches
   - Logic:  
     - Apply pagination, sorting, filtering based on query params
     - Fetch products with status "Active" from specified seller from database
   - Request Body: {keyword (Product Name, Description), pagination, filter, sort)
   - Response: Paginated list of products
     - id, name, seller_id, price, stock, description, status
3. **GET** `/api/products/{productId}` 
   - Role: Customer
   - Retrieve detailed information about a specific available product
   - Logic:  
     - Validate product status is "Active"
     - Fetch product details from database
   - Response: Product details
     - id, name, seller_id, price, stock, description, status

#### Admin Product APIs
1. **POST** `/api/admin/products/search` 
   - Role: Admin
   - Retrieve a paginated list of all products. Allows for sorting, filtering and searches
   - Logic:
     - Apply pagination, sorting, filtering based on query params
     - Fetch all products from database
    - Request Body: {keyword (Product Name), pagination, filter, sort)
    - Response: Paginated list of products
      - id, name, seller_id, price, stock, description, status
2. **POST** `/api/admin/{sellerId}/products/search`
   - Role: Admin
   - Retrieve a paginated list of products from a specific seller. Allows for sorting, filtering and searches
   - Logic:
     - Apply pagination, sorting, filtering based on query params
     - Fetch products from specified seller from database
    - Request Body: {keyword (Product Name), pagination, filter, sort)
    - Response: Paginated list of products
      - id, name, seller_id, price, stock, description, status
3. **GET** `/api/admin/products/{productId}` 
   - Role: Admin
   - Retrieve detailed information about a specific product
   - Logic:  
     - Fetch product details from database
   - Response: Product details
     - id, name, seller_id, price, stock, description, categories, status, c_time, m_time
4. **POST** `/api/admin/products` 
   - Role: Admin
   - Create a new product for a seller
   - Logic:  
     - Validate input data
     - Store product in database
   - Request Body: {name, price, stock, description, categories, sellerId}  
   - Response: Success/Failure message
5. **PUT** `/api/admin/products/{productId}/activate`
   - Role: Admin
   - Set product status to "Active"
   - Logic:  
     - Validate product exists
     - Update product status to "Active" in database
   - Response: Success/Failure message
6. **PUT** `/api/admin/products/{productId}/deactivate`
   - Role: Admin
   - Set product status to "Inactive"
   - Logic:  
     - Validate product exists
     - Update product status to "Inactive" in database
   - Response: Success/Failure message

#### Favourite APIs
1. **POST** `/api/favourites/search`
    - Role: Customer
    - Retrieve a paginated list of favourite products. Allows for sorting and filtering
    - Logic:  
      - Extract customer from request token
      - Apply pagination, sorting, filtering based on query params
      - Fetch favourite products from database
    - Request Body: {pagination, filter, sort)
    - Response: Paginated list of favourite products
      - id, name, seller_id, price, stock
2. **POST** `/api/favourites/{productId}`
   - Role: Customer
   - Add a product to favourite list
   - Logic:  
     - Extract customer from request token
     - Validate product status is "Active"
     - Validate product not already in favourite list
     - Add product to favourite list in database
   - Response: Success/Failure message
3. **DELETE** `/api/favourites/{productId}`
   - Role: Customer
   - Remove a product from favourite list
   - Logic:  
     - Extract customer from request token
     - Validate product exists in favourite list
     - Remove product from favourite list in database
   - Response: Success/Failure message

#### Cart APIs
1. **GET** `/api/cart`
    - Role: Customer
    - Retrieves the current contents of the user's shopping cart, including items, total quantity, and total price
    - Logic:  
      - Extract customer from request token
      - Fetch cart items from database
      - Calculate total quantity and price
      - Return cart details
      - Response: {totalItems, totalQuantity, totalPrice, items: [{cartItemId, quantity, ProductListing {id, name, selleid, stock, price}, subTotalPrice}], updatedAt}
2. **PUT** `/api/cart/`
   - Role: Customer
   - Adds a product to the user's shopping cart or updates the quantity if it already exists
   - Logic:  
     - Extract customer from request token
     - Validate product status is "Active"
     - Validate requested quantity does not exceed stock
     - If product already in cart, update quantity; else add new cart item
     - If product quantity is set to 0, remove item from cart
     - Save changes to database
   - Request Body: {productid, quantity}  
   - Response: Success/Failure message

#### Customer Order APIs
1. **POST** `/api/orders/checkout`
   - Role: Customer
   - Creates an order based on selected cart items
   - Logic:  
     - Extract customer from request token
     - Validate selected cart items exist and belong to customer
     - Create Order with Idempotency Key to prevent duplicate orders
     - Validate product stock for each cart item
     - Create order and order items in database and
     - Deduct stock from products
     - Remove purchased items from cart
   - Request Body: {cartItemIds: []}, idempotencyKey
   - Response: Success/Failure message with order details
2. **POST** `/api/orders/search`
   - Role: Customer
   - Retrieve a paginated list of customer's orders. Allows for sorting and filtering
   - Logic:  
     - Extract custo    mer from request token
     - Apply pagination, sorting, filtering based on query params
     - Fetch orders from database
   - Request Body: {pagination, filter, sort)
   - Response: Paginated list of orders
     - orderid, totalItems, totalquantity, totalPrice, orderItems[{productid, name, quantity, price, totalsubprice}], status, ctime, mtime

#### Seller Order APIs
1. **POST** `/api/seller/orders/search`
    - Role: Seller
    - Retrieve a paginated list of orders for products sold by the seller. Allows for sorting and filtering
    - Logic:
    - Extract seller from request token
      - Apply pagination, sorting, filtering based on query params
      - Fetch orders containing seller's products from database
      - Request Body: {pagination, filter, sort)
      - Response: Paginated list of orders
        - orderItemId, orderid, buyerid, productid, productname, quantity, price, totalPrice, status, ctime, mtime
#### Admin Order APIs
1. **POST** `/api/admin/orders/search`
    - Role: Admin
    - Retrieve a paginated list of all orders. Allows for sorting and filtering
    - Logic:
      - Apply pagination, sorting, filtering based on query params
      - Fetch all orders from database
    - Request Body: {pagination, filter, sort)
    - Response: Paginated list of orders
        - orderItemId, orderid, buyerid, productid, productname, quantity, price, totalPrice, status, ctime, mtime

### Database Design

![img.png](databaseDiagram.png)
