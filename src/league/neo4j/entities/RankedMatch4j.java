package league.neo4j.entities;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import league.LeagueConstants;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.Team;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;
import league.neo4j.Neo4jLeagueDatabaseAPI;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.neo4j.graphdb.Node;

@JsonIgnoreProperties(value = {"blueTeam", "redTeam", "blueBans", "redBans", "teams", "blueTeamIds", "redTeamIds",
        "blueBanIds", "redBanIds"})
public class RankedMatch4j extends Match{
    private String matchVersion;
    private String platformId;
    private String region;
    private String season;
    private List<Team> teams;
    private List<ChampionDto> blueBans;
    private List<ChampionDto> redBans;

    private long[] blueTeamIds = new long[LeagueConstants.MAX_TEAM_SIZE];
    private long[] redTeamIds = new long[LeagueConstants.MAX_TEAM_SIZE];
    private int[] blueBanIds = new int[LeagueConstants.BAN_COUNT];
    private int[] redBanIds = new int[LeagueConstants.BAN_COUNT];
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
        setBlueTeamIds((long[]) node.getProperty("blueTeamIds"));
        setRedTeamIds((long[]) node.getProperty("redTeamIds"));
        setBlueBanIds((int[]) node.getProperty("blueBanIds"));
        setRedBanIds((int[]) node.getProperty("redBanIds"));
        try{
            setTeams(mapper.readValue((String) node.getProperty("teamsString"), new TypeReference<List<Team>>(){
            }));
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    /**
     * Use the provided APIs to populate teams and bans
     * (APIs can be the same but want to preserve multiple interfaces...)
     */
    public void processLinks(LeagueAPI api, Neo4jLeagueDatabaseAPI api_neo4j){
        try{
            for(Long playerId : blueTeamIds)
                addToBlueTeam(api_neo4j.getMatchPlayer(getId(), playerId));
            for(Long playerId : redTeamIds)
                addToRedTeam(api_neo4j.getMatchPlayer(getId(), playerId));
            
            for(Integer champId : blueBanIds)
                blueBans.add(api.getChampFromId(champId));
            for(Integer champId : redBanIds)
                redBans.add(api.getChampFromId(champId));
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public void setBlueTeam(List<MatchPlayer> team){
        super.setBlueTeam(team);
        for(int i = 0; i < team.size(); i++)
            blueTeamIds[i] = team.get(i).getSummoner().getId();
    }

    @Override
    public void setRedTeam(List<MatchPlayer> team){
        super.setRedTeam(team);
        for(int i = 0; i < team.size(); i++)
            redTeamIds[i] = team.get(i).getSummoner().getId();
    }

    @Override
    public void addToBlueTeam(MatchPlayer player){
        super.addToBlueTeam(player);
        blueTeamIds[getBlueTeam().size() - 1] = player.getSummoner().getId();
    }

    @Override
    public void addToRedTeam(MatchPlayer player){
        super.addToRedTeam(player);
        redTeamIds[getRedTeam().size() - 1] = player.getSummoner().getId();
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
        for(int i = 0; i < bans.size(); i++)
            blueBanIds[i] = bans.get(i).getId();
    }

    public List<ChampionDto> getRedBans(){
        return redBans;
    }

    public void setRedBans(List<ChampionDto> bans){
        this.redBans = bans;
        for(int i = 0; i < bans.size(); i++)
            redBanIds[i] = bans.get(i).getId();
    }

    public long[] getBlueTeamIds(){
        return blueTeamIds;
    }

    public void setBlueTeamIds(long[] blueTeamIds){
        this.blueTeamIds = blueTeamIds;
    }

    public long[] getRedTeamIds(){
        return redTeamIds;
    }

    public void setRedTeamIds(long[] redTeamIds){
        this.redTeamIds = redTeamIds;
    }

    public int[] getBlueBanIds(){
        return blueBanIds;
    }

    public void setBlueBanIds(int[] blueBanIds){
        this.blueBanIds = blueBanIds;
    }

    public int[] getRedBanIds(){
        return redBanIds;
    }

    public void setRedBanIds(int[] redBanIds){
        this.redBanIds = redBanIds;
    }

    public String getTeamsString(){
        return teamsString;
    }

    public void setTeamsString(String teamsString){
        this.teamsString = teamsString;
    }
}
