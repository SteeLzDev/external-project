package com.zetra.econsig.web.tag.v4;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.web.tag.HTMLInputTag;

/**
 * <p>Title: HTMLUfInputTag</p>
 * <p>Description: TAG HTML para construção de campos de UF v4.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLUfInputTag extends HTMLInputTag {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HTMLUfInputTag.class);

    @Autowired
    private SistemaController sistemaController;

    protected String description;
    protected boolean desabilitado;
    protected String rotuloUf;
    protected String valorCampo;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDesabilitado(boolean desabilitado) {
        this.desabilitado = desabilitado;
    }

    public void setRotuloUf(String rotuloUf) {
        this.rotuloUf = rotuloUf;
    }

    public void setValorCampo(String valorCampo) {
        this.valorCampo = valorCampo;
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        String retorno = "";

        // Recupera o valor do campo pelo nome e define o valor padrão
        if (TextHelper.isNull(value)) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            setValue(TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, name)));
        }

        // Constrói a linha da tabela contendo a descrição e o campo
        if (TextHelper.isNull(description)) {
            description = ApplicationResourcesHelper.getMessage(rotuloUf, responsavel);//Não pode passar a chave fixa.
        }

        try {
            List<TransferObject> listUf = sistemaController.lstUf(responsavel);
            if (!listUf.isEmpty()) {
                String combo = JspHelper.geraComboUF(name, valorCampo, desabilitado, classe, responsavel);
                retorno = JspHelper.gerarLinhaTabelav4(description, "\n" + combo, null, "TLEDmeio", "CEDmeio", false);
            }
        } catch (ConsignanteControllerException e) {
            LOG.error("Não foi possível carregar o combo de UF (USU_COD:" + responsavel + ").", e);
        }

        return retorno;
    }

    @Override
    protected void clean() {
        super.clean();
        desabilitado = false;
        description = null;
        rotuloUf = null;
        valorCampo = null;
   }
}
