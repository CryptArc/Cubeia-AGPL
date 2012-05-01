package poker.gs.server.blinds;

import java.util.*;

import static poker.gs.server.blinds.utils.PokerUtils.*;

/**
 * Used for calculating where the dealer button, small blind and big blind should be.
 * <p/>
 * Also calculates who should post entry bets and who should be marked as having missed blinds.
 *
 * @author viktor
 */
public class BlindsCalculator {

    /**
     * The blinds info from last hand.
     */
    private BlindsInfo lastHandsBlinds;

    /**
     * The blinds info for the new hand.
     */
    private BlindsInfo blindsInfo = new BlindsInfo();

    /**
     * Used for fetching a random player to get the dealer button.
     */
    private final RandomSeatProvider randomSeatProvider;

    /**
     * Contains all players at the table. Used for calculating missed blinds.
     */
    private List<BlindsPlayer> players;

    /**
     * Maps players to seat ids. Only contains seated players.
     */
    private SortedMap<Integer, BlindsPlayer> seatedPlayers;

    /**
     * List of players who should pay the entry bet.
     */
    private final Queue<EntryBetter> entryBetters = new LinkedList<EntryBetter>();

    /**
     * List of players who missed blinds.
     */
    private final List<MissedBlind> missedBlinds = new ArrayList<MissedBlind>();

    /**
     * Used for logging.
     */
    private LogCallback logCallback;

    /**
     * Constructor.
     */
    public BlindsCalculator() {
        this(new FeedableSeatProvider());
    }

    /**
     * Constructor.
     *
     * @param randomSeatProvider the random seat provider to use
     */
    public BlindsCalculator(final RandomSeatProvider randomSeatProvider) {
        this.randomSeatProvider = randomSeatProvider;
    }

    /**
     * Initializes the blinds for a new hand.
     *
     * @param lastHandsBlinds The blinds info from last hand, cannot be null.
     * @param players         A list of players at the table, cannot be null.
     *                        Should contain all players, including players sitting out.
     * @return the {@link BlindsInfo} for the new hand, or null if no hand could be started
     */
    public BlindsInfo initializeBlinds(final BlindsInfo lastHandsBlinds, final List<BlindsPlayer> players) {
        this.lastHandsBlinds = lastHandsBlinds;
        this.players = players;

        clearLists();
        initPlayerMap();
        if (enoughPlayers()) {
            initBlinds();
            markMissedBlinds();
            return blindsInfo;
        } else {
            return null;
        }
    }

    private void clearLists() {
        missedBlinds.clear();
        entryBetters.clear();
    }

    /**
     * Calculates which players should post the entry bet, and what the should post.
     */
    private void calculateEntryBets() {
        // Players between the big blind and the dealer button are eligible to pay an entry bet.
        final int dealerSeatId = blindsInfo.getDealerSeatId();
        final int bigBlindSeatId = blindsInfo.getBigBlindSeatId();
        final int smallBlindSeatId = blindsInfo.getSmallBlindSeatId();

        final BlindsPlayer nextPlayer = getElementAfter(bigBlindSeatId, seatedPlayers);
        final List<BlindsPlayer> playerList = unwrapList(seatedPlayers, nextPlayer.getSeatId());
        for (BlindsPlayer player : playerList) {
            if (!player.hasPostedEntryBet()) {
                // A player on the dealer button cannot post entry bet.
                final boolean onDealer = player.getSeatId() == dealerSeatId;
                // Nor can a player between the dealer and the big blind.
                final boolean betweenDealerAndBig = isBetween(player.getSeatId(), dealerSeatId, bigBlindSeatId);
                // Nor can a player between the dealer and the small blind.
                final boolean betweenDealerAndSmall = isBetween(player.getSeatId(), dealerSeatId, smallBlindSeatId);

                if (player.getMissedBlindsStatus() == MissedBlindsStatus.NO_MISSED_BLINDS) {
                    /*
                          * This should not happen, a player who has not missed any blinds should still
                          * be considered as having paid the entry bet.
                          */
                    log("WARN: Player with id " + player.getPlayerId() + " has not missed any blinds, but has not posted the entry bet.");
                } else if (!onDealer && !betweenDealerAndBig && !betweenDealerAndSmall) {
                    entryBetters.add(new EntryBetter(player, getEntryBetType(player)));
                }
            }
        }
    }

