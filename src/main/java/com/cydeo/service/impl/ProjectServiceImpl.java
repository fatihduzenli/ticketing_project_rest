package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;


    public ProjectServiceImpl(ProjectMapper projectMapper, ProjectRepository projectRepository, @Lazy UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return projectMapper.convertToDto(projectRepository.findByProjectCode(code));
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectRepository.findAll().stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setStatus(Status.OPEN);
        projectRepository.save(projectMapper.convertToEntity(dto));
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());

        Project convertedProject = projectMapper.convertToEntity(dto);

        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());

        projectRepository.save(convertedProject);

    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true); // Here we do soft deletion

        // Here we manipulate project code to something else,
        // so we can use the same project code for our future projects
        project.setProjectCode(project.getProjectCode()+"-"+project.getId());
        projectRepository.save(project);
        // Here we also delete all the task that belongs to the certain project
        // ones we delete the project there is no point to keep the tasks that belong to deleted project
        taskService.deleteByProject(projectMapper.convertToDto(project));

    }

    @Override
    public void complete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
        taskService.completeByProject(projectMapper.convertToDto(project));
    }


 // listAllProjectDetails method will allow us to see project lists that belong to the assigned manager@Override
    public List<ProjectDTO> listAllProjectDetails() {
// This is how spring provides us the username who logged in the system
        String username= SecurityContextHolder.getContext().getAuthentication().getName();



        // business logic: we get all the project that assigned to certain manager
        // Since we don't have the security we get the manager hard coded

        UserDTO currentUserDTO = userService.findByUserName(username);
        // Here we captured the manager who logged in with hard code

        User user = userMapper.convertToUserEntity(currentUserDTO);
        // converted to entity because we retrieve these data from DB and DB works with entity

        List<Project>list=projectRepository.findAllByAssignedManager(user);
        //here we got all the project who is assigned to the manager

        // Here we need to set these 2 additional fields, that's the way we converted to the DTO and set each field and return
        return list.stream().map(project -> {

            ProjectDTO obj= projectMapper.convertToDto(project);
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTask(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTask(project.getProjectCode()));
            return obj;
        }
        ).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedUser) {
        List<Project> projects=projectRepository.findAllByAssignedManagerAndProjectStatusIsNot(userMapper.convertToUserEntity(assignedUser),Status.COMPLETE);
        return projects.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }
}
