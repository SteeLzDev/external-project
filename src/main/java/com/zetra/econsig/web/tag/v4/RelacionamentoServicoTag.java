package com.zetra.econsig.web.tag.v4;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class RelacionamentoServicoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelacionamentoServicoTag.class);

    // Nome do atributo que contém os dados do relacionamento
    protected String name;
    // Escopo do atributo que contém os dados do relacionamento
    protected String scope;
    // Título do atributo a ser exibido na página
    protected String title;
    // Chave do atributo a ser exibido na página
    protected String key;
    // Indica se está desabilitado o campo
    protected boolean disabled;

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setKey(String titleKey) {
        key = titleKey;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            // Obtém a lista com os relacionamentos
            @SuppressWarnings("unchecked")
            List<TransferObject> relacionamentoSvc = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            // Gera o resultado
            pageContext.getOut().print(geraHTML(relacionamentoSvc));

            return EVAL_PAGE;

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String geraHTML(List<TransferObject> relacionamentoSvc) throws ParseException {

        // Recupera o responsavel
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        code.append("<div class=\"row\">");
        code.append("  <div class=\"form-group col-sm-12 col-md-6 mb-0\">");
        String descricaoSemBR = "";
        if(StringUtils.isEmpty(key)){
            descricaoSemBR = title.replace("<br>", " ");
        } else {
            descricaoSemBR = key.replace("<br>", " ");
        }
        code.append("<label for='").append(name).append("'>").append(descricaoSemBR).append("</label>");
        code.append("<select name=\"").append(name).append("\" id=\"").append(name).append("\" class=\"form-control form-select\" multiple='multiple' SIZE=\"6\" ");
        if (disabled) {
            code.append(" disabled");
        }
        code.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        code.append(" onBlur=\"fout(this);ValidaMascara(this);\"> ");

        Iterator<TransferObject> it = relacionamentoSvc.iterator();
        TransferObject cto = null;
        while (it.hasNext()) {
            cto = it.next();
            code.append("<option value=\"").append(TextHelper.forHtmlAttribute(cto.getAttribute(Columns.SVC_CODIGO))).append("\" ").append(TextHelper.forHtmlAttribute(cto.getAttribute("SELECTED"))).append(">");
            code.append(TextHelper.forHtmlContent(cto.getAttribute(Columns.SVC_IDENTIFICADOR)) + " - " + TextHelper.forHtmlContent(cto.getAttribute(Columns.SVC_DESCRICAO)));
            code.append("</option>");
        }
        code.append("</select>");
        code.append("<div><p></p></div>");

        if (!disabled) {
          String msgCtrl = ApplicationResourcesHelper.getMessage("rotulo.relacionamento.servico.ctrl", responsavel);
          code.append("<div class='slider mt-2 col-sm-12 col-md-12 pl-0 pr-0'>");
          code.append("  <div class='tooltip-inner'>").append(msgCtrl).append("</div>");
          code.append("</div>");
          code.append("<div class='btn-action float-end mt-3'>");
          String msgLimparSelecao = ApplicationResourcesHelper.getMessage("mensagem.limpar.selecao", responsavel);
          code.append("  <a class='btn btn-outline-danger' href='#' onclick=\"desmarcarSelecao('").append(name).append("')\">").append(msgLimparSelecao).append("</a>");
          code.append("</div>");
        }
        code.append("  </div>");
        code.append("</div>");

        return code.toString();
    }
}
