/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.session;

import com.spontecorp.entity.PersonaCurso;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sponte03
 */
@Stateless
public class PersonaCursoFacade extends AbstractFacade<PersonaCurso> {
    @PersistenceContext(unitName = "cursocafPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonaCursoFacade() {
        super(PersonaCurso.class);
    }
    
}
