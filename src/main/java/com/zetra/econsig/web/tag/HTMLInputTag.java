package com.zetra.econsig.web.tag;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
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
public class HTMLInputTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HTMLInputTag.class);

    // ----------------------------------------------------- Instance Variables

    protected String name; // Nome do Campo
    protected String type; // Tipo do Campo
    protected String value; // Valor do Campo
    protected String src; // Source de uma imgem, se type = image
    protected String width; // Largura de uma imgem, se type = image
    protected String height; // Altura de uma imgem, se type = image
    protected String size; // Tamanho do Campo
    protected String maxlength; // Máximo de caracteres para campos texto
    protected String classe; // Classe de CSS
    protected String onChange; // Evento ao ser alterado
    protected String onClick; // Evento ao ser clicado, se type = button ou image
    protected String onFocus; // Evento ao ser focado
    protected String onBlur; // Evento ao ser desfocado
    protected String onKeypress; // Evento ao se digitar algo no campo
    protected String onPaste; // Evento ao se colar algo no campo
    protected String readonly; // True se o campo é apenas leitura
    protected String checked; // Checked para campos radio ou checkbox
    protected String mask; // Código da máscara para campos texto
    protected String autoSkip; // True se o focus deve pular para o próximo campo
    protected String others; // Outros parâmetros
    protected String di; // Identificador do campo
    protected String nf; // Nome do campo para onde o focus deve ser alterado
    protected String cols; // Numero de colunas de um textarea
    protected String rows; // Numero de linhas de um textarea
    protected String configKey; // Nome do chave de configuração no arquivo de propriedades
    protected String placeHolder;
    protected String ariaLabel;
    protected String ariaLabelBy;

    protected boolean disabled;

    // ------------------------------------------------------------- Properties

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setOnFocus(String onFocus) {
        this.onFocus = onFocus;
    }

    public void setOnBlur(String onBlur) {
        this.onBlur = onBlur;
    }

    public void setAutoSkip(String autoSkip) {
        this.autoSkip = autoSkip;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public void setDi(String di) {
        this.di = di;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }

    public void setAriaLabelBy(String ariaLabelBy) {
        this.ariaLabelBy = ariaLabelBy;
    }

    public void setOnKeypress(String onKeypress) {
        this.onKeypress = onKeypress;
    }

    public void setOnPaste(String onPaste) {
        this.onPaste = onPaste;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean isDisabled(AcessoSistema responsavel) {
        if (disabled) {
            return true;
        }
        if (!TextHelper.isNull(configKey)) {
            try {
                return ShowFieldHelper.isDisabled(configKey, responsavel);
            } catch (ZetraException e) {
                LOG.error(e.getMessage(), e);
                return true;
            }
        }

        return false;
    }

    protected void clean() {
        name = null;
        type = null;
        value = null;
        src = null;
        width = null;
        height = null;
        size = null;
        maxlength = null;
        classe = null;
        onChange = null;
        onClick = null;
        onFocus = null;
        onBlur = null;
        onKeypress = null;
        onPaste = null;
        readonly = null;
        checked = null;
        mask = null;
        autoSkip = null;
        others = null;
        di = null;
        nf = null;
        cols = null;
        rows = null;
        configKey = null;
    }

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

    public String generateHtml(AcessoSistema responsavel) {
        // codigo que gera a tag input
        StringBuilder code = new StringBuilder();

        // Se readonly for true e o campo for text, cria um campo hidden e escreve o conteúdo na página
        if (type.equalsIgnoreCase("text") && readonly != null && readonly.equalsIgnoreCase("true")) {
            type = "hidden";
            code.append(value.replaceAll(";", "<br>&nbsp;"));
        }

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

        if (ariaLabel != null) {
            code.append(" aria-label=\"");
            code.append(ariaLabel);
            code.append("\"");
        }

        if (ariaLabelBy != null) {
            code.append(" aria-labelladby=\"");
            code.append(ariaLabelBy);
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