package it.ismb.pert.dal.web.apis.rest.application;

import it.ismb.pert.dal.web.apis.rest.resources.DevicesResource;
import it.ismb.pert.dal.web.apis.rest.resources.FunctionsDeviceResource;
import it.ismb.pert.dal.web.apis.rest.resources.OperationInvokeResource;


import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * Class implementind Restlet Application, used to attache all used Resources to restlet router
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JemmaApplication extends Application{

	/**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot()  {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        //register restlet resources
        
        router.attach("/devices", DevicesResource.class); //get devices list
        router.attach("/devices/{device_uid}/functions", FunctionsDeviceResource.class); //get device functions
        router.attach("/functions/{function_uid}", OperationInvokeResource.class); //invoke function method
 
        return router;
    }
}

