package league.entities.azhu;

import java.util.List;

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.Mastery;
import league.entities.Participant;
import league.entities.ParticipantStats;
import league.entities.Rune;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public class RankedPlayer{
    private SummonerDto summoner;
    private ChampionDto champion;
    private String highestAchievedSeasonTier;
    private List<Mastery> masteries;
    private int participantId;
    private List<Rune> runes;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private ParticipantStats stats;
    private int teamId;

    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();

    public RankedPlayer(SummonerDto summoner, Participant participant){
        try{
            this.summoner = summoner;
            champion = api.getChampFromId(participant.getChampionId());
            highestAchievedSeasonTier = participant.getHighestAchievedSeasonTier();
            masteries = participant.getMasteries();
            participantId = participant.getParticipantId();
            runes = participant.getRunes();
            spell1 = api.getSummonerSpellFromId(participant.getSpell1Id());
            spell2 = api.getSummonerSpellFromId(participant.getSpell2Id());
            stats = participant.getStats();
            teamId = participant.getTeamId();
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "RankedPlayer [summoner=" + summoner + ", champion=" + champion + "]";
    }

    public RankedPlayer(SummonerDto summoner, ChampionDto champion, String highestAchievedSeasonTier,
            List<Mastery> masteries, int participantId, List<Rune> runes, SummonerSpellDto spell1,
            SummonerSpellDto spell2, ParticipantStats stats, int teamId){
        super();
        this.summoner = summoner;
        this.champion = champion;
        this.highestAchievedSeasonTier = highestAchievedSeasonTier;
        this.masteries = masteries;
        this.participantId = participantId;
        this.runes = runes;
        this.spell1 = spell1;
        this.spell2 = spell2;
        this.stats = stats;
        this.teamId = teamId;
    }

    public SummonerDto getSummoner(){
        return summoner;
    }

    public void setSummoner(SummonerDto summoner){
        this.summoner = summoner;
    }

    public ChampionDto getChampion(){
        return champion;
    }

    public void setChampion(ChampionDto champion){
        this.champion = champion;
    }

    public String getHighestAchievedSeasonTier(){
        return highestAchievedSeasonTier;
    }

    public void setHighestAchievedSeasonTier(String highestAchievedSeasonTier){
        this.highestAchievedSeasonTier = highestAchievedSeasonTier;
    }

    public List<Mastery> getMasteries(){
        return masteries;
    }

    public void setMasteries(List<Mastery> masteries){
        this.masteries = masteries;
    }

    public int getParticipantId(){
        return participantId;
    }

    public void setParticipantId(int participantId){
        this.participantId = participantId;
    }

    public List<Rune> getRunes(){
        return runes;
    }

    public void setRunes(List<Rune> runes){
        this.runes = runes;
    }

    public SummonerSpellDto getSpell1(){
        return spell1;
    }

    public void setSpell1(SummonerSpellDto spell1){
        this.spell1 = spell1;
    }

    public SummonerSpellDto getSpell2(){
        return spell2;
    }

    public void setSpell2(SummonerSpellDto spell2){
        this.spell2 = spell2;
    }

    public ParticipantStats getStats(){
        return stats;
    }

    public void setStats(ParticipantStats stats){
        this.stats = stats;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

    public static LeagueAPI getApi(){
        return api;
    }

    public static void setApi(LeagueAPI api){
        RankedPlayer.api = api;
    }

}
