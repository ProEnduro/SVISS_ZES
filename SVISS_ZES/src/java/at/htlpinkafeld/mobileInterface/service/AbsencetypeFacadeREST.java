/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Base_DAO;
import at.htlpinkafeld.mobileInterface.authorization.Secured;
import at.htlpinkafeld.pojo.AbsenceType;
import java.util.List;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Martin Six
 */
@javax.ejb.Stateless
@Path("absencetype")
public class AbsencetypeFacadeREST extends AbstractFacade<AbsenceType> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;

    public AbsencetypeFacadeREST() {
        super(AbsenceType.class);
        dao = DAOFactory.getDAOFactory().getAbsenceTypeDAO();
    }

    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public AbsenceType create(AbsenceType entity) {
        return super.create(entity);
    }

    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(AbsenceType entity) {
        super.edit(entity);
    }

    @DELETE
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(AbsenceType entity) {
        super.remove(entity);
    }

    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<AbsenceType> findAll() {
        return super.findAll();
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }

    @Override
    public AbsenceType create(String jsonEntity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit(String jsonEntity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(String jsonEntity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
