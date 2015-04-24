package league.analysis;

import league.entities.ParticipantStats;
import league.entities.RawStatsDto;
import league.entities.azhu.Match;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.RankedPlayerImpl;
import league.neo4j.entities.GeneralMatch4j;
import league.neo4j.entities.RankedMatch4j;
import league.neo4j.entities.RankedPlayer4j;

/**
 * Per-champion statistics for a set of games. add[type of match]() to add a match to the set
 * and call process() after all matches are finished for averages stats
 */
public class SummaryData{
    private int numGames = 0;
    private int wins = 0;
    private int losses = 0;
    private double timePlayed = 0;
    private double kills = 0;
    private double deaths = 0;
    private double assists = 0;
    private double firstBloods = 0;
    private double firstBloodAssists = 0;
    private double goldEarned = 0;
    private double goldSpent = 0;
    private double largestKillingSpree = 0;
    private double largestMultiKill = 0;
    private double magicDamageDealt = 0;
    private double magicDamageDealtToChampions = 0;
    private double magicDamageTaken = 0;
    private double physicalDamageDealt = 0;
    private double physicalDamageDealtToChampions = 0;
    private double physicalDamageTaken = 0;
    private double trueDamageDealt = 0;
    private double trueDamageDealtToChampions = 0;
    private double trueDamageTaken = 0;
    private double totalDamageDealt = 0;
    private double totalDamageDealtToChampions = 0;
    private double totalDamageTaken = 0;
    private double totalHeal = 0;
    private double minionsKilled = 0;
    private double neutralMinionsKilled = 0;
    private double neutralMinionsKilledEnemyJungle = 0;
    private double neutralMinionsKilledTeamJungle = 0;
    private double wardsKilled = 0;
    private double wardsPlaced = 0;
    private double sightWardsBought = 0;
    private double visionWardsBought = 0;
    private long doubleKills = 0;
    private long tripleKills = 0;
    private long quadraKills = 0;
    private long pentaKills = 0;

    private double winrate;
    private double kda;
    private double cs;
    private double cspm;
    private double gpm;
    
    private Object category;

    public SummaryData(){

    }

    /**
     * Process all games to get per-game, per-minute stats, kda, and winrate
     */
    public void process(){
        timePlayed /= numGames;
        kills /= numGames;
        deaths /= numGames;
        assists /= numGames;
        firstBloods /= numGames;
        firstBloodAssists /= numGames;
        goldEarned /= numGames;
        goldSpent /= numGames;
        magicDamageDealt /= numGames;
        magicDamageDealtToChampions /= numGames;
        magicDamageTaken /= numGames;
        physicalDamageDealt /= numGames;
        physicalDamageDealtToChampions /= numGames;
        physicalDamageTaken /= numGames;
        trueDamageDealt /= numGames;
        trueDamageDealtToChampions /= numGames;
        trueDamageTaken /= numGames;
        totalDamageDealt /= numGames;
        totalDamageDealtToChampions /= numGames;
        totalDamageTaken /= numGames;
        totalHeal /= numGames;
        minionsKilled /= numGames;
        neutralMinionsKilled /= numGames;
        neutralMinionsKilledEnemyJungle /= numGames;
        neutralMinionsKilledTeamJungle /= numGames;
        wardsKilled /= numGames;
        wardsPlaced /= numGames;
        sightWardsBought /= numGames;
        visionWardsBought /= numGames;
        
        winrate = (double) wins / numGames;
        kda = deaths != 0 ? (kills + assists) / deaths : kills + assists;
        cs = minionsKilled + neutralMinionsKilled;
        cspm = cs / timePlayed * 60;
        gpm = goldEarned / timePlayed * 60;
    }

    /**
     * Deals with RankedMatchImpl
     * 
     * @deprecated Use {@link #addRankedMatchImpl(Match)} instead
     */
    public void addMatch(Match match){
        addRankedMatchImpl(match);
    }

    public void addRankedMatchImpl(Match match){
        RankedMatchImpl rankedMatch = (RankedMatchImpl) match;
        RankedPlayerImpl player = (RankedPlayerImpl) rankedMatch.getQueryPlayer();
        timePlayed += match.getMatchDuration();
        ParticipantStats stats = player.getStats();
        numGames++;
        addStats(stats);
    }

