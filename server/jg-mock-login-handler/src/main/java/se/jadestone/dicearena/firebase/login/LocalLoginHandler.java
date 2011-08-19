package se.jadestone.dicearena.firebase.login;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.local.LoginRequestAction;
import com.cubeia.firebase.api.action.local.LoginResponseAction;
import com.cubeia.firebase.api.login.LoginHandler;
import com.cubeia.firebase.api.login.LoginLocator;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;

public class LocalLoginHandler implements LoginLocator, LoginHandler, Service {

	Logger log = Logger.getLogger(this.getClass());
	
	AtomicInteger counter = new AtomicInteger(1);
	
	@Override
	public LoginHandler locateLoginHandler(LoginRequestAction request) {
		return this;
	}

	@Override
	public LoginResponseAction handle(LoginRequestAction request) {
		LoginResponseAction response = new LoginResponseAction(true, request.getUser(), counter.getAndIncrement());
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
