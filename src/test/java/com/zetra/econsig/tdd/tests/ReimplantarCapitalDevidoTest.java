package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.MargemService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.ServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.ReimplantarCapitalDevidoPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

public class ReimplantarCapitalDevidoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ReimplantarCapitalDevidoPage reimplantarCapitalDevidoPage;
	private final LoginInfo loginCse = LoginValues.cse1;
	private String usuCodigoLogado = null;

	// declaração do usuário servidor
	private final String serLogin = "cap";
	private final String serNome = "Yuri Alberto";
	private final String serCpf = "792.144.790-20";
	private final String serStatus = "1";
	private final String orgCodigo = "751F8080808080808080808080809780"; // órgão Carlota Joaquina
	private final String srsCodigo = "1"; // status "ativo"
	private final String matricula = "159753";
	private String usuCodigo = null;
	private Servidor servidor = null;
	private RegistroServidor registroServidor = null;

	// declaração do convênio
	private final String cnvCodigo = "260C8089998080808089008080D187";

	// declaração da consignação
	private final String adeCodigo = "1A";
	private final String sadCodigo = "14"; // status "em carência"
	private final float adeVlr = 1000f;
	private final long adeNumero = 10L;
	private final int adePrazo = 20;
	private final short adeIncMargem = 3;
	private final short adeIntFolha = 1;
	private final String adeTipoVlr = "F";

	// declaração da parcela
	private final String spdCodigo = "1";
	private final BigDecimal prdVlrPrevisto = BigDecimal.valueOf(1000);
	private final BigDecimal prdVlrRealizado = BigDecimal.valueOf(500);
	private final String prdNumero = "1"; // status "em aberto"

	// declaração da margem
	private final String marCodigo = "3";
	private final String marTipoVlr = "F";
	private final BigDecimal margemVlr = BigDecimal.valueOf(1000);
	private final BigDecimal margemUsada = BigDecimal.ZERO;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private ServidorService servidorService;

	@Autowired
	private MargemService margemService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

    @BeforeEach
    public void beforeEach() throws Exception {
    	super.setUp();
    	// criação das páginas
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
        usuCodigoLogado = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
        reimplantarCapitalDevidoPage = new ReimplantarCapitalDevidoPage(webDriver);

        // inserção da página de reimplantar capital devido nos favoritos para fácil acesso
        itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuCodigoLogado,
				Integer.toString(ItemMenuEnum.REIMPLANTAR_CAPITAL_DEVIDO.getCodigo()));

		// inclusão do usuário servidor
		usuCodigo = usuarioService.criarUsuario(serLogin, serStatus, serCpf);
		servidor = servidorService.incluirServidor(serNome, serCpf);
		registroServidor = registroServidorService.incluirRegistroServidor(servidor.getSerCodigo(), orgCodigo, srsCodigo, matricula, margemVlr, margemVlr, margemUsada);

		// inclusão da margem
		margemService.incluirMargem(marCodigo, marTipoVlr);
		margemService.incluirMargemRegistroServidor(marCodigo, registroServidor.getRseCodigo(), margemVlr, margemUsada);

		// inclusão da consignação
		autDescontoService.inserirAutDesconto(adeCodigo, sadCodigo, registroServidor.getRseCodigo(), cnvCodigo, usuCodigo, adeVlr, adeNumero, adePrazo, adeIncMargem, adeIntFolha, adeTipoVlr);

		// inclusão da parcela
		autDescontoService.incluirParcelaDesconto(adeCodigo, spdCodigo, prdVlrPrevisto, prdVlrRealizado, prdNumero);
    }

    @AfterEach
    public void afterEach() throws Exception {
    	super.tearDown();
    	// só é criado pela reimplantação do capital devido
    	if(relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero) != null) {
    		relacionamentoAutorizacaoService.excluirRelacionamentoAdesPorAdeNumero(adeNumero);
    	}
    	// deleção das inserções no banco
    	autDescontoService.excluirParcelaDesconto(adeCodigo);
    	registroServidorService.excluirRegistroServidor(registroServidor.getRseCodigo());
    	servidorService.excluirServidor(servidor.getSerCodigo());
    	autDescontoService.deleteAutDesconto(adeCodigo);
    }

    @Test
    public void reimplantarCapitalDevidoComSucesso() {
    	// declaração dos valores alterados na operação
    	String adeVlr = "500";
    	String adePrazo = "1";
    	String motivoDaOp = "Outro";
    	String adeObs = "teste";

    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação
    	reimplantarCapitalDevidoPage.preencherADE(String.valueOf(adeNumero));
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// seleciona a consignação para operação
    	reimplantarCapitalDevidoPage.clicarReimplantarCapitalDevido();

    	// faz a reimplantação do capital devido
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, adePrazo, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();
    	reimplantarCapitalDevidoPage.clicarOkAlerta();

    	// confere se a operação teve um retorno positivo
		assertEquals("Capital devido reimplantado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// verifica se na tb_ocorrencia_autorizacao a consignação tem toc_codigo = 236
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_PARCELAS_REINSERIDAS,
				autDescontoService.getAde(String.valueOf(adeNumero)).getAdeCodigo(), adeObs));

		// verifica se na tb_relacionamento_autorizacao a consignação tem tnt_codigo = 21
		assertEquals(relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getTntCodigo(), "21");
    }

    @Test
    public void buscarConsignacaoComCapitalDevidoPeloCpfComSucesso() {
    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação
    	reimplantarCapitalDevidoPage.preencherCPF(serCpf);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// verifica se a consignação está na página
    	assertTrue(webDriver.getPageSource().contains(serLogin));
		assertTrue(webDriver.getPageSource().contains(String.valueOf(adeNumero)));
    }

    @Test
    public void buscarConsignacaoComCapitalDevidoPelaMatriculaComSucesso() {
    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação
    	reimplantarCapitalDevidoPage.preencherMatricula(matricula);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// verifica se a consignação está na página
    	assertTrue(webDriver.getPageSource().contains(serLogin));
		assertTrue(webDriver.getPageSource().contains(String.valueOf(adeNumero)));
    }

    @Test
    public void testeDosCamposDeBuscaPorAdeDeConsignacaoComCapitalDevido() {
    	// declaração de variáveis que não deveriam ser aceitas na busca
    	String valorNegativo = "-11";
    	String letras = "abcdef";

    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação com valor negativo
    	reimplantarCapitalDevidoPage.preencherADE(valorNegativo);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("Nenhum registro encontrado para a pesquisa:\nNº ADE: 11", econsigHelper.getMensagemErro(webDriver));

    	// busca a consignação com letras
    	reimplantarCapitalDevidoPage.preencherADE(letras);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
    }

    @Test
    public void testeDosCamposDeBuscaPorMatriculaDeConsignacaoComCapitalDevido() {
    	// declaração de variáveis que não deveriam ser aceitas na busca
    	String matriculaCurta = "111";
    	String valorNegativo = "-111111";
    	String letras = "abcdef";

    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação com valor negativo
    	reimplantarCapitalDevidoPage.preencherMatricula(valorNegativo);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("Nenhum registro encontrado para a pesquisa:\nMatrícula: 111111", econsigHelper.getMensagemErro(webDriver));

    	// busca a consignação com letras
    	reimplantarCapitalDevidoPage.preencherMatricula(letras);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
		reimplantarCapitalDevidoPage.fecharPopUpMatricula();

		// busca a consignação com matricula curta
    	reimplantarCapitalDevidoPage.preencherMatricula(matriculaCurta);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("Preencha matrícula corretamente. Informe pelo menos 6 dígitos.", econsigHelper.getMensagemPopUp(webDriver));
    }

    @Test
    public void testeDosCamposDeBuscaPorCpfDeConsignacaoComCapitalDevido() {
    	// declaração de variáveis que não deveriam ser aceitas na busca
    	String cpfCurto = "111";
    	String valorNegativo = "-11111111111";
    	String letras = "abcdef";

    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação com valor negativo
    	reimplantarCapitalDevidoPage.preencherCPF(valorNegativo);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("Nenhum registro encontrado para a pesquisa:\nCPF: 111.111.111-11", econsigHelper.getMensagemErro(webDriver));

    	// busca a consignação com letras
    	reimplantarCapitalDevidoPage.preencherCPF(letras);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
		reimplantarCapitalDevidoPage.fecharPopUpMatricula();

		// busca a consignação com matricula curta
    	reimplantarCapitalDevidoPage.preencherCPF(cpfCurto);
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// confere a mensagem de erro
		assertEquals("Número de CPF inválido.", econsigHelper.getMensagemPopUp(webDriver));
    }

    @Test
    public void testeDosCamposDeReimplentacaoDeCapitalDevido() {
    	// declaração dos valores corretos
    	String adeVlr = "500";
    	String adePrazo = "1";
    	String motivoDaOp = "Outro";
    	String adeObs = "teste";

    	// declaração dos valores incorretos
    	String adeVlrNegativo = "-1";
    	String adeVlrGrande = "2000";
    	String letrasNoCampoNumerico = "abcdef";
    	String adePrazoZerado = "0";
    	String vlrVazio = "";

    	// loga no sistema
    	loginPage.loginSimples(loginCse);

    	// acessa a página de reimplantar capital devido
    	menuPage.acessarMenuFavoritos();
    	menuPage.acessarFavoritosReimplantarCapitalDevido();

    	// busca a consignação
    	reimplantarCapitalDevidoPage.preencherADE(String.valueOf(adeNumero));
    	reimplantarCapitalDevidoPage.clicarPesquisar();

    	// seleciona a consignação para operação
    	reimplantarCapitalDevidoPage.clicarReimplantarCapitalDevido();

    	// faz a operação sem valor
    	reimplantarCapitalDevidoPage.preencherNovosValores(vlrVazio, adePrazo, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("O valor da consignação deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação com valor negativo
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlrNegativo, adePrazo, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();
    	reimplantarCapitalDevidoPage.clicarOkAlerta();

    	// confere a mensagem de erro
    	assertEquals("O VALOR DA PRESTAÇÃO DEVE SER MAIOR DO QUE ZERO.", econsigHelper.getMensagemErro(webDriver));
    	reimplantarCapitalDevidoPage.clicarVoltar();

    	// faz a operação com letras no valor da ade
    	reimplantarCapitalDevidoPage.preencherNovosValores(letrasNoCampoNumerico, adePrazo, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("O valor da consignação deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação sem prazo
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, vlrVazio, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("O número de parcelas deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação com letras no prazo da ade
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, letrasNoCampoNumerico, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("O número de parcelas deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação com prazo zerado
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, adePrazoZerado, motivoDaOp, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();
    	reimplantarCapitalDevidoPage.clicarOkAlerta();


    	// confere a mensagem de erro
    	assertEquals("O NOVO Nº PRESTAÇÕES NÃO PODE SER ZERO.", econsigHelper.getMensagemErro(webDriver));
    	reimplantarCapitalDevidoPage.clicarVoltar();

    	// faz a operação sem motivo de operação
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, adePrazo, vlrVazio, adeObs);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("O motivo da operação deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação sem observação
    	reimplantarCapitalDevidoPage.preencherNovosValores(adeVlr, adePrazo, motivoDaOp, vlrVazio);
    	reimplantarCapitalDevidoPage.clicarProximo();

    	// confere a mensagem de erro
    	assertEquals("A observação é obrigatória.", econsigHelper.getMensagemPopUp(webDriver));
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// faz a operação com valor maior do que o da parcela
    	reimplantarCapitalDevidoPage.preencherNovoVlrAde(adeVlrGrande);
    	reimplantarCapitalDevidoPage.fecharPopUpCapital();

    	// confere a mensagem de erro
    	assertEquals("O valor informado é maior do que o valor da parcela atual.", econsigHelper.getMensagemPopUp(webDriver));
    }
}
