package league.entities;

import org.neo4j.graphdb.Node;

/**
 * NOTE: Lots of stuff missing
 */
public class SummonerSpellDto{
    private int id;
    private String name;
    private String description;
    private String key;
    private int summonerLevel;
    private ImageDto image;

    public SummonerSpellDto(){
        
    }
    
    public SummonerSpellDto(int id, String name, String description, String key, int summonerLevel, ImageDto image){
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
        this.summonerLevel = summonerLevel;
        this.image = image;
    }

    public SummonerSpellDto(Node node){
        setId((int)(long) node.getProperty("id"));
        setName((String) node.getProperty("name"));
        setDescription((String) node.getProperty("description"));
        setKey((String) node.getProperty("key"));
        setSummonerLevel((int)(long) node.getProperty("summonerLevel"));
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public int getSummonerLevel(){
        return summonerLevel;
    }

    public void setSummonerLevel(int summonerLevel){
        this.summonerLevel = summonerLevel;
    }

    public ImageDto getImage(){
        return image;
    }

    public void setImage(ImageDto image){
        this.image = image;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof SummonerSpellDto))
            return false;
        else return obj.hashCode() == this.hashCode();
    }
    
    @Override
    public int hashCode(){
        return id;
    }
    
    @Override
    public String toString(){
        return "Summoner spell " + name;
    }
}
