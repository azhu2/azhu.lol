package league.entities;

public class Player{
    private String matchHistoryUri;
    private int profileIcon;
    private long summonerId;
    private String summonerName;

    public String getMatchHistoryUri(){
        return matchHistoryUri;
    }

    public void setMatchHistoryUri(String matchHistoryUri){
        this.matchHistoryUri = matchHistoryUri;
    }

    public int getProfileIcon(){
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon){
        this.profileIcon = profileIcon;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }

    public String getSummonerName(){
        return summonerName;
    }

    public void setSummonerName(String summonerName){
        this.summonerName = summonerName;
    }
}
