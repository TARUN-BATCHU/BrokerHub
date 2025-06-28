package com.brokerhub.brokerageapp.config;

import com.brokerhub.brokerageapp.service.BrokerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/BrokerHub/Broker/createBroker").permitAll()
                        .requestMatchers("/BrokerHub/Broker/login").permitAll()
                        .requestMatchers("/BrokerHub/Broker/").permitAll()
                        .requestMatchers("/BrokerHub/user/createUser").permitAll()
                        .requestMatchers("/BrokerHub/user/bulkUpload").permitAll()
                        .requestMatchers("/BrokerHub/user/downloadTemplate").permitAll()
                        .requestMatchers("/BrokerHub/Dashboard/**").permitAll()
                        .requestMatchers("/BrokerHub/Broker/generateHash/**").permitAll()
                        .requestMatchers("/BrokerHub/Broker/resetAdminPassword/**").permitAll()
                        .requestMatchers("/BrokerHub/Broker/UserNameExists/**").permitAll()
                        .requestMatchers("/BrokerHub/Broker/BrokerFirmNameExists/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/BrokerHub/Broker/forgotPassword").permitAll()
                        .requestMatchers("/BrokerHub/Broker/verify-account").permitAll()
                        .requestMatchers("/BrokerHub/Broker/createPassword").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Only send 401 for API calls, not browser requests
                            String acceptHeader = request.getHeader("Accept");
                            if (acceptHeader != null && acceptHeader.contains("application/json")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/BrokerHub/Broker/brokerDashboard");
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new BrokerDetailService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        //DAO - data access object
        //for loading the users from database
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