    public void addRankedMatch4j(Match match, long summonerId){
        RankedMatch4j rankedMatch = (RankedMatch4j) match;
        RankedPlayer4j player = (RankedPlayer4j) AnalysisUtils.getLookupPlayer(rankedMatch, summonerId);
        ParticipantStats stats = player.getStats();
        timePlayed += match.getMatchDuration();
        numGames++;
        addStats(stats);
    }

    public void addGeneralMatch4j(Match match, long summonerId){
        GeneralMatch4j generalMatch = (GeneralMatch4j) match;
        RawStatsDto stats = generalMatch.getStats();
        timePlayed += match.getMatchDuration();
        numGames++;
        addStats(stats);
    }

    private void addStats(RawStatsDto stats){
        if(stats.isWin())
            wins++;
        else
            losses++;
        kills += stats.getChampionsKilled();
        deaths += stats.getNumDeaths();
        assists += stats.getAssists();
        if(stats.getFirstBlood() != 0)
            firstBloods++;
        goldEarned += stats.getGoldEarned();
        goldSpent += stats.getGoldSpent();
        largestKillingSpree = Math.max(largestKillingSpree, stats.getLargestKillingSpree());
        largestMultiKill = Math.max(largestMultiKill, stats.getLargestMultiKill());
        magicDamageDealt += stats.getMagicDamageDealtPlayer();
        magicDamageDealtToChampions += stats.getMagicDamageDealtToChampions();
        magicDamageTaken += stats.getMagicDamageTaken();
        physicalDamageDealt += stats.getPhysicalDamageDealtPlayer();
        physicalDamageDealtToChampions += stats.getPhysicalDamageDealtToChampions();
        physicalDamageTaken += stats.getPhysicalDamageTaken();
        trueDamageDealt += stats.getTrueDamageDealtPlayer();
        trueDamageDealtToChampions += stats.getTrueDamageDealtToChampions();
        trueDamageTaken += stats.getTrueDamageTaken();
        totalDamageDealt += stats.getTotalDamageDealt();
        totalDamageDealtToChampions += stats.getTotalDamageDealtToChampions();
        totalDamageTaken += stats.getTotalDamageTaken();
        totalHeal += stats.getTotalHeal();
        minionsKilled += stats.getMinionsKilled();
        neutralMinionsKilled += stats.getNeutralMinionsKilled();
        neutralMinionsKilledEnemyJungle += stats.getNeutralMinionsKilledEnemyJungle();
        neutralMinionsKilledTeamJungle += stats.getNeutralMinionsKilledYourJungle();
        wardsKilled += stats.getWardKilled();
        wardsPlaced += stats.getWardPlaced();
        sightWardsBought += stats.getSightWardsBought();
        visionWardsBought += stats.getVisionWardsBought();
        doubleKills += stats.getDoubleKills();
        tripleKills += stats.getTripleKills();
        quadraKills += stats.getQuadraKills();
        pentaKills += stats.getPentaKills();
    }

    private void addStats(ParticipantStats stats){
        if(stats.isWinner())
            wins++;
        else
            losses++;
        kills += stats.getKills();
        deaths += stats.getDeaths();
        assists += stats.getAssists();
        if(stats.isFirstBloodKill())
            firstBloods++;
        if(stats.isFirstBloodAssist())
            firstBloodAssists++;
        goldEarned += stats.getGoldEarned();
        goldSpent += stats.getGoldSpent();
        largestKillingSpree = Math.max(largestKillingSpree, stats.getLargestKillingSpree());
        largestMultiKill = Math.max(largestMultiKill, stats.getLargestMultiKill());
        magicDamageDealt += stats.getMagicDamageDealt();
        magicDamageDealtToChampions += stats.getMagicDamageDealtToChampions();
        magicDamageTaken += stats.getMagicDamageTaken();
        physicalDamageDealt += stats.getPhysicalDamageDealt();
        physicalDamageDealtToChampions += stats.getPhysicalDamageDealtToChampions();
        physicalDamageTaken += stats.getPhysicalDamageTaken();
        trueDamageDealt += stats.getTrueDamageDealt();
        trueDamageDealtToChampions += stats.getTrueDamageDealtToChampions();
        trueDamageTaken += stats.getTrueDamageTaken();
        totalDamageDealt += stats.getTotalDamageDealt();
        totalDamageDealtToChampions += stats.getTotalDamageDealtToChampions();
        totalDamageTaken += stats.getTotalDamageTaken();
        totalHeal += stats.getTotalHeal();
        minionsKilled += stats.getMinionsKilled();
        neutralMinionsKilled += stats.getNeutralMinionsKilled();
        neutralMinionsKilledEnemyJungle += stats.getNeutralMinionsKilledEnemyJungle();
        neutralMinionsKilledTeamJungle += stats.getNeutralMinionsKilledTeamJungle();
        wardsKilled += stats.getWardsKilled();
        wardsPlaced += stats.getWardsPlaced();
        sightWardsBought += stats.getSightWardsBoughtInGame();
        visionWardsBought += stats.getVisionWardsBoughtInGame();
        doubleKills += stats.getDoubleKills();
        tripleKills += stats.getTriplekills();
        quadraKills += stats.getQuadrakills();
        pentaKills += stats.getPentakills();
    }

