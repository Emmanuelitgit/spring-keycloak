package springKeycloak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private static final String[] SWAGGER_ENDPOINTS = {
            "/", HttpMethod.GET.name(),
            "/actuator/**",
            "/swagger-ui/**",
            "/configuration/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/assets/**",
            "/static/**",
    };

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomFilter customFilter) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                            .anyRequest().authenticated();
                })
                .oauth2ResourceServer((auth->{
                    auth.jwt(jwt->{
                        jwt.jwtAuthenticationConverter(jwtAuthConverter);
                    });
                }))
                .exceptionHandling((exception->{
                    exception.accessDeniedHandler(new AccessDeniedHandler());
                }))
                .addFilterAfter(customFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

}