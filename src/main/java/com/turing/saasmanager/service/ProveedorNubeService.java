package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.ProveedorNube;
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
        return proveedorNubeRepository.findById(id);
    }

    @Transactional
    public ProveedorNube crear(ProveedorNube proveedor) {
        return proveedorNubeRepository.save(proveedor);
    }

    @Transactional
    public Optional<ProveedorNube> actualizar(Integer id, ProveedorNube proveedorActualizado) {
        return proveedorNubeRepository.findById(id)
                .map(proveedorExistente -> {
                    proveedorExistente.setNombrePlataforma(proveedorActualizado.getNombrePlataforma());
                    proveedorExistente.setCategoriaServicio(proveedorActualizado.getCategoriaServicio());
                    return proveedorNubeRepository.save(proveedorExistente);
                });
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (proveedorNubeRepository.existsById(id)) {
            proveedorNubeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
