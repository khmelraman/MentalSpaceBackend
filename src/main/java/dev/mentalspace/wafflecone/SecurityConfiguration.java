package dev.mentalspace.wafflecone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: enable CSRF when done
        // HttpSessionCsrfTokenRepository repository = new
        // HttpSessionCsrfTokenRepository();
        // repository.setParameterName("csrfToken");
        // http
        // .csrf()
        // .csrfTokenRepository(repository);
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v0/**")
                        .allowedOrigins("https://dev-waffle-cone.probablyanasian.dev",
                                "https://probablyanasian.stoplight.io/", "https://editor.swagger.io/")
                        .allowCredentials(true).allowedMethods("GET", "POST", "PATCH", "DELETE");
            }
        };
    }

}