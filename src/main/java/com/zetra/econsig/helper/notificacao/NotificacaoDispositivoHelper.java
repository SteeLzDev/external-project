package com.zetra.econsig.helper.notificacao;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.LocaleHelper;

import jakarta.json.stream.JsonGenerationException;

/**
 * <p>Title: NotificacaoDispositivoHelper</p>
 * <p>Description: Helper para geração e envio de push notifications.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NotificacaoDispositivoHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NotificacaoDispositivoHelper.class);

    public static boolean enviarNotificacao(List<String> chaveDispositivos, String ndiTexto, String mensagemNdi, String tituloNdi, String tnoCodigo, String collapseId, AcessoSistema responsavel) throws ZetraException {
        return enviarOneSignalNotificacao(chaveDispositivos, ndiTexto, mensagemNdi, tituloNdi, tnoCodigo, collapseId, responsavel);
    }

    /**
     * enviar notificação para tecnologia push OneSignal
     * @param chaveDispositivos
     * @param ndiTexto
     * @param tnoCodigo
     * @param responsavel
     * @throws ZetraException
     */
    private static boolean enviarOneSignalNotificacao(List<String> chaveDispositivos, String ndiTexto, String mensagemNdi, String tituloNdi, String tnoCodigo, String collapseId, AcessoSistema responsavel) throws ZetraException {
        HttpURLConnection con = null;
        try {
            //String jsonResponse;

            Properties fcmProp = new Properties();

            fcmProp.load(NotificacaoDispositivoHelper.class.getClassLoader().getResourceAsStream("serverAPIKey.properties"));
            String apiUrl = fcmProp.getProperty("onesignal.ulr").trim();
            String apiKey = fcmProp.getProperty("onesignal.api.key").trim();
            String apiId = fcmProp.getProperty("onesignal.app.id").trim();

            Map<String, String> messages = new HashMap<>();
            // um resource em inglês sempre deve ser enviado pro API oneSignal. Porém, será exibido pro usuário a do locale
            // em que está o device.
            String linguagem = LocaleHelper.getLanguage().indexOf("-") >= 0 ? LocaleHelper.getLanguage().substring(0, LocaleHelper.getLanguage().indexOf("-")) : LocaleHelper.getLanguage();
            messages.put(LocaleHelper.INGLATERRA.substring(0, LocaleHelper.getLocale().indexOf("-")), mensagemNdi);
            messages.put(linguagem, mensagemNdi);

            //títulos: mesma lógica do messages
            Map<String, String> titulos = new HashMap<>();
            titulos.put(LocaleHelper.INGLATERRA.substring(0, LocaleHelper.getLocale().indexOf("-")), tituloNdi);
            titulos.put(linguagem, tituloNdi);

            URL url = URI.create(apiUrl).toURL();

            con = (HttpURLConnection)url.openConnection();
            if (con != null) {
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", "Basic " + apiKey);
                con.setRequestMethod("POST");

                String strJsonBody = gerarJsonNotificacao(chaveDispositivos, ndiTexto, collapseId, responsavel, apiId, messages, titulos);

                byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                con.setFixedLengthStreamingMode(sendBytes.length);

                OutputStream outputStream = con.getOutputStream();
                if (outputStream != null) {
                    outputStream.write(sendBytes);
                    outputStream.close();
                } else {
                    throw new ZetraException("mensagem.erro.notificacao.envio.dados", responsavel);
                }

                int httpResponse = con.getResponseCode();
                con.getInputStream().close();
                LOG.info("Notification HttpResponse: " + httpResponse);

                if (httpResponse != 200) {
                    return false;
                }
            } else {
                throw new ZetraException("mensagem.erro.notificacao.conexao.servidor", responsavel);
            }
        } catch (Throwable t) {
            throw new ZetraException("mensagem.erro.notificacao.falha.enviar", responsavel, t);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return true;
    }

    /**
     * gera JSON de notificação à plataforma OneSignal
     * @param chaveDispositivos - lista de chaves de dispositivos de destino
     * @param ndiTexto - texto livre a enviar como dados adicionais na notificação. Geralmente, em estrutura JSON
     * @param responsavel
     * @param apiId - ID da aplicação criada no OneSignal que representa o sistema eConsigMobile
     * @param messages - Map com uma chave pra cada idioma em que se deseja enviar a mensagem de corpo da Notificação. "en" é obrigatório
     * @param titulos - Map com uma chave pra cada idioma em que se deseja enviar o título da Notificação. "en" é obrigatório.
     *                  ex.: titulos.put("en", "Title"); titulos.put("pt", "Título");
     * @return
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws UsuarioControllerException
     */
    public static String gerarJsonNotificacao(List<String> chaveDispositivos, String ndiTexto, String collapseId, AcessoSistema responsavel, String apiId, Map<String, String> messages, Map<String, String> titulos) throws IOException, JsonGenerationException, JsonMappingException, UsuarioControllerException {
        ObjectMapper mapper = new ObjectMapper();
        String ids = mapper.writeValueAsString(chaveDispositivos);
        String tituloJson = mapper.writeValueAsString(titulos);
        String contents = mapper.writeValueAsString(messages);

        String strJsonBody = "{"
                +   "\"app_id\": \""+ apiId + "\","
                +   "\"include_player_ids\": " + ids + ","
                +   "\"data\": " + ndiTexto + ","
                +   "\"collapse_id\": \"" + collapseId + "\","
                +   "\"headings\": " + tituloJson + ","
                +   "\"contents\": " + contents
                + "}";
        return strJsonBody;
    }

}
