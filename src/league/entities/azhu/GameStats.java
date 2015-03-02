package league.entities.azhu;

import java.util.logging.Logger;

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ItemDto;
import league.entities.RawStatsDto;

public class GameStats extends RawStatsDto{
    private ItemDto itemDto0;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto itemDto3;
    private ItemDto itemDto4;
    private ItemDto itemDto5;
    private ItemDto itemDto6;

    private static Logger log = Logger.getLogger(GameStats.class.getName());
    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();

    public GameStats(){

    }

    public GameStats(RawStatsDto stats){
        try{
            itemDto0 = api.getItem(stats.getItem0());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto1 = api.getItem(stats.getItem1());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto2 = api.getItem(stats.getItem2());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto3 = api.getItem(stats.getItem3());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto4 = api.getItem(stats.getItem4());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto5 = api.getItem(stats.getItem5());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        try{
            itemDto6 = api.getItem(stats.getItem6());
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }

        assists = stats.getAssists();
        barracksKilled = stats.getBarracksKilled();
        championsKilled = stats.getChampionsKilled();
        combatPlayerScore = stats.getCombatPlayerScore();
        consumablesPurchased = stats.getConsumablesPurchased();
        damageDealtPlayer = stats.getDamageDealtPlayer();
        doubleKills = stats.getDoubleKills();
        firstBlood = stats.getFirstBlood();
        gold = stats.getGold();
        goldEarned = stats.getGoldEarned();
        goldSpent = stats.getGoldSpent();
        itemsPurchased = stats.getItemsPurchased();
        killingSprees = stats.getKillingSprees();
        largestCriticalStrike = stats.getLargestCriticalStrike();
        largestKillingSpree = stats.getLargestKillingSpree();
        largestMultiKill = stats.getLargestMultiKill();
        legendaryItemsCreated = stats.getLegendaryItemsCreated();
        level = stats.getLevel();
        magicDamageDealtPlayer = stats.getMagicDamageDealtPlayer();
        magicDamageDealtToChampions = stats.getMagicDamageDealtToChampions();
        magicDamageTaken = stats.getMagicDamageTaken();
        minionsDenied = stats.getMinionsDenied();
        minionsKilled = stats.getMinionsKilled();
        neutralMinionsKilled = stats.getNeutralMinionsKilled();
        neutralMinionsKilledEnemyJungle = stats.getNeutralMinionsKilledEnemyJungle();
        neutralMinionsKilledYourJungle = stats.getNeutralMinionsKilledYourJungle();
        nexusKilled = stats.isNexusKilled();
        nodeCapture = stats.getNodeCapture();
        nodeCaptureAssist = stats.getNodeCaptureAssist();
        nodeNeutralize = stats.getNodeNeutralize();
        nodeNeutralizeAssist = stats.getNodeNeutralizeAssist();
        numDeaths = stats.getNumDeaths();
        numItemsBought = stats.getNumItemsBought();
        objectivePlayerScore = stats.getObjectivePlayerScore();
        pentakills = stats.getPentakills();
        physicalDamageDealtPlayer = stats.getPhysicalDamageDealtPlayer();
        physicalDamageDealtToChampions = stats.getPhysicalDamageDealtToChampions();
        physicalDamageTaken = stats.getPhysicalDamageTaken();
        quadrakills = stats.getQuadrakills();
        sightWardsBought = stats.getSightWardsBought();
        spell1Cast = stats.getSpell1Cast();
        spell2Cast = stats.getSpell2Cast();
        spell3Cast = stats.getSpell3Cast();
        spell4Cast = stats.getSpell4Cast();
        summonSpell1Cast = stats.getSummonSpell1Cast();
        summonSpell2Cast = stats.getSummonSpell2Cast();
        superMonsterKilled = stats.getSuperMonsterKilled();
        team = stats.getTeam();
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
        triplekills = stats.getTriplekills();
        trueDamageDealtPlayer = stats.getTrueDamageDealtPlayer();
        trueDamageDealtToChampions = stats.getTrueDamageDealtToChampions();
        trueDamageTaken = stats.getTrueDamageTaken();
        turretsKilled = stats.getTurretsKilled();
        unrealKills = stats.getUnrealKills();
        victoryPointTotal = stats.getVictoryPointTotal();
        visionWardsBought = stats.getVisionWardsBought();
        wardKilled = stats.getWardKilled();
        wardPlaced = stats.getWardPlaced();
        win = stats.isWin();
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
