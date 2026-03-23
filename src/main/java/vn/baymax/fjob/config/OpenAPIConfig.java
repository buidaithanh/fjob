package vn.baymax.fjob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI openAPI() {

                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(
                                                new Components()
                                                                .addSecuritySchemes(securitySchemeName,
                                                                                new SecurityScheme()
                                                                                                .name(securitySchemeName)
                                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                                .scheme("bearer")
                                                                                                .bearerFormat("JWT")))
                                .info(new Info()
                                                .title("FJob API Documentation")
                                                .version("1.0.0")
                                                .description("Comprehensive REST API for FJob - Job Finding and Recruitment Platform\n\n"
                                                                +
                                                                "This API provides endpoints for:\n" +
                                                                "- User authentication and authorization\n" +
                                                                "- Job posting and management\n" +
                                                                "- Job application tracking\n" +
                                                                "- Company profile management\n" +
                                                                "- Email notifications\n" +
                                                                "- File management\n\n" +
                                                                "**Default Credentials for Testing:**\n" +
                                                                "- Email: admin@gmail.com\n" +
                                                                "- Password: 123456")
                                                .contact(new Contact()
                                                                .name("Bùi Đại Thành")
                                                                .url("https://github.com/buidaithanh")
                                                                .email("thanh.11db@gmail.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")));
        }
}