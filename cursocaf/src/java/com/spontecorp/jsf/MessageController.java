/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.jsf;

import com.spontecorp.entity.Curso;
import com.spontecorp.entity.Persona;
import com.spontecorp.jsf.util.JpaUtilities;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;


/**
 *
 * @author sponte07
 */
@ManagedBean(name = "messageController")
@RequestScoped
public class MessageController implements Serializable {
    @EJB 
    private com.spontecorp.session.PersonaCursoFacadeExt ejbPersonaCursoFacadeExt;
    @EJB
    private com.spontecorp.session.PersonaFacade ejbPersonaFacade;
    @EJB
    private com.spontecorp.session.PersonaCursoFacade ejbPersonaCursoFacade;
    @EJB
    private com.spontecorp.session.CursoFacade ejbCursoFacade;
    @ManagedProperty(value = "#{param.idCurso}")
    private String idCurso;
    @ManagedProperty(value = "#{param.idPersona}")
    private String idPersona;
    @ManagedProperty(value = "#{param.email}")
    private String email;
    private boolean valid = false;

    @PostConstruct
    public void init() {
     chequear();
    }

    public MessageController() {
    }

    public void chequear() {
        if (idPersona == null && idCurso==null) {
            valid = false;
        } else {
            Persona per = ejbPersonaFacade.find(Integer.parseInt(idPersona));
            Curso cur = ejbCursoFacade.find(Integer.parseInt(idCurso));
            if (per == null && cur ==null) {
                valid = false;
             
            } else {
                ejbPersonaCursoFacadeExt.setStatusInscritos(per, cur, JpaUtilities.INSCRITO);
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
