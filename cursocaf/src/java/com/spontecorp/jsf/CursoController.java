package com.spontecorp.jsf;

import com.spontecorp.entity.Curso;
import com.spontecorp.entity.Persona;
import com.spontecorp.entity.PersonaCurso;
import com.spontecorp.jsf.util.Emailer;
import com.spontecorp.jsf.util.JpaUtilities;
import com.spontecorp.jsf.util.JsfUtil;
import com.spontecorp.jsf.util.PaginationHelper;
import com.spontecorp.session.CursoFacade;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

@ManagedBean(name = "cursoController")
@SessionScoped
public class CursoController implements Serializable {
    
    private Curso current;
    private List<Curso> listCurso;
    private transient DataModel items = null;
    @EJB
    private com.spontecorp.session.CursoFacade ejbFacade;
    @EJB
    private com.spontecorp.session.CursoFacadeExt ejbCursoFacadeExt;
    @EJB
    private com.spontecorp.session.PersonaFacade ejbPersonaFacade;
    @EJB
    private com.spontecorp.session.PersonaFacadeExt ejbPersonaFacadeExt;
    @EJB
    private com.spontecorp.session.PersonaCursoFacade ejbPersonaCursoFacade;
    @EJB
    private com.spontecorp.session.PersonaCursoFacadeExt ejbPersonaCursoFacadeExt;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private String nombre;
    private String apellido;
    private String email;
    private Curso selected;
    private int idSelectedCurso;
    private boolean valid = false;
    private boolean showProgressBar = false;
    
    public CursoController() {
    }
    
    public Curso getSelected() {
        if (current == null) {
            current = new Curso();
            //selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Curso curso) {
        current = curso;
    }
    
    private CursoFacade getFacade() {
        return ejbFacade;
    }
    
    public PaginationHelper getPagination() {
        recreateModel();
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }
                
                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }
    
    public String prepareList() {
        recreateModel();
        return "List";
    }
    
    public String prepareView() {
        current = (Curso) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    
    public String prepareRegister() {
        FacesContext context = FacesContext.getCurrentInstance();
        int idCurso = context.getExternalContext().getRequestParameterMap().get("idCurso") != null ? Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("idCurso")) : -1;
        
        idSelectedCurso = idCurso;
        //current = (Curso) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        current = ejbFacade.find(idCurso);
        return "/content/register";
    }
    
    public String prepareCreate() {
        current = new Curso();
        selectedItemIndex = -1;
        return "Create";
    }

    /**
     * Muestra información ala Usuario del Status de su Registro
     *
     * @return
     */
    public String prepareMessage() {
        return "message";
    }

    /**
     * Método para realizar el Proceso de Inscripción de una Persona en un Curso
     *
     * @return
     */
    public String registerPerson() {
        try {

            //Verifico si el email existe
            System.out.println("Email: " + getEmail());
            Persona person = new Persona();
            person = ejbPersonaFacadeExt.findEmail(getEmail());
            showProgressBar = true;
            if (person == null) {
                Persona persona = new Persona();
                PersonaCurso personaCurso = new PersonaCurso();

                //Se setean los Datos de la Persona
                persona.setNombre(getNombre());
                persona.setApellido(getApellido());
                persona.setEmail(getEmail());

                //Se crea la Persona
                ejbPersonaFacade.create(persona);

                //Se setean los Datos de la relación Curso-Persona
                current = ejbFacade.find(idSelectedCurso);
                personaCurso.setCursoId(current);
                personaCurso.setPersonaId(persona);
                personaCurso.setStatus(JpaUtilities.PENDIENTE);
                personaCurso.setFecha(new Date());

                //Se guarda la Relación Curso-Persona
                ejbPersonaCursoFacade.create(personaCurso);
                
                JsfUtil.addSuccessMessage("Regístro Creado con éxito!");

                //Se envía el correo eletrónico 
                sendEmail(persona);
                
            } else {
                PersonaCurso personCurso = ejbPersonaCursoFacadeExt.findPersonaCurso(person);
                if (personCurso.getStatus() == JpaUtilities.NO_ASISTIO) {

                    //Se setean los Datos de la relación Curso-Persona
                    current = ejbFacade.find(idSelectedCurso);
                    personCurso.setCursoId(current);
                    personCurso.setFecha(new Date());
                    personCurso.setStatus(JpaUtilities.PENDIENTE);

                    //Se actualiza la Relación Curso-Persona
                    ejbPersonaCursoFacade.edit(personCurso);
                    
                    JsfUtil.addSuccessMessage("Regístro Creado con éxito! "
                            + "Revise su correo electrónico y confirme su asistencia al curso!");

                    //Se envía el correo eletrónico 
                    sendEmail(personCurso.getPersonaId());
                    
                } else if (personCurso.getStatus() == JpaUtilities.PENDIENTE) {
                    JsfUtil.addSuccessMessage("Ud. ya se encuentra Regístrado en el Sistema. "
                            + "Su status de Registro aún no ha sido confirmado, por favor revise "
                            + "su correo eléctrónico y confirme su asistencia al Curso.");
                } else if (personCurso.getStatus() == JpaUtilities.INSCRITO) {
                    JsfUtil.addSuccessMessage("Ud. ya se encuentra Regístrado en el Sistema. "
                            + "Su Registro ya se encuentra Activado.");
                } else if (personCurso.getStatus() == JpaUtilities.ASISTIO) {
                    JsfUtil.addSuccessMessage("Ud. ya se encuentra Regístrado en el Sistema. "
                            + "Su status de Registro confirma que Ud. ya asistió al Curso.");
                }
            }
            person = null;
            recreateModel();
            return prepareMessage();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Problemas al crear el Registro");
            return null;
        }
    }

