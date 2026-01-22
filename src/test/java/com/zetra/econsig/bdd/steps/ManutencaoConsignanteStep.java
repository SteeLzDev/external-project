package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignantePage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoPostoPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.ConsignanteService;
import com.zetra.econsig.service.ManutencaoPerfilService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoConsignanteStep {

	private final String descricaoSemFuncao = "Teste Sem Funcoes";
	private final String descricaoComFuncao = "Teste Com Funcoes";

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private ManutencaoPerfilService manutencaoPerfilService;

	@Autowired
	private ConsignanteService consignanteService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

    private LoginPage loginPage;
    private MenuPage menuPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private UsuarioPage usuarioPage;
    private ReservarMargemPage reservarMargemPage;
    private ManutencaoConsignantePage manutencaoConsignantePage;
    private ManutencaoPostoPage manutencaoPostoPage;

    @Before
    public void setUp() throws Exception {
        loginPage = new LoginPage(getWebDriver());
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        usuarioPage = new UsuarioPage(getWebDriver());
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
        manutencaoConsignantePage = new ManutencaoConsignantePage(getWebDriver());
        manutencaoPostoPage = new ManutencaoPostoPage(getWebDriver());
    }

	@Dado("que o consignante esteja ativo")
	public void consignanteAtivo() {
		log.info("Dado que o consignante esteja ativo");

		consignanteService.alterarStatusConsignante("1");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG, "N");
		EConsigInitializer.limparCache();
		loginPage.acessarTelaLogin();
	}

	@Dado("que o consignante esteja bloqueado")
	public void consignanteBloqueado() {
		log.info("Dado que o consignante esteja bloqueado");

		consignanteService.alterarStatusConsignante("2");
	}

	@Quando("clicar em Lista de Perfil")
	public void clicar_lista_de_perfil() throws Throwable {
		log.info("Quando clicar em Lista de Perfil");

		manutencaoConsignantePage.clicarListarPerfilUsuario();
	}

	@Quando("bloquear perfil com a descricao {string}")
	public void clicar_em_bloquear_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando clicar em bloquear perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.perfilBloquear(perfilDescricao);
	}

	@Quando("desbloquear perfil com a descricao {string}")
	public void clicar_em_desbloquear_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando clicar em desbloquear perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.perfilDesbloquear(perfilDescricao);
	}

	@Quando("clicar em adicionar novo perfil")
	public void clicar_em_adicionar_novo_perfil() throws Throwable {
		log.info("Quando clicar em adicionar novo perfil");

		manutencaoConsignantePage.clicarBotaoNovo();
	}

	@Quando("excluir perfil com a descricao {string}")
	public void clicar_em_excluir_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando clicar em excluir perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.perfilExcluir(perfilDescricao);
	}

	@Quando("preencher a descricao, desmarcar as funcoes e salvar")
	public void preencher_descricao_desmarcar_funcao_salvar() {
		log.info("Quando preencher a descrição, desmarcar as funções e salvar");

		manutencaoConsignantePage.gravarNovoPerfilSemFuncoes(descricaoSemFuncao);
		assertFalse(manutencaoPerfilService.getPerfil(descricaoSemFuncao).isEmpty());
	}

	@Quando("preencher a descricao, marcar as funcoes e salvar")
	public void preencher_descricao_marcar_funcao_salvar() {
		log.info("Quando preencher a descrição e salvar");

		manutencaoConsignantePage.gravarNovoPerfilComFuncoesAdministrativas(descricaoComFuncao);

		assertFalse(manutencaoPerfilService.getPerfil(descricaoComFuncao).isEmpty());
	}

	@Quando("editar perfil com a descricao {string}")
	public void editar_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando editar perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.perfilEditar(perfilDescricao);
	}

	@Quando("filtrar perfil com a descricao {string}")
	public void filtrar_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando filtrar perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.filtroPerfil(perfilDescricao, "Descrição");
	}

	@Quando("clicar em editar perfil com a descricao {string}")
	public void clicar_editar_perfil(String perfilDescricao) throws Throwable {
		log.info("Quando clicar em editar perfil com a descrição {}", perfilDescricao);

		manutencaoConsignantePage.clicarEditarPerfil(perfilDescricao);
	}

	@Quando("bloquear o consignante")
	public void bloquearConsignante() {
		log.info("Quando bloquear o consignante");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarBloquear();
		manutencaoConsignantePage.preencherMotivo("Automacao");
		manutencaoConsignantePage.clicarConfirmar();
	}

	@Quando("desbloquear o consignante")
	public void desbloquearConsignante() {
		log.info("Quando desbloquear o consignante");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarDesbloquear();
		manutencaoConsignantePage.clicarConfirmar();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));
	}

	@Quando("alterar configuracoes de margem")
	public void alterarConfiguracoesMargem() {
		log.info("Quando alterar configurações de margem");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarAlterarConfiguracoesMargem();
		manutencaoConsignantePage.selecionarExibeOrgaoMargem1("Não exibe valor");
		manutencaoConsignantePage.selecionarExibeCorrespondenteMargem1("Não exibe valor");
		manutencaoConsignantePage.selecionarExibeSuporteMargem1("Exibe (zera quando for negativa)");
		manutencaoConsignantePage.preencherPorcentagemMargem2("90");
		manutencaoConsignantePage.selecionarExibeServidorMargem2("Não exibe valor");
		manutencaoConsignantePage.selecionarExibeConsignatariaMargem3("Exibe (zera quando for negativa)");
		manutencaoConsignantePage.alterarNomeMargem3("Margem Aut");
	}

	@Quando("configurar auditoria {string}")
	public void configurarAuditoria(String usuario) {
		log.info("Quando configurar auditoria {}", usuario);

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarConfigurarAuditoria();

		assertEquals("Periodicidade de envio de e-mails de auditoria: Diário", econsigHelper.getMensagemSucesso(getWebDriver()));

		if (usuario.matches("cse")) {
		    manutencaoConsignantePage.selecionarFuncoesCse();
		} else {
		    manutencaoConsignantePage.selecionarFuncoesSuporte();
		}

		manutencaoPostoPage.clicarSalvar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Atualizações salvas com sucesso.");
	}

	@Quando("clica no botao salvar alteracoes")
	public void clicarBotaoSalvar() {
		log.info("Quando clica no botão salvar alterações");

		manutencaoConsignantePage.clicarSalvarConfiguracoes();
	}

	@Quando("alterar os parametros")
	public void alterarParametros() {
		log.info("Quando alterar os parametros");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarAlterarParametros();

		manutencaoConsignantePage.alterarPrazoExpiracaoSenhaServidor("90");
		manutencaoConsignantePage.alterarQuantidadeMensagensExibidasAposLogin("8");
		manutencaoConsignantePage.marcarExigeCertificadoDigitalParaConsignataria();
	}

	@Quando("alterar o parametro exige certificado digital para consignante")
	public void alterarParametroExigeCertificadoCse() {
		log.info("Quando alterar o parametro exige certificado digital para consignante");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarAlterarParametros();

		manutencaoConsignantePage.marcarExigeCertificadoDigitalParaConsignante();
	}

	@Quando("alterar o parametro exige certificado digital para consignataria")
	public void alterarParametroExigeCertificadoCsa() {
		log.info("Quando alterar o parametro exige certificado digital para consignatária");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarAlterarParametros();

		manutencaoConsignantePage.marcarExigeCertificadoDigitalParaConsignataria();
	}

	@Quando("alterar o parametro para verificar cadastro de IP endereco de acesso no login")
	public void alterarParametroVerificaIp() {
		log.info("Quando alterar o parametro para verificar cadastro de IP endereço de acesso no login");

		acoesUsuarioPage.clicarMaisAcoes();
		manutencaoConsignantePage.clicarAlterarParametros();

		manutencaoConsignantePage.marcarVerificaCadastroIPAcessoUsuarioConsignante();
	}

	@Entao("sera exibido os perfis que contem a descricao {string}")
	public void exibe_perfil(String perfilDescricao) throws Throwable {
		log.info("Então será exibido os perfis que contém a descrição {}", perfilDescricao);

		assertTrue(getWebDriver().getPageSource().contains(perfilDescricao));
	}

	@Entao("nao sera exibido os perfis que contem outras descricoes {string}")
	public void nao_exibe_perfil(String perfilDescricao) throws Throwable {
		log.info("Então não será exibido os perfis que contém outras descrições {}", perfilDescricao);

		assertFalse(getWebDriver().getPageSource().matches(perfilDescricao));
	}

	@Entao("clicar em Cancelar")
	public void clicar_cancelar() throws Throwable {
		log.info("Então clicar em Cancelar");

		manutencaoConsignantePage.clicarCancelar();
	}

	@Entao("retorna para a tela de pesquisa")
	public void retorna_tela_pesquisa() throws Throwable {
		log.info("Entao retorna para a tela de pesquisa");

		assertTrue(manutencaoConsignantePage.verificarTelaPerfil());
	}

	@Entao("o perfil {string} e bloqueado")
	public void verificar_perfil_bloqueado_banco(String perfilDescricao) {
		log.info("Entao o perfil {} é bloqueado", perfilDescricao);

		assertEquals(0, manutencaoPerfilService.getStatusPerfilCse(perfilDescricao));
	}

	@Entao("o perfil {string} e desbloqueado")
	public void verificar_perfil_desbloqueado_banco(String perfilDescricao) {
		log.info("Entao o perfil {} é desbloqueado", perfilDescricao);

		assertEquals(1, manutencaoPerfilService.getStatusPerfilCse(perfilDescricao));
	}

	@Entao("o perfil {string} e excluido")
	public void verificar_perfil_excluido_banco(String perfilDescricao) {
		log.info("Entao o perfil {} é excluido", perfilDescricao);

		assertTrue(manutencaoPerfilService.getPerfil(perfilDescricao).isEmpty());
	}

	@Entao("exibe a mensagem {string}")
	public void verificar_mensagem_sucesso(String mensagem) throws Throwable {
		log.info("Entao exibe a mensagem {}", mensagem);

		assertEquals(mensagem, econsigHelper.getMensagemSucesso(getWebDriver()));
	}

	@Quando("editar os dados do consignante")
	public void editarDadosConsignante() {
		log.info("Quando editar os dados do consignante");

		manutencaoConsignantePage.preencherResponsavel("Carlos");
		manutencaoConsignantePage.preencherCargo("Advogado");
		manutencaoConsignantePage.preencherCEP("31710400");
	}

	@Quando("cadastrar Ip de acesso para cse")
	public void cadastrarIpAcesso() {
		log.info("Quando cadastrar Ip de acesso para cse");

		usuarioPage.incluirIPsAcessoAtualCSE();
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));
	}

	@Entao("verifica que nao consegue reservar margem")
	public void verificarNaoReservaMargem() {
		log.info("Entao verifica que não consegue reservar margem");

		// verifica o status no banco
		assertEquals("2", consignanteService.getCseAtivo());

		// reservar margem
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();

		reservarMargemPage.selecionarConsignataria("BB - 001");
		reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
		reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
		reservarMargemPage.clicarPesquisar();

		assertEquals(
				"NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS O(A) CONSIGNANTE 'CARLOTA JOAQUINA' ESTÁ BLOQUEADO.",
				econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Entao("verifica que consegue reservar margem")
	public void verificarReservaMargem() {
		log.info("Entao verifica que não consegue reservar margem");

		// verifica o status no banco
		assertEquals("1", consignanteService.getCseAtivo());

		// reservar margem
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosReservarMargem();

		reservarMargemPage.selecionarConsignataria("BB - 001");
		reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
		reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
		reservarMargemPage.clicarPesquisar();

		econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
		assertFalse(getWebDriver().getPageSource().contains(
				"NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS O(A) CONSIGNANTE 'CARLOTA JOAQUINA' ESTÁ BLOQUEADO."));
	}

	@Entao("verifica as alteracoes de margem realizadas")
	public void verificarAlteracoesMargem() {
		log.info("Quando verifica as alterações de margem realizadas");

		loginPage.loginServidor(LoginValues.servidor1);
		assertEquals("Margem Aut:", manutencaoConsignantePage.getNomeCardMargem());
	}

	@Entao("usuario {string} nao consegue logar")
	public void verificarAlteracoesMargem(String usuario) {
		log.info("Entao usuário {} não consegue logar", usuario);

		loginPage.acessarTelaLogin();

		if (usuario.contains("cse")) {
			loginPage.login(usuario, LoginValues.cse1.getSenha());
		} else {
			loginPage.login(usuario, LoginValues.csa1.getSenha());
		}

		assertEquals("NÃO É POSSÍVEL ACESSAR O SISTEMA POIS A UTILIZAÇÃO DO CERTIFICADO DIGITAL É OBRIGATÓRIA PARA SEU USUÁRIO.",
				econsigHelper.getMensagemSessaoExpirada(getWebDriver()));
	}

	@Entao("verifica que cse {string} nao consegue logar")
	public void verificarNaoLoga(String usuario) {
		log.info("Entao verifica que cse {} não consegue logar", usuario);

		// verifica o status no banco
		assertEquals("S", parametroSistemaService
				.getParamSistemaConsignante(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG).getPsiVlr());

		// logar com csa
		loginPage.acessarTelaLogin();
		loginPage.login(usuario, LoginValues.cse1.getSenha());

		assertEquals("USUÁRIO OU SUA ENTIDADE DEVE POSSUIR IPS DE ACESSO CADASTRADOS NO SISTEMA.",
				econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Entao("verifica que cse {string} consegue logar")
	public void verificarCsaLoga(String usuario) {
		log.info("Entao verifica que cse {} consegue logar", usuario);

		// verifica o status no banco
		assertEquals("S", parametroSistemaService
				.getParamSistemaConsignante(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG).getPsiVlr());

		// logar com csa
		loginPage.acessarTelaLogin();
		loginPage.login(usuario, LoginValues.cse1.getSenha());

		econsigHelper.verificaTextoPagina(getWebDriver(), "Página inicial");
		assertEquals("eConsig - Principal", getWebDriver().getTitle());
	}
}
