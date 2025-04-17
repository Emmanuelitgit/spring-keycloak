package springKeycloak.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


/**
 * @auther Emmanuel Yidana
 * @description configurations for swagger implementation
 * @date 04-02-2025.
 */
@Configuration
public class SwaggerConfig {

    private String KEYCLOAK_TOKEN_PATH = "http://localhost:8080/realms/TestRealm/protocol/openid-connect/token";


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                                .addSecuritySchemes("spring_oauth", new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2) //Specifying the kind of authentication to use i.e. OAUTH2
                                        .description("Oauth2 flow")
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow().tokenUrl(KEYCLOAK_TOKEN_PATH) //token generation url ie Keycloak in this case
                                                )))
                )
                .security(Arrays.asList(
                        new SecurityRequirement().addList("spring_oauth")));
    }
}