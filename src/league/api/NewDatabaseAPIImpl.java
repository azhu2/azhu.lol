package league.api;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.Team;
import league.entities.azhu.GeneralMatchImpl;
import league.entities.azhu.GeneralStatsImpl;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.RankedMatchImpl;
import league.entities.azhu.RankedPlayerImpl;
import league.entities.azhu.Summoner;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.type.TypeReference;

public class NewDatabaseAPIImpl extends DatabaseAPIImpl implements NewLeagueDatabaseAPI{
    private static NewDatabaseAPIImpl _instance = new NewDatabaseAPIImpl();
    private static RiotAPIImpl api_riot = RiotAPIImpl.getInstance();

    public static NewDatabaseAPIImpl getInstance(){
        return _instance;
    }

    private NewDatabaseAPIImpl(){
        super();
    }

    @Override
    public void cacheRankedMatch(Match match){
        try{
            if(hasRankedMatch(match) || !(match instanceof RankedMatchImpl))
                return;

            RankedMatchImpl rankedMatch = (RankedMatchImpl) match;
            String sql = "INSERT INTO ranked_matches_new"
                    + "(mapId, matchCreation, matchDuration, matchId, matchMode, matchType, "
                    + "matchVersion, platformId, queueType, region, season, teams, "
                    + "lookupPlayer, bluePlayers, redPlayers, blueBans, redBans, players, summonerId)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, rankedMatch.getMapId());
            stmt.setLong(2, rankedMatch.getMatchCreation());
            stmt.setLong(3, rankedMatch.getMatchDuration());
            stmt.setLong(4, rankedMatch.getId());
            stmt.setString(5, rankedMatch.getMatchMode());
            stmt.setString(6, rankedMatch.getMatchType());
            stmt.setString(7, rankedMatch.getMatchVersion());
            stmt.setString(8, rankedMatch.getPlatformId());
            stmt.setString(9, rankedMatch.getQueueType());
            stmt.setString(10, rankedMatch.getRegion());
            stmt.setString(11, rankedMatch.getSeason());
            stmt.setString(12, mapper.writeValueAsString(rankedMatch.getTeams()));
            stmt.setInt(13, rankedMatch.getLookupPlayer());
            stmt.setString(14, mapper.writeValueAsString(rankedMatch.getBluePlayers()));
            stmt.setString(15, mapper.writeValueAsString(rankedMatch.getRedPlayers()));
            stmt.setString(16, mapper.writeValueAsString(rankedMatch.getBlueBans()));
            stmt.setString(17, mapper.writeValueAsString(rankedMatch.getRedBans()));
            stmt.setString(18, mapper.writeValueAsString(rankedMatch.getPlayers()));
            stmt.setLong(19, rankedMatch.getSummonerId());

            new CacheThread(stmt, rankedMatch).start();
        } catch(IOException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public boolean hasRankedMatch(Match match){
        try{
            if(!(match instanceof RankedMatchImpl))
                return false;
            RankedMatchImpl rankedMatch = (RankedMatchImpl) match;
            
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT COUNT(*) AS rowCount FROM ranked_matches_new WHERE matchId = %d AND summonerId = %d",
                rankedMatch.getId(), rankedMatch.getQueryPlayer().getSummoner().getId());

            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();

            rs.next();
            int rows = rs.getInt("rowCount");
            return rows > 0;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Match getRankedMatch(long matchId, long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT mapId, matchCreation, matchDuration, matchId, matchMode, matchType, "
                    + "matchVersion, platformId, queueType, region, season, teams, "
                    + "lookupPlayer, bluePlayers, redPlayers, blueBans, redBans, players "
                    + "FROM ranked_matches_new WHERE matchId = %d AND summonerId = %d", matchId, summonerId);

            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            if(rs.next()){
                int mapId = rs.getInt("mapId");
                long matchCreation = rs.getLong("matchCreation");
                long matchDuration = rs.getLong("matchDuration");
                String matchMode = rs.getString("matchMode");
                String matchType = rs.getString("matchType");
                String matchVersion = rs.getString("matchVersion");
                String platformId = rs.getString("platformId");
                String queueType = rs.getString("queueType");
                String region = rs.getString("region");
                String season = rs.getString("season");
                List<Team> teams = mapper.readValue(rs.getString("teams"), new TypeReference<List<Team>>(){
                });
                int lookupPlayer = rs.getInt("lookupPlayer");
                List<Integer> bluePlayers = mapper.readValue(rs.getString("bluePlayers"),
                    new TypeReference<List<Integer>>(){
                    });
                List<Integer> redPlayers = mapper.readValue(rs.getString("redPlayers"),
                    new TypeReference<List<Integer>>(){
                    });
                List<ChampionDto> blueBans = mapper.readValue(rs.getString("blueBans"),
                    new TypeReference<List<ChampionDto>>(){
                    });
                List<ChampionDto> redBans = mapper.readValue(rs.getString("redBans"),
                    new TypeReference<List<ChampionDto>>(){
                    });
                List<MatchPlayer> players = mapper.readValue(rs.getString("players"),
                    new TypeReference<List<RankedPlayerImpl>>(){
                    });

                RankedMatchImpl match = new RankedMatchImpl(mapId, matchCreation, matchDuration, matchId, matchMode, matchType,
                        matchVersion, players, platformId, queueType, region, season, teams, lookupPlayer, bluePlayers,
                        redPlayers, blueBans, redBans, summonerId);
                log.info("Fetched (new) ranked match " + match + " from db in " + time + " ms.");
                return match;
            }
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Match> getRankedMatchesAll(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT matchId, mapId, matchCreation, matchDuration, matchId, matchMode, matchType, "
                    + "matchVersion, platformId, queueType, region, season, teams, "
                    + "lookupPlayer, bluePlayers, redPlayers, blueBans, redBans, players "
                    + "FROM ranked_matches_new WHERE summonerId = %d", summonerId);

            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            List<Match> matches = new LinkedList<>();
            while(rs.next()){
                long matchId = rs.getLong("matchId");
                int mapId = rs.getInt("mapId");
                long matchCreation = rs.getLong("matchCreation");
                long matchDuration = rs.getLong("matchDuration");
                String matchMode = rs.getString("matchMode");
                String matchType = rs.getString("matchType");
                String matchVersion = rs.getString("matchVersion");
                String platformId = rs.getString("platformId");
                String queueType = rs.getString("queueType");
                String region = rs.getString("region");
                String season = rs.getString("season");
                List<Team> teams = mapper.readValue(rs.getString("teams"), new TypeReference<List<Team>>(){
                });
                int lookupPlayer = rs.getInt("lookupPlayer");
                List<Integer> bluePlayers = mapper.readValue(rs.getString("bluePlayers"),
                    new TypeReference<List<Integer>>(){
                    });
                List<Integer> redPlayers = mapper.readValue(rs.getString("redPlayers"),
                    new TypeReference<List<Integer>>(){
                    });
                List<ChampionDto> blueBans = mapper.readValue(rs.getString("blueBans"),
                    new TypeReference<List<ChampionDto>>(){
                    });
                List<ChampionDto> redBans = mapper.readValue(rs.getString("redBans"),
                    new TypeReference<List<ChampionDto>>(){
                    });
                List<MatchPlayer> players = mapper.readValue(rs.getString("players"),
                    new TypeReference<List<RankedPlayerImpl>>(){
                    });

                RankedMatchImpl match = new RankedMatchImpl(mapId, matchCreation, matchDuration, matchId, matchMode, matchType,
                        matchVersion, players, platformId, queueType, region, season, teams, lookupPlayer, bluePlayers,
                        redPlayers, blueBans, redBans, summonerId);
                log.info("Fetched (new) ranked match " + match + " from db in " + time + " ms.");
                matches.add(match);
            }
            return matches;
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Match getGame(long gameId, long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT mapId, createDate, gameMode, gameType, subType, "
                    + "lookupPlayer, blueTeam, redTeam, ipEarned, level, stats, spell1, spell2, stats, "
                    + "teamId, summoner FROM games WHERE gameId = %d AND summonerId = %d", gameId, summonerId);

            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            if(rs.next()){
                int mapId = rs.getInt("mapId");
                long createDate = rs.getLong("createDate");
                String gameMode = rs.getString("gameMode");
                String gameType = rs.getString("gameType");
                String subType = rs.getString("subType");
                int lookupPlayer = rs.getInt("lookupPlayer");
                List<MatchPlayer> blueTeam = mapper.readValue(rs.getString("blueTeam"),
                    new TypeReference<List<MatchPlayer>>(){
                    });
                List<MatchPlayer> redTeam = mapper.readValue(rs.getString("redTeam"),
                    new TypeReference<List<MatchPlayer>>(){
                    });
                int ipEarned = rs.getInt("ipEarned");
                int level = rs.getInt("level");
                int teamId = rs.getInt("teamId");
                GeneralStatsImpl stats = mapper.readValue(rs.getString("stats"), GeneralStatsImpl.class);
                SummonerSpellDto spell1 = mapper.readValue(rs.getString("spell1"), SummonerSpellDto.class);
                SummonerSpellDto spell2 = mapper.readValue(rs.getString("spell2"), SummonerSpellDto.class);
                SummonerDto summoner = mapper.readValue(rs.getString("summoner"), SummonerDto.class);

                GeneralMatchImpl game = new GeneralMatchImpl(createDate, blueTeam, redTeam, gameId, gameMode, gameType, ipEarned, level, mapId,
                        spell1, spell2, stats, subType, teamId, summoner, lookupPlayer, summonerId);
                log.info("Fetched (new) game " + game + " from db in " + time + " ms.");
                return game;
            }
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Match> getGamesAll(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT gameId, mapId, createDate, gameMode, gameType, subType, "
                    + "lookupPlayer, blueTeam, redTeam, ipEarned, level, stats, spell1, spell2, stats, "
                    + "teamId, summoner FROM games WHERE summonerId = %d", summonerId);

            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            List<Match> games = new LinkedList<>();
            while(rs.next()){
                long gameId = rs.getLong("gameId");
                int mapId = rs.getInt("mapId");
                long createDate = rs.getLong("createDate");
                String gameMode = rs.getString("gameMode");
                String gameType = rs.getString("gameType");
                String subType = rs.getString("subType");
                int lookupPlayer = rs.getInt("lookupPlayer");
                List<MatchPlayer> blueTeam = mapper.readValue(rs.getString("blueTeam"),
                    new TypeReference<List<MatchPlayer>>(){
                    });
                List<MatchPlayer> redTeam = mapper.readValue(rs.getString("redTeam"),
                    new TypeReference<List<MatchPlayer>>(){
                    });
                int ipEarned = rs.getInt("ipEarned");
                int level = rs.getInt("level");
                int teamId = rs.getInt("teamId");
                GeneralStatsImpl stats = mapper.readValue(rs.getString("stats"), GeneralStatsImpl.class);
                SummonerSpellDto spell1 = mapper.readValue(rs.getString("spell1"), SummonerSpellDto.class);
                SummonerSpellDto spell2 = mapper.readValue(rs.getString("spell2"), SummonerSpellDto.class);
                SummonerDto summoner = mapper.readValue(rs.getString("summoner"), SummonerDto.class);

                GeneralMatchImpl game = new GeneralMatchImpl(createDate, blueTeam, redTeam, gameId, gameMode, gameType, ipEarned, level, mapId,
                        spell1, spell2, stats, subType, teamId, summoner, lookupPlayer, summonerId);
                log.info("Fetched (new) game " + game + " from db in " + time + " ms.");
                games.add(game);
            }
            return games;
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean hasGame(Match game){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT COUNT(*) AS rowCount FROM games WHERE gameId = %d", game.getId());

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            int rows = rs.getInt("rowCount");
            return rows > 0;
        } catch(SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void cacheGame(Match match){
        try{
            if(hasGame(match) || !(match instanceof GeneralMatchImpl))
                return;            
            GeneralMatchImpl game = (GeneralMatchImpl) match;

            String sql = "INSERT INTO games (mapId, createDate, gameMode, gameType, subType, "
                    + "lookupPlayer, blueTeam, redTeam, ipEarned, level, stats, spell1, spell2, "
                    + "teamId, summoner, gameId, summonerId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, game.getMapId());
            stmt.setLong(2, game.getMatchCreation());
            stmt.setString(3, game.getMatchMode());
            stmt.setString(4, game.getMatchType());
            stmt.setString(5, game.getQueueType());
            stmt.setInt(6, game.getLookupPlayer());
            stmt.setString(7, mapper.writeValueAsString(game.getBlueTeam()));
            stmt.setString(8, mapper.writeValueAsString(game.getRedTeam()));
            stmt.setInt(9, game.getIpEarned());
            stmt.setInt(10, game.getLevel());
            stmt.setString(11, mapper.writeValueAsString(game.getStats()));
            stmt.setString(12, mapper.writeValueAsString(game.getSpell1()));
            stmt.setString(13, mapper.writeValueAsString(game.getSpell2()));
            stmt.setInt(14, game.getTeamId());
            stmt.setString(15, mapper.writeValueAsString(game.getSummoner()));
            stmt.setLong(16, game.getId());
            stmt.setLong(17, game.getSummonerId());

            new CacheThread(stmt, game).start();
        } catch(IOException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public Summoner searchSummonerNew(String summonerName){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate, league FROM summoners WHERE name = '%s'",
                summonerName);
            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");
                League league = null;
                if(rs.getString("league") != null)
                    league = mapper.readValue(rs.getString("league"), League.class);

                Summoner summoner = new Summoner(id, name, profileIconId, summonerLevel, revisionDate, league);
                log.info("Fetched summoner " + summoner + " from db in " + time + " ms");
                return summoner;
            }
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds) throws RiotPlsException{
        return getSummonersNew(summonerIds, true);
    }

    @Override
    public List<Summoner> getSummonersNew(List<Long> summonerIds, boolean cache) throws RiotPlsException{
        List<Summoner> dbResults = new LinkedList<>();
        List<Long> idsCopy = new LinkedList<>(summonerIds);

        try{
            List<League> leagues = null;
            if(cache)
                leagues = api_riot.getLeagues(summonerIds);
            ListIterator<Long> itr = summonerIds.listIterator();
            int index = 0;
            while(itr.hasNext()){
                Long id = itr.next();
                Summoner summoner = getSummonerNewFromId(id);
                if(summoner != null){
                    if(leagues != null && !leagues.isEmpty())
                        summoner.setLeague(leagues.get(index));
                    dbResults.add(summoner);
                    itr.remove();
                }
                index++;
            }
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }

        if(summonerIds.isEmpty())
            return dbResults;

        List<SummonerDto> riotResults = api_riot.getSummoners(summonerIds);
        List<League> leagueResults = null;
        try{
            leagueResults = api_riot.getLeagues(summonerIds);
        } catch(RiotPlsException e){
            log.warning(e.getMessage());
        }
        List<Summoner> riotSummoners = new LinkedList<>();
        for(int i = 0; i < riotResults.size(); i++){
            if(leagueResults != null && !leagueResults.isEmpty())
                riotSummoners.add(new Summoner(riotResults.get(i), leagueResults.get(i)));
            else
                riotSummoners.add(new Summoner(riotResults.get(i), new League()));
        }

        List<Summoner> results = new LinkedList<>();
        for(long id : idsCopy){
            Summoner summoner = getSummonerFromList(dbResults, id);
            if(summoner == null){
                summoner = getSummonerFromList(riotSummoners, id);
                cacheSummoner(summoner);
            }

            results.add(summoner);
        }
        return results;
    }

    private Summoner getSummonerFromList(List<Summoner> list, long id){
        for(Summoner summoner : list)
            if(summoner.getId() == id)
                return summoner;
        return null;
    }

    @Override
    public Summoner getSummonerNewFromId(long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format(
                "SELECT id, name, profileIconId, summonerLevel, revisionDate, league FROM summoners WHERE id = %d",
                summonerId);
            Pair<ResultSet, Long> results = runQuery(stmt, sql);
            ResultSet rs = results.getLeft();
            long time = results.getRight();

            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");
                League league = null;
                if(rs.getString("league") != null)
                    league = mapper.readValue(rs.getString("league"), League.class);

                Summoner summoner = new Summoner(id, name, profileIconId, summonerLevel, revisionDate, league);
                log.info("Fetched summoner " + summoner + " from db in " + time + " ms.");
                return summoner;
            }
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void cacheSummoner(Summoner summoner){
        if(summoner == null)
            return;

        try{
            String sql = "INSERT INTO summoners (id, name, profileIconId, summonerLevel, revisionDate, league)"
                    + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE profileIconId=VALUES(profileIconId), "
                    + "summonerLevel=VALUES(summonerLevel), revisionDate=VALUES(revisionDate), name=VALUES(name), league=VALUES(league)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setLong(1, summoner.getId());
            stmt.setString(2, summoner.getName());
            stmt.setInt(3, summoner.getProfileIconId());
            stmt.setLong(4, summoner.getSummonerLevel());
            stmt.setLong(5, summoner.getRevisionDate());
            stmt.setString(6, mapper.writeValueAsString(summoner.getLeague()));

            new CacheThread(stmt, summoner).start();
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void cacheRankedMatches(List<Match> matches){
        for(Match match : matches)
            cacheRankedMatch(match);
    }
}
