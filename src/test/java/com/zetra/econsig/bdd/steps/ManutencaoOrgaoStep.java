package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.ManutencaoOrgaoPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoOrgaoStep {

    @Autowired
    private EconsigHelper econsigHelper;

    private AcoesUsuarioPage acoesUsuarioPage;
    private ReservarMargemPage reservarMargemPage;
    private ManutencaoOrgaoPage manutencaoOrgaoPage;

    @Before
    public void setUp() throws Exception {
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
        manutencaoOrgaoPage = new ManutencaoOrgaoPage(getWebDriver());
    }

//    @BeforeEach
//    public static void setup() {
//        EConsigInitializer.limparCache();
//    }

    @Quando("bloquear o orgao")
    public void bloquearOrgao() {
        log.info("Quando bloquear o órgão");

        acoesUsuarioPage.clicarOpcoesOrgao("90");
        manutencaoOrgaoPage.clicarBloquear();

        assertEquals("Confirma o bloqueio de \"Teste Orgao Bloqueio Desbloqueio\"?", econsigHelper.getMensagemPopUp(getWebDriver()));

    }

    @Quando("editar os dados do orgao")
    public void editarDadosOrgao() {
        log.info("Quando editar os dados do órgão");

        acoesUsuarioPage.clicarOpcoesOrgao("0001");
        acoesUsuarioPage.clicarEditar();
        manutencaoOrgaoPage.preencherResponsavel("Carlos");
        manutencaoOrgaoPage.preencherCargo("Advogado");
        manutencaoOrgaoPage.preencherCEP("31710400");

    }

    @Quando("desbloquear o orgao")
    public void desbloquearOrgao() {
        log.info("Quando desbloquear o órgão");

        acoesUsuarioPage.clicarOpcoesOrgao("90");
        manutencaoOrgaoPage.clicarDesbloquear();

        assertEquals("Confirma o desbloqueio de \"Teste Orgao Bloqueio Desbloqueio\"?", econsigHelper.getMensagemPopUp(getWebDriver()));

    }

    @Quando("excluir orgao")
    public void excluirOrgao() {
        log.info("Quando excluir órgão");

        acoesUsuarioPage.clicarOpcoesOrgao("70");
        acoesUsuarioPage.clicarExcluir();

        assertEquals("Confirma a exclusão de \"TESTE EXCLUSAO\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Entao("tentar editar os dados do orgao invalidos")
    public void editarDadosOrgaoInvalidos() {
        log.info("Então tentar editar os dados do órgão inválidos");

        acoesUsuarioPage.clicarOpcoesOrgao("40");
        acoesUsuarioPage.clicarEditar();
        manutencaoOrgaoPage.preencherCNPJ("01.211.11");
        manutencaoOrgaoPage.clicarBotaoSalvar();

        assertEquals("Por favor, verifique o conteúdo dos campos grafados em vermelho.", econsigHelper.getMensagemPopUp(getWebDriver()));

        while (SeleniumHelper.isAlertPresent(getWebDriver())) {
            getWebDriver().switchTo().alert().dismiss();
        }
    }

    @Quando("excluir orgao com dependentes")
    public void excluirOrgaocomDependentes() {
        log.info("Quando excluir orgão com dependentes");

        acoesUsuarioPage.clicarOpcoesOrgao("0001");
        acoesUsuarioPage.clicarExcluir();

        assertEquals("Confirma a exclusão de \"Órgão Pomodori\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("pesquisar orgao pelo filtro {string} com {string}")
    public void pesquisarOrgao(String tipoFiltro, String filtro) {
        log.info("Quando pesquisar órgão pelo filtro {} com {}", tipoFiltro, filtro);

        manutencaoOrgaoPage.filtroPerfil(filtro, tipoFiltro);
    }

    @Entao("exibe os orgaos com o filtro Nome")
    public void retornaOrgaosPorNome() {
        log.info("Entao exibe os órgãos com o filtro Nome");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("CARLOTA"));

    }

    @Entao("exibe os orgaos com o filtro Codigo")
    public void retornaOrgaosPorCodigo() {
        log.info("Entao exibe os órgãos com o filtro Código");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("ÓRGÃO POMODORI"));

    }

    @Entao("exibe os orgaos com o filtro Codigo estabelecimento")
    public void retornaOrgaosPorCodigoEstabelecimento() {
        log.info("Entao exibe os órgãos com o filtro Código estabelecimento");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("ÓRGÃO POMODORI"));

    }

    @Entao("exibe os orgaos com o filtro Bloqueado")
    public void retornaOrgaoBloqueado() {
        log.info("Entao exibe os órgãos com o filtro Bloqueado");

        // verifica que exibe somente o usuario selecionado
        assertFalse(getWebDriver().getPageSource().contains("ÓRGÃO POMODORI"));
        assertFalse(getWebDriver().getPageSource().contains("CARLOTA"));

    }

    @Entao("exibe os orgaos com o filtro Desbloqueado")
    public void retornaOrgaodesbloqueado() {
        log.info("Então exibe os órgãos com o filtro Desbloqueado");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("CARLOTA"));
        assertTrue(getWebDriver().getPageSource().contains("FUNDACAO MUNICIPAL DO MEIO AMBIENTE"));
        assertTrue(getWebDriver().getPageSource().contains("ÓRGÃO POMODORI"));

    }

    @Quando("criar orgao com codigo {string}")
    public void criarOrgao(String codigo) {
        log.info("Quando criar órgão com código {}", codigo);

        manutencaoOrgaoPage.clicarCriarNovoOrgao();

        // preencher campos
        manutencaoOrgaoPage.preencherCodigo(codigo);
        manutencaoOrgaoPage.preencherNome("Orgao Teste");
        manutencaoOrgaoPage.selecionarEstabelecimento("Estabelecimento Pomodori");

        acoesUsuarioPage.clicarSalvar();
    }

    @Quando("criar orgao com codigo ja existente {string}")
    public void criarOrgaojaExistente(String codigo) {
        log.info("Quando criar órgão com código já existente {}", codigo);

        manutencaoOrgaoPage.clicarCriarNovoOrgao();

        // preencher campos
        manutencaoOrgaoPage.preencherCodigo("36");
        manutencaoOrgaoPage.preencherNome("SECRETARIA DE MEIO AMBIENTE E DES URBANO");
        manutencaoOrgaoPage.selecionarEstabelecimento("PREFEITURA MUNICIPAL DE FLORIANOPOLIS");

        acoesUsuarioPage.clicarSalvar();
    }

    @Entao("tentar criar orgao sem informar campos obrigatorios")
    public void naoInformarCodigo() {
        log.info("Então tentar criar órgão sem informar campos obrigatórios");

        manutencaoOrgaoPage.clicarCriarNovoOrgao();

        // salvar sem informar nenhum campo
        manutencaoOrgaoPage.clicarBotaoSalvar();
        assertEquals("O código do órgão deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoOrgaoPage.preencherCodigo("008");
        manutencaoOrgaoPage.clicarBotaoSalvar();
        assertEquals("A descrição do órgão deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoOrgaoPage.preencherNome("Teste Orgao 008");
        manutencaoOrgaoPage.clicarBotaoSalvar();
        assertEquals("O estabelecimento do órgão deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("alterar o codigo orgao")
    public void alterarCodigoOrgao() {
        log.info("Quando alterar o código órgão");

        acoesUsuarioPage.clicarOpcoesOrgao("49");
        acoesUsuarioPage.clicarEditar();
        manutencaoOrgaoPage.preencherCodigo("36");
    }

    @Entao("exibe a mensagem de erro para codigo ja existente {string}")
    public void exibeMensagemComErro(String mensagem) throws Throwable {
        log.info("Entao exibe a mensagem de erro para código já existente {}", mensagem);

        assertTrue(reservarMargemPage.retornarMensagemErro().contains(mensagem));
    }
}
