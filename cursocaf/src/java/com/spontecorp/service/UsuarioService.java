/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.service;

import com.spontecorp.entity.PerfilPersona;
import com.spontecorp.entity.Persona;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author sponte03
 */
public class UsuarioService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("cursocafPU");
    private EntityManager em = emf.createEntityManager();

    /**
     *
     * @param email
     * @return
     */
    public Persona findUsuario(String email) {
        Query query = em.createNamedQuery("Persona.findByEmail", Persona.class);
        query.setParameter("email", email);
        return (Persona) query.getSingleResult();
    }

    /**
     *
     * @param id
     * @return
     */
    public List<PerfilPersona> findPerfil(Persona persona) {
        String q = "SELECT pp FROM PerfilPersona pp WHERE pp.personaId = :persona";
        Query query = em.createQuery(q);
        query.setParameter("persona", persona);
        return query.getResultList();
    }
}
