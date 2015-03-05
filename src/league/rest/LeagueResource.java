package league.rest;

import java.io.IOException;
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

import league.api.APIConstants;
import league.api.DynamicLeagueAPIImpl;
import league.api.LeagueAPI;
import league.api.RiotAPIImpl.RiotPlsException;
import league.entities.ChampionDto;
import league.entities.GameDto;
import league.entities.ItemDto;
import league.entities.MatchDetail;
import league.entities.MatchSummary;
import league.entities.SummonerDto;
import league.entities.SummonerSpellDto;
import league.entities.azhu.League;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Path("/")
public class LeagueResource{
    private static LeagueAPI api = DynamicLeagueAPIImpl.getInstance();
    protected static ObjectMapper mapper = new ObjectMapper();

    public LeagueResource(){
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

    @GET
    @Path("/summoner/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoner(@PathParam("name") String name) throws ServletException, IOException{
        try{
            SummonerDto summoner = api.searchSummoner(name);
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
            SummonerDto summoner = api.getSummonerFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoner)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/summoners/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummoners(@PathParam("ids") String ids) throws ServletException, IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : ids.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<SummonerDto> summoners = api.getSummoners(idList);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(summoners)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    /**
     * @deprecated in favor of {@link NewLeagueResource}
     */
    @GET
    @Path("/ranked-matches/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankedMatches(@PathParam("id") long id) throws ServletException, IOException{
        try{
            List<MatchSummary> history = api.getRankedMatches(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    /**
     * @deprecated in favor of {@link NewLeagueResource}
     */
    @GET
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRankedMatches(@PathParam("id") long id) throws ServletException, IOException{
        try{
            List<MatchSummary> history = api.getAllRankedMatches(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    /**
     * @deprecated in favor of {@link NewLeagueResource}
     */
    @POST
    @Path("/ranked-matches/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheAllRankedMatches(@PathParam("id") long id) throws ServletException, IOException{
        try{
            int count = api.cacheAllRankedMatches(id);
            UpdateCount countObj = new UpdateCount();
            countObj.count = count;
            if(count != APIConstants.INVALID)
                return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(countObj)).build();
            else
                return Response.status(APIConstants.HTTP_INTERNAL_SERVER_ERROR).entity("Error caching all ranked matches").build();
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

    /**
     * @deprecated in favor of {@link NewLeagueResource}
     */
    @GET
    @Path("/match-history/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistory(@PathParam("id") long id) throws ServletException, IOException{
        try{
            Set<GameDto> history = api.getMatchHistory(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    /**
     * @deprecated in favor of {@link NewLeagueResource}
     */
    @GET
    @Path("/match-history/{id}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchHistoryAll(@PathParam("id") long id) throws ServletException, IOException{
        try{
            Set<GameDto> history = api.getMatchHistoryAll(id);
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
            ChampionDto champion = api.getChampFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(champion)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
   
    @GET
    @Path("/item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@PathParam("id") long id) throws ServletException, IOException{
        try{
            ItemDto item = api.getItemFromId(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(item)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/match/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchDetail(@PathParam("id") long id) throws ServletException, IOException{
        try{
            MatchDetail history = api.getMatchDetail(id);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(history)).build();
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
    public Response getLeagues(@PathParam("ids") String ids) throws ServletException, IOException{
        try{
            List<Long> idList = new LinkedList<>();
            for(String id : ids.split(","))
                idList.add(Long.parseLong(id.trim()));

            List<League> leagues = api.getLeagues(idList);
            return Response.status(APIConstants.HTTP_OK).entity(mapper.writeValueAsString(leagues)).build();
        } catch(RiotPlsException e){
            return Response.status(e.getStatus()).entity(e.getMessage()).build();
        }
    }
}
