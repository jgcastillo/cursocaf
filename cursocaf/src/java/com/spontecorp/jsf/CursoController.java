package com.spontecorp.jsf;

import com.spontecorp.entity.Curso;
import com.spontecorp.entity.Persona;
import com.spontecorp.entity.PersonaCurso;
import com.spontecorp.jsf.util.JpaUtilities;
import com.spontecorp.jsf.util.JsfUtil;
import com.spontecorp.jsf.util.PaginationHelper;
import com.spontecorp.session.CursoFacade;

import java.io.Serializable;
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

@ManagedBean(name = "cursoController")
@SessionScoped
public class CursoController implements Serializable {

    private Curso current;
    private List<Curso> listCurso;
    private transient DataModel items = null;
    @EJB
    private com.spontecorp.session.CursoFacade ejbFacade;
    @EJB
    private com.spontecorp.session.PersonaFacade ejbPersonaFacade;
    @EJB
    private com.spontecorp.session.PersonaCursoFacade ejbPersonaCursoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private String nombre;
    private String apellido;
    private String email;
    private Curso selected;
    private int idSelectedCurso;

    public CursoController() {
    }

    public Curso getSelected() {
        if (current == null) {
            current = new Curso();
            //selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Curso curso){
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
    
    public String prepareMessage() {
        return "message";
    }

    public String registerPerson() {
        try {
            //current = (Curso)getItems().getRowData();
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
            
            recreateModel();
            JsfUtil.addSuccessMessage("Regístro Creado con éxito!");
            return prepareMessage();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Problemas al crear el Registro");
            return null;
        }
    }
    
    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("CursoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Curso) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("CursoUpdated"));
            return "View";
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
        listCurso = ejbFacade.findAll();
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
