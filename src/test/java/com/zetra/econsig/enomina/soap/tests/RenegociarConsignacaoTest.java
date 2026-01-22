package com.zetra.econsig.enomina.soap.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.RenegociarConsignacaoClient;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.soap.RenegociarConsignacaoResponse;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RenegociarConsignacaoTest extends ENominaContextSpringConfiguration {

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginCor = LoginValues.cor1;
	private final Long adeNumero = (long) 60926;
	private final Long adeNumero2 = (long) 61018;
	private final double valorParcela = 149;
	private final double valorParcela2 = 15;
	private final String valorLiberado = "2000";
	private final String codVerba = "145";
	private final String prazo = "10";
	private final String senhaServidor = "ser12345";
	private final String matricula = "123456";

	@Autowired
    private RenegociarConsignacaoClient renegociarConsignacaoClient;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
    private FuncaoService funcaoService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @BeforeEach
    public void setUpForUsuCor() throws Exception {
        funcaoService.criarFuncaoPerfilCor(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), CodedValues.FUN_RENE_CONTRATO, usuarioService.getCorCodigo(loginCor.getLogin()));
        funcaoService.criarFuncaoPerfilCor(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA, usuarioService.getCorCodigo(loginCor.getLogin()));
        funcaoService.criarFuncaoPerfilCor(usuarioService.getUsuario(loginCor.getLogin()).getUsuCodigo(), CodedValues.FUN_INTEGRA_SOAP_OPERACIONAL, usuarioService.getCorCodigo(loginCor.getLogin()));
    }

	@Test
	public void renegociarConsignacaoComSucesso() throws IOException {
		log.info("Renegociar Consignação com sucesso.");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);


		assertEquals("Operação realizada com sucesso.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("000", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertEquals("Sr. BOB da Silva Shawn", renegociarConsignacaoResponse.getBoleto().getValue().getServidor());
		assertEquals("092.459.399-79", renegociarConsignacaoResponse.getBoleto().getValue().getCpf());
		assertEquals("M", renegociarConsignacaoResponse.getBoleto().getValue().getSexo());
		assertEquals("S", renegociarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
		assertEquals("Jose Augusto",renegociarConsignacaoResponse.getBoleto().getValue().getPai());
		assertEquals("Maria Raimunda",renegociarConsignacaoResponse.getBoleto().getValue().getMae());
		assertEquals("123456", renegociarConsignacaoResponse.getBoleto().getValue().getMatricula());
		assertEquals("Carlota Joaquina 21.346.414/0001-47", renegociarConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
		assertEquals("213464140", renegociarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
		assertEquals("213464140", renegociarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
		assertEquals("145", renegociarConsignacaoResponse.getBoleto().getValue().getCodVerba());
		assertEquals("Deferida", renegociarConsignacaoResponse.getBoleto().getValue().getSituacao());
		assertEquals("4", renegociarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

		//validar novo numero de Ade gerado na renegociação
		assertNotNull(autDescontoService.getAde(String.valueOf(renegociarConsignacaoResponse.getBoleto().getValue().getAdeNumero())));

	}

	@Test
	public void tentarRenegociarConsignacaoComMatriculaInvalida() throws IOException {
		log.info("Tentar renegociar consignação com matricula invalida");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, "1111");

		assertEquals("A matrícula informada é inválida.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("210", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoSemMatricula() throws IOException {
		log.info("Tentar renegociar consignação sem matricula");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, "");

		assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("305", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoSemCodVerba() throws IOException {
		log.info("Tentar renegociar consignação sem código verba");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, "", prazo, senhaServidor, matricula);

		assertEquals("Código da verba ou código do serviço deve ser informado.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("406", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoComCodVerbaInvalido() throws IOException {
		log.info("Tentar renegociar consignação com código verba inválido");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, "0123", prazo, senhaServidor, matricula);

		assertEquals("Código da verba ou código do serviço inválido.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("232", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoValorParcelaZero() throws IOException {
		log.info("Tentar renegociar consignação com valor parcela igual a zero");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, (double) 0, valorLiberado, codVerba, prazo, senhaServidor, matricula);

		assertEquals("O valor da prestação deve ser maior do que zero.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("334", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoPrazoAcimaDolimite() throws IOException {
		log.info("Tentar renegociar consignação com valor de prazo acima do limite");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, "100", senhaServidor, matricula);

		assertEquals("Prazo total do contrato (número de prestações mais carência) deve ser menor ou igual a 36.", renegociarConsignacaoResponse.getMensagem());
		assertEquals("347", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoSenhaIncorreta() throws IOException {
		log.info("Tentar renegociar consignação com senha incorreta");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				"123456789", adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);

		assertEquals("Usuário ou senha inválidos", renegociarConsignacaoResponse.getMensagem());
		assertEquals("358", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoUsuarioSemPermissao() throws IOException {
		log.info("Tentar renegociar consignação com senha incorreta");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse("zetra_igor",
				"abc12345", adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);

		assertEquals("O usuário não tem permissão para executar esta operação", renegociarConsignacaoResponse.getMensagem());
		assertEquals("329", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
	public void tentarRenegociarConsignacaoAdeInexistente() throws IOException {
		log.info("Tentar renegociar consignação com ADE inexistente");

		final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCsa.getLogin(),
				loginCsa.getSenha(), (long) 124578, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);

		assertEquals("Nenhuma consignação encontrada", renegociarConsignacaoResponse.getMensagem());
		assertEquals("294", renegociarConsignacaoResponse.getCodRetorno().getValue());
		assertFalse(renegociarConsignacaoResponse.isSucesso());

	}

	@Test
    public void renegociarConsignacaoComSucessoComUsuCor() throws IOException {
        log.info("Renegociar Consignação com sucesso.");

        // Cria consignacao
        final AutDesconto ade = autDescontoService.inserirAutDesconto("909304", CodedValues.SAD_DEFERIDA, "48178080808080808080808080808C80", "751F8080808080808080808080809Z85", "C4228080808080808080808080800D80", 100.00f, 4099000L, 10, Short.parseShort("1"));

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), ade.getAdeNumero(), valorParcela2, "", codVerba, prazo, senhaServidor, matricula);

        assertEquals("Operação realizada com sucesso.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("000", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertEquals("Sr. BOB da Silva Shawn", renegociarConsignacaoResponse.getBoleto().getValue().getServidor());
        assertEquals("092.459.399-79", renegociarConsignacaoResponse.getBoleto().getValue().getCpf());
        assertEquals("M", renegociarConsignacaoResponse.getBoleto().getValue().getSexo());
        assertEquals("S", renegociarConsignacaoResponse.getBoleto().getValue().getEstadoCivil());
        assertEquals("Jose Augusto",renegociarConsignacaoResponse.getBoleto().getValue().getPai());
        assertEquals("Maria Raimunda",renegociarConsignacaoResponse.getBoleto().getValue().getMae());
        assertEquals("123456", renegociarConsignacaoResponse.getBoleto().getValue().getMatricula());
        assertEquals("Carlota Joaquina 21.346.414/0001-47", renegociarConsignacaoResponse.getBoleto().getValue().getEstabelecimento());
        assertEquals("213464140", renegociarConsignacaoResponse.getBoleto().getValue().getEstabelecimentoCodigo());
        assertEquals("213464140", renegociarConsignacaoResponse.getBoleto().getValue().getOrgaoCodigo());
        assertEquals("145", renegociarConsignacaoResponse.getBoleto().getValue().getCodVerba());
        assertEquals("Aguard. Confirmação", renegociarConsignacaoResponse.getBoleto().getValue().getSituacao());
        assertEquals("1", renegociarConsignacaoResponse.getBoleto().getValue().getStatusCodigo());

        //validar novo numero de Ade gerado na renegociação
        assertNotNull(autDescontoService.getAde(String.valueOf(renegociarConsignacaoResponse.getBoleto().getValue().getAdeNumero())));

    }

    @Test
    public void tentarRenegociarConsignacaoComMatriculaInvalidaComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com matricula invalida");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, "1111");

        assertEquals("A matrícula informada é inválida.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("210", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoSemMatriculaComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação sem matricula");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, "");

        assertEquals("A matrícula e/ou o CPF do servidor devem ser informados.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("305", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoSemCodVerbaComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação sem código verba");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero, valorParcela, valorLiberado, "", prazo, senhaServidor, matricula);

        assertEquals("Código da verba ou código do serviço deve ser informado.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("406", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoComCodVerbaInvalidoComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com código verba inválido");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero, valorParcela, valorLiberado, "0123", prazo, senhaServidor, matricula);

        assertEquals("Código da verba ou código do serviço inválido.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("232", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoValorParcelaZeroComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com valor parcela igual a zero");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero, (double) 0, valorLiberado, codVerba, prazo, senhaServidor, matricula);

        assertEquals("O valor da prestação deve ser maior do que zero.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("334", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoPrazoAcimaDolimiteComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com valor de prazo acima do limite");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), adeNumero2, valorParcela, valorLiberado, codVerba, "2000", senhaServidor, matricula);

        assertEquals("Prazo total do contrato (número de prestações mais carência) deve ser menor ou igual a 36.", renegociarConsignacaoResponse.getMensagem());
        assertEquals("347", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoSenhaIncorretaComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com senha incorreta");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                "123456789", adeNumero, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);

        assertEquals("Usuário ou senha inválidos", renegociarConsignacaoResponse.getMensagem());
        assertEquals("358", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

    @Test
    public void tentarRenegociarConsignacaoAdeInexistenteComUsuCor() throws IOException {
        log.info("Tentar renegociar consignação com ADE inexistente");

        final RenegociarConsignacaoResponse renegociarConsignacaoResponse = renegociarConsignacaoClient.getResponse(loginCor.getLogin(),
                loginCor.getSenha(), (long) 124578, valorParcela, valorLiberado, codVerba, prazo, senhaServidor, matricula);

        assertEquals("Nenhuma consignação encontrada", renegociarConsignacaoResponse.getMensagem());
        assertEquals("294", renegociarConsignacaoResponse.getCodRetorno().getValue());
        assertFalse(renegociarConsignacaoResponse.isSucesso());

    }

}
