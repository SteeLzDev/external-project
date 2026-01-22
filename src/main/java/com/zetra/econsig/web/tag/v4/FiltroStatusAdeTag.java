package com.zetra.econsig.web.tag.v4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroStatusAdeTag</p>
 * <p>Description: Tag para filtro de status de uma consignação.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroStatusAdeTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroStatusAdeTag.class);

    private static final long serialVersionUID = 2L;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    private String disabled;
    private String descricao;

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpSession session = pageContext.getSession();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            Map<String, Boolean> selecionados = new HashMap<>();
            if (request.getParameter("SAD_CODIGO") != null) {
                String[] codigos = request.getParameterValues("SAD_CODIGO");
                for (String codigo : codigos) {
                    selecionados.put(codigo, Boolean.TRUE);
                }
            }

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            List<?> status = null;
            try {
                status = pesquisarConsignacaoController.lstStatusAutorizacao(null, true, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            if (status != null && status.size() > 0) {
                Iterator<?> it = status.iterator();
                CustomTransferObject nextStatus = null;
                String sadCodigo = null;
                String sadDescricao = null;
                int contador = 0;
                StringBuilder linhas = new StringBuilder();
                while (it.hasNext()) {
                    nextStatus = (CustomTransferObject) it.next();
                    sadCodigo = (String) nextStatus.getAttribute(Columns.SAD_CODIGO);
                    sadDescricao = (String) nextStatus.getAttribute(Columns.SAD_DESCRICAO);

                    String id = "SAD_CODIGO" + contador++;

                    linhas.append("<div class=\"col-sm-12 col-md-4\">");
                    linhas.append("  <span class=\"text-nowrap align-text-top\">");
                    linhas.append("    <input type=\"checkbox\" class=\"form-check-input ml-1\" ");
                    linhas.append("name=\"SAD_CODIGO\" id=\"").append(id).append("\" ");
                    linhas.append("title=\"").append(TextHelper.forHtmlAttribute(sadDescricao)).append("\" ");
                    linhas.append((selecionados.containsKey(sadCodigo)) ? " checked " : "");
                    linhas.append("value=\"").append(TextHelper.forHtmlAttribute(sadCodigo)).append("\" ");
                    linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
                    if (disabled != null && disabled.equals("true")) {
                        linhas.append(" disabled ");
                    }
                    linhas.append("onBlur=\"fout(this);ValidaMascara(this);\">");
                    linhas.append("    <label class=\"form-check-label labelSemNegrito ml-1\" for=\"").append(id).append("\">").append(TextHelper.forHtmlContent(sadDescricao)).append("</label>");
                    linhas.append("  </span>");
                    linhas.append("</div>");
                }
                code.append("<fieldset class=\"col-sm-12 col-md-12\">");
                if (!TextHelper.isNull(descricao)) {
                    code.append("<div class=\"legend\"><span>").append(descricao).append("</span></div>");
                }
                code.append("  <div class=\"form-check\">");
                code.append("    <div class=\"row\">");
                code.append(linhas);
                code.append("    </div>");
                code.append("  </div>");
                code.append("</fieldset>");
            }
            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
