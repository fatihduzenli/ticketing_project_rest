package com.cydeo.service.impl;

import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrinciple;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {
    private final UserRepository userRepository;
    // we are injecting UserRepository to retrieve data from DB

    public SecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //get the user from db,and convert to user springs understands by using userPrincipal

        User user = userRepository.findByUserNameAndIsDeleted(username,false);

        if (user==null) throw new UsernameNotFoundException(username);
        return new UserPrinciple(user); // this line converts User (Entity) to UserDetails by using UserPrinciple
        // we need to return UserDetail, but UserDetail is an interface, so we use polymorphism, and
        // we retun UserPrinciple class that implements UserDetail. We create constructor in the
        // userPrinciple class, so we can pass our user inside and return.
    }
}
