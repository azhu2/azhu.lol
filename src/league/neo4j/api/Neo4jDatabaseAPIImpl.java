package league.neo4j.api;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import league.LeagueConstants;
import league.api.APIConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.RankedPlayerImpl;
import league.entities.azhu.Summoner;
import league.neo4j.entities.Champion4j;
import league.neo4j.entities.GeneralMatch4j;
import league.neo4j.entities.GeneralPlayer4j;
import league.neo4j.entities.Item4j;
import league.neo4j.entities.RankedMatch4j;
import league.neo4j.entities.RankedPlayer4j;
import league.neo4j.entities.Summoner4j;
import league.neo4j.entities.SummonerSpell4j;
import league.neo4j.entities.Views;

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

public class Neo4jDatabaseAPIImpl implements Neo4jDatabaseAPI{
    private static final String DB_PATH = "/home/alex/workspace/azhu.lol/lol.db";

    // Java cache(-ish) to speed up lookups
    private static Map<Integer, ItemDto> itemMap = new HashMap<>();
    private static Map<Integer, SummonerSpellDto> spellMap = new HashMap<>();
    private static Map<Integer, ChampionDto> championMap = new HashMap<>();

    private static Logger log = Logger.getLogger(Neo4jDatabaseAPIImpl.class.getName());
    private GraphDatabaseService db;
    private ObjectMapper mapper = new ObjectMapper();
    private ExecutionEngine engine;

    private static final Neo4jDatabaseAPIImpl _instance = new Neo4jDatabaseAPIImpl();

    public static Neo4jDatabaseAPIImpl getInstance(){
        return _instance;
    }

