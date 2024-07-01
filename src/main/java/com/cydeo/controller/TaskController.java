package com.cydeo.controller;

import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;

import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/task")
public class TaskController {
    private final UserService userService;
    private final TaskService taskService;
    private final ProjectService projectService;


    public TaskController(UserService userService, TaskService taskService, ProjectService projectService) {
        this.userService = userService;
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping("/create")
    public String taskCreate(Model model) {

        model.addAttribute("task", new TaskDTO());
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("employees", userService.listByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());

        return "task/create";
    }


    @PostMapping("/create")
    public String insertTask(@ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("projects", projectService.listAllProjects());
            model.addAttribute("employees", userService.listByRole("employee"));
            model.addAttribute("tasks", taskService.listAllTasks());

            return "task/create";

        }

        taskService.save(task);

        return "redirect:/task/create";

    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return "redirect:/task/create";
    }


    @GetMapping("/update/{taskId}")
    public String editTask(@PathVariable("taskId") Long taskId, Model model) {

        model.addAttribute("task", taskService.getByTaskId(taskId));
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("employees", userService.listByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());

        return "/task/update";

    }


    @PostMapping("/update/{id}")
    public String updateTask(@ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("projects", projectService.listAllProjects());
            model.addAttribute("employees", userService.listByRole("employee"));
            model.addAttribute("tasks", taskService.listAllTasks());

            return "/task/update";

        }

        taskService.update(task);

        return "redirect:/task/create";

    }

    @GetMapping("/employee/pending-tasks")
    public String employeePendingTasks(Model model) {

        model.addAttribute("tasks", taskService.listAllTaskByStatusIsNot(Status.COMPLETE));
        return "/task/pending-tasks";
    }

    @GetMapping("/employee/archive")
    public String employeeArchive(Model model) {
        model.addAttribute("archive", taskService.listAllTaskByStatus(Status.COMPLETE));

        return "task/archive";
    }

    @GetMapping("/employee/edit/{id}")
    public String editEmployeeTask(@PathVariable Long id, Model model) {

        model.addAttribute("task", taskService.getByTaskId(id));
        model.addAttribute("statuses", Status.values());

        model.addAttribute("tasks", taskService.listAllTaskByStatusIsNot(Status.COMPLETE));
        return "/task/status-update";
    }

    @PostMapping("/employee/update/{id}")
    public String employeeUpdateTask( @ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("statuses", Status.values());
            model.addAttribute("tasks", taskService.listAllTaskByStatusIsNot(Status.COMPLETE));

            return "/task/status-update";

        }

        taskService.update(task);

        return "redirect:/task/employee/pending-tasks";

    }

}
