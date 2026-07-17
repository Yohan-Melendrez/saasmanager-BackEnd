package com.turing.saasmanager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.turing.saasmanager.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Usuario> findByUsernameOrEmail(String username, String email);
}
