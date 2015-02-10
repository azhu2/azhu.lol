package league.entities;

public class ParticipantIdentity{
    private int participantId;
    private Player player;

    public int getParticipantId(){
        return participantId;
    }

    public void setParticipantId(int participantId){
        this.participantId = participantId;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}
