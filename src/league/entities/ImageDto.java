package league.entities;

public class ImageDto{
    private int w;
    private int h;
    private String full;
    private String group;
    private String sprite;

    public ImageDto(){
        
    }
    
    public int getW(){
        return w;
    }

    public void setW(int w){
        this.w = w;
    }

    public int getH(){
        return h;
    }

    public void setH(int h){
        this.h = h;
    }

    public String getFull(){
        return full;
    }

    public void setFull(String full){
        this.full = full;
    }

    public String getSprite(){
        return sprite;
    }

    public void setSprite(String sprite){
        this.sprite = sprite;
    }

    public String getGroup(){
        return group;
    }

    public void setGroup(String group){
        this.group = group;
    }

}
