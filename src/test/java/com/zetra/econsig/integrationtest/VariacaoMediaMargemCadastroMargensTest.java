package com.zetra.econsig.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.dao.HistoricoMargemFolhaDao;
import com.zetra.econsig.dao.HistoricoMargemRseDao;
import com.zetra.econsig.dao.MargemDao;
import com.zetra.econsig.dao.MargemRegistroServidorDao;
import com.zetra.econsig.dao.ParamSvcConsignanteDao;
import com.zetra.econsig.dao.PeriodoExportacaoDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.Margem;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.service.MargemService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PeriodoExportacaoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.folha.ImpCadMargemController;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;

public class VariacaoMediaMargemCadastroMargensTest extends ContextSpringConfiguration {

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private ImpCadMargemController impCadMargemController;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private PeriodoExportacaoService periodoExportacaoService;

	@Autowired
	private PeriodoExportacaoDao periodoExportacaoDao;

	@Autowired
	private ParamSvcConsignanteDao paramSvcConsignanteDao;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private MargemService margemService;

	@Autowired
	private MargemDao margemDao;

	@Autowired
	private MargemRegistroServidorDao margemRegistroServidorDao;

	@Autowired
	private HistoricoMargemFolhaDao historicoMargemDao;

	@Autowired
	private HistoricoMargemRseDao historicoMargemRseDao;

	private AcessoSistema usuSup;

	@BeforeEach
	public void beforeClass() {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, "10");

		usuSup = new AcessoSistemaBuilder("AA808080808080808080808080809E80")
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSE)
				.setCodigoEntidade("1").build();

		LocalDate now = LocalDate.now();
		Date pexPeriodo = DateHelper.toPeriodDate(now.toDate());
		String orgCodigo = "751F8080808080808080808080809780";

		periodoExportacaoService.deleteById(orgCodigo, pexPeriodo);
		periodoExportacaoService.insertRegistroPeriodo(DateHelper.format(pexPeriodo, "yyyy-MM-dd"), orgCodigo,
				DateHelper.format(now.minusDays(15).toDate(), "yyyy-MM-dd"), DateHelper.format(now.plusDays(15).toDate(), "yyyy-MM-dd"), Short.valueOf("10"));
	}

	@Test
	public void bloqueio_servidor_margem_maior_percentual_da_media_margem_com_servico_ativo() throws ServidorControllerException {
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("121314", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		//arquivo possui margem 1 com variação acima de 10% em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media.txt", AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("181818");

		assertEquals(CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM, rse.getSrsCodigo());

		registroServidorService.alterarRseMediaMargem("121314", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));
	}

	@Test
	public void nao_bloqueio_servidor_margem_maior_percentual_da_media_margem_com_servico_inativo() throws ServidorControllerException {
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("213243", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		List<ParamSvcConsignante> listParamsIncMargem = paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);

		listParamsIncMargem.stream().filter(param -> param.getPseVlr().equals("1")).map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, false));

		//arquivo possui margem 1 com variação acima de 10% em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_2.txt", AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("213243");

		assertEquals(CodedValues.SRS_ATIVO, rse.getSrsCodigo());

		listParamsIncMargem.stream().filter(param -> param.getPseVlr().equals("1")).map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, true));
	}

	@Test
	public void nao_bloqueio_servidor_margem_maior_param_svc_zerado() throws ServidorControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, "0");
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("579771", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		//arquivo possui margem 1 com variação acima de 10% em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_4.txt",
				AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("579771");

		assertEquals(CodedValues.SRS_ATIVO, rse.getSrsCodigo());
	}

	@Test
	public void nao_bloqueio_servidor_margem_menor_variacao_margem_media() throws ServidorControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, "10");
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("181818", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		//arquivo possui margem 1 com variação acima de 10%, mas com valor menor em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_5.txt",
				AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("181818");

		assertEquals(CodedValues.SRS_ATIVO, rse.getSrsCodigo());
	}

	@Test
	public void bloqueio_servidor_margem_extra_maior_variacao_margem_media() throws ServidorControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, "10");
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("181818", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		Margem mar = margemService.incluirMargem("101", "F");

		margemService.incluirMargemRegistroServidor("101", "83808080808080808080808080804CAB", BigDecimal.valueOf(2000d), BigDecimal.ZERO, BigDecimal.valueOf(2000d));

		paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM).stream().filter(paramSvc -> paramSvc.getPseVlr().equals(CodedValues.INCIDE_MARGEM_SIM.toString()))
		.findFirst()
		.ifPresent(param -> {
			param.setPseVlr("101");
			paramSvcConsignanteDao.save(param);
		});

		//arquivo possui margem 101 com variação acima de 10%  em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_6.txt",
				AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("181818");

		assertEquals(CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM, rse.getSrsCodigo());

		registroServidorService.alterarRseMediaMargem("181818", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM).stream().filter(paramSvc -> paramSvc.getPseVlr().equals("101"))
		.findFirst()
		.ifPresent(param -> {
			param.setPseVlr(CodedValues.INCIDE_MARGEM_SIM.toString());
			paramSvcConsignanteDao.save(param);
		});

		margemRegistroServidorDao.findByMarCodigo(Short.valueOf("101")).forEach(m -> margemRegistroServidorDao.delete(m));
		historicoMargemDao.findByMarCodigo("101").forEach(hist -> historicoMargemDao.delete(hist));
		historicoMargemRseDao.findByMarCodigo("101").forEach(hist -> historicoMargemRseDao.delete(hist));
		margemDao.delete(mar);
	}

	@Test
	public void nao_bloqueio_servidor_margem_menor_percentual_da_media_margem_com_servico_ativo() throws ServidorControllerException {
		ParamSist.getInstance().reset();

		registroServidorService.alterarRseMediaMargem("181818", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		//arquivo possui margem 1 com variação abaixo de 10% em relação à media da margem do servidor de rse_matricula = 181818
		impCadMargemController.importaCadastroMargens("/tmp/eConsigTestes/econsig_arquivos/eConsig/arquivos/margem/cse/margem_acima_media_3.txt",
				AcessoSistema.ENTIDADE_EST, "751F8080808080808080808080809680", false, false, usuSup);

		RegistroServidor rse = registroServidorDao.findByRseMatricula("181818");

		assertEquals(CodedValues.SRS_ATIVO, rse.getSrsCodigo());
	}

	@AfterEach
	public void afterTest() {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, "0");

		registroServidorService.alterarRseMediaMargem("181818", BigDecimal.valueOf(2000.00), Short.valueOf(CodedValues.SRS_ATIVO));

		periodoExportacaoDao.deleteAll();
	}
}
