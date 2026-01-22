package com.zetra.econsig.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.component.ConfiguradorPermissoesUsuario;
import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.UsuarioDao;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.StatusLogin;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.OcorrenciaAutorizacaoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PeriodoExportacaoService;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProcessaLoteComTodasConsignacoesTest extends ContextSpringConfiguration {

	@Autowired
	private com.zetra.econsig.service.UsuarioServiceTest usuService;

	@Autowired
	private AutDescontoDao autDescontoDao;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private FuncaoService funcaoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private ConfiguradorPermissoesUsuario configuradorPermissoesUsuario;

	@Autowired
	private PeriodoExportacaoService periodoExportacaoService;

	@Autowired
	private OcorrenciaAutorizacaoService ocorrenciaAutorizacaoService;

	@Autowired
	private UsuarioDao usuarioDao;

    @BeforeEach
    public void before() {
        // Altera todos os serviços para permitir importação via lote
        parametroSistemaService.configurarParametroServicoCse(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE, CodedValues.PSE_BOOLEANO_SIM);
        // Altera todos os serviços para permitir aumento do valor da consignação
        parametroSistemaService.configurarParametroServicoCse(CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO, CodedValues.PSE_BOOLEANO_SIM);
    }

	@Test
	public void test001_importacao_parcial_ades_sem_param_todas_ade_no_lote() throws ViewHelperException, UsuarioControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS, CodedValues.TPC_NAO);
		periodoExportacaoService.limpaTabela();
		criarDadosPeriodoExportacao();
		ParamSist.getInstance().reset();

        String usuCse = getUsuCodigoCse();
		List<AutDesconto> adesAtivosAntes = getTodasAdesAtivas();
		AcessoSistema responsavel = new AcessoSistemaBuilder().build();

		prepararUsuarioResponsavel(usuCse, responsavel);

		LoteHelper loteHelper = new LoteHelper(null, null, false, false, true, false, false, null, responsavel);
		loteHelper.importarLote("lote_default_entrada.xml", "lote_default_tradutor.xml", "lote_com_apenas_um_ade.txt");

		assertEquals(0, getOcasLoteTodaCarteira().size());

		restauraAdeStatus(adesAtivosAntes);
		restauraAdeVlrs(adesAtivosAntes);
		deletarFuncoesLote(usuCse);
		deleteOcasLoteTodaCarteira();
		periodoExportacaoService.limpaTabela();
	}

	@Test
	public void test002_importacao_total_ades_sem_duas_ades_ativas_no_lote() throws UsuarioControllerException, ViewHelperException {
        AcessoSistema responsavel = new AcessoSistemaBuilder().build();

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS, CodedValues.TPC_SIM);
		ParamSist.getInstance().reset();
		periodoExportacaoService.limpaTabela();
		criarDadosPeriodoExportacao();

        final String raizArquivos = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIR_RAIZ_ARQUIVOS, responsavel);
        final String nomeArqLote = "lote_com_todos_ades_ativos.txt";
        autDescontoService.gerarLoteTodasAdesAtivas(raizArquivos + "/lote/cse/" + nomeArqLote);

        // Cria duas consignações que não estarão no lote para serem excluídas
        int adeForaLoteAguardConf = 0;
        int adeForaLoteEmAndamento = 0;

        try {
            adeForaLoteAguardConf = NumberHelper.getRandomNumber(100000, 1000000, 0);
            adeForaLoteEmAndamento = NumberHelper.getRandomNumber(100000, 1000000, 0);
            assertNotEquals(adeForaLoteAguardConf, adeForaLoteEmAndamento);

            autDescontoService.inserirAutDesconto(String.valueOf(adeForaLoteAguardConf), CodedValues.SAD_AGUARD_CONF, "E04D33A02740475ZA227AB277021537D", "751F8080808080808080808080809D80", "5400808080808080808080808080C98B", 95.95f, Long.valueOf(adeForaLoteAguardConf), 12, CodedValues.INCIDE_MARGEM_SIM);
            autDescontoService.inserirAutDesconto(String.valueOf(adeForaLoteEmAndamento), CodedValues.SAD_EMANDAMENTO, "E04D33A02740475ZA227AB277021537D", "751F8080808080808080808080809D80", "5400808080808080808080808080C98B", 96.96f, Long.valueOf(adeForaLoteEmAndamento), 24, CodedValues.INCIDE_MARGEM_SIM);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            assertEquals(1, 0);
        }

		String usuCse = getUsuCodigoCse();
		List<AutDesconto> adesAtivosAntes = getTodasAdesAtivas();

		prepararUsuarioResponsavel(usuCse, responsavel);

		LoteHelper loteHelper = new LoteHelper(null, null, false, false, true, false, false, null, responsavel);
		loteHelper.importarLote("lote_default_entrada.xml", "lote_default_tradutor.xml", nomeArqLote);

		List<AutDesconto> adesAtivosDepois = getTodasAdesAtivas();

		// nova implementação DESENV-20546: importação de lote com todos ativos, contratos aguardando deferimentos devem ser deferidas no processamento do lote
		AutDesconto adeDeferida = autDescontoService.getAde("2");
		assertEquals(CodedValues.SAD_DEFERIDA, adeDeferida.getSadCodigo());

		// ADEs fora do lote devem estar excluídas
		AutDesconto adeLiquidada = autDescontoService.getAde(String.valueOf(adeForaLoteEmAndamento));
		assertEquals(CodedValues.SAD_LIQUIDADA, adeLiquidada.getSadCodigo());
		AutDesconto adeCancelada = autDescontoService.getAde(String.valueOf(adeForaLoteAguardConf));
		assertEquals(CodedValues.SAD_CANCELADA, adeCancelada.getSadCodigo());

		assertEquals(adesAtivosAntes.size() - 2, adesAtivosDepois.size());
		assertEquals(adesAtivosDepois.size(), getOcasLoteTodaCarteira().size());

		restauraAdeStatus(adesAtivosAntes);
		restauraAdeVlrs(adesAtivosAntes);
		deletarFuncoesLote(usuCse);
		deleteOcasLoteTodaCarteira();
	}

	@AfterEach
	public void after() {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS, CodedValues.TPC_NAO);
		ParamSist.getInstance().reset();
	}

	private List<AutDesconto> getTodasAdesAtivas() {
	    // Não inclui os suspensos, que não são alterados pelo lote nem cancelados/liquidados ao final
	    // Não incluir também aguard. liquidação e aguard. liquidação compra, pois o relacionamento pode não deixar cancelar/liquidar
	    List<String> sadCodigosPesquisa = new ArrayList<>();
	    sadCodigosPesquisa.add(CodedValues.SAD_SOLICITADO);
	    sadCodigosPesquisa.add(CodedValues.SAD_AGUARD_CONF);
	    sadCodigosPesquisa.add(CodedValues.SAD_AGUARD_DEFER);
	    sadCodigosPesquisa.add(CodedValues.SAD_DEFERIDA);
	    sadCodigosPesquisa.add(CodedValues.SAD_EMANDAMENTO);
	    sadCodigosPesquisa.add(CodedValues.SAD_ESTOQUE);
	    sadCodigosPesquisa.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
	    sadCodigosPesquisa.add(CodedValues.SAD_EMCARENCIA);
	    sadCodigosPesquisa.add(CodedValues.SAD_ESTOQUE_MENSAL);
	    sadCodigosPesquisa.add(CodedValues.SAD_AGUARD_MARGEM);

        return autDescontoDao.findBySadCodigo(sadCodigosPesquisa);
	}

	private void prepararUsuarioResponsavel(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
		criarFuncoesLote(usuCodigo, "1");

		responsavel.setUsuCodigo(usuCodigo);
		responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
		responsavel.setCodigoEntidade("1");
		responsavel.setIpUsuario("127.0.0.1");

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigo, AcessoSistema.ENTIDADE_CSE, "1");
	}

	private void criarDadosPeriodoExportacao() {
		LocalDate now = LocalDate.now();
		periodoExportacaoService.insertRegistroPeriodo(DateHelper.format(DateHelper.toPeriodDate(now.toDate()), "yyyy-MM-dd"), "751F8080808080808080808080809780",
				DateHelper.format(now.minusDays(15).toDate(), "yyyy-MM-dd"), DateHelper.format(now.plusDays(15).toDate(), "yyyy-MM-dd"), Short.valueOf("10"));
	}

	private String getUsuCodigoCse() {
	    Usuario usuCse = usuService.getUsuario("cse");

	    StatusLogin sts = new StatusLogin();
	    sts.setStuCodigo(CodedValues.STU_ATIVO);
	    usuCse.setStatusLogin(sts);
	    usuarioDao.save(usuCse);

	    return usuCse.getUsuCodigo();
	}

	private void criarFuncoesLote(String usuCodigo, String usuEntidadeCodigo) {
		funcaoService.criarFuncaoPerfilCse(usuCodigo, CodedValues.FUN_INCLUSAO_VIA_LOTE, usuEntidadeCodigo);
		funcaoService.criarFuncaoPerfilCse(usuCodigo, CodedValues.FUN_EXCLUSAO_VIA_LOTE, usuEntidadeCodigo);
		funcaoService.criarFuncaoPerfilCse(usuCodigo, CodedValues.FUN_ALTERACAO_VIA_LOTE, usuEntidadeCodigo);
		funcaoService.criarFuncaoPerfilCse(usuCodigo, CodedValues.FUN_CONFIRMACAO_VIA_LOTE, usuEntidadeCodigo);
		funcaoService.criarFuncaoPerfilCse(usuCodigo, CodedValues.FUN_IMPORTACAO_VIA_LOTE, usuEntidadeCodigo);
	}

	private void deletarFuncoesLote(String usuCodigo) {
		funcaoService.deletarFuncaoPerfilCse(CodedValues.FUN_INCLUSAO_VIA_LOTE, usuCodigo);
		funcaoService.deletarFuncaoPerfilCse(CodedValues.FUN_EXCLUSAO_VIA_LOTE, usuCodigo);
		funcaoService.deletarFuncaoPerfilCse(CodedValues.FUN_ALTERACAO_VIA_LOTE, usuCodigo);
		funcaoService.deletarFuncaoPerfilCse(CodedValues.FUN_CONFIRMACAO_VIA_LOTE, usuCodigo);
		funcaoService.deletarFuncaoPerfilCse(CodedValues.FUN_IMPORTACAO_VIA_LOTE, usuCodigo);
	}

	private List<OcorrenciaAutorizacao> getOcasLoteTodaCarteira() {
		return ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_ALTERACAO_VIA_LOTE_COM_TODOS_ADES);
	}

	private void deleteOcasLoteTodaCarteira() {
		List<OcorrenciaAutorizacao> ocas = ocorrenciaAutorizacaoService.getOcorrenciaAutorizacao(CodedValues.TOC_ALTERACAO_VIA_LOTE_COM_TODOS_ADES);

		if (ocas != null) {
			ocas.forEach(oca -> ocorrenciaAutorizacaoService.deleteOcorrencia(oca));
		}
	}

    private void restauraAdeVlrs(List<AutDesconto> adesAtivosAntes) {
        List<AutDesconto> ades = getTodasAdesAtivas();
        Map<String, BigDecimal> mapValorAntes = new HashMap<>();

        for (AutDesconto adeAntes: adesAtivosAntes) {
        	mapValorAntes.put(adeAntes.getAdeCodigo(), adeAntes.getAdeVlr());
        }

        ades.forEach(ade -> {
        	BigDecimal adeVlrAntes = mapValorAntes.get(ade.getAdeCodigo());
        	if (adeVlrAntes.compareTo(ade.getAdeVlr()) != 0) {
        		ade.setAdeVlr(ade.getAdeVlr().add(BigDecimal.valueOf(0.5)));
        		autDescontoDao.save(ade);
        	}
        });
    }

    private void restauraAdeStatus(List<AutDesconto> ades) {
        ades.forEach(ade -> {
            autDescontoDao.save(ade);
        });
    }

}
