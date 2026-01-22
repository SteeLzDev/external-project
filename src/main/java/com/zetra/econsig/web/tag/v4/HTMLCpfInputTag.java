package com.zetra.econsig.web.tag.v4;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.tag.HTMLInputTag;

/**
 * <p>Title: HTMLCpfInputTag</p>
 * <p>Description: TAG HTML para construção de campos de CPF no leiaute v3.</p>
 * <p>Copyright: Copyright (c) 2003-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLCpfInputTag extends HTMLInputTag {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HTMLCpfInputTag.class);

    private String description;
    private String textHelpKey;

    @Override
    public void setName(String name) {
        super.setName(name);
        super.setDi(name);
        super.setOnPaste("return Formata_CPF('" + name + "')");
    }

    public void setTextHelpKey(String textHelpKey) {
        this.textHelpKey = textHelpKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        try {
            // Se omite o campo de CPF ou a chave de FieldsPermission foi informada e está configurada
            // para não exibir o campo, então retorna resultado vazio.
            if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel) ||
                    (!TextHelper.isNull(configKey) && !ShowFieldHelper.showField(configKey, responsavel))) {
                return "";
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            return "";
        }

        // Define valores padronizados para o campo CPF
        if (TextHelper.isNull(name)) {
            setName("SER_CPF");
        }
        if (TextHelper.isNull(di)) {
            setDi("SER_CPF");
        }
        if (TextHelper.isNull(type)) {
            setType("text");
        }
        if (TextHelper.isNull(classe)) {
            setClasse("form-control");
        }
        if (TextHelper.isNull(mask)) {
            setMask(LocaleHelper.getCpfMask());
        }
        if (TextHelper.isNull(size)) {
            setSize(LocaleHelper.getCpfSize());
        }
        if (TextHelper.isNull(maxlength)) {
            setMaxlength(LocaleHelper.getCpfMaxLenght());
        }

        // Recupera o valor do campo pelo nome e define o valor padrão
        if (TextHelper.isNull(value)) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            setValue(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, name)));
        }
        String textoAjuda = null;
        if (!TextHelper.isNull(textHelpKey)) {
            textoAjuda = ApplicationResourcesHelper.getMessage(textHelpKey, responsavel);
        }

        // Constrói a linha da tabela contendo a descrição e o campo
        if (TextHelper.isNull(description)) {
            description = ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel);
        }
        try {
            if (!TextHelper.isNull(configKey) && ShowFieldHelper.isRequired(configKey, responsavel)) {
                description = "*" + description;
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        String campo = super.generateHtml(responsavel);
        return montarLinha(description, campo, name, textoAjuda);
    }

    protected String montarLinha(String descricao, String campo, String nomeCampo, String textoAjuda) {
        return "\n<label for=\"" + nomeCampo + "\">" + descricao + "</label>"
             + "\n" + campo;
    }

    @Override
    protected void clean() {
        super.clean();
        description = null;
        textHelpKey = null;
    }
}
