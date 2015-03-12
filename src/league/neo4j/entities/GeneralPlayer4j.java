package league.neo4j.entities;

import league.entities.ChampionDto;
import league.entities.azhu.MatchPlayer;
import league.entities.azhu.Summoner;

import org.codehaus.jackson.map.annotate.JsonView;
import org.neo4j.graphdb.Node;

public class GeneralPlayer4j extends MatchPlayer{
    public GeneralPlayer4j(){
        super();
    }

    public GeneralPlayer4j(Summoner summoner, ChampionDto champion, int teamId){
        setSummoner(summoner);
        setChampion(champion);
        setTeamId(teamId);
    }

    public GeneralPlayer4j(Node node){
        super(node);
    }

    @Override
    @JsonView(Views.RestView.class)
    public ChampionDto getChampion(){
        return super.getChampion();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setChampion(ChampionDto champion){
        super.setChampion(champion);
    }

    @Override
    @JsonView(Views.RestView.class)
    public Summoner getSummoner(){
        return super.getSummoner();
    }

    @Override
    @JsonView(Views.RestView.class)
    public void setSummoner(Summoner summoner){
        super.setSummoner(summoner);
    }
}
