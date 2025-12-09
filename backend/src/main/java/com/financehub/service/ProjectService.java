package com.financehub.service;

import com.financehub.dto.ProjectDTO;
import com.financehub.entity.Project;
import com.financehub.entity.ProjectStatus;
import com.financehub.entity.User;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.repository.ProjectRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<ProjectDTO> getAllActiveProjects() {
        return projectRepository.findByStatus(ProjectStatus.ACTIVE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getMyProjects(Long userId) {
        return projectRepository.findByOwnerId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO createProject(ProjectDTO projectDTO, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setTargetAmount(projectDTO.getTargetAmount());
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE); // Auto-activate for demo purposes

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return mapToDTO(project);
    }

    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setTargetAmount(project.getTargetAmount());
        dto.setCurrentAmount(project.getCurrentAmount());
        dto.setStatus(project.getStatus());
        dto.setOwnerId(project.getOwner().getId());
        dto.setOwnerName(project.getOwner().getFullName());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }
}
