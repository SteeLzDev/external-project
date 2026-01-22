package com.zetra.econsig.dto.web;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CalendarioFolhaModel</p>
 * <p>Description: Model da página de edição de calendário folha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 27758 $
 * $Date: 2019-09-06 15:26:28 -0300 (sex, 06 set 2019) $
 */
public class CalendarioFolhaModel {
    private List<Integer> diasMaxMes;

    private boolean habilitaDataPrevistaRetorno;

    private boolean habilitaDataFiscal;

    private int qtdPeriodos;

    private int ano;

    private String periodicidade;

    private String tipoEntidade;

    private TransferObject criterio;

    private String estCodigo;

    private String orgCodigo;

    private boolean habilitaPeriodoAjustes;

    private boolean permiteApenasReducoes;

    private Map<Integer, TransferObject> calendarioAno;

    private Map<Integer, TransferObject> calendarioAnoProximoAno;

    private String nomeCampoDataIni;

    private String nomeCampoDataFim;

    private String nomeCampoDataFimAjustes;

    private String nomeCampoDiaCorte;

    private String nomeCampoDataPrevistaRetorno;

    private String nomeCampoApenasReducoes;

    private String nomeCampoDataIniFiscal;

    private String nomeCampoDataFimFiscal;

    private String nomeCampoNumPeriodo;

    private boolean podeEditarCalendario;

    private List<TransferObject> lstEstabelecimentos;

    private List<TransferObject> lstOrgaos;

    public List<TransferObject> getLstOrgaos() {
        return lstOrgaos;
    }

    public void setLstOrgaos(List<TransferObject> lstOrgaos) {
        this.lstOrgaos = lstOrgaos;
    }

    public List<TransferObject> getLstEstabelecimentos() {
        return lstEstabelecimentos;
    }

    public void setLstEstabelecimentos(List<TransferObject> lstEstabelecimentos) {
        this.lstEstabelecimentos = lstEstabelecimentos;
    }

    public List<Integer> getDiasMaxMes() {
        return diasMaxMes;
    }

    public void setDiasMaxMes(List<Integer> diasMaxMes) {
        this.diasMaxMes = diasMaxMes;
    }

    public boolean isHabilitaDataPrevistaRetorno() {
        return habilitaDataPrevistaRetorno;
    }

    public void setHabilitaDataPrevistaRetorno(boolean habilitaDataPrevistaRetorno) {
        this.habilitaDataPrevistaRetorno = habilitaDataPrevistaRetorno;
    }

    public int getQtdPeriodos() {
        return qtdPeriodos;
    }

    public void setQtdPeriodos(int qtdPeriodos) {
        this.qtdPeriodos = qtdPeriodos;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public boolean isMensal() {
        return periodicidade != null && periodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_MENSAL);
    }

    public boolean isQuinzenal() {
        return periodicidade != null && periodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);
    }

    public boolean isQuatorzenal() {
        return periodicidade != null && periodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL);
    }

    public boolean isSemanal() {
        return periodicidade != null && periodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_SEMANAL);
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public TransferObject getCriterio() {
        return criterio;
    }

    public void setCriterio(TransferObject criterio) {
        this.criterio = criterio;
    }

    public String getEstCodigo() {
        return estCodigo;
    }

    public void setEstCodigo(String estCodigo) {
        this.estCodigo = estCodigo;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public boolean isHabilitaPeriodoAjustes() {
        return habilitaPeriodoAjustes;
    }

    public void setHabilitaPeriodoAjustes(boolean habilitaPeriodoAjustes) {
        this.habilitaPeriodoAjustes = habilitaPeriodoAjustes;
    }

    public boolean isPermiteApenasReducoes() {
        return permiteApenasReducoes;
    }

    public void setPermiteApenasReducoes(boolean permiteApenasReducoes) {
        this.permiteApenasReducoes = permiteApenasReducoes;
    }

    public String getNomeCampoDataIniFiscal() {
        return nomeCampoDataIniFiscal;
    }

    public void setNomeCampoDataIniFiscal(String nomeCampoDataIniFiscal) {
        this.nomeCampoDataIniFiscal = nomeCampoDataIniFiscal;
    }

    public String getNomeCampoDataFimFiscal() {
        return nomeCampoDataFimFiscal;
    }

    public void setNomeCampoDataFimFiscal(String nomeCampoDataFimFiscal) {
        this.nomeCampoDataFimFiscal = nomeCampoDataFimFiscal;
    }

    public Map<Integer, TransferObject> getCalendarioAno() {
        return calendarioAno;
    }

    public void setCalendarioAno(Map<Integer, TransferObject> calendarioAno) {
        this.calendarioAno = calendarioAno;
    }

    public Map<Integer, TransferObject> getCalendarioAnoProximoAno() {
        return calendarioAnoProximoAno;
    }

    public void setCalendarioAnoProximoAno(Map<Integer, TransferObject> calendarioAnoProximoAno) {
        this.calendarioAnoProximoAno = calendarioAnoProximoAno;
    }

    public String getNomeCampoDataIni() {
        return nomeCampoDataIni;
    }

    public void setNomeCampoDataIni(String nomeCampoDataIni) {
        this.nomeCampoDataIni = nomeCampoDataIni;
    }

    public String getNomeCampoDataFim() {
        return nomeCampoDataFim;
    }

    public void setNomeCampoDataFim(String nomeCampoDataFim) {
        this.nomeCampoDataFim = nomeCampoDataFim;
    }

    public String getNomeCampoDataFimAjustes() {
        return nomeCampoDataFimAjustes;
    }

    public void setNomeCampoDataFimAjustes(String nomeCampoDataFimAjustes) {
        this.nomeCampoDataFimAjustes = nomeCampoDataFimAjustes;
    }

    public String getNomeCampoDiaCorte() {
        return nomeCampoDiaCorte;
    }

    public void setNomeCampoDiaCorte(String nomeCampoDiaCorte) {
        this.nomeCampoDiaCorte = nomeCampoDiaCorte;
    }

    public String getNomeCampoDataPrevistaRetorno() {
        return nomeCampoDataPrevistaRetorno;
    }

    public void setNomeCampoDataPrevistaRetorno(String nomeCampoDataPrevistaRetorno) {
        this.nomeCampoDataPrevistaRetorno = nomeCampoDataPrevistaRetorno;
    }

    public String getNomeCampoApenasReducoes() {
        return nomeCampoApenasReducoes;
    }

    public void setNomeCampoApenasReducoes(String nomeCampoApenasReducoes) {
        this.nomeCampoApenasReducoes = nomeCampoApenasReducoes;
    }

    public boolean isPodeEditarCalendario() {
        return podeEditarCalendario;
    }

    public void setPodeEditarCalendario(boolean podeEditarCalendario) {
        this.podeEditarCalendario = podeEditarCalendario;
    }

    public boolean isHabilitaDataFiscal() {
        return habilitaDataFiscal;
    }

    public void setHabilitaDataFiscal(boolean habilitaDataFiscal) {
        this.habilitaDataFiscal = habilitaDataFiscal;
    }

    public String getNomeCampoNumPeriodo() {
        return nomeCampoNumPeriodo;
    }

    public void setNomeCampoNumPeriodo(String nomeCampoNumPeriodo) {
        this.nomeCampoNumPeriodo = nomeCampoNumPeriodo;
    }
}