    private EntryBetType getEntryBetType(final BlindsPlayer player) {
        EntryBetType result = null;
        switch (player.getMissedBlindsStatus()) {
            case MISSED_BIG_AND_SMALL_BLIND:
                result = EntryBetType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND;
                break;
            case MISSED_SMALL_BLIND:
                result = EntryBetType.DEAD_SMALL_BLIND;
                break;
            case NOT_ENTERED_YET:
                result = EntryBetType.BIG_BLIND;
                break;
        }
        return result;
    }

    /**
     * Checks if we have enough players to start a hand.
     */
    private boolean enoughPlayers() {
        return (seatedPlayers.size() > 1);
    }

    /**
     * Initializes the player map, by mapping players to their seat ids.
     *
     * @param players the list of players at the table
     */
    private void initPlayerMap() {
        seatedPlayers = new TreeMap<Integer, BlindsPlayer>();
        for (BlindsPlayer player : players) {
            if (player.isSittingIn()) {
                seatedPlayers.put(player.getSeatId(), player);
            }
        }
    }

    private void initBlinds() {
        if (firstHandAtTable()) {
            initFirstHandAtTable();
        } else if (onlyOneEnteredPlayer()) {
            initHandWhenOnlyOneEnteredPlayer();
        } else if (lastHandCanceled()) {
            initHandWhenLastHandCanceled();
        } else {
            initNonFirstHandAtTable();
            calculateEntryBets();
        }
    }

    private void initHandWhenLastHandCanceled() {
        if (dealerStillSeated()) {
            log("Initializing hand last hand was canceled and dealer is still seated.");
            final BlindsPlayer dealer = getPlayerInSeat(lastHandsBlinds.getDealerSeatId());
            initWithDealer(dealer);
        } else {
            initFirstHandAtTable();
        }
    }

    private void initWithDealer(final BlindsPlayer dealer) {
        blindsInfo.setDealerSeatId(dealer.getSeatId());

        if (headsUp()) {
            // The entered player gets the small blind.
            blindsInfo.setSmallBlindSeatId(dealer.getSeatId());
            blindsInfo.setSmallBlindPlayerId(dealer.getPlayerId());

            // The other player gets the big blind.
            BlindsPlayer bigBlind = getElementAfter(dealer.getSeatId(), seatedPlayers);
            blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
        } else {
            // The next player gets the small blind.
            BlindsPlayer smallBlind = getElementAfter(dealer.getSeatId(), seatedPlayers);
            blindsInfo.setSmallBlindSeatId(smallBlind.getSeatId());
            blindsInfo.setSmallBlindPlayerId(smallBlind.getPlayerId());

            // The next player gets the big blind.
            BlindsPlayer bigBlind = getElementAfter(smallBlind.getSeatId(), seatedPlayers);
            blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
        }
    }

    private boolean dealerStillSeated() {
        BlindsPlayer dealer = getPlayerInSeat(lastHandsBlinds.getDealerSeatId());
        return dealer != null && dealer.isSittingIn();
    }

    private boolean lastHandCanceled() {
        return lastHandsBlinds.handCanceled();
    }

    private boolean onlyOneEnteredPlayer() {
        return countEnteredPlayers() == 1;
    }

    private void markMissedBlinds() {
        // If small blind is sitting out, mark him as having missed the small blind.
        final BlindsPlayer smallBlind = getPlayerInSeat(blindsInfo.getSmallBlindSeatId());
        if (smallBlind != null && !smallBlind.isSittingIn() && smallBlind.getPlayerId() == lastHandsBlinds.getBigBlindPlayerId()) {
            addMissedBlind(smallBlind, MissedBlindsStatus.MISSED_SMALL_BLIND);
        }

        /*
           * All sitting out players between the old dealer button position and the
           * new dealer button position should be marked as having missed big+small.
           */
        final int lastDealerSeatId = lastHandsBlinds.getDealerSeatId();
        final int newDealerSeatId = blindsInfo.getDealerSeatId();
        for (final BlindsPlayer player : players) {
            if (player.isSittingIn()) {
                continue;
            }

            final boolean betweenOldAndNewDealerButton = isBetween(player.getSeatId(), lastDealerSeatId, newDealerSeatId);
            final boolean betweenSmallAndBigBlind = isBetween(player.getSeatId(), blindsInfo.getSmallBlindSeatId(), blindsInfo.getBigBlindSeatId());
            if (betweenOldAndNewDealerButton || betweenSmallAndBigBlind) {
                addMissedBlind(player, MissedBlindsStatus.MISSED_BIG_AND_SMALL_BLIND);
            }
        }
    }

