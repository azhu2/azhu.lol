package league.neo4j.entities;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.Node;

import league.entities.ItemDto;
import league.entities.Mastery;
import league.entities.ParticipantStats;
import league.entities.Rune;
import league.entities.SummonerSpellDto;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedPlayerImpl;

@JsonIgnoreProperties(value = {"champion", "summoner", "masteries", "runes", "stats", "spell1", "spell2", "items"})
public class RankedPlayer4j extends MatchPlayer{
    private String highestAchievedSeasonTier;
    private int participantId;
    private List<Mastery> masteries;
    private List<Rune> runes;
    private ParticipantStats stats;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private List<ItemDto> items;

    private String statsString;
    
    private static Logger log = Logger.getLogger(RankedMatch4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public RankedPlayer4j(){

    }
    
    public RankedPlayer4j(RankedPlayerImpl player){
        setChampion(player.getChampion());
        setHighestAchievedSeasonTier(player.getHighestAchievedSeasonTier());
        setMasteries(player.getMasteries());
        setParticipantId(player.getParticipantId());
        setRunes(player.getRunes());
        setSpell1(player.getSpell1());
        setSpell2(player.getSpell2());
        setStats(player.getStats());
        setSummoner(player.getSummoner());
        setTeamId(player.getTeamId());
    }
    
    public RankedPlayer4j(Node node){
        super(node);
        try{
            setStats(mapper.readValue((String) node.getProperty("statsString"), ParticipantStats.class));
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    public String getHighestAchievedSeasonTier(){
        return highestAchievedSeasonTier;
    }

    public void setHighestAchievedSeasonTier(String highestAchievedSeasonTier){
        this.highestAchievedSeasonTier = highestAchievedSeasonTier;
    }

    public int getParticipantId(){
        return participantId;
    }

    public void setParticipantId(int participantId){
        this.participantId = participantId;
    }

    public List<Mastery> getMasteries(){
        return masteries;
    }

    public void setMasteries(List<Mastery> masteries){
        this.masteries = masteries;
    }

    public List<Rune> getRunes(){
        return runes;
    }

    public void setRunes(List<Rune> runes){
        this.runes = runes;
    }

    public ParticipantStats getStats(){
        return stats;
    }

    public void setStats(ParticipantStats stats){
        this.stats = stats;
        try{
            this.statsString = mapper.writeValueAsString(stats);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
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

    public List<ItemDto> getItems(){
        return items;
    }

    public void setItems(List<ItemDto> items){
        this.items = items;
    }

    public String getStatsString(){
        return statsString;
    }

    public void setStatsString(String statsString){
        this.statsString = statsString;
    }

}
