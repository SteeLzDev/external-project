package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MensagemTO</p>
 * <p>Description: Transfer Object da tabela de mensagem</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MensagemTO extends CustomTransferObject {

    public MensagemTO() {
        super();
    }

    public MensagemTO(String menCodigo) {
        this();
        setAttribute(Columns.MEN_CODIGO, menCodigo);
    }

    public MensagemTO(MensagemTO mensagem) {
        this();
        setAtributos(mensagem.getAtributos());
    }

    // Getter
    public String getMenCodigo() {
        return (String) getAttribute(Columns.MEN_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.MEN_USU_CODIGO);
    }

    public String getFunCodigo() {
        return (String) getAttribute(Columns.MEN_FUN_CODIGO);
    }

    public String getMenTitulo() {
        return (String) getAttribute(Columns.MEN_TITULO);
    }

    public String getMenTexto() {
        return (String) getAttribute(Columns.MEN_TEXTO);
    }

    public Date getMenData() {
        return (Date) getAttribute(Columns.MEN_DATA);
    }

    public Short getMenSequencia() {
        return (Short) getAttribute(Columns.MEN_SEQUENCIA);
    }

    public String getMenExibeCse() {
        return (String) getAttribute(Columns.MEN_EXIBE_CSE);
    }

    public String getMenExibeOrg() {
        return (String) getAttribute(Columns.MEN_EXIBE_ORG);
    }

    public String getMenExibeCsa() {
        return (String) getAttribute(Columns.MEN_EXIBE_CSA);
    }

    public String getMenExibeCor() {
        return (String) getAttribute(Columns.MEN_EXIBE_COR);
    }

    public String getMenExibeSer() {
        return (String) getAttribute(Columns.MEN_EXIBE_SER);
    }

    public String getMenExibeSup() {
        return (String) getAttribute(Columns.MEN_EXIBE_SUP);
    }

    public String getMenExigeLeitura() {
        return (String) getAttribute(Columns.MEN_EXIGE_LEITURA);
    }

    public String getMenHtml() {
        return (String) getAttribute(Columns.MEN_HTML);
    }

    public String getMenPermiteLerDepois() {
        return (String) getAttribute(Columns.MEN_PERMITE_LER_DEPOIS);
    }

    public String getMenNotificarCseLeitura() {
        return (String) getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA);
    }

    public String getMenBloqCsaSemLeitura() {
        return (String) getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA);
    }
    
    public String getMenPublica() {
        return (String) getAttribute(Columns.MEN_PUBLICA);
    }
        
    public String getMenLidaIndividualmente() {
        return (String) getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE);
    }

    public String getMenPushNotificationSer() {
        return (String) getAttribute(Columns.MEN_PUSH_NOTIFICATION_SER);
    }

    // Setter
    public void setMenCodigo(String menCodigo) {
        setAttribute(Columns.MEN_CODIGO, menCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.MEN_USU_CODIGO, usuCodigo);
    }

    public void setFunCodigo(String funCodigo) {
        setAttribute(Columns.MEN_FUN_CODIGO, funCodigo);
    }

    public void setMenTitulo(String menTitulo) {
        setAttribute(Columns.MEN_TITULO, menTitulo);
    }

    public void setMenTexto(String menTexto) {
        setAttribute(Columns.MEN_TEXTO, menTexto);
    }

    public void setMenData(Date menData) {
        setAttribute(Columns.MEN_DATA, menData);
    }

    public void setMenSequencia(Short menSequencia) {
        setAttribute(Columns.MEN_SEQUENCIA, menSequencia);
    }

    public void setMenExibeCse(String menExibeCse) {
        setAttribute(Columns.MEN_EXIBE_CSE, menExibeCse);
    }

    public void setMenExibeOrg(String menExibeOrg) {
        setAttribute(Columns.MEN_EXIBE_ORG, menExibeOrg);
    }

    public void setMenExibeCsa(String menExibeCsa) {
        setAttribute(Columns.MEN_EXIBE_CSA, menExibeCsa);
    }

    public void setMenExibeCor(String menExibeCor) {
        setAttribute(Columns.MEN_EXIBE_COR, menExibeCor);
    }

    public void setMenExibeSer(String menExibeSer) {
        setAttribute(Columns.MEN_EXIBE_SER, menExibeSer);
    }

    public void setMenExibeSup(String menExibeSup) {
        setAttribute(Columns.MEN_EXIBE_SUP, menExibeSup);
    }

    public void setMenExigeLeitura(String menExigeLeitura) {
        setAttribute(Columns.MEN_EXIGE_LEITURA, menExigeLeitura);
    }

    public void setMenHtml(String menHtml) {
        setAttribute(Columns.MEN_HTML, menHtml);
    }

    public void setMenPermiteLerDepois(String menPermiteLerDepois) {
        setAttribute(Columns.MEN_PERMITE_LER_DEPOIS, menPermiteLerDepois);
    }

    public void setMenNotificarCseLeitura(String menNotificarCseLeitura) {
        setAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA, menNotificarCseLeitura);
    }

    public void setMenBloqCsaSemLeitura(String menBloqCsaSemLeitura) {
        setAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA, menBloqCsaSemLeitura);
    }
    
    public void setMenPublica(String menPublica) {
        setAttribute(Columns.MEN_PUBLICA, menPublica);
    }
    
    public void setMenLidaIndividualmente(String menLidaIndividualmente) {
        setAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE, menLidaIndividualmente);
    }

    public void setMenPushNotificationSer(String menPushNotificationSer) {
        setAttribute(Columns.MEN_PUSH_NOTIFICATION_SER, menPushNotificationSer);
    }

}
