package league.neo4j.entities;

import league.entities.azhu.League;
import league.entities.azhu.Summoner;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "league" })
public class Summoner4j extends Summoner{
    protected League league;
    
    public Summoner4j(Summoner summoner){
        setId(summoner.getId());
        setLeague(summoner.getLeague());
        setName(summoner.getName());
        setProfileIconId(summoner.getProfileIconId());
        setRevisionDate(summoner.getRevisionDate());
        setSummonerLevel(summoner.getSummonerLevel());
    }
}
