package com.joseuji.smartcampus.Models;

public class Ubicacion {

    private String descripcion, _id, edificio, metros;

    public Ubicacion() {
    }

    public Ubicacion(String descripcion, String _id, String edificio, String metros) {
        this.descripcion = descripcion;
        this._id = _id;
        this.edificio = edificio;
        this.metros = metros;
    }

    //GETS Y SETS
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getMetros() {
        return metros;
    }

    public void setMetros(String metros) {
        this.metros = metros;
    }
}
