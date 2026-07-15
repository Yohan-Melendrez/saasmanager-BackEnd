package com.turing.saasmanager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.turing.saasmanager.entity.ProveedorNube;

public interface ProveedorNubeRepository extends JpaRepository<ProveedorNube, Integer> {
    boolean existsByNombrePlataforma(String nombrePlataforma);
    Optional<ProveedorNube> findByNombrePlataforma(String nombrePlataforma);
}
