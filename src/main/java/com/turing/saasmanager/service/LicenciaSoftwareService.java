package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.LicenciaSoftware;
import com.turing.saasmanager.entity.ProveedorNube;
import com.turing.saasmanager.exception.ResourceAlreadyExistsException;
import com.turing.saasmanager.exception.ResourceNotFoundException;
import com.turing.saasmanager.repository.LicenciaSoftwareRepository;
import com.turing.saasmanager.repository.ProveedorNubeRepository;

@Service
public class LicenciaSoftwareService {

    private final LicenciaSoftwareRepository licenciaSoftwareRepository;
    private final ProveedorNubeRepository proveedorNubeRepository;

    public LicenciaSoftwareService(LicenciaSoftwareRepository licenciaSoftwareRepository,
                                   ProveedorNubeRepository proveedorNubeRepository) {
        this.licenciaSoftwareRepository = licenciaSoftwareRepository;
        this.proveedorNubeRepository = proveedorNubeRepository;
    }

    @Transactional(readOnly = true)
    public List<LicenciaSoftware> obtenerTodas() {
        return licenciaSoftwareRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<LicenciaSoftware> obtenerPorId(Integer id) {
        LicenciaSoftware licencia = licenciaSoftwareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La licencia con ID " + id + " no existe."));
        return Optional.of(licencia);
    }

    @Transactional
    public LicenciaSoftware crear(LicenciaSoftware licencia) {
        if (licencia.getProveedor() != null && licencia.getProveedor().getIdProveedor() != null) {
            ProveedorNube prov = proveedorNubeRepository.findById(licencia.getProveedor().getIdProveedor())
                    .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + licencia.getProveedor().getIdProveedor() + " no existe."));
            licencia.setProveedor(prov);

            if (licenciaSoftwareRepository.existsByProveedorIdProveedorAndTipoPlan(prov.getIdProveedor(), licencia.getTipoPlan())) {
                throw new ResourceAlreadyExistsException("El proveedor '" + prov.getNombrePlataforma() + "' ya cuenta con el plan '" + licencia.getTipoPlan() + "'.");
            }
        } else {
            throw new ResourceNotFoundException("Se requiere especificar el ID del proveedor para crear la licencia.");
        }
        return licenciaSoftwareRepository.save(licencia);
    }

    @Transactional
    public Optional<LicenciaSoftware> actualizar(Integer id, LicenciaSoftware licenciaActualizada) {
        LicenciaSoftware licenciaExistente = licenciaSoftwareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La licencia con ID " + id + " no existe."));

        if (licenciaActualizada.getProveedor() != null && licenciaActualizada.getProveedor().getIdProveedor() != null) {
            ProveedorNube prov = proveedorNubeRepository.findById(licenciaActualizada.getProveedor().getIdProveedor())
                    .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + licenciaActualizada.getProveedor().getIdProveedor() + " no existe."));
            licenciaExistente.setProveedor(prov);
        }
        licenciaExistente.setTipoPlan(licenciaActualizada.getTipoPlan());
        licenciaExistente.setCostoMensual(licenciaActualizada.getCostoMensual());
        licenciaExistente.setAsientosTotales(licenciaActualizada.getAsientosTotales());
        return Optional.of(licenciaSoftwareRepository.save(licenciaExistente));
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!licenciaSoftwareRepository.existsById(id)) {
            throw new ResourceNotFoundException("La licencia con ID " + id + " no existe para ser eliminada.");
        }
        licenciaSoftwareRepository.deleteById(id);
        return true;
    }
}
