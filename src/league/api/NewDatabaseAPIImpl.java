package league.api;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import league.entities.ChampionDto;
import league.entities.Team;
import league.entities.azhu.RankedMatch;
import league.entities.azhu.RankedPlayer;

import org.codehaus.jackson.type.TypeReference;

public class NewDatabaseAPIImpl extends DatabaseAPIImpl implements NewLeagueAPI{
    private static NewDatabaseAPIImpl _instance = new NewDatabaseAPIImpl();

    public static NewDatabaseAPIImpl getInstance(){
        return _instance;
    }

    private NewDatabaseAPIImpl(){
        super();
    }

    @Override
    public boolean cacheRankedMatch(RankedMatch match){
        try{
            if(hasMatch(match))
                return false;

            String sql = "INSERT INTO ranked_matches_new"
                    + "(mapId, matchCreation, matchDuration, matchId, matchMode, matchType, "
                    + "matchVersion, platformId, queueType, region, season, teams, "
                    + "lookupPlayer, bluePlayers, redPlayers, blueBans, redBans, players, summonerId)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, match.getMapId());
            stmt.setLong(2, match.getMatchCreation());
            stmt.setLong(3, match.getMatchDuration());
            stmt.setLong(4, match.getMatchId());
            stmt.setString(5, match.getMatchMode());
            stmt.setString(6, match.getMatchType());
            stmt.setString(7, match.getMatchVersion());
            stmt.setString(8, match.getPlatformId());
            stmt.setString(9, match.getQueueType());
            stmt.setString(10, match.getRegion());
            stmt.setString(11, match.getSeason());
            stmt.setString(12, mapper.writeValueAsString(match.getTeams()));
            stmt.setInt(13, match.getLookupPlayer());
            stmt.setString(14, mapper.writeValueAsString(match.getBluePlayers()));
            stmt.setString(15, mapper.writeValueAsString(match.getRedPlayers()));
            stmt.setString(16, mapper.writeValueAsString(match.getBlueBans()));
            stmt.setString(17, mapper.writeValueAsString(match.getRedBans()));
            stmt.setString(18, mapper.writeValueAsString(match.getPlayers()));
            stmt.setLong(19, match.getSummonerId());

            int updated = stmt.executeUpdate();
            if(updated < 1){
                log.log(Level.WARNING, "Cache (new) ranked match " + match.getMatchId() + " failed.");
                return false;
            }
            log.info("Cached (new) ranked match " + match.getMatchId());
            return true;
        } catch(IOException | SQLException e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    private boolean hasMatch(RankedMatch match){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT COUNT(*) AS rowCount FROM ranked_matches_new WHERE " + "matchId = %d",
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

    @Override
    public RankedMatch getRankedMatch(long matchId, long summonerId){
        try{
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("SELECT mapId, matchCreation, matchDuration, matchId, matchMode, matchType, "
                    + "matchVersion, platformId, queueType, region, season, teams, "
                    + "lookupPlayer, bluePlayers, redPlayers, blueBans, redBans, players "
                    + "FROM ranked_matches_new WHERE matchId = %d AND summonerId = %d", matchId, summonerId);

            ResultSet rs = stmt.executeQuery(sql);

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
                List<RankedPlayer> players = mapper.readValue(rs.getString("players"),
                    new TypeReference<List<RankedPlayer>>(){
                    });

                RankedMatch match = new RankedMatch(mapId, matchCreation, matchDuration, matchId, matchMode, matchType,
                        matchVersion, players, platformId, queueType, region, season, teams, lookupPlayer, bluePlayers,
                        redPlayers, blueBans, redBans, summonerId);
                log.info("Fetched (new) ranked match " + match + " from db.");
                return match;
            }
        } catch(SQLException | IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