    private void addMissedBlind(final BlindsPlayer player, final MissedBlindsStatus status) {
        // If the player has not entered yet, don't mark him as having missed any blinds.
        if (player.getMissedBlindsStatus() != MissedBlindsStatus.NOT_ENTERED_YET) {
            log("Marking player in seat " + player.getSeatId() + " as having missed blinds status: " + status);
            missedBlinds.add(new MissedBlind(player, status));
        }
    }

    /**
     * Gets the player in the given seat, or null if there is no player in that seat.
     *
     * @param seatId
     * @return
     */
    private BlindsPlayer getPlayerInSeat(final int seatId) {
        BlindsPlayer result = null;
        for (final BlindsPlayer player : players) {
            if (player.getSeatId() == seatId) {
                result = player;
            }
        }
        return result;
    }

    /**
     * Initializes a hand when there is only one entered player.
     * This should mean that the last hand was canceled. The rule is that
     * the entered player does _not_ have to pay the big blind.
     */
    private void initHandWhenOnlyOneEnteredPlayer() {
        log("Initializing hand when only one entered player.");
        final BlindsPlayer enteredPlayer = getFirstEnteredPlayer();
        initWithDealer(enteredPlayer);
    }

    /**
     * Gets the first player who has posted the entry bet.
     *
     * @return the first player who has posted the entry bet, or null if no player has posted the entry bet
     */
    private BlindsPlayer getFirstEnteredPlayer() {
        BlindsPlayer result = null;

        for (BlindsPlayer player : seatedPlayers.values()) {
            if (player.hasPostedEntryBet()) {
                result = player;
            }
        }
        return result;
    }

    private void initNonFirstHandAtTable() {
        log("Initializing non first heads up hand at table.");
        if (lastHandsBlinds.isHeadsUpLogic()) {
            if (headsUp()) {
                // Keeping heads up logic.
                initHeadsUpHand();
            } else {
                // Moving from heads up to non heads up logic.
                moveFromHeadsUpToNonHeadsUp();
            }
        } else {
            if (headsUp()) {
                // Moving from non heads up to heads up logic.
                moveFromNonHeadsUpToHeadsUp();
            } else {
                // Keeping non heads up logic.
                initNonHeadsUpHand();
            }
        }
    }

    private void initNonHeadsUpHand() {
        log("Initializing non heads up hand.");
        // Last hand's small blind gets the dealer button.
        final int dealerSeatId = lastHandsBlinds.getSmallBlindSeatId();
        blindsInfo.setDealerSeatId(dealerSeatId);

        // Last hand's big blind gets the small blind.
        final int smallBlindSeatId = lastHandsBlinds.getBigBlindSeatId();
        blindsInfo.setSmallBlindSeatId(smallBlindSeatId);
        blindsInfo.setSmallBlindPlayerId(lastHandsBlinds.getBigBlindPlayerId());

        // The next player gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(smallBlindSeatId, seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
    }

    private void moveFromHeadsUpToNonHeadsUp() {
        log("Moving from heads up to non heads up.");
        // Dealer button stays where it is.
        final int dealerSeatId = lastHandsBlinds.getDealerSeatId();
        blindsInfo.setDealerSeatId(dealerSeatId);

        // The big blind from last hand gets the small blind.
        final int smallBlindSeatId = lastHandsBlinds.getBigBlindSeatId();
        blindsInfo.setSmallBlindSeatId(smallBlindSeatId);
        blindsInfo.setSmallBlindPlayerId(lastHandsBlinds.getBigBlindPlayerId());

        // The next player gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(smallBlindSeatId, seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
    }

