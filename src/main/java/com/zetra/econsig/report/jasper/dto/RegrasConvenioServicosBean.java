package com.zetra.econsig.report.jasper.dto;

/**
 * <p> Title: RegrasConvenioServicosBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta de serviços para o Relatório de Regras Convênio.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegrasConvenioServicosBean {

    private String csaCodigo;
    private String csaNome;
    private String svcCodigo;
    private String svcDescricao;
    private String tpsCodigo;
    private String tpsDescricao;
    private String pseVlr;
    private String pseVlrRef;
    private String subtitulo;
    private Boolean salaryPay;
    private Boolean acessaApi;

    public RegrasConvenioServicosBean(String csaCodigo, String csaNome, String svcCodigo, String svcDescricao, String tpsCodigo, String tpsDescricao, String pseVlr, String pseVlrRef, String subtitulo, Boolean salaryPay, Boolean acessaApi) {
        this.csaCodigo = csaCodigo;
        this.csaNome = csaNome;
        this.svcCodigo = svcCodigo;
        this.svcDescricao = svcDescricao;
        this.tpsCodigo = tpsCodigo;
        this.tpsDescricao = tpsDescricao;
        this.pseVlr = pseVlr;
        this.pseVlrRef = pseVlrRef;
        this.subtitulo = subtitulo;
        this.salaryPay = salaryPay;
        this.acessaApi = acessaApi;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public String getSvcDescricao() {
        return svcDescricao;
    }

    public String getTpsCodigo() {
        return tpsCodigo;
    }

    public String getTpsDescricao() {
        return tpsDescricao;
    }

    public String getPseVlr() {
        return pseVlr;
    }

    public String getPseVlrRef() {
        return pseVlrRef;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public Boolean getSalaryPay() {
        return salaryPay;
    }

    public Boolean getAcessaApi() {
        return acessaApi;
    }

}