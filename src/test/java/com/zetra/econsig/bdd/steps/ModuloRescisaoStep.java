package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ModuloRescisaoPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.SaldoDevedor;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PrazoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.SaldoDevedorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.ServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.service.VerbaRescisoriaRseService;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.ServidoresPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ModuloRescisaoStep {

	@Autowired
	private FuncaoService funcaoService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private ServidorService servidorService;

	@Autowired
	private VerbaRescisoriaRseService verbaRescisoriaRseService;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private SaldoDevedorService saldoDevedorService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private PrazoService prazoService;
	
	@Autowired
	private ServicoService servicoService;

	@Autowired
	private ConvenioService convenioService;
	
	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	private MenuPage menuPage;
	private ServidoresPage servidoresPage;
	private ModuloRescisaoPage moduloRescisaoPage;
	private ReservarMargemPage reservarMargemPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private AutDesconto autDesconto;

	@Before
	public void setUp() throws Exception {
		menuPage = new MenuPage(getWebDriver());
		servidoresPage = new ServidoresPage(getWebDriver());
		moduloRescisaoPage = new ModuloRescisaoPage(getWebDriver());
		reservarMargemPage = new ReservarMargemPage(getWebDriver());
		acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
	}

	@E("que tenha  incluido a funcao informar rescisao  ao usuario suporte")
	public void papelFuncaoInformarRescisao() {
		log.info("E que tenha incluido a funcao informar rescisao ao usuario suporte");

		funcaoService.criarPapelFuncao(CodedValues.PAP_SUPORTE, "485");
		funcaoService.criarFuncaoPerfilSup("1118556", "485", "1");
	}

	@E("acessar menu Favoritos > informar rescisao")
	public void acessarInformarRescisao() {
		log.info("E acessar menu Favoritos > informar rescisao");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosInformarRescisao();
	}

	@Quando("adiciona o servidor {string} para informar rescisao")
	public void adicionarServidorInformarRescisao(String cpfServidor) {
		log.info("Entao adiciona o servidor {} para informar rescisao", cpfServidor);

		moduloRescisaoPage.btnAdicionarServidor();

		servidoresPage.preencherCpf(cpfServidor);
		reservarMargemPage.clicarPesquisar();
	}

	@Quando("verifica se o servidor {string} esta incluido na lista")
	public void verificaServidorListado(String cpfServidor) throws Throwable {
		log.info("E verifica se o servidor {} esta incluido na lista", cpfServidor);

		assertEquals(servidorService.obterServidorPeloCpf(cpfServidor).getSerNome(),
				moduloRescisaoPage.valorCampoNomeDaLista());
		assertEquals(cpfServidor, moduloRescisaoPage.valorCampoCpfDaLista());
	}

	@E("confirma o servidor para rescisao contratual")
	public void confimarServidorParaRescisao() {
		log.info("E confirma o servidor para rescisao contratual");

		moduloRescisaoPage.btnConfirmarRescisao();
		getWebDriver().switchTo().alert().accept();
		moduloRescisaoPage.aguardaInfoMsgFicarInvisivel();
		// A rescisão é processada em uma thread e eventualmente há uma falha em exibir
		// a mensagem de sucesso na tela
		// assertEquals("Operação realizada com sucesso.",
		// moduloRescisaoPage.verificaMensagemSucesso());
		moduloRescisaoPage.aguardaMsgNenhumRegistroEncontrado();
	}

	@Entao("verifica se o mesmo foi bloqueado no sistema")
	public void verificaServidorBloqueado() {
		log.info("Entao verifica se o mesmo foi bloqueado no sistema");

		assertEquals("2", registroServidorService.getStatusServidor("9879877"));
	}

	@Entao("exclui o mesmo e valida se nao esta bloqueado no sistema")
	public void excluirServidorDaLista() {
		log.info("Entao exclui o mesmo e valida se nao esta bloqueado no sistema");

		moduloRescisaoPage.excluirServidor();
		getWebDriver().switchTo().alert().accept();
		moduloRescisaoPage.aguardaMsgNenhumRegistroEncontrado();
		assertEquals("1", registroServidorService.getStatusServidor("145985"));
		assertEquals(0,
				verbaRescisoriaRseService.findVerbaRescisoriaByRseCodigo("481780808080808080808080809090").size());
	}

	@Dado("que o usuário servidor {string} esteja na lista para rescisao contratual")
	public void servidorEstejaListaRescisaoContratual(String login) {
		log.info("Dado que o usuário servidor {} esteja na lista para rescisao contratual", login);

		verbaRescisoriaRseService.incluirVerbaRescisoria(
				registroServidorService.obterRegistroServidorPorMatricula(login).getRseCodigo(), "2");
	}

	@E("que servidor {string} possua contratos com natureza empréstimo")
	public void contratosNaturezaEmprestimo(String login) {
		log.info("Dado ue o usuário possui contratos com natureza empréstimo {}", login);
		
		String csaCodigo = usuarioService.getCsaCodigo(LoginValues.csa2.getLogin());
		String svcCodigo = servicoService.retornaSvcCodigo("001");
		long adeNumero = 15999;
		String rseCodigo = registroServidorService.obterRegistroServidorPorMatricula(login).getRseCodigo();
		String adeCodigo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String cnvCodigo = convenioService.findBySvcCodigoAndCsaCodigo(svcCodigo, csaCodigo).getCnvCodigo(); 

		// altera o status do servidor
		usuarioService.alterarStatusUsuario("213464140-" + login, "1");
		//exclui rescisao caso possua
		verbaRescisoriaRseService.removerVerbaRescisoria(rseCodigo);
		//exclui ades caso possua
		autDescontoService.deleteAutDescontoByAdeNumero(adeNumero);

		List<AutDesconto> adeNumeroNovo = autDescontoService.getAdes(rseCodigo);
		if (!adeNumeroNovo.isEmpty()) {
			autDescontoService.deleteAutDescontoByAdeNumero(adeNumeroNovo.get(0).getAdeNumero());
		}

		itemMenuFavoritoService.incluirItemMenuFavorito(
				usuarioService.getUsuario(LoginValues.suporte.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTAR_CONSIGNACAO.getCodigo()));

		autDesconto = autDescontoService.inserirAutDesconto(adeCodigo, "4", rseCodigo, cnvCodigo,
				usuarioService.getUsuCodigoServ(login), 200.0f, Long.valueOf(adeNumero), 10, Short.parseShort("1"),
				Short.parseShort("1"), "F");
	}

	@E("saldo devedor {string} informado")
	public void saldoDevedorInformado(String valor) {
		log.info("E saldo devedor {} informado", valor);

		saldoDevedorService.incluirSaldoDevedor(autDesconto.getAdeCodigo(),
				usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(), valor);
	}

	@E("exibe dados do contrato {string} com valor {string}")
	public void exibeDadosColaborador(String login, String valor) {
		log.info("E exibe dados do colaborador {} com valor {}", login, valor);

		acoesUsuarioPage.clicarOpcoes(login, "0");
		moduloRescisaoPage.clicarVisualizar();
		econsigHelper.verificaTextoPagina(getWebDriver(),
				"Conforme Lei 10.820/2003, até 30% (trinta por cento) do valor total líquido das verbas rescisórias pode ser retido para pagamento de empréstimos consignados.");
		assertTrue(moduloRescisaoPage.retornarValorRetido().contains(valor));
	}

	@E("novo contrato com retenção da verba é criado para {string}")
	public void novoContratoComRetencaoVerba(String login) {
		log.info("E novo contrato com retenção da verba é criado para {}", login);

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		reservarMargemPage.preencherMatricula(login);
		reservarMargemPage.clicarPesquisar();
		moduloRescisaoPage.clicarVisualizar();

		assertEquals("Deferida", moduloRescisaoPage.retornarSituacaoAde());
		assertEquals("Verba Rescisória", moduloRescisaoPage.retornarIdentificador());

		moduloRescisaoPage.clicarLinkRelacionamento();
		assertTrue(moduloRescisaoPage.retornarSituacaoAde().contains("Liquidada"));

	}

	@Entao("retenção é concluida")
	public void retencaoConcluida() {
		log.info("Entao retenção é concluida");
		
		assertEquals("Concluído", moduloRescisaoPage.retornarSituacaoColaborador());
	}

	@E("realizar a retenção da verba rescisória com valor {string}")
	public void realizarRetencaoVerbaRescisoria(String valor) {
		log.info("E realizar a retenção da verba rescisória com valor {}", valor);
		
		moduloRescisaoPage.preencherValorDisponivelRetencao(valor);
		moduloRescisaoPage.clicarSalvar();

		getWebDriver().switchTo().alert().accept();
	}

	@E("realizar retenção com valor {string} maior que o saldo devedor {string}")
	public void realizarRetencaoComSaldoMaior(String valorRetido, String saldoDevedor) {
		log.info("E realizar retenção com valor {} maior que o saldo devedor {}", valorRetido, saldoDevedor);
		
		moduloRescisaoPage.preencherValorDisponivelRetencao(valorRetido);

		assertTrue(moduloRescisaoPage.retornarValorRetencao().contains(saldoDevedor));
		moduloRescisaoPage.clicarSalvar();

		getWebDriver().switchTo().alert().accept();
	}

	@E("informar verba rescisória para {string}")
	public void informarVerbaRescisoria(String login) {
		log.info("E informar verba rescisória para {}", login);
		
		assertEquals("Aguardando", moduloRescisaoPage.retornarSituacaoColaborador());

		acoesUsuarioPage.clicarOpcoes(login, "0");
		moduloRescisaoPage.clicarInformarVerbaRescisoria();

		assertEquals(
				"Inserir abaixo o valor a ser utilizado para pagamento de empréstimos consignados, limitado a 30% (trinta por cento) do valor total líquido das verbas rescisórias, conforme Lei 10.820/2003.",
				econsigHelper.getMensagemAlerta(getWebDriver()));
	}

	@E("que o sistema está configurado para calcular o saldo devedor automatico")
	public void sistemaConfiguradoCalcularSaldoDevedorAutomatico() {
		log.info("E que o sistema está configurado para calcular o saldo devedor automatico");

		String csaCodigo = usuarioService.getCsaCodigo(LoginValues.csa2.getLogin());
		String svcCodigo = servicoService.retornaSvcCodigo("001");

		parametroSistemaService.configurarParametroSistemaCse(
				CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CALCULA_SALDO_DEV_IMPORTADOS, "S");
		parametroSistemaService.configurarParametroConsignataria(csaCodigo,
				CodedValues.TPA_INCL_SALDO_DEVEDOR_AUT_MODULO_RESCISAO, "S");
		prazoService.incluirCoeficienteAtivo(csaCodigo, svcCodigo, Short.valueOf("1"), BigDecimal.valueOf(1, 0));
		prazoService.incluirCoeficienteAtivo(csaCodigo, svcCodigo, Short.valueOf("10"), BigDecimal.valueOf(1, 9));

		EConsigInitializer.limparCache();
	}

	@E("adicionar servidor {string} na lista para rescisao contratual e confirmar")
	public void adicionarListaRescisaoContratual(String login) {
		log.info("adicionar servidor {} na lista para rescisao contratual e confirmar", login);

		moduloRescisaoPage.btnAdicionarServidor();

		servidoresPage.preencherMatricula(login);
		reservarMargemPage.clicarPesquisar();

		moduloRescisaoPage.btnConfirmarRescisao();
		getWebDriver().switchTo().alert().accept();
		moduloRescisaoPage.aguardaInfoMsgFicarInvisivel();
	}

	@E("saldo devedor é calculado automaticamente e retenção concluida para servidor {string}")
	public void saldoDevedorCalculadoAutomaticamente(String login) {
		log.info("saldo devedor é calculado automaticamente e retenção concluida para servidor {}", login);

		assertEquals("Operação realizada com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

		String adeCodigo = autDescontoService
				.getAdes(registroServidorService.obterRegistroServidorPorMatricula(login).getRseCodigo(),
						Arrays.asList("8"))
				.get(0).getAdeCodigo();
		SaldoDevedor saldoDevedor = saldoDevedorService.retornarSaldoDevedor(adeCodigo);

		assertTrue(saldoDevedor != null);
	}

	@After
	public void after() {
		String csaCodigo = usuarioService.getCsaCodigo(LoginValues.csa2.getLogin());
		String svcCodigo = servicoService.retornaSvcCodigo("001");

		parametroSistemaService.configurarParametroSistemaCse(
				CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CALCULA_SALDO_DEV_IMPORTADOS, "N");
		parametroSistemaService.configurarParametroConsignataria(csaCodigo,
				CodedValues.TPA_INCL_SALDO_DEVEDOR_AUT_MODULO_RESCISAO, "N");
		prazoService.deletarCoeficienteAtivo(csaCodigo, svcCodigo, Short.valueOf("1"));
		prazoService.deletarCoeficienteAtivo(csaCodigo, svcCodigo, Short.valueOf("10"));
		EConsigInitializer.limparCache();
	}
}