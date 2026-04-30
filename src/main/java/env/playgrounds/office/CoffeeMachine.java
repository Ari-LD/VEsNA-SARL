package env.playgrounds.office;

import vesna.WsClient;
import vesna.WsClientMsgHandler;
import java.net.URI;
import org.json.JSONObject;

public class CoffeeMachine {

    private WsClient client;

    public CoffeeMachine() {
        try {
            client = new WsClient(new URI("ws://localhost:8090"));
            client.setMsgHandler(new WsClientMsgHandler() {
                @Override
                public void handle_msg(String msg) { manage_msg(msg); }
                @Override
                public void handle_error(Exception ex) { manage_error(ex); }
            });
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manage_msg(String msg) {
        System.out.println("Received from Godot: " + msg);
        JSONObject log = new JSONObject(msg);
        String type = log.getString("type");
        JSONObject data = log.getJSONObject("data");
        
        if ("signal".equals(type)) {
            handle_event(data);
        }
    }

    private void handle_event(JSONObject data) {
        String type = data.getString("type");
        String status = data.getString("status");
        if ("interaction".equals(type) && "completed".equals(status)) {
            System.out.println("Status updated to: coffee");
        }
    }

    private void manage_error(Exception ex) {
        System.err.println(ex.getMessage());
    }
    
    // Standard public method instead of @OPERATION
    public void make_coffee(String cup_name) {
        JSONObject log = new JSONObject();
        log.put("sender", "coffee_machine");
        log.put("receiver", "artifact");
        log.put("type", "interaction");
        
        JSONObject data = new JSONObject();
        data.put("type", "make_coffee");
        data.put("quantity", "espresso");
        data.put("cup", cup_name);
        log.put("data", data);

        client.send(log.toString());
    }

    public void take_cup() {
        System.out.println("Status: ready");
    }
    
    public WsClient getClient() {
    	return this.client;
    }
}