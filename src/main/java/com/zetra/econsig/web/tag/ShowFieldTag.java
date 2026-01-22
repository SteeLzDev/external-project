package com.zetra.econsig.web.tag;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: ShowFieldTag</p>
 * <p>Description: Tag para exibição de campos na tela, baseado em
 * um arquivo de configuração.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ShowFieldTag extends BodyTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ShowFieldTag.class);

    private String key;

    // 1
    public void setKey(String key) {
        this.key = key;
    }

    // 2
    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (!ShowFieldHelper.showField(key, responsavel)) {
                // Se não é para exibir o campo, pula o processamento do corpo
                return BodyTagSupport.SKIP_BODY;
            } else {
                // Se é, então gera o corpo
                return BodyTagSupport.EVAL_BODY_BUFFERED;
            }
        } catch (ZetraException ex) {
            throw new JspException(ex.getMessage(), ex);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    // 3
    @Override
    public void setBodyContent(BodyContent b) {
        super.setBodyContent(b);
    }

    // 4
    @Override
    public void doInitBody() throws JspException {
        super.doInitBody();
    }

    // 5
    @Override
    public int doAfterBody() throws JspException {
        try {
            getPreviousOut().write(getBodyContent().getString());
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return BodyTagSupport.SKIP_BODY;
    }
}
