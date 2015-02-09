# Interacting with the Smart Home through the DAL WEB APIs

This README file provides instructions on how to interact with the Energy@home eco-sys through JEMMA and its REST APIs (more precisely known as DAL WEB APIs - Device Abstraction Layer WEB APIs). You can use it to start developing your Smart Home applications.

**Something not clear ? Something not working on your System ?** Ask for help on the JEMMA General Mailing list. Click [here](http://ismb.github.io/jemma/devteam.html) to join.
 
The DAL WEB APIs provide REST and WebSocket access to Devices, Functions and Events available in OSGi Device Abstraction Layer.

REST APIs are used to provide access to Device and Function services. Functions operations invoke and FunctionData editing is implemented using Java Reflection. 
WebSocket APIs provides a publish/subscribe service built upon OSGi EventAdmin 

You can test APIs with some fake devices downloading and unpacking [this zip file] (https://github.com/ismb/it.ismb.pert.osgi.dal.web-apis/releases/download/1.1/dal-web-apis-bin.zip).

Start the application running start.sh (on linux) or start.bat (on Windows)

point your browser to the address

	http://localhost:8080/virtualhome/index.html
	
This simple GUI will provide you some simple ways to interact with the APIs.
The code in "virtualhome/index.html" contains some examples of how to write an API javascript client.

##License

This project have been developed by:
 
* [Ivan Grimaldi](https://github.com/ivangrimaldi)
* [Federico Rizzo](https://github.com/rfederico)
* [Fabio Baravalle](https://github.com/Tinwor)

for [Istituto Superiore Mario Boella](http://www.ismb.it/) which holds the Copyright on the original code base.

The source code, unless specified otherwise in specific files, have been released under the GNU Lesser General Public License (LGPL) version 3.
LGPL conditions can be found on the GNU website.

##Interacting with REST APIs

Remember: Content-type must be set to "application/json" when a json is sent to the server 

### Getting devices list

Using this request, you can retrieve a list of all the available devices.
For every device, among other info, there is the indication of the device unique ID, which
can be used to directly access to the device and the indication of the device driver (ZigBee, Bluetooth, etc.).


#### HTTP request:
```
GET http://host:port/api/devices/
```
Example Request:
```
GET http://localhost/api/devices
```
Example Response:
```
[
    {
        "dal.device.status": 2,
        "dal.device.UID": "ZigBee:test123",
        "dal.device.driver": "ZigBee",
        "service.id": 28,
        "objectClass": [
            "org.osgi.service.dal.Device"
        ]
    }
]
```


### Getting device functions

This API is used to retrieve the list of the available functions supported by the device. For
example a Smart Plug has two functions: one to retrieve the energy consumption and another 
"boolean" function useful to change the status of the smart plug (ON/OFF). Every function
indicates the id, which can be used to access directly the function and the list of the operation
that can be invoked on the function.

#### HTTP request:
```
GET http://host:port/api/devices/${device_uid}/functions
```
Example Request:
```
GET http://localhost/api/devices/ZigBee:test123/functions
```
Example Response:
```
[
    {
        "CLASS": "ismb.pert.jemma.dummydevice.DummyFunction",
        "dal.function.device.UID": "ZigBee:test123",
        "service.id": 27,
        "dal.function.UID": "ZigBee:test123:testButton",
        "objectClass": [
            "org.osgi.service.dal.Function"
        ],
        "dal.function.operation.names": [
            "getData",
            "reverse",
            "setFalse",
            "setTrue"
        ]
    }
]
```

###Invoking operations

In order to invoke an operation on a function, you would use the function uri (http://host:port/functions/${function_uid}) and send an HTTP POST with the operation in the request body as application/json.
Hereafter, some example of operations invoked (the first one with a result returned).

### Getting data from a device button
#### HTTP request:
```
POST http://host:port/api/functions/${function_uid}
```
Example Request:
```
POST http://localhost/api/functions/ZigBee:test123:testButton

{"operation":"getData"}

```
Example Response:
```
{"value":false,"timestamp":1409234704982}
```

### Setting true the device button
#### HTTP request:
```
POST http://host:port/api/functions/${function_uid}
```
Example Request:
```
POST http://localhost/api/functions/ZigBee:test123:testButton

{"operation":"setTrue"}

```
Example Response:
```
{"code":200}
```




### Setting false the device button
#### HTTP request:
```
POST http://host:port/api/functions/${function_uid}
```
Example Request:
```
POST http://localhost/api/functions/ZigBee:test123:testButton

{"operation":"getData"}

```
Example Response:
```
{"code":200,"result":{"value":false,"timestamp":1410111194964}}
```

Or in case of error:

```
{"code":404,"message":"Function not found"}

```

or

```
{"code":500,"message":"Error invoking operation"}
```


### Setting a value for a function (e.g. LevelControl)
#### HTTP request:
```
POST http://host:port/api/functions/${function_uid}
```
Example Request:
```
POST http://localhost/functions/ZigBee:test123:testButton

{"operation":"setData",
"arguments": 
[
{"type":"java.math.BigDecimal","value":1}
]
}

```
Example Response:
```
{"code":200}
```

Or in case of error:

```
{"code":404,"message":"Function not found"}

```

or

```
{"code":500,"message":"Error invoking operation"}
```
 
 
 
 
## Subscribing to events using WebSockets
 
 The Websocket address to be specified is:
 
 ```
 ws://host:port/ws
 ```
 
 Once the WebSocket connection have been set-up, the system expects a json object describing subscriptions to events.
 
 This example shows a message to be sent to the server if you want to subscribe to events related
 to function "ZigBee:SmartPlug1:OnOff" and property "data"
  
 ```
 {"dal.function.UID":"ZigBee:SmartPlug1:OnOff","dal.function.property.name":"data"}
 ```
 
  Wildcards (\*) are accepted! So you can subscribe to all events coming from every function sending the screen below
  
 ```
 {"dal.function.UID":"*","dal.function.property.name":"*"}
 ```
 
 or you can subscribe to all events coming froma specific device
 
 ```
 {"dal.function.UID":"ZigBee:SmartPlug1:*","dal.function.property.name":"*"}
 ```
 
 
 Every time you specify a subscription string over the same connection, older subscriptions on the same connection are cancelled.
 
 If you specify an invalid subscription string, the connection will be closed and an error will be reported
 
 Events matching the specified filters will be written by the server on this WebSocket connection, like in the example below:
 
 
 ```
 {"topic":"org/osgi/service/dal/FunctionEvent/PROPERTY_CHANGED","properties":{"dal.function.UID":"ZigBee:SmartPlug1:OnOff","dal.function.property.value":{"value":false,"timestamp":1410703590988},"dal.function.property.name":"data"}}
 ```
 
 ## Using WebSocket to get Overload notifications
 
 If you are running JEMMA with APIs, you can get Overload status notifications through websocket. The URI is
 
 
 ```
  ws://host:port/wsoverload
 ```
 
 on this channel, the client will receive events according to the Overload status in the topic:
 
 ```
 ah/eh/overload/NO_OVERLOAD
 ```
 
 or
 
 ```
ah/eh/overload/CONTRACTUAL_WARNING
 ```
 
 or
 
 ```
 ah/eh/overload/FIRST_WARNING
 ```
 
 or
 
 ```
 ah/eh/overload/SECOND_WARNING
 ```
 ##License

This project have been developed by [Ivan Grimaldi](https://github.com/ivangrimaldi) for [Istituto Superiore Mario Boella](http://www.ismb.it/) which holds the Copyright on the original code base. The source code, unless specified otherwise in specific files, have been released under the GNU Lesser General Public License (LGPL) version 3. LGPL conditions can be found on the GNU website.

 ISMB acknowledges that the JEMMA Open Source Project was partially supported by [GreenCom](http://www.greencom-project.eu/), FP7 ICT European Project (Contract No. 318213).
