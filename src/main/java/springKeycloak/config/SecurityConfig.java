package springKeycloak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(
        securedEnabled = true, // For @Secured
        prePostEnabled = true  // For @PreAuthorize and @PostAuthorize
)
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer((auth->{
                    auth.jwt(jwt->{
                        jwt.jwtAuthenticationConverter(jwtAuthConverter);
                    });
                }))
                .exceptionHandling((exception->{
                    exception.accessDeniedHandler(new AccessDeniedHandler());
                }))
//                .addFilterAfter(new CustomFilter(), BearerTokenAuthenticationFilter.class)
                .build();
    }

}
