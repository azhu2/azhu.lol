package league.neo4j.entities;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ImageDto;
import league.entities.ItemDto;
import league.entities.Mastery;
import league.entities.Participant;
import league.entities.ParticipantStats;
import league.entities.Rune;
import league.entities.SummonerSpellDto;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedPlayerImpl;
import league.entities.azhu.Summoner;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

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

    private static Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
    private static Logger log = Logger.getLogger(RankedMatch4j.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    private static final ItemDto DUMMY_ITEM = new ItemDto("No item", 0, null, "none", "none");
    private static final ImageDto REMOVED_IMAGE = new ImageDto("item_removed.png");

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
        setStats(player.getStats());
        addItems();
    }

    public RankedPlayer4j(Node node){
        super(node);
        setParticipantId((int) (long) node.getProperty("participantId"));
        setHighestAchievedSeasonTier((String) node.getProperty("highestAchievedSeasonTier"));
        try{
            setStats(mapper.readValue((String) node.getProperty("statsString"), ParticipantStats.class));
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    public RankedPlayer4j(Summoner summoner, Participant participant) throws RiotPlsException{
        setSummoner(summoner);
        setChampion(api.getChampionFromId(participant.getChampionId()));
        setHighestAchievedSeasonTier(participant.getHighestAchievedSeasonTier());
        setMasteries(participant.getMasteries());
        setParticipantId(participant.getParticipantId());
        setRunes(participant.getRunes());
        setSpell1(api.getSummonerSpellFromId(participant.getSpell1Id()));
        setSpell2(api.getSummonerSpellFromId(participant.getSpell2Id()));
        setStats(participant.getStats());
        setTeamId(participant.getTeamId());
        addItems();
    }

    private void addItems(){
        items = new LinkedList<>();

        LinkedList<Integer> itemIds = new LinkedList<>();
        // Casting is done because stats has item ids as longs, but lookup is done as ints
        itemIds.add((int) stats.getItem0());
        itemIds.add((int) stats.getItem1());
        itemIds.add((int) stats.getItem2());
        itemIds.add((int) stats.getItem3());
        itemIds.add((int) stats.getItem4());
        itemIds.add((int) stats.getItem5());
        itemIds.add((int) stats.getItem6());
        
        for(Integer itemId : itemIds){
            try{
                ItemDto item = api.getItemFromId(itemId);
                if(item == null && itemId != 0)
                    item = new ItemDto("This item has been removed.", itemId, REMOVED_IMAGE, "Removed item",
                            "Item removed");
                items.add(item);
            } catch(RiotPlsException e){
                items.add(null);
                log.warning(e.getMessage());
            }
        }

        processItems();
    }

    /**
     * Replace null items with a dummy ItemDto object
     */
    private void processItems(){
        for(int i = 0; i < items.size(); i++){
            ItemDto item = items.get(i);
            if(item == null)
                items.set(i, DUMMY_ITEM);
        }
    }

    @Override
    @JsonView(Views.RestView.class)
    public ChampionDto getChampion(){
        return super.getChampion();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setChampion(ChampionDto champion){
        super.setChampion(champion);
    }

    @Override
    @JsonView(Views.RestView.class)
    public Summoner getSummoner(){
        return super.getSummoner();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setSummoner(Summoner summoner){
        super.setSummoner(summoner);
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

    @JsonView(Views.RestView.class)
    public List<Mastery> getMasteries(){
        return masteries;
    }

    @JsonView(Views.RestView.class)
    public void setMasteries(List<Mastery> masteries){
        this.masteries = masteries;
    }

    @JsonView(Views.RestView.class)
    public List<Rune> getRunes(){
        return runes;
    }

    @JsonView(Views.RestView.class)
    public void setRunes(List<Rune> runes){
        this.runes = runes;
    }

    @JsonView(Views.RestView.class)
    public ParticipantStats getStats(){
        return stats;
    }

    @JsonView(Views.RestView.class)
    public void setStats(ParticipantStats stats){
        this.stats = stats;
        try{
            this.statsString = mapper.writeValueAsString(stats);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @JsonView(Views.RestView.class)
    public SummonerSpellDto getSpell1(){
        return spell1;
    }

    @JsonView(Views.RestView.class)
    public void setSpell1(SummonerSpellDto spell1){
        this.spell1 = spell1;
    }

    @JsonView(Views.RestView.class)
    public SummonerSpellDto getSpell2(){
        return spell2;
    }

    @JsonView(Views.RestView.class)
    public void setSpell2(SummonerSpellDto spell2){
        this.spell2 = spell2;
    }

    @JsonView(Views.RestView.class)
    public List<ItemDto> getItems(){
        return items;
    }

    @JsonView(Views.RestView.class)
    public void setItems(List<ItemDto> items){
        this.items = items;
        processItems();
    }

    @JsonView(Views.Neo4jView.class)
    public String getStatsString(){
        return statsString;
    }

    @JsonView(Views.Neo4jView.class)
    public void setStatsString(String statsString){
        this.statsString = statsString;
    }
}
