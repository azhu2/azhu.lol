package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import league.entities.MatchDetail;
import league.entities.Participant;
import league.entities.ParticipantIdentity;
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

    public RankedMatch(){

    }

    public RankedMatch(int mapId, long matchCreation, long matchDuration, long matchId, String matchMode,
            String matchType, String matchVersion, List<RankedPlayer> players, String platformId, String queueType,
            String region, String season, List<Team> teams){
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
    }

    private void processPlayers(List<ParticipantIdentity> participantIdentities, List<Participant> participants){
        players = new LinkedList<>();
        for(int i = 0; i < participantIdentities.size(); i++)
            players.add(new RankedPlayer(participantIdentities.get(i), participants.get(i)));
    }

    public RankedMatch(MatchDetail detail){
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
        
        processPlayers(detail.getParticipantIdentities(), detail.getParticipants());
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
        return "RankedMatch [matchId=" + matchId + "]";
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (matchId ^ (matchId >>> 32));
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
        return true;
    }

}
