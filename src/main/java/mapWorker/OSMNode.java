package mapWorker;

import java.util.HashMap;
import java.util.Map;

public class OSMNode {

    private String id;

    private String lat;

    private String lon;

    private final Map<String, String> tags = new HashMap<String,String>();

    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public OSMNode(String id, String latitude, String longitude, String version, Map<String, String> tags) {

        this.id = id;
        this.lat= latitude;
        this.lon = longitude;
        this.version = version;
        this.tags.putAll(tags);

    }

    @Override
    public String toString() {
        return getId() + " - " + getLat() + "," + getLon() + " - " + getTags();
    }

}