/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.session;

import com.spontecorp.entity.Persona;
import com.spontecorp.jsf.util.JpaUtilities;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author zuleidyb
 */
@Stateless
public class PersonaFacadeExt extends AbstractFacade<Persona> {

    @PersistenceContext(unitName = "cursocafPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public PersonaFacadeExt() {
        super(Persona.class);
    }
    
     /**
     * Verifico si existe el email
     *
     * @param email
     * @return
     */
    public Persona findEmail(String email) {
        String query = "SELECT p from Persona p WHERE p.email = :email";
        Query q = getEntityManager().createQuery(query);
        q.setParameter("email", email);
        List<Persona> personas = q.getResultList();
        if (personas.isEmpty()) {
            return null;
        } else {
            return personas.get(0);
        }

    }
}
