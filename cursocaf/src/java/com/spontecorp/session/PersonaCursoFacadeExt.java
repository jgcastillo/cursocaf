/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.session;

import com.spontecorp.entity.Curso;
import com.spontecorp.entity.PersonaCurso;
import com.spontecorp.jsf.util.JpaUtilities;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sponte03
 */
@Stateless
public class PersonaCursoFacadeExt extends AbstractFacade<PersonaCurso> {

    @PersistenceContext(unitName = "cursocafPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonaCursoFacadeExt() {
        super(PersonaCurso.class);
    }

    public int findCuposDisponibles(Curso curso) {
        String query = "SELECT pc from PersonaCurso pc WHERE pc.cursoId = :curso and "
                + "pc.status = " + JpaUtilities.ACTIVO;
        Query q = getEntityManager().createQuery(query);
        q.setParameter("curso", curso);
        return q.getResultList().size();
    }
}
