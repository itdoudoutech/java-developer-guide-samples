package com.doudou.client;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public final class WebsocketClient extends WebSocketClient {

    public WebsocketClient(final URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        send("This is client");
    }

    @Override
    public void onMessage(final String result) {
        log.info("onMessage and message is [{}]", result);
    }

    @Override
    public void onClose(final int i, final String s, final boolean b) {
        this.close();
    }

    @Override
    public void onError(final Exception e) {
        this.close();
    }

}
