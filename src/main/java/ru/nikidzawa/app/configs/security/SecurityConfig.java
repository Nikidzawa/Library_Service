package ru.nikidzawa.app.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.nikidzawa.app.responses.exceptions.CustomAccessDeniedHandler;
import ru.nikidzawa.app.responses.exceptions.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService () {
        return new MyUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(httpBasic -> {})
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint());
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler());
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/readers/registration", "swagger-ui/**", "v3/api-docs/**").permitAll()
                        .requestMatchers("api/**").authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::disable)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider () {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder () {return new BCryptPasswordEncoder();}

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {return new CustomAuthenticationEntryPoint();}

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {return new CustomAccessDeniedHandler();}
}
