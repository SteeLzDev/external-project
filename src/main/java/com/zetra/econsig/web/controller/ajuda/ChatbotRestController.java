package com.zetra.econsig.web.controller.ajuda;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.dhatim.businesshours.BusinessHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.zetra.econsig.dto.web.AtendimentoMensagemDTO;
import com.zetra.econsig.dto.web.ChatbotResponseDTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Atendimento;
import com.zetra.econsig.persistence.entity.AtendimentoMensagem;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.sistema.AtendimentoController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ChatbotRestController</p>
 * <p>Description: REST Controller para Atendimento via Chatbot</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@RestController
public class ChatbotRestController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ChatbotRestController.class);

    public static final String CHATBOT_SESSION_ID            = "_chatbot_session_id";
    public static final String CHATBOT_CODIGO_ATENDIMENTO    = "_chatbot_codigo_atendimento";
    public static final String CHATBOT_FALLBACK_COUNT        = "_chatbot_fallback_count";
    public static final String CHATBOT_NOME_USUARIO          = "_chatbot_nome_usuario";
    public static final String CHATBOT_EMAIL_USUARIO         = "_chatbot_email_usuario";
    public static final String CHATBOT_ORIGEM_LOGIN_SERVIDOR = "_chatbot_login_servidor";

    @Autowired
    private AtendimentoController atendimentoController;

    @RequestMapping(value = "/v3/iniciarChatbot", produces = { "application/json" })
    public Map<String, Object> iniciarConversa(@RequestParam(value = "nome", required = false) String nome, @RequestParam(value = "email", required = false) String email, @RequestParam(value = "sessionId", required = false) String sessionId, HttpServletRequest request, HttpSession session) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        Map<String, Object> response = new HashMap<>();
        Atendimento atendimento = null;
        List<AtendimentoMensagem> mensagens = null;

        if (StringUtils.isBlank(sessionId) && session.getAttribute(CHATBOT_SESSION_ID) != null) {
            sessionId = session.getAttribute(CHATBOT_SESSION_ID).toString();
        }
        if (StringUtils.isBlank(nome) && session.getAttribute(CHATBOT_NOME_USUARIO) != null) {
            nome = session.getAttribute(CHATBOT_NOME_USUARIO).toString();
        }
        if (StringUtils.isBlank(email) && session.getAttribute(CHATBOT_EMAIL_USUARIO) != null) {
            email = session.getAttribute(CHATBOT_EMAIL_USUARIO).toString();
        }

        // Se o idSessao foi passado do cookie, verifica se é válido para o e-mail informado
        if (StringUtils.isNotBlank(sessionId)) {
            atendimento = atendimentoController.findByEmailAndSessao(email, sessionId, responsavel);
            if (atendimento == null) {
                sessionId = null;
            }
        }

        if (atendimento == null) {
            atendimento = new Atendimento();
        }

        if (responsavel.isSessaoValida()) {
            // Substitui as informações passadas pelo usuário logado
            nome = responsavel.getUsuNome();
            email = StringUtils.isNotBlank(responsavel.getUsuEmail()) ? responsavel.getUsuEmail() : email;
            if (atendimento.getUsuario() == null) {
                atendimento.setUsuario(new Usuario(responsavel.getUsuCodigo()));
            }
        }

        // Gera novo id de sessão caso não tenha sido informado ou seja inválido para o e-mail
        if (StringUtils.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }

        atendimento.setAteNomeUsuario(nome);
        atendimento.setAteEmailUsuario(email);
        atendimento.setAteIdSessao(sessionId);
        atendimento.setAteIpAcesso(responsavel.getIpUsuario());

        if (StringUtils.isBlank(atendimento.getAteCodigo())) {
            atendimento = atendimentoController.create(atendimento, responsavel);
        } else {
            atendimento = atendimentoController.update(atendimento, responsavel);
            mensagens = atendimentoController.lstMensagensByAtendimento(atendimento.getAteCodigo(), responsavel);
        }

        session.setAttribute(CHATBOT_SESSION_ID, sessionId);
        session.setAttribute(CHATBOT_CODIGO_ATENDIMENTO, atendimento.getAteCodigo());
        session.setAttribute(CHATBOT_NOME_USUARIO, nome);
        session.setAttribute(CHATBOT_EMAIL_USUARIO, email);

        response.put("result", "true");
        response.put("sessionId", sessionId);
        if (mensagens != null && !mensagens.isEmpty()) {
            List<AtendimentoMensagemDTO> mensagensRetorno = new ArrayList<>(mensagens.size());
            for (AtendimentoMensagem mensagem : mensagens) {
                mensagensRetorno.add(new AtendimentoMensagemDTO(mensagem.getAmeTexto(), mensagem.getAmeBot()));
            }
            response.put("mensagens", mensagensRetorno);
        }
        return response;
    }

    @RequestMapping(value = "/v3/enviarMensagemChatbot", produces = { "application/json" })
    public Map<String, String> enviarMensagem(@RequestParam(value = "mensagem", required = false) String mensagem, HttpServletRequest request, HttpSession session) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        int fallbackLimit = 3;
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CHATBOT_FALLBACK_LIMIT, responsavel))) {
            try {
                fallbackLimit = Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_CHATBOT_FALLBACK_LIMIT, responsavel).toString());
            } catch (NumberFormatException ex) {
                LOG.error(ex);
                fallbackLimit = 3;
            }
        }

        String businessHoursConfig = "wday{Mon-Fri} hour{8am-5pm}";
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_CHATBOT_BUSINESS_HOURS, responsavel))) {
            businessHoursConfig = ParamSist.getInstance().getParam(CodedValues.TPC_CHATBOT_BUSINESS_HOURS, responsavel).toString();
        }

        Map<String, String> response = new HashMap<>();

        // Recuperar da sessão o id da sessão e código de atendimento
        String sessionId = (String) session.getAttribute(CHATBOT_SESSION_ID);
        String ateCodigo = (String) session.getAttribute(CHATBOT_CODIGO_ATENDIMENTO);

        // Define o projeto do chatbot
        String projectId = JspHelper.getIdAgenteChatbot(responsavel, session);

        // Se não está configurado o id do chatbot, então retorna
        if (StringUtils.isBlank(projectId)) {
            return null;
        }

        if (StringUtils.isNotBlank(ateCodigo)) {
            AtendimentoMensagem pergunta = new AtendimentoMensagem();
            pergunta.setAteCodigo(ateCodigo);
            pergunta.setAmeBot(false);
            pergunta.setAmeTexto(mensagem);
            atendimentoController.addMensagem(pergunta, responsavel);
        }

        String mensagemChatbot = null;
        ChatbotResponseDTO chatbotResponse = detectIntentText(mensagem, sessionId, projectId);
        if (chatbotResponse == null) {
            mensagemChatbot = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.nao.configurado", responsavel);
        } else {
            mensagemChatbot = chatbotResponse.getResponse();
        }

        boolean result = StringUtils.isNotBlank(mensagemChatbot);
        boolean redirect = false;

        if (chatbotResponse != null && chatbotResponse.isFallback()) {
            Integer fallbackCount = (Integer) session.getAttribute(CHATBOT_FALLBACK_COUNT);
            if (fallbackCount == null) {
                fallbackCount = 0;
            }
            fallbackCount++;
            if (fallbackCount >= fallbackLimit) {
                result = false;

                boolean mostraChatSuporte = false;
                if (responsavel.isCseSupOrg()) {
                    mostraChatSuporte = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_CSE_ORG, CodedValues.TPC_SIM, responsavel);
                } else if (responsavel.isCsaCor()) {
                    mostraChatSuporte = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_CSA_COR, CodedValues.TPC_SIM, responsavel);
                } else if (responsavel.isSer()) {
                    mostraChatSuporte = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SER, CodedValues.TPC_SIM, responsavel);
                } else {
                    // Sem sessão válida, olhar se veio da página de login ou servidor
                    Boolean origemLoginServidor = (Boolean) session.getAttribute(ChatbotRestController.CHATBOT_ORIGEM_LOGIN_SERVIDOR);
                    if (origemLoginServidor != null && origemLoginServidor) {
                        mostraChatSuporte = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                    } else {
                        // Página de login de NÃO servidor não tem chat de suporte
                        mostraChatSuporte = false;
                    }
                }

                if (mostraChatSuporte) {
                    // Avalia se está em horário comercial
                    BusinessHours businessHours = new BusinessHours(businessHoursConfig);
                    if (businessHours.isOpen(LocalDateTime.now())) {
                        // Se está no horário comercial, então mostra mensagem e redireciona para o chat de suporte
                        mensagemChatbot = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.sem.treinamento", responsavel);
                        redirect = true;
                    } else {
                        // Fora do horário comercial
                        mensagemChatbot = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.sem.treinamento.fora.horario", responsavel);
                    }
                } else {
                    // Manda a dúvida por e-mail para suporte
                    String nomeUsuario = (String) session.getAttribute(CHATBOT_NOME_USUARIO);
                    String emailUsuario = (String) session.getAttribute(CHATBOT_EMAIL_USUARIO);

                    // Não tem chat de suporte, exibe mensagem para usuário
                    if (!TextHelper.isNull(emailUsuario)) {
                        mensagemChatbot = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.sem.treinamento.sem.suporte", responsavel);
                    } else {
                        mensagemChatbot = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.sem.treinamento.sem.suporte.sem.email", responsavel);
                    }

                    EnviaEmailHelper.enviarEmailSuporteDuvidaChatbot(nomeUsuario, emailUsuario, mensagem, responsavel);
                }
                session.removeAttribute(CHATBOT_FALLBACK_COUNT);
            } else {
                session.setAttribute(CHATBOT_FALLBACK_COUNT, fallbackCount);
            }
        } else {
            session.removeAttribute(CHATBOT_FALLBACK_COUNT);
        }

        if (StringUtils.isNotBlank(mensagemChatbot) && StringUtils.isNotBlank(ateCodigo) && chatbotResponse != null) {
            AtendimentoMensagem resposta = new AtendimentoMensagem();
            resposta.setAteCodigo(ateCodigo);
            resposta.setAmeBot(true);
            resposta.setAmeTexto(mensagemChatbot);
            atendimentoController.addMensagem(resposta, responsavel);
        }

        response.put("result", String.valueOf(result));
        response.put("redirect", String.valueOf(redirect));
        response.put("message", mensagemChatbot);

        return response;
    }

    private ChatbotResponseDTO detectIntentText(String text, String sessionId, String projectId) {
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            LOG.info("Session Path: " + session.toString());

            // Detect intents for each text input
            // Set the text (hello) and language code (en-US) for the query
            Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(LocaleHelper.getLocale());

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            // Display the query result
            QueryResult queryResult = response.getQueryResult();

            LOG.info("Query Text: " + queryResult.getQueryText());
            LOG.info("Detected Intent: " + queryResult.getIntent().getDisplayName() + " (confidence: " + queryResult.getIntentDetectionConfidence() + ")");
            LOG.info("Fulfillment Text: " + queryResult.getFulfillmentText());

            ChatbotResponseDTO responseDto = new ChatbotResponseDTO(queryResult.getFulfillmentText());

            if (queryResult.getIntent().getDisplayName().toLowerCase().indexOf("fallback") != -1 || queryResult.getAction().toLowerCase().indexOf("unknow") != -1) {
                responseDto.setFallback(true);
            }

            return responseDto;
        } catch (IOException ex) {
            LOG.error(ex);
            return null;
        }
    }
}
