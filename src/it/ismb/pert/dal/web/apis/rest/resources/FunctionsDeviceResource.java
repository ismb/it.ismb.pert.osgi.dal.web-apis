package it.ismb.pert.dal.web.apis.rest.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

/**
 * A Restlet resource used to get all functions from a specified device 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class FunctionsDeviceResource extends ServerResource {

    @SuppressWarnings("unchecked")
	@Get
    public String represent() {
    	
    	Gson gson = new Gson();
   
    	//Get device uid parameter
    	String device_uid_prop=(String) getRequest().getAttributes().get("device_uid");
    	
    	//Get function services matching with device uid
    	BundleContext bc = FrameworkUtil.getBundle(FunctionsDeviceResource.class).getBundleContext();
    	ServiceReference[] functionRefs = null;
		try {
			System.out.println("Device uid property:"+device_uid_prop);
			String filterString = "("+Function.SERVICE_DEVICE_UID+"="+URLDecoder.decode(device_uid_prop,"UTF-8")+")";
			Filter filter=bc.createFilter(filterString);
			System.out.println(filter);
			functionRefs = (ServiceReference[]) bc.getServiceReferences(
				    Function.class.getName(),
				    filterString);
			
			if (null == functionRefs) {
				System.out.println("No services ref");
			    return null; // no such services
			}
			System.out.println("service references...");
			for (int i = 0; i < functionRefs.length; i++) {
				System.out.println(functionRefs[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		//write the map back to the client
		return gson.toJson(functions);
    }
}