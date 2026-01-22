package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ReservarMargemParametros</p>
 * <p>Description: Parâmetros necessários na reserva de margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReservarMargemParametros extends Parametros {

    public final static boolean PADRAO_VALIDA_EXIGE_INF_BANCARIA = true;
    public final static boolean PADRAO_VALIDA_MARGEM = true;
    public final static boolean PADRAO_VALIDA_TAXA_JUROS = true;
    public final static boolean PADRAO_VALIDA_PRAZO = true;
    public final static boolean PADRAO_VALIDA_DADOS_BANCARIOS = true;
    public final static boolean PADRAO_VALIDA_SENHA_SERVIDOR = true;
    public final static boolean PADRAO_VALIDA_BLOQ_SER_CNV_CSA = true;
    public final static boolean PADRAO_VALIDA_DATA_NASCIMENTO = true;
    public final static boolean PADRAO_CONSOME_SENHA_RESERVA = true;
    public final static boolean PADRAO_VALIDA_LIMITE_ADE = true;
    public final static boolean PADRAO_VALIDA_ANEXO = true;
    public final static boolean PADRAO_VALIDA_ADE_IDENTIFICADOR = true;

    private String rseCodigo = null;
    private String cnvCodigo = null;
    private String corCodigo = null;
    private String sadCodigo = null;
    private String cftCodigo = null;
    private String dtjCodigo = null;
    private String cidCodigo = null;

    private Integer adePrazo = null;
    private Integer adeCarencia = null;

    private java.util.Date adeAnoMesIni = null;
    private java.util.Date adeAnoMesFim = null;
    private java.sql.Date adeAnoMesIniRef = null;
    private java.sql.Date adeAnoMesFimRef = null;
    private Timestamp adeDtHrOcorrencia = null;

    private Short adeIntFolha = null;
    private Short adeIncMargem = null;
    private Short cdeRanking = null;

    private String adeTipoVlr = null;
    private String adeIndice = null;
    private String adeIdentificador = null;
    private String adePeriodicidade = null;
    private String acao = null;
    private String adeCodReg = null;
    private String cdeTxtContato = null;
    private String nomeResponsavel = null;
    private String serSenha = null;
    private String svcCodigoOrigem = null;

    private String adeAgencia = null;
    private String adeBanco = null;
    private String adeConta = null;

    private BigDecimal adeVlr = null;
    private BigDecimal adeVlrTac = null;
    private BigDecimal adeVlrIof = null;
    private BigDecimal adeVlrLiquido = null;
    private BigDecimal adeVlrMensVinc = null;
    private BigDecimal adeVlrOriginal = null;
    private BigDecimal adeVlrCorrecao = null;
    private BigDecimal adeTaxaJuros = null;
    private BigDecimal adeVlrSegPrestamista = null;
    private BigDecimal cdeVlrLiberado = null;
    private BigDecimal vlrTotalCompradoRenegociado = null;

    private Boolean comSerSenha = null;
    private Boolean validar = null;
    private Boolean permitirValidacaoTaxa = null;
    private Boolean serAtivo = null;
    private Boolean cnvAtivo = null;
    private Boolean serCnvAtivo = null;
    private Boolean svcAtivo = null;
    private Boolean csaAtivo = null;
    private Boolean orgAtivo = null;
    private Boolean estAtivo = null;
    private Boolean cseAtivo = null;
    private Boolean aceitoTermoUsoColetaDados = null;

    private String tmoCodigo = null;
    private String ocaObs = null;
    private String ocaPeriodo;

    // a ser usado por reserva decorrente de reimplante de capital devido
    // permite que seja feita uma reserva avançada mesmo com papéis CSA/COR.
    private Boolean isReimpCapitalDevido = false;

    // a ser usado por reserva decorrente de retenção de verba rescisória
    // permite que seja feita uma reserva avançada com papéis CSE/ORG/SUP.
    private Boolean isRetencaoVerbaRescisoria = false;

    private Boolean consomeSenha = PADRAO_CONSOME_SENHA_RESERVA;
    private Boolean validaExigeInfBancaria = PADRAO_VALIDA_EXIGE_INF_BANCARIA;

    private File anexo;
    private String nomeAnexo = null;
    private String aadDescricao = null;
    private String idAnexo = null;
    private Boolean validaAnexo = PADRAO_VALIDA_ANEXO;
    private String tdaModalidadeOperacao;
    private String tdaMatriculaSerCsa;
    private String exigenciaConfirmacaoLeitura;
    protected String tdaTelSolicitacaoSer;

    // parâmetros de inclusão avançada
    private Boolean validaMargem = PADRAO_VALIDA_MARGEM;
    private Boolean validaTaxaJuros = PADRAO_VALIDA_TAXA_JUROS;
    private Boolean validaPrazo = PADRAO_VALIDA_PRAZO;
    private Boolean validaDadosBancarios = PADRAO_VALIDA_DADOS_BANCARIOS;
    private Boolean validaSenhaServidor = PADRAO_VALIDA_SENHA_SERVIDOR;
    private Boolean validaBloqSerCnvCsa = PADRAO_VALIDA_BLOQ_SER_CNV_CSA;
    private Boolean validaDataNascimento = PADRAO_VALIDA_DATA_NASCIMENTO;
    private Boolean validaLimiteAde = PADRAO_VALIDA_LIMITE_ADE;
    private Boolean validaAdeIdentificador = PADRAO_VALIDA_ADE_IDENTIFICADOR;

    // parâmetros para o leilão reverso.
    private boolean iniciarLeilaoReverso = false;
    private boolean destinoAprovacaoLeilaoReverso = false;
    private boolean simulacaoPorAdeVlr = false;

    // parâmetros para reserva de benefícios
    private String cbeCodigo = null;
    private String tlaCodigo = null;

    private Map<String, Object> parametros;
    private List<String> adeCodigosRenegociacao;

    // Dados da tb_dados_autorizacao_desconto
    private Map<String,String> dadosAutorizacao = new HashMap<>();

    private boolean telaConfirmacaoDuplicidade = false;
    private boolean chkConfirmarDuplicidade = false;
    private String motivoOperacaoCodigoDuplicidade;
    private String motivoOperacaoObsDuplicidade;

    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;

    //verificar se força o periodo de lançamento cartão
    private boolean forcaPeriodoLancamentoCartao = false;

    //Inclusão judicial é considerado uma inclusão avançada sem validação de nenhum parâmetro
    private boolean inclusaoJucicial = false;
    
    //determina se é incluído atraves de mobile servidor econsig
    private boolean mobileEconsig = false;

    public ReservarMargemParametros() {
        requiredFieldsMap = new HashMap<>();

        requiredFieldsMap.put(ParametrosFieldNames.RSE_CODIGO, rseCodigo);
        requiredFieldsMap.put(ParametrosFieldNames.ADE_VLR, adeVlr);
        requiredFieldsMap.put(ParametrosFieldNames.CNV_CODIGO, cnvCodigo);
        requiredFieldsMap.put(ParametrosFieldNames.ADE_TIPO_VLR, adeTipoVlr);
        requiredFieldsMap.put(ParametrosFieldNames.ADE_INT_FOLHA, adeIntFolha);
        requiredFieldsMap.put(ParametrosFieldNames.COM_SER_SENHA, comSerSenha);
        requiredFieldsMap.put(ParametrosFieldNames.VALIDAR, validar);
        requiredFieldsMap.put(ParametrosFieldNames.PERMITIR_VALIDACAO_TAXA, permitirValidacaoTaxa);
        requiredFieldsMap.put(ParametrosFieldNames.SER_ATIVO, serAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.CNV_ATIVO, cnvAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.SER_CNV_ATIVO, serCnvAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.SVC_ATIVO, svcAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.CSA_ATIVO, csaAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.ORG_ATIVO, orgAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.EST_ATIVO, estAtivo);
        requiredFieldsMap.put(ParametrosFieldNames.CSE_ATIVO, cseAtivo);
    }

    public BigDecimal getAdeVlrCorrecao() {
        return adeVlrCorrecao;
    }

    public void setAdeVlrCorrecao(BigDecimal adeVlrCorrecao) {
        this.adeVlrCorrecao = adeVlrCorrecao;
    }

    public BigDecimal getAdeVlrOriginal() {
        return adeVlrOriginal;
    }

    public void setAdeVlrOriginal(BigDecimal adeVlrOriginal) {
        this.adeVlrOriginal = adeVlrOriginal;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public java.util.Date getAdeAnoMesFim() {
        return adeAnoMesFim;
    }

    public void setAdeAnoMesFim(java.util.Date adeAnoMesFim) {
        this.adeAnoMesFim = adeAnoMesFim;
    }

    public java.sql.Date getAdeAnoMesFimRef() {
        return adeAnoMesFimRef;
    }

    public void setAdeAnoMesFimRef(java.sql.Date adeAnoMesFimRef) {
        this.adeAnoMesFimRef = adeAnoMesFimRef;
    }

    public java.util.Date getAdeAnoMesIni() {
        return adeAnoMesIni;
    }

    public void setAdeAnoMesIni(java.util.Date adeAnoMesIni) {
        this.adeAnoMesIni = adeAnoMesIni;
    }

    public Integer getAdeCarencia() {
        return adeCarencia;
    }

    public void setAdeCarencia(Integer adeCarencia) {
        this.adeCarencia = adeCarencia;
    }

    public String getAdeCodReg() {
        return adeCodReg;
    }

    public void setAdeCodReg(String adeCodReg) {
        this.adeCodReg = adeCodReg;
    }

    public String getAdeIdentificador() {
        return adeIdentificador;
    }

    public void setAdeIdentificador(String adeIdentificador) {
        this.adeIdentificador = adeIdentificador;
    }

    public Short getAdeIncMargem() {
        return adeIncMargem;
    }

    public void setAdeIncMargem(Short adeIncMargem) {
        this.adeIncMargem = adeIncMargem;
    }

    public String getAdeIndice() {
        return adeIndice;
    }

    public void setAdeIndice(String adeIndice) {
        this.adeIndice = adeIndice;
    }

    public Short getAdeIntFolha() {
        return adeIntFolha;
    }

    public void setAdeIntFolha(Short adeIntFolha) {
        this.adeIntFolha = adeIntFolha;
        requiredFieldsMap.put(ParametrosFieldNames.ADE_INT_FOLHA, adeIntFolha);
    }

    public Integer getAdePrazo() {
        return adePrazo;
    }

    public void setAdePrazo(Integer adePrazo) {
        this.adePrazo = adePrazo;
    }

    public BigDecimal getAdeTaxaJuros() {
        return adeTaxaJuros;
    }

    public void setAdeTaxaJuros(BigDecimal adeTaxaJuros) {
        this.adeTaxaJuros = adeTaxaJuros;
    }

    public String getAdeTipoVlr() {
        return adeTipoVlr;
    }

    public void setAdeTipoVlr(String adeTipoVlr) {
        this.adeTipoVlr = adeTipoVlr;
        requiredFieldsMap.put(ParametrosFieldNames.ADE_TIPO_VLR, adeTipoVlr);
    }

    public BigDecimal getAdeVlr() {
        return adeVlr;
    }

    public void setAdeVlr(BigDecimal adeVlr) {
        this.adeVlr = adeVlr;
        requiredFieldsMap.put(ParametrosFieldNames.ADE_VLR, adeVlr);
    }

    public BigDecimal getAdeVlrIof() {
        return adeVlrIof;
    }

    public void setAdeVlrIof(BigDecimal adeVlrIof) {
        this.adeVlrIof = adeVlrIof;
    }

    public BigDecimal getAdeVlrLiquido() {
        return adeVlrLiquido;
    }

    public void setAdeVlrLiquido(BigDecimal adeVlrLiquido) {
        this.adeVlrLiquido = adeVlrLiquido;
    }

    public BigDecimal getAdeVlrMensVinc() {
        return adeVlrMensVinc;
    }

    public void setAdeVlrMensVinc(BigDecimal adeVlrMensVinc) {
        this.adeVlrMensVinc = adeVlrMensVinc;
    }

    public BigDecimal getAdeVlrTac() {
        return adeVlrTac;
    }

    public void setAdeVlrTac(BigDecimal adeVlrTac) {
        this.adeVlrTac = adeVlrTac;
    }

    public java.sql.Date getAdeAnoMesIniRef() {
        return adeAnoMesIniRef;
    }

    public void setAdeAnoMesIniRef(java.sql.Date adeAnoMesIniRef) {
        this.adeAnoMesIniRef = adeAnoMesIniRef;
    }

    public BigDecimal getAdeVlrSegPrestamista() {
        return adeVlrSegPrestamista;
    }

    public void setAdeVlrSegPrestamista(BigDecimal adeVlrSegPrestamista) {
        this.adeVlrSegPrestamista = adeVlrSegPrestamista;
    }

    public Boolean getCnvAtivo() {
        return cnvAtivo;
    }

    public void setCnvAtivo(Boolean cnvAtivo) {
        this.cnvAtivo = cnvAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.CNV_ATIVO, cnvAtivo);
    }

    public String getCnvCodigo() {
        return cnvCodigo;
    }

    public void setCnvCodigo(String cnvCodigo) {
        this.cnvCodigo = cnvCodigo;
        requiredFieldsMap.put(ParametrosFieldNames.CNV_CODIGO, cnvCodigo);
    }

    public Boolean getComSerSenha() {
        return comSerSenha;
    }

    public void setComSerSenha(Boolean comSerSenha) {
        this.comSerSenha = comSerSenha;
        requiredFieldsMap.put(ParametrosFieldNames.COM_SER_SENHA, comSerSenha);
    }

    public String getCorCodigo() {
        return corCodigo;
    }

    public void setCorCodigo(String corCodigo) {
        this.corCodigo = corCodigo;
    }

    public Boolean getCsaAtivo() {
        return csaAtivo;
    }

    public void setCsaAtivo(Boolean csaAtivo) {
        this.csaAtivo = csaAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.CSA_ATIVO, csaAtivo);
    }

    public Boolean getCseAtivo() {
        return cseAtivo;
    }

    public void setCseAtivo(Boolean cseAtivo) {
        this.cseAtivo = cseAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.CSE_ATIVO, cseAtivo);
    }

    public Boolean getEstAtivo() {
        return estAtivo;
    }

    public void setEstAtivo(Boolean estAtivo) {
        this.estAtivo = estAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.EST_ATIVO, estAtivo);
    }

    public Boolean getOrgAtivo() {
        return orgAtivo;
    }

    public void setOrgAtivo(Boolean orgAtivo) {
        this.orgAtivo = orgAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.ORG_ATIVO, orgAtivo);
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }

    public Boolean getPermitirValidacaoTaxa() {
        return permitirValidacaoTaxa;
    }

    public void setPermitirValidacaoTaxa(Boolean permitirValidacaoTaxa) {
        this.permitirValidacaoTaxa = permitirValidacaoTaxa;
        requiredFieldsMap.put(ParametrosFieldNames.PERMITIR_VALIDACAO_TAXA, permitirValidacaoTaxa);
    }

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
        requiredFieldsMap.put(ParametrosFieldNames.RSE_CODIGO, rseCodigo);
    }

    public String getSadCodigo() {
        return sadCodigo;
    }

    public void setSadCodigo(String sadCodigo) {
        this.sadCodigo = sadCodigo;
    }

    public Boolean getSerAtivo() {
        return serAtivo;
    }

    public void setSerAtivo(Boolean serAtivo) {
        this.serAtivo = serAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.SER_ATIVO, serAtivo);
    }

    public Boolean getSerCnvAtivo() {
        return serCnvAtivo;
    }

    public void setSerCnvAtivo(Boolean serCnvAtivo) {
        this.serCnvAtivo = serCnvAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.SER_CNV_ATIVO, serCnvAtivo);
    }

    public Boolean getSvcAtivo() {
        return svcAtivo;
    }

    public void setSvcAtivo(Boolean svcAtivo) {
        this.svcAtivo = svcAtivo;
        requiredFieldsMap.put(ParametrosFieldNames.SVC_ATIVO, svcAtivo);
    }

    public Timestamp getAdeDtHrOcorrencia() {
        return adeDtHrOcorrencia;
    }

    public void setAdeDtHrOcorrencia(Timestamp adeDtHrOcorrencia) {
        this.adeDtHrOcorrencia = adeDtHrOcorrencia;
    }

    public Boolean getValidar() {
        return validar;
    }

    public void setValidar(Boolean validar) {
        this.validar = validar;
        requiredFieldsMap.put(ParametrosFieldNames.VALIDAR, validar);
    }

    public List<String> getAdeCodigosRenegociacao() {
        return adeCodigosRenegociacao;
    }

    public void setAdeCodigosRenegociacao(List<String> adeCodigosRenegociacao) {
        this.adeCodigosRenegociacao = adeCodigosRenegociacao;
    }

    public BigDecimal getCdeVlrLiberado() {
        return cdeVlrLiberado;
    }

    public void setCdeVlrLiberado(BigDecimal cdeVlrLiberado) {
        this.cdeVlrLiberado = cdeVlrLiberado;
    }

    public String getCftCodigo() {
        return cftCodigo;
    }

    public void setCftCodigo(String cftCodigo) {
        this.cftCodigo = cftCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
    }

    public Short getCdeRanking() {
        return cdeRanking;
    }

    public void setCdeRanking(Short cdeRanking) {
        this.cdeRanking = cdeRanking;
    }

    public String getCdeTxtContato() {
        return cdeTxtContato;
    }

    public void setCdeTxtContato(String cdeTxtContato) {
        this.cdeTxtContato = cdeTxtContato;
    }

    public String getAdeAgencia() {
        return adeAgencia;
    }

    public void setAdeAgencia(String adeAgencia) {
        this.adeAgencia = adeAgencia;
    }

    public String getAdeBanco() {
        return adeBanco;
    }

    public void setAdeBanco(String adeBanco) {
        this.adeBanco = adeBanco;
    }

    public String getAdeConta() {
        return adeConta;
    }

    public void setAdeConta(String adeConta) {
        this.adeConta = adeConta;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public Boolean getValidaExigeInfBancaria() {
        return validaExigeInfBancaria;
    }

    public void setValidaExigeInfBancaria(Boolean validaExigeInfBancaria) {
        this.validaExigeInfBancaria = validaExigeInfBancaria;
    }

    public Boolean getValidaMargem() {
        return validaMargem;
    }

    public void setValidaMargem(Boolean validaMargem) {
        this.validaMargem = validaMargem;
    }

    public Boolean getValidaTaxaJuros() {
        return validaTaxaJuros;
    }

    public void setValidaTaxaJuros(Boolean validaTaxaJuros) {
        this.validaTaxaJuros = validaTaxaJuros;
    }

    public Boolean getValidaPrazo() {
        return validaPrazo;
    }

    public void setValidaPrazo(Boolean validaPrazo) {
        this.validaPrazo = validaPrazo;
    }

    public Boolean getValidaDadosBancarios() {
        return validaDadosBancarios;
    }

    public void setValidaDadosBancarios(Boolean validaDadosBancarios) {
        this.validaDadosBancarios = validaDadosBancarios;
    }

    public Boolean getValidaSenhaServidor() {
        return validaSenhaServidor;
    }

    public void setValidaSenhaServidor(Boolean validaSenhaServidor) {
        this.validaSenhaServidor = validaSenhaServidor;
    }

    public Boolean getValidaBloqSerCnvCsa() {
        return validaBloqSerCnvCsa;
    }

    public void setValidaBloqSerCnvCsa(Boolean validaBloqSerCnvCsa) {
        this.validaBloqSerCnvCsa = validaBloqSerCnvCsa;
    }

    public Boolean getValidaDataNascimento() {
        return validaDataNascimento;
    }

    public void setValidaDataNascimento(Boolean validaDataNascimento) {
        this.validaDataNascimento = validaDataNascimento;
    }

    public Boolean getValidaLimiteAde() {
        return validaLimiteAde;
    }

    public void setValidaLimiteAde(Boolean validaLimiteAde) {
        this.validaLimiteAde = validaLimiteAde;
    }

    public Boolean getValidaAdeIdentificador() {
        return validaAdeIdentificador;
    }

    public void setValidaAdeIdentificador(Boolean validaAdeIdentificador) {
        this.validaAdeIdentificador = validaAdeIdentificador;
    }

    public String getTmoCodigo() {
        return tmoCodigo;
    }

    public void setTmoCodigo(String tmoCodigo) {
        this.tmoCodigo = tmoCodigo;
    }

    public String getOcaObs() {
        return ocaObs;
    }

    public void setOcaObs(String ocaObs) {
        this.ocaObs = ocaObs;
    }

    public String getOcaPeriodo() {
        return ocaPeriodo;
    }

    public void setOcaPeriodo(String ocaPeriodo) {
        this.ocaPeriodo = ocaPeriodo;
    }

    public String getSerSenha() {
        return serSenha;
    }

    public void setSerSenha(String serSenha) {
        this.serSenha = serSenha;
    }

    public Boolean getConsomeSenha() {
        return consomeSenha;
    }

    public void setConsomeSenha(Boolean consomeSenha) {
        this.consomeSenha = (consomeSenha == null ? PADRAO_CONSOME_SENHA_RESERVA : consomeSenha);
    }

    public BigDecimal getVlrTotalCompradoRenegociado() {
        return vlrTotalCompradoRenegociado;
    }

    public void setVlrTotalCompradoRenegociado(BigDecimal vlrTotalCompradoRenegociado) {
        this.vlrTotalCompradoRenegociado = vlrTotalCompradoRenegociado;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public String getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(String idAnexo) {
        this.idAnexo = idAnexo;
    }

    public String getAadDescricao() {
        return aadDescricao;
    }

    public void setAadDescricao(String aadDescricao) {
        this.aadDescricao = aadDescricao;
    }

    public Boolean getValidaAnexo() {
        return validaAnexo;
    }

    public void setValidaAnexo(Boolean validaAnexo) {
        this.validaAnexo = validaAnexo;
    }

    public String getAdePeriodicidade() {
        return adePeriodicidade;
    }

    public void setAdePeriodicidade(String adePeriodicidade) {
        this.adePeriodicidade = adePeriodicidade;
    }

    public String getTdaModalidadeOperacao() {
        return tdaModalidadeOperacao;
    }

    public void setTdaModalidadeOperacao(String tdaModalidadeOperacao) {
        this.tdaModalidadeOperacao = tdaModalidadeOperacao;
    }

    public String getTdaMatriculaSerCsa() {
        return tdaMatriculaSerCsa;
    }

    public void setTdaMatriculaSerCsa(String tdaMatriculaSerCsa) {
        this.tdaMatriculaSerCsa = tdaMatriculaSerCsa;
    }

    public String getTdaTelSolicitacaoSer() {
        return tdaTelSolicitacaoSer;
    }

    public void setTdaTelSolicitacaoSer(String tdaTelSolicitacaoSer) {
        this.tdaTelSolicitacaoSer = tdaTelSolicitacaoSer;
    }

    public Boolean isReimpCapitalDevido() {
        return isReimpCapitalDevido;
    }

    public void setIsReimpCapitalDevido(Boolean isReimpCapitalDevido) {
        this.isReimpCapitalDevido = isReimpCapitalDevido;
    }

    public void setDadoAutorizacao(String codigo, String valor){
    	dadosAutorizacao.put(codigo, valor);
    }

    public String getDadoAutorizacao(String codigo){
    	return dadosAutorizacao.get(codigo);
    }

    public boolean existsDadoAutorizacao(String codigo){
    	return dadosAutorizacao.containsKey(codigo);
    }

    public Map<String,String> getDadosAutorizacaoMap(){
    	return Collections.unmodifiableMap(dadosAutorizacao);
    }

    public void setDadosAutorizacaoMap(Map<String,String> map){
    	dadosAutorizacao = map;
    }

	public File getAnexo() {
		return anexo;
	}

	public void setAnexo(File anexo) {
		this.anexo = anexo;
	}

	public boolean isIniciarLeilaoReverso() {
		return iniciarLeilaoReverso;
	}

	public void setIniciarLeilaoReverso(boolean iniciarLeilaoReverso) {
		this.iniciarLeilaoReverso = iniciarLeilaoReverso;
	}

	public boolean isSimulacaoPorAdeVlr() {
        return simulacaoPorAdeVlr;
    }

    public void setSimulacaoPorAdeVlr(boolean simulacaoPorAdeVlr) {
        this.simulacaoPorAdeVlr = simulacaoPorAdeVlr;
    }

    public boolean isDestinoAprovacaoLeilaoReverso() {
	    return destinoAprovacaoLeilaoReverso;
	}

	public void setDestinoAprovacaoLeilaoReverso(boolean destinoAprovacaoLeilaoReverso) {
        this.destinoAprovacaoLeilaoReverso = destinoAprovacaoLeilaoReverso;
    }

    public String getCbeCodigo() {
        return cbeCodigo;
    }

    public void setCbeCodigo(String cbeCodigo) {
        this.cbeCodigo = cbeCodigo;
    }

    public String getTlaCodigo() {
        return tlaCodigo;
    }

    public void setTlaCodigo(String tlaCodigo) {
        this.tlaCodigo = tlaCodigo;
    }

	public String getSvcCodigoOrigem() {
		return svcCodigoOrigem;
	}

	public void setSvcCodigoOrigem(String svcCodigoOrigem) {
		this.svcCodigoOrigem = svcCodigoOrigem;
	}

	public String getExigenciaConfirmacaoLeitura() {
		return exigenciaConfirmacaoLeitura;
	}

	public void setExigenciaConfirmacaoLeitura(String exigenciaConfirmacaoLeitura) {
		this.exigenciaConfirmacaoLeitura = exigenciaConfirmacaoLeitura;
	}

	public boolean isTelaConfirmacaoDuplicidade() {
		return telaConfirmacaoDuplicidade;
	}

	public void setTelaConfirmacaoDuplicidade(boolean telaConfirmacaoDuplicidade) {
		this.telaConfirmacaoDuplicidade = telaConfirmacaoDuplicidade;
	}

	public boolean isChkConfirmarDuplicidade() {
		return chkConfirmarDuplicidade;
	}

	public void setChkConfirmarDuplicidade(boolean chkConfirmarDuplicidade) {
		this.chkConfirmarDuplicidade = chkConfirmarDuplicidade;
	}

	public String getMotivoOperacaoCodigoDuplicidade() {
		return motivoOperacaoCodigoDuplicidade;
	}

	public void setMotivoOperacaoCodigoDuplicidade(String motivoOperacaoCodigoDuplicidade) {
		this.motivoOperacaoCodigoDuplicidade = motivoOperacaoCodigoDuplicidade;
	}

	public String getMotivoOperacaoObsDuplicidade() {
		return motivoOperacaoObsDuplicidade;
	}

	public void setMotivoOperacaoObsDuplicidade(String motivoOperacaoObsDuplicidade) {
		this.motivoOperacaoObsDuplicidade = motivoOperacaoObsDuplicidade;
	}

	public String getDtjCodigo() {
		return dtjCodigo;
	}

	public void setDtjCodigo(String dtjCodigo) {
		this.dtjCodigo = dtjCodigo;
	}


    public String getTjuCodigo() {
        return tjuCodigo;
    }

    public void setTjuCodigo(String tjuCodigo) {
        this.tjuCodigo = tjuCodigo;
    }

    public String getDjuNumProcesso() {
        return djuNumProcesso;
    }

    public void setDjuNumProcesso(String djuNumProcesso) {
        this.djuNumProcesso = djuNumProcesso;
    }

    public Date getDjuData() {
        return djuData;
    }

    public void setDjuData(Date djuData) {
        this.djuData = djuData;
    }

    public String getDjuTexto() {
        return djuTexto;
    }

    public void setDjuTexto(String djuTexto) {
        this.djuTexto = djuTexto;
    }

    public Boolean isRetencaoVerbaRescisoria() {
        return isRetencaoVerbaRescisoria;
    }

    public void setIsRetencaoVerbaRescisoria(Boolean isRetencaoVerbaRescisoria) {
        this.isRetencaoVerbaRescisoria = isRetencaoVerbaRescisoria;
    }

    public Boolean getAceitoTermoUsoColetaDados() {
        return aceitoTermoUsoColetaDados;
    }

    public void setAceitoTermoUsoColetaDados(Boolean aceitoTermoUsoColetaDados) {
        this.aceitoTermoUsoColetaDados = aceitoTermoUsoColetaDados;
    }

    public boolean isForcaPeriodoLancamentoCartao() {
        return forcaPeriodoLancamentoCartao;
    }

    public void setForcaPeriodoLancamentoCartao(boolean forcaPeriodoLancamentoCartao) {
        this.forcaPeriodoLancamentoCartao = forcaPeriodoLancamentoCartao;
    }

    public boolean isInclusaoJucicial() {
        return inclusaoJucicial;
    }

    public void setInclusaoJucicial(boolean inclusaoJucicial) {
        this.inclusaoJucicial = inclusaoJucicial;
    }
    
    public boolean isMobileEconsig() {
        return mobileEconsig;
    }

    public void setMobileEconsig(boolean mobileEconsig) {
        this.mobileEconsig = mobileEconsig;
    }
}
