package com.joseuji.smartcampus.Models;

import java.util.List;

public class Ubicaciones
{
    private Paginacion paginacion;
    private List<Ubicacion> datos;
    // Incluir otros campos

    public Ubicaciones()
    {

    }

    public Ubicaciones(Paginacion paginacion, List<Ubicacion> datos) {
        this.paginacion = paginacion;
        this.datos = datos;
    }

    public Paginacion getPaginacion() {
        return paginacion;
    }

    public void setPaginacion(Paginacion paginacion) {
        this.paginacion = paginacion;
    }

    public List<Ubicacion> getDatos() {
        return datos;
    }

    public void setDatos(List<Ubicacion> datos) {
        this.datos = datos;
    }
}
