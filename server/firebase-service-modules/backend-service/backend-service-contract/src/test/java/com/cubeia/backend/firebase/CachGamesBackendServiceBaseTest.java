package com.cubeia.backend.firebase;

import static com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse.ErrorCode.EXTERNAL_CALL_FAILED;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.service.ServiceRouter;

public class CachGamesBackendServiceBaseTest {

	@Mock
	private ServiceRouter router;
	
	@Mock
	private CashGamesBackend backend;

	private CashGamesBackendServiceBase service;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new CashGamesBackendServiceBase(1, 1) {
			
			@Override
			protected ServiceRouter getServiceRouter() {
				return router;
			}
			
			@Override
			protected CashGamesBackend getCashGamesBackend() {
				return backend;
			}
		};
	}
	
	@Test
	public void announceSuccess() throws Exception {
		AnnounceTableRequest req = new AnnounceTableRequest(new TableId(1, 1));
		AnnounceTableResponse resp = Mockito.mock(AnnounceTableResponse.class);
		Mockito.when(backend.announceTable(req)).thenReturn(resp);
		ArgumentCaptor<GameAction> capt = ArgumentCaptor.forClass(GameAction.class);
		service.announceTable(req);
		Thread.sleep(100);
		Mockito.verify(router).dispatchToGame(Mockito.anyInt(), capt.capture());
		GameObjectAction action = (GameObjectAction) capt.getValue();
		Assert.assertTrue(action.getAttachment() instanceof AnnounceTableResponse);
	}
	
	@Test
	public void announceFailed() throws Exception {
		AnnounceTableRequest req = new AnnounceTableRequest(new TableId(1, 1));
		Mockito.when(backend.announceTable(req)).thenThrow(new AnnounceTableFailedException("kkk", EXTERNAL_CALL_FAILED));
		ArgumentCaptor<GameAction> capt = ArgumentCaptor.forClass(GameAction.class);
		service.announceTable(req);
		Thread.sleep(100);
		Mockito.verify(router).dispatchToGame(Mockito.anyInt(), capt.capture());
		GameObjectAction action = (GameObjectAction) capt.getValue();
		AnnounceTableFailedResponse resp = (AnnounceTableFailedResponse) action.getAttachment();
		Assert.assertEquals(EXTERNAL_CALL_FAILED, resp.getErrorCode());
	}
}
