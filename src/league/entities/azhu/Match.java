package league.entities.azhu;

public abstract class Match{
    private long id;
    private int mapId;
    private long matchCreation;
    private long matchDuration;
    private String matchMode;
    private String matchType;
    private String queueType;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
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

    public String getQueueType(){
        return queueType;
    }

    public void setQueueType(String queueType){
        this.queueType = queueType;
    }

}
