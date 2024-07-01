package com.cydeo.repository;

import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

//If we pass true as a parameter, we will get all the deleted users, otherwise we will get deleted users
    List<User>findAllByIsDeletedOrderByFirstNameDesc(Boolean deleted);
    User findByUserNameAndIsDeleted(String username,Boolean deleted);

    @Transactional  // if there is any issue while executing the method, it will roll back
    void deleteByUserName(String username);// we created another drived query to be able to delete by username

    List<User>findByRoleDescriptionIgnoreCaseAndIsDeleted(String role, Boolean deleted);



}
