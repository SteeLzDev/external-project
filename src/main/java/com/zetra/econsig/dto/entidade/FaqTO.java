package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FaqTO</p>
 * <p>Description: Transfer Object da tabela de Faq</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FaqTO extends CustomTransferObject {

    public FaqTO() {
        super();
    }

    public FaqTO(String faqCodigo) {
        this();
        setAttribute(Columns.FAQ_CODIGO, faqCodigo);
    }

    public FaqTO(FaqTO faq) {
        this();
        setAtributos(faq.getAtributos());
    }

   // Getter
    public String getFaqCodigo() {
        return (String) getAttribute(Columns.FAQ_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.FAQ_USU_CODIGO);
    }

    public String getFaqTitulo1() {
        return (String) getAttribute(Columns.FAQ_TITULO_1);
    }

    public String getFaqTitulo2() {
        return (String) getAttribute(Columns.FAQ_TITULO_2);
    }

    public String getFaqTexto() {
        return (String) getAttribute(Columns.FAQ_TEXTO);
    }

    public Date getFaqData() {
        return (Date) getAttribute(Columns.FAQ_DATA);
    }

    public Integer getFaqSequencia() {
        return (Integer) getAttribute(Columns.FAQ_SEQUENCIA);
    }

    public String getFaqExibeCse() {
        return (String) getAttribute(Columns.FAQ_EXIBE_CSE);
    }

    public String getFaqExibeOrg() {
        return (String) getAttribute(Columns.FAQ_EXIBE_ORG);
    }

    public String getFaqExibeCsa() {
        return (String) getAttribute(Columns.FAQ_EXIBE_CSA);
    }

    public String getFaqExibeCor() {
        return (String) getAttribute(Columns.FAQ_EXIBE_COR);
    }

    public String getFaqExibeSer() {
        return (String) getAttribute(Columns.FAQ_EXIBE_SER);
    }

    public String getFaqExibeSup() {
        return (String) getAttribute(Columns.FAQ_EXIBE_SUP);
    }

    public String getFaqExibeMobile() {
        return (String) getAttribute(Columns.FAQ_EXIBE_MOBILE);
    }

    public String getFaqHtml() {
        return (String) getAttribute(Columns.FAQ_HTML);
    }

    public String getCafCodigo() {
        return (String) getAttribute(Columns.FAQ_CAF_CODIGO);
    }

    // Setter
    public void setFaqCodigo(String faqCodigo) {
        setAttribute(Columns.FAQ_CODIGO, faqCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.FAQ_USU_CODIGO, usuCodigo);
    }

    public void setFaqTitulo1(String faqTitulo1) {
        setAttribute(Columns.FAQ_TITULO_1, faqTitulo1);
    }

    public void setFaqTitulo2(String faqTitulo2) {
        setAttribute(Columns.FAQ_TITULO_2, faqTitulo2);
    }

    public void setFaqTexto(String faqTexto) {
        setAttribute(Columns.FAQ_TEXTO, faqTexto);
    }

    public void setFaqData(Date faqData) {
        setAttribute(Columns.FAQ_DATA, faqData);
    }

    public void setFaqSequencia(Integer faqSequencia) {
        setAttribute(Columns.FAQ_SEQUENCIA, faqSequencia);
    }

    public void setFaqExibeCse(String faqExibeCse) {
        setAttribute(Columns.FAQ_EXIBE_CSE, faqExibeCse);
    }

    public void setFaqExibeOrg(String faqExibeOrg) {
        setAttribute(Columns.FAQ_EXIBE_ORG, faqExibeOrg);
    }

    public void setFaqExibeCsa(String faqExibeCsa) {
        setAttribute(Columns.FAQ_EXIBE_CSA, faqExibeCsa);
    }

    public void setFaqExibeCor(String faqExibeCor) {
        setAttribute(Columns.FAQ_EXIBE_COR, faqExibeCor);
    }

    public void setFaqExibeSer(String faqExibeSer) {
        setAttribute(Columns.FAQ_EXIBE_SER, faqExibeSer);
    }

    public void setFaqExibeSup(String faqExibeSup) {
        setAttribute(Columns.FAQ_EXIBE_SUP, faqExibeSup);
    }

    public void setFaqExibeMobile(String faqExibeMobile) {
        setAttribute(Columns.FAQ_EXIBE_MOBILE, faqExibeMobile);
    }

    public void setFaqHtml(String faqHtml) {
        setAttribute(Columns.FAQ_HTML, faqHtml);
    }

    public void setCafCodigo(String cafCodigo) {
        setAttribute(Columns.FAQ_CAF_CODIGO, cafCodigo);
    }
}
