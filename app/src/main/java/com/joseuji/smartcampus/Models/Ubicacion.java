package com.joseuji.smartcampus.Models;

public class Ubicacion {

    private String id, edificioId, metros,descripcion;
    private Localizacion localizacion;

    public Ubicacion() {
    }



    public Ubicacion(String id, String edificioId, String descripcion, String metros, Localizacion localizacion) {
        this.id = id;
        this.edificioId = edificioId;
        this.metros = metros;
        this.descripcion = descripcion;
        this.localizacion=localizacion;

    }

    //GETS Y SETS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEdificioId() {
        return edificioId;
    }

    public void setEdificioId(String edificioId) {
        this.edificioId = edificioId;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }
    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
}
