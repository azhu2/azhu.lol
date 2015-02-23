package league.entities;

import java.util.List;

public class LeagueDto{
    private List<LeagueEntryDto> entries;
    private String name;
    private String participantId;
    private String queue;
    private String tier;
    
    public LeagueDto(){
        
    }

    public LeagueDto(List<LeagueEntryDto> entries, String name, String participantId, String queue, String tier){
        super();
        this.entries = entries;
        this.name = name;
        this.participantId = participantId;
        this.queue = queue;
        this.tier = tier;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((participantId == null) ? 0 : participantId.hashCode());
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
        LeagueDto other = (LeagueDto) obj;
        if(participantId == null){
            if(other.participantId != null)
                return false;
        } else if(!participantId.equals(other.participantId))
            return false;
        return true;
    }

    @Override
    public String toString(){
        return "LeagueDto [queue=" + queue + ", tier=" + tier + "]";
    }

    public List<LeagueEntryDto> getEntries(){
        return entries;
    }

    public void setEntries(List<LeagueEntryDto> entries){
        this.entries = entries;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getParticipantId(){
        return participantId;
    }

    public void setParticipantId(String participantId){
        this.participantId = participantId;
    }

    public String getQueue(){
        return queue;
    }

    public void setQueue(String queue){
        this.queue = queue;
    }

    public String getTier(){
        return tier;
    }

    public void setTier(String tier){
        this.tier = tier;
    }
    
    
}
