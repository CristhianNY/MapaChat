package com.optimusfly.cali1.mapa.Models;

/**
 * Created by cali1 on 25/10/2017.
 */
public class UsuarioCerca {

    private String usuario;
    private String email;
    private String imagenPerfil;
    private String idUsuario;
    private String tipoUser;
    private String deviceToken;
    private String distancia;
    public UsuarioCerca(){

    }

    public UsuarioCerca(String usuario, String email, String imagenPerfil, String idUsuario, String tipoUser, String deviceToken,String distancia) {
        this.usuario = usuario;
        this.email = email;
        this.imagenPerfil = imagenPerfil;
        this.idUsuario = idUsuario;
        this.tipoUser = tipoUser;
        this.deviceToken = deviceToken;
        this.distancia = distancia;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        this.tipoUser = tipoUser;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
}