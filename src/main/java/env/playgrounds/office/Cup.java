package env.playgrounds.office;

import org.json.JSONObject;
import vesna.WsClient;

public class Cup {
    private String art_name;
    private String owner = null;
    private WsClient client;

    public Cup(String name, WsClient sharedClient) {
        this.art_name = name;
        this.client = sharedClient;
    }

    public synchronized boolean grab(String agentName) {
        if (this.owner != null) return false;
        
        this.owner = agentName;
        sendAction("grab");
        return true;
    }

    public synchronized void release() {
        this.owner = null;
        sendAction("release");
    }

    private void sendAction(String type) {
        JSONObject action = new JSONObject();
        action.put("type", "interact");
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("art_name", art_name);
        action.put("data", data);
        client.send(action.toString());
    }
}