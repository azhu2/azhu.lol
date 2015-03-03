package league.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import league.LeagueConstants;
import league.api.NewDatabaseAPIImpl;
import league.api.NewLeagueAPI;
import league.entities.ChampionDto;
import league.entities.azhu.RankedMatch;

public class RankedAnalysis{
    public static Collection<SummaryData> getChampData(Collection<RankedMatch> matchList){
        List<RankedMatch> queueFiltered = AnalysisUtils.filterByQueueType(matchList).get(LeagueConstants.SOLO_QUEUE_5v5);
        Map<ChampionDto, SummaryData> champData = new HashMap<>();
        Map<ChampionDto, List<RankedMatch>> champMatches = AnalysisUtils.getChampMatches(queueFiltered);
        
        for(ChampionDto champ : champMatches.keySet()){
            List<RankedMatch> matches = champMatches.get(champ);
            SummaryData data = AnalysisUtils.getSummary(matches);
            data.setChampion(champ);
            champData.put(champ, data);
        }
        
        return champData.values();
    }
    
    public static void main(String[] args){
        NewLeagueAPI api = NewDatabaseAPIImpl.getInstance();
        List<RankedMatch> matches = api.getRankedMatchesAll(31569637);
        Collection<SummaryData> data = RankedAnalysis.getChampData(matches);
        System.out.println(data);
    }
}
