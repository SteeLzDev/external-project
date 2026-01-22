package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.CampoSistemaDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.CampoSistemaService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.ConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.MultiplosContratosPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AlterarContratoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ConsignacaoPage consignacaoPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;
    private ReservarMargemPage reservarMargemPage;
    private UsuarioPage usuarioPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private MultiplosContratosPage multiplosContratosPage;

	private final String casChaveNovoValor = "alterarConsignacao_novoValor";
	private String adeNumero = null;
	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor1 = LoginValues.servidor1;
	private final LoginInfo loginServidor2 = LoginValues.servidor2;
	private String svcCodigoEmprestimo;
	private final String csaCodigo = "267";
	private final String  matriculaServidor = "579771";
	private final String motivoBloqueio = "bloqueio teste";
	private final String motivoDesbloqueio = "Desbloqueio Teste";
	private final String novoValorContratos = "MARGEM 3";

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RegistroServidorService registroServidor;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private CampoSistemaService campoSistemaService;

	@Autowired
	private CampoSistemaDao campoSistemaDao;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private AutDescontoDao autDescontoDao;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
	    loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        consignacaoPage = new ConsignacaoPage(webDriver);
        consultarConsignacaoPage = new ConsultarConsignacaoPage(webDriver);
        reservarMargemPage = new ReservarMargemPage(webDriver);
        usuarioPage = new UsuarioPage(webDriver);
        acoesUsuarioPage = new AcoesUsuarioPage(webDriver);
        multiplosContratosPage = new MultiplosContratosPage(webDriver);

		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_BUSCA_BOLETO_EXTERNO,
				"0");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "N");
        parametroSistemaService.configurarParametroServicoCsa(svcCodigoEmprestimo, CodedValues.TPS_CNV_PODE_DEFERIR,
                csaCodigo, "N");
		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTAR_CONSIGNACAO.getCodigo()));
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTAR_MARGEM.getCodigo()));
		EConsigInitializer.limparCache();
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void alterarContratoComSucesso() {
		log.info("Alterar Contrato com sucesso");
		// cria consignacao
		adeNumero = "20";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.clicarPesquisar();

		// alterar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlterarContrato();

		consignacaoPage.alterarContrato("7");

		assertEquals("Confirma a alteração do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	// DESENV-22379 - Teste Automatizado - Validação campo inválido ao preencher matricula do servidor
	@Test
	public void alterarContratoComMatriculaInvalida() {
		log.info("Alterar Contrato: Valida matricula invalida");

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula("999999");
		consultarConsignacaoPage.clicarPesquisar();

	     
        // Valida mensagem de erro matricula invalida
        assertEquals("Nenhum registro encontrado para a pesquisa:Matrícula: 999999", econsigHelper.getMensagemErro(webDriver).replace("\n", "").replace("\r", ""));
	}
	
	//DESENV-19575 - Teste automatizado - Conferir campos dos contratos após alteração
	@Test
	public void alterarContratoPesquisandoPorMatriculaComParametroAlterado() {
		log.info("Alterar Contrato com sucesso");
		// cria consignacao
		adeNumero = "20";

		// Ativa parametro 911 no sistema
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, "S");
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.clicarPesquisar();

		// alterar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlterarContrato();

		consignacaoPage.alterarContrato("7");

		assertEquals("Confirma a alteração do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// retorna valor 911 para o padrao
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, "N");
		EConsigInitializer.limparCache();
	}

	//DESENV-19575 - Teste automatizado - Conferir campos dos contratos após alteração
	@Test
	public void alterarContratoPesquisandoPorAdeNumeroComParametroAlterado() {
		log.info("Alterar Contrato com sucesso");

		// Ativa parametro 911 no sistema
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, "S");
		EConsigInitializer.limparCache();

		// cria consignacao
		adeNumero = "20";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao por ade numero
		consultarConsignacaoPage.preencherAdeNumero(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// alterar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlterarContrato();

		consignacaoPage.alterarContrato("7");

		assertEquals("Confirma a alteração do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// retorna valor 911 para o padrao
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, "N");
		EConsigInitializer.limparCache();
	}


	// DESENV-19575 - Conferir campos dos contratos após alteração
	@Test
	public void tentaAlterarContratoComCampoValorParcelaBloqueado() {
		log.info("Alterar Contrato com sucesso");

		// Bloqueia novo valor no banco
		campoSistemaService.alterarCampoSistema(casChaveNovoValor, "N");
		ShowFieldHelper.reset();

		// Verifica se o bloqueio do campo esta presente
		assertEquals("N", campoSistemaDao.findByCasChave(casChaveNovoValor).getCasValor());

		// cria consignacao
		adeNumero = "20";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.clicarPesquisar();

		// alterar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlterarContrato();

		// verifica se contem o campo novo valor
		assertTrue(consignacaoPage.novoValorPrestacaoDisable());

		// retorna o novo campo valor para padao
		campoSistemaService.alterarCampoSistema(casChaveNovoValor, "S");
		ShowFieldHelper.reset();
		assertEquals("S", campoSistemaDao.findByCasChave(casChaveNovoValor).getCasValor());
	}

	@Test
	public void tentarAlterarContratoComPrazoInvalidoEPrazoMaiorQuePermitido() {
		log.info("Tentar alterar contrato com prazo maior");
		// cria consignacao
		adeNumero = "20";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.clicarPesquisar();

		// tentar alterar contrato sem valor da prestacao
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlterarContrato();
		consignacaoPage.limparNroPrestacaoAlterarContrato();
		acoesUsuarioPage.clicarSalvar();
		assertEquals("O número de parcelas deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));

		// tentar alterar contrato com valor da prestacao maior que o permitido
		consignacaoPage.alterarContrato("20");

		assertEquals("Confirma a alteração do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("NÃO É POSSÍVEL ALTERAR ESTA CONSIGNAÇÃO POIS O PRAZO ESCOLHIDO É MAIOR DO QUE O PRAZO ATUAL.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void liquidarConsignacao() throws InterruptedException {
		log.info("Liquidar Consignacao");
		// cria consignacao
		adeNumero = "18";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela para liquidar consignacao
		consultarConsignacaoPage.clicarVisualizar();
		consignacaoPage.clicarAcoes();

		// liquidar consignacao
		consignacaoPage.clicarLiquidarConsignacao();

		assertEquals("Confirma a liquidação deste contrato?", econsigHelper.getMensagemPopUp(webDriver));

		// informar motivo operacao
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Bloquear servico");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco se alterou a situacao da consignacao
		assertEquals(CodedValues.SAD_LIQUIDADA, autDescontoService.getSadCodigo(adeNumero));
	}

	@Test
	public void desliquidarConsignacao() throws InterruptedException {
		log.info("Liquidar Consignacao");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "S");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "1");
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();

		// cria consignacao
		adeNumero = reservarMargemPage.criarReservaMargemComSenhaServidor(loginServidor1);

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// liquidar consignação antes
		liquidarConsignacao(adeNumero, loginServidor1.getLogin());

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela para liquidar consignacao
		consultarConsignacaoPage.clicarVisualizar();
		consignacaoPage.clicarAcoes();

		// liquidar consignacao
		consignacaoPage.clicarDesliquidarConsignacao();

		// informar motivo operacao
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Bloquear servico");
		consignacaoPage.clicarConfirmar();

		assertEquals("Confirma a desliquidação deste contrato?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco se alterou a situacao da consignacao
		assertEquals(CodedValues.SAD_DEFERIDA, autDescontoService.getSadCodigo(adeNumero));
	}

	@Test
	public void tentarDesliquidarConsignacaoComMargemLiberadaJaUtilizada() throws InterruptedException {
		log.info("Tentar desliquidar Consignacao com margem liberada ja utilizada");
		registroServidor.alterarRseMargemRest(loginServidor2.getLogin(), BigDecimal.valueOf(1000));
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, "S");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_SENHA_EXP_SERVIDOR_RESERVA_MARGEM, "S");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "1");
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();

		// cria consignacao
		adeNumero = reservarMargemPage.criarReservaMargemComSenhaServidor(loginServidor2);

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// liquidar consignação antes
		liquidarConsignacao(adeNumero, loginServidor1.getLogin());
		registroServidor.alterarRseMargemRest(loginServidor2.getLogin(), BigDecimal.ZERO);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela para liquidar consignacao
		consultarConsignacaoPage.clicarVisualizar();
		consignacaoPage.clicarAcoes();

		// liquidar consignacao
		consignacaoPage.clicarDesliquidarConsignacao();

		// informar motivo operacao
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Bloquear servico");
		consignacaoPage.clicarConfirmar();

		assertEquals("Confirma a desliquidação deste contrato?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Autorização não pode ser desliquidada porque a margem liberada pela liquidação já foi utilizada.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void liquidarParcelaComSucesso() {
		log.info("Liquidar Parcela com sucesso");

		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");

		// loga no sistema como CSA
		EConsigInitializer.limparCache();
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();

		// cria consignacao
		adeNumero = reservarMargemPage.criarReservaMargem(loginServidor1.getLogin());

		// loga no sistema como cse
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// deferir contrato
		deferirConsignacao(loginServidor1.getLogin(), adeNumero);

		consignacaoPage.clicarAcoes();

		// liquidar consignacao
		consignacaoPage.clicarLiquidarParcela();
		consignacaoPage.clicarLiquidar();
		consignacaoPage.clicarConfirmar();

		assertEquals("Confirma a liquidação das parcelas selecionadas?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica no banco se alterou a situacao da consignacao
		assertEquals(CodedValues.SAD_EMANDAMENTO, autDescontoService.getSadCodigo(adeNumero));
	}

	@Test
	public void tentarLiquidarParcelaSemSelecionarParcela() throws InterruptedException {
		log.info("Tentar liquidar parcela sem selecionar parcela");
		// cria consignacao
		adeNumero = "20";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela para liquidar consignacao
		consultarConsignacaoPage.clicarVisualizar();
		consignacaoPage.clicarAcoes();

		// confirmar liquidacao sem informar parcela consignacao
		consignacaoPage.clicarLiquidarParcela();
		consignacaoPage.clicarConfirmarSemParcela();

		assertEquals("Selecione pelo menos uma parcela.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void incluirAnexoConsignacao() throws Exception {
		log.info("Incluir anexo consignação");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "20";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarEditarAnexoConsignacao();

		// Seleciona o arquivo para o upload.
		consignacaoPage.anexarArquivo();
		// verificar o link para download
		assertTrue(
				consignacaoPage.getValorAtributo(By.cssSelector("a[onclick='downloadAnexoVisualizacao(0)']"), "title")
						.contains("Download deste anexo"));

		consignacaoPage.preencherDescricao("Anexo automacao");
		consignacaoPage.clicarConfirmar();

		assertEquals("Anexo foi incluído com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void editarAnexoConsignacao() throws Exception {
		log.info("Editar anexo consignação");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "23";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarEditarAnexoConsignacao();
		// Seleciona o arquivo para o upload.
		consignacaoPage.anexarArquivo();
		consignacaoPage.preencherDescricao("Anexo automacao");
		consignacaoPage.clicarConfirmarAnexo();

		// editar anexo
		consignacaoPage.editarAnexo("Alteracao anexo");

		assertEquals("Anexo foi alterado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void bloquearEDesbloquearAnexoConsignacao() throws Exception {
		log.info("Bloquear e Desbloquear anexo consignação");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "22";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarEditarAnexoConsignacao();

		// Seleciona o arquivo para o upload.
		consignacaoPage.anexarArquivo();
		consignacaoPage.preencherDescricao("Anexo automacao");
		consignacaoPage.clicarConfirmar();

		// bloquear anexo
		consignacaoPage.bloquearAnexo();

		assertEquals("Anexo foi alterado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
		econsigHelper.verificaTextoPagina(webDriver, "Bloqueado");

		// desbloquear anexo
		consignacaoPage.desbloquearAnexo();
		assertEquals("Anexo foi alterado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
		econsigHelper.verificaTextoPagina(webDriver, "Desbloqueado");

	}

	@Test
	public void tentarAnexarArquivoConsignacaoComDadosInvalidos() throws Exception {
		log.info("Tentar anexar arquivo consignação com dados invalidos");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "20";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarEditarAnexoConsignacao();

		// anexar arquivo maior que o permitido
		consignacaoPage.anexarArquivoMaiorPermitido();
		assertEquals(
				"O arquivo " + ConsignacaoPage.nomeArquivoMaior200k + " é muito grande. (200 K é o tamanho máximo)",
				econsigHelper.getMensagemPopUp(webDriver));

		// Não selecionar o arquivo
		consignacaoPage.clicarConfirmar();
		assertEquals("Selecione o arquivo para o upload.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void removerAnexoConsignacao() throws Exception {
		log.info("Remover Anexo Consignacao");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "24";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarEditarAnexoConsignacao();

		// Seleciona o arquivo para o upload.
		consignacaoPage.anexarArquivo();

		// remover anexo
		consignacaoPage.removerAnexo();

		assertEquals("Anexo removido com sucesso", econsigHelper.getMensagemPopUp(webDriver));

		// salvar pra verificar se foi removido
		consignacaoPage.preencherDescricao("Anexo automacao");
		consignacaoPage.clicarConfirmar();
		assertEquals("Selecione o arquivo para o upload.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void registrarOcorrenciaComSucesso() {
		log.info("Registrar ocorrencia com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "12";
		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarRegistrarOcorrencia();

		// incluir ocorrencia
		consignacaoPage.selecionarTipoOcorrencia("Aviso");
		consignacaoPage.preencherObservacao("Testes automatizado, registrar ocorrencia para consignação");
		consignacaoPage.clicarConfirmar();

		assertEquals("Prosseguir com o registro de ocorrência do contrato selecionado?",
				econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarRegistrarOcorrenciaSemInformarObservacao() {
		log.info("Tentar registrar ocorrencia sem informar observacao");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "12";
		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarRegistrarOcorrencia();

		// tentar incluir ocorrencia
		consignacaoPage.clicarConfirmarSemObservacao();
		assertEquals("Observação da ocorrência deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void suspenderConsignacaoComSucesso() {
		log.info("Suspender consignação com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "25";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();

		// suspender consignacao
		consignacaoPage.clicarSuspenderConsignacao();
		assertEquals("Confirma a suspensão desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.preencherDataReativacao(DateHelper
				.format(new Timestamp(DateHelper.addDays(DateHelper.getSystemDate(), +5).getTime()), "dd/MM/yyyy")
				.toString());
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Suspender Consignação");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Atenção: a reativação automática pode não ocorrer na data informada caso o sistema esteja indisponível no dia ou ocorra algo que impeça a reativação.",
				econsigHelper.getMensagemPopUp(webDriver));
		multiplosContratosPage.confirmarAlert();

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
		assertTrue(webDriver.getPageSource().contains("Suspensa pela Cse."));
	}

	@Test
	public void suspenderConsignacaoSemInformarDataReativacaoComSucesso() {
		log.info("Suspender consignação sem informar data reativacao com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "26";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();

		// suspender consignacao
		consignacaoPage.clicarSuspenderConsignacao();
		assertEquals("Confirma a suspensão desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Suspender Consignação");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma a suspensão desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
		assertTrue(webDriver.getPageSource().contains("Suspensa pela Cse."));
	}

	@Test
	public void reativarConsignacaoComSucesso() {
		log.info("Reativar consignação com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "32";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela de editar anexo
		consignacaoPage.clicarOpcoesAlterar(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();

		// reativar consignacao
		consignacaoPage.clicarReativarConsignacao();
		multiplosContratosPage.confirmarAlert();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Reativar Consignação");
		consignacaoPage.clicarConfirmar();

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
		assertTrue(webDriver.getPageSource().contains("Deferida"));
	}

	@Test
	public void solicitarSaldoDevedorInformativo() {
		log.info("Solicitar Saldo Devedor Informativo com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "27";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar solicitar saldo devedor
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();

		// suspender consignacao
		consignacaoPage.clicarSolicitarSaldoDevedorInformativo();
		consignacaoPage.clicarConfirmarSolicitarSaldo();

		assertEquals("A solicitação do saldo devedor foi enviada para a consignatária.",
				econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void solicitarSaldoDevedorParaLiquidacao() {
		log.info("Solicitar Saldo Devedor para Liquidacao com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// cria consignacao
		adeNumero = "28";

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar solicitar saldo devedor
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();

		// suspender consignacao
		consignacaoPage.clicarSolicitarSaldoDevedorParaLiquidacao();
		consignacaoPage.clicarConfirmarSolicitarSaldoLiquidacao();

		assertEquals("A solicitação do saldo devedor foi enviada para a consignatária.",
				econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void renegociarConsignacaoComSucesso() {
		log.info("Renegociar Consignação com sucesso");
		// criar ade
		adeNumero = "29";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela renegociar consignacao
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarRenegociarConsignacao();

		consignacaoPage.renegociarContrato("9", "4", "199");

		assertEquals("Confirma a renegociação do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		econsigHelper.verificaTextoPagina(webDriver, "Controle de renegociação - ADE " + adeNumero);
		assertEquals("9,00", consignacaoPage.getValorPretacao());
		assertEquals("4", consignacaoPage.getNumeroPrestacao());
		assertEquals("199,00", consignacaoPage.getValorLiquido());

		// Confere se a ocorrencia de relacionamento foi gerada com sucesso.
		assertTrue(relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(Long.valueOf(adeNumero)).getTntCodigo()
				.contains(CodedValues.TNT_CONTROLE_RENEGOCIACAO));

		// Confere se a ocorrencia de autorizacao foi gerada com sucesso.
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_INFORMACAO,
				autDescontoService.getAde(adeNumero).getAdeCodigo(), "SITUAÇÃO ALTERADA DE 4 PARA 11"));

		// verificar status do contrato anterior - Aguard. Liquidação
		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		econsigHelper.verificaTextoPagina(webDriver, "Aguard. Liquidação");
	}

	@Test
	public void tentarRenegociarConsignacaoComDadosInvalidos() {
		log.info("Tentar renegociar consignacao com dados invalidos");
		// criar ade
		adeNumero = "30";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela renegociar consignacao
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarRenegociarConsignacao();

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("", "14", "1010");
		assertEquals("O valor da consignação deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("99", "", "1010");
		assertEquals("O número de parcelas deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("99", "14", "");
		assertEquals("O valor líquido liberado deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("200", "14", "1010");
		assertEquals("O valor informado deve ser inferior a R$ 100,00.", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("99", "0", "1010");
		assertEquals("Preencha a quantidade de parcelas com um número inteiro positivo no formato 99. Ex.: 12",
				econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.limparCamposRenegociarContrato();
		consignacaoPage.renegociarContrato("0", "14", "1010");
		assertEquals("Confirma a renegociação do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("O VALOR DA PRESTAÇÃO DEVE SER MAIOR DO QUE ZERO.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void visualizarAutorizacaoDesconto() {
		log.info("Visualizar Autorizacao de Desconto");
		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_BUSCA_BOLETO_EXTERNO,
				"1");
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		adeNumero = "30";

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela visualizar autorizacao desconto
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarVisualizarAutorizacaoDesconto();

		assertTrue(webDriver.getPageSource().contains("Autorização de Desconto"));
		assertTrue(webDriver.getPageSource().contains("I - Dados Pessoais"));
	}

	@Test
	public void reimplatarConsignacaoComSucesso() {
		log.info("Reimplatar Consignacao com sucesso");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		adeNumero = "31";

		AutDesconto adeTeste = autDescontoService.getAde("31");
		Calendar calMesFim = Calendar.getInstance();
		calMesFim.add(Calendar.MONTH, 20);
		adeTeste.setAdeAnoMesFim(calMesFim.getTime());
		autDescontoDao.save(adeTeste);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// acessar tela visualizar autorizacao desconto
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarReimplantarConsignacao();

		assertEquals("Confirma a reimplantação desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		consignacaoPage.preencherObservacaoReimplantar("Reimplantar consignação");
		consignacaoPage.clicarConfirmarReimplantacao();

		assertEquals("Confirma a reimplantação desta consignação?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// Confere se a ocorrencia de autorizacao foi gerada com sucesso.
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_RELANCAMENTO,
				autDescontoService.getAde(adeNumero).getAdeCodigo(), "REIMPLANTE DE CONTRATO:"));
	}

    public void deferirConsignacao(String login, String ade) {
        // acessa menu
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosConsultarConsignacao();

        // pesquisa consignacao
        consultarConsignacaoPage.preencherMatricula(login);
        consultarConsignacaoPage.preencherADE(ade);
        consultarConsignacaoPage.clicarPesquisar();

        // acessar tela para deferir consignacao
        consultarConsignacaoPage.clicarVisualizar();
        consignacaoPage.clicarAcoes();
        consignacaoPage.clicarDeferirConsignacao();

        assertEquals("Confirma o deferimento desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

        usuarioPage.selecionarMotivoOperacao("Outros");
        usuarioPage.preencherObservacao("Teste");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
    }

    // DESENV-22379 - Teste Automatizado - Validação campo inválido ao preencher matricula do servidor
    @Test
    public void alterarMultiplosContratosComMatriculaInvalida() {
    	log.info("Alterar Multiplos Contratos: preenchendo com matricula invalida");
       
    	// loga no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);
        
        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuAlterarMultiplosContratos();
        
        // seleciona natureza de servico e opcao de margem
        multiplosContratosPage.selecionaOpcaoTodasNaturezasDeServico();
        multiplosContratosPage.selecionaOpcaoTodasMargens();
             
        // preenche matricula incorreta
        multiplosContratosPage.descerPagina();
        multiplosContratosPage.preencherMatricula("5555555");
        multiplosContratosPage.botaoPesquisar();
        
        // Valida mensagem de erro matricula invalida
        assertEquals("Nenhum registro encontrado para a pesquisa:Matrícula: 5555555", econsigHelper.getMensagemErro(webDriver).replace("\n", "").replace("\r", ""));
    }
    
    // DESENV-19993 - Bloqueio de Servidor na Alteração de Multiplos Contratos
    @Test
    public void alterarMultiplosContratosPermitindoBloquearServidor() {
        log.info(" Alterar Múltiplos contratos: permitir bloquear servidor");

        // Cria consignacao
        autDescontoService.inserirAutDesconto("909204", CodedValues.SAD_DEFERIDA, "838080808080808080808080808094B2", "751F8080808080808080808080809D80", "5400808080808080808080808080C98B", 100.00f, 4098000l, 10, Short.parseShort("1"));
        autDescontoService.inserirAutDesconto("909305", CodedValues.SAD_DEFERIDA, "838080808080808080808080808094B2", "751F8080808080808080808080809D80", "5400808080808080808080808080C98B", 100.00f, 98092340l, 10, Short.parseShort("1"));

        // loga no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuAlterarMultiplosContratos();

        // seleciona natureza de servico e opcao de margem
        multiplosContratosPage.selecionaOpcaoTodasNaturezasDeServico();
        multiplosContratosPage.selecionaOpcaoTodasMargens();

        // pesquisa consignacoes
        multiplosContratosPage.preencherNumeroAde("4098000");
        multiplosContratosPage.preencherNumeroAde("98092340");
        multiplosContratosPage.botaoPesquisar();

        // seleciona contratos
        multiplosContratosPage.selecionaContratos();
        multiplosContratosPage.botaoConfirmar();
        multiplosContratosPage.confirmarAlert();

        //  bloqueia servidor  e informa motivo bloqueio
        multiplosContratosPage.bloquearServidorParaNovasContratacoes();
        multiplosContratosPage.motivoBloqueio(motivoBloqueio);

        // aplica novo valor de parcela e valida alteracoes
        multiplosContratosPage.preencherNovoValorDosContratos(novoValorContratos);
        multiplosContratosPage.botaoValdiar();

        // seleciona motivo operacao e justifica a operacao
        multiplosContratosPage.selecionaOpcaoMotivoOperacaoAPedidoDoServidor();
        multiplosContratosPage.preencheCampoObservacao(motivoBloqueio);

        // aplica alteracoes
        multiplosContratosPage.botaoAplicar();
        assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

        // valida se o servidor esta bloqueado e o motivo do bloqueio
        assertEquals("2", registroServidorDao.findByRseMatricula(matriculaServidor).getSrsCodigo());
        assertEquals(motivoBloqueio, registroServidorDao.findByRseMatricula(matriculaServidor).getRseMotivoBloqueio());

        // Realisa busca de margem para verificar se o servidor esta bloqueado corretamente
        menuPage.acessarMenuFavoritos();
        menuPage.acessarItemMenuConsultarMargem();
        multiplosContratosPage.preencherMatricula(matriculaServidor);
        multiplosContratosPage.btnEnvia();

        // Verifica se o servidor segue bloqueado
        assertEquals("Servidor não pode fazer novas reservas pois está bloqueado", reservarMargemPage.retornarMensagemErro());
    }

    // DESENV-19993 - Desbloqueio de Servidor na Alteração de Multiplos Contratos
    @Test
    public void alterarMultiplosContratosPermitindoDesbloquearServidor() {
        log.info(" Alterar Múltiplos contratos: permitir bloquear servidor");
        // loga no sistema
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCse);

        // acessa menu
        menuPage.acessarMenuOperacional();
        menuPage.acessarItemMenuAlterarMultiplosContratos();

        // seleciona natureza de servico e opcao de margem
        multiplosContratosPage.selecionaOpcaoTodasNaturezasDeServico();
        multiplosContratosPage.selecionaOpcaoTodasMargens();

        // pesquisa consignacoao
        multiplosContratosPage.preencherNumeroAde("4098000");
        multiplosContratosPage.preencherNumeroAde("98092340");
        multiplosContratosPage.botaoPesquisar();

        // seleciona contratos
        multiplosContratosPage.selecionaContratos();
        multiplosContratosPage.botaoConfirmar();
        multiplosContratosPage.confirmarAlert();

        //  desbloqueia servidor
        multiplosContratosPage.desbloquearServidorParaNovasContratacoes();

        // aplica novo valor de parcela e valida alteracoes
        multiplosContratosPage.preencherNovoValorDosContratos(novoValorContratos);
        multiplosContratosPage.botaoValdiar();
        multiplosContratosPage.confirmarAlert();

        // seleciona motivo operacao e justifica a operacao
        multiplosContratosPage.selecionaOpcaoMotivoOperacaoOutros();
        multiplosContratosPage.preencheCampoObservacao(motivoDesbloqueio);

        // aplica alteracoes
        multiplosContratosPage.botaoAplicar();
        assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

        // valida se o servidor esta desbloqueado e o motivo do desbloqueio
        assertEquals("1", registroServidorDao.findByRseMatricula(matriculaServidor).getSrsCodigo());
        assertNull(registroServidorDao.findByRseMatricula(matriculaServidor).getRseMotivoBloqueio());
    }

    private void liquidarConsignacao(String adeNumero, String loginServidor) {
        // acessar consultarConsignacao
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosConsultarConsignacao();
        consultarConsignacaoPage.preencherMatricula(loginServidor);
        consultarConsignacaoPage.preencherADE(adeNumero);
        consultarConsignacaoPage.clicarPesquisar();

        // acessar tela para liquidar consignacao
        consultarConsignacaoPage.clicarVisualizar();
        consignacaoPage.clicarAcoes();

        // liquidar consignacao
        consignacaoPage.clicarLiquidarConsignacao();

        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }
        // informar motivo operacao
        usuarioPage.selecionarMotivoOperacao("Outros");
        usuarioPage.preencherObservacao("Bloquear servico");
        acoesUsuarioPage.clicarSalvar();

        consignacaoPage.conferirMensagemSucesso();
    }
}
