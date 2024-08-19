package dev.fernando.demo.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.fernando.demo.dto.UserDTO;
import dev.fernando.demo.dto.UserInsertDTO;
import dev.fernando.demo.dto.UserUpdateDTO;
import dev.fernando.demo.entities.Role;
import dev.fernando.demo.entities.User;
import dev.fernando.demo.projections.UserDetailsProjection;
import dev.fernando.demo.repositories.UserRepository;
import dev.fernando.demo.services.exceptions.DatabaseException;
import dev.fernando.demo.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return this.userRepository.findAll().stream().map(UserDTO::new).toList();
    }
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        return this.userRepository.findAll(pageable).map(UserDTO::new);
    }
    protected User findEntityById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário de Id = %d não encontrado!".formatted(id)));
    }
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return new UserDTO(this.findEntityById(id));
    }
    @Transactional
    public UserDTO store(UserInsertDTO dto) {
        dto.setId(null);
        var entity = new User();
        copyDTOToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return new UserDTO(this.userRepository.save(entity));
    }
    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        User entity = this.findEntityById(id);
        copyDTOToEntity(dto, entity);
        return new UserDTO(this.userRepository.save(entity));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        this.findEntityById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível excluir o usuário de id = %d, pois o mesmo possui vínculos!".formatted(id));
        }
    }
    private void copyDTOToEntity(UserDTO dto, User entity) {
        entity.clearRoles();
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        dto.getRoles().stream().map(Role::new).forEach(entity::addRole);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> userDetailsProjection = userRepository.getUserAndRolesByEmail(username);
        if(userDetailsProjection.isEmpty()) {
            throw new UsernameNotFoundException("Usuário %s não encontrado!".formatted(username));
        }
        User user = new User();
        UserDetailsProjection userProjection = userDetailsProjection.get(0);
        user.setEmail(userProjection.getUsername());
        user.setPassword(userProjection.getPassword());
        userDetailsProjection.stream().forEach(projection -> {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        });
        return user;
    }
}
