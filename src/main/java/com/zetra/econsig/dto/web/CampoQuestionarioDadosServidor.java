package com.zetra.econsig.dto.web;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: CampoQuestionarioDadosServidor</p>
 * <p>Description: POJO para campos do questionário de dados do servidor para cadastro de senha.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CampoQuestionarioDadosServidor {

    private String name;
    private String type;
    private String mask;
    private String label;
    private String optionValue;
    private String optionLabel;
    private List<TransferObject> content;
    private String notSelectedLabel;
    private String msgControle;

    public CampoQuestionarioDadosServidor(String name, String type, String mask, String label, String optionValue, String optionLabel, List<TransferObject> content, String notSelectedLabel, String msgControle, String placeholder) {
        super();
        this.name = name;
        this.type = type;
        this.mask = mask;
        this.label = label;
        this.optionValue = optionValue;
        this.optionLabel = optionLabel;
        this.content = content;
        this.notSelectedLabel = notSelectedLabel;
        this.msgControle = msgControle;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMask() {
        return mask;
    }

    public String getLabel() {
        return label;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public List<TransferObject> getContent() {
        return content;
    }

    public String getNotSelectedLabel() {
        return notSelectedLabel;
    }

    public String getMsgControle() {
        return msgControle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public void setContent(List<TransferObject> content) {
        this.content = content;
    }

    public void setNotSelectedLabel(String notSelectedLabel) {
        this.notSelectedLabel = notSelectedLabel;
    }

    public void setMsgControle(String msgControle) {
        this.msgControle = msgControle;
    }
}
