package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ComprarContratoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.compra.ComprarContratoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ComprarContratoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final String codVerba = "145";
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final int prazo = 10;
	private final String valorParcela = "50";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private String svcCodigoEmprestimo;
	private final Integer carencia = 0;
	private Long adeNumero = (long) 60911;

	@Autowired
    private ComprarContratoClient comprarContratoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@BeforeEach
	public void setUp() throws Exception {
		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_INCIDE_MARGEM, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "0");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_POR_COMPRA, "10");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, "0");
		ENominaInitializer.limparCache();
	}

	@Test
	public void comprarContratoComSucesso() {
		log.info("Comprar contrato com sucesso");

		adeNumero = (long) 60910;

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Operação realizada com sucesso.", comprarContratoResponse.getMensagem());
		assertEquals("000", comprarContratoResponse.getCodRetorno().getValue());
		assertTrue(comprarContratoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", comprarContratoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", comprarContratoResponse.getBoleto().getValue().getCpf());
		assertEquals("MASCULINO", comprarContratoResponse.getBoleto().getValue().getSexo());
		assertEquals("S", comprarContratoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", comprarContratoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("Jose Augusto", comprarContratoResponse.getBoleto().getValue().getPai());
		assertEquals("Maria Raimunda", comprarContratoResponse.getBoleto().getValue().getMae());
		assertEquals("123456", comprarContratoResponse.getBoleto().getValue().getMatricula());
		assertEquals("213464140", comprarContratoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", comprarContratoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", comprarContratoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("145", comprarContratoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("3000.0", comprarContratoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("50.0", String.valueOf(comprarContratoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, comprarContratoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, comprarContratoResponse.getBoleto().getValue().getPagas());
		assertEquals("1.1", comprarContratoResponse.getBoleto().getValue().getTaxaJuros().getValue().toString());
		assertEquals("Aguard. Confirmação", comprarContratoResponse.getBoleto().getValue().getSituacao());
		assertEquals("001", comprarContratoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("1", comprarContratoResponse.getBoleto().getValue().getStatusCodigo());

		// verifica se alterou o status da ade antiga para "Aguard. Liquidação
		// Portabilidade"
		assertEquals("15", autDescontoService.getAde(String.valueOf(adeNumero)).getSadCodigo());
		// criou uma nova ade com status aguardando confirmacao
		assertEquals("1", autDescontoService
				.getAde(String.valueOf(comprarContratoResponse.getBoleto().getValue().getAdeNumero())).getSadCodigo());
	}

	@Test
	public void tentarComprarContratoUsandoOutraMargem() {
		log.info("Comprar contrato usando outra margem");

		adeNumero = (long) 60912;

		// alterar para incidir margem 2
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_INCIDE_MARGEM, "2");
		ENominaInitializer.limparCache();

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, "100", codOrgao, codEstabelecimento, carencia);

		assertEquals(
				"Não é possível renegociar este contrato pois o valor da parcela não pode ser maior que a soma dos demais contratos.",
				comprarContratoResponse.getMensagem());
		assertEquals("280", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComQuantidadeMaiorQueAMaximaConsignatariasPorCompra() throws Exception {
		log.info("Tentar comprar contrato com quantidade maior que a maxima consignatarias por compra");

		// alterar parametro 244 para valor zero (tb_param_sist_consignante)
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_POR_COMPRA, "0");
		ENominaInitializer.limparCache();

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, "100", codOrgao, codEstabelecimento, carencia);

		assertEquals("São permitidas no máximo 0 consignatárias por portabilidade.",
				comprarContratoResponse.getMensagem());
		assertEquals("São permitidas no máximo 0 consignatárias por portabilidade.", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComUsuarioInvalido() {
		log.info("Tentar comprar contrato com usuário inválido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse("csa1", loginCsa.getSenha(),
				loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Usuário ou senha inválidos", comprarContratoResponse.getMensagem());
		assertEquals("358", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComSenhaInvalida() {
		log.info("Tentar comprar contrato com senha inválida");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				"abc145", loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Usuário ou senha inválidos", comprarContratoResponse.getMensagem());
		assertEquals("358", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComUsuarioSemPermissao() {
		log.info("Tentar comprar contrato com usuário sem permissão");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse("zetra_igor", "abc12345",
				loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("O usuário não tem permissão para executar esta operação", comprarContratoResponse.getMensagem());
		assertEquals("329", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemCodigoDaVerbaOuServico() {
		log.info("Tentar inserir solicitacão sem código da verba ou serviço");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "", "", adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Código da verba ou código do serviço deve ser informado.", comprarContratoResponse.getMensagem());
		assertEquals("406", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComCodigoDaVerbaInvalido() {
		log.info("Tentar comprar contrato com código da verba inválido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "5", codServico, adeNumero,
				cpf, prazo, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Código da verba ou código do serviço inválido.", comprarContratoResponse.getMensagem());
		assertEquals("232", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComCodigoServicoInvalido() {
		log.info("Tentar comprar contrato com código serviço inválido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, "1", adeNumero, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Código da verba ou código do serviço inválido.", comprarContratoResponse.getMensagem());
		assertEquals("232", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarMatriculaOuCPFDoServidor() {
		log.info("Tentar comprar contrato sem informar matricula ou CPF do servidor");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "", loginServidor.getSenha(), codVerba, codServico, adeNumero, "", prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", comprarContratoResponse.getMensagem());
		assertEquals("305", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarNumeroParcelas() {
		log.info("Tentar comprar contrato sem informar número de parcelas");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, 0, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("O número de parcelas deve ser informado.", comprarContratoResponse.getMensagem());
		assertEquals("431", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComNumeroParcelasNaoPermitido() {
		log.info("Tentar comprar contrato com numero parcelas não permitido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, 5, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Os prazos permitidos para este serviço são: 10.", comprarContratoResponse.getMensagem());
		assertEquals("471", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComNumeroParcelasMaiorQuePermitido() {
		log.info("Tentar comprar contrato com numero parcelas maior que o permitido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, 150, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Prazo total do contrato (número de prestações mais carência) deve ser menor ou igual a 36.",
				comprarContratoResponse.getMensagem());
		assertEquals("347", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComValorPrestacaoZero() {
		log.info("Tentar comprar contrato com valor prestação zero");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, "0", codOrgao, codEstabelecimento, carencia);

		assertEquals("O valor da prestação deve ser maior do que zero.", comprarContratoResponse.getMensagem());
		assertEquals("334", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarValorConsignacao() {
		log.info("Tentar comprar contrato sem informar valor consignação");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
		comprarContratoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
		                                  codVerba, codServico, adeNumero, cpf, prazo, "", codOrgao, codEstabelecimento, carencia);
        });
	}

	@Test
	public void tentarComprarContratoComValorParcelaMaiorQueSomaDosDemaisContratos() {
		log.info("Tentar comprar contrato com valor parcela maior que soma dos demais contratos");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, "250", codOrgao, codEstabelecimento, carencia);

		assertEquals(
				"Não é possível renegociar este contrato pois o valor da parcela não pode ser maior que a soma dos demais contratos.",
				comprarContratoResponse.getMensagem());
		assertEquals("280", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarSenhaOuTokenDoServidor() {
		log.info("Tentar comprar contrato sem informar senha ou token do servidor");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "", codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("A senha ou token do servidor deve ser informada.", comprarContratoResponse.getMensagem());
		assertEquals("211", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComSenhaDoServidorInvalido() {
		log.info("Tentar comprar contrato com senha do servidor inválido");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "ser589", codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("A senha de autorização do servidor não confere.", comprarContratoResponse.getMensagem());
		assertEquals("363", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoParaServidorNaoCadastrado() {
		log.info("Tentar comprar contrato para servidor não cadastrado");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "252525", loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Nenhum servidor encontrado", comprarContratoResponse.getMensagem());
		assertEquals("293", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComOrgaoInexistente() {
		log.info("Tentar comprar contrato com orgão inexistente");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, valorParcela, "859", codEstabelecimento, carencia);

		assertEquals("Nenhum servidor encontrado", comprarContratoResponse.getMensagem());
		assertEquals("293", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComEstabelecimentoInexistente() {
		log.info("Tentar comprar contrato com estabelecimento inexistente");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, valorParcela, codOrgao, "859", carencia);

		assertEquals("Nenhum servidor encontrado", comprarContratoResponse.getMensagem());
		assertEquals("293", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComMatriculaInvalida() {
		log.info("Tentar comprar contrato com matricula inválida");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "12456", loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("A matrícula informada é inválida.", comprarContratoResponse.getMensagem());
		assertEquals("210", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarDataNascimento() {
		log.info("Tentar comprar contrato sem informar data nascimento");

		// alterar parametro 116 exigir data nascimento
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "1");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Data de nascimento deve ser informada.",
				comprarContratoResponse.getMensagem());
		assertEquals("207", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());

		// Restaura o valor do parâmetro de serviço
        parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
                CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "0");
	}

	@Test
	public void tentarComprarContratoComIPDeAcessoInvalido() {
		log.info("Tentar comprar contrato com IP de Acesso inválido");

		// usuario csa não possui parametro 17 (tb_param_consignataria)
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
		ENominaInitializer.limparCache();

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse("csa", loginCsa.getSenha(),
				loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("IP de acesso inválido", comprarContratoResponse.getMensagem());
		assertEquals("362", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoComCETMaiorQueOsAnterior() {
		log.info("Tentar comprar contrato com CET maior que os anteriores");

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico,
				adeNumero, cpf, prazo, "9000", codOrgao, codEstabelecimento, carencia);

		assertEquals("O CET utilizado deve ser inferior ao menor do(s) anterior(es): 0,00",
				comprarContratoResponse.getMensagem());
		assertEquals("393", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}

	@Test
	public void tentarComprarContratoSemInformarAnexoObrigatorio() {
		log.info("Tentar comprar contrato sem informar anexo obrigatorio");

		// alterar parametro 517 para valor 2 anexo obrigatorio (tb_param_sist_consignante)
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, "2");
		ENominaInitializer.limparCache();

		final ComprarContratoResponse comprarContratoResponse = comprarContratoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(),
				loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, adeNumero, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, carencia);

		assertEquals("Não pode ser inserida uma nova reserva para este serviço, pois o anexo é obrigatório.", comprarContratoResponse.getMensagem());
		assertEquals("474", comprarContratoResponse.getCodRetorno().getValue());
		assertFalse(comprarContratoResponse.isSucesso());
	}
}
