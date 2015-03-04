package league.neo4j;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import league.api.NewLeagueDBAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ItemDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.Game;
import league.entities.azhu.League;
import league.entities.azhu.RankedMatch;
import league.entities.azhu.Summoner;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jDatabaseAPIImpl implements NewLeagueDBAPI{
    private static final String DB_PATH = "lol.db";
    private GraphDatabaseService db;
    private ObjectMapper mapper = new ObjectMapper();
    private ExecutionEngine engine;
    private static Neo4jDatabaseAPIImpl _instance = new Neo4jDatabaseAPIImpl();

    public static Neo4jDatabaseAPIImpl getInstance(){
        return _instance;
    }

    private Neo4jDatabaseAPIImpl(){
        createDb();
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
    }

    private void createDb(){
        db = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        registerShutdownHook(db);
        engine = new ExecutionEngine(db);
        System.out.printf(new Timestamp(System.currentTimeMillis()) + ": " + "Database %s created.\n", DB_PATH);
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                graphDb.shutdown();
            }
        });
    }

    @Override
    public ChampionDto getChampFromId(long champId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchDetail getMatchDetail(long matchId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public Set<GameDto> getMatchHistory(long summonerId) throws RiotPlsException{
        return null;
    }

    @Override
    @Deprecated
    public Set<GameDto> getMatchHistoryAll(long summonerId) throws RiotPlsException{
        return null;
    }

    @Override
    @Deprecated
    public List<MatchSummary> getRankedMatches(long summonerId) throws RiotPlsException{
        return null;
    }

    @Override
    @Deprecated
    public SummonerDto getSummonerFromId(long summonerId) throws RiotPlsException{
        return null;
    }

    @Override
    @Deprecated
    public List<SummonerDto> getSummoners(List<Long> summonerIds) throws RiotPlsException{
        return null;
    }

    @Override
    @Deprecated
    public SummonerDto searchSummoner(String summonerName) throws RiotPlsException{
        return null;
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<MatchSummary> getAllRankedMatches(long summonerId) throws RiotPlsException{
        return null;
    }

    @Override
    public int cacheAllRankedMatches(long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public League getLeague(long summonerId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<League> getLeagues(List<Long> idList) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInfiniteRetry(boolean infinite){
        // Nothing to do here
    }

    @Override
    public ItemDto getItem(long itemId) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheRankedMatch(RankedMatch match){
        // TODO Auto-generated method stub

    }

    @Override
    public RankedMatch getRankedMatch(long matchId, long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasRankedMatch(RankedMatch match){
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Game getGame(long matchId, long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheGame(Game match){
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasGame(Game game){
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<RankedMatch> getRankedMatchesAll(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Game> getGamesAll(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Summoner getSummonerNewFromId(long summonerId) throws RiotPlsException{
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.id = %d return n", summonerId);
            ExecutionResult results = engine.execute(stmt);
            Summoner summoner = parseSummoner(results);

            tx.success();
            return summoner;
        }
    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds) throws RiotPlsException{
        List<Summoner> summoners = new LinkedList<>();
        
        for(Long id : summonerIds){
            summoners.add(getSummonerNewFromId(id));
        }
        
        return null;
    }

    @Override
    public Summoner searchSummonerNew(String summonerName) throws RiotPlsException{
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.name =~ '(?i)%s' return n", summonerName);
            ExecutionResult results = engine.execute(stmt);
            Summoner summoner = parseSummoner(results);
            
            tx.success();
            return summoner;
        }
    }

    private Summoner parseSummoner(ExecutionResult results) throws RiotPlsException{
        ResourceIterator<Map<String, Object>> itr = results.iterator();

        if(!itr.hasNext()){
            System.out.println("Summoner not found.");
            return null;
        }

        Map<String, Object> found = itr.next();
        Node node = (Node) found.get("n");
        Summoner summoner = new Summoner(node);
        itr.close();
        
        League league = getLeague(summoner.getId());
        summoner.setLeague(league);
        
        return summoner;
    }
    
    @Override
    public void cacheSummoner(Summoner summoner){
        try(Transaction tx = db.beginTx()){
            String objectMap = mapper.writeValueAsString(summoner);
            String stmt = String.format("CREATE (n:Summoner %s)", objectMap);
            engine.execute(stmt);
            tx.success();
            
            cacheLeague(summoner.getLeague());
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void cacheLeague(League league){
        // TODO: Implement
    }

    @Override
    public void cacheRankedMatches(List<RankedMatch> matches){
        // TODO Auto-generated method stub

    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds, boolean cache) throws RiotPlsException{
        // TODO Auto-generated method stub
        return null;
    }

    public static void main(String[] args){
        try{
            Neo4jDatabaseAPIImpl n = Neo4jDatabaseAPIImpl.getInstance();
//            Summoner summ = NewDatabaseAPIImpl.getInstance().searchSummonerNew("l am bjerg");
//            n.cacheSummoner(new Summoner4j(summ));
            System.out.println(n.searchSummonerNew("l am bjerg"));
            System.out.println(n.getSummonerNewFromId(31569637));
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }
}
