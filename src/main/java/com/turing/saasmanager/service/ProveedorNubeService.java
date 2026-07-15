package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.ProveedorNube;
import com.turing.saasmanager.exception.ResourceAlreadyExistsException;
import com.turing.saasmanager.exception.ResourceNotFoundException;
import com.turing.saasmanager.repository.ProveedorNubeRepository;

@Service
public class ProveedorNubeService {

    private final ProveedorNubeRepository proveedorNubeRepository;

    public ProveedorNubeService(ProveedorNubeRepository proveedorNubeRepository) {
        this.proveedorNubeRepository = proveedorNubeRepository;
    }

    @Transactional(readOnly = true)
    public List<ProveedorNube> obtenerTodos() {
        return proveedorNubeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ProveedorNube> obtenerPorId(Integer id) {
        ProveedorNube proveedor = proveedorNubeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + id + " no existe."));
        return Optional.of(proveedor);
    }

    @Transactional
    public ProveedorNube crear(ProveedorNube proveedor) {
        if (proveedorNubeRepository.existsByNombrePlataforma(proveedor.getNombrePlataforma())) {
            throw new ResourceAlreadyExistsException("El proveedor con el nombre '" + proveedor.getNombrePlataforma() + "' ya existe.");
        }
        return proveedorNubeRepository.save(proveedor);
    }

    @Transactional
    public Optional<ProveedorNube> actualizar(Integer id, ProveedorNube proveedorActualizado) {
        ProveedorNube proveedorExistente = proveedorNubeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + id + " no existe."));

        if (!proveedorExistente.getNombrePlataforma().equals(proveedorActualizado.getNombrePlataforma()) &&
                proveedorNubeRepository.existsByNombrePlataforma(proveedorActualizado.getNombrePlataforma())) {
            throw new ResourceAlreadyExistsException("El proveedor con el nombre '" + proveedorActualizado.getNombrePlataforma() + "' ya existe.");
        }

        proveedorExistente.setNombrePlataforma(proveedorActualizado.getNombrePlataforma());
        proveedorExistente.setCategoriaServicio(proveedorActualizado.getCategoriaServicio());
        return Optional.of(proveedorNubeRepository.save(proveedorExistente));
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!proveedorNubeRepository.existsById(id)) {
            throw new ResourceNotFoundException("El proveedor con ID " + id + " no existe para ser eliminado.");
        }
        proveedorNubeRepository.deleteById(id);
        return true;
    }
}
