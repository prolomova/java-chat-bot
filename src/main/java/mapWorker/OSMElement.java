package mapWorker;

import java.util.HashMap;
import java.util.Map;

public abstract class OSMElement {
    private int id;
    private String version;
    private Map<String, String> tags = new HashMap<>();

    public OSMElement(int id, String version) {
        this.id = id;
        this.version = version;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}