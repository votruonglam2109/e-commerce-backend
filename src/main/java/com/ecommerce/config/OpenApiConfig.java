package com.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Vo Truong Lam",
                        email = "votruonglam2109@gmail.com",
                        url = "https://github.com/lamvt03"
                ),
                title = "Ecommerce RESTful API documentation",
                description = "OpenApi documentation for Ecommerce RESTful API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "PROD ENV",
                        url = "http://ec2-54-179-75-46.ap-southeast-1.compute.amazonaws.com"
                ),
                @Server(
                        description = "LOCAL ENV",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = "bearAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
