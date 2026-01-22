package com.zetra.econsig.enomina.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.component.ConfiguradorPermissoesUsuario;
import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.PeriodoExportacaoDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.dao.ServicoDao;
import com.zetra.econsig.dao.ServidorDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSer;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PeriodoExportacaoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.service.VerbaConvenioService;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;

public class TransferirConsignacaoTest extends ENominaContextSpringConfiguration {

	@Autowired
	private PeriodoExportacaoService periodoExportacaoService;

	@Autowired
	private TransferirConsignacaoController transferirConsignacaoController;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ConvenioDao convenioDao;

	@Autowired
	private ConvenioService convenioService;

	@Autowired
	private VerbaConvenioDao verbaConvenioDao;

	@Autowired
	private PeriodoExportacaoDao periodoExportacaoDao;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private ServicoDao servicoDao;

	@Autowired
	private ServidorDao servidorDao;

	@Autowired
    private AutDescontoDao autDescontoDao;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private VerbaConvenioService verbaConvenioService;

	@Autowired
	private ConfiguradorPermissoesUsuario configuradorPermissoesUsuario;


	private AcessoSistema usuSup;

	private VerbaConvenio oldVco;

	private VerbaConvenio newVco;

	@BeforeEach
	public void beforeClass() {
		if (!servidorDao.findById("73A63Z2D980940ADAAB9B6Z349798ED7").isPresent()) {
			Servidor ser = new Servidor();
			ser.setSerCodigo("73A63Z2D980940ADAAB9B6Z349798ED7");
			ser.setSerCpf("035.193.990-32");
			ser.setSerNome("Carlos Eduardo Pereira Silva");
			ser.setSerPermiteAlterarEmail("N");

			servidorDao.save(ser);
		}

		if (registroServidorService.getRegistroServidor("73A63Z2D980940ADAAB9B6Z349798ED7") == null) {
			RegistroServidor rse = new RegistroServidor();
			rse.setRseCodigo("4817808080808099980809090");
			rse.setSerCodigo("73A63Z2D980940ADAAB9B6Z349798ED7");
			rse.setOrgCodigo("1001808080808080808080808080017B");
			rse.setRseMatricula("546576");
			rse.setRseMargem(BigDecimal.valueOf(1000.00d));
			rse.setRseMargemRest(BigDecimal.valueOf(1000.00d));
			rse.setRseMargemUsada(null);
			rse.setRsePrazo(48);
			rse.setRseAgenciaSal("1111");
			rse.setRseBancoSal("001");
			rse.setRseContaSal("111111");
			rse.setUsuCodigo("AA808080808080808080808080809E80");
			rse.setSrsCodigo("1");
			rse.setRseAuditoriaTotal("S");

			registroServidorDao.save(rse);
		}

		usuSup = new AcessoSistemaBuilder("AA808080808080808080808080809E80")
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSE)
				.setCodigoEntidade("1").build();

		LocalDate now = LocalDate.now();

		periodoExportacaoService.limpaTabela();
		periodoExportacaoService.insertRegistroPeriodo(DateHelper.format(DateHelper.toPeriodDate(now.toDate()), "yyyy-MM-dd"), "751F8080808080808080808080809780",
				DateHelper.format(now.minusDays(15).toDate(), "yyyy-MM-dd"), DateHelper.format(now.plusDays(15).toDate(), "yyyy-MM-dd"), Short.valueOf("10"));


		if (oldVco == null) {
			oldVco = new VerbaConvenio();
			oldVco.setVcoCodigo("AA808081828F80808080808080809E80");
			oldVco.setCnvCodigo("05108080808080808080808080808780");
			oldVco.setVcoAtivo(Short.valueOf("1"));
			oldVco.setVcoDataIni(now.minusYears(1).toDate());
			oldVco.setVcoDataFim(now.plusYears(1).toDate());
			oldVco.setVcoVlrVerba(BigDecimal.valueOf(9999999.99d));
			oldVco.setVcoVlrVerbaRest(BigDecimal.valueOf(9999999.99d));

			verbaConvenioDao.save(oldVco);
		}

