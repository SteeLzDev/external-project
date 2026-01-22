package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.HTMLInputTag;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: EfetivaAcaoMotivoOperacaoTag</p>
 * <p>Description: Tag para motivo de operação layout v4.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EfetivaAcaoMotivoOperacaoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EfetivaAcaoMotivoOperacaoTag.class);

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    private boolean scriptOnly = false;
    private String inputSizeCSS = "col-sm-6";
    private String msgConfirmacao;
    private boolean operacaoUsuario = false;
    private boolean operacaoRegistroServidor = false;
    private boolean operacaoServico = false;
    private boolean operacaoConvenio = false;
    private boolean operacaoDispensaValidacaoDigital = false;
    private boolean tmoSempreObrigatorio = true;

    public void setMsgConfirmacao(String msgConfirmacao) {
        this.msgConfirmacao = msgConfirmacao;
    }

    public void setOperacaoUsuario(boolean operacaoUsuario) {
        this.operacaoUsuario = operacaoUsuario;
    }

    public void setOperacaoRegistroServidor(boolean operacaoRegistroServidor) {
        this.operacaoRegistroServidor = operacaoRegistroServidor;
    }

    public void setOperacaoServico(boolean operacaoServico) {
        this.operacaoServico = operacaoServico;
    }

    public void setOperacaoConvenio(boolean operacaoConvenio) {
        this.operacaoConvenio = operacaoConvenio;
    }

    public void setOperacaoDispensaValidacaoDigital(boolean operacaoDispensaValidacaoDigital) {
        this.operacaoDispensaValidacaoDigital = operacaoDispensaValidacaoDigital;
    }

    public void setTmoSempreObrigatorio(boolean tmoSempreObrigatorio) {
        this.tmoSempreObrigatorio = tmoSempreObrigatorio;
    }

    public void setScriptOnly(boolean scriptOnly) {
        this.scriptOnly = scriptOnly;
    }

    public void setInputSizeCSS(String inputSizeCSS) {
        this.inputSizeCSS = inputSizeCSS;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            imprimeHTMLTag();
        } catch (final IOException ex) {
            throw new JspException(ex);
        }
        return EVAL_PAGE;
    }

    private void imprimeHTMLTag() throws IOException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Inicia geração do código HTML
        StringBuilder html = new StringBuilder();

        if (scriptOnly) {
            html.append(geraJavaScript(responsavel));
            pageContext.getOut().print(html.toString());
            return;
        }

        String motivoOperacao = ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento", responsavel);

        TransferObject ctoMtvOperacao = null;
        String tmo_codigo, tmo_descricao;
        List<TransferObject> lstMtvOperacao;
        try {
            if (operacaoUsuario) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoUsuario(CodedValues.STS_ATIVO, responsavel);
            } else if (operacaoRegistroServidor) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoRegistroServidor(CodedValues.STS_ATIVO, responsavel);
            } else if (operacaoServico) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoServico(CodedValues.STS_ATIVO, responsavel);
            } else if (operacaoConvenio) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConvenio(CodedValues.STS_ATIVO, responsavel);
            } else if (operacaoDispensaValidacaoDigital) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoDispensaValidacaoDigital(CodedValues.STS_ATIVO, responsavel);
            } else {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
            }

            html.append("<div class=\"row\">");
            // Define tipo de motivo da operacao
            html.append("<div class=\"form-group "+inputSizeCSS+"\">");
            html.append("<label for=\"TMO_CODIGO\">" + motivoOperacao + "</label>");

            html.append("<SELECT CLASS=\"form-control form-select\" NAME=\"TMO_CODIGO\" ID=\"TMO_CODIGO\" onFocus=\"SetarEventoMascara(this,'#*100',true);\" onBlur=\"fout(this);ValidaMascara(this);\" onChange=\"validaExigenciaObs();\">");
            html.append("<OPTION VALUE=\"\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.arg", responsavel, motivoOperacao.toLowerCase()) + "</OPTION>");

            StringBuilder hiddens = new StringBuilder();

            Iterator<TransferObject> itMtvOperacao = lstMtvOperacao.iterator();
            while (itMtvOperacao.hasNext()) {
                ctoMtvOperacao = itMtvOperacao.next();
                tmo_codigo = (String) ctoMtvOperacao.getAttribute(Columns.TMO_CODIGO);
                tmo_descricao = (String) ctoMtvOperacao.getAttribute(Columns.TMO_DESCRICAO);

                html.append("<OPTION VALUE=\"" + TextHelper.forHtmlAttribute(tmo_codigo) + "\">"+ TextHelper.forHtmlContent(tmo_descricao) + "</OPTION>");
                hiddens.append("<INPUT TYPE=\"hidden\" id=\"exige_obs_" + tmo_codigo + "\" value=\"" + ctoMtvOperacao.getAttribute(Columns.TMO_EXIGE_OBS) + "\"  />");
            }
            html.append("</SELECT>" + (tmoSempreObrigatorio ? JspHelper.verificaCampoNulo(request, "TMO_CODIGO") : ""));

            html.append(hiddens);

            html.append("</div>");
            html.append("</div>");

            HTMLInputTag inputTag = new HTMLInputTag();
            inputTag.setName("ADE_OBS");
            inputTag.setDi("ADE_OBS");
            inputTag.setType("textarea");
            inputTag.setCols("32");
            inputTag.setRows("5");
            inputTag.setMask("#*10000");
            inputTag.setReadonly("false");
            inputTag.setClasse("form-control");
            inputTag.setPlaceHolder(ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel));
            String adeObs = inputTag.generateHtml(responsavel);

            html.append("<div class=\"row\" id=\"campo_tipo_motivo_obs\" style=\"display:none\">");
            html.append("<div class=\"form-group "+inputSizeCSS+"\">");
            html.append("<label for=\"ADE_OBS\">").append(ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.dados.observacao", responsavel)).append("</label>");
            html.append(adeObs);
            html.append("</div>");
            html.append("</div>");

        } catch (TipoMotivoOperacaoControllerException ex) {
            pageContext.getSession().setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return;
        }

        pageContext.getOut().print(html.toString());
    }

    private String geraJavaScript(AcessoSistema responsavel) {
        final StringBuilder html = new StringBuilder();
        html.append("<script language=\"JavaScript\" type=\"text/JavaScript\">");
        html.append(" function confirmaAcaoConsignacao () {");
        html.append("     var motivoField = document.getElementById('TMO_CODIGO');");
        html.append("     if (motivoField != null) {");
        html.append("         var exigeObs = document.getElementById('exige_obs_' + motivoField.value);");
        html.append("         var obsField = document.getElementById('ADE_OBS');");
        html.append("         if (motivoField.disabled == false) {");
        html.append("             if (motivoField.value == '') {");
        html.append("                 alert('" + ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel) + "');");
        html.append("                 motivoField.focus();");
        html.append("                 return false;");
        html.append("             }");
        html.append("             if (exigeObs.value == 'S' && obsField.value.trim() == ''){");
        html.append("                 alert('" + ApplicationResourcesHelper.getMessage("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel) + "');");
        html.append("                 obsField.focus();");
        html.append("                 return false;");
        html.append("             }");
        html.append("         }");
        html.append("     }");


        if (ParamSist.paramEquals(CodedValues.TPC_OBS_OBRIGATORIO_SUSPENSAO_ADE, CodedValues.TPC_SIM, responsavel)
                && CodedValues.FUN_SUSP_CONSIGNACAO.equals(responsavel.getFunCodigo())) {
            html.append("     var obsField = document.getElementById('ADE_OBS');");
            html.append("     if (obsField != null && obsField.disabled == false) {");
            html.append("         if (obsField.value.trim() == '') {");
            html.append("             alert('" + ApplicationResourcesHelper.getMessage("mensagem.observacao.operacao.obrigatorio.suspender", responsavel) + "');");
            html.append("             obsField.focus();");
            html.append("             return false;");
            html.append("          }");
            html.append("     }");
        }

        if (!TextHelper.isNull(msgConfirmacao)) {
            html.append("     if (confirm(\'" + TextHelper.forJavaScriptBlock(msgConfirmacao) + "\')) {");
            html.append("         return true;");
            html.append("     } else {");
            html.append("         return false;");
            html.append("     }");
        } else {
            html.append("     return true;");
        }
        html.append(" }");

        //Valida se o campo observação é exigido ou não de acordo com o "Motivo da operação". (v4)
        html.append(" function validaExigenciaObs () {");
        html.append("     var motivoField = document.getElementById('TMO_CODIGO');");
        html.append("     if (motivoField != null && motivoField.disabled == false && motivoField.value != '') {");
        html.append("         var exigeObs = document.getElementById('exige_obs_' + motivoField.value);");
        html.append("         var obsField = document.getElementById('campo_tipo_motivo_obs');");
        html.append("         if (exigeObs.value != 'D') {");
        html.append("             obsField.style.display = \"block\"; ");
        html.append("         } else {");
        html.append("             obsField.style.display = \"none\"; ");
        html.append("         }");
        html.append("         if (exigeObs.value == 'D') {");
        html.append("             obsField.value = \"\"; ");
        html.append("         }");
        html.append("     }");
        html.append(" } ");


        //Valida se o campo observação é exigido ou não de acordo com o "Motivo da operação". (v2)
        html.append(" function validaExigenciaObsV2 () {");
        html.append("     var motivoField = document.getElementById('TMO_CODIGO');");
        html.append("     if (motivoField != null && motivoField.disabled == false && motivoField.value != '') {");
        html.append("         var exigeObs = document.getElementById('exige_obs_' + motivoField.value);");
        html.append("         var obsField = document.getElementById('ADE_OBS');");
        html.append("             obsField.value = \"\"; ");
        html.append("         if (exigeObs.value != 'D') {");
        html.append("             obsField.disabled = false; ");
        html.append("         } else {");
        html.append("             obsField.disabled = \"true\"; ");
        html.append("         }");
        html.append("         if (exigeObs.value == 'D') {");
        html.append("             obsField.value = \"\"; ");
        html.append("         }");
        html.append("     }");
        html.append(" }");

        html.append("</script>");
        return html.toString();
    }
}
