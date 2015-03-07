package league.neo4j.entities;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import league.entities.ChampionDto;
import league.entities.Team;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.neo4j.graphdb.Node;

@JsonIgnoreProperties(value = {"blueTeam", "redTeam", "blueBans", "redBans", "teams", "players"})
public class RankedMatch4j extends Match{
    private String matchVersion;
    private String platformId;
    private String region;
    private String season;
    private List<Team> teams;
    private List<ChampionDto> blueBans;
    private List<ChampionDto> redBans;

    private String teamsString;

    private static Logger log = Logger.getLogger(RankedMatch4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public RankedMatch4j(){

    }

    public RankedMatch4j(RankedMatchImpl match){
        setBlueBans(match.getBlueBans());
        setBlueTeam(match.getBlueTeam());
        setId(match.getId());
        setMapId(match.getMapId());
        setMatchCreation(match.getMatchCreation());
        setMatchDuration(match.getMatchDuration());
        setMatchMode(match.getMatchMode());
        setMatchType(match.getMatchType());
        setMatchVersion(match.getMatchVersion());
        setPlatformId(match.getPlatformId());
        setQueueType(match.getQueueType());
        setRedBans(match.getRedBans());
        setRedTeam(match.getRedTeam());
        setRegion(match.getRegion());
        setSeason(match.getSeason());
        setTeams(match.getTeams());
    }

    public RankedMatch4j(Node node){
        super(node);
        setMatchVersion((String) node.getProperty("matchVersion"));
        setPlatformId((String) node.getProperty("platformId"));
        setRegion((String) node.getProperty("region"));
        setSeason((String) node.getProperty("season"));
        try{
            setTeams(mapper.readValue((String) node.getProperty("teamsString"), new TypeReference<List<Team>>(){
            }));
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public void setBlueTeam(List<MatchPlayer> team){
        super.setBlueTeam(team);
    }

    @Override
    public void setRedTeam(List<MatchPlayer> team){
        super.setRedTeam(team);
    }

    @Override
    public void addToBlueTeam(MatchPlayer player){
        super.addToBlueTeam(player);
    }

    @Override
    public void addToRedTeam(MatchPlayer player){
        super.addToRedTeam(player);
    }

    public String getMatchVersion(){
        return matchVersion;
    }

    public void setMatchVersion(String matchVersion){
        this.matchVersion = matchVersion;
    }

    public String getPlatformId(){
        return platformId;
    }

    public void setPlatformId(String platformId){
        this.platformId = platformId;
    }

    public String getRegion(){
        return region;
    }

    public void setRegion(String region){
        this.region = region;
    }

    public String getSeason(){
        return season;
    }

    public void setSeason(String season){
        this.season = season;
    }

    public List<Team> getTeams(){
        return teams;
    }

    public void setTeams(List<Team> teams){
        this.teams = teams;
        try{
            teamsString = mapper.writeValueAsString(teams);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    public List<ChampionDto> getBlueBans(){
        return blueBans;
    }

    public void setBlueBans(List<ChampionDto> bans){
        this.blueBans = bans;
    }

    public List<ChampionDto> getRedBans(){
        return redBans;
    }

    public void setRedBans(List<ChampionDto> bans){
        this.redBans = bans;
    }

    public String getTeamsString(){
        return teamsString;
    }

    public void setTeamsString(String teamsString){
        this.teamsString = teamsString;
    }

    @Override
    public String toString(){
        return "Ranked match " + getId();
    }

}
