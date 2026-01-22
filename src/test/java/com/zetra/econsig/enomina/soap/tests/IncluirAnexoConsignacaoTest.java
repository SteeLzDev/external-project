package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.IncluirAnexoConsignacaoClient;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.soap.IncluirAnexoConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IncluirAnexoConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private String adeNumero = "60897";
	private final String adeIdentificador = "Solicitação Web";
	private final String nomeArquivoPdf = "arquivo_para_teste.pdf";
	private final String nomeArquivoZip = "carlotajoaquina.zip";
	private final String nomeArquivoMsg = "boleto_v3.msg";
	private final String arquivoPdf = "src/test/resources/files/arquivo_para_teste.pdf";
	private final String arquivoZip = "src/test/resources/files/carlotajoaquina_20200501_20210318103454.zip";
	private final String arquivoMsg = "src/test/resources/files/boleto_v3.msg";

	@Autowired
    private IncluirAnexoConsignacaoClient incluirAnexoConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Test
	public void incluirAnexoConsignacaoFormatoPdfComSucesso() throws IOException {
		log.info("Incluir anexo consignação formato pdf com sucesso");

		adeNumero = "60906";

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoPdf, arquivoPdf, "07/2021");

		assertEquals("Operação realizada com sucesso.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("000", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que gravou no banco de dados
		assertFalse(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void incluirAnexoConsignacaoFormatoZipComSucesso() throws IOException {
		log.info("Incluir anexo consignação formato zip com sucesso");

		adeNumero = "60907";

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Operação realizada com sucesso.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("000", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertTrue(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que gravou no banco de dados
		assertFalse(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void incluirVariosAnexoConsignacao() throws IOException {
		log.info("Incluir varios anexo consignação");

		adeNumero = "60908";

		IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Operação realizada com sucesso.", incluirAnexoConsignacaoResponse.getMensagem());

		incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoPdf, arquivoPdf, "06/2021");

		assertEquals("Operação realizada com sucesso.", incluirAnexoConsignacaoResponse.getMensagem());

		// verifica que gravou no banco de dados
		assertEquals(2, autDescontoService.getAnexoAutDesconto(adeNumero.toString()).size());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComPeriodoInvalido() throws IOException {
		log.info("Tentar incluir anexo consignação com periodo inválido");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoPdf, arquivoPdf, "01/06/2021");

		assertEquals("O período informado é inválido.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("409", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoVariasConsignacoes() throws IOException {
		log.info("Tentar incluir anexo varias consignações");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "", adeIdentificador, nomeArquivoPdf, arquivoPdf, "06/2021");

		assertEquals("Mais de uma consignação encontrada", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("245", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoSemInformaAdeNumeroEAdeIdentificador() throws IOException {
		log.info("Tentar incluir anexo varias consignações");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "", "", nomeArquivoPdf, arquivoPdf, "06/2021");

		assertEquals("Mais de uma consignação encontrada", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("245", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoSemInformarNomeArquivo() throws IOException {
		log.info("Tentar incluir anexo consignação sem informar nome arquivo");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirAnexoConsignacaoClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "", arquivoPdf, "06/2021");
        });

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComNomeArquivoInvalido() throws IOException {
		log.info("Tentar incluir anexo consignação com nome arquivo invalido");

		IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "carlotajoaquina.png", arquivoPdf, "06/2021");

		assertEquals("Upload abortado, conteúdo do arquivo não é compatível com a(s) extensões permitidas. Extensões permitidas: .gif,.jpg,.jpeg,.jpe,.jfif,.jfi,.png,.pdf,.rtf,.doc,.docx,.xls,.xlsx,.txt,.csv,.zip,.webm,.mp4,.fad,.wma,.mp3,.key,.fida.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("430", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, "carlotajoaquina.xml", arquivoPdf, "06/2021");

		assertEquals("Não é possível realizar a cópia deste tipo de arquivo. São permitidos apenas arquivos .gif, .jpg, .jpeg, .jpe, .jfif, .jfi, .png, .pdf, .rtf, .doc, .docx, .xls, .xlsx, .txt, .csv, .zip, .webm, .mp4, .fad, .wma, .mp3, .key, .fida.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("430", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComArquivoInvalido() throws IOException {
		log.info("Tentar incluir anexo consignação com arquivo inválido");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoMsg, arquivoMsg, "06/2021");

		assertEquals("Não é possível realizar a cópia deste tipo de arquivo. São permitidos apenas arquivos .gif, .jpg, .jpeg, .jpe, .jfif, .jfi, .png, .pdf, .rtf, .doc, .docx, .xls, .xlsx, .txt, .csv, .zip, .webm, .mp4, .fad, .wma, .mp3, .key, .fida.", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("430", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoNaoExistente() throws IOException {
		log.info("Tentar incluir anexo consignação não existente");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), "60800", adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Nenhuma consignação encontrada", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("294", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoJaCancelada() throws IOException {
		log.info("Tentar incluir anexo consignação já cancelada");

		// ade com status cancelada
		adeNumero = "60903";

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Nenhuma consignação encontrada", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("294", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComUsuarioInvalido() throws IOException {
		log.info("Tentar incluir anexo consignação com usuário inválido");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse("csa1", loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Usuário ou senha inválidos", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("358", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoDeSenhaInvalida() throws IOException {
		log.info("Tentar incluir anexo consignação de senha inválida");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse(loginCsa.getLogin(), "ser12345", adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("Usuário ou senha inválidos", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("358", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComIPDeAcessoInvalido() throws IOException {
		log.info("Tentar incluir anexo consignação com IP de acesso inválido");

		// alterar parametro
		parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, "N");
        ENominaInitializer.limparCache();

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse("csa", loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("IP de acesso inválido", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("362", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoSemInformarUsuario() throws IOException {
		log.info("Tentar incluir anexo consignação sem informar usuário");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirAnexoConsignacaoClient.getResponse("", loginCsa.getSenha(), adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");
        });

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoSemInformarSenha() throws IOException {
		log.info("Tentar incluir anexo consignação sem informar senha");

        Assertions.assertThrows(SoapFaultClientException.class, () -> {
            incluirAnexoConsignacaoClient.getResponse(loginCsa.getLogin(), "", adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");
        });

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}

	@Test
	public void tentarIncluirAnexoConsignacaoComUsuarioSemPermissao() throws IOException {
		log.info("Tentar incluir anexo consignação com usuário sem permissão");

		final IncluirAnexoConsignacaoResponse incluirAnexoConsignacaoResponse = incluirAnexoConsignacaoClient
				.getResponse("cse", "cse12345", adeNumero, adeIdentificador, nomeArquivoZip, arquivoZip, "06/2021");

		assertEquals("O usuário não tem permissão para executar esta operação", incluirAnexoConsignacaoResponse.getMensagem());
		assertEquals("329", incluirAnexoConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(incluirAnexoConsignacaoResponse.isSucesso());

		// verifica que não gravou no banco de dados
		assertTrue(autDescontoService.getAnexoAutDesconto(adeNumero.toString()).isEmpty());
	}
}
