package com.feathersoft.trainingproject.OnlineTrainTicketBooking.filter;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.TokenRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.securityConfiguration.UserInfoUserDetailsService;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.file.AccessDeniedException;


public class JwtAuthFilter extends OncePerRequestFilter {

    private HandlerExceptionResolver exceptionResolver;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    public JwtAuthFilter(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);

            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean isTokenValid= tokenRepository.findByToken(token).map(t->!t.isExpired() && !t.isRevoked()).orElse(false);

                if (jwtService.validateToken(token, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
            filterChain.doFilter(request, response);


        }catch (Exception ex){
            exceptionResolver.resolveException(request,response,null,ex);
        }
    }
}
