package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.zetra.econsig.enomina.soap.client.ConsultarMargemClient;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.ConsultarMargemResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsultarMargemTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private final String codVerba = "145";
	private final String codServico = "001";
	private final String matricula = "181818";
	private final String cpf = "956.052.586-72";
	private final String valorParcela = "100";
	private final String codOrgao = "213464140";
	private final String codEstabelecimento = "213464140";

	@Autowired
    private ConsultarMargemClient consultarMargemClient;

	@Autowired
    private ConsignatariaService consignatariaService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void setUp() {
	    // Desbloqueia a CSA caso esteja bloqueada
	    consignatariaService.alterarStatusConsignataria(usuarioService.getCsaCodigo(loginCsa.getLogin()),
                CodedValues.STS_ATIVO.toString());
	}

	@Test
	public void consultarMargemComSucessoPrincipaisCampos() {
		log.info("Consultar margem com sucesso.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", "",	"", "", "", valorParcela, "", "");

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("360", solicitacaoResponse.getCodRetorno().getValue());
		assertTrue(solicitacaoResponse.isSucesso());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", solicitacaoResponse.getServidores().get(0).getEstabelecimento());
		assertEquals("Sr. RONNIE SOARES DE SOUZA", solicitacaoResponse.getServidores().get(0).getServidor());
		assertEquals("956.052.586-72", solicitacaoResponse.getServidores().get(0).getCpf());
		assertEquals("181818", solicitacaoResponse.getServidores().get(0).getMatricula());
		assertEquals("1972-09-22-03:00", solicitacaoResponse.getServidores().get(0).getDataNascimento().getValue().toString());
		assertEquals("-1", solicitacaoResponse.getServidores().get(0).getPrazoServidor().getValue().toString());
		assertEquals("MARGEM 1", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getDescricao());
		assertEquals("4635.97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getValorDisponivel().getValue().toString());
		assertEquals("Valor da parcela informado é menor ou igual à margem disponível.: R$4635,97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getMensagem().getValue().toString());
		assertEquals("MARGEM 2", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getDescricao());
		assertEquals("191.56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getValorDisponivel().getValue().toString());
		assertEquals("Valor da parcela informado é menor ou igual à margem disponível.: R$191,56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getMensagem().getValue().toString());
		assertEquals("7799.36", solicitacaoResponse.getServidores().get(0).getSalarioLiquido().getValue().toString());
	}


	@Test
	public void consultarMargemComValorMaiorMargemDisponivel() {
		log.info("Consultar margem com valor de parcela maior do que margem disponivel.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), matricula, "", "", "", "", "", "1000000.00", "", "");

		assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
		assertEquals("359", solicitacaoResponse.getCodRetorno().getValue());
		assertTrue(solicitacaoResponse.isSucesso());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", solicitacaoResponse.getServidores().get(0).getEstabelecimento());
		assertEquals("Sr. RONNIE SOARES DE SOUZA", solicitacaoResponse.getServidores().get(0).getServidor());
		assertEquals("956.052.586-72", solicitacaoResponse.getServidores().get(0).getCpf());
		assertEquals("181818", solicitacaoResponse.getServidores().get(0).getMatricula());
		assertEquals("1972-09-22-03:00", solicitacaoResponse.getServidores().get(0).getDataNascimento().getValue().toString());
		assertEquals("-1", solicitacaoResponse.getServidores().get(0).getPrazoServidor().getValue().toString());
		assertEquals("MARGEM 1", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getDescricao());
		assertEquals("4635.97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getValorDisponivel().getValue().toString());
		assertEquals("Valor da parcela informado é maior do que margem disponível.: R$4635,97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getMensagem().getValue().toString());
		assertEquals("MARGEM 2", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getDescricao());
		assertEquals("191.56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getValorDisponivel().getValue().toString());
		assertEquals("Valor da parcela informado é maior do que margem disponível.: R$191,56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getMensagem().getValue().toString());
		assertEquals("7799.36", solicitacaoResponse.getServidores().get(0).getSalarioLiquido().getValue().toString());
		assertEquals("MG", solicitacaoResponse.getServidores().get(0).getUfIdentidade().getValue().toString());
		assertEquals("SSP", solicitacaoResponse.getServidores().get(0).getEmissorIdentidade().getValue().toString());
		assertEquals("Belo Horizonte", solicitacaoResponse.getServidores().get(0).getCidadeNascimento().getValue().toString());
		assertEquals("BRASILEIRO", solicitacaoResponse.getServidores().get(0).getNacionalidade().getValue().toString());
		assertEquals("M", solicitacaoResponse.getServidores().get(0).getSexo().getValue().toString());
		assertEquals("Solteiro(a)", solicitacaoResponse.getServidores().get(0).getEstadoCivil().getValue().toString());
		assertEquals("31-32659874", solicitacaoResponse.getServidores().get(0).getTelefone().getValue().toString());
		assertEquals("31-985637485", solicitacaoResponse.getServidores().get(0).getCelular().getValue().toString());
	}

	@Test
	public void consultarMargemSemMatricula() {
		log.info("Consultar Margem sem matricula.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), "", loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, "", valorParcela, codOrgao, codEstabelecimento);

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", solicitacaoResponse.getMensagem());
		assertEquals("305", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemSemValorParcela() {
		log.info("Consultar Margem sem valor de parcela.");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            consultarMargemClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), matricula, loginServidor.getLogin(), loginServidor.getSenha(),
                                              codVerba, codServico, cpf, "", codOrgao, codEstabelecimento);
        });
	}

	@Test
	public void consultarMargemUsarioSemPermissao() {
		log.info("Consultar Margem com usuário sem permissão.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse("zetra_igor", "abc12345",
				matricula, loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, cpf, valorParcela, codOrgao, codEstabelecimento);

		assertEquals("O usuário não tem permissão para executar esta operação", solicitacaoResponse.getMensagem());
		assertEquals("329", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemServidorInvalido() {
		log.info("Consultar Margem com servidor invalido.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(),"888520", loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, cpf, valorParcela, codOrgao, codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", solicitacaoResponse.getMensagem());
		assertEquals("293", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemUsuarioUsuarioInvalido() {
		log.info("Consultar Margem usuario e senha invalido.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse("csa1",
				loginCsa.getSenha(),matricula, loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, cpf, valorParcela, codOrgao, codEstabelecimento);

		assertEquals("Usuário ou senha inválidos", solicitacaoResponse.getMensagem());
		assertEquals("358", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemMatriculaInvalida() {
		log.info("Consultar Margem com matricula invalida.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(),"1100", loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, cpf, valorParcela, codOrgao, codEstabelecimento);

		assertEquals("A matrícula informada é inválida.", solicitacaoResponse.getMensagem());
		assertEquals("210", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemCpfInvalido() {
		log.info("Consultar Margem com cpf invalido.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(),matricula, loginServidor.getLogin(), loginServidor.getSenha(),
				codVerba, codServico, "22222222222", valorParcela, codOrgao, codEstabelecimento);

		assertEquals("Nenhum servidor encontrado", solicitacaoResponse.getMensagem());
		assertEquals("293", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
	public void consultarMargemCodigoVerbaInvalido() {
		log.info("Consultar Margem com codigo de verba invalido.");

		final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(),matricula, loginServidor.getLogin(), loginServidor.getSenha(),
				"7890", codServico, cpf, valorParcela, codOrgao, codEstabelecimento);

		assertEquals("Código da verba ou código do serviço inválido.", solicitacaoResponse.getMensagem());
		assertEquals("232", solicitacaoResponse.getCodRetorno().getValue());
	}

	@Test
    public void consultarMargemComMatriculaIncompleta() {
        log.info("Consultar Margem passando parte da matricula.");

        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA, "S");
        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "4");
        ENominaInitializer.limparCache();

        final ConsultarMargemResponse solicitacaoResponse = consultarMargemClient.getResponse(loginCsa.getLogin(),
                                                                                              loginCsa.getSenha(), "1818", "", "", "", "", "", valorParcela, "", "");

        assertEquals("Operação realizada com sucesso.", solicitacaoResponse.getMensagem());
        assertEquals("360", solicitacaoResponse.getCodRetorno().getValue());
        assertTrue(solicitacaoResponse.isSucesso());
        assertEquals("Carlota Joaquina 21.346.414/0001-47", solicitacaoResponse.getServidores().get(0).getEstabelecimento());
        assertEquals("Sr. RONNIE SOARES DE SOUZA", solicitacaoResponse.getServidores().get(0).getServidor());
        assertEquals("956.052.586-72", solicitacaoResponse.getServidores().get(0).getCpf());
        assertEquals("181818", solicitacaoResponse.getServidores().get(0).getMatricula());
        assertEquals("1972-09-22-03:00", solicitacaoResponse.getServidores().get(0).getDataNascimento().getValue().toString());
        assertEquals("-1", solicitacaoResponse.getServidores().get(0).getPrazoServidor().getValue().toString());
        assertEquals("MARGEM 1", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getDescricao());
        assertEquals("4635.97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getValorDisponivel().getValue().toString());
        assertEquals("Valor da parcela informado é menor ou igual à margem disponível.: R$4635,97", solicitacaoResponse.getServidores().get(0).getMargens().get(0).getMensagem().getValue().toString());
        assertEquals("MARGEM 2", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getDescricao());
        assertEquals("191.56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getValorDisponivel().getValue().toString());
        assertEquals("Valor da parcela informado é menor ou igual à margem disponível.: R$191,56", solicitacaoResponse.getServidores().get(0).getMargens().get(1).getMensagem().getValue().toString());
        assertEquals("7799.36", solicitacaoResponse.getServidores().get(0).getSalarioLiquido().getValue().toString());

        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA, "");
        parametroSistemaService.alterarParametroConsignataria(loginCsa.getLogin(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, "");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MATRICULA, "6");
        ENominaInitializer.limparCache();
    }
}
