package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegistroServidorTO</p>
 * <p>Description: Transfer Object do Registro Servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroServidorTO extends CustomTransferObject {

    public RegistroServidorTO() {
    }

    public RegistroServidorTO(String rseCodigo) {
        this();
        setAttribute(Columns.RSE_CODIGO, rseCodigo);
    }

    public RegistroServidorTO(RegistroServidorTO registroServidor) {
        this();
        setAtributos(registroServidor.getAtributos());
    }

    public boolean isExcluido() {
        final String srsCodigo = getSrsCodigo();
        return ((srsCodigo != null) && CodedValues.SRS_INATIVOS.contains(srsCodigo));
    }

    public boolean isBloqueado() {
        final String srsCodigo = getSrsCodigo();
        return ((srsCodigo != null) && CodedValues.SRS_BLOQUEADOS.contains(srsCodigo));
    }

    // Getter
    public String getRseCodigo() {
        return (String) getAttribute(Columns.RSE_CODIGO);
    }

    public String getSerCodigo() {
        return (String) getAttribute(Columns.RSE_SER_CODIGO);
    }

    public String getOrgCodigo() {
        return (String) getAttribute(Columns.RSE_ORG_CODIGO);
    }

    public String getRseMatricula() {
        return (String) getAttribute(Columns.RSE_MATRICULA);
    }

    public String getRseMatriculaInst() {
        return (String) getAttribute(Columns.RSE_MATRICULA_INST);
    }

    public BigDecimal getRseMargem() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM);
    }

    public BigDecimal getRseMargemRest() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_REST);
    }

    public BigDecimal getRseMargemUsada() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_USADA);
    }

    public BigDecimal getRseMediaMargem() {
        return (BigDecimal) getAttribute(Columns.RSE_MEDIA_MARGEM);
    }

    public BigDecimal getRseMargem2() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_2);
    }

    public BigDecimal getRseMargemRest2() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_REST_2);
    }

    public BigDecimal getRseMargemUsada2() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_USADA_2);
    }

    public BigDecimal getRseMediaMargem2() {
        return (BigDecimal) getAttribute(Columns.RSE_MEDIA_MARGEM_2);
    }

    public BigDecimal getRseMargem3() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_3);
    }

    public BigDecimal getRseMargemRest3() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_REST_3);
    }

    public BigDecimal getRseMargemUsada3() {
        return (BigDecimal) getAttribute(Columns.RSE_MARGEM_USADA_3);
    }

    public BigDecimal getRseMediaMargem3() {
        return (BigDecimal) getAttribute(Columns.RSE_MEDIA_MARGEM_3);
    }

    public String getCrsCodigo() {
        return (String) getAttribute(Columns.CRS_CODIGO);
    }

    public String getCrsDescricao() {
        return (String) getAttribute(Columns.CRS_DESCRICAO);
    }

    public String getPrsCodigo() {
        return (String) getAttribute(Columns.PRS_CODIGO);
    }

    public String getPrsDescricao() {
        return (String) getAttribute(Columns.PRS_DESCRICAO);
    }

    public String getSboCodigo() {
        return (String) getAttribute(Columns.SBO_CODIGO);
    }

    public String getSboDescricao() {
        return (String) getAttribute(Columns.SBO_DESCRICAO);
    }

    public String getUniCodigo() {
        return (String) getAttribute(Columns.UNI_CODIGO);
    }

    public String getUniDescricao() {
        return (String) getAttribute(Columns.UNI_DESCRICAO);
    }

    public String getVrsCodigo() {
        return (String) getAttribute(Columns.VRS_CODIGO);
    }

    public String getRseTipo() {
        return (String) getAttribute(Columns.RSE_TIPO);
    }

    public String getSrsCodigo() {
        return (String) getAttribute(Columns.SRS_CODIGO);
    }

    public Integer getRsePrazo() {
        return (Integer) getAttribute(Columns.RSE_PRAZO);
    }

    public Timestamp getRseDataAdmissao() {
        return (Timestamp) getAttribute(Columns.RSE_DATA_ADMISSAO);
    }

    public Short getBcoCodigo() {
        return (Short) getAttribute(Columns.RSE_BCO_CODIGO);
    }

    public String getRseAgenciaSal() {
        return (String) getAttribute(Columns.RSE_AGENCIA_SAL);
    }

    public String getRseAgenciaDvSal() {
        return (String) getAttribute(Columns.RSE_AGENCIA_DV_SAL);
    }

    public String getRseContaSal() {
        return (String) getAttribute(Columns.RSE_CONTA_SAL);
    }

    public String getRseBancoSal() {
        return (String) getAttribute(Columns.RSE_BANCO_SAL);
    }

    public String getRseContaDvSal() {
        return (String) getAttribute(Columns.RSE_CONTA_DV_SAL);
    }

    public BigDecimal getRseSalario() {
        return (BigDecimal) getAttribute(Columns.RSE_SALARIO);
    }

    public BigDecimal getRseProventos() {
        return (BigDecimal) getAttribute(Columns.RSE_PROVENTOS);
    }

    public BigDecimal getRseDescontosComp() {
        return (BigDecimal) getAttribute(Columns.RSE_DESCONTOS_COMP);
    }

    public BigDecimal getRseDescontosFacu() {
        return (BigDecimal) getAttribute(Columns.RSE_DESCONTOS_FACU);
    }

    public BigDecimal getRseOutrosDescontos() {
        return (BigDecimal) getAttribute(Columns.RSE_OUTROS_DESCONTOS);
    }

    public String getRseAssociado() {
        return (String) getAttribute(Columns.RSE_ASSOCIADO);
    }

    public Timestamp getRseDataCarga() {
        return (Timestamp) getAttribute(Columns.RSE_DATA_CARGA);
    }

    public Date getRseDataContracheque() {
        return (java.util.Date) getAttribute(Columns.RSE_DATA_CTC);
    }

    public String getRseCLT() {
        return (String) getAttribute(Columns.RSE_CLT);
    }

    public String getRseObs() {
        return (String) getAttribute(Columns.RSE_OBS);
    }

    public Short getRseParamQtdAdeDefault() {
        return (Short) getAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT);
    }

    public String getPosCodigo() {
        return (String) getAttribute(Columns.RSE_POS_CODIGO);
    }

    public String getTrsCodigo() {
        return (String) getAttribute(Columns.RSE_TRS_CODIGO);
    }

    public String getRseEstabilizado() {
        return (String) getAttribute(Columns.RSE_ESTABILIZADO);
    }

    public Timestamp getRseDataFimEngajamento() {
        return (Timestamp) getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO);
    }

    public Timestamp getRseDataLimitePermanencia() {
        return (Timestamp) getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA);
    }

    public String getCapCodigo() {
        return (String) getAttribute(Columns.RSE_CAP_CODIGO);
    }

    public String getRseBancoSalAlternativo() {
        return (String) getAttribute(Columns.RSE_BANCO_SAL_2);
    }

    public String getRseAgenciaSalAlternativa() {
        return (String) getAttribute(Columns.RSE_AGENCIA_SAL_2);
    }

    public String getRseAgenciaDvSalAlternativa() {
        return (String) getAttribute(Columns.RSE_AGENCIA_DV_SAL_2);
    }

    public String getRseContaSalAlternativa() {
        return (String) getAttribute(Columns.RSE_CONTA_SAL_2);
    }

    public String getRseContaDvSalAlternativa() {
        return (String) getAttribute(Columns.RSE_CONTA_DV_SAL_2);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.RSE_USU_CODIGO);
    }

    public Timestamp getRseDataAlteracao() {
        return (Timestamp) getAttribute(Columns.RSE_DATA_ALTERACAO);
    }

    public BigDecimal getRseBaseCalculo() {
        return (BigDecimal) getAttribute(Columns.RSE_BASE_CALCULO);
    }

    public String getRseAuditoriaTotal() {
        return (String) getAttribute(Columns.RSE_AUDITORIA_TOTAL);
    }

    public String getRseMunicipioLotacao() {
        return (String) getAttribute(Columns.RSE_MUNICIPIO_LOTACAO);
    }

    public String getRseBeneficiarioFinanDvCart() {
        return (String) getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART);
    }

    public String getRsePraca() {
        return (String) getAttribute(Columns.RSE_PRACA);
    }

    public String getRsePedidoDemissao() {
        return (String) getAttribute(Columns.RSE_PEDIDO_DEMISSAO);
    }

    public Date getRseDataSaida() {
        return (Date) getAttribute(Columns.RSE_DATA_SAIDA);
    }

    public Date getRseDataUltSalario() {
        return (Date) getAttribute(Columns.RSE_DATA_ULT_SALARIO);
    }

    public Date getRseDataRetorno() {
        return (Date) getAttribute(Columns.RSE_DATA_RETORNO);
    }

    public String getOrsObs() {
        return (String) getAttribute(Columns.ORS_OBS);
    }

    public String getTipoMotivo() {
        return (String) getAttribute(Columns.TMO_CODIGO);
    }

    public Integer getRsePontuacao() {
        return (Integer) getAttribute(Columns.RSE_PONTUACAO);
    }

    public String getRseMotivoBloqueio() {
        return (String) getAttribute(Columns.RSE_MOTIVO_BLOQUEIO);
    }

    public Short getMarCodigo () {
        return (Short) getAttribute(Columns.RSE_MAR_CODIGO);
    }

    public String getRseMotivoFaltaMargem() {
        return (String) getAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM);
    }

    // Setter
    public void setSerCodigo(String serCodigo) {
        setAttribute(Columns.RSE_SER_CODIGO, serCodigo);
    }

    public void setOrgCodigo(String orgCodigo) {
        setAttribute(Columns.RSE_ORG_CODIGO, orgCodigo);
    }

    public void setRseMatricula(String rseMatricula) {
        setAttribute(Columns.RSE_MATRICULA, rseMatricula);
    }

    public void setRseMatriculaInst(String rseMatriculaInst) {
        setAttribute(Columns.RSE_MATRICULA_INST, rseMatriculaInst);
    }

    public void setRseMargem(BigDecimal rseMargem) {
        setAttribute(Columns.RSE_MARGEM, rseMargem);
    }

    public void setRseMargemRest(BigDecimal rseMargemRest) {
        setAttribute(Columns.RSE_MARGEM_REST, rseMargemRest);
    }

    public void setRseMargemUsada(BigDecimal rseMargemUsada) {
        setAttribute(Columns.RSE_MARGEM_USADA, rseMargemUsada);
    }

    public void setRseMediaMargem(BigDecimal rseMediaMargem) {
        setAttribute(Columns.RSE_MEDIA_MARGEM, rseMediaMargem);
    }

    public void setRseMargem2(BigDecimal rseMargem2) {
        setAttribute(Columns.RSE_MARGEM_2, rseMargem2);
    }

    public void setRseMargemRest2(BigDecimal rseMargemRest2) {
        setAttribute(Columns.RSE_MARGEM_REST_2, rseMargemRest2);
    }

    public void setRseMargemUsada2(BigDecimal rseMargemUsada2) {
        setAttribute(Columns.RSE_MARGEM_USADA_2, rseMargemUsada2);
    }

    public void setRseMediaMargem2(BigDecimal rseMediaMargem2) {
        setAttribute(Columns.RSE_MEDIA_MARGEM_2, rseMediaMargem2);
    }

    public void setRseMargem3(BigDecimal rseMargem3) {
        setAttribute(Columns.RSE_MARGEM_3, rseMargem3);
    }

    public void setRseMargemRest3(BigDecimal rseMargemRest3) {
        setAttribute(Columns.RSE_MARGEM_REST_3, rseMargemRest3);
    }

    public void setRseMargemUsada3(BigDecimal rseMargemUsada3) {
        setAttribute(Columns.RSE_MARGEM_USADA_3, rseMargemUsada3);
    }

    public void setRseMediaMargem3(BigDecimal rseMediaMargem3) {
        setAttribute(Columns.RSE_MEDIA_MARGEM_3, rseMediaMargem3);
    }

    public void setCrsCodigo(String crsCodigo) {
        setAttribute(Columns.CRS_CODIGO, crsCodigo);
    }

    public void setCrsDescricao(String crsDescricao) {
        setAttribute(Columns.CRS_DESCRICAO, crsDescricao);
    }

    public void setPrsCodigo(String prsCodigo) {
        setAttribute(Columns.PRS_CODIGO, prsCodigo);
    }

    public void setPrsDescricao(String prsDescricao) {
        setAttribute(Columns.PRS_DESCRICAO, prsDescricao);
    }

    public void setSboCodigo(String sboCodigo) {
        setAttribute(Columns.SBO_CODIGO, sboCodigo);
    }

    public void setSboDescricao(String sboDescricao) {
        setAttribute(Columns.SBO_DESCRICAO, sboDescricao);
    }

    public void setUniCodigo(String uniCodigo) {
        setAttribute(Columns.UNI_CODIGO, uniCodigo);
    }

    public void setUniDescricao(String uniDescricao) {
        setAttribute(Columns.UNI_DESCRICAO, uniDescricao);
    }

    public void setVrsCodigo(String vrsCodigo) {
        setAttribute(Columns.VRS_CODIGO, vrsCodigo);
    }

    public void setRseTipo(String rseTipo) {
        setAttribute(Columns.RSE_TIPO, rseTipo);
    }

    public void setSrsCodigo(String srsCodigo) {
        setAttribute(Columns.SRS_CODIGO, srsCodigo);
    }

    public void setRsePrazo(Integer rsePrazo) {
        setAttribute(Columns.RSE_PRAZO, rsePrazo);
    }

    public void setRseDataAdmissao(Timestamp rseDataAdmissao) {
        setAttribute(Columns.RSE_DATA_ADMISSAO, rseDataAdmissao);
    }

    public void setRseDataContracheque(Date rseDataContracheque) {
        setAttribute(Columns.RSE_DATA_CTC, rseDataContracheque);
    }

    public void setBcoCodigo(Short bcoCodigo) {
        setAttribute(Columns.RSE_BCO_CODIGO, bcoCodigo);
    }

    public void setRseAgenciaDvSal(String rseAgenciaDvSal) {
        setAttribute(Columns.RSE_AGENCIA_DV_SAL, rseAgenciaDvSal);
    }

    public void setRseAgenciaSal(String rseAgenciaSal) {
        setAttribute(Columns.RSE_AGENCIA_SAL, rseAgenciaSal);
    }

    public void setRseContaDvSal(String rseContaDvSal) {
        setAttribute(Columns.RSE_CONTA_DV_SAL, rseContaDvSal);
    }

    public void setRseContaSal(String rseContaSal) {
        setAttribute(Columns.RSE_CONTA_SAL, rseContaSal);
    }

    public void setRseBancoSal(String rseBancoSal) {
        setAttribute(Columns.RSE_BANCO_SAL, rseBancoSal);
    }

    public void setRseSalario(BigDecimal rseSalario) {
        setAttribute(Columns.RSE_SALARIO, rseSalario);
    }

    public void setRseProventos(BigDecimal rseProventos) {
        setAttribute(Columns.RSE_PROVENTOS, rseProventos);
    }

    public void setRseDescontosComp(BigDecimal rseDescontosComp) {
        setAttribute(Columns.RSE_DESCONTOS_COMP, rseDescontosComp);
    }

    public void setRseDescontosFacu(BigDecimal rseDescontosFacu) {
        setAttribute(Columns.RSE_DESCONTOS_FACU, rseDescontosFacu);
    }

    public void setRseOutrosDescontos(BigDecimal rseOutrosDescontos) {
        setAttribute(Columns.RSE_OUTROS_DESCONTOS, rseOutrosDescontos);
    }

    public void setRseAssociado(String rseAssociado) {
        setAttribute(Columns.RSE_ASSOCIADO, rseAssociado);
    }

    public void setRseDataCarga(Timestamp rseDataCarga) {
        setAttribute(Columns.RSE_DATA_CARGA, rseDataCarga);
    }

    public void setRseCLT(String rseCLT) {
        setAttribute(Columns.RSE_CLT, rseCLT);
    }

    public void setRseObs(String rseObs) {
        setAttribute(Columns.RSE_OBS, rseObs);
    }

    public void setRseParamQtdAdeDefault(Short rseParamQtdAdeDefault) {
        setAttribute(Columns.RSE_PARAM_QTD_ADE_DEFAULT, rseParamQtdAdeDefault);
    }

    public void setPosCodigo(String postoCodigo) {
        setAttribute(Columns.RSE_POS_CODIGO, postoCodigo);
    }

    public void setTrsCodigo(String trsCodigo) {
        setAttribute(Columns.RSE_TRS_CODIGO, trsCodigo);
    }

    public void setRseEstabilizado(String rseEstabilizado) {
        setAttribute(Columns.RSE_ESTABILIZADO, rseEstabilizado);
    }

    public void setRseDataFimEngajamento(Timestamp rseDataFimEngajamento) {
        setAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO, rseDataFimEngajamento);
    }

    public void setRseDataLimitePermanencia(Timestamp rseDataLimitePermanencia) {
        setAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA, rseDataLimitePermanencia);
    }

    public void setCapCodigo(String capCodigo) {
        setAttribute(Columns.RSE_CAP_CODIGO, capCodigo);
    }

    public void setRseBancoSalAlternativo(String rseBancoSalAlternativo) {
        setAttribute(Columns.RSE_BANCO_SAL_2, rseBancoSalAlternativo);
    }

    public void setRseAgenciaSalAlternativa(String rseAgenciaSalAlternativa) {
        setAttribute(Columns.RSE_AGENCIA_SAL_2, rseAgenciaSalAlternativa);
    }

    public void setRseAgenciaDvSalAlternativa(String rseAgenciaDvSalAlternativa) {
        setAttribute(Columns.RSE_AGENCIA_DV_SAL_2, rseAgenciaDvSalAlternativa);
    }

    public void setRseContaSalAlternativa(String rseContaSalAlternativa) {
        setAttribute(Columns.RSE_CONTA_SAL_2, rseContaSalAlternativa);
    }

    public void setRseContaDvSalAlternativa(String rseContaDvSalAlternativa) {
        setAttribute(Columns.RSE_CONTA_DV_SAL_2, rseContaDvSalAlternativa);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.RSE_USU_CODIGO, usuCodigo);
    }

    public void setRseDataAlteracao(Timestamp rseDataAlteracao) {
        setAttribute(Columns.RSE_DATA_ALTERACAO, rseDataAlteracao);
    }

    public void setRseBaseCalculo(BigDecimal rseBaseCalculo) {
        setAttribute(Columns.RSE_BASE_CALCULO, rseBaseCalculo);
    }

    public void setRseAuditoriaTotal(String rseAuditoriaTotal) {
        setAttribute(Columns.RSE_AUDITORIA_TOTAL, rseAuditoriaTotal);
    }

    public void setRseMunicipioLotacao(String rseMunicipioLotacao) {
        setAttribute(Columns.RSE_MUNICIPIO_LOTACAO, rseMunicipioLotacao);
    }

    public void setRseBeneficiarioFinanDvCart(String rseBeneficiarioFinanDvCart) {
        setAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART, rseBeneficiarioFinanDvCart);
    }

    public void setRsePraca(String rsePraca) {
        setAttribute(Columns.RSE_PRACA, rsePraca);
    }

    public void setOrsObs(String orsObs) {
        setAttribute(Columns.ORS_OBS, orsObs);
    }

    public void setTipoMotivo(String tmoCodigo) {
        setAttribute(Columns.TMO_CODIGO, tmoCodigo);
    }

    public void setRsePedidoDemissao(String rsePedidoDemissao) {
        setAttribute(Columns.RSE_PEDIDO_DEMISSAO, rsePedidoDemissao);
    }

    public void setRseDataSaida(Date rseDataSaida) {
        setAttribute(Columns.RSE_DATA_SAIDA, rseDataSaida);
    }

    public void setRseDataUltSalario(Date rseDataUltSalario) {
        setAttribute(Columns.RSE_DATA_ULT_SALARIO, rseDataUltSalario);
    }

    public void setRseDataRetorno(Date rseDataRetorno) {
        setAttribute(Columns.RSE_DATA_RETORNO, rseDataRetorno);
    }

    public void setRsePontuacao(Integer rsePontuacao){
        setAttribute(Columns.RSE_PONTUACAO, rsePontuacao);
    }

    public void setRseMotivoBloqueio(String rseMotivoBloqueio) {
        setAttribute(Columns.RSE_MOTIVO_BLOQUEIO, rseMotivoBloqueio);
    }

    public void setMarCodigo (Short marCodigo) {
        setAttribute(Columns.RSE_MAR_CODIGO, marCodigo);
    }

    public void setRseMotivoFaltaMargem(String rseMotivoFaltaMargem) {
        setAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM, rseMotivoFaltaMargem);
    }
}