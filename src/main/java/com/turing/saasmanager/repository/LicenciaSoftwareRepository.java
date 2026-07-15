package com.turing.saasmanager.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.turing.saasmanager.entity.LicenciaSoftware;

public interface LicenciaSoftwareRepository extends JpaRepository<LicenciaSoftware, Integer> {
    boolean existsByProveedorIdProveedorAndTipoPlan(Integer idProveedor, String tipoPlan);
    List<LicenciaSoftware> findByProveedorIdProveedor(Integer idProveedor);
}
