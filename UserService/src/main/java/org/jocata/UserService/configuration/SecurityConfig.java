package org.jocata.UserService.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jocata.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

        @Autowired
        private UserService  userService;

        @Autowired
        private KafkaConfig kafkaConfig;

        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(userService);
            authenticationProvider.setPasswordEncoder(kafkaConfig.getPSEncode());
            return authenticationProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/user/getUser/**").hasAnyAuthority("ADMIN","USER","SERVICE")
                    .anyRequest().permitAll()
            ).formLogin(withDefaults()).httpBasic(withDefaults()).csrf(csrf -> csrf.disable());
            return http.build();
        }


}
