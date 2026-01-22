package com.zetra.econsig.web.tag.v4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroStatusRegistroServidorTag</p>
 * <p>Description: Tag para filtro de status de um servidor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroStatusRegistroServidorTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroStatusRegistroServidorTag.class);

    private static final long serialVersionUID = 2L;

    @Autowired
    private ServidorController servidorController;

    private String disabled;
    private String descricao;

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpSession session = pageContext.getSession();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            Map<String, Boolean> selecionados = new HashMap<>();
            if (request.getParameter("SRS_CODIGO") != null) {
                String[] codigos = request.getParameterValues("SRS_CODIGO");
                for (String codigo : codigos) {
                    String srsCodigo = (codigo.split(",|;"))[0];
                    selecionados.put(srsCodigo, Boolean.TRUE);
                }
            }

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            List<TransferObject> status = null;
            try {
                status = servidorController.lstStatusRegistroServidor(false, false, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();
            if (status != null && status.size() > 0) {
                Iterator<TransferObject> it = status.iterator();
                TransferObject nextStatus = null;
                String srsCodigo = null;
                String srsDescricao = null;
                int contador = 0;
                StringBuilder linhas = new StringBuilder();
                while (it.hasNext()) {
                    nextStatus = it.next();
                    srsCodigo = (String) nextStatus.getAttribute(Columns.SRS_CODIGO);
                    srsDescricao = (String) nextStatus.getAttribute(Columns.SRS_DESCRICAO);

                    String id = "SRS_CODIGO" + contador++;

                    linhas.append("<div class=\"col-sm-12 col-md-2\">");
                    linhas.append("  <span class=\"align-text-top\">");
                    linhas.append("    <input type=\"checkbox\" class=\"form-check-input ml-1\" ");
                    linhas.append("name=\"SRS_CODIGO\" id=\"").append(id).append("\" ");
                    linhas.append("title=\"").append(TextHelper.forHtmlAttribute(srsDescricao)).append("\" ");
                    linhas.append((selecionados.containsKey(srsCodigo)) ? " checked " : "");
                    linhas.append("value=\"").append(TextHelper.forHtmlAttribute(srsCodigo) + ";" + TextHelper.forHtmlAttribute(srsDescricao)).append("\" ");
                    linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
                    if (disabled != null && disabled.equals("true")) {
                        linhas.append(" disabled ");
                    }
                    linhas.append("onBlur=\"fout(this);ValidaMascara(this);\">");
                    linhas.append("    <label class=\"form-check-label labelSemNegrito ml-1\" for=\"").append(id).append("\">").append(TextHelper.forHtmlContent(srsDescricao)).append("</label>");
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