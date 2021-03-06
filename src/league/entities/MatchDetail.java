package league.entities;

import java.util.List;

public class MatchDetail{
    private int mapId;
    private long matchCreation;
    private long matchDuration;
    private long matchId;
    private String matchMode;
    private String matchType;
    private String matchVersion;
    private List<ParticipantIdentity> participantIdentities;
    private List<Participant> participants;
    private String platformId;
    private String queueType;
    private String region;
    private String season;
    private List<Team> teams;

    // private Timeline timeline;

    public MatchDetail(){

    }

    public MatchDetail(int mapId, long matchCreation, long matchDuration, long matchId, String matchMode,
            String matchType, String matchVersion, List<ParticipantIdentity> participantIdentities,
            List<Participant> participants, String platformId, String queueType, String region, String season,
            List<Team> teams){
        super();
        this.mapId = mapId;
        this.matchCreation = matchCreation;
        this.matchDuration = matchDuration;
        this.matchId = matchId;
        this.matchMode = matchMode;
        this.matchType = matchType;
        this.matchVersion = matchVersion;
        this.participantIdentities = participantIdentities;
        this.participants = participants;
        this.platformId = platformId;
        this.queueType = queueType;
        this.region = region;
        this.season = season;
        this.teams = teams;
        // this.timeline = timeline;
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

    public List<ParticipantIdentity> getParticipantIdentities(){
        return participantIdentities;
    }

    public void setParticipantIdentities(List<ParticipantIdentity> participantIdentities){
        this.participantIdentities = participantIdentities;
    }

    public List<Participant> getParticipants(){
        return participants;
    }

    public void setParticipants(List<Participant> participants){
        this.participants = participants;
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
        return "MatchDetail " + matchId;
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
        MatchDetail other = (MatchDetail) obj;
        if(matchId != other.matchId)
            return false;
        return true;
    }

    // public Timeline getTimeline(){
    // return timeline;
    // }
    //
    // public void setTimeline(Timeline timeline){
    // this.timeline = timeline;
    // }
}
