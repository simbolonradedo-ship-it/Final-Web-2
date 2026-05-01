package com.example.productcrud.config;

import com.example.productcrud.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
             .authorizeHttpRequests(auth -> auth
                 .requestMatchers("/auth/login", "/auth/register", "/auth/logout", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                 .anyRequest().authenticated()
             )
             .formLogin(form -> form
                 .loginPage("/auth/login")
                 .loginProcessingUrl("/auth/login")
                 .defaultSuccessUrl("/products", true)
                 .failureUrl("/auth/login?error=true")
                 .permitAll()
             )
             .logout(logout -> logout
                 .logoutUrl("/logout")
                 .logoutSuccessUrl("/auth/login?logout=true")
                 .invalidateHttpSession(true)
                 .deleteCookies("JSESSIONID")
                 .permitAll()
             )
             .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
             .headers(headers -> headers.frameOptions().sameOrigin());

         return http.build();
     }
}
