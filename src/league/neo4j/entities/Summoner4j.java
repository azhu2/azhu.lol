package league.neo4j.entities;

import league.entities.azhu.League;
import league.entities.azhu.Summoner;

import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

public class Summoner4j extends Summoner{
    @JsonView(Views.Neo4jView.class)
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

    @Override
    public void setLeague(League league){
        super.setLeague(league);
        leagueString = league != null ? league.toString() : "unranked";
    }
}
