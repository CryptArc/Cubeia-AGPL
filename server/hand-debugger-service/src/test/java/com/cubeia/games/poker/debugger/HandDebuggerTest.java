package com.cubeia.games.poker.debugger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;



public class HandDebuggerTest {

	@Mock ServiceContext context;
	
	@Mock ServiceRegistry registry;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(context.getParentRegistry()).thenReturn(registry);
	}
	
	@Test
	public void testBootstrapping() throws SystemException, InterruptedException {
		HandDebuggerFacade facade = new HandDebuggerFacade();
		facade.init(context);
		
		facade.start();
		
		
		Thread.sleep(100000000);
	}
	
}