		if (newVco == null) {
			newVco = new VerbaConvenio();
			newVco.setVcoCodigo("AA808081828F80808080808080808F81");
			newVco.setCnvCodigo("0E0B8080808080808080808080809780");
			newVco.setVcoAtivo(Short.valueOf("1"));
			newVco.setVcoDataIni(now.minusYears(1).toDate());
			newVco.setVcoDataFim(now.plusYears(1).toDate());
			newVco.setVcoVlrVerba(BigDecimal.valueOf(9999999.99d));
			newVco.setVcoVlrVerbaRest(BigDecimal.valueOf(9999999.99d));

			verbaConvenioDao.save(newVco);
		}
	}

	@Test
	public void transferir_ade_entre_csas_servidor_altera_margem_incidente() throws AutorizacaoControllerException {
		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("357854477", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 82634, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, null, "546576", null, null, true, false, usuSup);

			adeNovo = autDescontoService.getAde("82634");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeNovo.getVcoCodigo());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM_3, adeNovo.getAdeIncMargem());
		} finally {
			deleteAde("357854477");
		}
	}

	@Test
	public void transferir_ade_entre_csas_servidor_nao_altera_margem_incidente() throws AutorizacaoControllerException {
		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("257854477", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 22343445, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, null, "546576", null, null, false, false, usuSup);

			adeNovo = autDescontoService.getAde("22343445");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeNovo.getVcoCodigo());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM, adeNovo.getAdeIncMargem());
		} finally {
			deleteAde("257854477");
		}
	}

	@Test
	public void nao_transfere_por_exceder_limite_contrato_por_csa_em_transferencia() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, "1");

		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("684848484", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 41848484, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("6184484844444", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 7787777, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(41848484l), "546576", null, null, false, false, usuSup);

			adeNovo = autDescontoService.getAde("41848484");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeNovo.getVcoCodigo());
			assertEquals(CodedValues.SAD_SUSPENSA_CSE, adeNovo.getSadCodigo());
		}finally {
			deleteAde("684848484");
			deleteAde("6184484844444");
		}
	}

	@Test
	public void transfere_sem_exceder_limite_contrato_por_csa_em_transferencia() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, "2");

		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("684848484", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 41848484, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("6184484844444", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 7787777, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(41848484l), "546576", null, null, true, false, usuSup);

			adeNovo = autDescontoService.getAde("41848484");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeNovo.getVcoCodigo());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM_3, adeNovo.getAdeIncMargem());
		} finally {
			deleteAde("684848484");
			deleteAde("6184484844444");
		}
	}

	@Test
	public void transfere_dentro_limite_grupo_servico() throws AutorizacaoControllerException {

		servicoService.setNovoGrupoSvcAServico("010B8080808080808080808080809480", "010B8080808080808080808080809999", "GRUPO_DESPESA", "587", 2, 2);

		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("357854477", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 636747, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, null, "546576", null, null, true, false, usuSup);

			adeNovo = autDescontoService.getAde("636747");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeNovo.getVcoCodigo());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM_3, adeNovo.getAdeIncMargem());

		} finally {

			servicoDao.findById("010B8080808080808080808080809480").ifPresent(svc -> {
				svc.setTgsCodigo(null);
				servicoDao.save(svc);
			});

			servicoService.deleteTipoGrupoSvc("010B8080808080808080808080809999");

			deleteAde("357854477");
		}
	}

	@Test
	public void nao_transfere_acima_limite_grupo_servico() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();


		servicoService.setNovoGrupoSvcAServico("010B8080808080808080808080809480", "010B8080808080808080808080809999", "GRUPO_DESPESA", "587", 1, 1);

		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		// contrato no cnv destino
		autDescontoService.inserirAutDesconto("78145444", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 5792444, 10, Short.valueOf("1"));

		// contrato no cnv de origem
		autDescontoService.inserirAutDesconto("24884547774", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 949449449, 10, Short.valueOf("1"));

		AutDesconto adeOrigem2 = autDescontoService.inserirAutDesconto("864777777", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 7787777, 10, Short.valueOf("1"));

		AutDesconto adeNovo = null;
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, null, "546576", null, null, true, false, usuSup);

			adeNovo = autDescontoService.getAde("949449449");
			VerbaConvenio verbaConvenioNova = verbaConvenioDao.findByCnvCodigo("0E0B8080808080808080808080809780");

			adeOrigem2 = autDescontoService.getAde("7787777");

			assertEquals(verbaConvenioNova.getVcoCodigo(), adeOrigem2.getVcoCodigo());
			assertEquals(CodedValues.SAD_SUSPENSA_CSE, adeOrigem2.getSadCodigo());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM_3, adeNovo.getAdeIncMargem());
			assertEquals(CodedValues.INCIDE_MARGEM_SIM_3, adeOrigem2.getAdeIncMargem());
		} finally {
			servicoDao.findById("010B8080808080808080808080809480").ifPresent(svc -> {
				svc.setTgsCodigo(null);
				servicoDao.save(svc);
			});
			servicoService.deleteTipoGrupoSvc("010B8080808080808080808080809999");

			deleteAde("949449449");
			deleteAde("7787777");
			deleteAde("78145444");
		}


	}

	@Test
	public void nao_transfere_param_limite_por_rse_cnv_configurado_com_zero_responsavel_sup_entao_suspende_ade() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroServicoCse("010B8080808080808080808080809480", CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, "0");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("684848484", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 9785444, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("88484848", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 57744, 10, Short.valueOf("1"));
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(57744l), "546576", null, null, true, false, usuSup);

			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,autDescontoService.getAdeByAdeCodigo("88484848").getSadCodigo());

		} finally {
			deleteAde("684848484");
			deleteAde("88484848");
		}
	}

	@Test
	public void tenta_transferir_ade_rse_que_ja_tem_limite_ades_cnv_responsavel_sup_entao_suspende_ade() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroServicoCse("010B8080808080808080808080809480", CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, "3");
		parametroSistemaService.configurarParametroCnvRegistroSer("4817808080808099980809090", "0E0B8080808080808080808080809780", CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "1");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("8484888", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 8971187, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("114568547", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 864798, 10, Short.valueOf("1"));
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(864798l), "546576", null, null, true, false, usuSup);


			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("114568547");
			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});

		} finally {
			deleteAde("8484888");
			deleteAde("114568547");
		}
	}

	@Test
	public void tenta_transferir_ade_rse_que_ja_tem_limite_ades_cnv() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroServicoCse("010B8080808080808080808080809480", CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, "3");
		parametroSistemaService.configurarParametroCnvRegistroSer("4817808080808099980809090", "0E0B8080808080808080808080809780", CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO, "1");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("494848488", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 975144, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("366623333", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 974441, 10, Short.valueOf("1"));
		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(974441l), "546576", null, null, true, false, criaAcessoSistema(usuCsa2.getUsuCodigo(), "267", AcessoSistema.ENTIDADE_CSA));

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("366623333");

			assertEquals(CodedValues.SAD_DEFERIDA ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});
		} finally {
			deleteAde("494848488");
			deleteAde("366623333");
		}
	}

	@Test
	public void param_svc_ade_por_svc_por_rse_zero() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroSvcRegistroSer("4817808080808099980809090" ,"010B8080808080808080808080809480", CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "0");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("3678514", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 57914, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(57914l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("3678514");
			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});

		}finally {
			deleteAde("3678514");
		}

	}

	@Test
	public void param_svc_ade_por_svc_por_rse_atingiu_max_ade() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroSvcRegistroSer("4817808080808099980809090" ,"010B8080808080808080808080809480", CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO, "1");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("91478147", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 36799, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("36662333", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 98885, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(36799l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("91478147");
			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});

		}finally {
			deleteAde("91478147");
			deleteAde("36662333");
		}

	}

	@Test
	public void param_svc_ade_por_nse_rse_atingiu_max_qntd() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroNseRegistroSer("4817808080808099980809090", CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "1");

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("9811774", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 97237, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("973647", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 92456, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(97237l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("9811774");
			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});

		}finally {
			deleteAde("9811774");
			deleteAde("973647");
		}
	}

	@Test
	public void param_svc_ade_por_nse_rse_nao_atingiu_max_qntd() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamSist.getInstance().reset();
		parametroSistemaService.configurarParametroNseRegistroSer("4817808080808099980809090", CodedValues.NSE_MENSALIDADE, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO, "2");

		Convenio cnv = convenioService.createConvenio("1518851444", "47008080808080808080808080809A77", "1001808080808080808080808080017B", "267", "TEST_TRANSFER");

		VerbaConvenio vcoOrigem = verbaConvenioService.createVerbaConvenio("84693244", "1518851444", BigDecimal.valueOf(33434l), BigDecimal.valueOf(5851848l));
		VerbaConvenio vcoDestino = verbaConvenioService.createVerbaConvenio("87935874", "74168080808080808080808080808A80", BigDecimal.valueOf(778848l), BigDecimal.valueOf(697454l));

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("557984", "4",
				"4817808080808099980809090", "1518851444",
				usuCsa2.getUsuCodigo(), 100.0f, 55931, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("55328", "4",
				"4817808080808099980809090", "74168080808080808080808080808A80",
				usuCsa2.getUsuCodigo(), 100.0f, 99147, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "47008080808080808080808080809A77", "47008080808080808080808080809A77", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(55931l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("557984");
			assertEquals(CodedValues.SAD_DEFERIDA ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(convenio -> {
					assertEquals("3700808080808080808080808080A538", convenio.getCsaCodigo());
				});
			});

		}finally {
			deleteAde("557984");
			deleteAde("55328");

			verbaConvenioService.deleteVco(vcoDestino);
			verbaConvenioService.deleteVco(vcoOrigem);

			convenioDao.delete(cnv);
		}
	}

	@Test
	public void ade_transferida_estado_suspenso_pois_atingiu_max_contratos_por_csa() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamConsignatariaRegistroSer paramCsa = parametroSistemaService.configurarParamCsaRse("4817808080808099980809090", "267", CodedValues.TPA_QTD_CONTRATOS_POR_CSA, "1");
		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("16749887", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 74647, 10, Short.valueOf("1"));

		autDescontoService.inserirAutDesconto("387587", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 597532, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("3700808080808080808080808080A538", "267", "010B8080808080808080808080809480", "050E8080808080808080808080808280", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(597532l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("387587");
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("267", cnv.getCsaCodigo());
				});
			});
			assertEquals(CodedValues.SAD_SUSPENSA_CSE ,adePos.getSadCodigo());

		}finally {
			deleteAde("16749887");
			deleteAde("387587");

			parametroSistemaService.deletarParamConsignatariaRse(paramCsa);
		}
	}

	@Test
	public void pode_transferir_nao_atingiu_max_contratos_por_csa_rse() throws AutorizacaoControllerException {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "S");
		ParamConsignatariaRegistroSer paramCsa = parametroSistemaService.configurarParamCsaRse("4817808080808099980809090", "3700808080808080808080808080A538", CodedValues.TPA_QTD_CONTRATOS_POR_CSA, "2");
		ParamSist.getInstance().reset();

		Usuario usuCsa2 = usuarioService.getUsuario("csa2");

		autDescontoService.inserirAutDesconto("4794745", "4",
				"4817808080808099980809090", "05108080808080808080808080808780",
				usuCsa2.getUsuCodigo(), 100.0f, 324774, 10, Short.valueOf("1"));

	    autDescontoService.inserirAutDesconto("442574", "4",
				"4817808080808099980809090", "0E0B8080808080808080808080809780",
				usuCsa2.getUsuCodigo(), 100.0f, 842874, 10, Short.valueOf("1"));

		try {
			transferirConsignacaoController.transfereAde("267", "3700808080808080808080808080A538", "050E8080808080808080808080808280", "010B8080808080808080808080809480", "1001808080808080808080808080017B"
					, List.of(CodedValues.SAD_DEFERIDA), null, null, List.of(324774l), "546576", null, null, true, false, usuSup);

			AutDesconto adePos = autDescontoService.getAdeByAdeCodigo("4794745");
			assertEquals(CodedValues.SAD_DEFERIDA ,adePos.getSadCodigo());
			verbaConvenioDao.findById(adePos.getVcoCodigo()).ifPresent(vco -> {
				convenioDao.findById(vco.getCnvCodigo()).ifPresent(cnv -> {
					assertEquals("3700808080808080808080808080A538", cnv.getCsaCodigo());
				});
			});

		}finally {
			deleteAde("4794745");
			deleteAde("442574");

			parametroSistemaService.deletarParamConsignatariaRse(paramCsa);
		}
	}

	private AcessoSistema criaAcessoSistema(String usuCodigo, String entCodigo, String tipoEntidade) throws UsuarioControllerException {
		AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigo)
				.setTipoEntidade(tipoEntidade)
				.setCodigoEntidade(entCodigo).build();

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigo, AcessoSistema.ENTIDADE_CSA, entCodigo);

		return responsavel;
	}

	private void deleteAde(String adeCodigo) {
        AutDesconto ade = autDescontoDao.findByAdeNumero(Long.valueOf(adeCodigo));
        ade = ade != null ? ade : autDescontoDao.findByAdeCodigo(adeCodigo);
        if (ade != null) {
            autDescontoService.deleteAutDesconto(ade.getAdeCodigo());
        }
	}

	@AfterEach
	public void afterTests() {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, "N");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, "");
		parametroSistemaService.deleteParametroSvcRegistroSer("4817808080808099980809090" ,"010B8080808080808080808080809480", CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO);
		parametroSistemaService.deleteParametroSvcNseRse("4817808080808099980809090", CodedValues.NSE_EMPRESTIMO, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);
		parametroSistemaService.deleteParametroNseRse("4817808080808099980809090", CodedValues.NSE_MENSALIDADE, CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);

		ParamSist.getInstance().reset();

		verbaConvenioDao.delete(newVco);
		verbaConvenioDao.delete(oldVco);
		newVco = null;
		oldVco = null;

		periodoExportacaoDao.deleteAll();
	}

}
