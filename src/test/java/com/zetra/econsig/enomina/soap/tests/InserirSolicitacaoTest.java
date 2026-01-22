package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.dao.ConsignatariaDao;
import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.OrgaoDao;
import com.zetra.econsig.dao.ServicoDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.InserirSolicitacaoClient;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.soap.InserirSolicitacaoResponse;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InserirSolicitacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final String codVerba = "145";
	private final String codServico = "001";
	private final String cpf = "092.459.399-79";
	private final int prazo = 10;
	private final String valorParcela = "200";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private String svcCodigoEmprestimo;
	private final String telefone = "3232658996";

	@Autowired
    private InserirSolicitacaoClient inserirSolicitacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private ServicoDao servicoDao;

	@Autowired
	private OrgaoDao orgaoDao;

	@Autowired
	private ConsignatariaDao consignatariaDao;

	@Autowired
	private ConvenioDao convenioDao;

	@Autowired
	private VerbaConvenioDao verbaConvenioDao;

	@BeforeEach
	public void setUp() throws Exception {
		svcCodigoEmprestimo = servicoService.retornaSvcCodigo("001");
		// alterar parametro
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo,
				CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR, "");
		parametroSistemaService.configurarParametroServicoCse(svcCodigoEmprestimo, CodedValues.TPS_INCIDE_MARGEM, "1");
	}

	@Test
	public void inserirSolicitacaoComSucessoValidarDadosPessoais() {
		log.info("Inserir solicitação com sucesso validar dados pessoais");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("000", solicitacaoResponse.getCodRetorno().getValue());
		assertTrue(solicitacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", solicitacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", solicitacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", solicitacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("S", solicitacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", solicitacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("Jose Augusto", solicitacaoResponse.getBoleto().getValue().getPai());
		assertEquals("Maria Raimunda", solicitacaoResponse.getBoleto().getValue().getMae());
		assertEquals("Avenida Portugal", solicitacaoResponse.getBoleto().getValue().getEndereco());
		assertEquals("Itapoa", solicitacaoResponse.getBoleto().getValue().getBairro());
		assertEquals("Belo Horizonte", solicitacaoResponse.getBoleto().getValue().getCidade());
		assertEquals("MG", solicitacaoResponse.getBoleto().getValue().getUf());
		assertEquals("31710400", solicitacaoResponse.getBoleto().getValue().getCep());
		assertEquals("31-32659874", solicitacaoResponse.getBoleto().getValue().getTelefone());
		assertEquals("123456", solicitacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("1", solicitacaoResponse.getBoleto().getValue().getBanco().getValue());
		assertEquals("1111", solicitacaoResponse.getBoleto().getValue().getAgencia().getValue());
		assertEquals("111111", solicitacaoResponse.getBoleto().getValue().getConta().getValue());
		assertEquals("1", solicitacaoResponse.getBoleto().getValue().getQtdFilhos().getValue().toString());
		assertEquals("Vitoria", solicitacaoResponse.getBoleto().getValue().getCidadeNascimento().getValue());
		assertEquals("BRASILEIRO", solicitacaoResponse.getBoleto().getValue().getNacionalidade().getValue());
		assertEquals("32-987457485", solicitacaoResponse.getBoleto().getValue().getCelular().getValue());

		// verifica se criou a solicitação no banco
		assertNotNull(
				autDescontoService.getAde(String.valueOf(solicitacaoResponse.getBoleto().getValue().getAdeNumero())));
	}

	@Test
	public void inserirSolicitacaoComSucessoValidarDadosADE() {
		log.info("Inserir solicitação com sucesso validar dados ADE");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("36", solicitacaoResponse.getBoleto().getValue().getPrazoServidor().toString());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", solicitacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", solicitacaoResponse.getBoleto().getValue().getOrgao());
		assertEquals("213464140", solicitacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", solicitacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", solicitacaoResponse.getBoleto().getValue().getConsignataria());
		assertEquals("145", solicitacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("EMPRÉSTIMO", solicitacaoResponse.getBoleto().getValue().getServico());
		assertEquals("3000.0", solicitacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("200.0", String.valueOf(solicitacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, solicitacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, solicitacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("1.1", solicitacaoResponse.getBoleto().getValue().getTaxaJuros().getValue().toString());
		assertEquals("Solicitação", solicitacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("001", solicitacaoResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("0", solicitacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("csa2", solicitacaoResponse.getBoleto().getValue().getResponsavel());
		assertEquals("291500.5", solicitacaoResponse.getBoleto().getValue().getSalario().getValue().toString());

		// verifica se criou a solicitação no banco
		final AutDesconto autDesconto = autDescontoService
				.getAde(String.valueOf(solicitacaoResponse.getBoleto().getValue().getAdeNumero()));

		assertNotNull(autDesconto);
		assertEquals("3000.00", autDesconto.getAdeVlrLiquido().toString());
		assertEquals("200.00", autDesconto.getAdeVlr().toString());
		assertEquals("10", autDesconto.getAdePrazo().toString());
	}

	@Test
	public void inserirSolicitacaoSomenteComDadosObrigatorios() {
		log.info("Inserir solicitação somente com dados obrigatorios");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, prazo, valorParcela,
				codOrgao, codEstabelecimento);

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("000", solicitacaoResponse.getCodRetorno().getValue());
		assertTrue(solicitacaoResponse.isSucesso());
		assertEquals("Sr. BOB da Silva Shawn", solicitacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", solicitacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", solicitacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("S", solicitacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("1980-01-01-03:00", solicitacaoResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("Jose Augusto", solicitacaoResponse.getBoleto().getValue().getPai());
		assertEquals("Maria Raimunda", solicitacaoResponse.getBoleto().getValue().getMae());
		assertEquals("Avenida Portugal", solicitacaoResponse.getBoleto().getValue().getEndereco());
		assertEquals("Itapoa", solicitacaoResponse.getBoleto().getValue().getBairro());
		assertEquals("Belo Horizonte", solicitacaoResponse.getBoleto().getValue().getCidade());
		assertEquals("MG", solicitacaoResponse.getBoleto().getValue().getUf());
		assertEquals("31710400", solicitacaoResponse.getBoleto().getValue().getCep());
		assertEquals("31-32659874", solicitacaoResponse.getBoleto().getValue().getTelefone());
		assertEquals("123456", solicitacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("145", solicitacaoResponse.getBoleto().getValue().getCodVerba());

		// verifica se criou a solicitação no banco
		assertNotNull(autDescontoService.getAde(String.valueOf(solicitacaoResponse.getBoleto().getValue().getAdeNumero())));
	}

	@Test
	public void inserirSolicitacaoUsandoOutraMargem() throws ConvenioControllerException {
		log.info("Inserir solicitação usando outra margem");

		final AcessoSistema responsavel = new AcessoSistemaBuilder("AA808080808080808080808080809E80")
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSE)
				.setCodigoEntidade("1").build();

		servicoService.copiaServico("r4r4435343455", "164", svcCodigoEmprestimo, false, responsavel);

		final Convenio cnv = new Convenio();
		cnv.setCnvCodigo("4545454555");
		cnv.setServico(servicoDao.findById("r4r4435343455").get());
		cnv.setOrgao(orgaoDao.findById("751F8080808080808080808080809780").get());
		cnv.setConsignataria(consignatariaDao.findById("267").get());
		cnv.setCnvDataIni(new Date(LocalDate.now().minusMonths(2).toEpochDay()));
		cnv.setCnvDataFim(new Date(LocalDate.now().plusYears(2).toEpochDay()));
		cnv.setVceCodigo("1");
		cnv.setScvCodigo(CodedValues.SCV_ATIVO);
		cnv.setCnvIdentificador("outraMargem");
		cnv.setCnvCodVerba("8754");
		cnv.setCnvCodVerbaRef("8442");

		convenioDao.save(cnv);

		final VerbaConvenio vco = new VerbaConvenio();
		vco.setVcoCodigo("efefj93ej3j");
		vco.setCnvCodigo("4545454555");
		vco.setVcoAtivo(Short.valueOf("1"));
		vco.setVcoDataIni(new Date(LocalDate.now().minusMonths(2).toEpochDay()));
		vco.setVcoDataFim(new Date(LocalDate.now().plusYears(2).toEpochDay()));
		vco.setVcoVlrVerba(BigDecimal.valueOf(99999999999.99));
		vco.setVcoVlrVerbaRest(BigDecimal.valueOf(99999999999.99));

		verbaConvenioDao.save(vco);

		// alterar para incidir margem 2
		parametroSistemaService.configurarParametroServicoCse("r4r4435343455", CodedValues.TPS_INCIDE_MARGEM, "2");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "8754", "164", cpf,
				prazo, "100", "213464140", codEstabelecimento, telefone);

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("36", solicitacaoResponse.getBoleto().getValue().getPrazoServidor().toString());
		assertEquals("EMPRESTIMO 2", solicitacaoResponse.getBoleto().getValue().getServico());
		assertEquals("3000.0", solicitacaoResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("100.0", String.valueOf(solicitacaoResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(prazo, solicitacaoResponse.getBoleto().getValue().getPrazo());
		assertEquals(0, solicitacaoResponse.getBoleto().getValue().getPagas());
		assertEquals("1.1", solicitacaoResponse.getBoleto().getValue().getTaxaJuros().getValue().toString());
		assertEquals("0", solicitacaoResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("291500.5", solicitacaoResponse.getBoleto().getValue().getSalario().getValue().toString());

		// verifica se criou a solicitação no banco
		final AutDesconto autDesconto = autDescontoService
				.getAde(String.valueOf(solicitacaoResponse.getBoleto().getValue().getAdeNumero()));

		assertNotNull(autDesconto);
		assertEquals("3000.00", autDesconto.getAdeVlrLiquido().toString());
		assertEquals("100.00", autDesconto.getAdeVlr().toString());
		assertEquals("10", autDesconto.getAdePrazo().toString());
	}

	@Test
	public void tentarInserirSolicitacaoComUsuarioInvalido() {
		log.info("Tentar inserir solicitação com usuário inválido");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse("csa1",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Usuário ou senha inválidos", solicitacaoResponse.getMensagem());
		assertEquals("358", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComSenhaInvalida() {
		log.info("Tentar inserir solicitação com senha inválida");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				"123456", loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Usuário ou senha inválidos", solicitacaoResponse.getMensagem());
		assertEquals("358", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComUsuarioSemPermissao() {
		log.info("Tentar inserir solicitação com usuário sem permissão");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse("zetra_igor", "abc12345",
				loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf, prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("O usuário não tem permissão para executar esta operação", solicitacaoResponse.getMensagem());
		assertEquals("329", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoSemCodigoDaVerbaOuServico() {
		log.info("Tentar inserir solicitacão sem código da verba ou serviço");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "", "", cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Código da verba ou código do serviço deve ser informado.", solicitacaoResponse.getMensagem());
		assertEquals("406", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComCodigoDaVerbaInvalido() {
		log.info("Tentar inserir solicitação com código da verba inválido");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "5", codServico, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Código da verba ou código do serviço inválido.", solicitacaoResponse.getMensagem());
		assertEquals("232", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComCodigoServicoInvalido() {
		log.info("Tentar inserir solicitação com código serviço inválido");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, "1", cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Código da verba ou código do serviço inválido.", solicitacaoResponse.getMensagem());
		assertEquals("232", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoSemInformarMatriculaOuCPFDoServidor() {
		log.info("Tentar inserir solicitacao sem informar matricula ou CPF do servidor");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "", loginServidor.getSenha(), codVerba, codServico, "", prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", solicitacaoResponse.getMensagem());
		assertEquals("305", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoSemInformarNumeroParcelas() {
		log.info("Tentar inserir solicitação sem informar número de parcelas");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf, 0,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("O número de parcelas deve ser informado.", solicitacaoResponse.getMensagem());
		assertEquals("431", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComValorConsignacaoZero() {
		log.info("Tentar inserir solicitação com valor consignação zero");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, "0", codOrgao, codEstabelecimento, telefone);

		assertEquals("O valor da prestação deve ser maior do que zero.", solicitacaoResponse.getMensagem());
		assertEquals("334", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoSemInformarValorConsignacao() {
		log.info("Tentar inserir solicitação sem informar valor consignação");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            inserirSolicitacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba,
                                                 codServico, cpf, prazo, "", codOrgao, codEstabelecimento, telefone);
        });
	}

	@Test
	public void tentarInserirSolicitacaoSemInformarSenhaOuTokenDoServidor() {
		log.info("Tentar inserir solicitação sem informar senha ou token do servidor");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "", codVerba, codServico, cpf, prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("A senha ou token do servidor deve ser informada.", solicitacaoResponse.getMensagem());
		assertEquals("211", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComSenhaDoServidorInvalido() {
		log.info("Tentar inserir solicitação com senha do servidor inválido");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), "abc12345", codVerba, codServico, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("A senha de autorização do servidor não confere.", solicitacaoResponse.getMensagem());
		assertEquals("363", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoParaServidorNaoCadastrado() {
		log.info("Tentar inserir solicitação para servidor não cadastrado");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "252525", loginServidor.getSenha(), codVerba, codServico, cpf, prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("Nenhum servidor encontrado", solicitacaoResponse.getMensagem());
		assertEquals("293", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComOrgaoInexistente() {
		log.info("Tentar inserir solicitação com orgão inexistente");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, "25365", codEstabelecimento, telefone);

		assertEquals("Nenhum servidor encontrado", solicitacaoResponse.getMensagem());
		assertEquals("293", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComEstabelecimentoInexistente() {
		log.info("Tentar inserir solicitação com estabelecimento inexistente");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, codOrgao, "2563", telefone);

		assertEquals("Nenhum servidor encontrado", solicitacaoResponse.getMensagem());
		assertEquals("293", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComMatriculaInvalida() {
		log.info("Tentar inserir solicitação com matricula inválida");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "12456", loginServidor.getSenha(), codVerba, codServico, cpf, prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("A matrícula informada é inválida.", solicitacaoResponse.getMensagem());
		assertEquals("210", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoParaConsignatariaSemPermissao() {
		log.info("Tentar inserir solicitação para consignatária sem permissão");

		// usuario csa não possui coeficiente ativo (tb_coeficiente_ativo), mas possui o
		// parametro 17 (tb_param_consignataria)
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "S");
        ENominaInitializer.limparCache();

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse("csa",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "", codServico, cpf, prazo,
				valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("Não é permitido inserir uma solicitação para consignatária informada.",
				solicitacaoResponse.getMensagem());
		assertEquals("473", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComQuantidadeConsignatariasPermitidasParaSimulacaoIgualZero() throws ConvenioControllerException, CreateException {
		log.info("Tentar inserir solicitação com quantidade consignatarias permitidas para simulacao igual zero");

		final AcessoSistema responsavel = new AcessoSistemaBuilder("AA808080808080808080808080809E80")
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSE)
				.setCodigoEntidade("1").build();

		servicoService.copiaServico("FEFEEFSDVDVFF", "891", svcCodigoEmprestimo, responsavel);

		final Convenio cnv = new Convenio();
		cnv.setCnvCodigo("4r4rf4f4r4r9898989");
		cnv.setServico(servicoDao.findById("FEFEEFSDVDVFF").get());
		cnv.setOrgao(orgaoDao.findById("751F8080808080808080808080809780").get());
		cnv.setConsignataria(consignatariaDao.findById("267").get());
		cnv.setCnvDataIni(new Date(LocalDate.now().minusMonths(2).toEpochDay()));
		cnv.setCnvDataFim(new Date(LocalDate.now().plusYears(2).toEpochDay()));
		cnv.setVceCodigo("1");
		cnv.setScvCodigo(CodedValues.SCV_ATIVO);
		cnv.setCnvIdentificador("cnvTeste");
		cnv.setCnvCodVerba("0475");
		cnv.setCnvCodVerbaRef("0344");

		convenioDao.save(cnv);

		final VerbaConvenio vco = new VerbaConvenio();
		vco.setVcoCodigo("feafaefefefe");
		vco.setCnvCodigo("4r4rf4f4r4r9898989");
		vco.setVcoAtivo(Short.valueOf("1"));
		vco.setVcoDataIni(new Date(LocalDate.now().minusMonths(2).toEpochDay()));
		vco.setVcoDataFim(new Date(LocalDate.now().plusYears(2).toEpochDay()));
		vco.setVcoVlrVerba(BigDecimal.valueOf(99999999999.99));
		vco.setVcoVlrVerbaRest(BigDecimal.valueOf(99999999999.99));

		verbaConvenioDao.save(vco);

		// alterar parametro 133 para valor zero (tb_param_svc_consignante)
		parametroSistemaService.configurarParametroServicoCse("FEFEEFSDVDVFF",
				CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR, "0");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), "0475", "891", cpf, prazo,
				valorParcela, "213464140", codEstabelecimento, telefone);

		assertEquals("Não é permitido inserir uma solicitação para consignatária informada.",
				solicitacaoResponse.getMensagem());
		assertEquals("473", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComIPDeAcessoInvalido() {
		log.info("Tentar inserir solicitação com IP de Acesso inválido");

		// usuario csa não possui parametro 17 (tb_param_consignataria)
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse("csa",
				loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(), codVerba, codServico, cpf,
				prazo, valorParcela, codOrgao, codEstabelecimento, telefone);

		assertEquals("IP de acesso inválido", solicitacaoResponse.getMensagem());
		assertEquals("362", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComUsuarioComSenhaExpirada() {
		log.info("Tentar inserir solicitação com usuário com senha expirada");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "579771", loginServidor.getSenha(), codVerba, codServico, "", prazo, valorParcela,
				codOrgao, codEstabelecimento, telefone);

		assertEquals("A senha de autorização do servidor está expirada.", solicitacaoResponse.getMensagem());
		assertEquals("363", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoComValorParcelaInformadoMaiorQueMargemDisponivel() {
		log.info("Tentar inserir solicitação com valor parcela informado maior que margem disponivel");

		final InserirSolicitacaoResponse solicitacaoResponse = inserirSolicitacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "145985", loginServidor.getSenha(), codVerba, codServico, "004.503.189-40", prazo,
				"501.00", codOrgao, codEstabelecimento, telefone);

		assertEquals("Valor da parcela informado maior do que a margem disponível.", solicitacaoResponse.getMensagem());
		assertEquals("359", solicitacaoResponse.getCodRetorno().getValue());
		assertFalse(solicitacaoResponse.isSucesso());
	}

	@Test
	public void tentarInserirSolicitacaoSemInformarTelefone() {
		log.info("Tentar inserir solicitação sem informar telefone");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            inserirSolicitacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), loginServidor.getLogin(), loginServidor.getSenha(),
                                                 codVerba, codServico, cpf, prazo, valorParcela, codOrgao, codEstabelecimento, "");
        });
	}
}
