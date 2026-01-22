package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ReservarMargemClient;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.ReservarMargemResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReservarMargemTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor2;
	private final LoginInfo loginCor = LoginValues.cor1;
	private final String codVerba = "145";
	private final String codServico = "001";
	private final String matricula = "145985";
	private final String cpf = "004.503.189-40";
	private final String valorParcela = "100";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";
	private final Integer prazo = 10;
	private final String valorLiberado = "500";
	private final String nomeArquivo = "carlotajoaquina.pdf";
	private final String arquivo = "src/test/resources/files/arquivo_para_teste.pdf";
	private final String orgaoCodigo = "751F8080808080808080808080809780";

	@Autowired
    private ReservarMargemClient reservarMargemClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
    private FuncaoService funcaoService;

	@Autowired
    private UsuarioServiceTest usuarioService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private RegistroServidorService registroServidorService;

	@BeforeEach
    public void setUpForUsuCor() throws Exception {
	    funcaoService.criarFuncaoPerfilCor(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), CodedValues.FUN_RES_MARGEM, usuarioService.getCorCodigo(loginCor.getLogin()));
        funcaoService.criarFuncaoPerfilCor(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), CodedValues.FUN_INTEGRA_SOAP_OPERACIONAL, usuarioService.getCorCodigo(loginCor.getLogin()));
	}

	@Test
	public void reservarMargemComSucesso() throws IOException {
		log.info("Reservar Margem com sucesso.");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, loginServidor.getSenha(), loginServidor.getLogin(), codServico, valorParcela,
				prazo, valorLiberado, codVerba, nomeArquivo, arquivo);

		assertEquals("Operação realizada com sucesso.", reservarMargemResponse.getMensagem());
		assertEquals("000", reservarMargemResponse.getCodRetorno().getValue());
		assertEquals("Sr. Antonio da Silva Augusto", reservarMargemResponse.getBoleto().getValue().getServidor());
		assertEquals("004.503.189-40", reservarMargemResponse.getBoleto().getValue().getCpf());
		assertEquals("M", reservarMargemResponse.getBoleto().getValue().getSexo());
		assertEquals("1980-01-01-03:00", reservarMargemResponse.getBoleto().getValue().getDataNascimento().toString());
		assertEquals("S", reservarMargemResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("3987139", reservarMargemResponse.getBoleto().getValue().getIdentidade());
		assertEquals("Jose Augusto", reservarMargemResponse.getBoleto().getValue().getPai());
		assertEquals("Antonia da Silva", reservarMargemResponse.getBoleto().getValue().getMae());
		assertEquals("Rua do Ouro", reservarMargemResponse.getBoleto().getValue().getEndereco());
		assertEquals("171", reservarMargemResponse.getBoleto().getValue().getNumero());
		assertEquals("apto 171", reservarMargemResponse.getBoleto().getValue().getComplemento());
		assertEquals("Centro", reservarMargemResponse.getBoleto().getValue().getBairro());
		assertEquals("Belo Horizonte", reservarMargemResponse.getBoleto().getValue().getCidade());
		assertEquals("MG", reservarMargemResponse.getBoleto().getValue().getUf());
		assertEquals("145985", reservarMargemResponse.getBoleto().getValue().getMatricula());
		assertEquals("1992-02-07-02:00", reservarMargemResponse.getBoleto().getValue().getDataAdmissao().toString());

	}

	@Test
	public void reservarMargemComSucessoDadosADE() throws IOException {
		log.info("Reservar Margem com sucesso.");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, loginServidor.getSenha(), loginServidor.getLogin(), codServico, valorParcela,
				prazo, valorLiberado, codVerba, nomeArquivo, arquivo);

		assertEquals("Operação realizada com sucesso.", reservarMargemResponse.getMensagem());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", reservarMargemResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", reservarMargemResponse.getBoleto().getValue().getOrgao());
		assertEquals("213464140", reservarMargemResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", reservarMargemResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("BANCO BRASIL", reservarMargemResponse.getBoleto().getValue().getConsignataria());
		assertEquals("145", reservarMargemResponse.getBoleto().getValue().getCodVerba());
		assertEquals("EMPRÉSTIMO", reservarMargemResponse.getBoleto().getValue().getServico());
		assertEquals("500.0", reservarMargemResponse.getBoleto().getValue().getValorLiberado().toString());
		assertEquals("100.0", String.valueOf(reservarMargemResponse.getBoleto().getValue().getValorParcela()));
		assertEquals(10, reservarMargemResponse.getBoleto().getValue().getPrazo());
		assertEquals("Deferida", reservarMargemResponse.getBoleto().getValue().getSituacao());
		assertEquals("001", reservarMargemResponse.getBoleto().getValue().getServicoCodigo());
		assertEquals("4", reservarMargemResponse.getBoleto().getValue().getStatusCodigo());
		assertEquals("csa2", reservarMargemResponse.getBoleto().getValue().getResponsavel());
		assertEquals("29150.5", reservarMargemResponse.getBoleto().getValue().getSalario().getValue().toString());

		// verificar se criou a solicitação no banco e valor de parcela igual
		assertNotNull(
				autDescontoService.getAde(String.valueOf(reservarMargemResponse.getBoleto().getValue().getAdeNumero())));
		assertEquals("100.00", autDescontoService.getAde(String.valueOf(reservarMargemResponse.getBoleto().getValue().getAdeNumero())).getAdeVlr().toString());
	}


	@Test
	public void tentarReservarMargemSemInformarUsuario() throws IOException {
		log.info("Tentar reservar margem sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            reservarMargemClient.getResponse("", loginCsa.getSenha(),"" , "",
                                             "", "", "", "", "", "0", 0, "", "", nomeArquivo, arquivo);
        });
	}

	@Test
	public void tentarReservarMargemComUsuarioSemPermissao() throws IOException {
		log.info("Tentar reservar margem com usuário sem permissão");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse("zetra_igor", "abc12345",
				"" , "", "", "", "", "", "", "0", 0, "", "", nomeArquivo, arquivo);

		assertEquals("O usuário não tem permissão para executar esta operação", reservarMargemResponse.getMensagem());
		assertEquals("329", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
	public void tentarReservarMargemSemMatriculaouCpf() throws IOException {
		log.info("Tentar reservar margem sem matricula ou cpf");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(),"" , "", "", "", "", "", "", "0", 0, "", "", nomeArquivo, arquivo);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", reservarMargemResponse.getMensagem());
		assertEquals("305", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
	public void tentarReservarMargemSemCodigoVerbaouServico() throws IOException {
		log.info("Tentar reservar margem sem codigo de verba ou servico");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "", "0", 0, "", "", nomeArquivo, arquivo);

		assertEquals("Código da verba ou código do serviço deve ser informado.", reservarMargemResponse.getMensagem());
		assertEquals("406", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
	public void tentarReservarMargemComCodigoVerbaInvalido() throws IOException {
		log.info("Tentar reservar margem com codigo de verba inválido");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "", "0", 0, "", "000", nomeArquivo, arquivo);

		assertEquals("Código da verba ou código do serviço inválido.", reservarMargemResponse.getMensagem());
		assertEquals("232", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
	public void tentarReservarMargemComCodigoServicoInvalido() throws IOException {
		log.info("Tentar reservar margem com codigo de serviço inválido");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "00045", "0", 0, "", "", nomeArquivo, arquivo);

		assertEquals("Código da verba ou código do serviço inválido.", reservarMargemResponse.getMensagem());
		assertEquals("232", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}


	@Test
	public void tentarReservarMargemComSenhaServidorInvalida() throws IOException {
		log.info("Tentar reservar margem com senha de servidor inválida");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", codOrgao, codEstabelecimento, "123456789", "", codServico, valorParcela, 0, "", codVerba, nomeArquivo, arquivo);

		assertEquals("A senha de autorização do servidor não confere.", reservarMargemResponse.getMensagem());
		assertEquals("363", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
	public void tentarReservarMargemComPrazoInvalido() throws IOException {
		log.info("Tentar reservar margem com senha de servidor inválida");

		final RegistroServidor registroServidor = registroServidorService.obterRegistroServidorPorMatriculaOrgao(matricula, orgaoCodigo);

		registroServidorService.alterarRseMargemRest(matricula, new BigDecimal("100000.00"));

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", codOrgao, codEstabelecimento,loginServidor.getSenha(), loginServidor.getLogin(),
				codServico, valorParcela, 5, valorLiberado, codVerba, nomeArquivo, arquivo);

		assertEquals("Os prazos permitidos para este serviço são: 10.", reservarMargemResponse.getMensagem());
		assertEquals("471", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());


		registroServidorService.alterarRseMargemRest(matricula, registroServidor.getRseMargemRest());
	}

	@Test
	public void tentarReservarMargemComNomeAnexoIncorreto() throws IOException {
		log.info("Tentar reservar margem com nome de anexo incorreto, sem extensão");

		final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, cpf, codOrgao, codEstabelecimento,loginServidor.getSenha(), "", codServico, valorParcela, prazo, valorLiberado,
				codVerba, "carlotajoaquina", arquivo);

		assertEquals("O arquivo carlotajoaquina possui extensão não permitida.<br><br>Lista de extensões permitidas: .doc, .pdf, .xls, .docx, .xlsx, .txt, .csv", reservarMargemResponse.getMensagem());
		assertEquals("430", reservarMargemResponse.getCodRetorno().getValue());
		assertFalse(reservarMargemResponse.isSucesso());

	}

	@Test
    public void reservarMargemComSucessoLogadoComUsuCor() throws IOException {
        log.info("Reservar Margem com sucesso.");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, loginServidor.getSenha(), loginServidor.getLogin(), codServico, valorParcela,
                prazo, valorLiberado, codVerba, nomeArquivo, arquivo);

        assertEquals("Operação realizada com sucesso.", reservarMargemResponse.getMensagem());
        assertEquals("000", reservarMargemResponse.getCodRetorno().getValue());
        assertEquals("Sr. Antonio da Silva Augusto", reservarMargemResponse.getBoleto().getValue().getServidor());
        assertEquals("004.503.189-40", reservarMargemResponse.getBoleto().getValue().getCpf());
        assertEquals("M", reservarMargemResponse.getBoleto().getValue().getSexo());
        assertEquals("1980-01-01-03:00", reservarMargemResponse.getBoleto().getValue().getDataNascimento().toString());
        assertEquals("S", reservarMargemResponse.getBoleto().getValue().getEstadoCivil());
        assertEquals("3987139", reservarMargemResponse.getBoleto().getValue().getIdentidade());
        assertEquals("Jose Augusto", reservarMargemResponse.getBoleto().getValue().getPai());
        assertEquals("Antonia da Silva", reservarMargemResponse.getBoleto().getValue().getMae());
        assertEquals("Rua do Ouro", reservarMargemResponse.getBoleto().getValue().getEndereco());
        assertEquals("171", reservarMargemResponse.getBoleto().getValue().getNumero());
        assertEquals("apto 171", reservarMargemResponse.getBoleto().getValue().getComplemento());
        assertEquals("Centro", reservarMargemResponse.getBoleto().getValue().getBairro());
        assertEquals("Belo Horizonte", reservarMargemResponse.getBoleto().getValue().getCidade());
        assertEquals("MG", reservarMargemResponse.getBoleto().getValue().getUf());
        assertEquals("145985", reservarMargemResponse.getBoleto().getValue().getMatricula());
        assertEquals("1992-02-07-02:00", reservarMargemResponse.getBoleto().getValue().getDataAdmissao().toString());

    }

	@Test
    public void reservarMargemComSucessoDadosADEComUsuCor() throws IOException {
        log.info("Reservar Margem com sucesso.");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, cpf, codOrgao, codEstabelecimento, loginServidor.getSenha(), loginServidor.getLogin(), codServico, valorParcela,
                prazo, valorLiberado, codVerba, nomeArquivo, arquivo);

        assertEquals("Operação realizada com sucesso.", reservarMargemResponse.getMensagem());
        assertEquals("Carlota Joaquina 21.346.414/0001-47", reservarMargemResponse.getBoleto().getValue().getEstabelecimento());
        assertEquals("Carlota Joaquina 21.346.414/0001-47", reservarMargemResponse.getBoleto().getValue().getOrgao());
        assertEquals("213464140", reservarMargemResponse.getBoleto().getValue().getEstabelecimentoCodigo());
        assertEquals("213464140", reservarMargemResponse.getBoleto().getValue().getOrgaoCodigo());
        assertEquals("BANCO BRASIL", reservarMargemResponse.getBoleto().getValue().getConsignataria());
        assertEquals("145", reservarMargemResponse.getBoleto().getValue().getCodVerba());
        assertEquals("EMPRÉSTIMO", reservarMargemResponse.getBoleto().getValue().getServico());
        assertEquals("500.0", reservarMargemResponse.getBoleto().getValue().getValorLiberado().toString());
        assertEquals("100.0", String.valueOf(reservarMargemResponse.getBoleto().getValue().getValorParcela()));
        assertEquals(10, reservarMargemResponse.getBoleto().getValue().getPrazo());
        assertEquals("Aguard. Confirmação", reservarMargemResponse.getBoleto().getValue().getSituacao());
        assertEquals("001", reservarMargemResponse.getBoleto().getValue().getServicoCodigo());
        assertEquals("1", reservarMargemResponse.getBoleto().getValue().getStatusCodigo());
        assertEquals("cor", reservarMargemResponse.getBoleto().getValue().getResponsavel());
        assertEquals("29150.5", reservarMargemResponse.getBoleto().getValue().getSalario().getValue().toString());

        // verificar se criou a solicitação no banco e valor de parcela igual
        assertNotNull(
                autDescontoService.getAde(String.valueOf(reservarMargemResponse.getBoleto().getValue().getAdeNumero())));
        assertEquals("100.00", autDescontoService.getAde(String.valueOf(reservarMargemResponse.getBoleto().getValue().getAdeNumero())).getAdeVlr().toString());
    }

	@Test
    public void tentarReservarMargemSemMatriculaouCpfComUsuCor() throws IOException {
        log.info("Tentar reservar margem sem matricula ou cpf");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(),"" , "", "", "", "", "", "", "0", 0, "", "", nomeArquivo, arquivo);

        assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", reservarMargemResponse.getMensagem());
        assertEquals("305", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemSemCodigoVerbaouServicoComUsuCor() throws IOException {
        log.info("Tentar reservar margem sem codigo de verba ou servico");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "", "0", 0, "", "", nomeArquivo, arquivo);

        assertEquals("Código da verba ou código do serviço deve ser informado.", reservarMargemResponse.getMensagem());
        assertEquals("406", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemComCodigoVerbaInvalidoComUsuCor() throws IOException {
        log.info("Tentar reservar margem com codigo de verba inválido");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "", "0", 0, "", "000", nomeArquivo, arquivo);

        assertEquals("Código da verba ou código do serviço inválido.", reservarMargemResponse.getMensagem());
        assertEquals("232", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemComCodigoServicoInvalidoComUsuCor() throws IOException {
        log.info("Tentar reservar margem com codigo de serviço inválido");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, "", codOrgao, codEstabelecimento, "", "", "00045", "0", 0, "", "", nomeArquivo, arquivo);

        assertEquals("Código da verba ou código do serviço inválido.", reservarMargemResponse.getMensagem());
        assertEquals("232", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemComPrazoInvalidoComUsuCor() throws IOException {
        log.info("Tentar reservar margem com senha de servidor inválida");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, "", codOrgao, codEstabelecimento,loginServidor.getSenha(), loginServidor.getLogin(),
                codServico, valorParcela, 5, valorLiberado, codVerba, nomeArquivo, arquivo);

        assertEquals("Os prazos permitidos para este serviço são: 10.", reservarMargemResponse.getMensagem());
        assertEquals("471", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemComNomeAnexoIncorretoComUsuCor() throws IOException {
        log.info("Tentar reservar margem com nome de anexo incorreto, sem extensão");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), matricula, cpf, codOrgao, codEstabelecimento,loginServidor.getSenha(), "", codServico, valorParcela, prazo, valorLiberado,
                codVerba, "carlotajoaquina", arquivo);

        assertEquals("O arquivo carlotajoaquina possui extensão não permitida.<br><br>Lista de extensões permitidas: .doc, .pdf, .xls, .docx, .xlsx, .txt, .csv", reservarMargemResponse.getMensagem());
        assertEquals("430", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void tentarReservarMargemComSenhaServidorInvalidaComUsuCor() throws IOException {
        log.info("Tentar reservar margem com senha de servidor inválida");

        parametroSistemaService.alterarParametroConsignataria("csa2", CodedValues.TPA_SENHA_SER_RESERVAR_MARGEM_HOST_A_HOST_COR, "S");

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCor.getLogin(),
                  loginCor.getSenha(), matricula, "", codOrgao, codEstabelecimento, "1478", loginServidor.getLogin(), codServico, valorParcela, 10, valorLiberado, codVerba, nomeArquivo, arquivo);

        assertEquals("A senha de autorização do servidor não confere.", reservarMargemResponse.getMensagem());
        assertEquals("363", reservarMargemResponse.getCodRetorno().getValue());
        assertFalse(reservarMargemResponse.isSucesso());

    }

    @Test
    public void reservarMargemComSucessoComMatriculaIncompleta() throws IOException {
        log.info("Reservar Margem com sucesso passando parte da matricula.");

        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA, "S");
        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "4");
        ENominaInitializer.limparCache();

        final ReservarMargemResponse reservarMargemResponse = reservarMargemClient.getResponse(loginCsa.getLogin(),
                loginCsa.getSenha(), "1459", cpf, codOrgao, codEstabelecimento, loginServidor.getSenha(), loginServidor.getLogin(), codServico, valorParcela,
                prazo, valorLiberado, codVerba, nomeArquivo, arquivo);

        assertEquals("Operação realizada com sucesso.", reservarMargemResponse.getMensagem());
        assertEquals("000", reservarMargemResponse.getCodRetorno().getValue());
        assertEquals("Sr. Antonio da Silva Augusto", reservarMargemResponse.getBoleto().getValue().getServidor());
        assertEquals("004.503.189-40", reservarMargemResponse.getBoleto().getValue().getCpf());
        assertEquals("M", reservarMargemResponse.getBoleto().getValue().getSexo());
        assertEquals("1980-01-01-03:00", reservarMargemResponse.getBoleto().getValue().getDataNascimento().toString());
        assertEquals("S", reservarMargemResponse.getBoleto().getValue().getEstadoCivil());
        assertEquals("3987139", reservarMargemResponse.getBoleto().getValue().getIdentidade());
        assertEquals("Jose Augusto", reservarMargemResponse.getBoleto().getValue().getPai());
        assertEquals("Antonia da Silva", reservarMargemResponse.getBoleto().getValue().getMae());
        assertEquals("Rua do Ouro", reservarMargemResponse.getBoleto().getValue().getEndereco());
        assertEquals("171", reservarMargemResponse.getBoleto().getValue().getNumero());
        assertEquals("apto 171", reservarMargemResponse.getBoleto().getValue().getComplemento());
        assertEquals("Centro", reservarMargemResponse.getBoleto().getValue().getBairro());
        assertEquals("Belo Horizonte", reservarMargemResponse.getBoleto().getValue().getCidade());
        assertEquals("MG", reservarMargemResponse.getBoleto().getValue().getUf());
        assertEquals("145985", reservarMargemResponse.getBoleto().getValue().getMatricula());
        assertEquals("1992-02-07-02:00", reservarMargemResponse.getBoleto().getValue().getDataAdmissao().toString());

        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA, "");
        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "6");
        ENominaInitializer.limparCache();
    }
}
