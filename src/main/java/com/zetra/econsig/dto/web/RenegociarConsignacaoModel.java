package com.zetra.econsig.dto.web;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: RenegociarConsignacaoModel</p>
 * <p>Description: Model para páginas de caso de uso de renegociar e comprar consignações.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RenegociarConsignacaoModel {

    private BigDecimal valorTotal;
    private BigDecimal vlrMargemRestNew;
    private BigDecimal vlrMaxParcelaSaldoDevedor;

    private boolean compra;
    private boolean serInfBancariaObrigatoria;
    private boolean validarInfBancaria;
    private boolean validarDataNasc;
    private boolean mensagemMargemComprometida;
    private boolean permiteCadVlrTac;
    private boolean permiteCadVlrIof;
    private boolean permiteCadVlrLiqLib;
    private boolean permiteCadVlrMensVinc;
    private boolean permiteVlrNegativo;
    private boolean permiteRenegociarComprarMargem3NegativaCasada;
    private boolean valorMaxIgualSomaContratos;
    private boolean prazoMaxIgualMaiorContratos;
    private boolean exigeModalidadeOperacao;
    private boolean exigeMatriculaSerCsa;
    private boolean rseTemInfBancaria;
    private boolean exigeEmailServidor;
    private boolean serSenhaObrigatoria;
    private boolean anexoInclusaoContratosObrigatorio;
    private boolean podeMostrarMargem;
    private boolean tpcExigeSenha;
    private boolean sistExibeHistLiqAntecipadas;
    private boolean possuiControleVlrMaxDesconto;
    private boolean permiteEscolherPeriodicidade;
    private boolean exibeVlrAtual;
    private boolean permitePrazoMaiorContSer;
    private boolean identificadorAdeObrigatorio;
    private boolean alteraAdeVlr;
    private boolean seguroPrestamista;
    private boolean permiteVlrLiqTxJuros;
    private boolean temCET;
    private boolean vlrIndiceDisabled;
    private boolean geraComboIndice;
    private boolean permiteCadIndice;
    private boolean indiceSomenteAutomatico;
    private boolean anexoObrigatorio;
    private boolean pmtCompMargem;
    private boolean exigeSenhaServidor;
    private boolean possuiVariacaoMargem;
    private boolean exibeAlgumaMargem;
    private boolean podeMostrarDatasRenegociacao;
    private boolean padraoAlterarDataEncerramento;

    private Date anoMesIniNovaAde;
    private Date ocaPeriodoRenegociacao;

    private Integer maiorPrazoRestante;
    private int carenciaMinPermitida;
    private int carenciaMaxPermitida;
    private int numAdeHistLiqAntecipadas;
    private int maxPrazo;

    private List<TransferObject> propostas;
    private List<TransferObject> autdesList;
    private List<String> adesReneg;
    private List<TransferObject> indices;
    private List<TransferObject> tdaList;

    private TransferObject autdes;

    private Set<Integer> prazosPossiveisMensal;
    private Set<Integer> prazosPossiveisPeriodicidadeFolha;

    private String mensagem;
    private String labelTipoVlr;
    private String serDataNasc;
    private String numBancoAlt;
    private String numAgenciaAlt;
    private String numContaAlt;
    private String numBanco;
    private String numAgencia;
    private String numConta;
    private String numConta1;
    private String numConta2;
    private String numContaAlt1;
    private String numContaAlt2;
    private String fileName;
    private String servico;
    private String csaNome;
    private String csaNomeAbrev;
    private String csaIdentificador;
    private String cnvCodigo;
    private String csaCodigo;
    private String orgCodigo;
    private String rseCodigo;
    private String rseMatricula;
    private String rsePrazo;
    private String svcCodigo;
    private String vlrLimite;
    private String rotuloPeriodicidadePrazo;
    private String descricaoTipoVlrMargem;
    private String cnvCodVerba;
    private String svcIdentifcador;
    private String svcDescricao;
    private String adeVlrPadrao;
    private String mascaraIndice;
    private String vlrIndice;
    private String mascaraAdeIdentificador;
    private String mascaraLogin;
    private String serNome;

    public String getSerNome() {
        return serNome;
    }

    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }

    public BigDecimal getVlrMaxParcelaSaldoDevedor() {
        return vlrMaxParcelaSaldoDevedor;
    }

    public void setVlrMaxParcelaSaldoDevedor(BigDecimal vlrMaxParcelaSaldoDevedor) {
        this.vlrMaxParcelaSaldoDevedor = vlrMaxParcelaSaldoDevedor;
    }

    public boolean isExibeAlgumaMargem() {
        return exibeAlgumaMargem;
    }

    public void setExibeAlgumaMargem(boolean exibeAlgumaMargem) {
        this.exibeAlgumaMargem = exibeAlgumaMargem;
    }

    public boolean isPossuiVariacaoMargem() {
        return possuiVariacaoMargem;
    }

    public void setPossuiVariacaoMargem(boolean possuiVariacaoMargem) {
        this.possuiVariacaoMargem = possuiVariacaoMargem;
    }

    public boolean isExigeSenhaServidor() {
        return exigeSenhaServidor;
    }

    public void setExigeSenhaServidor(boolean exigeSenhaServidor) {
        this.exigeSenhaServidor = exigeSenhaServidor;
    }

    public boolean isPmtCompMargem() {
        return pmtCompMargem;
    }

    public void setPmtCompMargem(boolean pmtCompMargem) {
        this.pmtCompMargem = pmtCompMargem;
    }

    public boolean isAnexoObrigatorio() {
        return anexoObrigatorio;
    }

    public void setAnexoObrigatorio(boolean anexoObrigatorio) {
        this.anexoObrigatorio = anexoObrigatorio;
    }

    public String getMascaraLogin() {
        return mascaraLogin;
    }

    public void setMascaraLogin(String mascaraLogin) {
        this.mascaraLogin = mascaraLogin;
    }

    public List<TransferObject> getTdaList() {
        return tdaList;
    }

    public void setTdaList(List<TransferObject> tdaList) {
        this.tdaList = tdaList;
    }

    public String getMascaraAdeIdentificador() {
        return mascaraAdeIdentificador;
    }

    public void setMascaraAdeIdentificador(String mascaraAdeIdentificador) {
        this.mascaraAdeIdentificador = mascaraAdeIdentificador;
    }

    public List<TransferObject> getIndices() {
        return indices;
    }

    public void setIndices(List<TransferObject> indices) {
        this.indices = indices;
    }

    public String getVlrIndice() {
        return vlrIndice;
    }

    public void setVlrIndice(String vlrIndice) {
        this.vlrIndice = vlrIndice;
    }

    public boolean isIndiceSomenteAutomatico() {
        return indiceSomenteAutomatico;
    }

    public void setIndiceSomenteAutomatico(boolean indiceSomenteAutomatico) {
        this.indiceSomenteAutomatico = indiceSomenteAutomatico;
    }

    public boolean isPermiteCadIndice() {
        return permiteCadIndice;
    }

    public void setPermiteCadIndice(boolean permiteCadIndice) {
        this.permiteCadIndice = permiteCadIndice;
    }

    public boolean isGeraComboIndice() {
        return geraComboIndice;
    }

    public void setGeraComboIndice(boolean geraComboIndice) {
        this.geraComboIndice = geraComboIndice;
    }

    public boolean isVlrIndiceDisabled() {
        return vlrIndiceDisabled;
    }

    public void setVlrIndiceDisabled(boolean vlrIndiceDisabled) {
        this.vlrIndiceDisabled = vlrIndiceDisabled;
    }

    public String getMascaraIndice() {
        return mascaraIndice;
    }

    public void setMascaraIndice(String mascaraIndice) {
        this.mascaraIndice = mascaraIndice;
    }

    public boolean isTemCET() {
        return temCET;
    }

    public void setTemCET(boolean temCET) {
        this.temCET = temCET;
    }

    public boolean isPermiteVlrLiqTxJuros() {
        return permiteVlrLiqTxJuros;
    }

    public void setPermiteVlrLiqTxJuros(boolean permiteVlrLiqTxJuros) {
        this.permiteVlrLiqTxJuros = permiteVlrLiqTxJuros;
    }

    public boolean isSeguroPrestamista() {
        return seguroPrestamista;
    }

    public void setSeguroPrestamista(boolean seguroPrestamista) {
        this.seguroPrestamista = seguroPrestamista;
    }

    public TransferObject getAutdes() {
        return autdes;
    }

    public void setAutdes(TransferObject autdes) {
        this.autdes = autdes;
    }

    public boolean isAlteraAdeVlr() {
        return alteraAdeVlr;
    }

    public void setAlteraAdeVlr(boolean alteraAdeVlr) {
        this.alteraAdeVlr = alteraAdeVlr;
    }

    public String getAdeVlrPadrao() {
        return adeVlrPadrao;
    }

    public void setAdeVlrPadrao(String adeVlrPadrao) {
        this.adeVlrPadrao = adeVlrPadrao;
    }

    public String getCnvCodVerba() {
        return cnvCodVerba;
    }

    public void setCnvCodVerba(String cnvCodVerba) {
        this.cnvCodVerba = cnvCodVerba;
    }

    public String getSvcIdentifcador() {
        return svcIdentifcador;
    }

    public void setSvcIdentifcador(String svcIdentifcador) {
        this.svcIdentifcador = svcIdentifcador;
    }

    public String getSvcDescricao() {
        return svcDescricao;
    }

    public void setSvcDescricao(String svcDescricao) {
        this.svcDescricao = svcDescricao;
    }

    public Set<Integer> getprazosPossiveisPeriodicidadeFolha() {
        return prazosPossiveisPeriodicidadeFolha;
    }

    public void setprazosPossiveisPeriodicidadeFolha(Set<Integer> prazosPossiveisPeriodicidadeFolha) {
        this.prazosPossiveisPeriodicidadeFolha = prazosPossiveisPeriodicidadeFolha;
    }

    public boolean isExibeVlrAtual() {
        return exibeVlrAtual;
    }

    public void setExibeVlrAtual(boolean exibeVlrAtual) {
        this.exibeVlrAtual = exibeVlrAtual;
    }

    public int getMaxPrazo() {
        return maxPrazo;
    }

    public void setMaxPrazo(int maxPrazo) {
        this.maxPrazo = maxPrazo;
    }

    public boolean isPermitePrazoMaiorContSer() {
        return permitePrazoMaiorContSer;
    }

    public void setPermitePrazoMaiorContSer(boolean permitePrazoMaiorContSer) {
        this.permitePrazoMaiorContSer = permitePrazoMaiorContSer;
    }

    public boolean isIdentificadorAdeObrigatorio() {
        return identificadorAdeObrigatorio;
    }

    public void setIdentificadorAdeObrigatorio(boolean identificadorAdeObrigatorio) {
        this.identificadorAdeObrigatorio = identificadorAdeObrigatorio;
    }

    public BigDecimal getVlrMargemRestNew() {
        return vlrMargemRestNew;
    }

    public void setVlrMargemRestNew(BigDecimal vlrMargemRestNew) {
        this.vlrMargemRestNew = vlrMargemRestNew;
    }

    public String getDescricaoTipoVlrMargem() {
        return descricaoTipoVlrMargem;
    }

    public void setDescricaoTipoVlrMargem(String descricaoTipoVlrMargem) {
        this.descricaoTipoVlrMargem = descricaoTipoVlrMargem;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public String getRseMatricula() {
        return rseMatricula;
    }

    public void setRseMatricula(String rseMatricula) {
        this.rseMatricula = rseMatricula;
    }

    public String getRsePrazo() {
        return rsePrazo;
    }

    public void setRsePrazo(String rsePrazo) {
        this.rsePrazo = rsePrazo;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getVlrLimite() {
        return vlrLimite;
    }

    public void setVlrLimite(String vlrLimite) {
        this.vlrLimite = vlrLimite;
    }

    public boolean isPossuiControleVlrMaxDesconto() {
        return possuiControleVlrMaxDesconto;
    }

    public void setPossuiControleVlrMaxDesconto(boolean possuiControleVlrMaxDesconto) {
        this.possuiControleVlrMaxDesconto = possuiControleVlrMaxDesconto;
    }

    public boolean isPermiteEscolherPeriodicidade() {
        return permiteEscolherPeriodicidade;
    }

    public void setPermiteEscolherPeriodicidade(boolean permiteEscolherPeriodicidade) {
        this.permiteEscolherPeriodicidade = permiteEscolherPeriodicidade;
    }

    public String getRotuloPeriodicidadePrazo() {
        return rotuloPeriodicidadePrazo;
    }

    public void setRotuloPeriodicidadePrazo(String rotuloPeriodicidadePrazo) {
        this.rotuloPeriodicidadePrazo = rotuloPeriodicidadePrazo;
    }

    public int getNumAdeHistLiqAntecipadas() {
        return numAdeHistLiqAntecipadas;
    }

    public void setNumAdeHistLiqAntecipadas(int numAdeHistLiqAntecipadas) {
        this.numAdeHistLiqAntecipadas = numAdeHistLiqAntecipadas;
    }

    public List<String> getAdesReneg() {
        return adesReneg;
    }

    public void setAdesReneg(List<String> adesReneg) {
        this.adesReneg = adesReneg;
    }

    public String getCnvCodigo() {
        return cnvCodigo;
    }

    public void setCnvCodigo(String cnvCodigo) {
        this.cnvCodigo = cnvCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public boolean isTpcExigeSenha() {
        return tpcExigeSenha;
    }

    public void setTpcExigeSenha(boolean tpcExigeSenha) {
        this.tpcExigeSenha = tpcExigeSenha;
    }

    public boolean isPodeMostrarMargem() {
        return podeMostrarMargem;
    }

    public void setPodeMostrarMargem(boolean podeMostrarMargem) {
        this.podeMostrarMargem = podeMostrarMargem;
    }

    public boolean isSistExibeHistLiqAntecipadas() {
        return sistExibeHistLiqAntecipadas;
    }

    public void setSistExibeHistLiqAntecipadas(boolean sistExibeHistLiqAntecipadas) {
        this.sistExibeHistLiqAntecipadas = sistExibeHistLiqAntecipadas;
    }

    public boolean isSerInfBancariaObrigatoria() {
        return serInfBancariaObrigatoria;
    }

    public void setSerInfBancariaObrigatoria(boolean serInfBancariaObrigatoria) {
        this.serInfBancariaObrigatoria = serInfBancariaObrigatoria;
    }

    public boolean isCompra() {
        return compra;
    }

    public void setCompra(boolean compra) {
        this.compra = compra;
    }

    public boolean isValidarInfBancaria() {
        return validarInfBancaria;
    }

    public void setValidarInfBancaria(boolean validarInfBancaria) {
        this.validarInfBancaria = validarInfBancaria;
    }

    public boolean isValidarDataNasc() {
        return validarDataNasc;
    }

    public void setValidarDataNasc(boolean validarDataNasc) {
        this.validarDataNasc = validarDataNasc;
    }

    public List<TransferObject> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<TransferObject> propostas) {
        this.propostas = propostas;
    }

    public boolean isMensagemMargemComprometida() {
        return mensagemMargemComprometida;
    }

    public void setMensagemMargemComprometida(boolean mensagemMargemComprometida) {
        this.mensagemMargemComprometida = mensagemMargemComprometida;
    }

    public boolean isPermiteCadVlrTac() {
        return permiteCadVlrTac;
    }

    public void setPermiteCadVlrTac(boolean permiteCadVlrTac) {
        this.permiteCadVlrTac = permiteCadVlrTac;
    }

    public boolean isPermiteCadVlrIof() {
        return permiteCadVlrIof;
    }

    public void setPermiteCadVlrIof(boolean permiteCadVlrIof) {
        this.permiteCadVlrIof = permiteCadVlrIof;
    }

    public boolean isPermiteCadVlrLiqLib() {
        return permiteCadVlrLiqLib;
    }

    public void setPermiteCadVlrLiqLib(boolean permiteCadVlrLiqLib) {
        this.permiteCadVlrLiqLib = permiteCadVlrLiqLib;
    }

    public boolean isPermiteCadVlrMensVinc() {
        return permiteCadVlrMensVinc;
    }

    public void setPermiteCadVlrMensVinc(boolean permiteCadVlrMensVinc) {
        this.permiteCadVlrMensVinc = permiteCadVlrMensVinc;
    }

    public boolean permiteVlrNegativo() {
        return permiteVlrNegativo;
    }

    public boolean isPermiteRenegociarComprarMargem3NegativaCasada() {
        return permiteRenegociarComprarMargem3NegativaCasada;
    }

    public void setPermiteVlrNegativo(boolean permiteVlrNegativo) {
        this.permiteVlrNegativo = permiteVlrNegativo;
    }

    public void setPermiteRenegociarComprarMargem3NegativaCasada(boolean permiteRenegociarComprarMargem3NegativaCasada) {
        this.permiteRenegociarComprarMargem3NegativaCasada = permiteRenegociarComprarMargem3NegativaCasada;
    }

    public boolean isValorMaxIgualSomaContratos() {
        return valorMaxIgualSomaContratos;
    }

    public void setValorMaxIgualSomaContratos(boolean valorMaxIgualSomaContratos) {
        this.valorMaxIgualSomaContratos = valorMaxIgualSomaContratos;
    }

    public boolean isPrazoMaxIgualMaiorContratos() {
        return prazoMaxIgualMaiorContratos;
    }

    public void setPrazoMaxIgualMaiorContratos(boolean prazoMaxIgualMaiorContratos) {
        this.prazoMaxIgualMaiorContratos = prazoMaxIgualMaiorContratos;
    }

    public boolean isExigeModalidadeOperacao() {
        return exigeModalidadeOperacao;
    }

    public void setExigeModalidadeOperacao(boolean exigeModalidadeOperacao) {
        this.exigeModalidadeOperacao = exigeModalidadeOperacao;
    }

    public boolean isExigeMatriculaSerCsa() {
        return exigeMatriculaSerCsa;
    }

    public void setExigeMatriculaSerCsa(boolean exigeMatriculaSerCsa) {
        this.exigeMatriculaSerCsa = exigeMatriculaSerCsa;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getLabelTipoVlr() {
        return labelTipoVlr;
    }

    public void setLabelTipoVlr(String labelTipoVlr) {
        this.labelTipoVlr = labelTipoVlr;
    }

    public Integer getMaiorPrazoRestante() {
        return maiorPrazoRestante;
    }

    public void setMaiorPrazoRestante(Integer maiorPrazoRestante) {
        this.maiorPrazoRestante = maiorPrazoRestante;
    }

    public String getSerDataNasc() {
        return serDataNasc;
    }

    public void setSerDataNasc(String serDataNasc) {
        this.serDataNasc = serDataNasc;
    }

    public boolean isRseTemInfBancaria() {
        return rseTemInfBancaria;
    }

    public void setRseTemInfBancaria(boolean rseTemInfBancaria) {
        this.rseTemInfBancaria = rseTemInfBancaria;
    }

    public String getNumBancoAlt() {
        return numBancoAlt;
    }

    public void setNumBancoAlt(String numBancoAlt) {
        this.numBancoAlt = numBancoAlt;
    }

    public String getNumAgenciaAlt() {
        return numAgenciaAlt;
    }

    public void setNumAgenciaAlt(String numAgenciaAlt) {
        this.numAgenciaAlt = numAgenciaAlt;
    }

    public String getNumContaAlt() {
        return numContaAlt;
    }

    public void setNumContaAlt(String numContaAlt) {
        this.numContaAlt = numContaAlt;
    }

    public String getNumBanco() {
        return numBanco;
    }

    public void setNumBanco(String numBanco) {
        this.numBanco = numBanco;
    }

    public String getNumAgencia() {
        return numAgencia;
    }

    public void setNumAgencia(String numAgencia) {
        this.numAgencia = numAgencia;
    }

    public String getNumConta() {
        return numConta;
    }

    public void setNumConta(String numConta) {
        this.numConta = numConta;
    }

    public String getNumConta1() {
        return numConta1;
    }

    public void setNumConta1(String numConta1) {
        this.numConta1 = numConta1;
    }

    public String getNumConta2() {
        return numConta2;
    }

    public void setNumConta2(String numConta2) {
        this.numConta2 = numConta2;
    }

    public String getNumContaAlt1() {
        return numContaAlt1;
    }

    public void setNumContaAlt1(String numContaAlt1) {
        this.numContaAlt1 = numContaAlt1;
    }

    public String getNumContaAlt2() {
        return numContaAlt2;
    }

    public void setNumContaAlt2(String numContaAlt2) {
        this.numContaAlt2 = numContaAlt2;
    }

    public int getCarenciaMinPermitida() {
        return carenciaMinPermitida;
    }

    public void setCarenciaMinPermitida(int carenciaMinPermitida) {
        this.carenciaMinPermitida = carenciaMinPermitida;
    }

    public int getCarenciaMaxPermitida() {
        return carenciaMaxPermitida;
    }

    public void setCarenciaMaxPermitida(int carenciaMaxPermitida) {
        this.carenciaMaxPermitida = carenciaMaxPermitida;
    }

    public boolean isExigeEmailServidor() {
        return exigeEmailServidor;
    }

    public void setExigeEmailServidor(boolean exigeEmailServidor) {
        this.exigeEmailServidor = exigeEmailServidor;
    }

    public boolean isSerSenhaObrigatoria() {
        return serSenhaObrigatoria;
    }

    public void setSerSenhaObrigatoria(boolean serSenhaObrigatoria) {
        this.serSenhaObrigatoria = serSenhaObrigatoria;
    }

    public boolean isAnexoInclusaoContratosObrigatorio() {
        return anexoInclusaoContratosObrigatorio;
    }

    public void setAnexoInclusaoContratosObrigatorio(boolean anexoInclusaoContratosObrigatorio) {
        this.anexoInclusaoContratosObrigatorio = anexoInclusaoContratosObrigatorio;
    }

    public Set<Integer> getPrazosPossiveisMensal() {
        return prazosPossiveisMensal;
    }

    public void setPrazosPossiveisMensal(Set<Integer> prazosPossiveisMensal) {
        this.prazosPossiveisMensal = prazosPossiveisMensal;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<TransferObject> getAutdesList() {
        return autdesList;
    }

    public void setAutdesList(List<TransferObject> autdesList) {
        this.autdesList = autdesList;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }

    public boolean isPodeMostrarDatasRenegociacao() {
        return podeMostrarDatasRenegociacao;
    }

    public void setPodeMostrarDatasRenegociacao(boolean podeMostrarDatasRenegociacao) {
        this.podeMostrarDatasRenegociacao = podeMostrarDatasRenegociacao;
    }

    public boolean isPadraoAlterarDataEncerramento() {
        return padraoAlterarDataEncerramento;
    }

    public void setPadraoAlterarDataEncerramento(boolean padraoAlterarDataEncerramento) {
        this.padraoAlterarDataEncerramento = padraoAlterarDataEncerramento;
    }

    public Date getAnoMesIniNovaAde() {
        return anoMesIniNovaAde;
    }

    public void setAnoMesIniNovaAde(Date anoMesIniNovaAde) {
        this.anoMesIniNovaAde = anoMesIniNovaAde;
    }

    public Date getOcaPeriodoRenegociacao() {
        return ocaPeriodoRenegociacao;
    }

    public void setOcaPeriodoRenegociacao(Date ocaPeriodoRenegociacao) {
        this.ocaPeriodoRenegociacao = ocaPeriodoRenegociacao;
    }

}
