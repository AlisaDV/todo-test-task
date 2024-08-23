package ru.fateev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.fateev.model.Role;
import ru.fateev.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}