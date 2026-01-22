package com.zetra.econsig.web.tag;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: HcaptchaTag</p>
 * <p>Description: Tag para gerar o Hcaptcha.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HcaptchaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HcaptchaTag.class);

    @Override
    public int doEndTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            final StringBuilder code = new StringBuilder();

            final String chavePublica = (String) ParamSist.getInstance().getParam(CodedValues.TPC_H_CAPTCHA_CHAVE_PUBLICA, responsavel);

            if (TextHelper.isNull(chavePublica)) {
                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", (AcessoSistema) null);
            }

            code.append("<div class=\"g-recaptcha\" data-sitekey=\"").append(chavePublica).append("\"></div>");

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }
}
