package com.zetra.econsig.enomina.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.zetra.econsig.component.ConfiguradorPermissoesUsuario;
import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.PeriodoExportacaoDao;
import com.zetra.econsig.dao.PrazoConsignatariaDao;
import com.zetra.econsig.dao.PrazoDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.enomina.ENominaInitializer;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ParamSistConsignante;
import com.zetra.econsig.persistence.entity.PeriodoExportacao;
import com.zetra.econsig.persistence.entity.Prazo;
import com.zetra.econsig.persistence.entity.PrazoConsignataria;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.AgendamentoService;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ConsignanteService;
import com.zetra.econsig.service.ConvenioService;
import com.zetra.econsig.service.FuncaoSistemaService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.PeriodoExportacaoService;
import com.zetra.econsig.service.PrazoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.RelacionamentoServicoService;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoControllerBean;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TpsExigeConfirmacaoRenegociacaoValoresEnum;

public class PortabilidadeEnominaTest extends ENominaContextSpringConfiguration {

	@Autowired
	private com.zetra.econsig.service.UsuarioServiceTest UsuarioService;

	@Autowired
	@Qualifier("renegociarConsignacaoController")
	private RenegociarConsignacaoControllerBean renegociarConsignacaoControllerBean;

	@Autowired
	private RelacionamentoServicoService relacionamentoServicoService;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private AutDescontoDao autDescontoDao;

	@Autowired
	private ConvenioService convenioService;

	@Autowired
	private ConfiguradorPermissoesUsuario configuradorPermissoesUsuario;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private AgendamentoService agendamentoService;

	@Autowired
	private ConsignanteService consignanteService;

	@Autowired
	private FuncaoSistemaService funcaoSistemaService;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private RegistroServidorService registroServidorService;

	@Autowired
	private PeriodoExportacaoService periodoExportacaoService;

	@Autowired
	private PeriodoExportacaoDao periodoExportacaoDao;

	@Autowired
	private PrazoService prazoService;

	@Autowired
	private PrazoDao prazoDao;

	@Autowired
	private PrazoConsignatariaDao prazoConsignatariaDao;

	private boolean periodicidadeMensalOriginal;
	private boolean configurado;

	private static final String SVC_EMPRESTIMO = "B3858080808080808080808088887ED6";
	private static final String CSA_ORIGEM = "3700808080808080808080808080A538";
	private static final String CSA_DESTINO = "267";

