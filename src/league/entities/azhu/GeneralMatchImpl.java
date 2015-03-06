package league.entities.azhu;

import java.util.LinkedList;
import java.util.List;

import league.LeagueConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.PlayerDto;
import league.entities.RawStatsDto;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public class GeneralMatchImpl extends Match{
    private int ipEarned;
    private int level;
    private SummonerSpellDto spell1;
    private SummonerSpellDto spell2;
    private GeneralStatsImpl stats;
    private int teamId;
    private SummonerDto summoner;
    private long summonerId;        // Redundant, just used for table index

    @Deprecated
    private int lookupPlayer;

    private NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();

    public GeneralMatchImpl(){

    }

    public GeneralMatchImpl(GameDto game, long summonerId) throws RiotPlsException{
        setMatchCreation(game.getCreateDate());
        setId(game.getGameId());
        setMatchMode(game.getGameMode());
        setMatchType(game.getGameType());
        setIpEarned(game.getIpEarned());
        setLevel(game.getLevel());
        setMapId(game.getMapId());
        setStats(new GeneralStatsImpl(game.getStats()));
        setQueueType(game.getSubType());
        setTeamId(game.getTeamId());
        setSummonerId(summonerId);

        spell1 = api.getSummonerSpellFromId(game.getSpell1());
        spell2 = api.getSummonerSpellFromId(game.getSpell2());

        List<PlayerDto> fellowPlayers = new LinkedList<>(game.getFellowPlayers());
        fellowPlayers.add(new PlayerDto(game.getChampionId(), summonerId, game.getTeamId()));
        List<Long> summonerIds = new LinkedList<>();
        for(PlayerDto player : fellowPlayers)
            summonerIds.add(player.getSummonerId());
        List<Summoner> summoners = api.getSummonersNew(summonerIds);

        // Janky stuff that works cause getSummoners returns summoners in same order as requested ids
        for(int i = 0; i < fellowPlayers.size(); i++){
            PlayerDto p = fellowPlayers.get(i);

            Summoner summoner = summoners.get(i);
            ChampionDto champion = api.getChampFromId(p.getChampionId());
            int teamId = p.getTeamId();
            GeneralPlayerImpl player = new GeneralPlayerImpl(champion, summoner, teamId);

            if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
                addToBlueTeam(player);
            else
                addToRedTeam(player);
        }

        // Last one is lookup player... this is so bad lol
        lookupPlayer = getBlueTeam().size();
    }

    public GeneralMatchImpl(long createDate, List<MatchPlayer> blueTeam, List<MatchPlayer> redTeam, long gameId,
            String gameMode, String gameType, int ipEarned, int level, int mapId, SummonerSpellDto spell1,
            SummonerSpellDto spell2, GeneralStatsImpl stats, String subType, int teamId, SummonerDto summoner,
            int lookupPlayer, long summonerId){
        super();
        setMatchCreation(createDate);
        setBlueTeam(blueTeam);
        setRedTeam(redTeam);
        setId(gameId);
        setMatchMode(gameMode);
        setMatchType(gameType);
        setIpEarned(ipEarned);
        setLevel(level);
        setMapId(mapId);
        setSpell1(spell1);
        setSpell2(spell2);
        setStats(stats);
        setQueueType(subType);
        setTeamId(teamId);
        setSummoner(summoner);
        setLookupPlayer(lookupPlayer);
        setSummonerId(summonerId);
    }

    public GeneralMatchImpl(long createDate, List<MatchPlayer> blueTeam, List<MatchPlayer> redTeam, long gameId,
            String gameMode, String gameType, int ipEarned, int level, int mapId, SummonerSpellDto spell1,
            SummonerSpellDto spell2, RawStatsDto stats, String subType, int teamId, SummonerDto summoner,
            int lookupPlayer, long summonerId){
        this(createDate, blueTeam, redTeam, gameId, gameMode, gameType, ipEarned, level, mapId, spell1, spell2,
             new GeneralStatsImpl(stats), subType, teamId, summoner, lookupPlayer, summonerId);
    }

    @Override
    public String toString(){
        return "Game " + getId();
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (getId() ^ (getId() >>> 32));
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
        GeneralMatchImpl other = (GeneralMatchImpl) obj;
        if(getId() != other.getId())
            return false;
        if(summoner == null){
            if(other.summoner != null)
                return false;
        } else if(!summoner.equals(other.summoner))
            return false;
        return true;
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

    public GeneralStatsImpl getStats(){
        return stats;
    }

    public void setStats(GeneralStatsImpl stats){
        this.stats = stats;
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
