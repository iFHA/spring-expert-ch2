package dev.fernando.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.fernando.demo.entities.User;
import dev.fernando.demo.projections.UserDetailsProjection;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    
    @Query(nativeQuery = true, value = """
            SELECT
            a.email as username,
            a.password,
            b.role_id,
            c.authority
            FROM
            tb_user a
            join tb_user_role b on b.user_id=a.id
            join tb_role c on c.id=b.role_id
            WHERE
                a.email = :email
            """)
    List<UserDetailsProjection> getUserAndRolesByEmail(String email);
}
