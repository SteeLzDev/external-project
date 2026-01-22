package com.zetra.econsig.web.tag.v4;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: HTMLInputTag</p>
 * <p>Description: Tag Lib para a geração de campos para formulários (input)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLInputTag extends com.zetra.econsig.web.tag.HTMLInputTag {

    // --------------------------------------------------------- Public Methods

    @Override
    public int doEndTag() throws JspException {
        try {
            AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());
            pageContext.getOut().print(generateHtml(responsavel));
        } catch (IOException ex) {
            throw new JspException(ex.getMessage());
        }
        clean();
        // Continue processing this page
        return (EVAL_PAGE);
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        // codigo que gera a tag input
        StringBuilder code = new StringBuilder();

        // Se readonly for true e o campo for text, cria um campo hidden e escreve o conteúdo na página
//        if (type.equalsIgnoreCase("text") &&
//                readonly != null && readonly.equalsIgnoreCase("true")) {
//            type = "hidden";
//            code.append(value);
//        }

        if (!type.equalsIgnoreCase("TEXTAREA")) {
            code.append("<INPUT TYPE=\"");
            code.append(type);
            code.append("\" NAME=\"");
            code.append(name);
            code.append("\"");
        } else {
            code.append("<TEXTAREA ");
            code.append("NAME=\"");
            code.append(name);
            code.append("\"");
        }

        if (di != null) {
            code.append(" ID=\"");
            code.append(di);
            code.append("\"");
        }

        if (value != null && !type.equalsIgnoreCase("TEXTAREA")) {
            code.append(" VALUE=\"");
            code.append(value);
            code.append("\"");
        }

        if (classe != null) {
            code.append(" CLASS=\"");
            code.append(classe);
            code.append("\"");
        }

        if (type.equalsIgnoreCase("image")) {
            if (src != null) {
                code.append(" SRC=\"");
                code.append(src);
                code.append("\"");
            }
            if (height != null) {
                code.append(" HEIGHT=\"");
                code.append(height);
                code.append("\"");
            }
            if (width != null) {
                code.append(" WIDTH=\"");
                code.append(width);
                code.append("\"");
            }
        } else if (type.equalsIgnoreCase("text") || type.equalsIgnoreCase("password")) {
            if (size != null) {
                code.append(" SIZE=\"");
                code.append(size);
                code.append("\"");
            }
            if (maxlength != null) {
                code.append(" MAXLENGTH=\"");
                code.append(maxlength);
                code.append("\"");
            }

        } else if ((type.equalsIgnoreCase("radio") || (type.equalsIgnoreCase("checkbox"))) &&
                (checked != null) && (checked.equalsIgnoreCase("true"))) {
            code.append(" CHECKED");

        } else if ((type.equalsIgnoreCase("password")) &&
                (readonly != null && readonly.equalsIgnoreCase("true"))) {
            code.append(" READONLY");

        } else if (type.equalsIgnoreCase("button")) {
        }

        // onChange
        if (onChange != null) {
            code.append(" onChange=\"");
            code.append(onChange);
            code.append("\"");
        }

        // onClick
        if (onClick != null) {
            code.append(" onClick=\"");
            code.append(onClick);
            code.append("\"");
        }

        // nf
        if (nf != null) {
            code.append(" nf=\"");
            code.append(nf);
            code.append("\"");
        }

        // cols
        if (cols != null) {
            code.append(" cols=\"");
            code.append(cols);
            code.append("\"");
        }

        // rols
        if (rows != null) {
            code.append(" rows=\"");
            code.append(rows);
            code.append("\"");
        }

        // Mascara
        if ((readonly == null || !readonly.equalsIgnoreCase("true")) &&
                mask != null) {
            code.append(" onFocus=\"SetarEventoMascaraV4(this,'");
            code.append(mask);

            if (autoSkip != null) {
                code.append("',");
                code.append(autoSkip);
                code.append(");");
            } else {
                code.append("',true);");
            }

            if (onFocus != null) {
                code.append(onFocus);
            }

            code.append("\" onBlur=\"fout(this);ValidaMascaraV4(this);");

            if (onBlur != null) {
                code.append(onBlur);
            }
            code.append("\"");
        } else {
            if (onFocus != null) {
                code.append(" onFocus=\"");
                code.append(onFocus);
                code.append("\"");
            }
            if (onBlur != null) {
                code.append(" onBlur=\"");
                code.append(onBlur);
                code.append("\"");
            }
        }

        if (onKeypress != null) {
            code.append(" onkeypress=\"");
            code.append(onKeypress);
            code.append("\"");
        }
        if (onPaste != null) {
            code.append(" onpaste=\"");
            code.append(onPaste);
            code.append("\"");
        }

        // lê no arquivo de propriedades se campo está desabilitado (com valor B)
        if (isDisabled(responsavel)) {
            code.append(" disabled");
        }

        // autocomplete="off" em campos de senha
        if (type.equalsIgnoreCase("password")) {
            code.append(" autocomplete=\"off\"");
        }

        if (placeHolder != null) {
            code.append(" placeholder=\"");
            code.append(placeHolder);
            code.append("\"");
        }

        if ("true".equalsIgnoreCase(readonly)) {
        	code.append(" readonly ");
        }

        if (ariaLabel != null) {
            code.append(" aria-label=\"");
            code.append(ariaLabel);
            code.append("\"");
        }

        // Outros Parametros
        if (others != null) {
            code.append(" ");
            code.append(others);
        }

        code.append(">");

        if (type.equalsIgnoreCase("TEXTAREA")) {
            if (!TextHelper.isNull(value)) {
                code.append(value);
            }

            code.append("</textarea>");
        }

        return code.toString();
    }
}