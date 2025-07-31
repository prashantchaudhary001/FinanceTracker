# Personal Finance Tracker

A comprehensive web application for managing personal finances built with Spring Boot and Thymeleaf.

## Features

- **Dashboard**: Overview of income, expenses, and balance
- **Transaction Management**: Add, edit, delete, and view transactions
- **Category Management**: Organize transactions by categories
- **Responsive Design**: Bootstrap-based UI that works on all devices
- **Data Validation**: Form validation with error messages
- **Flash Messages**: Success/error notifications

## Technologies Used

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL 8.0, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Build Tool**: Maven
- **Containerization**: Docker (optional)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0
- Git

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd personal-finance-tracker
   ```

2. **Setup MySQL Database**
   ```sql
   CREATE DATABASE finance_tracker;
   ```

3. **Configure Database**
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the Application**
   Open your browser and go to: http://localhost:8080

## Docker Setup (Optional)

1. **Build the application**
   ```bash
   mvn clean package
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## API Endpoints

- `GET /` - Dashboard
- `GET /transactions` - List all transactions
- `GET /transactions/add` - Add transaction form
- `POST /transactions/add` - Save new transaction
- `GET /transactions/edit/{id}` - Edit transaction form
- `POST /transactions/edit/{id}` - Update transaction
- `GET /transactions/delete/{id}` - Delete transaction
- `GET /categories` - List all categories
- `GET /categories/add` - Add category form
- `POST /categories/add` - Save new category
- `GET /categories/edit/{id}` - Edit category form
- `POST /categories/edit/{id}` - Update category
- `GET /categories/delete/{id}` - Delete category

## Project Structure

```
src/
├── main/
│   ├── java/com/financetracker/
│   │   ├── controller/          # Web controllers
│   │   ├── entity/              # JPA entities
│   │   ├── repository/          # Data repositories
│   │   ├── service/             # Business logic
│   │   └── PersonalFinanceTrackerApplication.java
│   └── resources/
│       ├── templates/           # Thymeleaf templates
│       └── application.properties
└── test/                        # Test files
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.