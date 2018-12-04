package com.joseuji.smartcampus.Models;

import java.util.List;

public class Asignaturas {

    private Paginacion page;
    private List<Asignatura> content;
    // Incluir otros campos

    public Asignaturas()
    {

    }

    public Asignaturas(Paginacion page, List<Asignatura> content) {
        this.page = page;
        this.content = content;
    }

    public Paginacion getPage() {
        return page;
    }

    public void setPage(Paginacion page) {
        this.page = page;
    }

    public List<Asignatura> getContent() {
        return content;
    }

    public void setContent(List<Asignatura> content) {
        this.content = content;
    }
}
