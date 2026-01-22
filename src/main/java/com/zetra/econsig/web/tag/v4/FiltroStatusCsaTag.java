package com.zetra.econsig.web.tag.v4;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroStatusCsaTag</p>
 * <p>Description: Tag para filtro de status de uma Consignatária layout v4.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
  * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroStatusCsaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroStatusCsaTag.class);

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
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            Map<String, Boolean> selecionados = new HashMap<>();
            if (request.getParameter("CSA_ATIVO") != null) {
                String[] codigos = request.getParameterValues("CSA_ATIVO");
                for (String codigo : codigos) {
                    String csaAtivo = (codigo.split(",|;"))[0];
                    selecionados.put(csaAtivo, Boolean.TRUE);
                }
            }

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            StringBuilder linhas = new StringBuilder();
            linhas.append(abrirLinha());
            linhas.append(abrirColuna());

            linhas.append(gerarCheckbox("CSA_ATIVO_1", "1", selecionados.containsKey("1"), ApplicationResourcesHelper.getMessage("rotulo.status.ativo", responsavel)));
            linhas.append(gerarLabel("CSA_ATIVO_1", ApplicationResourcesHelper.getMessage("rotulo.status.ativo", responsavel)));

            linhas.append(fecharColuna());
            linhas.append(fecharLinha());

            linhas.append(abrirLinha());
            linhas.append(abrirColuna());

            linhas.append(gerarCheckbox("CSA_ATIVO_0", "0", selecionados.containsKey("0"), ApplicationResourcesHelper.getMessage("rotulo.status.inativo", responsavel)));
            linhas.append(gerarLabel("CSA_ATIVO_0", ApplicationResourcesHelper.getMessage("rotulo.status.inativo", responsavel)));

            linhas.append(fecharColuna());
            linhas.append(fecharLinha());

            linhas.append(abrirLinha());
            linhas.append(abrirColuna());

            linhas.append(gerarCheckbox("CSA_ATIVO_2", "2", selecionados.containsKey("2"), ApplicationResourcesHelper.getMessage("rotulo.status.indisponivel", responsavel)));
            linhas.append(gerarLabel("CSA_ATIVO_2", ApplicationResourcesHelper.getMessage("rotulo.status.indisponivel", responsavel)));

            linhas.append(fecharColuna());
            linhas.append(fecharLinha());

            if (!TextHelper.isNull(descricao)) {
                code.append(abrirDescricao());
            }

            code.append(abrirTabela());
            code.append(linhas);
            code.append(fecharTabela());

            if (!TextHelper.isNull(descricao)) {
                code.append(fecharDescricao());
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
        return "<div class=\"row\">";
    }

    @Override
    protected String fecharTabela() {
        return "</div>";
    }

    private String abrirDescricao() {
        StringBuilder code = new StringBuilder();
        code.append("<fieldset class=\"col-sm-12 col-md-12\">");
        code.append("<div class=\"legend\">");
        code.append("<span>").append(descricao).append("</span>");
        code.append("</div>");
        code.append("<div class=\"form-check\">");
        return code.toString();
    }

    private String fecharDescricao() {
        StringBuilder code = new StringBuilder();
        code.append("</div>");
        code.append("</fieldset>");
        return code.toString();
    }

    private String abrirLinha() {
        return "<div class=\"col-sm-12 col-md-4\">";
    }

    private String fecharLinha() {
        return "</div>";
    }

    private String abrirColuna() {
        return "<span class=\"text-nowrap align-text-top\">";
    }

    private String fecharColuna() {
        return "</span>";
    }

    private String gerarCheckbox(String id, String value, Boolean selecionado, String title) {
        StringBuilder linhas = new StringBuilder();
        linhas.append("<input type=\"checkbox\" ");
        linhas.append("name=\"CSA_ATIVO\" id=\"").append(id).append("\" ");
        linhas.append("title=\"").append(title).append("\" ");
        linhas.append(selecionado ? " checked " : "");
        linhas.append("value=\"").append(value).append("\" ");
        linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        if (disabled != null && disabled.equals("true")) {
            linhas.append(" disabled ");
        }
        linhas.append("class=\"form-check-input ml-1\" ");
        linhas.append("onBlur=\"fout(this);ValidaMascara(this);\">");
        return linhas.toString();
    }

    private String gerarLabel(String id, String texto) {
        StringBuilder code = new StringBuilder();
        code.append("<label class=\"form-check-label labelSemNegrito ml-1\" for=\"").append(id).append("\">");
        code.append(texto);
        code.append("</label>");
        return code.toString();
    }

}
