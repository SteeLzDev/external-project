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
import com.zetra.econsig.enomina.soap.client.CancelarCompraClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.compra.CancelarCompraResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CancelarCompraTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	Long adeNumero = (long) 60922;
	private final String codigoMotivo = "01";

	@Autowired
    private CancelarCompraClient cancelarCompraClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Test
	public void cancelarCompraComStatusAguardandoConfirmacao() {
		log.info("Cancelar compra com status aguardando confirmação");

		// ade com status aguardando confirmação
		adeNumero = (long) 60919;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarCompraResponse.getMensagem());
		assertEquals("000", cancelarCompraResponse.getCodRetorno().getValue());
		assertTrue(cancelarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco ade nova para cancelada
		assertEquals("7", autDesconto.getSadCodigo());
		// ade antiga alterou para status deferido
		assertEquals("4", autDescontoService.getAde("60913").getSadCodigo());
		// verificar o ocorrências para registro do cancelamento da operação de compra
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(
				CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA, autDesconto.getAdeCodigo()));
	}

	@Test
	public void cancelarCompraComStatusAguardandoDeferimento() {
		log.info("Cancelar compra com status aguardando deferimento");

		// ade com status aguardando deferimento
		adeNumero = (long) 60923;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarCompraResponse.getMensagem());
		assertEquals("000", cancelarCompraResponse.getCodRetorno().getValue());
		assertTrue(cancelarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco ade nova para cancelada
		assertEquals("7", autDesconto.getSadCodigo());
		// ade antiga alterou para status deferido
		assertEquals("4", autDescontoService.getAde("60914").getSadCodigo());
	}

	@Test
	public void cancelarCompraComCSACompradoraDeConsignacaoQueTevePagamentoSaldoDevedor() {
		log.info("Cancelar compra com CSA compradora de consignação que teve pagamento saldo devedor");

		// ade com pagamento saldo devedor
		adeNumero = (long) 60921;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", cancelarCompraResponse.getMensagem());
		assertEquals("000", cancelarCompraResponse.getCodRetorno().getValue());
		assertTrue(cancelarCompraResponse.isSucesso());

		final AutDesconto autDesconto = autDescontoService.getAde(adeNumero.toString());

		// verifica se alterou o status no banco ade nova para cancelada
		assertEquals("7", autDesconto.getSadCodigo());
		// ade antiga alterou para status deferido
		assertEquals("4", autDescontoService.getAde("60916").getSadCodigo());
		// verificar o ocorrências para registro do cancelamento da operação de compra
		assertNotNull(ocorrenciaAutorizacaoService.getOcorrenciaAutorizacaoPorAde(
				CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA, autDesconto.getAdeCodigo()));
	}

	@Test
	public void tentarCancelarCompraDeConsignacaoLiquidadaComCSACompradora() {
		log.info("Tentar cancelar compra de consignação liquidada com CSA compradora");

		// ade liquidada
		adeNumero = (long) 60927;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(),
				adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarCompraResponse.getMensagem());
		assertEquals("294", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());
	}

	@Test
	public void tentarCancelarCompraComStatusDeferida() {
		log.info("Tentar cancelar compra com status deferida");

		// ade com status deferida
		adeNumero = (long) 60926;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarCompraResponse.getMensagem());
		assertEquals("294", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraQueConsignacaoTevePagamentoSaldoDevedorComCSAVendedora() {
		log.info("Tentar cancelar compra que consignação teve pagamento saldo devedor");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();

		// ade com pagamento saldo devedor
		adeNumero = (long) 60920;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse("csa", loginCsa.getSenha(),
				adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarCompraResponse.getMensagem());
		assertEquals("294", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());
	}

	@Test
	public void tentarCancelarCompraDeConsignacaoNaoExistente() {
		log.info("Tentar cancelar compra de consignação não existente");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 60800, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarCompraResponse.getMensagem());
		assertEquals("294", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());
	}

	@Test
	public void tentarCancelarCompraDeConsignacaoJaCancelada() {
		log.info("Tentar cancelar compra de consignação já cancelada");

		// ade com status cancelada
		adeNumero = (long) 60929;

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", cancelarCompraResponse.getMensagem());
		assertEquals("294", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("7", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraSemInformarAdeNumero() {
		log.info("Tentar cancelar compra sem informar motivo operação");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 0, codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.", cancelarCompraResponse.getMensagem());
		assertEquals("322", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());
	}

	@Test
	public void tentarCancelarCompraSemInformarMotivoDeOperacao() {
		log.info("Tentar cancelar compra sem informar motivo operação");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "");

		assertEquals("O motivo da operação deve ser informado.", cancelarCompraResponse.getMensagem());
		assertEquals("445", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraComMotivoDaOperacaoInvalido() {
		log.info("Tentar cancelar compra com motivo da operação inválido");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "001");

		assertEquals("O motivo da operação inválido.", cancelarCompraResponse.getMensagem());
		assertEquals("401", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraComUsuarioInvalido() {
		log.info("Tentar cancelar compra com usuário inválido");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse("csa1", loginCsa.getSenha(),
				adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarCompraResponse.getMensagem());
		assertEquals("358", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraComSenhaInvalida() {
		log.info("Tentar cancelar compra com senha inválida");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse(loginCsa.getLogin(),
				"ser12345", adeNumero, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", cancelarCompraResponse.getMensagem());
		assertEquals("358", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraComIPDeAcessoInvalido() {
		log.info("Tentar cancelar compra com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse("csa", loginCsa.getSenha(),
				adeNumero, codigoMotivo);

		assertEquals("IP de acesso inválido", cancelarCompraResponse.getMensagem());
		assertEquals("362", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraSemInformarUsuario() {
		log.info("Tentar cancelar compra sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarCompraClient.getResponse("", loginCsa.getSenha(), adeNumero, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraSemInformarSenha() {
		log.info("Tentar cancelar compra sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarCompraClient.getResponse(loginCsa.getLogin(), "", adeNumero, codigoMotivo);
        });

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarCancelarCompraComUsuarioSemPermissao() {
		log.info("Tentar cancelar compra com usuário sem permissão");

		final CancelarCompraResponse cancelarCompraResponse = cancelarCompraClient.getResponse("cse", "cse12345", adeNumero,
				codigoMotivo);

		assertEquals("O usuário não tem permissão para executar esta operação", cancelarCompraResponse.getMensagem());
		assertEquals("329", cancelarCompraResponse.getCodRetorno().getValue());
		assertFalse(cancelarCompraResponse.isSucesso());

		// verifica que nao alterou o status no banco
		assertEquals("1", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}
}
