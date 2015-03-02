package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import league.api.APIConstants;
import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.PlayerDto;
import league.entities.RawStatsDto;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public class Game{
    private long createDate;
    private List<GamePlayer> blueTeam;
    private List<GamePlayer> redTeam;
    private long gameId;
    private String gameMode;
    private String gameType;
    private int ipEarned;
    private int level;
    private int mapId;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private GameStats stats;
    private String subType;
    private int teamId;
    private SummonerDto summoner;
    private long summonerId;        // Redundant, just used for table index

    @Deprecated
    private int lookupPlayer;

    private LeagueAPI api = DynamicLeagueAPIImpl.getInstance();

    public Game(){

    }

    public Game(GameDto game, long summonerId) throws RiotPlsException{
        createDate = game.getCreateDate();
        gameId = game.getGameId();
        gameMode = game.getGameMode();
        gameType = game.getGameType();
        ipEarned = game.getIpEarned();
        level = game.getLevel();
        mapId = game.getMapId();
        stats = new GameStats(game.getStats());
        subType = game.getSubType();
        teamId = game.getTeamId();
        this.summonerId = summonerId;

        blueTeam = new LinkedList<>();
        redTeam = new LinkedList<>();

        spell1 = api.getSummonerSpellFromId(game.getSpell1());
        spell2 = api.getSummonerSpellFromId(game.getSpell2());

        List<PlayerDto> fellowPlayers = new LinkedList<>(game.getFellowPlayers());
        fellowPlayers.add(new PlayerDto(game.getChampionId(), summonerId, game.getTeamId()));
        List<Long> summonerIds = new LinkedList<>();
        for(PlayerDto player : fellowPlayers)
            summonerIds.add(player.getSummonerId());
        List<SummonerDto> summoners = api.getSummoners(summonerIds);

        // Janky stuff that works cause getSummoners returns summoners in same order as requested ids
        for(int i = 0; i < fellowPlayers.size(); i++){
            PlayerDto p = fellowPlayers.get(i);

            SummonerDto summoner = summoners.get(i);
            ChampionDto champion = api.getChampFromId(p.getChampionId());
            int teamId = p.getTeamId();
            GamePlayer player = new GamePlayer(champion, summoner, teamId);

            if(player.getTeamId() == APIConstants.BLUE_TEAM)
                blueTeam.add(player);
            else
                redTeam.add(player);
        }

        // Last one is lookup player... this is so bad lol
        lookupPlayer = blueTeam.size();
    }

    public Game(long createDate, List<GamePlayer> blueTeam, List<GamePlayer> redTeam, long gameId, String gameMode,
            String gameType, int ipEarned, int level, int mapId, SummonerSpellDto spell1, SummonerSpellDto spell2,
            GameStats stats, String subType, int teamId, SummonerDto summoner, int lookupPlayer, long summonerId){
        super();
        this.createDate = createDate;
        this.blueTeam = blueTeam;
        this.redTeam = redTeam;
        this.gameId = gameId;
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.ipEarned = ipEarned;
        this.level = level;
        this.mapId = mapId;
        this.spell1 = spell1;
        this.spell2 = spell2;
        this.stats = stats;
        this.subType = subType;
        this.teamId = teamId;
        this.summoner = summoner;
        this.lookupPlayer = lookupPlayer;
        this.summonerId = summonerId;
    }

    public Game(long createDate, List<GamePlayer> blueTeam, List<GamePlayer> redTeam, long gameId, String gameMode,
            String gameType, int ipEarned, int level, int mapId, SummonerSpellDto spell1, SummonerSpellDto spell2,
            RawStatsDto stats, String subType, int teamId, SummonerDto summoner, int lookupPlayer, long summonerId){
        this(createDate, blueTeam, redTeam, gameId, gameMode, gameType, ipEarned, level, mapId, spell1, spell2,
             new GameStats(stats), subType, teamId, summoner, lookupPlayer, summonerId);
    }

    @Override
    public String toString(){
        return "Game " + gameId;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (gameId ^ (gameId >>> 32));
        result = prime * result + ((summoner == null) ? 0 : summoner.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Game other = (Game) obj;
        if(gameId != other.gameId)
            return false;
        if(summoner == null){
            if(other.summoner != null)
                return false;
        } else if(!summoner.equals(other.summoner))
            return false;
        return true;
    }

    public long getCreateDate(){
        return createDate;
    }

    public void setCreateDate(long createDate){
        this.createDate = createDate;
    }

    public List<GamePlayer> getBlueTeam(){
        return blueTeam;
    }

    public void setBlueTeam(List<GamePlayer> blueTeam){
        this.blueTeam = blueTeam;
    }

    public List<GamePlayer> getRedTeam(){
        return redTeam;
    }

    public void setRedTeam(List<GamePlayer> redTeam){
        this.redTeam = redTeam;
    }

    public long getGameId(){
        return gameId;
    }

    public void setGameId(long gameId){
        this.gameId = gameId;
    }

    public String getGameMode(){
        return gameMode;
    }

    public void setGameMode(String gameMode){
        this.gameMode = gameMode;
    }

    public String getGameType(){
        return gameType;
    }

    public void setGameType(String gameType){
        this.gameType = gameType;
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

    public int getMapId(){
        return mapId;
    }

    public void setMapId(int mapId){
        this.mapId = mapId;
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

    public GameStats getStats(){
        return stats;
    }

    public void setStats(GameStats stats){
        this.stats = stats;
    }

    public String getSubType(){
        return subType;
    }

    public void setSubType(String subType){
        this.subType = subType;
    }

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

    public SummonerDto getSummoner(){
        return summoner;
    }

    public void setSummoner(SummonerDto summoner){
        this.summoner = summoner;
    }

    public int getLookupPlayer(){
        return lookupPlayer;
    }

    public void setLookupPlayer(int lookupPlayer){
        this.lookupPlayer = lookupPlayer;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }

}
