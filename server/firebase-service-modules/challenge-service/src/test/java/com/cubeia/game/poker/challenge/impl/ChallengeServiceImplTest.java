package com.cubeia.game.poker.challenge.impl;


import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.service.ServiceRegistryAdapter;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.firebase.api.service.config.ClusterConfigProviderContract;
import com.cubeia.firebase.api.service.config.ClusterConfigProviderContractAdapter;
import com.cubeia.firebase.guice.inject.FirebaseModule;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.game.poker.challenge.api.Challenge;
import com.cubeia.game.poker.challenge.api.ChallengeConfiguration;
import com.cubeia.game.poker.challenge.api.ChallengeManager;
import com.cubeia.game.poker.challenge.api.ChallengeNotFoundException;
import com.cubeia.games.challenge.io.protocol.AcceptChallengeRequest;
import com.cubeia.games.challenge.io.protocol.ChallengeRequest;
import com.cubeia.games.challenge.io.protocol.DeclineChallengeRequest;
import com.cubeia.games.challenge.io.protocol.ProtocolObjectFactory;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ChallengeServiceImplTest {


    ChallengeServiceImpl service;

    @Mock
    private ServiceAction action;

    @Mock
    private ChallengeManager challengeManager;

    @Mock
    private PublicClientRegistryService publicClientRegistryService;

    @Mock
    private ServiceRouter router;

    ChallengeConfiguration config = new ChallengeConfiguration(1,"t", BigDecimal.ONE,BigDecimal.ONE);


    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        service = new ChallengeServiceImpl();
        service.clientRegistryService = publicClientRegistryService;
        service.challengeManager = challengeManager;
        service.router = router;

    }

    @Test
    public void testHandleAction() throws IOException {
        ChallengeRequest req = new ChallengeRequest(1,1);
        StyxSerializer ss = new StyxSerializer(new ProtocolObjectFactory());
        ByteBuffer bf = ss.pack(req);
        when(action.getData()).thenReturn(bf.array());
        when(publicClientRegistryService.getScreenname(anyInt())).thenReturn("aScreenName");
        when(challengeManager.createChallenge(anyInt(),anyInt(),any(ChallengeConfiguration.class))).thenReturn(UUID.randomUUID());
        service.onAction(action);
    }
    @Test
    public void testHandleAcceptChallengeAction() throws IOException, ChallengeNotFoundException {
        UUID uuid = UUID.randomUUID();
        AcceptChallengeRequest acr = new AcceptChallengeRequest(uuid.toString());
        StyxSerializer ss = new StyxSerializer(new ProtocolObjectFactory());
        ByteBuffer bf = ss.pack(acr);
        when(action.getData()).thenReturn(bf.array());
        when(publicClientRegistryService.getScreenname(anyInt())).thenReturn("aScreenName");
        when(challengeManager.acceptChallenge(any(UUID.class),anyInt())).thenReturn(new Challenge(uuid,1,2,config));
        service.onAction(action);
        verify(challengeManager,times(1)).acceptChallenge(any(UUID.class),anyInt());
    }
    @Test
    public void testHandleDeclineChallenge() throws IOException {
        UUID id = UUID.randomUUID();
        DeclineChallengeRequest req = new DeclineChallengeRequest(id.toString());
        StyxSerializer ss = new StyxSerializer(new ProtocolObjectFactory());
        ByteBuffer bf = ss.pack(req);
        when(action.getData()).thenReturn(bf.array());
        when(action.getPlayerId()).thenReturn(2); //decliner
        when(challengeManager.removeChallenge(any(UUID.class))).thenReturn(new Challenge(id,1,2,config));
        service.onAction(action);

        verify(challengeManager,times(1)).removeChallenge(eq(id));
        verify(router, times(1)).dispatchToPlayer(eq(1), any(ServiceAction.class));

    }

}
