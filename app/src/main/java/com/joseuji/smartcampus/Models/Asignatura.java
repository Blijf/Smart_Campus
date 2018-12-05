package com.joseuji.smartcampus.Models;

public class Asignatura {


    private String nombreCA, nombreES, estudiantesMatriculados, _id, nombreEN;

    public Asignatura() {
    }

    public Asignatura(String nombreCA,String nombreES,String estudiantesMatriculados, String _id, String nombreEN) {
        this.nombreCA = nombreCA;
        this.nombreES = nombreES;
        this.estudiantesMatriculados = estudiantesMatriculados;
        this._id = _id;
        this.nombreEN = nombreEN;
    }

    //GETS Y SETS
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNombreCA() {
        return nombreCA;
    }
    public void setNombreCA(String nombreCA) {
        this.nombreCA = nombreCA;
    }
    public String getNombreES() {
        return nombreES;
    }
    public void setNombreES(String nombreES) {
        this.nombreES = nombreES;
    }
    public String getEstudiantesMatriculados() {
        return estudiantesMatriculados;
    }
    public void setEstudiantesMatriculados(String estudiantesMatriculados) {
        this.estudiantesMatriculados = estudiantesMatriculados;
    }
    public String getNombreEN() {
        return nombreEN;
    }
    public void setNombreEN(String nombreEN) {
        this.nombreEN = nombreEN;
    }
}
