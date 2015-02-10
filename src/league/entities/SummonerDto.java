package league.entities;


public class SummonerDto{
    private long id;
    private String name;
    private int profileIconId;
    private long summonerLevel;
    private long revisionDate;

    public SummonerDto(){

    }

    public SummonerDto(long id, String name, int profileIconId, long summonerLevel,
            long revisionDate){
        this.id = id;
        this.name = name;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.revisionDate = revisionDate;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getProfileIconId(){
        return profileIconId;
    }

    public void setProfileIconId(int profileIconId){
        this.profileIconId = profileIconId;
    }

    public long getSummonerLevel(){
        return summonerLevel;
    }

    public void setSummonerLevel(long summonerLevel){
        this.summonerLevel = summonerLevel;
    }

    public long getRevisionDate(){
        return revisionDate;
    }

    public void setRevisionDate(long revisionDate){
        this.revisionDate = revisionDate;
    }

    @Override
    public String toString(){
        return String.format("Summoner %s(%d) - level %d", name, id, summonerLevel);
    }
}
