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
import com.zetra.econsig.enomina.soap.client.ConfirmarSolicitacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.ConfirmarSolicitacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConfirmarSolicitacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private Long adeNumero = (long) 60879;
	private final String adeIdentificador = "Solicitação Web";
	private final Double coeficiente = (double) 0;
	private final String codigoMotivo = "01";
	private String svcCodigoEmprestimo;

	@Autowired
    private ConfirmarSolicitacaoClient confirmarSolicitacaoClient;

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
	}

	@Test
	public void confirmarSolicitacaoComValorCoeficienteMenorQueOCadastrado() {
		log.info("Confirmar solicitação com valor coeficiente menor que o cadastrado");

		adeNumero = (long) 60878;

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				true, adeIdentificador, (double) 1, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("000", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(confirmarSolicitacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", confirmarSolicitacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", confirmarSolicitacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", confirmarSolicitacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", confirmarSolicitacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", confirmarSolicitacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("1", confirmarSolicitacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", confirmarSolicitacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", confirmarSolicitacaoResponse.getBoleto().getValue().getConta().getValue());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void confirmarSolicitacaoComSucesso() {
		log.info("Confirmar solicitação com sucesso");

		adeNumero = (long) 60880;

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				false, adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Operação realizada com sucesso.", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("000", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(confirmarSolicitacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", confirmarSolicitacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", confirmarSolicitacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", confirmarSolicitacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", confirmarSolicitacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", confirmarSolicitacaoResponse.getBoleto().getValue().getDataNascimento().toString());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void confirmarSolicitacaoSomenteComDadosObrigatorios() {
		log.info("Confirmar solicitação somente com dados obrigatorios");

		adeNumero = (long) 60881;

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, false);

		assertEquals("Operação realizada com sucesso.", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("000", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertTrue(confirmarSolicitacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", confirmarSolicitacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", confirmarSolicitacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", confirmarSolicitacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", confirmarSolicitacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", confirmarSolicitacaoResponse.getBoleto().getValue().getDataNascimento().toString());

		// verifica se alterou o status no banco
		assertEquals("4", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComNovoValorParcelaMenorQueAtual() {
		log.info("Tentar confirmar solicitação com novo valor parcela menor que atual");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				false, adeIdentificador, (double) 1, codigoMotivo);

		assertEquals("O novo valor da parcela deve ser menor que o valor atual.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("O novo valor da parcela deve ser menor que o valor atual.",
				confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComValorCoeficienteIgualQueAnterior() {
		log.info("Tentar confirmar solicitação com valor coeficiente igual que anterior");

		// alteraValorLiberado = true, e coeficiente igual ao anterior 1.1
		ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				true, adeIdentificador, 1.1, codigoMotivo);

		assertEquals("O novo coeficiente deve ser menor do que o coeficiente anterior.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("320", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		// alteraValorLiberado = false, e coeficiente igual ao anterior 1.1
		confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(),
				loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false, adeIdentificador,
				1.1, codigoMotivo);

		assertEquals("O novo coeficiente deve ser menor do que o coeficiente anterior.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("320", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComValorCoeficienteMaiorQueAnterior() {
		log.info("Tentar confirmar solicitação com valor coeficiente maior que anterior");

		// alteraValorLiberado = true, e coeficiente igual ao anterior 2.1
		ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				true, adeIdentificador, 2.1, codigoMotivo);

		assertEquals("O novo coeficiente deve ser menor do que o coeficiente anterior.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("320", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());

		// alteraValorLiberado = false, e coeficiente igual ao anterior 1.9
		confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(),
				loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false, adeIdentificador,
				1.9, codigoMotivo);

		assertEquals("O novo coeficiente deve ser menor do que o coeficiente anterior.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("320", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoSemInformarNumeroOrdemAdeOuIdentificador() {
		log.info("Tentar confirmar solicitação sem informar número ordem ADE ou identificador");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
				(long) 0, false, "", coeficiente, codigoMotivo);

		assertEquals("O N&ordm; ADE ou o identificador deve ser informado.",
				confirmarSolicitacaoResponse.getMensagem());
		assertEquals("322", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarConfirmarSolicitacaoComUsuarioInvalido() {
		log.info("Tentar confirmar solicitação com usuário inválido");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse("csa1",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false,
				adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("358", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComSenhaInvalida() {
		log.info("Tentar confirmar solicitação com senha inválida");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), "12345", loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false,
				adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Usuário ou senha inválidos", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("358", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoSemInformarUsuario() {
		log.info("Tentar confirmar solicitação sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            confirmarSolicitacaoClient.getResponse("", loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false,
                                                   adeIdentificador, coeficiente, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoSemInformarSenha() {
		log.info("Tentar confirmar solicitação sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            confirmarSolicitacaoClient.getResponse(loginCsa.getLogin(), "", loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false,
                                                   adeIdentificador, coeficiente, codigoMotivo);
        });

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComIPDeAcessoInvalido() {
		log.info("Tentar confirmar solicitação com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse("csa",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero, false,
				adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("IP de acesso inválido", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("362", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void confirmarSolicitacaoComMaisDeUmaConsignacao() {
		log.info("Confirmar solicitação com mais de uma consignação");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
				(long) 0, false, adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Mais de uma consignação encontrada", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("245", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());
		assertTrue(confirmarSolicitacaoResponse.getResumos().size() > 1);

	}

	@Test
	public void tentarConfirmarSolicitacaoComConsignacaoNaoCadastrada() {
		log.info("Tentar confirmar solicitação com consignacao não cadastrada");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
				(long) 60800, false, adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("294", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

	}

	@Test
	public void tentarConfirmarSolicitacaoComConsignacaoComStatusDiferenteDeSolicitacao() {
		log.info("Tentar confirmar solicitação com consignacao com status diferente de solicitação");

		adeNumero = (long) 60884;

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				false, adeIdentificador, coeficiente, codigoMotivo);

		assertEquals("Nenhuma consignação encontrada", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("294", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("2", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComTipoDeMotivoDaOperacaoNaoCadastrado() {
		log.info("Tentar confirmar solicitação com tipo de motivo da operação não cadastrado");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				false, adeIdentificador, coeficiente, "Outros");

		assertEquals("Tipo de motivo da operação não encontrado.", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("356", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

	@Test
	public void tentarConfirmarSolicitacaoComValorCoeficienteInvalido() {
		log.info("Tentar confirmar solicitação com valor coeficiente inválido");

		final ConfirmarSolicitacaoResponse confirmarSolicitacaoResponse = confirmarSolicitacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), adeNumero,
				false, adeIdentificador, (double) -1, "Outros");

		assertEquals("O coeficiente informado é inválido.", confirmarSolicitacaoResponse.getMensagem());
		assertEquals("303", confirmarSolicitacaoResponse.getCodRetorno().getValue());
		assertFalse(confirmarSolicitacaoResponse.isSucesso());

		// verifica que não alterou o status no banco
		assertEquals("0", autDescontoService.getAde(adeNumero.toString()).getSadCodigo());
	}

}
