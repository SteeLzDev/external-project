package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.AlongarConsignacaoClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.AlongarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AlongarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor2;
	private final String codVerba = "123";
	private final String codServico = "023";
	private final String cpf = "004.503.189-40";
	private final int prazo = 10;
	private final String valorParcela = "100";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private String svcCodigo;
	private final Integer carencia = 0;
	private Long adeNumero = (long) 12;
	private final String adeIdentificador = "";
	private final String dataNascimento = "1980-01-01";
	private final String valorLiberado = "1000";
	private final String indice = "2";
	private final String banco = "1";
	private final String agencia = "1111";
	private final String conta = "111111";

	@Autowired
    private AlongarConsignacaoClient alongarConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@BeforeEach
	public void setUp() {
		svcCodigo = servicoService.retornaSvcCodigo("023");
		// alterar margem
		registroServidorService.alterarRseMargemRest(loginServidor.getLogin(), BigDecimal.ZERO);
		registroServidorService.alterarRseMargemUsada(loginServidor.getLogin(), BigDecimal.valueOf(2000));

		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_INCIDE_MARGEM, "1");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_CAD_INDICE, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INDICE_NUMERICO, "S");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_VALIDAR_TAXA_JUROS, "0");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MINIMA, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MAXIMA, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO, "");
		ENominaInitializer.limparCache();
	}

	@Test
	public void alongarConsignacaoComStatusDeferida() {
		log.info("Alongar consignação com status deferida");

		// ade status deferida
		adeNumero = (long) 61006;

		// consulta margem antes de alongar
		final BigDecimal margemDisponivel = registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginServidor.getLogin())).getRseMargemRest();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Operação realizada com sucesso.", alongarConsignacaoResponse.getMensagem());
		assertEquals("000", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(alongarConsignacaoResponse.isSucesso());
		assertEquals("Sr. Antonio da Silva Augusto", alongarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, alongarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alongarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alongarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("BANCO BRASIL", alongarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", alongarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("1000.0", alongarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("100.0", String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, alongarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals("Deferida", alongarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("023", alongarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("4", alongarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		final AutDesconto adeNova = autDescontoService
				.getAde(String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		// verifica se criou outra ade
		assertNotNull(adeNova);
		// verifica o status
		assertEquals(CodedValues.SAD_DEFERIDA, adeNova.getSadCodigo());
		// verificar relacionamento
		assertEquals(CodedValues.TNT_CONTROLE_RENEGOCIACAO, relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getTntCodigo());
		// verificar margem
		assertTrue(margemDisponivel.compareTo(registroServidorService
				.getRegistroServidor(usuarioService.getSerCodigo(loginServidor.getLogin())).getRseMargemRest()) <= 0);
	}

	@Test
	public void alongarConsignacaoComStatusEmAndamento() {
		log.info("Alongar consignação com status em andamento");

		// ade status em andamento
		adeNumero = (long) 61009;

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Operação realizada com sucesso.", alongarConsignacaoResponse.getMensagem());
		assertEquals("000", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(alongarConsignacaoResponse.isSucesso());
		assertEquals("Sr. Antonio da Silva Augusto", alongarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, alongarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alongarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alongarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", alongarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("145985", alongarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("BANCO BRASIL", alongarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", alongarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("1000.0", alongarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("100.0", String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, alongarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, alongarConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("Deferida", alongarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("023", alongarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("4", alongarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		final AutDesconto adeNova = autDescontoService
				.getAde(String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		// verifica se criou outra ade
		assertNotNull(adeNova);
		// verifica o status
		assertEquals(CodedValues.SAD_DEFERIDA, adeNova.getSadCodigo());
		// verificar relacionamento
		assertEquals(CodedValues.TNT_CONTROLE_RENEGOCIACAO, relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getTntCodigo());
	}

	@Test
	public void alongarConsignacaoComStatusEstoque() {
		log.info("Alongar consignação com status estoque");

		// ade status estoque
		adeNumero = (long) 61010;

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Operação realizada com sucesso.", alongarConsignacaoResponse.getMensagem());
		assertEquals("000", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(alongarConsignacaoResponse.isSucesso());
		assertEquals("Sr. Antonio da Silva Augusto", alongarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, alongarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alongarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alongarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", alongarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("145985", alongarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", alongarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", alongarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("1000.0", alongarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("100.0", String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, alongarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals("Deferida", alongarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("4", alongarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		final AutDesconto adeNova = autDescontoService
				.getAde(String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		// verifica se criou outra ade
		assertNotNull(adeNova);
		// verifica o status
		assertEquals(CodedValues.SAD_DEFERIDA, adeNova.getSadCodigo());
		// verificar relacionamento
		assertEquals(CodedValues.TNT_CONTROLE_RENEGOCIACAO, relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getTntCodigo());
	}

	@Test
	public void alongarConsignacaoComEstoqueMensal() {
		log.info("Alongar consignação com status estoque mensal");

		// ade status estoque mensal
		adeNumero = (long) 61011;

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, "80",
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Operação realizada com sucesso.", alongarConsignacaoResponse.getMensagem());
		assertEquals("000", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(alongarConsignacaoResponse.isSucesso());
		assertEquals("Sr. Antonio da Silva Augusto", alongarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, alongarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alongarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alongarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", alongarConsignacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("145985", alongarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("123", alongarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("80.0", String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, alongarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, alongarConsignacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("Deferida", alongarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("023", alongarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("4", alongarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		final AutDesconto adeNova = autDescontoService
				.getAde(String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		// verifica se criou outra ade
		assertNotNull(adeNova);
		// verifica o status
		assertEquals(CodedValues.SAD_DEFERIDA, adeNova.getSadCodigo());
		// verificar relacionamento
		assertEquals(CodedValues.TNT_CONTROLE_RENEGOCIACAO, relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getTntCodigo());
	}

	@Test
	public void alongarConsignacaoComUltimaParcelaRejeitada() {
		log.info("Alongar consignação com ultima parcela rejeitada");

		// ade com ultima parcela rejeitada
		adeNumero = (long) 61012;

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, "130",
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Operação realizada com sucesso.", alongarConsignacaoResponse.getMensagem());
		assertEquals("000", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(alongarConsignacaoResponse.isSucesso());
		assertEquals("Sr. Antonio da Silva Augusto", alongarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals(cpf, alongarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", alongarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("Solteiro(a)", alongarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("145985", alongarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", alongarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", alongarConsignacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("123", alongarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("1000.0", alongarConsignacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("130.0", String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, alongarConsignacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals("Deferida", alongarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("023", alongarConsignacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("4", alongarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		final AutDesconto adeNova = autDescontoService
				.getAde(String.valueOf(alongarConsignacaoResponse.getBoleto().getValue().getAdeNumero()));
		// verifica se criou outra ade
		assertNotNull(adeNova);
		// verifica o status
		assertEquals(CodedValues.SAD_DEFERIDA, adeNova.getSadCodigo());
		// verificar relacionamento
		assertEquals(CodedValues.TNT_CONTROLE_RENEGOCIACAO, relacionamentoAutorizacaoService
				.getRelacionamentoAutorizacao(adeNumero).getTntCodigo());
	}

	@Test
	public void tentarAlongarConsignacaoComIndiceNumerico() {
		log.info("Alongar consignação com indice numerico");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INDICE_NUMERICO, "N");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), "a", cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals(
				"Configuração de parâmetros incorreta. Para que o índice seja calculado sequencialmente considerando todas as consignações (699), este deve ser numérico (77).",
				alongarConsignacaoResponse.getMensagem());
		assertEquals(
				"Configuração de parâmetros incorreta. Para que o índice seja calculado sequencialmente considerando todas as consignações (699), este deve ser numérico (77).",
				alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoUsandoOutraMargem() {
		log.info("Tentar alongar consignação usando outra margem");

		adeNumero = (long) 61008;

		// alterar para incidir margem 2
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_INCIDE_MARGEM, "2");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Servidor possui margem positiva e não pode alongar este contrato.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("Servidor possui margem positiva e não pode alongar este contrato.",
				alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComMaisDeUmaConsignacaoEncontrada() {
		log.info("Tentar alongar consignação com mais de uma consignação encontrada");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, adeIdentificador, dataNascimento,
				valorParcela, valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Mais de uma consignação encontrada", alongarConsignacaoResponse.getMensagem());
		assertEquals("245", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
		assertTrue(alongarConsignacaoResponse.getResumos().size() > 1);
	}

	@Test
	public void tentarAlongarConsignacaoComServicoQueNaoEstaRelacionadoAoAlongamento() {
		log.info("Tentar alongar consignação com serviço que não esta relacionado ao alongamento");

		// ade servico sem alongamento
		adeNumero = (long) 61013;

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Este contrato não pode ser alongado pois o serviço não está relacionado ao alongamento.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("239", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemPermissaoParaAlongamento() {
		log.info("Tentar alongar consignação sem permissão para alongamento");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, "N");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Este contrato não pode ser alongado pois o sistema não permite operações de alongamento.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("383", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemInformarValorLiberado() {
		log.info("Tentar alongar consignação sem informar valor liberado");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO,
				"1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O valor líquido liberado deve ser informado.", alongarConsignacaoResponse.getMensagem());
		assertEquals("387", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCarenciaForaDoIntervaloPermitido() {
		log.info("Tentar alongar consignação com carencia fora do intervalo permitido");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MINIMA, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MAXIMA, "5");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, 10, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O prazo de carência deve ser maior ou igual a 1 e menor ou igual a 5.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("332", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCarenciaForaDoIntervaloPermitidoSemConfiguracaoDeParametros() {
		log.info("Tentar alongar consignação com carencia fora do intervalo permitido sem configuracao de paremetros");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MINIMA, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MAXIMA, "");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, 102, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O prazo de carência deve ser maior ou igual a 0 e menor ou igual a 99.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("332", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCarenciaMenorQueMinimoPermitido() {
		log.info("Tentar alongar consignação com carencia menor que minimo permitido");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MINIMA, "10");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MAXIMA, "");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, 9, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O prazo de carência deve ser maior ou igual a 10 e menor ou igual a 99.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("332", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComIndiceNaoNumerico() {
		log.info("Tentar alongar consignação com indice não numerico");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INDICE_NUMERICO, "S");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), "a", cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Índice informado deve ser numérico.", alongarConsignacaoResponse.getMensagem());
		assertEquals("400", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComIndiceComParametroConfiguradoParaNaoPermitir() {
		log.info("Tentar alongar consignação com indice com parametro configurado para nao permitir");

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERMITE_CAD_INDICE, "N");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O sistema não permite o cadastro de índice.", alongarConsignacaoResponse.getMensagem());
		assertEquals("416", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComValidacaoTaxaJuros() {
		log.info("Tentar alongar consignação com validacao taxa de juros");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_VALIDAR_TAXA_JUROS, "1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado,
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals(
				"ATENÇÃO: Contrato não foi registrado no sistema.<br>Não existe CET anunciado para o prazo do contrato.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("380", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemInformarDadosBancarios() {
		log.info("Tentar alongar consignação sem informar dados bancarios");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_INF_BANCARIA_OBRIGATORIA, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA, "1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado,
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, "", "", "");

		assertEquals("As informações bancárias devem ser informadas.", alongarConsignacaoResponse.getMensagem());
		assertEquals("374", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComDadosBancariosIncorretos() {
		log.info("Tentar alongar consignação sem informar dados bancarios");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_INF_BANCARIA_OBRIGATORIA, "1");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA, "1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, "01", "20", "123");

		assertEquals("As informações bancárias não conferem com as cadastradas no sistema.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("392", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCarenciaMaiorQueMaximoPermitido() {
		log.info("Tentar alongar consignação com carencia maior que maximo permitido");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MINIMA, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_CARENCIA_MAXIMA, "10");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, "",
				codVerba, codServico, prazo, 15, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O prazo de carência deve ser maior ou igual a 0 e menor ou igual a 10.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("332", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComUsuarioInvalido() {
		log.info("Tentar alongar consignação com usuário inválido");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse("csa1",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado, codVerba,
				codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf, codOrgao,
				codEstabelecimento, banco, agencia, conta);

		assertEquals("Usuário ou senha inválidos", alongarConsignacaoResponse.getMensagem());
		assertEquals("358", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComSenhaInvalida() {
		log.info("Tentar alongar consignação com senha inválida");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), "abc145", adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado,
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Usuário ou senha inválidos", alongarConsignacaoResponse.getMensagem());
		assertEquals("358", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComUsuarioSemPermissao() {
		log.info("Tentar alongar consignação com usuário sem permissão");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse("zetra_igor",
				"abc12345", adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado, codVerba,
				codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf, codOrgao,
				codEstabelecimento, banco, agencia, conta);

		assertEquals("O usuário não tem permissão para executar esta operação",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("329", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemCodigoDaVerbaOuServico() {
		log.info("Tentar inserir solicitacão sem código da verba ou serviço");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, "", "", prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Código da verba ou código do serviço deve ser informado.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("406", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCodigoDaVerbaInvalido() {
		log.info("Tentar alongar consignação com código da verba inválido");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, "5", codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(),
				indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Código da verba ou código do serviço inválido.", alongarConsignacaoResponse.getMensagem());
		assertEquals("232", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComCodigoServicoInvalido() {
		log.info("Tentar alongar consignação com código serviço inválido");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, "1", prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(),
				indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Código da verba ou código do serviço inválido.", alongarConsignacaoResponse.getMensagem());
		assertEquals("232", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemInformarMatriculaOuCPFDoServidor() {
		log.info("Tentar alongar consignação sem informar matricula ou CPF do servidor");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), (long) 0, adeIdentificador, dataNascimento,
				valorParcela, valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(), "",
				indice, "", codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Múltiplos servidores encontrados", alongarConsignacaoResponse.getMensagem());
		assertEquals("248", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComPrazoServicoNaoPermitido() {
		log.info("Tentar alongar consignação com prazo não permitido");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_MAX_PRAZO, "15");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, 20, carencia, loginServidor.getSenha(), loginServidor.getLogin(),
				indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals(
				"Quantidade de parcelas maior do que o permitido para este serviço. Quantidade máxima permitida (meses): 15",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("426", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComPrazoMaiorQuePermitido() {
		log.info("Tentar alongar consignação com prazo maior que o permitido");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_MAX_PRAZO, "");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, 150, carencia, loginServidor.getSenha(), loginServidor.getLogin(),
				indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals(
				"Quantidade de parcelas maior do que o permitido para este servidor. Quantidade máxima permitida (meses): 36",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("425", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComValorPrestacaoZero() {
		log.info("Tentar alongar consignação com valor prestação zero");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, "0",
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O valor da prestação deve ser maior do que zero.", alongarConsignacaoResponse.getMensagem());
		assertEquals("334", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComValorParcelaMaiorQueValorContratoAtual() {
		log.info("Tentar alongar consignação com valor parcela maior que valor contrato atual");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, "550",
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O novo valor da parcela não pode ser maior do que o valor atual do contrato.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("321", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComValorParcelaMaiorQuePercentualDoValorContratoAtual() {
		log.info("Tentar alongar consignação com valor parcela maior que percentual do valor contrato atual");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO, "0");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("O novo valor da parcela não pode ser maior do que 0,00% do valor atual do contrato.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("321", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemInformarSenhaOuTokenDoServidor() {
		log.info("Tentar alongar consignação sem informar senha ou token do servidor");

		parametroSistemaService.configurarParametroServicoCse(svcCodigo, CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA,
				"1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, "", loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("A senha ou token do servidor deve ser informada.", alongarConsignacaoResponse.getMensagem());
		assertEquals("211", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComSenhaDoServidorInvalido() {
		log.info("Tentar alongar consignação com senha do servidor inválido");

		parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, "S");
        ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, "ser589", loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("A senha de autorização do servidor não confere.", alongarConsignacaoResponse.getMensagem());
		assertEquals("363", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoParaServidorNaoCadastrado() {
		log.info("Tentar alongar consignação para servidor não cadastrado");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(), indice, "252525", cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Nenhum servidor encontrado", alongarConsignacaoResponse.getMensagem());
		assertEquals("293", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComOrgaoInexistente() {
		log.info("Tentar alongar consignação com orgão inexistente");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, "859", codEstabelecimento, banco, agencia, conta);

		assertEquals("Nenhum servidor encontrado", alongarConsignacaoResponse.getMensagem());
		assertEquals("293", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoComEstabelecimentoInexistente() {
		log.info("Tentar alongar consignação com estabelecimento inexistente");

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, "859", banco, agencia, conta);

		assertEquals("Nenhum servidor encontrado", alongarConsignacaoResponse.getMensagem());
		assertEquals("293", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}

	@Test
	public void tentarAlongarConsignacaoSemInformarDataNascimento() {
		log.info("Tentar alongar consignação sem informar data nascimento");

		// alterar parametro 116 exigir data nascimento
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "", valorParcela, valorLiberado,
				codVerba, codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf,
				codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Data de nascimento deve ser informada.", alongarConsignacaoResponse.getMensagem());
		assertEquals("207", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());

		// Restaura o valor do parâmetro de serviço
        parametroSistemaService.configurarParametroServicoCse(svcCodigo,
                CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "0");
	}

	@Test
	public void tentarAlongarConsignacaoComDataNascimentoDiferenteDaDataCadastrada() {
		log.info("Tentar alongar consignação sem informar data nascimento");

		// alterar parametro 116 exigir data nascimento
		parametroSistemaService.configurarParametroServicoCse(svcCodigo,
				CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "1");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse(
				loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "1985-01-01", valorParcela,
				valorLiberado, codVerba, codServico, prazo, carencia, loginServidor.getSenha(),
				loginServidor.getLogin(), indice, cpf, codOrgao, codEstabelecimento, banco, agencia, conta);

		assertEquals("Data de nascimento não confere com a cadastrada no sistema.",
				alongarConsignacaoResponse.getMensagem());
		assertEquals("391", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());

		// Restaura o valor do parâmetro de serviço
        parametroSistemaService.configurarParametroServicoCse(svcCodigo,
                CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA, "0");
	}

	@Test
	public void tentarAlongarConsignacaoComIPDeAcessoInvalido() {
		log.info("Tentar alongar consignação com IP de Acesso inválido");

		// usuario csa não possui parametro 17 (tb_param_consignataria)
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
		ENominaInitializer.limparCache();

		final AlongarConsignacaoResponse alongarConsignacaoResponse = alongarConsignacaoClient.getResponse("csa",
				loginCsa.getSenha(), adeNumero, adeIdentificador, dataNascimento, valorParcela, valorLiberado, codVerba,
				codServico, prazo, carencia, loginServidor.getSenha(), loginServidor.getLogin(), indice, cpf, codOrgao,
				codEstabelecimento, banco, agencia, conta);

		assertEquals("IP de acesso inválido", alongarConsignacaoResponse.getMensagem());
		assertEquals("362", alongarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(alongarConsignacaoResponse.isSucesso());
	}
}
