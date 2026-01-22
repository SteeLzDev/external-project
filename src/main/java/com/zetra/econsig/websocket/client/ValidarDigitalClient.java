package com.zetra.econsig.websocket.client;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Properties;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ValidarDigitalClient</p>
 * <p>Description: Cliente websocket para validação de digital.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarDigitalClient {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarDigitalClient.class);

    // Timeout de 1 minuto
    private final long maxTime = 60000;

    private boolean running = false;

    private boolean digitalValida = false;

    private String uri;

    private final String idEmpregado;

    private final String tokenLeitor;

    public ValidarDigitalClient(String idEmpregado, String tokenLeitor) {
        configura();
        this.idEmpregado = idEmpregado;
        this.tokenLeitor = tokenLeitor;
    }

    private void configura() {
        String name = "ValidarDigitalClient.properties";
        try {
            Properties env = new Properties();
            env.load(ValidarDigitalClient.class.getClassLoader().getResourceAsStream(name));

            uri = env.getProperty("server.uri");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public boolean validarDigital() {
        if (TextHelper.isNull(idEmpregado) || TextHelper.isNull(tokenLeitor)) {
            return false;
        }

        try {
            // open websocket
            final ValidarDigitalClientEndpoint clientEndPoint = new ValidarDigitalClientEndpoint(new URI(uri));

            // add listener
            clientEndPoint.addMessageHandler(new ValidarDigitalClientEndpoint.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    JsonReader reader = Json.createReader(new StringReader(message));
                    if (reader != null) {
                        JsonObject jsonMessage = reader.readObject();
                        String action = jsonMessage.getString("action");
                        if (!TextHelper.isNull(action)) {
                            if ("VerifyResult".equals(jsonMessage.getString("action"))) {
                                String idEmpregadoResult = jsonMessage.getString("ID_Empleado");
                                String tokenLeitorResult = jsonMessage.getString("TokenLector");
                                if (!TextHelper.isNull(idEmpregadoResult) && idEmpregado.equals(idEmpregadoResult) &&
                                    !TextHelper.isNull(tokenLeitorResult) && tokenLeitor.equals(tokenLeitorResult)) {
                                    digitalValida = jsonMessage.getBoolean("Resultado", false);
                                    running = false;
                                }
                            }
                        }
                    }
                }
            });
            StringBuilder message = new StringBuilder();
            message.append("{");
            message.append("\"action\": \"Verify\",");
            message.append("\"TokenLector\": \"").append(tokenLeitor).append("\", ");
            message.append("\"ID_Empleado\": \"").append(idEmpregado).append("\"");
            message.append("}");

            long startTime = Calendar.getInstance().getTimeInMillis();
            while (clientEndPoint.userSession == null) {
                // wait 500 miliseconds for messages from connection
                Thread.sleep(500);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - startTime > maxTime) {
                    running = false;
                }
            }
            running = true;

            // send message to websocket
            clientEndPoint.sendMessage(message.toString());
            startTime = Calendar.getInstance().getTimeInMillis();
            while (running) {
                // wait 500 miliseconds for messages from websocket
                Thread.sleep(500);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - startTime > maxTime) {
                    running = false;
                }
            }
            clientEndPoint.userSession.close();
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
            digitalValida = false;
        } catch (URISyntaxException ex) {
            LOG.error(ex.getMessage(), ex);
            digitalValida = false;
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            digitalValida = false;
        }
        return digitalValida;
    }
}
