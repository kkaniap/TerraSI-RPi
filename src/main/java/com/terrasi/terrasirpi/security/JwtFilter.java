package com.terrasi.terrasirpi.security;

import com.terrasi.terrasirpi.model.JwtModel;
import com.terrasi.terrasirpi.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JwtFilter extends BasicAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);
    
    public JwtFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {

        try {
            String header = request.getHeader("Authorization");
            if(header != null) {
                UsernamePasswordAuthenticationToken authResult = getAuthenticationByToken(header);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        }catch (SignatureException e){
            response.setContentType(request.getRemoteAddr() + " " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Enter valid token");
        }catch (ExpiredJwtException e){
            response.setContentType(request.getRemoteAddr() + " " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        }catch(Exception e) {
            response.setContentType(request.getRemoteAddr() + "" + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }

        try{
            chain.doFilter(request, response);
        }catch(Exception ex){
            logger.info(response.getContentType());
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticationByToken(String header){
        JwtModel model = JwtUtils.parseAccessToken(header);
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        model.getRoles().forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = Collections.unmodifiableSet(authorities);
        return new UsernamePasswordAuthenticationToken(model.getUsername(), null, simpleGrantedAuthorities);
    }
}
