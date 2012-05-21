package com.cubeia.poker.variant.factory;

import com.cubeia.poker.GameType;
import com.cubeia.poker.rng.RNGProvider;
import com.cubeia.poker.variant.PokerVariant;
import com.cubeia.poker.variant.telesina.Telesina;
import com.cubeia.poker.variant.telesina.TelesinaDealerButtonCalculator;
import com.cubeia.poker.variant.telesina.TelesinaDeckFactory;
import com.cubeia.poker.variant.telesina.TelesinaRoundFactory;
import com.cubeia.poker.variant.texasholdem.TexasHoldem;

public class GameTypeFactory {

    public static GameType createGameType(PokerVariant variant, RNGProvider rngProvider) {
        GameType gameType;

        switch (variant) {
            case TEXAS_HOLDEM:
                gameType = new TexasHoldem(rngProvider);
                break;
            case TELESINA:
                gameType = new Telesina(rngProvider, new TelesinaDeckFactory(), new TelesinaRoundFactory(), new TelesinaDealerButtonCalculator());
                break;
            default:
                throw new UnsupportedOperationException("unsupported poker variant: " + variant);
        }

        return gameType;
    }

}
