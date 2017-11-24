package com.optimusfly.cali1.mapa.Models;

import android.print.PageRange;

/**
 * Created by cali1 on 18/10/2017.
 */

public class Usuario {

    private String usuario;
    private String email;
    private String imagenPerfil;
    private String idUsuario;
    private String tipoUser;
    private String deviceToken;
    private String urlPerfil;
    private String birthday,publicProfile;
    public Usuario(){

    }

    public Usuario(String usuario, String email, String imagenPerfil, String idUsuario, String tipoUser, String deviceToken,String urlPerfil,String birthday, String publicProfile) {
        this.usuario = usuario;
        this.email = email;
        this.imagenPerfil = imagenPerfil;
        this.idUsuario = idUsuario;
        this.tipoUser = tipoUser;
        this.deviceToken = deviceToken;
        this.urlPerfil = urlPerfil;
        this.birthday = birthday;
        this.publicProfile = publicProfile;

    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(String publicProfile) {
        this.publicProfile = publicProfile;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
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