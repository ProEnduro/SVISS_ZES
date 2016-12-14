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
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Path("user")
public class UserFacadeREST extends AbstractFacade<User> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;
    private ObjectMapper mapper;

    public UserFacadeREST() {
        super(User.class);
        dao = DAOFactory.getDAOFactory().getUserDAO();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    }

    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Override
    public User create(String entity) {
        User u = null;
        try {
            u = mapper.readValue(entity, UserImpl.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.create(u);
    }

    @PUT
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Override
    public void edit(String entity) {
        User u = null;
        try {
            u = mapper.readValue(entity, UserImpl.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.edit(u);
    }

    @PATCH
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Override
    public void remove(String entity) {
        User u = null;
        try {
            u = mapper.readValue(entity, UserImpl.class);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.remove(u);
    }

    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> findAll() {
        List<User> users = super.findAll();
        List<User> usersImpl = new ArrayList<>();
        for (User u : users) {
            usersImpl.add(new UserImpl(u));
        }
        return usersImpl;
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }
}
