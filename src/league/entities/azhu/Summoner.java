package league.entities.azhu;

import league.entities.SummonerDto;

public class Summoner extends SummonerDto{
    private League league;

    public Summoner(){
        
    }
    
    public Summoner(SummonerDto summoner, League league){
        this(summoner.getId(), summoner.getName(), summoner.getProfileIconId(), summoner.getSummonerLevel(),
             summoner.getRevisionDate(), league);
    }

    public Summoner(long id, String name, int profileIconId, long summonerLevel, long revisionDate, League league){
        super(id, name, profileIconId, summonerLevel, revisionDate);
        this.league = league;
    }

    public League getLeague(){
        return league;
    }

    public void setLeague(League league){
        this.league = league;
    }

    @Override
    public String toString(){
        return super.toString() + " | " + league.getTier() + " " + league.getDivision();
    }
}