    public int getNumGames(){
        return numGames;
    }

    public int getWins(){
        return wins;
    }

    public int getLosses(){
        return losses;
    }

    public double getTimePlayed(){
        return timePlayed;
    }

    public double getKills(){
        return kills;
    }

    public double getDeaths(){
        return deaths;
    }

    public double getAssists(){
        return assists;
    }

    public double getFirstBloods(){
        return firstBloods;
    }

    public double getFirstBloodAssists(){
        return firstBloodAssists;
    }

    public double getGoldEarned(){
        return goldEarned;
    }

    public double getGoldSpent(){
        return goldSpent;
    }

    public double getLargestKillingSpree(){
        return largestKillingSpree;
    }

    public double getLargestMultiKill(){
        return largestMultiKill;
    }

    public double getMagicDamageDealt(){
        return magicDamageDealt;
    }

    public double getMagicDamageDealtToChampions(){
        return magicDamageDealtToChampions;
    }

    public double getMagicDamageTaken(){
        return magicDamageTaken;
    }

    public double getPhysicalDamageDealt(){
        return physicalDamageDealt;
    }

    public double getPhysicalDamageDealtToChampions(){
        return physicalDamageDealtToChampions;
    }

    public double getPhysicalDamageTaken(){
        return physicalDamageTaken;
    }

    public double getTrueDamageDealt(){
        return trueDamageDealt;
    }

    public double getTrueDamageDealtToChampions(){
        return trueDamageDealtToChampions;
    }

    public double getTrueDamageTaken(){
        return trueDamageTaken;
    }

    public double getTotalDamageDealt(){
        return totalDamageDealt;
    }

    public double getTotalDamageDealtToChampions(){
        return totalDamageDealtToChampions;
    }

    public double getTotalDamageTaken(){
        return totalDamageTaken;
    }

    public double getTotalHeal(){
        return totalHeal;
    }

    public double getMinionsKilled(){
        return minionsKilled;
    }

    public double getNeutralMinionsKilled(){
        return neutralMinionsKilled;
    }

    public double getNeutralMinionsKilledEnemyJungle(){
        return neutralMinionsKilledEnemyJungle;
    }

    public double getNeutralMinionsKilledTeamJungle(){
        return neutralMinionsKilledTeamJungle;
    }

    public double getWardsKilled(){
        return wardsKilled;
    }

    public double getWardsPlaced(){
        return wardsPlaced;
    }

    public double getSightWardsBought(){
        return sightWardsBought;
    }

    public double getVisionWardsBought(){
        return visionWardsBought;
    }

    public long getDoubleKills(){
        return doubleKills;
    }

    public long getTripleKills(){
        return tripleKills;
    }

    public long getQuadraKills(){
        return quadraKills;
    }

    public long getPentaKills(){
        return pentaKills;
    }

    public Object getCategory(){
        return category;
    }

    public void setCategory(Object category){
        this.category = category;
    }

    @Override
    public String toString(){
        return "Summary data for " + category;
    }

    public double getWinrate(){
        return winrate;
    }

    public double getKda(){
        return kda;
    }

    public double getCs(){
        return cs;
    }

    public double getCspm(){
        return cspm;
    }

    public double getGpm(){
        return gpm;
    }
}
