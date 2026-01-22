package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.InformarPagamentoSaldoDevedorClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.soap.compra.InformarPagamentoSaldoDevedorResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InformarPagamentoSaldoDevedorTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private Long adeNumero = (long) 60938;
	private final String observacao = "Automação";
	private final String nomeArquivo = "arquivo_boleto.pdf";
	private final String arquivo = "src/test/resources/files/arquivo_boleto.pdf";

	@Autowired
    private InformarPagamentoSaldoDevedorClient informarPagamentoSaldoDevedorClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Test
	public void informarPagamentoSaldoDevedorComSucesso() throws Exception {
		log.info("Informar pagamento saldo devedor com sucesso");

		adeNumero = (long) 60939;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Operação realizada com sucesso.",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando liquidação
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro do pagamento do saldo
		assertNotNull(
				ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR,
						autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
	}

	@Test
	public void informarPagamentoSaldoDevedorSemAnexoComSucesso() {
		log.info("Informar pagamento saldo devedor sem anexo com sucesso");

		parametroSistemaService
				.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR, "N");
		ENominaInitializer.limparCache();

		adeNumero = (long) 60942;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero);

		assertEquals("Operação realizada com sucesso.",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("000", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertTrue(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica se alterou o status do relacionamento para aguardando liquidação
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
		// verificar ocorrências para registro do pagamento do saldo
		assertNotNull(
				ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR,
						autDescontoService.getAde(adeNumero.toString()).getAdeCodigo()));
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorParaAdeComMaisDeUmRelacionamento() throws Exception {
		log.info("Tentar informar pagamento saldo devedor para ade com mais de um relacionamento");

		// ade com dois relacionamentos
		adeNumero = (long) 29;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("MAIS DE UM RELACIONAMENTO DE PORTABILIDADE PARA A CONSIGNAÇÃO ENCONTRADA.",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("423", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComStatusAguardandoSaldoDevedor() throws Exception {
		log.info("Informar pagamento saldo devedor com status aguardando saldo devedor");

		// ade com status aguardando saldo devedor
		adeNumero = (long) 60915;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Nenhuma consignação encontrada", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("1", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorDeConsignacaoLiquidada() throws Exception {
		log.info("Tentar informar pagamento saldo devedor de consignação liquidada");

		// ade liquidada
		adeNumero = (long) 60925;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Nenhuma consignação encontrada", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorDeConsignacaoNaoExistente() throws Exception {
		log.info("Tentar informar pagamento saldo devedor de consignação não existente");

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 60800, observacao, nomeArquivo,
						arquivo);

		assertEquals("Nenhuma consignação encontrada", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorDeConsignacaoComPagamentoJaInformado() throws Exception {
		log.info("Tentar informar pagamento saldo devedor de consignação com pagamento ja informado");

		// ade com pagamento já informado
		adeNumero = (long) 28;

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Nenhuma consignação encontrada", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("294", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("3", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorSemInformarAdeNumero() throws Exception {
		log.info("Tentar informar pagamento saldo devedor sem informar ade numero");

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, observacao, nomeArquivo,
						arquivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("322", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorSemInformarAnexo() {
		log.info("Tentar informar pagamento saldo devedor sem informar anexo");

		parametroSistemaService
				.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR, "S");
		ENominaInitializer.limparCache();

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero);

		assertEquals(
				"Não pode ser informado o comprovante de pagamento de saldo devedor para esta consignação, pois o anexo é obrigatório.",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("547", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComAnexoInvalido() throws Exception {
		log.info("Tentar informar pagamento saldo devedor com anexo inválido");

		parametroSistemaService
				.configurarParametroSistemaCse(CodedValues.TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR, "S");
		ENominaInitializer.limparCache();

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, observacao, "arquivo.xml", arquivo);

		assertEquals(
				"O arquivo arquivo.xml possui extensão não permitida.<br><br>Lista de extensões permitidas: .doc, .pdf, .xls, .docx, .xlsx, .txt, .csv",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("430", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComUsuarioInvalido() throws Exception {
		log.info("Tentar informar pagamento saldo devedor com usuário inválido");

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse("csa1", loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Usuário ou senha inválidos", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("358", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComSenhaInvalida() throws Exception {
		log.info("Tentar informar pagamento saldo devedor com senha inválida");

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse(loginCsa.getLogin(), "ser12345", adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("Usuário ou senha inválidos", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("358", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComIPDeAcessoInvalido() throws Exception {
		log.info("Tentar informar pagamento saldo devedor com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("IP de acesso inválido", informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("362", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorSemInformarUsuario() throws Exception {
		log.info("Tentar informar pagamento saldo devedor sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarPagamentoSaldoDevedorClient.getResponse("", loginCsa.getSenha(), adeNumero, observacao, nomeArquivo, arquivo);
        });

        // verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorSemInformarSenha() throws Exception {
		log.info("Tentar informar pagamento saldo devedor sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            informarPagamentoSaldoDevedorClient.getResponse(loginCsa.getLogin(), "", adeNumero, observacao, nomeArquivo, arquivo);
        });

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}

	@Test
	public void tentarInformarPagamentoSaldoDevedorComUsuarioSemPermissao() throws Exception {
		log.info("Tentar informar pagamento saldo devedor com usuário sem permissão");

		final InformarPagamentoSaldoDevedorResponse informarPgtoSaldoDevedorResponse = informarPagamentoSaldoDevedorClient
				.getResponse("cse", "cse12345", adeNumero, observacao, nomeArquivo, arquivo);

		assertEquals("O usuário não tem permissão para executar esta operação",
				informarPgtoSaldoDevedorResponse.getMensagem());
		assertEquals("329", informarPgtoSaldoDevedorResponse.getCodRetorno().getValue());
		assertFalse(informarPgtoSaldoDevedorResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getStcCodigo());
	}
}
