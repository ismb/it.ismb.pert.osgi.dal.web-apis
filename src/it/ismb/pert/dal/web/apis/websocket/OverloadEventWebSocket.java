package it.ismb.pert.dal.web.apis.websocket;

import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A WebSocket handler. receives subscription filters and subscribes to DAL topics on OSGi EventAdmin
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class OverloadEventWebSocket implements EventHandler,WebSocket.OnTextMessage{

	private Connection connection;
	private ComponentContext context;
	private ServiceRegistration registration;
	private Gson gson;
	
	protected void activate(ComponentContext context)
	{
		this.context=context;
		gson=new Gson();
		registerEventHandler();
	}
	
	
	public void onClose(int closeCode, String message) {
		//unregister the eventhandler
		unregisterEventHandlerService();
	}

	public void onOpen(Connection connection) {
		this.connection=connection;
	}

	public void onMessage(String message) {
		//nothing to do
		
	}


	private void registerEventHandler() {

			
		//Prepare properties for event filter to be used with EventAdmin
		Hashtable properties=new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, "ah/eh/overload/*");
		
		
		//unregister previous event handler
		unregisterEventHandlerService();
		
		//register event handlers
		registration=this.context.getBundleContext().registerService(EventHandler.class.getName(), this, properties);
	}

	public void handleEvent(Event arg0) {
		//an event mathcing the filter specified by the client have been received		
		try {
			this.connection.sendMessage(gson.toJson(arg0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void unregisterEventHandlerService() {
		if(registration!=null)
		{
			try{
				registration.unregister();
			}catch(IllegalStateException e)
			{
				//do nothing, the service was unregistered before
			}
		}
		
	}

}
