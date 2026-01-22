package com.zetra.econsig.web.tag;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: HTMLComboTag</p>
 * <p>Description: Tag Lib para a geração de combos para formulários</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HTMLComboTag extends HTMLInputTag {

    private String listName;
    private String fieldValue;
    private String fieldLabel;
    private String notSelectedLabel;
    private String autoSelect;
    private String selectedValue;
    private boolean disabled;

    public void setAutoSelect(String autoSelect) {
        this.autoSelect = autoSelect;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setNotSelectedLabel(String notSelectedLabel) {
        this.notSelectedLabel = notSelectedLabel;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String generateHtml(AcessoSistema responsavel) {
        @SuppressWarnings("unchecked")
        List<TransferObject> content = (List<TransferObject>) pageContext.getRequest().getAttribute(listName);

        boolean autoSelected = false;
        int comboSize = 1;

        if (autoSelect != null) {
            autoSelected = Boolean.valueOf(autoSelect).booleanValue();
        }

        if (size != null) {
            comboSize = Integer.valueOf(size).intValue();
        }

        // Recupera o valor do campo pelo nome e define o valor padrão
        if (TextHelper.isNull(selectedValue)) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            selectedValue = TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, name));
        }

        return JspHelper.geraCombo(content, name, fieldValue, fieldLabel, notSelectedLabel, others, autoSelected, comboSize, selectedValue, onChange, disabled || isDisabled(responsavel), classe);
    }

    @Override
    protected void clean() {
        super.clean();
        disabled = false;
        listName = null;
        fieldValue = null;
        fieldLabel = null;
        notSelectedLabel = null;
        autoSelect = null;
        selectedValue = null;
    }
}
