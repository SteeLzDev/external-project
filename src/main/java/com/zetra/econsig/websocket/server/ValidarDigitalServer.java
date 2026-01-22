package com.zetra.econsig.websocket.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import javax.script.ScriptException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * <p>Title: ValidarDigitalServer</p>
 * <p>Description: Servidor websocket para validação de digital.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@ServerEndpoint(value = "/actions")
public class ValidarDigitalServer {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarDigitalServer.class);

    private static final Set<Session> SESSIONS = new HashSet<>();

    @OnOpen
    public void open(Session session) {
        SESSIONS.add(session);
        if (session != null) {
            LOG.debug("open(): SESSIONS.size() = " + SESSIONS.size());
        }
    }

    @OnError
    public void onError(Throwable error) {
        LOG.error(error.getMessage());
    }

    @OnClose
    public void close(Session session) {
        SESSIONS.remove(session);
        if (session != null) {
            LOG.debug("close(): SESSIONS.size() = " + SESSIONS.size());
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException, ScriptException, NoSuchMethodException {
        if (session != null) {
            LOG.debug("handleMessage(): message = " + message);
        }
        JsonReader reader = Json.createReader(new StringReader(message));
        if (reader != null) {
            JsonObject jsonMessage = reader.readObject();
            String action = jsonMessage.getString("action");
            if (action != null && !action.equals("")) {
                if ("Verify".equals(jsonMessage.getString("action"))) {
                    sendToAllConnectedSessions(jsonMessage);
                }

                if ("VerifyResult".equals(jsonMessage.getString("action"))) {
                    sendToAllConnectedSessions(jsonMessage);
                }
            }
        }
    }

    public void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : SESSIONS) {
            sendToSession(session, message);
        };
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            if (session != null) {
                LOG.debug("sendToSession(), sending: message = " + message.toString());
            }
            session.getBasicRemote().sendText(message.toString());
            if (session != null) {
                LOG.debug("sendToSession(), sent: message = " + message.toString());
            }
        } catch (IOException ex) {
            SESSIONS.remove(session);
            LOG.error(ex.getMessage());
        }
    }
}