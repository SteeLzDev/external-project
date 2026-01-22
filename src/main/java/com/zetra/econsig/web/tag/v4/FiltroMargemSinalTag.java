package com.zetra.econsig.web.tag.v4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroMargemSinalTag</p>
 * <p>Description: Tag para filtro de margem layout v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroMargemSinalTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroMargemSinalTag.class);

    @Autowired
    private MargemController margemController;

    private String disabled;
    private String descricao;
    private String obrigatoriedade;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setObrigatoriedade(String obrigatoriedade) {
        this.obrigatoriedade = obrigatoriedade;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpSession session = pageContext.getSession();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            List<MargemTO> margens = null;
            try {
                margens = margemController.lstMargemRaiz(responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Rótulos
            String rotuloMargemPositiva = ApplicationResourcesHelper.getMessage("rotulo.positiva.singular", responsavel);
            String rotuloMargemZerada = ApplicationResourcesHelper.getMessage("rotulo.zerada.singular", responsavel);
            String rotuloMargemNegativa = ApplicationResourcesHelper.getMessage("rotulo.negativa.singular", responsavel);

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            if (margens != null && margens.size() > 0) {
                Iterator<MargemTO> it = margens.iterator();
                int contador = 0;
                StringBuilder identificadores = new StringBuilder();
                StringBuilder linhas = new StringBuilder();
                List<Short> marCodigos = new ArrayList<>();
                while (it.hasNext()) {
                    MargemTO margem = it.next();
                    Short marCodigo = margem.getMarCodigo();
                    String marDescricao = margem.getMarDescricao();
                    Character exibeMargem = ExibeMargem.NAO_EXIBE;
                    if (responsavel.isCse()) {
                        exibeMargem = margem.getMarExibeCse();
                    } else if (responsavel.isSup()) {
                        exibeMargem = margem.getMarExibeSup();
                    } else if (responsavel.isOrg()) {
                        exibeMargem = margem.getMarExibeOrg();
                    } else if (responsavel.isCsa()) {
                        exibeMargem = margem.getMarExibeCsa();
                    } else if (responsavel.isCor()) {
                        exibeMargem = margem.getMarExibeCor();
                    } else if (responsavel.isSer()) {
                        exibeMargem = margem.getMarExibeSer();
                    }
                    String id = "SINAL" + marCodigo;
                    if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && !ExibeMargem.NAO_EXIBE.equals(exibeMargem)) {
                        marCodigos.add(marCodigo);
                        String[] selecionados = request.getParameterValues("SINAL" + marCodigo);
                        boolean positivoSelecionado = (selecionados != null && Arrays.binarySearch(selecionados, "1") >= 0);
                        boolean zeradoSelecionado = (selecionados != null && Arrays.binarySearch(selecionados, "0") >= 0);
                        boolean negativoSelecionado = (selecionados != null && Arrays.binarySearch(selecionados, "-1") >= 0);
                        String identificador = "lblMargemSinal" + contador++;
                        identificadores.append(identificador);
                        identificadores.append(";");

                        linhas.append(abrirLinha(marDescricao, identificador));

                        identificador = id + contador++;
                        identificadores.append(identificador);
                        identificadores.append(";");

                        // Gera check margem positiva
                        linhas.append(gerarCheckBox(marCodigo, identificador, "1", rotuloMargemPositiva, positivoSelecionado));

                        identificador = id + contador++;
                        identificadores.append(identificador);
                        identificadores.append(";");

                        // Gera check margem zerada
                        linhas.append(gerarCheckBox(marCodigo, identificador, "0", rotuloMargemZerada, zeradoSelecionado));

                        identificador = id + contador++;
                        identificadores.append(identificador);
                        if (it.hasNext()) {
                            identificadores.append(";");
                        }

                        // Gera check margem zerada
                        linhas.append(gerarCheckBox(marCodigo, identificador, "-1", rotuloMargemNegativa, negativoSelecionado));

                        linhas.append(fecharLinha());
                    }
                }

                // Insere javascript
                code.append(gerarJavaScript(marCodigos));
                if (!TextHelper.isNull(descricao)) {
                    code.append(abrirDescricao(identificadores));
                }

                code.append(abrirTabela());
                code.append(linhas);
                code.append(fecharTabela());

                if (!TextHelper.isNull(descricao)) {
                    code.append(fecharDescricao());
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
        code.append("<div class=\"row\">");
        code.append("<div class=\"col-sm-12 col-md-12\">");
        return code.toString();
    }

    @Override
    protected String fecharTabela() {
        StringBuilder code = new StringBuilder();
        code.append("</div>");
        code.append("</div>");
        return code.toString();
    }

    private String abrirDescricao(StringBuilder identificadores) {
        StringBuilder code = new StringBuilder();

        code.append("<fieldset class=\"col-sm-12 col-md-12\">");
        code.append("<div class=\"legend\">");
        code.append("<span>").append(TextHelper.forHtmlContent(descricao)).append("</span>");
        code.append("</div>");

        return code.toString();
    }

    private String fecharDescricao() {
        return "</fieldset>";
    }

    private String abrirLinha(String marDescricao, String identificador) {
        StringBuilder linhas = new StringBuilder();

        linhas.append("<div class=\"form-group mb-1\" role=\"checkbox\">");
        linhas.append("<span>").append(TextHelper.forHtmlContent(marDescricao)).append("</span>");
        linhas.append("<div class=\"form-check pt-2\">");
        linhas.append("<div class=\"row\">");

        return linhas.toString();
    }

    private String fecharLinha() {
        StringBuilder linhas = new StringBuilder();

        linhas.append("</div>");
        linhas.append("</div>");
        linhas.append("</div>");

        return linhas.toString();
    }

    private String gerarCheckBox(Short marCodigo, String identificador, String value, String title, boolean selecionado) {
        StringBuilder linhas =  new StringBuilder();

        linhas.append("<div class=\"col-sm-2 col-md-2\">");
        linhas.append("<input type=\"checkbox\" ");
        linhas.append("name=\"SINAL").append(TextHelper.forHtmlAttribute(marCodigo)).append("\" id=\"").append(TextHelper.forHtmlAttribute(identificador)).append("\" ");
        linhas.append("value=\"").append(value).append("\"  ");
        linhas.append("title=\"" + title + "\" ");
        if (selecionado) {
            linhas.append(" checked ");
        }
        linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        linhas.append("onBlur=\"fout(this);ValidaMascara(this);\" ");
        if (disabled.equalsIgnoreCase("true")) {
            linhas.append(" disabled ");
        }

        linhas.append(" class=\"form-check-input ml-1\"");
        linhas.append(">");

        linhas.append("<label class=\"form-check-label labelSemNegrito ml-1 pr-4\" for=\"").append(TextHelper.forHtmlAttribute(identificador)).append("\">").append(TextHelper.forHtmlAttribute(title)).append("</label>");
        linhas.append("</div>");

        return linhas.toString();
    }

    private String gerarJavaScript(List<Short> marCodigos) {
        StringBuilder html = new StringBuilder();
        html.append("<script type=\"text/JavaScript\">");
        html.append("function valida_campo_margem_sinal() {");
        if (obrigatoriedade.equals("true")) {
            html.append("    var descSinalMargem = '").append(TextHelper.forJavaScriptBlock(descricao)).append("';");
            html.append("    var qtd = 0;");
            for (Short marCodigo : marCodigos) {
                html.append("      for(var i = 0; i < 3; i++) {");
                html.append("        if (document.forms[0].SINAL").append(TextHelper.forJavaScriptBlock(marCodigo)).append("[i].checked == true) {");
                html.append("          qtd++");
                html.append("        }");
                html.append("      }");
            }
            html.append("    if (qtd <= 0) {");
            html.append("      alert('" + ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.um.arg0", null, "' + descSinalMargem + '") + "');");
            html.append("      return false;");
            html.append("    }");
        }
        html.append("    return true;");
        html.append("}");
        html.append("</script>");

        return html.toString();
    }
}
