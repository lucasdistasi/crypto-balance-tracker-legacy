package com.distasilucas.cryptobalancetracker.configuration;

import com.distasilucas.cryptobalancetracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String header = StringUtils.isBlank(authHeader) ? "" : authHeader.split(" ")[0];

        if (StringUtils.isBlank(authHeader) || isNotBearerToken(header)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.split(" ")[1];
        String userName = jwtService.extractUsername(jwtToken);

        if (StringUtils.isNotBlank(userName) && isNotAlreadyAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            if (Boolean.TRUE.equals(jwtService.isTokenValid(jwtToken, userDetails))) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);

                authenticationToken.setDetails(webAuthenticationDetails);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isNotBearerToken(String authHeader) {
        return !authHeader.equals("Bearer");
    }

    private boolean isNotAlreadyAuthenticated() {
        return null == SecurityContextHolder.getContext().getAuthentication();
    }
}
