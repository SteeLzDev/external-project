package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.EditarParametroPostoGraduacaoPage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignantePage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignatariaPage;
import com.zetra.econsig.bdd.steps.pages.ManutencaoPostoPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.CorrespondenteConvenio;
import com.zetra.econsig.service.CampoSistemaService;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.IndiceService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.ManutencaoPerfilService;
import com.zetra.econsig.service.OrgaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PrazoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoConsignatariaStep {

    private final LoginInfo loginCsa1 = LoginValues.csa1;

    private final LoginInfo loginCsa2 = LoginValues.csa2;

    private final LoginInfo loginCor = LoginValues.cor1;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private ConsignatariaService consignatariaService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @Autowired
    private ConvenioService convenioService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private IndiceService indiceService;

    @Autowired
    private PrazoService prazoService;

    @Autowired
    private ManutencaoPerfilService manutencaoPerfilService;

    @Autowired
    private ItemMenuFavoritoService itemMenuFavoritoService;

    @Autowired
    private CampoSistemaService campoSistemaService;

    @Autowired
    private OrgaoService orgaoService;

    private LoginPage loginPage;

    private MenuPage menuPage;

    private AcoesUsuarioPage acoesUsuarioPage;

    private UsuarioPage usuarioPage;

    private ReservarMargemPage reservarMargemPage;

    private ManutencaoConsignantePage manutencaoConsignantePage;

    private ManutencaoConsignatariaPage manutencaoConsignatariaPage;

    private ManutencaoPostoPage manutencaoPostoPage;

    private EditarParametroPostoGraduacaoPage editarParametroPostoGraduacaoPage;

    @Before
    public void setUp() throws Exception {
        loginPage = new LoginPage(getWebDriver());
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        usuarioPage = new UsuarioPage(getWebDriver());
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
        manutencaoConsignantePage = new ManutencaoConsignantePage(getWebDriver());
        manutencaoConsignatariaPage = new ManutencaoConsignatariaPage(getWebDriver());
        manutencaoPostoPage = new ManutencaoPostoPage(getWebDriver());
        editarParametroPostoGraduacaoPage = new EditarParametroPostoGraduacaoPage(getWebDriver());
    }

    @Dado("que a consignataria esteja ativo")
    public void consignatariaAtivo() {
        log.info("Dado que a consignatária esteja ativo");

        consignatariaService.alterarStatusConsignataria(usuarioService.getCsaCodigo(loginCsa1.getLogin()), CodedValues.STS_ATIVO.toString());
        parametroSistemaService.alterarParametroConsignataria(loginCsa2.getLogin(), CodedValues.TPA_EXIGE_CERTIFICADO_DIGITAL, "N");
        consignatariaService.alterarCsaExigeEnderecoAcesso(usuarioService.getCsaCodigo(loginCsa1.getLogin()), "N");
        consignatariaService.alterarCsaPermiteIncluirAde(usuarioService.getCsaCodigo(loginCsa1.getLogin()), "S");
        consignatariaService.alterarCsaIpAcesso(usuarioService.getCsaCodigo(loginCsa1.getLogin()), "");
        usuarioService.alterarUsuExigeCertificado(loginCsa2.getLogin(), "");
        usuarioService.alterarUsuIPAcesso(loginCsa1.getLogin(), "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG, "N");
        convenioService.alterarScvCodigoConvenio("020", usuarioService.getCsaCodigo(loginCsa2.getLogin()), "213464140", CodedValues.SCV_ATIVO);
        indiceService.excluirIndices(usuarioService.getCsaCodigo(loginCsa2.getLogin()));
        prazoService.excluirPrazoConsignataria();
        prazoService.excluirPrazo(servicoService.retornaSvcCodigo("019"));
        EConsigInitializer.limparCache();
    }

    @Dado("que possui indice {string} criado para servico {string}")
    public void possuiIndice(String indice, String servico) {
        log.info("Dado que possui índice {} criado para serviço {}", indice, servico);

        indiceService.incluirIndice(servicoService.retornaSvcCodigo(servico), usuarioService.getCsaCodigo(loginCsa2.getLogin()), indice, "Indice " + indice);
    }

    @Dado("que a consignataria esteja bloqueado")
    public void consignatariaBloqueado() {
        log.info("Dado que a consignatária esteja bloqueado");

        consignatariaService.alterarStatusConsignataria(usuarioService.getCsaCodigo(loginCsa1.getLogin()), CodedValues.STS_INATIVO.toString());
    }

    @Dado("que a consignataria esteja bloqueada por seguranca")
    public void consignatariaBloqueadoPorSeguranca() {
        log.info("Dado que a consignatária esteja bloqueada por segurança");

        consignatariaService.alterarStatusConsignataria(usuarioService.getCsaCodigo(loginCsa1.getLogin()), CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.toString());
    }

    @Dado("que o campo Codigo Zetrasoft nao seja obrigatorio")
    public void codigoZetrasoftSemObrigatoriedade() {
        log.info("Dado que o campo Codigo Zetrasoft nao seja obrigatorio");

        // alterar tabela tb_campo_sistema, o CAS_CHAVE
        // sup.editarConsignataria_idn_interno para N
        campoSistemaService.alterarCampoSistema("sup.editarConsignataria_idn_interno", "N");

        // chama o limpa cache
        EConsigInitializer.limparCache();
    }

    @Quando("bloquear a consignataria com data para desbloqueio automatico")
    public void bloquearConsignatariaComData() {
        log.info("Quando bloquear a consignatária");

        final Date dataDesbloqueio = new Timestamp(DateHelper.addDays(DateHelper.getSystemDatetime(), +30).getTime());

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        assertEquals("Confirma o bloqueio de \"TREINAMENTO\"?", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherDataParaDesbloqueio(DateHelper.format(dataDesbloqueio, "dd/MM/yyyy"));
        acoesUsuarioPage.clicarSalvar();
        assertEquals("Informe o motivo da operação", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.selecionarMotivo("Bloqueio");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("A observação é obrigatória.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherObservacao("Automacao");
        manutencaoConsignatariaPage.preencherDataParaDesbloqueio("01/07/2021");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("Data para desbloqueio automático deve ser maior que a data corrente.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherDataParaDesbloqueio(DateHelper.format(dataDesbloqueio, "dd/MM/yyyy"));
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Atenção: o desbloqueio automático pode não ocorrer na data informada caso o sistema esteja indisponível no dia ou ocorra algo que impeça o desbloqueio.", econsigHelper.getMensagemPopUp(getWebDriver()));

        assertEquals("Confirma o bloqueio da consignatária?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("bloquear a consignataria sem data para desbloqueio automatico")
    public void bloquearConsignatariaSemData() {
        log.info("Quando bloquear a consignatária");

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        assertEquals("Confirma o bloqueio de \"TREINAMENTO\"?", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.selecionarMotivo("Bloqueio");
        manutencaoConsignatariaPage.preencherObservacao("Automacao");
        manutencaoConsignatariaPage.selecionarPenalidade("Penalidade bloqueio");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Confirma o bloqueio da consignatária?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("desbloquear a consignataria")
    public void desbloquearConsignataria() {
        log.info("Quando desbloquear a consignatária");

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        assertEquals("Confirma o desbloqueio de \"TREINAMENTO\"?", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherObservacao("Desbloqueio");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("Informe o motivo da operação", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.selecionarMotivo("Bloqueio");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Confirma o desbloqueio da consignatária?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("editar a consignataria com usuario {string}")
    public void editarConsignatariaCseSup(String usuario) {
        log.info("Quando editar a consignatária com usuário {}", usuario);

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarEditar();

        if (usuario.matches("cse")) {
            manutencaoConsignatariaPage.alterarConsignatariaCse();

        } else {
            manutencaoConsignatariaPage.alterarConsignatariaSup();

            acoesUsuarioPage.clicarSalvar();
            assertEquals("Por favor, verifique o conteúdo dos campos grafados em vermelho.", econsigHelper.getMensagemPopUp(getWebDriver()));

            manutencaoConsignatariaPage.selecionarCodigoZetrasoft("BANCO BNL DO BRASIL S/A - 116");
        }
    }

    @Quando("editar a consignataria")
    public void editarConsignataria() {
        log.info("Quando editar a consignatária");

        manutencaoConsignatariaPage.selecionarNatureza("Seguradora");
        manutencaoConsignatariaPage.preencherEmailExpiracao("csa@gmail.com");
        manutencaoConsignatariaPage.preencherCNPJ("21.977.338/0001-78");
        manutencaoConsignatariaPage.preencherBanco("01");
        manutencaoConsignatariaPage.preencherAgencia("1236");
        manutencaoConsignatariaPage.preencherConta("023654");
        manutencaoConsignatariaPage.preencherDigito("3");
        manutencaoConsignatariaPage.selecionarUF("Rio Grande do Sul");
        manutencaoConsignatariaPage.preencherInstrucoesContato("Testes automatizado, instruçoes da Consignataria para servidor entrar em contato");
        usuarioPage.incluirIPsAcessoAtualCSACaso1();
    }

    @Quando("acessar tela de edicao")
    public void acessarTelaEdicao() {
        log.info("Quando acessar tela de edição");

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarEditar();
    }

    @Entao("tentar editar consignataria com campos invalidos")
    public void tentarEditarConsignataria() {
        log.info("Então tentar editar consignatária com campos inválidos");

        manutencaoConsignatariaPage.selecionarNatureza("-- Selecione --");
        manutencaoConsignatariaPage.preencherBanco("01");
        manutencaoConsignatariaPage.preencherAgencia("1236");
        manutencaoConsignatariaPage.preencherConta("023654");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("A natureza da consignatária deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.selecionarNatureza("Cooperativa");
        manutencaoConsignatariaPage.preencherCodigo("");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("O código da consignatária deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherCodigo("17167412007983");
        manutencaoConsignatariaPage.preencherCNPJ("6");
        acoesUsuarioPage.clicarSalvar();
        assertEquals("Por favor, verifique o conteúdo dos campos grafados em vermelho.", econsigHelper.getMensagemPopUp(getWebDriver()));

    }

    @Quando("editar consignataria para nao permite incluir novas consignacoes")
    public void editarCsaNaoPermiteIncluirNovasConsignacoes() {
        log.info("Quando editar consignatária para não permite incluir novas consignações");

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarEditar();
        manutencaoConsignatariaPage.selecionarNaoPermiteIncluirAde();

    }

    @Quando("editar consignataria para verificar cadastro de IP endereco de acesso no login")
    public void editarCsaNaoVerificarIP() {
        log.info("Quando editar consignatária para verificar cadastro de IP endereço de acesso no login");

        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarEditar();
        manutencaoConsignatariaPage.marcarExigeEnderecoAcesso();
    }

    @Quando("excluir consignataria")
    public void excluirConsignataria() {
        log.info("Quando excluir consignatária");

        acoesUsuarioPage.clicarOpcoesConsignatarias("001");
        acoesUsuarioPage.clicarExcluir();

        assertEquals("Confirma a exclusão de \"BB\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("alterar o codigo")
    public void alterarCodigo() {
        log.info("Quando alterar o código");

        acoesUsuarioPage.clicarOpcoesConsignatarias("8036");
        acoesUsuarioPage.clicarEditar();
        manutencaoConsignatariaPage.preencherCodigo("001");
    }

    @Quando("alterar o parametro exige certificado digital")
    public void alterarParametroExigeCertificadoDigital() {
        log.info("Quando editar consignatária para verificar cadastro de IP endereço de acesso no login");

        acoesUsuarioPage.clicarOpcoesConsignatarias("001");
        manutencaoConsignatariaPage.clicarEditarParamConsignataria();
        manutencaoConsignatariaPage.marcarExigeCertificadoDigital();
    }

    @Quando("cadastrar Ip de acesso")
    public void cadastrarIpAcesso() {
        log.info("Quando cadastrar Ip de acesso");

        usuarioPage.incluirIPsAcessoAtualCSACaso2();
    }

    @Quando("pesquisar consignataria pelo filtro {string} com {string}")
    public void pesquisarConsignataria(String tipoFiltro, String filtro) {
        log.info("Quando pesquisar consignatária pelo filtro {} com {}", tipoFiltro, filtro);

        manutencaoConsignantePage.filtroPerfil(filtro, tipoFiltro);
    }

    @Quando("acessar opcao Imprimir")
    public void acessarImprimir() {
        log.info("Quando acessar opção Imprimir");

        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarImprimir();
    }

    @Quando("criar consignataria com codigo {string}")
    public void criarConsignataria(String codigo) {
        log.info("Quando criar consignatária com código {}", codigo);

        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarCriarNovaConsignataria();

        // preencher campos
        manutencaoConsignatariaPage.preencherCadastroConsignataria(codigo, "256397", "18.968.802/0001-55");
    }

    @Quando("bloquear um servico")
    public void bloquearServico() {
        log.info("Quando bloquear um serviço");

        acoesUsuarioPage.clicarOpcoesConsignatarias("001");
        acoesUsuarioPage.clicarServicos();

        // bloquear serviço
        acoesUsuarioPage.clicarOpcoes("020", "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        manutencaoConsignatariaPage.desmarcarOrgaoCarlotaJoaquina();

        usuarioPage.selecionarMotivoOperacao("Outros");
        usuarioPage.preencherObservacao("Automacao");
        acoesUsuarioPage.clicarSalvar();
    }

    @Quando("desbloquear um servico")
    public void desbloquearServico() {
        log.info("Quando desbloquear um serviço");

        convenioService.alterarScvCodigoConvenio("036", usuarioService.getCsaCodigo(loginCsa2.getLogin()), "213464140", CodedValues.SCV_INATIVO);

        acoesUsuarioPage.clicarOpcoesConsignatarias("001");
        acoesUsuarioPage.clicarServicos();

        // bloquear serviço
        acoesUsuarioPage.clicarOpcoes("036", "0");
        acoesUsuarioPage.clicarBloquearDesbloquear();
        manutencaoConsignatariaPage.marcarOrgaoCarlotaJoaquina();

        usuarioPage.selecionarMotivoOperacao("Outros");
        usuarioPage.preencherObservacao("Automacao");
        acoesUsuarioPage.clicarSalvar();
    }

    @Quando("cadastrar novo indice {string}")
    public void cadastrarNovoIndice(String indice) {
        log.info("Quando cadastrar novo índice {}", indice);

        manutencaoConsignatariaPage.clicarNovoIndice();
        manutencaoConsignatariaPage.preencherCodigoIndice(indice);
        manutencaoConsignatariaPage.preencherDescricaoIndice("Indice 1");
        manutencaoPostoPage.clicarSalvar();
    }

    @Quando("cadastrar varios indices")
    public void cadastrarVariosIndice() {
        log.info("Quando cadastrar vários índices");

        manutencaoConsignatariaPage.clicarNovoIndice();
        manutencaoConsignatariaPage.preencherCodigoIndice("05");
        manutencaoConsignatariaPage.preencherDescricaoIndice("Indice 05");
        manutencaoPostoPage.clicarSalvar();
        assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));

        // cancelar para retornar para tela anterior e criar novo indice
        usuarioPage.clicarCancelar();
        manutencaoConsignatariaPage.clicarNovoIndice();
        manutencaoConsignatariaPage.preencherCodigoIndice("07");
        manutencaoConsignatariaPage.preencherDescricaoIndice("Indice 07");
        manutencaoPostoPage.clicarSalvar();
    }

    @Quando("editar indice {string}")
    public void editarIndice(String indice) {
        log.info("Quando editar índice {}", indice);

        acoesUsuarioPage.clicarOpcoes(indice, "0");
        manutencaoConsignatariaPage.clicarEditarIndice();
        manutencaoConsignatariaPage.preencherCodigoIndice("07");
        manutencaoConsignatariaPage.preencherDescricaoIndice("Indice 7");
        manutencaoPostoPage.clicarSalvar();
    }

    @Quando("excluir indice {string}")
    public void excluirIndice(String indice) {
        log.info("Quando excluir índice {}", indice);

        acoesUsuarioPage.clicarOpcoes(indice, "0");
        manutencaoConsignatariaPage.clicarExcluirIndice();

        assertEquals("Confirma a exclusão de \"Indice " + indice + "\" ?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("configurar auditoria sobre operacoes")
    public void configurarAuditoria() {
        log.info("Quando configurar auditoria sobre operações");

        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarConfigurarAuditoria();

        assertEquals("Periodicidade de envio de e-mails de auditoria: Semanal", econsigHelper.getMensagemSucesso(getWebDriver()));

        manutencaoConsignatariaPage.selecionarFuncoes();

        manutencaoPostoPage.clicarSalvar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Atualizações salvas com sucesso.");
    }

    @Quando("acessar indice do servico {string}")
    public void acessarIndice(String servico) {
        log.info("Quando acessar índice do serviço {}", servico);

        acoesUsuarioPage.clicarOpcoesConsignatarias("001");
        acoesUsuarioPage.clicarServicos();

        // cadastrar indice
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarIndice();
    }

    @Quando("csa acessar indice do servico {string}")
    public void acessarIndiceCsa(String servico) {
        log.info("Quando csa acessar índice do serviço {}", servico);

        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarEditarParamServico();

        // cadastrar indice
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarIndice();
    }

    @Quando("bloquear todos os prazos do servico {string}")
    public void bloquearPrazos(String servico) {
        log.info("Quando bloquear todos os prazos do serviço {}", servico);

        // acessar prazos
        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarServicos();
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarPrazos();

        // bloquear prazos
        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarBloquearTodos();
    }

    @Quando("tentar bloquear e desbloquear os prazos do servico {string}")
    public void tentarBloquearDesbloquearPrazos(String servico) {
        log.info("Quando bloquear os prazos do serviço {}", servico);

        // acessar prazos
        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarServicos();
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarPrazos();

        // bloquear prazos
        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarBloquearTodos();
    }

    @Quando("bloquear prazo {string} do servico {string}")
    public void bloquearUmPrazo(String prazo, String servico) {
        log.info("Quando bloquear os prazos do serviço {}", prazo, servico);

        // acessar prazos
        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarServicos();
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarPrazos();

        // bloquear prazos
        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarDesbloquearTodos();

        // bloquear um serviço
        //while (!manutencaoConsignatariaPage.isSituacaoPrazoBloqueado("5")) {
        manutencaoConsignatariaPage.clicarBloquear(prazo);
        //}
    }

    @Quando("desbloquear os prazos do servico {string}")
    public void desbloquearPrazos(String servico) {
        log.info("Quando desbloquear os prazos do serviço {}", servico);

        // acessar prazos
        acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
        acoesUsuarioPage.clicarServicos();
        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarPrazos();

        // verificar que os prazos estão bloqueados
        assertTrue(manutencaoConsignatariaPage.isSituacaoPrazosBloqueados());

        // desbloquear prazos
        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarDesbloquearTodos();

        // verificar que os prazos foram desbloqueados
        assertFalse(manutencaoConsignatariaPage.isSituacaoPrazosBloqueados());
    }

    @Quando("incluir prazo para servico {string}")
    public void incluirPrazoParaServico(String servico) {
        log.info("Quando incluir prazo para serviço {}", servico);

        acoesUsuarioPage.clicarOpcoes(servico, "0");
        manutencaoConsignatariaPage.clicarPrazos();

        manutencaoConsignatariaPage.preencherPrazoInicial("2");
        manutencaoConsignatariaPage.preencherPrazoFinal("8");
        manutencaoConsignatariaPage.clicarInserir();

        econsigHelper.verificaTextoPagina(getWebDriver(), "loqueado");

        final WebElement main = getWebDriver().findElement(By.cssSelector(".table"));
        assertEquals(7, main.findElements(By.xpath(".//tbody/tr")).size());
    }

    @Quando("incluir penalidade")
    public void incluirPenalidade() {
        log.info("Quando incluir penalidade");

        acoesUsuarioPage.clicarOpcoesConsignatarias("8036");
        manutencaoConsignatariaPage.clicarPenalidade();

        manutencaoConsignatariaPage.preencherObservacao("Automacao");
        manutencaoConsignatariaPage.selecionarPenalidade("Penalidade bloqueio");
        manutencaoPostoPage.clicarSalvar();

    }

    @Quando("tentar incluir penalidade")
    public void tentarIncluirPenalidade() {
        log.info("Quando tentar incluir penalidade");

        acoesUsuarioPage.clicarOpcoesConsignatarias("8036");
        manutencaoConsignatariaPage.clicarPenalidade();

        manutencaoPostoPage.clicarSalvar();
        assertEquals("A observação é obrigatória.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherObservacao("Automacao");
        manutencaoPostoPage.clicarSalvar();
        assertEquals("Informe uma penalidade para a consignatária.", econsigHelper.getMensagemPopUp(getWebDriver()));

    }

    @Quando("clicar em Listar perfis de usuarios")
    public void clicarListarperfil() throws Throwable {
        log.info("Quando clicar em Listar perfis de usuários");

        acoesUsuarioPage.clicarAcoes();
        acoesUsuarioPage.clicarListarPerfilUsuario();
    }

    @Entao("o perfil consignataria {string} e bloqueado")
    public void verificarPerfilBloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil {} é bloqueado", perfilDescricao);

        assertEquals(0, manutencaoPerfilService.getStatusPerfilCsa(perfilDescricao));
    }

    @Entao("o perfil consignataria {string} e desbloqueado")
    public void verificarPerfilDesbloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil {} é desbloqueado", perfilDescricao);

        assertEquals(1, manutencaoPerfilService.getStatusPerfilCsa(perfilDescricao));
    }

    @Entao("csa nao pode criar reserva com o prazo {string} bloqueado {string}")
    public void csaNaoCriarReservaPrazoBloqueado(String prazo, String mensagem) {
        log.info("Então csa não pode criar reserva com os prazos {} bloqueados {}", prazo, mensagem);

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // alterar parametro para nao solicitar senha servidor
        parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("019"), CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
        EConsigInitializer.limparCache();

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa1);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("CARTAO DE CREDITO - RESERVA - 019");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        // preencher com prazo não cadastrado
        reservarMargemPage.preencherNumeroPrestacao(prazo);
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviarComErro();

        assertEquals(mensagem, econsigHelper.getMensagemErro(getWebDriver()));

    }

    @Entao("tentar cadastrar indice com dados invalidos")
    public void tentarCadastrarIndice() {
        log.info("Então tentar cadastrar índice com dados invalidos");

        manutencaoConsignatariaPage.clicarNovoIndice();
        manutencaoConsignatariaPage.preencherDescricaoIndice("Indice invalido");

        manutencaoPostoPage.clicarSalvar();
        assertEquals("O código do índice deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherCodigoIndice("05");
        manutencaoConsignatariaPage.preencherDescricaoIndice("");
        manutencaoPostoPage.clicarSalvar();
        assertEquals("A descrição do índice deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

    }

    @Entao("verifica que nao exibe o servico bloqueado para a consignataria")
    public void verificaServicoBloqueado() {
        log.info("Então verifica que não exibe o serviço bloqueado para a consignatária");

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        final String svcCodigoCartao = servicoService.retornaSvcCodigo("020");
        final String csaCodigo = usuarioService.getCsaCodigo(loginCsa2.getLogin());
        final String orgCodigo = orgaoService.obterOrgaoPorIdentificador("213464140").getOrgCodigo();
        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigoCartao, orgCodigo, csaCodigo);
        final CorrespondenteConvenio convenioCor = convenioService.getConvenioCorrespondente(usuarioService.getCorCodigo(loginCor.getLogin()), convenio.getCnvCodigo());
        // verifica o status no banco
        assertEquals(CodedValues.SCV_INATIVO, convenio.getScvCodigo());
        assertEquals(CodedValues.SCV_INATIVO, convenioCor.getScvCodigo());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa2);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // verificar que não exibe o serviço bloqueado
        assertFalse(manutencaoConsignatariaPage.isExibeServico("CARTAO DE CREDITO - LANCAMENTO - 020"));
    }

    @Entao("verifica que exibe o servico desbloqueado para a consignataria")
    public void verificaServicoDesbloqueado() {
        log.info("Então verifica que exibe o serviço desbloqueado para a consignatária");

        final String svcCodigo = servicoService.retornaSvcCodigo("036");
        final String csaCodigo = usuarioService.getCsaCodigo(loginCsa2.getLogin());
        final String orgCodigo = orgaoService.obterOrgaoPorIdentificador("213464140").getOrgCodigo();
        final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(svcCodigo, orgCodigo, csaCodigo);

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // verifica o status no banco
        assertEquals(CodedValues.SCV_ATIVO, convenio.getScvCodigo());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa2);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // verificar que exibe o serviço desbloqueado
        assertFalse(manutencaoConsignatariaPage.isExibeServico("EMPRESTIMO MARGEM 3 - 036"));

        // verificar que consegue reservar margem com o servico desbloqueado
        reservarMargemPage.selecionarServico("EMPRESTIMO MARGEM 3 - 036");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
    }

    @Entao("csa reserva margem com o indice cadastrado {string}")
    public void reservarMargemComIndice(String indice) {
        log.info("Então csa reserva margem com o índice cadastrado {}", indice);

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // alterar parametro para nao solicitar senha servidor
        parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("001"), CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
        EConsigInitializer.limparCache();

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa2);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        // verificar o indice cadastrado
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
        assertEquals(indice, manutencaoConsignatariaPage.getIndiceReservarMargem());

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        reservarMargemPage.preencherValorLiquidoLiberado("200");
        reservarMargemPage.preencherNumeroPrestacao("9");
        reservarMargemPage.selecionarIndice(indice + " - Indice 1");
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Dados da consignação");
        econsigHelper.verificaTextoPagina(getWebDriver(), "Visualizar Consignação");
        assertEquals("0" + indice, manutencaoConsignatariaPage.getTextoIndiceReservarMargem());
    }

    @Entao("csa tenta cadastrar mais uma reserva margem com o indice cadastrado {string}")
    public void tentaCadastrarReservarMargemComIndice(String indice) {
        log.info("Então csa tenta cadastrar mais uma reserva margem com o índice cadastrado {}", indice);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        // verificar o indice cadastrado
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
        assertEquals(indice, manutencaoConsignatariaPage.getIndiceReservarMargem());

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        reservarMargemPage.preencherValorLiquidoLiberado("200");
        reservarMargemPage.preencherNumeroPrestacao("9");
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviarComErro();
    }

    @Entao("csa pode criar reserva com os prazos cadastrados")
    public void criarReservaPrazosCadastrados() {
        log.info("Então csa pode criar reserva com os prazos cadastrados");

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // alterar parametro para nao solicitar senha servidor
        parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("019"), CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
        EConsigInitializer.limparCache();

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa1);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("CARTAO DE CREDITO - RESERVA - 019");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        // preencher com prazo cadastrado
        reservarMargemPage.preencherNumeroPrestacao("7");
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Dados da consignação");
        econsigHelper.verificaTextoPagina(getWebDriver(), "Visualizar Consignação");
        assertEquals("7", manutencaoConsignatariaPage.getTextoPrazosReservarMargem());
    }

    @Entao("exibe mensagem de erro ao tentar cadastrar com prazo nao cadastrado {string}")
    public void tentaCriarReservaPrazosNaoCadastrados(String mensagem) {
        log.info("Então exibe mensagem de erro ao tentar cadastrar com prazo não cadastrado {}", mensagem);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("CARTAO DE CREDITO - RESERVA - 019");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        // preencher com prazo não cadastrado
        reservarMargemPage.preencherNumeroPrestacao("9");
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviarComErro();

        assertEquals(mensagem, econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("csa reserva margem com um dos indices cadastrado")
    public void reservarMargemComUmDosIndices() {
        log.info("Então csa reserva margem com um dos índices cadastrado");

        // incluir item menu favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa2.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // alterar parametro para nao solicitar senha servidor
        parametroSistemaService.configurarParametroServicoCse(servicoService.retornaSvcCodigo("020"), CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA, "0");
        EConsigInitializer.limparCache();

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa2);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        // reservar margem
        reservarMargemPage.selecionarServico("CARTAO DE CREDITO - LANCAMENTO - 020");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        // verificar o indice cadastrado
        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
        assertEquals("05 - Indice 05", manutencaoConsignatariaPage.getIndicesReservarMargem().get(1).getText());
        assertEquals("07 - Indice 07", manutencaoConsignatariaPage.getIndicesReservarMargem().get(2).getText());

        // preencher outros campos
        reservarMargemPage.preencherValorPrestacao("10");
        reservarMargemPage.preencherNumeroPrestacao("9");
        manutencaoConsignatariaPage.selecionarIndiceReservarMargem("07 - Indice 07");
        reservarMargemPage.clicarConfirmar();
        reservarMargemPage.clicarEnviar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Dados da consignação");
        econsigHelper.verificaTextoPagina(getWebDriver(), "Visualizar Consignação");
        assertEquals("007", manutencaoConsignatariaPage.getTextoIndiceReservarMargem());
    }

    @Entao("tentar criar consignataria sem informar campos obrigatorios")
    public void naoInformarNatureza() {
        log.info("Então tentar criar consignatária sem informar campos obrigatórios");

        acoesUsuarioPage.clicarMaisAcoes();
        manutencaoConsignatariaPage.clicarCriarNovaConsignataria();

        // salvar sem informar nenhum campo
        manutencaoPostoPage.clicarSalvar();
        assertEquals("O código da consignatária deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherCodigo("004");
        manutencaoPostoPage.clicarSalvar();
        assertEquals("A descrição da consignatária deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

        manutencaoConsignatariaPage.preencherNome("Banco Bradesco");
        manutencaoPostoPage.clicarSalvar();
        assertEquals("A natureza da consignatária deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Entao("exibe a tela com a lista de consignatarias")
    public void exibeTelaListaConsignatarias() {
        log.info("Entao exibe a tela com a lista de consignatárias");

        econsigHelper.verificaTextoPagina(getWebDriver(), "Liberação de arquivo de movimento");
        assertTrue(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertTrue(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertTrue(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("exibe as consignataria com o filtro Nome")
    public void retornaConsignatariasPorNome() {
        log.info("Entao exibe as consignatária com o filtro Nome");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertFalse(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertFalse(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertFalse(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("exibe as consignataria com o filtro Codigo")
    public void retornaConsignatariasPorCodigo() {
        log.info("Entao exibe as consignatária com o filtro Código");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertFalse(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertFalse(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("exibe as consignataria com o filtro Codigo de Verba")
    public void retornaConsignatariasPorCodigoVerba() {
        log.info("Entao exibe as consignatária com o filtro Código de Verba");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertFalse(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertFalse(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertFalse(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("exibe as consignataria com o filtro Bloqueado")
    public void retornaConsignatariasPorBloqueado() {
        log.info("Entao exibe as consignatária com o filtro Bloqueado");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("Nenhum registro encontrado."));
        assertFalse(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertFalse(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertFalse(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertFalse(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));

    }

    @Entao("exibe as consignataria com o filtro Desbloqueado")
    public void retornaConsignatariasPorDesbloqueado() {
        log.info("Entao exibe as consignatária com o filtro Desbloqueado");

        // verifica que exibe somente o usuario selecionado
        assertTrue(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertTrue(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertTrue(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("exibe as consignataria com o filtro Natureza")
    public void retornaConsignatariasPorNatureza() {
        log.info("Entao exibe as consignatária com o filtro Natureza");

        // verifica que exibe somente o usuario selecionado
        assertFalse(getWebDriver().getPageSource().contains("BANCO BRASIL"));
        assertFalse(getWebDriver().getPageSource().contains("BANCO TREINAMENTO"));
        assertTrue(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
        assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"));
    }

    @Entao("verifica que csa {string} nao consegue logar")
    public void verificarNaoLoga(String usuario) {
        log.info("Entao verifica que csa {} não consegue logar", usuario);

        // verifica o status no banco
        assertEquals("S", consignatariaService.getConsignataria(usuarioService.getCsaCodigo(usuario)).getCsaExigeEnderecoAcesso());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.login(usuario, LoginValues.csa1.getSenha());

        assertEquals("USUÁRIO OU SUA ENTIDADE DEVE POSSUIR IPS DE ACESSO CADASTRADOS NO SISTEMA.", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica que csa {string} consegue logar")
    public void verificarCsaLoga(String usuario) {
        log.info("Entao verifica que csa {} consegue logar", usuario);

        // verifica o status no banco
        assertEquals("S", consignatariaService.getConsignataria(usuarioService.getCsaCodigo(usuario)).getCsaExigeEnderecoAcesso());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.login(usuario, LoginValues.csa1.getSenha());

        econsigHelper.verificaTextoPagina(getWebDriver(), "Página inicial");
        assertEquals("eConsig - Principal", getWebDriver().getTitle());
    }

    @Entao("verifica que csa {string} nao consegue reservar margem")
    public void verificarNaoReservaMargem(String usuario) {
        log.info("Entao verifica que csa {} não consegue reservar margem", usuario);

        // incluir menu no favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(usuario).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // verifica o status no banco
        assertEquals("0", consignatariaService.getConsignataria(usuarioService.getCsaCodigo(usuario)).getCsaAtivo().toString());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.login(usuario, LoginValues.csa1.getSenha());

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        assertEquals("NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS A CONSIGNATÁRIA 'BANCO TREINAMENTO' ESTÁ BLOQUEADA.", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica que csa {string} nao consegue incluir consignacao")
    public void verificarNaoIncluirConsignacao(String usuario) {
        log.info("Entao verifica que csa {} não consegue reservar margem", usuario);

        // incluir menu no favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(usuario).getUsuCodigo(), Integer.toString(ItemMenuEnum.INCLUIR_CONSIGNACAO.getCodigo()));
        // verifica o status no banco
        assertEquals("N", consignatariaService.getConsignataria(usuarioService.getCsaCodigo(usuario)).getCsaPermiteIncluirAde());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.login(usuario, LoginValues.csa1.getSenha());

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosIncluirConsignacao();

        reservarMargemPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47 - 213464140");
        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        assertEquals("NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS A CONSIGNATÁRIA 'BANCO TREINAMENTO' ESTÁ BLOQUEADA.", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica que csa consegue reservar margem")
    public void verificarReservaMargem() {
        log.info("Entao verifica que não consegue reservar margem");

        // incluir menu no favoritos
        itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa1.getLogin()).getUsuCodigo(), Integer.toString(ItemMenuEnum.RESERVAR_MARGEM.getCodigo()));
        // verifica o status no banco
        assertEquals("1", consignatariaService.getConsignataria(usuarioService.getCsaCodigo(loginCsa1.getLogin())).getCsaAtivo().toString());

        // logar com csa
        loginPage.acessarTelaLogin();
        loginPage.loginSimples(loginCsa1);

        // reservar margem
        menuPage.acessarMenuFavoritos();
        menuPage.acessarFavoritosReservarMargem();

        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
        reservarMargemPage.clicarPesquisar();

        econsigHelper.verificaTextoPagina(getWebDriver(), "Confirmação dos dados");
        assertFalse(getWebDriver().getPageSource().contains("NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS A CONSIGNATÁRIA 'BANCO TREINAMENTO' ESTÁ BLOQUEADA."));
    }

    @E("clicar na ação Editar Posto de Parâmetro")
    public void selecionarMenuEditarPostoParametro() {
        log.info("clicar na ação Editar Posto de Parâmetro");
        acoesUsuarioPage.clicarAcoes();
        manutencaoConsignatariaPage.clicarAcaoEditarPostoGraduacao();
    }

    @Entao("deve ser exibida a tela Editar parâmetros de posto de graduação")
    public void verificaTituloPagina() {
        log.info("Então deve ser exibida a tela Editar parâmetros de posto de graduação");
        assertEquals("Editar valor fixo por posto de graduação", editarParametroPostoGraduacaoPage.tituloPagina());
    }

    @Entao("que não tenha a ação Editar Posto de Parâmetro")
    public void verificaQueNaoTemAcaoEditarPostoDeParametro() {
        log.info("Então que não tenha a ação Editar Posto de Parâmetro");
        acoesUsuarioPage.clicarAcoes();
        final List<WebElement> acaoPosto = manutencaoConsignatariaPage.listarAcoes().stream().filter(acao -> acao.getText().equals("Editar parâm. posto graduação")).toList();
        assertTrue(acaoPosto.isEmpty());
    }

}
