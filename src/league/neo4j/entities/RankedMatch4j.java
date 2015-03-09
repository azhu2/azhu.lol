package league.neo4j.entities;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import league.LeagueConstants;
import league.api.RiotPlsException;
import league.entities.BannedChampion;
import league.entities.ChampionDto;
import league.entities.MatchDetail;
import league.entities.Participant;
import league.entities.ParticipantIdentity;
import league.entities.Team;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.Summoner;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jackson.type.TypeReference;
import org.neo4j.graphdb.Node;

public class RankedMatch4j extends Match{
    private String matchVersion;
    private String platformId;
    private String region;
    private String season;
    private List<Team> teams;
    @JsonView(Views.RestView.class)
    private List<ChampionDto> blueBans;
    @JsonView(Views.RestView.class)
    private List<ChampionDto> redBans;

    @JsonView(Views.Neo4jView.class)
    private String teamsString;

    private static Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
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

    public RankedMatch4j(MatchDetail detail) throws RiotPlsException{
        setMapId(detail.getMapId());
        setMatchCreation(detail.getMatchCreation());
        setMatchDuration(detail.getMatchDuration());
        setId(detail.getMatchId());
        setMatchMode(detail.getMatchMode());
        setMatchType(detail.getMatchType());
        setMatchVersion(detail.getMatchVersion());
        setPlatformId(detail.getPlatformId());
        setQueueType(detail.getQueueType());
        setRegion(detail.getRegion());
        setSeason(detail.getSeason());
        setTeams(detail.getTeams());

        processPlayers(detail.getParticipantIdentities(), detail.getParticipants());
        processBans(detail.getTeams());
    }

    private void processPlayers(List<ParticipantIdentity> participantIdentities, List<Participant> participants)
            throws RiotPlsException{
        List<MatchPlayer> bluePlayers = new LinkedList<>();
        List<MatchPlayer> redPlayers = new LinkedList<>();

        List<Long> summonerIds = new LinkedList<>();
        for(ParticipantIdentity id : participantIdentities)
            summonerIds.add(id.getPlayer().getSummonerId());
        List<Summoner> summoners;
        try{
            summoners = api.getSummoners(summonerIds);
            for(int i = 0; i < participantIdentities.size(); i++){
                MatchPlayer player = new RankedPlayer4j(summoners.get(i), participants.get(i));
                if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
                    bluePlayers.add(player);
                else
                    redPlayers.add(player);
            }
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }

        setBlueTeam(bluePlayers);
        setRedTeam(redPlayers);
    }

    private void processBans(List<Team> teamsData) throws RiotPlsException{
        List<List<ChampionDto>> banLists = new LinkedList<>();
        blueBans = new LinkedList<>();
        redBans = new LinkedList<>();
        banLists.add(blueBans);
        banLists.add(redBans);

        for(int i = 0; i < teamsData.size(); i++){
            List<BannedChampion> bans = teamsData.get(i).getBans();
            for(BannedChampion ban : bans){
                ChampionDto champ = api.getChampionFromId(ban.getChampionId());
                banLists.get(i).add(champ);
            }
        }
        
        setBlueBans(blueBans);
        setRedBans(redBans);
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
