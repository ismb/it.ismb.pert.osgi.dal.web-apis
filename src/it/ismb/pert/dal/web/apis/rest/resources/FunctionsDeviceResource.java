package it.ismb.pert.dal.web.apis.rest.resources;

import it.ismb.pert.dal.web.apis.rest.pojos.InvokeResponse;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * A Restlet resource used to get all functions from a specified device 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class FunctionsDeviceResource extends BaseServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(FunctionsDeviceResource.class);

	@Get
    public String represent() {
    	
    	Gson gson = new Gson();
    	
    	InvokeResponse resp=new InvokeResponse();
    	resp.setCode(200);
    	
    	//Get device uid parameter
    	String device_uid_prop=(String) getRequest().getAttributes().get("device_uid");
    	
    	//Get function services matching with device uid
    	BundleContext bc = FrameworkUtil.getBundle(FunctionsDeviceResource.class).getBundleContext();
    	ServiceReference[] functionRefs = null;
		try {
			LOG.debug("Device uid property: {}",device_uid_prop);
			String filterString = "("+Function.SERVICE_DEVICE_UID+"="+URLDecoder.decode(device_uid_prop,"UTF-8")+")";
			Filter filter=bc.createFilter(filterString);
			LOG.info("Filter: {}",filter);
			functionRefs = (ServiceReference[]) bc.getServiceReferences(
				    Function.class.getName(),
				    filterString);
			
			if (null == functionRefs) {
				LOG.error("No service function reference with deviceUID: {}",device_uid_prop);
			    resp.setResult(new Vector());
				return gson.toJson(resp); // no such services
			}
			LOG.info("Found {} service references",functionRefs.length);
			for (int i = 0; i < functionRefs.length; i++) {
				LOG.info("Service Reference: {}",functionRefs[i]);
			}
		} catch (Exception e) {
			LOG.error("Erro getting service references: {}",e);
			resp.setCode(500);
			resp.setMessage("Error getting function references: "+e.getMessage());
			return gson.toJson(resp);
		}
    	
    	//prepare a map of found functions
		List<Map<String,Object>> functions=new LinkedList<Map<String,Object>>();
		for(int i=0;i<functionRefs.length;i++)
		{
			Map<String,Object> propMap=new HashMap<String,Object>();
			for(int j=0;j<functionRefs[i].getPropertyKeys().length;j++)
			{
				propMap.put(functionRefs[i].getPropertyKeys()[j], functionRefs[i].getProperty(functionRefs[i].getPropertyKeys()[j]));
				bc.ungetService(functionRefs[i]);
			}
			functions.add(propMap);
			
		}
		
		this.addCustomHeaders();
		
		resp.setResult(functions);
		
		//write the map back to the client
		return gson.toJson(resp);
    }
}