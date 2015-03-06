package league.neo4j;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import league.api.NewLeagueDatabaseAPI;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ItemDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.GeneralMatchImpl;
import league.entities.azhu.League;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.Summoner;
import league.neo4j.entities.Champion4j;
import league.neo4j.entities.Item4j;
import league.neo4j.entities.Summoner4j;
import league.neo4j.entities.SummonerSpell4j;

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

public class Neo4jDatabaseAPIImpl implements NewLeagueDatabaseAPI, Neo4jLeagueDatabaseAPI{
    private static final String DB_PATH = "lol.db";

    private static Logger log = Logger.getLogger(Neo4jDatabaseAPIImpl.class.getName());
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
        log.info(String.format(new Timestamp(System.currentTimeMillis()) + ": " + "Database %s connected.\n", DB_PATH));

        setIndices();
    }

    private void setIndices(){
        try(Transaction tx = db.beginTx()){
            String stmt = "CREATE INDEX ON :Summoner(id)";
            engine.execute(stmt);
            tx.success();
        }
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
    public Summoner getSummonerNewFromId(long summonerId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.id = %d return n", summonerId);
            ExecutionResult results = engine.execute(stmt);
            Summoner summoner = parseSummoner(results);

            tx.success();
            return summoner;
        }
    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds){
        List<Summoner> summoners = new LinkedList<>();

        for(Long id : summonerIds){
            summoners.add(getSummonerNewFromId(id));
        }

        return null;
    }

    @Override
    public Summoner searchSummonerNew(String summonerName){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.name =~ '(?i)%s' return n", summonerName);
            ExecutionResult results = engine.execute(stmt);
            Summoner summoner = parseSummoner(results);

            tx.success();
            return summoner;
        }
    }

    private Summoner parseSummoner(ExecutionResult results){
        ResourceIterator<Map<String, Object>> itr = results.iterator();

        if(!itr.hasNext()){
            System.out.println("Neo4j: Summoner not found.");
            return null;
        }

        Map<String, Object> found = itr.next();
        Node node = (Node) found.get("n");
        Summoner summoner = new Summoner4j(node);
        itr.close();

        log.info("Neo4j: Fetched summoner " + summoner);
        return summoner;
    }

    @Override
    public void cacheSummoner(Summoner summoner){
        try(Transaction tx = db.beginTx()){
            Summoner s = new Summoner4j(summoner);
            String objectMap = mapper.writeValueAsString(s);
            String stmt = String.format("CREATE (n:Summoner %s)", objectMap);
            engine.execute(stmt);

            log.info("Neo4j: cached summoner " + summoner);
            tx.success();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds, boolean cache){
        // This okay?
        return getSummonersNew(summonerIds);
    }

    @Override
    public ChampionDto getChampFromId(long champId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Champion) WHERE n.id = %d return n", champId);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            if(!itr.hasNext()){
                System.out.println("Neo4j: Champion " + champId + " not found.");
                return null;
            }

            Map<String, Object> found = itr.next();
            Node node = (Node) found.get("n");
            ChampionDto champion = new Champion4j(node);
            itr.close();

            log.info("Neo4j: Fetched champion " + champion);
            tx.success();
            return champion;
        }
    }

    @Override
    public void cacheChampion(ChampionDto champion){
        try(Transaction tx = db.beginTx()){
            ChampionDto c = new Champion4j(champion);
            String objectMap = mapper.writeValueAsString(c);
            String stmt = String.format("CREATE (n:Champion %s)", objectMap);
            engine.execute(stmt);

            log.info("Neo4j: cached champion " + champion);
            tx.success();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public ItemDto getItemFromId(long itemId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Item) WHERE n.id = %d return n", itemId);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            if(!itr.hasNext()){
                System.out.println("Neo4j: Item" + itemId + " not found.");
                return null;
            }

            Map<String, Object> found = itr.next();
            Node node = (Node) found.get("n");
            ItemDto item = new Item4j(node);
            itr.close();

            log.info("Neo4j: Fetched item " + item);
            tx.success();
            return item;
        }
    }

    @Override
    public void cacheItem(ItemDto item){
        try(Transaction tx = db.beginTx()){
            ItemDto i = new Item4j(item);
            String objectMap = mapper.writeValueAsString(i);
            String stmt = String.format("CREATE (n:Item %s)", objectMap);
            engine.execute(stmt);

            log.info("Neo4j: cached item " + item);
            tx.success();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summonerspell) WHERE n.id = %d return n", spellId);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            if(!itr.hasNext()){
                System.out.println("Neo4j: Summoner spell " + spellId + " not found.");
                return null;
            }

            Map<String, Object> found = itr.next();
            Node node = (Node) found.get("n");
            SummonerSpellDto spell = new SummonerSpell4j(node);
            itr.close();

            log.info("Neo4j: Fetched summoner spell " + spell);
            tx.success();
            return spell;
        }
    }

    @Override
    public void cacheSummonerSpell(SummonerSpellDto spell){
        try(Transaction tx = db.beginTx()){
            SummonerSpellDto i = new SummonerSpell4j(spell);
            String objectMap = mapper.writeValueAsString(i);
            String stmt = String.format("CREATE (n:Summonerspell %s)", objectMap);
            engine.execute(stmt);

            log.info("Neo4j: cached summoner spell " + spell);
            tx.success();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public MatchDetail getMatchDetail(long matchId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int cacheAllRankedMatches(long summonerId){
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public League getLeague(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<League> getLeagues(List<Long> idList){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheRankedMatch(RankedMatchImpl match){
        // TODO Auto-generated method stub
    }

    @Override
    public RankedMatchImpl getRankedMatch(long matchId, long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasRankedMatch(RankedMatchImpl match){
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public GeneralMatchImpl getGame(long matchId, long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheGame(GeneralMatchImpl match){
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasGame(GeneralMatchImpl game){
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<RankedMatchImpl> getRankedMatchesAll(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GeneralMatchImpl> getGamesAll(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheRankedMatches(List<RankedMatchImpl> matches){
        // TODO Auto-generated method stub

    }

    @Override
    public MatchPlayer getMatchPlayer(long matchId, long summonerId){
        // TODO implement
        return null;
    }
    
    @Override
    public void cacheMatchPlayer(MatchPlayer player){
        // TODO implement
    }
    
    @Override
    public void setInfiniteRetry(boolean infinite){
        // Nothing to do here
    }

    @Override
    @Deprecated
    public Set<GameDto> getMatchHistory(long summonerId){
        return null;
    }

    @Override
    @Deprecated
    public Set<GameDto> getMatchHistoryAll(long summonerId){
        return null;
    }

    @Override
    @Deprecated
    public List<MatchSummary> getRankedMatches(long summonerId){
        return null;
    }

    @Override
    @Deprecated
    public SummonerDto getSummonerFromId(long summonerId){
        return null;
    }

    @Override
    @Deprecated
    public List<SummonerDto> getSummoners(List<Long> summonerIds){
        return null;
    }

    @Override
    @Deprecated
    public SummonerDto searchSummoner(String summonerName){
        return null;
    }

    @Override
    @Deprecated
    public List<MatchSummary> getAllRankedMatches(long summonerId){
        return null;
    }

    public static void main(String[] args){
        try{
            Neo4jDatabaseAPIImpl n = Neo4jDatabaseAPIImpl.getInstance();
            // Summoner summ = NewDatabaseAPIImpl.getInstance().searchSummonerNew("zedenstein");
            // Summoner s = new Summoner4j(summ);
            // n.cacheSummoner(summ);
            System.out.println(n.searchSummonerNew("l am bjerg"));
            System.out.println(n.getSummonerNewFromId(31569637));
            System.out.println(n.searchSummonerNew("irascent"));

            // ChampionDto c = NewDatabaseAPIImpl.getInstance().getChampFromId(1);
            // n.cacheChampion(c);
            System.out.println(n.getChampFromId(1));

            // ItemDto i = NewDatabaseAPIImpl.getInstance().getItemFromId(1055);
            // n.cacheItem(i);
            System.out.println(n.getItemFromId(3031));
            
//            SummonerSpellDto s = NewDatabaseAPIImpl.getInstance().getSummonerSpellFromId(4);
//            n.cacheSummonerSpell(s);
            System.out.println(n.getSummonerSpellFromId(4));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
