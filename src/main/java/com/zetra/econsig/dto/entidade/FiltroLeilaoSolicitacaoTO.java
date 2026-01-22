package com.zetra.econsig.dto.entidade;

import java.util.Date;

/**
 * <p>Title: FaqTO</p>
 * <p>Description: Transfer Object da tabela de FiltroLeilaoSolicitacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroLeilaoSolicitacaoTO implements java.io.Serializable{

    private String flsCodigo;

    private String cidCodigo;

    private String usuCodigo;

    private String posCodigo;

    private String flsDescricao;

    private Date flsData;

    private String flsEmailNotificacao;

    private Date flsDataAberturaInicial;

    private Date flsDataAberturaFinal;

    private Short flsHorasEncerramento;

    private Integer flsPontuacaoMinima;

    private String flsAnaliseRisco;

    private Integer flsMargemLivreMax;

    private String flsTipoPesquisa;

    private String flsMatricula;

    private String flsCpf;


    public FiltroLeilaoSolicitacaoTO() {
        super();
    }

    public String getFlsCodigo() {
        return flsCodigo;
    }

    public void setFlsCodigo(String flsCodigo) {
        this.flsCodigo = flsCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getPosCodigo() {
        return posCodigo;
    }

    public void setPosCodigo(String posCodigo) {
        this.posCodigo = posCodigo;
    }

    public String getFlsDescricao() {
        return flsDescricao;
    }

    public void setFlsDescricao(String flsDescricao) {
        this.flsDescricao = flsDescricao;
    }

    public Date getFlsData() {
        return flsData;
    }

    public void setFlsData(Date flsData) {
        this.flsData = flsData;
    }

    public String getFlsEmailNotificacao() {
        return flsEmailNotificacao;
    }

    public void setFlsEmailNotificacao(String flsEmailNotificacao) {
        this.flsEmailNotificacao = flsEmailNotificacao;
    }

    public Date getFlsDataAberturaInicial() {
        return flsDataAberturaInicial;
    }

    public void setFlsDataAberturaInicial(Date flsDataAberturaInicial) {
        this.flsDataAberturaInicial = flsDataAberturaInicial;
    }

    public Date getFlsDataAberturaFinal() {
        return flsDataAberturaFinal;
    }

    public void setFlsDataAberturaFinal(Date flsDataAberturaFinal) {
        this.flsDataAberturaFinal = flsDataAberturaFinal;
    }

    public Short getFlsHorasEncerramento() {
        return flsHorasEncerramento;
    }

    public void setFlsHorasEncerramento(Short flsHorasEncerramento) {
        this.flsHorasEncerramento = flsHorasEncerramento;
    }

    public Integer getFlsPontuacaoMinima() {
        return flsPontuacaoMinima;
    }

    public void setFlsPontuacaoMinima(Integer flsPontuacaoMinima) {
        this.flsPontuacaoMinima = flsPontuacaoMinima;
    }

    public String getFlsAnaliseRisco() {
        return flsAnaliseRisco;
    }

    public void setFlsAnaliseRisco(String flsAnaliseRisco) {
        this.flsAnaliseRisco = flsAnaliseRisco;
    }

    public Integer getFlsMargemLivreMax() {
        return flsMargemLivreMax;
    }

    public void setFlsMargemLivreMax(Integer flsMargemLivreMax) {
        this.flsMargemLivreMax = flsMargemLivreMax;
    }

    public String getFlsTipoPesquisa() {
        return flsTipoPesquisa;
    }

    public void setFlsTipoPesquisa(String flsTipoPesquisa) {
        this.flsTipoPesquisa = flsTipoPesquisa;
    }

    public String getFlsMatricula() {
        return flsMatricula;
    }

    public void setFlsMatricula(String flsMatricula) {
        this.flsMatricula = flsMatricula;
    }

    public String getFlsCpf() {
        return flsCpf;
    }

    public void setFlsCpf(String flsCpf) {
        this.flsCpf = flsCpf;
    }

    @Override
    public String toString() {
        return "FiltroLeilaoSolicitacaoTransferObject [flsCodigo=" + flsCodigo + ", usuCodigo=" + usuCodigo + ", flsDescricao=" + flsDescricao + "]";
    }

}
