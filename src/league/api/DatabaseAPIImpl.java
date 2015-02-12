package league.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;

public class DatabaseAPIImpl implements LeagueAPI{
    private static Logger log = Logger.getLogger(DatabaseAPIImpl.class.getName());

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/lol";
    private static final String USER = SecurityConstants.DB_USER;
    private static final String PASS = SecurityConstants.DB_PASS;
    private Connection db;

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
    }

    @Override
    public SummonerDto searchSummoner(String summonerName){
        summonerName = summonerName.toLowerCase().replace(" ", "");

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

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel,
                        revisionDate);
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
                "SELECT id, name, profileIconId, summonerLevel, revisionDate FROM summoners WHERE id = %d",
                summonerId);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int profileIconId = rs.getInt("profileIconId");
                long summonerLevel = rs.getLong("summonerLevel");
                long revisionDate = rs.getLong("revisionDate");

                SummonerDto summoner = new SummonerDto(id, name, profileIconId, summonerLevel,
                        revisionDate);
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
            Statement stmt = db.createStatement();
            String sql;
            sql = String.format("INSERT INTO summoners VALUES (%d, '%s', %d, %d, %d)",
                summoner.getId(), summoner.getName(), summoner.getProfileIconId(),
                summoner.getSummonerLevel(), summoner.getRevisionDate());
            int updated = stmt.executeUpdate(sql);
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

    @Override
    public List<MatchSummary> getRankedMatches(long summonerId){
        return null;
    }

    @Override
    public Set<GameDto> getMatchHistory(long summonerId){
        return null;
    }

    @Override
    public ChampionDto getChampFromId(long id){
        return null;
    }

    @Override
    public SummonerSpellDto getSummonerSpellFromId(long id){
        return null;
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
