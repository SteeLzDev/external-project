package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.CancelarRenegociacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.soap.CancelarRenegociacaoResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CancelarRenegociacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final Long adeNumero = (long) 61016;
	private final String codigoMotivoOperacao = "";
	private final String obsMotivoOperacao = "";

	@Autowired
	private CancelarRenegociacaoClient cancelarRenegociacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Test
	public void cancelarRenegociacaoComSucesso() throws IOException {
		log.info("Cancelar Renegociação com sucesso.");

		final CancelarRenegociacaoResponse cancelarRenegociacaoResponse = cancelarRenegociacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("Operação realizada com sucesso.", cancelarRenegociacaoResponse.getMensagem());
		assertEquals("000", cancelarRenegociacaoResponse.getCodRetorno().getValue());
		assertEquals("Sr. BOB da Silva Shawn", cancelarRenegociacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", cancelarRenegociacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", cancelarRenegociacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", cancelarRenegociacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("123456", cancelarRenegociacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", cancelarRenegociacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", cancelarRenegociacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", cancelarRenegociacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("145", cancelarRenegociacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("Cancelada", cancelarRenegociacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("7", cancelarRenegociacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("csa2", cancelarRenegociacaoResponse.getHistoricos().get(0).getResponsavel());

		//validar novo numero de Ade gerado na renegociação
		assertNotNull(autDescontoService.getAde(String.valueOf(cancelarRenegociacaoResponse.getBoleto().getValue().getAdeNumero())));

	}

	@Test
	public void tentarCancelarRenegociacaoSemUsuarioeSenha() throws IOException {
		log.info("Tentar cancelar renegociação sem informar usuário e senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            cancelarRenegociacaoClient.getResponse("", "", (long) 61018, codigoMotivoOperacao, obsMotivoOperacao);
        });
	}

	@Test
	public void tentarCancelarrenegociacaoComAdeInvalida() throws IOException {
		log.info("Tentar cancelar renegociação com ADE inválida");

		final CancelarRenegociacaoResponse cancelarRenegociacaoResponse = cancelarRenegociacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 124578, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("Nenhuma consignação encontrada", cancelarRenegociacaoResponse.getMensagem());
		assertEquals("294", cancelarRenegociacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarRenegociacaoResponse.isSucesso());

	}

	@Test
	public void tentarCancelarrenegociacaoComMotivoInvalido() throws IOException {
		log.info("Tentar cancelar renegociação com motivo de operação inválido");

		final CancelarRenegociacaoResponse cancelarRenegociacaoResponse = cancelarRenegociacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 61018, "112233", obsMotivoOperacao);

		assertEquals("Tipo de motivo da operação não encontrado.", cancelarRenegociacaoResponse.getMensagem());
		assertEquals("356", cancelarRenegociacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarRenegociacaoResponse.isSucesso());

	}

	@Test
	public void tentarCancelarrenegociacaoComUsuarioSemPermissao() throws IOException {
		log.info("Tentar cancelar renegociação com usuario sem permissão");

		final CancelarRenegociacaoResponse cancelarRenegociacaoResponse = cancelarRenegociacaoClient.getResponse("zetra_igor",
				"abc12345", (long) 61018, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("O usuário não tem permissão para executar esta operação", cancelarRenegociacaoResponse.getMensagem());
		assertEquals("329", cancelarRenegociacaoResponse.getCodRetorno().getValue());
		assertFalse(cancelarRenegociacaoResponse.isSucesso());

	}

}
