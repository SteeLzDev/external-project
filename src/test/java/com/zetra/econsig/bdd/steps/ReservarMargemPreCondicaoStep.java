package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.BloqueioRseFun;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.CalendarioFolhaCseService;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.EstabelecimentoService;
import com.zetra.econsig.service.FuncaoSistemaService;
import com.zetra.econsig.service.MargemService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.OrgaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PrazoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoServicoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.ServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.service.VerbaConvenioService;
import com.zetra.econsig.service.VinculoRegistroService;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReservarMargemPreCondicaoStep {

    private final LoginInfo loginCsa = LoginValues.csa2;
    private final LoginInfo loginCor = LoginValues.cor1;
    private final LoginInfo loginSer = LoginValues.servidor1;

    private String svcCodigoEmprestimo;
    private String svcCodigoCartao;
    private String svcCodigoMensalidade;

    private String csaCodigo;
    private String corCodigo;
    private String serCodigo;

    private Usuario usuarioCsa;
    private Usuario usuarioCor;

    private Orgao orgao;
    private RegistroServidor registroServidor;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @Autowired
    private FuncaoSistemaService funcaoSistemaService;

    @Autowired
    private MargemService margemService;

    @Autowired
    private AutDescontoService autDescontoService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private ConvenioService convenioService;

    @Autowired
    private VerbaConvenioService verbaConvenioService;

    @Autowired
    private ServidorService servidorService;

    @Autowired
    private RegistroServidorService registroServidorService;

    @Autowired
    private VinculoRegistroService vinculoRegistroService;

    @Autowired
    private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

    @Autowired
    private ConsignatariaService consignatariaService;

    @Autowired
    private CalendarioFolhaCseService calendarioFolhaCseService;

    @Autowired
    private EstabelecimentoService estabelecimentoService;

    @Autowired
    private OrgaoService orgaoService;

    @Autowired
    private PrazoService prazoService;

    @Autowired
    private RelacionamentoServicoService relacionamentoServicoService;

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ReservarMargemPage reservarMargemPage;

    @Before
    public void setUp() throws Exception {
        loginPage = new LoginPage(getWebDriver());
        menuPage = new MenuPage(getWebDriver());
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
        setConfig();
    }

    private void setConfig() throws InterruptedException {
        csaCodigo = usuarioService.getCsaCodigo(loginCsa.getLogin());
        corCodigo = usuarioService.getCorCodigo(loginCor.getLogin());
        serCodigo = usuarioService.getSerCodigo(loginSer.getLogin());
        svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
        svcCodigoCartao = servicoService.retornaSvcCodigo("020");
        svcCodigoMensalidade = servicoService.retornaSvcCodigo("008");
        usuarioCsa = usuarioService.getUsuario(loginCsa.getLogin());
        usuarioCor = usuarioService.getUsuario(loginCor.getLogin());
        registroServidor = registroServidorService.getRegistroServidor(serCodigo);
        orgao = orgaoService.obterOrgaoPorIdentificador("213464140");
    }

    @Dado("Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional")
    public void sistema_permite_CPF_opcional_na_pesquisaServidor_servico_emprestimo() throws Throwable {
        log.info("Dado Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO, "1");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG, "N");
        EConsigInitializer.limparCache();
    }

    @Dado("Que o sistema permite senhaAutorizacaoServidor opcional")
    public void sistema_permite_senhaAutorizacaoServidor_opcional_cartao() throws Throwable {
        log.info("Dado Que o sistema permite senhaAutorizacaoServidor opcional");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoCartao, CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
    }

    @Dado("Sistema permite CPF opcional reserva mensalidade")
    public void sistema_permite_CPF_opcional_na_pesquisaServidor_servico_mensalidade() throws Throwable {
        log.info("Dado Sistema permite CPF opcional reserva mensalidade");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
    }

    @Dado("servidor com margem suficiente")
    public void verificarMargemSuficiente() throws Throwable {
        log.info("Dado servidor com margem suficiente");

        // verificar se possui margem
        assertTrue(registroServidor.getRseMargem().intValue() > 0);
    }

    @Dado("servidor com margem de cartao suficiente")
    public void verificarMargemCartaoSuficiente() throws Throwable {
        log.info("Dado servidor com margem de cartão suficiente");

        // verificar se possui margem
        assertTrue(registroServidor.getRseMargem3().intValue() > 0);
    }

    @E("Usuario com margem de credito para cartao insuficiente {string}")
    public void usuCSACredInsuf(String valorParcela) throws Throwable {
        log.info("Dado Usuario com margem de crédito para cartão insuficiente {}", valorParcela);

        // verificar se possui margem menor que a solicitada
        assertTrue(registroServidor.getRseMargem3().intValue() < Integer.parseInt(valorParcela));
    }

    @E("servidor com margem insuficiente {string}")
    public void verificarMargemInsuficiente(String valorParcela) throws Throwable {
        log.info("Dado servidor com margem insuficiente {}", valorParcela);

        // verificar se possui margem menor que a solicitada
        assertTrue(registroServidor.getRseMargem().intValue() < Integer.parseInt(valorParcela));
    }

    @E("Servidor com contratos ativos para o servico")
    public void servidorComContratosAtivosParaServico() throws Throwable {
        log.info("Dado Servidor com contratos ativos para o servico");

        final String rseCodigo = registroServidor.getRseCodigo();

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);

        final List<AutDesconto> autDescontos = autDescontoService.getAdes(rseCodigo, sadCodigos);

        if (autDescontos.isEmpty()) {
            reservarMargemUsandoSteps();
        }
    }

    @E("Parametro de Sistema com valores zerados para Limite Ades")
    public void resetParamSistLimitAdes() throws Throwable {
        log.info("Dado Parametro de Sistema com valores zerados para Limite Ades");

        resetParametroSistLimiteAdes();
        resetParamSvcLimitesAde();
    }

    @E("Reserva verificando limite de margem {string} para CSA")
    public void reservaCheckLimitMargemCsa(String marCodigoLimit) throws Throwable {
        log.info("Dado Reserva verificando limite de margem {} para CSA", marCodigoLimit);

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, marCodigoLimit);
        EConsigInitializer.limparCache();
    }

    @E("Reserva verificacao bloqueio de funcao")
    public void reservarCheckBloqFuncao() throws Throwable {
        log.info("Dado Reserva verificacao bloqueio de funcao");

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_BLOQUEIO_FUNCAO_RSE, CodedValues.TPC_SIM);
        EConsigInitializer.limparCache();
    }

    @E("Servidor possui margem de limite {string} para CSA cadastrado {string}")
    public void serPossuiMarLimitCsa(String marCodigoLimit, String margem) throws Throwable {
        log.info("Dado Servidor possui margem de limite {} para CSA cadastrado {}", marCodigoLimit, margem);

        final String rseCodigo = registroServidor.getRseCodigo();
        margemService.incluirMargem(marCodigoLimit, CodedValues.TIPO_VLR_FIXO);
        margemService.incluirMargemRegistroServidor(marCodigoLimit, rseCodigo, new BigDecimal(margem), BigDecimal.ZERO);
    }

    @E("Servidor com Ade no periodo de restricao de novos ades")
    public void serComAdeNoPeriodoRestricaoSvc() throws Throwable {
        log.info("Dado Servidor com Ade no periodo de restricao de novos ades");

        resetParamSvcLimitesAde();
        final String rseCodigo = registroServidor.getRseCodigo();

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_MARGEM);
        sadCodigos.add(CodedValues.SAD_DEFERIDA);

        List<AutDesconto> autDescontos = autDescontoService.getAdes(rseCodigo, sadCodigos);

        if (autDescontos.isEmpty()) {
            reservarMargemUsandoSteps();
            autDescontos = autDescontoService.getAdes(rseCodigo, sadCodigos);
        }

        autDescontos.get(0).setAdeData(new Timestamp(DateHelper.addDays(DateHelper.getSystemDate(), -1).getTime()));

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE, "2");
    }

    @E("UsuCsa com Convenio para Servico Mensalidade")
    public void usucsa_com_Convenio_para_Servico_Mensalidade() throws Throwable {
        log.info("Dado UsuCsa com Convenio para Servico Mensalidade");

        assertTrue(convenioService.getConvenios(CodedValues.NSE_MENSALIDADE.toString(), csaCodigo).size() > 0);
    }

    @E("UsuCsa com Convenio para Servico Cartao")
    public void usucsa_com_Convenio_para_Servico_Cartao() throws Throwable {
        log.info("Dado UsuCsa com Convenio para Servico Cartao");

        assertTrue(convenioService.getConvenios(CodedValues.NSE_CARTAO.toString(), csaCodigo).size() > 0);
    }

    @E("UsuCor com Convenio para Servico Cartao")
	public void usucor_com_Convenio_para_Servico_Cartao() throws Throwable {
		log.info("Dado UsuCor com Convenio para Servico Cartao");

		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoCartao, orgao.getOrgCodigo(), csaCodigo);

		convenioService.alterarScvCodigoConvenio("020", csaCodigo, "213464140", CodedValues.SCV_ATIVO);
		convenioService.alterarScvCodigoCorrespondenteConvenio(corCodigo, convenio.getCnvCodigo(), CodedValues.SCV_ATIVO);

		assertEquals(CodedValues.SCV_ATIVO, convenioService.getConvenioCorrespondente(usuarioService.getCorCodigo(loginCor.getLogin()), convenio.getCnvCodigo()).getScvCodigo());
	}

    @E("UsuCsa com Convenio para Servico Emprestimo")
    public void usucsa_com_Convenio_para_Servico_Emprestimo() throws Throwable {
        log.info("Dado UsuCsa com Convenio para Servico Emprestimo");

        assertTrue(convenioService.getConvenios(CodedValues.NSE_EMPRESTIMO.toString(), csaCodigo).size() > 0);
    }

    @E("UsuCsa possui prazo Maximo")
    public void svcComPrazoFixoEPrazoMaximo() throws Throwable {
        log.info("Dado UsuCsa possui prazo Maximo");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_PRAZO_FIXO, "0");
    }

    @E("Servico com Prazo Maximo definido {string}")
    public void svcComPrazoFixoEPrazoMaximoDefinido(String prazoMax) throws Throwable {
        log.info("Dado Servico com Prazo Maximo definido ", prazoMax);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_MAX_PRAZO, prazoMax);
    }

    @Dado("UsuCsa sem permissao para confirmar reserva")
    public void csaSemPermissaoConfirmarReserva() throws Throwable {
        log.info("Dado UsuCsa sem permissão para confirmar reserva");

        funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
    }

    @Dado("UsuCor sem permissao para confirmar reserva")
    public void corSemPermissaoConfirmarReserva() throws Throwable {
        log.info("Dado UsuCor sem permissão para confirmar reserva");

        funcaoSistemaService.excluirFuncaoCor(CodedValues.FUN_CONF_RESERVA, corCodigo);
    }

    @E("UsuCsa sem permissao para confirmar renegociacao")
    public void csaSemPermissaoConfirmarRenegociacao() {
        log.info("Dado UsuCsa sem permissão para confirmar renegociacao");

        funcaoSistemaService.excluirFuncaoCor(CodedValues.FUN_CONF_RESERVA, usuarioCsa.getUsuCodigo());
    }

    @Dado("UsuCsa com permissao para confirmar reserva e deferimento automatico")
    public void csaComPermissaoConfirmarReserva() throws Throwable {
        log.info("Dado UsuCsa com permissão para confirmar reserva e deferimento automático");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "S");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "S");
    }

    @Dado("UsuCsa com permissão para confirmar reserva")
    public void csaComPermissaoConfirmarReservaNaoDefereAde() throws Throwable {
        log.info("Dado UsuCsa com permissão para confirmar reserva e deferimento automático");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "N");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "N");
    }

    @Dado("UsuCsa com permissão para confirmar solicitacao")
    public void csaComPermissaoConfirmarSolicitacaoNaoDefereAde() throws Throwable {
        log.info("UsuCsa com permissão para confirmar solicitacao");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_SOLICITACAO, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "N");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "N");
    }

    @Dado("UsuCsa com permissao para confirmar reserva e deferimento automatico de cartao")
    public void csaComPermissaoConfirmarReservaCartao() throws Throwable {
        log.info("Dado UsuCsa com permissão para confirmar reserva e deferimento automático de cartão");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "S");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoCartao, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "S");
    }

    @Dado("UsuCor com permissao para confirmar reserva e deferimento automatico de cartao")
    public void corComPermissaoConfirmarReserva() throws Throwable {
        log.info("Dado UsuCor com permissão para confirmar reserva e deferimento automático de cartão");

        funcaoSistemaService.incluirFuncaoCor(CodedValues.FUN_CONF_RESERVA, corCodigo, usuarioCor.getUsuCodigo());
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoCartao, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "S");
    }

    @Dado("UsuCor com permissao para confirmar reserva e deferimento manual com cartao")
    public void corPermissaoDeferimentoManual() throws Throwable {
        log.info("Dado UsuCor com permissão para confirmar reserva e deferimento manual com cartão");

        funcaoSistemaService.incluirFuncaoCor(CodedValues.FUN_CONF_RESERVA, corCodigo, usuarioCor.getUsuCodigo());
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoCartao, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "N");
    }

    @Dado("UsuCsa com permissao para confirmar reserva e deferimento manual")
    public void requererDeferimentoManual() throws Throwable {
        log.info("Dado UsuCsa com permissão para confirmar reserva e deferimento manual");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "N");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "N");
        usuarioService.alterarUsuExigeCertificado(loginCsa.getLogin(), "");
        EConsigInitializer.limparCache();
    }

    @Dado("UsuCsa com permissao para confirmar reserva e deferimento manual com cartao")
    public void requererDeferimentoManualCartao() throws Throwable {
        log.info("Dado UsuCsa com permissão para confirmar reserva e deferimento manual com cartão");

        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "N");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoCartao, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "N");
    }

    @E("Parametro de Sistema {string} com valor {string}")
    public void setParamSistComVlr(String tpcCodigo, String psiVlr) throws Throwable {
        log.info("Dado Parametro de Sistema {} com valor {}", tpcCodigo, psiVlr);

        parametroSistemaService.configurarParametroSistemaCse(tpcCodigo, (psiVlr.equals("null") ? "" : psiVlr));
    }

    @E("Sistema trabalha com CET ou correcao do valor presente")
    public void sistemaTemCETOuCorrecaoVlrPresente() throws Throwable {
        log.info("Dado Sistema trabalha com CET ou correcao do valor presente");

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM);
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_USA_TAXA_CSA_CORRECAO_VLR_PRESENTE, CodedValues.TPC_SIM);
    }

    @E("Servico Bloqueado por parametro de servico por Rse {string} com valor zero")
    public void svcBloqueadoPorParamSvcRse(String tpsCodigo) throws Throwable {
        log.info("Dado Servico Bloqueado por parâmetro de serviço por Rse {string} com valor zero");

        parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(), svcCodigoEmprestimo, tpsCodigo, "0");
    }

    @E("Servico configurado para exibir campo CET")
    public void servicoConfiguraExibirCampoCET() throws Throwable {
        log.info("Dado Serviço configurado para exibir campo CET");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_VLR_LIQ_TAXA_JUROS, "1");
    }

    @E("Servico configurado para nao exibir campo CET")
    public void servicoConfiguraNaoExibirCampoCET() throws Throwable {
        log.info("Dado Serviço configurado para não exibir campo CET");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_VLR_LIQ_TAXA_JUROS, "0");
    }

    @E("Natureza de Servico Possui limite de ADE acima do numero de ADEs do servidor para este")
    public void nseComLimitADEsMenorNumeroAdesSer() throws Throwable {
        log.info("Dado Natureza de Servico Possui limite de ADE acima do numero de ADEs do servidor para este");

        // anula limite por svc para este teste
        parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(), svcCodigoEmprestimo, CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "");

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(), CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, String.valueOf(numAdesSer + 1));
    }

    @E("Natureza de Servico Possui limite de ADE no limite do numero de ADEs do servidor para este")
    public void nseComLimitADEsIgualNumeroAdesSer() throws Throwable {
        log.info("Dado Natureza de Servico Possui limite de ADE no limite do numero de ADEs do servidor para este");

        parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(), CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "1");
    }

    @E("Parametro Registro Servidor com valores zerados")
    public void resetParamRSE() throws Throwable {
        log.info("Dado Parametro Registro Servidor com valores zerados");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(), CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "");
        parametroSistemaService.excluirParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);

        registroServidorService.alterarVrsCodigo(registroServidor, null);

        funcaoSistemaService.excluirBloqueioRseFuncao(registroServidor.getRseCodigo(), CodedValues.FUN_RES_MARGEM);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_BUSCA_BOLETO_EXTERNO, "0");
    }

    @E("Natureza de Servico bloqueado para novas reservas por parametro de servico {string}")
    public void svcBloqueadoPorParamSvcNse(String tpsCodigo) throws Throwable {
        log.info("Dado Natureza de Servico bloqueado para novas reservas por parametro de servico {}", tpsCodigo);

        parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(), CodedValues.NSE_EMPRESTIMO, tpsCodigo, "0");
    }

    @E("Servico Mensalidade com valor alteravel Monetario")
    public void servico_Mensalidade_com_valor_alteravel_Monetario() throws Throwable {
        log.info("Dado Servico Mensalidade com valor alteravel Monetario");

        registroServidorService.alterarRseBaseCalculo(registroServidor, BigDecimal.ONE);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_ADE_VLR, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_ALTERA_ADE_VLR, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_TIPO_VLR, "F");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "");
    }

    @E("Parametros de Sistema de limites de numero de CSAs nao configurados")
    public void parametros_de_limites_que_nao_de_numero_de_CSAs_nao_configurados() throws Throwable {
        log.info("Dado Parametros de Sistema de limites de numero de CSAs nao configurados");

        resetParametroSistLimiteAdes();

        final List<AutDesconto> adeCount = listaAdesAtivosLimites();
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, String.valueOf(adeCount.size() + 1));
    }

    @E("Servico Mensalidade com valor alteravel Percentual Que Nao Retem")
    public void servico_Mensalidade_com_valor_alteravel_Percentual() throws Throwable {
        log.info("Dado Servico Mensalidade com valor alteravel Percentual Que Nao Retem");

        registroServidorService.alterarRseBaseCalculo(registroServidor, null);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_ADE_VLR, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_ALTERA_ADE_VLR, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_TIPO_VLR, "P");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "0");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "");
    }

    @E("Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Possui Base de Calculo")
    public void servico_Mensalidade_com_valor_alteravel_Percentual_Rerem_Margem_Possui_Base_Calculo() throws Throwable {
        log.info("Dado Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Possui Base de Calculo");

        registroServidorService.alterarRseBaseCalculo(registroServidor, BigDecimal.ONE);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_ADE_VLR, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_ALTERA_ADE_VLR, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_TIPO_VLR, "P");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "");
    }

    @E("Que a base de calculo do parametro condiz com a tabela de base de calculo")
    public void verificaBaseCalculoCondizente() throws Throwable {
        log.info("Dado que a base de calculo do parametro condiz com a tabela de base de calculo");

        final String tbcCodigo = "2";
        registroServidorService.incluirBaseCalcRegistroServidor(registroServidor.getRseCodigo(), tbcCodigo);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL, tbcCodigo);
    }

    @E("Verifica que sua base de calculo do parametro condiz com a base de calculo padrao")
    public void verificaBaseCalculoPadraoCondizente() throws Throwable {
        log.info("Dado Verifica que sua base de calculo do parametro condiz com a base de calculo padrao");

        final String tbcCodigo = CodedValues.TBC_PADRAO;
        registroServidorService.incluirBaseCalcRegistroServidor(registroServidor.getRseCodigo(), tbcCodigo);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL, tbcCodigo);
    }

    @E("Servidor com base de calculo cadastrado")
    public void serBaseCalculoCadastrado() throws Throwable {
        log.info("Dado Servidor com base de calculo cadastrado");

        final String tbcCodigo = "999";
        registroServidorService.incluirBaseCalcRegistroServidor(registroServidor.getRseCodigo(), tbcCodigo);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL, tbcCodigo);
    }

    @E("Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Nao Possui Base de Calculo")
    public void servico_Mensalidade_com_valor_alteravel_Margem_Nao_Possui_Base_Calculo() {
        log.info("Dado Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Nao Possui Base de Calculo");

        registroServidorService.alterarRseBaseCalculo(registroServidor, null);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_TIPO_VLR, "P");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "");
        EConsigInitializer.limparCache();
    }

    @Dado("Servico Mensalidade com valor maximo de capital devido configurado")
    public void servico_Mensalidade_com_valor_maximo_configurado() {
        log.info("Dado Servico Mensalidade com valor máximo de capital devido configurado");

        registroServidorService.alterarRseBaseCalculo(registroServidor, BigDecimal.ONE);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_TIPO_VLR, "P");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "1");
        EConsigInitializer.limparCache();
    }

    @Dado("Que o valor maximo de capital devido para contratos aberto esteja configurado")
    public void valor_maximo_capital_devido_contratos_aberto_configurado() {
        log.info("Dado Que o valor máximo de capital devido para contratos aberto esteja configurado");

        registroServidorService.alterarRseBaseCalculo(registroServidor, null);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_TIPO_VLR, "P");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, "1");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "1");
    }

    @E("Servidor sem Ade no periodo de restricao de novos ades")
    public void serSemAdeNoPeriodoRestricaoSvc() throws Throwable {
        log.info("Dado Servidor sem Ade no periodo de restricao de novos ades");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE, "1");
    }

    @E("Servidor possui vinculo com convenio do Servico para a CSA")
    public void serComVinculoRse() throws Throwable {
        log.info("Dado Servidor possui vínculo com convênio do Servico para a CSA");

        final String vrsCodigo = "AUTO" + random.nextLong();

        vinculoRegistroService.incluirVinculoRegistroServidor(vrsCodigo, "001");

        // Alterar parametros do metodo para csaCodigo, svcCodigo e vrsCodigo pois nao existe mais a coluna cnvCodigo
        vinculoRegistroService.incluirConvenioVinculoRegistro(vrsCodigo, csaCodigo, svcCodigoEmprestimo);

        registroServidorService.alterarVrsCodigo(registroServidor, vrsCodigo);
    }

    @E("Servidor correntista da Consignataria")
    public void serCorrentistaCsa() throws Throwable {
        log.info("Dado Servidor correntista da Consignataria");

        final Consignataria consignataria = consignatariaService.getConsignataria(csaCodigo);

        registroServidorService.alterarRseBancoSal(registroServidor, consignataria.getCsaIdentificadorInterno());

        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA, csaCodigo, CodedValues.TPA_SIM);
    }

    @E("Servidor nao correntista da Consignataria")
    public void serNaoCorrentistaCsa() throws Throwable {
        log.info("Dado Servidor não correntista da Consignataria");

        registroServidorService.alterarRseBancoSal(registroServidor, null);

        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA, csaCodigo, CodedValues.TPA_SIM);
    }

    @E("Servico com limita capital devido a base de calculo")
    public void serLimitaCapitalDevidoBaseCalculo() throws Throwable {
        log.info("Dado Servico com limita capital devido a base de calculo");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "1");
    }

    @E("Usuario verifica o valor liquido e menor do que residual da base de calculo {string}")
    public void limiteBaseCalcMenorAdeVlrLiq(String vlrLiquido) throws Throwable {
        log.info("Dado Usuario verifica o valor liquido é menor do que residual da base de calculo {string}");

        BigDecimal capitalDevido = capitalDevidoTotal();

        final BigDecimal adeVlrLiq = new BigDecimal(vlrLiquido);
        capitalDevido = capitalDevido.add(adeVlrLiq);

        registroServidorService.alterarRseBaseCalculo(registroServidor, new BigDecimal("20000"));
    }

    @E("Usuario verifica que valor liquido mais o capital devido total e maior do que a base de calculo")
    public void limiteBaseCalcMaiorAdeVlrLiq() throws Throwable {
        log.info("Dado Usuario verifica que valor liquido mais o capital devido total é maior do que a base de calculo");

        registroServidorService.alterarRseBaseCalculo(registroServidor, new BigDecimal("100"));
    }

    @E("Alem do periodo de Bloqueio da Funcao Reservar")
    public void alemPeriodoBloqFun() throws Throwable {
        log.info("Dado Alem do periodo de Bloqueio da Funcao Reservar");

        funcaoSistemaService.alteraDataLimiteFuncao(CodedValues.FUN_RES_MARGEM, registroServidor.getRseCodigo(), new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), -2).getTime()));
    }

    @E("Verifica que Bloqueio da Funcao Reserva Foi Removido")
    public void checkBloqFunReservarRemovido() throws Throwable {
        log.info("Dado Verifica que Bloqueio da Funcao Reserva Foi Removido");

        final BloqueioRseFun dataLimite = funcaoSistemaService.getBloqueioRseFuncao(registroServidor.getRseCodigo(), CodedValues.FUN_RES_MARGEM);
        assertNull(dataLimite);
    }

    @E("Dentro do periodo de Bloqueio da Funcao Reservar")
    public void dentroPeriodoBloqFun() throws Throwable {
        log.info("Dado Dentro do periodo de Bloqueio da Funcao Reservar");

        funcaoSistemaService.alteraDataLimiteFuncao(CodedValues.FUN_RES_MARGEM, registroServidor.getRseCodigo(), new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), 1).getTime()));
    }

    @E("Servico Possui limite de ADE ativos no limite do numero de ADEs do servidor para este")
    public void svcComLimitADEsNoLimitNumeroAdesSer() throws Throwable {
        log.info("Dado Servico Possui limite de ADE ativos no limite do numero de ADEs do servidor para este");

        parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(), svcCodigoEmprestimo, CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "1");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_NAO);
    }

    @E("Servico Possui limite de ADE ativos maior que numero de ADEs do servidor")
    public void cnvComLimitADEsMaiorNumeroAdesSer() throws Throwable {
        log.info("Dado Servico Possui limite de ADE ativos maior que numero de ADEs do servidor");

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, String.valueOf(numAdesSer + 1));
    }

    @E("Servico Possui limite de ADE ativos menor que numero de ADEs do servidor")
    public void usuSelectSerComAdeAtivos() throws Throwable {
        log.info("Dado Servico Possui limite de ADE ativos menor que numero de ADEs do servidor");

        String rseCodigo = "48178080808080808080808080808C80";
        String cnvCodigo = "751F8080808080808080808080809Z85";
        final List<Object[]> contratos = autDescontoService.getAdesLimite(rseCodigo, cnvCodigo);
        final int numAdesSer = contratos.size();

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, String.valueOf(numAdesSer - 1));
    }

    @E("Limite de Ades do RSE por convenio menor que numero de ADEs do servidor")
    public void limitAdeCnvRseMenorNumAdeSer() throws Throwable {
        log.info("Dado Limite de Ades do RSE por convenio menor que numero de ADEs do servidor");

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        if (numAdesSer > 0) {
            parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, String.valueOf(numAdesSer));
        }
    }

    @E("Convenio Possui limite de ADE ativos acima do limite do numero de ADEs do servidor")
    public void cnvAcimaLimitADEsMenorNumeroAdesSer() throws Throwable {
        log.info("Dado Convenio Possui limite de ADE ativos acima do limite do numero de ADEs do servidor");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, String.valueOf(numAdesSer + 2));
    }

    @E("Convenio Possui limite de ADE ativos menor que numero de ADEs do servidor")
    public void cnvMenorADEsMenorNumeroAdesSer() throws Throwable {
        log.info("Dado Convenio Possui limite de ADE ativos menor que numero de ADEs do servidor");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "0");
    }

    @E("Nao possui limite de ADE ativos por Servico por servidor")
    public void naoPossuiLimitAdeRseCnv() throws Throwable {
        log.info("Dado Não possui limite de ADE ativos por Servico por servidor");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, "");
    }

    @E("Limite Ades Por Cnv Para o Servico maior que numero de ADEs do servidor")
    public void limitAdeCnvRseServicoMaiorNumAdeSer() throws Throwable {
        log.info("Dado Limite Ades Por Cnv Para o Servico maior que numero de ADEs do servidor");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, String.valueOf(numAdesSer + 2));
    }

    @E("Limite Ades Por Cnv Para o Servico menor que numero de ADEs do servidor")
    public void limitAdeCnvRseServicoMenorNumAdeSer() throws Throwable {
        log.info("Dado Limite Ades Por Cnv Para o Servico menor que numero de ADEs do servidor");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "1");
    }

    @E("Servidor possui contrato ativo a concluir")
    public void serComAdeAConcluir() throws Throwable {
        log.info("Dado Servidor possui contrato ativo a concluir");

        final String rseCodigo = registroServidor.getRseCodigo();
        final AutDesconto ade = autDescontoService.alterarAutDesconto(rseCodigo, "2", "1");

        autDescontoService.incluirParcelaDesconto(ade.getAdeCodigo(), CodedValues.SPD_EMPROCESSAMENTO, new BigDecimal("10"), "1");
        autDescontoService.incluirParcelaDescontoPeriodo(ade.getAdeCodigo(), CodedValues.SPD_EMPROCESSAMENTO, new BigDecimal("10"), "1");
    }

    @E("Servico Possui limite de ADE ativos acima do numero de ADEs do servidor para este")
    public void svcComLimitADEsMenorNumeroAdesSer() throws Throwable {
        log.info("Dado Servico Possui limite de ADE ativos acima do numero de ADEs do servidor para este");

        final List<AutDesconto> ades = listaAdesAtivosLimites();
        final int numAdesSer = ((ades != null) && !ades.isEmpty()) ? ades.size() : 0;

        parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(), svcCodigoEmprestimo, CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, String.valueOf(numAdesSer + 1));
    }

    @E("Sistema ignora contratos a concluir no limite de Ades")
    public void sistIgnoraAdesAConcluir() throws Throwable {
        log.info("Dado Sistema ignora contratos a concluir no limite de Ades");

        resetParametroSistLimiteAdes();
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM);
    }

    @E("Data de inclusao fora do periodo de restricao do servico apos ultima liquidacao de contrato do servidor")
    public void curDateForaPeriodoUltimaLiquidacao() throws Throwable {
        log.info("Dado Data de inclusao fora do periodo de restricao do servico apos ultima liquidacao de contrato do servidor");

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ, "1");
    }

    @E("Data de inclusao dentro do periodo de restricao do servico apos ultima liquidacao de contrato do servidor")
    public void curDateDentroPeriodoUltimaLiquidacao() throws Throwable {
        log.info("Dado Data de inclusao dentro do periodo de restricao do servico apos ultima liquidacao de contrato do servidor");

        final OcorrenciaAutorizacao ocorrenciaAutorizacao = ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_TARIF_LIQUIDACAO, usuarioCsa.getUsuCodigo()).get(0);

        final Date dataUltimaLiq = ocorrenciaAutorizacao.getOcaData();

        final Integer qtdDiasUltimaLiq = DateHelper.dayDiff(DateHelper.getSystemDatetime(), dataUltimaLiq);

        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ, String.valueOf(qtdDiasUltimaLiq + 2));
    }

    @Dado("Servico ativo de Identificador {string} e Natureza {string}")
    public void servicoAtivoComIdentificadorInformado(String servicoId, String nseCodigo) throws Throwable {
        log.info("Servico ativo de Identificador {} e Natureza {}", servicoId, nseCodigo);

        // Busca servico pelo identificador, e caso não exista, cria
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);
        if (servico == null) {
            // Busca natureza de serviço pelo codigo
            NaturezaServico nse = servicoService.obterNaturezaServicoPorCodigo(nseCodigo);
            servico = servicoService.incluirServicoAtivo(servicoId, nse.getNseDescricao(), nseCodigo);
        }
        log.debug("Servico {}", servico.getSvcCodigo());
    }

    @E("Parametro de Servico {string} com valor {string} para o Servico {string} para {string}")
    public void parametroServicoComValorInformado(String tpsCodigo, String pseVlr, String servicoId, String textoExplicacaoParam) throws Throwable {
        log.info("Parametro de Servico {} com valor {} para o Servico {}", tpsCodigo, pseVlr, servicoId);

        // Busca servico pelo identificador
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);
        if (servico != null) {
            parametroSistemaService.configurarParametroServicoCse(servico.getSvcCodigo(), tpsCodigo, pseVlr);
        }
    }

    @E("Parametro de Servico de Consignataria {string} com valor {string} para o Servico {string} e Consignataria {string} para {string}")
    public void parametroServicoCsaComValorInformado(String tpsCodigo, String pscVlr, String servicoId, String consignatariaId, String textoExplicacaoParam) throws Throwable {
        log.info("Parametro de Servico de Consignataria {} com valor {} para o Servico {} e Consignataria {} para {}", tpsCodigo, pscVlr, servicoId, consignatariaId, textoExplicacaoParam);

        // Busca servico e consignataria pelo identificador
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);
        Consignataria consignataria = consignatariaService.obterConsignatariaPorIdentificador(consignatariaId);
        if (servico != null && consignataria != null) {
            parametroSistemaService.configurarParametroServicoCsa(servico.getSvcCodigo(), tpsCodigo, consignataria.getCsaCodigo(), pscVlr);
        }
    }

    @E("Relacionamento do Servico Origem {string} para o Servico Destino {string} de Natureza {string}")
    public void relacionamentoServicoComNaturezaInformada(String servicoOrigemId, String servicoDestinoId, String tntCodigo) throws Throwable {
        log.info("Relacionamento do Servico Origem {} para o Servico Destino {} de Natureza {}", servicoOrigemId, servicoDestinoId, tntCodigo);

        // Busca servico origem e destino pelo identificador
        Servico servicoOrigem = servicoService.obterServicoPorIdentificador(servicoOrigemId);
        Servico servicoDestino = servicoService.obterServicoPorIdentificador(servicoDestinoId);
        if (servicoOrigem != null && servicoDestino != null) {
            relacionamentoServicoService.incluirRelacionamentoServico(servicoOrigem.getSvcCodigo(), servicoDestino.getSvcCodigo(), tntCodigo);
        }
    }

    @E("Orgao ativo de Identificador {string} no Estabelecimento {string}")
    public void orgaoAtivoComIdentificadorInformado(String orgaoId, String estabelecimentoId) throws Throwable {
        log.info("Orgao ativo de Identificador {} no Estabelecimento {}", orgaoId, estabelecimentoId);

        // Busca orgao pelo identificador, e caso não exista, cria
        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgaoId);
        if (orgao == null) {
            // Busca estabelecimento pelo identificador
            Estabelecimento estabelecimento = estabelecimentoService.obterEstabelecimentoPorIdentificador(estabelecimentoId);
            orgao = orgaoService.incluirOrgaoAtivo(estabelecimento.getEstCodigo(), "TESTE", orgaoId);
        }
        log.debug("Orgao {}", orgao.getOrgCodigo());
    }

    @E("Convenio ativo de Verba {string} para o Servico {string}, Consignataria {string} e Orgao {string}")
    public void convenioAtivoComServicoConsignatariaOrgaoInformados(String verba, String servicoId, String consignatariaId, String orgaoId) throws Throwable {
        log.info("Convenio ativo de Verba {} para o Servico {}, Consignataria {} e Orgao {}", verba, servicoId, consignatariaId, orgaoId);

        // Busca consignatária, serviço e órgão pelos identificadores
        Consignataria consignataria = consignatariaService.obterConsignatariaPorIdentificador(consignatariaId);
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);
        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgaoId);
        // Busca convênio pelo código das entidades, e caso não exista, cria
        Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(servico.getSvcCodigo(), orgao.getOrgCodigo(), consignataria.getCsaCodigo());
        if (convenio == null) {
            convenio = convenioService.incluirConvenioAtivo(servico.getSvcCodigo(), orgao.getOrgCodigo(), consignataria.getCsaCodigo(), verba);
            verbaConvenioService.incluirVerbaConvenioAtivo(convenio.getCnvCodigo());
        }
        log.debug("Convenio {}", convenio.getCnvCodigo());
    }

    @E("Taxa para o Prazo {short} cadastrada para o Servico {string} e Consignataria {string} com valor {double}")
    public void taxaAtivaParaPrazoServicoConsignatariaInformados(Short prazo, String servicoId, String consignatariaId, Double taxa) throws Throwable {
        log.info("Taxa para o Prazo {} cadastrada para o Servico {} e Consignataria {} com valor {}", prazo, servicoId, consignatariaId, taxa);

        Consignataria consignataria = consignatariaService.obterConsignatariaPorIdentificador(consignatariaId);
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);

        prazoService.incluirCoeficienteAtivo(consignataria.getCsaCodigo(), servico.getSvcCodigo(), prazo, BigDecimal.valueOf(taxa));
    }

    @E("Servidor ativo de CPF {string} e Matricula {string} no Orgao {string}")
    public void servidorAtivoComCpfMatriculaInformados(String cpf, String matricula, String orgaoId) throws Throwable {
        log.info("Servidor ativo de CPF {} e Matricula {} no Orgao {}", cpf, matricula, orgaoId);

        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgaoId);
        // Busca o servidor pelo CPF, se não existir cria
        Servidor servidor = servidorService.obterServidorPeloCpf(cpf);
        if (servidor == null) {
            servidor = servidorService.incluirServidor("TESTE", cpf);
        }
        // Busca o registro servidor pela matrícula e órgão, se não existir cria
        RegistroServidor registroServidor = registroServidorService.obterRegistroServidorPorMatriculaOrgao(matricula, orgao.getOrgCodigo());
        if (registroServidor == null) {
            registroServidor = registroServidorService.incluirRegistroServidorAtivoComMargem(servidor.getSerCodigo(), orgao.getOrgCodigo(), matricula);
        }
        log.debug("RegistroServidor {}", registroServidor.getRseCodigo());
    }

    private void resetParamSvcLimitesAde() {
        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);

        resetParamSvcCseLimitesAde(svcCodigoEmprestimo);
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA, csaCodigo, CodedValues.TPC_NAO);
        parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(), convenio.getCnvCodigo(), CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "");
        parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(), svcCodigoEmprestimo, CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "");
        parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(), CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigoMensalidade, CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE, "");
    }

    private List<AutDesconto> listaAdesAtivosLimites() {
        final String rseCodigo = registroServidor.getRseCodigo();

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);

        return autDescontoService.getAdes(rseCodigo, sadCodigos);
    }

    private BigDecimal capitalDevidoTotal() throws Throwable {
        final String rseCodigo = registroServidor.getRseCodigo();
        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, registroServidor.getOrgCodigo(), csaCodigo);
        final CalendarioFolhaCse calendarioFolhaCse = calendarioFolhaCseService.getCalendarioFolhaCse(convenio.getOrgCodigo());

        final GregorianCalendar gc = new GregorianCalendar(); // assume a data e hora atuais
        // Ajustar campos da data:
        gc.set(GregorianCalendar.DAY_OF_MONTH, 01); // muda o dia do mês

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);

        final List<AutDesconto> autDescontos = autDescontoService.getAdes(rseCodigo, sadCodigos);
        for (final AutDesconto autDesconto : autDescontos) {
            autDesconto.setAdeAnoMesFim(new Date(gc.getTimeInMillis()));
        }

        BigDecimal capitalDevido = BigDecimal.ZERO;
        for (final AutDesconto autDesconto : autDescontos) {
            final Date anoMesFim = autDesconto.getAdeAnoMesFim();
            int mesDiff = DateHelper.monthDiff(DateHelper.parse(DateHelper.format(anoMesFim, "yyyy-MM"), "yyyy-MM"), DateHelper.parse(DateHelper.format(calendarioFolhaCse.getCfcPeriodo(), "yyyy-MM"), "yyyy-MM")) + 1;
            mesDiff = Math.max(mesDiff, 0);
            capitalDevido = capitalDevido.add(autDesconto.getAdeVlr().multiply(new BigDecimal(mesDiff)));
        }

        return capitalDevido;
    }

    private void reservarMargemUsandoSteps() throws Throwable {
        // configura paramentro
        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_VLR_LIQ_TAXA_JUROS, "0");
        funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_CONF_RESERVA, csaCodigo, usuarioCsa.getUsuCodigo());
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "S");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR, csaCodigo, "S");
        funcaoSistemaService.alteraExigeSegundaSenhaCsaFuncao(CodedValues.FUN_RES_MARGEM, "N");

        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa);

        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();
        reservarMargemPage.criarReserva(loginSer.getLogin());

        assertTrue(reservarMargemPage.retornarPrestacaoTelaSucesso().contains("10"));
    }

    private void resetParametroSistLimiteAdes() {
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_NAO);
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INCLUSAO_ADE_PRZ_UM_ALEM_LIMITE_CSA_SER, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_BLOQUEIO_FUNCAO_RSE, "");
        EConsigInitializer.limparCache();
    }

    /**
     * desabilita todos os paremetros de serviço de limites de contratos
     *
     * @throws Throwable
     */
    private void resetParamSvcCseLimitesAde(String svcCodigo) {
        parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ, "");
        parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, "");
    }
}
