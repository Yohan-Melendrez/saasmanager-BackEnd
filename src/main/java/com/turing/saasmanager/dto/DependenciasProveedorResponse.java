package com.turing.saasmanager.dto;

public class DependenciasProveedorResponse {

    private long licencias;
    private long asignaciones;

    public DependenciasProveedorResponse() {
    }

    public DependenciasProveedorResponse(long licencias, long asignaciones) {
        this.licencias = licencias;
        this.asignaciones = asignaciones;
    }

    public long getLicencias() {
        return licencias;
    }

    public void setLicencias(long licencias) {
        this.licencias = licencias;
    }

    public long getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(long asignaciones) {
        this.asignaciones = asignaciones;
    }
}
