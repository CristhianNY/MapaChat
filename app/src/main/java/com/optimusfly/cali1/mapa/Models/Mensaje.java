package com.optimusfly.cali1.mapa.Models;

/**
 * Created by cali1 on 23/10/2017.
 */

public class Mensaje {

    private String mensaje,fotoperfil,typeMensaje,hora,idUsuario;

    public Mensaje() {
    }

    public Mensaje(String mensaje,String fotoperfil, String typeMensaje, String hora,String idUsuario) {
        this.mensaje = mensaje;
        this.fotoperfil = fotoperfil;
        this.typeMensaje = typeMensaje;
        this.hora = hora;
        this.idUsuario = idUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFotoperfil() {
        return fotoperfil;
    }

    public void setFotoperfil(String fotoperfil) {
        this.fotoperfil = fotoperfil;
    }

    public String getTypeMensaje() {
        return typeMensaje;
    }

    public void setTypeMensaje(String typeMensaje) {
        this.typeMensaje = typeMensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
