package com.zetra.econsig.web.controller.rest;

/**
 * <p>Title: ExecucaoRemotaRequest</p>
 * <p>Description: Objeto de requisição para o REST Controller de execução retoma de rotina.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExecucaoRemotaRequest {

    private String nomeClasseRotina;
    private String[] parametrosRotina;
    private String enderecoRetornoLog;
    private Integer portaRetornoLog;

    public String getNomeClasseRotina() {
        return nomeClasseRotina;
    }

    public void setNomeClasseRotina(String nomeClasseRotina) {
        this.nomeClasseRotina = nomeClasseRotina;
    }

    public String[] getParametrosRotina() {
        return parametrosRotina == null ? new String[]{} : parametrosRotina;
    }

    public void setParametrosRotina(String[] parametrosRotina) {
        this.parametrosRotina = parametrosRotina;
    }

    public String getEnderecoRetornoLog() {
        return enderecoRetornoLog;
    }

    public void setEnderecoRetornoLog(String enderecoRetornoLog) {
        this.enderecoRetornoLog = enderecoRetornoLog;
    }

    public Integer getPortaRetornoLog() {
        return portaRetornoLog;
    }

    public void setPortaRetornoLog(Integer portaRetornoLog) {
        this.portaRetornoLog = portaRetornoLog;
    }
}
