package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskMapper taskMapper, TaskRepository taskRepository, ProjectMapper projectMapper, @Lazy UserServiceImpl userService, UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public TaskDTO getByTaskId(Long id) {

        return taskMapper.convertToDto(taskRepository.getById(id));
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {

        dto.setStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        taskRepository.save(taskMapper.convertToEntity(dto));
    }

    @Override
    public void update(TaskDTO dto) {
        Task task = taskRepository.getById(dto.getId());
        Task convertedTask = taskMapper.convertToEntity(dto);
      //  convertedTask.setId(task.getId());
        convertedTask.setAssignedDate(task.getAssignedDate());
        // dto.getStatus()==null means its coming from form that manager uses
        // if not null it means it's coming from employee page that we do update to change the task status if the task is in progress or completed
        convertedTask.setStatus(dto.getStatus()==null ? task.getStatus() : dto.getStatus()); // explain in week 29 live review 2:15:00
        taskRepository.save(convertedTask);

    }

    @Override
    public void delete(Long id) {
        Task task = taskRepository.getById(id);
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Override
    public int totalNonCompletedTask(String projectCode) {
        return taskRepository.totalNonCompletedTask(projectCode);
    }

    @Override
    public int totalCompletedTask(String projectCode) {
        return taskRepository.totalCompletedTask(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        // Here we are going the deleting all the task that was belonged to the deleted project
        Project project=projectMapper.convertToEntity(projectDTO);
        //Here we got a list of the task that belong to project that we deleted
        List<Task> task= taskRepository.findAllByProject(project);
        // Here we use the delete method that we use above which sets setIsDeleted(true)
        // We set one by one all the task's setIsDeleted field true by delete() method
        task.forEach(task1 -> delete(task1.getId()));
    }

    @Override
    public void completeByProject(ProjectDTO projectDTO) { // we retrieve the tasks by the project
        Project project=projectMapper.convertToEntity(projectDTO);
        // Here we get all the tasks that belong to a certain project
        List<Task> task= taskRepository.findAllByProject(project);
        // And we convert each task's status to be completed
        task.stream().map(taskMapper::convertToDto).forEach(taskDTO -> {
            taskDTO.setStatus(Status.COMPLETE);
            update(taskDTO);
        });
    }

    @Override
    public List<TaskDTO> listAllTaskByStatusIsNot(Status status) {
        // we get the logged-in user by hard code until we learn security
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO loggedInUser= userService.findByUserName(username);
        // Here we get all the tasks that belong to logged-in user and we pass the status
        List<Task> tasks =  taskRepository.findAllByStatusIsNotAndEmployee(status,userMapper.convertToUserEntity(loggedInUser) );
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTaskByStatus(Status status) {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO loggedInUser= userService.findByUserName(username);
        List<Task> tasks =  taskRepository.findAllByStatusAndEmployee(status,userMapper.convertToUserEntity(loggedInUser) );
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllNonCompletedByAssignedEmployee(UserDTO user) {
        List<Task> tasks= taskRepository.findAllByStatusIsNotAndEmployee(Status.COMPLETE, userMapper.convertToUserEntity(user));
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }
}
