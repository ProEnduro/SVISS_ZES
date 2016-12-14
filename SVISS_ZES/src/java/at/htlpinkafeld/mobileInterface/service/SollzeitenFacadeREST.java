/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Base_DAO;
import at.htlpinkafeld.mobileInterface.authorization.Secured;
import at.htlpinkafeld.pojo.SollZeit;
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
@Path("sollzeiten")
public class SollzeitenFacadeREST extends AbstractFacade<SollZeit> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;

    public SollzeitenFacadeREST() {
        super(SollZeit.class);
        dao = DAOFactory.getDAOFactory().getSollZeitenDAO();
    }

    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SollZeit create(SollZeit entity) {
        return super.create(entity);
    }

    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(SollZeit entity) {
        super.edit(entity);
    }

    @DELETE
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(SollZeit entity) {
        super.remove(entity);
    }

    @GET
//    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<SollZeit> findAll() {
        List<SollZeit> sollZeiten = super.findAll();
        return sollZeiten;
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }

    @Override
    public SollZeit create(String jsonEntity) {
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
