package com.turing.saasmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.dto.DependenciasProveedorResponse;
import com.turing.saasmanager.entity.ProveedorNube;
import com.turing.saasmanager.service.ProveedorNubeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class ProveedorNubeController {

    private final ProveedorNubeService proveedorNubeService;

    public ProveedorNubeController(ProveedorNubeService proveedorNubeService) {
        this.proveedorNubeService = proveedorNubeService;
    }

    @GetMapping
    public ResponseEntity<List<ProveedorNube>> obtenerTodos() {
        List<ProveedorNube> proveedores = proveedorNubeService.obtenerTodos();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorNube> obtenerPorId(@PathVariable Integer id) {
        return proveedorNubeService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ProveedorNube> crear(@Valid @RequestBody ProveedorNube proveedor) {
        ProveedorNube nuevoProveedor = proveedorNubeService.crear(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorNube> actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorNube proveedor) {
        return proveedorNubeService.actualizar(id, proveedor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/dependencias")
    public ResponseEntity<DependenciasProveedorResponse> obtenerDependencias(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorNubeService.obtenerDependencias(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id,
            @RequestParam(name = "cascada", defaultValue = "false") boolean cascada) {
        boolean eliminado = proveedorNubeService.eliminar(id, cascada);
        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
