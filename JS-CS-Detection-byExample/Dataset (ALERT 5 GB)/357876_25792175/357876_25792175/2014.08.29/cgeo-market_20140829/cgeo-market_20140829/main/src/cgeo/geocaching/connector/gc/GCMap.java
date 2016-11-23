package cgeo.geocaching.connector.gc;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.DataStore;
import cgeo.geocaching.Geocache;
import cgeo.geocaching.SearchResult;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LiveMapStrategy.Strategy;
import cgeo.geocaching.enumerations.LiveMapStrategy.StrategyFlag;
import cgeo.geocaching.enumerations.StatusCode;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.geopoint.GeopointFormatter.Format;
import cgeo.geocaching.geopoint.Units;
import cgeo.geocaching.geopoint.Viewport;
import cgeo.geocaching.network.Parameters;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.utils.Formatter;
import cgeo.geocaching.utils.LeastRecentlyUsedMap;
import cgeo.geocaching.utils.Log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.functions.Func2;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GCMap {
    private static Viewport lastSearchViewport = null;

    public static SearchResult searchByGeocodes(Set<String> geocodes) {
        final SearchResult result = new SearchResult();

        final String geocodeList = StringUtils.join(geocodes.toArray(), "|");

        try {
            final Parameters params = new Parameters("i", geocodeList, "_", String.valueOf(System.currentTimeMillis()));
            params.add("app", "cgeo");
            final String referer = GCConstants.URL_LIVE_MAP_DETAILS;
            final String data = StringUtils.defaultString(Tile.requestMapInfo(referer, params, referer).toBlocking().first());

            // Example JSON information
            // {"status":"success",
            //    "data":[{"name":"Mission: Impossible","gc":"GC1234","g":"34c2e609-5246-4f91-9029-d6c02b0f2a82","available":true,"archived":false,"subrOnly":false,"li":false,"fp":"5","difficulty":{"text":3.5,"value":"3_5"},"terrain":{"text":1.0,"value":"1"},"hidden":"7/23/2001","container":{"text":"Regular","value":"regular.gif"},"type":{"text":"Unknown Cache","value":8},"owner":{"text":"Ca$h_Cacher","value":"2db18e69-6877-402a-848d-6362621424f6"}},
            //            {"name":"HP: Hannover - Sahlkamp","gc":"GC2Q97X","g":"a09149ca-00e0-4aa2-b332-db2b4dfb18d2","available":true,"archived":false,"subrOnly":false,"li":false,"fp":"0","difficulty":{"text":1.0,"value":"1"},"terrain":{"text":1.5,"value":"1_5"},"hidden":"5/29/2011","container":{"text":"Small","value":"small.gif"},"type":{"text":"Traditional Cache","value":2},"owner":{"text":"GeoM@n","value":"1deaa69e-6bcc-421d-95a1-7d32b468cb82"}}]
            // }

            final JSONObject json = new JSONObject(data);
            final String status = json.getString("status");
            if (StringUtils.isBlank(status)) {

                throw new JSONException("No status inside JSON");
            }
            if ("success".compareTo(status) != 0) {
                throw new JSONException("Wrong status inside JSON");
            }
            final JSONArray dataArray = json.getJSONArray("data");
            if (dataArray == null) {
                throw new JSONException("No data inside JSON");
            }

            final ArrayList<Geocache> caches = new ArrayList<>();
            for (int j = 0; j < dataArray.length(); j++) {
                final Geocache cache = new Geocache();

                JSONObject dataObject = dataArray.getJSONObject(j);
                cache.setName(dataObject.getString("name"));
                cache.setGeocode(dataObject.getString("gc"));
                cache.setGuid(dataObject.getString("g")); // 34c2e609-5246-4f91-9029-d6c02b0f2a82"
                cache.setDisabled(!dataObject.getBoolean("available"));
                cache.setArchived(dataObject.getBoolean("archived"));
                cache.setPremiumMembersOnly(dataObject.getBoolean("subrOnly"));
                // "li" seems to be "false" always
                cache.setFavoritePoints(Integer.parseInt(dataObject.getString("fp")));
                JSONObject difficultyObj = dataObject.getJSONObject("difficulty");
                cache.setDifficulty(Float.parseFloat(difficultyObj.getString("text"))); // 3.5
                JSONObject terrainObj = dataObject.getJSONObject("terrain");
                cache.setTerrain(Float.parseFloat(terrainObj.getString("text"))); // 1.5
                cache.setHidden(GCLogin.parseGcCustomDate(dataObject.getString("hidden"), "MM/dd/yyyy")); // 7/23/2001
                JSONObject containerObj = dataObject.getJSONObject("container");
                cache.setSize(CacheSize.getById(containerObj.getString("text"))); // Regular
                JSONObject typeObj = dataObject.getJSONObject("type");
                cache.setType(CacheType.getByPattern(typeObj.getString("text"))); // Traditional Cache
                JSONObject ownerObj = dataObject.getJSONObject("owner");
                cache.setOwnerDisplayName(ownerObj.getString("text"));

                caches.add(cache);
            }
            result.addAndPutInCache(caches);
        } catch (JSONException e) {
            result.setError(StatusCode.UNKNOWN_ERROR);
        } catch (ParseException e) {
            result.setError(StatusCode.UNKNOWN_ERROR);
        } catch (NumberFormatException e) {
            result.setError(StatusCode.UNKNOWN_ERROR);
        }
        return result;
    }

    /**
     * @param data
     *            Retrieved data.
     * @return SearchResult. Never null.
     */
    public static SearchResult parseMapJSON(final String data, Tile tile, Bitmap bitmap, final Strategy strategy) {
        final SearchResult searchResult = new SearchResult();

        try {

            final LeastRecentlyUsedMap<String, String> nameCache = new LeastRecentlyUsedMap.LruCache<>(2000); // JSON id, cache name

            if (StringUtils.isEmpty(data)) {
                throw new JSONException("No page given");
            }

            // Example JSON information
            // {"grid":[....],
            //  "keys":["","55_55","55_54","17_25","55_53","17_27","17_26","57_53","57_55","3_62","3_61","57_54","3_60","15_27","15_26","15_25","4_60","4_61","4_62","16_25","16_26","16_27","2_62","2_60","2_61","56_53","56_54","56_55"],
            //  "data":{"55_55":[{"i":"gEaR","n":"Spiel & Sport"}],"55_54":[{"i":"gEaR","n":"Spiel & Sport"}],"17_25":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"55_53":[{"i":"gEaR","n":"Spiel & Sport"}],"17_27":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"17_26":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"57_53":[{"i":"gEaR","n":"Spiel & Sport"}],"57_55":[{"i":"gEaR","n":"Spiel & Sport"}],"3_62":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"3_61":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"57_54":[{"i":"gEaR","n":"Spiel & Sport"}],"3_60":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"15_27":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"15_26":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"15_25":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"4_60":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"4_61":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"4_62":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"16_25":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"16_26":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"16_27":[{"i":"Rkzt","n":"EDSSW:  Rathaus "}],"2_62":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"2_60":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"2_61":[{"i":"gOWz","n":"Baumarktserie - Wer Wo Was -"}],"56_53":[{"i":"gEaR","n":"Spiel & Sport"}],"56_54":[{"i":"gEaR","n":"Spiel & Sport"}],"56_55":[{"i":"gEaR","n":"Spiel & Sport"}]}
            //  }

            final JSONObject json = new JSONObject(data);

            final JSONArray grid = json.getJSONArray("grid");
            if (grid == null || grid.length() != (UTFGrid.GRID_MAXY + 1)) {
                throw new JSONException("No grid inside JSON");
            }
            final JSONArray keys = json.getJSONArray("keys");
            if (keys == null) {
                throw new JSONException("No keys inside JSON");
            }
            final JSONObject dataObject = json.getJSONObject("data");
            if (dataObject == null) {
                throw new JSONException("No data inside JSON");
            }

            // iterate over the data and construct all caches in this tile
            Map<String, List<UTFGridPosition>> positions = new HashMap<>(); // JSON id as key
            Map<String, List<UTFGridPosition>> singlePositions = new HashMap<>(); // JSON id as key

            for (int i = 1; i < keys.length(); i++) { // index 0 is empty
                String key = keys.getString(i);
                if (StringUtils.isNotBlank(key)) {
                    UTFGridPosition pos = UTFGridPosition.fromString(key);
                    JSONArray dataForKey = dataObject.getJSONArray(key);
                    for (int j = 0; j < dataForKey.length(); j++) {
                        JSONObject cacheInfo = dataForKey.getJSONObject(j);
                        String id = cacheInfo.getString("i");
                        nameCache.put(id, cacheInfo.getString("n"));

                        List<UTFGridPosition> listOfPositions = positions.get(id);
                        List<UTFGridPosition> singleListOfPositions = singlePositions.get(id);

                        if (listOfPositions == null) {
                            listOfPositions = new ArrayList<>();
                            positions.put(id, listOfPositions);
                            singleListOfPositions = new ArrayList<>();
                            singlePositions.put(id, singleListOfPositions);
                        }

                        listOfPositions.add(pos);
                        if (dataForKey.length() == 1) {
                            singleListOfPositions.add(pos);
                        }

                    }
                }
            }

            final ArrayList<Geocache> caches = new ArrayList<>();
            for (Entry<String, List<UTFGridPosition>> entry : positions.entrySet()) {
                String id = entry.getKey();
                List<UTFGridPosition> pos = entry.getValue();
                UTFGridPosition xy = UTFGrid.getPositionInGrid(pos);
                Geocache cache = new Geocache();
                cache.setDetailed(false);
                cache.setReliableLatLon(false);
                cache.setGeocode(id);
                cache.setName(nameCache.get(id));
                cache.setCoords(tile.getCoord(xy), tile.getZoomLevel());
                if (strategy.flags.contains(StrategyFlag.PARSE_TILES) && bitmap != null) {
                    for (UTFGridPosition singlePos : singlePositions.get(id)) {
                        if (IconDecoder.parseMapPNG(cache, bitmap, singlePos, tile.getZoomLevel())) {
                            break; // cache parsed
                        }
                    }
                } else {
                    cache.setType(CacheType.UNKNOWN, tile.getZoomLevel());
                }

                boolean exclude = false;
                if (Settings.isExcludeMyCaches() && (cache.isFound() || cache.isOwner())) { // workaround for BM
                    exclude = true;
                }
                if (Settings.isExcludeDisabledCaches() && cache.isDisabled()) {
                    exclude = true;
                }
                if (!Settings.getCacheType().contains(cache) && cache.getType() != CacheType.UNKNOWN) { // workaround for BM
                    exclude = true;
                }
                if (!exclude) {
                    caches.add(cache);
                }
            }
            searchResult.addAndPutInCache(caches);
            Log.d("Retrieved " + searchResult.getCount() + " caches for tile " + tile.toString());

        } catch (RuntimeException e) {
            Log.e("GCMap.parseMapJSON", e);
        } catch (JSONException e) {
            Log.e("GCMap.parseMapJSON", e);
        }

        return searchResult;
    }

    /**
     * Searches the view port on the live map with Strategy.AUTO
     *
     * @param viewport
     *            Area to search
     * @param tokens
     *            Live map tokens
     * @return
     */
    public static SearchResult searchByViewport(final Viewport viewport, final MapTokens tokens) {
        int speed = (int) CgeoApplication.getInstance().currentGeo().getSpeed() * 60 * 60 / 1000; // in km/h
        Strategy strategy = Settings.getLiveMapStrategy();
        if (strategy == Strategy.AUTO) {
            strategy = speed >= 30 ? Strategy.FAST : Strategy.DETAILED;
        }

        SearchResult result = searchByViewport(viewport, tokens, strategy);

        if (Settings.isDebug()) {
            StringBuilder text = new StringBuilder(Formatter.SEPARATOR).append(strategy.getL10n()).append(Formatter.SEPARATOR).append(Units.getSpeed(speed));
            result.setUrl(result.getUrl() + text);
        }

        return result;
    }

    /**
     * Searches the view port on the live map for caches.
     * The strategy dictates if only live map information is used or if an additional
     * searchByCoordinates query is issued.
     *
     * @param viewport
     *            Area to search
     * @param tokens
     *            Live map tokens
     * @param strategy
     *            Strategy for data retrieval and parsing, @see Strategy
     * @return
     */
    private static SearchResult searchByViewport(final Viewport viewport, final MapTokens tokens, final Strategy strategy) {
        Log.d("GCMap.searchByViewport" + viewport.toString());

        final SearchResult searchResult = new SearchResult();

        if (Settings.isDebug()) {
            searchResult.setUrl(viewport.getCenter().format(Format.LAT_LON_DECMINUTE));
        }

        if (strategy.flags.contains(StrategyFlag.LOAD_TILES)) {
            final Set<Tile> tiles = Tile.getTilesForViewport(viewport);

            if (Settings.isDebug()) {
                searchResult.setUrl(new StringBuilder().append(tiles.iterator().next().getZoomLevel()).append(Formatter.SEPARATOR).append(searchResult.getUrl()).toString());
            }

            for (final Tile tile : tiles) {
                if (!Tile.cache.contains(tile)) {
                    final Parameters params = new Parameters(
                            "x", String.valueOf(tile.getX()),
                            "y", String.valueOf(tile.getY()),
                            "z", String.valueOf(tile.getZoomLevel()),
                            "ep", "1",
                            "app", "cgeo");
                    if (tokens != null) {
                        params.put("k", tokens.getUserSession(), "st", tokens.getSessionToken());
                    }
                    if (Settings.isExcludeMyCaches()) { // works only for PM
                        params.put("hf", "1", "hh", "1"); // hide found, hide hidden
                    }
                    // ect: exclude cache type (probably), comma separated list
                    if (Settings.getCacheType() != CacheType.ALL) {
                        params.put("ect", getCacheTypeFilter(Settings.getCacheType()));
                    }
                    if (tile.getZoomLevel() != 14) {
                        params.put("_", String.valueOf(System.currentTimeMillis()));
                    }

                    // The PNG must be requested first, otherwise the following request would always return with 204 - No Content
                    final Observable<Bitmap> bitmapObs = Tile.requestMapTile(params);
                    final Observable<String> dataObs = Tile.requestMapInfo(GCConstants.URL_MAP_INFO, params, GCConstants.URL_LIVE_MAP);
                    Observable.zip(bitmapObs, dataObs, new Func2<Bitmap, String, Void>() {
                        @Override
                        public Void call(final Bitmap bitmap, final String data) {
                            final boolean validBitmap = bitmap != null && bitmap.getWidth() == Tile.TILE_SIZE && bitmap.getHeight() == Tile.TILE_SIZE;

                            if (StringUtils.isEmpty(data)) {
                                Log.w("GCMap.searchByViewport: No data from server for tile (" + tile.getX() + "/" + tile.getY() + ")");
                            } else {
                                final SearchResult search = GCMap.parseMapJSON(data, tile, validBitmap ? bitmap : null, strategy);
                                if (CollectionUtils.isEmpty(search.getGeocodes())) {
                                    Log.e("GCMap.searchByViewport: No cache parsed for viewport " + viewport);
                                } else {
                                    synchronized (searchResult) {
                                        searchResult.addSearchResult(search);
                                    }
                                }
                                synchronized (Tile.cache) {
                                    Tile.cache.add(tile);
                                }
                            }

                            // release native bitmap memory
                            if (bitmap != null) {
                                bitmap.recycle();
                            }

                            return null;
                        }
                    }).toBlocking().single();
                }
            }

            // Check for vanished found caches
            if (tiles.iterator().next().getZoomLevel() >= Tile.ZOOMLEVEL_MIN_PERSONALIZED) {
                searchResult.addFilteredGeocodes(DataStore.getCachedMissingFromSearch(searchResult, tiles, GCConnector.getInstance(), Tile.ZOOMLEVEL_MIN_PERSONALIZED - 1));
            }
        }

        if (strategy.flags.contains(StrategyFlag.SEARCH_NEARBY) && Settings.isGCPremiumMember()) {
            final Geopoint center = viewport.getCenter();
            if ((lastSearchViewport == null) || !lastSearchViewport.contains(center)) {
                //FIXME We don't have a RecaptchaReceiver!?
                SearchResult search = GCParser.searchByCoords(center, Settings.getCacheType(), false, null);
                if (search != null && !search.isEmpty()) {
                    final Set<String> geocodes = search.getGeocodes();
                    lastSearchViewport = DataStore.getBounds(geocodes);
                    searchResult.addGeocodes(geocodes);
                }
            }
        }

        return searchResult;
    }

    /**
     * Creates a list of caches types to filter on the live map (exclusion string)
     * 
     * @param typeToDisplay
     *            - cache type to omit from exclusion list so it gets displayed
     * @return
     * 
     *         cache types for live map filter:
     *         2 = traditional, 9 = ape, 5 = letterbox
     *         3 = multi
     *         6 = event, 453 = mega, 13 = cito, 1304 = gps adventures
     *         4 = virtual, 11 = webcam, 137 = earth
     *         8 = mystery, 1858 = whereigo
     */
    private static String getCacheTypeFilter(CacheType typeToDisplay) {
        Set<String> filterTypes = new HashSet<>();
        // Put all types in set, remove what should be visible in a second step
        filterTypes.addAll(Arrays.asList("2", "9", "5", "3", "6", "453", "13", "1304", "4", "11", "137", "8", "1858"));
        switch (typeToDisplay) {
            case TRADITIONAL:
                filterTypes.remove("2");
                break;
            case PROJECT_APE:
                filterTypes.remove("9");
                break;
            case LETTERBOX:
                filterTypes.remove("5");
                break;
            case MULTI:
                filterTypes.remove("3");
                break;
            case EVENT:
                filterTypes.remove("6");
                break;
            case MEGA_EVENT:
                filterTypes.remove("453");
                break;
            case CITO:
                filterTypes.remove("13");
                break;
            case GPS_EXHIBIT:
                filterTypes.remove("1304");
                break;
            case VIRTUAL:
                filterTypes.remove("4");
                break;
            case WEBCAM:
                filterTypes.remove("11");
                break;
            case EARTH:
                filterTypes.remove("137");
                break;
            case MYSTERY:
                filterTypes.remove("8");
                break;
            case WHERIGO:
                filterTypes.remove("1858");
                break;
            default:
                // nothing to remove otherwise
        }

        return StringUtils.join(filterTypes.toArray(), ",");
    }
}
