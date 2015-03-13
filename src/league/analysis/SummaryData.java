package league.analysis;

import league.entities.ChampionDto;
import league.entities.ParticipantStats;
import league.entities.RawStatsDto;
import league.entities.azhu.Match;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.RankedPlayerImpl;
import league.neo4j.entities.GeneralMatch4j;
import league.neo4j.entities.RankedMatch4j;
import league.neo4j.entities.RankedPlayer4j;

public class SummaryData{
    private int numGames = 0;
    private int wins = 0;
    private int losses = 0;
    private long timePlayed = 0;
    private long kills = 0;
    private long deaths = 0;
    private long assists = 0;
    private long firstBloods = 0;
    private long firstBloodAssists = 0;
    private long goldEarned = 0;
    private long goldSpent = 0;
    private long largestKillingSpree = 0;
    private long largestMultiKill = 0;
    private long magicDamageDealt = 0;
    private long magicDamageDealtToChampions = 0;
    private long magicDamageTaken = 0;
    private long physicalDamageDealt = 0;
    private long physicalDamageDealtToChampions = 0;
    private long physicalDamageTaken = 0;
    private long trueDamageDealt = 0;
    private long trueDamageDealtToChampions = 0;
    private long trueDamageTaken = 0;
    private long totalDamageDealt = 0;
    private long totalDamageDealtToChampions = 0;
    private long totalDamageTaken = 0;
    private long totalHeal = 0;
    private long minionsKilled = 0;
    private long neutralMinionsKilled = 0;
    private long neutralMinionsKilledEnemyJungle = 0;
    private long neutralMinionsKilledTeamJungle = 0;
    private long wardsKilled = 0;
    private long wardsPlaced = 0;
    private long sightWardsBought = 0;
    private long visionWardsBought = 0;
    private long doubleKills = 0;
    private long tripleKills = 0;
    private long quadraKills = 0;
    private long pentaKills = 0;
    
    private ChampionDto champion;

    public SummaryData(){
        
    }
    
    /** Deals with RankedMatchImpl 
     * @deprecated Use {@link #addRankedMatchImpl(Match)} instead*/
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
        neutralMinionsKilledTeamJungle  += stats.getNeutralMinionsKilledYourJungle();
        wardsKilled += stats.getWardKilled();
        wardsPlaced += stats.getWardPlaced();
        sightWardsBought += stats.getSightWardsBought();
        visionWardsBought += stats.getVisionWardsBought();
        doubleKills += stats.getDoubleKills();
        tripleKills += stats.getTriplekills();
        quadraKills += stats.getQuadrakills();
        pentaKills += stats.getPentakills();
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
        neutralMinionsKilledTeamJungle  += stats.getNeutralMinionsKilledTeamJungle();
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

    public long getTimePlayed(){
        return timePlayed;
    }

    public long getKills(){
        return kills;
    }

    public long getDeaths(){
        return deaths;
    }

    public long getAssists(){
        return assists;
    }

    public long getFirstBloods(){
        return firstBloods;
    }

    public long getFirstBloodAssists(){
        return firstBloodAssists;
    }

    public long getGoldEarned(){
        return goldEarned;
    }

    public long getGoldSpent(){
        return goldSpent;
    }

    public long getLargestKillingSpree(){
        return largestKillingSpree;
    }

    public long getLargestMultiKill(){
        return largestMultiKill;
    }

    public long getMagicDamageDealt(){
        return magicDamageDealt;
    }

    public long getMagicDamageDealtToChampions(){
        return magicDamageDealtToChampions;
    }

    public long getMagicDamageTaken(){
        return magicDamageTaken;
    }

    public long getPhysicalDamageDealt(){
        return physicalDamageDealt;
    }

    public long getPhysicalDamageDealtToChampions(){
        return physicalDamageDealtToChampions;
    }

    public long getPhysicalDamageTaken(){
        return physicalDamageTaken;
    }

    public long getTrueDamageDealt(){
        return trueDamageDealt;
    }

    public long getTrueDamageDealtToChampions(){
        return trueDamageDealtToChampions;
    }

    public long getTrueDamageTaken(){
        return trueDamageTaken;
    }

    public long getTotalDamageDealt(){
        return totalDamageDealt;
    }

    public long getTotalDamageDealtToChampions(){
        return totalDamageDealtToChampions;
    }

    public long getTotalDamageTaken(){
        return totalDamageTaken;
    }

    public long getTotalHeal(){
        return totalHeal;
    }

    public long getMinionsKilled(){
        return minionsKilled;
    }

    public long getNeutralMinionsKilled(){
        return neutralMinionsKilled;
    }

    public long getNeutralMinionsKilledEnemyJungle(){
        return neutralMinionsKilledEnemyJungle;
    }

    public long getNeutralMinionsKilledTeamJungle(){
        return neutralMinionsKilledTeamJungle;
    }

    public long getWardsKilled(){
        return wardsKilled;
    }

    public long getWardsPlaced(){
        return wardsPlaced;
    }

    public long getSightWardsBought(){
        return sightWardsBought;
    }

    public long getVisionWardsBought(){
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

    public ChampionDto getChampion(){
        return champion;
    }

    public void setChampion(ChampionDto champion){
        this.champion = champion;
    }

    @Override
    public String toString(){
        return "Summary data for " + champion.getName();
    }
}
