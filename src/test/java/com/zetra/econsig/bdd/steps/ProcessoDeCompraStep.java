package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ProcessoDeCompraPage;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PeriodoExportacaoService;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.service.RelacionamentoServicoService;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusCompraEnum;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProcessoDeCompraStep {
	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@Autowired
	private RelacionamentoServicoService relacionamentoServicoService;

	@Autowired
	private PeriodoExportacaoService periodoExportacaoService;

	private MenuPage menuPage;
	private ProcessoDeCompraPage processoDeCompraPage;

	private String dataFormatada() {
		String date = null;
		final LocalDate dataLocalMaisDias = LocalDate.now().plusDays(8);
		try {
			date = DateHelper.reformat(dataLocalMaisDias.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
		} catch (final ParseException e) {
			log.error(e.getMessage(), e);
		}
		return date;
	}

    @Before
    public void setUp() throws Exception {
        processoDeCompraPage = new ProcessoDeCompraPage(getWebDriver());
        menuPage = new MenuPage(getWebDriver());

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, "S");
		relacionamentoServicoService.incluirRelacionamentoServico("B3858080808080808080808088887ED6", "B3858080808080808080808088887ED6", "1");

		EConsigInitializer.limparCache();
	}

	//metodos utilizados em todos os steps
	@Quando("entra no menu Acompanhar Portabilidade De margem consignável")
	public void entraNaTelaAcompanharPortabilidadeDeMargemConsignavel() {
		log.info("Quando entra no menu Acompanhar Portabilidade De margem consignável");
		menuPage.acessarMenuOperacional();
	}

	@E("realiza busca na tela de Acompanhar portabilidade de margem consignável {string}")
	public void buscaMargemConsignavel(String matricula) {
		log.info("Quando realiza busca na tela de Acompanhar portabilidade de margem consignável");
		menuPage.acessarItemMenuOperacionalAcompPortMargemConsignavel();
		processoDeCompraPage.pesquisaNaTelaAcompanharPortabilidadeDeMargemConsignavel(matricula);
	}

	//Cenario: Verifica título da coluna na tabela
	@Entao("verifica se existe o titulo vencimento na tabela")
	public void verificaTituloVencimento() {
		log.info("Então verifica se existe o titulo vencimento na tabela");
		assertEquals("Vencimento", processoDeCompraPage.valorDoTituloDoCampoVencimentoDaTabela());
	}

	//Cenario: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Aguardando Informação Saldo Devedor"
	@E("que há um processo de compra com status Aguardando Informação Saldo Devedor")
	public void verificaInfoSaldoDevedor() {
		log.info("E que há um processo de compra com status Aguardando Informação Saldo Devedor");
		parametroSistemaService.configurarParametroServicoCse("B3858080808080808080808088887ED6", CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA, "9");
		autDescontoService.inserirAutDesconto("202123", CodedValues.SAD_AGUARD_CONF, "48178080808080808080808080808C80", "751F8080808080808080808080809D80",  "5400808080808080808080808080C98B", 100.00f, 1052000l, 10, Short.parseShort("1"));
		autDescontoService.inserirAutDesconto("404143", CodedValues.SAD_AGUARD_LIQUI_COMPRA, "48178080808080808080808080808C80", "751F8080808080808080808080809Z85",  "908C5E2864ZZ42E684E9E1CD7E2E961B", 90.00f, 101900l, 10, Short.parseShort("1"));
		relacionamentoAutorizacaoService.definirRelacionamentoAdes(101900l, 1052000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_INF_SALDO, null, null, null, null, DateHelper.getSystemDatetime(), "267", "3700808080808080808080808080A538");

	}

	@Entao("o campo de vencimento será a data de compra mais valor do parâmetro 149 menos um dia")
	public void processoDeCompraParamCentoEQuarentaENove() {
		log.info("o campo de vencimento será a data de compra mais valor do parâmetro 149 menos um dia");
		assertEquals(dataFormatada(), processoDeCompraPage.valorCampoVencimento());
	}

	@E("exclui processo de compra com status Aguardando Informação Saldo Devedor")
	public void excluirProcessoDeCompraStatusAguardandoInformacaoSaldoDevedor() {
		log.info("E exclui processo de compra com status Aguardando Informação Saldo Devedor");
		relacionamentoAutorizacaoService.excluirRelacionamentoAdes(101900l, 1052000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_INF_SALDO, null, null, null, null, DateHelper.getSystemDatetime(), "267", "3700808080808080808080808080A538");
	}

	//Cenario: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Aguardando Pagamento Saldo Devedor"
	@E("que há um processo de compra com status Aguardando Pagamento Saldo Devedor")
	public void verificaPagamentoSaldoDevedor() {
		log.info("E que há um processo de compra com status Aguardando Pagamento Saldo Devedor");
		parametroSistemaService.configurarParametroServicoCse("B3858080808080808080808088887ED6", CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA, "9");
		autDescontoService.inserirAutDesconto("123456", CodedValues.SAD_AGUARD_CONF, "48178080808080808080808080808C80", "751F8080808080808080808080809D80",  "5400808080808080808080808080C98B", 100.00f, 1064000l, 10, Short.parseShort("1"));
		autDescontoService.inserirAutDesconto("654123", CodedValues.SAD_AGUARD_LIQUI_COMPRA, "48178080808080808080808080808C80", "751F8080808080808080808080809Z85",  "908C5E2864ZZ42E684E9E1CD7E2E961B", 90.00f, 201500l, 10, Short.parseShort("1"));
		relacionamentoAutorizacaoService.definirRelacionamentoAdes(201500l, 1064000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_PAG_SALDO, null, null, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}

	@Entao("o campo vencimento será a data de compra mais valor do parametro 150  menos um dia")
	public void processoDeCompraParamCentoECinquenta() {
		log.info("Então para esse processo de compra, o campo vencimento será a data de compra mais valor do parametro 150  menos um dia ");
		assertEquals(dataFormatada(), processoDeCompraPage.valorCampoVencimento());
	}

	@E("exclui processo de compra com status Aguardando Pagamento Saldo Devedor")
	public void excluirProcessoDeCompraStatusAguardandoPagamentoSaldoDevedor() {
		log.info("E exclui processo de compra com status Aguardando Pagamento Saldo Devedor");
		relacionamentoAutorizacaoService.excluirRelacionamentoAdes(201500l, 1064000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_PAG_SALDO, null, null, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}
	//Cenario: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Agu/rdando Liquidação"
	@E("que há um processo de compra com status Aguardando Liquidação")
	public void verificaStatusAguardandoLiquidacao() {
		log.info("E que há um processo de compra com status Aguardando Liquidação");
		parametroSistemaService.configurarParametroServicoCse("B3858080808080808080808088887ED6", CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA, "9");
		autDescontoService.inserirAutDesconto("505153", CodedValues.SAD_AGUARD_CONF, "48178080808080808080808080808C80", "751F8080808080808080808080809D80",  "5400808080808080808080808080C98B", 100.00f, 1048000l, 10, Short.parseShort("1"));
		autDescontoService.inserirAutDesconto("595653", CodedValues.SAD_AGUARD_LIQUI_COMPRA, "48178080808080808080808080808C80", "751F8080808080808080808080809Z85",  "908C5E2864ZZ42E684E9E1CD7E2E961B", 90.00f, 102800l, 10, Short.parseShort("1"));
		relacionamentoAutorizacaoService.definirRelacionamentoAdes(102800l, 1048000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_LIQUIDACAO, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}

	@Entao("o campo Vencimento será a data de compra mais o parametro 151 menos um dia")
	public void processoDeCompraParamCentoECinquentaEUm() {
		log.info("para esse processo de compra, o campo Vencimento será a data de compra mais o parametro 151 menos um dia");
		assertEquals(dataFormatada(), processoDeCompraPage.valorCampoVencimento());
	}

	@E("exclui processo de compra com status Aguardando Liquidação")
	public void excluirProcessoDecompraStatusAguardandoLiquidacao() {
		log.info("E exclui processo de compra com status Aguardando Liquidação");
		relacionamentoAutorizacaoService.excluirRelacionamentoAdes(102800l, 1048000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.AGUARDANDO_LIQUIDACAO, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(), null, DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}

	//Cenario: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra diferente de "Aguardando Informação Saldo Devedor", "Aguardando Pagamento Saldo Devedor" e "Aguardando Liquidação"
	@E("que há um processo de compra com status diferente de Aguardando Informação Saldo Devedor, Aguardando Pagamento Saldo Devedor e Aguardando Liquidação")
	public void verificaStatusDiferente() {
		autDescontoService.inserirAutDesconto("909201", CodedValues.SAD_DEFERIDA, "48178080808080808080808080808C80", "751F8080808080808080808080809D80",  "5400808080808080808080808080C98B", 100.00f, 1098000l, 10, Short.parseShort("1"));
		autDescontoService.inserirAutDesconto("808562", CodedValues.SAD_LIQUIDADA, "48178080808080808080808080808C80", "751F8080808080808080808080809Z85",  "908C5E2864ZZ42E684E9E1CD7E2E961B", 90.00f, 205200l, 10, Short.parseShort("1"));
		relacionamentoAutorizacaoService.definirRelacionamentoAdes(205200l, 1098000l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.FINALIZADO, DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}

	@Entao("no campo Vencimento deve ser exibido um traço")
	public void verificaStatusVencimentoContendoUmTraco() {
		assertEquals("-", processoDeCompraPage.valorCampoVencimento());
	}

	@E("exclui processo de compra com status diferente de Aguardando Informação Saldo Devedor, Aguardando Pagamento Saldo Devedor e Aguardando Liquidação")
	public void excluirProcessoDeCompraDiferente() {
		log.info("E exclui processo de compra com status diferente de Aguardando Informação Saldo Devedor, Aguardando Pagamento Saldo Devedor e Aguardando Liquidação");
		relacionamentoAutorizacaoService.excluirRelacionamentoAdes(1098000l, 205200l, CodedValues.TNT_CONTROLE_COMPRA, StatusCompraEnum.FINALIZADO, DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(),"267", "3700808080808080808080808080A538");
	}

	@After
	public void after() {
		parametroSistemaService.DeletarParametroSistemaCse(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, "S");
		relacionamentoServicoService.excluirRelacionamentoServico("B3858080808080808080808088887ED6", "B3858080808080808080808088887ED6", "1");
		periodoExportacaoService.limpaTabela();

	}
}
