import org.json.JSONObject;

public interface Observed {
    void addObserver(Observer observer);
    void notifyObservers(JSONObject message);
}
