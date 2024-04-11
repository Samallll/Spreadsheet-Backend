package com.spreadsheet.spreadsheetcelloperation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Samal A",
                        email = "samalchokli@gmail.com"
                ),
                description = "Documentation for Spreadsheet Cell Operations",
                title = "OpenAPI Documentation - Spreadsheet",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local DEV",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApiConfig {
}
