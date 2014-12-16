package it.ismb.pert.dal.web.apis.rest.resources;

import it.ismb.pert.dal.web.apis.rest.pojos.Argument;
import it.ismb.pert.dal.web.apis.rest.pojos.InvokeRequest;
import it.ismb.pert.dal.web.apis.rest.pojos.InvokeResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A Restlet resource used to invoke operations on functions using reflection
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class OperationInvokeResource extends BaseServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(OperationInvokeResource.class);
			
	@Post("json")
    public String represent( Representation entity ) {
    	

    	Gson gson = new Gson();
       	
    	//Get function uid from request
    	String function_uid_prop=(String) getRequest().getAttributes().get("function_uid");
   
    	//Resolve functions matching with specified UID 
    	BundleContext bc = FrameworkUtil.getBundle(OperationInvokeResource.class).getBundleContext();
    	InvokeResponse response=new InvokeResponse();
    	
    	//find service references
    	ServiceReference[] functionRefs = null;
		try {
			
			String filterString = "("+Function.SERVICE_UID+"="+URLDecoder.decode(function_uid_prop,"UTF-8")+")";
			Filter filter=bc.createFilter(filterString);
			LOG.info("Filter: {}",filter);
			functionRefs = (ServiceReference[]) bc.getServiceReferences(
				    Function.class.getName(),
				    filterString);
			
		
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (null == functionRefs || functionRefs.length==0) {
			LOG.info("No service reference found with function UID: {}",function_uid_prop);
			response.setCode(404);
		    response.setMessage("Function not found");
		}else{
			LOG.info("Found {} service references",functionRefs.length);
			for (int i = 0; i < functionRefs.length; i++) {
				LOG.info("Service reference: {}",functionRefs[i]);
			}
			InvokeRequest invReq = null;
			String operation=null;
			try {
				invReq = gson.fromJson(entity.getText(),InvokeRequest.class);
				operation=invReq.getOperation();
			} catch (Exception e1) {
				LOG.error("Invalid request body");
				response.setCode(400);
			    response.setMessage("Invalid request");
			    e1.printStackTrace();
			}
	    	
		
			//get the Function service reference
			Object f=bc.getService(functionRefs[0]);
			
			//get the implementing class 
			Class clazz=f.getClass();
			
			
			//invoke the method
			Object o=null;
			// get the method corresponding to the desired command...
			
			Method meth = null;
			Argument[] params=invReq.getArguments();
			try{
				
				Class[] ptypes=this.paramsClassArray(params);
				meth = clazz.getDeclaredMethod(invReq.getOperation(),
				ptypes);
				
				
				// execute the command
				if (meth != null)
				{
					Object[] paramValues=null;
					if(params!=null)
					{
						paramValues=new Object[params.length];
						for(int i=0;i<params.length;i++)
						{
							paramValues[i]=params[i].getValue();
						}
					}

					o=meth.invoke(clazz.cast(f), paramValues);
					response.setCode(200);
					response.setResult(o);
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
				response.setCode(500);
				LOG.error("Error invoking operation: {}",ex);
				if(ex.getCause()!=null)
				{
					response.setMessage("Error invoking operation - "+ex.getCause().getClass().getName()+":"+ex.getCause().getMessage());
				}else{
					response.setMessage("Error invoking operation - "+ex.getClass().getName()+":"+ex.getMessage());
				}
			}finally{
				//unget the service reference
				bc.ungetService(functionRefs[0]);
			}
					
		}
		
		this.addCustomHeaders();
		
        return gson.toJson(response);
    }

	private Class[] paramsClassArray(Argument[] params) {
		if(params==null)
			return null;
		Class[] classes=new Class[params.length];
		for(int i=0;i<params.length;i++)
		{
			try {
				classes[i]=Class.forName(params[i].getType());
				try{
					params[i].setValue(classes[i].cast(params[i].getValue()));
				}catch(ClassCastException cce)
				{
					Constructor constructor;
					try {
						//treat BigDecimal
						if(classes[i].getName().equals(BigDecimal.class.getName()))
						{
							params[i].setValue(new BigDecimal((Double.valueOf((String) params[i].getValue()))));
						}else if(classes[i].getName().equals(Short.class.getName()))
						{
							//handle Short values
							params[i].setValue(Short.parseShort((String) params[i].getValue()));
						}else if(classes[i].getName().equals(Integer.class.getName()))
						{
							//handle Short values
							params[i].setValue(((Double)(params[i].getValue())).intValue());
						}else if(classes[i].getName().equals(Boolean.class.getName()))
						{
							params[i].setValue( Boolean.valueOf((String)(params[i].getValue())));
						}else{
							//try to invoke a constructor for the type
							constructor = classes[i].getConstructor(params[i].getValue().getClass());
							params[i].setValue(constructor.newInstance(params[i].getValue()));
						}
						
					} catch (Exception e) {
						LOG.error("Error converting params: {}",e);
					}
					
				}
			} catch (ClassNotFoundException e) {
				LOG.error("Class not found: {}",e);
			}
		}
		return classes;
	}

}