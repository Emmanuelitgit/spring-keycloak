package springKeycloak.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @auther Emmanuel Yidana
 * @description configurations for swagger implementation
 * @date 04-02-2025.
 */
@Configuration
public class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().
//                        addList("Bearer Authentication"))
//                .components(new Components().addSecuritySchemes
//                        ("Bearer Authentication", createAPIKeyScheme()))
//                .info(new Info().title("Functional Based Permission")
//                        .description("Spring Integration API.")
//                        .version("1.0").contact(new Contact().name("Code With Manuel Dev")
//                                .email( "www.manueldev.com").url("eyidana001@gmail.com"))
//                        .license(new License().name("License of API")
//                                .url("API license URL")));
//    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("keycloak", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl("http://localhost:8080/realms/TestRealm/protocol/openid-connect/token")
                                                .scopes(new Scopes().addString("openid", "OpenID Connect scope"))
                                        )
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                .info(new Info().title("My API").version("1.0"));
    }
}