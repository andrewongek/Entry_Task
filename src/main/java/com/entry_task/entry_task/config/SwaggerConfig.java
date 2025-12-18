package com.entry_task.entry_task.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Card Shop API")
                        .version("1.0")
                        .description("API documentation for the Card Shop project"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .tags(List.of(
                        new Tag().name("Auth").description("Operations related to authentication"),
                        new Tag().name("User Product").description("User operations related to products"),
                        new Tag().name("Seller Product").description("Seller operations related to products"),
                        new Tag().name("Admin Product").description("Admin operations related to products"),
                        new Tag().name("User Order").description("User operations related to orders"),
                        new Tag().name("Seller Order").description("Seller operations related to orders"),
                        new Tag().name("Admin Order").description("Admin operations related to orders"),
                        new Tag().name("Cart").description("Operations related to shopping cart"),
                        new Tag().name("Favourite").description("Operations related to favourites"),
                        new Tag().name("Category").description("Admin operations related to category")
                ));
    }
}