/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Base_DAO;
import at.htlpinkafeld.mobileInterface.authorization.Secured;
import at.htlpinkafeld.mobileInterface.service.util.PATCH;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
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
@Path("absence")
public class AbsenceFacadeREST extends AbstractFacade<Absence> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;
    private ObjectMapper mapper;

    public AbsenceFacadeREST() {
        super();
        dao = DAOFactory.getDAOFactory().getAbsenceDAO();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    }

    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Absence create(Absence entity) {
        return super.create(entity);
    }

    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(Absence entity) {
        super.edit(entity);
    }

    @PATCH
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(Absence entity) {
        super.remove(entity);
    }

    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Absence> findAll() {
        List<Absence> absences = super.findAll();
        absences.forEach((a) -> {
            a.setUser(new User(a.getUser()));
        });
        return absences;
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }

}
