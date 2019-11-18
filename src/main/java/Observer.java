import org.json.JSONObject;

public interface Observer {
    void notify(JSONObject message);
}
