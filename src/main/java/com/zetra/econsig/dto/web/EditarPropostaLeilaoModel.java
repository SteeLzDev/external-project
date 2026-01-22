package com.zetra.econsig.dto.web;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.margem.ExibeMargem;

/**
 * <p>Title: EditarPropostaLeilaoModel</p>
 * <p>Description: Model para exibição da tela de edição de proposta de leilão reverso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28168 $
 * $Date: 2019-11-05 17:02:21 -0300 (ter, 05 nov 2019) $
 */
public class EditarPropostaLeilaoModel {

    private String adeCodigo;

    private String filtro;

    private boolean temCET;

    private TransferObject ade;

    private TransferObject adeOrigem;

    private boolean temRiscoPelaCsa;

    private String arrRisco;

    private String tipoVlrMargemDisponivel;

    private ExibeMargem exibeMargem;

    private BigDecimal rseMargemRest;

    private String margemConsignavel;

    private String bcoDesc;

    private List<TransferObject> propostas;

    private String soaData;

    private String soaDataValidade;

    private boolean podeEditarProposta;

    private List<TransferObject> convenio;

    private String svcCodigo;

    private String taxaJuros;

    private String valorLiberado;

    private String prazo;

    private String valorParcela;

    private String txtContatoCsa;

    private boolean plsCsaAprovada;

    private String telefoneContato;

    private String emailContato;

    private String dddTelefoneContato;

    private String decremento;

    private String taxaMin;

    private String email;

    public String getAdeCodigo() {
        return adeCodigo;
    }

    public void setAdeCodigo(String adeCodigo) {
        this.adeCodigo = adeCodigo;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public boolean isTemCET() {
        return temCET;
    }

    public void setTemCET(boolean temCET) {
        this.temCET = temCET;
    }

    public TransferObject getAde() {
        return ade;
    }

    public void setAde(TransferObject ade) {
        this.ade = ade;
    }

    public TransferObject getAdeOrigem() {
        return adeOrigem;
    }

    public void setAdeOreigem(TransferObject adeOrigem) {
        this.adeOrigem = adeOrigem;
    }

    public boolean isTemRiscoPelaCsa() {
        return temRiscoPelaCsa;
    }

    public void setTemRiscoPelaCsa(boolean temRiscoPelaCsa) {
        this.temRiscoPelaCsa = temRiscoPelaCsa;
    }

    public String getArrRisco() {
        return arrRisco;
    }

    public void setArrRisco(String arrRisco) {
        this.arrRisco = arrRisco;
    }

    public String getTipoVlrMargemDisponivel() {
        return tipoVlrMargemDisponivel;
    }

    public void setTipoVlrMargemDisponivel(String tipoVlrMargemDisponivel) {
        this.tipoVlrMargemDisponivel = tipoVlrMargemDisponivel;
    }

    public ExibeMargem getExibeMargem() {
        return exibeMargem;
    }

    public void setExibeMargem(ExibeMargem exibeMargem) {
        this.exibeMargem = exibeMargem;
    }

    public BigDecimal getRseMargemRest() {
        return rseMargemRest;
    }

    public void setRseMargemRest(BigDecimal rseMargemRest) {
        this.rseMargemRest = rseMargemRest;
    }

    public String getMargemConsignavel() {
        return margemConsignavel;
    }

    public void setMargemConsignavel(String margemConsignavel) {
        this.margemConsignavel = margemConsignavel;
    }

    public String getBcoDesc() {
        return bcoDesc;
    }

    public void setBcoDesc(String bcoDesc) {
        this.bcoDesc = bcoDesc;
    }

    public List<TransferObject> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<TransferObject> propostas) {
        this.propostas = propostas;
    }

    public String getSoaData() {
        return soaData;
    }

    public void setSoaData(String soaData) {
        this.soaData = soaData;
    }

    public String getSoaDataValidade() {
        return soaDataValidade;
    }

    public void setSoaDataValidade(String soaDataValidade) {
        this.soaDataValidade = soaDataValidade;
    }

    public boolean isPodeEditarProposta() {
        return podeEditarProposta;
    }

    public void setPodeEditarProposta(boolean podeEditarProposta) {
        this.podeEditarProposta = podeEditarProposta;
    }

    public List<TransferObject> getConvenio() {
        return convenio;
    }

    public void setConvenio(List<TransferObject> convenio) {
        this.convenio = convenio;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(String taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public String getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(String valorLiberado) {
        this.valorLiberado = valorLiberado;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public String getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(String valorParcela) {
        this.valorParcela = valorParcela;
    }

    public String getTxtContatoCsa() {
        return txtContatoCsa;
    }

    public void setTxtContatoCsa(String txtContatoCsa) {
        this.txtContatoCsa = txtContatoCsa;
    }

    public boolean isPlsCsaAprovada() {
        return plsCsaAprovada;
    }

    public void setPlsCsaAprovada(boolean plsCsaAprovada) {
        this.plsCsaAprovada = plsCsaAprovada;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getDddTelefoneContato() {
        return dddTelefoneContato;
    }

    public void setDddTelefoneContato(String dddTelefoneContato) {
        this.dddTelefoneContato = dddTelefoneContato;
    }

    public String getDecremento() {
        return decremento;
    }

    public void setDecremento(String decremento) {
        this.decremento = decremento;
    }

    public String getTaxaMin() {
        return taxaMin;
    }

    public void setTaxaMin(String taxaMin) {
        this.taxaMin = taxaMin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
