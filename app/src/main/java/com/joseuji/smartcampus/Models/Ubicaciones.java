package com.joseuji.smartcampus.Models;

import java.util.List;

public class Ubicaciones
{
    private Paginacion page;
    private List<Ubicacion> content;
    // Incluir otros campos

    public Ubicaciones()
    {

    }

    public Ubicaciones(Paginacion page, List<Ubicacion> content) {
        this.page = page;
        this.content = content;
    }

    public Paginacion getPage() {
        return page;
    }

    public void setPage(Paginacion page) {
        this.page = page;
    }

    public List<Ubicacion> getContent() {
        return content;
    }

    public void setContent(List<Ubicacion> content) {
        this.content = content;
    }
}
