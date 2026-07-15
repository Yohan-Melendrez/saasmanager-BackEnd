package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.AsignacionEmpleado;
import com.turing.saasmanager.repository.AsignacionEmpleadoRepository;
import com.turing.saasmanager.repository.LicenciaSoftwareRepository;

@Service
public class AsignacionEmpleadoService {

    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final LicenciaSoftwareRepository licenciaSoftwareRepository;

    public AsignacionEmpleadoService(AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                                     LicenciaSoftwareRepository licenciaSoftwareRepository) {
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.licenciaSoftwareRepository = licenciaSoftwareRepository;
    }

    @Transactional(readOnly = true)
    public List<AsignacionEmpleado> obtenerTodas() {
        return asignacionEmpleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<AsignacionEmpleado> obtenerPorId(Integer id) {
        return asignacionEmpleadoRepository.findById(id);
    }

    @Transactional
    public AsignacionEmpleado crear(AsignacionEmpleado asignacion) {
        if (asignacion.getLicencia() != null && asignacion.getLicencia().getIdLicencia() != null) {
            licenciaSoftwareRepository.findById(asignacion.getLicencia().getIdLicencia())
                    .ifPresent(asignacion::setLicencia);
        }
        return asignacionEmpleadoRepository.save(asignacion);
    }

    @Transactional
    public Optional<AsignacionEmpleado> actualizar(Integer id, AsignacionEmpleado asignacionActualizada) {
        return asignacionEmpleadoRepository.findById(id)
                .map(asignacionExistente -> {
                    if (asignacionActualizada.getLicencia() != null && asignacionActualizada.getLicencia().getIdLicencia() != null) {
                        licenciaSoftwareRepository.findById(asignacionActualizada.getLicencia().getIdLicencia())
                                .ifPresent(asignacionExistente::setLicencia);
                    } else {
                        asignacionExistente.setLicencia(asignacionActualizada.getLicencia());
                    }
                    asignacionExistente.setCorreoEmpleado(asignacionActualizada.getCorreoEmpleado());
                    asignacionExistente.setFechaAsignacion(asignacionActualizada.getFechaAsignacion());
                    asignacionExistente.setEstatusActivo(asignacionActualizada.getEstatusActivo());
                    return asignacionEmpleadoRepository.save(asignacionExistente);
                });
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (asignacionEmpleadoRepository.existsById(id)) {
            asignacionEmpleadoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
