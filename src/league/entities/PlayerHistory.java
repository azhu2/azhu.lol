package league.entities;

import java.util.List;

public class PlayerHistory{
    private List<MatchSummary> matches;

    public List<MatchSummary> getMatches(){
        return matches;
    }

    public void setMatches(List<MatchSummary> matches){
        this.matches = matches;
    }

    public String toString(){
        StringBuilder history = new StringBuilder("Match History [");
        
        for(MatchSummary match : matches)
            history.append(match.getMatchId() + ",");
        
        history.append("]");
        return history.toString();
    }
}
