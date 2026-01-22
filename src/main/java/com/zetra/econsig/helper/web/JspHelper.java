package com.zetra.econsig.helper.web;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

import org.springframework.http.HttpStatus;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.TransferObjectCache;
import com.zetra.econsig.helper.comunicacao.ControleComunicacaoPermitida;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto;
import com.zetra.econsig.helper.email.ControleEnvioEmail;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidor;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidorFactory;
import com.zetra.econsig.helper.limiteoperacao.RegraLimiteOperacaoCache;
import com.zetra.econsig.helper.log.ControleTipoEntidade;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.prazo.PrazoSvcCsa;
import com.zetra.econsig.helper.rede.DDNSAddress;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleAcessoSeguranca;
import com.zetra.econsig.helper.seguranca.ControleLogin;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.senhaexterna.ParamSenhaExternaHelper;
import com.zetra.econsig.helper.senhaexterna.SenhaExterna;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sistema.RecursoSistemaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.sistema.ViewImageHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.CertificadoDigital;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ParamEmailExternoServidorEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.ajuda.ChatbotRestController;
import com.zetra.econsig.webservice.rest.filter.IpWatchdog;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: JspHelper</p>
 * <p>Description: Helper Class para operações JSP.</p>
 * <p>Copyright: Copyright (c) 2005-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Alexandre Fernandes, Igor Lucas, Marcos Nolasco, Leonel Martins
 */
