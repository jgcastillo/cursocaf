/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.session;

import com.spontecorp.entity.Curso;
import com.spontecorp.jsf.util.JpaUtilities;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sponte03
 */
@Stateless
public class CursoFacadeExt extends AbstractFacade<Curso>{

    @PersistenceContext(unitName = "cursocafPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public CursoFacadeExt() {
        super(Curso.class);
    }
    
    /**
     *  Listado de Cursos Activos
     * @param 
     * @return 
     */
    public List<Curso> getCursosActivos() {
        String query = "SELECT c from Curso c WHERE c.status = " + JpaUtilities.ACTIVO;
        Query q = getEntityManager().createQuery(query);
        return q.getResultList();
    }
    
}
