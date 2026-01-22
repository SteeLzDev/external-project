package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.ReservarMargemPage;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.service.OrgaoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.ServidorService;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReservarMargemStep {

    @Autowired
    private EconsigHelper econsigHelper;

    private ReservarMargemPage reservarMargemPage;

    @Autowired
    private OrgaoService orgaoService;

    @Autowired
    private RegistroServidorService registroServidorService;

    @Autowired
    private ServidorService servidorService;

    @Autowired
    private ServicoService servicoService;

    @Before
    public void setUp() throws Exception {
        reservarMargemPage = new ReservarMargemPage(getWebDriver());
    }

    @Quando("Usuario seleciona Servico com identificador {string}")
    public void selecionaServicoEmprestimo(String servicoId) throws Throwable {
        log.info("Quando Usuario seleciona Servico com identificador {}", servicoId);

        reservarMargemPage.selecionarServicoPeloIdentificador(servicoId);
    }

    @Quando("Usuario seleciona Servico Emprestimo")
    public void selecionaServicoEmprestimo() throws Throwable {
        log.info("Quando Usuario seleciona Servico Emprestimo");

        reservarMargemPage.selecionarServico("EMPRÉSTIMO - 001");
    }

    @Quando("Usuario seleciona Servico Cartao de Credito")
    public void selecionaServicoCartaoCredito() throws Throwable {
        log.info("Quando Usuario seleciona Servico Cartão de Crédito");

        reservarMargemPage.selecionarServico("CARTAO DE CREDITO - LANCAMENTO - 020");
    }

    @E("Usuario preenche matricula de servidor")
    public void usuarioPreencheMatSuf() throws Throwable {
        log.info("E Usuario preenche matricula de servidor");

        reservarMargemPage.preencherMatricula(LoginValues.servidor1.getLogin());
    }

    @E("Usuario preenche matricula de servidor com {string}")
    public void usuarioPreencheMatricula(String rseMatricula) throws Throwable {
        log.info("E Usuario preenche matricula de servidor com {}", rseMatricula);

        reservarMargemPage.preencherMatricula(rseMatricula);
    }

    @E("Usuario clica no botao Pesquisar")
    public void clickPesquisar() throws Throwable {
        log.info("E Usuario clica no botao Pesquisar");

        reservarMargemPage.clicarPesquisar();
    }

    @Quando("Usuario seleciona Servico Mensalidade")
    public void usucsa_seleciona_Servico_Mensalidade() throws Throwable {
        log.info("E Usuario seleciona Servico Mensalidade");

        reservarMargemPage.selecionarServico("MENSALIDADE CONVÊNIO ODONTOLÓGICO I - 008");
    }

    @E("Usuario preenche campo valor parcela {string}")
    public void usuCSAPreencheValorPrestacao(String valorParcela) throws Throwable {
        log.info("E Usuario preenche campo valor parcela {}", valorParcela);

        reservarMargemPage.preencherValorPrestacao(valorParcela);
    }

    @E("Usuario marca prazo indeterminado")
    public void usuCSAPrazoIndeterminado() throws Throwable {
        log.info("E Usuario marca prazo indeterminado");

        reservarMargemPage.marcarPrazoIndeterminado();
    }

    @E("Usuario preenche campo numero prestacoes {string}")
    public void usuCSAPreencheNumPrestacao(String nroPrestacao) throws Throwable {
        log.info("E Usuario preenche campo numero prestacoes {}", nroPrestacao);

        reservarMargemPage.preencherNumeroPrestacao(nroPrestacao);
    }

    @E("Usuario seleciona numero prestacoes {string}")
    public void usuSelecionaNumPrestacao(String nroPrestacao) throws Throwable {
        log.info("E Usuario seleciona numero prestacoes {}", nroPrestacao);

        reservarMargemPage.selecionarNumeroPrestacao(nroPrestacao);
    }

    @E("Usuario preenche valor carencia {string}")
    public void usuCSAPreencheCarencia(String vlrCarencia) throws Throwable {
        log.info("E Usuario preenche valor carência {}", vlrCarencia);

        reservarMargemPage.preencherValorCarencia(vlrCarencia);
    }

    @E("Usuario preenche valor liquido {string}")
    public void preencheValorLiquido(String valorLiquido) throws Throwable {
        log.info("E Usuario preenche valor liquido {}", valorLiquido);

        reservarMargemPage.preencherValorLiquidoLiberado(valorLiquido);
    }

    @E("Usuario preenche valor CET {string}")
    public void preencheValorCET(String valorCET) throws Throwable {
        log.info("E Usuario preenche valor CET {}", valorCET);

        reservarMargemPage.preencherValorCET(valorCET);
    }

    @E("Usuario clica no botao Confirmar para prosseguir")
    public void clickBtnProsseguir() throws Throwable {
        log.info("E Usuario clica no botão Confirmar para prosseguir");

        reservarMargemPage.clicarConfirmar();
    }

    @E("Usuario clica no botao Confirmar")
    public void clicarConfirmarComErro() throws Throwable {
        log.info("E Usuario clica no botão Confirmar");

        reservarMargemPage.clicarConfirmarComErro();
    }

    @Entao("verifica se dados estao corretos {string}, {string} e {string}")
    public void verificarDadosCorretos(String parcela, String prazo, String valorLiquido) throws Throwable {
        log.info("Entao verifica se dados estão corretos {}, {} e {}", parcela, prazo, valorLiquido);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaConfirmar());
        assertTrue(reservarMargemPage.retornarValorLiquidoTelaConfirmar().contains(valorLiquido));
    }

    @Entao("verifica se dados estao corretos {string} e {string}")
    public void verificarDadosCorretos(String parcela, String prazo) throws Throwable {
        log.info("Entao verifica se dados estão corretos {} e {}", parcela, prazo);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaConfirmar());
    }

    @Entao("verifica se dados estao corretos {string}")
    public void verificarDadosCorretos(String parcela) throws Throwable {
        log.info("Entao verifica se dados estão corretos {}", parcela);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
    }

    @Entao("verifica se prazo Indeterminado esta correto")
    public void verificarPrazoIndeterminado() throws Throwable {
        log.info("Entao verifica se dado do prazo Indeterminado está correto");

        assertEquals("1", reservarMargemPage.retornarPrazoIndeterminadoTelaConfirmar());
    }

    @Entao("verifica se dados estao corretos {string}, {string}, {string} e {string}")
    public void verificarDadosCorretosCarencia(String parcela, String prazo, String valorLiquido, String valorCarencia) throws Throwable {
        log.info("Entao verifica se dados estão corretos {}, {}, {} e {}", parcela, prazo, valorLiquido, valorCarencia);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaConfirmar());
        assertTrue(reservarMargemPage.retornarValorLiquidoTelaConfirmar().contains(valorLiquido));
        assertEquals(valorCarencia + " (meses)", reservarMargemPage.retornarValorCarenciaTelaConfirmar());
    }

    @Entao("verifica se dados estao corretos para concluir reserva {string}, {string}, {string} e {string}")
    public void verificarDadosCorretosCET(String parcela, String prazo, String valorLiquido, String valorCet) throws Throwable {
        log.info("Entao verifica se dados estão corretos para concluir reserva {}, {}, {} e {}", parcela, prazo, valorLiquido, valorCet);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaConfirmar());
        assertTrue(reservarMargemPage.retornarValorLiquidoTelaConfirmar().contains(valorLiquido));
        assertTrue(reservarMargemPage.retornarValorCETTelaConfirmar().contains(valorCet));
    }

    @Quando("Usuario clica em Concluir")
    public void clicarConcluir() throws Throwable {
        log.info("Quando Usuario clica em Concluir");

        reservarMargemPage.clicarEnviar();
    }

    @Quando("Usuario clica em Concluir para prosseguir")
    public void clicarConcluirComErro() throws Throwable {
        log.info("Quando Usuario clica em Concluir");

        reservarMargemPage.clicarEnviarComErro();
    }

    @Entao("Sistema mostra tela de conclusao com as informacoes {string}, {string}, {string} e {string}")
    public void mostraTelaConclusao(String parcela, String prazo, String valorLiquido, String situacao) throws Throwable {
        log.info("Entao Sistema mostra tela de conclusão com as informações {}, {}, {} e {}", parcela, prazo, valorLiquido, situacao);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaSucesso().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaSucesso());
        assertTrue(reservarMargemPage.retornarValorLiquidoTelaSucesso().contains(valorLiquido));
        assertEquals(situacao, reservarMargemPage.retornarSituacao());

    }

    @Entao("Sistema mostra tela de conclusao com as informacoes {string}, {string} e {string}")
    public void mostraTelaConclusao(String parcela, String prazo, String situacao) throws Throwable {
        log.info("Entao Sistema mostra tela de conclusão com as informações {}, {} e {}", parcela, prazo, situacao);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaSucesso().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaSucesso());
        assertEquals(situacao, reservarMargemPage.retornarSituacao());

    }

    @Entao("Sistema mostra tela de conclusao com as informacoes {string}, {string}, {string}, {string} e {string}")
    public void mostraTelaConclusao(String parcela, String prazo, String valorLiquido, String valorCarencia, String situacao) throws Throwable {
        log.info("Entao Sistema mostra tela de conclusão com as informações {}, {}, {}, {} e {}", parcela, prazo, valorLiquido, valorCarencia, situacao);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaSucesso().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoTelaSucesso());
        assertTrue(reservarMargemPage.retornarValorLiquidoTelaSucesso().contains(valorLiquido));
        assertEquals(valorCarencia, reservarMargemPage.retornarValorCarenciaTelaSucesso());
        assertEquals(situacao, reservarMargemPage.retornarSituacao());
    }

    @Entao("com as iformacoes da taxa de juros {string} e {string}")
    public void mostraTelaConclusao(String valorCet, String valorCetAnual) throws Throwable {
        log.info("Entao com as iformações da taxa de juros {} e {}", valorCet, valorCetAnual);

        assertEquals(valorCet + ",00", reservarMargemPage.retornarValorCETTelaSucesso());
        assertEquals(valorCetAnual, reservarMargemPage.retornarValorCETAnualTelaSucesso());
    }

    @Entao("UsuCor verifica se dados estao corretos {string} e {string}")
    public void mostraTelaConclusaoUsuarioCor(String parcela, String prazo) throws Throwable {
        log.info("Entao UsuCor verifica se dados estão corretos {} e {}", parcela, prazo);

        assertTrue(reservarMargemPage.retornarPrestacaoTelaConfirmar().contains(parcela));
        assertEquals(prazo, reservarMargemPage.retornarPrazoUsuarioCorTelaConfirmar());
    }

    @Entao("Sistema lanca mensagem de erro de margem {string}")
    public void exibeMensagemErroLancamentoMargem(String mensagem) throws Throwable {
        log.info("Entao Sistema lança mensagem de erro de margem {}", mensagem);

        assertEquals(mensagem, reservarMargemPage.retornarMensagemErro());
    }

    @Entao("Sistema informa mensagem de erro de matricula {string}")
    public void exibeMensagemErroMatricula(String mensagem) throws Throwable { 
    	log.info("Entao sistema informa mensagem de erro de matricula {}", mensagem);
    	
    	assertEquals(mensagem, reservarMargemPage.retornarMensagemErro().replace("\n", "").replace("\r", ""));
    }
    
    @Entao("Sistema exibe mensagem com o erro {string}")
    public void exibeMensagemComErro(String mensagem) throws Throwable {
        log.info("Entao Sistema exibe mensagem com o erro {}", mensagem);

        assertTrue(reservarMargemPage.retornarMensagemErro().contains(mensagem));
    }

    @E("Sistema exibe mensagem de erro {string}")
    public void verificaMensagemAlertaMargem(String mensagem) throws Throwable {
        log.info("E Sistema exibe mensagem de erro {}", mensagem);

        assertTrue(econsigHelper.getMensagemPopUp(getWebDriver()).matches(mensagem));
    }

    @E("Sistema exibe alerta de erro {string}")
    public void verificaAlertaMargem(String mensagem) throws Throwable {
        log.info("E Sistema exibe alerta de erro {}", mensagem);

        if (!SeleniumHelper.isAlertPresent(getWebDriver())) {
            reservarMargemPage.clicarConfirmar();
        }

        assertEquals(mensagem, econsigHelper.getMensagemPopUp(getWebDriver()));

        while (SeleniumHelper.isAlertPresent(getWebDriver())) {
            getWebDriver().switchTo().alert().accept();
        }
    }

    @E("Remove o servidor de CPF {string} e Matricula {string} no Orgao {string}")
    public void removeServidorCriado(String cpf, String matricula, String orgaoId) throws Throwable {
        log.info("Remove o servidor de CPF {} e Matricula {} no Orgao {}", cpf, matricula, orgaoId);

        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgaoId);
        RegistroServidor registroServidor = registroServidorService.obterRegistroServidorPorMatriculaOrgao(matricula, orgao.getOrgCodigo());
        // Remove registro servidor e suas dependências
        registroServidorService.excluirRegistroServidor(registroServidor.getRseCodigo());
        servidorService.excluirServidor(registroServidor.getSerCodigo());
    }

    @E("Remove o servico de Identificador {string}")
    public void removeServicoCriado(String servicoId) throws Throwable {
        log.info("Remove o servico de Identificador {}", servicoId);
        Servico servico = servicoService.obterServicoPorIdentificador(servicoId);
        if (servico != null) {
            servicoService.excluirServico(servico.getSvcCodigo());
        }
    }

    @E("Remove o orgao de Identificador {string}")
    public void removeOrgaoCriado(String orgaoId) throws Throwable {
        log.info("Remove o orgao de Identificador {}", orgaoId);
        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgaoId);
        if (orgao != null) {
            orgaoService.excluirOrgao(orgao.getOrgCodigo());
        }
    }
}
