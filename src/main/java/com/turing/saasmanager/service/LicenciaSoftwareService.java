package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.LicenciaSoftware;
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
        return licenciaSoftwareRepository.findById(id);
    }

    @Transactional
    public LicenciaSoftware crear(LicenciaSoftware licencia) {
        if (licencia.getProveedor() != null && licencia.getProveedor().getIdProveedor() != null) {
            proveedorNubeRepository.findById(licencia.getProveedor().getIdProveedor())
                    .ifPresent(licencia::setProveedor);
        }
        return licenciaSoftwareRepository.save(licencia);
    }

    @Transactional
    public Optional<LicenciaSoftware> actualizar(Integer id, LicenciaSoftware licenciaActualizada) {
        return licenciaSoftwareRepository.findById(id)
                .map(licenciaExistente -> {
                    if (licenciaActualizada.getProveedor() != null && licenciaActualizada.getProveedor().getIdProveedor() != null) {
                        proveedorNubeRepository.findById(licenciaActualizada.getProveedor().getIdProveedor())
                                .ifPresent(licenciaExistente::setProveedor);
                    } else {
                        licenciaExistente.setProveedor(licenciaActualizada.getProveedor());
                    }
                    licenciaExistente.setTipoPlan(licenciaActualizada.getTipoPlan());
                    licenciaExistente.setCostoMensual(licenciaActualizada.getCostoMensual());
                    licenciaExistente.setAsientosTotales(licenciaActualizada.getAsientosTotales());
                    return licenciaSoftwareRepository.save(licenciaExistente);
                });
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (licenciaSoftwareRepository.existsById(id)) {
            licenciaSoftwareRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
