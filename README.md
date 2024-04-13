# Spreadsheet Backend

This project is a backend service for a spreadsheet application. It provides APIs to set and get values in cells, supporting various data types such as integers, strings, and others. The backend uses the Javaluator library for evaluating expressions, MySQL for storing data, and Postman for API testing. OpenAPI Swagger is used for documentation.

## Features

- `setValue(cellId, value)`: Sets the value in the specified cell.
- `getValue(cellId)`: Retrieves the value from the specified cell.

## Technologies and Tools Used

- Java
- Spring boot
- Javaluator
- MySQL
- Postman
- OpenAPI Swagger

## Handled Scenarios

- Circular Dependency:
    - Example: A1->A2->A3->A1
- Self Reference:
    - Example: A1 -> "=A1"
- Direct Value Assignment:
    - Examples:
        - A1 -> "12.0"
        - A2 -> "-12"
        - A3 -> "=12.0"
        - A5 -> "test"
        - A4 -> "test12"
- Expression with Reference:
    - Example: A1 -> "=A2+A3"
- Cell Reference:
    - Example: A1 -> "=A3"
- Expression with Parentheses:
    - Example: A1 -> "=A4-(A2+A3)"

## Failed Unit Tests

- `shouldEvaluateExpressionToGenerateFinalExpression`
- `shouldCreateDependencyListFromExpression`

## Limitations

- Designed for a single spreadsheet with multiple cells.
- Mathematical functions cannot be used when setting data in a cell.

## Setup

1. Clone the repository.
2. Compare and set up the `application-dev.yml` file based on `application-prod.yml`.
3. Set up the MySQL database.

## How to Use

1. Build and run the application.
2. Use Postman to test the APIs.
3. Use OpenAPI Swagger for documentation.

## Sample API Usage

### Setting a Value in a Cell

```http
POST /api/v1/cells/A1/value
Content-Type: application/json

"=A1+A2+A3"
```

### Getting a Value from a Cell

```http
GET /api/v1/cells/A1
```
# Contact Information

Feel free to contact me if you have any queries regarding the project.