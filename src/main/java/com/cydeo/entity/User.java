package com.cydeo.entity;

import com.cydeo.enums.Gender;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
//@Where(clause = "is_deleted=false")
//Where annotation concatenates this clause to all the queries inside the user repository
public class User extends BaseEntity{

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String userName;
    private String password;
    private boolean enabled;
    private String phone;
    @ManyToOne
    private Role role;
    @Enumerated(EnumType.STRING)
    private Gender gender;


}
