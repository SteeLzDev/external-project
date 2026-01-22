package com.zetra.econsig.web.tag;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: ApplicationResourcesTag</p>
 * <p>Description: Tag para obtenção das mensagens de Application Resources</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ApplicationResourcesTag extends TagSupport {

    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    private String key;
    private String fieldKey;

    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public void setArg4(String arg4) {
        this.arg4 = arg4;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setfieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
            String message = ApplicationResourcesHelper.getMessage(key, responsavel, arg0, arg1, arg2, arg3, arg4);

            if (!TextHelper.isNull(fieldKey) && ShowFieldHelper.isRequired(fieldKey, responsavel)) {
                message = "*" + message;
            }

            pageContext.getOut().print(message);
        } catch (IOException | ZetraException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    @Override
    public void release() {
        super.release();
        arg0 = null;
        arg1 = null;
        arg2 = null;
        arg3 = null;
        arg4 = null;
        key = null;
    }
}
