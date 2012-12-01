package com.cubeia.game.poker.challenge.api;


import com.cubeia.firebase.api.service.Contract;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.games.challenge.io.protocol.ChallengeRequest;

import java.util.UUID;

public interface ChallengeService extends Contract, RoutableService {

    public void startChallenge(int tournamentId,UUID challengeId);

}
