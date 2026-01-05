package com.brokerhub.brokerageapp.config;

import com.brokerhub.brokerageapp.security.JwtAuthenticationEntryPoint;
import com.brokerhub.brokerageapp.security.JwtAuthenticationFilter;
import com.brokerhub.brokerageapp.service.BrokerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // Public backend APIs
                        .requestMatchers("/BrokerHub/Broker/createBroker").permitAll()
                        .requestMatchers("/BrokerHub/Broker/login").permitAll()
                        .requestMatchers("/BrokerHub/Broker/createPassword").permitAll()
                        .requestMatchers("/BrokerHub/Broker/verify-account").permitAll()
                        .requestMatchers("/BrokerHub/Broker/forgotPassword").permitAll()
                        .requestMatchers("/BrokerHub/Broker/BrokerFirmNameExists/**").permitAll()
                        .requestMatchers("/BrokerHub/Broker/UserNameExists/**").permitAll()
                        .requestMatchers("/BrokerHub/user/createUser").permitAll()
                        .requestMatchers("/api/network-test/**").permitAll()
                        .requestMatchers("/api/plans").permitAll()
                        .requestMatchers("/BrokerHub/api/plans").permitAll()
                        .requestMatchers("/login-test.html").permitAll()
                        .requestMatchers("/network-test.html").permitAll()
                        // React frontend routes (permit all)
                        .requestMatchers(
                                "/login",
                                "/dashboard",
                                "/profile",
                                "/settings/**",
                                "/",                  // root
                                "/index.html",
                                "/static/**",
                                "/favicon.ico",
                                "/manifest.json"
                        ).permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new BrokerDetailService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
