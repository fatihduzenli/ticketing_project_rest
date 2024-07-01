package com.cydeo.dto;

import com.cydeo.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
   private Long id;
@NotNull
   private ProjectDTO project;
@NotNull
   private UserDTO employee;
@NotBlank
   private String taskSubject;
@NotBlank
   private String taskDetail;
   private LocalDate assignedDate;
   private Status status;
}
