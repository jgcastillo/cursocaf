/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.jsf;

import com.spontecorp.entity.Persona;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sponte07
 */
@ManagedBean(name = "messageController")
@RequestScoped
public class MessageController implements Serializable {

    @EJB
    private com.spontecorp.session.CursoFacade ejbFacade;
    @EJB
    private com.spontecorp.session.PersonaFacade ejbPersonaFacade;
    @EJB
    private com.spontecorp.session.PersonaCursoFacade ejbPersonaCursoFacade;
    @ManagedProperty(value = "#{param.idCurso}")
    private String idCurso = "";
    @ManagedProperty(value = "#{param.idPersona}")
    private String idPersona = "";
    @ManagedProperty(value = "#{param.email}")
    private String email = "";
    private boolean valid = false;

    @PostConstruct
    public void init() {
     chequear();
    }

    public MessageController() {
    }

    public void chequear() {
        if (idPersona == null) {
            valid = false;
        } else {
            Persona per = ejbPersonaFacade.find(Integer.parseInt(idPersona));
            if (per == null) {
                valid = false;
            } else {
                valid = true;
            }
        }

    }

    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }

    public String getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
