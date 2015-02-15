package league.entities;

public class ChampionDto{
    private int id;
    // private ImageDto image;
    private String name;
    private String title;
    private String key;

    public ChampionDto(){
        
    }
    
    public ChampionDto(int id, String name, String title, String key){
        this.id = id;
        this.name = name;
        this.title = title;
        this.key = key;
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

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof ChampionDto))
            return false;
        ChampionDto champ = (ChampionDto) obj;
        return champ.id == this.id;
    }
    
    @Override
    public String toString(){
        return "Champion " + id + ": " + name;
    }
}
