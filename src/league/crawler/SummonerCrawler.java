package league.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.GameDto;
import league.entities.PlayerDto;
import league.entities.azhu.RankedMatch;
import league.entities.azhu.RankedPlayer;
import league.entities.azhu.Summoner;

public class SummonerCrawler{
    private static final long REQUEST_SIZE = 40;
    private static final long START_ID = 0;
    private static final long SUMMONER_SEARCH_COUNT = 10000;
    private static final long SEED_SUMMONER_ID = 31569637;
    private static Logger log = Logger.getLogger(SummonerCrawler.class.getName());

    public static void main(String[] args){
        crawlByMatches();
    }

    public static void crawlByMatches(){
        Queue<Long> summoners = new LinkedList<>();
        Set<Long> matchesSeen = new HashSet<>();
        Set<Long> summonersSeen = new HashSet<>();
        NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
        LeagueAPI api_dynamic = DynamicLeagueAPIImpl.getInstance();
        api.setInifiteRetry(true);

        summoners.add(SEED_SUMMONER_ID);
        while(summoners.size() < SUMMONER_SEARCH_COUNT){
            try{
                long summonerId = summoners.remove();

                
                List<RankedMatch> rankedHistory = api.getRankedMatchesAll(summonerId);                
                if(rankedHistory != null){
                    for(RankedMatch match : rankedHistory){
                        long gameId = match.getMatchId();
                        if(!matchesSeen.contains(gameId)){
                            matchesSeen.add(gameId);
                            List<RankedPlayer> players = match.getPlayers();
                            List<Long> ids = new LinkedList<>();

                            if(players == null)
                                continue;
                            for(RankedPlayer player : players){
                                long playerId = player.getSummoner().getId();
                                if(!summonersSeen.contains(playerId)){
                                    summonersSeen.add(playerId);
                                    summoners.add(playerId);
                                    ids.add(playerId);
                                    log.info(playerId + " added (" + summonersSeen.size() + ")");
                                } else
                                    log.info(playerId + " already seen");
                            }
                            api.getSummonersNew(ids);
                        }
                    }
                }
                
                Set<GameDto> history = api_dynamic.getMatchHistoryAll(summonerId);
                if(history != null){
                    for(GameDto game : history){
                        long gameId = game.getGameId();
                        if(!matchesSeen.contains(gameId)){
                            matchesSeen.add(gameId);
                            List<PlayerDto> players = game.getFellowPlayers();
                            List<Long> ids = new LinkedList<>();

                            if(players == null)
                                continue;
                            for(PlayerDto player : players){
                                long playerId = player.getSummonerId();
                                if(!summonersSeen.contains(playerId)){
                                    summonersSeen.add(playerId);
                                    summoners.add(playerId);
                                    ids.add(playerId);
                                    log.info(playerId + " added (" + summonersSeen.size() + ")");
                                } else
                                    log.info(playerId + " already seen");
                            }
                            api.getSummonersNew(ids);
                        }
                    }
                }
            } catch(RiotPlsException e){
                e.printStackTrace();
            }
        }
    }

    public static void crawlBySummonerId(){
        NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
        api.setInifiteRetry(true);
        long start = START_ID;

        List<Summoner> summoners = null;
        do{
            List<Long> ids = new LinkedList<>();
            for(long i = 0; i < REQUEST_SIZE; i++){
                ids.add(start);
                start++;
            }

            try{
                summoners = api.getSummonersNew(ids);
            } catch(RiotPlsException e){
                e.printStackTrace();
            }
        } while(summoners != null && !summoners.isEmpty());
    }
}
