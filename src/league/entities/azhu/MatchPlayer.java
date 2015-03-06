package league.entities.azhu;

import league.entities.ChampionDto;

public class MatchPlayer{
    private ChampionDto champion;
    private Summoner summoner;
    private int teamId;

    public ChampionDto getChampion(){
        return champion;
    }

    public void setChampion(ChampionDto champion){
        this.champion = champion;
    }

    public Summoner getSummoner(){
        return summoner;
    }

    public void setSummoner(Summoner summoner){
        this.summoner = summoner;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

}
