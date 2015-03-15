package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.azhu.Match;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;

public class GeneralAnalysis{

    public static Map<String, Collection<SummaryData>> getChampData(Collection<Match> matchList, long summonerId){
        Map<String, List<Match>> queueMap = AnalysisUtils.filterByCustomQueueType(matchList);
        Map<String, Collection<SummaryData>> dataMap = new HashMap<>();
        
        for(String queueType : queueMap.keySet()){
            Map<ChampionDto, SummaryData> champData = new HashMap<>();
            List<Match> filteredMatches = queueMap.get(queueType);
            Map<ChampionDto, List<Match>> champMatches = AnalysisUtils.getChampMatches(filteredMatches, summonerId);

            for(ChampionDto champ : champMatches.keySet()){
                List<Match> matches = champMatches.get(champ);
                SummaryData data = AnalysisUtils.getGeneralSummary(matches, summonerId);
                data.setChampion(champ);
                champData.put(champ, data);
            }
            
            dataMap.put(queueType, champData.values());
        }

        return dataMap;
    }

    public static void main(String[] args){
        try{
            Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
            Set<Match> matches = api.getAllGames(31569637);
            Map<String, Collection<SummaryData>> data = GeneralAnalysis.getChampData(matches, 31569637);
            System.out.println(data);
        } catch(RiotPlsException e){
            e.printStackTrace();
        }
    }
}
