package com.cydeo.mapper;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper mapper;


    public UserMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public User convertToUserEntity(UserDTO userDTO){

       return mapper.map(userDTO,User.class);
    }

    public UserDTO convertToUserDto(User entity){

        return mapper.map(entity,UserDTO.class);
    }


}
