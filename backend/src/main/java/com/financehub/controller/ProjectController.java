package com.financehub.controller;

import com.financehub.dto.ProjectDTO;
import com.financehub.entity.User;
import com.financehub.service.ProjectService;
import com.financehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllActiveProjects() {
        return ResponseEntity.ok(projectService.getAllActiveProjects());
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectDTO>> getMyProjects(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(projectService.getMyProjects(user.getId()));
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(projectService.createProject(projectDTO, user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
}
