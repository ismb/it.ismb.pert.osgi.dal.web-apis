package it.ismb.pert.dal.web.apis.websocket;

import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * WebSocket Servlet implementation. Uses OSGi ComponentFactory to instantiate handlers
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class APIsWebSocketHandler extends WebSocketServlet{
	
	private int webSocketClients=0;
	private ComponentFactory webSocketFactory;
		
	public APIsWebSocketHandler(ComponentFactory webSocketFactory) {
		this.webSocketFactory=webSocketFactory;
	}

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest servlet, String protocol) {
		ComponentInstance instance=webSocketFactory.newInstance(new Hashtable());
		return  (WebSocket) instance.getInstance();
	}


	
}
