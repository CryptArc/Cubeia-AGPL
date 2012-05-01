package poker.gs.server.blinds;

public class MissedBlind {

    private BlindsPlayer player;

    private MissedBlindsStatus missedBlindsStatus;

    public MissedBlind(BlindsPlayer player, MissedBlindsStatus missedBlindsStatus) {
        super();
        this.player = player;
        this.missedBlindsStatus = missedBlindsStatus;
    }

    public BlindsPlayer getPlayer() {
        return player;
    }

    public void setPlayer(BlindsPlayer player) {
        this.player = player;
    }

    public MissedBlindsStatus getMissedBlindsStatus() {
        return missedBlindsStatus;
    }

    public void setMissedBlindsStatus(MissedBlindsStatus missedBlindsStatus) {
        this.missedBlindsStatus = missedBlindsStatus;
    }

}
