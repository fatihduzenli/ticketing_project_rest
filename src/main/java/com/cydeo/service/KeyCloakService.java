package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import javax.ws.rs.core.Response;

public interface KeyCloakService {

    Response userCreate(UserDTO userDTO);
    void delete(String username);
}
