package com.ecom.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ecom.model.LocalUser;
import com.ecom.model.dao.LocalUserDao;
import com.ecom.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDao localUserDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
        String token = tokenHeader.substring(7);
        try {

            String userName = jwtService.getUserName(token);
            Optional<LocalUser> opUser = localUserDao.findByUserNameIgnoreCase(userName);

            if(opUser.isPresent()){
                LocalUser user = opUser.get();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }


            }catch (JWTDecodeException ex){
                ex.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
