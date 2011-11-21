package se.jadestone.dicearena.firebase.login;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.service.network.protocol.Enums.LoginStatus;
import se.jadestone.dicearena.service.network.protocol.LoginRequestPayloadStruct;
import se.jadestone.dicearena.service.network.protocol.LoginResponsePayloadStruct;
import se.jadestone.dicearena.service.network.protocol.ProtocolObjectFactory;

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
	
	@Override
	public LoginHandler locateLoginHandler(LoginRequestAction request) {
		return this;
	}

	@Override
	public LoginResponseAction handle(LoginRequestAction request) {
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        
        byte[] requestPayloadData = request.getData();
        
        if (requestPayloadData.length > 0) {
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
	        
//	        System.out.println(" --- LOGIN PARAMETERS ---");
//	        System.out.println("User: "+request.getUser());
//	        System.out.println("Password: "+request.getPassword());
//	        System.out.println("Credentials "+requestPayloadStruct.credential);
//	        System.out.println("partnerAccountId "+requestPayloadStruct.partnerAccountId);
//	        System.out.println("desiredNickName "+requestPayloadStruct.desiredNickName);
//	        System.out.println("partnerCode "+requestPayloadStruct.partnerCode);
//	        System.out.println("arenaPopulationId "+requestPayloadStruct.arenaPopulationId);
//	        System.out.println("-------------------");
	        
	        LoginResponseAction response = new LoginResponseAction(true, requestPayloadStruct.partnerAccountId, createUserIdFromString(requestPayloadStruct.partnerAccountId));
	        response.setData(responsePayload);
	        
	        log.debug("Login user["+request.getUser()+"@"+request.getRemoteAddress()+"] with id["+response.getPlayerid()+"]");
	        return response;
	        
        } else {
        	// We also want to support logins without Jadestone specific payload data plz
        	log.warn("Using login without JG Payload");
        	LoginResponseAction response = new LoginResponseAction(true, request.getUser(), createUserIdFromString(request.getUser()));
        	return response;
        }
	}
	
	// ---- UNUSED SERVICE METHODS ----
	
	private int createUserIdFromString(String credential) {
		return Math.abs(credential.hashCode());
	}

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
