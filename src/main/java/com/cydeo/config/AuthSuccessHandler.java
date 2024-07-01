package com.cydeo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
@Configuration
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    //This declares the AuthSuccessHandler class, which will handle actions upon successful authentication.
    //AuthenticationSuccessHandler interface: which defines a method to be called when a user has been successfully authenticated.

    @Override
    //This method will be executed when the user authentication is successful.
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      //HttpServletRequest:This parameter provides request information for HTTP servlets, such as request parameters, headers, and attributes.
        //HttpServletResponse response: This parameter allows the servlet to respond to the client with HTTP response data, including headers and content.
        // Authentication: This parameter holds the authentication details of the user, such as authorities and credentials.
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities()); // This converts the list of authorities (roles) from the Authentication object into a Set of String for easier manipulation and checks.

        if(roles.contains("Admin")){
            response.sendRedirect("/user/create");
        }

        if(roles.contains("Manager")){
            response.sendRedirect("/task/create");
        }

        if(roles.contains("Employee")){
            response.sendRedirect("/task/employee/pending-tasks");
        }

    }
}
