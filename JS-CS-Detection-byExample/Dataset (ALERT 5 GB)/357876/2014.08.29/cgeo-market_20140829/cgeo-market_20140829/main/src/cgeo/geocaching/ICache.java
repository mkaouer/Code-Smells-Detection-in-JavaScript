/**
 *
 */
package cgeo.geocaching;

import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LogType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Basic interface for caches
 */
public interface ICache extends ILogable, ICoordinates {

    /**
     * @return Displayed owner, might differ from the real owner
     */
    public String getOwnerDisplayName();

    /**
     * @return GC username of the (actual) owner, might differ from the owner. Never empty.
     */
    public String getOwnerUserId();

    /**
     * @return true if the user is the owner of the cache, false else
     */
    public boolean isOwner();

    /**
     * @return true is the cache is archived, false else
     */
    public boolean isArchived();

    /**
     * @return true is the cache is a Premium Member cache only, false else
     */
    public boolean isPremiumMembersOnly();

    /**
     * @return Decrypted hint
     */
    public String getHint();

    /**
     * @return Description
     */
    public String getDescription();

    /**
     * @return Short Description
     */
    public String getShortDescription();


    /**
     * @return Id
     */
    public String getCacheId();

    /**
     * @return Guid
     */
    public String getGuid();

    /**
     * @return Location
     */
    public String getLocation();

    /**
     * @return Personal note
     */
    public String getPersonalNote();


    /**
     * @return true if the user gave a favorite point to the cache
     *
     */
    public boolean isFavorite();

    /**
     * @return number of favorite points
     *
     */
    public int getFavoritePoints();

    /**
     * @return true if the cache is on the watchlist of the user
     *
     */
    public boolean isOnWatchlist();

    /**
     * @return The date the cache has been hidden
     *
     */
    public Date getHiddenDate();

    /**
     * null safe list of attributes
     * 
     * @return the list of attributes for this cache
     */
    public List<String> getAttributes();

    /**
     * @return the list of trackables in this cache
     */
    public List<Trackable> getInventory();

    /**
     * @return the list of spoiler images
     */
    public List<Image> getSpoilers();

    /**
     * @return a statistic how often the caches has been found, disabled, archived etc.
     */
    public Map<LogType, Integer> getLogCounts();

    /**
     * get the name for lexicographical sorting.
     *
     * @return normalized, cached name which sort also correct for numerical parts in the name
     */
    public String getNameForSorting();

    /**
     * @return Tradi, multi etc.
     */
    CacheType getType();

    /**
     * @return Micro, small etc.
     */
    CacheSize getSize();

    /**
     * @return true if the user already found the cache
     *
     */
    boolean isFound();

    /**
     * @return true if the cache is disabled, false else
     */
    boolean isDisabled();

    /**
     * @return Difficulty assessment
     */
    float getDifficulty();

    /**
     * @return Terrain assessment
     */
    float getTerrain();
}
