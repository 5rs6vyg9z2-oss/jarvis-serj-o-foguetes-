package br.com.meira.jarvis.controller;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CabecalhosSegurancaFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "microphone=(self), camera=(), geolocation=()");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; base-uri 'self'; object-src 'none'; frame-ancestors 'none'; "
                        + "form-action 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; "
                        + "img-src 'self' data:; media-src 'self' blob:; connect-src 'self'");

        filterChain.doFilter(request, response);
    }
}
