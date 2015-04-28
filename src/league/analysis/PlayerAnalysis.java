package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import league.api.RiotPlsException;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;

public class PlayerAnalysis{
    private static Logger log = Logger.getLogger(PlayerAnalysis.class.getName());
    
    public static Map<String, Collection<SummaryData>> getPlayerData(Collection<Match> matchList, long summonerId){
        Map<String, List<Match>> queueMap = AnalysisUtils.filterByQueueType(matchList);
        Map<String, Collection<SummaryData>> dataMap = new HashMap<>();
        
        for(String queueType : queueMap.keySet()){
            Map<Summoner, SummaryData> playerData = new HashMap<>();
            List<Match> filteredMatches = queueMap.get(queueType);
            Map<Summoner, List<Match>> playerMatches = AnalysisUtils.getPlayerMatches(filteredMatches, summonerId);

            for(Summoner summoner : playerMatches.keySet()){
                List<Match> matches = playerMatches.get(summoner);
                SummaryData data = AnalysisUtils.getPlayerSummary(matches, summonerId);
                data.setCategory(summoner);
                playerData.put(summoner, data);
            }
            
            dataMap.put(queueType, playerData.values());
        }
        log.info("Player stats generated for " + summonerId);
        
        return dataMap;
    }

    public static void main(String[] args){
        try{
            Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
            Set<Match> matches = api.getAllGames(31569637);
            Map<String, Collection<SummaryData>> data = PlayerAnalysis.getPlayerData(matches, 31569637);
            System.out.println(data);
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }

}
