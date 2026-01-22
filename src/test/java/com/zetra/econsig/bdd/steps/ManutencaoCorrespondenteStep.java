package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignatariaPage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoCorrespondentePage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoPostoPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.CorrespondentesService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.ManutencaoPerfilService;
import com.zetra.econsig.service.OrgaoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoCorrespondenteStep {

    private final LoginInfo loginCsa2 = LoginValues.csa2;

    private final LoginInfo loginCor = LoginValues.cor1;

    private String orgCodigo;
    private String csaCodigo;
    private String svcCodigoEmprestimo;
    private String svcCodigoCartao;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private CorrespondentesService correspondentesService;

    @Autowired
    private ConvenioService convenioService;

    @Autowired
    private ItemMenuFavoritoService itemMenuFavoritoService;

    @Autowired
    private ManutencaoPerfilService manutencaoPerfilService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private OrgaoService orgaoService;

    private LoginPage loginPage;
    private MenuPage menuPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private ReservarMargemPage reservarMargemPage;
    private ManutencaoPostoPage manutencaoPostoPage;
    private ManutencaoConsignatariaPage manutencaoConsignatariaPage;
    private ManutencaoCorrespondentePage manutencaoCorrespondentePage;

    @Before
    public void setUp() throws Exception {
        loginPage = new LoginPage(getWebDriver());
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
        manutencaoPostoPage = new ManutencaoPostoPage(getWebDriver());
        manutencaoConsignatariaPage = new ManutencaoConsignatariaPage(getWebDriver());
        manutencaoCorrespondentePage = new ManutencaoCorrespondentePage(getWebDriver());

        orgCodigo = orgaoService.obterOrgaoPorIdentificador("213464140").getOrgCodigo();
        csaCodigo = usuarioService.getCsaCodigo(loginCsa2.getLogin());
        svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
        svcCodigoCartao = servicoService.retornaSvcCodigo("020");
    }

    @Dado("que o correspondente esteja ativo")
    public void correspondenteAtivo() {
        log.info("Dado que o correspondente esteja ativo");

        final Convenio convenioEmprestimo = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, orgCodigo, csaCodigo);
        final Convenio convenioCartao = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoCartao, orgCodigo, csaCodigo);

        correspondentesService.alterarStatusCorrespondente("138", "1");
        convenioService.alterarScvCodigoCorrespondenteConvenio(usuarioService.getCorCodigo(loginCor.getLogin()), convenioEmprestimo.getCnvCodigo(), CodedValues.SCV_ATIVO);
        convenioService.alterarScvCodigoCorrespondenteConvenio(usuarioService.getCorCodigo(loginCor.getLogin()), convenioCartao.getCnvCodigo(), CodedValues.SCV_ATIVO);
        convenioService.alterarScvCodigoConvenio("020", csaCodigo, "213464140", CodedValues.SCV_ATIVO);
        convenioService.alterarScvCodigoConvenio("001", csaCodigo, "213464140", CodedValues.SCV_ATIVO);

        EConsigInitializer.limparCache();
    }

    @Dado("criar novo correspondente {string} e codigo {string}")
    public void criarNovoCorrespondente(String cnpj, String codigo) {
        log.info("criar novo correspondente {} e código {}", cnpj, codigo);

        manutencaoCorrespondentePage.clicarBotaoNovo();
        manutencaoCorrespondentePage.preencherCNPJ(cnpj);
        manutencaoCorrespondentePage.clicarPesquisar();
        manutencaoCorrespondentePage.preencherCodigo(codigo);
        manutencaoCorrespondentePage.preencherNome("Correspondente automação");
        manutencaoCorrespondentePage.preencherResponsaveis();
        manutencaoCorrespondentePage.preencherEndereco();
        manutencaoCorrespondentePage.preencherContato();
    }

    @Dado("editar os dados do correspondente {string}")
    public void editarDadosCorrespondente(String nome) {
        log.info("Dado editar os dados do correspondente {}", nome);

        acoesUsuarioPage.clicarOpcoes("002", "0");
        acoesUsuarioPage.clicarEditar();
        manutencaoCorrespondentePage.preencherNome(nome);
        manutencaoCorrespondentePage.preencherResponsaveis();
        manutencaoCorrespondentePage.preencherEndereco();
    }

    @Dado("editar os dados do correspondente com codigo {string}")
    public void tentarEditarCorrespondente(String codigo) {
        log.info("editar os dados do correspondente com código {}", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarEditar();
        manutencaoCorrespondentePage.preencherCodigo("002");

    }

    @Dado("excluir os dados do correspondente {string}")
    public void excluirDadosCorrespondente(String codigo) {
        log.info("Dado excluir os dados do correspondente {}", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarExcluir();

        assertEquals("Confirma a exclusão de \"Correspondente Automacao\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Dado("que possui o correspondente {string} com codigo {string} criado")
    public void criarCorrespondente(String cnpj, String codigo) {
        log.info("Dado que possui o correspondente {} com código {} criado", cnpj, codigo);

        correspondentesService.criarCorrespondente(codigo, cnpj);

    }

    @Dado("que o convenio esteja desbloqueado")
    public void convenioDesbloqueado() {
        log.info("Dado que o convênio esteja desbloqueado");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, orgCodigo, csaCodigo);

        correspondentesService.alterarStatusCorrespondente("138", "1");
        convenioService.alterarScvCodigoCorrespondenteConvenio(usuarioService.getCorCodigo(loginCor.getLogin()), convenio.getCnvCodigo(), "1");
    }

    @Dado("que o convenio esteja bloqueado")
    public void convenioBloqueado() {
        log.info("Dado que o convênio esteja bloqueado");

        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoEmprestimo, orgCodigo, csaCodigo);

        correspondentesService.alterarStatusCorrespondente("138", "1");
        convenioService.alterarScvCodigoCorrespondenteConvenio(usuarioService.getCorCodigo(loginCor.getLogin()), convenio.getCnvCodigo(), "2");
    }

    @Dado("que o correspondente {string} esteja bloqueado")
    public void correspondenteBloqueado(String codigo) {
        log.info("Dado que o correspondente {} esteja bloqueado", codigo);

        correspondentesService.alterarStatusCorrespondente(codigo, "4");
    }

    @Dado("que o correspondente {string} esteja bloqueado pela consignataria")
    public void correspondenteBloqueadoPelaCsa(String codigo) {
        log.info("Dado que o correspondente {} esteja bloqueado pela consignatária", codigo);

        correspondentesService.alterarStatusCorrespondente(codigo, "0");
    }

    @Quando("bloquear o correspondente {string}")
    public void bloquearCorrespondente(String codigo) {
        log.info("Quando bloquear o correspondente {}", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();

        assertEquals("Confirma o bloqueio de \"Correspondente BB\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("desbloquear o correspondente {string}")
    public void desbloquearCorrespondente(String codigo) {
        log.info("Quando desbloquear o correspondente {}", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();

        assertEquals("Confirma o desbloqueio de \"Correspondente BB\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("consultar convenios do correspondente {string} e bloquear")
    public void bloquearConvenio(String codigo) {
        log.info("Quando consultar convênios do correspondente {} e bloquear", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarConsultarConvenios();

        acoesUsuarioPage.clicarOpcoes("001", "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        manutencaoCorrespondentePage.desmarcarConvenio();
        manutencaoPostoPage.clicarSalvar();
    }

    @Quando("consultar convenios do correspondente {string} e desbloquear")
    public void desbloquearConvenio(String codigo) {
        log.info("Quando consultar convênios do correspondente {} e desbloquear", codigo);

        acoesUsuarioPage.clicarOpcoes(codigo, "0");
        acoesUsuarioPage.clicarConsultarConvenios();

        acoesUsuarioPage.clicarOpcoes("001", "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        manutencaoCorrespondentePage.selecionarConvenio();
        manutencaoPostoPage.clicarSalvar();
    }

    @Quando("configurar auditoria correspondente")
    public void configurarAuditoria() {
        log.info("Quando configurar auditoria correspondente");

        acoesUsuarioPage.clicarAcoes();
        manutencaoCorrespondentePage.clicarConfigurarAuditoria();

        assertEquals("Periodicidade de envio de e-mails de auditoria: Semanal", econsigHelper.getMensagemSucesso(getWebDriver()));

        manutencaoCorrespondentePage.selecionarFuncoes();

        manutencaoPostoPage.clicarSalvar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Atualizações salvas com sucesso.");
    }

    @Entao("o perfil correspondente {string} e bloqueado")
    public void verificarPerfilBloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil correspondente {} é bloqueado", perfilDescricao);

        assertEquals(0, manutencaoPerfilService.getStatusPerfilCor(perfilDescricao));
    }

    @Entao("o perfil correspondente {string} e desbloqueado")
    public void verificarPerfilDesbloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil correspondente {} é desbloqueado", perfilDescricao);

        assertEquals(1, manutencaoPerfilService.getStatusPerfilCor(perfilDescricao));
    }

    @Entao("verifica que correspondente nao consegue fazer reserva")
    public void verificarNaoReservaMargemCorrespondente() {
        log.info("Entao verifica que correspondente não consegue fazer reserva");

        // incluir menu no favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));

        // autenticar
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCor);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        assertFalse(manutencaoConsignatariaPage.isExibeServico("EMPRÉSTIMO - 001"));
    }

    @Entao("verifica que correspondente consegue fazer reserva")
    public void verificarReservaMargemCorrespondente() {
        log.info("Entao verifica que correspondente consegue fazer reserva");

        // incluir menu no favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));

        // autenticar
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCor);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");

        reservarMargemPage.preencherValorLiquidoLiberado("1000");
        reservarMargemPage.preencherValorPrestacao("100");
        reservarMargemPage.preencherNumeroPrestacao("11");
        reservarMargemPage.clicarConfirmar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Dados da consignação");
        econsigHelper.verificaTextoPagina(getWebDriver(), "Favor verificar e confirmar as informações abaixo");
    }

    @Entao("verifica que correspondente nao consegue autenticar")
    public void verificarStatusBancoCorrespondenteNaoAutentica() {
        log.info("Entao verifica que correspondente não consegue autenticar");

        // verifica o status no banco
        assertEquals("4", correspondentesService.getCorAtivo("138"));

        // autenticar
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCor);

        assertEquals("CORRESPONDENTE BLOQUEADO NO SISTEMA.", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica status no banco e que o correspondente nao consegue autenticar")
    public void verificarCorrespondenteNaoAutentica() {
        log.info("Entao verifica status no banco e que o correspondente não consegue autenticar");

        // verifica o status no banco
        assertEquals("0", correspondentesService.getCorAtivo("138"));

        // autenticar
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCor);

        assertEquals("CORRESPONDENTE BLOQUEADO NO SISTEMA.", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica que correspondente consegue autenticar")
    public void verificarCorrespondenteAutentica() {
        log.info("Entao verifica que correspondente consegue autenticar");

        // verifica o status no banco
        assertEquals("1", correspondentesService.getCorAtivo("138"));

        // autenticar
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCor);

        econsigHelper.verificaTextoPagina(getWebDriver(), "Página inicial");
        assertEquals("eConsig - Principal", getWebDriver().getTitle());
    }
}
