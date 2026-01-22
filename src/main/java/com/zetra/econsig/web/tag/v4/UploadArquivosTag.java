package com.zetra.econsig.web.tag.v4;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import java.util.Iterator;
import java.util.List;

public class UploadArquivosTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadArquivosTag.class);

    @Override
    public int doEndTag() throws JspException {
        try {
            StringBuilder code = new StringBuilder();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            code.append(montaHeader(responsavel, request));
            code.append(montaBody(responsavel, request));
            code.append(montaFooter(responsavel));

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    public String montaHeader(AcessoSistema responsavel, HttpServletRequest request) {
        String header = "";
        String msgResultadoComando = (String) request.getAttribute("msgResultadoComando");

        if(!TextHelper.isNull(msgResultadoComando)){
            header += "  <div class=\"alert alert-warning\" role=\"alert\">\n" +
                      "  <i class=\"fa fa-exclamation-triangle fa-2x fa-stack\" aria-hidden=\"true\"></i>\n" +
                    msgResultadoComando + "</div>";
        }
        header = " <div class=\"card\">\n" +
                "    <div class=\"card-header hasIcon\">\n" +
                "      <span class=\"card-header-icon\"><svg width=\"26\"><use xlink:href=\"#i-upload\"></use></svg></span>\n" +
                "      <h2 class=\"card-header-title\">" + ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.titulo", responsavel) +
                "    </div>";

        return header;
    }

    public String montaBody(AcessoSistema responsavel, HttpServletRequest request) throws ZetraException {
        StringBuilder body = new StringBuilder();

        boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
        boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
        boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
        boolean comentario = (boolean) request.getAttribute("comentario");
        boolean temPermissaoEst = (boolean) request.getAttribute("temPermissaoEst");
        boolean exibeCampoUpload = (boolean) request.getAttribute("exibeCampoUpload");
        boolean selecionaEstOrgUploadMargemRetorno = (boolean) request.getAttribute("selecionaEstOrgUploadMargemRetorno");
        boolean selecionaEstOrgUploadContracheque = (boolean) request.getAttribute("selecionaEstOrgUploadContracheque");
        List<?> lstEstabelecimentos = (List<?>) request.getAttribute("lstEstabelecimentos");
        List<?> lstOrgaos = (List<?>) request.getAttribute("lstOrgaos");
        String estCodigo = (String) request.getAttribute("estCodigo");
        String orgCodigo = (String) request.getAttribute("orgCodigo");
        String tipo = (String) request.getAttribute("tipo");
        String fluxo = (String) request.getAttribute("fluxo");
        String action = (String) request.getAttribute("action");

        // chave publica de recaptcha avancado só é solicitada, caso o parametro 394 esteja habilitado
        String chavePublica = "";
        if (exibeCaptchaAvancado) {
            chavePublica = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAPTCHA_AVANCADO_CHAVE_PUBLICA, responsavel);
            if (chavePublica.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", (AcessoSistema) null);
            }
        }

        String checkedCse = "", checkedOrg = "", checkedEst = "";
        String papCodigo = (String) request.getAttribute("papCodigo");
        if (TextHelper.isNull(papCodigo) || papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) {
            checkedCse = "checked";
        } else if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {
            checkedOrg = "checked";
        } else if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {
            checkedEst = "checked";
        }
        body = new StringBuilder("<div class=\"card-body\">" +
                "                  <form name=\"form2\" method=\"POST\" action= " + action + " enctype=\"multipart/form-data\">\n" +
                "                   <div class=\"row\">");
        body.append("          <div class=\"form-group col-sm-6\">\n" +
                "              <label for=\"tipo\">" + ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.tipo", responsavel) + "</label>\n" +
                "              <select class=\"form-control form-select\" id=\"tipo\" name=\"tipo\" onChange=\"alterarTipoArquivo();\">\n" +
                "              <option value=\"\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) + " </option>");
        if (fluxo.equals("uploadListarMargem") || fluxo.equals("uploadListarMargemComplementar") || fluxo.equals("transferidos")) {
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS, responsavel)) {
                body.append("<option value=\"margem\"").append(tipo != null && tipo.equals("margem") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margens.servidores", responsavel)).append("</option>");
            }
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR, responsavel)) {
                body.append("<option value=\"margemcomplementar\"").append(tipo != null && tipo.equals("margemcomplementar") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margem.complementar", responsavel)).append("</option>");
            }
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_TRANSFERIDOS, responsavel)) {
                body.append("<option value=\"transferidos\"").append(tipo != null && tipo.equals("transferidos") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.transferidos", responsavel)).append("</option>");
            }
        }
        if (fluxo.equals("uploadListarRetornoIntegracao")) {
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO, responsavel)) {
                body.append("<option value=\"retorno\"").append(tipo != null && tipo.equals("retorno") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.integracao", responsavel)).append("</option>");
            }
        }
        if (fluxo.equals("uploadListarRetornoAtrasado")) {
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO, responsavel)) {
                body.append("<option value=\"retornoatrasado\"").append(tipo != null && tipo.equals("retornoatrasado") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.atrasado", responsavel)).append("</option>");
            }
        }
        if (fluxo.equals("uploadListarHistorico")) {
            if (UploadHelper.temPermissaoUpload(TipoArquivoEnum.ARQUIVO_HISTORICO, responsavel)) {
                body.append("<option value=\"historico\"").append(tipo != null && tipo.equals("historico") ? " selected " : "").append(">").append(ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.historico", responsavel)).append("</option>");
            }
        }
        body.append("<input type=\"hidden\" name=\"FLUXO\" id=\"FLUXO\" value=\"").append(fluxo).append("\"/>");
        body.append("</select></div>");
        if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) {
            body.append("       <div class=\"form-group col-sm-6\">\n" +
                    "                     <div><span>" + ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.entidade", responsavel) + "</span></div>\n" +
                    "                     <div class=\"form-check form-check-inline mt-2\">\n" +
                    "                      <input class=\"form-check-input mt-1 ml-1\" type=\"radio\" name=\"PAP_CODIGO\" id=\"entidadeGeral\" onClick=alterarTipoArquivo(); value=" + (String) AcessoSistema.ENTIDADE_CSE + " onFocus=SetarEventoMascara(this,'#*100',true); " + checkedCse + " onBlur=fout(this);ValidaMascara(this);>" +
                    "                      <label class=\"form-check-label labelSemNegrito ml-1 pr-4\" for=\"entidadeGeral\"> " + ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.geral", responsavel) + "</label>" +
                    "                      </div>" +
                    "                      <div class=\"form-check form-check-inline mt-2\">\n" +
                    "                      <input class=\"form-check-input mt-1 ml-1\" type=\"radio\" name=\"PAP_CODIGO\" id=\"entidadeEstabelecimento\" onClick=alterarTipoArquivo(); value=" + (String) AcessoSistema.ENTIDADE_EST + " onFocus=SetarEventoMascara(this,'#*100',true); " + checkedEst + " onBlur=fout(this);ValidaMascara(this);>" +
                    "                      <label class=\"form-check-label labelSemNegrito ml-1 pr-4 pt-1\" for=\"entidadeEstabelecimento\">" + ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel) + "</label>" +
                    "                      </div>" +
                    "                      <div class=\"form-check form-check-inline mt-2\">\n" +
                    "                      <input class=\"form-check-input mt-1 ml-1\" type=\"radio\" name=\"PAP_CODIGO\" id=\"entidadeOrgao\" onClick=alterarTipoArquivo(); value=" + (String) AcessoSistema.ENTIDADE_ORG + " onFocus=SetarEventoMascara(this,'#*100',true); " + checkedOrg + " onBlur=fout(this);ValidaMascara(this);>" +
                    "                      <label class=\"form-check-label labelSemNegrito ml-1 pr-4 pt-1\" for=\"entidadeOrgao\">" + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel) + "</label>" +
                    "                     </div>\n" +
                    "                   </div>\n");
        }
        body.append("      </div>\n");
        if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && responsavel.isCseSup()) {
            if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {
                body.append("<div class=\"row\"><div class=\"form-group col-sm-6\">\n" + "<label for=\"EST_CODIGO\">" + ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel) + "</label>\n" +
                        "<select class=\"form-control form-select\" id=\"EST_CODIGO\" name=\"EST_CODIGO\" onChange=\"vf_nome_arquivo(); alterarTipoArquivo();\" onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">\n" +
                        "<option value=\"\"").append((String) (TextHelper.isNull(estCodigo) ? " selected " : "" )).append(">").append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("</option>");
                Iterator<?> iteEst = lstEstabelecimentos.iterator();
                while (iteEst.hasNext()) {
                    TransferObject ctoEst = (TransferObject) iteEst.next();
                    String fieldValueEst = ctoEst.getAttribute(Columns.EST_CODIGO).toString();
                    String fieldLabelEst = ctoEst.getAttribute(Columns.EST_NOME) + " - " + ctoEst.getAttribute(Columns.EST_IDENTIFICADOR);
                    body.append("<option value=").append(TextHelper.forHtmlAttribute(fieldValueEst)).append((String) ((!TextHelper.isNull(estCodigo) && estCodigo.equalsIgnoreCase(fieldValueEst)) ? " selected " : "")).append(">").append(TextHelper.forHtmlContent(fieldLabelEst)).append("</option>");
                }
                body.append("</select>").append("</div>").append("</div>");
            } else if (!TextHelper.isNull(papCodigo) && papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {
                body.append("<div class=\"row\"\n><div class=\"form-group col-sm-6\">\n <label for=\"ORG_CODIGO\">" + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel) + "</label>\n" +
                        "<select class=\"form-control form-select\" id=\"ORG_CODIGO\" name=\"ORG_CODIGO\" onChange=\"vf_nome_arquivo(); alterarTipoArquivo();\" onFocus=\"SetarEventoMascara(this,'#*200',true);\" onBlur=\"fout(this);\">\n" +
                        "<option value=\"\"").append((String) (TextHelper.isNull(orgCodigo) ? " selected " : " ")).append(">").append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)).append("</option>");
                Iterator<?> iteOrg = lstOrgaos.iterator();
                while (iteOrg.hasNext()) {
                    TransferObject ctoOrg = (TransferObject) iteOrg.next();
                    String fieldValueOrg = ctoOrg.getAttribute(Columns.ORG_CODIGO).toString();
                    String fieldLabelOrg = ctoOrg.getAttribute(Columns.ORG_NOME) + " - " + ctoOrg.getAttribute(Columns.ORG_IDENTIFICADOR);
                    body.append("<option value=").append(TextHelper.forHtmlAttribute(fieldValueOrg)).append((String) ((!TextHelper.isNull(orgCodigo) && orgCodigo.equalsIgnoreCase(fieldValueOrg)) ? " selected " : "")).append(">").append(TextHelper.forHtmlContent(fieldLabelOrg)).append("</option>");
                }
                body.append("</select>").append("</div>").append("</div>");
            }
        }
        if (responsavel.isOrg() && temPermissaoEst) {
            body.append("<input name=\"EST_CODIGO\" type=\"HIDDEN\" value=\"").append(TextHelper.forHtmlAttribute(responsavel.getEstCodigo())).append("\">");
        } else if (responsavel.isOrg()) {
            body.append(" <input name=\"ORG_CODIGO\" type=\"HIDDEN\" value=\"").append(TextHelper.forHtmlAttribute(responsavel.getOrgCodigo())).append("\">");
        }
        if (exibeCampoUpload) {
            body.append("  <div class=\"row\">\n" +
                    "            <div class=\"form-group col-sm-12\">\n" +
                    "              <label for=\"arquivo\"><hl:message key=\"rotulo.upload.arquivo.arquivo\"/></label>\n" +
                    "              <input type=\"file\" class=\"form-control\" id=\"arquivo\" name=\"FILE1\" onChange=\"vf_nome_arquivo();\">\n" +
                    "            </div>\n" +
                    "          </div>");
        }
        if (comentario) {
            body.append("  <div class=\"row\">\n" +
                            "            <div class=\"form-group col-sm-12\">\n" +
                            "              <label for=\"obs\"><hl:message key=\"rotulo.upload.arquivo.observacao\"/></label>\n" +
                            "              <hl:htmlinput name=\"obs\" type=\"textarea\" placeHolder=\"").append(ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)).append("\" classe=\"form-control\" di=\"obs\" rows=\"6\" others=\"onFocus=\\\"SetarEventoMascara(this,'#*65000',true);\\\" onBlur=\\\"fout(this);ValidaMascara(this);\\\"\"/>\n")
                    .append("</div></div>");
        }
        body.append("<div class=\"row\">\n");
        if (exibeCaptcha || exibeCaptchaDeficiente) {
            body.append("<div class=\"form-group col-sm-5\">\n" +
                    "              <label for=\"captcha\">" + ApplicationResourcesHelper.getMessage("rotulo.captcha.codigo", responsavel) + "</label>\n" +
                    "          \t  <input type=\"text\" class=\"form-control\" id=\"captcha\" name=\"captcha\" placeholder=" + ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.codigo.acesso", responsavel) + ">\n" +
                    "          \t</div>");
        }
        body.append(" <div class=\"form-group col-sm-6\">\n" +
                    "\t<div class=\"captcha\">\n");

        if (exibeCaptcha) {
            body.append("<img name=\"captcha_img\" src=\"../captcha.jpg?t="+DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss") +"\"" + "/>")
                    .append(" <a href=\"#no-back\" class=\"btn-i-right pr-1\" data-bs-toggle=\"popover\" title=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel))
                    .append("\" data-bs-content=\"" + ApplicationResourcesHelper.getMessage("mensagem.ajuda.captcha.operacao.imagem.v3", responsavel) + "\" data-original-title=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + ">")
                    .append(" <img src=\"../img/icones/help.png\" alt=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "title=\"" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "\" aborder=\"0\">\n</a>")
                    .append(" <a href=\"#no-back\" onclick=reloadCaptcha(); ><img src=../img/icones/refresh.png alt=\"" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.codigo", responsavel) + "\" title=\"" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.codigo", responsavel) + "\" border=\"0\"/></a>\n");

        } else if (exibeCaptchaAvancado) {
            body.append("<div class=\"g-recaptcha\" data-sitekey=\"").append(chavePublica).append("\"></div>");
        } else if (exibeCaptchaDeficiente) {
            body.append("<div id=\"divCaptchaSound\"></div>\n" +
                    "                    <a href=\"#no-back\" onclick=\"reloadSimpleCaptcha()\"><img src=\"../img/icones/refresh.png\" alt=" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.audio", responsavel) + "title=" + ApplicationResourcesHelper.getMessage("rotulo.captcha.novo.audio", responsavel) + "border=\"0\"/></a>\n" +
                    "                    <a href=\"#no-back\" onclick=\"helpCaptcha3();\"><img src=\"../img/icones/help.png\" alt=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "title=" + ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel) + "border=\"0\"/></a>\n");
        }
        body.append("</div></div></div>");
        return body.toString();
    }

    public String montaFooter(AcessoSistema responsavel) {
        String footer = "";
        footer = "</form>" +
                "</div>" +
                "</div>";

        return footer;
    }
}
