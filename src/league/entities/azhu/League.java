package league.entities.azhu;

import league.api.APIConstants;
import league.entities.LeagueDto;
import league.entities.LeagueEntryDto;
import league.entities.MiniSeriesDto;

/**
 * Only for solo-queue for now
 */
public class League{
    private String name;
    private String queue;
    private String tier;
    private String division;
    private boolean isFreshBlood;
    private boolean isHotStreak;
    private boolean isInactive;
    private boolean isVeteran;
    private int leaguePoints;
    private int losses;
    private MiniSeriesDto miniSeries;
    private int wins;

    /**
     * Works if league only has one entry for the summoner - not the whole league
     */
    public League(LeagueDto league){
        name = league.getName();
        queue = league.getQueue();
        tier = league.getTier().toLowerCase();

        if(league.getEntries() == null || league.getEntries().isEmpty()){
            division = "";
            isFreshBlood = false;
            isHotStreak = false;
            isInactive = false;
            isVeteran = false;
            leaguePoints = APIConstants.INVALID;
            losses = APIConstants.INVALID;
            miniSeries = null;
            wins = APIConstants.INVALID;
        } else{
            LeagueEntryDto entry = league.getEntries().get(0);
            division = entry.getDivision();
            isFreshBlood = entry.isFreshBlood();
            isHotStreak = entry.isHotStreak();
            isInactive = entry.isInactive();
            isVeteran = entry.isVeteran();
            leaguePoints = entry.getLeaguePoints();
            losses = entry.getLosses();
            miniSeries = entry.getMiniSeries();
            wins = entry.getWins();
        }
    }

    public League(){

    }

    public League(String name, String queue, String tier, String division, boolean isFreshBlood, boolean isHotStreak,
            boolean isInactive, boolean isVeteran, int leaguePoints, int losses, MiniSeriesDto miniSeries, int wins){
        super();
        this.name = name;
        this.queue = queue;
        this.tier = tier.toLowerCase();
        this.division = division;
        this.isFreshBlood = isFreshBlood;
        this.isHotStreak = isHotStreak;
        this.isInactive = isInactive;
        this.isVeteran = isVeteran;
        this.leaguePoints = leaguePoints;
        this.losses = losses;
        this.miniSeries = miniSeries;
        this.wins = wins;
    }

    @Override
    public String toString(){
        return "League " + name + " (" + tier + " " + division + ")";
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getQueue(){
        return queue;
    }

    public void setQueue(String queue){
        this.queue = queue;
    }

    public String getTier(){
        return tier;
    }

    public void setTier(String tier){
        this.tier = tier.toLowerCase();
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

    public int getWins(){
        return wins;
    }

    public void setWins(int wins){
        this.wins = wins;
    }

}
