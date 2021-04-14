/*
 * */
package com.synectiks.process.server.xformation.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.service.AlertService;

import io.swagger.annotations.Api;

//@RequiresAuthentication
@Api(value = "Xformation/Alert", description = "Manage all xformation alerts")
@Path("/xformation/alert")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlertController extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(AlertController.class);

//    private final AlertRepository alertRepository;
    
//    @Inject
//	public AlertController(AlertRepository alertRepository) {
//		this.alertRepository = alertRepository;
//	}
    
    @GET
    public void checkSpringContext() {
    	
    	AlertService ar = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
    	LOG.info("Alert Service instance : {} ",ar);
    	ar.getAllAlerts();
//    	List<Alert> listAlert = alertRepository.findAll();
//    	for(Alert al: listAlert) {
//    		LOG.info("Alert object ::::::: {}", al);
//    	}
    }
//    @GET
//    @Timed
//    @ApiOperation(value = "Get a list of all alerts originated from grafana dashboards")
//    public List<Alert> getAllAlerts() {
//    	LOG.info("Start getAllAlert");
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	List<Alert> list = cs.getAllAlerts();
//    	LOG.info("End getAllAlert");
//    	return list;
//    }
//    
//    @GET
//    @Path("/{id}")
//    @ApiOperation("Get an alert for a given alert id")
//    public Alert getAlert(@ApiParam(name = "id") @PathParam("id") @NotBlank Long id) {
//    	LOG.info("Start controller getAlert. Alert id: "+id);
////        checkPermission(RestPermissions.EVENT_NOTIFICATIONS_READ, alertId);
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	Alert alert = cs.getAlert(id);
//    	LOG.info("End controller getAlert. Alert id: "+id);
//    	return alert;
//    }
//    
//    @GET
//    @Path("/{guid}")
//    @ApiOperation("Get an alert for a given guid")
//    public Alert getAlert(@ApiParam(name = "guid") @PathParam("guid") @NotBlank String guid) {
//    	LOG.info("Start controller getAlert. Alert guid: "+guid);
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	Alert alert = cs.getAlert(guid);
//    	LOG.info("End controller getAlert. Alert guid: "+guid);
//    	return alert;
//    }
//    
//    @PUT
//    @ApiOperation("Update an alert")
//    public Response updateAlert(@ApiParam(name = "JSON Body") ObjectNode obj, @Context UserContext userContext) {
//    	LOG.info("Start controller updateAlert");
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	Alert alert = cs.updateAlert(obj);
//    	List<Alert> list = cs.getAllAlerts();
//    	sortAlert(list);
//    	LOG.info("End controller updateAlert");
//    	return Response.ok().entity(list).build();
//    }
//    
//    @DELETE
//    @Path("/{alertId}")
//    @ApiOperation("Delete an alert on alert id")
//    public Response deleteAlert(@ApiParam(name = "alertId") @PathParam("alertId") @NotBlank Long alertId) {
//    	LOG.info("Start controller deleteAlert. Alert id: "+alertId);
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	cs.deleteAlert(alertId);
//    	List<Alert> list = cs.getAllAlerts();
//    	sortAlert(list);
//    	LOG.info("End controller deleteAlert. Alert id: "+alertId);
//    	return Response.ok().entity(list).build();
//    }
//    
//    @DELETE
//    @Path("/{guid}")
//    @ApiOperation("Delete an alert on guid")
//    public Response deleteAlert(@ApiParam(name = "guid") @PathParam("guid") @NotBlank String guid) {
//    	LOG.info("Start controller deleteAlert. Alert guid: "+guid);
//    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
//    	cs.deleteAlert(guid);
//    	List<Alert> list = cs.getAllAlerts();
//    	sortAlert(list);
//    	LOG.info("End controller deleteAlert. Alert guid: "+guid);
//    	return Response.ok().entity(list).build();
//    }
//    
//    private void sortAlert(List<Alert> allAlList){
//    	if(allAlList.size() >0) {
//    		LOG.debug("Sorting alerts on updated on");
//			Collections.sort(allAlList, new Comparator<Alert>() {
//				@Override
//				public int compare(Alert m1, Alert m2) {
//					Instant val1 = Instant.parse(m1.getUpdatedOn().toString());
//					Instant val2 = Instant.parse(m2.getUpdatedOn().toString());
//					return val2.compareTo(val1);
//				}
//			});
//		}
//    }
    
}
