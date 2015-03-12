package league.neo4j.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import league.LeagueConstants;
import league.api.APIConstants;
import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ItemDto;
import league.entities.PlayerDto;
import league.entities.RawStatsDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.Summoner;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;

import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

public class GeneralMatch4j extends Match{
    private int ipEarned;
    private int level;
    private int teamId;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private Summoner summoner;

    private long summonerId;
    private List<ItemDto> items;

    private static Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
    private static Logger log = Logger.getLogger(GeneralMatch4j.class.getName());

    public GeneralMatch4j(){

    }

    public GeneralMatch4j(GameDto game, long summonerId){
        setId(game.getGameId());
        setMapId(game.getMapId());
        setMatchCreation(game.getCreateDate());
        setMatchDuration(game.getStats().getTimePlayed());
        setMatchMode(game.getGameMode());
        setMatchType(game.getGameType());
        setQueueType(game.getSubType());

        setIpEarned(game.getIpEarned());
        setLevel(game.getLevel());
        setTeamId(game.getTeamId());
        setSummonerId(summonerId);
        try{
            setSpell1(api.getSummonerSpellFromId(game.getSpell1()));
            setSpell2(api.getSummonerSpellFromId(game.getSpell2()));
            setSummoner(api.getSummonerFromId(summonerId));
            if(game.getTeamId() == LeagueConstants.BLUE_TEAM)
                addToBlueTeam(new GeneralPlayer4j(getSummoner(), api.getChampionFromId(game.getChampionId()),
                        game.getTeamId()));
            else
                addToRedTeam(new GeneralPlayer4j(getSummoner(), api.getChampionFromId(game.getChampionId()),
                        game.getTeamId()));
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }

        processPlayers(game.getFellowPlayers());
        processItems(game.getStats());
    }

    public GeneralMatch4j(Node node){
        super(node);

        setIpEarned((int) (long) node.getProperty("ipEarned"));
        setLevel((int) (long) node.getProperty("level"));
        setTeamId((int) (long) node.getProperty("teamId"));
        setSummonerId((long) node.getProperty("summonerId"));
    }

    private void processPlayers(List<PlayerDto> fellowPlayers){
        List<Long> summonerIds = new LinkedList<>();
        for(PlayerDto dto : fellowPlayers)
            summonerIds.add(dto.getSummonerId());
        List<Summoner> summoners;
        try{
            summoners = api.getSummoners(summonerIds);
            for(int i = 0; i < summonerIds.size(); i++){
                ChampionDto champion = api.getChampionFromId(fellowPlayers.get(i).getChampionId());
                MatchPlayer player = new GeneralPlayer4j(summoners.get(i), champion, fellowPlayers.get(i).getTeamId());
                if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
                    addToBlueTeam(player);
                else
                    addToRedTeam(player);
            }
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
    }

    private void processItems(RawStatsDto stats){
        items = new LinkedList<>();

        LinkedList<Integer> itemIds = new LinkedList<>();
        itemIds.add(stats.getItem0());
        itemIds.add(stats.getItem1());
        itemIds.add(stats.getItem2());
        itemIds.add(stats.getItem3());
        itemIds.add(stats.getItem4());
        itemIds.add(stats.getItem5());
        itemIds.add(stats.getItem6());
        
        for(Integer itemId : itemIds){
            try{
                ItemDto item = api.getItemFromId(itemId);
                if(item == null && itemId != 0)
                    item = new ItemDto("This item has been removed.", itemId, APIConstants.REMOVED_IMAGE, "Removed item",
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
                items.set(i, APIConstants.DUMMY_ITEM);
        }
    }

    @Override
    @JsonView(Views.RestView.class)
    public List<MatchPlayer> getBlueTeam(){
        return super.getBlueTeam();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setBlueTeam(List<MatchPlayer> team){
        super.setBlueTeam(team);
    }

    @Override
    @JsonView(Views.RestView.class)
    public List<MatchPlayer> getRedTeam(){
        return super.getRedTeam();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setRedTeam(List<MatchPlayer> team){
        super.setRedTeam(team);
    }

    @Override
    public void addToBlueTeam(MatchPlayer player){
        super.addToBlueTeam(player);
    }

    @Override
    public void addToRedTeam(MatchPlayer player){
        super.addToRedTeam(player);
    }

    public int getIpEarned(){
        return ipEarned;
    }

    public void setIpEarned(int ipEarned){
        this.ipEarned = ipEarned;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
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
    public Summoner getSummoner(){
        return summoner;
    }

    @JsonView(Views.RestView.class)
    public void setSummoner(Summoner summoner){
        this.summoner = summoner;
    }

    @JsonView(Views.RestView.class)
    public List<ItemDto> getItems(){
        return items;
    }

    @JsonView(Views.RestView.class)
    public void setItems(List<ItemDto> items){
        this.items = items;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }
}
