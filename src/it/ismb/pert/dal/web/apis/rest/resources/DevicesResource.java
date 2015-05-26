package it.ismb.pert.dal.web.apis.rest.resources;

import it.ismb.pert.dal.web.apis.rest.pojos.InvokeResponse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * A Restlet resource returning the list of all devices
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class DevicesResource extends BaseServerResource {
	private static final Logger LOG=LoggerFactory.getLogger(DevicesResource.class);
			
	@Get("json")
    public String represent() {
    	
    	Gson gson = new Gson();
    	
    	InvokeResponse resp=new InvokeResponse();
    	//initially set 200, overwrite in case of exceptions
    	resp.setCode(200);
    	
    	//Get all devices services
    	BundleContext bc = FrameworkUtil.getBundle(DevicesResource.class).getBundleContext();
    	ServiceReference[] deviceRefs = null;
		try {
			deviceRefs = (ServiceReference[]) bc.getServiceReferences(
				    Device.class.getName(),
				    null);
			
			if (null == deviceRefs) {
				LOG.info("No device reference found");
				resp.setResult(new Vector());
			    return gson.toJson(resp); // no such services
			}
			LOG.info("Found {} device references", deviceRefs.length);
			for (int i = 0; i < deviceRefs.length; i++) {
				LOG.info("Service reference: {}",deviceRefs[i]);
			}
		} catch (InvalidSyntaxException e) {
			LOG.error("Invalid filter syntax: {}",e);
			resp.setCode(500);
			resp.setMessage("Invalid filter syntax");
			return gson.toJson(resp);
			
		}
			
		//Fill a map of devices parameters to be returned to the client
		List<Map<String,Object>> devs=new LinkedList<Map<String,Object>>();
		for(int i=0;i<deviceRefs.length;i++)
		{
			Map<String,Object> propMap=new HashMap<String,Object>();
			for(int j=0;j<deviceRefs[i].getPropertyKeys().length;j++)
			{
				//ignore propery objectClass 
				if(!deviceRefs[i].getPropertyKeys()[j].equals("objectClass"))
				{
					propMap.put(deviceRefs[i].getPropertyKeys()[j], deviceRefs[i].getProperty(deviceRefs[i].getPropertyKeys()[j]));
					bc.ungetService(deviceRefs[i]);
				}
			}
			devs.add(propMap);
			
		}
		
		this.addCustomHeaders();
		
		resp.setResult(devs);
		
		//write devices to client
		return gson.toJson(resp);
    }
}