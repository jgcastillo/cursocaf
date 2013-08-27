/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spontecorp.security;

import com.spontecorp.entity.PerfilPersona;
import com.spontecorp.entity.Persona;
import com.spontecorp.jsf.util.JpaUtilities;
import com.spontecorp.service.UsuarioService;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.NoResultException;

/**
 *
 * @author sponte03
 */
public class Login {

    private static Persona usuario;
    private static List<PerfilPersona> permisos;

    public static Persona authenticate(String username, char[] password) {
        String pswEncripted = SecurePassword.encript(password);
        UsuarioService controller = new UsuarioService();
        try {
            System.out.println("2.- Entro en authenticate");
            usuario = controller.findUsuario(username);
            System.out.println("3.- El usuario es: "+usuario.getEmail());
            permisos = controller.findPerfil(usuario);
            usuario.setPerfilPersonaList(permisos);
            
            System.out.println("4.- El permiso es: "+permisos);
            System.out.println("5.- El password es: "+usuario.getPsw());
            System.out.println("6.- El password encriptado es: "+pswEncripted);
            
            if (!pswEncripted.equals(usuario.getPsw())) {
                return null;
            } else {
                return usuario;
            }


        } catch (NoResultException e) {
            return null;
        }
    }

    //    public static int getPermiso() {
    //        return permiso;
    //    }
    public static List<PerfilPersona> getPermisos() {
        return permisos;
    }
    
    
}
