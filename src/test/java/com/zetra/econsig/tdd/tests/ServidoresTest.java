package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.service.CampoSistemaService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.ServidoresPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServidoresTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private UsuarioPage usuarioPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private ServidoresPage servidoresPage;
    private BeneficiariosPage beneficiariosPage;
    private ReservarMargemPage reservarMargemPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginSup = LoginValues.suporte;
	private final LoginInfo loginSer = LoginValues.servidor1;
	private String serCodigo;
	private RegistroServidor registroServidor;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private CampoSistemaService campoSistemaService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private ServicoService servicoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        usuarioPage = new UsuarioPage(webDriver);
        acoesUsuarioPage = new AcoesUsuarioPage(webDriver);
        servidoresPage = new ServidoresPage(webDriver);
        beneficiariosPage = new BeneficiariosPage(webDriver);
        reservarMargemPage = new ReservarMargemPage(webDriver);

		serCodigo = usuarioService.getSerCodigo(loginSer.getLogin());
		registroServidor = registroServidorService.getRegistroServidor(serCodigo);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.SERVIDORES.getCodigo()));
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginSup.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.SERVIDORES.getCodigo()));
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.INCLUIR_CONSIGNACAO.getCodigo()));

		parametroSistemaService.configurarParametroSvcRegistroSer(registroServidor.getRseCodigo(),
				servicoService.retornaSvcCodigo("001"), CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "");
		parametroSistemaService.configurarParametroNseRegistroSer(registroServidor.getRseCodigo(),
				CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "");
		parametroSistemaService.configurarParametroCnvRegistroSer(registroServidor.getRseCodigo(),
				"751F8080808080808080808080809Z85", CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, "S");
		EConsigInitializer.limparCache();
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void criarServidorLogadoComConsignante() {
		log.info("Criar Servidor logado com consignante");

		String matricula = "127583";
		String cpf = "791.414.410-01";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.preencherMatricula(matricula);
		servidoresPage.preencherCpf(cpf);
		servidoresPage.clicarPesquisar();

		assertEquals("Nenhum registro encontrado para a pesquisa:\n" + "Matrícula: " + matricula + " CPF: " + cpf + "",
				econsigHelper.getMensagemErro(webDriver));
		assertEquals(
				"Nenhum servidor encontrado para os dados informados. Caso queira cadastrá-lo, informe os dados do cadastro abaixo.",
				econsigHelper.getMensagemInformacao(webDriver));

		servidoresPage.criarServidor(matricula, cpf);
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Servidor cadastrado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarCriarServidorLogadoComSuporte() {
		log.info("Tentar criar Servidor logado com suporte");

		String matricula = "127584";
		String cpf = "370.177.520-66";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.preencherMatricula(matricula);
		servidoresPage.preencherCpf(cpf);
		servidoresPage.clicarPesquisar();

		assertEquals("Nenhum registro encontrado para a pesquisa:\n" + "Matrícula: " + matricula + " CPF: " + cpf + "",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void consultarServidorComSucesso() {
		log.info("Consultar Servidor com sucesso");
		// alterar tabela tb_campo_sistema, o CAS_CHAVE
		// edtRegistroServidor_margem_limite_desconto_folha para S
		campoSistemaService.alterarCampoSistema("cse.edtRegistroServidor_margem_limite_desconto_folha", "S");
		// chama o limpa cache
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");

		assertEquals(loginSer.getLogin(), servidoresPage.getMatricula());
		assertEquals("092.459.399-79", servidoresPage.getCPF());
		assertEquals("Sr. BOB da Silva Shawn", servidoresPage.getNomeCompleto());
	}

	@Test
	public void consultarServidorComCampoRegistroServidorMargemLimiteDescontoFolhaIgualN() {
		log.info("Consultar Servidor com campo registro servidor margem limite desconto folha igual N");
		// alterar tabela tb_campo_sistema, o CAS_CHAVE
		// edtRegistroServidor_margem_limite_desconto_folha para N
		campoSistemaService.alterarCampoSistema("cse.edtRegistroServidor_margem_limite_desconto_folha", "N");
		// chama o limpa cache
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");

		assertEquals(loginSer.getLogin(), servidoresPage.getMatricula());
		assertEquals("092.459.399-79", servidoresPage.getCPF());
		assertEquals("Sr. BOB da Silva Shawn", servidoresPage.getNomeCompleto());
	}

	@Test
	public void consultarServidorInexistente() {
		log.info("Consultar Servidor Inexistente");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.preencherMatricula("145698");
		servidoresPage.clicarPesquisar();

		assertTrue(econsigHelper.getMensagemErro(webDriver).contains("Nenhum registro encontrado para a pesquisa:"));
		assertEquals(
				"Nenhum servidor encontrado para os dados informados. Caso queira cadastrá-lo, informe os dados do cadastro abaixo.",
				econsigHelper.getMensagemInformacao(webDriver));
	}

	@Test
	public void consultarServidorSemInformarCamposObrigatorios() {
		log.info("Consultar Servidor sem informar campos obrigatorios");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		// nao preenche matricula
		servidoresPage.clicarPerquisarSemInformarCampos();

		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void acaoExibirOcorrenciaDesteServidor() {
		log.info("Ação Exibir ocorrencia deste Servidor");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoExibirOcorrencia();

		econsigHelper.verificaTextoPagina(webDriver, "Alteração de dados cadastrais");
		assertTrue(webDriver.getPageSource().contains("DADOS CADASTRAIS FORAM ALTERADOS:"));
	}

	@Test
	public void acaoBloquearServicosDesteServidor() {
		log.info("Ação Bloquear os serviços deste servidor");
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoBloquearServicos();

		// bloquear servico
		servidoresPage.preencherServico("1");
		usuarioPage.selecionarMotivoOperacao("Sem motivo");
		usuarioPage.preencherObservacao("Bloquear servico");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Deseja bloquear este(s) serviço(s)?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verificar que não é possivel incluir consignação
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);
		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosIncluirConsignacao();

		// tentar incluir consignação
		reservarMargemPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
		reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
		reservarMargemPage.clicarPesquisar();

		assertEquals(
				"NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS DO SERVIDOR PARA ESTE SERVIÇO.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void acaoBloquearServicosDesteServidorPorNatureza() {
		log.info("Ação Bloquear os serviços deste servidor por natureza");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoBloquearServicosPorNatureza();

		// bloquear servico
		servidoresPage.preencherServicoPorNatureza("1");
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Bloquear servico");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Deseja bloquear este(s) serviço(s)?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verificar que não é possivel incluir consignação
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);
		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosIncluirConsignacao();

		// tentar incluir consignação
		reservarMargemPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
		reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
		reservarMargemPage.clicarPesquisar();

		assertEquals(
				"NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS DO SERVIDOR PARA ESTA NATUREZA DE SERVIÇO.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void acaoBloquearVerbasDesteServidor() {
		log.info("Ação Bloquear as verbas deste servidor");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoBloquearVerbas();

		// bloquear verba
		servidoresPage.preencherConvenioEmprestimo("1");
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Bloquear servico");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Deseja confirmar o bloqueio de verba selecionado?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verificar que não é possivel incluir consignação
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);
		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosIncluirConsignacao();

		// tentar incluir consignação
		reservarMargemPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
		reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
		reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
		reservarMargemPage.clicarPesquisar();

		assertEquals(
				"NÃO É POSSÍVEL INSERIR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS PARA A MESMA MATRÍCULA E CONSIGNATÁRIA/VERBA",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void acaoTransferirValoresEntreMargensDesteServidor() {
		log.info("Ação Transferir valores entre margens deste servidor");

		BigDecimal margem1 = registroServidor.getRseMargemRest();
		BigDecimal margem2 = registroServidor.getRseMargemRest2();
		String valorTransferencia = "10";

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoTransferirValoresEntreMargens();

		// bloquear servico
		servidoresPage.marcarTransferenciaParcial();
		servidoresPage.preencherValorTransferencia(valorTransferencia);
		servidoresPage.clicarConcluir();

		assertEquals("Deseja realmente transferir a margem?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Transferência de valores entre margens realizada com sucesso.",
				econsigHelper.getMensagemSucesso(webDriver));

		// comparar se a margem foi alterada

		BigDecimal margemAtual1 = registroServidorService.getRegistroServidor(serCodigo).getRseMargemRest();
		BigDecimal margemAtual2 = registroServidorService.getRegistroServidor(serCodigo).getRseMargemRest2();

		assertEquals(margemAtual1, margem1.subtract(new BigDecimal(valorTransferencia)));
		assertEquals(margemAtual2, margem2.add(new BigDecimal(valorTransferencia)));
	}

	@Test
	public void acaoConsultarContrachequesDesteServidor() {
		log.info("Ação Consultar contracheques deste servidor");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoConsultarContracheques();

		assertTrue(webDriver.getPageSource()
				.contains("O contracheque deste período não está disponível."));
	}

	@Test
	public void acaoSolicitarSaldoDevedorTodasConsignatarias() {
		log.info("Ação Solicitar o saldo devedor a todas consignatárias.");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoSolicitarSaldoDevedor();

		assertEquals("Deseja solicitar para a todas consignatárias o saldo devedor deste servidor?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("O serviço desta consignação não permite cadastro de saldo devedor.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarServidorLogadoComConsignante() {
		log.info("Editar Servidor logado com consignante");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.editarServidor();
		acoesUsuarioPage.clicarSalvar();

		// confirmar alteracao
		assertEquals("Confirma a alteração do cadastro?", econsigHelper.getMensagemPopUp(webDriver));
		// verifica mensagem sucesso
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void editarServidorLogadoComSuporte() {
		log.info("Editar Servidor logado com Suporte");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");
		servidoresPage.editarServidor();
		acoesUsuarioPage.clicarSalvar();

		// confirmar alteracao
		assertEquals("Confirma a alteração do cadastro?", econsigHelper.getMensagemPopUp(webDriver));
		// verifica mensagem sucesso
		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarEditarCPFServidor() {
		log.info("Tentar editar CPF do Servidor");
		// alterar parametro 187 para não permitir alterar cpf do servidor
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_EDITAR_CPF_SERVIDOR, "N");
		EConsigInitializer.limparCache();

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor(loginSer.getLogin(), "092.459.399-79");

		assertFalse(servidoresPage.isCampoCPFHabilitado());
	}

	@Test
	public void acaoEditarEnderecosDesteServidor() {
		log.info("Ação editar endereços deste servidor");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritoServidores();

		servidoresPage.consultarServidor("181818", "956.052.586-72");
		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoEditarEnderecosDesteServidor();

		beneficiariosPage.clicarCadastrarNovoEndereco();
		beneficiariosPage.preencherCEP("31.710-400");
		beneficiariosPage.preencherLogradouro("Teste acaoEditarEnderecosDesteServidor");
		beneficiariosPage.preencherUF("Minas Gerais");
		beneficiariosPage.preencherBairro("teste");
		beneficiariosPage.preencherCidade("Belo Horizonte");
		beneficiariosPage.preencherNumero("542");
		beneficiariosPage.preencherComplemento("B");
		beneficiariosPage.clicarSalvar();

		assertEquals("Endereço do servidor criado com sucesso", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void exibirOcorrenciaDesteServidorLogadoComPapelServidor() {
		log.info("Exibir ocorrencia deste servidor logado com papel servidor");
		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoExibirOcorrencia();

		econsigHelper.verificaTextoPagina(webDriver, "Alteração de dados cadastrais");
		assertTrue(webDriver.getPageSource().contains("DADOS CADASTRAIS FORAM ALTERADOS:"));
	}

	@Test
	public void consultarOsServicosDesteServidorLogadoComPapelServidor() {
		log.info("Consultar os serviços deste servidor logado com papel servidor");
		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoConsultarServicos();

		econsigHelper.verificaTextoPagina(webDriver, "Lista de serviços");
		assertTrue(webDriver.getPageSource().contains("PLANO DE SAUDE SUBSIDIO DEP 1 MEDICO"));
		assertTrue(webDriver.getPageSource().contains("CARTAO DE CREDITO - RESERVA"));
		assertTrue(webDriver.getPageSource().contains("COMPRA PARA EMPRÉSTIMO"));
		assertTrue(webDriver.getPageSource().contains("COMPRA PARA EMPRÉSTIMO II"));
		assertTrue(webDriver.getPageSource().contains("COPARTICIPAÇÃO DEPENDENTE 1"));
		assertTrue(webDriver.getPageSource().contains("COPARTICIPAÇÃO TITULAR"));
		assertTrue(
				webDriver.getPageSource().contains("CRÉDITO PRO RATA PLANO DE SAÚDE DEPENDENTE 1"));
		assertTrue(webDriver.getPageSource().contains("EMPRÉSTIMO"));
		assertTrue(webDriver.getPageSource().contains("EMPRESTIMO ALONGADO"));
		assertTrue(webDriver.getPageSource().contains("EMPRESTIMO MARGEM 3"));
	}

	@Test
	public void consultarOsServicosDesteServidorPorNaturezaLogadoComPapelServidor() {
		log.info("Consultar os serviços deste servidor por natureza logado com papel servidor");
		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoConsultarServicosPorNatureza();

		econsigHelper.verificaTextoPagina(webDriver, "Lista de serviços");
		assertTrue(webDriver.getPageSource().contains("AUXÍLIO FINANCEIRO"));
		assertTrue(webDriver.getPageSource().contains("CARTÃO"));
		assertTrue(webDriver.getPageSource().contains("COMPULSORIO"));
		assertTrue(webDriver.getPageSource().contains("EMPRÉSTIMO"));
		assertTrue(webDriver.getPageSource().contains("FINANCIAMENTO"));
		assertTrue(webDriver.getPageSource().contains("MENSALIDADE"));
		assertTrue(webDriver.getPageSource().contains("OUTROS"));
		assertTrue(webDriver.getPageSource().contains("PECÚLIO"));
		assertTrue(webDriver.getPageSource().contains("PLANO DE SAÚDE"));
		assertTrue(webDriver.getPageSource().contains("PLANO ODONTOLÓGICO"));
	}

	@Test
	public void consultarAsVerbasDesteServidorLogadoComPapelServidor() {
		log.info("Consultar as verbas deste servidor logado com papel servidor");
		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoConsultarVerbas();

		econsigHelper.verificaTextoPagina(webDriver, "Lista de convênios");
		assertTrue(webDriver.getPageSource().contains("FINANCIAMENTO DE DÍVIDA"));
		assertTrue(webDriver.getPageSource().contains("EMPRÉSTIMO"));
		assertTrue(webDriver.getPageSource().contains("COPARTICIPAÇÃO TITULAR"));
		assertTrue(webDriver.getPageSource().contains("PLANO DE SAÚDE DEPENDENTE 4"));
		assertTrue(webDriver.getPageSource().contains("COPARTICIPAÇÃO DEPENDENTE 1"));
		assertTrue(webDriver.getPageSource().contains("UNIMED BH"));
	}

	@Test
	public void transferirValoresEntreMargensDesteServidorLogadoComPapelServidor() {
		log.info("Transferir valores entre margens deste servidor logado com papel servidor");

		BigDecimal margem1 = registroServidor.getRseMargemRest();
		BigDecimal margem2 = registroServidor.getRseMargemRest2();
		String valorTransferencia = "15";

		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoTransferirValoresEntreMargens();

		// bloquear servico
		servidoresPage.marcarTransferenciaParcial();
		servidoresPage.preencherValorTransferencia(valorTransferencia);
		servidoresPage.marcarTermoTransferencia();
		servidoresPage.clicarConcluir();

		assertEquals("Deseja realmente transferir a margem?", econsigHelper.getMensagemPopUp(webDriver));
		assertEquals("Transferência de valores entre margens realizada com sucesso.",
				econsigHelper.getMensagemSucesso(webDriver));

		// comparar se a margem foi alterada
		BigDecimal margemAtual1 = registroServidorService.getRegistroServidor(serCodigo).getRseMargemRest();
		BigDecimal margemAtual2 = registroServidorService.getRegistroServidor(serCodigo).getRseMargemRest2();

		assertEquals(margemAtual1, margem1.subtract(new BigDecimal(valorTransferencia)));
		assertEquals(margemAtual2, margem2.add(new BigDecimal(valorTransferencia)));
	}

	@Test
	public void tentarTransferirValoresEntreMargensDesteServidorSemSelecionarTermoDeTransferencia() {
		log.info("Tentar transferir valores entre margens deste servidor sem selecionar termo de transferencia");

		String valorTransferencia = "15";

		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoTransferirValoresEntreMargens();

		// bloquear servico
		servidoresPage.marcarTransferenciaParcial();
		servidoresPage.preencherValorTransferencia(valorTransferencia);
		servidoresPage.clicarConcluir();

		assertEquals("Deseja realmente transferir a margem?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("É necessário concordar com os termos da transferência para realizar a operação.",
				econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void consultarContrachequesDesteServidorLogadoComPapelServidor() {
		log.info("Consultar contracheques deste servidor logado com papel servidor");
		// loga no sistema
		loginPage.loginServidor(loginSer);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuDadosCadastrais();

		servidoresPage.clicarMaisAcoes();
		servidoresPage.clicarAcaoConsultarContracheques();

		assertTrue(webDriver.getPageSource()
				.contains("O contracheque deste período não está disponível."));
	}
}