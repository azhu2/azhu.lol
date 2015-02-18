package league.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ImageDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.Participant;
import league.entities.ParticipantIdentity;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

public class DatabaseAPIImpl implements LeagueAPI{
    private static Logger log = Logger.getLogger(DatabaseAPIImpl.class.getName());

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/lol";
    private static final String USER = SecurityConstants.DB_USER;
    private static final String PASS = SecurityConstants.DB_PASS;

    private static final int FETCH_ALL = -1;

    private Connection db;
    private ObjectMapper mapper = new ObjectMapper();

    private static DatabaseAPIImpl _instance = new DatabaseAPIImpl();

    public static DatabaseAPIImpl getInstance(){
        return _instance;
    }

    private DatabaseAPIImpl(){
        try{
            Class.forName(JDBC_DRIVER);
            log.info("Connecting to database...");
            db = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch(ClassNotFoundException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public SummonerDto searchSummoner(String summonerName){
        // summonerName = summonerName.toLowerCase().replace(" ", "");

        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate FROM summoners WHERE name = '%s'",
                summonerName);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel, revisionDate);
                log.info("Fetched summoner " + summoner + " from db.");
                return summoner;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<SummonerDto> getSummoners(List<Long> summonerIds){
        List<SummonerDto> summoners = new LinkedList<>();

        ListIterator<Long> itr = summonerIds.listIterator();
        while(itr.hasNext()){
            Long id = itr.next();
            SummonerDto summ = getSummonerFromId(id);
            if(summ != null){
                summoners.add(summ);
                itr.remove();
            }
        }

        return summoners;
    }

    @Override
    public SummonerDto getSummonerFromId(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate FROM summoners WHERE id = %d", summonerId);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel, revisionDate);
                log.info("Fetched summoner " + summoner + " from db.");
                return summoner;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    public boolean cacheSummoner(SummonerDto summoner){
        if(summoner == null)
            return false;

        try{
            String sql = "INSERT INTO summoners VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, summoner.getId());
            stmt.setString(2, summoner.getName());
            stmt.setInt(3, summoner.getProfileIconId());
            stmt.setLong(4, summoner.getSummonerLevel());
            stmt.setLong(5, summoner.getRevisionDate());

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache summoner " + summoner + " failed.");
                return false;
            }
            log.info("Cached summoner " + summoner);
            return true;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public List<MatchSummary> getRankedMatches(long summonerId, int numRecords){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT mapId, matchCreation, matchDuration, matchId, "
                    + "matchMode, matchType, matchVersion, participantIdentities, "
                    + "participants, platformId, queueType, region, season "
                    + "FROM ranked_matches WHERE summonerId = %d ORDER BY matchCreation DESC", summonerId);
            if(numRecords != FETCH_ALL)
                sql += " LIMIT " + numRecords;

            ResultSet rs = stmt.executeQuery(sql);

            List<MatchSummary> matches = new LinkedList<>();
            while(rs.next()){
                int mapId = rs.getInt("mapId");
                long matchCreation = rs.getLong("matchCreation");
                long matchDuration = rs.getLong("matchDuration");
                long matchId = rs.getLong("matchId");
                String matchMode = rs.getString("matchMode");
                String matchType = rs.getString("matchType");
                String matchVersion = rs.getString("matchVersion");
                List<ParticipantIdentity> participantIdentities = mapper.readValue(
                    rs.getString("participantIdentities"), new TypeReference<List<ParticipantIdentity>>(){
                    });
                List<Participant> participants = mapper.readValue(rs.getString("participants"),
                    new TypeReference<List<Participant>>(){
                    });
                String platformId = rs.getString("platformId");
                String queueType = rs.getString("queueType");
                String region = rs.getString("region");
                String season = rs.getString("season");

                MatchSummary match = new MatchSummary(mapId, matchCreation, matchDuration, matchId, matchMode,
                        matchType, matchVersion, participantIdentities, participants, platformId, queueType, region,
                        season);
                matches.add(match);

                log.info("Fetched ranked match " + match.getMatchId() + " for summoner " + summonerId + " from db.");
            }
            return matches;
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get one page of ranked results
     */
    @Override
    public List<MatchSummary> getRankedMatches(long summonerId){
        return getRankedMatches(summonerId, APIConstants.MAX_PAGE_SIZE);
    }

    @Override
    public List<MatchSummary> getAllRankedMatches(long summonerId){
        return getRankedMatches(summonerId, FETCH_ALL);
    }

    /**
     * Nothing to do here. See {@link RiotAPIImpl}
     */
    @Override
    @Deprecated
    public int cacheAllRankedMatches(long summonerId){
        return APIConstants.INVALID;
    }

    private boolean hasRankedMatch(long summonerId, MatchSummary match){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT COUNT(*) AS rowCount FROM ranked_matches "
                    + "WHERE summonerId = %d AND matchId = %d ORDER BY matchCreation DESC", summonerId,
                match.getMatchId());

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            int rows = rs.getInt("rowCount");
            return rows > 0;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean cacheRankedMatch(long summonerId, MatchSummary match){
        try{
            if(hasRankedMatch(summonerId, match)){
                return false;
            }

            String sql = "INSERT INTO ranked_matches VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, match.getMatchId());
            stmt.setLong(2, summonerId);
            stmt.setInt(3, match.getMapId());
            stmt.setLong(4, match.getMatchCreation());
            stmt.setLong(5, match.getMatchDuration());
            stmt.setString(6, match.getMatchMode());
            stmt.setString(7, match.getMatchType());
            stmt.setString(8, match.getMatchVersion());
            stmt.setString(9, mapper.writeValueAsString(match.getParticipantIdentities()));
            stmt.setString(10, mapper.writeValueAsString(match.getParticipants()));
            stmt.setString(11, match.getPlatformId());
            stmt.setString(12, match.getQueueType());
            stmt.setString(13, match.getRegion());
            stmt.setString(14, match.getSeason());

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache ranked match " + match.getMatchId() + " for summoner " + summonerId
                        + " failed.");
                return false;
            }
            log.info("Cached ranked match " + match.getMatchId() + " for summoner " + summonerId);
            return true;
        } catch(IOException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Set<GameDto> getMatchHistory(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT id, summonerId, createDate, gameData FROM match_history "
                    + "WHERE summonerId = %d ORDER BY createDate DESC LIMIT " + APIConstants.PAGE_SIZE, summonerId);
            ResultSet rs = stmt.executeQuery(sql);

            Set<GameDto> games = new HashSet<>();
            while(rs.next()){
                String gameData = rs.getString("gameData");

                GameDto game = mapper.readValue(gameData, GameDto.class);
                games.add(game);

                log.info("Fetched match " + game.getGameId() + " from match history for summoner " + summonerId
                        + " from db.");
            }
            return games;
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<GameDto> getMatchHistoryAll(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT id, summonerId, createDate, gameData FROM match_history WHERE summonerId = %d",
                summonerId);
            ResultSet rs = stmt.executeQuery(sql);

            Set<GameDto> games = new HashSet<>();
            while(rs.next()){
                String gameData = rs.getString("gameData");

                GameDto game = mapper.readValue(gameData, GameDto.class);
                games.add(game);

                log.info("Fetched match " + game.getGameId() + " from match history for summoner " + summonerId
                        + " from db.");
            }
            return games;
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public boolean cacheMatchHistoryMatch(long summonerId, GameDto game){
        try{
            long gameId = game.getGameId();
            long createDate = game.getCreateDate();
            String gameData = mapper.writeValueAsString(game);

            String sql = "INSERT INTO match_history VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, gameId);
            stmt.setLong(2, summonerId);
            stmt.setLong(3, createDate);
            stmt.setString(4, gameData);

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache match history match " + gameId + " for summoner " + summonerId
                        + " failed.");
                return false;
            }
            log.info("Cached match history match " + gameId + " for summoner " + summonerId);
            return true;
        } catch(IOException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ChampionDto getChampFromId(long champId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT id, name, title, champKey FROM champions WHERE id = %d", champId);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String title = rs.getString("title");
                String key = rs.getString("champKey");

                ChampionDto champ = new ChampionDto(id, name, title, key);
                log.info("Fetched champion " + champ + " from db.");
                return champ;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    public boolean cacheChampion(ChampionDto champ){
        try{
            long id = champ.getId();
            String name = champ.getName();
            String title = champ.getTitle();
            String key = champ.getKey();

            String sql = "INSERT INTO champions VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.setString(2, name);
            stmt.setString(3, title);
            stmt.setString(4, key);

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache champion " + champ + " failed.");
                return false;
            }
            log.info("Cached champion " + champ);
            return true;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long spellId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, description, spellKey, summonerLevel, image FROM summoner_spells WHERE id = %d",
                spellId);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String key = rs.getString("spellKey");
                int summonerLevel = rs.getInt("summonerLevel");
                ImageDto image = null;
                try{
                    image = mapper.readValue(rs.getString("image"), ImageDto.class);
                } catch(IOException e){
                    log.warning(e.getMessage());
                }

                SummonerSpellDto spell = new SummonerSpellDto(id, name, description, key, summonerLevel, image);
                log.info("Fetched summoner spell " + spell + " from db.");
                return spell;
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    public boolean cacheSummonerSpell(SummonerSpellDto spell){
        try{
            long id = spell.getId();
            String name = spell.getName();
            String description = spell.getDescription();
            String key = spell.getKey();
            int summonerLevel = spell.getSummonerLevel();
            String image;
            try{
                image = mapper.writeValueAsString(spell.getImage());
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }

            String sql = "INSERT INTO summoner_spells VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setString(4, key);
            stmt.setInt(5, summonerLevel);
            stmt.setString(6, image);

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache summoner spell " + spell + " failed.");
                return false;
            }
            log.info("Cached summoner spell " + spell);
            return true;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public MatchDetail getMatchDetail(long id){
        return null;
    }

    public static void main(String[] args){
        DatabaseAPIImpl r = new DatabaseAPIImpl();
        System.out.println(r.searchSummoner("Zedenstein"));
    }
}