    private void moveFromNonHeadsUpToHeadsUp() {
        log("Moving from non heads up to heads up.");
        // The player after last hand's big blind gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(lastHandsBlinds.getBigBlindSeatId(), seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());

        // The other player gets the dealer button and the small blind.
        final BlindsPlayer smallBlind = getElementAfter(bigBlind.getSeatId(), seatedPlayers);
        blindsInfo.setDealerSeatId(smallBlind.getSeatId());
        blindsInfo.setSmallBlindSeatId(smallBlind.getSeatId());
        blindsInfo.setSmallBlindPlayerId(smallBlind.getPlayerId());
    }

    private void initHeadsUpHand() {
        log("Initializing heads up hand.");
        // The big blind from last hand gets the dealer button and small blind.
        BlindsPlayer dealer = getPlayerInSeat(lastHandsBlinds.getBigBlindSeatId());
        if (dealer == null) {
            // If the dealer is not there, find the first player after last hand's big blind.
            dealer = getElementAfter(lastHandsBlinds.getBigBlindSeatId(), seatedPlayers);
        }
        final int dealerSeatId = dealer.getSeatId();

        blindsInfo.setDealerSeatId(dealerSeatId);
        blindsInfo.setSmallBlindSeatId(dealerSeatId);
        blindsInfo.setSmallBlindPlayerId(dealer.getPlayerId());

        // The next player gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(dealerSeatId, seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
    }

    private void initFirstHandAtTable() {
        log("Initializing first hand at table.");
        if (headsUp()) {
            initFirstHeadsUpHand();
        } else {
            initFirstNonHeadsUpHand();
        }
    }

    private void initFirstNonHeadsUpHand() {
        log("Initializing first non heads up hand at table.");
        // Random player gets the button.
        final BlindsPlayer dealer = getRandomSeatedPlayer();
        blindsInfo.setDealerSeatId(dealer.getSeatId());

        // The next player gets the small blind.
        final BlindsPlayer smallBlind = getElementAfter(dealer.getSeatId(), seatedPlayers);
        blindsInfo.setSmallBlindSeatId(smallBlind.getSeatId());
        blindsInfo.setSmallBlindPlayerId(smallBlind.getPlayerId());

        // The next player gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(smallBlind.getSeatId(), seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
    }

    private void initFirstHeadsUpHand() {
        log("Initializing first heads up hand at table.");
        // Random player gets the button and small blind.
        final BlindsPlayer dealer = getRandomSeatedPlayer();
        blindsInfo.setDealerSeatId(dealer.getSeatId());
        blindsInfo.setSmallBlindSeatId(dealer.getSeatId());
        blindsInfo.setSmallBlindPlayerId(dealer.getPlayerId());

        // The other player gets the big blind.
        final BlindsPlayer bigBlind = getElementAfter(dealer.getSeatId(), seatedPlayers);
        blindsInfo.setBigBlindSeatId(bigBlind.getSeatId());
    }

    /**
     * Gets a random player from the players who are sitting in.
     *
     * @return a random player from the players who are sitting in
     */
    private BlindsPlayer getRandomSeatedPlayer() {
        final List<Integer> seatIds = getSeatIdsOfSeatedPlayers();
        return getPlayerInSeat(randomSeatProvider.getRandomSeatId(seatIds));
    }

    private List<Integer> getSeatIdsOfSeatedPlayers() {
        final List<Integer> seatIds = new ArrayList<Integer>();
        for (BlindsPlayer player : seatedPlayers.values()) {
            seatIds.add(player.getSeatId());
        }
        return seatIds;
    }

    /**
     * Checks whether this hand is heads up.
     *
     * @return <code>true</code> if this hand is heads up, <code>false</code> otherwise
     */
    private boolean headsUp() {
        return seatedPlayers.size() == 2;
    }

    /**
     * Checks whether this is the first hand at the table.
     * <p/>
     * We consider the hand to be the "first" if 1 or fewer players have payed
     * the entry bet.
     *
     * @return <code>true</code> is this is the first hand, <code>false</code> otherwise
     */
    private boolean firstHandAtTable() {
        return countEnteredPlayers() == 0 || !lastHandsBlinds.isDefined();
    }

    /**
     * Counts the number of players who have payed the entry bet.
     *
     * @return the number of players who have payed the entry bet
     */
    private int countEnteredPlayers() {
        int enteredPlayers = 0;
        for (final BlindsPlayer player : seatedPlayers.values()) {
            if (player.hasPostedEntryBet()) {
                enteredPlayers++;
            }
        }
        return enteredPlayers;
    }