@SuppressWarnings({ "java:S107", "java:S1118", "java:S1192", "java:S3776" })
public class JspHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(JspHelper.class);

    public static final int LIMITE = 20;

    public static final int ESQ = 0;

    public static final int DIR = 1;

    public static final String CAPTCHA_FIELD = "captcha";

    public static final String ID_MSG_ERROR_SESSION = "idMsgErrorSession";

    public static final String ID_MSG_INFO_SESSION = "idMsgInfoSession";

    public static final String ID_MSG_SUCCESS_SESSION = "idMsgSuccessSession";

    /**
     * Método getNomeSistema
     * @param responsavel
     * @return XSS : Inseguro, tratar resultado conforme o contexto.
     */
    public static String getNomeSistema(AcessoSistema responsavel) {
        String nomeSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_NOME_SISTEMA, responsavel);
        if (nomeSistema == null) {
            nomeSistema = ApplicationResourcesHelper.getMessage("rotulo.titulo.sistema", responsavel);
        }
        return nomeSistema;
    }

    /**
     * Metodo para padronizar mensagens enviadas aos usuarios podendo ser do tipo:<br>
     * <br>@MSG_ERRO - Mensagem de erro
     * <br>@MSG_ALERT -  Mensagem de alerta
     * <br>@MSG_INFO - Mensagem de confirmação
     * <br><br>Que encontram-se gravadas na session
     * @param session - A session onde a mensagem esta gravada <br>
     * @param tableWidth - O tamanho da table no html <br>
     * @return - codigo html para o browser. XSS : Seguro pois trata o valor das entradas.
     * @deprecated
     */
    @Deprecated(since = "leiayte v4")
    public static String msgSession(HttpSession session, String tableWidth) {
        String result = "";
        if ((session.getAttribute(CodedValues.MSG_ERRO) != null) && !"".equals(session.getAttribute(CodedValues.MSG_ERRO).toString().trim())) {
            result += geraMsgGeral(session.getAttribute(CodedValues.MSG_ERRO).toString(), tableWidth, CodedValues.MSG_ERRO);
            session.setAttribute(CodedValues.MSG_ERRO, "");
        }
        if ((session.getAttribute(CodedValues.MSG_ALERT) != null) && !"".equals(session.getAttribute(CodedValues.MSG_ALERT).toString().trim())) {
            result += geraMsgGeral(session.getAttribute(CodedValues.MSG_ALERT).toString(), tableWidth, CodedValues.MSG_ALERT);
            session.setAttribute(CodedValues.MSG_ALERT, "");
        }
        if ((session.getAttribute(CodedValues.MSG_INFO) != null) && !"".equals(session.getAttribute(CodedValues.MSG_INFO).toString().trim())) {
            result += geraMsgGeral(session.getAttribute(CodedValues.MSG_INFO).toString(), tableWidth, CodedValues.MSG_INFO);
            session.setAttribute(CodedValues.MSG_INFO, "");
        }
        if ("".equals(result)) {
            return "<BR>";
        } else {
            return result;
        }
    }

    /**
     * Metodo para padronizar mensagens enviadas aos usuarios no novo padrão de layout <br>
     * podendo ser do tipo:<br>
     * @MSG_ERRO - Mensagem de erro <br>
     * @MSG_ALERT -  Mensagem de alerta <br>
     * @MSG_INFO - Mensagem de confirmação <br>
     * Que encontram-se gravadas na session <br>
     * @param session - A session onde a mensagem esta gravada <br>
     * @return - codigo html para o browser. XSS : Seguro pois trata o valor das entradas.
     */
    public static String msgSession(HttpSession session, boolean exibeIcone) {
        final StringBuilder result = new StringBuilder();
        if (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO))) {
            result.append(geraMsgGeral(session.getAttribute(CodedValues.MSG_ERRO).toString(), CodedValues.MSG_ERRO, exibeIcone));
            session.setAttribute(CodedValues.MSG_ERRO, "");
        }
        if (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_ALERT))) {
            result.append(geraMsgGeral(session.getAttribute(CodedValues.MSG_ALERT).toString(), CodedValues.MSG_ALERT, exibeIcone));
            session.setAttribute(CodedValues.MSG_ALERT, "");
        }
        if (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_ALERT_CONSULTAR_MARGEM))) {
            result.append(geraMsgGeral(session.getAttribute(CodedValues.MSG_ALERT_CONSULTAR_MARGEM).toString(), CodedValues.MSG_ALERT_CONSULTAR_MARGEM, exibeIcone));
            session.setAttribute(CodedValues.MSG_ALERT_CONSULTAR_MARGEM, "");
        }
        if (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_INFO))) {
            result.append(geraMsgGeral(session.getAttribute(CodedValues.MSG_INFO).toString(), CodedValues.MSG_INFO, exibeIcone));
            session.setAttribute(CodedValues.MSG_INFO, "");
        }
        return result.toString();
    }

    public static void addMsgSession(HttpSession session, String type, String message) {
        final Object allMessages = session.getAttribute(type);
        if (TextHelper.isNull(allMessages)) {
            session.setAttribute(type, message);
        } else {
            session.setAttribute(type, allMessages + "<BR><BR>" + message);
        }
    }

    /**
     * Método msgGenerica
     * @param msg
     * @param tam
     * @param tipo
     * @return XSS : Seguro pois trata o valor das entradas.
     * @deprecated
     */
    @Deprecated(since = "leiayte v4")
    public static String msgGenerica(String msg, String tam, String tipo) {
        return geraMsgGeral(msg, tam, tipo);
    }

    /**
     * Método geraMsgGeral
     * @param msg
     * @param tableWidth
     * @param tipo
     * @return - XSS : Seguro pois trata o valor das entradas.
     */
    @SuppressWarnings("java:S3358")
    private static String geraMsgGeral(String msg, String tableWidth, String tipo) {
        //selecionando o tipo de mensagem a ser exibida na pagina
        final String icone = CodedValues.MSG_ERRO.equals(tipo) ? "erro" : CodedValues.MSG_ALERT.equals(tipo) ? "aviso" : "info";

        final String idMsg = CodedValues.MSG_ERRO.equals(tipo) ? ID_MSG_ERROR_SESSION : CodedValues.MSG_ALERT.equals(tipo) ? ID_MSG_INFO_SESSION : ID_MSG_SUCCESS_SESSION;

        return "<table width=\"" + TextHelper.forHtmlAttribute(tableWidth) + "\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">" + "<tr>" + "<td width=\"40\"><IMG SRC=\"../img/icones/" + icone + ".gif\" BORDER=\"0\"></td>" + "<td><font id=\"" + idMsg + "\" class=\"" + icone + "\">" + TextHelper.forHtmlContentComTags(msg) + "</font></td>" + "</tr></table><BR>";
    }

    /**
     * Método gera mensagem geral no novo layout
     * @param msg
     * @param tipo
     * @return - XSS : Seguro pois trata o valor das entradas.
     */
    @SuppressWarnings("java:S3358")
    private static String geraMsgGeral(String msg, String tipo, boolean exibeIcone) {

        final String alerta = CodedValues.MSG_ERRO.equals(tipo) ? "alert-danger" : CodedValues.MSG_ALERT.equals(tipo) ? "alert-warning" : CodedValues.MSG_ALERT_CONSULTAR_MARGEM.equals(tipo) ? "alert-warning-consultar-margem" : "alert-success";

        final String icone = CodedValues.MSG_ERRO.equals(tipo) ? "fa-times-circle" : CodedValues.MSG_ALERT.equals(tipo) ? "fa-exclamation-triangle" : "fa-check-circle";

        final String idMsg = CodedValues.MSG_ERRO.equals(tipo) ? ID_MSG_ERROR_SESSION : CodedValues.MSG_ALERT.equals(tipo) ? ID_MSG_INFO_SESSION : ID_MSG_SUCCESS_SESSION;

        final StringBuilder mensagem = new StringBuilder();
        mensagem.append("<div class=\"alert ").append(alerta).append("\" role=\"alert\">");
        if (exibeIcone) {
            mensagem.append("<i class=\"fa ").append(icone).append(" fa-2x fa-stack\" aria-hidden=\"true\"><span class=\"d-none\">.</span></i>");
        }
        mensagem.append("<span id=\"").append(idMsg).append("\" ").append(">");
        mensagem.append(TextHelper.forHtmlContentComTags(msg));
        mensagem.append("</span>");
        mensagem.append("</div>");

        return mensagem.toString();
    }

    /**
     * Método verificaCampoNulo
     * @param request
     * @param nomeCampo
     * @return - XSS : Seguro pois retorna strings constantes que não dependem da entrada.
     */
    public static String verificaCampoNulo(HttpServletRequest request, String nomeCampo) {
        if ((request.getParameter("MM_update") != null) || (request.getParameter("MM_insert") != null)) {
            return (request.getParameter(nomeCampo) == null) || "".equals(request.getParameter(nomeCampo).trim()) ? "<SPAN class=\"MsgErro\">* " + ApplicationResourcesHelper.getMessage("rotulo.campo.obrigatorio", (AcessoSistema) null) + "</SPAN>" : "";
        }
        return "";
    }

    /**
     * Método verificaCamposForm
     * @param request
     * @param session
     * @param mmReqColumnsStr
     * @param msg
     * @param tableWidth
     * @return - XSS : Seguro, retorna string vazia ou resultado de geraMsgGeral() que é seguro.
     */
    @SuppressWarnings("java:S1172")
    public static String verificaCamposForm(HttpServletRequest request, HttpSession session, String mmReqColumnsStr, String msg, String tableWidth) {
        if ((request.getParameter("MM_update") != null) || (request.getParameter("MM_insert") != null)) {
            verificaCamposForm(request, mmReqColumnsStr, msg, tableWidth);
        }
        return "";
    }

    /**
     * Método verificaCamposForm
     * @param request
     * @param session
     * @param mmReqColumnsStr
     * @param msg
     * @param tableWidth
     * @return - XSS : Seguro, retorna string vazia ou resultado de geraMsgGeral() que é seguro.
     */
    public static String verificaCamposForm(HttpServletRequest request, String mmReqColumnsStr, String msg, String tableWidth) {
        final StringTokenizer tokens = new StringTokenizer(mmReqColumnsStr, "|");
        final String[] mmFields = new String[tokens.countTokens()];

        for (int i = 0; tokens.hasMoreTokens(); i++) {
            mmFields[i] = tokens.nextToken();
            mmFields[i] = request.getParameter(mmFields[i]) != null ? (String) request.getParameter(mmFields[i]) : "";
            if (mmFields[i].isBlank()) {
                return geraMsgGeral(msg, tableWidth, CodedValues.MSG_ERRO);
            }
        }
        return "";
    }

    /**
     * Método verificaCamposForm
     * @param request
     * @param requiredFields
     * @param errorMessages
     * @return - XSS : Inseguro, tratar resultado conforme contexto.
     */
    public static String verificaCamposForm(HttpServletRequest request, String[] requiredFields, String[] errorMessages) {
        if ((requiredFields != null) && (requiredFields.length > 0)) {
            for (int i = 0; i < requiredFields.length; i++) {
                if (TextHelper.isNull(verificaVarQryStr(request, requiredFields[i]))) {
                    return (errorMessages != null) && (errorMessages.length > i) ? errorMessages[i] : ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", (AcessoSistema) null);
                }
            }
        }
        return "";
    }

    /**
     * Retorna o valor de um campo enviado em uma requisição HTTP.
     * @param request
     * @param campo
     * @return - XSS : Inseguro, tratar resultado conforme contexto.
     */
    public static String verificaVarQryStr(HttpServletRequest request, String campo) {
        if (request.getParameter(campo) != null) {
            return request.getParameter(campo).trim();
        } else {
            return "";
        }
    }

    /**
     * Retorna o valor de um campo enviado em uma requisição HTTP. Caso a requisição
     * seja do tipo "multipart/form-data" o objeto UploadHelper deve ser utilizado
     * para recuperar os campos da requisição.
     * @param request
     * @param uploadHelper
     * @param campo
     * @return - XSS : Inseguro, tratar resultado conforme contexto.
     */
    public static String verificaVarQryStr(HttpServletRequest request, UploadHelper uploadHelper, String campo) {
        if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload()) {
            return uploadHelper.getValorCampoFormulario(campo);
        } else {
            return verificaVarQryStr(request, campo);
        }
    }

    /**
     * Recupera o valor de um parâmetro da requisição, seja normal ou multi-part sendo que o
     * parâmetro pode ter vários nomes distintos (segunda_senha)
     * @param request
     * @param uploadHelper
     * @param nomesCampo
     * @return
     */
    public static String[] obterParametrosRequisicao(HttpServletRequest request, UploadHelper uploadHelper, String[] nomesCampo) {
        String[] valores = null;

        for (final String nomeCampo : nomesCampo) {
            List<String> listaValores = null;
            if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload()) {
                listaValores = uploadHelper.getValoresCampoFormulario(nomeCampo);
            }
            if ((listaValores != null) && !listaValores.isEmpty() && !TextHelper.isNull(listaValores.get(0))) {
                valores = listaValores.toArray(new String[] {});
            } else {
                valores = request.getParameterValues(nomeCampo);
            }
            if ((valores != null) && (valores.length > 0) && !TextHelper.isNull(valores[0])) {
                break;
            }
        }

        return valores;
    }

    /** Recupera o valor de um campo, validando se o campo pode ser alterado.
     * @param request Requisição
     * @param fieldKey Chave do campo
     * @param oldValue Valor anterior do campo
     * @param converteVazioEmNulo Indica se deve transformar valor recuperado vazio em nulo.
     * @param responsavel
     * @return - XSS : Inseguro, tratar resultado conforme contexto.
     * @throws ZetraException
     */
    public static Object getFieldValue(HttpServletRequest request, String fieldKey, Object oldValue, boolean converteVazioEmNulo, AcessoSistema responsavel) throws ZetraException {
        final String requestValue = request.getParameter(fieldKey);

        // Se foi postado algum valor para o campo no form que não poderia ser editado.
        if ((requestValue != null) && !ShowFieldHelper.showField(fieldKey, responsavel)) {
            throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
        }

        if ((requestValue == null) || ShowFieldHelper.isDisabled(fieldKey, responsavel)) {
            return oldValue;
        }

        if ("".equals(requestValue.trim()) && converteVazioEmNulo) { // requestValue não é null
            return null;
        }

        return requestValue;
    }

    /**
     * Método getFieldValue
     * @param request
     * @param fieldKey
     * @param oldValue
     * @param responsavel
     * @return - XSS : Inseguro, tratar resultado conforme contexto.
     * @throws ZetraException
     */
    public static Object getFieldValue(HttpServletRequest request, String fieldKey, Object oldValue, AcessoSistema responsavel) throws ZetraException {
        return getFieldValue(request, fieldKey, oldValue, false, responsavel);
    }

    /**
     * Método msgRstVazio
     * @param rstVazio
     * @param colSpan
     * @param classe
     * @return - XSS : Seguro, entradas tratadas.
     */
    @Deprecated
    public static String msgRstVazio(boolean rstVazio, String colSpan, String classe) {
        //        String msg = "";
        //        if (rstVazio) {
        //            msg = "<TR CLASS=\"" + TextHelper.forHtmlAttribute(classe) + "\">";
        //            msg += "<TD COLSPAN=\"" + TextHelper.forHtmlAttribute(colSpan) + "\">" + ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null) + "</TD></TR>";
        //        }
        //        return msg;
        return msgRstVazio(rstVazio, Integer.parseInt(colSpan), AcessoSistema.getAcessoUsuarioSistema());
    }

    public static String msgRstVazio(boolean rstVazio, int colSpan, AcessoSistema responsavel) {
        String msg = "";
        if (rstVazio) {
            msg = "<tr><td colspan=\"" + colSpan + "\">" + ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", responsavel) + "</td></tr>";
        }
        return msg;
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, null, false, 1, null);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, false, 1, null);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, 1, null);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, null);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, selectedValue, null, false);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param onChange
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, String onChange) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, selectedValue, onChange, false);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param disabled
     * @return - XSS : Seguro, tratamento realizado.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, boolean disabled) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, selectedValue, null, disabled);
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param onChange
     * @param disabled
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, String onChange, boolean disabled) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, selectedValue, onChange, disabled, "Select");
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param onChange
     * @param disabled
     * @param cssClass
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, String onChange, boolean disabled, String cssClass) {
        return geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelect, size, selectedValue, onChange, disabled, cssClass, ";");
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param onChange
     * @param disabled
     * @param cssClass
     * @param selectedValueSeparator
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, String onChange, boolean disabled, String cssClass, String selectedValueSeparator) {

        final String[] fieldValues = fieldValue.split(";");
        final String[] fieldLabels = fieldLabel.split(";");
        final boolean selecionaOpcao = autoSelect && (content != null) && (content.size() == 1);

        final StringBuilder combo = new StringBuilder();

        if (size > 1) {
            combo.append("<SELECT NAME=\"").append(TextHelper.forHtmlAttribute(name)).append("\" id=\"").append(TextHelper.forHtmlAttribute(name)).append("\" MULTIPLE SIZE=\"").append(size).append("\" ");
        } else {
            combo.append("<SELECT NAME=\"").append(TextHelper.forHtmlAttribute(name)).append("\" id=\"").append(TextHelper.forHtmlAttribute(name)).append("\" ");
        }
        if (others != null) {
            combo.append(others);
        }

        if (disabled) {
            combo.append(" disabled");
        }

        combo.append(" class=\"").append(cssClass).append(" form-select").append("\" ");
        combo.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        if (!TextHelper.isNull(onChange)) {
            combo.append(" onChange=\"").append(onChange).append("\"");
        }
        combo.append(" onBlur=\"fout(this);ValidaMascara(this);\"> ");

        if (!TextHelper.isNull(notSelectedLabel)) {
            combo.append("<OPTION VALUE=\"\"" + (selecionaOpcao || !TextHelper.isNull(selectedValue) ? "" : " SELECTED") + ">").append(TextHelper.forHtmlContent(notSelectedLabel)).append("</OPTION> ");
        }

        final String codigoSelectedValue = !TextHelper.isNull(selectedValue) && selectedValue.contains(";") ? selectedValue.split(";")[0] : selectedValue;

        if (content != null) {
            TransferObject row = null;
            final StringBuilder txtLabel = new StringBuilder();
            final StringBuilder value = new StringBuilder();
            final int labelMaxSize = 80;

            for (final TransferObject element : content) {
                row = element;

                Object valueFirstField = "";
                // Concatena todos os valores do select
                value.setLength(0);
                for (final String fieldValue2 : fieldValues) {
                    if (TextHelper.isNull(valueFirstField)) {
                        valueFirstField = row.getAttribute(fieldValue2);
                    }
                    value.append(row.getAttribute(fieldValue2)).append(";");
                }
                // Apaga último separador
                value.deleteCharAt(value.length() - 1);

                combo.append("<OPTION VALUE=\"").append(TextHelper.forHtmlAttribute(value)).append("\"");

                if (((selectedValue != null) && selectedValue.equals(value.toString())) || selecionaOpcao) {
                    combo.append(" SELECTED");
                } else if (!TextHelper.isNull(codigoSelectedValue) && codigoSelectedValue.equals(valueFirstField.toString())) {
                    combo.append(" SELECTED");
                } else if (selectedValue != null) {
                    final String[] selectedValues = selectedValue.split(selectedValueSeparator);
                    for (final String singleValue : selectedValues) {
                        if (singleValue.equals(value.toString()) || valueFirstField.equals(singleValue.split(";")[0])) {
                            combo.append(" SELECTED");
                        }
                    }
                }

                combo.append(">");

                txtLabel.setLength(0);
                for (int i = 0; i < fieldLabels.length; i++) {
                    txtLabel.append(row.getAttribute(fieldLabels[i]));
                    if (i < (fieldLabels.length - 1)) {
                        txtLabel.append(" - ");
                    }
                }
                if (txtLabel.length() > labelMaxSize) {
                    txtLabel.setLength(labelMaxSize - 4);
                    txtLabel.append(" ...");
                }
                combo.append(TextHelper.forHtmlAttribute(txtLabel));
                combo.append("</OPTION>");
            }
        }

        combo.append("</SELECT>");

        return combo.toString();
    }

    /**
     * Método geraComboUF
     * @param nomeCampo
     * @param valorCampo
     * @param desabilitado
     * @param responsavel
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraComboUF(String nomeCampo, String valorCampo, boolean desabilitado, AcessoSistema responsavel) {
        return geraComboUF(nomeCampo, valorCampo, desabilitado, null, responsavel);
    }

    public static String geraComboUF(String nomeCampo, String valorCampo, boolean desabilitado, String css, AcessoSistema responsavel) {
        return geraComboUF(nomeCampo, null, valorCampo, desabilitado, css, responsavel);
    }

    public static String geraComboUF(String nomeCampo, String idCampo, String valorCampo, boolean desabilitado, String css, AcessoSistema responsavel) {
        final StringBuilder combo = new StringBuilder();
        css = TextHelper.isNull(css) ? "Select" : css;

        combo.append("<select name=\"").append(TextHelper.forHtmlAttribute(nomeCampo)).append("\" ");

        if (idCampo != null) {
            combo.append(" id=\"").append(idCampo).append("\" ");
        }

        combo.append(" class=\"").append(css).append(" form-select").append("\"");
        combo.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
        combo.append(" onBlur=\"fout(this);ValidaMascara(this);\"");
        combo.append(desabilitado ? " disabled>" : ">");

        if ((valorCampo == null) || "".equals(valorCampo.trim())) {
            combo.append("<option value=\"\" selected>");
        } else {
            combo.append("<option value=\"\">");
        }
        combo.append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("</option>");
        try {
            final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            final List<TransferObject> listUf = sistemaController.lstUf(responsavel);
            for (final TransferObject uf : listUf) {
                combo.append("<option value=\"");
                combo.append(TextHelper.forHtmlAttribute(uf.getAttribute(Columns.UF_COD)));
                if (uf.getAttribute(Columns.UF_COD).equals(valorCampo)) {
                    combo.append("\" selected>");
                } else {
                    combo.append("\">");
                }
                combo.append(TextHelper.forHtmlContent(uf.getAttribute(Columns.UF_NOME)));
                combo.append("</option>");
            }
        } catch (final ConsignanteControllerException e) {
            LOG.error("Não foi possível carregar o combo de UF (USU_COD:" + responsavel + ").", e);
        }

        combo.append("</select>");
        return combo.toString();
    }

    public static String geraArrayBancos(AcessoSistema responsavel) {
        return geraArrayBancos(false, responsavel);
    }

    public static String geraArrayBancos(boolean identificadorComoChave, AcessoSistema responsavel) {
        final boolean manutencaoCsaUsaCRM = ParamSist.getBoolParamSist(CodedValues.TPC_MANUTENCAO_CSA_UTILIZA_CRM, responsavel);
        final StringBuilder arBancos = new StringBuilder();
        arBancos.append("[[\"\", \"").append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("\"]");

        try {
            final String campoChave = identificadorComoChave ? Columns.BCO_IDENTIFICADOR : Columns.BCO_CODIGO;

            // Carrega o combo de bancos
            final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            final List<TransferObject> listaBancos = sistemaController.pesquisarBancos(responsavel);
            for (final TransferObject ctoBanco : listaBancos) {
                // Ignora o banco não informado na manutenção de consignatária
                if (manutencaoCsaUsaCRM && "000".equals(ctoBanco.getAttribute(Columns.BCO_IDENTIFICADOR).toString())) {
                    continue;
                }

                arBancos.append(",[\"").append(TextHelper.forJavaScriptBlock(ctoBanco.getAttribute(campoChave)));
                arBancos.append("\", \"").append(TextHelper.forJavaScriptBlock(ctoBanco.getAttribute(Columns.BCO_DESCRICAO)));
                arBancos.append(" - ").append(TextHelper.forJavaScriptBlock(ctoBanco.getAttribute(Columns.BCO_IDENTIFICADOR)));
                arBancos.append("\"]");
            }

        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        arBancos.append("]");

        return arBancos.toString();
    }

    /**
     * Método gera combo com 5 opções de bancos, sendo 1 delas o do servidor real e X (default 4) aleatórias
     * @param identificadorComoChave
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    public static String geraArrayBancosParaValidacaoPerguntas(String bcoCodigoPrincipal, Integer nAlternativas, AcessoSistema responsavel) throws ZetraException {

        final int bancosAlternativos = nAlternativas != null ? nAlternativas : 5;
        final List<TransferObject> bancos = new ArrayList<>();

        final StringBuilder arBancos = new StringBuilder();
        arBancos.append("[[\"\", \"").append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("\"]");

        // Carrega o combo de bancos
        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        final List<TransferObject> listaBancos = sistemaController.pesquisarBancos(responsavel);

        final Optional<TransferObject> optional = listaBancos.stream().filter(banco -> banco.getAttribute(Columns.BCO_CODIGO).toString().equals(bcoCodigoPrincipal)).findFirst();
        if (!optional.isPresent()) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        }

        bancos.add(optional.get());

        do {
            int randomNumber = 0;
            try {
                randomNumber = NumberHelper.getRandomNumber(listaBancos.size() - 1, Integer.parseInt(optional.get().getAttribute(Columns.BCO_CODIGO).toString()));
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new ZetraException("mensagem.erro.interno.questionario.servidor", responsavel);
            }

            final TransferObject banco = listaBancos.get(randomNumber);
            listaBancos.removeIf(b -> b.getAttribute(Columns.BCO_CODIGO).toString().equals(banco.getAttribute(Columns.BCO_CODIGO).toString()));

            if (!bancos.contains(banco)) {
                bancos.add(banco);
            }
        } while (bancos.size() < bancosAlternativos);

        Collections.shuffle(bancos);

        for (final TransferObject bancoSorteado : bancos) {
            arBancos.append(",[\"").append(TextHelper.forJavaScriptBlock(bancoSorteado.getAttribute(Columns.BCO_CODIGO)));
            arBancos.append("\", \"").append(TextHelper.forJavaScriptBlock(bancoSorteado.getAttribute(Columns.BCO_DESCRICAO)));
            arBancos.append(" - ").append(TextHelper.forJavaScriptBlock(bancoSorteado.getAttribute(Columns.BCO_IDENTIFICADOR)));
            arBancos.append("\"]");
        }

        arBancos.append("]");

        return arBancos.toString();
    }

    /**
     * Método geraCamposHidden
     * @param queryString
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraCamposHidden(String queryString) {
        final StringBuilder html = new StringBuilder();

        if ((queryString != null) && !queryString.isBlank()) {
            final String[] parametros = queryString.split("&");
            for (final String parametro : parametros) {
                final String[] valores = parametro.split("=");
                if ((valores != null) && (valores.length > 0)) {
                    final String nome = valores[0].substring(Math.max(valores[0].indexOf('?') + 1, 0));
                    final String valor = valores.length > 1 ? valores[1] : "";
                    html.append("\n<input type=\"hidden\" name=\"").append(TextHelper.forHtmlAttribute(nome)).append("\" value=\"").append(TextHelper.forHtmlAttribute(valor)).append("\">");
                }
            }
        }

        return html.toString();
    }

    /**
     * Método geraLayerDeEspera
     * @param nomeLayer
     * @param mensagem
     * @param top
     * @param left
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraLayerDeEspera(String nomeLayer, String mensagem, String top, String left) {
        if ((top == null) || "".equals(top)) {
            top = "0px";
        }
        if ((left == null) || "".equals(left)) {
            left = "0px";
        }
        final StringBuilder retorno = new StringBuilder();
        retorno.append("<DIV ID=\"").append(TextHelper.forHtmlAttribute(nomeLayer)).append("\" CLASS=\"fundo\" STYLE=\"visibility: hidden; position:absolute; left: ").append(TextHelper.forHtmlAttribute(left)).append("; top: ").append(TextHelper.forHtmlAttribute(top)).append("; width: 70%; height: 71px;\">");
        retorno.append("<TABLE WIDTH=\"100%\" BORDER=\"0\" ALIGN=\"center\" CELLPADDING=\"0\" CELLSPACING=\"1\" CLASS=\"TabelaEntradaDeDados\">");
        retorno.append("<TR VALIGN=\"baseline\"><TD ALIGN=\"CENTER\" CLASS=\"CEDtopo\"><BR>");
        retorno.append("<SPAN CLASS=\"MsgOK\"><IMG SRC=\"../img/hourglass.gif\" WIDTH=\"32\" HEIGHT=\"32\" ALIGN=\"ABSMIDDLE\">").append(TextHelper.forHtmlContentComTags(mensagem));
        retorno.append("&nbsp;<BR><BR></TD></TR></TABLE></DIV>");

        return retorno.toString();
    }

    public static String gerarLinhaTabela(String descricao, Object valor) {
        return gerarLinhaTabela(descricao, valor, null, "TLEDmeio", "CEDmeio");
    }

    public static String gerarLinhaTabela(String descricao, Object valor, String descricaoCss, String valorCss) {
        return gerarLinhaTabela(descricao, valor, null, descricaoCss, valorCss);
    }

    public static String gerarLinhaTabelaTooltip(String descricao, Object valor, String descricaoCss, String valorCss, String tooltip) {
        return gerarLinhaTabelaTooltip(descricao, valor, null, descricaoCss, valorCss, tooltip);
    }

    /**
     * gerarLinhaTabela - XSS : Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param textoAjuda - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @return - XSS : Não é seguro, tratar entradas conforme sugerido.
     */
    public static String gerarLinhaTabela(String descricao, Object valor, String textoAjuda, String descricaoCss, String valorCss) {
        descricaoCss = TextHelper.isNull(descricaoCss) ? "TLEDmeio" : descricaoCss;
        valorCss = TextHelper.isNull(valorCss) ? "CEDmeio" : valorCss;

        // Não tentar proteger de XSS neste código pois descrição e valor podem ter HTML (Usário servidor, simular, selecionar CSA, tela de confirmacao.jsp).
        if ((descricao != null) && (valor != null) && !TextHelper.isNull(textoAjuda)) {
            return "<tr valign=\"baseline\">\n" + "  <td class=\"" + descricaoCss + "\" align=\"right\">" + descricao + ":&nbsp;</td>\n" + "  <td class=\"" + valorCss + "\">&nbsp;" + valor.toString() + "</td>\n" + "  <td class=\"" + valorCss + "\" style=\"border-left:1px; font-style:italic\">" + textoAjuda + "</td>" + "</tr>\n";

        } else if ((descricao != null) && (valor != null)) {
            return "<tr valign=\"baseline\">\n" + "  <td class=\"" + descricaoCss + "\" align=\"right\">" + descricao + ":&nbsp;</td>\n" + "  <td class=\"" + valorCss + "\">&nbsp;" + valor.toString() + "</td>\n" + "</tr>\n";

        } else {
            final String css = descricao != null ? descricaoCss : valorCss;
            final String text = descricao != null ? descricao : valor.toString();

            return "<tr valign=\"baseline\">\n" + "  <td colspan=\"2\" class=\"" + css + "\" align=\"center\">" + TextHelper.forHtmlContentComTags(text) + "</td>\n" + "</tr>\n";
        }
    }

    /**
     * gerarLinhaTabela - XSS : Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param textoAjuda - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @param tooltip
     * @return - XSS : Não é seguro, tratar entradas conforme sugerido.
     */
    public static String gerarLinhaTabelaTooltip(String descricao, Object valor, String textoAjuda, String descricaoCss, String valorCss, String tooltip) {
        descricaoCss = TextHelper.isNull(descricaoCss) ? "TLEDmeio" : descricaoCss;
        valorCss = TextHelper.isNull(valorCss) ? "CEDmeio" : valorCss;

        // Não tentar proteger de XSS neste código pois descrição e valor podem ter HTML (Usário servidor, simular, selecionar CSA, tela de confirmacao.jsp).
        if ((descricao != null) && (valor != null) && !TextHelper.isNull(textoAjuda)) {
            return "<tr valign=\"baseline\">\n" + "  <td class=\"" + descricaoCss + "\" align=\"right\">" + descricao + "&nbsp;<i class=\"fa fa-chevron-down tooltip\" aria-hidden=\"true\"><span class=\"tooltiptext\">" + tooltip + "</span></i>:&nbsp;</td>\n" + "  <td class=\"" + valorCss + "\">&nbsp;" + valor.toString() + "</td>\n" + "  <td class=\"" + valorCss + "\" style=\"border-left:1px; font-style:italic\">" + textoAjuda + "</td>" + "</tr>\n";

        } else if ((descricao != null) && (valor != null)) {
            return "<tr valign=\"baseline\">\n" + "  <td class=\"" + descricaoCss + "\" align=\"right\">" + descricao + "&nbsp;<i class=\"fa fa-chevron-down tooltip\" aria-hidden=\"true\"><span class=\"tooltiptext\">" + tooltip + "</span></i>:&nbsp;</td>\n" + "  <td class=\"" + valorCss + "\">&nbsp;" + valor.toString() + "</td>\n" + "</tr>\n";

        } else {
            final String css = descricao != null ? descricaoCss : valorCss;
            final String text = descricao != null ? descricao : valor.toString();

            return "<tr valign=\"baseline\">\n" + "  <td colspan=\"2\" class=\"" + css + "\" align=\"center\">" + TextHelper.forHtmlContentComTags(text) + "</td>\n" + "</tr>\n";
        }
    }

    public static String gerarLinhaTabelaTooltipv4(String descricao, Object valor, String textoAjuda, String descricaoCss, String valorCss, String tooltip) {
        descricaoCss = TextHelper.isNull(descricaoCss) ? "col-6" : descricaoCss;
        valorCss = TextHelper.isNull(valorCss) ? "col-6" : valorCss;
        final String rotuloAjuda = ApplicationResourcesHelper.getMessage("rotulo.ajuda", AcessoSistema.getAcessoUsuarioSistema());

        // Não tentar proteger de XSS neste código pois descrição e valor podem ter HTML (Usário servidor, simular, selecionar CSA, tela de confirmacao.jsp).
        if ((descricao != null) && (valor != null) && !TextHelper.isNull(textoAjuda)) {
            final StringBuilder code = new StringBuilder();
            code.append("<dt class=\"d-flex justify-content-end align-items-center " + descricaoCss + "\">" + descricao);
            code.append("<a href=\"#no-back\" class=\"btn-i-right pr-1\" data-bs-toggle=\"popover\" title=\"").append(rotuloAjuda).append("\" ");
            code.append("onClick=\"javascript: return false;\" ");
            code.append("data-bs-content=\"").append(tooltip).append("\" ");
            code.append("data-original-title=\"").append(rotuloAjuda).append("\">");
            code.append("<span class=\"question-icon\" >");
            code.append("<svg data-bs-toggle=\"").append(tooltip).append("\">");
            code.append("<use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#i-question\"");
            code.append("</use>");
            code.append("</svg>");
            code.append("</span>");
            code.append("</a>");

            code.append(":</dt>\n");
            code.append("<dd class=\"" + valorCss + "\">&nbsp;" + valor.toString() + "</dd>\n");

            return code.toString();

        } else if ((descricao != null) && (valor != null)) {
            final StringBuilder code = new StringBuilder();
            code.append("<dt class=\"d-flex justify-content-end align-items-center " + descricaoCss + "\">").append(descricao);
            code.append("<a href=\"#no-back\" class=\"btn-i-right pr-1\" data-bs-toggle=\"popover\" title=\"").append(rotuloAjuda).append("\" ");
            code.append("onClick=\"javascript: return false;\" ");
            code.append("data-bs-content=\"").append(tooltip).append("\" ");
            code.append("data-original-title=\"").append(rotuloAjuda).append("\">");
            code.append("<span class=\"question-icon\" >");
            code.append("<svg data-bs-toggle=\"").append(tooltip).append("\">");
            code.append("<use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#i-question\"");
            code.append("</use>");
            code.append("</svg>");
            code.append("</span>");
            code.append("</a>");
            code.append(":</dt>\n");
            code.append("<dd class=\"" + valorCss + "\">" + valor.toString() + "</dd>\n");
            return code.toString();

        } else {
            final String css = descricao != null ? descricaoCss : valorCss;
            final String text = descricao != null ? descricao : valor.toString();

            return "<dt class=\"" + descricaoCss + "\">" + "  <dd class=\"" + css + "\">" + TextHelper.forHtmlContentComTags(text) + "</dd>\n";
        }
    }

    public static String gerarLinhaTabelav4(String descricao, Object valor, String textoAjuda, String descricaoCss, String valorCss) {
        return gerarLinhaTabelav4(descricao, valor, textoAjuda, descricaoCss, valorCss, true);
    }

    public static String gerarLinhaTabelav4(String descricao, Object valor, String textoAjuda, String descricaoCss, String valorCss, boolean incluirLabel) {
        descricaoCss = TextHelper.isNull(descricaoCss) ? "col-6" : descricaoCss;
        valorCss = TextHelper.isNull(valorCss) ? "col-6" : valorCss;

        // Não tentar proteger de XSS neste código pois descrição e valor podem ter HTML (Usário servidor, simular, selecionar CSA, tela de confirmacao.jsp).
        if ((descricao != null) && (valor != null) && !TextHelper.isNull(textoAjuda)) {
            return gerarLinhaTabela(descricao, valor, textoAjuda, descricaoCss, valorCss);
        } else if ((descricao != null) && (valor != null)) {
            return "<dt class=\"" + descricaoCss + "\">" + (incluirLabel ? descricao + ":</dt>\n" : "") + "<dd class=\"" + valorCss + "\">" + valor.toString() + "</dd>\n";
        } else {
            return gerarLinhaTabela(descricao, valor, textoAjuda, descricaoCss, valorCss);
        }
    }

    /**
     * Método montaValor
     * @param nome
     * @param dominio
     * @param valor
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String montaValor(String nome, String dominio, String valor) {
        return montaValor(nome, dominio, valor, true, null);
    }

    /**
     * Método montaValor
     * @param nome
     * @param dominio
     * @param valor
     * @param habilitado
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String montaValor(String nome, String dominio, String valor, boolean habilitado) {
        return montaValor(nome, dominio, valor, habilitado, null);
    }

    /**
     * Método montaValor
     * @param nome
     * @param dominio
     * @param valor
     * @param habilitado
     * @param onClick
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String montaValor(String nome, String dominio, String valor, boolean habilitado, String onClick) {
        return montaValor(nome, dominio, valor, habilitado, onClick, -1, -1);
    }

    /**
     * Método montaValor
     * @param nome
     * @param dominio
     * @param valor
     * @param habilitado
     * @param onClick
     * @param tamanho
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String montaValor(String nome, String dominio, String valor, boolean habilitado, String onClick, int tamanho, int qtdMaxCaracteres) {
        return montaValor(nome, dominio, valor, habilitado, onClick, tamanho, qtdMaxCaracteres, "Edit", null, null);
    }

    public static String montaValor(String nome, String dominio, String valor, boolean habilitado, String onClick, int tamanho, int qtdMaxCaracteres, String cssClass, String placeHolder, String descricao) {
        final StringBuilder ret = new StringBuilder("");
        final boolean layoutNovo = !TextHelper.isNull(cssClass) && "form-control".equals(cssClass);

        if (dominio.startsWith("ESCOLHA") || dominio.startsWith("SELECAO")) {
            // ESCOLHA e SELECAO são campos de escolha única, onde o primeiro forma "radiobox" e o segundo um "selectbox", ex:
            // ESCOLHA[1;2;3;4]
            // ESCOLHA[1=Fraco;2=Médio;3=Forte;4=Muito Forte]
            // ESCOLHA[1=SIM;0=NÃO]
            // SELECAO[D=Dia do Mês;S=Dia da Semana;U=Dias Úteis]
            if (dominio.startsWith("SELECAO")) {
                // Abre o campo "select", com a opção vazia
                ret.append("<select class=\"form-select\" name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (habilitado ? "" : "disabled") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : ""));
                if (layoutNovo) {
                    ret.append(" onFocus=\"SetarEventoMascaraV4(this,'#*200',true);\"");
                } else {
                    ret.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
                }

                ret.append(" onBlur=\"fout(this);\">" + "<option value=\"\" " + (TextHelper.isNull(valor) ? "selected" : "") + ">" + ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", (AcessoSistema) null) + "</option>");
            }
            final String[] opcoes = dominio.substring(dominio.indexOf('[') + 1, dominio.indexOf(']')).split(";");
            for (final String opcoe : opcoes) {
                String nomeOpcao = opcoe;
                String valorOpcao = opcoe;
                if (opcoe.indexOf('=') != -1) {
                    valorOpcao = opcoe.substring(0, opcoe.indexOf('='));
                    nomeOpcao = opcoe.substring(opcoe.indexOf('=') + 1);
                }

                if (dominio.startsWith("SELECAO")) {
                    ret.append("<option value=\"" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "\" " + (valor.equals(valorOpcao.trim()) ? "selected" : "") + ">" + TextHelper.forHtmlContent(nomeOpcao.trim()) + "</option>");
                } else {
                    ret.append("<div class=\"form-check form-check-inline\">");
                    ret.append("<input name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (habilitado ? "" : "disabled") + " type=\"radio\" value=\"" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "\" " + (valor.equals(valorOpcao.trim()) ? "checked" : "") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : ""));
                    if (layoutNovo) {
                        ret.append(" id=\"").append(TextHelper.forHtmlAttribute(nome) + TextHelper.forHtmlAttribute(nomeOpcao.trim())).append("\"");
                        ret.append(" class=\"form-check-input ml-1\"");
                        ret.append(" onFocus=\"SetarEventoMascaraV4(this,'#*200',true);\"");
                    } else {
                        ret.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
                    }

                    ret.append(" onBlur=\"fout(this);\">" + (layoutNovo ? "" : TextHelper.forHtmlContent(nomeOpcao.trim())));

                    if (layoutNovo) {
                        ret.append("<label class=\"form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top\"");
                        if (!TextHelper.isNull(descricao)) {
                            ret.append(" aria-label=\"").append(descricao).append("\"");
                        }
                        ret.append(" for=\"").append(TextHelper.forHtmlAttribute(nome) + TextHelper.forHtmlAttribute(nomeOpcao.trim())).append("\">" + TextHelper.forHtmlContent(nomeOpcao.trim()) + "</label>");
                    }
                    ret.append("</div>");
                }
            }
            if (dominio.startsWith("SELECAO")) {
                // Fecha o campo "select"
                ret.append("</select>");
            }

        } else if ("SN".equals(dominio)) {
            final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", (AcessoSistema) null);
            final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", (AcessoSistema) null);
            ret.append("<div class=\"form-check form-check-inline\">");
            // SN: campo "radiobox" de escolha única entre Sim(S) e Não(N)
            ret.append("<input name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (habilitado ? "" : "disabled") + " type=\"radio\" value=\"S\" ");
            if (layoutNovo) {
                ret.append(" id=\"").append(TextHelper.forHtmlAttribute(nome + rotuloSim)).append("\"");
                ret.append(" class=\"form-check-input\"");
            }
            ret.append(("S".equals(valor) ? "checked" : "") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : ""));
            if (layoutNovo) {
                ret.append(" onFocus=\"SetarEventoMascaraV4(this,'#*200',true);\"");
            } else {
                ret.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
            }
            ret.append(" onBlur=\"fout(this);\"/>");

            if (layoutNovo) {
                ret.append("<label class=\"form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top\"");
                if (!TextHelper.isNull(descricao)) {
                    ret.append(" aria-label=\"").append(descricao).append("\"");
                }
                ret.append(" for=\"").append(TextHelper.forHtmlAttribute(nome + rotuloSim)).append("\">");
            }
            ret.append(rotuloSim);
            if (layoutNovo) {
                ret.append("</label>");
            }
            ret.append("</div>");
            ret.append(" <div class=\"form-check form-check-inline\">");

            ret.append("<input name=\"" + TextHelper.forHtmlAttribute(nome) + "\" " + (habilitado ? "" : "disabled") + " type=\"radio\" value=\"N\" ");
            if (layoutNovo) {
                ret.append(" id=\"").append(TextHelper.forHtmlAttribute(nome + rotuloNao)).append("\"");
                ret.append(" class=\"form-check-input\"");
            }
            ret.append(("N".equals(valor) ? "checked" : "") + (onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : ""));

            if (layoutNovo) {
                ret.append(" onFocus=\"SetarEventoMascaraV4(this,'#*200',true);\"");
            } else {
                ret.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
            }

            ret.append(" onBlur=\"fout(this);\">");

            if (layoutNovo) {
                ret.append("<label class=\"form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top\"");
                if (!TextHelper.isNull(descricao)) {
                    ret.append(" aria-label=\"").append(descricao).append("\"");
                }
                ret.append(" for=\"").append(TextHelper.forHtmlAttribute(nome + rotuloNao)).append("\">");
            }
            ret.append(rotuloNao);
            if (layoutNovo) {
                ret.append("</label>");
            }
            ret.append("</div>");
        } else {
            // Campos de tipo "text" que possuem máscara e tamanho definidos pelo domínio:
            // DATA: DD/DD/DDDD
            // ANOMES: DDDDDD
            // MES: DD (entre 1 e 12)
            // DIA: DD (entre 1 e 31)
            // INT: #D4
            // MONETARIO ou FLOAT: #F20
            // ALFA: #*255

            String mask = "";
            String maxlength = "";
            String onblur;

            if (layoutNovo) {
                onblur = "fout(this);ValidaMascaraV4(this);";
            } else {
                onblur = "fout(this);ValidaMascara(this);";
            }

            String size = "";
            switch (dominio) {
                case "DATA":
                    mask = LocaleHelper.getDateJavascriptPattern();
                    size = tamanho <= 0 ? "11" : String.valueOf(tamanho);
                    maxlength = "10";
                    break;
                case "ANOMES":
                    mask = "DDDDDD";
                    size = tamanho <= 0 ? "8" : String.valueOf(tamanho);
                    maxlength = "6";
                    break;
                case "MES":
                    mask = "DD";
                    size = tamanho <= 0 ? "2" : String.valueOf(tamanho);
                    maxlength = "2";
                    onblur += "if (this.value != '' && (isNaN(this.value) || this.value<01 || this.value>12)) { alert('" + ApplicationResourcesHelper.getMessage("mensagem.intervalo.permitido.mes", (AcessoSistema) null) + "'); this.focus(); return false; }";
                    break;
                case "DIA":
                    mask = "DD";
                    size = tamanho <= 0 ? "2" : String.valueOf(tamanho);
                    maxlength = "2";
                    onblur += "if (this.value != '' && (isNaN(this.value) || this.value<01 || this.value>31)) { alert('" + ApplicationResourcesHelper.getMessage("mensagem.intervalo.permitido.dia", (AcessoSistema) null) + "'); this.focus(); return false; }";
                    break;
                case "INT":
                    size = tamanho <= 0 ? "10" : String.valueOf(tamanho);
                    maxlength = qtdMaxCaracteres <= 0 ? "4" : String.valueOf(qtdMaxCaracteres);
                    mask = "#D" + maxlength;
                    break;
                case "MONETARIO":
                case "FLOAT":
                    // TODO: Monetário deve aceitar apenas 2 casas decimais (ok) porém float deveria aceitar mais que 2
                    size = tamanho <= 0 ? "10" : String.valueOf(tamanho);
                    maxlength = qtdMaxCaracteres <= 0 ? "20" : String.valueOf(qtdMaxCaracteres);
                    mask = "#F" + maxlength;
                    onblur = "if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" + onblur;
                    if (!TextHelper.isNull(valor)) {
                        try {
                            valor = NumberHelper.reformat(valor, "en", NumberHelper.getLang());
                        } catch (final ParseException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                    break;
                case "ALFA":
                    size = tamanho <= 0 ? "60" : String.valueOf(tamanho);
                    maxlength = qtdMaxCaracteres <= 0 ? "255" : String.valueOf(qtdMaxCaracteres);
                    mask = "#*" + maxlength;
                    break;
                case null:
                default:
                    break;
            }

            if (TextHelper.isNull(valor)) {
                valor = "";
            }

            // Tratamento de XSS removido da variável "onblur" pois estava dando erro com os operadores de javascript && e outros. Não cria
            // nenhum problema de segurança visto que este atributo é definido localmente apenas.
            ret.append("<input type=\"text\" " + (habilitado ? "" : "disabled"));
            ret.append(" class=\"" + cssClass + "\"");
            ret.append(" name=\"" + TextHelper.forHtmlAttribute(nome) + "\"");
            ret.append(" id=\"" + TextHelper.forHtmlAttribute(nome) + "\"");
            ret.append(" value=\"" + TextHelper.forHtmlAttribute(valor) + "\"");
            ret.append(" size=\"" + TextHelper.forHtmlAttribute(size) + "\"");
            ret.append(" maxlength=\"" + TextHelper.forHtmlAttribute(maxlength) + "\"");
            ret.append(onClick != null ? " onClick=\"" + TextHelper.forJavaScriptAttribute(onClick) + "\"" : "");
            if (layoutNovo) {
                ret.append(" onFocus=\"SetarEventoMascaraV4(this,'" + TextHelper.forJavaScriptAttribute(mask) + "',true);\"");
            } else {
                ret.append(" onFocus=\"SetarEventoMascara(this,'" + TextHelper.forJavaScriptAttribute(mask) + "',true);\"");
            }
            ret.append(" onBlur=\"" + onblur + "\"");
            if (!TextHelper.isNull(placeHolder)) {
                ret.append(" placeHolder=\"").append(ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", AcessoSistema.getAcessoUsuarioSistema(), placeHolder.toLowerCase())).append("\"");
            }
            ret.append(">");
        }

        return ret.toString();
    }

    /**
     * Recupera um valor do request e prepara este para estar no formato correto de gravá-lo no banco de dados
     * @param request
     * @param campo
     * @param dominio
     * @return
     */
    public static String parseValor(HttpServletRequest request, UploadHelper uploadHelper, String campo, String dominio) {
        String valor = verificaVarQryStr(request, uploadHelper, campo);
        if (!TextHelper.isNull(valor)) {
            if ("MONETARIO".equals(dominio) || "FLOAT".equals(dominio)) {
                try {
                    valor = NumberHelper.reformat(valor, NumberHelper.getLang(), "en");
                } catch (final ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            return valor;
        } else {
            return "";
        }
    }

    /**
     * Formata a observação das ocorrências de consignação para serem exibidas
     * na tela de detalhe. Transforma os textos em maiúsculo, com exceção das
     * tags de links que possam existir.
     * @param oca
     * @return - XSS : Provavelmente seguro, Usa TextHelper.forHtmlContentComTags() exceto sobre a tag <a href=...>...</a>
     */
    public static String formataMsgOca(String oca) {
        if (oca == null) {
            return "";
        }
        int offSet = 0;
        int salto = oca.toLowerCase().indexOf("<a", offSet);
        final StringBuilder msg = new StringBuilder();
        if (salto == -1) {
            return TextHelper.forHtmlContentComTags(oca.toUpperCase());
        } else {
            while (salto >= 0) {
                msg.append(TextHelper.forHtmlContentComTags(oca.substring(offSet, salto).toUpperCase()));
                offSet = salto;
                salto = oca.toLowerCase().indexOf("</a>", offSet);
                if (salto >= 0) {
                    salto += 4;
                    msg.append(oca.substring(offSet, salto));
                    offSet = salto;
                    salto = oca.toLowerCase().indexOf("<a", offSet);
                }
            }
            if (msg.length() < oca.length()) {
                msg.append(TextHelper.forHtmlContentComTags(oca.substring(msg.length(), oca.length()).toUpperCase()));
            }
            return msg.toString();
        }
    }

    public static boolean isThisIpAddressLocal(String ipAddr) {
        InetAddress addr = null;

        try {
            addr = InetAddress.getByName(ipAddr);
        } catch (final UnknownHostException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }

        // Check if the address is a valid special local or loop back
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return true;
        }

        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (final SocketException e) {
            return false;
        }
    }

    /**
     * Verifica se uma lista de IPs possui apenas endereços válidos.
     * @param listaIps
     * @return
     */
    public static boolean validaListaIps(List<String> listaIps) {
        if ((listaIps == null) || (listaIps.isEmpty())) {
            return true;
        }

        // Quatro conjuntos de 1 a 3 dígitos separados por ponto, sendo que os dígitos nos
        // dois últimos grupos podem ser substituídos por asterisco.
        final String expressaoRegularValidacao = "([0-9]{1,3}\\.)([0-9]{1,3}|\\*)\\.([0-9]{1,3}|\\*)\\.([0-9]{1,3}|\\*)";

        for (final String ip : listaIps) {
            if (!ip.matches(expressaoRegularValidacao)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica se um dado IP está na lista de IPs válidos.
     * @param ipAcesso
     * @param ipsValidos
     * @return
     */
    public static boolean validaIp(String ipAcesso, String ipsValidos) {
        if (ipsValidos == null) {
            return false;
        }

        ipsValidos = ipsValidos.replace(".", "\\.");
        ipsValidos = ipsValidos.replace("*", "[0-9]{1,3}");
        ipsValidos = ipsValidos.replace(';', '|');

        return ipAcesso.matches(ipsValidos);
    }

    public static boolean validaDDNS(String ipAcesso, String ddnsValidos) {
        if (ddnsValidos != null) {
            final String[] ddnsAddresses = ddnsValidos.split(";");

            for (final String ddnsAddresse : ddnsAddresses) {
                try {
                    if (DDNSAddress.getIpDDNSAddress(ddnsAddresse).indexOf(ipAcesso) >= 0) {
                        return true;
                    }
                } catch (final UnknownHostException uex) {
                    // just ignore
                }
            }
        }
        return false;
    }

    /**
     * Valida um endereco de ip de acordo com uma url.
     * @param ipAcesso endereco de ip.
     * @param url url com um formato qualquer. Ex: https://www.servidor.com/xxx, http://www.servidor.com:8080/xxx
     * @return true se o endereco corresponde a url, false caso contrario.
     */
    public static boolean validaUrl(String ipAcesso, String url) {
        return validaUrl(ipAcesso, Arrays.asList(url));
    }

    /**
     * Valida um endereco de ip de acordo com uma lista de urls.
     * @param ipAcesso endereco de ip.
     * @param urls lista de urls com um formato qualquer. Ex: https://www.servidor.com/xxx, http://www.servidor.com:8080/xxx
     * @return true se o endereco corresponde qualquer uma das urls da lista, false caso contrario.
     */
    @SuppressWarnings("java:S2692")
    public static boolean validaUrl(String ipAcesso, List<String> urls) {
        if (ipAcesso == null) {
            return false;
        }

        for (String url : urls) {
            if (url.indexOf("//") > 0) {
                url = url.substring(url.indexOf("//") + 2, url.length());
            }
            if (url.indexOf(":") > 0) {
                url = url.substring(0, url.indexOf(":"));
            }
            if (url.indexOf("/") > 0) {
                url = url.substring(0, url.indexOf("/"));
            }
            try {
                final String ipLookup = DDNSAddress.getIpDDNSAddress(url);
                if (ipLookup.indexOf(ipAcesso) >= 0) {
                    return true;
                }
            } catch (final UnknownHostException uex) {
                LOG.error(uex.getMessage());
                return false;
            }
        }
        LOG.warn("IPs Permitidos: " + TextHelper.join(urls, ";") + " | " + "IP Origem: " + ipAcesso);
        return false;
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = null;
        if (request != null) {
            // Obtém o IP da requisição
            ip = request.getRemoteAddr();

            final String ipsPrivados = "192\\.168\\.[0-9]{1,3}\\.[0-9]{1,3}" + "|172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\.[0-9]{1,3}.[0-9]{1,3}" + "|10\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

            /*
             * Se o IP da requisição vem de um IP privado, então significa
             * que o servidor do sistema está sob um proxy, somente neste
             * caso verifica o cabeçalho de repasse do IP real.
             *
             * Na Marinha há um proxy interno e portanto é preciso pegar
             * o IP via X-Forwarded-For. O IP do cliente que acessou deverá
             * estar na última posição da lista de IPs deste parametro.
             */
            if (ip.matches(ipsPrivados) && !TextHelper.isNull(request.getHeader("X-Forwarded-For"))) {
                final String[] ips = TextHelper.split(request.getHeader("X-Forwarded-For"), ", ");
                ip = ips[ips.length - 1];
            }
        }
        return ip;
    }

    /**
     *
     * @param request
     * @return - Retorna a porta lógica do usuário logado.
     */
    public static int getRemotePort(HttpServletRequest request) {
        if (request != null) {
            // Obtém a Porta lógica da requisição
            return request.getRemotePort();

        }
		return 0;
    }

    public static AcessoSistema getAcessoSistema(HttpServletRequest request) {
        AcessoSistema responsavel = null;
        if ((request != null) && (request.getSession(false) != null)) {
            // A sessão deve ter armazenado o AcessoSistema
            responsavel = (AcessoSistema) request.getSession().getAttribute(AcessoSistema.SESSION_ATTR_NAME);
        }
        if (responsavel == null) {
            // Se não tem o AcessoSistema na sessão então cria um novo AcessoSistema vazio
            responsavel = new AcessoSistema(null, getRemoteAddr(request), getRemotePort(request));
        }
        return responsavel;
    }

    public static AcessoSistema getAcessoSistema(HttpSession session) {
        if (session != null) {
            return (AcessoSistema) session.getAttribute(AcessoSistema.SESSION_ATTR_NAME);
        }
        return null;
    }

    /**
     * Metodo para remover um padrao de uma string
     * @param arg
     * @param padrao
     * @param direcao
     * @return
     */
    public static String removePadrao(String arg, String padrao, int direcao) {
        String letra = "";
        final int tam = arg.length() - 1;
        final int tamP = padrao.length();
        int pos = 0;

        if (((tam > 0) && (tam >= tamP)) || ((tam == 0) && (tamP == 1))) {
            if (direcao == DIR) {
                pos = tam;
                letra = arg.substring(pos, tamP + pos);
                while (letra.equals(padrao)) {
                    pos = pos - tamP;
                    letra = arg.substring(pos, pos + tamP);
                }
                return arg.substring(0, pos);
            } else if (direcao == ESQ) {
                letra = arg.substring(pos, tamP);
                try {
                    while (letra.equals(padrao)) {
                        pos = pos + tamP;
                        letra = arg.substring(pos, pos + tamP);
                    }
                } catch (final IndexOutOfBoundsException ex) {
                    return "";
                }
                return arg.substring(pos, arg.length());
            }
        }
        return "";
    }

    public static Map<String, String> recuperaParametro(String valores, String delimitador1, String delimitador2) {
        final StringTokenizer token = new StringTokenizer(valores, delimitador1, false);
        final Map<String, String> ret = new HashMap<>();

        while (token.hasMoreTokens()) {
            final String vlrPar = token.nextToken();
            if (vlrPar.indexOf("(") >= 0) {
                final String chave = vlrPar.substring(0, vlrPar.indexOf(delimitador2));
                final String vlrChave = vlrPar.substring(vlrPar.indexOf(delimitador2) + 1, vlrPar.length());
                ret.put(chave, vlrChave);
            }
        }
        return ret;
    }

    public static String makeURL(String link, Map<String, String[]> parameterMap) {
        final StringBuilder returnLink = new StringBuilder(link);
        returnLink.append("?");

        for (final Map.Entry<String, String[]> parametro : parameterMap.entrySet()) {
            final String[] value = parametro.getValue();
            for (final String element : value) {
                returnLink.append(parametro.getKey()).append("=").append(TextHelper.escapeSql(element)).append("&");
            }
        }

        // Apaga último caractere '&', ou '?' caso a lista de parâmetros seja nula
        returnLink.deleteCharAt(returnLink.length() - 1);
        return returnLink.toString();
    }

    public static String makeReturnURL(String link, Map<String, String[]> parameterMap) {
        return makeURL(link, parameterMap).replace('?', '$').replace('=', '(').replace('&', '|');
    }

    public static String makeReturnURLWithSpace(String link, Map<String, String[]> parameterMap) {
        return makeURL(link, parameterMap).replace('?', '$').replace('=', '(').replace('&', '|').replace(' ', '¨');
    }

    public static String makeReturnURL64(String link, Map<String, String[]> parameterMap) {
        return TextHelper.encode64(makeURL(link, parameterMap));
    }

    public static void alcancouNumMaxTentativasLogin(CustomTransferObject usuario, AcessoSistema responsavel) throws ZetraException {
        ControleLogin.getInstance().bloqueiaUsuario(usuario, responsavel);
    }

    public static void limpaCacheTentativasLogin(String usuCodigo) {
        ControleLogin.getInstance().resetTetantivasLogin(usuCodigo);
    }

    /**
     * Verifica se o parametro TPC_DIR_IMG_SERVIDORES contem o diretorio relativo de imagens dos servidores, ou
     * seja, subdiretorio de "imagem" que fica abaixo do diretorio raiz de arquivos. Depois pesquisa o nome do
     * arquivo de foto do servidor passado como parametro e verifica se esse arquivo existe, retornando o
     * nome do arquivo mais o caminho relativo obtido do parametro TPC_DIR_IMG_SERVIDORES.
     * @param serCpf CPF do servidor.
     * @param rseCodigo Codigo do servidor.
     * @param responsavel Responsavel pela operacao.
     * @return Valor do parametro TPC_DIR_IMG_SERVIDORES concatenado ao nome do arquivo de imagem.
     */
    public static String getPhoto(String serCpf, String rseCodigo, AcessoSistema responsavel) {
        try {
            final String dirImgServidores = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIR_IMG_SERVIDORES, responsavel);
            if ((dirImgServidores != null) && !"".equals(dirImgServidores)) {

                final String dirRaiz = ParamSist.getDiretorioRaizArquivos();
                final File dirFisico = new File(dirRaiz + File.separatorChar + dirImgServidores);
                if (!dirFisico.exists() && !dirFisico.mkdirs()) {
                    LOG.warn("Diretório '" + dirFisico.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                } else {
                    final ServidorDelegate serDelegate = new ServidorDelegate();
                    final String imagem = serDelegate.buscaImgServidor(serCpf, rseCodigo, responsavel);
                    if ((imagem != null) && !"".equals(imagem)) {
                        final File arqImagem = new File(dirFisico.getAbsolutePath() + File.separatorChar + imagem);
                        if (!arqImagem.exists()) {
                            LOG.warn("Arquivo de imagem de servidor '" + dirFisico.getAbsolutePath() + "' não existe.");
                            return null;
                        } else {
                            return dirImgServidores + File.separatorChar + imagem;
                        }
                    }
                }
            }
        } catch (final ServidorControllerException e) {
            LOG.warn("Erro ao obter foto do servidor.");
        }
        return null;
    }

    public static long limparCacheParametros() {
        final long start = Calendar.getInstance().getTimeInMillis();
        ParamSist.getInstance().reset();
        ParamSvcTO.reset();
        ParamCsa.reset();
        PrazoSvcCsa.reset();
        ControleConsulta.getInstance().reset();
        ControleComunicacaoPermitida.getInstance().reset();
        ControleTipoEntidade.getInstance().reset();
        AcessoRecursoHelper.reset();
        MargemHelper.getInstance().reset();
        NaturezaRelSvc.getInstance().reset();
        CertificadoDigital.getInstance().reset();
        FuncaoExigeMotivo.getInstance().reset();
        ConfigRelatorio.getInstance().reset();
        ControleRestricaoAcesso.resetCacheRestricoes();
        CasamentoMargem.getInstance().reset();
        StatusAutorizacaoDesconto.getInstance().reset();
        PeriodoHelper.getInstance().reset();
        RepasseHelper.getInstance().reset();
        ControleAcessoSeguranca.CONTROLESEGURANCA.reset();
        ApplicationResourcesHelper.getInstance().reset();
        ParamSenhaExternaHelper.getInstance().reset();
        SenhaExterna.getInstance().reset(); // Executar depois de ParamSenhaExternaHelper.getInstance().reset()
        ControleEnvioEmail.getInstance().reset();
        ShowFieldHelper.reset();
        ViewImageHelper.getInstance().reset();
        IpWatchdog.reset();
        RecursoSistemaHelper.reset();
        RegraLimiteOperacaoCache.reset();
        TransferObjectCache.getInstance().reset();
        final long finish = Calendar.getInstance().getTimeInMillis();
        final long time = finish - start;
        LOG.debug("Duration of limparCacheParametros(): " + time + " ms");
        return time;
    }

    public static String getApplicationLogPath() {
        return System.getProperty("jboss.server.log.dir");
    }

    public static String getParametroDifDatasRelatorio(String paramDias, AcessoSistema responsavel) {
        final String parametro = !TextHelper.isNull(paramDias) ? paramDias : (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_DIFERENCA_DATAS_RELATORIO, responsavel);
        if (parametro != null) {
            if ("-1".equals(parametro)) {
                return "";
            } else {
                return parametro;
            }
        } else {
            return "30";
        }
    }

    // Lazy cache com os endereços das interfaces locais
    private static List<String> localIpList = new ArrayList<>();

    /**
     * Carrega lista de endereços encontrados na interface local, na variável localIpList
     */
    private static void loadLocalIpList() {
        Enumeration<NetworkInterface> e1;
        try {
            e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                final NetworkInterface ifc = e1.nextElement();
                if (ifc.isUp()) {
                    final Enumeration<InetAddress> e2 = ifc.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        final InetAddress addr = e2.nextElement();
                        localIpList.add(addr.getHostAddress());
                    }
                }
            }
        } catch (final SocketException e) {
            LOG.error("Erro ao carregar lista de IPs locais ", e);
        }
    }

    /**
     * Valida se o ip designado é um dos IPs definidos nas interfaces locais
     *
     * @param ip
     * @return
     */
    public static boolean validaLanIP(String ip) {
        synchronized (localIpList) {
            if (localIpList.isEmpty()) {
                loadLocalIpList();
            }
        }
        for (final String addr : localIpList) {
            if (addr.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna uma lista com os dias da semana.
     * Esta função foi feita para ser usado em um código javascript
     * @return
     */
    public static String getWeekdayList() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 8; i++) {
            sb.append("'");
            sb.append(DateHelper.getWeekDayName(i));
            sb.append("'");
            if (i != 7) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Extrai o Ip ou o endereço do host de uma URL
     * @param url
     * @return
     */
    @SuppressWarnings("java:S2692")
    public static String getIp(String url) {
        if (url.indexOf("//") > 0) {
            url = url.substring(url.indexOf("//") + 2, url.length());
        }
        if (url.indexOf("/") > 0) {
            url = url.substring(0, url.indexOf("/"));
        }
        return url;
    }

    public static String getRotuloAjudaPesquisaServidor(boolean requerMatriculaCpf, boolean exibeAdeNumero, boolean exibeValorParcela, AcessoSistema responsavel) {
        String chaveTextoAjuda = "";

        if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, CodedValues.TPC_NAO, responsavel)) {
            if (!requerMatriculaCpf && !exibeAdeNumero && !exibeValorParcela) {
                chaveTextoAjuda = "mensagem.pesquisa.informe.cpf";

            } else if (!requerMatriculaCpf && exibeAdeNumero && !exibeValorParcela) {
                chaveTextoAjuda = "mensagem.pesquisa.informe.ade.cpf";

            } else if (!requerMatriculaCpf && !exibeAdeNumero) {
                chaveTextoAjuda = "mensagem.pesquisa.informe.valor.cpf";

            } else {
                throw new UnsupportedOperationException();
            }

        } else if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            if (!requerMatriculaCpf && !exibeAdeNumero && !exibeValorParcela) {
                chaveTextoAjuda = "mensagem.pesquisa.informe.matricula";

            } else if (!requerMatriculaCpf && exibeAdeNumero && !exibeValorParcela) {
                chaveTextoAjuda = "mensagem.pesquisa.informe.ade.matricula";

            } else if (!requerMatriculaCpf && !exibeAdeNumero) { // exibeValorParcela é true
                chaveTextoAjuda = "mensagem.pesquisa.informe.valor.matricula";

            } else {
                throw new UnsupportedOperationException();
            }

        } else if (requerMatriculaCpf && !exibeAdeNumero && !exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.matricula.e.cpf";
        } else if (!requerMatriculaCpf && !exibeAdeNumero && !exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.matricula.ou.cpf";
        } else if (requerMatriculaCpf && exibeAdeNumero && !exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.ade.matricula.e.cpf";
        } else if (!requerMatriculaCpf && exibeAdeNumero && !exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.ade.matricula.ou.cpf";
        } else if (requerMatriculaCpf && !exibeAdeNumero && exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.valor.matricula.e.cpf";
        } else if (!requerMatriculaCpf && !exibeAdeNumero && exibeValorParcela) {
            chaveTextoAjuda = "mensagem.pesquisa.informe.valor.matricula.ou.cpf";
        } else {
            throw new UnsupportedOperationException();
        }

        return chaveTextoAjuda;
    }

    public static String getUserBrowser(HttpServletRequest request) {
        final String userAgent = request.getHeader("User-Agent");
        final String user = userAgent.toLowerCase();
        String browser = "";

        if (user.contains("msie")) {
            final String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0].split("/")[0] + "-" + userAgent.substring(userAgent.indexOf("Version")).split(" ")[0].split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0].split("/")[0] + "-" + userAgent.substring(userAgent.indexOf("Version")).split(" ")[0].split("/")[1];
            } else if (user.contains("opr")) {
                browser = userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0].replace("/", "-").replace("OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0].replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            browser = "Netscape-?";
        } else if (user.contains("firefox")) {
            browser = userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0].replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else {
            browser = "UnKnown, More-Info: " + userAgent;
        }

        return browser;
    }

    public static String montaSerNome(String titulacao, String nome, String nomeMeio, String ultimoNome) {
        return ((!TextHelper.isNull(titulacao) ? titulacao + " " : "") + (!TextHelper.isNull(nome) ? nome + " " : "") + (!TextHelper.isNull(nomeMeio) ? nomeMeio + " " : "") + (!TextHelper.isNull(ultimoNome) ? ultimoNome + " " : "")).trim();

    }

    /**
     * define o tamanho do campo serNome de acordo com a visibilidade das chaves FieldsPermission passadas como parâmetros
     * @param titulacaoChave
     * @param nomeChave
     * @param nomeMeioChave
     * @param ultimoNomeChave
     * @param responsavel
     * @return
     * @throws ZetraException
     */
    public static String configTamCampoNome(String titulacaoChave, String nomeChave, String nomeMeioChave, String ultimoNomeChave, AcessoSistema responsavel) throws ZetraException {
        int countCamposVisiveis = 0;

        countCamposVisiveis = ShowFieldHelper.showField(titulacaoChave, responsavel) ? ++countCamposVisiveis : countCamposVisiveis;
        countCamposVisiveis = ShowFieldHelper.showField(nomeChave, responsavel) ? ++countCamposVisiveis : countCamposVisiveis;
        countCamposVisiveis = ShowFieldHelper.showField(nomeMeioChave, responsavel) ? ++countCamposVisiveis : countCamposVisiveis;
        countCamposVisiveis = ShowFieldHelper.showField(ultimoNomeChave, responsavel) ? ++countCamposVisiveis : countCamposVisiveis;

        return countCamposVisiveis <= 2 ? "32" : "15";
    }

    /**
     * Invalida cookie de marcação de portal de acessa para cada navegador
     * @param response
     * @param contextPath
     * @return
     */
    public static void setaCookieLogin(HttpServletResponse response, String contextPath) {
        //com as novas interfaces do spring os cookies são gravados com domínios diferentes
        final Cookie killMyCookieFirefox = new Cookie("LOGIN", null);
        killMyCookieFirefox.setMaxAge(0);
        killMyCookieFirefox.setPath(contextPath + "/v3/");
        response.addCookie(killMyCookieFirefox);

        final Cookie killMyCookieChrome = new Cookie("LOGIN", null);
        killMyCookieChrome.setMaxAge(0);
        killMyCookieChrome.setPath(contextPath + "/v3");
        response.addCookie(killMyCookieChrome);
    }

    /**
     *
     * @param nome
     * @param dominio
     * @param valor
     * @param habilitado
     * @param cssClass
     * @param placeHolder
     * @param descricao
     * @param responsavel
     * @return
     */
    @SuppressWarnings("java:S1172")
    public static String montaValorParamCsaV4(String nome, String dominio, String valor, boolean habilitado, String cssClass, String placeHolder, String descricao, AcessoSistema responsavel) throws ParametroControllerException, UsuarioControllerException {
        StringBuilder card = new StringBuilder();
        if ("ALFA".equals(dominio) || "INT".equals(dominio)) {
            cssClass = !TextHelper.isNull(cssClass) ? cssClass : "form-group col-sm-6";
            card = new StringBuilder("<div class=\"").append(cssClass).append("\">");
            card.append("<label for=\"").append(TextHelper.forHtmlAttribute(nome)).append("\">").append(!TextHelper.isNull(descricao) ? TextHelper.forHtmlAttribute(descricao) : "").append("</label>");
            card.append("<input type=\"text\" class=\"form-control\" id=\"").append(TextHelper.forHtmlAttribute(nome)).append("\"");
            card.append(" value=\"").append(!TextHelper.isNull(valor) ? TextHelper.forHtmlAttribute(valor) : "").append("\" name=\"");
            card.append(TextHelper.forHtmlAttribute(nome)).append("\" placeholder=\"").append(!TextHelper.isNull(placeHolder) ? TextHelper.forHtmlAttribute(placeHolder) : "").append("\">");
            card.append("</div>");
        } else if (dominio.startsWith("SELECAO")) {
            cssClass = !TextHelper.isNull(cssClass) ? cssClass : "form-group col-sm-6";
            card = new StringBuilder("<div class=\"").append(cssClass).append("\">");
            card.append("<label for=\"").append(TextHelper.forHtmlAttribute(TextHelper.forHtmlAttribute(nome))).append("\">");
            card.append(!TextHelper.isNull(descricao) ? TextHelper.forHtmlAttribute(descricao) : "").append("</label>");
            card.append("<select multiple class=\"form-control form-select\" id=\"").append(TextHelper.forHtmlAttribute(nome)).append("\" name=\"").append(TextHelper.forHtmlAttribute(nome)).append("\">");
            card.append("<option value=\"\">").append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("</option>");

            final String[] opcoes = dominio.substring(dominio.indexOf('[') + 1, dominio.indexOf(']')).split(";");
            if ("FUNCAO".equals(opcoes[0])) {
                final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);

                final String tpaFunCodigos = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS, responsavel);
                final List<TransferObject> funcoes = usuarioController.findFuncoesRegraTaxa(tpaFunCodigos, responsavel);

                for (final TransferObject fun : funcoes) {
                    final String valorOpcao = (String) fun.getAttribute(Columns.FUN_CODIGO);
                    final String nomeOpcao = (String) fun.getAttribute(Columns.FUN_DESCRICAO);
                    final String[] vlr = valor.split(",");
                    final List<String> selecionados = Arrays.asList(vlr);

                    card.append("<option value=\"" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "\" " + (selecionados.contains(valorOpcao) ? "selected" : "") + ">" + TextHelper.forHtmlContent(nomeOpcao.trim()) + "</option>");
                }
            } else {
                for (final String opcoe : opcoes) {
                    String nomeOpcao = opcoe;
                    String valorOpcao = opcoe;
                    if (opcoe.indexOf('=') != -1) {
                        valorOpcao = opcoe.substring(0, opcoe.indexOf('='));
                        nomeOpcao = opcoe.substring(opcoe.indexOf('=') + 1);
                    }

                    card.append("<option value=\"" + TextHelper.forHtmlAttribute(valorOpcao.trim()) + "\" " + (valor.equals(valorOpcao.trim()) ? "selected" : "") + ">" + TextHelper.forHtmlContent(nomeOpcao.trim()) + "</option>");
                }
            }
            card.append("</select></div>");
        } else if (dominio.startsWith("SN")) {
            cssClass = !TextHelper.isNull(cssClass) ? cssClass : "col-sm-12 col-md-6";
            card = new StringBuilder("<div class=\"").append(cssClass).append("\">");
            card.append("<div class=\"form-group\" role=\"").append("radiogroup\" ").append("aria-labelledby=\"").append(!TextHelper.isNull(placeHolder) ? placeHolder : "").append("\">");
            card.append("<div><span ").append("id=\"").append(TextHelper.forHtmlAttribute(nome)).append("\">").append(descricao).append("</span></div>");
            card.append("<div class=\"form-check form-check-inline pt-2\">");
            card.append("<input class=\"form-check-input ml-1\" type=\"radio\" name=\"").append(TextHelper.forHtmlAttribute(nome)).append("\" id=\"").append(TextHelper.forHtmlAttribute(nome)).append("Sim");
            card.append("\" value=\"").append("S\" ").append("S".equals(valor) ? "checked" : "").append(">");
            card.append("<label class=\"form-check-label labelSemNegrito ml-1 pr-4\" for=\"").append(TextHelper.forHtmlAttribute(nome)).append("Sim").append("\">").append(ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)).append("</label>");
            card.append("</div>");
            card.append("<div class=\"form-check form-check-inline pt-2\">");
            card.append("<input class=\"form-check-input ml-1\" type=\"radio\" name=\"").append(TextHelper.forHtmlAttribute(nome)).append("\" id=\"").append(TextHelper.forHtmlAttribute(nome)).append("Nao");
            card.append("\" value=\"").append("N\" ").append("N".equals(valor) ? "checked" : "").append(">");
            card.append("<label class=\"form-check-label labelSemNegrito ml-1 pr-4\" for=\"").append(TextHelper.forHtmlAttribute(nome)).append("Nao").append("\">").append(ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)).append("</label>");
            card.append("</div></div></div>");
        } else if (dominio.startsWith("ESCOLHA")) {
            cssClass = !TextHelper.isNull(cssClass) ? cssClass : "col-sm-12 col-md-6";
            card = new StringBuilder("<div class=\"").append(cssClass).append("\">");
            card.append("<div class=\"form-group\" role=\"").append("radiogroup\" ").append("aria-labelledby=\"").append(!TextHelper.isNull(placeHolder) ? placeHolder : "").append("\">");
            card.append("<div><span ").append("id=\"").append(TextHelper.forHtmlAttribute(nome)).append("\">").append(descricao).append("</span></div>");

            final String[] opcoes = dominio.substring(dominio.indexOf('[') + 1, dominio.indexOf(']')).split(";");
            for (final String opcoe : opcoes) {
                String nomeOpcao = opcoe;
                String valorOpcao = opcoe;
                if (opcoe.indexOf('=') != -1) {
                    valorOpcao = opcoe.substring(0, opcoe.indexOf('='));
                    nomeOpcao = opcoe.substring(opcoe.indexOf('=') + 1);
                }
                card.append("<div class=\"form-check form-check-inline pt-2\">");
                card.append("<input class=\"form-check-input ml-1\" type=\"radio\" name=\"").append(TextHelper.forHtmlAttribute(nome)).append("\" id=\"").append(TextHelper.forHtmlAttribute(nomeOpcao.trim())).append(TextHelper.forHtmlAttribute(nome));
                card.append("\" value=\"").append(TextHelper.forHtmlAttribute(valorOpcao.trim())).append("\" ").append(valor.equals(valorOpcao.trim()) ? "checked" : "").append(">");
                card.append("<label class=\"form-check-label labelSemNegrito ml-1 pr-4\" for=\"").append(TextHelper.forHtmlAttribute(nomeOpcao.trim())).append(TextHelper.forHtmlAttribute(nome)).append("\">").append(TextHelper.forHtmlContent(nomeOpcao.trim())).append("</label>");
                card.append("</div>");
            }
            card.append("</div></div>");
        } else if ("FLOAT".equals(dominio)) {
            final String mask = "FLOAT".equals(dominio) ? "#F20" : "";
            cssClass = !TextHelper.isNull(cssClass) ? cssClass : "form-group col-sm-6";
            card = new StringBuilder("<div class=\"").append(cssClass).append("\">");
            card.append("<label for=\"").append(TextHelper.forHtmlAttribute(nome)).append("\">").append(!TextHelper.isNull(descricao) ? TextHelper.forHtmlAttribute(descricao) : "").append("</label>");
            card.append("<input type=\"text\" class=\"form-control\" id=\"").append(TextHelper.forHtmlAttribute(nome)).append("\"");
            card.append(" value=\"").append(!TextHelper.isNull(valor) ? TextHelper.forHtmlAttribute(valor) : "").append("\" name=\"");
            card.append(TextHelper.forHtmlAttribute(nome)).append("\" placeholder=\"").append(!TextHelper.isNull(placeHolder) ? TextHelper.forHtmlAttribute(placeHolder) : "").append("\"");
            card.append(" onFocus=\"SetarEventoMascaraV4(this,'" + TextHelper.forJavaScriptAttribute(mask) + "',true);\"").append("\">");
            card.append("</div>");
        }

        return card.toString();
    }

    /**
     * Retorna quantidade de mensagens sem leitura do usuário logado.
     *
     * @param session
     * @param responsavel
     * @return
     */
    public static Integer qtdeMsgSemLeitura(java.util.Date usuDataCad, AcessoSistema responsavel) {
        // Recupera quantidade de mensagens sem leitura
        Integer qtdeMsgSemLeitura = 0;
        if (usuDataCad != null) {
            try {
                final MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.USU_DATA_CAD, usuDataCad);

                qtdeMsgSemLeitura = mensagemController.countMensagemUsuarioSemLeitura(criterio, responsavel);
            } catch (final MensagemControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return qtdeMsgSemLeitura;
    }

    public static String getMsgEnvioSenhaAutorizacaoOtp(String rseCodigo, AcessoSistema responsavel) {
        String mensagem = "";

        if (!TextHelper.isNull(rseCodigo)) {
            try {
                final ServidorDelegate serDelegate = new ServidorDelegate();
                final ServidorTransferObject servidor = serDelegate.findServidorByRseCodigo(rseCodigo, responsavel);

                String email = null;
                final String celular = !TextHelper.isNull(servidor.getSerCelular()) ? TextHelper.escondeTelefone(servidor.getSerCelular()) : "";

                final String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;
                final String consultarEmailExternoClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_BUSCA_EMAIL_SERVIDOR_API_EXTERNA, responsavel);

                //DESENV-21344
                if (!TextHelper.isNull(consultarEmailExternoClassName) &&
                    (CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega) ||
                     CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega) ||
                     CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) ||
                     CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega))) {
                    try {
                        final ConsultarEmailExternoServidor consultarEmailExternoServidor = ConsultarEmailExternoServidorFactory.getClasseConsultarEmailExternoServidor(consultarEmailExternoClassName);
                        final CustomTransferObject resultadoConsultaAPIExterna = consultarEmailExternoServidor.consultarEmailExternoServidor(servidor.getSerCpf());

                        if (HttpStatus.OK.equals(resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_STATUS.getChave()))) {
                            email = TextHelper.escondeEmail((String) resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_SUCCESS_DATA.getChave()));
                        } else {
                            return (String) resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_ERROR_DATA.getChave());
                        }
                    } catch (final ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                } else {
                    email = !TextHelper.isNull(servidor.getSerEmail()) ? TextHelper.escondeEmail(servidor.getSerEmail()) : "";
                }

                mensagem = switch (modoEntrega) {
                    case CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL -> ApplicationResourcesHelper.getMessage("mensagem.confirma.envio.senha.autorizacao.otp.email", responsavel, email);
                    case CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA -> ApplicationResourcesHelper.getMessage("mensagem.confirma.envio.senha.autorizacao.otp.email", responsavel, email);
                    case CodedValues.ALTERACAO_SENHA_AUT_SER_SMS -> ApplicationResourcesHelper.getMessage("mensagem.confirma.envio.senha.autorizacao.otp.sms", responsavel, celular);
                    case CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL -> ApplicationResourcesHelper.getMessage("mensagem.confirma.envio.senha.autorizacao.otp.email.e.sms", responsavel, email, celular);
                    default -> ApplicationResourcesHelper.getMessage("mensagem.confirma.envio.senha.autorizacao.otp.email", responsavel, email);
                };

            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return mensagem;
    }

    /**
     *
     * @param responsavel
     * @param funCodigo
     * @param useRestricaoAcesso
     * @return
     */
    @SuppressWarnings("java:S1172")
    public static Boolean temPermissao(AcessoSistema responsavel, String funCodigo, Boolean useRestricaoAcesso) {
        if (responsavel == null) {
            return false;
        }
        return responsavel.temPermissao(funCodigo);
    }

    /**
     *
     * @param responsavel
     * @param session
     * @return
     */
    public static String getIdAgenteChatbot(AcessoSistema responsavel, HttpSession session) {
        // Define o projeto do chatbot
        String projectId = null;
        if (responsavel.isCseSupOrg()) {
            projectId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_USUARIO_CSE_ORG_SUP, responsavel);
        } else if (responsavel.isCsaCor()) {
            projectId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_USUARIO_CSA_COR, responsavel);
        } else if (responsavel.isSer()) {
            projectId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_USUARIO_SER, responsavel);
        } else {
            // Sem sessão válida, olhar se veio da página de login ou servidor
            final Boolean origemLoginServidor = (Boolean) session.getAttribute(ChatbotRestController.CHATBOT_ORIGEM_LOGIN_SERVIDOR);
            if ((origemLoginServidor != null) && origemLoginServidor) {
                projectId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_SER, responsavel);
            } else {
                projectId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_USU, responsavel);
            }
        }
        return projectId;
    }

    /**
     *
     * @param request
     * @return
     */
    @SuppressWarnings("java:S1172")
    public static String getContentSecurityPolicyHeader(HttpServletRequest request) {
        return "default-src 'self' blob: *.google.com *.gstatic.com *.googleapis.com *.fontawesome.com *.zdassets.com *.zendesk.com wss://widget-mediator.zopim.com *.typekit.net *.data:application *.hcaptcha.com http://127.0.0.1:65056 wss://127.0.0.1:65156/signer; "
               + "img-src 'self' data: *.econsig.com.br http://127.0.0.1:65056/verify.gif; "
               + "script-src-elem 'self' 'unsafe-inline' *.site24x7rum.com *.zdassets.com *.google.com/recaptcha/api.js *.gstatic.com/recaptcha/releases/iZWPJyR27lB0cR4hL_xOX0GC/recaptcha__pt_br.js *.fontawesome.com/ea731dcb6f.js *.com/vue-select@2.4.0/dist/vue-select.js *.gstatic.com/recaptcha/releases/iRvKkcsnpNcOYYwhqaQxPITz/recaptcha__pt_br.js *.hcaptcha.com; "
               + "script-src 'self' 'unsafe-eval' 'unsafe-inline' *.fontawesome.com *.zdassets.com https://unpkg.com/vue-select@2.4.0/dist/vue-select.js; "
               + "style-src-elem 'self' 'unsafe-inline' *.googleapis.com *.fontawesome.com/ea731dcb6f.css *.fontawesome.com/releases/v4.7.0/css/font-awesome-css.min.css; "
               + "style-src 'self' 'unsafe-eval' 'unsafe-inline' *.googleapis.com *.fontawesome.com";
    }

    /**
     *
     * @param request
     * @return
     */
    public static boolean getNavegadorExclusivo(HttpServletRequest request) {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, getAcessoSistema(request))) {
            final String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("");
            final String uaId = request.getHeader("Sec-Ch-Ua-Id");
            return userAgent.endsWith(" eConsig/1.0.0.20890") && "20890".equals(uaId);
        }
        return false;
    }
}
