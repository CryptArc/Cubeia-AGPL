package com.cubeia.game.poker.challenge.impl;

import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.routing.ActivatorAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.game.poker.challenge.api.*;
import com.cubeia.games.challenge.io.protocol.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChallengeServiceImpl implements ChallengeService, Service {

    final private Logger log = Logger.getLogger(ChallengeServiceImpl.class);

    ServiceRouter router;

    ChallengeManager challengeManager;

    ChallengeConfigurationManager challengeConfigurationManager;

    PublicClientRegistryService clientRegistryService;




    @Override
    public void setRouter(ServiceRouter router) {
       this.router = router;
    }

    @Override
    public void onAction(ServiceAction a) {
        log.error("on action");
        StyxSerializer ss = new StyxSerializer(new ProtocolObjectFactory());
        try {
            ProtocolObject po = ss.unpack(ByteBuffer.wrap(a.getData()));
            if(po instanceof ChallengeRequest) {
                handleChallenge((ChallengeRequest)po,a.getPlayerId());
            } else if(po instanceof AcceptChallengeRequest) {
                handleAcceptChallenge((AcceptChallengeRequest)po,a.getPlayerId());
            } else if(po instanceof DeclineChallengeRequest) {
                handleDeclineChallenge((DeclineChallengeRequest)po,a.getPlayerId());
            } else if(po instanceof ChallengeConfigurationsRequest) {
                handleChallengeConfigurationRequest(a.getPlayerId());
            }
        } catch (Exception e) {
            log.error("unable to unpack service action data",e);
        }
    }

    private void handleChallengeConfigurationRequest(int playerId) {
        try {
            ChallengeConfigurationList configs = getChallengeConfList();
            ServiceAction a = new ClientServiceAction(playerId,0,getData(configs));
            router.dispatchToPlayer(playerId,a);
        } catch (IOException e) {
            log.error("Unable to marshal decline response",e);
        }

    }

    private void handleDeclineChallenge(DeclineChallengeRequest declineRequest, int playerId) {
        try {
            Challenge c = challengeManager.removeChallenge(UUID.fromString(declineRequest.challengeId));
            if(c!=null) {
                ChallengeResponse res = new ChallengeResponse(c.getId().toString(),Enums.ChallengeRequestStatus.PLAYER_DECLINED);
                ServiceAction a = new ClientServiceAction(c.getCreator(),0,getData(res));
                router.dispatchToPlayer(c.getCreator(),a);
            } else {
                log.info("Unable to find challenge to decline " + declineRequest.challengeId);
            }
        } catch (IOException e) {
            log.error("Unable to marshal decline response",e);
        }

    }

    public void handleAcceptChallenge(AcceptChallengeRequest po, int playerId) {
        log.debug("Handle accept challenge for player id " + playerId);
        try {
            Challenge challenge = challengeManager.acceptChallenge(UUID.fromString(po.challengeId), playerId);
            router.dispatchToTournamentActivator(1,new ActivatorAction<Challenge>(challenge));
        } catch (ChallengeNotFoundException e) {
            log.error("Invite " + po.challengeId + " not found", e);
        }
    }



    public void handleChallenge(ChallengeRequest packet, int creator) {

        log.debug("Challenge request packet recieved playerId  = " + packet.challengedPlayerId);

        try {
            ChallengeConfiguration config = challengeConfigurationManager.getConfiguration(packet.configurationId);
            if(config==null) {
                log.info("Configuration " + packet.configurationId + " not found");
                return;
            }
            UUID challengeID = challengeManager.createChallenge(creator, packet.challengedPlayerId,config);

            ChallengeInvite invite = new ChallengeInvite(challengeID.toString(),
                    clientRegistryService.getScreenname(creator),
                    new Configuration(config.getId(),config.getName()));

            ServiceAction a = new ClientServiceAction(packet.challengedPlayerId,0,getData(invite));
            router.dispatchToPlayer(packet.challengedPlayerId,a);

            ChallengeResponse challengeResponse = new ChallengeResponse(challengeID.toString(),Enums.ChallengeRequestStatus.WAITING_FOR_ACCEPT);
            ServiceAction responseAction = new ClientServiceAction(creator,0,getData(challengeResponse));
            router.dispatchToPlayer(creator,responseAction);

        } catch (IOException e) {
            log.error("unable to marshal invite",e);

        }


    }

    private byte[] getData(ProtocolObject resp) throws IOException {

        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        byte[] rawData = serializer.pack(resp).array();
        return rawData;
    }


    @Override
    public void init(ServiceContext context) throws SystemException {
        this.challengeManager = new ChallengeManagerImpl();
        this.challengeConfigurationManager = new ChallengeConfigurationManagerImpl();
        ServiceRegistry registry =  context.getParentRegistry();
        this.clientRegistryService = registry.getServiceInstance(PublicClientRegistryService.class);


    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
    }

    @Override
    public void startChallenge(int tournamentId, UUID challengeId) {
        log.debug("Starting challenge, tournament id  = " + tournamentId);
        challengeManager.removeChallenge(challengeId);
    }

    @Override
    public ChallengeConfiguration getChallengeConfiguration(int id) {
        return challengeConfigurationManager.getConfiguration(id);
    }

    public ChallengeConfigurationList getChallengeConfList() {
        List<Configuration> configs = new ArrayList<Configuration>();
        for(ChallengeConfiguration c : challengeConfigurationManager.getConfigurations()) {
            configs.add(new Configuration(c.getId(),c.getName()));
        }
        return new ChallengeConfigurationList(configs);
    }
}
