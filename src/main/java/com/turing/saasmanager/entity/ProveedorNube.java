package com.turing.saasmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "proveedor_nube")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProveedorNube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @NotBlank(message = "El nombre de la plataforma es obligatorio")
    @Column(name = "nombre_plataforma", nullable = false, unique = true, length = 100)
    private String nombrePlataforma;

    @NotBlank(message = "La categoría del servicio es obligatoria")
    @Column(name = "categoria_servicio", nullable = false, length = 50)
    private String categoriaServicio;

    
    public ProveedorNube() {
    }

    public ProveedorNube(Integer idProveedor, String nombrePlataforma, String categoriaServicio) {
        this.idProveedor = idProveedor;
        this.nombrePlataforma = nombrePlataforma;
        this.categoriaServicio = categoriaServicio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombrePlataforma() {
        return nombrePlataforma;
    }

    public void setNombrePlataforma(String nombrePlataforma) {
        this.nombrePlataforma = nombrePlataforma;
    }

    public String getCategoriaServicio() {
        return categoriaServicio;
    }

    public void setCategoriaServicio(String categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    
}