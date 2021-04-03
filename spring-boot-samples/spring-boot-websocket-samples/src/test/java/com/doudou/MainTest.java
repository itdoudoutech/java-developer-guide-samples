package com.doudou;

import com.doudou.client.WebsocketClient;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class MainTest {

    public static void main(String[] args) throws Exception {
        final String url = String.format("ws://localhost:%s/websocket", 9091);
        URI uri = new URI(url);
        WebSocketClient client = new WebsocketClient(uri);
        client.connectBlocking(3000, TimeUnit.MILLISECONDS);
        client.send("the second message ...");
        Thread.sleep(1000 * 5);
        client.close();
    }

}
