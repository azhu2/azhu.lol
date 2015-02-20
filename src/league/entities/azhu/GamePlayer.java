package league.entities.azhu;

import league.entities.ChampionDto;
import league.entities.SummonerDto;

public class GamePlayer{
    private ChampionDto champion;
    private SummonerDto summoner;
    private int teamId;

    public GamePlayer(){

    }

    public GamePlayer(ChampionDto champion, SummonerDto summoner, int teamId){
        super();
        this.champion = champion;
        this.summoner = summoner;
        this.teamId = teamId;
    }

    @Override
    public String toString(){
        return "Player " + summoner + " (" + champion + ")";
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((summoner == null) ? 0 : summoner.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        GamePlayer other = (GamePlayer) obj;
        if(summoner == null){
            if(other.summoner != null)
                return false;
        } else if(!summoner.equals(other.summoner))
            return false;
        return true;
    }

    public ChampionDto getChampion(){
        return champion;
    }

    public void setChampion(ChampionDto champion){
        this.champion = champion;
    }

    public SummonerDto getSummoner(){
        return summoner;
    }

    public void setSummoner(SummonerDto summoner){
        this.summoner = summoner;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

}
