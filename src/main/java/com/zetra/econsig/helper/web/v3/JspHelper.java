package com.zetra.econsig.helper.web.v3;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: JspHelper</p>
 * <p>Description: Helper Class para operações JSP.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class JspHelper extends com.zetra.econsig.helper.web.JspHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(JspHelper.class);

    /**
     * Método geraComboUF
     * @param nomeCampo
     * @param valorCampo
     * @param desabilitado
     * @param responsavel
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraComboUF(String nomeCampo, String valorCampo, boolean desabilitado, String labelGrupo, AcessoSistema responsavel) {
        StringBuilder combo = new StringBuilder();

        combo.append("<select name=\"").append(TextHelper.forHtmlAttribute(nomeCampo)).append("\"");
        combo.append(" id=\"").append(TextHelper.forHtmlAttribute(nomeCampo)).append("\"");
        combo.append(" class=\"form-control form-select select\"");
        combo.append(desabilitado ? " disabled>" : ">");

        if (!TextHelper.isNull(labelGrupo)) {
            combo.append(" <optgroup label=\"").append(labelGrupo).append("\">");
        }

        if (valorCampo == null || valorCampo.trim().equals("")) {
            combo.append("<option value=\"\" selected>");
        } else {
            combo.append("<option value=\"\">");
        }
        combo.append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.uf", responsavel)).append("</option>");
        try {
            SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            List<TransferObject> listUf = sistemaController.lstUf(responsavel);
            for (TransferObject uf : listUf) {
                combo.append("<option value=\"");
                combo.append(TextHelper.forHtmlAttribute(uf.getAttribute(Columns.UF_COD)));
                if(uf.getAttribute(Columns.UF_COD).equals(valorCampo)){
                    combo.append("\" selected>");
                } else {
                    combo.append("\">");
                }
                combo.append(TextHelper.forHtmlContent(uf.getAttribute(Columns.UF_NOME)));
                combo.append("</option>");
            }
        } catch (ConsignanteControllerException e) {
            LOG.error("Não foi possível carregar o combo de UF (USU_COD:"  + responsavel + ").", e);
        }

        if (!TextHelper.isNull(labelGrupo)) {
            combo.append("</optgroup>");
        }

        combo.append("</select>");
        return combo.toString();
    }

    /**
     * Método geraCombo
     * @param content
     * @param name
     * @param fieldValue
     * @param fieldLabel
     * @param notSelectedLabel
     * @param others
     * @param autoSelect
     * @param size
     * @param selectedValue
     * @param onChange
     * @param disabled
     * @return - XSS : Seguro, tratamento realizado no método.
     */
    public static String geraCombo(List<TransferObject> content, String name, String fieldValue, String fieldLabel, String notSelectedLabel, String others, boolean autoSelect, int size, String selectedValue, String onChange, String labelGroup, boolean disabled) {
        String fieldValues[] = fieldValue.split(";");
        String fieldLabels[] = fieldLabel.split(";");
        boolean selecionaOpcao = (autoSelect && content.size() == 1);

        StringBuilder combo = new StringBuilder();

        if (size > 1) {
            combo.append("<select name=\"").append(TextHelper.forHtmlAttribute(name)).append(" form-select").append("\" id=\"").append(TextHelper.forHtmlAttribute(name)).append("\" multiple size=\"").append(size).append("\" ");
        } else {
            combo.append("<select name=\"").append(TextHelper.forHtmlAttribute(name)).append(" form-select").append("\" id=\"").append(TextHelper.forHtmlAttribute(name)).append("\" ");
        }
        if (others != null) {
            combo.append(others);
        }

        if (disabled) {
            combo.append(" disabled");
        }

        combo.append(" class=\"form-control select\"> ");

        if (!TextHelper.isNull(labelGroup)) {
            combo.append(" <optgroup label=\"").append(labelGroup).append("\">");
        }

        if (!TextHelper.isNull(notSelectedLabel)) {
            combo.append("<option value=\"\"" + (selecionaOpcao ? "" : " selected") + ">").append(TextHelper.forHtmlContent(notSelectedLabel)).append("</option> ");
        }

        String txtLabel = null;
        StringBuilder value = new StringBuilder();
        int labelMaxSize = 80;

        for (TransferObject row : content) {
            // Concatena todos os valores do select
            value.setLength(0);
            for (String fieldValue2 : fieldValues) {
                value.append(row.getAttribute(fieldValue2)).append(";");
            }
            // Apaga último separador
            value.deleteCharAt(value.length() - 1);

            combo.append("<option value=\"").append(TextHelper.forHtmlAttribute(value)).append("\"");

            if ((selectedValue != null && selectedValue.equals(value.toString())) || selecionaOpcao) {
                combo.append(" selected");
            }

            combo.append(">");

            txtLabel = "";
            for (int i = 0; i < fieldLabels.length; i++) {
                txtLabel += row.getAttribute(fieldLabels[i]);
                if (i < fieldLabels.length - 1) {
                    txtLabel += " - ";
                }
            }
            if (txtLabel.length() > labelMaxSize) {
                txtLabel = txtLabel.substring(0, labelMaxSize - 4) + " ...";
            }
            combo.append(TextHelper.forHtmlAttribute(txtLabel));
            combo.append("</option>");
        }

        if (!TextHelper.isNull(labelGroup)) {
            combo.append("</optgroup>");
        }

        combo.append("</select>");

        return combo.toString();
    }
}
