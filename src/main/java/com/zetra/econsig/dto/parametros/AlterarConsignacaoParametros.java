package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.AutorizacaoControllerException;

/**
 * <p>Title: AlterarConsignacaoParametos</p>
 * <p>Description: Parâmetros necessários na alteração de consignação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlterarConsignacaoParametros extends Parametros {

    public final static boolean PADRAO_ALTERA_MARGEM = true;
    public final static boolean PADRAO_EXIGE_SENHA = true;
    public final static boolean PADRAO_VALIDA_MARGEM = true;
    public final static boolean PADRAO_VALIDA_TAXA_JUROS = true;
    public final static boolean PADRAO_VALIDA_SENHA_SERVIDOR = true;
    public final static boolean PADRAO_VALIDA_ADE_IDENTIFICADOR = true;
    public final static boolean PADRAO_VALIDA_RESERVA_CARTAO = true;
    public final static boolean PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE = false;
    public final static boolean PADRAO_CRIAR_NOVO_CONTRATO_DIF = false;
    public final static boolean PADRAO_CALCULAR_PRAZO_DIF_VALOR = false;
    public final static boolean PADRAO_MANTER_DIF_VALOR_MARGEM = false;
    public final static boolean PADRAO_INCLUI_OCORRENCIA = true;
    public final static boolean PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS = false;
    public final static boolean PADRAO_PERMITE_PRZ_NAO_CADASTRADO = false;
    public final static boolean PADRAO_VALIDA_LIMITE_ADE = true;
    public final static boolean PADRAO_LIQUIDA_RELACIONAMENTO_JUDICIAL = false;

    private final String adeCodigo;
    private final BigDecimal adeVlr;
    private final Integer adePrazo;
    private final String adeIdentificador;
    private final boolean validar;
    private final String adeIndice;
    private final BigDecimal adeVlrTac;
    private final BigDecimal adeVlrIof;
    private final BigDecimal adeVlrLiquido;
    private final BigDecimal adeVlrMensVinc;
    private final boolean renegociacao = false;
    private final BigDecimal adeTaxaJuros;
    private final java.util.Date anoMesFim;
    private final Map<String, Object> parametros;
    private final BigDecimal adeVlrSegPrestamista;
    private boolean serAtivo = true;
    private boolean validaBloqueado = false;
    private boolean cnvAtivo = true;
    private boolean svcAtivo = true;
    private boolean csaAtivo = true;
    private boolean orgAtivo = true;
    private boolean estAtivo = true;
    private boolean cseAtivo = true;
    private final Integer adeCarencia;
    private final String serLogin;
    private final String serSenha;
    private boolean atualizacaoCompra = false;
    private boolean atualizacaoReajuste = false;
    private String ocaPeriodo;
    private String adePeriodicidade;
    private File anexo;
    private String[] nomeAnexos;
    private String[] visibilidadeAnexos;
    private String dirAnexos;
    private String nomeAnexo;
    private String aadDescricao;
    private String idAnexo;

    // Atributos para alteração avançada de contrato
    private boolean alteracaoAvancada = false;
    private boolean alteraMargem = PADRAO_ALTERA_MARGEM;
    private boolean exigeSenha = PADRAO_EXIGE_SENHA;
    private boolean validaMargem = PADRAO_VALIDA_MARGEM;
    private boolean validaTaxaJuros = PADRAO_VALIDA_TAXA_JUROS;
    private boolean validaSenhaServidor = PADRAO_VALIDA_SENHA_SERVIDOR;
    private boolean validaAdeIdentificador = PADRAO_VALIDA_ADE_IDENTIFICADOR;
    private boolean validaReservaCartao = PADRAO_VALIDA_RESERVA_CARTAO;
    private boolean alterarValorPrazoSemLimite = PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE;
    private boolean criarNovoContratoDif = PADRAO_CRIAR_NOVO_CONTRATO_DIF;
    private boolean calcularPrazoDifValor = PADRAO_CALCULAR_PRAZO_DIF_VALOR;
    private boolean manterDifValorMargem = PADRAO_MANTER_DIF_VALOR_MARGEM;
    private String novaSituacaoContrato = "";
    private boolean incluiOcorrencia = PADRAO_INCLUI_OCORRENCIA;
    private boolean permiteAltEntidadesBloqueadas = PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS;
    private Short adeIntFolha = null;
    private Short adeIncideMargem = null;
    private String tmoCodigo = null;
    private String ocaObs = null;
    private boolean permitePrzNaoCadastrado = PADRAO_PERMITE_PRZ_NAO_CADASTRADO;
    private boolean validaLimiteAde = PADRAO_VALIDA_LIMITE_ADE;
    private boolean liquidaRelacionamentoJudicial = PADRAO_LIQUIDA_RELACIONAMENTO_JUDICIAL;
    private String tdaModalidadeOperacao = null;
    private String tdaMatriculaSerCsa = null;

    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String cidCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;
    private Date djuDataRevogacao;
    private String djuCodigo;

    private boolean alteracaoForcaPeriodo = false;

    // Dados da tb_dados_autorizacao_desconto
    private Map<String,String> dadosAutorizacao = new HashMap<>();

    public AlterarConsignacaoParametros(String adeCodigo, BigDecimal adeVlr, Integer adePrazo, String adeIdentificador,
                                       boolean validar, String adeIndice, BigDecimal adeVlrTac, BigDecimal adeVlrIof,
                                       BigDecimal adeVlrLiquido, BigDecimal adeVlrMensVinc,
                                       BigDecimal adeTaxaJuros, Date anoMesFim, Map<String, Object> parametros, BigDecimal adeVlrSegPrestamista,
                                       boolean serAtivo, boolean validaBloqueado, boolean cnvAtivo, boolean svcAtivo, boolean csaAtivo,
                                       boolean orgAtivo, boolean estAtivo, boolean cseAtivo, Integer adeCarencia, String serLogin, String serSenha,
                                       boolean validaSenhaServidor) {

        requiredFieldsMap = new HashMap<>();
        requiredFieldsMap.put(ParametrosFieldNames.ADE_CODIGO, adeCodigo);

        this.adeCodigo = adeCodigo;
        this.adeVlr = adeVlr;
        this.adePrazo = adePrazo;
        this.adeIdentificador = adeIdentificador;
        this.validar = validar;
        this.adeIndice = adeIndice;
        this.adeVlrTac = adeVlrTac;
        this.adeVlrIof = adeVlrIof;
        this.adeVlrLiquido = adeVlrLiquido;
        this.adeVlrMensVinc = adeVlrMensVinc;
        this.adeTaxaJuros = adeTaxaJuros;
        this.anoMesFim = anoMesFim;
        this.parametros = parametros;
        this.adeVlrSegPrestamista = adeVlrSegPrestamista;
        this.serAtivo = serAtivo;
        this.validaBloqueado = validaBloqueado;
        this.cnvAtivo = cnvAtivo;
        this.svcAtivo = svcAtivo;
        this.csaAtivo = csaAtivo;
        this.orgAtivo = orgAtivo;
        this.estAtivo = estAtivo;
        this.cseAtivo = cseAtivo;
        this.adeCarencia = adeCarencia;
        this.serLogin = serLogin;
        this.serSenha = serSenha;
        this.validaSenhaServidor = validaSenhaServidor;
    }

    public AlterarConsignacaoParametros(String adeCodigo, BigDecimal adeVlr, Integer adePrazo,
                                       String adeIdentificador, String adeIndice,
                                       BigDecimal adeVlrTac, BigDecimal adeVlrIof, BigDecimal adeVlrLiquido,
                                       BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, BigDecimal adeVlrSegPrestamista,
                                       boolean serAtivo, boolean validaBloqueado, boolean cnvAtivo, boolean svcAtivo, boolean csaAtivo,
                                       boolean orgAtivo, boolean estAtivo, boolean cseAtivo, Integer adeCarencia, String serLogin, String serSenha) {

        this (adeCodigo, adeVlr, adePrazo, adeIdentificador, false, adeIndice, adeVlrTac, adeVlrIof,
              adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, null, null,
              adeVlrSegPrestamista, serAtivo, validaBloqueado, cnvAtivo, svcAtivo,
              csaAtivo, orgAtivo, estAtivo, cseAtivo, adeCarencia, serLogin, serSenha, true);
    }

    public AlterarConsignacaoParametros(String adeCodigo, BigDecimal adeVlr, Integer adePrazo,
            String adeIdentificador, String adeIndice,
            BigDecimal adeVlrTac, BigDecimal adeVlrIof, BigDecimal adeVlrLiquido,
            BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, BigDecimal adeVlrSegPrestamista, Integer adeCarencia, String serLogin, String serSenha) {

        this (adeCodigo, adeVlr, adePrazo, adeIdentificador, false, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc,
              adeTaxaJuros, null, null, adeVlrSegPrestamista, true, false, true, true, true, true, true, true, adeCarencia, serLogin, serSenha, true);
    }

    public AlterarConsignacaoParametros(String adeCodigo, BigDecimal adeVlr, Integer adePrazo, String adeIdentificador,
                                       boolean validar, String adeIndice, BigDecimal adeVlrTac,
                                       BigDecimal adeVlrIof, BigDecimal adeVlrLiquido,
                                       BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, java.util.Date adeAnoMesFim,
                                       Map<String, Object> parametros) throws AutorizacaoControllerException {

        this (adeCodigo, adeVlr, adePrazo, adeIdentificador, validar, adeIndice, adeVlrTac, adeVlrIof,
              adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeAnoMesFim, parametros, null,
              true, false, true, true, true, true, true, true, null, null, null, false);

    }

    public AlterarConsignacaoParametros(String adeCodigo, BigDecimal adeVlr, Integer adePrazo, String adeIdentificador,
                                        boolean validar, String adeIndice, BigDecimal adeVlrTac, BigDecimal adeVlrIof,
                                        BigDecimal adeVlrLiquido, BigDecimal adeVlrMensVinc,
                                        BigDecimal adeTaxaJuros, Date anoMesFim, Map<String, Object> parametros, BigDecimal adeVlrSegPrestamista,
                                        boolean serAtivo, boolean validaBloqueado, boolean cnvAtivo, boolean svcAtivo, boolean csaAtivo,
                                        boolean orgAtivo, boolean estAtivo, boolean cseAtivo, Integer adeCarencia, String serLogin, String serSenha,
                                        boolean validaSenhaServidor, AlterarConsignacaoParametros alterarParametros) {

        this (adeCodigo, adeVlr, adePrazo, adeIdentificador, validar, adeIndice, adeVlrTac, adeVlrIof,
                adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, anoMesFim, parametros,
                adeVlrSegPrestamista, serAtivo, validaBloqueado, cnvAtivo, svcAtivo,
                csaAtivo, orgAtivo, estAtivo, cseAtivo, adeCarencia, serLogin, serSenha, validaSenhaServidor);

        this.aadDescricao = alterarParametros.getAadDescricao();
        this.adeIntFolha = alterarParametros.getAdeIntFolha();
        this.adeIncideMargem = alterarParametros.getAdeIncideMargem();
        this.adePeriodicidade = alterarParametros.getAdePeriodicidade();
        this.visibilidadeAnexos = alterarParametros.getVisibilidadeAnexos();
        this.anexo = alterarParametros.getAnexo();
        this.nomeAnexos = alterarParametros.getNomeAnexos();
        this.nomeAnexo = alterarParametros.getNomeAnexo();
        this.idAnexo = alterarParametros.getIdAnexo();
        this.dirAnexos = alterarParametros.getDirAnexos();
        this.cidCodigo = alterarParametros.getCidCodigo();
        this.djuData = alterarParametros.getDjuData();
        this.djuNumProcesso = alterarParametros.getDjuNumProcesso();
        this.djuTexto = alterarParametros.getDjuTexto();
        this.tjuCodigo = alterarParametros.getTjuCodigo();
        this.tmoCodigo = alterarParametros.getTmoCodigo();
        this.ocaObs = alterarParametros.getOcaObs();
        this.ocaPeriodo = alterarParametros.getOcaPeriodo();
        this.tdaModalidadeOperacao = alterarParametros.getTdaModalidadeOperacao();
        this.novaSituacaoContrato = alterarParametros.getNovaSituacaoContrato();
        this.tdaMatriculaSerCsa = alterarParametros.getTdaMatriculaSerCsa();
        this.dadosAutorizacao = alterarParametros.getDadosAutorizacaoMap();
        this.djuDataRevogacao = alterarParametros.getDjuDataRevogacao();
        this.djuCodigo = alterarParametros.getDjuCodigo();
        this.exigeSenha = alterarParametros.isExigeSenha();
        this.permitePrzNaoCadastrado = alterarParametros.isPermitePrzNaoCadastrado();
        this.validaReservaCartao = alterarParametros.isValidaReservaCartao();
        this.validaSenhaServidor = alterarParametros.isValidaSenhaServidor();
        this.validaAdeIdentificador = alterarParametros.isValidaAdeIdentificador();
        this.validaMargem = alterarParametros.isValidaMargem();
        this.validaTaxaJuros = alterarParametros.isValidaTaxaJuros();
        this.liquidaRelacionamentoJudicial = alterarParametros.isLiquidaRelacionamentoJudicial();
        this.incluiOcorrencia = alterarParametros.isIncluiOcorrencia();
        this.manterDifValorMargem = alterarParametros.isManterDifValorMargem();
        this.criarNovoContratoDif = alterarParametros.isCriarNovoContratoDif();
        this.alterarValorPrazoSemLimite = alterarParametros.isAlterarValorPrazoSemLimite();
        this.permiteAltEntidadesBloqueadas = alterarParametros.isPermiteAltEntidadesBloqueadas();
        this.alteracaoAvancada = alterarParametros.isAlteracaoAvancada();
        this.calcularPrazoDifValor = alterarParametros.isCalcularPrazoDifValor();
        this.alteraMargem = alterarParametros.isAlteraMargem();
        this.validaLimiteAde = alterarParametros.isValidaLimiteAde();
        this.atualizacaoReajuste = alterarParametros.isAtualizacaoReajuste();
        this.atualizacaoCompra = alterarParametros.isAtualizacaoCompra();
    }


    public String getAdeCodigo() {
        return adeCodigo;
    }

    public BigDecimal getAdeVlr() {
        return adeVlr;
    }

    public Integer getAdePrazo() {
        return adePrazo;
    }

    public String getAdeIdentificador() {
        return adeIdentificador;
    }

    public boolean isValidar() {
        return validar;
    }

    public String getAdeIndice() {
        return adeIndice;
    }

    public BigDecimal getAdeVlrTac() {
        return adeVlrTac;
    }

    public BigDecimal getAdeVlrIof() {
        return adeVlrIof;
    }

    public BigDecimal getAdeVlrLiquido() {
        return adeVlrLiquido;
    }

    public BigDecimal getAdeVlrMensVinc() {
        return adeVlrMensVinc;
    }

    public boolean isRenegociacao() {
        return renegociacao;
    }

    public BigDecimal getAdeTaxaJuros() {
        return adeTaxaJuros;
    }

    public java.util.Date getAnoMesFim() {
        return anoMesFim;
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public BigDecimal getAdeVlrSegPrestamista() {
        return adeVlrSegPrestamista;
    }

    public boolean isSerAtivo() {
        return serAtivo;
    }

    public boolean isValidaBloqueado() {
        return validaBloqueado;
    }

    public boolean isCnvAtivo() {
        return cnvAtivo;
    }

    public boolean isSvcAtivo() {
        return svcAtivo;
    }

    public boolean isCsaAtivo() {
        return csaAtivo;
    }

    public boolean isOrgAtivo() {
        return orgAtivo;
    }

    public boolean isEstAtivo() {
        return estAtivo;
    }

    public boolean isCseAtivo() {
        return cseAtivo;
    }

    public Integer getAdeCarencia() {
        return adeCarencia;
    }

    public boolean isAlteraMargem() {
        return alteraMargem;
    }

    public void setAlteraMargem(boolean alteraMargem) {
        this.alteraMargem = alteraMargem;
    }

    public boolean isValidaMargem() {
        return validaMargem;
    }

    public void setValidaMargem(boolean validaMargem) {
        this.validaMargem = validaMargem;
    }

    public boolean isValidaTaxaJuros() {
        return validaTaxaJuros;
    }

    public void setValidaTaxaJuros(boolean validaTaxaJuros) {
        this.validaTaxaJuros = validaTaxaJuros;
    }

    public boolean isAlterarValorPrazoSemLimite() {
        return alterarValorPrazoSemLimite;
    }

    public void setAlterarValorPrazoSemLimite(boolean alterarValorPrazoSemLimite) {
        this.alterarValorPrazoSemLimite = alterarValorPrazoSemLimite;
    }

    public boolean isCriarNovoContratoDif() {
        return criarNovoContratoDif;
    }

    public void setCriarNovoContratoDif(boolean criarNovoContratoDif) {
        this.criarNovoContratoDif = criarNovoContratoDif;
    }

    public boolean isCalcularPrazoDifValor() {
        return calcularPrazoDifValor;
    }

    public void setCalcularPrazoDifValor(boolean calcularPrazoDifValor) {
        this.calcularPrazoDifValor = calcularPrazoDifValor;
    }

    public boolean isManterDifValorMargem() {
        return manterDifValorMargem;
    }

    public void setManterDifValorMargem(boolean manterDifValorMargem) {
        this.manterDifValorMargem = manterDifValorMargem;
    }

    public String getNovaSituacaoContrato() {
        return novaSituacaoContrato;
    }

    public void setNovaSituacaoContrato(String novaSituacaoContrato) {
        this.novaSituacaoContrato = novaSituacaoContrato;
    }

    public boolean isIncluiOcorrencia() {
        return incluiOcorrencia;
    }

    public void setIncluiOcorrencia(boolean incluiOcorrencia) {
        this.incluiOcorrencia = incluiOcorrencia;
    }

    public Short getAdeIntFolha() {
        return adeIntFolha;
    }

    public void setAdeIntFolha(Short adeIntFolha) {
        this.adeIntFolha = adeIntFolha;
    }

    public Short getAdeIncideMargem() {
        return adeIncideMargem;
    }

    public void setAdeIncideMargem(Short adeIncideMargem) {
        this.adeIncideMargem = adeIncideMargem;
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

    public boolean isValidaSenhaServidor() {
        return validaSenhaServidor;
    }

    public void setValidaSenhaServidor(boolean validaSenhaServidor) {
        this.validaSenhaServidor = validaSenhaServidor;
    }

    public boolean isValidaAdeIdentificador() {
        return validaAdeIdentificador;
    }

    public void setValidaAdeIdentificador(boolean validaAdeIdentificador) {
        this.validaAdeIdentificador = validaAdeIdentificador;
    }

    public boolean isValidaReservaCartao() {
        return validaReservaCartao;
    }

    public void setValidaReservaCartao(boolean validaReservaCartao) {
        this.validaReservaCartao = validaReservaCartao;
    }

    public boolean isExigeSenha() {
        return exigeSenha;
    }

    public void setExigeSenha(boolean exigeSenha) {
        this.exigeSenha = exigeSenha;
    }

    public boolean isPermiteAltEntidadesBloqueadas() {
        return permiteAltEntidadesBloqueadas;
    }

    public void setPermiteAltEntidadesBloqueadas(boolean permiteAltEntidadesBloqueadas) {
        this.permiteAltEntidadesBloqueadas = permiteAltEntidadesBloqueadas;
    }

    public String getSerLogin() {
        return serLogin;
    }

    public String getSerSenha() {
        return serSenha;
    }

    public boolean isPermitePrzNaoCadastrado() {
        return permitePrzNaoCadastrado;
    }

    public void setPermitePrzNaoCadastrado(boolean permitePrzNaoCadastrado) {
        this.permitePrzNaoCadastrado = permitePrzNaoCadastrado;
    }

    public boolean isAtualizacaoCompra() {
        return atualizacaoCompra;
    }

    public void setAtualizacaoCompra(boolean atualizacaoCompra) {
        this.atualizacaoCompra = atualizacaoCompra;
    }

    public boolean isAtualizacaoReajuste() {
        return atualizacaoReajuste;
    }

    public void setAtualizacaoReajuste(boolean atualizacaoReajuste) {
        this.atualizacaoReajuste = atualizacaoReajuste;
    }

    public boolean isAlteracaoAvancada() {
        return alteracaoAvancada;
    }

    public void setAlteracaoAvancada(boolean alteracaoAvancada) {
        this.alteracaoAvancada = alteracaoAvancada;
    }

    public boolean isValidaLimiteAde() {
        return validaLimiteAde;
    }

    public void setValidaLimiteAde(boolean validaLimiteAde) {
        this.validaLimiteAde = validaLimiteAde;
    }

    public boolean isLiquidaRelacionamentoJudicial() {
        return liquidaRelacionamentoJudicial;
    }

    public void setLiquidaRelacionamentoJudicial(boolean liquidaRelacionamentoJudicial) {
        this.liquidaRelacionamentoJudicial = liquidaRelacionamentoJudicial;
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

    public String getOcaPeriodo() {
        return ocaPeriodo;
    }

    public void setOcaPeriodo(String ocaPeriodo) {
        this.ocaPeriodo = ocaPeriodo;
    }

    public String getAdePeriodicidade() {
        return adePeriodicidade;
    }

    public void setAdePeriodicidade(String adePeriodicidade) {
        this.adePeriodicidade = adePeriodicidade;
    }

    public File getAnexo() {
        return anexo;
    }

    public void setAnexo(File anexo) {
        this.anexo = anexo;
    }

    public String[] getNomeAnexos() {
        return nomeAnexos;
    }

    public void setNomeAnexos(String[] nomeAnexos) {
        this.nomeAnexos = nomeAnexos;
    }

    public String getDirAnexos() {
        return dirAnexos;
    }

    public void setDirAnexos(String dirAnexos) {
        this.dirAnexos = dirAnexos;
    }

    public String[] getVisibilidadeAnexos() {
        return visibilidadeAnexos;
    }

    public void setVisibilidadeAnexos(String[] visibilidadeAnexos) {
        this.visibilidadeAnexos = visibilidadeAnexos;
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

    public String getTjuCodigo() {
        return tjuCodigo;
    }

    public void setTjuCodigo(String tjuCodigo) {
        this.tjuCodigo = tjuCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
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

    public Date getDjuDataRevogacao() {
        return djuDataRevogacao;
    }

    public void setDjuDataRevogacao(Date djuDataRevogacao) {
        this.djuDataRevogacao = djuDataRevogacao;
    }

    public String getDjuCodigo() {
        return djuCodigo;
    }

    public void setDjuCodigo(String djuCodigo) {
        this.djuCodigo = djuCodigo;
    }


    public String getNomeAnexo() {
		return nomeAnexo;
	}

	public void setNomeAnexo(String nomeAnexo) {
		this.nomeAnexo = nomeAnexo;
	}

	public String getAadDescricao() {
		return aadDescricao;
	}

	public void setAadDescricao(String aadDescricao) {
		this.aadDescricao = aadDescricao;
	}

	public String getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(String idAnexo) {
		this.idAnexo = idAnexo;
	}

    public boolean isAlteracaoForcaPeriodo() {
        return alteracaoForcaPeriodo;
    }

    public void setAlteracaoForcaPeriodo(boolean alteracaoViaLote) {
        this.alteracaoForcaPeriodo = alteracaoViaLote;
    }

}
