package league.entities;

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

}
