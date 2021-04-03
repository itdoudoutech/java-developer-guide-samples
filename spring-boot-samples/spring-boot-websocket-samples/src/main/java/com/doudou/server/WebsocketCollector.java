package com.doudou.server;

import com.doudou.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@ServerEndpoint("/websocket")
public class WebsocketCollector {

    private static final Set<Session> SESSION_SET = new CopyOnWriteArraySet<>();

    private static final String SESSION_KEY = "sessionKey";

    /**
     * On open.
     *
     * @param session the session
     */
    @OnOpen
    public void onOpen(final Session session) {
        log.info("websocket on open successful....");
        SESSION_SET.add(session);
    }

    /**
     * On message.
     *
     * @param message the message
     * @param session the session
     */
    @OnMessage
    public void onMessage(final String message, final Session session) {
        ThreadLocalUtil.put(SESSION_KEY, session);
        log.info("onMessage message = {}", message);
        send(String.format("Hello, I have receive your message, your message is {%s}", message));
    }

    /**
     * On close.
     *
     * @param session the session
     */
    @OnClose
    public void onClose(final Session session) {
        SESSION_SET.remove(session);
        ThreadLocalUtil.clear();
    }

    /**
     * On error.
     *
     * @param session the session
     * @param error   the error
     */
    @OnError
    public void onError(final Session session, final Throwable error) {
        SESSION_SET.remove(session);
        ThreadLocalUtil.clear();
        log.error("websocket collection error: ", error);
    }

    public static void send(final String message) {
        Session session = (Session) ThreadLocalUtil.get(SESSION_KEY);
        sendMessageBySession(session, message);
    }

    private static void sendMessageBySession(final Session session, final String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("websocket send result is exception: ", e);
        }
    }
}
