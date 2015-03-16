package league.api;

import league.entities.ImageDto;
import league.entities.ItemDto;
import league.entities.SummonerSpellDto;

public class APIConstants{
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_RATELIMIT = 429;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_UNAVAILABLE = 503;
    public static final int MAX_PAGE_SIZE = 15;
    public static final int PAGE_SIZE = 10;
    public static final int RANKED_PAGE_SIZE = 10;
    public static final int LEAGUES_PAGE_SIZE = 10;
    public static final int INVALID = -1;

    public static final ItemDto DUMMY_ITEM = new ItemDto("No item", 0, null, "none", "none");
    public static final ImageDto REMOVED_IMAGE = new ImageDto("item_removed.png");
    public static final SummonerSpellDto DUMMY_SUMMONER_SPELL = new SummonerSpellDto(0, "No spell", "removed", "none",
            31, REMOVED_IMAGE);

}
