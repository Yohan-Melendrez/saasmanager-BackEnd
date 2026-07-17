package com.turing.saasmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.dto.JwtResponse;
import com.turing.saasmanager.dto.LoginRequest;
import com.turing.saasmanager.dto.RegisterRequest;
import com.turing.saasmanager.entity.Usuario;
import com.turing.saasmanager.exception.ResourceAlreadyExistsException;
import com.turing.saasmanager.repository.UsuarioRepository;
import com.turing.saasmanager.security.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Subject debe ser email: CustomUserDetailsService busca por email, no por username.
        String jwt = jwtUtils.generarTokenDesdeUsername(loginRequest.getEmail());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String rol = userDetails.getAuthorities().isEmpty() ? "ROLE_USER" : userDetails.getAuthorities().iterator().next().getAuthority();

        // El email es el identificador usado para autenticarse
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), loginRequest.getEmail(), rol));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("El nombre de usuario '" + registerRequest.getUsername() + "' ya existe.");
        }
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("El correo '" + registerRequest.getEmail() + "' ya está registrado.");
        }

        Usuario usuario = new Usuario(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getRol()
        );

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
