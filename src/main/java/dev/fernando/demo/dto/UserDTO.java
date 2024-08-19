package dev.fernando.demo.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dev.fernando.demo.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    @NotBlank(message = "Primeiro nome é obrigatório!")
    private String firstName;
    private String lastName;
    @Email(message = "Email inválido!")
    private String email;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(){}

    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    public UserDTO(User entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.email = entity.getEmail();
        entity.getRoles()
        .stream()
        .map(RoleDTO::new)
        .forEach(this::addRole);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(RoleDTO roleDTO) {
        this.roles.add(roleDTO);
    }
}