	@BeforeEach
	public void before() {
        agendamentoService.desabilitarTodosAgendamentos();
        consignanteService.alterarStatusConsignante("1");

        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_INCLUSAO_CONSOME_SENHA_AUT_DESC, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, "N");
        parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA, "0");
        parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG, "0");

        parametroSistemaService.alterarParametroConsignataria("csa", CodedValues.TPA_INF_BANCARIA_OBRIGATORIA_HOST_A_HOST, "N");

        final ParamSistConsignante periodicidade = parametroSistemaService.getParamSistemaConsignante(CodedValues.TPC_PERIODICIDADE_FOLHA);

        periodicidadeMensalOriginal = (periodicidade != null) && CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(periodicidade.getPsiVlr());
        if (periodicidadeMensalOriginal) {
            parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERIODICIDADE_FOLHA, CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);
        }

        if (!configurado) {
            final Short przVlr10 = Short.valueOf("10");
            final Short przVlr20 = Short.valueOf("20");

            final Prazo prazo10 = prazoDao.findBySvcCodigoAndPrzVlr(SVC_EMPRESTIMO, przVlr10);
            final PrazoConsignataria przCsaOrigem10 = prazoService.inserirPrzCsa("84318", CSA_ORIGEM, prazo10.getPrzCodigo(), CodedValues.STS_ATIVO);
            final PrazoConsignataria przCsaDestino10 = prazoConsignatariaDao.getPrzCsaCodigoBySvcCodigoAndCsaCodigo(SVC_EMPRESTIMO, CSA_DESTINO, przVlr10);
            prazoService.inserirCft("9C168080808080808080808080803F80", przCsaOrigem10.getPrzCsaCodigo(), Short.parseShort("1"), BigDecimal.valueOf(1.23d), LocalDate.now().minusDays(2).toDate(), null, LocalDate.now().minusDays(3).toDate());
            prazoService.inserirCft("8F168080808080808080808080803F80", przCsaDestino10.getPrzCsaCodigo(), Short.parseShort("1"), BigDecimal.valueOf(1.23d), LocalDate.now().minusDays(2).toDate(), null, LocalDate.now().minusDays(3).toDate());

            final Prazo prazo20 = prazoService.incluirPrazo("B38580808080808080808080888853DE", SVC_EMPRESTIMO, przVlr20, CodedValues.STS_ATIVO);
            final PrazoConsignataria przCsaOrigem = prazoService.inserirPrzCsa("84314", CSA_ORIGEM, prazo20.getPrzCodigo(), CodedValues.STS_ATIVO);
            final PrazoConsignataria przCsaDestino = prazoService.inserirPrzCsa("84315", CSA_DESTINO, prazo20.getPrzCodigo(), CodedValues.STS_ATIVO);
            prazoService.inserirCft("8C168080808080808080808080803F80", przCsaOrigem.getPrzCsaCodigo(), Short.parseShort("1"), BigDecimal.valueOf(1.23d), LocalDate.now().minusDays(2).toDate(), null, LocalDate.now().minusDays(3).toDate());
            prazoService.inserirCft("9F168080808080808080808080803F80", przCsaDestino.getPrzCsaCodigo(), Short.parseShort("1"), BigDecimal.valueOf(1.23d), LocalDate.now().minusDays(2).toDate(), null, LocalDate.now().minusDays(3).toDate());
            configurado = true;
        }

        ENominaInitializer.limparCache();
	}

	@Test
	public void portabilidade_sucesso_novo_ade_aguardando_confirmacao() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

		JspHelper.limparCacheParametros();

		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

        final AutDesconto ade = autDescontoService.inserirAutDesconto("3454455466", "4",
				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
				usuCsa2.getUsuCodigo(), 100.0f, 12343445, 10, Short.parseShort("1"));

		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
				, "1");

		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

		funcaoSistemaService.excluirFuncaoCsa(CodedValues.FUN_DEF_CONSIGNACAO, CSA_ORIGEM, usuCodigoEntidadeCompradora);

		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
				.setCodigoEntidade(CSA_ORIGEM).build();

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
				"751F8080808080808080808080809780", CSA_ORIGEM);

		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo("48178080808080808080808080808C80");
        renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
        renegociarParam.setAdePrazo(20);
        renegociarParam.setAdeCarencia(ade.getAdeCarencia());
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setComSerSenha(false);
        renegociarParam.setAdeIndice(ade.getAdeIndice());
        renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
        renegociarParam.setAdeCodigosRenegociacao(List.of(ade.getAdeCodigo()));
        renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
        renegociarParam.setCompraContrato(true);
        renegociarParam.setAdeIdentificador("");

		final String adeCodigoNovo = renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("12343445").getSadCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAdeByAdeCodigo(adeCodigoNovo).getSadCodigo());
	}

	@Test
	public void portabilidade_sucesso_novo_usuario_pode_deferir() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.NENHUMA.getCodigo());

		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");

        JspHelper.limparCacheParametros();

		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

		final AutDesconto ade = autDescontoService.inserirAutDesconto("3454455455", "4",
				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
				usuCsa2.getUsuCodigo(), 100.0f, 871777, 10, Short.parseShort("1"));

		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
				, "1");

		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
				.setCodigoEntidade(CSA_ORIGEM).build();

		funcaoSistemaService.incluirFuncaoCsa(CodedValues.FUN_DEF_CONSIGNACAO, CSA_ORIGEM, usuCodigoEntidadeCompradora);

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
				"751F8080808080808080808080809780", CSA_ORIGEM);

		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo("48178080808080808080808080808C80");
        renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
        renegociarParam.setAdePrazo(20);
        renegociarParam.setAdeCarencia(ade.getAdeCarencia());
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setComSerSenha(false);
        renegociarParam.setAdeIndice(ade.getAdeIndice());
        renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
        renegociarParam.setAdeCodigosRenegociacao(List.of(ade.getAdeCodigo()));
        renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
        renegociarParam.setCompraContrato(true);
        renegociarParam.setAdeIdentificador("");

		final String adeCodigoNovo = renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

		// mesmo com usuario comprador com permissão para deferir, a compra ainda ficará aguardando liquidação de compra.
		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("871777").getSadCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAdeByAdeCodigo(adeCodigoNovo).getSadCodigo());
	}

	//teste setar prazo da compra para até 2x o práximo máximo setado para o serviço em sistemas quinzenais.
	@Test
	public void portabilidade_sistema_quinzenal_praxo_maximo_pode_ser_ate_duas_vezes_prazo_max_servico() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE, "10");

		JspHelper.limparCacheParametros();

		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

		final AutDesconto ade = autDescontoService.inserirAutDesconto("3453433554455455", "4",
				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
				usuCsa2.getUsuCodigo(), 100.0f, 824788, 20, Short.parseShort("1"));

		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
				, "1");

		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
				.setCodigoEntidade(CSA_ORIGEM).build();

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
				"751F8080808080808080808080809780", CSA_ORIGEM);

		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo("48178080808080808080808080808C80");
        renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
        renegociarParam.setAdePrazo(20);
        renegociarParam.setAdeCarencia(ade.getAdeCarencia());
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setComSerSenha(false);
        renegociarParam.setAdeIndice(ade.getAdeIndice());
        renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
        renegociarParam.setAdeCodigosRenegociacao(List.of(ade.getAdeCodigo()));
        renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
        renegociarParam.setCompraContrato(true);
        renegociarParam.setAdeIdentificador("");
        renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

		final String adeCodigoNovo = renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("824788").getSadCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAdeByAdeCodigo(adeCodigoNovo).getSadCodigo());
	}

	@Test
	public void portabilidade_sistema_quinzenal_novo_prazo_maior_prazo_max_quinzenal() throws AutorizacaoControllerException, UsuarioControllerException {
	    Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE, "10");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade = autDescontoService.inserirAutDesconto("34534335558746878554", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 3217474, 20, Short.parseShort("1"));

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
            renegociarParam.setAdePrazo(21);
            renegociarParam.setAdeCarencia(ade.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);
        });
	}

	@Test
	public void tentar_portar_mais_contratos_permitido_para_servico() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_QTDE_MAX_ADE_COMPRA, "1");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("34534335544568554", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 9824777L, 20, Short.parseShort("1"));

    		final AutDesconto ade2 = autDescontoService.inserirAutDesconto("574411444", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 200.0f, 38414, 20, Short.parseShort("1"));

    		final AutDesconto ade3 = autDescontoService.inserirAutDesconto("8975184844", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 130.0f, 578251, 20, Short.parseShort("1"));

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(250.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo(), ade2.getAdeCodigo(), ade3.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);
        });
	}

	@Test
	public void destino_compra_vlr_maior_que_soma_ades_origem_permitido() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_QTDE_MAX_ADE_COMPRA, "2");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS, "1");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("34534335544568554", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 396777, 20, Short.parseShort("1"));

    		final AutDesconto ade2 = autDescontoService.inserirAutDesconto("574411444", "4",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 200.0f, 32789744, 20, Short.parseShort("1"));

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(301.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo(), ade2.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);
        });
	}

	@Test
	public void tenta_compra_prazo_maior_prazo_restante_nao_permitido() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_QTDE_MAX_ADE_COMPRA, "2");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "1");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("34534335544568554", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 396777, 20, Short.parseShort("1"));

    		final AutDesconto ade2 = autDescontoService.inserirAutDesconto("574411444", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 200.0f, 32789744, 20, Short.parseShort("1"));

    		autDescontoService.alterarAutDescontoPorNumeroAde(32789744L, "20", "2");
    		autDescontoService.alterarAutDescontoPorNumeroAde(396777L, "20", "3");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(250.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo(), ade2.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);
        });
	}

	@Test
	public void portabilidade_compra_prazo_maior_prazo_restante() throws UsuarioControllerException, AutorizacaoControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_QTDE_MAX_ADE_COMPRA, "2");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "1");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("34534335544568554", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 46656, 20, Short.parseShort("1"));

    		final AutDesconto ade2 = autDescontoService.inserirAutDesconto("574411444", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 200.0f, 8475656, 20, Short.parseShort("1"));

    		autDescontoService.alterarAutDescontoPorNumeroAde(46656L, "20", "2");
    		autDescontoService.alterarAutDescontoPorNumeroAde(8475656L, "20", "3");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(250.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(980.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo(), ade2.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(970.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

    		/*Assert.assertThrows(AutorizacaoControllerException.class, () -> ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.sso", AcessoSistema.getAcessoUsuarioSistema()));

    		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("46656").getSadCodigo());
    		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("8475656").getSadCodigo());
    		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAdeByAdeCodigo(adeCodigoNovo).getSadCodigo());*/
        });
	}

	@Test
	public void compra_invalida_novo_valor_maior_que_margem_restante() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("34534335544568554", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 579255544, 20, Short.parseShort("1"));


    		final RegistroServidor rse = registroServidorDao.findById("48178080808080808080808080808C80").get();

    		final BigDecimal margemRestAnterior = rse.getRseMargemRest();

    		registroServidorService.alterarRseMargemRest(rse.getRseMatricula(), BigDecimal.valueOf(0.0d));

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(101.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(2020.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
            renegociarParam.setCdeVlrLiberado(BigDecimal.valueOf(2010.0d));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

    		registroServidorService.alterarRseMargemRest(rse.getRseMatricula(), margemRestAnterior);
        });
	}

	@Test
	public void compra_invalida_sem_minimo_prazos_pagos() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA, "3");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("35423562466", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 57284184L, 20, Short.parseShort("1"));

    		autDescontoService.alterarAutDescontoPorNumeroAde(57284184L, "20", "2");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(1960.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA, "0");
        });
	}

	@Test
	public void compra_invalida_sem_minimo_percentual_prazos_pagos() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG, "10");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("35423562466", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 57284184L, 20, Short.parseShort("1"));

    		autDescontoService.alterarAutDescontoPorNumeroAde(57284184L, "20", "1");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(1960.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG, "0");
        });
	}

	@Test
	public void compra_com_minimo_percentual_prazos_pagos() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG, "10");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("9048484909498", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 5877898544L, 20, Short.parseShort("1"));

    		autDescontoService.alterarAutDescontoPorNumeroAde(5877898544L, "20", "1");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(1960.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

            renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);
        });
	}

	@Test
	public void compra_invalida_percentual_minimo_vingencia_quinzenal() throws AutorizacaoControllerException, UsuarioControllerException {
        Assertions.assertThrows(AutorizacaoControllerException.class, () -> {
    		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
    		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");
    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA, "30");

    		JspHelper.limparCacheParametros();

    		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

    		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("35423562466", "5",
    				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
    				usuCsa2.getUsuCodigo(), 100.0f, 5333484L, 20, Short.parseShort("1"));

    		ade1.setAdeAnoMesIni(LocalDate.now().minusMonths(1).toDate());
    		ade1.setAdeAnoMesIniRef(LocalDate.now().minusMonths(1).toDate());
    		ade1.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		autDescontoDao.save(ade1);

    		final SimpleDateFormat sfData = new SimpleDateFormat("yyyy-MM-dd");

    		final Date pex = LocalDate.now().toDate();

    		final List<PeriodoExportacao> listPex = periodoExportacaoDao.findAll();

    		if ((listPex != null) && !listPex.isEmpty()) {
    			periodoExportacaoService.updateTodosPeriodosExportacao(sfData.format(DateHelper.toPeriodDate(pex)), DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-01"
    					, DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-15");
    		} else {
    			periodoExportacaoService.inserePeriodoExportacaoOrgaos(sfData.format(DateHelper.toPeriodDate(pex)), DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-01"
    					, DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-15");
    		}

    		autDescontoService.alterarAutDescontoPorNumeroAde(5333484L, "20", "1");

    		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
    				, "1");

    		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

    		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
    				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
    				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
    				.setCodigoEntidade(CSA_ORIGEM).build();

    		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

    		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
    				"751F8080808080808080808080809780", CSA_ORIGEM);

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo("48178080808080808080808080808C80");
            renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
            renegociarParam.setAdePrazo(20);
            renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
            renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
            renegociarParam.setComSerSenha(false);
            renegociarParam.setAdeIndice(ade1.getAdeIndice());
            renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(1960.0d)));
            renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
            renegociarParam.setCompraContrato(true);
            renegociarParam.setAdeIdentificador("");
            renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

    		renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

    		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA, null);
        });
	}

	@Test
	public void compra_atendendo_percentual_minimo_vingencia_quinzenal() throws AutorizacaoControllerException, UsuarioControllerException {
		parametroSistemaService.configurarParametroServicoCsa(SVC_EMPRESTIMO, CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO,
		        CSA_ORIGEM, TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo());

		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS, "0");
		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA, "10.00");

		JspHelper.limparCacheParametros();

		final Usuario usuCsa2 = UsuarioService.getUsuario("csa2");

		final AutDesconto ade1 = autDescontoService.inserirAutDesconto("3842157456222", "4",
				"48178080808080808080808080808C80", "751F8080808080808080808080809Z85",
				usuCsa2.getUsuCodigo(), 100.0f, 7439344L, 20, Short.parseShort("1"));

		ade1.setAdeAnoMesIni(LocalDate.now().minusMonths(3).toDate());
		ade1.setAdeAnoMesIniRef(LocalDate.now().minusMonths(3).toDate());
		ade1.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

		autDescontoDao.save(ade1);

		final SimpleDateFormat sfData = new SimpleDateFormat("yyyy-MM-dd");

		final Date pex = LocalDate.now().toDate();

		final List<PeriodoExportacao> listPex = periodoExportacaoDao.findAll();

		if ((listPex != null) && !listPex.isEmpty()) {
			periodoExportacaoService.updateTodosPeriodosExportacao(sfData.format(DateHelper.toPeriodDate(pex)), DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-01"
					, DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-15");
		} else {
			periodoExportacaoService.inserePeriodoExportacaoOrgaos(sfData.format(DateHelper.toPeriodDate(pex)), DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-01"
					, DateHelper.getYear(pex) + "-" + DateHelper.getMonth(pex) + "-15");
		}

		relacionamentoServicoService.incluirRelacionamentoServico(SVC_EMPRESTIMO, SVC_EMPRESTIMO
				, "1");

		final String usuCodigoEntidadeCompradora = UsuarioService.getUsuario("csa").getUsuCodigo();

		final AcessoSistema responsavel = new AcessoSistemaBuilder(usuCodigoEntidadeCompradora)
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSA)
				.setFunCodigo(CodedValues.FUN_COMP_CONTRATO)
				.setCodigoEntidade(CSA_ORIGEM).build();

		configuradorPermissoesUsuario.carregarPermissoes(responsavel, usuCodigoEntidadeCompradora, AcessoSistema.ENTIDADE_CSA, CSA_ORIGEM);

		final Convenio convenio = convenioService.findBySvcCodigoAndOrgCodigoAndCsaCodigo(SVC_EMPRESTIMO,
				"751F8080808080808080808080809780", CSA_ORIGEM);

		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo("48178080808080808080808080808C80");
        renegociarParam.setAdeVlr(BigDecimal.valueOf(98.0d));
        renegociarParam.setAdePrazo(20);
        renegociarParam.setAdeCarencia(ade1.getAdeCarencia());
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setComSerSenha(false);
        renegociarParam.setAdeIndice(ade1.getAdeIndice());
        renegociarParam.setAdeVlrLiquido((BigDecimal.valueOf(1960.0d)));
        renegociarParam.setAdeCodigosRenegociacao(List.of(ade1.getAdeCodigo()));
        renegociarParam.setCompraContrato(true);
        renegociarParam.setAdeIdentificador("");
        renegociarParam.setAdePeriodicidade(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

        final String adeCodigoNovo = renegociarConsignacaoControllerBean.renegociar(renegociarParam, responsavel);

		assertEquals(CodedValues.SAD_AGUARD_LIQUI_COMPRA, autDescontoService.getAde("7439344").getSadCodigo());
		assertEquals(CodedValues.SAD_AGUARD_CONF, autDescontoService.getAdeByAdeCodigo(adeCodigoNovo).getSadCodigo());

		parametroSistemaService.configurarParametroServicoCse(SVC_EMPRESTIMO, CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA, null);
	}

	@AfterEach
	public void after() {
        if (periodicidadeMensalOriginal) {
            parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_PERIODICIDADE_FOLHA, CodedValues.PERIODICIDADE_FOLHA_MENSAL);
        }
	}
}
