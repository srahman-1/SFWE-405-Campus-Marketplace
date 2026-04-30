package edu.sfwe405.campusmarketplace.config;

import edu.sfwe405.campusmarketplace.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/h2-console/**", "/error").permitAll()
                .requestMatchers("/", "/index.html", "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.png", "/**/*.jpg").permitAll()
                .requestMatchers("/ui/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/users").authenticated()
                .requestMatchers(HttpMethod.POST, "/products").authenticated()
                .requestMatchers(HttpMethod.GET, "/products/me").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/users/me").authenticated()
                .requestMatchers("/cart/**").authenticated()
                .requestMatchers("/orders/**", "/payments/**").authenticated()
                .anyRequest().permitAll()
            );

        return http.build();
    }

}
