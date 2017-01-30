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
import at.htlpinkafeld.pojo.UserHistoryEntry;
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
@Path("userholidayovertimehistory")
public class UserholidayovertimehistoryFacadeREST extends AbstractFacade<UserHistoryEntry> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private final Base_DAO dao;

    public UserholidayovertimehistoryFacadeREST() {
        super(UserHistoryEntry.class);
        dao = DAOFactory.getDAOFactory().getUserHistoryDAO();
    }

    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserHistoryEntry create(UserHistoryEntry entity) {
        UserHistoryEntry uhe = super.create(entity);
        return uhe;
    }

    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(UserHistoryEntry entity) {
        super.edit(entity);
    }

    @PATCH
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(UserHistoryEntry entity) {
        super.remove(entity);
    }

    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<UserHistoryEntry> findAll() {
        List<UserHistoryEntry> historyEntrys = super.findAll();
        for (UserHistoryEntry uhe : historyEntrys) {
            uhe.setUser(new User(uhe.getUser()));
        }
        return historyEntrys;
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }
}
