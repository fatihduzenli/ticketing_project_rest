package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Status;

import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private final UserService userService;
    private final ProjectService projectService;

    public ProjectController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }


    @GetMapping("/create")
    public String productCreate(Model model) {

        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("managers", userService.listByRole("Manager"));
        // listAllProjectDetails method will allow us to see project lists that belong to the assigned manager
        model.addAttribute("projects", projectService.listAllProjectDetails());

        return "project/create";
    }

    @PostMapping("/create")
    public String saveProject(@ModelAttribute("project") ProjectDTO projectDTO, Model model) {


        model.addAttribute("managers", userService.listByRole("manager"));
        model.addAttribute("projects", projectService.listAllProjectDetails());


        projectDTO.setStatus(Status.OPEN);

        projectService.save(projectDTO);

        return "redirect:/project/create";
    }


    @GetMapping("delete/{projectCode}")
    public String deleting(@PathVariable("projectCode") String code) {

        projectService.delete(code);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectCode}")
    public String completeProject(@PathVariable("projectCode") String projectCode) {
        projectService.complete(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("update/{projectCode}")
    public String updateProject(@PathVariable("projectCode") String projectCode, Model model) {

        model.addAttribute("project", projectService.getByProjectCode(projectCode));
        model.addAttribute("managers", userService.listByRole("manager"));
        // listAllProjectDetails method will allow us to see project lists that belong to the assigned manager
        model.addAttribute("projects", projectService.listAllProjectDetails());


        return "project/update";
    }

    @PostMapping("/update")
    public String editProject(@ModelAttribute("project") ProjectDTO projectDTO) {


        projectService.update(projectDTO);

        return "redirect:/project/create";
    }

    @GetMapping("/manager/complete/{projectCode}")
    public String managerCompleteProject(@PathVariable("projectCode") String projectCode) {

        projectService.complete(projectCode);

        return "redirect:/project/manager/project-status";
    }

    @GetMapping("/manager/project-status")
    public String getProjectByManager(Model model) {

//        UserDTO manager = userService.findById("john@cydeo.com");
        List<ProjectDTO> projects = projectService.listAllProjectDetails();

        model.addAttribute("projects", projects);


        return "/manager/project-status";
    }

}