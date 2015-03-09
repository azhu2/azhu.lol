package league.entities.azhu;

import league.entities.SummonerDto;

import org.neo4j.graphdb.Node;

public class Summoner extends SummonerDto{
    protected League league;

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
    
    public Summoner(Node node){     
        setId((long) node.getProperty("id"));
        setName((String) node.getProperty("name"));
        setProfileIconId((int)(long) node.getProperty("profileIconId"));
        setSummonerLevel((long) node.getProperty("summonerLevel"));
        setRevisionDate((long) node.getProperty("revisionDate"));
//        setLeague((League) node.getProperty("league"));
    }

    public League getLeague(){
        return league;
    }

    public void setLeague(League league){
        this.league = league;
    }

    @Override
    public String toString(){
        return super.toString() + " | " + (league != null ? league.getTier() + " " + league.getDivision() + " (" + league.getLeaguePoints() + " LP)": "unranked");
    }
}
