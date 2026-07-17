package com.turing.saasmanager.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.entity.AsignacionEmpleado;
import com.turing.saasmanager.entity.LicenciaSoftware;
import com.turing.saasmanager.entity.Usuario;
import com.turing.saasmanager.exception.ResourceNotFoundException;
import com.turing.saasmanager.repository.UsuarioRepository;
import com.turing.saasmanager.service.AsignacionEmpleadoService;

@RestController
@RequestMapping("/api/v1/asignaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class AsignacionEmpleadoController {

    private final AsignacionEmpleadoService asignacionEmpleadoService;
    private final UsuarioRepository usuarioRepository;

    public AsignacionEmpleadoController(AsignacionEmpleadoService asignacionEmpleadoService,
            UsuarioRepository usuarioRepository) {
        this.asignacionEmpleadoService = asignacionEmpleadoService;
        this.usuarioRepository = usuarioRepository;
    }

    public static class AsignacionRequestDto {
        private Integer idLicencia;
        private Integer idEmpleado;
        private LicenciaSoftware licencia;
        private String correoEmpleado;
        private LocalDate fechaAsignacion;
        private Boolean estatusActivo;

        public Integer getIdLicencia() { return idLicencia; }
        public void setIdLicencia(Integer idLicencia) { this.idLicencia = idLicencia; }
        public Integer getIdEmpleado() { return idEmpleado; }
        public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }
        public LicenciaSoftware getLicencia() { return licencia; }
        public void setLicencia(LicenciaSoftware licencia) { this.licencia = licencia; }
        public String getCorreoEmpleado() { return correoEmpleado; }
        public void setCorreoEmpleado(String correoEmpleado) { this.correoEmpleado = correoEmpleado; }
        public LocalDate getFechaAsignacion() { return fechaAsignacion; }
        public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
        public Boolean getEstatusActivo() { return estatusActivo; }
        public void setEstatusActivo(Boolean estatusActivo) { this.estatusActivo = estatusActivo; }
    }

    @GetMapping
    public ResponseEntity<List<AsignacionEmpleado>> obtenerTodas(Authentication authentication) {
        if (esAdmin(authentication)) {
            return ResponseEntity.ok(asignacionEmpleadoService.obtenerTodas());
        }
        String email = obtenerEmailAutenticado(authentication);
        return ResponseEntity.ok(asignacionEmpleadoService.obtenerPorCorreoEmpleado(email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignacionEmpleado> obtenerPorId(@PathVariable Integer id) {
        return asignacionEmpleadoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignacionEmpleado> crear(@RequestBody AsignacionRequestDto dto) {
        AsignacionEmpleado asignacion = convertirDtoAEntidad(dto);
        AsignacionEmpleado nuevaAsignacion = asignacionEmpleadoService.crear(asignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignacionEmpleado> actualizar(@PathVariable Integer id, @RequestBody AsignacionRequestDto dto) {
        AsignacionEmpleado asignacion = convertirDtoAEntidad(dto);
        return asignacionEmpleadoService.actualizar(id, asignacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = asignacionEmpleadoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String obtenerEmailAutenticado(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + authentication.getName()));
        return usuario.getEmail();
    }

    private AsignacionEmpleado convertirDtoAEntidad(AsignacionRequestDto dto) {
        AsignacionEmpleado entidad = new AsignacionEmpleado();
        
        Integer licId = dto.getIdLicencia() != null ? dto.getIdLicencia() : (dto.getLicencia() != null ? dto.getLicencia().getIdLicencia() : null);
        if (licId != null) {
            LicenciaSoftware lic = new LicenciaSoftware();
            lic.setIdLicencia(licId);
            entidad.setLicencia(lic);
        } else if (dto.getLicencia() != null) {
            entidad.setLicencia(dto.getLicencia());
        }

        if (dto.getCorreoEmpleado() != null && !dto.getCorreoEmpleado().isBlank()) {
            entidad.setCorreoEmpleado(dto.getCorreoEmpleado());
        } else if (dto.getIdEmpleado() != null) {
            entidad.setCorreoEmpleado("empleado" + dto.getIdEmpleado() + "@empresa.com");
        } else {
            entidad.setCorreoEmpleado("carlos.martinez@empresa.com");
        }

        if (dto.getFechaAsignacion() != null) {
            entidad.setFechaAsignacion(dto.getFechaAsignacion());
        } else {
            entidad.setFechaAsignacion(LocalDate.now());
        }

        if (dto.getEstatusActivo() != null) {
            entidad.setEstatusActivo(dto.getEstatusActivo());
        } else {
            entidad.setEstatusActivo(true);
        }

        return entidad;
    }
}
