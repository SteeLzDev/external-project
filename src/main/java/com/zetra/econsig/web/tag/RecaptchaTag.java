package com.zetra.econsig.web.tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RecaptchaTag</p>
 * <p>Description: Tag para gerar captcha avançado.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecaptchaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecaptchaTag.class);

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            StringBuilder code = new StringBuilder();

            String chavePublica = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAPTCHA_AVANCADO_CHAVE_PUBLICA, responsavel);

            if (TextHelper.isNull(chavePublica)) {
                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", (AcessoSistema) null);
            }

            code.append("<div class=\"g-recaptcha\" data-sitekey=\"").append(chavePublica).append("\"></div>");

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }
}
