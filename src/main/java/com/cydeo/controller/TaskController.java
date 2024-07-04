package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> getTask() {

        return ResponseEntity
                .ok(new ResponseWrapper("Task successfully retrieved", taskService.listAllTasks(), HttpStatus.OK));
    }

    @GetMapping("{taskId}")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity
                .ok(new ResponseWrapper("Task by the id successfully retrieved", taskService.findById(taskId), HttpStatus.OK));
    }

    @PostMapping
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper("Task successfully created", HttpStatus.CREATED));
    }

    @PutMapping
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) {

        taskService.update(taskDTO);
        return ResponseEntity
                .ok(new ResponseWrapper("Task successfully retrieved", HttpStatus.OK));

    }

    @DeleteMapping("{taskId}")
    @RolesAllowed({"Manager"})
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.ok(new ResponseWrapper("Task is successfully deleted", HttpStatus.OK));
    }

    @GetMapping("/employee/pending-task")
    @RolesAllowed({"Employee"})
    public ResponseEntity<ResponseWrapper> employeePendingTask() {
        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are successfully retrieved", taskDTOList, HttpStatus.OK));
    }

    @PutMapping("/employee/update")
    @RolesAllowed({"Employee"})
    public ResponseEntity<ResponseWrapper> employeeUpdateTask(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);
        return ResponseEntity
                .ok(new ResponseWrapper("Tasks are successfully updated",HttpStatus.OK));
    }
@GetMapping("/employee/archive")
@RolesAllowed({"Employee"})
    public ResponseEntity<ResponseWrapper> employeeArchiveTask() {
List<TaskDTO>taskDTOList= taskService.listAllTasksByStatus(Status.COMPLETE);
return ResponseEntity
        .ok(new ResponseWrapper("Tasks are successfully moved to archive",taskDTOList,HttpStatus.OK));
    }

}
