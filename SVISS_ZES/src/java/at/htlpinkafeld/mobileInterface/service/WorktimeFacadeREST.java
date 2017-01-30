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
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.IstZeitService;
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
@Path("worktime")
public class WorktimeFacadeREST extends AbstractFacade<WorkTime> {

    @PersistenceContext(unitName = "SVISS_ZESPU")
    private Base_DAO dao;

    public WorktimeFacadeREST() {
        super(WorkTime.class);
        dao = DAOFactory.getDAOFactory().getWorkTimeDAO();
    }

    @POST
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public WorkTime create(WorkTime entity) {
        IstZeitService.addIstTime(entity);

        return entity;
    }

    @PUT
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(WorkTime entity) {
        super.edit(entity);
    }

    @PATCH
    @Secured
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void remove(WorkTime entity) {
        super.remove(entity);
    }

    @GET
    @Secured
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<WorkTime> findAll() {
        List<WorkTime> workTimes = super.findAll();
        for (WorkTime wt : workTimes) {
            wt.setUser(new User(wt.getUser()));
        }
        return workTimes;
    }

    @Override
    protected Base_DAO getDAO() {
        return dao;
    }
}
