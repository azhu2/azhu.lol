package league.neo4j.entities;

import league.entities.azhu.League;
import league.entities.azhu.Summoner;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.neo4j.graphdb.Node;

@JsonIgnoreProperties(value = { "league" })
public class Summoner4j extends Summoner{
    private String leagueString;
    
    public Summoner4j(Summoner summoner){
        setId(summoner.getId());
        setLeague(summoner.getLeague());
        setName(summoner.getName());
        setProfileIconId(summoner.getProfileIconId());
        setRevisionDate(summoner.getRevisionDate());
        setSummonerLevel(summoner.getSummonerLevel());
    }

    public Summoner4j(Node node){
        super(node);
        leagueString = (String) node.getProperty("leagueString");
        setLeague(new League(leagueString));
    }

    public String getLeagueString(){
        return leagueString;
    }

    public void setLeagueString(String leagueString){
        this.leagueString = leagueString;
    }
    
    public void setLeague(League league){
        super.setLeague(league);
        leagueString = league != null ? league.toString() : "unranked";
    }
}
