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

    /**
     * Cantidad de Inscritos cuyo Status sea PENDIENTE = 1 o INSCRITO = 2
     * Este resultado se usa para Calcular los Cupos Disponibles en un Curso determinado
     * @param curso
     * @return 
     */
    public int findInscritos(Curso curso) {
        String query = "SELECT pc from PersonaCurso pc WHERE pc.cursoId = :curso and "
                + "(pc.status = " + JpaUtilities.INSCRITO + " OR pc.status = " + JpaUtilities.PENDIENTE + ")" ;
        Query q = getEntityManager().createQuery(query);
        q.setParameter("curso", curso);
        return q.getResultList().size();
    }
}
