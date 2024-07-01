package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,  ProjectService projectService, @Lazy TaskService taskService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        return userRepository.findAllByIsDeletedOrderByFirstNameDesc(false).stream().map(userMapper::convertToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        return userMapper.convertToUserDto(userRepository.findByUserNameAndIsDeleted(username,false));
    }

    @Override
    public void save(UserDTO user) {
        user.setEnabled(true);
        User obj = userMapper.convertToUserEntity(user);
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));

        userRepository.save(obj);

    }



    @Override
    public void deleteByUserName(String username) {
        User user2 = userRepository.findByUserNameAndIsDeleted(username,false);
        //  userRepository.deleteById(user2.getId()); we can delete with getting the id of user
        userRepository.deleteByUserName(username); // or with a method we created

    }

    @Override
    public UserDTO update(UserDTO user) {
        User user1 = userRepository.findByUserNameAndIsDeleted(user.getUserName(),false); // this is not for update purpose.We are getting this from DB, so we can set the id
        User convertedUser = userMapper.convertToUserEntity(user); // Whatever the changes happened on the view side, we are saving to DB layer by converting it
        convertedUser.setId(user1.getId());// here we are setting id to converted object
        userRepository.save(convertedUser);
        return findByUserName(user.getUserName());
    }

    //with the method below, we are going to delete user from the user side, but we will keep the data in db
    // Steps: go to db and get the user by calling it with username => change the isDeleted field to true
    // => save the object in db
    @Override
    public void delete(String username) {
        User user = userRepository.findByUserNameAndIsDeleted(username,false);

        if (checkIfUserCanBeDeleted(user)){
            user.setIsDeleted(true);
            // After deletion, we change the username of the user, so we can use the same username after
            user.setUserName(user.getUserName()+ user.getId());
            userRepository.save(user);
        }


    }

    @Override
    public List<UserDTO> listByRole(String role) {
        return userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role,false).stream().map(userMapper::convertToUserDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
            case "Manager": // Here we check if the manager has a non-completed project, we cannot delete the manager
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(userMapper.convertToUserDto(user));
                return projectDTOList.size() == 0;
            case "Employee": // Here we check if the employee has a non-completed task, we cannot delete the employee
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(userMapper.convertToUserDto(user));
                return taskDTOList.size()==0;
            default:
                return true;
        }

    }


}
