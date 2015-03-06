package league.entities.azhu;

import league.entities.ChampionDto;

public class GeneralPlayerImpl extends MatchPlayer{

    public GeneralPlayerImpl(){

    }

    public GeneralPlayerImpl(ChampionDto champion, Summoner summoner, int teamId){
        super();
        setChampion(champion);
        setSummoner(summoner);
        setTeamId(teamId);
    }

    @Override
    public String toString(){
        return "Player " + getSummoner() + " (" + getChampion() + ")";
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSummoner() == null) ? 0 : getSummoner().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        GeneralPlayerImpl other = (GeneralPlayerImpl) obj;
        if(getSummoner() == null){
            if(other.getSummoner() != null)
                return false;
        } else if(!getSummoner().equals(other.getSummoner()))
            return false;
        return true;
    }
}
