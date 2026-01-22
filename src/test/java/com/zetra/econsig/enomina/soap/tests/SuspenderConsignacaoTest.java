package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.SuspenderConsignacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.soap.SuspenderConsignacaoResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SuspenderConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final Long adeNumero = Long.valueOf(26);
	private final String codigoMotivoOperacao = "04";
	private final String obsMotivoOperacao = "";

	@Autowired
    private SuspenderConsignacaoClient suspenderConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Test
	public void suspenderConsignacaoComSucesso() throws IOException {
		log.info("Suspender consignação com sucesso.");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("Operação realizada com sucesso.", suspenderConsignacaoResponse.getMensagem());
		assertEquals("000", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertEquals("Sr. BOB da Silva Shawn", suspenderConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", suspenderConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", suspenderConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", suspenderConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("123456", suspenderConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", suspenderConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", suspenderConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", suspenderConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("123", suspenderConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("FINANCIAMENTO DE DÍVIDA", suspenderConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals(8, suspenderConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals("Suspensa", suspenderConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("6", suspenderConsignacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("csa2", suspenderConsignacaoResponse.getHistoricos().get(0).getResponsavel());

		//validar novo numero de Ade gerado na renegociação
		assertNotNull(autDescontoService.getAde(String.valueOf(suspenderConsignacaoResponse.getBoleto().getValue().getAdeNumero())));

	}

	@Test
	public void tentarSuspenderConsignacaoComMotivoOperacaoInvalido() throws IOException {
		log.info("Tentar suspender consignacao com motivo operação inválido");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "xxx", obsMotivoOperacao);

		assertEquals("O motivo da operação inválido.", suspenderConsignacaoResponse.getMensagem());
		assertEquals("401", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(suspenderConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarSuspenderConsignacaoSemMotivoOperacao() throws IOException {
		log.info("Tentar suspender consignação sem motivo operação");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "", obsMotivoOperacao);

		assertEquals("O motivo da operação deve ser informado.", suspenderConsignacaoResponse.getMensagem());
		assertEquals("445", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(suspenderConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarSuspenderConsignacaoComAdeInexistente() throws IOException {
		log.info("Tentar suspender consignacação com ADE inexistente");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(457845), codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("Nenhuma consignação encontrada", suspenderConsignacaoResponse.getMensagem());
		assertEquals("294", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(suspenderConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarSuspenderConsignacaoComUsuarioeSenhaSemPermissoa() throws IOException {
		log.info("Tentar suspender consignação com usuário e senha sem permissão");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse("zetra_igor",
				"abc12345", adeNumero, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("O usuário não tem permissão para executar esta operação", suspenderConsignacaoResponse.getMensagem());
		assertEquals("329", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(suspenderConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarSuspenderConsignacaoComSenhaIncorreta() throws IOException {
		log.info("Tentar suspender consignação com senha incorreto");

		SuspenderConsignacaoResponse suspenderConsignacaoResponse = suspenderConsignacaoClient.getResponse("loginCsa.getLogin()",
				"abc1234567", adeNumero, codigoMotivoOperacao, obsMotivoOperacao);

		assertEquals("Usuário ou senha inválidos", suspenderConsignacaoResponse.getMensagem());
		assertEquals("358", suspenderConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(suspenderConsignacaoResponse.isSucesso());

	}


}
