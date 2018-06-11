/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.proposalAPI;

import java.io.File;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author SVISS_NES
 */
@Path("/proposal")
public class ProposalRestAPI {

    String BASEPATH = "/home/share";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFolder(Folder f) {
        File file = new File(BASEPATH + "/SVISS-SALES/Sales/" + f.getFolderName());
        Boolean success = file.mkdirs();

        if (success) {
            f.setFolderName(file.getAbsolutePath());
            return Response.ok(f).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}
