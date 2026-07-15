package com.turing.saasmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.AsignacionEmpleado;
import com.turing.saasmanager.entity.LicenciaSoftware;
import com.turing.saasmanager.exception.ResourceAlreadyExistsException;
import com.turing.saasmanager.exception.ResourceNotFoundException;
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
        AsignacionEmpleado asignacion = asignacionEmpleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La asignación con ID " + id + " no existe."));
        return Optional.of(asignacion);
    }

    @Transactional
    public AsignacionEmpleado crear(AsignacionEmpleado asignacion) {
        if (asignacion.getLicencia() != null && asignacion.getLicencia().getIdLicencia() != null) {
            LicenciaSoftware lic = licenciaSoftwareRepository.findById(asignacion.getLicencia().getIdLicencia())
                    .orElseThrow(() -> new ResourceNotFoundException("La licencia con ID " + asignacion.getLicencia().getIdLicencia() + " no existe."));
            asignacion.setLicencia(lic);

            if (asignacionEmpleadoRepository.existsByLicenciaIdLicenciaAndCorreoEmpleadoAndEstatusActivoTrue(
                    lic.getIdLicencia(), asignacion.getCorreoEmpleado())) {
                throw new ResourceAlreadyExistsException("El empleado con correo '" + asignacion.getCorreoEmpleado() + "' ya tiene asignada la licencia '" + lic.getTipoPlan() + "' de forma activa.");
            }
        } else {
            throw new ResourceNotFoundException("Se requiere especificar el ID de la licencia para realizar la asignación.");
        }
        return asignacionEmpleadoRepository.save(asignacion);
    }

    @Transactional
    public Optional<AsignacionEmpleado> actualizar(Integer id, AsignacionEmpleado asignacionActualizada) {
        AsignacionEmpleado asignacionExistente = asignacionEmpleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La asignación con ID " + id + " no existe."));

        if (asignacionActualizada.getLicencia() != null && asignacionActualizada.getLicencia().getIdLicencia() != null) {
            LicenciaSoftware lic = licenciaSoftwareRepository.findById(asignacionActualizada.getLicencia().getIdLicencia())
                    .orElseThrow(() -> new ResourceNotFoundException("La licencia con ID " + asignacionActualizada.getLicencia().getIdLicencia() + " no existe."));
            asignacionExistente.setLicencia(lic);
        }
        asignacionExistente.setCorreoEmpleado(asignacionActualizada.getCorreoEmpleado());
        asignacionExistente.setFechaAsignacion(asignacionActualizada.getFechaAsignacion());
        asignacionExistente.setEstatusActivo(asignacionActualizada.getEstatusActivo());
        return Optional.of(asignacionEmpleadoRepository.save(asignacionExistente));
    }

    @Transactional
    public boolean eliminar(Integer id) {
        if (!asignacionEmpleadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("La asignación con ID " + id + " no existe para ser eliminada.");
        }
        asignacionEmpleadoRepository.deleteById(id);
        return true;
    }
}
