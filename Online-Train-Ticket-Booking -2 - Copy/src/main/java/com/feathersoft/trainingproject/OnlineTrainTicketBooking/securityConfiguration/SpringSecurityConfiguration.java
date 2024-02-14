package com.feathersoft.trainingproject.OnlineTrainTicketBooking.securityConfiguration;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.filter.JwtAuthFilter;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfiguration {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;


    @Bean
    //authentication
    public UserDetailsService userDetailsService(){

        return  new UserInfoUserDetailsService();
    }
    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return  new JwtAuthFilter(exceptionResolver);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer-> customizer
                                .requestMatchers("/user/login","/user/register-user","user/delete-account",
                                        "booking/cancel-booking","user/downloadProfileImage").permitAll()

                                .requestMatchers("user/delete-user/**","/user/get-user/**",
                                          "train/add-train","/train/update-train-details/**","/train/change-ticket-price","/train/check-available-seats","/train/delete-train/**",
                                          "/booking/display-all-bookings","/booking/display-all-booking-by-train/**","/booking/accept-booking/**","/booking/reject-booking/**",
                                          "/booking/cancel-booking-by-bookingId/**",
                                          "/payments/check-all-payments","/compartment/add-compartment",
                                          "booking/confirm-cancel-booking-status")
                                         .hasAuthority("ROLE_ADMIN")

                                .requestMatchers("/booking/book-ticket","/payments/make-payment/**","booking/display-all-booking-by-user","/booking/cancel-booking",
                                        "payments/check-payments-by-user", "invoice/download-invoice/**")
                                .hasAuthority("ROLE_USER")

                                .requestMatchers("user/my-info","/user/manage-profile","/user/change-password",
                                        "train/search-train"
                                       )
                                .hasAnyAuthority("ROLE_ADMIN","ROLE_USER")


                        .anyRequest().hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                )
                .sessionManagement(customizer->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                       .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/user/logout").addLogoutHandler(customLogoutHandler())
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return security.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){

        return  new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();

    }


    @Bean
    public LogoutHandler customLogoutHandler(){
        return (request, response, authentication) -> {
            String authHeader=request.getHeader("Authorization");
            String token;
            if(authHeader==null && !authHeader.startsWith("Bearer")){
                return;
            }
            token=authHeader.substring(7);
            var storedToken=tokenRepository.findByToken(token).orElse(null);

            if (storedToken!=null){
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
            }
            try {
                response.getWriter().write("LogOut successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        };
    }

}