    /**
     * Gets the blinds info for the new hand.
     *
     * @return the blinds info for the new hand, never null
     */
    public BlindsInfo getBlindsInfo() {
        return blindsInfo;
    }

    /**
     * Gets the list of players who should pay the entry bet. The list will be ordered
     * in the order the players should be asked.
     *
     * @return the list of players who should pay the entry bet
     */
    public Queue<EntryBetter> getEntryBetters() {
        return entryBetters;
    }

    /**
     * Gets a list of {@link MissedBlind}s, representing who should be marked as having missed blinds.
     *
     * @return
     */
    public List<MissedBlind> getMissedBlinds() {
        return missedBlinds;
    }

    /**
     * Returns the next player to ask for the big blind.
     *
     * @param lastAskedSeatId
     * @return the next player to ask for the big blind, or null if there are no more eligible players
     */
    public BlindsPlayer getNextBigBlindPlayer(final int lastAskedSeatId) {
        BlindsPlayer nextBigBlindPlayer;
        final int dealerSeatId = blindsInfo.getDealerSeatId();
        final int smallBlindSeatId = blindsInfo.getSmallBlindSeatId();

        if (lastAskedSeatId == -1) {
            nextBigBlindPlayer = getPlayerInSeat(blindsInfo.getBigBlindSeatId());
        } else {
            nextBigBlindPlayer = getElementAfter(lastAskedSeatId, seatedPlayers);
            boolean onSmallBlind = nextBigBlindPlayer.getSeatId() == smallBlindSeatId;
            boolean betweenDealerAndSmall = isBetween(nextBigBlindPlayer.getSeatId(), dealerSeatId, smallBlindSeatId);
            if (onSmallBlind || betweenDealerAndSmall) {
                // Small and big blind cannot be the same player, hand should be canceled.
                nextBigBlindPlayer = null;
            }
        }

        return nextBigBlindPlayer;
    }

    /**
     * Gets a list of players who are between the dealer button and the big blind.
     *
     * @return a list of players who are between the dealer button and the big blind
     */
    public List<BlindsPlayer> getPlayersBetweenDealerAndBig() {
        List<BlindsPlayer> result = new ArrayList<BlindsPlayer>();
        for (BlindsPlayer player : seatedPlayers.values()) {
            boolean betweenDealerAndSmall = isBetween(player.getSeatId(), blindsInfo.getDealerSeatId(), blindsInfo.getSmallBlindSeatId());
            boolean betweenSmallAndBig = isBetween(player.getSeatId(), blindsInfo.getSmallBlindSeatId(), blindsInfo.getBigBlindSeatId());
            if (betweenDealerAndSmall || betweenSmallAndBig) {
                result.add(player);
            }
        }
        return result;
    }

    /**
     * Gets a list of players who are eligible to play this hand.
     * Excludes players who have not paid their entry bet.
     *
     * @return a list of players eligible to play in the hand, excluding players who have to post an entry bet
     */
    public List<BlindsPlayer> getEligiblePlayerList() {
        List<BlindsPlayer> result = new ArrayList<BlindsPlayer>();
        final int dealerSeatId = blindsInfo.getDealerSeatId();
        final int bigBlindSeatId = blindsInfo.getBigBlindSeatId();
        final int smallBlindSeatId = blindsInfo.getSmallBlindSeatId();

        for (BlindsPlayer player : seatedPlayers.values()) {
            if (!player.hasPostedEntryBet()) {
                continue;
            }
            // Player between the dealer and the big blind is not eligible.
            final boolean betweenDealerAndBig = isBetween(player.getSeatId(), dealerSeatId, bigBlindSeatId);
            // Nor is a player between the dealer and the small blind.
            final boolean betweenDealerAndSmall = isBetween(player.getSeatId(), dealerSeatId, smallBlindSeatId);

            if (!betweenDealerAndBig && !betweenDealerAndSmall) {
                result.add(player);
            }
        }
        return result;
    }

    /**
     * Adds a callback for logging.
     *
     * @param logCallback
     */
    public void setLogCallback(LogCallback logCallback) {
        this.logCallback = logCallback;
    }

    private void log(String message) {
        if (logCallback != null) {
            logCallback.log(message);
        }
    }


}
