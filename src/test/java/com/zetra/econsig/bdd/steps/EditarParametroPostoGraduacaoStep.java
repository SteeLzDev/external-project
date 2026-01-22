package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.EditarParametroPostoGraduacaoPage;
import com.zetra.econsig.persistence.entity.ParamPostoCsaSvc;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.ParamPostoCsaSvcService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PostoRegistroServidorService;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Então;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EditarParametroPostoGraduacaoStep {

	@Autowired
	private FuncaoService funcaoService;

	@Autowired
	private ParametroSistemaService paramSistemaService;

	@Autowired
	private PostoRegistroServidorService postoRegistroServidorService;

	@Autowired
	private ParamPostoCsaSvcService paramPostoCsaSvcService;

    private EditarParametroPostoGraduacaoPage editarParametroPostoGraduacaoPage;

    @Before
    public void setUp() throws Exception {
        editarParametroPostoGraduacaoPage = new EditarParametroPostoGraduacaoPage(getWebDriver());
    }

	@Dado("que a função {string} exista para usuário {string} da consignatária {string}")
	public void setarParametroDePosto(String funCodigo, String usuCodigo, String csaCodigo) {
		log.info("que a função {string} exista para usuário {string} da consignatária {string}");
		funcaoService.criarPapelFuncao(CodedValues.PAP_CONSIGNATARIA, funCodigo);
		funcaoService.criarFuncaoPerfilCsa(usuCodigo, funCodigo, csaCodigo);
	}

	@Dado("que a função {string} não exista para usuário {string} da consignatária {string}")
	public void deletarParametroDePosto(String funCodigo, String usuCodigo, String csaCodigo) {
		log.info("que a função {string} não exista para usuário {string} da consignatária {string}");
		funcaoService.deletarFuncaoPerfilCsa(usuCodigo, funCodigo, csaCodigo);
	}

	@Dado("que o serviço {string} tenha valor fixo por posto de graduação para consignatária {string}")
	public void setarParametroDeServicoDeEditarPosto(String svcCodigo, String csaCodigo) {
		log.info("Dado que o serviço {string} tenha valor fixo por posto de graduação");
		paramSistemaService.configurarParametroServicoCsa(svcCodigo, CodedValues.TPS_VALOR_SVC_FIXO_POSTO, csaCodigo, "S");

	}

	@Então("o {string} esteja presente no combo de serviços")
	public void verificaComboServico(String svcCodigo) {
		log.info("E o {string} esteja presente no combo de serviços");
		editarParametroPostoGraduacaoPage.selecionarServico();
		assertTrue(editarParametroPostoGraduacaoPage.temOServico(svcCodigo));
	}

	@E("escolhe o serviço {string} configurado para ter valor fixo por posto de graduação")
	public void configurarServicoParamDePosto(String svcCodigo) {
		log.info("E escolhe o serviço {string} configurado para ter valor fixo por posto de graduação");
		editarParametroPostoGraduacaoPage.selecionarServico();
		assertEquals("4C868080808080808080808088886275",editarParametroPostoGraduacaoPage.verificaValorCampoServico());
	}

	@E("informa valores para os postos de graduação e salva")
	public void informarValoresPostoGraduacaoESalva() {
		log.info("E informa valores para os postos de graduação e salva");
		editarParametroPostoGraduacaoPage.preencherCamposPostoDeGraduacaoSoldado("10000");
		editarParametroPostoGraduacaoPage.preencherCamposPostoDeGraduacaoCapitao("20000");
		editarParametroPostoGraduacaoPage.preencherCamposPostoDeGraduacaoTenente("35000");
		editarParametroPostoGraduacaoPage.salvarValoresPosto();
		assertEquals("Alterações salvas com sucesso.", editarParametroPostoGraduacaoPage.mensagemSucessoAoSalvar());
	}

	@Entao("deve ser exibida a tela de edição de parâmetros de posto de graduação")
	public void verificaTituloTela() {
		log.info("Então deve ser exibida a tela de edição de parâmetros de posto de graduação");
		assertEquals("Edição de dados", editarParametroPostoGraduacaoPage.tituloTela());
	}

	@Então("para cada posto de graduação, verifique se os dados foram persistidos no banco")
	public void verificaValoresPostGraduacao() {
		log.info("para cada posto de graduação, verifique se os dados foram persistidos no banco");
		final ParamPostoCsaSvc postoSoldado = paramPostoCsaSvcService.buscarParamPostoCsaSvc("141", "4C868080808080808080808088886275 ", "267");
		final ParamPostoCsaSvc postoCapitao = paramPostoCsaSvcService.buscarParamPostoCsaSvc("142", "4C868080808080808080808088886275 ", "267");
		final ParamPostoCsaSvc postoTenente = paramPostoCsaSvcService.buscarParamPostoCsaSvc("143", "4C868080808080808080808088886275 ", "267");
		assertEquals("100,00", postoSoldado.getPpoVlr());
		assertEquals("200,00", postoCapitao.getPpoVlr());
		assertEquals("350,00", postoTenente.getPpoVlr());
	}


	@Dado("que tenha postos de graduação cadastrado")
	public void postosGraduacaoCadastro() {
		log.info("que tenha postos de graduação cadastrado");
		postoRegistroServidorService.criarPostoRegistroServidor("141", "soldado", "001");
		postoRegistroServidorService.criarPostoRegistroServidor("142", "capitao","002");
		postoRegistroServidorService.criarPostoRegistroServidor("143", "tenente", "003");

	}
}