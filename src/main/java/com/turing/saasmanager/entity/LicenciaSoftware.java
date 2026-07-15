package com.turing.saasmanager.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "licencia_software")
public class LicenciaSoftware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_licencia")
    private Integer idLicencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private ProveedorNube proveedor;

    @Column(name = "tipo_plan", nullable = false, length = 100)
    private String tipoPlan;

    @Column(name = "costo_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoMensual;

    @Column(name = "asientos_totales", nullable = false)
    private Integer asientosTotales;

    public LicenciaSoftware() {
    }

    public LicenciaSoftware(Integer idLicencia, ProveedorNube proveedor, String tipoPlan, BigDecimal costoMensual,
            Integer asientosTotales) {
        this.idLicencia = idLicencia;
        this.proveedor = proveedor;
        this.tipoPlan = tipoPlan;
        this.costoMensual = costoMensual;
        this.asientosTotales = asientosTotales;
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
}
