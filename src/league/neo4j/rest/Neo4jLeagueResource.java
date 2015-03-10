package league.neo4j.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import league.analysis.RankedAnalysis;
import league.analysis.SummaryData;
import league.api.APIConstants;
import league.api.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;
import league.entities.azhu.Match;
import league.entities.azhu.Summoner;
import league.neo4j.api.Neo4jAPI;
import league.neo4j.api.Neo4jDynamicAPIImpl;
import league.neo4j.entities.Views;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Path("/")
public class Neo4jLeagueResource{
    private static final Neo4jAPI api = Neo4jDynamicAPIImpl.getInstance();
    protected static ObjectMapper mapper = new ObjectMapper();

    public Neo4jLeagueResource(){
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.getSerializationConfig().setSerializationView(Views.RestView.class);
    }

    @GET
    @Path("/summoner/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoner(@PathParam("name") String name) throws ServletException, IOException{
        try{
            Summoner summoner = api.searchSummoner(name);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoner/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerById(@PathParam("id") long id) throws ServletException, IOException{
        try{
            Summoner summoner = api.getSummonerFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoners/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoners(@PathParam("ids") String summonerIds) throws ServletException, IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : summonerIds.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<Summoner> summoners = api.getSummoners(idList);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoners)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatch(@PathParam("id") long matchId) throws ServletException, IOException{
        try{
            Match match = api.getRankedMatch(matchId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(match)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            List<Match> history = api.getRankedMatches(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            List<Match> history = api.getAllRankedMatches(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheAllRankedMatches(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            int count = api.cacheAllRankedMatches(summonerId);
            UpdateCount countObj = new UpdateCount();
            countObj.count = count;
            if(count != APIConstants.INVALID)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(countObj)).build();
            else
                return Response.status(APIConstants.HTTP_INTERNAL_SERVER_ERROR)
                               .entity("Error caching all ranked matches").build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    protected class UpdateCount{
        int count;

        public int getCount(){
            return count;
        }
    }

    @GET
    @Path("/match-history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistory(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            Set<Match> history = api.getMatchHistory(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/match-history/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistoryAll(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            Set<Match> history = api.getAllGames(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/champion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChampFromId(@PathParam("id") long id) throws ServletException, IOException{
        try{
            ChampionDto champion = api.getChampionFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(champion)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemFromId(@PathParam("id") long id) throws ServletException, IOException{
        try{
            ItemDto item = api.getItemFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(item)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoner-spell/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummonerSpellFromId(@PathParam("id") long id) throws ServletException, IOException{
        try{
            SummonerSpellDto history = api.getSummonerSpellFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/league/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLeague(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            League league = api.getLeague(summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(league)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/leagues/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLeagues(@PathParam("ids") String summonerIds) throws ServletException, IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : summonerIds.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<League> leagues = api.getLeagues(idList);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(leagues)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ranked-stats/champions/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedStatsByChampion(@PathParam("id") long summonerId) throws ServletException, IOException{
        try{
            List<Match> matches = api.getAllRankedMatches(summonerId);
            Collection<SummaryData> champData = RankedAnalysis.getChampData(matches, summonerId);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(champData)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
}
