package league.entities;

import java.util.Set;

public class RecentGamesDto{
    private Set<GameDto> games;
    private long summonerId;

    public Set<GameDto> getGames(){
        return games;
    }

    public void setGames(Set<GameDto> games){
        this.games = games;
    }

    public long getSummonerId(){
        return summonerId;
    }

    public void setSummonerId(long summonerId){
        this.summonerId = summonerId;
    }
}
