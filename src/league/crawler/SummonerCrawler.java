package league.crawler;

import java.util.LinkedList;
import java.util.List;

import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.azhu.Summoner;

public class SummonerCrawler{
    private static final long REQUEST_SIZE = 40;
    private static final long START_ID = 224080;

    public static void main(String[] args){
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
