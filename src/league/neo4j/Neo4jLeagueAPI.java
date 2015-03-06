package league.neo4j;

import league.entities.azhu.MatchPlayer;

public interface Neo4jLeagueAPI{

    public MatchPlayer getMatchPlayer(long matchId, long summonerId);

}
