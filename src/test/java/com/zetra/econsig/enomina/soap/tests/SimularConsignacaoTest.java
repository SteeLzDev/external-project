package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.SimularConsignacaoClient;
import com.zetra.econsig.soap.SimularConsignacaoResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SimularConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final String codVerba = "145";
	private final String codServico = "001";
	private final String matricula = "123456";
	private final String cpf = "092.459.399-79";
	private final String valorParcela = "100";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private final Integer prazo = 10;
	private final String valorLiberado = "1000.00";

	@Autowired
    private SimularConsignacaoClient simularConsignacaoClient;

	@Test
	public void simularConsignacaoComSucesso() throws IOException {
		log.info("Simular Consignação com sucesso.");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, prazo, valorLiberado, codVerba);

		assertEquals("Operação realizada com sucesso.", simularConsignacaoResponse.getMensagem());
		assertEquals("000", simularConsignacaoResponse.getCodRetorno().getValue());
		assertEquals("001", simularConsignacaoResponse.getSimulacoes().get(0).getConsignatariaCodigo());
		assertEquals("BB", simularConsignacaoResponse.getSimulacoes().get(0).getConsignataria());
		assertEquals("100.0", String.valueOf(simularConsignacaoResponse.getSimulacoes().get(0).getValorParcela()));
		assertEquals("1", String.valueOf(simularConsignacaoResponse.getSimulacoes().get(0).getRanking()));
		assertEquals("1.1", String.valueOf(simularConsignacaoResponse.getSimulacoes().get(0).getTaxaJuros()));
		assertEquals("EMPRÉSTIMO", simularConsignacaoResponse.getSimulacoes().get(0).getServico().getValue().toString());
		assertEquals("001", simularConsignacaoResponse.getSimulacoes().get(0).getServicoCodigo().getValue().toString());

	}

	@Test
	public void simularConsignacaoComValorLiberadoSemParcelaeSemPrazo() throws IOException {
		log.info("Silumar consignação com valor Liberado e sem valor parcela.");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            simularConsignacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento,
                                                 codServico, "", 0, valorLiberado, codVerba);
        });
	}

	@Test
	public void simularConsignacaoSemValorLiberadoComParcelaeSemPrazo() throws IOException {
		log.info("Silumar consignação com valor Liberado e sem valor parcela.");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, 0, "", codVerba);

		assertEquals("O número de parcelas deve ser informado.", simularConsignacaoResponse.getMensagem());
		assertEquals("431", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoSemCodigoVerba() throws IOException {
		log.info("Silumar consignação sem codigo verba");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, "", "", valorParcela, prazo, "", "");

		assertEquals("Nenhum convênio encontrado", simularConsignacaoResponse.getMensagem());
		assertEquals("290", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoComCodigoVerbaInvalido() throws IOException {
		log.info("Silumar consignação com codigo verba inválido");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, prazo, "", "111");

		assertEquals("Código da verba ou código do serviço inválido.", simularConsignacaoResponse.getMensagem());
		assertEquals("232", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoSemMatricula() throws IOException {
		log.info("Silumar consignação sem matricula");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "", "", codOrgao, codEstabelecimento, codServico, valorParcela, prazo, valorLiberado, codVerba);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", simularConsignacaoResponse.getMensagem());
		assertEquals("305", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoComMatriculaInvalida() throws IOException {
		log.info("Silumar consignação com matricula inválida");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "456789123", cpf, codOrgao, codEstabelecimento, codServico, valorParcela, prazo, valorLiberado, codVerba);

		assertEquals("Nenhum servidor encontrado", simularConsignacaoResponse.getMensagem());
		assertEquals("293", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoComUsuarioInvalido() throws IOException {
		log.info("Silumar consignação com usuario inválido");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse("xxxx",
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, prazo, valorLiberado, codVerba);

		assertEquals("Usuário ou senha inválidos", simularConsignacaoResponse.getMensagem());
		assertEquals("358", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoSemSenha() throws IOException {
		log.info("Silumar consignação com usuario inválido");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				"xxxx", matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, prazo, valorLiberado, codVerba);

		assertEquals("Usuário ou senha inválidos", simularConsignacaoResponse.getMensagem());
		assertEquals("358", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}

	@Test
	public void simularConsignacaoComPrazoInvalido() throws IOException {
		log.info("Silumar consignação com usuario inválido");

		final SimularConsignacaoResponse simularConsignacaoResponse = simularConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, codServico, valorParcela, 5, valorLiberado, codVerba);

		assertEquals("Nenhuma consignatária encontrada para o prazo informado.", simularConsignacaoResponse.getMensagem());
		assertEquals("295", simularConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(simularConsignacaoResponse.isSucesso());

	}


}
