package com.turing.saasmanager.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.entity.Usuario;
import com.turing.saasmanager.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/v1/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {

    private final UsuarioRepository usuarioRepository;

    public EmpleadoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public static class EmpleadoDto {
        private Integer idEmpleado;
        private String nombre;
        private String email;
        private String rol;

        public EmpleadoDto() {}

        public EmpleadoDto(Integer idEmpleado, String nombre, String email, String rol) {
            this.idEmpleado = idEmpleado;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
        }

        public Integer getIdEmpleado() { return idEmpleado; }
        public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDto>> obtenerEmpleados() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<EmpleadoDto> empleados = new ArrayList<>();

        // Si hay usuarios registrados, los convertimos a EmpleadoDto
        for (Usuario u : usuarios) {
            String correo = u.getUsername().contains("@") ? u.getUsername() : u.getUsername() + "@empresa.com";
            String nombre = u.getUsername().split("@")[0].replace(".", " ");
            String rol = (u.getRol() != null && u.getRol().contains("ADMIN")) ? "ADMIN" : "EMPLEADO";
            empleados.add(new EmpleadoDto(u.getIdUsuario(), nombre, correo, rol));
        }

        // Si la lista está vacía o para asegurar empleados base siempre disponibles para asignaciones
        if (empleados.isEmpty() || empleados.size() < 3) {
            empleados.add(new EmpleadoDto(101, "Carlos López", "carlos.martinez@empresa.com", "EMPLEADO"));
            empleados.add(new EmpleadoDto(102, "María Fernández", "maria.fernandez@empresa.com", "EMPLEADO"));
            empleados.add(new EmpleadoDto(103, "Roberto González", "roberto.gonzalez@empresa.com", "EMPLEADO"));
            empleados.add(new EmpleadoDto(104, "Ana López", "ana.lopes@empresa.com", "EMPLEADO"));
            empleados.add(new EmpleadoDto(105, "Luis Gómez", "luis.gomez@empresa.com", "EMPLEADO"));
        }

        return ResponseEntity.ok(empleados);
    }
}
