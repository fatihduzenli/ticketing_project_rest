package com.cydeo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TicketingProjectMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingProjectMvcApplication.class, args);
    }
// Here we are trying to add bean in the container through @Bean annotation
    @Bean
    public ModelMapper mapper(){return new ModelMapper();}

    @Bean
    public PasswordEncoder passwordEncoder(){ // this makes our password encoded
        // PasswordEncode an interface, However, when we create bean from method we need to return object
        // BCryptPasswordEncoder is one of the implementation class of the PasswordEncoder
        return new BCryptPasswordEncoder();
    }

}
