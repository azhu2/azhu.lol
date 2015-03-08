package league.entities.azhu;

import java.util.List;

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.Mastery;
import league.entities.Participant;
import league.entities.Rune;
import league.entities.SummonerSpellDto;

public class RankedPlayerImpl extends MatchPlayer{
    private String highestAchievedSeasonTier;
    private List<Mastery> masteries;
    private int participantId;
    private List<Rune> runes;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private RankedStatsImpl stats;

    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();

    public RankedPlayerImpl(){

    }

    public RankedPlayerImpl(Summoner summoner, Participant participant) throws RiotPlsException{
        setSummoner(summoner);
        setChampion(api.getChampFromId(participant.getChampionId()));
        highestAchievedSeasonTier = participant.getHighestAchievedSeasonTier();
        masteries = participant.getMasteries();
        participantId = participant.getParticipantId();
        runes = participant.getRunes();
        spell1 = api.getSummonerSpellFromId(participant.getSpell1Id());
        spell2 = api.getSummonerSpellFromId(participant.getSpell2Id());
        stats = new RankedStatsImpl(participant.getStats());
        setTeamId(participant.getTeamId());
    }

    @Override
    public String toString(){
        return "RankedPlayer [summoner=" + getSummoner() + ", champion=" + getChampion() + "]";
    }

    public RankedPlayerImpl(Summoner summoner, ChampionDto champion, String highestAchievedSeasonTier,
            List<Mastery> masteries, int participantId, List<Rune> runes, SummonerSpellDto spell1,
            SummonerSpellDto spell2, RankedStatsImpl stats, int teamId){
        super();
        setSummoner(summoner);
        setChampion(champion);
        this.highestAchievedSeasonTier = highestAchievedSeasonTier;
        this.masteries = masteries;
        this.participantId = participantId;
        this.runes = runes;
        this.spell1 = spell1;
        this.spell2 = spell2;
        this.stats = stats;
        setTeamId(teamId);
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

    public RankedStatsImpl getStats(){
        return stats;
    }

    public void setStats(RankedStatsImpl stats){
        this.stats = stats;
    }

    public static LeagueAPI getApi(){
        return api;
    }

    public static void setApi(LeagueAPI api){
        RankedPlayerImpl.api = api;
    }

}