    private Neo4jDatabaseAPIImpl(){
        createDb();
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        mapper.getSerializationConfig().setSerializationView(Views.Neo4jView.class);
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
            String stmt = "CREATE INDEX ON :Summoner(id);";
            engine.execute(stmt);
            tx.success();
        }
        try(Transaction tx = db.beginTx()){
            String stmt = "CREATE INDEX ON :Champion(id);";
            engine.execute(stmt);
            tx.success();
        }
        try(Transaction tx = db.beginTx()){
            String stmt = "CREATE INDEX ON :Item(id);";
            engine.execute(stmt);
            tx.success();
        }
        try(Transaction tx = db.beginTx()){
            String stmt = "CREATE INDEX ON :Summonerspell(id);";
            engine.execute(stmt);
            tx.success();
        }
        try(Transaction tx = db.beginTx()){
            String stmt = "CREATE INDEX ON :RankedMatch(id);";
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

    private Node getNode(String query){
        String stmt = query;
        ExecutionResult results = engine.execute(stmt);
        ResourceIterator<Map<String, Object>> itr = results.iterator();

        if(!itr.hasNext())
            return null;

        Map<String, Object> found = itr.next();
        Node node = (Node) found.get("n");
        return node;
    }

    private class CacheThread extends Thread{
        private String stmt;
        private String msg;

        public CacheThread(String stmt, String msg){
            this.stmt = stmt;
            this.msg = msg;
        }

        public void run(){
            try(Transaction tx = db.beginTx()){
                engine.execute(stmt);
                log.info(msg);
                tx.success();
            }
        }
    }

    private void cacheItem(String statement, String logMessage){
        new CacheThread(statement, logMessage).run();
    }

    @Override
    public Summoner getSummonerFromId(long summonerId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.id = %d RETURN n;", summonerId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Summoner " + summonerId + " not found.");
                return null;
            }
            Summoner summoner = new Summoner4j(node);
            log.info("Neo4j: Fetched summoner " + summoner);
            tx.success();
            return summoner;
        }
    }

    @Override
    public List<Summoner> getSummoners(List<Long> summonerIds){
        List<Summoner> summoners = new LinkedList<>();
        for(Long id : summonerIds)
            summoners.add(getSummonerFromId(id));
        return null;
    }

    @Override
    public Summoner searchSummoner(String summonerName){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summoner) WHERE n.name =~ '(?i)%s' RETURN n;", summonerName);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Summoner name " + summonerName + " not found.");
                return null;
            }
            Summoner summoner = new Summoner4j(node);
            log.info("Neo4j: Fetched summoner " + summoner);
            tx.success();
            return summoner;
        }
    }

    @Override
    public void cacheSummoner(Summoner summoner){
        if(summoner == null)
            return;

        try{
            Summoner s = new Summoner4j(summoner);
            String objectMap = mapper.writeValueAsString(s);
            String stmt = String.format("MERGE (n:Summoner { id:%d }) ON CREATE SET n=%s ON MATCH SET n=%s;",
                summoner.getId(), objectMap, objectMap);
            cacheItem(stmt, "Neo4j: cached summoner " + summoner);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public ChampionDto getChampionFromId(int champId){
        ChampionDto champion = championMap.get(champId);
        if(champion != null)
            return champion;

        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Champion) WHERE n.id = %d RETURN n;", champId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Champion " + champId + " not found.");
                return null;
            }
            champion = new Champion4j(node);
            log.info("Neo4j: Fetched champion " + champion);
            tx.success();
            return champion;
        }
    }

    @Override
    public void cacheChampion(ChampionDto champion){
        if(champion == null)
            return;

        if(championMap.containsKey(champion.getId())){
            log.info("Neo4j: " + champion + " already cached.");
            return;
        }

        championMap.put(champion.getId(), champion);
        try{
            ChampionDto c = new Champion4j(champion);
            String objectMap = mapper.writeValueAsString(c);
            String stmt = String.format("MERGE (n:Champion { id:%d }) ON CREATE SET n=%s ON MATCH SET n=%s;",
                champion.getId(), objectMap, objectMap);
            cacheItem(stmt, "Neo4j: cached champion " + champion);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public ItemDto getItemFromId(int itemId){
        ItemDto item = itemMap.get(itemId);
        if(item != null)
            return item;

        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Item) WHERE n.id = %d RETURN n;", itemId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Item " + itemId + " not found.");
                return null;
            }
            item = new Item4j(node);
            log.info("Neo4j: Fetched item " + item);
            tx.success();
            return item;
        }
    }

    @Override
    public void cacheItem(ItemDto item){
        if(item == null)
            return;

        if(itemMap.containsKey(item.getId())){
            log.info("Neo4j: " + item + " already cached.");
            return;
        }

        itemMap.put(item.getId(), item);
        try{
            ItemDto i = new Item4j(item);
            String objectMap = mapper.writeValueAsString(i);
            String stmt = String.format("MERGE (n:Item { id:%d }) ON CREATE SET n=%s ON MATCH SET n=%s;", item.getId(),
                objectMap, objectMap);
            cacheItem(stmt, "Neo4j: cached item " + item);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(int spellId){
        SummonerSpellDto spell = spellMap.get(spellId);
        if(spell != null)
            return spell;

        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Summonerspell) WHERE n.id = %d RETURN n;", spellId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Summoner spell " + spellId + " not found.");
                return null;
            }
            spell = new SummonerSpell4j(node);
            log.info("Neo4j: Fetched summoner spell " + spell);
            tx.success();
            return spell;
        }
    }

    @Override
    public void cacheSummonerSpell(SummonerSpellDto spell){
        if(spell == null)
            return;

        if(spellMap.containsKey(spell.getId())){
            log.info("Neo4j: " + spell + " already cached.");
            return;
        }

        spellMap.put(spell.getId(), spell);
        try{
            SummonerSpellDto i = new SummonerSpell4j(spell);
            String objectMap = mapper.writeValueAsString(i);
            String stmt = String.format("MERGE (n:Summonerspell { id:%d }) ON CREATE SET n=%s ON MATCH SET n=%s;",
                spell.getId(), objectMap, objectMap);
            cacheItem(stmt, "Neo4j: cached summoner spell " + spell);
        } catch(IOException e){
            log.warning(e.getMessage());
        }
    }

    @Override
    public League getLeague(long summonerId){
        Summoner summoner = getSummonerFromId(summonerId);
        return summoner.getLeague();
    }

    @Override
    public List<League> getLeagues(List<Long> idList){
        List<Summoner> summoners = getSummoners(idList);
        List<League> leagues = new LinkedList<>();
        for(Summoner summoner : summoners)
            leagues.add(summoner.getLeague());
        return leagues;
    }

    @Override
    public Match getRankedMatch(long matchId){
        RankedMatch4j match;
        // Fetch match itself
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:RankedMatch) WHERE n.id = %d RETURN n;", matchId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: Ranked match " + matchId + " not found.");
                return null;
            }
            match = new RankedMatch4j(node);
            log.info("Neo4j: Fetched ranked match " + match);
            tx.success();
        }

        populateMatch(match);
        return match;
    }

    private void populateMatch(RankedMatch4j match){
        // Fetch players
        try(Transaction tx = db.beginTx()){
            // @formatter:off
            String stmt = String.format(
                  "MATCH (player:RankedPlayer)-[:PLAYED_IN]->(match:RankedMatch) WHERE match.id = %d "
                + "MATCH (champion:Champion)-[:CHAMP_PLAYED]->(player) "
                + "MATCH (summoner:Summoner)-[:SUMMONER]->(player) "
                + "MATCH (spell1:Summonerspell)-[:SPELL1]->(player) "
                + "MATCH (spell2:Summonerspell)-[:SPELL2]->(player) "
                + "MATCH (item0:Item)-[:ITEM0]->(player) "
                + "MATCH (item1:Item)-[:ITEM1]->(player) "
                + "MATCH (item2:Item)-[:ITEM2]->(player) "
                + "MATCH (item3:Item)-[:ITEM3]->(player) "
                + "MATCH (item4:Item)-[:ITEM4]->(player) "
                + "MATCH (item5:Item)-[:ITEM5]->(player) "
                + "MATCH (item6:Item)-[:ITEM6]->(player) "
                + "RETURN player, champion, summoner, spell1, spell2, "
                + "item0, item1, item2, item3, item4, item5, item6;",
                match.getId());
            // @formatter:on
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node playerNode = (Node) found.get("player");
                RankedPlayer4j player = new RankedPlayer4j(playerNode);

                Node championNode = (Node) found.get("champion");
                Node summonerNode = (Node) found.get("summoner");
                Node spell1Node = (Node) found.get("spell1");
                Node spell2Node = (Node) found.get("spell2");
                Node item0Node = (Node) found.get("item0");
                Node item1Node = (Node) found.get("item1");
                Node item2Node = (Node) found.get("item2");
                Node item3Node = (Node) found.get("item3");
                Node item4Node = (Node) found.get("item4");
                Node item5Node = (Node) found.get("item5");
                Node item6Node = (Node) found.get("item6");

                player.setChampion(new Champion4j(championNode));
                player.setSummoner(new Summoner4j(summonerNode));
                player.setSpell1(new SummonerSpell4j(spell1Node));
                player.setSpell2(new SummonerSpell4j(spell2Node));
                List<ItemDto> items = new LinkedList<>();
                items.add(new Item4j(item0Node));
                items.add(new Item4j(item1Node));
                items.add(new Item4j(item2Node));
                items.add(new Item4j(item3Node));
                items.add(new Item4j(item4Node));
                items.add(new Item4j(item5Node));
                items.add(new Item4j(item6Node));
                player.setItems(items);

                if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
                    match.addToBlueTeam(player);
                else
                    match.addToRedTeam(player);
            }
            tx.success();
        }

        // Bans
        try(Transaction tx = db.beginTx()){
            List<ChampionDto> bans = new LinkedList<>();

            // @formatter:off
            String stmt = String.format(
                  "MATCH (match:RankedMatch) WHERE match.id = %d "
                + "MATCH (ban:Champion)-[:BANNED_BLUE]->(match) "
                + "RETURN ban", match.getId());
            // @formatter:on
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node banNode = (Node) found.get("ban");
                ChampionDto champ = new Champion4j(banNode);
                bans.add(champ);
            }
            match.setBlueBans(bans);
        }
        try(Transaction tx = db.beginTx()){
            List<ChampionDto> bans = new LinkedList<>();

            // @formatter:off
            String stmt = String.format(
                  "MATCH (match:RankedMatch) WHERE match.id = %d "
                + "MATCH (ban:Champion)-[:BANNED_RED]->(match) "
                + "RETURN ban", match.getId());
            // @formatter:on
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node banNode = (Node) found.get("ban");
                ChampionDto champ = new Champion4j(banNode);
                bans.add(champ);
            }
            match.setRedBans(bans);
        }
    }

    @Override
    public boolean hasRankedMatch(long matchId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:RankedMatch) WHERE n.id = %d RETURN n;", matchId);
            Node node = getNode(stmt);
            return node != null;
        }
    }

    /**
     * Use Riot API
     */
    @Override
    public List<Long> getAllRankedMatchIds(long summonerId) throws RiotPlsException{
        return null;
    }

    /**
     * Use Riot API
     */
    @Override
    public List<Long> getRankedMatchIds(long summonerId){
        return null;
    }

    @Override
    public List<Match> getRankedMatches(long summonerId){
        List<Match> matches = new LinkedList<>();

        try(Transaction tx = db.beginTx()){
            String stmt = String.format(
                "MATCH (summoner:Summoner)-[:SUMMONER]->(player:RankedPlayer)-[:PLAYED_IN]->(match:RankedMatch) "
                        + "WHERE summoner.id = %d RETURN match ORDER BY match.matchCreation LIMIT %d ", summonerId,
                APIConstants.RANKED_PAGE_SIZE);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> map = itr.next();
                Node matchNode = (Node) map.get("match");
                RankedMatch4j match = new RankedMatch4j(matchNode);
                populateMatch(match);
                matches.add(match);
            }
        }

        return matches;
    }

    @Override
    public List<Match> getAllRankedMatches(long summonerId){
        List<Match> matches = new LinkedList<>();

        try(Transaction tx = db.beginTx()){
            String stmt = String.format(
                "MATCH (summoner:Summoner)-[:SUMMONER]->(player:RankedPlayer)-[:PLAYED_IN]->(match:RankedMatch) "
                        + "WHERE summoner.id = %d RETURN match", summonerId);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> map = itr.next();
                Node matchNode = (Node) map.get("match");
                RankedMatch4j match = new RankedMatch4j(matchNode);
                populateMatch(match);
                matches.add(match);
            }
        }

        return matches;
    }

    @Override
    public void cacheRankedMatch(Match match){
        if(match == null)
            return;

        if(hasRankedMatch(match.getId())){
            log.info("Neo4j: Match " + match + " already cached.");
            return;
        }
        RankedMatch4j rankedMatch;
        if(match instanceof RankedMatchImpl)
            rankedMatch = new RankedMatch4j((RankedMatchImpl) match);
        else
            rankedMatch = (RankedMatch4j) match;

        // Cache match itself
        try{
            String objectMap = mapper.writeValueAsString(rankedMatch);
            String stmt = String.format("MERGE (n:RankedMatch { id:%d }) ON CREATE SET n=%s ON MATCH SET n=%s;",
                match.getId(), objectMap, objectMap);
            cacheItem(stmt, "Neo4j: cached ranked match " + match);
        } catch(IOException e){
            log.warning(e.getMessage());
        }

        // Cache and link players
        for(MatchPlayer player : rankedMatch.getPlayers()){
            RankedPlayer4j rankedPlayer;
            if(player instanceof RankedPlayerImpl)
                rankedPlayer = new RankedPlayer4j((RankedPlayerImpl) player);
            else
                rankedPlayer = (RankedPlayer4j) player;

            try{
                String objectMap = mapper.writeValueAsString(rankedPlayer);

                cacheChampion(rankedPlayer.getChampion());
                cacheSummoner(rankedPlayer.getSummoner());
                cacheSummonerSpell(rankedPlayer.getSpell1());
                cacheSummonerSpell(rankedPlayer.getSpell2());
                for(ItemDto item : rankedPlayer.getItems())
                    cacheItem(item);

                // @formatter:off
                String statement = "MATCH (match:RankedMatch) WHERE match.id=%d "
                                 + "MATCH (champion:Champion) WHERE champion.id=%d "
                                 + "MATCH (summoner:Summoner) WHERE summoner.id=%d "
                                 + "MATCH (spell1:Summonerspell) WHERE spell1.id=%d "
                                 + "MATCH (spell2:Summonerspell) WHERE spell2.id=%d "
                                 + "MATCH (item0:Item) WHERE item0.id=%d "
                                 + "MATCH (item1:Item) WHERE item1.id=%d "
                                 + "MATCH (item2:Item) WHERE item2.id=%d "
                                 + "MATCH (item3:Item) WHERE item3.id=%d "
                                 + "MATCH (item4:Item) WHERE item4.id=%d "
                                 + "MATCH (item5:Item) WHERE item5.id=%d "
                                 + "MATCH (item6:Item) WHERE item6.id=%d "
                                 + "CREATE (player:RankedPlayer %s) "
                                 + "CREATE (player)-[:PLAYED_IN]->(match) "
                                 + "CREATE (champion)-[:CHAMP_PLAYED]->(player) "
                                 + "CREATE (summoner)-[:SUMMONER]->(player) "
                                 + "CREATE (spell1)-[:SPELL1]->(player) "
                                 + "CREATE (spell2)-[:SPELL2]->(player) "
                                 + "CREATE (item0)-[:ITEM0]->(player) "
                                 + "CREATE (item1)-[:ITEM1]->(player) "
                                 + "CREATE (item2)-[:ITEM2]->(player) "
                                 + "CREATE (item3)-[:ITEM3]->(player) "
                                 + "CREATE (item4)-[:ITEM4]->(player) "
                                 + "CREATE (item5)-[:ITEM5]->(player) "
                                 + "CREATE (item6)-[:ITEM6]->(player);";
                String stmt = String.format(statement, match.getId(), rankedPlayer.getChampion().getId(),
                    rankedPlayer.getSummoner().getId(), rankedPlayer.getSpell1().getId(),
                    rankedPlayer.getSpell2().getId(), rankedPlayer.getItems().get(0).getId(),
                    rankedPlayer.getItems().get(1).getId(), rankedPlayer.getItems().get(2).getId(),
                    rankedPlayer.getItems().get(3).getId(), rankedPlayer.getItems().get(4).getId(),
                    rankedPlayer.getItems().get(5).getId(), rankedPlayer.getItems().get(6).getId(), objectMap);
                // @formatter:on
                cacheItem(stmt, "Neo4j: cached ranked player " + player);
            } catch(IOException e){
                log.warning(e.getMessage());
                continue;
            }
        }

        // Link banned champions
        for(ChampionDto ban : rankedMatch.getBlueBans()){
            cacheChampion(ban);
            // @formatter:off
                String stmt = String.format("MATCH (match:RankedMatch) WHERE match.id = %d "
                                          + "MATCH (ban:Champion) WHERE ban.id=%d "
                                          + "CREATE (ban)-[:BANNED_BLUE]->(match);",
                                          match.getId(), ban.getId());
                // @formatter:on
            cacheItem(stmt, "Neo4j: cached blue banned champion " + ban);
        }
        for(ChampionDto ban : rankedMatch.getRedBans()){
            cacheChampion(ban);
            // @formatter:off
                String stmt = String.format("MATCH (match:RankedMatch) WHERE match.id = %d "
                                          + "MATCH (ban:Champion) WHERE ban.id=%d " 
                                          + "CREATE (ban)-[:BANNED_RED]->(match);",
                                          match.getId(), ban.getId());
                // @formatter:on
            cacheItem(stmt, "Neo4j: cached red banned champion " + ban);
        }
    }

    @Override
    public void cacheRankedMatches(List<Match> matches){
        for(Match match : matches)
            cacheRankedMatch(match);
    }

    /**
     * @deprecated Use the one in the Riot API
     */
    @Override
    public int cacheAllRankedMatches(long summonerId){
        return -1;
    }

    @Override
    public Match getGame(long matchId, long summonerId){
        GeneralMatch4j match;
        // Fetch match itself
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:GeneralMatch) WHERE n.id = %d AND n.summonerId = %d RETURN n;",
                matchId, summonerId);
            Node node = getNode(stmt);

            if(node == null){
                log.warning("Neo4j: General match " + matchId + " not found for summoner " + summonerId);
                return null;
            }
            match = new GeneralMatch4j(node);
            log.info("Neo4j: Fetched general match " + match + " for summoner " + summonerId);
            tx.success();
        }

        populateMatch(match, summonerId);
        return match;
    }

    @Override
    public void cacheGame(Match match, long summonerId){
        if(match == null)
            return;

        if(hasGame(match.getId(), summonerId)){
            log.info("Neo4j: Match " + match + " already cached for summoner " + summonerId);
            return;
        }
        GeneralMatch4j game = (GeneralMatch4j) match;

        // Cache match itself
        try{
            String objectMap = mapper.writeValueAsString(game);
            String stmt = String.format("CREATE (n:GeneralMatch %s);", objectMap);
            cacheItem(stmt, "Neo4j: cached general match " + match + " for summoner " + summonerId);
        } catch(IOException e){
            log.warning(e.getMessage());
        }

        // Cache and link summoner spells and items
        cacheSummonerSpell(game.getSpell1());
        cacheSummonerSpell(game.getSpell2());
        for(ItemDto item : game.getItems())
            cacheItem(item);

        // @formatter:off
            String statement = "MATCH (match:GeneralMatch) WHERE match.id=%d AND match.summonerId = %d "
                             + "MATCH (spell1:Summonerspell) WHERE spell1.id=%d "
                             + "MATCH (spell2:Summonerspell) WHERE spell2.id=%d "
                             + "MATCH (item0:Item) WHERE item0.id=%d "
                             + "MATCH (item1:Item) WHERE item1.id=%d "
                             + "MATCH (item2:Item) WHERE item2.id=%d "
                             + "MATCH (item3:Item) WHERE item3.id=%d "
                             + "MATCH (item4:Item) WHERE item4.id=%d "
                             + "MATCH (item5:Item) WHERE item5.id=%d "
                             + "MATCH (item6:Item) WHERE item6.id=%d "
                             + "CREATE (spell1)-[:SPELL1]->(match) "
                             + "CREATE (spell2)-[:SPELL2]->(match) "
                             + "CREATE (item0)-[:ITEM0]->(match) "
                             + "CREATE (item1)-[:ITEM1]->(match) "
                             + "CREATE (item2)-[:ITEM2]->(match) "
                             + "CREATE (item3)-[:ITEM3]->(match) "
                             + "CREATE (item4)-[:ITEM4]->(match) "
                             + "CREATE (item5)-[:ITEM5]->(match) "
                             + "CREATE (item6)-[:ITEM6]->(match);";
            String stmt = String.format(statement, match.getId(), summonerId, game.getSpell1().getId(),
                game.getSpell2().getId(), game.getItems().get(0).getId(),
                game.getItems().get(1).getId(), game.getItems().get(2).getId(),
                game.getItems().get(3).getId(), game.getItems().get(4).getId(),
                game.getItems().get(5).getId(), game.getItems().get(6).getId());
            // @formatter:on
        cacheItem(stmt, "Neo4j: cached general stats for " + game + " for summoner " + summonerId);

        // Cache and link players
        for(MatchPlayer player : game.getPlayers()){
            GeneralPlayer4j generalPlayer = (GeneralPlayer4j) player;

            try{
                String objectMap = mapper.writeValueAsString(generalPlayer);

                cacheChampion(generalPlayer.getChampion());
                cacheSummoner(generalPlayer.getSummoner());

                // @formatter:off
                statement = "MATCH (match:GeneralMatch) WHERE match.id=%d AND match.summonerId = %d "
                                 + "MATCH (champion:Champion) WHERE champion.id=%d "
                                 + "MATCH (summoner:Summoner) WHERE summoner.id=%d "
                                 + "CREATE (player:GeneralPlayer %s) "
                                 + "CREATE (player)-[:PLAYED_IN]->(match) "
                                 + "CREATE (champion)-[:CHAMP_PLAYED]->(player) "
                                 + "CREATE (summoner)-[:SUMMONER]->(player);";
               stmt = String.format(statement, match.getId(), summonerId, generalPlayer.getChampion().getId(),
                                    generalPlayer.getSummoner().getId(), objectMap);
                // @formatter:on
                cacheItem(stmt, "Neo4j: cached general player " + player);
            } catch(IOException e){
                log.warning(e.getMessage());
                continue;
            }
        }
    }

    private boolean hasGame(long gameId, long summonerId){
        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:GeneralMatch) WHERE n.id = %d AND n.summonerId = %d RETURN n;",
                gameId, summonerId);
            Node node = getNode(stmt);
            return node != null;
        }
    }

    private void populateMatch(GeneralMatch4j match, long summonerId){
        // Fetch players
        try(Transaction tx = db.beginTx()){
            // @formatter:off
            String stmt = String.format(
                  "MATCH (player:GeneralPlayer)-[:PLAYED_IN]->(match:GeneralMatch) "
                + "WHERE match.id = %d AND match.summonerId = %d "
                + "MATCH (champion:Champion)-[:CHAMP_PLAYED]->(player) "
                + "MATCH (summoner:Summoner)-[:SUMMONER]->(player) "
                + "RETURN player, champion, summoner;",
                match.getId(), summonerId);
            // @formatter:on
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node playerNode = (Node) found.get("player");
                GeneralPlayer4j player = new GeneralPlayer4j(playerNode);

                Node championNode = (Node) found.get("champion");
                Node summonerNode = (Node) found.get("summoner");

                player.setChampion(new Champion4j(championNode));
                player.setSummoner(new Summoner4j(summonerNode));

                if(player.getTeamId() == LeagueConstants.BLUE_TEAM)
                    match.addToBlueTeam(player);
                else
                    match.addToRedTeam(player);
            }
            tx.success();
        }

        // Summoner spells & items
        try(Transaction tx = db.beginTx()){
            // @formatter:off
            String stmt = String.format(
                  "MATCH (match:GeneralMatch) WHERE match.id = %d  AND match.summonerId = %d "
                + "MATCH (spell1:Summonerspell)-[:SPELL1]->(match) "
                + "MATCH (spell2:Summonerspell)-[:SPELL2]->(match) "
                + "MATCH (item0:Item)-[:ITEM0]->(match) "
                + "MATCH (item1:Item)-[:ITEM1]->(match) "
                + "MATCH (item2:Item)-[:ITEM2]->(match) "
                + "MATCH (item3:Item)-[:ITEM3]->(match) "
                + "MATCH (item4:Item)-[:ITEM4]->(match) "
                + "MATCH (item5:Item)-[:ITEM5]->(match) "
                + "MATCH (item6:Item)-[:ITEM6]->(match) "
                + "RETURN spell1, spell2, item0, item1, item2, item3, item4, item5, item6;",
                match.getId(), summonerId);
            // @formatter:on
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();

                Node spell1Node = (Node) found.get("spell1");
                Node spell2Node = (Node) found.get("spell2");
                Node item0Node = (Node) found.get("item0");
                Node item1Node = (Node) found.get("item1");
                Node item2Node = (Node) found.get("item2");
                Node item3Node = (Node) found.get("item3");
                Node item4Node = (Node) found.get("item4");
                Node item5Node = (Node) found.get("item5");
                Node item6Node = (Node) found.get("item6");

                match.setSpell1(new SummonerSpell4j(spell1Node));
                match.setSpell2(new SummonerSpell4j(spell2Node));
                List<ItemDto> items = new LinkedList<>();
                items.add(new Item4j(item0Node));
                items.add(new Item4j(item1Node));
                items.add(new Item4j(item2Node));
                items.add(new Item4j(item3Node));
                items.add(new Item4j(item4Node));
                items.add(new Item4j(item5Node));
                items.add(new Item4j(item6Node));
                match.setItems(items);
            }
            tx.success();
        }
    }

    @Override
    public Set<Match> getAllGames(long summonerId){
        Set<Match> games = new HashSet<>();

        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:GeneralMatch) WHERE n.summonerId = %d RETURN n;", summonerId);
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node node = (Node) found.get("n");
                long matchId = (Long) node.getProperty("id");
                games.add(getGame(matchId, summonerId));
            }

            tx.success();
        }
        return games;
    }

    @Override
    public Set<Match> getMatchHistory(long summonerId){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ChampionDto> getChampionList() throws RiotPlsException{
        Set<ChampionDto> champions = new HashSet<>();

        try(Transaction tx = db.beginTx()){
            String stmt = String.format("MATCH (n:Champion) RETURN n;");
            ExecutionResult results = engine.execute(stmt);
            ResourceIterator<Map<String, Object>> itr = results.iterator();

            while(itr.hasNext()){
                Map<String, Object> found = itr.next();
                Node championNode = (Node) found.get("n");
                Champion4j champion = new Champion4j(championNode);
                champions.add(champion);
            }

            tx.success();
        }
        return champions;
    }

    public static void main(String[] args){
        try{
            Neo4jDatabaseAPIImpl n = Neo4jDatabaseAPIImpl.getInstance();
            Summoner summ = NewDatabaseAPIImpl.getInstance().searchSummonerNew("zedenstein");
            n.cacheSummoner(summ);
            System.out.println(n.getSummonerFromId(31569637));

            ChampionDto c = NewDatabaseAPIImpl.getInstance().getChampFromId(1);
            n.cacheChampion(c);
            System.out.println(n.getChampionFromId(1));

            ItemDto i = NewDatabaseAPIImpl.getInstance().getItemFromId(1055);
            n.cacheItem(i);
            System.out.println(n.getItemFromId(3031));

            SummonerSpellDto s = NewDatabaseAPIImpl.getInstance().getSummonerSpellFromId(4);
            n.cacheSummonerSpell(s);
            System.out.println(n.getSummonerSpellFromId(4));

            List<Match> matches = NewDatabaseAPIImpl.getInstance().getRankedMatchesAll(108491);
            for(Match m : matches)
                n.cacheRankedMatch(m);
            System.out.println(n.getRankedMatch(1719650755));
            // System.out.println(n.getAllRankedMatches(108491));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
