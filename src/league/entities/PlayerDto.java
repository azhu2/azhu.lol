package league.entities;

public class PlayerDto{
    private int championId;
    private long summonerId;
    private int teamId;

    public PlayerDto(){

    }

    public PlayerDto(int championId, long summonerId, int teamId){
        super();
        this.championId = championId;
        this.summonerId = summonerId;
        this.teamId = teamId;
    }

    public int getChampionId(){
        return championId;
    }

    public void setChampionId(int championId){
        this.championId = championId;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

}
