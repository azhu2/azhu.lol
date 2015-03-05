package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import league.LeagueConstants;
import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.BannedChampion;
import league.entities.ChampionDto;
import league.entities.MatchDetail;
import league.entities.Participant;
import league.entities.ParticipantIdentity;
import league.entities.Team;

public class RankedMatchImpl extends Match{
    private String matchVersion;
    private List<RankedPlayer> players;     // TODO replace this
    private String platformId;
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
    private static NewLeagueAPI api_new = NewDatabaseAPIImpl.getInstance();

    public RankedMatchImpl(){

    }

    public RankedMatchImpl(int mapId, long matchCreation, long matchDuration, long matchId, String matchMode,
            String matchType, String matchVersion, List<RankedPlayer> players, String platformId, String queueType,
            String region, String season, List<Team> teams, int lookupPlayer, List<Integer> bluePlayers,
            List<Integer> redPlayers, List<ChampionDto> blueBans, List<ChampionDto> redBans, long summonerId){
        super();
        setMapId(mapId);
        setMatchCreation(matchCreation);
        setMatchDuration(matchDuration);
        setId(matchId);
        setMatchMode(matchMode);
        setMatchType(matchType);
        setMatchVersion(matchVersion);
        setPlayers(players);
        setPlatformId(platformId);
        setQueueType(queueType);
        setRegion(region);
        setSeason(season);
        setTeams(teams);
        setLookupPlayer(lookupPlayer);
        setBluePlayers(bluePlayers);
        setRedPlayers(redPlayers);
        setBlueBans(blueBans);
        setRedBans(redBans);
        setSummonerId(summonerId);
    }

    private void processPlayers(List<ParticipantIdentity> participantIdentities, List<Participant> participants,
            long summonerId, boolean cache) throws RiotPlsException{
        players = new LinkedList<>();
        bluePlayers = new LinkedList<>();
        redPlayers = new LinkedList<>();

        List<Long> summonerIds = new LinkedList<>();
        for(ParticipantIdentity id : participantIdentities)
            summonerIds.add(id.getPlayer().getSummonerId());
        List<Summoner> summoners = api_new.getSummonersNew(summonerIds, cache);
        for(int i = 0; i < participantIdentities.size(); i++){
            RankedPlayer player = new RankedPlayer(summoners.get(i), participants.get(i));
            players.add(player);
            if(participantIdentities.get(i).getPlayer().getSummonerId() == summonerId)
                lookupPlayer = i;
            if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
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

    public RankedMatchImpl(MatchDetail detail, long summonerId) throws RiotPlsException{
        this(detail, summonerId, true);
    }

    public RankedMatchImpl(MatchDetail detail, long summonerId, boolean cache) throws RiotPlsException{
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
        setSummonerId(summonerId);

        processPlayers(detail.getParticipantIdentities(), detail.getParticipants(), summonerId, cache);
        processBans(detail.getTeams());
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
        return "RankedMatch " + getId();
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
        result = prime * result + (int) (getId() ^ (getId() >>> 32));
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
        RankedMatchImpl other = (RankedMatchImpl) obj;
        if(getId() != other.getId())
            return false;
        if(summonerId != other.summonerId)
            return false;
        return true;
    }

    public RankedPlayer getQueryPlayer(){
        return players.get(lookupPlayer);
    }
}
