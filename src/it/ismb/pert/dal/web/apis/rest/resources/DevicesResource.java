package it.ismb.pert.dal.web.apis.rest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

/**
 * A Restlet resource returning the list of all devices
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class DevicesResource extends BaseServerResource {

	@Get("json")
    public String represent() {
    	
    	Gson gson = new Gson();
    	
    	//Get all devices services
    	BundleContext bc = FrameworkUtil.getBundle(DevicesResource.class).getBundleContext();
    	ServiceReference[] deviceRefs = null;
		try {
			deviceRefs = (ServiceReference[]) bc.getServiceReferences(
				    Device.class.getName(),
				    null);
			
			if (null == deviceRefs) {
				System.out.println("No devices ref");
			    return null; // no such services
			}
			System.out.println("Device references...");
			for (int i = 0; i < deviceRefs.length; i++) {
				System.out.println(deviceRefs[i]);
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		//write devices to client
		return gson.toJson(devs);
    }
}