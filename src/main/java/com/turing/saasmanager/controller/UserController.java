package com.turing.saasmanager.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.dto.UpdateUserRequest;
import com.turing.saasmanager.entity.Usuario;
import com.turing.saasmanager.exception.ResourceAlreadyExistsException;
import com.turing.saasmanager.exception.ResourceNotFoundException;
import com.turing.saasmanager.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/users/me  →  Perfil del usuario autenticado
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<Usuario> getProfile(Principal principal) {
        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + principal.getName()));
        return ResponseEntity.ok(usuario);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/users/me  →  El usuario autenticado edita su propio perfil
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/me")
    public ResponseEntity<Usuario> updateProfile(Principal principal,
                                                  @Valid @RequestBody UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + principal.getName()));

        aplicarCambios(usuario, request, false);

        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(actualizado);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/users/{id}  →  Admin edita cualquier usuario por ID
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id,
                                                  @Valid @RequestBody UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado."));

        aplicarCambios(usuario, request, true);

        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(actualizado);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica compartida de actualización
    // ─────────────────────────────────────────────────────────────────────────
    private void aplicarCambios(Usuario usuario, UpdateUserRequest request, boolean esAdmin) {

        // Actualizar username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(usuario.getUsername())
                    && usuarioRepository.existsByUsername(request.getUsername())) {
                throw new ResourceAlreadyExistsException(
                        "El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
            }
            usuario.setUsername(request.getUsername());
        }

        // Actualizar email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(usuario.getEmail())
                    && usuarioRepository.existsByEmail(request.getEmail())) {
                throw new ResourceAlreadyExistsException(
                        "El correo '" + request.getEmail() + "' ya está registrado.");
            }
            usuario.setEmail(request.getEmail());
        }

        // Actualizar contraseña (requiere contraseña actual para el propio usuario)
        if (request.getPasswordNuevo() != null && !request.getPasswordNuevo().isBlank()) {
            if (!esAdmin) {
                // El usuario normal debe confirmar su contraseña actual
                if (request.getPasswordActual() == null || request.getPasswordActual().isBlank()) {
                    throw new IllegalArgumentException(
                            "Debe proporcionar la contraseña actual para cambiarla.");
                }
                if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
                    throw new IllegalArgumentException(
                            "La contraseña actual no es correcta.");
                }
            }
            usuario.setPassword(passwordEncoder.encode(request.getPasswordNuevo()));
        }

        // Actualizar rol (solo admin)
        if (esAdmin && request.getRol() != null && !request.getRol().isBlank()) {
            usuario.setRol(request.getRol());
        }
    }
}
