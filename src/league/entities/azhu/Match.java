package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Node;

public class Match{
    private long id;
    private int mapId;
    private long matchCreation;
    private long matchDuration;
    private String matchMode;
    private String matchType;
    private String queueType;
    private List<MatchPlayer> blueTeam = new LinkedList<>();
    private List<MatchPlayer> redTeam = new LinkedList<>();

    public Match(){
        
    }
    
    public Match(Node node){
        setId((long) node.getProperty("id"));
        setMapId((int)(long) node.getProperty("mapId"));
        setMatchCreation((long) node.getProperty("matchCreation"));
        setMatchDuration((long) node.getProperty("matchDuration"));
        setMatchMode((String) node.getProperty("matchMode"));
        setMatchType((String) node.getProperty("matchType"));
        setQueueType((String) node.getProperty("queueType"));
    }

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

    public List<MatchPlayer> getBlueTeam(){
        return blueTeam;
    }

    public void setBlueTeam(List<MatchPlayer> blueTeam){
        this.blueTeam = blueTeam;
    }

    public List<MatchPlayer> getRedTeam(){
        return redTeam;
    }

    public void setRedTeam(List<MatchPlayer> redTeam){
        this.redTeam = redTeam;
    }

    public void addToBlueTeam(MatchPlayer player){
        blueTeam.add(player);
    }
    
    public void addToRedTeam(MatchPlayer player){
        redTeam.add(player);
    }
}
