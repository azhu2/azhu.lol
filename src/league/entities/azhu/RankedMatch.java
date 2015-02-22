package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import league.api.APIConstants;
import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.BannedChampion;
import league.entities.ChampionDto;
import league.entities.MatchDetail;
import league.entities.Participant;
import league.entities.ParticipantIdentity;
import league.entities.SummonerDto;
import league.entities.Team;

public class RankedMatch{
    private int mapId;
    private long matchCreation;
    private long matchDuration;
    private long matchId;
    private String matchMode;
    private String matchType;
    private String matchVersion;
    private List<RankedPlayer> players;
    private String platformId;
    private String queueType;
    private String region;
    private String season;
    private List<Team> teams;
    private int lookupPlayer;
    private List<Integer> bluePlayers;
    private List<Integer> redPlayers;
    private List<ChampionDto> blueBans;
    private List<ChampionDto> redBans;
    private long summonerId;

    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();

    public RankedMatch(){

    }

    public RankedMatch(int mapId, long matchCreation, long matchDuration, long matchId, String matchMode,
            String matchType, String matchVersion, List<RankedPlayer> players, String platformId, String queueType,
            String region, String season, List<Team> teams, int lookupPlayer, List<Integer> bluePlayers,
            List<Integer> redPlayers, List<ChampionDto> blueBans, List<ChampionDto> redBans, long summonerId){
        super();
        this.mapId = mapId;
        this.matchCreation = matchCreation;
        this.matchDuration = matchDuration;
        this.matchId = matchId;
        this.matchMode = matchMode;
        this.matchType = matchType;
        this.matchVersion = matchVersion;
        this.players = players;
        this.platformId = platformId;
        this.queueType = queueType;
        this.region = region;
        this.season = season;
        this.teams = teams;
        this.lookupPlayer = lookupPlayer;
        this.bluePlayers = bluePlayers;
        this.redPlayers = redPlayers;
        this.blueBans = blueBans;
        this.redBans = redBans;
        this.summonerId = summonerId;
    }

    private void processPlayers(List<ParticipantIdentity> participantIdentities, List<Participant> participants,
            long summonerId) throws RiotPlsException{
        players = new LinkedList<>();
        bluePlayers = new LinkedList<>();
        redPlayers = new LinkedList<>();

        List<Long> summonerIds = new LinkedList<>();
        for(ParticipantIdentity id : participantIdentities)
            summonerIds.add(id.getPlayer().getSummonerId());
        List<SummonerDto> summoners = api.getSummoners(summonerIds);
        for(int i = 0; i < participantIdentities.size(); i++){
            RankedPlayer player = new RankedPlayer(summoners.get(i), participants.get(i));
            players.add(player);
            if(participantIdentities.get(i).getPlayer().getSummonerId() == summonerId)
                lookupPlayer = i;
            if(player.getTeamId() == APIConstants.BLUE_TEAM)
                bluePlayers.add(i);
            else
                redPlayers.add(i);
        }
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
                ChampionDto champ = api.getChampFromId(ban.getChampionId());
                banLists.get(i).add(champ);
            }
        }
    }

    public RankedMatch(MatchDetail detail, long summonerId) throws RiotPlsException{
        this.mapId = detail.getMapId();
        this.matchCreation = detail.getMatchCreation();
        this.matchDuration = detail.getMatchDuration();
        this.matchId = detail.getMatchId();
        this.matchMode = detail.getMatchMode();
        this.matchType = detail.getMatchType();
        this.matchVersion = detail.getMatchVersion();
        this.platformId = detail.getPlatformId();
        this.queueType = detail.getQueueType();
        this.region = detail.getRegion();
        this.season = detail.getSeason();
        this.teams = detail.getTeams();
        this.summonerId = summonerId;

        processPlayers(detail.getParticipantIdentities(), detail.getParticipants(), summonerId);
        processBans(detail.getTeams());
    }

    public int getMapId(){
        return mapId;
    }

    public void setMapId(int mapId){
        this.mapId = mapId;
    }

    public long getMatchCreation(){
        return matchCreation;
    }

    public void setMatchCreation(long matchCreation){
        this.matchCreation = matchCreation;
    }

    public long getMatchDuration(){
        return matchDuration;
    }

    public void setMatchDuration(long matchDuration){
        this.matchDuration = matchDuration;
    }

    public long getMatchId(){
        return matchId;
    }

    public void setMatchId(long matchId){
        this.matchId = matchId;
    }

    public String getMatchMode(){
        return matchMode;
    }

    public void setMatchMode(String matchMode){
        this.matchMode = matchMode;
    }

    public String getMatchType(){
        return matchType;
    }

    public void setMatchType(String matchType){
        this.matchType = matchType;
    }

    public String getMatchVersion(){
        return matchVersion;
    }

    public void setMatchVersion(String matchVersion){
        this.matchVersion = matchVersion;
    }

    public List<RankedPlayer> getPlayers(){
        return players;
    }

    public void setPlayers(List<RankedPlayer> players){
        this.players = players;
    }

    public String getPlatformId(){
        return platformId;
    }

    public void setPlatformId(String platformId){
        this.platformId = platformId;
    }

    public String getQueueType(){
        return queueType;
    }

    public void setQueueType(String queueType){
        this.queueType = queueType;
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
    }

    @Override
    public String toString(){
        return "RankedMatch " + matchId;
    }

    public int getLookupPlayer(){
        return lookupPlayer;
    }

    public void setLookupPlayer(int lookupPlayer){
        this.lookupPlayer = lookupPlayer;
    }

    public List<Integer> getBluePlayers(){
        return bluePlayers;
    }

    public void setBluePlayers(List<Integer> bluePlayers){
        this.bluePlayers = bluePlayers;
    }

    public List<Integer> getRedPlayers(){
        return redPlayers;
    }

    public void setRedPlayers(List<Integer> redPlayers){
        this.redPlayers = redPlayers;
    }

    public List<ChampionDto> getBlueBans(){
        return blueBans;
    }

    public void setBlueBans(List<ChampionDto> blueBans){
        this.blueBans = blueBans;
    }

    public List<ChampionDto> getRedBans(){
        return redBans;
    }

    public void setRedBans(List<ChampionDto> redBans){
        this.redBans = redBans;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (matchId ^ (matchId >>> 32));
        result = prime * result + (int) (summonerId ^ (summonerId >>> 32));
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
        RankedMatch other = (RankedMatch) obj;
        if(matchId != other.matchId)
            return false;
        if(summonerId != other.summonerId)
            return false;
        return true;
    }

}
