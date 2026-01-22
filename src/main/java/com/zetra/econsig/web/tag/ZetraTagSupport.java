package com.zetra.econsig.web.tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ZetraTagSupport</p>
 * <p>Description: Tag base do sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ZetraTagSupport extends RequestContextAwareTag {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ZetraTagSupport.class);

    private boolean wired;

    @Override
    protected int doStartTagInternal() throws Exception {
        if (!wired) {
            ApplicationContextProvider
                    .getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .autowireBean(this);
            wired = true;
        }
        return SKIP_BODY;
    }

    /*****************************************************************************************/
    /** Funções Auxiliares **/

    protected int getScopeAsInt(String scope) {
        if (scope != null) {
            if (scope.equals("request")) {
                return PageContext.REQUEST_SCOPE;
            }
            if (scope.equals("session")) {
                return PageContext.SESSION_SCOPE;
            }
            if (scope.equals("application")) {
                return PageContext.APPLICATION_SCOPE;
            }
        }

        // Default
        return PageContext.PAGE_SCOPE;
    }

    /*****************************************************************************************/
    /** Funções para montar o design HTML **/

    /**
     * montarLinha - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @return
     */
    protected String montarLinha(String descricao, Object valor) {
        return montarLinha(descricao, valor, null, null, null);
    }

    /**
     * montarLinha - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param fieldKey
     * @return
     */
    protected String montarLinha(String descricao, Object valor, String fieldKey) {
        return montarLinha(descricao, valor, null, null, fieldKey);
    }

    /**
     * montarLinha - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param fieldKey
     * @param tooltip
     * @return
     */
    protected String montarLinhaTooltip(String descricao, Object valor, String fieldKey, String tooltip) {
        return montarLinhaTooltip(descricao, valor, null, null, fieldKey,tooltip);
    }

    /**
     * montarLinha - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @return
     */
    protected String montarLinha(String descricao, Object valor, String descricaoCss, String valorCss) {
        return montarLinha(descricao, valor, descricaoCss, valorCss, null);
    }

    /**
     * montarLinha - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @param fieldKey
     * @return
     */
    protected String montarLinha(String descricao, Object valor, String descricaoCss, String valorCss, String fieldKey) {
        try {
            if (TextHelper.isNull(fieldKey)) {
                return JspHelper.gerarLinhaTabela(descricao, valor, descricaoCss, valorCss);
            } else {
                AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest)pageContext.getRequest());
                if (ShowFieldHelper.showField(fieldKey, responsavel)) {
                    return JspHelper.gerarLinhaTabela(descricao, valor, descricaoCss, valorCss);
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * montarLinhaTooltip - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @param fieldKey
     * @param tooltip
     * @return
     */
    protected String montarLinhaTooltip(String descricao, Object valor, String descricaoCss, String valorCss, String fieldKey, String tooltip) {
        try {
            if (TextHelper.isNull(fieldKey)) {
                return JspHelper.gerarLinhaTabelaTooltip(descricao, valor, descricaoCss, valorCss,tooltip);
            } else {
                AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest)pageContext.getRequest());
                if (ShowFieldHelper.showField(fieldKey, responsavel)) {
                    return JspHelper.gerarLinhaTabelaTooltip(descricao, valor, descricaoCss, valorCss,tooltip);
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * montarLinha layout v4 - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @param fieldKey
     * @return
     */
    protected String montarLinhav4(String descricao, Object valor, String fieldKey) {
        try {
            if (TextHelper.isNull(fieldKey)) {
                return JspHelper.gerarLinhaTabelav4(descricao, valor, null, null, null);
            } else {
                AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest)pageContext.getRequest());
                if (ShowFieldHelper.showField(fieldKey, responsavel)) {
                    return JspHelper.gerarLinhaTabelav4(descricao, valor, null, null, null);
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * montarLinhaTooltip layout v4 - XSS: Deve assegurar que os parâmetros a serem exibidos são seguros antes de usar este método.
     * @param descricao - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param valor - XSS : usar forHtmlContent ou forHtmlContentComTags
     * @param descricaoCss - XSS : usar forHtmlAttribute
     * @param valorCss - XSS : usar forHtmlAttribute
     * @param fieldKey
     * @param tooltip
     * @return
     */
    protected String montarLinhaTooltipv4(String descricao, Object valor, String descricaoCss, String valorCss, String fieldKey, String tooltip) {
        try {
            if (TextHelper.isNull(fieldKey)) {
                return JspHelper.gerarLinhaTabelaTooltipv4(descricao, valor, null, descricaoCss, valorCss,tooltip);
            } else {
                AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest)pageContext.getRequest());
                if (ShowFieldHelper.showField(fieldKey, responsavel)) {
                    return JspHelper.gerarLinhaTabelaTooltipv4(descricao, valor, null, descricaoCss, valorCss,tooltip);
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }

    protected String abrirTabela() {
        return abrirTabela(null, null);
    }

    protected String abrirTabela(String width, String css) {
        if (TextHelper.isNull(width)) {
            width = "100%";
        }
        if (TextHelper.isNull(css)) {
            css = "TabelaEntradaDeDados";
        }
        return "<table width=\"" + width + "\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"1\" class=\"" + css +"\">\n";
    }

    protected String abrirTabelaPaginacao(String width) {
        if (TextHelper.isNull(width)) {
            width = "100%";
        }
        return "<table width=\"" + width + "\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n";
    }

    protected String fecharTabela() {
        return "</table>";
    }
}
