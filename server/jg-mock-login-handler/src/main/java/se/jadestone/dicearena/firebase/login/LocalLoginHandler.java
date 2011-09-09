package se.jadestone.dicearena.firebase.login;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.service.network.protocol.LoginRequestPayloadStruct;
import se.jadestone.dicearena.service.network.protocol.LoginResponsePayloadStruct;
import se.jadestone.dicearena.service.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.service.network.protocol.Enums.LoginStatus;

import com.cubeia.firebase.api.action.local.LoginRequestAction;
import com.cubeia.firebase.api.action.local.LoginResponseAction;
import com.cubeia.firebase.api.login.LoginHandler;
import com.cubeia.firebase.api.login.LoginLocator;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;

public class LocalLoginHandler implements LoginLocator, LoginHandler, Service {

	Logger log = Logger.getLogger(this.getClass());
	
	AtomicInteger counter = new AtomicInteger(1);
	
	@Override
	public LoginHandler locateLoginHandler(LoginRequestAction request) {
		return this;
	}

	@Override
	public LoginResponseAction handle(LoginRequestAction request) {
	    log.debug("handle login request");
	    log.warn("login mocked!");
	    
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        
        byte[] requestPayloadData = request.getData();
        
        ProtocolObject object;
        try {
            object = serializer.unpack(ByteBuffer.wrap(requestPayloadData));
        } catch (IOException e) {
            String msg = "bad incoming login request data";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        
        if (!(object instanceof LoginRequestPayloadStruct)) {
            throw new RuntimeException("Illegal request payload type: " + object.getClass().getName());
        }
        LoginRequestPayloadStruct requestPayloadStruct = (LoginRequestPayloadStruct) object;

        
        // TODO: check credentials here!
		
		// TODO: mocked login response
        LoginResponsePayloadStruct responsePayloadStruct = new LoginResponsePayloadStruct(
            requestPayloadStruct.requestIdentifier, 
            LoginStatus.ACCEPTED, 
            UUID.randomUUID().toString());  
        byte[] responsePayload;
        try {
            responsePayload = serializer.packArray(responsePayloadStruct);
        } catch (IOException ee) {
            log.error("Error packing response payload for failed login.", ee);
            responsePayload = null;
        }

        LoginResponseAction response = new LoginResponseAction(true, request.getUser(), counter.getAndIncrement());
        response.setData(responsePayload);
        
		log.debug("Login user["+request.getUser()+"@"+request.getRemoteAddress()+"] with id["+response.getPlayerid()+"]");
		return response;
	}
	
	// ---- UNUSED SERVICE METHODS ----
	
	@Override
	public void init(ServiceContext con) throws SystemException {}

	@Override
	public void destroy() {}

	@Override
	public void start() {}

	@Override
	public void stop() {}

	@Override
	public void init(ServiceRegistry serviceRegistry) {}

}
