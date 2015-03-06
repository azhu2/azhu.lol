package league.neo4j;

import league.entities.azhu.MatchPlayer;

public interface Neo4jLeagueDatabaseAPI extends Neo4jLeagueAPI{

    public void cacheMatchPlayer(MatchPlayer player);

}
