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
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: FiltroStatusLoginTag</p>
 * <p>Description: Tag para filtro de status login layout v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroStatusLoginTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FiltroStatusLoginTag.class);

    @Autowired
    private UsuarioController usuarioController;

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
            if (request.getParameter("STU_CODIGO") != null) {
                String[] codigos = request.getParameterValues("STU_CODIGO");
                for (String codigo : codigos) {
                    String stuCodigo = (codigo.split(",|;"))[0];
                    selecionados.put(stuCodigo, Boolean.TRUE);
                }
            }

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            List<TransferObject> status = null;
            try {
                status = usuarioController.lstStatusLogin(responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            if (status != null && status.size() > 0) {
                int contador = 0;
                StringBuilder identificadores = new StringBuilder();
                StringBuilder linhas = new StringBuilder();

                Iterator<TransferObject> it = status.iterator();
                TransferObject nextStatus = null;
                Object stuCodigo = null;
                String stuDescricao = "";
                while(it.hasNext()) {
                  nextStatus = it.next();
                  stuCodigo = nextStatus.getAttribute(Columns.STU_CODIGO).toString();
                  stuDescricao = nextStatus.getAttribute(Columns.STU_DESCRICAO).toString();

                  String id = "STU_CODIGO" + contador++;
                  identificadores.append(id);
                  if (it.hasNext()) {
                      identificadores.append(";");
                  }

                  linhas.append(abrirLinha());
                  linhas.append(abrirColuna());
                  linhas.append(gerarCheckbox(id, stuCodigo, stuDescricao, selecionados.containsKey(stuCodigo)));
                  linhas.append(gerarLabel(id, stuDescricao));
                  linhas.append(fecharColuna());

                  if (it.hasNext()) {
                      nextStatus = it.next();
                      stuCodigo = nextStatus.getAttribute(Columns.STU_CODIGO).toString();
                      stuDescricao = nextStatus.getAttribute(Columns.STU_DESCRICAO).toString();

                      id = "STU_CODIGO" + contador++;
                      identificadores.append(id);
                      if (it.hasNext()) {
                          identificadores.append(";");
                      }

                      linhas.append(abrirColuna());
                      linhas.append(gerarCheckbox(id, stuCodigo, stuDescricao, selecionados.containsKey(stuCodigo)));
                      linhas.append(gerarLabel(id, stuDescricao));
                      linhas.append(fecharColuna());
                      linhas.append(fecharLinha());
                  }
                }

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
        return "<div class=\"row\">";
    }

    @Override
    protected String fecharTabela() {
        return "</div>";
    }

    private String abrirLinha() {
        return "";
    }

    private String fecharLinha() {
        return "";
    }

    private String abrirColuna() {
        StringBuilder code = new StringBuilder();
        code.append("<div class=\"col-sm-12 col-md-4\">");
        code.append("<span class=\"text-nowrap align-text-top\">");
        return code.toString();
    }

    private String fecharColuna() {
        StringBuilder code = new StringBuilder();
        code.append("</div>");
        code.append("</span>");
        return code.toString();
    }

    private String abrirDescricao(StringBuilder identificadores) {
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

    private String gerarCheckbox(String id, Object stuCodigo, String stuDescricao, Boolean selecionado) {
        StringBuilder linhas = new StringBuilder();
        linhas.append("<INPUT TYPE=\"CHECKBOX\" ");
        linhas.append("NAME=\"STU_CODIGO\" ID=\"").append(id).append("\" ");
        linhas.append("TITLE=\"").append(TextHelper.forHtmlAttribute(stuDescricao)).append("\" ");
        linhas.append("VALUE=\"").append(TextHelper.forHtmlAttribute(stuCodigo)).append("\" ");
        linhas.append(selecionado ? " checked " : "");
        if (disabled != null && disabled.equals("true")) {
            linhas.append(" disabled ");
        }
        linhas.append("class=\"form-check-input ml-1\" ");
        linhas.append("onFocus=\"SetarEventoMascara(this,'#*200',true);\" ");
        linhas.append("onBlur=\"fout(this);ValidaMascara(this);\">");
        return linhas.toString();
    }


    private String gerarLabel(String id, String stuDescricao) {
        StringBuilder code = new StringBuilder();
        code.append("<label class=\"form-check-label labelSemNegrito ml-1\" for=\"").append(id).append("\">");
        code.append(TextHelper.forHtmlContent(stuDescricao));
        code.append("</label>");
        return code.toString();
    }
}
