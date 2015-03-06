package league.entities.azhu;

import java.util.logging.Logger;

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ItemDto;
import league.entities.ParticipantStats;

public class RankedStatsImpl extends ParticipantStats{
    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();
    private static Logger log = Logger.getLogger(ParticipantStats.class.getName());

    private ItemDto itemDto0;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto itemDto3;
    private ItemDto itemDto4;
    private ItemDto itemDto5;
    private ItemDto itemDto6;

    public RankedStatsImpl(){

    }

    public RankedStatsImpl(ParticipantStats stats){
        try{
            itemDto0 = api.getItemFromId(stats.getItem0());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto1 = api.getItemFromId(stats.getItem1());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto2 = api.getItemFromId(stats.getItem2());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto3 = api.getItemFromId(stats.getItem3());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto4 = api.getItemFromId(stats.getItem4());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto5 = api.getItemFromId(stats.getItem5());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto6 = api.getItemFromId(stats.getItem6());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        
        assists = stats.getAssists();
        champLevel = stats.getChampLevel();
        combatPlayerScore = stats.getCombatPlayerScore();
        deaths = stats.getDeaths();
        doubleKills = stats.getDoubleKills();
        firstBloodAssist = stats.isFirstBloodAssist();
        firstBloodKill = stats.isFirstBloodKill();
        firstInhibitorAssist = stats.isFirstInhibitorAssist();
        firstInhibitorKill = stats.isFirstInhibitorKill();
        firstTowerAssist = stats.isFirstTowerAssist();
        firstTowerKill = stats.isFirstTowerKill();
        goldEarned = stats.getGoldEarned();
        goldSpent = stats.getGoldSpent();
        inhibitorKills = stats.getInhibitorKills();
        killingSprees = stats.getKillingSprees();
        kills = stats.getKills();
        largestCriticalStrike = stats.getLargestCriticalStrike();
        largestKillingSpree = stats.getLargestKillingSpree();
        largestMultiKill = stats.getLargestMultiKill();
        magicDamageDealtToChampions = stats.getMagicDamageDealtToChampions();
        magicDamageTaken = stats.getMagicDamageTaken();
        minionsKilled = stats.getMinionsKilled();
        neutralMinionsKilled = stats.getNeutralMinionsKilled();
        neutralMinionsKilledEnemyJungle = stats.getNeutralMinionsKilledEnemyJungle();
        neutralMinionsKilledTeamJungle = stats.getNeutralMinionsKilledTeamJungle();
        nodeCapture = stats.getNodeCapture();
        nodeCaptureAssist = stats.getNodeCaptureAssist();
        nodeNeutralize = stats.getNodeNeutralize();
        nodeNeutralizeAssist = stats.getNodeNeutralizeAssist();
        objectivePlayerScore = stats.getObjectivePlayerScore();
        pentakills = stats.getPentakills();
        physicalDamageDealt = stats.getPhysicalDamageDealt();
        physicalDamageDealtToChampions = stats.getPhysicalDamageDealtToChampions();
        physicalDamageTaken = stats.getPhysicalDamageTaken();
        quadrakills = stats.getQuadrakills();
        sightWardsBoughtInGame = stats.getSightWardsBoughtInGame();
        teamObjective = stats.getTeamObjective();
        timePlayed = stats.getTimePlayed();
        totalDamageDealt = stats.getTotalDamageDealt();
        totalDamageDealtToChampions = stats.getTotalDamageDealtToChampions();
        totalDamageTaken = stats.getTotalDamageTaken();
        totalHeal = stats.getTotalHeal();
        totalPlayerScore = stats.getTotalPlayerScore();
        totalScoreRank = stats.getTotalScoreRank();
        totalTimeCrowdControlDealt = stats.getTotalTimeCrowdControlDealt();
        totalUnitsHealed = stats.getTotalUnitsHealed();
        towerKills = stats.getTowerKills();
        triplekills = stats.getTriplekills();
        trueDamageDealt= stats.getTrueDamageDealt();
        trueDamageDealtToChampions = stats.getTrueDamageDealtToChampions();
        trueDamageTaken = stats.getTrueDamageTaken();
        unrealKills = stats.getUnrealKills();
        visionWardsBoughtInGame = stats.getVisionWardsBoughtInGame();
        wardsKilled = stats.getWardsKilled();
        wardsPlaced = stats.getWardsPlaced();
        winner = stats.isWinner();
    }

    public ItemDto getItemDto0(){
        return itemDto0;
    }

    public void setItemDto0(ItemDto itemDto0){
        this.itemDto0 = itemDto0;
    }

    public ItemDto getItemDto1(){
        return itemDto1;
    }

    public void setItemDto1(ItemDto itemDto1){
        this.itemDto1 = itemDto1;
    }

    public ItemDto getItemDto2(){
        return itemDto2;
    }

    public void setItemDto2(ItemDto itemDto2){
        this.itemDto2 = itemDto2;
    }

    public ItemDto getItemDto3(){
        return itemDto3;
    }

    public void setItemDto3(ItemDto itemDto3){
        this.itemDto3 = itemDto3;
    }

    public ItemDto getItemDto4(){
        return itemDto4;
    }

    public void setItemDto4(ItemDto itemDto4){
        this.itemDto4 = itemDto4;
    }

    public ItemDto getItemDto5(){
        return itemDto5;
    }

    public void setItemDto5(ItemDto itemDto5){
        this.itemDto5 = itemDto5;
    }

    public ItemDto getItemDto6(){
        return itemDto6;
    }

    public void setItemDto6(ItemDto itemDto6){
        this.itemDto6 = itemDto6;
    }

}
