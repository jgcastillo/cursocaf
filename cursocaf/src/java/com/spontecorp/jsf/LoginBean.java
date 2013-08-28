/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.jsf;

import com.spontecorp.entity.Persona;
import com.spontecorp.security.Login;
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author sponte03
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String usuario;
    private String password;
    private Persona current;
    private Date fechaActual;
    private boolean admin = false;

    public LoginBean() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    public Persona getCurrent() {
        return current;
    }

    public void setCurrent(Persona current) {
        this.current = current;
    }

    public String login() {
        char[] pswChar = password.toCharArray();
        FacesMessage msg = null;
        String result = "";
        //System.out.println("1.- El Usuario es: " + usuario);
        current = Login.authenticate(usuario, pswChar);
        if (current != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", usuario);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", usuario);
            if (current.getPerfilPersonaList().size() > 0) {
                if (current.getPerfilPersonaList().get(0).getPerfilId().getId() == 1 || current.getPerfilPersonaList().get(0).getPerfilId().getId() == 2) {
                    result = "index?faces-redirect=true";
                } else {
                    result = "login?faces-redirect=true";
                }
            }
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", "");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Ingreso", "Credenciales no válidas");
            //result = "login?faces-redirect=true";
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return result;
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(usuario);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        current = null;
        setAdmin(false);
        return "/index?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return (current != null);
    }

    public boolean isAdmin() {
        if (current != null) {
            if (current.getPerfilPersonaList().size() > 0) {
                String perfil = current.getPerfilPersonaList().get(0).getPerfilId().getNombre();
                if (perfil.equals("Administrador")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isInstructor() {
        if (current != null) {
            if (current.getPerfilPersonaList().size() > 0) {
                String perfil = current.getPerfilPersonaList().get(0).getPerfilId().getNombre();
                if (perfil.equals("Instructor")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
