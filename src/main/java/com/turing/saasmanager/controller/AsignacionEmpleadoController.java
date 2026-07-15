package com.turing.saasmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turing.saasmanager.entity.AsignacionEmpleado;
import com.turing.saasmanager.service.AsignacionEmpleadoService;

@RestController
@RequestMapping("/api/v1/asignaciones")
public class AsignacionEmpleadoController {

    private final AsignacionEmpleadoService asignacionEmpleadoService;

    public AsignacionEmpleadoController(AsignacionEmpleadoService asignacionEmpleadoService) {
        this.asignacionEmpleadoService = asignacionEmpleadoService;
    }

    @GetMapping
    public ResponseEntity<List<AsignacionEmpleado>> obtenerTodas() {
        List<AsignacionEmpleado> asignaciones = asignacionEmpleadoService.obtenerTodas();
        return ResponseEntity.ok(asignaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionEmpleado> obtenerPorId(@PathVariable Integer id) {
        return asignacionEmpleadoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<AsignacionEmpleado> crear(@RequestBody AsignacionEmpleado asignacion) {
        AsignacionEmpleado nuevaAsignacion = asignacionEmpleadoService.crear(asignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionEmpleado> actualizar(@PathVariable Integer id, @RequestBody AsignacionEmpleado asignacion) {
        return asignacionEmpleadoService.actualizar(id, asignacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = asignacionEmpleadoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
