package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> getProjects() {
        List<ProjectDTO> projectDTO = projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Project successfully retrieved", projectDTO, HttpStatus.OK));
    }

    @GetMapping("{projectCode}")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> getProjectsProjectByCode(@PathVariable("projectCode") String projectCode) {
        return ResponseEntity
                .ok(new ResponseWrapper("Project retrieved by project code successfully", projectService.getByProjectCode(projectCode), HttpStatus.OK));
    }

    @PostMapping
    @RolesAllowed({"Manager","Admin"})
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO) {
        projectService.save(projectDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper("Project successfully created", HttpStatus.CREATED));
    }

    @PutMapping
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) {
        projectService.update(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project updated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/projectCode")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable String projectCode) {
        projectService.delete(projectCode);

        return ResponseEntity.ok(new ResponseWrapper("Project deleted successfully", HttpStatus.OK));
    }

    @GetMapping("/manager/project-status")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> getProjectsByManager() {
        List<ProjectDTO> projectDTOList = projectService.listAllProjectDetails();
        return ResponseEntity.ok(new ResponseWrapper("Project successfully retrieved", projectDTOList, HttpStatus.OK));

    }

    @PutMapping("/manager/complete/{projectCode}")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> managerCompleteProject(@PathVariable String projectCode) {
        projectService.complete(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Project successfully completed", HttpStatus.OK));
    }

}