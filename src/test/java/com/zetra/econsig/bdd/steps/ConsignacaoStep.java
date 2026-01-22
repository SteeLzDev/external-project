package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.DetalharConsignacaoPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.RegistroServidorService;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsignacaoStep {

	private final ConditionFactory await = await().atMost(50, TimeUnit.SECONDS);

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private com.zetra.econsig.service.UsuarioServiceTest UsuarioService;

    @Autowired
    private EconsigHelper econsigHelper;

	private DetalharConsignacaoPage detalharConsignacaoPage;

    @Before
    public void setUp() throws Exception {
        detalharConsignacaoPage = new DetalharConsignacaoPage(getWebDriver());
    }

	@E("Contrato para servidor {string} com {string} para {string} com status {string}	cujo login responsavel {string}")
	public void novo_contrato_para_convenio_servidor_dado(String rseCodigo, String adeNumero, String cnvCodigo, String sadCodigo, String loginResponsavel) throws Throwable {
		log.info("Criando contrato no banco com adeNumero: " + adeNumero);

		autDescontoService.inserirAutDesconto(adeNumero, sadCodigo, rseCodigo, cnvCodigo, UsuarioService.getUsuario(loginResponsavel).getUsuCodigo(), 100.0f, Integer.parseInt(adeNumero), 10, Short.parseShort("1"));
	}

    @E("servidor {string} com status {string}")
    public void muda_status_registro_servidor(String rseCodigo, String srsCodigo) throws InterruptedException {
        log.info("Alterando status do registro servidor " + rseCodigo + " para " + srsCodigo);
        registroServidorService.alterarStatusRegistroServidor(rseCodigo, srsCodigo);
    }

	@E("confere contrato com adeNumero {string} com status {string}")
	public void confere_status_contrato_adenumero(String adeNumero, String sadCodigo) throws InterruptedException {
		await.atLeast(Duration.ofMillis(7000));
		final AutDesconto adeResult = autDescontoService.getAde(adeNumero);

		assertNotNull(adeResult);
		assertEquals(sadCodigo, adeResult.getSadCodigo());
	}

	@Entao("exibe mensagem de sessao de sucesso {string}")
	public void exibe_mensagem_de_sessao_sucesso(String msgSessao) {
		assertEquals(detalharConsignacaoPage.checkMensagemSessaoSucersso().toLowerCase(), msgSessao.trim().toLowerCase());
	}

	@Entao("exibe mensagem de sessao de erro {string}")
	public void exibe_mensagem_sessao_erro(String msgSessao) {
	    assertEquals(econsigHelper.getMensagemErro(getWebDriver()).toLowerCase(), msgSessao.trim().toLowerCase());
	}

	@E("exibe tela de detalhe da consignacao")
	public void exibe_tela_detalhe_consignacao() {
	    detalharConsignacaoPage.carregarDetalheConsignacao();
	}

	@E("Usuario clica no botao acoes")
	public void clica_botao_acoes() {
	    detalharConsignacaoPage.clicarBotaoAcoes();
	}

	@E("Usuario seleciona opcao {string} no botao acoes")
	public void seleciona_acao_botao_acoes(String acao) {
	    detalharConsignacaoPage.selecionarAcao(acao);
	}
}
