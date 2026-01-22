package com.zetra.econsig.helper.folha;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: DadosProcessamentoSemBloqueio</p>
 * <p>Description: Classe Bean de dados do processamento sem bloqueio em execução.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosProcessamentoSemBloqueio {

    private String bprPeriodo = "";
    private long estimativaTerminoHoras = 0;
    private long estimativaTerminoMinutos = 0;

    private String orgaoIdentificadorProcessamento = null;
    private String orgaoIdentificadorVariacaoMargem = null;
    private String linkRet;

    private boolean temBlocoProcessamento = false;
    private boolean temProcessoRodando = false;

    private double percentualBlocosProcessados = 0.00;
    private double percentualBlocosProcessadosMargem = 0.00;
    private double percentualBlocosProcessadosRetorno = 0.00;
    private double percentualBlocosProcessadosComErro = 0.00;
    private double percentualBlocosProcessadosRejeitados = 0.00;

    private Map<String, String> dadosMediaMargem = new LinkedHashMap<>();

    List<TransferObject> lstOrgao;

    public String getBprPeriodo() {
        return bprPeriodo;
    }

    public void setBprPeriodo(String bprPeriodo) {
        this.bprPeriodo = bprPeriodo;
    }

    public long getEstimativaTerminoHoras() {
        return estimativaTerminoHoras;
    }

    public void setEstimativaTerminoHoras(long estimativaTerminoHoras) {
        this.estimativaTerminoHoras = estimativaTerminoHoras;
    }

    public long getEstimativaTerminoMinutos() {
        return estimativaTerminoMinutos;
    }

    public void setEstimativaTerminoMinutos(long estimativaTerminoMinutos) {
        this.estimativaTerminoMinutos = estimativaTerminoMinutos;
    }

    public String getOrgaoIdentificadorProcessamento() {
        return orgaoIdentificadorProcessamento;
    }

    public void setOrgaoIdentificadorProcessamento(String orgaoIdentificadorProcessamento) {
        this.orgaoIdentificadorProcessamento = orgaoIdentificadorProcessamento;
    }

    public String getOrgaoIdentificadorVariacaoMargem() {
        return orgaoIdentificadorVariacaoMargem;
    }

    public void setOrgaoIdentificadorVariacaoMargem(String orgaoIdentificadorVariacaoMargem) {
        this.orgaoIdentificadorVariacaoMargem = orgaoIdentificadorVariacaoMargem;
    }

    public boolean isTemBlocoProcessamento() {
        return temBlocoProcessamento;
    }

    public void setTemBlocoProcessamento(boolean temBlocoProcessamento) {
        this.temBlocoProcessamento = temBlocoProcessamento;
    }

    public boolean isTemProcessoRodando() {
        return temProcessoRodando;
    }

    public void setTemProcessoRodando(boolean temProcessoRodando) {
        this.temProcessoRodando = temProcessoRodando;
    }

    public double getPercentualBlocosProcessados() {
        return percentualBlocosProcessados;
    }

    public void setPercentualBlocosProcessados(double percentualBlocosProcessados) {
        this.percentualBlocosProcessados = percentualBlocosProcessados;
    }

    public double getPercentualBlocosProcessadosMargem() {
        return percentualBlocosProcessadosMargem;
    }

    public void setPercentualBlocosProcessadosMargem(double percentualBlocosProcessadosMargem) {
        this.percentualBlocosProcessadosMargem = percentualBlocosProcessadosMargem;
    }

    public double getPercentualBlocosProcessadosRetorno() {
        return percentualBlocosProcessadosRetorno;
    }

    public void setPercentualBlocosProcessadosRetorno(double percentualBlocosProcessadosRetorno) {
        this.percentualBlocosProcessadosRetorno = percentualBlocosProcessadosRetorno;
    }

    public double getPercentualBlocosProcessadosComErro() {
        return percentualBlocosProcessadosComErro;
    }

    public void setPercentualBlocosProcessadosComErro(double percentualBlocosProcessadosComErro) {
        this.percentualBlocosProcessadosComErro = percentualBlocosProcessadosComErro;
    }

    public double getPercentualBlocosProcessadosRejeitados() {
        return percentualBlocosProcessadosRejeitados;
    }

    public void setPercentualBlocosProcessadosRejeitados(double percentualBlocosProcessadosRejeitados) {
        this.percentualBlocosProcessadosRejeitados = percentualBlocosProcessadosRejeitados;
    }

    public Map<String, String> getDadosMediaMargem() {
        return dadosMediaMargem;
    }

    public void setDadosMediaMargem(Map<String, String> dadosMediaMargem) {
        this.dadosMediaMargem = dadosMediaMargem;
    }

    public String getLinkRet() {
        return linkRet;
    }

    public void setLinkRet(String linkRet) {
        this.linkRet = linkRet;
    }

    public List<TransferObject> getLstOrgao() {
        return lstOrgao;
    }

    public void setLstOrgao(List<TransferObject> lstOrgao) {
        this.lstOrgao = lstOrgao;
    }
}
