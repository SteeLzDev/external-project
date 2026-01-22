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
import com.zetra.econsig.service.servidor.ReclamacaoRegistroServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroMotivoReclamacaoTag</p>
 * <p>Description: Tag para filtro de motivo de reclamacao layout v4.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroMotivoReclamacaoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroMotivoReclamacaoTag.class);

    @Autowired
    private ReclamacaoRegistroServidorController reclamacaoRegistroServidorController;

    protected String disabled;
    protected String descricao;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpSession session = pageContext.getSession();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            Map<String, Boolean> selecionados = new HashMap<>();
            if (request.getParameter("TMR_CODIGO") != null) {
                String[] codigos = request.getParameterValues("TMR_CODIGO");
                for (String codigo : codigos) {
                    String srsCodigo = (codigo.split(",|;"))[0];
                    selecionados.put(srsCodigo, Boolean.TRUE);
                }
            }

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            List<TransferObject> motivoReclamacao = null;
            try {
                motivoReclamacao = reclamacaoRegistroServidorController.lstTipoMotivoReclamacao(responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            if (motivoReclamacao != null && motivoReclamacao.size() > 0) {
                Iterator<TransferObject> it = motivoReclamacao.iterator();
                TransferObject nextMotivo = null;
                String tmrCodigo = null;
                String tmrDescricao = null;
                int contador = 0;
                StringBuilder identificadores = new StringBuilder();
                StringBuilder linhas = new StringBuilder();
                while (it.hasNext()) {
                    nextMotivo = it.next();
                    tmrCodigo = (String) nextMotivo.getAttribute(Columns.TMR_CODIGO);
                    tmrDescricao = (String) nextMotivo.getAttribute(Columns.TMR_DESCRICAO);

                    String id = "TMR_CODIGO" + contador++;
                    identificadores.append(id);
                    if (it.hasNext()) {
                        identificadores.append(";");
                    }

                    linhas.append(abrirLinha());

                    linhas.append(gerarCheckbox(tmrCodigo, tmrDescricao, id, selecionados.containsKey(tmrCodigo)));

                    linhas.append(gerarLabel(tmrDescricao, id));

                    linhas.append(fecharLinha());
                }

                if (!TextHelper.isNull(descricao)) {
                    code.append(abrirCampo(identificadores));
                }

                code.append(abrirTabela());
                code.append(linhas);
                code.append(fecharTabela());

                if (!TextHelper.isNull(descricao)) {
                    code.append(fecharCampo());
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    @Override
    protected String abrirTabela() {
        StringBuilder code = new StringBuilder();
        code.append("<fieldset class=\"col-sm-12 col-md-12\">");
        return code.toString();
    }

    @Override
    protected String fecharTabela() {
        StringBuilder code = new StringBuilder();
        code.append("</fieldset>");
        return code.toString();
    }

    private String abrirCampo(StringBuilder identificadores) {
        StringBuilder code = new StringBuilder();
        code.append("<div class=\"col-sm-12\">");
        code.append("<div class=\"legend\">");
        code.append("<span>").append(descricao).append("</span>");
        code.append("</div>");
        code.append("</div>");
        code.append("<div class=\"form-check\">");
        return code.toString();
    }

    private String fecharCampo() {
        return "</div>";
    }

    private String abrirLinha() {
        StringBuilder code = new StringBuilder();
        code.append("<div class=\"row\">");
        code.append("<div class=\"col-sm-12 col-md-4\">");
        code.append("<span class=\"text-nowrap align-text-top\">");
        return code.toString();
    }

    private String fecharLinha() {
        StringBuilder code = new StringBuilder();
        code.append("</span>");
        code.append("</div>");
        code.append("</div>");
        return code.toString();
    }

    protected String gerarCheckbox(String tmrCodigo, String tmrDescricao, String id, Boolean selecionado) {
        StringBuilder linhas = new StringBuilder();

        linhas.append("<input type=\"checkbox\" ");
        linhas.append("name=\"TMR_CODIGO\" id=\"").append(id).append("\" ");
        linhas.append("title=\"").append(TextHelper.forHtmlAttribute(tmrDescricao)).append("\" ");
        linhas.append(selecionado ? " checked " : "");
        linhas.append("value=\"").append(TextHelper.forHtmlAttribute(tmrCodigo) + ";" + TextHelper.forHtmlAttribute(tmrDescricao)).append("\" ");
        linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        if (disabled != null && disabled.equals("true")) {
            linhas.append(" disabled ");
        }
        linhas.append("class=\"form-check-input ml-1\" ");
        linhas.append("onBlur=\"fout(this);ValidaMascara(this);\">");

        return linhas.toString();
    }

    private String gerarLabel(String tmrDescricao, String id) {
        StringBuilder code = new StringBuilder();
        code.append("<label class=\"form-check-label labelSemNegrito ml-1\" for=\"").append(TextHelper.forHtmlContent(id)).append("\">");
        code.append(TextHelper.forHtmlContent(tmrDescricao));
        code.append("</label>");
        return code.toString();
    }
}
