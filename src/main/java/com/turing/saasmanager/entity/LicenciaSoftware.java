package com.turing.saasmanager.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "licencia_software")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LicenciaSoftware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_licencia")
    private Integer idLicencia;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProveedorNube proveedor;

    @NotBlank(message = "El tipo de plan es obligatorio")
    @Column(name = "tipo_plan", nullable = false, length = 100)
    private String tipoPlan;

    @NotNull(message = "El costo mensual es obligatorio")
    @Positive(message = "El costo mensual debe ser mayor a 0")
    @Column(name = "costo_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoMensual;

    @NotNull(message = "Los asientos totales son obligatorios")
    @Positive(message = "El número de asientos totales debe ser mayor a 0")
    @Column(name = "asientos_totales", nullable = false)
    private Integer asientosTotales;

    @Column(name = "estado", length = 30)
    private String estado = "ACTIVA";

    @Column(name = "fecha_vencimiento", length = 50)
    private String fechaVencimiento = "2026-12-31";

    public LicenciaSoftware() {
    }

    public LicenciaSoftware(Integer idLicencia, ProveedorNube proveedor, String tipoPlan, BigDecimal costoMensual,
            Integer asientosTotales, String estado, String fechaVencimiento) {
        this.idLicencia = idLicencia;
        this.proveedor = proveedor;
        this.tipoPlan = tipoPlan;
        this.costoMensual = costoMensual;
        this.asientosTotales = asientosTotales;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getIdLicencia() {
        return idLicencia;
    }

    public void setIdLicencia(Integer idLicencia) {
        this.idLicencia = idLicencia;
    }

    public ProveedorNube getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorNube proveedor) {
        this.proveedor = proveedor;
    }

    public String getTipoPlan() {
        return tipoPlan;
    }

    public void setTipoPlan(String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    public BigDecimal getCostoMensual() {
        return costoMensual;
    }

    public void setCostoMensual(BigDecimal costoMensual) {
        this.costoMensual = costoMensual;
    }

    public Integer getAsientosTotales() {
        return asientosTotales;
    }

    public void setAsientosTotales(Integer asientosTotales) {
        this.asientosTotales = asientosTotales;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
