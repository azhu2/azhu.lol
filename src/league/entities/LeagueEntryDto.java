package league.entities;

public class LeagueEntryDto{
    private String division;
    private boolean isFreshBlood;
    private boolean isHotStreak;
    private boolean isInactive;
    private boolean isVeteran;
    private int leaguePoints;
    private int losses;
    private MiniSeriesDto miniSeries;
    private String playerOrTeamId;
    private String playerOrTeamName;
    private int wins;

    public LeagueEntryDto(){

    }

    public LeagueEntryDto(String division, boolean isFreshBlood, boolean isHotStreak, boolean isInactive,
            boolean isVeteran, int leaguePoints, int losses, MiniSeriesDto miniSeries, String playerOrTeamId,
            String playerOrTeamName, int wins){
        super();
        this.division = division;
        this.isFreshBlood = isFreshBlood;
        this.isHotStreak = isHotStreak;
        this.isInactive = isInactive;
        this.isVeteran = isVeteran;
        this.leaguePoints = leaguePoints;
        this.losses = losses;
        this.miniSeries = miniSeries;
        this.playerOrTeamId = playerOrTeamId;
        this.playerOrTeamName = playerOrTeamName;
        this.wins = wins;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerOrTeamId == null) ? 0 : playerOrTeamId.hashCode());
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
        LeagueEntryDto other = (LeagueEntryDto) obj;
        if(playerOrTeamId == null){
            if(other.playerOrTeamId != null)
                return false;
        } else if(!playerOrTeamId.equals(other.playerOrTeamId))
            return false;
        return true;
    }

    @Override
    public String toString(){
        return "LeagueEntryDto [division=" + division + ", playerOrTeamId=" + playerOrTeamId + ", playerOrTeamName="
                + playerOrTeamName + "]";
    }

    public String getDivision(){
        return division;
    }

    public void setDivision(String division){
        this.division = division;
    }

    public boolean isFreshBlood(){
        return isFreshBlood;
    }

    public void setFreshBlood(boolean isFreshBlood){
        this.isFreshBlood = isFreshBlood;
    }

    public boolean isHotStreak(){
        return isHotStreak;
    }

    public void setHotStreak(boolean isHotStreak){
        this.isHotStreak = isHotStreak;
    }

    public boolean isInactive(){
        return isInactive;
    }

    public void setInactive(boolean isInactive){
        this.isInactive = isInactive;
    }

    public boolean isVeteran(){
        return isVeteran;
    }

    public void setVeteran(boolean isVeteran){
        this.isVeteran = isVeteran;
    }

    public int getLeaguePoints(){
        return leaguePoints;
    }

    public void setLeaguePoints(int leaguePoints){
        this.leaguePoints = leaguePoints;
    }

    public int getLosses(){
        return losses;
    }

    public void setLosses(int losses){
        this.losses = losses;
    }

    public MiniSeriesDto getMiniSeries(){
        return miniSeries;
    }

    public void setMiniSeries(MiniSeriesDto miniSeries){
        this.miniSeries = miniSeries;
    }

    public String getPlayerOrTeamId(){
        return playerOrTeamId;
    }

    public void setPlayerOrTeamId(String playerOrTeamId){
        this.playerOrTeamId = playerOrTeamId;
    }

    public String getPlayerOrTeamName(){
        return playerOrTeamName;
    }

    public void setPlayerOrTeamName(String playerOrTeamName){
        this.playerOrTeamName = playerOrTeamName;
    }

    public int getWins(){
        return wins;
    }

    public void setWins(int wins){
        this.wins = wins;
    }

}
