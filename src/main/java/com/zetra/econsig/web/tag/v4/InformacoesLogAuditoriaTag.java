package com.zetra.econsig.web.tag.v4;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.exception.AuditoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.auditoria.AuditoriaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: InformacoesLogAuditoriaTag</p>
 * <p>Description: Tag para impressão de informações do log de auditoria para o usuário.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InformacoesLogAuditoriaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformacoesLogAuditoriaTag.class);

    @Autowired
    private AuditoriaController auditoriaController;

    private AcessoSistema responsavel;

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    public String generateHtml() {
        responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());

        String codigoEntidade = responsavel.getCodigoEntidade();
        String tipoEntidade = responsavel.getTipoEntidade();

        boolean usuarioAuditor = responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR);

        // Se o usuário não é auditor ou não é consignante ou suporte, não há o que ser carregado
        if (!usuarioAuditor || !responsavel.isCseSup()) {
            return "";
        }

        int qtdeLogNaoAuditado = 0;
        try {
            qtdeLogNaoAuditado = auditoriaController.qtdeLogAuditoriaQuery(codigoEntidade, tipoEntidade, true, null, responsavel);

        } catch (AuditoriaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        StringBuilder html = new StringBuilder();

        if (usuarioAuditor && qtdeLogNaoAuditado > 0) {

            html.append("<div class=\"card\">");
            html.append("<div class=\"card-header hasIcon\">");
            html.append("<span class=\"card-header-icon\"> <svg width=\"26\">");
            html.append("<use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#i-box\"></use>");
            html.append("</svg>");
            html.append("</span>");
            html.append("<h2 class=\"card-header-title\">");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.auditoria.pendente", responsavel));
            html.append("</h2>");
            html.append("</div>");
            html.append("<div class=\"card-body\">");
            html.append("<ul class=\"list-links\">");
            html.append("<li>");
            html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.mais.detalhes.clique.aqui", responsavel));
            html.append("\" onClick=\"postData('../v3/auditarOperacoes?acao=iniciar')\">");
            html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.registros.auditoria.nao.visualizados", responsavel)).append("</a>");
            html.append("</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</div>");
        }
        return html.toString();
    }

}
