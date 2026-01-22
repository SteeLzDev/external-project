package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dao.CampoSistemaDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.AlterarConsignacaoClient;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.service.CampoSistemaService;
import com.zetra.econsig.soap.AlterarConsignacaoResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AlterarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final Long adeNumero = Long.valueOf(23);
	private final String valorParcela = "95";
	private final String prazo = "5";
	private final String nomeArquivo = "carlotajoaquina.pdf";
	private final String nomeArquivoDois = "carolotajoaquina2.pdf";
	private final String arquivo = "src/test/resources/files/arquivo_para_teste.pdf";
	private final String arquivoDois = "src/test/resources/files/arquivo_para_teste2.pdf";

	private final String valorParcelaPadrao = "100";

	private final String casChaveNovoValor = "alterarConsignacao_novoValor";

	@Autowired
	private CampoSistemaService campoSistemaService;

	@Autowired
	private CampoSistemaDao campoSistemaDao;

	@Autowired
    private AlterarConsignacaoClient alterarConsignacaoClient;

	@Test
	public void alterarConsignacaoComSucesso() throws IOException {
		log.info("Alterar Consignação com sucesso.");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, prazo, nomeArquivo, arquivo);

		assertEquals("Operação realizada com sucesso.", alterarConsignacaoResponse.getMensagem());
		assertEquals("000", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertEquals("Sr. BOB da Silva Shawn", alterarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", alterarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alterarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alterarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("123456", alterarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", alterarConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", alterarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", alterarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", alterarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", alterarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("FINANCIAMENTO DE DÍVIDA", alterarConsignacaoResponse.getBoleto().getValue().getServico());
		assertEquals("Deferida", alterarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("4", alterarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

	}

	@Test
	public void alterarConsignacaoComPrazoMaior() throws IOException {
		log.info("Alterar consignação com prazo maior que atual");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, "10", nomeArquivo, arquivo);


		assertEquals("O novo prazo não pode ser maior do que o prazo atual do contrato.", alterarConsignacaoResponse.getMensagem());
		assertEquals("253", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}

	@Test
	public void alterarConsignacaoSenhaIncorreta() throws IOException {
		log.info("Alterar consignação com senha de usuário incorreta");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				"abc12345", adeNumero, valorParcela, prazo, nomeArquivo, arquivo);


		assertEquals("Usuário ou senha inválidos", alterarConsignacaoResponse.getMensagem());
		assertEquals("358", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}


	@Test
	public void alterarConsignacaoUsuarioSemPermissao() throws IOException {
		log.info("Alterar consignação com usuário sem permissão");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse("zetra_igor",
				"abc12345", adeNumero, valorParcela, prazo, nomeArquivo, arquivo);


		assertEquals("O usuário não tem permissão para executar esta operação", alterarConsignacaoResponse.getMensagem());
		assertEquals("329", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}

	@Test
	public void alterarConsignacaoInexistente() throws IOException {
		log.info("Alterar consignação inexistente.");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), Long.valueOf(0001), valorParcela, prazo, nomeArquivo, arquivo);

		assertEquals("Nenhuma consignação encontrada", alterarConsignacaoResponse.getMensagem());
		assertEquals("294", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}

	@Test
	public void alterarConsignacaoValorParcelaZero() throws IOException {
		log.info("Alterar consignação com valor parcela igual a zero");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, "0", prazo, nomeArquivo, arquivo);

		assertEquals("O valor da prestação deve ser maior do que zero.", alterarConsignacaoResponse.getMensagem());
		assertEquals("334", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}

	@Test
	public void alterarConsignacaoNomeArquivoInvalido() throws IOException {
		log.info("Altear consignação com nome arquivo inválido");

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, prazo, "teste.asd", arquivo);

		assertEquals("O arquivo teste.asd possui extensão não permitida.<br><br>Lista de extensões permitidas: .doc, .pdf, .xls, .docx, .xlsx, .txt, .csv", alterarConsignacaoResponse.getMensagem());
		assertEquals("430", alterarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alterarConsignacaoResponse.isSucesso());

	}

	//DESENV-19575 - Conferir campos dos contratos após alteração
	@Test
	public void tentarAlterarCampoDeValorDeParcelaDeUmaConsignaçãoComOCampoBloqueado() throws IOException {
		log.info("Bloqueia campo novo valor para não ser alterado, e tenta alterar valor de parcela");

		campoSistemaService.alterarCampoSistema(casChaveNovoValor, "N");
		assertEquals("N", campoSistemaDao.findByCasChave(casChaveNovoValor).getCasValor());
		ShowFieldHelper.reset();

		AlterarConsignacaoResponse alterarConsignacaoResponse = alterarConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, "95", prazo, nomeArquivoDois, arquivoDois);

		assertEquals(0, Double.valueOf(valorParcelaPadrao).compareTo(alterarConsignacaoResponse.getBoleto().getValue().getValorParcela()));

		//Reseta valor do campo para padrao
		campoSistemaService.alterarCampoSistema(casChaveNovoValor, "S");
		ShowFieldHelper.reset();
	}

}