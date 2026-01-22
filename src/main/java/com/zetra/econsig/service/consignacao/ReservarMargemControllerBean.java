package com.zetra.econsig.service.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.GAPHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.contabilizacao.ContabilizacaoInclusaoContratos;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacaoFactory;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DecisaoJudicialHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailSer;
import com.zetra.econsig.persistence.entity.DestinatarioEmailSerHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.PrazoConsignataria;
import com.zetra.econsig.persistence.entity.PrazoConsignatariaHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusRegistroServidor;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.convenio.ObtemConvenioRelCadTaxasQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.parametro.ListaTodosParamSvcCseQuery;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.OrigemSolicitacaoEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ReservarMargemControllerBean</p>
 * <p>Description: Session Bean para a operação de Reserva de Margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service("reservarMargemController")
@Transactional
public class ReservarMargemControllerBean extends AutorizacaoControllerBean implements ReservarMargemController {
    private static final String RENEGOCIAR = "RENEGOCIAR";

    private static final String COMPRAR = "COMPRAR";

    private static final String ALONGAR = "ALONGAR";

    private static final String RESERVAR = "RESERVAR";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservarMargemControllerBean.class);

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private CoeficienteController coeficienteController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @SuppressWarnings("java:S3358")
    @Override
    public String reservarMargem(ReservarMargemParametros margemParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        String adeCodigoNovo = null;

        try {
            margemParam.checkNotNullSafe();
        } catch (final ParametrosException pe) {
            throw new AutorizacaoControllerException(pe);
        }

        Map<String, Object> parametros = margemParam.getParametros();
        final boolean cnvAtivo = margemParam.getCnvAtivo();
        final boolean svcAtivo = margemParam.getSvcAtivo();
        final boolean csaAtivo = margemParam.getCsaAtivo();
        final boolean orgAtivo = margemParam.getOrgAtivo();
        final boolean estAtivo = margemParam.getEstAtivo();
        final boolean cseAtivo = margemParam.getCseAtivo();
        String cnvCodigo = margemParam.getCnvCodigo();
        final String corCodigo = margemParam.getCorCodigo();
        final String rseCodigo = margemParam.getRseCodigo();
        final boolean serCnvAtivo = margemParam.getSerCnvAtivo();
        final boolean serAtivo = margemParam.getSerAtivo();
        final List<String> adeCodigosRenegociacao = margemParam.getAdeCodigosRenegociacao();
        String adeCodReg = margemParam.getAdeCodReg();
        Short adeIncMargem = margemParam.getAdeIncMargem();
        final String acao = margemParam.getAcao();
        BigDecimal adeVlrTac = margemParam.getAdeVlrTac();
        Integer adePrazo = margemParam.getAdePrazo();
        BigDecimal adeVlr = margemParam.getAdeVlr();
        Integer adeCarencia = margemParam.getAdeCarencia();
        String adeIdentificador = margemParam.getAdeIdentificador();
        String adeIndice = margemParam.getAdeIndice();
        BigDecimal adeVlrIof = margemParam.getAdeVlrIof();
        final BigDecimal adeVlrLiquido = margemParam.getAdeVlrLiquido();
        final BigDecimal adeVlrMensVinc = margemParam.getAdeVlrMensVinc();
        final BigDecimal adeTaxaJuros = margemParam.getAdeTaxaJuros();
        final boolean validar = margemParam.getValidar();
        final boolean permitirValidacaoTaxa = margemParam.getPermitirValidacaoTaxa();
        final java.util.Date adeAnoMesIni = margemParam.getAdeAnoMesIni();
        final java.util.Date adeAnoMesFim = margemParam.getAdeAnoMesFim();
        final String sadCodigo = margemParam.getSadCodigo();
        final boolean comSerSenha = margemParam.getComSerSenha();
        java.sql.Date adeAnoMesIniRef = margemParam.getAdeAnoMesIniRef();
        java.sql.Date adeAnoMesFimRef = margemParam.getAdeAnoMesFimRef();
        String adeTipoVlr = margemParam.getAdeTipoVlr();
        final Short adeIntFolha = margemParam.getAdeIntFolha();
        final Timestamp adeDtHrOcorrencia = margemParam.getAdeDtHrOcorrencia();
        final BigDecimal adeVlrSegPrestamista = margemParam.getAdeVlrSegPrestamista();
        final String banco = margemParam.getAdeBanco();
        final String agencia = margemParam.getAdeAgencia();
        final String conta = margemParam.getAdeConta();
        final BigDecimal vlrTotalCompradoRenegociado = margemParam.getVlrTotalCompradoRenegociado();

        final String nomeAnexo = margemParam.getNomeAnexo();
        final String idAnexo = margemParam.getIdAnexo();
        String aadDescricao = margemParam.getAadDescricao();
        final boolean validaAnexo = margemParam.getValidaAnexo();

        String adePeriodicidade = margemParam.getAdePeriodicidade();

        final boolean telaConfirmacaoDuplicidade = margemParam.isTelaConfirmacaoDuplicidade();
        final boolean chkConfirmarDuplicidade = margemParam.isChkConfirmarDuplicidade();
        final String motivoOperacaoCodigoDuplicidade = margemParam.getMotivoOperacaoCodigoDuplicidade();
        final String motivoOperacaoObsDuplicidade = margemParam.getMotivoOperacaoObsDuplicidade();
        final String tmoCodigo = margemParam.getTmoCodigo();

        adeVlr = RESERVAR.equals(margemParam.getAcao()) ? validaPostoFixoSvcCsa(margemParam.getCnvCodigo(), adeVlr, rseCodigo, responsavel) : adeVlr;

        if (TextHelper.isNull(adePeriodicidade)) {
            adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
        }
        if (PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
            throw new AutorizacaoControllerException("mensagem.erro.ade.periodicidade.invalida", responsavel);
        }
        final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);
        // Valida a periodicidade informada para a reserva quando o sistema não permite periodicidade diferente da configurada para o sistema
        if (!permiteEscolherPeriodicidade && !adePeriodicidade.equals(PeriodoHelper.getPeriodicidadeFolha(responsavel))) {
            throw new AutorizacaoControllerException("mensagem.erro.ade.periodicidade.invalida", responsavel);
        }

        final String cidCodigo = margemParam.getCidCodigo();

        // parâmetros para reserva de benefícios
        String cbeCodigo = margemParam.getCbeCodigo();
        final String tlaCodigo = margemParam.getTlaCodigo();
        final boolean isReservaContratoBeneficio = !TextHelper.isNull(cbeCodigo);

        final boolean temLeilaoSimulacao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        BigDecimal adeVlrPercentual = null;

        // Verifica se o usuário possui permissão para inclusão avançada de consignação
        final boolean usuPossuiIncAvancadaAde = (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) ||
                (responsavel.temPermissao(CodedValues.FUN_REIMPLANTAR_CAPITAL_DEVIDO) && margemParam.isReimpCapitalDevido()) ||
                (responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_DEMITIR_COLABORADOR) && margemParam.isRetencaoVerbaRescisoria()) ||
                ((responsavel.isCseSupOrg() || responsavel.isCsa()) && CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL))) &&
                (RESERVAR.equalsIgnoreCase(acao));
        // Verifica se o usuário possui permissão para alteração avançada de consignação
        final boolean usuPossuiAltAvancadaAde = usuarioPossuiAltAvancadaAde(responsavel);

        // Parâmetros de Inclusão Avançada
        final boolean validaMargemIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaMargem() : ReservarMargemParametros.PADRAO_VALIDA_MARGEM;
        final boolean validaTaxaJurosIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaTaxaJuros() : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
        final boolean validaPrazoIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaPrazo() : ReservarMargemParametros.PADRAO_VALIDA_PRAZO;
        final boolean validaDadosBancariosIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaDadosBancarios() : ReservarMargemParametros.PADRAO_VALIDA_DADOS_BANCARIOS;
        final boolean validaSenhaServidorIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaSenhaServidor() : ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR;
        final boolean validaBloqSerCnvCsaIncAvancada = usuPossuiIncAvancadaAde ? margemParam.getValidaBloqSerCnvCsa() : ReservarMargemParametros.PADRAO_VALIDA_BLOQ_SER_CNV_CSA;
        final boolean validaDataNascAvancado = usuPossuiIncAvancadaAde ? margemParam.getValidaDataNascimento() : ReservarMargemParametros.PADRAO_VALIDA_DATA_NASCIMENTO;
        final boolean validaLimiteContrato = usuPossuiIncAvancadaAde ? margemParam.getValidaLimiteAde() : ReservarMargemParametros.PADRAO_VALIDA_LIMITE_ADE;

        // Operação é uma inclusão avançada de contrato
        final boolean inclusaoAvancada = !validaMargemIncAvancada || !validaPrazoIncAvancada || !validaTaxaJurosIncAvancada || !validaDadosBancariosIncAvancada || !validaSenhaServidorIncAvancada || !validaBloqSerCnvCsaIncAvancada || !validaDataNascAvancado || !validaLimiteContrato;

        // Parâmetros de Alteração Avançada
        boolean validaExigeInfBancariaAltAvancada = usuPossuiAltAvancadaAde ? margemParam.getValidaExigeInfBancaria() : ReservarMargemParametros.PADRAO_VALIDA_EXIGE_INF_BANCARIA;
        // Vindo da alteração avançada, com opção de inclusão de contrato com diferença de parcelas, este valor diferencia-se do validaSenhaServidorIncAvancada
        final boolean validaSenhaServidorAltAvancada = usuPossuiAltAvancadaAde ? margemParam.getValidaSenhaServidor() : ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR;
        final boolean validaIndiceAltAvancada = validaExigeInfBancariaAltAvancada; // TODO ### Avaliar se está correto ###

        Convenio convenio = null;
        RegistroServidorTO rseDto = null;

        boolean verificaMargem = true;
        boolean validaTaxa = false;
        boolean validaIdentificador = true;
        Short carenciaFinal = null;
        Short prazoCarencia = null;
        String svcCodigo = null;
        String orgCodigo = null;
        String csaCodigo = null;
        String ocaCodigo = null;
        String cnvCodVerba = null;
        String modalidadeOp = null;
        String matriculaSerCsa = null;

        // Se não é processamento de Lote, realiza as validações das entidades
        if ((parametros == null) && (validaBloqSerCnvCsaIncAvancada && (cnvAtivo || svcAtivo || csaAtivo || orgAtivo || estAtivo || cseAtivo))) {
            validarEntidades(cnvCodigo, corCodigo, cnvAtivo, svcAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, true, responsavel);
        }

        // Verifica os limites de contratos
        final boolean serAtivoPodeReservarMargem = validaBloqSerCnvCsaIncAvancada && serAtivo;
        boolean podeReservar = false;

        // No módulo de rescisão, não devemos validar a inclusão da reserva ou os motivos para bloqueá-la,
        // pois todos os demais contratos serão liquidados. Além disso, o servidor perderá o acesso ao sistema,
        // resultando em uma reserva única vinculada exclusivamente à rescisão.
        if (margemParam.isRetencaoVerbaRescisoria()) {
            podeReservar = true;
        } else {
            podeReservar = podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, false, serCnvAtivo, serAtivoPodeReservarMargem, adeCodigosRenegociacao, adeVlr, adeVlrLiquido, adePrazo,
                                              adeCarencia, adePeriodicidade, adeIdentificador, parametros, acao, validaLimiteContrato, telaConfirmacaoDuplicidade, responsavel);
        }

        List<TransferObject> adesReservaCartao = null;
        String adeCodigoIncAlt = null;

        if (podeReservar) {
            final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);
            // Índice cadastrado automaticamente
            final boolean indiceSomenteAutomatico = ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel);

            if (CodedValues.COD_REG_ESTORNO.equals(adeCodReg)) {
                adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;
            } else if (adeCodReg == null) {
                adeCodReg = CodedValues.COD_REG_DESCONTO;
            }

            final boolean inclusaoViaLote = (responsavel.getFunCodigo() != null) && (CodedValues.FUN_INCLUSAO_VIA_LOTE.equals(responsavel.getFunCodigo()) || CodedValues.FUN_ALTERACAO_VIA_LOTE.equals(responsavel.getFunCodigo()));
            boolean serInfBancariaObrigatoria = false;
            boolean validarInfBancaria = false;
            boolean controlaSaldoDevedor = false;
            boolean possuiControleVlrMaxDesconto = false;
            String paramControleVlrMaxDesconto = null;
            String paramControleTetoDesconto = null;
            CustomTransferObject tpsControleVlrMaxDesconto = null;
            java.sql.Date ocaPeriodo = null;

            try {
                if (parametros == null) {
                    convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                    svcCodigo = convenio.getServico().getSvcCodigo();
                    orgCodigo = convenio.getOrgao().getOrgCodigo();
                    csaCodigo = convenio.getConsignataria().getCsaCodigo();
                    cnvCodVerba = convenio.getCnvCodVerba();
                } else {
                    svcCodigo = parametros.get(Columns.SVC_CODIGO).toString();
                    orgCodigo =  parametros.get(Columns.ORG_CODIGO).toString();
                    csaCodigo = parametros.get(Columns.CSA_CODIGO).toString();
                    cnvCodVerba = parametros.get(Columns.CNV_COD_VERBA).toString();
                }

                //Caso a inclusão esteja sendo feita de acordo com parâmetro de serviço 277
                //forçar a parcela com valor 1,00 e prazo indeterminado
                if (responsavel.isSer()) {
                    CustomTransferObject paramSvcCsePula = null;
                    boolean params = false;

                    paramSvcCsePula = getParametroSvc(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA, svcCodigo, "", false, parametros);

                    if ((paramSvcCsePula.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramSvcCsePula.getAttribute(Columns.PSE_VLR))) {
                        final CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(svcCodigo, responsavel);
                        params = ((naturezaSvc != null) && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && !CodedValues.NSE_EMPRESTIMO.equals(naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString()));
                    }


                    if (params) {
                        adeVlr = new BigDecimal(1.00);
                        adePrazo = null;
                    }
                }

                final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                final boolean exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao));
                if (responsavel.isCsaCor() && exigeModalidadeOperacao) {
                    modalidadeOp = margemParam.getTdaModalidadeOperacao();
                    if (TextHelper.isNull(modalidadeOp)) {
                        throw new AutorizacaoControllerException("mensagem.erro.modalidade.operacao.obrigatorio", responsavel);
                    }
                }

                final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                final boolean exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa));
                if (responsavel.isCsaCor() && exigeMatriculaSerCsa) {
                    matriculaSerCsa = margemParam.getTdaMatriculaSerCsa();
                    if (TextHelper.isNull(matriculaSerCsa)) {
                        throw new AutorizacaoControllerException("mensagem.erro.matricula.csa.obrigatoria", responsavel);
                    }
                }

                // Validação do anexo
                // DESENV-20474 - Foi solicitado a validação de obrigatoridade de anexo inclusive para o servidor, porém somente na Web, por este motivo, mantemos a validação de o responsável é diferente
                // de servidor, para que não bloquei inclusões que são feitas via Mobile, e por este motivo o bloqueio do anexo do servidor é feito somente via Web e não no Bean.
                if (validaAnexo && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                        parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel) &&
                        TextHelper.isNull(nomeAnexo) && !responsavel.isSer()) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.pode.inserir.nova.reserva.para.este.servico.pois.anexo.obrigatorio", responsavel);
                }
                // Busca os dados do servidor
                rseDto = RegistroServidorDtoAssembler.createDto(RegistroServidorHome.findByPrimaryKey(rseCodigo), true);
                // verifica se o órgão do convênio é o mesmo do servidor
                if (!rseDto.getOrgCodigo().equals(orgCodigo)) {
                    LOG.error("Não é possível inserir nova reserva, pois o servidor não pertence ao órgão informado para reserva de margem.");
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
                }

                // Se tem relacionamento para compartilhamento de taxas, verifica em qual serviço/convênio
                // o servidor ainda não tem contratos
                final boolean temRelacionamentoCompTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);
                try {
                    if (temRelacionamentoCompTaxas) {
                        final ObtemConvenioRelCadTaxasQuery cnvRelacCadTaxas = new ObtemConvenioRelCadTaxasQuery();
                        cnvRelacCadTaxas.svcCodigoOrigem = svcCodigo;
                        cnvRelacCadTaxas.csaCodigo = csaCodigo;
                        cnvRelacCadTaxas.corCodigo = corCodigo;
                        cnvRelacCadTaxas.orgCodigo = orgCodigo;
                        cnvRelacCadTaxas.rseCodigo = rseCodigo;
                        cnvRelacCadTaxas.adeCodigosReneg = adeCodigosRenegociacao;

                        final List<TransferObject> cnvCodigosPossiveis = cnvRelacCadTaxas.executarDTO();

                        if ((cnvCodigosPossiveis != null) && !cnvCodigosPossiveis.isEmpty()) {
                            final TransferObject cnvRelCadTaxa = cnvCodigosPossiveis.get(0);
                            final String cnvCodigoRel = cnvRelCadTaxa.getAttribute(Columns.CNV_CODIGO).toString();
                            if (!cnvCodigoRel.equals(cnvCodigo)) {
                                // Se o convênio deve ser outro
                                convenio = ConvenioHome.findByPrimaryKey(cnvCodigoRel);
                                cnvCodigo = convenio.getCnvCodigo();
                                cnvCodVerba = convenio.getCnvCodVerba();
                            }
                        }
                    }
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Verifica se o serviço da nova reserva é origem de um relacionamento de alongamento.
                // Caso seja, não permite a inclusão da reserva
                final boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
                if (temAlongamento) {
                    final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                    queryRel.tntCodigo = CodedValues.TNT_ALONGAMENTO;
                    queryRel.svcCodigoOrigem = svcCodigo;
                    queryRel.svcCodigoDestino = null;
                    final List<TransferObject> svcCodigosRel = queryRel.executarDTO();
                    if ((svcCodigosRel != null) && !svcCodigosRel.isEmpty()) {
                        if ((acao != null) && !ALONGAR.equalsIgnoreCase(acao)) {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.pode.inserir.nova.reserva.para.este.servico.pois.ele.esta.relacionado.alongamento", responsavel);
                        } else if (ALONGAR.equalsIgnoreCase(acao)) {
                            // Se é operação de alongamento de contrato, e o serviço é
                            // origem do relacionamento, então não valida a margem
                            verificaMargem = false;
                        }
                    }
                }

                // Se for renegociação ou compra e permitir renegociar contrato na margem 3 casada negativa, não valida margem
                // Não valida margem negativa do servidor, já que a margem pode ter sido positivada na liquidação dos contratos antigos
                if ((acao != null) && (COMPRAR.equals(acao) || RENEGOCIAR.equals(acao)) &&
                        permiteRenegociarComprarMargem3NegativaCasada(rseDto, adeIncMargem, vlrTotalCompradoRenegociado, adeVlr, false, responsavel)) {
                    verificaMargem = false;
                }

                // Se não valida situação do servidor e ele está excluído, caso a consignatária permita inclusão
                // para servidor excluído via Host-Host, não valida a margem pois esta estará inválida.
                if (!serAtivo && rseDto.isExcluido() && CanalEnum.SOAP.equals(responsavel.getCanal())) {
                    final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_HOST_A_HOST, responsavel);
                    if (!TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr)) {
                        verificaMargem = false;
                    }
                }

                // Se é aprovação/encerramento de leilão, não valida margem, já que o contrato deve ser inserido mesmo
                // que o servidor não possua margem
                if (temLeilaoSimulacao && margemParam.isDestinoAprovacaoLeilaoReverso()) {
                    verificaMargem = false;
                    validaIdentificador = false;
                }

                if(margemParam.isRetencaoVerbaRescisoria()) {
                    validaIdentificador = false;
                }

                if (!responsavel.isSer() && validaIdentificador) {
                    // Busca parâmetro de máscara para adeIdentificador para o serviço
                    final CustomTransferObject paramMascaraAde = getParametroSvc(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE, svcCodigo, "", false, parametros);
                    final String mascaraAdeIdentificador = (paramMascaraAde != null) ? (String) paramMascaraAde.getAttribute(Columns.PSE_VLR) : null;
                    if (!TextHelper.isNull(mascaraAdeIdentificador) && !TextHelper.isNull(adeIdentificador)) {
                        try {
                            adeIdentificador = TextHelper.aplicarMascara(adeIdentificador, mascaraAdeIdentificador);
                        } catch (final ZetraException e) {
                            throw new AutorizacaoControllerException("mensagem.erro.ade.identificador.invalido", responsavel);
                        }
                    }

                    if (margemParam.getValidaAdeIdentificador()) {
                        final CustomTransferObject paramIdentificadorObrig = getParametroSvc(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO, svcCodigo, cnvCodigo, Boolean.class, null, null, null, false, true, parametros);
                        final boolean adeIdentificadorObrigatorio = ((paramIdentificadorObrig != null) && (paramIdentificadorObrig.getAttribute(Columns.PSE_VLR) != null) && ((Boolean)paramIdentificadorObrig.getAttribute(Columns.PSE_VLR)).booleanValue());
                        if (adeIdentificadorObrigatorio && TextHelper.isNull(adeIdentificador)) {
                            throw new AutorizacaoControllerException("mensagem.informe.ade.identificador", responsavel);
                        }
                    }
                }

                //DESENV-17761: se reserva oriunda de processamento de lote, verifica parâmetro de serviço por consignatária 316.
                //              o parâmetro de serviço TPS_INF_BANCARIA_OBRIGATORIA (54) só será validado se este parâmetro não estiver definido.
                final CustomTransferObject paramSvcCsaInfoBancariaObrigatorioLote = !inclusaoViaLote ? null : getParametroSvc(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA, svcCodigo, cnvCodigo, String.class, null, null, null, false, false, parametros);
                final Boolean infoBancariaObrigatorioLote = paramSvcCsaInfoBancariaObrigatorioLote == null ? null : CodedValues.PSC_BOOLEANO_SIM.equals(paramSvcCsaInfoBancariaObrigatorioLote.getAttribute(Columns.PSE_VLR)); // O retorno é por PSE_VLR

                // Busca parâmetro de Informações bancárias do servidor
                final CustomTransferObject paramInfBancarias = getParametroSvc(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA, svcCodigo, "", false, parametros);
                serInfBancariaObrigatoria = (((infoBancariaObrigatorioLote != null) && infoBancariaObrigatorioLote) || ((infoBancariaObrigatorioLote == null) && validaDadosBancariosIncAvancada &&
                        (paramInfBancarias != null) && (paramInfBancarias.getAttribute(Columns.PSE_VLR) != null) &&
                        "1".equals(paramInfBancarias.getAttribute(Columns.PSE_VLR))));

                final CustomTransferObject paramValidacaoInfBancarias = getParametroSvc(CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA, svcCodigo, "", false, parametros);
                validarInfBancaria = (validaDadosBancariosIncAvancada &&
                        (paramValidacaoInfBancarias != null) && (paramValidacaoInfBancarias.getAttribute(Columns.PSE_VLR) != null) &&
                        "1".equals(paramValidacaoInfBancarias.getAttribute(Columns.PSE_VLR)));

                // Se é aprovação/encerramento de leilão, não exige informação bancária
                if (temLeilaoSimulacao && margemParam.isDestinoAprovacaoLeilaoReverso()) {
                    serInfBancariaObrigatoria = false;
                }

                validaExigeInfBancariaAltAvancada = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, validaExigeInfBancariaAltAvancada, null, responsavel);

                // Valida as informações bancárias do servidor, caso não seja solicitação vinda do servidor
                if (!responsavel.isSer() && validaExigeInfBancariaAltAvancada) {
                    validarDadosBancariosServidor(serInfBancariaObrigatoria, validarInfBancaria, banco, agencia, conta, rseDto, responsavel);
                }

                // Busca parâmetro de Controle de Saldo devedor
                final CustomTransferObject paramControleSdv = getParametroSvc(CodedValues.TPS_CONTROLA_SALDO, svcCodigo, "", false, parametros);
                controlaSaldoDevedor = ((paramControleSdv != null) && (paramControleSdv.getAttribute(Columns.PSE_VLR) != null) &&
                        "1".equals(paramControleSdv.getAttribute(Columns.PSE_VLR)));

                // Busca parâmetro de Controle de Valor máximo de desconto
                tpsControleVlrMaxDesconto = getParametroSvc(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO, svcCodigo, "", false, parametros);
                paramControleVlrMaxDesconto = (tpsControleVlrMaxDesconto != null ? (String)tpsControleVlrMaxDesconto.getAttribute(Columns.PSE_VLR): null);
                possuiControleVlrMaxDesconto = (controlaSaldoDevedor && (paramControleVlrMaxDesconto != null) && !"0".equals(paramControleVlrMaxDesconto));

                // Busca parâmetro de Controle de teto de desconto
                final CustomTransferObject tpsControleTetoDesconto = getParametroSvc(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO, svcCodigo, "", false, parametros);
                paramControleTetoDesconto = (tpsControleTetoDesconto != null ? (String) tpsControleTetoDesconto.getAttribute(Columns.PSE_VLR) : CodedValues.NAO_CONTROLA_TETO_DESCONTO);

                // Parâmetro com o valor máximo da TAC
                if (adeVlrTac != null) {
                    final CustomTransferObject tpsVlrMaxTac = getParametroSvc(CodedValues.TPS_VALOR_MAX_TAC, svcCodigo, "", false, parametros);
                    if (!TextHelper.isNull(tpsVlrMaxTac.getAttribute(Columns.PSE_VLR))) {
                        final BigDecimal maxTacCse = new BigDecimal(tpsVlrMaxTac.getAttribute(Columns.PSE_VLR).toString());
                        if (adeVlrTac.compareTo(maxTacCse) > 0) {
                            throw new AutorizacaoControllerException("mensagem.erro.valor.tac.maximo", responsavel, NumberHelper.format(maxTacCse.doubleValue(), NumberHelper.getLang()));
                        }
                    }
                }

                final boolean validarPrazoCompraCartao = !COMPRAR.equalsIgnoreCase(acao) ? true : margemParam.getValidaPrazo();

                /**
                 * Verifica o parametro de prazo fixo e o valor do prazo, se o prazo informado para a ade
                 * for diferente do prazo fixo é gerada uma exceção.
                 */
                if (validaPrazoIncAvancada && validarPrazoCompraCartao) {
                    verificaPrazo(adePrazo, adeCarencia, rseDto.getRsePrazo(), adePeriodicidade, rseCodigo, svcCodigo, csaCodigo, cnvCodigo, acao, parametros, responsavel);
                }

                /**
                 * Valida o valor da autorização quanto a valores negativos ou Zero, pois
                 * não são valores válidos. Permite apenas para lançamentos de contratos
                 * de correção de saldo, que são relacionados ao contrato principal de desconto,
                 * ou para serviços configurados para a consignatária que permitem inclusão de valores negativos
                 */
                if (((adeVlr == null) || (adeVlr.signum() != 1)) && ((acao == null) || !"CORRECAO_SALDO".equals(acao))) {
                    if (!parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
                        throw new AutorizacaoControllerException("mensagem.valorParcelaMenorIgualZero", responsavel);
                    } else {
                        verificaMargem = false;
                    }
                }

                if (((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                        ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) && !TextHelper.isNull(margemParam.getOcaPeriodo())) {
                    ocaPeriodo = DateHelper.toSQLDate(DateHelper.parse(margemParam.getOcaPeriodo(), "yyyy-MM-dd"));
                }

                /**
                 * Verificação do parametro que indica que uma inclusao de contrato será tratado como uma alteração
                 * desde que o servidor já possua outro contrato aberto para a mesma verba
                 */
                if (!CodedValues.COD_REG_ESTORNO.equals(adeCodReg) && (RESERVAR.equalsIgnoreCase(acao))) {
                    adeCodigoIncAlt = verificaInclusaoAlteracao(rseCodigo, tmoCodigo, cnvCodigo, svcCodigo, orgCodigo, adeVlr, adePrazo, adeCarencia, adePeriodicidade,
                            adeIdentificador, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido,
                            adeVlrMensVinc, adeTaxaJuros, adeAnoMesIni, adeAnoMesFim, validar, serCnvAtivo, serAtivo, false,
                            cnvAtivo, svcAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, ocaPeriodo, parametros, margemParam, responsavel);
                    if (!TextHelper.isNull(adeCodigoIncAlt)) {
                        if ((ParamSist.paramEquals(CodedValues.TPC_INSERE_ALTERA_CRIA_NOVO_CONTRATO, null, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INSERE_ALTERA_CRIA_NOVO_CONTRATO, CodedValues.TPC_NAO, responsavel))
                                || (ParamSist.paramEquals(CodedValues.TPC_INSERE_ALTERA_CRIA_NOVO_CONTRATO, CodedValues.TPC_SIM, responsavel) && usuarioPodeConfirmarReserva(responsavel))) {
                            // se operação de insere/altera não gera novo contrato para usuário sem permissão de confirmação,
                            // então aqui o método já retorna o adeCodigo do contrato origem. Senão, este será usado para gerar
                            // relacionamento de insere/altera gera novo contrato com o novo contrato a ser reservado ao final deste método.

                            checaPermissaoEnviaNotificacaoSerReservaMargem(rseDto, svcCodigo, csaCodigo, responsavel);

                            return adeCodigoIncAlt;
                        }
                    }
                }

                /**
                 * Verifica o teto de desconto para o serviço
                 */
                if (CodedValues.CONTROLA_TETO_DESCONTO_PELO_CARGO.equals(paramControleTetoDesconto)) {
                    // Se tem controle de teto de desconto pelo cargo, valida o valor informado
                    validarTetoDescontoPeloCargo(rseCodigo, svcCodigo, adeVlr, responsavel);
                }

                // verifica índice duplicado se não for inclusão de contrato por ordem judicial via alteração avançada de contrato
                if (permiteCadIndice && validaIndiceAltAvancada) {
                    if (indiceSomenteAutomatico) {
                        adeIndice = null;
                    }
                    // Se o sistema permitir o cadastro de índice e não houver valor para o índice passado por parâmetro
                    // busca um novo índice
                    adeIndice = verificaAdeIndice("", rseCodigo, cnvCodigo, adeIndice, adeCodReg, adeCodigosRenegociacao, false, responsavel);
                }

                // Recupera prazo obrigatorio para a carencia no final do contrato
                final CustomTransferObject paramPrazoCarencia = getParametroSvc(CodedValues.TPS_PRAZO_CARENCIA_FINAL, svcCodigo, Short.valueOf("0"), false, parametros);
                try {
                    prazoCarencia = (Short)paramPrazoCarencia.getAttribute(Columns.PSE_VLR);
                } catch (final NumberFormatException ex) {
                    prazoCarencia = null;
                }

                // Recupera a carencia para o final do contrato
                final CustomTransferObject paramCarenciaFinal = getParametroSvc(CodedValues.TPS_CARENCIA_FINAL, svcCodigo, Short.valueOf("0"), false, parametros);
                try {
                    carenciaFinal = (Short)paramCarenciaFinal.getAttribute(Columns.PSE_VLR);
                    if ((adePrazo != null) && (prazoCarencia != null) && (prazoCarencia.intValue() != adePrazo.intValue())) {
                        carenciaFinal = null;
                    }
                } catch (final NumberFormatException ex) {
                    carenciaFinal = null;
                }

                /**
                 * Verifica o parametro de serviço que indica a validação da taxa anunciada.
                 * */
                if (permitirValidacaoTaxa) {
                    final CustomTransferObject paramValidaTaxa = getParametroSvc(CodedValues.TPS_VALIDAR_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);
                    validaTaxa = ((Boolean)paramValidaTaxa.getAttribute(Columns.PSE_VLR)).booleanValue() && !CodedValues.COD_REG_ESTORNO.equals(adeCodReg);
                } else {
                    validaTaxa = false;
                }

                // Se o usuário possui permissão para inclusão avançada, verifica se deve validar a taxa de acordo com a opção avançada
                validaTaxa = usuPossuiIncAvancadaAde ? validaTaxa && validaTaxaJurosIncAvancada : validaTaxa;

            } catch (FindException | NumberFormatException | ParseException | HQueryException | ParametroControllerException | ServicoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            boolean consumirMargem = true;
            try {
                if(parametroController.isReservaSaudeSemModulo(svcCodigo, responsavel) && TextHelper.isNull(cbeCodigo)) {
                    String codigoDependente = "";
                    String tibCodigo = TipoBeneficiarioEnum.TITULAR.tibCodigo;

                    if (!margemParam.getDadosAutorizacaoMap().isEmpty() && margemParam.existsDadoAutorizacao(CodedValues.TDA_BENEFICIARIO_DEPENTENTE)) {
                        codigoDependente = margemParam.getDadoAutorizacao(CodedValues.TDA_BENEFICIARIO_DEPENTENTE);
                        tibCodigo = TipoBeneficiarioEnum.DEPENDENTE.tibCodigo;
                    }

                    //Quando o desconto é por boleto não tem validação de margem e consumo
                    if (!margemParam.getDadosAutorizacaoMap().isEmpty() && margemParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)
                            && CodedValues.FORMA_PAGAMENTO_BOLETO.equals(margemParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO))) {
                        consumirMargem = false;
                        verificaMargem = false;
                    }

                    cbeCodigo = contratoBeneficioController.criaContratoBeneficioSemRegrasModulos(rseCodigo, svcCodigo, codigoDependente, csaCodigo, tibCodigo, adeVlr, responsavel);
                }

                final String vcoCodigo = getVerbaConvenio(cnvCodigo, parametros);

                // Determina o período inicial da autorização
                Date prazoIni = null;
                if (adeAnoMesIni == null) {
                    // Pega o período atual
                    prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, ocaPeriodo, adeCarencia, adePeriodicidade, responsavel);
                    margemParam.setAdeAnoMesIni(prazoIni);
                } else {
                    // Usa a data informada por parâmetro
                    prazoIni = DateHelper.toPeriodDate(adeAnoMesIni);
                }

                // Valida o período inicial informado ou calculado
                prazoIni = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, prazoIni, responsavel);

                // Antes de concluir a reserva verifica se trata-se de um lançamento de cartão de crédito e se ele é válido.
                // Pelo fato de porder forçar o período, precisamos passar o valor do período setado no adeAnoMesIni para garantir o período da inclusão para o que foi passado.
                if (margemParam.isForcaPeriodoLancamentoCartao()) {
                    parametros = (parametros != null) && !parametros.isEmpty() ? parametros : new HashMap<>();
                    parametros.put(CodedValues.PERIODO_CARTAO_CONFIGURAVEL, prazoIni);
                }
                adesReservaCartao = verificaLancamentoCartaoCredito(rseCodigo, adeVlr, cnvCodigo, parametros, responsavel);

                final CustomTransferObject paramSvcIncide = getParametroSvc(CodedValues.TPS_INCIDE_MARGEM, svcCodigo, adeIncMargem, false, parametros);
                final Short adeIncMargemSvc = paramSvcIncide.getAttribute(Columns.PSE_VLR) != null ? (Short) paramSvcIncide.getAttribute(Columns.PSE_VLR) : adeIncMargem;

                // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data inicio de acordo com essa data.
                final Date dataInicioFimAde = calcularDataIniFimMargemExtra(rseCodigo, prazoIni, adeIncMargemSvc, true, false, responsavel);

                boolean ocorrenciaInformacaoDataInicioMargem = false;
                if((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(prazoIni) > 0)) {
                    prazoIni = dataInicioFimAde;
                    margemParam.setAdeAnoMesIni(prazoIni);
                    adeCarencia = 0;
                    ocorrenciaInformacaoDataInicioMargem = true;
                }

                // Determina o período final da autorização
                Date prazoFim = null;
                if (adeAnoMesFim != null) {
                    // Usa a data informada por parâmetro
                    prazoFim = DateHelper.toPeriodDate(adeAnoMesFim);
                    if (adePrazo != null) {
                        // Verifica se a data fim passada é consistente com o prazo informado
                        final Date prazoFimTeste = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, prazoIni, adePrazo, adePeriodicidade, responsavel);
                        if (prazoFim.compareTo(prazoFimTeste) != 0) {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.este.lancamento.pois.prazo.e.data.final.informados.sao.inconsistentes", responsavel);
                        }
                    }
                } else if (adePrazo != null) {
                    // Se a autorização tem prazo, calcula a data fim
                    prazoFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, prazoIni, adePrazo, adePeriodicidade, responsavel);
                }

                if ((prazoFim != null) && prazoIni.after(prazoFim)) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.este.lancamento.pois.data.inicial.contrato.maior.data.final", responsavel);
                }

                // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data fim de acordo com essa data.
                calcularDataIniFimMargemExtra(rseCodigo, prazoFim, adeIncMargemSvc, false, true, responsavel);

                // Define o status da autorização
                String sadCodigoAde = CodedValues.SAD_AGUARD_CONF;

                if (sadCodigo != null) {
                    sadCodigoAde = sadCodigo;
                } else if (!TextHelper.isNull(margemParam.getAcao()) && (RENEGOCIAR.equals(margemParam.getAcao()) || COMPRAR.equals(margemParam.getAcao()))) {
                    // Renegociação quando executada via Lote e as consignações renegociadas já estão liquidadas, não passando
                    // pela rotina do RenegociarConsignacaoController onde o sadCodigo é enviado e não entra nesse fluxo
                    if (usuarioPodeConfirmarRenegociacao(responsavel)) {
                        if (!usuarioPodeConfirmarEmDoisPassos(responsavel)) {
                            if (usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, comSerSenha, responsavel)) {
                                sadCodigoAde = CodedValues.SAD_DEFERIDA;
                            } else {
                                sadCodigoAde = CodedValues.SAD_AGUARD_DEFER;
                            }
                        } else {
                            sadCodigoAde = CodedValues.SAD_AGUARD_CONF;
                        }

                    }
                } else if (usuarioPodeConfirmarReserva(responsavel)) {
                    if (!usuarioPodeConfirmarEmDoisPassos(responsavel)) {
                        if (usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, comSerSenha, responsavel)) {
                            sadCodigoAde = CodedValues.SAD_DEFERIDA;
                        } else {
                            sadCodigoAde = CodedValues.SAD_AGUARD_DEFER;
                        }
                    } else {
                        sadCodigoAde = CodedValues.SAD_AGUARD_CONF;
                    }
                }

                if (validaTaxa && ((sadCodigo == null) || !CodedValues.SAD_SOLICITADO.equals(sadCodigo))) {

                    final BigDecimal[] valores = validarTaxaJuros(adeVlr, adeVlrLiquido, adeVlrTac,
                            adeVlrIof, adeVlrMensVinc, adePrazo,
                            DateHelper.toSQLDate(DateHelper.getSystemDate()), prazoIni, svcCodigo, csaCodigo,
                            orgCodigo, false, parametros, adePeriodicidade, rseCodigo, responsavel);

                    // validarTaxaJuros retorna 'new BigDecimal[]{adeVlr, vlrTotal, adeVlrTac, adeVlrIof, adeVlrMensVinc};'
                    // Atualiza o valor de Tac e Iof pelo calculado na rotina de validação. Os demais dados não são alterados
                    adeVlrTac = valores[2];
                    adeVlrIof = valores[3];
                    margemParam.setAdeVlrTac(adeVlrTac);
                    margemParam.setAdeVlrIof(adeVlrIof);
                }

                /*
                 * Se o tipo valor do serviço for percentual, calcula o valor em reais utilizando a base de cálculo do servidor
                 * e preenche o ade_vlr. Altera o tipo_vlr para valor fixo.
                 */
                // Parâmetro de serviço se desconta valor da margem de serviço percentual
                if (CodedValues.TIPO_VLR_PERCENTUAL.equals(adeTipoVlr)) {
                    final CustomTransferObject paramDescontaMargemSvcPercentual = getParametroSvc(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean retemMargemSvcPercentual = ((Boolean) paramDescontaMargemSvcPercentual.getAttribute(Columns.PSE_VLR));
                    if (retemMargemSvcPercentual) {
                        // Recupera o parâmetro que informa qual base de cálculo deve ser usada
                        final CustomTransferObject paramBaseCalcRetencaoSvcPercentual = getParametroSvc(CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL, svcCodigo, "", false, parametros);
                        final String tbcCodigo = (String) paramBaseCalcRetencaoSvcPercentual.getAttribute(Columns.PSE_VLR);

                        // Recupera a base de calculo para ser usada em serviço percentual
                        BigDecimal rseBaseCalculo = null;

                        if (TextHelper.isNull(tbcCodigo) || CodedValues.TBC_PADRAO.equals(tbcCodigo)) {
                            rseBaseCalculo = rseDto.getRseBaseCalculo();
                        } else {
                            rseBaseCalculo = BaseCalcRegistroServidorHome.getBcsValor(rseCodigo, tbcCodigo);
                        }

                        if ((rseBaseCalculo != null) && (rseBaseCalculo.compareTo(BigDecimal.ZERO) > 0)) {
                            // calcula valor folha
                            adeVlrPercentual = adeVlr;
                            adeVlr = (rseBaseCalculo.multiply(adeVlr).divide(new BigDecimal("100")));
                            adeTipoVlr = CodedValues.TIPO_VLR_FIXO;
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.calcular.valor.reserva.pois.base.calculo.invalida", responsavel);
                        }
                    }
                }

                // Define valor real da parcela, baseado no teto de desconto
                BigDecimal vlrParcela = adeVlr;

                if (!validar) {
                    /*
                     * Código do usuário responsável pela operação
                     */
                    final String usuCodigo = (responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);

                    /*
                     * Verifica se adeAnoMesIniRef foi informado, senão, como é uma inserção,
                     * o seu valor será igual ao ade_ano_mes_ini
                     */
                    if (adeAnoMesIniRef == null) {
                        adeAnoMesIniRef = prazoIni;
                    }

                    /*
                     * Verifica se adeAnoMesFimRef foi informado, senão, como é uma inserção,
                     * o seu valor será igual ao ade_ano_mes_fim
                     */
                    if (adeAnoMesFimRef == null) {
                        adeAnoMesFimRef = prazoFim;
                    }

                    /*
                     * Grava o tipo de registro, caso tenha sido informado algum,
                     * senão será setado com o valor CodedValues.COD_REG_DESCONTO
                     */
                    if (TextHelper.isNull(adeCodReg)) {
                        adeCodReg = CodedValues.COD_REG_DESCONTO;
                    }

                    /*
                     * Se o serviço possui controle de saldo devedor, então calcula o
                     * valor do saldo e armazena nas variáveis de controle
                     */
                    BigDecimal adeVlrSdoMov = null;
                    BigDecimal adeVlrSdoRet = null;
                    if (controlaSaldoDevedor) {
                        final BigDecimal saldoDevedor = BigDecimal.valueOf(adeVlr.doubleValue() * (adePrazo != null ? adePrazo.doubleValue() : 1.0)).setScale(2, java.math.RoundingMode.HALF_UP);
                        adeVlrSdoMov = saldoDevedor;
                        adeVlrSdoRet = saldoDevedor;
                    }

                    /*
                     * Grava as informações bancárias caso seja obrigatório no serviço
                     */
                    String adeBanco = null;
                    String adeAgencia = null;
                    String adeConta = null;
                    if (serInfBancariaObrigatoria || !validaDadosBancariosIncAvancada) {
                        adeBanco = banco;
                        adeAgencia = agencia;
                        adeConta = conta;
                    }

                    /*
                     * Se o serviço possui teto de desconto, altera o valor do contrato.
                     */
                    if (possuiControleVlrMaxDesconto) {
                        vlrParcela = this.calcularValorDescontoParcela(rseDto.getRseCodigo(), rseDto.getCrsCodigo(), svcCodigo, adeVlr, parametros);
                    }

                    /*
                     * Define o tipo de taxa utilizada para o contrato, caso tenha valor liberado
                     */
                    String adeTipoTaxa = null;
                    if ((adeVlrLiquido != null) && (adeVlrLiquido.signum() > 0)) {
                        // Parâmetro de sistema se tem simulação por taxa de juros
                        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
                        // Parâmetro de sistema se tem CET habilitado
                        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                        // Parâmetro de serviço se tem cadastro de valor líquido liberado
                        final CustomTransferObject paramVlrLiqTaxaJuros = getParametroSvc(CodedValues.TPS_VLR_LIQ_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);
                        final boolean permiteVlrLiqTxJuros = ((paramVlrLiqTaxaJuros != null) && (paramVlrLiqTaxaJuros.getAttribute(Columns.PSE_VLR) != null) &&
                                ((Boolean) paramVlrLiqTaxaJuros.getAttribute(Columns.PSE_VLR)).booleanValue());

                        if (temCET) {
                            adeTipoTaxa = CodedValues.TIPO_TAXA_CET;
                        } else if (permiteVlrLiqTxJuros || simulacaoPorTaxaJuros) {
                            adeTipoTaxa = CodedValues.TIPO_TAXA_JUROS;
                        } else {
                            adeTipoTaxa = CodedValues.TIPO_TAXA_COEFICIENTE;
                        }
                    }

                    /*
                     * Inclui a nova consignação
                     */
                    final AutDesconto autorizacao = AutDescontoHome.create(sadCodigoAde, vcoCodigo, rseCodigo, corCodigo, usuCodigo,
                            adeIdentificador, adeIndice, adeCodReg, adePrazo, 0, prazoIni,
                            prazoFim, adeAnoMesIniRef, adeAnoMesFimRef, vlrParcela, adeVlrTac,
                            adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros,
                            adeVlrSegPrestamista, adeTipoVlr, adeIntFolha, adeIncMargem,
                            adeCarencia, carenciaFinal, adeDtHrOcorrencia,
                            adeVlrSdoMov, adeVlrSdoRet, adeBanco, adeAgencia, adeConta, adeTipoTaxa,
                            adeVlrPercentual, adePeriodicidade, cidCodigo, cbeCodigo, tlaCodigo);
                    adeCodigoNovo = autorizacao.getAdeCodigo();

                    criarSolicitacaoAutorizacao(adeCodigoNovo, acao, orgCodigo, csaCodigo, svcCodigo, prazoIni, usuCodigo, (!TextHelper.isNull(nomeAnexo) || (margemParam.getAnexo() != null)), responsavel);

                    // Anexo
                    if (!TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(adeCodigoNovo)) {
                        String [] anexosName;
                        File anexo = null;
                        anexosName = nomeAnexo.split(";");

                        final CustomTransferObject paramSvcQntdeMinAnexos = getParametroSvc(CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS, svcCodigo, nomeAnexo, false, parametros);
                        final String strQntdadeMinAnexos = (String) paramSvcQntdeMinAnexos.getAttribute(Columns.PSE_VLR);
                        final Integer qntdadeMinAnexos = !TextHelper.isNull(strQntdadeMinAnexos) ? Integer.valueOf(strQntdadeMinAnexos) : 0;

                        if((qntdadeMinAnexos > 0) && (anexosName.length < qntdadeMinAnexos)) {
                            throw new AutorizacaoControllerException("mensagem.erro.upload.arquivo.qunt.min", responsavel, strQntdadeMinAnexos);
                        }

                        for (final String nomeAnexoCorrente : anexosName) {
                            anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigoNovo, idAnexo, responsavel);
                            if ((anexo != null) && anexo.exists()) {
                                aadDescricao = (!TextHelper.isNull(aadDescricao) && (aadDescricao.length() <= 255)) ? aadDescricao : anexo.getName();
                                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigoNovo, anexo.getName(), aadDescricao, prazoIni, null, responsavel);
                            }
                        }
                    }
                    // Anexa arquivo Soap
                    if ((margemParam.getAnexo() != null) && !TextHelper.isNull(adeCodigoNovo)) {
                        File anexo = margemParam.getAnexo();
                        anexo = UploadHelper.moverArquivoAnexoTemporario(anexo.getName(), adeCodigoNovo, idAnexo, responsavel);
                        if ((anexo != null) && anexo.exists()) {
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigoNovo, anexo.getName(), anexo.getName(), prazoIni, null, responsavel);
                        }
                    }

                    // Verifica se o usuário que está realizando a operação pode incluir consignação no convênio informado.
                    usuarioPodeModificarAde(adeCodigoNovo, false, true, responsavel);

                    // Cria ocorrência de inclusão de reserva
                    ocaCodigo = criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_TARIF_RESERVA, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.inclusao.reserva", responsavel), new BigDecimal("0.00"), adeVlr, null, prazoIni, null, responsavel);

                    // se é reserva de lançamento de cartão, cria relacionamento de autorização com a reserva de cartão de origem
                    if ((adesReservaCartao != null) && !adesReservaCartao.isEmpty()) {
                        for (final TransferObject adeOrigemCartao : adesReservaCartao) {
                            RelacionamentoAutorizacaoHome.create((String) adeOrigemCartao.getAttribute(Columns.ADE_CODIGO), adeCodigoNovo, CodedValues.TNT_CARTAO, responsavel.getUsuCodigo());
                        }
                    }

                    // se parâmetro de sistema que configura um insere/altera, mas que cria um novo contrato ao invés de alterar
                    // criar relacionamento de insere/altera entre novo contrato e o contrato recuperado.
                    if (!TextHelper.isNull(adeCodigoIncAlt)) {
                        RelacionamentoAutorizacaoHome.create(adeCodigoIncAlt, adeCodigoNovo, CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA, responsavel.getUsuCodigo());
                    }

                    // Se contrato na situação SAD_AGUARD_DEFER incluir ocorrência
                    // com prazo para deferimento automático
                    if (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigoAde)) {
                        alertarPrazoDeferimentoAut(autorizacao, svcCodigo, parametros, responsavel);
                    }

                    // Consome a senha de autorização utilizada para reserva.
                    // No caso de (inclusão|alteração) avançada, se o usuário possui permissão de (inclusão|alteracao) avançada, o
                    // boolean validaSenhaServidor(Inc|Alt)Avancada possui o valor indicado nas opções avançadas,
                    // se o usuário não possui permissão, este boolean possui o valor padrão (true atualmente).
                    if (margemParam.getConsomeSenha() && validaSenhaServidorIncAvancada && validaSenhaServidorAltAvancada) {
                        consumirSenhaDeAutorizacao(adeCodigoNovo, sadCodigoAde, rseCodigo, svcCodigo, csaCodigo, margemParam.getSerSenha(), true, true, false, false, responsavel);
                    }

                    // Se a reserva foi feita com uso de senha do servidor.
                    if (comSerSenha) {
                        // Cria ocorrência de autorização do servidor.
                        String obs;
                        String tocCodigo = CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR;
                        if (responsavel.isSer()) {
                            obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.solicitacao.responsavel.arg0", responsavel, margemParam.getNomeResponsavel());

                            // Se é o próprio servidor que está inserindo a reserva e o sistema utiliza
                            // senha de autorização, então não podemos considerar que a reserva foi realizada
                            // mediante senha de servidor, já que a senha de autorização não foi utilizada ainda.
                            // No entanto, se o sistema só trabalha com senha de consulta, consideramos que a reserva foi
                            // mediante senha, pois o usuário utilizou a senha de consulta para logar no sistema e
                            // fazer a solicitação.
                            if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, responsavel)) {
                                tocCodigo = CodedValues.TOC_INFORMACAO;
                            }
                        } else {
                            obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pela.senha.do.servidor", responsavel);
                        }
                        criaOcorrenciaADE(adeCodigoNovo, tocCodigo, obs, null, null, null, ocaPeriodo, null, responsavel);
                    }

                    // Se reserva foi realizada com tela de confirmacao de duplicidade
                    if (chkConfirmarDuplicidade) {
                        criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_ADE_DUPLICADO_MOTIVADO_USUARIO, motivoOperacaoObsDuplicidade, null, null, null, ocaPeriodo, motivoOperacaoCodigoDuplicidade, responsavel);
                    }

                    if ((margemParam.getAceitoTermoUsoColetaDados() != null) && margemParam.getAceitoTermoUsoColetaDados()) {
                        criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_ACEITACAO_TERMO_DE_USO, ApplicationResourcesHelper.getMessage("mensagem.termo.de.consentimento.coleta.dados.servidor.ocorrencia", responsavel), null, null, null, ocaPeriodo, null, responsavel);
                    }
                }

                if(ocorrenciaInformacaoDataInicioMargem) {
                    criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior", responsavel), responsavel);
                }

                BigDecimal limite = new BigDecimal("0.00");
                final CustomTransferObject param = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, BigDecimal.ZERO, false, parametros);
                if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
                    try {
                        limite = new BigDecimal(param.getAttribute(Columns.PSE_VLR).toString());
                    } catch (final NumberFormatException ex) {
                        LOG.debug("Valor do parametro de limite de ade sem margem invalido: " + param.getAttribute(Columns.PSE_VLR));
                    }
                }

                final boolean exibeMargemDisponivelCriticaCartao = ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_DISPONIVEL_CRITICA_CARTAO, CodedValues.TPC_SIM, responsavel);
                if (validar) {
                    if (exibeMargemDisponivelCriticaCartao && (adesReservaCartao != null) && !adesReservaCartao.isEmpty()) {
                        for (final TransferObject adeOrigemCartao : adesReservaCartao) {
                            final Short adeIncideMargem = (Short) adeOrigemCartao.getAttribute(Columns.ADE_INC_MARGEM);
                            final MargemTO marTO = MargemHelper.getInstance().getMargem(adeIncideMargem, responsavel);
                            final ExibeMargem exibeMargem = new ExibeMargem(marTO, responsavel);
                            if (exibeMargem.isExibeValor()) {
                                BigDecimal margemRest = obtemMargemRestante(rseDto, adeIncideMargem, csaCodigo, true, adeCodigosRenegociacao, responsavel);
                                margemRest = ((margemRest.doubleValue() < 0) && !exibeMargem.isSemRestricao()) ? new BigDecimal("0.00") : margemRest;
                                final String mensagemMargemCartao = ApplicationResourcesHelper.getMessage("mensagem.erro.valor.margem.cartao.servidor.arg0", responsavel, NumberHelper.format(margemRest.doubleValue(), NumberHelper.getLang(), true));
                                throw new AutorizacaoControllerException("mensagem.lote.inclusao.validada.arg0", responsavel, mensagemMargemCartao);
                            }
                        }
                    } else {
                        verificaMargem(rseDto, adeIncMargem, vlrParcela.subtract(limite), csaCodigo, svcCodigo, true, adeCodigosRenegociacao, responsavel);
                    }

                } else {
                    try {
                        verificaMargem &= validaMargemIncAvancada;

                        // Tenta reservar na margem o valor total da autorização
                        // DESENV-16874: Quando o serviço é para reserva de plano de saúde e o parâmetro de serviço 312 (Desconto via boleto) está habilitado e o servidor optou por pagar por boleto
                        // então não se deve consumir a margem do servidor de maneira nenhuma é um contrato que está registrado no sistema sem nenhum tipo de validação ou incidência de margem
                        if(consumirMargem) {
                            atualizaMargem(rseCodigo, adeIncMargem, vlrParcela, verificaMargem, true, serAtivoPodeReservarMargem, ocaCodigo, csaCodigo, svcCodigo, adeCodigosRenegociacao, responsavel);
                        }
                    } catch (final AutorizacaoControllerException ex) {
                        /*
                         * Se não foi possível reservar todo o valor, verifica se o serviço tem
                         * limite para autorizações sem margem. Verifica também se não é um compulsório,
                         * e caso positivo, pesquisa o vlr total dos contratos que podem ser estocados
                         * liberando assim vlr de margem para a inclusão
                         */
                        atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, adeCodigoNovo, ocaCodigo, vlrParcela, limite, adeIncMargem, parametros, true, serAtivoPodeReservarMargem, false, responsavel);
                    }

                    /*
                     * Se é serviço que possui correção de valor presente, verifica se deve
                     * ser criada ocorrência registrando o valor original e a data do evento
                     * da correção do valor presente.
                     */
                    final CustomTransferObject paramSvcCorrecaoVlrPresente = getParametroSvc(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean possuiCorrecaoVlrPresente = ((paramSvcCorrecaoVlrPresente != null) && (paramSvcCorrecaoVlrPresente.getAttribute(Columns.PSE_VLR) != null) &&
                            ((Boolean) paramSvcCorrecaoVlrPresente.getAttribute(Columns.PSE_VLR)).booleanValue());
                    if (possuiCorrecaoVlrPresente && (margemParam.getAdeVlrOriginal() != null)) {
                        final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.correcao.valor.presente.valor.original.arg0.data.evento.arg1", responsavel,
                                NumberHelper.reformat(margemParam.getAdeVlrOriginal().toString(), "en", NumberHelper.getLang()),
                                DateHelper.toDateString(new Date(adeDtHrOcorrencia.getTime())));
                        criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_INFORMACAO, ocaObs, null, null, null, ocaPeriodo, null, responsavel);
                    }

                    /*
                     * Se é contrato com correção de saldo devedor, insere o contrato de correção relacionado
                     * ao novo contrato criado, caso o serviço possua correção em serviço diferente.
                     */
                    final CustomTransferObject paramSvcCorrecaoSaldo = getParametroSvc(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR, svcCodigo, "", false, parametros);
                    final boolean corrigeSaldoDevedorOutroServico = ((paramSvcCorrecaoSaldo != null) && (paramSvcCorrecaoSaldo.getAttribute(Columns.PSE_VLR) != null) &&
                            CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO.equals(paramSvcCorrecaoSaldo.getAttribute(Columns.PSE_VLR).toString()));
                    if (corrigeSaldoDevedorOutroServico && (margemParam.getAdeVlrCorrecao() != null)) {
                        inserirCorrecaoOutroServico(adeCodigoNovo, sadCodigoAde, svcCodigo, rseCodigo, orgCodigo, csaCodigo, corCodigo, margemParam.getAdeVlrCorrecao(), adePeriodicidade, responsavel);
                    }

                    BigDecimal taxa = null;

                    // Grava dados de simulação, caso necessário
                    if (validaTaxaJurosIncAvancada) {
                        taxa = gravarDadosSimulacao(margemParam, adeCodigoNovo, sadCodigoAde, adePeriodicidade, csaCodigo, svcCodigo, orgCodigo, responsavel);
                    }

                    // Se é solicitação de contrato pelo simulador, e tiver módulo de leilão habilitado, inclui registro
                    // de solicitação de propostas para as demais consignatárias informarem propostas.
                    if (temLeilaoSimulacao &&
                            responsavel.isSer() && CodedValues.SAD_SOLICITADO.equals(sadCodigoAde) && margemParam.isIniciarLeilaoReverso()
                            && (taxa != null) && (taxa.compareTo(BigDecimal.ZERO) == 1)) {
                        leilaoSolicitacaoController.iniciarProcessoLeilao(adeCodigoNovo, rseCodigo, responsavel);
                        leilaoSolicitacaoController.informarPropostaLeilaoSolicitacao(adeCodigoNovo, svcCodigo, csaCodigo, taxa, false, rseCodigo, responsavel);
                    }

                    // Tipo motivo da operação é obrigatório para inclusão de reserva de margem avançada
                    // reserva oriunda de reimplante de capital devido não exige motivo de operação
                    if (usuPossuiIncAvancadaAde && TextHelper.isNull(margemParam.getTmoCodigo()) && inclusaoAvancada && !margemParam.isReimpCapitalDevido() && !margemParam.isRetencaoVerbaRescisoria()) {
                        throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
                    }

                    // Insere tipo motivo da operacação
                    CustomTransferObject tmo = null;
                    if (!TextHelper.isNull(margemParam.getTmoCodigo())) {
                        try {
                            tmo = new CustomTransferObject();
                            tmo.setAttribute(Columns.ADE_CODIGO, adeCodigoNovo);
                            tmo.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                            tmo.setAttribute(Columns.OCA_PERIODO, ocaPeriodo);
                            tmo.setAttribute(Columns.TMO_CODIGO, margemParam.getTmoCodigo());
                            tmo.setAttribute(Columns.OCA_OBS, margemParam.getOcaObs());
                            tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tmo, responsavel);
                        } catch (final TipoMotivoOperacaoControllerException e) {
                            if (usuPossuiIncAvancadaAde && inclusaoAvancada) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.motivo.operacao.para.consignacao", responsavel, e);
                            } else {
                                LOG.error("Não foi possível inserir motivo da operação para a consignação: ['" + adeCodigoNovo + "']. Motivo da operação: " + margemParam.getTmoCodigo(), e);
                            }
                        }
                    }

                    // grava dados de autorização, se preenchidos
                    if (!TextHelper.isNull(modalidadeOp)) {
                        setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_MODALIDADE_OPERACAO, modalidadeOp, responsavel);
                    }
                    if (!TextHelper.isNull(matriculaSerCsa)) {
                        setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_MATRICULA_SER_NA_CSA, matriculaSerCsa, responsavel);
                    }

                    // Verifica se exige cadastro de telefone na solicitação, exceto para solicitação de benefícios
                    if (!isReservaContratoBeneficio) {
                        final boolean exigeTelefone = CodedValues.SAD_SOLICITADO.equals(sadCodigoAde) &&
                                (ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel));
                        if (exigeTelefone && TextHelper.isNull(margemParam.getTdaTelSolicitacaoSer())) {
                            throw new AutorizacaoControllerException("mensagem.informe.servidor.telefone.solicitacao", responsavel);
                        }
                    }

                    if (!TextHelper.isNull(margemParam.getTdaTelSolicitacaoSer())) {
                        setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR, margemParam.getTdaTelSolicitacaoSer(), responsavel);
                    }

                    // Grava os dados de autorização, de forma genérica
                    if (!margemParam.getDadosAutorizacaoMap().isEmpty()) {
                        final List<TransferObject> dadList = lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST_LOTE_WEB, svcCodigo, csaCodigo, responsavel);
                        for (final TransferObject dad : dadList) {
                            final String tdaCodigo = (String) dad.getAttribute(Columns.TDA_CODIGO);
                            if (margemParam.existsDadoAutorizacao(tdaCodigo)) {
                                setDadoAutDesconto(adeCodigoNovo, tdaCodigo, margemParam.getDadoAutorizacao(tdaCodigo), responsavel);
                            }
                        }
                        //Insert na dados criado fora do loop pois são dados adicionais que não estão visiveis para alteração
                        if(margemParam.existsDadoAutorizacao(CodedValues.TDA_BENEFICIARIO_DEPENTENTE)) {
                            final String codigoDependente = margemParam.getDadoAutorizacao(CodedValues.TDA_BENEFICIARIO_DEPENTENTE);
                            final Beneficiario beneficiario = beneficiarioController.buscaBeneficiarioBfcCodigo(codigoDependente);

                            setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_BENEFICIARIO_DEPENTENTE, !TextHelper.isNull(beneficiario.getBfcIdentificador()) ? beneficiario.getBfcIdentificador() : beneficiario.getBfcCodigo(), responsavel);
                        }

                        if(margemParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)) {
                            setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_FORMA_PAGAMENTO, margemParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO), responsavel);
                        }
                    }

                    if (ParamSist.paramEquals(CodedValues.TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE, CodedValues.TPC_SIM, responsavel)) {
                        final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKeyForUpdate(rseCodigo);
                        if (!CodedValues.SRS_PENDENTE.equals(registroServidor.getStatusRegistroServidor().getSrsCodigo())) {
                            // Observação da ocorrência de alteração do status
                            final String orsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.alterado.de.arg0.para.arg1", responsavel, registroServidor.getStatusRegistroServidor().getSrsCodigo(), CodedValues.SRS_PENDENTE);
                            // Altera o status do registro servidor
                            registroServidor.setStatusRegistroServidor(new StatusRegistroServidor(CodedValues.SRS_PENDENTE));
                            AbstractEntityHome.update(registroServidor);
                            // Cria ocorrência de alteração dos dados cadastrais
                            OcorrenciaRegistroServidorHome.create(rseCodigo, CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS, responsavel.getUsuCodigo(), orsObs, responsavel.getIpUsuario(), null);
                        }
                    }

                    // Gera o Log de auditoria
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.RESERVAR_MARGEM, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigoNovo);
                    log.setRegistroServidor(rseCodigo);
                    log.setVerbaConvenio(vcoCodigo);
                    log.setConvenio(cnvCodigo);
                    log.setConsignataria(csaCodigo);
                    log.setCorrespondente(corCodigo);
                    log.setStatusAutorizacao(sadCodigoAde);
                    log.addChangedField(Columns.ADE_VLR, adeVlr);
                    log.addChangedField(Columns.ADE_PRAZO, adePrazo);
                    log.addChangedField(Columns.ADE_CARENCIA, adeCarencia);
                    log.addChangedField(Columns.ADE_INC_MARGEM, adeIncMargem);
                    log.addChangedField(Columns.CNV_COD_VERBA, cnvCodVerba);
                    log.addChangedField(Columns.ADE_ANO_MES_INI, margemParam.getAdeAnoMesIni());
                    log.addChangedField(Columns.ADE_INDICE, adeIndice);

                    String incAvancada = "";
                    if (!validaMargemIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.margem.selecionada", responsavel);
                    }
                    if (!validaTaxaJurosIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.taxa.juros.selecionada", responsavel);
                    }
                    if (!validaPrazoIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.prazo.selecionada", responsavel);
                    }
                    if (!validaDadosBancariosIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.dados.bancarios.selecionada", responsavel);
                    }
                    if (!validaSenhaServidorIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.senha.servidor.selecionada", responsavel);
                    }
                    if (!validaBloqSerCnvCsaIncAvancada) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.bloqueio.servidor.convenio.consigataria.selecionada", responsavel);
                    }
                    if (!validaDataNascAvancado) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.data.nascimento.selecionada", responsavel);
                    }
                    if (!validaLimiteContrato) {
                        incAvancada += ApplicationResourcesHelper.getMessage("mensagem.informacao.opcao.avancada.nao.valida.limite.autorizacao.desconto.selecionada", responsavel);
                    }

                    if (!TextHelper.isNull(margemParam.getTmoCodigo())) {
                        final String tmoDescricao = tipoMotivoOperacaoController.findMotivoOperacao(margemParam.getTmoCodigo(), responsavel).getTmoDescricao();

                        incAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.inclusao.avancada.tmoCodigo", responsavel) + ": " + (TextHelper.isNull(margemParam.getTmoCodigo()) ? "" : tmoDescricao);
                        incAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.inclusao.avancada.adeObs", responsavel) + ": " + (TextHelper.isNull(margemParam.getOcaObs()) ? "" : margemParam.getOcaObs());
                    }

                    if (!TextHelper.isNull(incAvancada)) {
                        log.add(incAvancada);
                    }

                    log.write();

                    if (inclusaoAvancada) {
                        criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_INCLUSAO_AVANCADA_CONTRATO, incAvancada, null, null, null, ocaPeriodo, margemParam.getTmoCodigo(), responsavel);
                    }

                    // Cria o registro de decisão judicial, caso informado e o sistema permita
                    boolean temDecisaoJudicial = false;
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                        if (!TextHelper.isNull(margemParam.getTjuCodigo()) && !TextHelper.isNull(margemParam.getDjuTexto()) && !TextHelper.isNull(margemParam.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                            DecisaoJudicialHome.create(ocaCodigo, margemParam.getTjuCodigo(), margemParam.getCidCodigo(), margemParam.getDjuNumProcesso(), margemParam.getDjuData(), margemParam.getDjuTexto(), null);
                            temDecisaoJudicial = true;
                        }
                    }

                    if (responsavel.isCseSupOrg() && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo()) && (inclusaoAvancada || temDecisaoJudicial)) {
                        setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel);
                    }

                    // Finaliza o processo de reserva de margem.
                    finalizarReservaMargem(adeCodigoNovo, svcCodigo, validar, responsavel);

                    // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                    // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                    OperacaoEConsigEnum opEnum = OperacaoEConsigEnum.RESERVAR_MARGEM;

                    if (!TextHelper.isNull(sadCodigo) && CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                        opEnum = OperacaoEConsigEnum.INSERIR_SOLICITACAO;
                    }

                    final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(opEnum, adeCodigoNovo, null, null, responsavel);
                    //processoEmail.start(); // DÁ ERRO NO ORACLE, POSSIVELMENTE PORQUE SERÁ UM PROCESSO SEPARADO E NÃO TERÁ ACESSO AO ESCOPO DE TRANSAÇÃO
                    processoEmail.run();

                    // se parâmetro 463 estiver com valor 'S' envia email para usuário cse do sistema na inclusão de contrato *aguardando deferimento*
                    // geralmente estará setado como 'S' para o eConsig Light. O email também será enviado caso o contrato esteja como "aguardando confirmação" e o
                    // status do usuário, "pendente".
                    if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CSE_NOVO_CONTRATO, CodedValues.TPC_SIM, responsavel) && (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigoAde) || (CodedValues.SRS_PENDENTE.equals(rseDto.getSrsCodigo()) && CodedValues.SAD_AGUARD_CONF.equals(sadCodigoAde)))) {
                        EnviaEmailHelper.enviarEmailCseInclusaoAde(adeCodigoNovo, OperacaoEConsigEnum.RESERVAR_MARGEM.getOperacao(), responsavel);
                    }

                    checaPermissaoEnviaNotificacaoSerReservaMargem(rseDto, svcCodigo, csaCodigo, responsavel);

                    if (ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_MINIMA_CONTRATOS_CSA_NO_DIA_NOTIFICAR_GESTOR, 0, responsavel) > 0) {
                        final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                        if (paramSvcCse.isTpsConsideradoInclusaoCsaPorDia()) {
                            ContabilizacaoInclusaoContratos.CONTABILIZACAOCONTRATOS.contabilizarInclusao(csaCodigo);
                        }
                    }

                    if(margemParam.isMobileEconsig()) {
                    	TransferObject boleto;
                        final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                        // envia email somente se for boleto externo
                        if ((adeCodigoNovo != null) && paramSvcCse.isTpsBuscaBoletoExterno()) {
                        	boleto =  buscaNovaAutorizacao(adeCodigoNovo, rseDto.getSerCodigo() , orgCodigo, responsavel);
                            EnviaEmailHelper.enviaBoleto(boleto, responsavel);
                        }
                    }
                }
            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                if (ex instanceof ZetraException) {
                    throw new AutorizacaoControllerException(ex);
                }
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        return adeCodigoNovo;
    }

    /**
     * cria solicitação de autorização de gestor se parâmetro 880 = S, ou uma solicitação de crêdito eletrônico esperando assinatura digital
     * caso o parâmetro de sistema por consignatária 251 = S
     * @param adeCodigoNovo
     * @param acao
     * @param orgCodigo
     * @param csaCodigo
     * @param svcCodigo
     * @param prazoIni
     * @param usuCodigo
     * @param temAnexo
     * @param responsavel
     * @throws CreateException
     * @throws PeriodoException
     * @throws ParametroControllerException
     * @throws FindException
     */
    private void criarSolicitacaoAutorizacao(String adeCodigoNovo, String acao, String orgCodigo, String csaCodigo, String svcCodigo, Date prazoIni, String usuCodigo, boolean temAnexo, AcessoSistema responsavel) throws CreateException, PeriodoException, ParametroControllerException, FindException  {
        String origemSolicitacao = null;
        if ((acao == null) || RESERVAR.equalsIgnoreCase(acao)) {
            origemSolicitacao = OrigemSolicitacaoEnum.ORIGEM_INCLUSAO.getCodigo();
        } else {
            origemSolicitacao = COMPRAR.equals(acao) ? OrigemSolicitacaoEnum.ORIGEM_COMPRA.getCodigo() : OrigemSolicitacaoEnum.ORIGEM_RENEGOCIACAO.getCodigo();
        }

        if (ParamSist.paramEquals(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, CodedValues.TPC_SIM, responsavel)) {
            SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigoNovo, usuCodigo, TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
                    StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, null, null, origemSolicitacao, ((acao == null) || RESERVAR.equalsIgnoreCase(acao)) ?
                            PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel) : prazoIni);

            return;
        }

        if (temAnexo) {
            final List<TransferObject> lstParamSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, List.of(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES), true, responsavel)
                    .stream().filter(paramSvcCsa -> {
                        final String pscVlr = (String) paramSvcCsa.getAttribute(Columns.PSC_VLR);
                        final String pscVlrRef = (String) paramSvcCsa.getAttribute(Columns.PSC_VLR_REF);

                        return ((CodedValues.PSC_BOOLEANO_SIM.equals(pscVlr)) || ((pscVlrRef != null) && CodedValues.PSC_BOOLEANO_SIM.equals(pscVlr)));
                    }).toList();

            if (!lstParamSvcCsa.isEmpty()) {
                SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigoNovo, usuCodigo, TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo(),
                        StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), null, null, null, origemSolicitacao, ((acao == null) || RESERVAR.equalsIgnoreCase(acao)) ?
                                PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel) : prazoIni);
            }
        }
    }

    @Override
    public List<String> reservarMargemGap(String rseCodigo, String orgCodigo, String cnvCodigo, String svcCodigo, String corCodigo,
                                          List<Short> marCodigos, String adeIdentificador, String serSenha, boolean comSerSenha,
                                          String numAgencia, String numBanco, String numConta,
                                          AcessoSistema responsavel) throws AutorizacaoControllerException {

        Short adeIncMargem = null;
        Short adeIntFolha = null;
        String adeTipoVlr = null;
        Integer paramMesInicioDesconto = null;

        try {
            // Parâmetros de serviço necessários
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_INCIDE_MARGEM);
            tpsCodigos.add(CodedValues.TPS_INTEGRA_FOLHA);
            tpsCodigos.add(CodedValues.TPS_TIPO_VLR);
            tpsCodigos.add(CodedValues.TPS_MES_INICIO_DESCONTO_GAP);

            final ListaTodosParamSvcCseQuery query = new ListaTodosParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.tpsCodigos = tpsCodigos;
            query.responsavel = responsavel;
            final List<TransferObject> parSvcCse = query.executarDTO();
            final Iterator<TransferObject> itParSvcCse = parSvcCse.iterator();
            TransferObject prox = null;
            while (itParSvcCse.hasNext()) {
                prox = itParSvcCse.next();
                if (CodedValues.TPS_INCIDE_MARGEM.equals(prox.getAttribute(Columns.TPS_CODIGO))) {
                    adeIncMargem = ((prox.getAttribute(Columns.PSE_VLR) != null) && !"".equals(prox.getAttribute(Columns.PSE_VLR))) ? Short.valueOf(prox.getAttribute(Columns.PSE_VLR).toString()) : CodedValues.INCIDE_MARGEM_SIM;
                } else if (CodedValues.TPS_INTEGRA_FOLHA.equals(prox.getAttribute(Columns.TPS_CODIGO))) {
                    adeIntFolha = ((prox.getAttribute(Columns.PSE_VLR) != null) && !"".equals(prox.getAttribute(Columns.PSE_VLR).toString())) ? Short.valueOf(prox.getAttribute(Columns.PSE_VLR).toString()) : CodedValues.INTEGRA_FOLHA_SIM;
                } else if (CodedValues.TPS_TIPO_VLR.equals(prox.getAttribute(Columns.TPS_CODIGO))) {
                    adeTipoVlr = ((prox.getAttribute(Columns.PSE_VLR) != null) && !"".equals(prox.getAttribute(Columns.PSE_VLR).toString())) ? prox.getAttribute(Columns.PSE_VLR).toString() : CodedValues.TIPO_VLR_FIXO;
                } else if (CodedValues.TPS_MES_INICIO_DESCONTO_GAP.equals(prox.getAttribute(Columns.TPS_CODIGO))) {
                    paramMesInicioDesconto = ((prox.getAttribute(Columns.PSE_VLR) != null) && !"".equals(prox.getAttribute(Columns.PSE_VLR))) ? Integer.valueOf(prox.getAttribute(Columns.PSE_VLR).toString()) : null;
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        try {
            final List<String> adeCodigos = new ArrayList<>();

            String adeCodigo = null;
            Short marCodigo = null;
            java.util.Date adeAnoMesIni = null;
            BigDecimal adeVlr = null;
            final Integer adePrazo = 1;
            final Integer adeCarencia = 0;
            boolean primeiro = true;

            final List<TransferObject> lstMargem = GAPHelper.lstMargemReservaGap(rseCodigo, orgCodigo, adeIncMargem, marCodigos, paramMesInicioDesconto, responsavel);
            final Iterator<TransferObject> itMargem = lstMargem.iterator();
            TransferObject margem = null;
            while (itMargem.hasNext()) {
                margem = itMargem.next();
                marCodigo = Short.valueOf(margem.getAttribute(Columns.MAR_CODIGO).toString());
                adeAnoMesIni = (java.util.Date) margem.getAttribute(Columns.ADE_ANO_MES_INI);
                adeVlr = new BigDecimal(NumberHelper.reformat(margem.getAttribute(Columns.ADE_VLR).toString(), NumberHelper.getLang(), "en"));

                // Cria objeto de parâmetro de reserva
                final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

                reservaParam.setRseCodigo(rseCodigo);
                reservaParam.setAdeVlr(adeVlr);
                reservaParam.setCorCodigo(corCodigo);
                reservaParam.setAdePrazo(adePrazo);
                reservaParam.setAdeAnoMesIni(adeAnoMesIni);
                reservaParam.setAdeCarencia(adeCarencia);
                reservaParam.setAdeIdentificador(adeIdentificador);
                reservaParam.setCnvCodigo(cnvCodigo);
                reservaParam.setSerSenha(serSenha);
                reservaParam.setComSerSenha(comSerSenha);
                reservaParam.setAdeTipoVlr(adeTipoVlr);
                reservaParam.setAdeIntFolha(adeIntFolha);
                reservaParam.setAdeIncMargem(marCodigo);
                reservaParam.setValidar(Boolean.FALSE);
                reservaParam.setPermitirValidacaoTaxa(Boolean.FALSE);
                reservaParam.setSerAtivo(Boolean.TRUE);
                reservaParam.setCnvAtivo(Boolean.TRUE);
                reservaParam.setSerCnvAtivo(Boolean.TRUE);
                reservaParam.setSvcAtivo(Boolean.TRUE);
                reservaParam.setCsaAtivo(Boolean.TRUE);
                reservaParam.setOrgAtivo(Boolean.TRUE);
                reservaParam.setEstAtivo(Boolean.TRUE);
                reservaParam.setCseAtivo(Boolean.TRUE);
                reservaParam.setAdeBanco(numBanco);
                reservaParam.setAdeAgencia(numAgencia);
                reservaParam.setAdeConta(numConta);
                reservaParam.setAcao(RESERVAR);

                // Nos casos de reserva de margem GAP, onde serão feitas múltiplas
                // reservas de margem, somente a primeira deve consumir senha de
                // autorização, já que as demais são incluídas com a mesma senha.
                if (!primeiro) {
                    reservaParam.setConsomeSenha(Boolean.FALSE);
                } else {
                    primeiro = false;
                }

                // Grava a reserva de margem
                adeCodigo = reservarMargem(reservaParam, responsavel);

                // Adiciona no resultado da operação o código do contrato
                adeCodigos.add(adeCodigo);
            }

            return adeCodigos;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof AutorizacaoControllerException) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private void inserirCorrecaoOutroServico(String adeCodigoOrigem, String sadCodigoOrigem, String svcCodigoOrigem, String rseCodigo,
                                             String orgCodigo, String csaCodigo, String corCodigo, BigDecimal adeVlr, String adePeriodicidade,
                                             AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String svcCodigo = parametroController.getServicoCorrecao(svcCodigoOrigem, responsavel).getAttribute(Columns.SVC_CODIGO).toString();
            final CustomTransferObject convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);

            final String cnvCodigo = convenio != null ? (String) convenio.getAttribute(Columns.CNV_CODIGO) : null;

            if (cnvCodigo == null) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.criar.relacionamento.de.correcao.nao.encontrado.servico.de.correcao.ativo", responsavel);
            }

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final Short adeIncMargem = paramSvcCse.getTpsIncideMargem();
            final Short adeIntFolha  = paramSvcCse.getTpsIntegraFolha();
            final String adeTipoVlr  = paramSvcCse.getTpsTipoVlr();
            final Integer adePrazo   = 1;

            // Cria objeto de parâmetro de reserva
            final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

            reservaParam.setRseCodigo(rseCodigo);
            reservaParam.setAdeVlr(adeVlr);
            reservaParam.setCorCodigo(corCodigo);
            reservaParam.setAdePrazo(adePrazo);
            reservaParam.setAdeCarencia(0);
            reservaParam.setAdeIdentificador("");
            reservaParam.setCnvCodigo(cnvCodigo);
            reservaParam.setSadCodigo(sadCodigoOrigem);
            reservaParam.setSerSenha(null);
            reservaParam.setComSerSenha(Boolean.FALSE);
            reservaParam.setAdeTipoVlr(adeTipoVlr);
            reservaParam.setAdeIntFolha(adeIntFolha);
            reservaParam.setAdeIncMargem(adeIncMargem);
            reservaParam.setAdePeriodicidade(adePeriodicidade);
            reservaParam.setValidar(Boolean.FALSE);
            reservaParam.setPermitirValidacaoTaxa(Boolean.FALSE);
            reservaParam.setSerAtivo(Boolean.FALSE);
            reservaParam.setCnvAtivo(Boolean.FALSE);
            reservaParam.setSerCnvAtivo(Boolean.FALSE);
            reservaParam.setSvcAtivo(Boolean.FALSE);
            reservaParam.setCsaAtivo(Boolean.FALSE);
            reservaParam.setOrgAtivo(Boolean.FALSE);
            reservaParam.setEstAtivo(Boolean.FALSE);
            reservaParam.setCseAtivo(Boolean.FALSE);
            reservaParam.setAcao("CORRECAO_SALDO");

            final String adeCodigoCorrecao = reservarMargem(reservaParam, responsavel);

            // Insere relacionamento de correção
            RelacionamentoAutorizacaoHome.create(adeCodigoOrigem, adeCodigoCorrecao, CodedValues.TNT_CORRECAO_SALDO, responsavel.getUsuCodigo());

        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.interno.nao.possivel.criar.relacionamento.de.correcao", responsavel, ex );
        } catch (ParametroControllerException | ConvenioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String getVerbaConvenio(String cnvCodigo, Map<String, Object> parametros) throws AutorizacaoControllerException {
        if ((parametros != null) && parametros.containsKey(Columns.VCO_CODIGO)) {
            return parametros.get(Columns.VCO_CODIGO).toString();
        }
        try {
            return VerbaConvenioHome.findAtivoByConvenio(cnvCodigo).getVcoCodigo();
        } catch (final FindException e) {
            LOG.debug("Erro em getVerbaConvenio, FinderException: " + e.getMessage());
            throw new AutorizacaoControllerException("mensagem.convenioNaoEncontrado", (AcessoSistema) null);
        }
    }

    /**
     * Metodo para realizar a operação Insere/Altera: se o registro do servidor possuir um contrato
     * cadastrado, deferido ou em andamento, o sistema irá gerar uma alteração neste contrato. O
     * parâmetro insereAlteraMesmoPeriodo indica se o contrato a ser alterado deve ser do mesmo
     * período, de acordo com o parâmetro de serviço TPS_INCLUI_ALTERANDO_MESMO_PERIODO, ou se
     * o contrato pode ser de qualquer período de acordo com o parâmetro TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO.
     */
    private String verificaInclusaoAlteracao(String rseCodigo, String tmoCodigo, String cnvCodigo, String svcCodigo, String orgCodigo,
                                             BigDecimal adeVlr, Integer adePrazo, Integer adeCarencia, String adePeriodicidade, String adeIdentificador, String adeIndice,
                                             BigDecimal adeVlrTac, BigDecimal adeVlrIof, BigDecimal adeVlrLiquido, BigDecimal adeVlrMensVinc, BigDecimal adeTaxaJuros, java.util.Date adeAnoMesIni, java.util.Date adeAnoMesFim,
                                             boolean validar, boolean serCnvAtivo, boolean serAtivo, boolean validaBloqueado, boolean cnvAtivo, boolean svcAtivo,
                                             boolean csaAtivo, boolean orgAtivo, boolean estAtivo, boolean cseAtivo, java.sql.Date ocaPeriodo, Map<String, Object> parametros, ReservarMargemParametros reservaParam, AcessoSistema responsavel) throws AutorizacaoControllerException {

        String adeCodigo = null;
        final TransferObject adeInsereAltera = getContratoInsereAltera(rseCodigo, cnvCodigo, svcCodigo, orgCodigo, adePrazo, adeCarencia, adePeriodicidade, adeIdentificador, parametros, responsavel);
        if (adeInsereAltera != null) {
            final BigDecimal ocaAdeVlrAnt = new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR).toString());
            adeVlr = adeVlr.add(new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR).toString()));
            adeCodigo = adeInsereAltera.getAttribute(Columns.ADE_CODIGO).toString();
            if ((adeVlrTac != null) && (adeInsereAltera.getAttribute(Columns.ADE_VLR_TAC) != null)) {
                adeVlrTac = adeVlrTac.add(new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR_TAC).toString()));
            }
            if ((adeVlrIof != null) && (adeInsereAltera.getAttribute(Columns.ADE_VLR_IOF) != null)) {
                adeVlrIof = adeVlrIof.add(new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR_IOF).toString()));
            }
            if ((adeVlrLiquido != null) && (adeInsereAltera.getAttribute(Columns.ADE_VLR_LIQUIDO) != null)) {
                adeVlrLiquido = adeVlrLiquido.add(new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR_LIQUIDO).toString()));
            }
            if ((adeVlrMensVinc != null) && (adeInsereAltera.getAttribute(Columns.ADE_VLR_MENS_VINC) != null)) {
                adeVlrMensVinc = adeVlrMensVinc.add(new BigDecimal(adeInsereAltera.getAttribute(Columns.ADE_VLR_MENS_VINC).toString()));
            }

            // insereAlteraUsandoMaiorPrazo : True, se o serviço é do tipo Insere/Altera, usa o maior prazo
            final CustomTransferObject paramIncAltUsaMaiorPrazo = getParametroSvc(CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO, svcCodigo, Boolean.FALSE, false, parametros);
            final boolean insereAlteraUsandoMaiorPrazo = ((paramIncAltUsaMaiorPrazo != null) && (paramIncAltUsaMaiorPrazo.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIncAltUsaMaiorPrazo.getAttribute(Columns.PSE_VLR)).booleanValue());

            final Integer adePrazoAtual = (Integer) adeInsereAltera.getAttribute(Columns.ADE_PRAZO);
            final Integer adePrdPagas = adeInsereAltera.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) adeInsereAltera.getAttribute(Columns.ADE_PRD_PAGAS) : 0;

            Integer adePrazoFinal = null;

            // Insere/altera não pode ter carência
            if ((adeCarencia != null) && (adeCarencia > 0)){
                throw new AutorizacaoControllerException("mensagem.erro.prazo.carencia.deve.ser.igual.arg0", responsavel, "0");
            }

            try {
                // Determina o período inicial da autorização
                Date prazoIni = null;
                if (adeAnoMesIni == null) {
                    // Pega o período atual
                    prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, ocaPeriodo, adeCarencia, adePeriodicidade, responsavel);
                } else {
                    // Usa a data informada por parâmetro
                    prazoIni = DateHelper.toPeriodDate(adeAnoMesIni);
                }

                Date prazoFim = null;
                if (adeAnoMesFim != null) {
                    // Usa a data informada por parâmetro
                    prazoFim = DateHelper.toPeriodDate(adeAnoMesFim);
                    if (adePrazo != null) {
                        // Verifica se a data fim passada é consistente com o prazo informado
                        final Date prazoFimTeste = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, prazoIni, adePrazo, adePeriodicidade, responsavel);
                        if (prazoFim.compareTo(prazoFimTeste) != 0) {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.este.lancamento.pois.prazo.e.data.final.informados.sao.inconsistentes", responsavel);
                        }
                    }
                } else if (adePrazo != null) {
                    // Se a autorização tem prazo, calcula a data fim
                    prazoFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, prazoIni, adePrazo, adePeriodicidade, responsavel);
                }

                if ((prazoFim != null) && prazoIni.after(prazoFim)) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.este.lancamento.pois.data.inicial.contrato.maior.data.final", responsavel);
                }

                java.util.Date adeAnoMesFimFinal = null;
                if ((adePrazo != null) && insereAlteraUsandoMaiorPrazo) {
                    adeAnoMesFimFinal = ((adePrazoAtual - adePrdPagas) > adePrazo) ? (java.util.Date) adeInsereAltera.getAttribute(Columns.ADE_ANO_MES_FIM) : prazoFim;
                    final java.util.Date adeAnoMesIniFinal = (java.util.Date) adeInsereAltera.getAttribute(Columns.ADE_ANO_MES_INI);
                    try {
                        adePrazoFinal = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, adeAnoMesIniFinal, adeAnoMesFimFinal, (String) adeInsereAltera.getAttribute(Columns.ADE_PERIODICIDADE), responsavel);
                    } catch (final PeriodoException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                } else {
                    adeAnoMesFimFinal = adeAnoMesFim;
                    adePrazoFinal = adePrazo;
                }


                if (!usuarioPodeConfirmarReserva(responsavel) && ParamSist.paramEquals(CodedValues.TPC_INSERE_ALTERA_CRIA_NOVO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                    return adeCodigo;
                }

                LOG.debug("Insere/Altera AdeCodigo: " + adeCodigo);

                // Executa a alteração do contrato
                final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazoFinal, adeIdentificador,
                        validar, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeAnoMesFimFinal, parametros, null,
                        serAtivo, validaBloqueado, cnvAtivo, svcAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, adeCarencia, null, null, false);
                alterarParam.setAdePeriodicidade(adePeriodicidade);
                alterarParam.setTmoCodigo(tmoCodigo);
                //Repassa os dados da autorização para serem gravados
                alterarParam.setDadosAutorizacaoMap(reservaParam.getDadosAutorizacaoMap());
                alterarConsignacaoController.alterar(alterarParam, responsavel);

                if (!validar) {
                    // Insere ocorrencia de Insere/Altera
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.informacao.alteracao.inclusao.contrato", responsavel), ocaAdeVlrAnt, adeVlr, responsavel);
                }
            } catch (final AutorizacaoControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw ex;
            } catch (final PeriodoException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        return adeCodigo;
    }

    /**
     * Valida o prazo do contrato que está sendo incluído, de acordo com os parâmetros de serviço
     * que limitam ou fixam o prazo do contrato, e de acordo com o prazo máximo do servidor, caso
     * este esteja cadastrado.
     * @param adePrazo
     * @param adeCarencia
     * @param rsePrazo
     * @param adePeriodicidade
     * @param svcCodigo
     * @param csaCodigo
     * @param cnvCodigo
     * @param acao
     * @param adeIncMargem
     * @param parametros
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaPrazo(Integer adePrazo, Integer adeCarencia, Integer rsePrazo, String adePeriodicidade, String rseCodigo, String svcCodigo, String csaCodigo, String cnvCodigo, String acao, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final CustomTransferObject paramPrazoFixo = getParametroSvc(CodedValues.TPS_PRAZO_FIXO, svcCodigo, "", false, parametros);
        final CustomTransferObject paramPrazo = getParametroSvc(CodedValues.TPS_MAX_PRAZO, svcCodigo, "", false, parametros);
        final CustomTransferObject paramPrazoRenegociacao = getParametroSvc(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE, svcCodigo, "", false, parametros);
        final CustomTransferObject paramValidaTaxaJuros = getParametroSvc(CodedValues.TPS_VALIDAR_TAXA_JUROS, svcCodigo, "", false, parametros);
        final CustomTransferObject paramPermitePrazoMaiorSer = getParametroSvc(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA, svcCodigo, cnvCodigo, Boolean.class, null, null, null, false, false, parametros);
        final boolean permitePrazoMaiorContSer = ((Boolean) paramPermitePrazoMaiorSer.getAttribute(Columns.PSE_VLR));

        Integer maxPrazo = null;
        boolean prazoFixo = false;

        if ((paramPrazo != null) && (paramPrazo.getAttribute(Columns.PSE_VLR) != null) &&
                !"".equals(paramPrazo.getAttribute(Columns.PSE_VLR).toString().trim())) {
            try {
                maxPrazo = (!TextHelper.isNull(acao) && (COMPRAR.equals(acao) || RENEGOCIAR.equals(acao))) ?
                        (!TextHelper.isNull(paramPrazoRenegociacao.getAttribute(Columns.PSE_VLR)) &&
                                !"".equals(paramPrazoRenegociacao.getAttribute(Columns.PSE_VLR).toString().trim())) ?
                                Integer.valueOf(paramPrazoRenegociacao.getAttribute(Columns.PSE_VLR).toString()) : Integer.valueOf(paramPrazo.getAttribute(Columns.PSE_VLR).toString()) :
                        Integer.valueOf(paramPrazo.getAttribute(Columns.PSE_VLR).toString());
            } catch (final NumberFormatException ex) {
                LOG.error(ex.getMessage());
            }
        }

        if ((paramPrazoFixo != null) && (paramPrazoFixo.getAttribute(Columns.PSE_VLR) != null) &&
                "1".equals(paramPrazoFixo.getAttribute(Columns.PSE_VLR))) {
            prazoFixo = true;
        }

        // Não olhar a incidência de margem do contrato, que pode ser zero caso não seja confirmado/deferido inicialmente
        // mas sim a incidência do serviço que sempre irá indicar a margem correta.
        final CustomTransferObject paramIncideMargem = getParametroSvc(CodedValues.TPS_INCIDE_MARGEM, svcCodigo, Short.valueOf("0"), false, parametros);
        final Short svcIncMargem = (paramIncideMargem.getAttribute(Columns.PSE_VLR) != null ? (Short) paramIncideMargem.getAttribute(Columns.PSE_VLR) : null);

        Integer mrsPrazo = null;
        if ((svcIncMargem != null)
                && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)
                && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)
                && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)
                && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            // Se o serviço incide em uma margem extra (tb_margem_registro_servidor) verifica se a margem
            // possui restrição de prazo máximo cadastrado (MRS_PRAZO_MAX)
            try {
                final MargemRegistroServidorId pk = new MargemRegistroServidorId(svcIncMargem, rseCodigo);
                final MargemRegistroServidor mrs = MargemRegistroServidorHome.findByPrimaryKey(pk);
                if ((mrs != null) && (mrs.getMrsPrazoMax() != null)) {
                    mrsPrazo = mrs.getMrsPrazoMax().intValue();
                }
            } catch (final FindException ex) {
                // Serviço incide em uma margem que não existe. Pode ser margem pai, exemplo GAP. Não gerar erro.
                LOG.error(ex.getMessage());
            }
        }

        // Se o serviço tem prazo máximo, porém é contrato quinzenal, o prazo máximo se refere
        // a meses, portanto ao comparar com o contrato quinzenal, deve multiplicar por 2
        if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
            maxPrazo = ((maxPrazo != null) && (maxPrazo > 1)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazo, responsavel) : maxPrazo;
            rsePrazo = ((rsePrazo != null) && (rsePrazo > 0)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(rsePrazo, responsavel) : rsePrazo;
            mrsPrazo = ((mrsPrazo != null) && (mrsPrazo > 0)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(mrsPrazo, responsavel) : mrsPrazo;
        }

        if (((adePrazo == null) && (maxPrazo != null) && (maxPrazo.compareTo(Integer.valueOf("0")) != 0)) ||
                ((maxPrazo != null) && (adePrazo != null) && (maxPrazo.compareTo(adePrazo) != 0) && prazoFixo) ||
                ((maxPrazo != null) && (adePrazo != null) && (maxPrazo.intValue() < adePrazo.intValue()))) {
            throw new AutorizacaoControllerException("mensagem.erro.prazo.informado.invalido.para.este.servico", responsavel);
        }

        final Integer prazoTotal = adePrazo == null ? 0 : adePrazo.intValue() + adeCarencia.intValue();
        if ((rsePrazo != null) && (adePrazo != null) && (rsePrazo.compareTo(prazoTotal) < 0) && !permitePrazoMaiorContSer) {
            // Servidor com prazo inferior ao prazo não pode fazer novas reservas.
            throw new AutorizacaoControllerException("mensagem.erro.prazo.total.maior.servidor", responsavel, String.valueOf(rsePrazo));
        }

        if ((mrsPrazo != null) && (adePrazo != null) && (mrsPrazo.compareTo(adePrazo) < 0)) {
            // Prazo da Consignação maior que o prazo máximo permitido para a margem
            throw new AutorizacaoControllerException("mensagem.erro.prazo.maior.maximo", responsavel, String.valueOf(mrsPrazo));
        }

        validaPrazoLimiteDataAdmissaoRseTemporario(rseCodigo, svcCodigo, csaCodigo, prazoTotal, responsavel);

        // Valida os prazos de acordo com o cadastrado para a consignatária / serviço
        try {
            final boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
            final boolean temValidacaoTaxaJuros = ((paramValidaTaxaJuros != null) && (paramValidaTaxaJuros.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramValidaTaxaJuros.getAttribute(Columns.PSE_VLR)));
            if (temSimulacaoConsignacao || temValidacaoTaxaJuros) {
                final List<PrazoTransferObject> prazos = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
                if ((prazos != null) && !prazos.isEmpty()) {
                    final Set<Integer> prazosPossiveis = new TreeSet<>();
                    if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                        prazosPossiveis.addAll(PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel));
                    } else {
                        prazos.forEach(p -> prazosPossiveis.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                    }
                    if (((adePrazo != null) && !prazosPossiveis.contains(adePrazo)) || ((adePrazo == null) && !prazosPossiveis.contains(0))) {
                        throw new AutorizacaoControllerException("mensagem.erro.prazos.permitidos.para.este.servico.sao.arg0", responsavel, TextHelper.join(prazosPossiveis, ", "));
                    }
                }
            }
        } catch (final SimulacaoControllerException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }


        // Verifica parametro Carencia Servico
        final CustomTransferObject paramCarenciaMin = getParametroSvc(CodedValues.TPS_CARENCIA_MINIMA, svcCodigo, Integer.valueOf("0"), false, parametros);
        final Integer carenciaMinimaSvc = ((paramCarenciaMin != null) && (paramCarenciaMin.getAttribute(Columns.PSE_VLR) != null) && !paramCarenciaMin.getAttribute(Columns.PSE_VLR).toString().isEmpty()) ? (Integer) paramCarenciaMin.getAttribute(Columns.PSE_VLR) : 0;
        final CustomTransferObject paramCarenciaMax = getParametroSvc(CodedValues.TPS_CARENCIA_MAXIMA, svcCodigo, Integer.valueOf("0"), false, parametros);
        final Integer carenciaMaximaSvc = ((paramCarenciaMax != null) && (paramCarenciaMax.getAttribute(Columns.PSE_VLR) != null) && !paramCarenciaMax.getAttribute(Columns.PSE_VLR).toString().isEmpty()) ? (Integer) paramCarenciaMax.getAttribute(Columns.PSE_VLR) : 99;

        // Verifica parametro Carencia Consignataria
        Integer carenciaMinimaCnv = 0;
        Integer carenciaMaximaCnv = 99;

        try {
            final List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_CARENCIA_MINIMA);
            tpsCodigo.add(CodedValues.TPS_CARENCIA_MAXIMA);
            final List<TransferObject> params = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
            for (final TransferObject param : params) {
                if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                    if (CodedValues.TPS_CARENCIA_MINIMA.equals(param.getAttribute(Columns.TPS_CODIGO))){
                        carenciaMinimaCnv = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty())? Integer.valueOf((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                    } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(param.getAttribute(Columns.TPS_CODIGO))){
                        carenciaMaximaCnv = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty())? Integer.valueOf((String)param.getAttribute(Columns.PSC_VLR)) : 99;
                    }
                }
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage());
        } catch (final ParametroControllerException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        // Se a nova carencia for menor ou maior que os valores pre-definidos indica um erro
        final int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinimaCnv, carenciaMaximaCnv, carenciaMinimaSvc, carenciaMaximaSvc);
        int carenciaMinPermitida = carenciaPermitida[0];
        int carenciaMaxPermitida = carenciaPermitida[1];

        if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
            carenciaMinPermitida = PeriodoHelper.converterPrazoMensalEmPeriodicidade(carenciaMinPermitida, responsavel);
            carenciaMaxPermitida = PeriodoHelper.converterPrazoMensalEmPeriodicidade(carenciaMaxPermitida, responsavel);
        }

        if (!responsavel.isRescisao() && ((adeCarencia < carenciaMinPermitida) || (adeCarencia > carenciaMaxPermitida))) {
            if (carenciaMaxPermitida > carenciaMinPermitida) {
                throw new AutorizacaoControllerException("mensagem.erro.carencia.entre.min.max", responsavel, String.valueOf(carenciaMinPermitida), String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida < carenciaMinPermitida) {
                throw new AutorizacaoControllerException("mensagem.erro.carencia.menor.max", responsavel, String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida == carenciaMinPermitida) {
                throw new AutorizacaoControllerException("mensagem.erro.carencia.fixa", responsavel, String.valueOf(carenciaMinPermitida));
            }
        }
    }

    private BigDecimal gravarDadosSimulacao(ReservarMargemParametros margemParam, String adeCodigoNovo, String sadCodigo, String adePeriodicidade, String csaCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final Map<String, Object> parametros = margemParam.getParametros();
        final String rseCodigo = margemParam.getRseCodigo();

        BigDecimal taxa = null;

        final Integer adePrazo = margemParam.getAdePrazo();
        final BigDecimal adeVlr = margemParam.getAdeVlr();
        final BigDecimal adeVlrTac = margemParam.getAdeVlrTac();
        final BigDecimal adeVlrIof = margemParam.getAdeVlrIof();
        final BigDecimal adeVlrLiquido = margemParam.getAdeVlrLiquido();

        BigDecimal cdeVlrLiberado = margemParam.getCdeVlrLiberado();
        Short cdeRanking = margemParam.getCdeRanking();
        String cftCodigo = margemParam.getCftCodigo();
        final String dtjCodigo = margemParam.getDtjCodigo();
        final String cdeTxtContato = margemParam.getCdeTxtContato();
        final Boolean validaBloqSerCnvCsaIncAvancada = margemParam.getValidaBloqSerCnvCsa();

        /*
         * Se existe simulação de consignação, então seleciona os coeficientes ativos.
         * Se houverem coeficientes cadastrados, então adiciona a entidade coeficiente
         * desconto e redireciona para a página do boleto
         */
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        if (temSimulacaoConsignacao && (adePrazo != null) && (adePrazo.intValue() > 0)) {
            // Realiza a simulação para obter o valor liberado e a posição no ranking da consignatária
            try {
                // Obtém os parâmetros de limite de ranking no simulador de consignação
                CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR, svcCodigo, Boolean.FALSE, false, parametros);
                final boolean bloqueiaReservaLimiteSimulador = ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) && ((Boolean)paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue());
                boolean csaDentroLimiteRanking = true;

                paramSvc = getParametroSvc(CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR, svcCodigo, "", false, parametros);
                final String qtdeConsignatariasSimulacaoParam = ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null)) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : "";
                final int qtdeConsignatariasSimulacao = (TextHelper.isNum(qtdeConsignatariasSimulacaoParam)) ? Integer.parseInt(qtdeConsignatariasSimulacaoParam) : Integer.MAX_VALUE;

                if ((cdeVlrLiberado == null) || (cdeRanking == null) || (cftCodigo == null)) {
                    // Realiza a simulação
                    List<TransferObject> simulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, adeVlr, null, adePrazo.shortValue(), null, validaBloqSerCnvCsaIncAvancada, adePeriodicidade, responsavel);

                    if (bloqueiaReservaLimiteSimulador && (qtdeConsignatariasSimulacao != Integer.MAX_VALUE)) {
                        /*
                         * Se tem limite de consignatárias no ranking, e novas reservas também devem ser bloqueadas,
                         * então seleciona as linhas que podem ser usadas de acordo com a posição no ranking
                         */
                        simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, null, qtdeConsignatariasSimulacao, true, false, responsavel);
                    }

                    if (!simulacao.isEmpty()) {
                        boolean encontrouCsa = false;

                        for (final TransferObject coeficiente : simulacao) {
                            final String csaCodigoLst = coeficiente.getAttribute(Columns.CSA_CODIGO).toString();

                            if (csaCodigo.trim().equals(csaCodigoLst.trim())) {
                                // Se CSA informa o valor liberado, então salva o valor informado, senão calcula.
                                if (TextHelper.isNull(cdeVlrLiberado)) {
                                    cdeVlrLiberado = (BigDecimal)coeficiente.getAttribute("VLR_LIBERADO");
                                }
                                cdeRanking = Short.valueOf(coeficiente.getAttribute("RANKING").toString());
                                cftCodigo = coeficiente.getAttribute(Columns.CFT_CODIGO).toString();
                                final BigDecimal cftVlr = (BigDecimal) coeficiente.getAttribute(Columns.CFT_VLR);

                                if (bloqueiaReservaLimiteSimulador && (qtdeConsignatariasSimulacao != Integer.MAX_VALUE)) {
                                    csaDentroLimiteRanking = ((Boolean) coeficiente.getAttribute("OK"));
                                }
                                if ((cftVlr != null) && (cftVlr.signum() == 1)) {
                                    encontrouCsa = true;
                                }
                                break;
                            }
                        }

                        /*
                         * Se o ranking existe, mas a consignatária não foi encontrada nele, então
                         * significa que a consignatária não cadastrou suas taxas, então dá erro
                         * para o usuário, de modo que a operação não pode ser concluída
                         */
                        if (!encontrouCsa) {
                            throw new AutorizacaoControllerException("mensagem.erro.operacao.nao.pode.ser.realizada.nao.existe.taxa.cadastrada.para.prazo.contrato", responsavel);
                        }
                    }
                }

                final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
                if (simulacaoPorTaxaJuros && (adeVlrLiquido != null) && (adeVlrLiquido.signum() == 1)) {
                    cdeVlrLiberado = adeVlrLiquido;
                }

                if ((cdeVlrLiberado != null) && (cftCodigo != null)) {
                    // Inclusão da TAC e OP como parametros de consignação
                    if ((cdeRanking != null) && !ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel)) {
                        final List<String> tpsCodigos = new ArrayList<>();
                        tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                        tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                        setParamSvcADE(adeCodigoNovo, tpsCodigos, responsavel);
                    }

                    // O coeficiente deve existir na tabela de histórico de coeficientes.
                    try {
                        if (!TextHelper.isNull(cftCodigo) && TextHelper.isNull(dtjCodigo)) {
                            CoeficienteHome.findByPrimaryKey(cftCodigo);
                        } else if (!TextHelper.isNull(dtjCodigo)) {
                            // Cria coeficiente para posterior uso baseado no valor da deficao taxa de juros
                            final CustomTransferObject dtj = simulacaoController.getDefinicaoTaxaJuros(dtjCodigo);
                            final TransferObject cft = new CustomTransferObject();

                            try {
                                List<PrazoConsignataria> przCsaLst = PrazoConsignatariaHome.findByCsaServico(csaCodigo, svcCodigo);
                                przCsaLst = przCsaLst.stream().filter(przCsa -> przCsa.getPrazo().getPrzVlr() == adePrazo.shortValue()).collect(Collectors.toList());

                                if ((przCsaLst != null) && !przCsaLst.isEmpty()) {
                                    cft.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, przCsaLst.get(0).getPrzCsaCodigo());
                                } else {
                                    throw new FindException("mensagem.erroInternoSistema", responsavel);
                                }
                            } catch (final FindException ex) {
                                throw new AutorizacaoControllerException("mensagem.simulacao.prazo.nao.permitido", responsavel, ex);
                            }

                            cft.setAttribute(Columns.CFT_CODIGO, null);
                            cft.setAttribute(Columns.CFT_VLR, NumberHelper.format(((BigDecimal) dtj.getAttribute(Columns.CFT_VLR)).doubleValue(), NumberHelper.getLang()));
                            cftCodigo = coeficienteController.insertCoeficiente(cft, responsavel);
                        }

                    } catch (final FindException ex) {
                        // Se não foi encontrado, cria uma cópia a partir do coeficiente ativo,
                        // porém com as datas de vigência reajustadas.
                        final TransferObject cftAtivo = simulacaoController.getCoeficienteAtivo(cftCodigo);
                        cftAtivo.setAttribute(Columns.CFT_VLR, NumberHelper.format(((BigDecimal) cftAtivo.getAttribute(Columns.CFT_VLR)).doubleValue(), NumberHelper.getLang()));
                        coeficienteController.insertCoeficiente(cftAtivo, responsavel);
                    }

                    /*
                     * Salva a taxa efetiva do contrato se ela for diferente da taxa
                     * cadastrada pela consignatária.
                     */
                    if (simulacaoPorTaxaJuros) {
                        try {
                            // DESENV-14069 : Usa a taxa informada na proposta pela CSA.
                            if (margemParam.isDestinoAprovacaoLeilaoReverso()) {
                                taxa = margemParam.getAdeTaxaJuros();
                            } else {
                                taxa = calcularTaxaJurosEfetiva(adeCodigoNovo, margemParam.getAdeVlr(), cdeVlrLiberado, margemParam.getAdeVlrTac(), margemParam.getAdeVlrIof(),
                                        margemParam.getAdePrazo(), DateHelper.getSystemDatetime(), margemParam.getAdeAnoMesIni(), svcCodigo, orgCodigo, simulacaoPorTaxaJuros, adePeriodicidade, responsavel);
                            }

                            if (taxa != null) {
                                final TransferObject cft = coeficienteController.getCoeficiente(cftCodigo, responsavel);
                                final double taxaCsa = NumberHelper.parse(cft.getAttribute(Columns.CFT_VLR).toString(), "en");
                                final double taxaEfetiva = taxa.doubleValue();
                                if ((taxaEfetiva > 0) && ((taxaCsa - taxaEfetiva) > 0.005)) {
                                    cft.setAttribute(Columns.CFT_CODIGO, null);
                                    cft.setAttribute(Columns.CFT_VLR, NumberHelper.format(taxaEfetiva, NumberHelper.getLang()));
                                    cftCodigo = coeficienteController.insertCoeficiente(cft, responsavel);
                                }
                            }
                        } catch (final Exception ex) {
                            // Se der algum erro, utiliza a taxa cadastrada pela CSA.
                        }
                    }
                    if (cdeRanking != null) {
                        /*
                         * Verifica se o serviço tem limite de consignatárias para operação no simulador.
                         * Se existir e a consignatária não for uma das X primeiras, e o parâmetro de serviço
                         * estiver configurado para bloquear a operação, então dá mensagem de erro para o usuário.
                         */
                        if (bloqueiaReservaLimiteSimulador && !csaDentroLimiteRanking) {
                            throw new AutorizacaoControllerException("mensagem.erro.operacao.nao.pode.ser.realizada.consignataria.arg0.do.ranking.somente.arg1.primeiras.sao.permitidas", responsavel, cdeRanking.toString(), String.valueOf(qtdeConsignatariasSimulacao));
                        }

                        /*
                         * Verifica se o valor liberado está dentro do esperado, ou seja um valor positivo.
                         * Se a consignatária tentar reservar para um prazo sem taxa, o valor liberado será zero,
                         * neste caso o sistema não pode permitir a reserva.
                         */
                        if (cdeVlrLiberado.signum() <= 0) {
                            throw new AutorizacaoControllerException("mensagem.erro.valor.liquido.liberado.deve.ser.maior.que.zero", responsavel);
                        }

                        /**
                         *  Calcula o valor liberado máximo, utilizando-se das taxas cadastradas pela consignatária
                         */
                        BigDecimal vlrLiberadoCalc = cdeVlrLiberado;
                        if (!CodedValues.FUN_SIM_CONSIGNACAO.equals(responsavel.getFunCodigo()) && !CodedValues.FUN_SOL_EMPRESTIMO.equals(responsavel.getFunCodigo())) {
                            /*
                             * Para a função de simulação, o valor será o mesmo encontrado na simulação original visto
                             * que o usuário não informa valor de parcela e valor liberado, então um valor é calculado pelo outro,
                             * sendo o valor liberado o máximo para o prazo e parcela informados
                             */
                            try {
                                final List<TransferObject> lsim = simulacaoController.simularConsignacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, adeVlr, (BigDecimal) null, adePrazo.shortValue(), margemParam.getAdeAnoMesIni(), false, false, adePeriodicidade, responsavel);
                                if ((lsim != null) && !lsim.isEmpty()) {
                                    vlrLiberadoCalc = (BigDecimal) lsim.get(0).getAttribute("VLR_LIBERADO");
                                }
                            } catch (final Exception ex) {
                                throw new AutorizacaoControllerException(ex);
                            }
                        }

                        // Cria os registros de coeficiente desconto
                        simulacaoController.createCoeficienteDesconto(adeCodigoNovo, cftCodigo, cdeVlrLiberado, vlrLiberadoCalc, cdeTxtContato, cdeRanking, adeVlrTac, adeVlrIof, responsavel);
                    } else {
                        // Não deveria chegar até aqui!
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
                    }
                } else {
                    // Não deveria chegar até aqui!
                    // OBS: Chega até aqui na importação de histórico, pois apesar de tudo estar habilitado,
                    // as informações não são passadas. Também reservas de prazo determinado porém de serviço
                    // que não tenham prazos e taxas cadastrados.
                    // throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
                }

                if (CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    // Verifica se exige codigo de autorização para solicitação
                    final CustomTransferObject paramExigeCodAutSolic = getParametroSvc(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC, svcCodigo, "", false, parametros);
                    final boolean exigeCodAutSolicitacao = ((paramExigeCodAutSolic != null) && (paramExigeCodAutSolic.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramExigeCodAutSolic.getAttribute(Columns.PSE_VLR)));

                    if (exigeCodAutSolicitacao && !margemParam.isIniciarLeilaoReverso() && !margemParam.isDestinoAprovacaoLeilaoReverso()) {
                        // Cria código de autorização para a solicitação
                        final String tdaCodAutSolic = CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO;
                        final String codigoAutorizacaoSolicitacao = GeradorSenhaUtil.getPasswordNumber(4, responsavel);
                        setDadoAutDesconto(adeCodigoNovo, tdaCodAutSolic, codigoAutorizacaoSolicitacao, responsavel);

                        try {
                            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                            final String serCodigo = registroServidor.getServidor().getSerCodigo();
                            final Servidor servidor = ServidorHome.findByPrimaryKey(serCodigo);
                            final String serEmail = servidor.getSerEmail();
                            // Envia e-mail para o servidor com o código de autorização para solicitação, caso o mesmo possua um e-mail cadastrado
                            if (!TextHelper.isNull(serEmail)) {
                                EnviaEmailHelper.enviarEmailCodigoAutorizacaoServidor(serEmail, registroServidor.getRseMatricula(), adeCodigoNovo, codigoAutorizacaoSolicitacao, responsavel);
                            }
                        } catch (final FindException e) {
                            LOG.error("Não foi possível enviar e-mail com o código de confirmação da solicitação para o servidor.", e);
                        } catch (final ViewHelperException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }

            } catch (final SimulacaoControllerException ex) {
                throw new AutorizacaoControllerException(ex);
            } catch (final UsuarioControllerException ex) {
                throw new AutorizacaoControllerException("mensagem.erro.interno.nao.possivel.gerar.codigo.autorizacao.solicitacao", responsavel, ex);
            }
        }

        return taxa;
    }

    /**
     * Valida as informações bancárias do servidor, caso o cadastro seja obrigatório no serviço.
     * @param serInfBancariaObrigatoria
     * @param validarInfBancaria
     * @param adeBanco
     * @param adeAgencia
     * @param adeConta
     * @param rseDto
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void validarDadosBancariosServidor(boolean serInfBancariaObrigatoria, boolean validarInfBancaria, String adeBanco, String adeAgencia, String adeConta, RegistroServidorTO rseDto, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (serInfBancariaObrigatoria && responsavel.isCsaCor() && CanalEnum.SOAP.equals(responsavel.getCanal())) {
            try {
                final String pcsVlr = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_INF_BANCARIA_OBRIGATORIA_HOST_A_HOST, responsavel);
                if (!TextHelper.isNull(pcsVlr)) {
                    serInfBancariaObrigatoria = "S".equalsIgnoreCase(pcsVlr);
                }
            } catch (final ParametroControllerException ex) {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        if (serInfBancariaObrigatoria) {
            if (TextHelper.isNull(adeBanco) || TextHelper.isNull(adeAgencia) || TextHelper.isNull(adeConta)) {
                throw new AutorizacaoControllerException("mensagem.erro.informacao.bancaria.nao.informada", responsavel);
            }

            if (validarInfBancaria) {
                final String rseBancoSal   = rseDto.getRseBancoSal();
                final String rseAgenciaSal = rseDto.getRseAgenciaSal();
                final String rseContaSal   = rseDto.getRseContaSal();
                if (TextHelper.isNull(rseBancoSal) || TextHelper.isNull(rseAgenciaSal) || TextHelper.isNull(rseContaSal) ||
                        !TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(adeBanco)) ||
                        !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(adeAgencia)) ||
                        !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(adeConta))) {
                    final String rseBancoSalAlt   = rseDto.getRseBancoSalAlternativo();
                    final String rseAgenciaSalAlt = rseDto.getRseAgenciaSalAlternativa();
                    final String rseContaSalAlt   = rseDto.getRseContaSalAlternativa();

                    if (TextHelper.isNull(rseBancoSalAlt) || TextHelper.isNull(rseAgenciaSalAlt) || TextHelper.isNull(rseContaSalAlt) ||
                            !TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(adeBanco)) ||
                            !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(adeAgencia)) ||
                            !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(adeConta))) {
                        throw new AutorizacaoControllerException("mensagem.informacaoBancariaIncorreta", responsavel);
                    }
                }
            }
        }
    }

    /**
     * Realiza a finalização do processo de reserva de margem.
     * @param adeCodigo Contrato sendo deferido.
     * @param svcCodigo Código do serviço da consignação
     * @param apenasValidacao Indica se é apenas uma validação da reserva.
     * @param responsavel
     * @throws GerenciadorAutorizacaoException
     */
    private void finalizarReservaMargem(String adeCodigo, String svcCodigo, boolean apenasValidacao, AcessoSistema responsavel) throws GerenciadorAutorizacaoException {
        try {
            // Classe de especialização das operações de autorização por serviço.
            GerenciadorAutorizacao gerenciadorAutorizacaoServico = null;

            // Verifica se há classe específica de manutenção de autorização para o serviço.
            final CustomTransferObject paramClasseGerenciadorAutorizacao = getParametroSvc(CodedValues.TPS_CLASSE_GERENCIADOR_AUTORIZACAO, svcCodigo, "", false, null);
            if ((paramClasseGerenciadorAutorizacao != null) && !TextHelper.isNull(paramClasseGerenciadorAutorizacao.getAttribute(Columns.PSE_VLR))) {
                final String nomeClasseGerenciadorAutorizacao = (String) paramClasseGerenciadorAutorizacao.getAttribute(Columns.PSE_VLR);
                gerenciadorAutorizacaoServico = GerenciadorAutorizacaoFactory.getGerenciadorAutorizacao(nomeClasseGerenciadorAutorizacao);
            }

            // Se há classe específica de manutençao das autorizações do serviço.
            if (gerenciadorAutorizacaoServico != null) {
                final long horaInicioFinalizacao = java.util.Calendar.getInstance().getTimeInMillis();
                gerenciadorAutorizacaoServico.finalizarReservaMargem(adeCodigo, apenasValidacao, responsavel);
                LOG.debug("TOTAL FINALIZACAO RESERVAR MARGEM (" + responsavel.getUsuCodigo() + ") = " + (java.util.Calendar.getInstance().getTimeInMillis() - horaInicioFinalizacao) + " ms");
            }
        } catch (final AutorizacaoControllerException ex) {
            throw new GerenciadorAutorizacaoException("mensagem.erro.finalizando.reserva.margem", responsavel, ex);
        }
    }

    private BigDecimal validaPostoFixoSvcCsa(String cnvCodigo, BigDecimal vlrPadrao, String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            BigDecimal vlr = vlrPadrao;

            final PostoRegistroServidor posto = PostoRegistroServidorHome.findByRseCodigo(rseCodigo);

            if (posto != null) {
                final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                final String svcCodigo = convenio.getServico().getSvcCodigo();
                final String csaCodigo = convenio.getConsignataria().getCsaCodigo();

                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido

                final List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_VALOR_SVC_FIXO_POSTO);

                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
                for (final TransferObject param2 : paramSvcCsa) {
                    final CustomTransferObject param = (CustomTransferObject) param2;
                    if (CodedValues.TPS_VALOR_SVC_FIXO_POSTO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                        final String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        if (CodedValues.TPC_SIM.equals(pscVlr)) {
                            final TransferObject valorSvcByPostoAndCsa = postoRegistroServidorController.findValorFixoByCsaSvcPos(svcCodigo, csaCodigo, posto.getPosCodigo(), responsavel);
                            if (!alteraAdeVlr && !TextHelper.isNull(valorSvcByPostoAndCsa)) {
                                final String vlrGet = (String) valorSvcByPostoAndCsa.getAttribute(Columns.PSP_PPO_VALOR);
                                vlr = new BigDecimal(vlrGet.replace(",", "."));
                            } else if (!alteraAdeVlr && TextHelper.isNull(valorSvcByPostoAndCsa)) {
                                LOG.error("Valor Fixo do serviço pelo posto não cadastrado");
                                throw new AutorizacaoControllerException("mensagem.erro.inserir.vlr.posto.fixo", responsavel, posto.getPosDescricao());
                            }
                        }
                    }
                }
            }
            return vlr;
        } catch (FindException | ParametroControllerException | PostoRegistroServidorControllerException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void checaPermissaoEnviaNotificacaoSerReservaMargem(RegistroServidorTO rseDto, String svcCodigo, String csaCodigo, AcessoSistema responsavel) {
        try {
            if (!parametroController.verificaAutorizacaoReservaSemSenha(rseDto.getRseCodigo(), svcCodigo, true, null, responsavel)) {
                boolean serDesabilitouEnvioNotificacao = false;

                try {
                    final DestinatarioEmailSer destinatarioEmailSer = DestinatarioEmailSerHome.findByPrimaryKey(responsavel.getFunCodigo(), responsavel.getPapCodigo(), rseDto.getSerCodigo());
                    serDesabilitouEnvioNotificacao = destinatarioEmailSer != null;

                } catch (final FindException e) {
                    // Por padrão devemos notificar o servidor caso dê erro ao buscar permissão de notificação
                }

                // Verifica se o servidor não desabilitou o envio de notificação para a função de reserva de margem
                if(!serDesabilitouEnvioNotificacao) {
                    String csaNome = "";
                    try {
                        csaNome = consignatariaController.findConsignataria(csaCodigo, responsavel).getCsaNome();
                        final String corpoSms = ApplicationResourcesHelper.getMessage("mensagem.sms.reserva.margem", responsavel, csaNome);
                        final String tituloPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.reserva.margem.titulo", responsavel);
                        final String textoPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.reserva.margem.texto", responsavel, csaNome);

                        servidorController.enviarNotificacaoSer(rseDto.getSerCodigo(), csaCodigo, corpoSms, tituloPush, textoPush, TipoNotificacaoEnum.EMAIL_NOTIFICACAO_RESERVA_MARGEM, null, responsavel);
                    } catch (final ConsignatariaControllerException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
        }
    }

    private CustomTransferObject buscaNovaAutorizacao(String adeCodigo, String serCodigo, String orgCodigo, AcessoSistema responsavel) throws ZetraException {
        // Busca a nova autorização
        final TransferObject novaAutorizacao = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
        // Guarda o Boleto no Hash para a geração do resultado
        // Busca o servidor
        final ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        // Pega a descrição do codigo de estado civil
        final String serEstCivil = servidorController.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        final OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);

        final CustomTransferObject boleto = new CustomTransferObject();
        // Adiciona Informações do servidor
        boleto.setAtributos(servidor.getAtributos());
        // Adiciona a descrição do estado civil
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil);
        // Adiciona Informações do orgão
        boleto.setAtributos(orgao.getAtributos());
        // Adiciona Informações da autorização
        boleto.setAtributos(novaAutorizacao.getAtributos());
        // Guarda o boleto no hash para ser consultada na geração do resultado
        return boleto;
    }
}