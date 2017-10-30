package com.optimusfly.cali1.mapa.Models;

/**
 * Created by cali1 on 27/10/2017.
 */

public class ChatList {


    private String imagenPerfil,nombre,fecha, currentId,idUsuario;

    public ChatList() {
    }

    public ChatList(String imagenPerfil, String nombre, String fecha, String currentId, String idUsuario) {
        this.currentId = currentId;
        this.idUsuario = idUsuario;
        this.imagenPerfil = imagenPerfil;
        this.nombre = nombre;
        this.fecha = fecha;

    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
