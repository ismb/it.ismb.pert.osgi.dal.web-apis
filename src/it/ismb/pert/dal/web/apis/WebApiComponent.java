package it.ismb.pert.dal.web.apis;

import it.ismb.pert.dal.web.apis.rest.application.JemmaApplication;
import it.ismb.pert.dal.web.apis.websocket.APIsWebSocketHandler;
import it.ismb.pert.dal.web.apis.websocket.OverLoadsWebSocketHandler;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Web Apis main component. registers REST and WebSocket servlets with HttpService
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class WebApiComponent {

	public static final String REST_PATH="/api";
	public static final String WEBSOCKET_API_PATH="/ws";
	public static final String WEBSOCKET_OVERLOAD_PATH="/wsoverload";
	
	//the factory for WebSocket handlers, used when creating new WebSocket connections
	private ComponentFactory webSocketAPIFactory;
	
	private ComponentFactory webSocketOverloadFactory;
	
	private HttpService httpService;
	
	public void activate(ComponentContext context)
	{
		try {
		
			httpService.registerServlet(REST_PATH, new RestApplicationServlet(), null, null);
			httpService.registerServlet(WEBSOCKET_API_PATH, new APIsWebSocketHandler(webSocketAPIFactory), null, null);
			httpService.registerServlet(WEBSOCKET_OVERLOAD_PATH, new OverLoadsWebSocketHandler(webSocketOverloadFactory), null, null);
			httpService.registerResources("/virtualhome", "/virtualhome", null);
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	public void bindHttpService(HttpService httpService)
	{
		this.httpService=httpService;
	}
	
	public void unbindHttpService(HttpService httpService)
	{
		httpService.unregister(REST_PATH);
		httpService.unregister(WEBSOCKET_API_PATH);
		httpService=null;
	}
	
	public void bindAPIComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketAPIFactory=componentFactory;
	}
	
	public void unbindAPIComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketAPIFactory=null;
	}
	
	public void bindOverloadComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketOverloadFactory=componentFactory;
	}
	
	public void unbindOverloadComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketOverloadFactory=null;
	}
	
}