    /**
     * Método para enviar el Correo Electrónico
     *
     * @param persona
     */
    public void sendEmail(Persona persona) {
        //Se envía el correo eletrónico 
        Emailer emailer = new Emailer();
        //Se arma la url base
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String vinculo = origRequest.getRequestURL().toString();
        vinculo = vinculo.replace("/content/register.xhtml", "/content/message.xhtml");
        emailer.setVinculo(vinculo);

        //Se configuran los datos del participante
        DateFormat formatFecha = DateFormat.getDateInstance(DateFormat.FULL);
        SimpleDateFormat formatHora = new SimpleDateFormat("hh:mm:ss aa");
        emailer.setPara(persona.getEmail());
        emailer.setNombre(persona.getNombre() + " " + persona.getApellido());
        emailer.setHorario(formatFecha.format(current.getFecha()) + "  a las " + formatHora.format(current.getHora()) + " en " + current.getLugar());
        emailer.setIdPersona(persona.getId().toString());
        emailer.setIdCurso(current.getId().toString());
        //Se envía la información!
        emailer.send();
    }
    
    public String create() {
        try {
            getFacade().create(current);
            
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("CursoCreated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    public String prepareEdit() {
        current = (Curso) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }
    
    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("CursoUpdated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    public String destroy() {
        current = (Curso) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }
    
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("CursoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
        }
    }
    
    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    /**
     * Modificación del DataModel para mostrar la Lista en PrimeFaces
     *
     * @return
     */
    public DataModel getItems() {
        //recreateModel();
        if (items == null) {
            items = new ListDataModel(getFacade().findAll());
        }
        return items;
    }
    
    private void recreateModel() {
        items = null;
        nombre = null;
        apellido = null;
        email = null;
        showProgressBar = false;
    }
    
    private void recreatePagination() {
        pagination = null;
    }
    
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }
    
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getIdSelectedCurso() {
        return idSelectedCurso;
    }
    
    public boolean isShowProgressBar() {
        return showProgressBar;
    }
    
    public void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }
    
    public void setIdSelectedCurso(int idSelectedCurso) {
        this.idSelectedCurso = idSelectedCurso;
    }
    
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }
    
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }
    
    public List<Curso> getListCurso() {
        listCurso = null;
        int capacidad = 0;
        int inscritos = 0;
        int totalDisponible = 0;
        if (listCurso == null) {
            listCurso = ejbCursoFacadeExt.getCursosActivos();
            for (Curso option : listCurso) {
                if (option.getCapacidad() != null) {
                    capacidad = option.getCapacidad();
                } else {
                    capacidad = 0;
                }
                inscritos = ejbPersonaCursoFacadeExt.findInscritos(option);
                totalDisponible = capacidad - inscritos;
                option.setTotalDisponible(totalDisponible);
                if (option.getTotalDisponible() <= 0) {
                    option.setDiponibilidad(false);
                }
            }
        }
        return listCurso;
    }
    
    public void setListCurso(List<Curso> listCurso) {
        this.listCurso = listCurso;
    }
    
    @FacesConverter(forClass = Curso.class)
    public static class CursoControllerConverter implements Converter {
        
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CursoController controller = (CursoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cursoController");
            return controller.ejbFacade.find(getKey(value));
        }
        
        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }
        
        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }
        
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Curso) {
                Curso o = (Curso) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Curso.class.getName());
            }
        }
    }
}
