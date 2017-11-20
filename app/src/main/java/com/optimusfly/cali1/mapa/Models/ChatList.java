package com.optimusfly.cali1.mapa.Models;

/**
 * Created by cali1 on 27/10/2017.
 */

public class ChatList {


    private String imagenPerfil,nombre,mensaje, currentId,idUsuario,email;

    public ChatList() {
    }

    public ChatList(String imagenPerfil, String nombre, String mensaje, String currentId, String idUsuario, String email) {
        this.currentId = currentId;
        this.idUsuario = idUsuario;
        this.imagenPerfil = imagenPerfil;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.email = email;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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
