package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p> Title: AvaliacaoFaqBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório de Avaliacao de FAQ.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AvaliacaoFaqBean implements Serializable {

    private String faqCodigo;
    private String faqTitulo1;
    private BigDecimal util;
    private BigDecimal inutil;
    private String avaliacao;
    private Date avfData;
    private String usuNome;
    private String usuLogin;
    private String entidade;
    private String avfComentario;
    private String avfCodigo;

    public String getFaqCodigo() {
        return faqCodigo;
    }
    public void setFaqCodigo(String faqCodigo) {
        this.faqCodigo = faqCodigo;
    }
    public String getFaqTitulo1() {
        return faqTitulo1;
    }
    public void setFaqTitulo1(String faqTitulo1) {
        this.faqTitulo1 = faqTitulo1;
    }
    public String getAvaliacao() {
        return avaliacao;
    }
    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }
    public Date getAvfData() {
        return avfData;
    }
    public void setAvfData(Date avfData) {
        this.avfData = avfData;
    }
    public String getUsuNome() {
        return usuNome;
    }
    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }
    public String getUsuLogin() {
        return usuLogin;
    }
    public void setUsuLogin(String usuLogin) {
        this.usuLogin = usuLogin;
    }
    public String getEntidade() {
        return entidade;
    }
    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }
    public String getAvfComentario() {
        return avfComentario;
    }
    public void setAvfComentario(String avfComentario) {
        this.avfComentario = avfComentario;
    }
    public BigDecimal getUtil() {
        return util;
    }
    public void setUtil(BigDecimal util) {
        this.util = util;
    }
    public BigDecimal getInutil() {
        return inutil;
    }
    public void setInutil(BigDecimal inutil) {
        this.inutil = inutil;
    }
    public String getAvfCodigo() {
        return avfCodigo;
    }
    public void setAvfCodigo(String avfCodigo) {
        this.avfCodigo = avfCodigo;
    }
}
