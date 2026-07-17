package com.turing.saasmanager.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.turing.saasmanager.entity.AsignacionEmpleado;

public interface AsignacionEmpleadoRepository extends JpaRepository<AsignacionEmpleado, Integer> {
    boolean existsByLicenciaIdLicenciaAndCorreoEmpleadoAndEstatusActivoTrue(Integer idLicencia, String correoEmpleado);
    List<AsignacionEmpleado> findByCorreoEmpleado(String correoEmpleado);
    List<AsignacionEmpleado> findByLicenciaIdLicencia(Integer idLicencia);
    long countByLicenciaProveedorIdProveedor(Integer idProveedor);
}
