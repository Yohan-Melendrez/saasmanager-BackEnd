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

import com.turing.saasmanager.entity.LicenciaSoftware;
import com.turing.saasmanager.service.LicenciaSoftwareService;

@RestController
@RequestMapping("/api/v1/licencias")
public class LicenciaSoftwareController {

    private final LicenciaSoftwareService licenciaSoftwareService;

    public LicenciaSoftwareController(LicenciaSoftwareService licenciaSoftwareService) {
        this.licenciaSoftwareService = licenciaSoftwareService;
    }

    @GetMapping
    public ResponseEntity<List<LicenciaSoftware>> obtenerTodas() {
        List<LicenciaSoftware> licencias = licenciaSoftwareService.obtenerTodas();
        return ResponseEntity.ok(licencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenciaSoftware> obtenerPorId(@PathVariable Integer id) {
        return licenciaSoftwareService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<LicenciaSoftware> crear(@RequestBody LicenciaSoftware licencia) {
        LicenciaSoftware nuevaLicencia = licenciaSoftwareService.crear(licencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLicencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicenciaSoftware> actualizar(@PathVariable Integer id, @RequestBody LicenciaSoftware licencia) {
        return licenciaSoftwareService.actualizar(id, licencia)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = licenciaSoftwareService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
