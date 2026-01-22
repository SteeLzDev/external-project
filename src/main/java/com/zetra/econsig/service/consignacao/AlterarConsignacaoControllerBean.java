package com.zetra.econsig.service.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_OBS_OCA_ALTERACAO_CONTRATO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.ROTULO_INDETERMINADO_ABREVIADO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.ROTULO_NAO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.ROTULO_SIM;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamNseRseTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.job.process.ProcessaRelatorioAlteracaoMultiplasConsignacoes;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Coeficiente;
import com.zetra.econsig.persistence.entity.CoeficienteDesconto;
import com.zetra.econsig.persistence.entity.CoeficienteDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DecisaoJudicial;
import com.zetra.econsig.persistence.entity.DecisaoJudicialHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemRse;
import com.zetra.econsig.persistence.entity.IgnoraInconsistenciaAdeHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacaoHome;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.admin.ListaCargoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoAguardandoMargemQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoDescontoEmFilaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoByOrigemQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCnvRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamNseRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcRseQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemUltimoPeriodoRetornoQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorAguardMargemQuery;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.OrigemSolicitacaoEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <p>Title: AlterarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operação de Alteração de Contrato.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AlterarConsignacaoControllerBean extends AutorizacaoControllerBean implements AlterarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarConsignacaoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarController;

    @Autowired
    private CoeficienteController coeficienteController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public void alterar(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {

        // Valida se o usuário possui permissão para alteração avançada de consignação
        final boolean usuPossuiAltAvancadaAde = usuarioPossuiAltAvancadaAde(responsavel);
        final boolean isAlteracaoAvancada = alterarParam.isAlteracaoAvancada();
        final boolean alteracaoViaLote = (responsavel.getFunCodigo() != null) && CodedValues.FUN_ALTERACAO_VIA_LOTE.equals(responsavel.getFunCodigo());
        final boolean alteracaoForcaPeriodo = alterarParam.isAlteracaoForcaPeriodo();

        try {
            alterarParam.checkNotNullSafe();
        } catch (final ParametrosException pe) {
            throw new AutorizacaoControllerException(pe);
        }

        final String adeCodigo = alterarParam.getAdeCodigo();
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                alterarParam = validaCamposSistema(autdes, alterarParam, responsavel);

                // A alteração de contrato que contenha decisão judicial só podem acontecer pelo módulo de decisão judicial
                autorizacaoController.verificaAlteracaoReativacaoDecisaoJudicial(adeCodigo, responsavel);

                String cnvCodigo = null;
                String svcCodigo = null;
                String csaCodigo = null;
                String msgOca = "";
                BigDecimal adeVlrPercentual = null;

                BigDecimal adeVlr = alterarParam.getAdeVlr();
                Integer adePrazo = alterarParam.getAdePrazo();
                String adeIdentificador = alterarParam.getAdeIdentificador();
                String adeIndice = alterarParam.getAdeIndice();
                BigDecimal adeVlrTac = alterarParam.getAdeVlrTac();
                BigDecimal adeVlrIof = alterarParam.getAdeVlrIof();
                BigDecimal adeVlrLiquido = alterarParam.getAdeVlrLiquido();
                final BigDecimal adeVlrMensVinc = alterarParam.getAdeVlrMensVinc();
                final BigDecimal adeTaxaJuros = alterarParam.getAdeTaxaJuros();
                final java.util.Date anoMesFim = alterarParam.getAnoMesFim();
                final BigDecimal adeVlrSegPrestamista = alterarParam.getAdeVlrSegPrestamista();
                final Integer adeCarencia = alterarParam.getAdeCarencia();
                final boolean validar = alterarParam.isValidar();
                final boolean renegociacao = alterarParam.isRenegociacao();
                boolean serAtivo = alterarParam.isSerAtivo();
                final boolean validaBloqueado = alterarParam.isValidaBloqueado();
                final boolean permiteAltEntidadesBloqueadas = alterarParam.isPermiteAltEntidadesBloqueadas();
                final boolean validaLimiteAde = alterarParam.isValidaLimiteAde();
                boolean cnvAtivo = alterarParam.isCnvAtivo();
                boolean svcAtivo = alterarParam.isSvcAtivo();
                boolean csaAtivo = alterarParam.isCsaAtivo();
                final boolean orgAtivo = alterarParam.isOrgAtivo();
                final boolean estAtivo = alterarParam.isEstAtivo();
                boolean cseAtivo = alterarParam.isCseAtivo();
                final boolean isAtualizacaoCompra = alterarParam.isAtualizacaoCompra();
                String adePeriodicidade = alterarParam.getAdePeriodicidade();
                final String nomeAnexo = alterarParam.getNomeAnexo();
                final String idAnexo = alterarParam.getIdAnexo();
                String aadDescricao = alterarParam.getAadDescricao();

                Date adeAnoMesIniOld = null;
                Date adeAnoMesIniNew = null;

                if (TextHelper.isNull(adePeriodicidade)) {
                    adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
                }
                if (PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                    throw new AutorizacaoControllerException("mensagem.erro.ade.periodicidade.invalida", responsavel);
                }

                final Map<String, Object> parametros = alterarParam.getParametros();
                final RegistroServidorTO rseDto = RegistroServidorDtoAssembler.createDto(RegistroServidorHome.findByPrimaryKey(autdes.getRegistroServidor().getRseCodigo()), true);
                List<HistoricoMargemRse> historicosMargem = null;

                if (parametros != null) {
                    // Se é importação de lote, obtém os dados do servidor da lista de parâmetros
                    cnvCodigo = parametros.get(Columns.CNV_CODIGO).toString();
                    svcCodigo = parametros.get(Columns.SVC_CODIGO).toString();
                    csaCodigo = parametros.get(Columns.CSA_CODIGO).toString();
                } else {
                    cnvCodigo = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo()).getConvenio().getCnvCodigo();

                    final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                    svcCodigo = convenio.getServico().getSvcCodigo();
                    csaCodigo = convenio.getConsignataria().getCsaCodigo();
                }

                final String rseCodigo = rseDto.getRseCodigo();
                final String orgCodigo = rseDto.getOrgCodigo();
                final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo();
                final Short adeIncMargem = (autdes.getAdeIncMargem() != null ? autdes.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM);

                // Se o usuário possui permissão para alteração avançada e permite que convênio, consignatária e serviço não sejam validados
                if (((usuPossuiAltAvancadaAde && isAlteracaoAvancada) || responsavel.isSistema()) && permiteAltEntidadesBloqueadas) {
                    cnvAtivo = false;
                    svcAtivo = false;
                    csaAtivo = false;
                    serAtivo = false;
                    cseAtivo = false;
                }

                // Se a opção avançada de cálculo de prazo pela diferença do valor alterado está habilitado,
                // verifica se houve alteração de valor e calcula o adeVlr e adePrazo corretos
                BigDecimal adeVlrParcelaFolha = autdes.getAdeVlrParcelaFolha();
                if (usuPossuiAltAvancadaAde && isAlteracaoAvancada && alterarParam.isCalcularPrazoDifValor()) {
                    // Parcelas em processamento devem ser consideradas como pagas
                    final List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);
                    final int qtdParcelasEmProcessamento = (parcelasPeriodo != null ? parcelasPeriodo.size() : 0);

                    final BigDecimal adeVlrOld = (adeVlrParcelaFolha != null ? adeVlrParcelaFolha : autdes.getAdeVlr());
                    final Integer adePrazoOld = autdes.getAdePrazo() != null ? autdes.getAdePrazo() : 0;
                    final Integer adePrdPagasOld = autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0;
                    final Integer prazoRestante = adePrazoOld - adePrdPagasOld - qtdParcelasEmProcessamento;
                    final BigDecimal capitalDevidoAtual = adeVlrOld.multiply(new BigDecimal(prazoRestante));

                    if (adeVlr.compareTo(capitalDevidoAtual) > 0) {
                        // Se o valor da parcela está aumentando de forma que fique maior que o capital devido
                        // então, retorna erro informando que a operação não pode ser realizada
                        throw new AutorizacaoControllerException("mensagem.erro.alteracao.avancada.capital.devido.novo.maior", responsavel);
                    }

                    if ((adeVlr.compareTo(adeVlrOld) != 0) && (capitalDevidoAtual.signum() > 0)) {
                        // Se houve alteração no valor da parcela, então calcula os novos valores
                        final Integer adePrazoNew = capitalDevidoAtual.divide(adeVlr, 0, RoundingMode.HALF_UP).intValue();
                        final BigDecimal adeVlrNew = capitalDevidoAtual.divide(new BigDecimal(adePrazoNew), 2, RoundingMode.DOWN);
                        final BigDecimal capitalDevidoNovo = adeVlrNew.multiply(new BigDecimal(adePrazoNew));

                        if (capitalDevidoNovo.compareTo(capitalDevidoAtual) <= 0) {
                            // Se os cálculos foram corretos, sobrepõe os valores passados por parâmetro.
                            adeVlr = adeVlrNew;
                            adePrazo = adePrazoNew + qtdParcelasEmProcessamento;

                            // Se o valor está aumentando, grava o valor de parcela folha igual ao valor que está na margem
                            if (adeVlr.compareTo(adeVlrOld) > 0) {
                                adeVlrParcelaFolha = adeVlrNew;
                            }
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.alteracao.avancada.capital.devido.calculado.invalido", responsavel);
                        }
                    }
                }

                // Se a opção avançada informa que o valor da consignação alterado deve permanecer na margem,
                // seta novo campo para informar o valor da parcela, mantendo o adeVlr original, caso a alteração seja para menor
                if (usuPossuiAltAvancadaAde && isAlteracaoAvancada && alterarParam.isManterDifValorMargem() && (adeVlr.compareTo(autdes.getAdeVlr()) < 0)) {
                    // Valor da parcela para desconto na folha será o informado pelo usuário
                    adeVlrParcelaFolha = adeVlr;
                    // O valor de face permanece o mesmo, prendendo a margem do servidor
                    adeVlr = autdes.getAdeVlr();
                }



                // Parâmetro informa se o valor retido da margem é o valor calculado em reais
                CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL, svcCodigo, Boolean.FALSE, false, parametros);
                final boolean retemMargemSvcPercentual = (paramSvc != null) && ((Boolean) paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue();

                paramSvc = getParametroSvc(CodedValues.TPS_TIPO_VLR, svcCodigo, cnvCodigo, String.class, null, null, null, false, true, parametros);
                final String svcTipoVlr = ((paramSvc != null) && !TextHelper.isNull((paramSvc.getAttribute(Columns.PSE_VLR)))) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : CodedValues.TIPO_VLR_FIXO;

                if ((!usuPossuiAltAvancadaAde || !isAlteracaoAvancada) || validaLimiteAde) {
                    // Verifica se o servidor está bloqueado para o serviço
                    verificaServidorSvcBloqueio(svcCodigo, rseCodigo, responsavel);
                    // Verifica se o servidor está bloqueado para o convênio
                    verificaServidorCnvBloqueio(cnvCodigo, rseCodigo, responsavel);
                    // Verifica se o servidor está bloqueado para a natureza do serviço
                    verificaServidorNseBloqueio(svcCodigo, rseCodigo, responsavel);
                }

                // Usuário valida prazo
                final boolean validarLimitesValorPrazo = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? !alterarParam.isAlterarValorPrazoSemLimite() : true;

                // se for serviço tipo percentual, calcula o novo valor da parcela
                if (retemMargemSvcPercentual && CodedValues.TIPO_VLR_PERCENTUAL.equals(svcTipoVlr)) {
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
                        adeVlrPercentual = adeVlr;
                        adeVlr = (rseBaseCalculo.multiply(adeVlrPercentual).divide(new BigDecimal("100")));
                    } else {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.calcular.valor.reserva.pois.base.calculo.invalida", responsavel);
                    }
                }

                // Busca parâmetro de máscara para adeIdentificador para o serviço
                final CustomTransferObject paramMascaraAde = getParametroSvc(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE, svcCodigo, "", false, parametros);
                final String mascaraAdeIdentificador = (paramMascaraAde != null) ? (String) paramMascaraAde.getAttribute(Columns.PSE_VLR) : null;

                //Se usuário não pode editar ADE_IDENTIFICADOR, não deve dar erro, más seguir com a informação anterior, sem salvar a informada pelo usuário
                if (ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_ADE_IDENTIFICADOR, responsavel)) {
                    if (!TextHelper.isNull(mascaraAdeIdentificador) && !TextHelper.isNull(adeIdentificador)) {
                        try {
                            adeIdentificador = TextHelper.aplicarMascara(adeIdentificador, mascaraAdeIdentificador);
                        } catch (final ZetraException e) {
                            if (alterarParam.isValidaAdeIdentificador()) {
                                throw new AutorizacaoControllerException("mensagem.erro.ade.identificador.invalido", responsavel);
                            }
                        }
                    }
                } else {
                    adeIdentificador = autdes.getAdeIdentificador();
                }

                // So é validado quando o caso de uso é diferente de Atualizar Processo de Compra (fun codigo 280)
                if (alterarParam.isValidaAdeIdentificador()) {
                    final CustomTransferObject paramIdentificadorObrig = getParametroSvc(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO, svcCodigo, cnvCodigo, Boolean.class, null, null, null, false, true, parametros);
                    final boolean adeIdentificadorObrigatorio = ((paramIdentificadorObrig != null) && (paramIdentificadorObrig.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIdentificadorObrig.getAttribute(Columns.PSE_VLR)).booleanValue());
                    if (adeIdentificadorObrigatorio && TextHelper.isNull(adeIdentificador)) {
                        throw new AutorizacaoControllerException("mensagem.informe.ade.identificador", responsavel);
                    }
                }

                // Obtém o período atual de lançamento
                final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

                // Verifica se o índice informado na alteração é válido
                final String adeIndiceOld = autdes.getAdeIndice() != null ? autdes.getAdeIndice() : "";
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_INDICE, CodedValues.TPC_SIM, responsavel) &&
                    (TextHelper.isNull(adeIndiceOld) || TextHelper.isNull(adeIndice) || !adeIndice.equals(adeIndiceOld))) {
                    // Se somenteo automático, um novo índice deve ser gerado
                    if (ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel)) {
                        final Collection<OcorrenciaAutorizacao> ocorrencia = findByAdeTocCodigoOcaPeriodo(adeCodigo, CodedValues.TOC_ALTERACAO_INDICE, periodoAtual, responsavel);
                        if (ocorrencia.isEmpty()) {
                            adeIndice = null;
                        } else if (TextHelper.isNull(adeIndice)) {
                            adeIndice = adeIndiceOld;
                        }
                    }
                    // Se o sistema permitir o cadastro de índice e não houver valor para o índice passado por parâmetro
                    // busca um novo índice
                    adeIndice = verificaAdeIndice(adeCodigo, rseCodigo, cnvCodigo, adeIndice, autdes.getAdeCodReg(), null, false, responsavel);
                }

                // Carencia para conclusao do contrato
                paramSvc = getParametroSvc(CodedValues.TPS_CARENCIA_FINAL, svcCodigo, Short.valueOf("0"), false, parametros);
                Short carenciaFinal = paramSvc != null ? (Short) paramSvc.getAttribute(Columns.PSE_VLR) : null;

                paramSvc = getParametroSvc(CodedValues.TPS_PRAZO_CARENCIA_FINAL, svcCodigo, Short.valueOf("0"), false, parametros);
                final Short prazoCarencia = (paramSvc != null) && validarLimitesValorPrazo ? (Short) paramSvc.getAttribute(Columns.PSE_VLR) : null;

                // Permite alteração de prazo e valor para maior
                final boolean reservaSaudeConsomeMargem = (!alterarParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO) || (alterarParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO) && CodedValues.FORMA_PAGAMENTO_FOLHA.equals(alterarParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO))));
                paramSvc = getParametroSvc(CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO, svcCodigo, "", false, parametros);
                final String podeAumentarValorPrazo = validarLimitesValorPrazo ? ((paramSvc != null) && !TextHelper.isNull(paramSvc.getAttribute(Columns.PSE_VLR)) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : CodedValues.NAO_PERMITE_AUMENTAR_VLR_PRZ_CONTRATO) : CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO;
                final boolean podeAumentarValor = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(podeAumentarValorPrazo) || CodedValues.PERMITE_AUMENTAR_APENAS_VLR_CONTRATO.equals(podeAumentarValorPrazo) || !reservaSaudeConsomeMargem;
                boolean podeAumentarPrazo = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(podeAumentarValorPrazo) || CodedValues.PERMITE_AUMENTAR_APENAS_PRZ_CONTRATO.equals(podeAumentarValorPrazo);

                // Recupera o valor real de desconto, basendo-se em parâmetros de teto de desconto quando controla saldo.
                final BigDecimal vlrDesconto = this.calcularValorDescontoParcela(rseCodigo, rseDto.getCrsCodigo(), svcCodigo, adeVlr, parametros);
                final BigDecimal margemAtual = obtemMargemRestante(rseDto, adeIncMargem, csaCodigo, true, null, responsavel);

                //Parâmetro de serviço que permite ou não alterar prazo e/ou valor de contrato com margem negativa (sobrepõe ao TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO)
                paramSvc = getParametroSvc(CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA, svcCodigo, "", false, parametros);
                final String podeAumentarValorPrazoMrgNegativa = (validarLimitesValorPrazo && (paramSvc != null) && !TextHelper.isNull(paramSvc.getAttribute(Columns.PSE_VLR))) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : null;
                boolean podeDiminuirPrzMrgNegativa = true;
                final boolean margemNegativa = (margemAtual.add(autdes.getAdeVlr()).subtract(vlrDesconto).signum() == -1);
                if (margemNegativa && (!TextHelper.isNull(podeAumentarValorPrazoMrgNegativa) && CodedValues.PERMITE_REDUZIR_VLR_AUMENTAR_PRZ_CONTRATO_MRG_NETAVIA.equals(podeAumentarValorPrazoMrgNegativa))) {
                    podeAumentarPrazo = true;
                } else if (margemNegativa && (!TextHelper.isNull(podeAumentarValorPrazoMrgNegativa) && !CodedValues.PERMITE_REDUZIR_VLR_AUMENTAR_PRZ_CONTRATO_MRG_NETAVIA.equals(podeAumentarValorPrazoMrgNegativa))) {
                    podeAumentarPrazo = false;
                }

                if (margemNegativa && (!TextHelper.isNull(podeAumentarValorPrazoMrgNegativa) && CodedValues.NAO_ALTERA_VLR_E_PRAZO_MARGEM_NEGATIVA.equals(podeAumentarValorPrazoMrgNegativa))) {
                    podeDiminuirPrzMrgNegativa = false;
                }

                // Permite alteração de prazo e valor para maior
                paramSvc = getParametroSvc(CodedValues.TPS_PRESERVA_DATA_ALTERACAO, svcCodigo, Boolean.FALSE, true, parametros);
                final boolean perdePrioridade = ((paramSvc != null) && !((Boolean) paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue());

                // Parâmetro de validação de taxa de juros / CET
                final boolean validaTaxaJuros = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? alterarParam.isValidaTaxaJuros() : true;
                paramSvc = getParametroSvc(CodedValues.TPS_VALIDAR_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);
                final boolean validaTaxa = ((paramSvc != null) && ((Boolean) paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue()) && validaTaxaJuros;

                // Parâmetro para validar se exige senha na alteração de contrato
                paramSvc = getParametroSvc(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS, svcCodigo, cnvCodigo, String.class, null, null, null, false, true, parametros);
                final String exigeSenhaServidor = (!responsavel.isSer() && (paramSvc != null) && !TextHelper.isNull((paramSvc.getAttribute(Columns.PSE_VLR)))) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS;

                // Parametro para verificar se permite alterar o valor liberado do contrato (Default: TRUE)
                paramSvc = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_VLR_LIBERADO, svcCodigo, Boolean.FALSE, true, parametros);
                final boolean permiteAlterarVlrLiberado = ((paramSvc == null) || ((Boolean) paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue() || isAtualizacaoCompra);

                if (!permiteAlterarVlrLiberado) {
                    // Se não pode alterar, inicializa o valor com o atual, evitando recalculo de taxa caso outras
                    // variáveis sejam alteradas
                    adeVlrLiquido = autdes.getAdeVlrLiquido();
                }

                // Verifica alterações no prazo do contrato
                final Integer adePrazoOld = autdes.getAdePrazo();
                final int adePrdPagas = autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0;

                if (anoMesFim != null) {
                    // Se a data final passada é diferente da atual e menor que o período atual de lançamento, então retorna erro
                    if (((autdes.getAdeAnoMesFim() == null) || (anoMesFim.compareTo(autdes.getAdeAnoMesFim()) != 0)) &&
                        (anoMesFim.compareTo(periodoAtual) == -1)) {
                        throw new AutorizacaoControllerException("mensagem.dataFinalInvalida", responsavel);
                    }

                    // Calcula o prazo de acordo com a data final: Só processamento de lote passa data final
                    final Integer prazoTotal = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, autdes.getAdeAnoMesIni(), anoMesFim, autdes.getAdePeriodicidade(), responsavel);
                    adePrazo = prazoTotal.intValue() - adePrdPagas;
                }

                // Calcula o novo prazo
                Integer adePrazoNew = adePrazo != null ? adePrazo.intValue() + adePrdPagas : null;

                if ((adePrazo != null) && ParamSist.paramEquals(CodedValues.TPC_CONSIDERA_PARCELAS_AGUARD_PROCESSAMENTO, CodedValues.TPC_SIM, responsavel)) {
                    final List<ParcelaDescontoPeriodo> parcelasEmProcessamento = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);
                    adePrazoNew += parcelasEmProcessamento != null ? parcelasEmProcessamento.size() : 0;
                }

                final boolean aumentouPrazo = ((adePrazoOld != null) && (adePrazoNew == null)) || ((adePrazoOld != null) && (adePrazoNew != null) && (adePrazoOld.intValue() < adePrazoNew.intValue()));
                if (aumentouPrazo && !podeAumentarPrazo && !renegociacao) {
                    if (margemNegativa && (!TextHelper.isNull(podeAumentarValorPrazoMrgNegativa) && !CodedValues.PERMITE_REDUZIR_VLR_AUMENTAR_PRZ_CONTRATO_MRG_NETAVIA.equals(podeAumentarValorPrazoMrgNegativa))) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.aumentar.prazo.margem.negativa", responsavel);
                    }

                    // Não pode fazer operação, pois o prazo atual é menor que o novo prazo
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.esta.consignacao.pois.prazo.escolhido.maior.atual", responsavel);
                }

                final BigDecimal adeVlrOld = autdes.getAdeVlr();
                final BigDecimal diferencaAdeVlr = adeVlrOld.subtract(adeVlr);
                final boolean aumentouVlr = diferencaAdeVlr.signum() == -1;
                final boolean diminuiuVlr = diferencaAdeVlr.signum() == 1;
                if (reservaSaudeConsomeMargem && aumentouVlr && !podeAumentarValor && !renegociacao) {
                    // Não pode fazer operação, pois o valor da parcela escolhido é maior que o valor atual
                    throw new AutorizacaoControllerException("mensagem.valorParcelaMaiorQueValorAtual", responsavel);
                }

                final String serSenha = alterarParam.getSerSenha();
                final String loginExterno = alterarParam.getSerLogin();

                // Verifica se aumentou o capital devido
                final boolean aumentouCapitalDevido = (((adePrazoOld != null) && (adePrazo != null)) ? (adePrazo.doubleValue() * adeVlr.doubleValue()) > (adePrazoOld.doubleValue() * adeVlrOld.doubleValue()) : true);

                final boolean exigeSenhaAltAvancada = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? alterarParam.isExigeSenha() : true;

                // Valida a senha do servidor para fazer a alteração do contrato se não for lote ou reajuste de contratos
                if (exigeSenhaAltAvancada && (alterarParam.isValidaSenhaServidor() &&
                                              (CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS.equals(exigeSenhaServidor) ||
                                               (CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR.equals(exigeSenhaServidor) && (aumentouVlr || aumentouPrazo)) ||
                                               (CodedValues.EXIGE_SENHA_ALTERACAO_CAPITAL_DEVIDO_MAIOR.equals(exigeSenhaServidor) && aumentouCapitalDevido)))) {

                    final CustomTransferObject permiteAlterarComLimitacao = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO, responsavel);
                    List<ParcelaDescontoTO> parcelasProcessadas = null;
                    List<ParcelaDescontoPeriodo> parcelasEmProcessamento = null;

                    boolean valorAlteradoDentroLimite = false;
                    boolean permiteAlterarSemSenha = false;
                    // DESENV-10459 - Permite alterar contratos deferidos que ainda não foram enviados para a folha sem senha do servidor.
                    if ((permiteAlterarComLimitacao != null) && (permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR) != null) && "1".equals(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR))) {
                        parcelasProcessadas = parcelaController.findParcelas(adeCodigo, null, responsavel);
                        parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);

                        final Double percentualAlteracao = Double.parseDouble(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR).toString());
                        final BigDecimal valorContrato = adePrazo != null ? adeVlr.multiply(new BigDecimal(adePrazo)) : adeVlr;
                        final BigDecimal valorContratoOld = adePrazoOld != null ? adeVlrOld.multiply(new BigDecimal(adePrazoOld)) : adeVlrOld;

                        final boolean valorParcelaDentroLimite = ((adeVlr.divide(adeVlrOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;
                        final boolean valorContratoDentroLimite = ((valorContrato.divide(valorContratoOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;

                        valorAlteradoDentroLimite = valorContratoDentroLimite && valorParcelaDentroLimite;

                        if (valorAlteradoDentroLimite && CodedValues.SAD_DEFERIDA.equals(sadCodigo) && (parcelasProcessadas != null) && parcelasProcessadas.isEmpty() && (parcelasEmProcessamento != null) && parcelasEmProcessamento.isEmpty()) {
                            permiteAlterarSemSenha = true;
                        }
                        if (!permiteAlterarSemSenha && TextHelper.isNull(serSenha)) {
                            throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.limite.sem.senha", responsavel, percentualAlteracao.toString());
                        }
                    }
                    if (!permiteAlterarSemSenha) {
                        if (!TextHelper.isNull(serSenha)) {
                            SenhaHelper.validarSenhaServidor(rseCodigo, serSenha, null, loginExterno, null, false, false, responsavel);
                        } else {
                            throw new AutorizacaoControllerException("mensagem.senha.servidor.autorizacao.invalida", responsavel);
                        }
                    }
                }

                BigDecimal adeVlrValidar = adeVlr;
                if (retemMargemSvcPercentual && CodedValues.TIPO_VLR_PERCENTUAL.equals(svcTipoVlr)) {
                    adeVlrValidar = adeVlrPercentual;
                }

                // Executa validações da operação de alteração
                validaAlteracao(autdes, rseDto, cnvCodigo, csaCodigo, svcCodigo, adeVlrValidar, adePrazo, adePrazoNew, cnvAtivo, svcAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, serAtivo, isAtualizacaoCompra, aumentouVlr, aumentouPrazo, validarLimitesValorPrazo, parametros, responsavel);

                // Busca as parcelas do periodo
                final List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);

                // Verifica a quantidade de parcelas em processamento
                final int qtdParcelasEmProcessamento = (parcelasPeriodo != null ? parcelasPeriodo.size() : 0);

                // Busca o total de parcelas já integradas
                final ObtemTotalParcelasQuery totalParcelasQuery = new ObtemTotalParcelasQuery();
                totalParcelasQuery.adeCodigo = adeCodigo;
                final int qtdParcelasTotal = totalParcelasQuery.executarContador() + qtdParcelasEmProcessamento;

                validaAlteracaoUltimasParcelas(orgCodigo, adePrazoOld, adePrdPagas, adePrazoNew, qtdParcelasEmProcessamento, responsavel);

                if ((adePrazoOld != null) && (adePrazoNew != null) && (adePrazoOld.intValue() > adePrazoNew.intValue()) &&
                    ((adePrdPagas + qtdParcelasEmProcessamento) > adePrazoNew.intValue())) {
                    // Se o prazo novo é menor que antigo, e o prazo novo é menor que a quantidade de parcelas
                    // já existentes para o contratao (adePrdPagas + qtdParcelasEmProcessamento), não deixa prosseguir
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.esta.consignacao.pois.numero.parcelas.pagas.somado.em.processamento.maior.prazo.escolhido", responsavel);
                }

                // Verifica se pode alterar a Carencia do contrato
                Integer adeCarenciaNew = verificaCarencia(adeCarencia, autdes, svcCodigo, csaCodigo, orgCodigo, parametros, responsavel, qtdParcelasTotal, periodoAtual);

                // Garante que os valores estejam preenchidos, com os valores armazenados no contrato caso não
                // estejam disponíveis para edição na interface, de modo que a atualização da taxa não gere erro
                adeVlrIof = (adeVlrIof == null) ? autdes.getAdeVlrIof() : adeVlrIof;
                adeVlrTac = (adeVlrTac == null) ? autdes.getAdeVlrTac() : adeVlrTac;

                final CustomTransferObject paramValidaTaxaAlteracaoAdeAndamento = getParametroSvc(CodedValues.TPS_VALIDA_TAXA_ALTERACAO_ADE_ANDAMENTO, svcCodigo, Boolean.FALSE, false, null);

                // Verifica se deve validar a taxa de juros (parâmetro de serviço), caso as informações financeiras
                // (prazo, prestação, valor liberado, tac e iof) tenham sido alteradas
                if (validaTaxa && ((qtdParcelasTotal == 0) || (Boolean) paramValidaTaxaAlteracaoAdeAndamento.getAttribute(Columns.PSE_VLR)) && (((adePrazoNew != null) && (adePrazoOld != null) && (adePrazoNew.intValue() != adePrazoOld.intValue())) ||
                                                                                                                                                ((adeVlr != null) && (autdes.getAdeVlr() != null) && (adeVlr.doubleValue() != autdes.getAdeVlr().doubleValue())) ||
                                                                                                                                                ((adeVlrLiquido != null) && (autdes.getAdeVlrLiquido() != null) && (adeVlrLiquido.doubleValue() != autdes.getAdeVlrLiquido().doubleValue())) ||
                                                                                                                                                ((adeVlrTac != null) && (autdes.getAdeVlrTac() != null) && !adeVlrTac.equals(autdes.getAdeVlrTac())) ||
                                                                                                                                                ((adeVlrIof != null) && (autdes.getAdeVlrIof() != null) && !adeVlrIof.equals(autdes.getAdeVlrIof())))) {

                    final BigDecimal[] valores = validarTaxaJuros(adeVlr, adeVlrLiquido, adeVlrTac,
                                                                  adeVlrIof, adeVlrMensVinc, adePrazoNew,
                                                                  autdes.getAdeData(), autdes.getAdeAnoMesIni(), svcCodigo, csaCodigo,
                                                                  orgCodigo, false, parametros, adePeriodicidade, rseCodigo, responsavel);
                    // validarTaxaJuros retorna 'new BigDecimal[]{adeVlr, vlrTotal, adeVlrTac, adeVlrIof, adeVlrMensVinc};'
                    // Atualiza o valor de Tac e Iof pelo calculado na rotina de validação. Os demais dados não são alterados
                    adeVlrTac = valores[2];
                    adeVlrIof = valores[3];
                }

                // Se valida taxa de juros, e ou o prazo, valor da parcela, valor liberado, tac ou iof foram alterados
                // então a taxa de juros deve ser recalculada para atualização da informação do contrato
                final boolean recalculaTaxa = (validaTaxa && ((qtdParcelasTotal == 0) || (Boolean) paramValidaTaxaAlteracaoAdeAndamento.getAttribute(Columns.PSE_VLR)) && (((adePrazoNew != null) && (adePrazoOld != null) && (adePrazoNew.compareTo(adePrazoOld) != 0)) ||
                                                                                                                                                                             ((adeVlr != null) && (autdes.getAdeVlr() != null) && (adeVlr.compareTo(autdes.getAdeVlr()) != 0)) ||
                                                                                                                                                                             ((adeVlrLiquido != null) && (autdes.getAdeVlrLiquido() != null) && (adeVlrLiquido.compareTo(autdes.getAdeVlrLiquido()) != 0)) ||
                                                                                                                                                                             ((adeVlrTac != null) && (autdes.getAdeVlrTac() != null) && (adeVlrTac.compareTo(autdes.getAdeVlrTac()) != 0)) ||
                                                                                                                                                                             ((adeVlrIof != null) && (autdes.getAdeVlrIof() != null) && (adeVlrIof.compareTo(autdes.getAdeVlrIof()) != 0))));

                // Verifica dados de autorização em sistemas que os exigem
                String modalidadeOp = null;
                String matriculaSerCsa = null;

                final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                final boolean exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao));
                if (exigeModalidadeOperacao) {
                    modalidadeOp = alterarParam.getTdaModalidadeOperacao();
                    if (TextHelper.isNull(modalidadeOp) && responsavel.isCsaCor()) {
                        throw new AutorizacaoControllerException("mensagem.erro.modalidade.operacao.obrigatorio", responsavel);
                    }
                }

                final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                final boolean exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa));
                if (exigeMatriculaSerCsa) {
                    matriculaSerCsa = alterarParam.getTdaMatriculaSerCsa();
                    if (TextHelper.isNull(matriculaSerCsa) && responsavel.isCsaCor()) {
                        throw new AutorizacaoControllerException("mensagem.erro.matricula.csa.obrigatoria", responsavel);
                    }
                }

                java.util.Date ocaPeriodo = null;
                if (((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                      ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                     ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) &&
                    !TextHelper.isNull(alterarParam.getOcaPeriodo())) {
                    ocaPeriodo = DateHelper.parse(alterarParam.getOcaPeriodo(), "yyyy-MM-dd");
                }

                if (alteracaoForcaPeriodo && !TextHelper.isNull(alterarParam.getOcaPeriodo())) {
                    ocaPeriodo = DateHelper.parse(alterarParam.getOcaPeriodo(), "yyyy-MM-dd");
                } else if (ocaPeriodo == null) {
                    ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                }

                final boolean soAceitaExclusao = ParamSist.paramEquals(CodedValues.TPC_PERIODO_COM_APENAS_REDUCOES_SOMENTE_EXCLUSAO, CodedValues.TPC_SIM, responsavel);
                if (aumentouVlr || aumentouPrazo || soAceitaExclusao) {
                    // Se aumentou o valor ou prazo da consignação, verifica se o período escolhido pode receber a alteração,
                    // e caso não possa posterga a alteração para o período subsequente permita inclusões e alterações para maior.
                    ocaPeriodo = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), responsavel);
                }

                // Caso o sistema permita períodos agrupados ou períodos que permitem apenas reduções,
                // verificar se a consignação que está sendo alterada possui ocorrência de alteração (TOC 14)
                // para período (OCA_PERIODO) com data base maior que o período que será atribuído à ocorrência da nova alteração (OCA_PERIODO).
                // Caso possua, bloquear a alteração e enviar mensagem de erro ao usuário.
                if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) && !OcorrenciaAutorizacaoHome.findByAdeTocCodigoMaiorPeriodoOrdenado(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO, ocaPeriodo).isEmpty()) {

                    throw new AutorizacaoControllerException("mensagem.erro.nao.permite.alteracoes.periodo.futuro", responsavel);
                }

                /********************** INÍCIO ALTERAÇÃO CONTRATO ************************/
                final boolean alteraMargem = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? alterarParam.isAlteraMargem() : true;
                if (alteraMargem) {
                    // Valida a alteração de margem de acordo com a modificação dos dados do contrato
                    // e grava registro de histórico de margem para a alteração
                    historicosMargem = validaMargemAlteracao(autdes, rseDto, adeVlr, cnvCodigo, svcCodigo, csaCodigo, serAtivo, validar,
                                                             validaBloqueado, podeAumentarValor, renegociacao, alterarParam, parametros, margemAtual, responsavel);
                }

                LogDelegate log = null;
                if (!validar) {
                    log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.ALTERAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                }

                boolean alterou = false;
                boolean gerarOcorrencia = false;
                boolean ocorrenciaInformacaoDataInicioMargem = false;

                final CustomTransferObject paramSvcIncide = getParametroSvc(CodedValues.TPS_INCIDE_MARGEM, svcCodigo, adeIncMargem, false, parametros);
                final Short adeIncMargemSvc = paramSvcIncide.getAttribute(Columns.PSE_VLR) != null ? (Short) paramSvcIncide.getAttribute(Columns.PSE_VLR) : adeIncMargem;

                if ((adeCarenciaNew != null) && !validar) {
                    // Altera ADE_CARENCIA = valor real calculado pelo metodo verificaCarencia
                    final Integer adeCarenciaOld = autdes.getAdeCarencia();
                    autdes.setAdeCarencia(adeCarenciaNew);
                    log.addChangedField(Columns.ADE_CARENCIA, adeCarenciaNew, adeCarenciaOld);

                    // Altera ADE_ANO_MES_INI = periodo atual + valor digitado usuario
                    adeAnoMesIniOld = DateHelper.toSQLDate(autdes.getAdeAnoMesIni());
                    adeAnoMesIniNew = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, autdes.getAdePeriodicidade(), responsavel);

                    // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data inicio de acordo com essa data.
                    final Date dataInicioFimAde = calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesIniNew, adeIncMargemSvc, true, false, responsavel);

                    if (dataInicioFimAde.compareTo(adeAnoMesIniNew) > 0) {
                        adeAnoMesIniNew = dataInicioFimAde;
                        ocorrenciaInformacaoDataInicioMargem = true;
                        adeCarenciaNew = 0;
                    }

                    msgOca += !"".equals(msgOca) ? "<BR>" : "";
                    msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.carencia.alterada.de.arg0.para.arg1", responsavel, adeCarenciaOld.toString(), adeCarenciaNew.toString());

                    autdes.setAdeAnoMesIni(adeAnoMesIniNew);
                    log.addChangedField(Columns.ADE_ANO_MES_INI, adeAnoMesIniNew, adeAnoMesIniOld);
                    msgOca += !"".equals(msgOca) ? "<BR>" : "";
                    msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inicial.alterada.de.arg0.para.arg1", responsavel, DateHelper.toPeriodString(adeAnoMesIniOld), DateHelper.toPeriodString(adeAnoMesIniNew));

                    // Alterar campo ADE_ANO_MES_INI_REF apenas se ele estava igual ao ADE_ANO_MES_INI (ja existe outra possibilidade de alteracao mais abaixo considerando parametro "perdePrioridade")
                    if (adeAnoMesIniOld.compareTo(autdes.getAdeAnoMesIniRef()) == 0) {
                        autdes.setAdeAnoMesIniRef(adeAnoMesIniNew);
                        log.addChangedField(Columns.ADE_ANO_MES_INI_REF, adeAnoMesIniNew, adeAnoMesIniOld);
                    }

                    // Altera ADE_ANO_MES_FIM = data fim antiga + diferenca entre datas iniciais nova e antiga
                    if (autdes.getAdeAnoMesFim() != null) {
                        final Date adeAnoMesFimOld = DateHelper.toSQLDate(autdes.getAdeAnoMesFim());
                        final Date adeAnoMesFimNew = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIniNew, adePrazoNew, autdes.getAdePeriodicidade(), responsavel);

                        // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data fim de acordo com essa data.
                        calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesFimNew, adeIncMargemSvc, false, true, responsavel);

                        autdes.setAdeAnoMesFim(adeAnoMesFimNew);
                        log.addChangedField(Columns.ADE_ANO_MES_FIM, adeAnoMesFimNew, adeAnoMesFimOld);
                        msgOca += !"".equals(msgOca) ? "<BR>" : "";
                        msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.data.final.alterada.de.arg0.para.arg1", responsavel, DateHelper.toPeriodString(adeAnoMesFimOld), DateHelper.toPeriodString(adeAnoMesFimNew));

                        // Alterar campo ADE_ANO_MES_FIM_REF apenas se ele estava igual ao ADE_ANO_MES_FIM
                        if (adeAnoMesFimOld.compareTo(autdes.getAdeAnoMesFimRef()) == 0) {
                            autdes.setAdeAnoMesFimRef(adeAnoMesFimNew);
                            log.addChangedField(Columns.ADE_ANO_MES_FIM_REF, adeAnoMesFimNew, adeAnoMesFimOld);
                        }
                    }
                    alterou = true;
                    gerarOcorrencia = true;
                }

                if (aumentouPrazo && !podeAumentarPrazo && !renegociacao) {
                    if (margemNegativa && (!TextHelper.isNull(podeAumentarValorPrazoMrgNegativa) && !CodedValues.PERMITE_REDUZIR_VLR_AUMENTAR_PRZ_CONTRATO_MRG_NETAVIA.equals(podeAumentarValorPrazoMrgNegativa))) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.aumentar.prazo.margem.negativa", responsavel);
                    }
                    // Não pode fazer operação, pois o prazo atual é menor que o novo prazo
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.esta.consignacao.pois.prazo.escolhido.maior.atual", responsavel);

                } else if ((((adePrazoOld == null) ? (adePrazoNew != null) : (((adePrazoNew != null) && (adePrazoOld > adePrazoNew)) ||
                                           ((podeAumentarPrazo || renegociacao) && ((adePrazoNew == null) || (adePrazoOld < adePrazoNew)))))) &&
                           !validar) {
                    // Se o prazo era indeterminado e agora o prazo é determinado ou
                    // o prazo era determinado e ou novo prazo é menor que o atual, ou maior e
                    // é permitido o aumento do valor, então muda o prazo do contrato
                    // e nao for validação
                    alterou = true;
                    gerarOcorrencia = true;

                    //se a margem está negativa para este serviço e não pode alterar o prazo, retorna
                    //o teste do boolean já foi feito acima
                    if (!podeDiminuirPrzMrgNegativa) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.altera.prazo.margem.negativa", responsavel);
                    }

                    // Verifica se o prazo foi alterado, e caso sim, gera ocorrência de alteração
                    final String strPrzOld = (adePrazoOld == null) ? ApplicationResourcesHelper.getMessage(ROTULO_INDETERMINADO_ABREVIADO, responsavel) : String.valueOf(adePrazoOld.intValue());
                    final String strPrzNew = (adePrazoNew == null) ? ApplicationResourcesHelper.getMessage(ROTULO_INDETERMINADO_ABREVIADO, responsavel) : String.valueOf(adePrazoNew.intValue());

                    // Calcula a diferença do prazo para mensagem de ocorrência.
                    String strPrzDif = "";
                    if ((adePrazoOld == null) || (adePrazoNew == null)) {
                        strPrzDif = ApplicationResourcesHelper.getMessage(ROTULO_INDETERMINADO_ABREVIADO, responsavel);
                    } else if ((adePrazoNew.intValue() - adePrazoOld.intValue()) < 0) {
                        strPrzDif = "(" + Math.abs(adePrazoNew.intValue() - adePrazoOld.intValue()) + ")";
                    } else {
                        strPrzDif = String.valueOf(adePrazoNew.intValue() - adePrazoOld.intValue());
                    }

                    msgOca = ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.arg0.de.arg1.para.arg2", responsavel, strPrzDif, strPrzOld, strPrzNew);

                    // Altera ade_ano_mes_fim
                    // Quando a alteração é por decisão judicial, e o prazo foi alterado para menor, devemos então recalcular a data inicio do contrato para que ela seja
                    // o período atual do sistema, e assim recalcular a data fim, pois é considerado como prazo restante do contrato, assim devemos tratar como se fosse um reimplante.
                    java.util.Date adeAnoMesIni = autdes.getAdeAnoMesIni();
                    final boolean decisaoJucialPrazoMenor = CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo()) && (adePrazoNew != null) && (adePrazoOld != null) && (adePrazoOld > adePrazoNew);
                    if (decisaoJucialPrazoMenor) {
                    	adeAnoMesIni = periodoAtual;
                    	log.addChangedField(Columns.ADE_ANO_MES_INI, adeAnoMesIni, autdes.getAdeAnoMesIni());
                    	autdes.setAdeAnoMesIni(adeAnoMesIni);
                    	autdes.setAdePrdPagas(0);
                        log.addChangedField(Columns.ADE_PRD_PAGAS, 0, autdes.getAdePrdPagas());
                    }
                    final Date adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, adePrazoNew, autdes.getAdePeriodicidade(), responsavel);
                    calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesFim, adeIncMargemSvc, false, true, responsavel);

                    // Define o novo prazo
                    autdes.setAdePrazo(adePrazoNew);
                    log.addChangedField(Columns.ADE_PRAZO, adePrazoNew, adePrazoOld);

                    log.addChangedField(Columns.ADE_ANO_MES_FIM, adeAnoMesFim, autdes.getAdeAnoMesFim());

                    if (anoMesFim == null) {
                        autdes.setAdeAnoMesFim(adeAnoMesFim);
                        if (!decisaoJucialPrazoMenor) {
                        	autdes.setAdeAnoMesFimRef(adeAnoMesFim);
                        }
                    } else {
                        autdes.setAdeAnoMesFim(DateHelper.toSQLDate(anoMesFim));
                        autdes.setAdeAnoMesFimRef(DateHelper.toSQLDate(anoMesFim));
                    }
                }

                BigDecimal ocaAdeVlrAnt = null, ocaAdeVlrNew = null;
                if (aumentouVlr && !podeAumentarValor && !renegociacao) {
                    // Não pode fazer operação, pois o valor da parcela escolhido é maior que o valor atual
                    throw new AutorizacaoControllerException("mensagem.valorParcelaMaiorQueValorAtual", responsavel);
                } else if (((diferencaAdeVlr.signum() == 1) ||
                            (aumentouVlr && (podeAumentarValor || renegociacao))) &&
                           !validar) {
                    // Se o novo valor é menor, ou o novo valor é maior e o valor pode ser aumentado,
                    // e nao for validação
                    alterou = true;
                    gerarOcorrencia = true;

                    // Obtém o tipo de valor do contrato
                    final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(autdes.getAdeTipoVlr());

                    // Altera o valor da autorização
                    msgOca += !"".equals(msgOca) ? "<BR>" : "";
                    msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.alterado.arg0.arg1.de.arg2.arg3.para.arg4.arg5", responsavel,
                                                                    labelTipoVlr, (aumentouVlr ? NumberHelper.format(diferencaAdeVlr.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(diferencaAdeVlr.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                    labelTipoVlr, NumberHelper.format(autdes.getAdeVlr().doubleValue(), NumberHelper.getLang()),
                                                                    labelTipoVlr, NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang()));

                    // Log de alteração do campo ade_vlr
                    log.addChangedField(Columns.ADE_VLR, adeVlr, autdes.getAdeVlr());

                    // Valor antes/depois da alteração para criação da ocorrência
                    ocaAdeVlrAnt = autdes.getAdeVlr();
                    ocaAdeVlrNew = adeVlr;

                    // Verifica se o serviço tem controle de saldo, e se tiver soma a diferença dos valores
                    // nos campos de saldo devedor do contrato
                    final CustomTransferObject paramControleSdv = getParametroSvc(CodedValues.TPS_CONTROLA_SALDO, svcCodigo, "", false, parametros);
                    final boolean controlaSaldoDevedor = ((paramControleSdv != null) && (paramControleSdv.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramControleSdv.getAttribute(Columns.PSE_VLR)));
                    if (controlaSaldoDevedor) {
                        final BigDecimal adeVlrSdoMov = autdes.getAdeVlrSdoMov();
                        final BigDecimal adeVlrSdoRet = autdes.getAdeVlrSdoRet();
                        final BigDecimal acrescimoSaldo = diferencaAdeVlr.negate().multiply(new BigDecimal(adePrazo.doubleValue()));
                        autdes.setAdeVlrSdoMov(adeVlrSdoMov != null ? adeVlrSdoMov.add(acrescimoSaldo) : acrescimoSaldo);
                        autdes.setAdeVlrSdoRet(adeVlrSdoRet != null ? adeVlrSdoRet.add(acrescimoSaldo) : acrescimoSaldo);
                    }

                    // Verifica se o serviço tem controle de teto máximo de desconto, com controle de saldo
                    // devedor para limitar o ade_vlr baseado nos tetos
                    // Busca parâmetro de Controle de Valor máximo de desconto
                    final CustomTransferObject tpsControleVlrMaxDesconto = getParametroSvc(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO, svcCodigo, "", false, parametros);
                    final String paramControleVlrMaxDesconto = (tpsControleVlrMaxDesconto != null ? (String) tpsControleVlrMaxDesconto.getAttribute(Columns.PSE_VLR) : null);
                    final boolean possuiControleVlrMaxDesconto = (controlaSaldoDevedor && (paramControleVlrMaxDesconto != null) && !"0".equals(paramControleVlrMaxDesconto));
                    if (possuiControleVlrMaxDesconto) {
                        // Se o serviço possui controle de valor máximo de desconto, limita
                        // o adeVlr pelo mínimo entre o teto e o valor informado pelo usuário
                        if (CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO.equals(paramControleVlrMaxDesconto)) {
                            // Controle de teto pelo cargo do servidor. Busca os dados do
                            // cargo do servidor
                            if (rseDto.getCrsCodigo() != null) {
                                TransferObject cargoRegistroServidor = null;

                                final ListaCargoRegistroServidorQuery query = new ListaCargoRegistroServidorQuery();
                                query.crsCodigo = rseDto.getCrsCodigo();
                                final List<TransferObject> result = query.executarDTO();
                                if ((result != null) && (result.size() > 0)) {
                                    cargoRegistroServidor = result.get(0);
                                }

                                // Se o cargo do servidor possui valor máximo de desconto
                                // então aplica o valor
                                if ((cargoRegistroServidor != null) && (cargoRegistroServidor.getAttribute(Columns.CRS_VLR_DESC_MAX) != null)) {
                                    // Mínimo entre o valor do contrato e o teto máximo do cargo
                                    final BigDecimal vlrMaxDesconto = (BigDecimal) cargoRegistroServidor.getAttribute(Columns.CRS_VLR_DESC_MAX);
                                    adeVlr = vlrMaxDesconto.min(adeVlr);
                                }
                            }
                        } else {
                            // Demais controles de teto máximo de desconto
                        }
                    }

                    // Altera o valor do contrato com o valor passado por parâmetro
                    autdes.setAdeVlr(adeVlr);
                    // Altera o valor percentual do contrato passado por parametro
                    if ((adeVlrPercentual != null) && (adeVlrPercentual.compareTo(BigDecimal.ZERO) > 0)) {
                        autdes.setAdeVlrPercentual(adeVlrPercentual);
                    }

                    // Não altera o valor de nenhuma parcela, pois como o sistema atualmente não gera mais
                    // as parcelas previamente, então nada deve ser alterado. A parcela do periodo já em
                    // processamento deve permanecer com o valor antes da alteração, portanto também não
                    // deve ser alterada. A rotina abaixo calcula o período anterior para verificar se o
                    // sistema já gerou uma parcela para este contrato do periodo anterior. Se não existir
                    // ela deve ser criada com o valor anterior a alteração de valor.
                    final Date periodoAnterior = PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);

                    // Determina se uma parcela deve ser criada, salvando o valor do contrato
                    // antes da alteração, que teria sido feita após o corte e antes da exportação
                    final Date dataIniADE = DateHelper.toSQLDate(autdes.getAdeAnoMesIni());
                    final Short adeIntFolha = (autdes.getAdeIntFolha() != null ? autdes.getAdeIntFolha() : CodedValues.INTEGRA_FOLHA_SIM);

                    // Começa avaliando a data inicial do contrato (que deve ser anterior ou igual ao
                    // período que será gerado uma parcela) e o flag de integra folha
                    boolean inserePrd = ((dataIniADE.compareTo(periodoAnterior) <= 0) && adeIntFolha.equals(CodedValues.INTEGRA_FOLHA_SIM));
                    int maxPrdNumero = 0;

                    // Navega nas parcelas do periodo e veja se já existe a parcela para o contrato
                    if (inserePrd && ((parcelasPeriodo != null) && !parcelasPeriodo.isEmpty())) {
                        ParcelaDescontoPeriodo pdp = null;
                        for (final ParcelaDescontoPeriodo element : parcelasPeriodo) {
                            pdp = element;
                            final Date dataDesconto = DateHelper.toSQLDate(pdp.getPrdDataDesconto());
                            maxPrdNumero = Math.max(maxPrdNumero, pdp.getPrdNumero().intValue());
                            if (dataDesconto.compareTo(periodoAnterior) == 0) {
                                // Se já existe uma parcela do contrato para o periodo anterior,
                                // então desabilita a inserção da parcela
                                inserePrd = false;
                                break;
                            }
                        }
                    }
                    if (inserePrd) {
                        // Se não encontrou parcela para o contrato na tabela do período, verifica nas parcelas históricas
                        // se a mesma já foi processada pela folha
                        final List<ParcelaDesconto> parcelasHistorico = ParcelaDescontoHome.findByAutDesconto(adeCodigo);
                        if ((parcelasHistorico != null) && !parcelasHistorico.isEmpty()) {
                            ParcelaDesconto prd = null;
                            for (final ParcelaDesconto element : parcelasHistorico) {
                                prd = element;
                                final Date dataDesconto = DateHelper.toSQLDate(prd.getPrdDataDesconto());
                                maxPrdNumero = Math.max(maxPrdNumero, prd.getPrdNumero().intValue());
                                if (dataDesconto.compareTo(periodoAnterior) == 0) {
                                    // Se já existe uma parcela do contrato para o periodo anterior,
                                    // então desabilita a inserção da parcela
                                    inserePrd = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (inserePrd) {
                        // Verifica se já teve o retorno do período para o qual a parcela será incluída, porque se já tiver
                        // não pode incluir a parcela, pois ela nunca será atualizada.
                        Date ultPeriodoRetorno = null;
                        final ObtemUltimoPeriodoRetornoQuery ultRetornoQuery = new ObtemUltimoPeriodoRetornoQuery();
                        ultRetornoQuery.orgCodigo = orgCodigo;
                        final List<Date> ultPeriodoList = ultRetornoQuery.executarLista();
                        if ((ultPeriodoList != null) && !ultPeriodoList.isEmpty()) {
                            ultPeriodoRetorno = ultPeriodoList.get(0);
                        }
                        if ((ultPeriodoRetorno != null) && (ultPeriodoRetorno.compareTo(periodoAnterior) >= 0)) {
                            // Se o último período de retorno é maior ou igual ao período que a parcela será
                            // gerada, então não deve inserir uma nova parcela.
                            inserePrd = false;
                        }
                    }
                    if (inserePrd) {
                        // Verifica se a consignação estava suspensa e foi reativada pós corte, neste caso
                        // não deve ser incluída a parcela para o período
                        final List<OcorrenciaAutorizacao> ocaReativacaoPosCorte = OcorrenciaAutorizacaoHome.findByAdeTocCodigoMaiorPeriodoOrdenado(adeCodigo, CodedValues.TOC_REATIVACAO_CONTRATO, periodoAnterior);
                        if ((ocaReativacaoPosCorte != null) && !ocaReativacaoPosCorte.isEmpty()) {
                            final List<OcorrenciaAutorizacao> ocaSuspensaoPosCorte = OcorrenciaAutorizacaoHome.findByAdeTocCodigoMaiorPeriodoOrdenado(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO, periodoAnterior);
                            if ((ocaSuspensaoPosCorte == null) || ocaSuspensaoPosCorte.isEmpty()) {
                                // Se tem ocorrência de reativação pós corte e não tem ocorrência de suspensão,
                                // então significa que o contrato estava suspenso e foi reativado, então não
                                // devemos inserir parcela
                                inserePrd = false;
                            } else {
                                // Se tem ocorrência de suspensão pós corte, verifica se a suspensão é anterior
                                // ou posterior à reativação, para tratar casos de operações em sequência.
                                // As listas já estão ordenadas, então basta pegar a primeira data
                                final java.util.Date minDataSuspensao = ocaSuspensaoPosCorte.get(0).getOcaData();
                                final java.util.Date minDataReativacao = ocaReativacaoPosCorte.get(0).getOcaData();
                                // Se a data de reativação é menor que de suspensão, então ela veio primeiro,
                                // significa que o contrato estava suspenso e foi reativado
                                if (minDataReativacao.compareTo(minDataSuspensao) < 0) {
                                    inserePrd = false;
                                }
                            }
                        }
                    }
                    // Verifica se a situação atual da consignação é suspensa, não deve ser incluída parcela para o período
                    if (inserePrd && CodedValues.SAD_CODIGOS_SUSPENSOS.contains(sadCodigo)) {
                        inserePrd = false;
                    }
                    if (inserePrd) {
                        // Se a alteração é depois do corte e ainda não foi exportado o movimento, então insere uma parcela
                        // na tabela de parcelas do período com o valor antigo do contrato
                        final Short prdNumero = Integer.valueOf(maxPrdNumero + 1).shortValue();
                        ParcelaDescontoPeriodoHome.create(adeCodigo, prdNumero, CodedValues.SPD_EMPROCESSAMENTO, periodoAnterior, adeVlrOld);
                    }
                }

                if (!validar) {
                    // Altera o adeIdentificador
                    final String adeIdentificadorOld = autdes.getAdeIdentificador() != null ? autdes.getAdeIdentificador() : "";
                    adeIdentificador = (adeIdentificador == null) ? "" : adeIdentificador;
                    if (!adeIdentificador.equals(adeIdentificadorOld)) {
                        msgOca += !"".equals(msgOca) ? "<BR>" : "";
                        msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.identificado.alterado.de.arg0.para.arg1", responsavel, adeIdentificadorOld, adeIdentificador);

                        autdes.setAdeIdentificador(adeIdentificador);
                        alterou = true;
                        log.addChangedField(Columns.ADE_IDENTIFICADOR, adeIdentificador, adeIdentificadorOld);
                    }

                    // Altera o adeIndice, gera ocorrência e salva o índice anterior
                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_INDICE, CodedValues.TPC_SIM, responsavel) &&
                        ((adeIndice != null) && !adeIndice.equals(adeIndiceOld))) {
                        final String msgAlteracaoIndice = ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.indice.alterado.de.arg0.para.arg1", responsavel, adeIndiceOld, adeIndice);

                        msgOca += (!"".equals(msgOca) ? "<BR>" : "") + msgAlteracaoIndice;

                        autdes.setAdeIndice(adeIndice);
                        log.addChangedField(Columns.ADE_INDICE, adeIndice, adeIndiceOld);

                        // Cria ocorrência de alteração do índice
                        criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_INDICE, msgAlteracaoIndice, null, null, null, ocaPeriodo, null, responsavel);

                        // Salva dado autorização com o índice anterior
                        setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_INDICE_ANTERIOR, adeIndiceOld, responsavel);

                        alterou = true;
                        gerarOcorrencia = true;
                    }

                    // Verifica o prazo para carencia
                    if (prazoCarencia != null) {
                        if (adePrazo != null) {
                            if (prazoCarencia.intValue() != adePrazo.intValue()) {
                                carenciaFinal = null;
                            }
                        } else {
                            carenciaFinal = null;
                        }
                    }

                    // Adicionando a carencia para termino do contrato.
                    if (((carenciaFinal != null) && (autdes.getAdeCarenciaFinal() == null)) ||
                        ((carenciaFinal == null) && (autdes.getAdeCarenciaFinal() != null)) ||
                        ((carenciaFinal != null) && (autdes.getAdeCarenciaFinal() != null) && !carenciaFinal.equals(autdes.getAdeCarenciaFinal()))) {
                        autdes.setAdeCarenciaFinal(carenciaFinal);
                        log.addChangedField(Columns.ADE_CARENCIA_FINAL, carenciaFinal, autdes.getAdeCarenciaFinal());
                    }

                    // Verifica parametros de sistema necessários
                    final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                    final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
                    final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

                    CustomTransferObject prox = getParametroSvc(CodedValues.TPS_CAD_VALOR_TAC, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean permiteCadVlrTac = !temCET && (prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)).booleanValue() : false);
                    prox = getParametroSvc(CodedValues.TPS_CAD_VALOR_IOF, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean permiteCadVlrIof = !temCET && (prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)).booleanValue() : false);
                    prox = getParametroSvc(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean permiteCadVlrLiqLib = prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)) : false;
                    prox = getParametroSvc(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean permiteCadVlrMensVinc = !temCET && (prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)).booleanValue() : false);
                    prox = getParametroSvc(CodedValues.TPS_VLR_LIQ_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean permiteVlrLiqTxJuros = prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)) : false;
                    prox = getParametroSvc(CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA, svcCodigo, Boolean.FALSE, false, parametros);
                    final boolean exigeAdeVlrSegPrestamista = !temCET && (prox.getAttribute(Columns.PSE_VLR) != null ? ((Boolean) prox.getAttribute(Columns.PSE_VLR)).booleanValue() : false);

                    // Se permitir cadastro de informações financeiras
                    if (permiteCadVlrTac) {
                        // Altera o adeVlrTac
                        final BigDecimal adeVlrTacOld = autdes.getAdeVlrTac() != null ? autdes.getAdeVlrTac() : new BigDecimal(0);
                        if ((adeVlrTac != null) && (adeVlrTac.compareTo(adeVlrTacOld) != 0)) {
                            final BigDecimal dif = adeVlrTacOld.subtract(adeVlrTac);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.tac.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrTacOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrTac.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeVlrTac(adeVlrTac);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_TAC, adeVlrTac, adeVlrTacOld);
                        }
                    }

                    if (permiteCadVlrIof) {
                        // Altera o adeVlrIof
                        final BigDecimal adeVlrIofOld = autdes.getAdeVlrIof() != null ? autdes.getAdeVlrIof() : new BigDecimal(0);
                        if ((adeVlrIof != null) && (adeVlrIof.compareTo(adeVlrIofOld) != 0)) {
                            final BigDecimal dif = adeVlrIofOld.subtract(adeVlrIof);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.iof.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrIofOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrIof.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeVlrIof(adeVlrIof);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_IOF, adeVlrIof, adeVlrIofOld);
                        }
                    }

                    if (permiteCadVlrMensVinc) {
                        // Altera o adeVlrMensVinc
                        final BigDecimal adeVlrMensVincOld = autdes.getAdeVlrMensVinc() != null ? autdes.getAdeVlrMensVinc() : new BigDecimal(0);
                        if ((adeVlrMensVinc != null) && (adeVlrMensVinc.compareTo(adeVlrMensVincOld) != 0)) {
                            final BigDecimal dif = adeVlrMensVincOld.subtract(adeVlrMensVinc);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.mens.vinculada.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrMensVincOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrMensVinc.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeVlrMensVinc(adeVlrMensVinc);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_MENS_VINC, adeVlrMensVinc, adeVlrMensVincOld);
                        }
                    }

                    // Se exigir cadastro de seguro prestamista
                    if (exigeAdeVlrSegPrestamista) {
                        // Altera o AdeVlrSegPrestamista
                        final BigDecimal adeVlrSegPrestamistaOld = autdes.getAdeVlrSegPrestamista() != null ? autdes.getAdeVlrSegPrestamista() : new BigDecimal(0);
                        if ((adeVlrSegPrestamista != null) && (adeVlrSegPrestamista.compareTo(adeVlrSegPrestamistaOld) != 0)) {
                            final BigDecimal dif = adeVlrSegPrestamistaOld.subtract(adeVlrSegPrestamista);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.seguro.prestamista.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrSegPrestamistaOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrSegPrestamista.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeVlrSegPrestamista(adeVlrSegPrestamista);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_SEG_PRESTAMISTA, adeVlrSegPrestamista, adeVlrSegPrestamistaOld);
                        }
                    }

                    // Se permitir cadastro taxa de juros
                    if (permiteVlrLiqTxJuros) {
                        // Altera o adeTaxaJuros
                        final BigDecimal adeTaxaJurosOld = autdes.getAdeTaxaJuros() != null ? autdes.getAdeTaxaJuros() : new BigDecimal(0);
                        if ((adeTaxaJuros != null) && (adeTaxaJuros.compareTo(adeTaxaJurosOld) != 0)) {
                            final BigDecimal dif = adeTaxaJurosOld.subtract(adeTaxaJuros);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.arg0.alterado.arg1.de.arg2.para.arg3", responsavel,
                                                                            (temCET ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)).toUpperCase(),
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeTaxaJurosOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeTaxaJuros.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeTaxaJuros(adeTaxaJuros);
                            alterou = true;
                            log.addChangedField(Columns.ADE_TAXA_JUROS, adeTaxaJuros, adeTaxaJurosOld);
                        }
                    }

                    if (exigeModalidadeOperacao) {
                        final String modalidadeOpOld = getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MODALIDADE_OPERACAO, false, responsavel);

                        if (!TextHelper.isNull(modalidadeOp) && !modalidadeOp.equals(modalidadeOpOld)) {
                            setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_MODALIDADE_OPERACAO, modalidadeOp, responsavel);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.modalidade.operacao.alterada.de.arg0.para.arg1", responsavel, TextHelper.isNull(modalidadeOpOld) ? "" : modalidadeOpOld, TextHelper.isNull(modalidadeOp) ? "" : modalidadeOp);

                            alterou = true;
                        }
                    }

                    if (exigeMatriculaSerCsa) {
                        final String matriculaSerCsaOld = getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MATRICULA_SER_NA_CSA, false, responsavel);

                        if (!TextHelper.isNull(matriculaSerCsa) && !matriculaSerCsa.equals(matriculaSerCsaOld)) {
                            setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_MATRICULA_SER_NA_CSA, matriculaSerCsa, responsavel);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.matricula.ser.csa.alterada.de.arg0.para.arg1", responsavel, TextHelper.isNull(matriculaSerCsaOld) ? "" : matriculaSerCsaOld, TextHelper.isNull(matriculaSerCsa) ? "" : matriculaSerCsa);

                            alterou = true;
                        }
                    }

                    // Altera de forma genêrica os dados da autorização
                    final List<TransferObject> dadList = lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST_LOTE_WEB, svcCodigo, csaCodigo, responsavel);
                    for (final TransferObject dad : dadList) {
                        final String tdaCodigo = (String) dad.getAttribute(Columns.TDA_CODIGO);
                        if (alterarParam.existsDadoAutorizacao(tdaCodigo)) {
                            setDadoAutDesconto(adeCodigo, tdaCodigo, alterarParam.getDadoAutorizacao(tdaCodigo), responsavel);
                        }
                    }
                    // A alteração deste dado adicional fica fora do loop por não ser um tda visível, campo utilizado fora da visibilidade na web e host a host
                    if (alterarParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)) {
                        setDadoAutDesconto(adeCodigo, CodedValues.TDA_FORMA_PAGAMENTO, alterarParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO), responsavel);
                    }

                    // Se permitir cadastro do valor liberado e pode ser alterado
                    if (permiteCadVlrLiqLib && permiteAlterarVlrLiberado) {
                        // Altera o adeVlrLiquido
                        final BigDecimal adeVlrLiquidoOld = autdes.getAdeVlrLiquido() != null ? autdes.getAdeVlrLiquido() : new BigDecimal(0);
                        if ((adeVlrLiquido != null) && (adeVlrLiquido.compareTo(adeVlrLiquidoOld) != 0)) {
                            final BigDecimal dif = adeVlrLiquidoOld.subtract(adeVlrLiquido);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.liquido.liberado.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrLiquidoOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrLiquido.doubleValue(), NumberHelper.getLang()));

                            autdes.setAdeVlrLiquido(adeVlrLiquido);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_LIQUIDO, adeVlrLiquido, adeVlrLiquidoOld);
                        }
                    }

                    if (adeVlrParcelaFolha != null) {
                        // Altera o adeVlrParcelaFolha
                        final BigDecimal adeVlrParcelaFolhaOld = autdes.getAdeVlrParcelaFolha() != null ? autdes.getAdeVlrParcelaFolha() : autdes.getAdeVlr();
                        if (adeVlrParcelaFolhaOld.compareTo(adeVlrParcelaFolha) != 0) {
                            final BigDecimal dif = adeVlrParcelaFolhaOld.subtract(adeVlrParcelaFolha);

                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.folha.alterado.arg0.de.arg1.para.arg2", responsavel,
                                                                            (dif.signum() == -1 ? NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(dif.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                                                            NumberHelper.format(adeVlrParcelaFolhaOld.doubleValue(), NumberHelper.getLang()),
                                                                            NumberHelper.format(adeVlrParcelaFolha.doubleValue(), NumberHelper.getLang()));
                        }

                        if ((adeVlrParcelaFolha.compareTo(autdes.getAdeVlr()) == 0) || aumentouVlr) {
                            // Se o valor da parcela para desconto na folha é igual ao valor de face do contrato,
                            // ou o valor foi aumentado, então grava NULL no campo, sendo portanto o valor a ser
                            // descontado o mesmo valor que está preso na margem
                            adeVlrParcelaFolha = null;
                        }

                        if ((adeVlrParcelaFolha == null) || (adeVlrParcelaFolhaOld.compareTo(adeVlrParcelaFolha) != 0)) {
                            autdes.setAdeVlrParcelaFolha(adeVlrParcelaFolha);
                            alterou = true;
                            log.addChangedField(Columns.ADE_VLR_PARCELA_FOLHA, adeVlrParcelaFolha, adeVlrParcelaFolhaOld);
                        }
                    }

                    if (temSimulacaoConsignacao && simulacaoPorTaxaJuros && recalculaTaxa) {
                        // Recalcula a taxa de juros efetiva para atualizar a informação do contrato
                        final BigDecimal taxa = calcularTaxaJurosEfetiva(adeCodigo, adeVlr, adeVlrLiquido, adeVlrTac, adeVlrIof, adePrazoNew, autdes.getAdeData(),
                                                                         autdes.getAdeAnoMesIni(), svcCodigo, orgCodigo, simulacaoPorTaxaJuros, adePeriodicidade, responsavel);

                        if (taxa != null) {
                            atualizaCoeficiente(responsavel, adeCodigo, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, taxa);
                        }
                    }

                    if (perdePrioridade && alterou) {
                        // Altera o ade_ano_mes_ini_ref -> perdendo a prioridade do contrato
                        final Date adeAnoMesIniRefOld = DateHelper.toSQLDate(autdes.getAdeAnoMesIniRef());
                        if (((adeAnoMesIniRefOld != null) && (adeAnoMesIniRefOld.compareTo(periodoAtual) != 0)) || (adeAnoMesIniRefOld == null)) {
                            autdes.setAdeAnoMesIniRef(periodoAtual);
                            alterou = true;
                            gerarOcorrencia = true;
                            log.addChangedField(Columns.ADE_ANO_MES_INI_REF, periodoAtual, adeAnoMesIniRefOld);
                        }
                    }

                    // Verifica informações avançadas que serão alteradas na alteração avançada de consignação
                    if (usuPossuiAltAvancadaAde && isAlteracaoAvancada) {
                        if (!TextHelper.isNull(alterarParam.getAdeIntFolha()) && (alterarParam.getAdeIntFolha().compareTo(autdes.getAdeIntFolha()) != 0)) {
                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.integra.folha.alterado.de.arg0.para.arg1", responsavel,
                                                                            (autdes.getAdeIntFolha().compareTo(CodedValues.INTEGRA_FOLHA_SIM) == 0 ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase()),
                                                                            (alterarParam.getAdeIntFolha().compareTo(CodedValues.INTEGRA_FOLHA_SIM) == 0 ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase()));

                            log.addChangedField(Columns.ADE_INT_FOLHA, alterarParam.getAdeIntFolha(), autdes.getAdeIntFolha());
                            alterou = true;
                        }
                        if (!TextHelper.isNull(alterarParam.getAdeIncideMargem()) && (alterarParam.getAdeIncideMargem().compareTo(adeIncMargem) != 0)) {
                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.incide.margem.alterado.de.arg0.para.arg1", responsavel,
                                                                            MargemHelper.getInstance().getMarDescricao(adeIncMargem, responsavel),
                                                                            MargemHelper.getInstance().getMarDescricao(alterarParam.getAdeIncideMargem(), responsavel));

                            log.addChangedField(Columns.ADE_INC_MARGEM, alterarParam.getAdeIncideMargem(), adeIncMargem);
                            alterou = true;
                        }

                        if (!TextHelper.isNull(alterarParam.getNovaSituacaoContrato()) &&
                            !alterarParam.getNovaSituacaoContrato().equals(sadCodigo) &&
                            CodedValues.SAD_CODIGOS_ALTERACAO_AVANCADA.contains(alterarParam.getNovaSituacaoContrato())) {

                            // Recupera descrições das situações do contrato para gerar observação da ocorrência
                            final com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto sad = com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto.getInstance();
                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.alterada.de.arg0.para.arg1", responsavel,
                                                                            sad.getDescricao(sadCodigo).toString().toUpperCase(),
                                                                            sad.getDescricao(alterarParam.getNovaSituacaoContrato()).toString().toUpperCase());

                            log.addChangedField(Columns.ADE_SAD_CODIGO, alterarParam.getNovaSituacaoContrato(), sadCodigo);
                            alterou = true;
                        }
                    }

                    // DESENV-15252 : Consignação em estoque com valor alterado para adequação à margem deve ser alterado para deferido
                    if (ParamSist.paramEquals(CodedValues.TPC_ADE_ESTOQUE_ADEQUADA_MARGEM_ALTERAR_PARA_DEFERIDA, CodedValues.TPC_SIM, responsavel) && diminuiuVlr &&
                        (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo))) {
                        // Consignação em estoque, teve seu valor diminuído, e o parâmetro diz que deve alterar para Deferida se for adequada à margem
                        // Verifica então se a margem restante ficará positiva ou pelo menos zero
                        boolean margemPositivaOuZero = false;
                        if ((historicosMargem != null) && !historicosMargem.isEmpty()) {
                            for (final HistoricoMargemRse historicoMargem : historicosMargem) {
                                if (historicoMargem.getMargem().getMarCodigo() == adeIncMargem) {
                                    margemPositivaOuZero = historicoMargem.getHmrMargemDepois().signum() >= 0;
                                }
                            }
                        }

                        if (margemPositivaOuZero) {
                            final String sadCodigoNew = CodedValues.SAD_DEFERIDA;

                            // Recupera descrições das situações do contrato para gerar observação da ocorrência
                            final com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto sad = com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto.getInstance();
                            msgOca += !"".equals(msgOca) ? "<BR>" : "";
                            msgOca += ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.alterada.de.arg0.para.arg1", responsavel,
                                                                            sad.getDescricao(sadCodigo).toString().toUpperCase(),
                                                                            sad.getDescricao(sadCodigoNew).toString().toUpperCase());

                            autdes.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(sadCodigoNew));

                            log.addChangedField(Columns.ADE_SAD_CODIGO, sadCodigoNew, sadCodigo);
                            alterou = true;
                        }
                    }

                    //Parâmetro de serviço que permite pagamento por boleto
                    paramSvc = getParametroSvc(CodedValues.TPS_PERMITE_DESCONTO_VIA_BOLETO, svcCodigo, "", false, parametros);
                    final String svcVlr = ((paramSvc != null) && !TextHelper.isNull((paramSvc.getAttribute(Columns.PSE_VLR)))) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : "";

                    if (!alterou && !TextHelper.isNull(svcTipoVlr) && !TextHelper.isNull(svcVlr) && !CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO.equals(svcVlr) && alterarParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)) {
                        alterou = true;
                        gerarOcorrencia = true;
                    }

                    // Gera ocorrência de alteração
                    String ocaCodigo = null;
                    if (alterou) {
                        AbstractEntityHome.update(autdes);
                        // Gera ocorrencia caso tenha alguma alteração e caso não tenha sido incluída uma ocorrência para alteração de status
                        final boolean incluiOcorrencia = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? TextHelper.isNull(alterarParam.getNovaSituacaoContrato()) && alterarParam.isIncluiOcorrencia() : true;
                        if (gerarOcorrencia && incluiOcorrencia) {
                            ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO, ("".equals(msgOca) ? ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_ALTERACAO_CONTRATO, responsavel) : msgOca), ocaAdeVlrAnt, ocaAdeVlrNew, null, ocaPeriodo, null, responsavel);
                            // Atualiza os históricos de margem
                            atualizaHistoricosMargem(historicosMargem, ocaCodigo, null, null);
                        } else {
                            ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ("".equals(msgOca) ? ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_ALTERACAO_CONTRATO, responsavel) : msgOca), ocaAdeVlrAnt, ocaAdeVlrNew, null, ocaPeriodo, null, responsavel);
                        }

                        final Object paramValidaVlrEOuPrzAlteracaoSemAnexo = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDA_VLR_E_OU_PRZ_EXPORTA_ALT_SEM_ANEXO, responsavel);
                        if (ParamSist.paramEquals(CodedValues.TPC_EXPORTA_INCL_ALT_ADE_SEM_ANEXO_PERIODO, CodedValues.TPC_NAO, responsavel) &&
                            !TextHelper.isNull(paramValidaVlrEOuPrzAlteracaoSemAnexo)) {
                            Integer validaVlrEOuPrzAlteracaoSemAnexo = CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO;
                            try {
                                // Caso parâmetro de sistema esteja configurado errado, padrão será validar somente alteração de valor para maior
                                validaVlrEOuPrzAlteracaoSemAnexo = Integer.parseInt(paramValidaVlrEOuPrzAlteracaoSemAnexo.toString());
                            } catch (final NumberFormatException e) {
                                LOG.error("Parâmetro para validar valor e/ou prazo na alteração sem anexo inválido.", e);
                            }

                            if ((validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO) && aumentouVlr) ||
                                (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_PRZ_EXPORTA_ALT_SEM_ANEXO) && aumentouPrazo) ||
                                (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_E_PRZ_EXPORTA_ALT_SEM_ANEXO) && aumentouPrazo && aumentouVlr) ||
                                (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_OU_PRZ_EXPORTA_ALT_SEM_ANEXO) && (aumentouPrazo || aumentouVlr))) {
                                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR, ("".equals(msgOca) ? ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_ALTERACAO_CONTRATO, responsavel) : msgOca), ocaAdeVlrAnt, ocaAdeVlrNew, null, ocaPeriodo, null, responsavel);

                                //DESENV-19997: Somente criar a solicitação de pendente na alteração quando estiver de acordo com o parâmetro de sistema 567 e o contrato envia com anexo o movimento.
                                if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                                    final String origemSolicitacao = OrigemSolicitacaoEnum.ORIGEM_ALTERACAO_PARA_MAIOR.getCodigo();
                                    SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
                                                                                       StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, null, null, origemSolicitacao, DateHelper.clearHourTime(autdes.getAdeAnoMesIni()));
                                }
                            }
                        }

                        if (ocorrenciaInformacaoDataInicioMargem) {
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior", responsavel), responsavel);
                        }

                        /* DESENV-18320
                         * 1) Ao alterar a carência de um contrato seja para maior ou menor, o sistema deve verificar se existe um anexo no
                         * mesmo período do contrato antes da alteração da carência e se tiver, deve alterar o período desse anexo para
                         * o mesmo período do contrato após o novo cálculo do período do contrato (período após a alteração de carência)
                         *
                         * 2) Estando habilitado o parâmetro de Sistema (880), se houver um registro na tb_solicitacao_autorizacao para a ADE que teve carência alterada com status solicitação
                         * igual a "Validação de documentos aprovada" ou "Validação de documentos reprovada" e o período do registro era igual ao período do contrato antes da alteração de carência,
                         * alterar também o período do registro na tb_solicitacao_autorizacao para o novo período do contrato.
                        */

                        if ((adeCarenciaNew != null) && !validar) {

                            final CustomTransferObject cto = new CustomTransferObject();
                            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                            cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                            final List<TransferObject> aadList = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);

                            if ((aadList != null) && !aadList.isEmpty()) {
                                for (final TransferObject aad : aadList) {
                                    if (adeAnoMesIniOld.equals(aad.getAttribute(Columns.AAD_PERIODO))) {
                                        aad.setAttribute(Columns.AAD_ADE_CODIGO, aad.getAttribute(Columns.ADE_CODIGO));
                                        aad.setAttribute(Columns.AAD_PERIODO, adeAnoMesIniNew);
                                        editarAnexoConsignacaoController.updateAnexoAutorizacaoDesconto(new CustomTransferObject(aad), responsavel);
                                    }
                                }
                            }

                            if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                                final String[] ssoCodigos = { StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo(), StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo() };
                                final String[] tisCodigos = { TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo() };

                                final Collection<SolicitacaoAutorizacao> solicitacaoSaldoInf = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);

                                for (final SolicitacaoAutorizacao solicitacao : solicitacaoSaldoInf) {
                                    if (solicitacao.getSoaPeriodo() == null) {
                                        final TransferObject periodo = periodoController.obtemPeriodoPorData(Arrays.asList(orgCodigo), null, solicitacao.getSoaData(), responsavel);
                                        if (adeAnoMesIniOld.equals(periodo.getAttribute(Columns.PEX_PERIODO))) {
                                            solicitacao.setSoaPeriodo(adeAnoMesIniNew);
                                            AbstractEntityHome.update(solicitacao);
                                        }

                                    } else if (solicitacao.getSoaPeriodo().equals(adeAnoMesIniOld)) {
                                        solicitacao.setSoaPeriodo(adeAnoMesIniNew);
                                        AbstractEntityHome.update(solicitacao);
                                    }
                                }
                            }
                        }
                    }

                    // Verifica se a função exige tipo de motivo da operação e se a função realmente é de alteração
                    final boolean funcaoAlteracao = TextHelper.isNull(responsavel.getFunCodigo()) || CodedValues.FUN_ALT_CONSIGNACAO.equals(responsavel.getFunCodigo());
                    boolean exigeMotivoOperacao = (ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && funcaoAlteracao &&
                                                   FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_ALT_CONSIGNACAO, responsavel)) ||
                                                  (usuPossuiAltAvancadaAde && isAlteracaoAvancada && avancada(alterarParam, responsavel));

                    if (exigeMotivoOperacao) {
                        final String tpaPermiteAlterarAdeSemMotivoOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_ALTERAR_ADE_SEM_MOTIVO_OPERACAO_VIA_LOTE, responsavel);
                        final boolean permiteAlterarAdeSemMotivoOperacao = !TextHelper.isNull(tpaPermiteAlterarAdeSemMotivoOperacao) && "S".equals(tpaPermiteAlterarAdeSemMotivoOperacao);

                        // Se é alteração via lote e permite alteração sem informar motivo de operação via lote
                        // define que o motivo não é exigido para que não reporte erro caso não seja informado
                        if (alteracaoViaLote && !isAlteracaoAvancada && permiteAlterarAdeSemMotivoOperacao) {
                            exigeMotivoOperacao = false;
                        }
                    }

                    // Tipo motivo da operação é obrigatório para alteração e não foi informado
                    if (exigeMotivoOperacao && TextHelper.isNull(alterarParam.getTmoCodigo())) {
                        throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
                    }

                    // Insere motivo de cancelamento
                    CustomTransferObject tmo = null;
                    if (!TextHelper.isNull(alterarParam.getTmoCodigo()) && !TextHelper.isNull(ocaCodigo)) {
                        try {
                            tmo = new CustomTransferObject();
                            tmo.setAttribute(Columns.ADE_CODIGO, alterarParam.getAdeCodigo());
                            tmo.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                            tmo.setAttribute(Columns.TMO_CODIGO, alterarParam.getTmoCodigo());
                            tmo.setAttribute(Columns.OCA_OBS, alterarParam.getOcaObs());
                            tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tmo, responsavel);
                        } catch (final TipoMotivoOperacaoControllerException e) {
                            if (exigeMotivoOperacao) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.motivo.operacao.para.consignacao", responsavel, e);
                            } else {
                                LOG.error("Não foi possível inserir motivo da operação para a consignação: ['" + alterarParam.getAdeCodigo() + "']. Motivo da operação: " + alterarParam.getTmoCodigo(), e);
                            }
                        }
                    }

                    // Anexa arquivo oriundo do serviço SOAP.
                    final File anexo = alterarParam.getAnexo();
                    if ((anexo != null) && anexo.exists()) {
                        editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), anexo.getName(), DateHelper.toSQLDate(ocaPeriodo), null, responsavel);
                    }

                    if (isAlteracaoAvancada) {
                        // Realiza alterações avançadas na consignação
                        alteracoesAvancadas(autdes, alterarParam, rseDto, cnvCodigo, svcCodigo, csaCodigo, parametros, ocaCodigo, ocaPeriodo, orgCodigo, diferencaAdeVlr, responsavel);

                        // Adiciona informações de opções avançadas
                        String altAvancada = ApplicationResourcesHelper.getMessage("mensagem.informacao.alteracao.avancada", responsavel) + " ";
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.permiteAltEntidadesBloqueadas", responsavel) + (alterarParam.isPermiteAltEntidadesBloqueadas() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.afetaMargem", responsavel) + (alterarParam.isAlteraMargem() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.validaMargem", responsavel) + (alterarParam.isValidaMargem() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.valorPrazoSemLimite", responsavel) + (alterarParam.isAlterarValorPrazoSemLimite() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.validaReservaCartao", responsavel) + (alterarParam.isValidaReservaCartao() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.exigeSenha", responsavel) + (alterarParam.isExigeSenha() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.novoContratoDif", responsavel) + (alterarParam.isCriarNovoContratoDif() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.calcularPrazoDifValor", responsavel) + (alterarParam.isCalcularPrazoDifValor() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.manterDifValorMargem", responsavel) + (alterarParam.isManterDifValorMargem() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.permitePrzNaoCadastrado", responsavel) + (alterarParam.isPermitePrzNaoCadastrado() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.validaTaxa", responsavel) + (alterarParam.isValidaTaxaJuros() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.novoSadCodigo", responsavel) + (TextHelper.isNull(alterarParam.getNovaSituacaoContrato()) ? "-" : alterarParam.getNovaSituacaoContrato());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.insereOcorrencia", responsavel) + (alterarParam.isIncluiOcorrencia() ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.integraFolha", responsavel) + ((alterarParam.getAdeIntFolha() == null) || alterarParam.getAdeIntFolha().equals(CodedValues.INTEGRA_FOLHA_SIM) ? ApplicationResourcesHelper.getMessage(ROTULO_SIM, responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage(ROTULO_NAO, responsavel).toUpperCase());
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.incideMargem", responsavel) + (alterarParam.getAdeIncideMargem() == null ? "-" : MargemHelper.getInstance().getMarDescricao(alterarParam.getAdeIncideMargem(), responsavel));

                        final String tmoDesc = tipoMotivoOperacaoController.findMotivoOperacao(alterarParam.getTmoCodigo(), responsavel).getTmoDescricao();

                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.tmoCodigo", responsavel) + (TextHelper.isNull(alterarParam.getTmoCodigo()) ? "-" : tmoDesc);
                        altAvancada += "<br/>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.adeObs", responsavel) + (TextHelper.isNull(alterarParam.getOcaObs()) ? "-" : alterarParam.getOcaObs());

                        log.add(altAvancada);

                        final String ocaCodigoAltAvancada = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_AVANCADA_CONTRATO, altAvancada, null, null, null, ocaPeriodo, alterarParam.getTmoCodigo(), responsavel);

                        // Caso não tenha sido criada ocorrência anteriormente, utiliza ocorrência de alteração avançada para incluir decisão judicial
                        if (TextHelper.isNull(ocaCodigo) && !TextHelper.isNull(ocaCodigoAltAvancada)) {
                            ocaCodigo = ocaCodigoAltAvancada;
                        }

                        // Cria o registro de decisão judicial, caso informado e o sistema permita
                        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                            if (TextHelper.isNull(alterarParam.getDjuCodigo()) && !TextHelper.isNull(alterarParam.getTjuCodigo()) && !TextHelper.isNull(alterarParam.getDjuTexto()) && !TextHelper.isNull(alterarParam.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                                DecisaoJudicialHome.create(ocaCodigo, alterarParam.getTjuCodigo(), alterarParam.getCidCodigo(), alterarParam.getDjuNumProcesso(), alterarParam.getDjuData(), alterarParam.getDjuTexto(), null);
                            }
                            if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel) && !TextHelper.isNull(alterarParam.getDjuCodigo()) && !TextHelper.isNull(alterarParam.getTjuCodigo()) && !TextHelper.isNull(alterarParam.getDjuTexto()) && !TextHelper.isNull(alterarParam.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                                DecisaoJudicialHome.create(ocaCodigo, alterarParam.getTjuCodigo(), alterarParam.getCidCodigo(), alterarParam.getDjuNumProcesso(), alterarParam.getDjuData(), alterarParam.getDjuTexto(), alterarParam.getDjuDataRevogacao());
                                final DecisaoJudicial dju = DecisaoJudicialHome.findByPrimaryKey(alterarParam.getDjuCodigo());
                                if (dju.getDjuDataRevogacao() == null) {
                                    dju.setDjuDataRevogacao(alterarParam.getDjuDataRevogacao());
                                    AbstractEntityHome.update(dju);
                                }
                            } else if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel) && !TextHelper.isNull(alterarParam.getDjuCodigo()) && TextHelper.isNull(alterarParam.getTjuCodigo()) && TextHelper.isNull(alterarParam.getDjuTexto()) && TextHelper.isNull(alterarParam.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                                final DecisaoJudicial dju = DecisaoJudicialHome.findByPrimaryKey(alterarParam.getDjuCodigo());
                                if (dju.getDjuDataRevogacao() == null) {
                                    dju.setDjuDataRevogacao(alterarParam.getDjuDataRevogacao());
                                    AbstractEntityHome.update(dju);
                                }
                            }
                        }
                    } else if (!TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(adeCodigo)) {
                            String[] anexosName;
                            File anexoAltContrato = null;
                            anexosName = nomeAnexo.split(";");
                            for (final String nomeAnexoCorrente : anexosName) {
                                try {
                                    anexoAltContrato = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigo, idAnexo, responsavel);
                                } catch (final ZetraException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
                                }
                                if ((anexoAltContrato != null) && anexoAltContrato.exists()) {
                                    aadDescricao = (!TextHelper.isNull(aadDescricao) && (aadDescricao.length() <= 255)) ? aadDescricao : anexoAltContrato.getName();
                                    editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexoAltContrato.getName(), aadDescricao, DateHelper.toSQLDate(ocaPeriodo), null, responsavel);
                                }
                            }
                    }

                    if (CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo())) {
                        setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel);
                    }

                    // Gera o Log de auditoria
                    log.write();

                    // se responsável pela operação for gestor ou de órgão, envia email para CSA do contrato alertando desta operação,
                    // se o param. para o envio estiver Sim.
                    if (!validar) {
                        // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                        // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                        final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.ALTERAR_CONSIGNACAO, adeCodigo, null, tmo, responsavel);
                        processoEmail.start();
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                if (ex.getClass().equals(AutorizacaoControllerException.class) ||
                    ex.getClass().equals(UsuarioControllerException.class) ||
                    ex.getClass().equals(PeriodoException.class)) {
                    throw new AutorizacaoControllerException(ex);
                } else {
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        }
    }

    private void atualizaCoeficiente(AcessoSistema responsavel, String adeCodigo, BigDecimal adeVlrTac,
                                     BigDecimal adeVlrIof, BigDecimal adeVlrLiquido, BigDecimal adeVlrMensVinc, BigDecimal taxa)
                                                                                                                                 throws SimulacaoControllerException, UpdateException {
        try {
            // Busca a taxa atual para comparação
            final CoeficienteDesconto cde = CoeficienteDescontoHome.findByAdeCodigo(adeCodigo);
            final Coeficiente cft = CoeficienteHome.findByPrimaryKey(cde.getCoeficiente().getCftCodigo());
            String cftCodigo = cft.getCftCodigo();

            final double taxaEfetivaAtual = cft.getCftVlr().doubleValue();
            final double taxaEfetivaNova = taxa.doubleValue();

            // Se a diferença é maior que 0.005, então insere novo registro de coeficiente e atualiza o coeficiente desconto
            if ((taxaEfetivaNova > 0) && (Math.abs(taxaEfetivaAtual - taxaEfetivaNova) > 0.005)) {
                final CustomTransferObject novoCft = new CustomTransferObject();
                novoCft.setAttribute(Columns.CFT_VLR, NumberHelper.format(taxaEfetivaNova, NumberHelper.getLang()));
                novoCft.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, cft.getPrazoConsignataria().getPrzCsaCodigo());
                cftCodigo = coeficienteController.insertCoeficiente(novoCft, responsavel);
                // Atualiza o coeficiente
                cde.setCoeficiente(CoeficienteHome.findByPrimaryKey(cftCodigo));
            }

            // Atualiza no registro de coeficiente desconto, os valores financeiros do contrato
            cde.setCdeVlrLiberado(adeVlrLiquido);
            cde.setCdeVlrTac(adeVlrTac);
            cde.setCdeVlrIof(adeVlrIof);
            cde.setCdeVlrMensVinc(adeVlrMensVinc);
            AbstractEntityHome.update(cde);

        } catch (final FindException ex) {
            // Contrato não tem registro de coeficiente desconto, provavelmente
            // é algum histórico, ou foi incluido antes de habilitar simulação
        }
    }

    /**
     * Atualiza consignação em processo de compra.
     * @param alterarParam
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void atualizarConsignacao(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_ATUALIZAR_PROCESSO_COMPRA)) {
                throw new AutorizacaoControllerException("mensagem.erro.usuario.sem.permissao.para.atualizar.esta.consignacao", responsavel);
            }

            final ListaConsignacaoRelacionamentoQuery query = new ListaConsignacaoRelacionamentoQuery();
            query.adeCodigoDestino = alterarParam.getAdeCodigo();
            query.tntCodigo = CodedValues.TNT_CONTROLE_COMPRA;
            final List<TransferObject> adeRelacionamento = query.executarDTO();

            if (!adeRelacionamento.isEmpty()) {
                final TransferObject ade = adeRelacionamento.get(0);

                //o processo de compra não pode estar em saldo devedor aprovado, pago ou contrato liquidado
                if (!TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA_APR_SALDO)) ||
                    !TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA_PGT_SALDO)) ||
                    !TextHelper.isNull(ade.getAttribute(Columns.RAD_DATA_LIQUIDACAO))) {
                    throw new AutorizacaoControllerException("mensagem.erro.status.compra.nao.permite.atualizacao", responsavel);
                } else {
                    alterarParam.setAtualizacaoCompra(true);
                    alterarParam.setValidaAdeIdentificador(false);
                    alterar(alterarParam, responsavel);
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.interno.executar.pesquisa.relacionamento.contratos", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> validarAlteracaoMultiplosAdes(AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Valida se o usuário possui permissão para alteração avançada de consignação
        if (!usuarioPossuiAltAvancadaAde(responsavel) && !responsavel.equals(AcessoSistema.getAcessoUsuarioSistema())) {
            throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.para.alteracao.avancada.contratos", responsavel);
        }

        if ((parametros == null) || (!parametros.isRestaurarValor() && (parametros.getVlrTotalNovo() == null))) {
            throw new AutorizacaoControllerException("mensagem.informe.valor.total.novo", responsavel);
        }

        final BigDecimal totalNovo = parametros.getVlrTotalNovo();
        BigDecimal totalAntigo = new BigDecimal(0);

        List<TransferObject> ades = null;
        String rseCodigo = null;

        try {
            final ListaConsignacaoQuery lstAde = new ListaConsignacaoQuery(responsavel);
            lstAde.adeCodigo = parametros.getAdeCodigos();
            lstAde.tipo = responsavel.getTipoEntidade();
            if (!parametros.isAjustaConsignacoesMargem()) {
                lstAde.codigo = responsavel.getCodigoEntidade();
            }
            ades = lstAde.executarDTO();

            for (final TransferObject ade : ades) {
                final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                final BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);

                if ((rseCodigo != null) && !rseCodigo.equals(ade.getAttribute(Columns.RSE_CODIGO).toString())) {
                    throw new AutorizacaoControllerException("mensagem.erro.alterar.multiplo.consignacao.multiplos.servidores", responsavel);
                } else if (rseCodigo == null) {
                    rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();
                }

                if (((parametros.getAdeCodigosNaoSelecionados() == null) || parametros.getAdeCodigosNaoSelecionados().isEmpty()) || ((parametros.getAdeCodigosNaoSelecionados() != null) && !parametros.getAdeCodigosNaoSelecionados().isEmpty() && !parametros.getAdeCodigosNaoSelecionados().contains(adeCodigo))) {
                    totalAntigo = totalAntigo.add(adeVlr);
                }

                if (parametros.isRestaurarValor()) {
                    // Buscar o TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA das consignações a serem alteradas
                    final String dadValor = getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA, responsavel);
                    BigDecimal adeVlrAnt = null;
                    if (!TextHelper.isNull(dadValor)) {
                        ade.setAttribute(Columns.DAD_VALOR, dadValor);
                        adeVlrAnt = new BigDecimal(dadValor);
                    } else {
                        throw new AutorizacaoControllerException("mensagem.erro.alterar.multiplo.consignacao.restaurar.valor.nao.encontrado", responsavel);
                    }

                    // Validar se a consignação não foi alterada após a última operação de alteração múltipla
                    // Consultar suas ocorrências de alteração e verificar em qual o valor alterado, sendo o valor anterior igual ao dado adicional do item 2 (DAD_VALOR = OCA_ADE_VLR_ANT).
                    // Verificar se há uma outra ocorrência de alteração de valor mais recente que esta, ou se o valor da consignação não é igual ao valor de destino da ocorrência de alteração (ADE_VLR != OCA_ADE_VLR_NOVO).
                    if (!parametros.isIgnorarAltPosterior() && verificarAlteracaoPosterior(adeCodigo, adeVlr, adeVlrAnt, responsavel)) {
                        throw new AutorizacaoControllerException("mensagem.erro.alterar.multiplo.consignacao.restaurar.valor.diferente", responsavel);
                    }
                }
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.interno.consultar.consignacoes", responsavel, ex);
        }

        BigDecimal minAdeVlrSist = BigDecimal.ZERO;
        try {
            String vlrMinSist = (String) ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);
            vlrMinSist = (TextHelper.isNull(vlrMinSist) ? "0.00" : NumberHelper.reformat(vlrMinSist, LocaleHelper.getLanguage(), "en"));
            minAdeVlrSist = new BigDecimal(vlrMinSist);
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        for (final TransferObject ade : ades) {
            final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
            final String svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
            final BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);
            BigDecimal novoAdeVlr = null;

            if (parametros.isRestaurarValor()) {
                novoAdeVlr = new BigDecimal(ade.getAttribute(Columns.DAD_VALOR).toString());
            } else if ((parametros.getAdeCodigosNaoSelecionados() != null) && !parametros.getAdeCodigosNaoSelecionados().isEmpty() && parametros.getAdeCodigosNaoSelecionados().contains(adeCodigo)) {
                novoAdeVlr = adeVlr;
            } else {
                // Novo valor do contrato proporcional ao somatório das parcelas em relação à margem limite
                final BigDecimal porcentagem = adeVlr.multiply(BigDecimal.valueOf(100)).divide(totalAntigo, 15, RoundingMode.DOWN);
                novoAdeVlr = totalNovo.multiply(porcentagem).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
            }

            // Se o novo valor calculado será zero
            if (novoAdeVlr.signum() == 0) {
                throw new AutorizacaoControllerException("mensagem.erro.alterar.multiplo.consignacao.novo.valor.invalido", responsavel, adeNumero);
            }

            final Integer adePrazo = (Integer) ade.getAttribute(Columns.ADE_PRAZO);
            final Integer adePrdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) ade.getAttribute(Columns.ADE_PRD_PAGAS) : 0;
            Integer adePrazoRestante = (adePrazo != null ? adePrazo - adePrdPagas : null);
            Integer novoAdePrazo = null;

            if (!parametros.isAlterarPrazo() || ((parametros.getAdeCodigosNaoSelecionados() != null) && !parametros.getAdeCodigosNaoSelecionados().isEmpty() && parametros.getAdeCodigosNaoSelecionados().contains(adeCodigo))) {
                novoAdePrazo = adePrazoRestante;
            } else if (adePrazo != null) {
                // Calcula o capital devido atual:
                BigDecimal capitalDevidoAtual = BigDecimal.ZERO;

                // 1) Procura por parcelas futuras criadas no status aberto
                try {
                    final ListaParcelasQuery listaParcelasQuery = new ListaParcelasQuery();
                    listaParcelasQuery.adeCodigo = adeCodigo;
                    listaParcelasQuery.spdCodigos = Arrays.asList(CodedValues.SPD_EMABERTO);
                    final List<TransferObject> parcelasAbertas = listaParcelasQuery.executarDTO();
                    if ((parcelasAbertas != null) && !parcelasAbertas.isEmpty()) {
                        ade.setAttribute("removerParcelaExtra", Boolean.TRUE);

                        for (final TransferObject parcela : parcelasAbertas) {
                            // 2) Soma o valor das parcelas abertas, e retira do prazo restante
                            adePrazoRestante--;
                            capitalDevidoAtual = capitalDevidoAtual.add((BigDecimal) parcela.getAttribute(Columns.PRD_VLR_PREVISTO));
                        }
                    }
                } catch (final HQueryException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.interno.consultar.parcelas", responsavel, ex);
                }

                // 3) Adiciona no capital devido o prazo restante multiplicado pelo valor da parcela
                capitalDevidoAtual = capitalDevidoAtual.add(adeVlr.multiply(BigDecimal.valueOf(adePrazoRestante)));

                // Adequa o novo prazo para manter o capital devido atual
                novoAdePrazo = capitalDevidoAtual.divide(novoAdeVlr, 0, RoundingMode.DOWN).intValue();

                // Compara o capital devido atual com o novo para ver se é necessário criar uma parcela extra
                final BigDecimal capitalDevidoNovo = novoAdeVlr.multiply(BigDecimal.valueOf(novoAdePrazo));
                final BigDecimal diferencaCapitalDevido = capitalDevidoAtual.subtract(capitalDevidoNovo);
                if (diferencaCapitalDevido.signum() != 0) {
                    // Busca parâmetro de serviço com valor mínimo da ADE
                    final CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, BigDecimal.ZERO, false, null);
                    final BigDecimal minAdeVlrSvc = ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) ? (BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR) : minAdeVlrSist);

                    if ((diferencaCapitalDevido.signum() > 0) && (diferencaCapitalDevido.compareTo(minAdeVlrSvc) >= 0)) {
                        // Se a diferença é positiva e maior que mínimo permitido, então cria uma parcela extra com a diferença
                        novoAdePrazo++;
                        ade.setAttribute("vlrParcelaExtra", diferencaCapitalDevido);
                    } else if (diferencaCapitalDevido.signum() < 0) {
                        // Se o capital devido novo é maior diminui o prazo, pois não pode aumentar
                        novoAdePrazo--;
                        // Se a diferença for menor que o valor de uma prestação cheia, cria uma nova prestação
                        // extra com o diferencial, caso seja maior que o mínimo permitido
                        final BigDecimal vlrParcelaExtra = novoAdeVlr.add(diferencaCapitalDevido);
                        if ((vlrParcelaExtra.signum() > 0) && (vlrParcelaExtra.compareTo(minAdeVlrSvc) >= 0)) {
                            ade.setAttribute("vlrParcelaExtra", vlrParcelaExtra);
                        }
                    }
                }
            }

            ade.setAttribute("adeVlrAtual", ade.getAttribute(Columns.ADE_VLR));
            ade.setAttribute("adeVlrNovo", novoAdeVlr);
            ade.setAttribute("adePrazoAtual", adePrazo != null ? adePrazo - adePrdPagas : null);
            ade.setAttribute("adePrazoNovo", novoAdePrazo);
        }

        return ades;
    }

    @Override
    public void alterarMultiplosAdes(List<TransferObject> ades, AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Valida se o usuário possui permissão para alteração avançada de consignação
        if (!usuarioPossuiAltAvancadaAde(responsavel)) {
            throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.para.alteracao.avancada.contratos", responsavel);
        }

        if ((parametros == null) || (ades == null) || ades.isEmpty()) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel);
        }

        if (TextHelper.isNull(parametros.getTmoCodigo())) {
            throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
        }

        // Define se deve recalcular margem do servidor após a alteração, caso
        // alguma das consignações tenha alterado a incidência de margem
        boolean recalcularMargem = false;
        final String rseCodigo = ades.get(0).getAttribute(Columns.RSE_CODIGO).toString();
        final BigDecimal adeVlr = new BigDecimal(ades.get(0).getAttribute(Columns.ADE_VLR).toString());

        // Consulta margem antes das alterações
        List<MargemTO> margemAntes = null;
        RegistroServidor registroServidor = null;
        boolean serAtivo = true;
        try {
            registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            // Caso esteja desbloqueando o servidor pelo fluxo de alteração de múltiplos contratos, o servidor esteja bloqueado e o usuário escolheu desbloquear o servidor para este fluxo
            // não precisa verificar se está ativo, deve permitir a consulta, desde que seja usuário CSE/SUP, com essa configuração, estamos sobrepondo o parâmetro de sistema 849,para permitir a alteração
            if (responsavel.isCseSup() && registroServidor.isBloqueado() && parametros.isDesbloquearRegistroServidor()) {
                serAtivo = false;
            }
            margemAntes = consultarMargemController.consultarMargem(rseCodigo, adeVlr, null, responsavel.getCsaCodigo(), true, null, serAtivo, null, responsavel);
        } catch (ServidorControllerException | FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }

        //DESENV-17862 - Inserir Ocorrência e dados autorização ao ajustar ao limite
        boolean criarOcorrenciaDadosAjusteLimite = false;
        TipoMotivoOperacao tipoMotivoOperacao = null;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MSG_MARGEM_ADEQUEADA_DECISAO_JUDICIAL, responsavel) && (!TextHelper.isNull(parametros.getMarCodigo()) || parametros.isRestaurarValor()) && !TextHelper.isNull(parametros.getTmoCodigo())) {
            try {
                tipoMotivoOperacao = TipoMotivoOperacaoHome.findByPrimaryKey(parametros.getTmoCodigo());
            } catch (final FindException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException(ex);
            }
            criarOcorrenciaDadosAjusteLimite = !TextHelper.isNull(tipoMotivoOperacao.getTmoDecisaoJudicial()) && CodedValues.TPA_SIM.equals(tipoMotivoOperacao.getTmoDecisaoJudicial());
        }

        final List<TransferObject> adesRelatorio = new ArrayList<>();

        for (final TransferObject ade : ades) {
            final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
            final String adeVlrString = NumberHelper.format(((Double) ade.getAttribute(Columns.ADE_VLR)), "en", 2, 2);
            final String adeIdentificador = (String) ade.getAttribute(Columns.ADE_IDENTIFICADOR);
            final String adeIndice = (String) ade.getAttribute(Columns.ADE_INDICE);
            final Short adeIncMargem = (Short) ade.getAttribute(Columns.ADE_INC_MARGEM);
            final Date adeAnoMesFim = (Date) ade.getAttribute(Columns.ADE_ANO_MES_FIM);

            final BigDecimal novoAdeVlr = (BigDecimal) ade.getAttribute("adeVlrNovo");
            final Integer novoAdePrazo = (Integer) ade.getAttribute("adePrazoNovo");

            final String dadValor87 = (String) ade.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_MARGEM_LIMITE_DECISAO_JUDICIAL);

            // A alteração de contrato que contenha decisão judicial só podem acontecer pelo módulo de decisão judicial
            autorizacaoController.verificaAlteracaoReativacaoDecisaoJudicial(adeCodigo, responsavel);

            final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, novoAdeVlr, novoAdePrazo, adeIdentificador, adeIndice, null, null, null, null, null, null, null, null, null);

            alterarParam.setExigeSenha(false);
            alterarParam.setValidaMargem(false);
            alterarParam.setValidaTaxaJuros(false);
            alterarParam.setAlterarValorPrazoSemLimite(true);
            alterarParam.setValidaLimiteAde(false);
            alterarParam.setValidaReservaCartao(false);
            alterarParam.setValidaAdeIdentificador(false);
            alterarParam.setPermiteAltEntidadesBloqueadas(true);
            alterarParam.setPermitePrzNaoCadastrado(true);
            alterarParam.setTmoCodigo(parametros.getTmoCodigo());
            alterarParam.setOcaObs(parametros.getOcaObs());
            alterarParam.setAdeIntFolha((Short) ade.getAttribute(Columns.ADE_INT_FOLHA));
            alterarParam.setAlteracaoAvancada(true);
            alterarParam.setAdePeriodicidade((String) ade.getAttribute(Columns.ADE_PERIODICIDADE));

            // Verifica alterações na incidência de margem da consignação
            if ((responsavel.isCseSupOrg() || (!TextHelper.isNull(ade.getAttribute("ajusteConsignacoesMargem")) && responsavel.isCsa())) && (parametros.isAlterarIncidencia() || parametros.isRestaurarIncidencia())) {
                try {
                    final String svcCodigo = convenioController.findServicoByAdeCodigo(adeCodigo, responsavel).getSvcCodigo();
                    ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse.setSvcCodigo(svcCodigo);
                    paramSvcCse = parametroController.findParamSvcCse(paramSvcCse, responsavel);
                    final Short svcIncideMargem = ((paramSvcCse != null) && TextHelper.isNum(paramSvcCse.getPseVlr()) ? Short.valueOf(paramSvcCse.getPseVlr()) : CodedValues.INCIDE_MARGEM_SIM);

                    if (parametros.isAlterarIncidencia()) {
                        // Se deve alterar incidência e a margem selecionada não é a margem onde o contrato incide
                        // então define o parâmetro para que a operação de alteração troque a incidência
                        if ((parametros.getMarCodigo() != null) && !parametros.getMarCodigo().equals(adeIncMargem)) {
                            alterarParam.setAdeIncideMargem(parametros.getMarCodigo());
                            recalcularMargem = true;

                            // Se está alterando a incidência para um valor diferente do serviço, cria regra para
                            // ignorar a inconsistência no item de validação de incidência de margem (10)
                            if (!parametros.getMarCodigo().equals(svcIncideMargem)) {
                                IgnoraInconsistenciaAdeHome.create(adeCodigo, CodedValues.IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.alterar.multiplo.consignacao.ignora.inconsistencia.incidencia.margem", responsavel), DateHelper.getSystemDatetime(), responsavel.getUsuLogin(), true);
                            }
                        }
                    } else if (parametros.isRestaurarIncidencia() && !adeIncMargem.equals(svcIncideMargem)) {
                        // Se o contrato incide em uma margem diferente do serviço, define que a operação de alteração
                        // troque a incidência para aquela definida no serviço
                        alterarParam.setAdeIncideMargem(svcIncideMargem);
                        recalcularMargem = true;

                        // Remove regra de inconsistência caso exista para este contrato no item de validação de
                        // incidência para que deixe de ser uma exceção
                        IgnoraInconsistenciaAdeHome.removeIfExists(adeCodigo, CodedValues.IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM);
                    }
                } catch (RemoveException | CreateException ex) {
                    LOG.error(ex.getMessage(), ex);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                } catch (ParametroControllerException | ConvenioControllerException ex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException(ex);
                }
            }

            // Repassa dados da decisão judicial
            alterarParam.setTjuCodigo(parametros.getTjuCodigo());
            alterarParam.setCidCodigo(parametros.getCidCodigo());
            alterarParam.setDjuNumProcesso(parametros.getDjuNumProcesso());
            alterarParam.setDjuData(parametros.getDjuData());
            alterarParam.setDjuTexto(parametros.getDjuTexto());

            // Quando o contrato pertence a consignatária que está efeutando a operação de ajuste a ocorrência da alteração deve ser deste usuário
            // porém se a consignação não pertence há ela, deve ser feito com o usuário do sistema.
            if (!TextHelper.isNull(ade.getAttribute("ajusteConsignacoesMargem")) && responsavel.isCsa() && !responsavel.getCodigoEntidade().equals(ade.getAttribute(Columns.CSA_CODIGO).toString())) {
                alterar(alterarParam, AcessoSistema.getAcessoUsuarioSistema());
            } else {
                // Executa alteração avançada
                alterar(alterarParam, responsavel);
            }
            // Grava o valor anterior para caso seja necessário restaurar
            setDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA, adeVlrString, responsavel);

            //DESENV-17862 - Inserir Ocorrência e dados autorização ao ajustar ao limite ou remover a ocorrência caso seja restauração de valor
            if (criarOcorrenciaDadosAjusteLimite && !parametros.isRestaurarValor()) {
                setDadoAutDesconto(adeCodigo, CodedValues.TDA_MARGEM_LIMITE_DECISAO_JUDICIAL, String.valueOf(parametros.getMarCodigo()), responsavel);
                removeOcorrenciaADE(adeCodigo, CodedValues.TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ, responsavel);
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.alteracao.multipla.contrato.ajuste.limite.margem", responsavel), responsavel);
            } else if (criarOcorrenciaDadosAjusteLimite && parametros.isRestaurarValor() && !TextHelper.isNull(dadValor87)) {
                removeOcorrenciaADE(adeCodigo, CodedValues.TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ, responsavel);
            }

            // Verifica se precisa remover parcelas extras
            if ((ade.getAttribute("removerParcelaExtra") != null) && ((Boolean) ade.getAttribute("removerParcelaExtra"))) {
                try {
                    ParcelaDescontoHome.deleteByAdeCodigoSpdCodigo(adeCodigo, CodedValues.SPD_EMABERTO);
                } catch (final RemoveException ex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erro.interno.remover.parcelas", responsavel, ex);
                }
            }

            // Verifica se precisa criar parcelas extras
            if (ade.getAttribute("vlrParcelaExtra") != null) {
                final BigDecimal vlrParcelaExtra = BigDecimal.valueOf((Double) ade.getAttribute("vlrParcelaExtra"));
                if ((vlrParcelaExtra != null) && (vlrParcelaExtra.signum() > 0)) {
                    try {
                        final Integer maxPrdNumero = ParcelaDescontoHome.findMaxPrdNumero(adeCodigo);
                        final Integer maxPdpNumero = ParcelaDescontoPeriodoHome.findMaxPrdNumero(adeCodigo);
                        final Short prdNumeroCalculado = Integer.valueOf(Math.max(maxPrdNumero, maxPdpNumero) + 1).shortValue();
                        ParcelaDescontoHome.create(adeCodigo, prdNumeroCalculado, CodedValues.SPD_EMABERTO, adeAnoMesFim, vlrParcelaExtra);
                    } catch (CreateException | FindException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new AutorizacaoControllerException("mensagem.erro.interno.criar.parcelas", responsavel, ex);
                    }
                }
            }

            if (parametros.isRestaurarValor()) {
                try {
                    ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse.setSvcCodigo((String) ade.getAttribute(Columns.SVC_CODIGO));
                    paramSvcCse = parametroController.findParamSvcCse(paramSvcCse, responsavel);
                    MargemRegistroServidorHome.restaurarValorParcelaUltimaAlteracao(rseCodigo, Short.parseShort(paramSvcCse.getPseVlr()));
                } catch (UpdateException | ParametroControllerException ex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erro.restaurar.valor.parcela.ultima.alteracao", responsavel, ex);
                }
            } else if (parametros.isAdequarMargemServidor()) {
                try {
                    for (final MargemTO margem : margemAntes) {
                        if(margem.getMarCodigo().equals(parametros.getMarCodigo())) {
                            MargemRegistroServidorHome.updateMargemRegistroServidor(rseCodigo, adeIncMargem, margem.getMrsMargem(), parametros.getMarCodigo());
                            break;
                        }
                    }
                    recalcularMargem = true;
                } catch (final UpdateException ex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erro.adequar.margem.servidor", responsavel, ex);
                }
            }

            if (!parametros.isRestaurarValor()) {
                final BigDecimal adeVlrOriginal = BigDecimal.valueOf((Double) ade.getAttribute(Columns.ADE_VLR));
                final Integer adePrazoRestante = NumberHelper.objectToInteger(ade.getAttribute("adePrazoAtual"));

                if (((adeVlrOriginal.compareTo(novoAdeVlr.setScale(2, RoundingMode.DOWN)) != 0) || (adePrazoRestante != novoAdePrazo))) {
                    ade.setAttribute(Columns.ADE_PRAZO, TextHelper.isNull(adePrazoRestante) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : adePrazoRestante);
                    adesRelatorio.add(ade);
                }
            }

        }

        // Quando o sistema trabalha com alteração em contratos não selecionados, até as margens que não tem contratos, mas foram selecionadas para adequação
        // precisam também ser adequadas a margem optada por adequação: Exemplo o servidor ter um contrato de empréstimo e não de cartão, mesmo assim, a margem cartão precisa ser adequada.
        if (parametros.isAdequarMargemServidor() && (parametros.getMarCodigosSelecionados() != null) && !parametros.getMarCodigosSelecionados().isEmpty()) {
            try {
                for (final MargemTO margem : margemAntes) {
                    for (final Short marCodigoSelecionado : parametros.getMarCodigosSelecionados()) {
                        if(margem.getMarCodigo().equals(marCodigoSelecionado)) {
                            MargemRegistroServidorHome.updateMargemRegistroServidor(rseCodigo, marCodigoSelecionado, margem.getMrsMargem(), parametros.getMarCodigo());
                        }
                    }
                }
                recalcularMargem = true;
            } catch (final UpdateException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException("mensagem.erro.adequar.margem.servidor", responsavel, ex);
            }
        }

        try {
            if (responsavel.isCseSupOrg() && (parametros.isBloquearServidor() || parametros.isDesbloquearServidor())) {
                // Bloqueia/Desbloqueia o servidor envolvido na alteração para os serviços de empréstimo
                final ParamNseRseTO bloqueio = new ParamNseRseTO();
                bloqueio.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);
                bloqueio.setRseCodigo(rseCodigo);
                bloqueio.setNseCodigo(CodedValues.NSE_EMPRESTIMO);
                bloqueio.setPnrAlteradoPeloServidor("N");
                bloqueio.setPnrObs(ApplicationResourcesHelper.getMessage("mensagem.alterar.multiplo.consignacao.bloqueio.natureza.emprestimo", responsavel));
                bloqueio.setPnrVlr(parametros.isBloquearServidor() ? "0" : null);
                parametroController.setBloqueioNseRegistroServidor(bloqueio, responsavel);
            }
        } catch (final ParametroControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }

        //Bloqueia/Desboqueia o registro servidor caso esta opção tenha sido selecionada.
        try {
            boolean alterouRegistroServidor = false;
            if (registroServidor.isBloqueado() && parametros.isDesbloquearRegistroServidor()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_ATIVO);
                registroServidor.setRseMotivoBloqueio(null);
                alterouRegistroServidor = true;
                servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_DESBLOQUEIO_STATUS_MANUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.desbloqueio.manual", responsavel), null, responsavel);
            }

            if (!registroServidor.isBloqueado() && parametros.isBloquearRegistroServidor()) {
                registroServidor.setSrsCodigo(CodedValues.SRS_BLOQUEADO);
                if (!TextHelper.isNull(parametros.getMotivoBloqueioRegistroServidor())) {
                    registroServidor.setRseMotivoBloqueio(parametros.getMotivoBloqueioRegistroServidor());
                }
                alterouRegistroServidor = true;
                servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_BLOQUEIO_STATUS_MANUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.bloqueio.manual", responsavel), null, responsavel);
            }

            if (alterouRegistroServidor) {
                AbstractEntityHome.update(registroServidor);
            }
        } catch (UpdateException | ServidorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }

        if (recalcularMargem) {
            try {
                margemController.recalculaMargemComHistorico("RSE", Arrays.asList(rseCodigo), responsavel);
            } catch (final MargemControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException(ex);
            }
        }

        // Consulta margem depois das alterações
        List<MargemTO> margemDepois = null;
        try {
            if (responsavel.isCseSup() && registroServidor.isBloqueado() && parametros.isDesbloquearRegistroServidor()) {
                serAtivo = false;
            }
            margemDepois = consultarMargemController.consultarMargem(rseCodigo, adeVlr, null, responsavel.getCsaCodigo(), true, null, serAtivo, null, responsavel);
        } catch (final ServidorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }

        try {
            LOG.debug("GERA RELATÓRIO DE ALTERAÇÃO DE MÚLTIPLAS CONSIGNAÇÕES");
            final Processo processo = new ProcessaRelatorioAlteracaoMultiplasConsignacoes(adesRelatorio.isEmpty() ? ades : adesRelatorio, parametros, margemAntes, margemDepois, responsavel);
            // Processo deve ser executado para utilizar a mesma Thread
            processo.run();

            if (processo.getCodigoRetorno() == Processo.ERRO) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException(processo.getMensagem(), responsavel);
            }

            LOG.debug("FIM RELATÓRIOS DE ALTERAÇÃO DE MÚLTIPLAS CONSIGNAÇÕES");
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Verifica se a consignação representada pelo "adeCodigo" possui uma ocorrência de alteração
     * posterior à alteração que mudou o valor da parcela de "adeVlrAnterior" para "adeVlrAtual".
     * @param adeCodigo
     * @param adeVlrAtual
     * @param adeVlrAnterior
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public boolean verificarAlteracaoPosterior(String adeCodigo, BigDecimal adeVlrAtual, BigDecimal adeVlrAnterior, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            boolean temOcorrenciaAltPosterior = false;
            // Busca as ocorrências de alteração de forma decrescente
            final List<OcorrenciaAutorizacao> ocas = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO);
            if ((ocas != null) && !ocas.isEmpty()) {
                for (final OcorrenciaAutorizacao oca : ocas) {
                    // Se OcaAdeVlrAnt e OcaAdeVlrNovo estão preenchidos, é uma ocorrência de alteração de valor
                    if ((oca.getOcaAdeVlrAnt() != null) && (oca.getOcaAdeVlrNovo() != null)) {
                        if ((oca.getOcaAdeVlrAnt().compareTo(adeVlrAnterior) != 0) || (oca.getOcaAdeVlrNovo().compareTo(adeVlrAtual) != 0)) {
                            // Se não é uma alteração de "adeVlrAnterior" para "adeVlrAtual", então foi alterado posteriormente
                            temOcorrenciaAltPosterior = true;
                        }
                        break;
                    }
                }
            }
            return temOcorrenciaAltPosterior;
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza alterações avançadas na consignação. Somente responsável com função de alteração avançada de consignação pode realizar essa operação.
     * @param autDesconto Contrato alterado.
     * @param alterarParam Parâmetros para alteração da consignação.
     * @param rseDto TO do registro servidor.
     * @param cnvCodigo Código do convênio.
     * @param svcCodigo Código do serviço.
     * @param csaCodigo Código da consignatária.
     * @param parametros Valores dos parâmetros já recuperados.
     * @param ocaCodigo Código da ocorrência que deverá ser vinculada a uma possível alteração de incidência de margem
     * @param orgCodigo Código do órgão.
     * @param difAdeVlr Diferença do valor do contrato original e o novo valor.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException
     * @throws UpdateException
     * @throws FindException
     */
    private void alteracoesAvancadas(AutDesconto autDesconto, AlterarConsignacaoParametros alterarParam, RegistroServidorTO rseDto, String cnvCodigo,
                                     String svcCodigo, String csaCodigo, Map<String, Object> parametros, String ocaCodigo, java.util.Date ocaPeriodo, String orgCodigo, BigDecimal difAdeVlr, AcessoSistema responsavel) throws ZetraException, UpdateException, FindException {

        // Caso o usuário não possua permissão para realizar alteração avançada de contrato
        if (!usuarioPossuiAltAvancadaAde(responsavel)) {
            return;
        }

        boolean alterou = false;
        final boolean validaMargem = alterarParam.isValidar();
        final boolean serAtivo = alterarParam.isSerAtivo();
        final boolean validaBloqueado = alterarParam.isValidaBloqueado();

        // Altera dados
        if (!TextHelper.isNull(alterarParam.getAdeIntFolha()) && (alterarParam.getAdeIntFolha().compareTo(autDesconto.getAdeIntFolha()) != 0)) {
            autDesconto.setAdeIntFolha(alterarParam.getAdeIntFolha());
            alterou = true;
        }
        if (!TextHelper.isNull(alterarParam.getAdeIncideMargem()) && (alterarParam.getAdeIncideMargem().compareTo(autDesconto.getAdeIncMargem()) != 0)) {
            final Short adeIncMargemOld = autDesconto.getAdeIncMargem();
            autDesconto.setAdeIncMargem(alterarParam.getAdeIncideMargem());
            alterou = true;

            if (alterarParam.isAlteraMargem()) {
                // Armazena o valor da autorização que já foi modificado para poder liberar o valor na margem antiga
                // e prender na margem nova para onde o contrato foi migrado.
                // Seta o ADE_VLR para zero para ser realizada as validações na migração da margem
                final BigDecimal adeVlr = autDesconto.getAdeVlr();
                autDesconto.setAdeVlr(BigDecimal.ZERO);

                // Libera margem de acordo com a incidência antiga
                atualizaMargem(autDesconto.getRegistroServidor().getRseCodigo(), adeIncMargemOld, adeVlr.negate(), validaMargem, validaBloqueado, serAtivo, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);

                final BigDecimal margemAtual = obtemMargemRestante(rseDto, autDesconto.getAdeIncMargem(), csaCodigo, true, null, responsavel);

                // Prende margem de acordo com a nova incidência
                final List<HistoricoMargemRse> historicosMargem = validaMargemAlteracao(autDesconto, rseDto, adeVlr, cnvCodigo, svcCodigo, csaCodigo, serAtivo, validaMargem,
                                                                                        validaBloqueado, true, alterarParam.isRenegociacao(), alterarParam, parametros, margemAtual, responsavel);

                // Atualiza o histórico de margem gerado pela atualização
                atualizaHistoricosMargem(historicosMargem, ocaCodigo, ocaPeriodo, null, null);

                // Volta com o ADE_VLR do contrato que já foi alterado anteriormente
                autDesconto.setAdeVlr(adeVlr);
            }
        }

        // Alterar a situação do contrato
        final String novaSituacaoContrato = alterarParam.getNovaSituacaoContrato();
        if (!TextHelper.isNull(novaSituacaoContrato)) {
            if (!CodedValues.SAD_CODIGOS_ALTERACAO_AVANCADA.contains(novaSituacaoContrato)) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.contrato.para.essa.situacao", responsavel);
            }

            StatusAutorizacaoDesconto statusAutDesconto;
            try {
                statusAutDesconto = StatusAutorizacaoDescontoHome.findByPrimaryKey(novaSituacaoContrato);
                autDesconto.setStatusAutorizacaoDesconto(statusAutDesconto);
                alterou = true;
            } catch (final FindException e) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.situacao.contrato", responsavel);
            }

            // Insere ocorrência
            if (alterarParam.isIncluiOcorrencia()) {
                String tocCodigo = "";
                String ocaObs = "";
                switch (novaSituacaoContrato) {
				case CodedValues.SAD_SUSPENSA_CSE:
					tocCodigo = CodedValues.TOC_TARIF_LIQUIDACAO;
					ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.suspensa.pela.cse", responsavel);
					break;
				case CodedValues.SAD_LIQUIDADA:
					tocCodigo = CodedValues.TOC_TARIF_LIQUIDACAO;
					ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.liquidacao.contrato", responsavel);
					break;
				case CodedValues.SAD_CANCELADA:
					tocCodigo = CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO;
					ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.cancelamento.consignacao", responsavel);
					break;
				case CodedValues.SAD_CONCLUIDO:
					if (!TextHelper.isNull(autDesconto.getAdePrazo()) && !TextHelper.isNull(autDesconto.getAdePrdPagas()) &&
                        autDesconto.getAdePrazo().equals(autDesconto.getAdePrdPagas())) {
                        tocCodigo = CodedValues.TOC_CONCLUSAO_CONTRATO;
                    } else {
                        tocCodigo = CodedValues.TOC_CONCLUSAO_SEM_DESCONTO;
                    }
					ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.nova.situacao.concluido", responsavel);
					break;
				case null:
				default:
					break;
				}
                criaOcorrenciaADE(autDesconto.getAdeCodigo(), tocCodigo, ocaObs, null, null, null, ocaPeriodo, null, responsavel);

                // Altera a margem
                if (alterarParam.isAlteraMargem()) {
                    atualizaMargem(autDesconto.getRegistroServidor().getRseCodigo(), autDesconto.getAdeIncMargem(), autDesconto.getAdeVlr().negate(), validaMargem, validaBloqueado, serAtivo, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                }
            }

            // Inclui ocorrencia de suspensão de contrato caso a nova situação do contrato seja suspenso, mesmo que o usuário decida por não inserir ocorrência
            if (CodedValues.SAD_SUSPENSA_CSE.equals(novaSituacaoContrato)) {
                criaOcorrenciaADE(autDesconto.getAdeCodigo(), CodedValues.TOC_SUSPENSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.suspensa.pela.cse", responsavel), null, null, null, ocaPeriodo, null, responsavel);
            }
        }

        if (alterou) {
            AbstractEntityHome.update(autDesconto);
        }

        if (alterarParam.isCriarNovoContratoDif() && (difAdeVlr != null) && (difAdeVlr.signum() == 1)) {
            criarNovoContratoDiferenca(autDesconto, rseDto.getRseCodigo(), cnvCodigo, orgCodigo, difAdeVlr, alterarParam.getTmoCodigo(), alterarParam.getOcaObs(), responsavel);
        }

        // Liquida as consignações relacionadas para alteração avançada em decisão judicial
        if (alterarParam.isLiquidaRelacionamentoJudicial()) {
            final Collection<RelacionamentoAutorizacao> adesRelacionamento = RelacionamentoAutorizacaoHome.findByOrigem(autDesconto.getAdeCodigo(), CodedValues.TNT_ALTERACAO_DECISAO_JUDICIAL, CodedValues.SAD_SUSPENSA_CSE);
            if ((adesRelacionamento != null) && !adesRelacionamento.isEmpty()) {
                final CustomTransferObject motivoOperacao = new CustomTransferObject();
                motivoOperacao.setAttribute(Columns.TMO_CODIGO, alterarParam.getTmoCodigo());
                motivoOperacao.setAttribute(Columns.OCA_OBS, alterarParam.getOcaObs());

                final LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
                parametrosLiquidacao.setOcaPeriodo(ocaPeriodo);

                for (final RelacionamentoAutorizacao adeDestino : adesRelacionamento) {
                    final String adeCodigoLiquidacao = adeDestino.getAutDescontoByAdeCodigoDestino().getAdeCodigo();
                    motivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigoLiquidacao);
                    liquidarConsignacaoController.liquidar(adeCodigoLiquidacao, motivoOperacao, parametrosLiquidacao, responsavel);
                }
            }
        }

        // Anexa os arquivos, caso existam
        if ((alterarParam.getNomeAnexos() != null) && (alterarParam.getNomeAnexos().length > 0) && !TextHelper.isNull(alterarParam.getDirAnexos())) {
            final String aadDescricao = ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.alteracao.avancada", responsavel);
            final String[] visibilidadeAnexos = alterarParam.getVisibilidadeAnexos();
            if (visibilidadeAnexos != null) {
                Arrays.sort(visibilidadeAnexos);
            }
            final String aadExibeSup = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SUPORTE) >= 0) ? "S" : "N");
            final String aadExibeCse = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNANTE) >= 0) ? "S" : "N");
            final String aadExibeOrg = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_ORGAO) >= 0) ? "S" : "N");
            final String aadExibeCsa = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNATARIA) >= 0) ? "S" : "N");
            final String aadExibeCor = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CORRESPONDENTE) >= 0) ? "S" : "N");
            final String aadExibeSer = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SERVIDOR) >= 0) ? "S" : "N");
            final String nomeAnexos = alterarParam.getNomeAnexo();
            String[] anexosName = nomeAnexos.split(";");
            for (final String nomeAnexo : anexosName) {
                if (!TextHelper.isNull(nomeAnexo)) {
                    final File arquivoAnexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexo, autDesconto.getAdeCodigo(), alterarParam.getDirAnexos(), responsavel);
                    if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                        editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(autDesconto.getAdeCodigo(), arquivoAnexo.getName(), aadDescricao, DateHelper.toSQLDate(ocaPeriodo),
                                                                                        TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_ALTERACAO_AVANCADA, aadExibeSup, aadExibeCse, aadExibeOrg, aadExibeCsa, aadExibeCor, aadExibeSer, responsavel);
                    }
                }
            }
        }
    }

    /**
     * Valida a margem para a operação de alteração.
     * @param autDesconto
     * @param rseDto
     * @param valorNovo
     * @param cnvCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param serAtivo
     * @param validar
     * @param validaBloqueado
     * @param podeAumentarValor
     * @param renegociacao
     * @param parametros
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    private List<HistoricoMargemRse> validaMargemAlteracao(AutDesconto autDesconto, RegistroServidorTO rseDto, BigDecimal valorNovo,
                                                           String cnvCodigo, String svcCodigo, String csaCodigo, boolean serAtivo, boolean validar, boolean validaBloqueado, boolean podeAumentarValor,
                                                           boolean renegociacao, AlterarConsignacaoParametros alterarParam, Map<String, Object> parametros, BigDecimal margemAtual, AcessoSistema responsavel) throws AutorizacaoControllerException {

        // Valida se o usuário possui permissão para alteração avançada de consignação
        final boolean usuPossuiAltAvancadaAde = usuarioPossuiAltAvancadaAde(responsavel);
        final boolean isAlteracaoAvancada = alterarParam.isAlteracaoAvancada();

        List<HistoricoMargemRse> historicosMargem = null;
        final String rseCodigo = rseDto.getRseCodigo();

        // Recupera o valor real de desconto, basendo-se em parâmetros de teto de desconto quando controla saldo.
        final BigDecimal vlrDesconto = this.calcularValorDescontoParcela(rseCodigo, rseDto.getCrsCodigo(), svcCodigo, valorNovo, parametros);

        // Diferença entre o novo valor e o atual.
        BigDecimal diferencaValor = autDesconto.getAdeVlr().subtract(vlrDesconto);

        boolean descontoViaBoleto = false;

        // Trativa para contratos que são utilizam da regra de negócio do param de serviço 312
        if (alterarParam.existsDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)) {
            if (CodedValues.FORMA_PAGAMENTO_BOLETO.equals(alterarParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO))) {
                diferencaValor = vlrDesconto;
                descontoViaBoleto = true;
            } else if (CodedValues.FORMA_PAGAMENTO_FOLHA.equals(alterarParam.getDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO)) && (autDesconto.getAdeVlr().subtract(vlrDesconto).compareTo(BigDecimal.ZERO) == 0)) {
                diferencaValor = vlrDesconto.negate();
            }
        }

        // Recupera o parâmetro de aumento de margem negativa.
        CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER, svcCodigo, new BigDecimal("0"), false, parametros);
        final BigDecimal valorLimiteAlteracaoSemMargem = (((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) ? (BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR) : new BigDecimal("0.00")));

        // Verifica se o serviço é um compulsório (e se o sistema tem controle de compulsórios)
        boolean servicoCompulsorio = false;
        final boolean temControleCompulsorios = (ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                                                 ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel));
        try {
            if (temControleCompulsorios) {
                // O serviço só é um compulsório caso o sistema tenha controle de compulsórios
                paramSvc = getParametroSvc(CodedValues.TPS_SERVICO_COMPULSORIO, svcCodigo, "", false, parametros);
                if ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null)) {
                    servicoCompulsorio = ("1".equals(paramSvc.getAttribute(Columns.PSE_VLR).toString()));
                }
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if ((diferencaValor.signum() == -1) && !podeAumentarValor && !renegociacao) {
            // Não pode fazer operação, pois o valor da parcela escolhido é maior que o valor atual
            throw new AutorizacaoControllerException("mensagem.valorParcelaMaiorQueValorAtual", responsavel);
        }

        // Se estiver aumentando o valor, verifica se trata-se de um lançamento de cartão de crédito e se ele é válido.
        if (diferencaValor.signum() == -1) {
            verificaLancamentoCartaoCredito(rseCodigo, diferencaValor.negate(), cnvCodigo, null, responsavel);
        } else if (diferencaValor.signum() == 1) {
            final boolean validaReservaCartao = (usuPossuiAltAvancadaAde && isAlteracaoAvancada) ? alterarParam.isValidaReservaCartao() : true;

            // Se estiver diminuindo o valor e o contrato está deferido,
            // verifica se trata-se de uma reserva de cartão de crédito e se ela é válida.
            if (validaReservaCartao && CodedValues.SAD_DEFERIDA.equals(autDesconto.getStatusAutorizacaoDesconto().getSadCodigo())) {
                verificaReservaCartaoCredito(rseCodigo, diferencaValor.negate(), cnvCodigo, responsavel);
            }
        }

        // Verifica se é permitido reduzir o valor de um contrato com margem negativa
        // Verifica se o seviço possui valor limite para margem negativa.
        // Se o usuário possui permissão para alteração avançada de contrato e não quer que a margem seja validada, a mesma não será
        final String rvamn = (String) ParamSist.getInstance().getParam(CodedValues.TPC_REDUCAO_VLR_ADE_MARGEM_NEG, responsavel);
        boolean permiteReducaoAdeVlrComMargemNegativa = usuPossuiAltAvancadaAde && !alterarParam.isValidaMargem() ? true : ((CodedValues.TPC_SIM.equals(rvamn)) || (valorLimiteAlteracaoSemMargem.compareTo(new BigDecimal("0.00")) > 0));

        paramSvc = getParametroSvc(CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA, svcCodigo, "", false, parametros);
        final String podeAlterarValorPrazoMrgNegativa = ((paramSvc != null) && !TextHelper.isNull(paramSvc.getAttribute(Columns.PSE_VLR))) ? paramSvc.getAttribute(Columns.PSE_VLR).toString() : null;

        if (!TextHelper.isNull(podeAlterarValorPrazoMrgNegativa)) {
            permiteReducaoAdeVlrComMargemNegativa = !CodedValues.NAO_ALTERA_VLR_E_PRAZO_MARGEM_NEGATIVA.equals(podeAlterarValorPrazoMrgNegativa);
        }

        final CustomTransferObject permiteAlterarAdeRseExcluido = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO, svcCodigo, "", false, parametros);
        final boolean boolPermiteAlterarAdeRseExcluido = !TextHelper.isNull(permiteAlterarAdeRseExcluido) && !TextHelper.isNull(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR)) && CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO_APENAS_REDUCAO.equals(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR));

        // Recupera o valor da margem atual para testar se pode ser feita alteração no contrato
        if ((autDesconto.getAdeIncMargem() != null) && !CodedValues.INCIDE_MARGEM_NAO.equals(autDesconto.getAdeIncMargem()) && !descontoViaBoleto) {
            // Se não permite redução de valor com margem negativa; se o valor está sendo alterado;
            // se a margem ficará negativa após a operação; e se não é um compulsório.
            // Então gera erro de margem insuficiente.
            if (!permiteReducaoAdeVlrComMargemNegativa && (diferencaValor.signum() != 0) &&
                (margemAtual.add(autDesconto.getAdeVlr()).subtract(vlrDesconto).signum() == -1) && !servicoCompulsorio) {

                // Verifica se exibe margem na crítica de lote
                final CustomTransferObject paramExibeMargem = getParametroSvc(CodedValues.TPS_EXIBE_MARGEM_CRITICA_LOTE, svcCodigo, "", false, null);
                final boolean exibeMargemViaLote = ((paramExibeMargem != null) && (paramExibeMargem.getAttribute(Columns.PSE_VLR) != null) &&
                                                    "1".equals(paramExibeMargem.getAttribute(Columns.PSE_VLR)));

                final MargemTO marTO = MargemHelper.getInstance().getMargem(autDesconto.getAdeIncMargem(), responsavel);
                final ExibeMargem exibeMargem = new ExibeMargem(marTO, responsavel);

                if (!boolPermiteAlterarAdeRseExcluido) {
                    // Se for lote e o parâmetro que exibe margem na importação de lote habilitado, inclui margem disponíveml na mensagem de erro
                    if (exibeMargemViaLote && CodedValues.FUNCOES_IMPORTACAO_LOTE.contains(responsavel.getFunCodigo()) && exibeMargem.isExibeValor()) {
                        throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel.arg0", responsavel, NumberHelper.format(margemAtual.doubleValue(), NumberHelper.getLang(), true));
                    } else {
                        throw new AutorizacaoControllerException("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel);
                    }
                }
            }

        }
        // Se não permite redução de valor com margem negativa; Se estiver aumentando o valor; e se não é um compulsório.
        // verifica se cabe na margem disponível por csa
        if (!permiteReducaoAdeVlrComMargemNegativa && (diferencaValor.signum() == -1) && !servicoCompulsorio) {
            verificaLimiteMargemPorConsignataria(csaCodigo, rseCodigo, vlrDesconto, autDesconto.getAdeCodigo(), null, responsavel);
        }

        // Valida a margem se o novo contrato for maior ou se o novo contrato é menor,
        // mas não é permitido reduzir o valor de um contrato sem margem
        // Se o usuário possui permissão para alteração avançada de contrato e não quer que a margem seja validada, a mesma não será
        boolean validaMargem = usuPossuiAltAvancadaAde && !alterarParam.isValidaMargem() ? false : (((diferencaValor.signum() == -1) || ((diferencaValor.signum() == 1) && !permiteReducaoAdeVlrComMargemNegativa)) && (diferencaValor.abs().subtract(margemAtual).compareTo(valorLimiteAlteracaoSemMargem) > 0));

        // Servidores excluídos (srs_codigo = 3) podem realizar alteração de consignação apenas reduzindo valor.
        if ((boolPermiteAlterarAdeRseExcluido && CodedValues.SRS_EXCLUIDO.equals(rseDto.getSrsCodigo()) && (diferencaValor.signum() == 1)) || descontoViaBoleto) {
            validaMargem = false;
        }

        if (((diferencaValor.signum() == 1) ||
             ((diferencaValor.signum() == -1) && (podeAumentarValor || renegociacao)))) {
            // Altera a margem do servidor
            if (validar) {
                if (validaMargem) {
                    verificaMargem(rseDto, autDesconto.getAdeIncMargem(), diferencaValor.negate(), csaCodigo, svcCodigo, true, null, responsavel);
                }
            } else {
                try {
                    historicosMargem = atualizaMargem(rseCodigo, autDesconto.getAdeIncMargem(), diferencaValor.negate(), validaMargem, validaBloqueado, serAtivo, null, csaCodigo, svcCodigo, null, responsavel);
                } catch (final AutorizacaoControllerException ex) {
                    historicosMargem = atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, autDesconto.getAdeCodigo(), null, diferencaValor.negate(), valorLimiteAlteracaoSemMargem, autDesconto.getAdeIncMargem(), parametros, validaBloqueado, serAtivo, true, responsavel);
                }
            }
        }

        // Se estiver aumentando o valor, verifica se o aumento está dentro do limite configurado.
        if (diferencaValor.signum() == -1) {
            paramSvc = getParametroSvc(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE, svcCodigo, cnvCodigo, BigDecimal.class, null, java.util.Date.class, LocaleHelper.getDatePattern(), false, true, parametros);
            // Se tem limite, faz a validação
            if ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null)) {
                final BigDecimal aumentoLimite = autDesconto.getAdeVlr().multiply((BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR)).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                if (diferencaValor.abs().compareTo(aumentoLimite) > 0) {
                    // Não pode fazer operação, pois o aumento do valor da parcela é maior que o limite cadastrado.
                    final double percentualLimite = ((BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR)).doubleValue();
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.consignacao.aumento.valor.parcela.maior.limite.cadastrado.arg0", responsavel, NumberHelper.format(percentualLimite, NumberHelper.getLang()));
                }
                if (paramSvc.getAttribute(Columns.PSE_VLR_REF) != null) {
                    final java.util.Date dataLimite = (java.util.Date) paramSvc.getAttribute(Columns.PSE_VLR_REF);
                    final java.util.Date hoje = DateHelper.getSystemDate();
                    if (hoje.compareTo(dataLimite) > 0) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.consignacao.pois.data.limite.passou.arg0", responsavel, DateHelper.toDateString(dataLimite));
                    }
                }
            }
        }

        return historicosMargem;
    }

    private void validaAlteracao(AutDesconto autdes, RegistroServidorTO rseDto, String cnvCodigo, String csaCodigo, String svcCodigo, BigDecimal adeVlr,
                                 Integer adePrazo, Integer adePrazoNew,
                                 boolean cnvAtivo, boolean svcAtivo, boolean csaAtivo,
                                 boolean orgAtivo, boolean estAtivo, boolean cseAtivo, boolean serAtivo,
                                 boolean isAtualizacaoMargem, boolean aumentouVlr, boolean aumentouPrazo, boolean validarLimitesValorPrazo,
                                 Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {

        final String adeCodigo = autdes.getAdeCodigo();
        final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo();
        final String rseCodigo = autdes.getRegistroServidor().getRseCodigo();

        // Contrato em estoque ou suspenso pode ser alterado por consignante ou suporte
        final boolean cseSupAlteraEstoqueSuspensa = responsavel.isCseSup() && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo));
        // Contratos aguard. confirmação e aguard. deferimento podem ser alterados conforme parâmetro
        final boolean permiteAlterarAguardConfDef = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_AGUARD_CONF_E_DEF, CodedValues.TPC_SIM, responsavel) && (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)));
        // Contratos em estoque podem ser alterados pela CSA conforme parâmetro
        final boolean permiteCsaAlterarEstoque = (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ALTERAR_ADE_ESTOQUE, CodedValues.TPC_SIM, responsavel) && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo)));

        // Valida o status da consignação que está sendo alterada
        if (!CodedValues.SAD_DEFERIDA.equals(sadCodigo) && !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) && !cseSupAlteraEstoqueSuspensa && !permiteAlterarAguardConfDef && !permiteCsaAlterarEstoque && (!isAtualizacaoMargem || !CodedValues.SAD_AGUARD_CONF.equals(sadCodigo))) {
            throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.alterada.porque.situacao.atual.dela.nao.permite.esta.operacao", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
        }

        // Caso seja usuário servidor, verifica se este pode alterar consignação deste serviço
        if (responsavel.isSer()) {
            final CustomTransferObject paramServidorAlteraConsignacao = getParametroSvc(CodedValues.TPS_SERVIDOR_ALTERA_CONTRATO, svcCodigo, Boolean.FALSE, false, parametros);
            final boolean servidorAlteraConsignacao = (paramServidorAlteraConsignacao != null) && ((Boolean) paramServidorAlteraConsignacao.getAttribute(Columns.PSE_VLR)).booleanValue();
            if (!servidorAlteraConsignacao) {
                Servico servico = null;
                try {
                    servico = ServicoHome.findByPrimaryKey(svcCodigo);
                } catch (final FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.alterada.pelo.servidor.porque.servico.nao.permite.esta.operacao", responsavel, servico.getSvcDescricao());
            }
        }

        // Verifica o novo valor do contrato, pois deve ser maior que zero
        if ((adeVlr == null) || (adeVlr.signum() != 1)) {
            try {
                if (!parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
                    throw new AutorizacaoControllerException("mensagem.valorParcelaMenorIgualZero", responsavel);
                }
            } catch (final ParametroControllerException ex) {
                throw new AutorizacaoControllerException(ex);
            }
        }

        try {
            // Valida o novo valor de acordo com os parâmetros de serviço e sistema
            if (validarLimitesValorPrazo) {
                AutorizacaoHelper.validarValorAutorizacao(adeVlr, svcCodigo, csaCodigo, responsavel);
            }
        } catch (final ViewHelperException ex) {
            throw new AutorizacaoControllerException(ex);
        }

        final CustomTransferObject paramPermiteAlterarComBloqueio = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO, svcCodigo, "", false, null);
        final String permiteAlterarComBloqueio = (paramPermiteAlterarComBloqueio != null) && (paramPermiteAlterarComBloqueio.getAttribute(Columns.PSE_VLR) != null) ? paramPermiteAlterarComBloqueio.getAttribute(Columns.PSE_VLR).toString() : CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO;

        // Se o parâmetro de serviço está habilitado, não testa os bloqueios de consignante, consignatária, serviço e convênio.
        if (CodedValues.PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO.equals(permiteAlterarComBloqueio) ||
            (CodedValues.PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO_APENAS_REDUCAO.equals(permiteAlterarComBloqueio) && !aumentouVlr && !aumentouPrazo)) {
            cnvAtivo = false;
            svcAtivo = false;
            csaAtivo = false;
        }

        // Valida o status de todas as entidades envolvidas na operação
        validarEntidades(cnvCodigo, null, cnvAtivo, svcAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, false, responsavel);

        // Valida status do servidor
        if (serAtivo) {
            if (rseDto.isExcluido()) {
                final CustomTransferObject permiteAlterarAdeRseExcluido = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO, svcCodigo, "", false, parametros);
                if ((permiteAlterarAdeRseExcluido == null) || TextHelper.isNull(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR)) ||
                    (CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO.equals(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR))) ||
                    (CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO_APENAS_REDUCAO.equals(permiteAlterarAdeRseExcluido.getAttribute(Columns.PSE_VLR)) && (aumentouPrazo || aumentouVlr))) {
                    // Servidor excluído não pode alterar reservas.
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.contrato.pois.servidor.foi.excluido", responsavel);
                }
            } else if (rseDto.isBloqueado()) {
                final CustomTransferObject permiteAlterarAdeRseBloq = getParametroSvc(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO, svcCodigo, "", false, parametros);
                if ((permiteAlterarAdeRseBloq == null) || TextHelper.isNull(permiteAlterarAdeRseBloq.getAttribute(Columns.PSE_VLR)) ||
                    (CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO.equals(permiteAlterarAdeRseBloq.getAttribute(Columns.PSE_VLR))) ||
                    (CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO_APENAS_REDUCAO.equals(permiteAlterarAdeRseBloq.getAttribute(Columns.PSE_VLR)) && (aumentouPrazo || aumentouVlr))) {
                    // Servidor bloqueado não pode alterar reservas.
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.contrato.pois.servidor.esta.bloqueado", responsavel);
                }
            }
        }
        // Pega o parametro para verificar se permite fazer uma renegociação maior que o prazo do servidor
        CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA, svcCodigo, cnvCodigo, Boolean.class, null, null, null, false, false, parametros);
        final boolean permitePrazoMaiorContSer = validarLimitesValorPrazo ? ((paramSvc != null) && ((Boolean) paramSvc.getAttribute(Columns.PSE_VLR)).booleanValue()) : true;

        // Validação de prazo máximo é em relação ao restante do contrato
        paramSvc = getParametroSvc(CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES, svcCodigo, "", false, parametros);
        final boolean validaPrzMaxRelativoRestantes = validarLimitesValorPrazo ? ((paramSvc != null) && !TextHelper.isNull(paramSvc.getAttribute(Columns.PSE_VLR))) ? ((CodedValues.VALIDA_PRAZO_MAX_RELATIVO_AOS_RESTANTES.equals(paramSvc.getAttribute(Columns.PSE_VLR).toString())))
                : false
                : false;

        // Prazo máximo para o serviço
        paramSvc = getParametroSvc(CodedValues.TPS_MAX_PRAZO, svcCodigo, Integer.valueOf("0"), false, parametros);
        final TransferObject maxPrazoRenegociacao = getParametroSvc(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE, svcCodigo, Integer.valueOf("0"), false, parametros);
        final boolean isDestinoRenegociacao = pesquisarConsignacaoController.isDestinoRenegociacaoPortabilidade(adeCodigo);

        // Se o contrato alterado é destino de uma renegociação/portabilidade, verifica se o serviço tem prazo máximo específico para estas operações.
        // Senão, utiliza o praxo máximo de inserção padrão.
        int maxPrazo = (isDestinoRenegociacao && (maxPrazoRenegociacao != null) && (maxPrazoRenegociacao.getAttribute(Columns.PSE_VLR) != null) && validarLimitesValorPrazo) ? ((Integer) maxPrazoRenegociacao.getAttribute(Columns.PSE_VLR)) : ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) && validarLimitesValorPrazo) ? ((Integer) paramSvc.getAttribute(Columns.PSE_VLR)) : Integer.MAX_VALUE;

        // Prazo máximo de contratação do servidor (em meses)
        int rsePrazo = (rseDto.getRsePrazo() != null ? rseDto.getRsePrazo() : Integer.MAX_VALUE);

        // Não olhar a incidência de margem do contrato, que pode ser zero caso não seja confirmado/deferido inicialmente
        // mas sim a incidência do serviço que sempre irá indicar a margem correta.
        paramSvc = getParametroSvc(CodedValues.TPS_INCIDE_MARGEM, svcCodigo, Short.valueOf("0"), false, parametros);
        final Short svcIncMargem = (paramSvc.getAttribute(Columns.PSE_VLR) != null ? (Short) paramSvc.getAttribute(Columns.PSE_VLR) : null);

        Integer mrsPrazo = null;
        if (validarLimitesValorPrazo && (svcIncMargem != null) && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO) && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM) && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) && !svcIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            // Se o serviço incide em uma margem extra (tb_margem_registro_servidor) verifica se a margem
            // possui restrição de prazo máximo cadastrado (MRS_PRAZO_MAX)
            try {
                final MargemRegistroServidorId pk = new MargemRegistroServidorId(svcIncMargem, rseCodigo);
                final MargemRegistroServidor mrs = MargemRegistroServidorHome.findByPrimaryKey(pk);
                if ((mrs != null) && (mrs.getMrsPrazoMax() != null)) {
                    mrsPrazo = mrs.getMrsPrazoMax().intValue();
                }
            } catch (final FindException ex) {
                // Serviço incide em uma margem que não existe
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        // Se o serviço ou servidor tem prazo máximo, porém é contrato quinzenal, a unidade do prazo máximo
        // será em meses, portanto ao comparar com o contrato quinzenal, deve multiplicar por 2
        if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(autdes.getAdePeriodicidade())) {
            maxPrazo = ((maxPrazo > 1) && (maxPrazo != Integer.MAX_VALUE)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazo, responsavel) : maxPrazo;
            rsePrazo = ((rsePrazo > 0) && (rsePrazo != Integer.MAX_VALUE)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(rsePrazo, responsavel) : rsePrazo;
            mrsPrazo = ((mrsPrazo != null) && (mrsPrazo > 0)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(mrsPrazo, responsavel) : mrsPrazo;
        }

        final int novoPrazoAde = adePrazoNew != null ? adePrazoNew : 0;

        if ((novoPrazoAde > 0) && (((validaPrzMaxRelativoRestantes) ? (novoPrazoAde - autdes.getAdePrdPagas()) : novoPrazoAde) > maxPrazo)) {
            // Não pode fazer operação, pois o prazo atual é maior que o permitido para o serviço
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.esta.consignacao.pois.prazo.maior.permitido", responsavel);
        } else if ((novoPrazoAde == 0) && (maxPrazo > 0) && (maxPrazo != Integer.MAX_VALUE)) {
            // Se o novo prazo é indeterminado mas o prazo deve ser limitado, então lança erro ao usuário
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.alterar.esta.consignacao.pois.prazo.deve.ser.determinado.limitado.arg0", responsavel, String.valueOf(maxPrazo));
        }

        // Verifica o rse_prazo: Não precisa verificar a carência do contrato pois ele está em andamento e o novo prazo será a partir da data atual
        if ((adePrazo != null) && (adePrazo.intValue() > rsePrazo) && !permitePrazoMaiorContSer) {
            // Não pode fazer operação, pois o rse prazo é menor que o prazo da consignação
            throw new AutorizacaoControllerException("mensagem.qtdParcelasMaiorPrazoServidor", responsavel);
        }

        if ((mrsPrazo != null) && (adePrazo != null) && (mrsPrazo.compareTo(adePrazo) < 0)) {
            // Prazo da Consignação maior que o prazo máximo permitido para a margem
            throw new AutorizacaoControllerException("mensagem.erro.prazo.maior.maximo", responsavel, String.valueOf(mrsPrazo));
        }

        // Verificar o limite de prazo para servidor temporário somente quando o prazo é aumentado, pois antes da criação dessa validação
        // poderia existir contratos com prazo fora do limite e assim eles não seriam afetados olhando somente se aumentou o prazo.
        if (aumentouPrazo) {
            validaPrazoLimiteDataAdmissaoRseTemporario(rseCodigo, svcCodigo, csaCodigo, novoPrazoAde, responsavel);
        }
    }

    /**
     * Metodo para verificar se o servidor pode alterar um contrato. Parametro TPS_NUM_CONTRATOS_POR_CONVENIO
     * @param cnvCodigo
     * @param rseCodigo
     * @throws AutorizacaoControllerException
     */
    private void verificaServidorCnvBloqueio(String cnvCodigo, String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica parametros do convênio para o registro servidor
        CustomTransferObject param = null;
        final ListaParamCnvRseQuery query = new ListaParamCnvRseQuery();
        query.rseCodigo = rseCodigo;
        query.cnvCodigo = cnvCodigo;
        query.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO;
        try {
            final List<TransferObject> result = query.executarDTO();
            if ((result != null) && (result.size() > 0)) {
                param = (CustomTransferObject) result.get(0);
            }
        } catch (final HQueryException e) {
            LOG.debug("Erro ao buscar parametro, HQueryException: " + e.getMessage());
        }
        final String qtdeMaxAde = ((param != null) && (param.getAttribute(Columns.PCR_VLR) != null) && !"".equals(param.getAttribute(Columns.PCR_VLR).toString())) ? param.getAttribute(Columns.PCR_VLR).toString() : "";
        final String qtdeMaxAdeCnvRse = (ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel).toString() : "";

        if (!"".equals(qtdeMaxAde) || !"".equals(qtdeMaxAdeCnvRse)) {
            int numContratos = 0;
            if (!"".equals(qtdeMaxAde) && !"".equals(qtdeMaxAdeCnvRse)) {
                if (Integer.parseInt(qtdeMaxAdeCnvRse) < Integer.parseInt(qtdeMaxAde)) {
                    numContratos = Integer.parseInt(qtdeMaxAdeCnvRse);
                } else {
                    numContratos = Integer.parseInt(qtdeMaxAde);
                }
            } else if (!"".equals(qtdeMaxAde)) {
                numContratos = Integer.parseInt(qtdeMaxAde);
            } else if (!"".equals(qtdeMaxAdeCnvRse)) {
                numContratos = Integer.parseInt(qtdeMaxAdeCnvRse);
            }
            if (numContratos == 0) {
                // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.servidor.esta.bloqueado.convenio.escolhido", responsavel);
            }
        }
    }

    /**
     * Metodo para verificar se o servidor pode alterar um contrato. Parametro TPS_NUM_CONTRATOS_POR_SERVICO
     * @param svcCodigo
     * @param rseCodigo
     * @throws AutorizacaoControllerException
     */
    private void verificaServidorSvcBloqueio(String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica parametros do convênio para o registro servidor
        CustomTransferObject param = null;
        final ListaParamSvcRseQuery query = new ListaParamSvcRseQuery();
        query.rseCodigo = rseCodigo;
        query.svcCodigo = svcCodigo;
        query.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO;
        try {
            final List<TransferObject> result = query.executarDTO();
            if ((result != null) && (result.size() > 0)) {
                param = (CustomTransferObject) result.get(0);
            }
        } catch (final HQueryException e) {
            LOG.debug("Erro ao buscar parametro, HQueryException: " + e.getMessage());
        }
        final String qtdeMaxAdeSvcRse = ((param != null) && (param.getAttribute(Columns.PSR_VLR) != null)) ? param.getAttribute(Columns.PSR_VLR).toString() : "";
        if (!"".equals(qtdeMaxAdeSvcRse)) {
            final int numMaxContratos = Integer.parseInt(qtdeMaxAdeSvcRse);
            if (numMaxContratos == 0) {
                // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.servidor.esta.bloqueado.servico.escolhido", responsavel);
            }
        }
    }

    /**
     * Metodo para verificar se o servidor pode alterar um contrato. Parametro TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO
     * @param svcCodigo
     * @param rseCodigo
     * @throws AutorizacaoControllerException
     */
    private void verificaServidorNseBloqueio(String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica parametros da natureza de serviço para o registro servidor
        try {
            CustomTransferObject param = null;
            final Servico servico = ServicoHome.findByPrimaryKey(svcCodigo);
            final String nseCodigo = servico.getNaturezaServico().getNseCodigo();

            final ListaParamNseRseQuery query = new ListaParamNseRseQuery();
            query.rseCodigo = rseCodigo;
            query.nseCodigo = nseCodigo;
            query.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO;
            try {
                final List<TransferObject> result = query.executarDTO();
                if ((result != null) && (result.size() > 0)) {
                    param = (CustomTransferObject) result.get(0);
                }
            } catch (final HQueryException e) {
                LOG.debug("Erro ao buscar parametro, HQueryException: " + e.getMessage());
            }
            final String qtdeMaxAdeNseRse = ((param != null) && (param.getAttribute(Columns.PNR_VLR) != null)) ? param.getAttribute(Columns.PNR_VLR).toString() : "";
            if (!"".equals(qtdeMaxAdeNseRse)) {
                final int numMaxContratos = Integer.parseInt(qtdeMaxAdeNseRse);
                if (numMaxContratos == 0) {
                    // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.servidor.esta.bloqueado.natureza.servico.escolhido", responsavel);
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Metodo para verificar se o campo carencia pode ser alterado.
     * @param adeCarencia Valor da carencia digitado pelo usuario.
     * @param autdes Consignacao a ser alterada.
     * @param svcCodigo Codigo do servico.
     * @param csaCodigo Codigo da consignataria.
     * @param orgCodigo Codigo do orgao.
     * @param parametros HashMap de parametros de alteracao, caso ja tenham sido recuperados anteriormente.
     * @param responsavel Responsavel pela alteracao.
     * @param qtdeParcelaDesconto Quantidade de parcelas processadas ou em processamento.
     * @return Novo valor da carencia, caso ela realmente esteja sendo alterada.
     * @throws AutorizacaoControllerException
     */
    private Integer verificaCarencia(Integer adeCarencia, AutDesconto autdes, String svcCodigo, String csaCodigo, String orgCodigo, Map<String, Object> parametros,
                                     AcessoSistema responsavel, int qtdeParcelaDesconto, Date periodoAtual) throws AutorizacaoControllerException {

        final Integer adeCarenciaOld = autdes.getAdeCarencia() != null ? autdes.getAdeCarencia() : 0;
        Integer adeCarenciaNew = null;
        if (adeCarencia != null) {
            try {
                // Carencia restante é a diferenca entre Data Inicio do pagamento e Periodo Atual
                Integer adeCarenciaRestante = PeriodoHelper.getInstance().calcularCarenciaInclusao(orgCodigo, autdes.getAdeAnoMesIni(), autdes.getAdePeriodicidade(), responsavel);

                // Quando está diminuindo a carência, e o sistema é quinzenal e tem periodo agrupado, precisamos retirar do cálculo os periodo agrupado da carência
                // pois se não o valor do prazo restante pode ser maior que o correto.
                if ((adeCarencia < adeCarenciaOld) && !PeriodoHelper.folhaMensal(responsavel)) {
                    final Integer qntPeriodosAgrupados = periodoController.obtemQtdPeriodoAgrupado(orgCodigo, periodoAtual, autdes.getAdeAnoMesIni(), responsavel);
                    adeCarenciaRestante = adeCarenciaRestante > 0 ? adeCarenciaRestante - qntPeriodosAgrupados : 0;
                }

                // Carencia cumprida é a diferenca entre carencia original e a carencia restante
                final int adeCarenciaCumprida = adeCarenciaOld - adeCarenciaRestante;
                // Nova Carencia é a soma da carencia ja cumprida com a informada pelo usuario
                adeCarenciaNew = adeCarenciaCumprida + adeCarencia;

                if (adeCarenciaNew.compareTo(adeCarenciaOld) != 0) {

                    if (qtdeParcelaDesconto > 0) {
                        throw new AutorizacaoControllerException("mensagem.erro.carencia.nao.pode.ser.alterada.existem.parcelas.processadas.ou.em.processamento", responsavel);
                    }

                    // Situacao deve ser Deferido
                    if (!CodedValues.SAD_DEFERIDA.equals(autdes.getStatusAutorizacaoDesconto().getSadCodigo())) {
                        throw new AutorizacaoControllerException("mensagem.erro.carencia.nao.pode.ser.alterada.situacao.atual.consignacao.nao.permite.esta.operacao", responsavel);
                    }

                    // Periodo atual deve ser menor que o periodo inicial do contrato
                    periodoAtual = DateHelper.toSQLDate(DateHelper.clearHourTime(periodoAtual));
                    if (autdes.getAdeAnoMesIni().compareTo(periodoAtual) < 0) {
                        throw new AutorizacaoControllerException("mensagem.erro.carencia.nao.pode.ser.alterada.contrato.ja.esta.sendo.processado", responsavel);
                    }

                    CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_CARENCIA_MINIMA, svcCodigo, Integer.valueOf("0"), false, parametros);
                    final Integer carenciaMinimaSvc = ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) && !paramSvc.getAttribute(Columns.PSE_VLR).toString().isEmpty()) ? (Integer) paramSvc.getAttribute(Columns.PSE_VLR) : 0;
                    paramSvc = getParametroSvc(CodedValues.TPS_CARENCIA_MAXIMA, svcCodigo, Integer.valueOf("0"), false, parametros);
                    final Integer carenciaMaximaSvc = ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) && !paramSvc.getAttribute(Columns.PSE_VLR).toString().isEmpty()) ? (Integer) paramSvc.getAttribute(Columns.PSE_VLR) : 99;

                    // Verifica parametro Carencia Consignataria
                    final List<String> tpsCodigo = new ArrayList<>();
                    tpsCodigo.add(CodedValues.TPS_CARENCIA_MINIMA);
                    tpsCodigo.add(CodedValues.TPS_CARENCIA_MAXIMA);
                    Integer carenciaMinima = 0;
                    Integer carenciaMaxima = 99;
                    final List<TransferObject> params = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
                    for (final TransferObject param2 : params) {
                        final CustomTransferObject param = (CustomTransferObject) param2;
                        if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                            if (CodedValues.TPS_CARENCIA_MINIMA.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                                carenciaMinima = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? Integer.valueOf((String) param.getAttribute(Columns.PSC_VLR)) : 0;
                            } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                                carenciaMaxima = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? Integer.valueOf((String) param.getAttribute(Columns.PSC_VLR)) : 99;
                            }
                        }
                    }

                    // Se a nova carencia for menor ou maior que os valores pre-definidos indica um erro
                    final int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinimaSvc, carenciaMaximaSvc);
                    final int carenciaMinPermitida = carenciaPermitida[0];
                    final int carenciaMaxPermitida = carenciaPermitida[1];
                    if (!responsavel.isRescisao() && ((adeCarenciaNew < carenciaMinPermitida) || (adeCarenciaNew > carenciaMaxPermitida))) {
                        final int minimoRelativo = (carenciaMinPermitida - adeCarenciaCumprida) > 0 ? (carenciaMinPermitida - adeCarenciaCumprida) : 0;
                        final int maximoRelativo = (carenciaMaxPermitida - adeCarenciaCumprida) > 0 ? (carenciaMaxPermitida - adeCarenciaCumprida) : 0;
                        if (carenciaMaxPermitida > carenciaMinPermitida) {
                            throw new AutorizacaoControllerException("mensagem.erro.prazo.carencia.deve.ser.maior.igual.arg0.menor.igual.arg1", responsavel, String.valueOf(minimoRelativo), String.valueOf(maximoRelativo));
                        } else if (carenciaMaxPermitida < carenciaMinPermitida) {
                            throw new AutorizacaoControllerException("mensagem.erro.prazo.carencia.deve.ser.menor.igual.arg0", responsavel, String.valueOf(maximoRelativo));
                        } else if (carenciaMaxPermitida == carenciaMinPermitida) {
                            throw new AutorizacaoControllerException("mensagem.erro.prazo.carencia.deve.ser.igual.arg0", responsavel, String.valueOf(minimoRelativo));
                        }
                    }
                } else {
                    // Valor nao sofreu alteracao
                    adeCarenciaNew = null;
                }
            } catch (final Exception ex) {
                if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                    throw (AutorizacaoControllerException) ex;
                }
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return adeCarenciaNew;
    }

    /**
     * Cria um novo contrato a partir de um contrato alterado. Ele vai ser do mesmo convênio, porém suspenso e com a diferença entre o
     * valor original do contrato e o novo valor, onde este novo contrato irá prender a margem do servidor.
     * @param autDesconto Contrato alterado.
     * @param rseCodigo Servidor dono do contrato alterado.
     * @param cnvCodigo Convenio do contrato.
     * @param orgCodigo Orgão do servidor.
     * @param difAdeVlr Diferença do valor do contrato original e o novo valor.
     * @param tmoCodigo Código do motivo de operação
     * @param ocaObs Observação do motivo de operação
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    private void criarNovoContratoDiferenca(AutDesconto autDesconto, String rseCodigo, String cnvCodigo, String orgCodigo, BigDecimal difAdeVlr,
                                            String tmoCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Cria objeto de parâmetro de reserva
            final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

            reservaParam.setAdeVlr(difAdeVlr);
            reservaParam.setCnvCodigo(cnvCodigo);
            reservaParam.setCorCodigo(autDesconto.getCorrespondente() != null ? autDesconto.getCorrespondente().getCorCodigo() : null);
            reservaParam.setRseCodigo(rseCodigo);
            reservaParam.setAdeVlrTac(autDesconto.getAdeVlrTac());
            Integer adePrazo = null;
            if (!TextHelper.isNull(autDesconto.getAdePrazo()) && !TextHelper.isNull(autDesconto.getAdePrdPagas())) {
                adePrazo = autDesconto.getAdePrazo() - autDesconto.getAdePrdPagas();
            } else if (!TextHelper.isNull(autDesconto.getAdePrazo())) {
                adePrazo = autDesconto.getAdePrazo();
            }
            reservaParam.setAdePrazo(adePrazo);
            reservaParam.setAdeIndice(autDesconto.getAdeIndice());
            reservaParam.setAdeVlrIof(autDesconto.getAdeVlrIof());
            reservaParam.setAdeVlrLiquido(autDesconto.getAdeVlrLiquido());
            reservaParam.setAdeVlrMensVinc(autDesconto.getAdeVlrMensVinc());
            reservaParam.setAdeTaxaJuros(autDesconto.getAdeTaxaJuros());
            reservaParam.setAdeTipoVlr(autDesconto.getAdeTipoVlr());
            reservaParam.setAdeIntFolha(autDesconto.getAdeIntFolha());
            reservaParam.setAdeVlrSegPrestamista(autDesconto.getAdeVlrSegPrestamista());
            reservaParam.setAdeBanco(autDesconto.getAdeBanco());
            reservaParam.setAdeAgencia(autDesconto.getAdeAgencia());
            reservaParam.setAdeConta(autDesconto.getAdeConta());
            reservaParam.setAdeIncMargem(autDesconto.getAdeIncMargem());
            reservaParam.setAdePeriodicidade(autDesconto.getAdePeriodicidade());
            reservaParam.setValidaExigeInfBancaria(Boolean.FALSE);
            reservaParam.setValidaSenhaServidor(Boolean.FALSE);
            reservaParam.setValidaAnexo(Boolean.FALSE);

            // Constantes
            reservaParam.setAdeCarencia(0);
            reservaParam.setAdeIdentificador("");
            reservaParam.setSadCodigo(CodedValues.SAD_SUSPENSA_CSE);
            reservaParam.setSerSenha(null);
            reservaParam.setComSerSenha(Boolean.FALSE);
            reservaParam.setAcao("RESERVAR");

            // Sem validacoes
            reservaParam.setValidar(Boolean.FALSE);
            reservaParam.setPermitirValidacaoTaxa(Boolean.FALSE);
            reservaParam.setValidaTaxaJuros(Boolean.FALSE);
            reservaParam.setValidaPrazo(Boolean.FALSE);
            reservaParam.setValidaLimiteAde(Boolean.FALSE);
            reservaParam.setValidaMargem(Boolean.FALSE);
            reservaParam.setSerAtivo(Boolean.FALSE);
            reservaParam.setCnvAtivo(Boolean.FALSE);
            reservaParam.setSerCnvAtivo(Boolean.FALSE);
            reservaParam.setSvcAtivo(Boolean.FALSE);
            reservaParam.setCsaAtivo(Boolean.FALSE);
            reservaParam.setOrgAtivo(Boolean.FALSE);
            reservaParam.setEstAtivo(Boolean.FALSE);
            reservaParam.setCseAtivo(Boolean.FALSE);
            reservaParam.setValidaAdeIdentificador(Boolean.FALSE);

            // Mesmo motivo de operação da alteração avançada
            reservaParam.setTmoCodigo(tmoCodigo);
            reservaParam.setOcaObs(ocaObs);

            final String adeCodigoNovo = reservarController.reservarMargem(reservaParam, responsavel);

            // Insere relacionamento entre os contratos com natureza de Alteracao por Decisao Judicial
            RelacionamentoAutorizacaoHome.create(autDesconto.getAdeCodigo(), adeCodigoNovo, CodedValues.TNT_ALTERACAO_DECISAO_JUDICIAL, responsavel.getUsuCodigo());

        } catch (final Exception ex) {
            LOG.error(ex.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            }
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);

        }
    }

    private boolean avancada(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) {
        boolean retorno = false;

        if (usuarioPossuiAltAvancadaAde(responsavel) && !alterarParam.isAtualizacaoReajuste()) {
            retorno = (alterarParam.isAlteraMargem() != AlterarConsignacaoParametros.PADRAO_ALTERA_MARGEM) ||
                      (alterarParam.isExigeSenha() != AlterarConsignacaoParametros.PADRAO_EXIGE_SENHA) ||
                      (alterarParam.isValidaMargem() != AlterarConsignacaoParametros.PADRAO_VALIDA_MARGEM) ||
                      (alterarParam.isValidaTaxaJuros() != AlterarConsignacaoParametros.PADRAO_VALIDA_TAXA_JUROS) ||
                      (alterarParam.isValidaSenhaServidor() != AlterarConsignacaoParametros.PADRAO_VALIDA_SENHA_SERVIDOR) ||
                      (alterarParam.isAlterarValorPrazoSemLimite() != AlterarConsignacaoParametros.PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE) ||
                      (alterarParam.isCriarNovoContratoDif() != AlterarConsignacaoParametros.PADRAO_CRIAR_NOVO_CONTRATO_DIF) ||
                      (alterarParam.isCalcularPrazoDifValor() != AlterarConsignacaoParametros.PADRAO_CALCULAR_PRAZO_DIF_VALOR) ||
                      (alterarParam.isManterDifValorMargem() != AlterarConsignacaoParametros.PADRAO_MANTER_DIF_VALOR_MARGEM) ||
                      (!alterarParam.getNovaSituacaoContrato().isEmpty()) ||
                      (alterarParam.isIncluiOcorrencia() != AlterarConsignacaoParametros.PADRAO_INCLUI_OCORRENCIA) ||
                      (alterarParam.isPermiteAltEntidadesBloqueadas() != AlterarConsignacaoParametros.PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS) ||
                      ((alterarParam.getAdeIntFolha() != null) && (alterarParam.getAdeIntFolha() != 1)) ||
                      ((alterarParam.getTmoCodigo() != null) && !alterarParam.getTmoCodigo().isEmpty()) ||
                      ((alterarParam.getOcaObs() != null) && !alterarParam.getOcaObs().isEmpty()) ||
                      (alterarParam.isPermitePrzNaoCadastrado() != AlterarConsignacaoParametros.PADRAO_PERMITE_PRZ_NAO_CADASTRADO) ||
                      (alterarParam.isValidaLimiteAde() != AlterarConsignacaoParametros.PADRAO_VALIDA_LIMITE_ADE) ||
                      (alterarParam.isValidaReservaCartao() != AlterarConsignacaoParametros.PADRAO_VALIDA_RESERVA_CARTAO);
        }

        return retorno;
    }

    /**
     * Realiza a rotina para ajuste dos contratos que estão em fila aguardando margem,
     * verificando quais deles podem ser enviados para a folha, e calculando valor e
     * prazo destes que serão enviados.
     * @param orgCodigos
     * @param estCodigos
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void alterarConsignacoesDescontoEmFila(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Se exportação apenas inicial, os contratos deverão ser reimplantados
            final boolean reimplante = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            // Lista os servidores que possuem contratos aguard. margem.
            final ListaRegistroServidorAguardMargemQuery query1 = new ListaRegistroServidorAguardMargemQuery();
            query1.estCodigos = estCodigos;
            query1.orgCodigos = orgCodigos;
            final List<String> rseCodigos = query1.executarLista();
            if ((rseCodigos != null) && (rseCodigos.size() > 0)) {
                // Para cada servidor, obtém os contratos juntamente com o serviço destes,
                // ordenado do mais antigo ao mais novo.
                for (final String rseCodigo : rseCodigos) {
                    final ListaConsignacaoAguardandoMargemQuery query2 = new ListaConsignacaoAguardandoMargemQuery();
                    query2.rseCodigo = rseCodigo;
                    final List<TransferObject> adesAguardMargem = query2.executarDTO();
                    if ((adesAguardMargem != null) && (adesAguardMargem.size() > 0)) {
                        final Set<String> svcCodigosAnalisados = new HashSet<>();
                        final Iterator<TransferObject> it2 = adesAguardMargem.iterator();
                        while (it2.hasNext()) {
                            // Verfica se o servidor possui desconto em fila já em andamento na margem ao qual o serviço incide,
                            // inclusive através dos serviços relacionados.
                            final TransferObject ade = it2.next();
                            final String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
                            final String svcCodigo = (String) ade.getAttribute(Columns.SVC_CODIGO);
                            final String svcPrioridade = (String) ade.getAttribute(Columns.SVC_PRIORIDADE);
                            final java.util.Date pexPeriodo = (java.util.Date) ade.getAttribute(Columns.PEX_PERIODO);
                            final java.util.Date pexDataFim = (java.util.Date) ade.getAttribute(Columns.PEX_DATA_FIM);

                            if (!svcCodigosAnalisados.contains(svcCodigo)) {
                                svcCodigosAnalisados.add(svcCodigo);
                                final ListaConsignacaoDescontoEmFilaQuery query3 = new ListaConsignacaoDescontoEmFilaQuery();
                                query3.rseCodigo = rseCodigo;
                                query3.svcCodigo = svcCodigo;
                                final List<TransferObject> adesDescFila = query3.executarDTO();
                                if ((adesDescFila != null) && (adesDescFila.size() > 0)) {
                                } else {
                                    // Se não possui desconto, calcula o valor máximo de desconto de acordo com a base
                                    // de cálculo e o percentual do serviço.
                                    final String percentual = (String) ade.getAttribute("PERCENTUAL_BASE_CALC");
                                    final String baseCalc = (String) ade.getAttribute("BASE_CALC");
                                    final String incMargem = (String) ade.getAttribute("INCIDE_MARGEM");

                                    if (!TextHelper.isNull(percentual) && !TextHelper.isNull(baseCalc)) {
                                        // Busca o valor da base de cálculo para o servidor
                                        final BigDecimal vlrBaseCalc = BaseCalcRegistroServidorHome.getBcsValor(rseCodigo, baseCalc);
                                        if (vlrBaseCalc != null) {
                                            // Calcula o valor máximo de desconto
                                            final BigDecimal vlrMaxDesconto = vlrBaseCalc.multiply(new BigDecimal(percentual).movePointLeft(2));

                                            // Verifica se o valor da dívida do primeiro da fila é menor que o valor máximo de desconto.
                                            final BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);
                                            if (adeVlr.compareTo(vlrMaxDesconto) <= 0) {
                                                if (servidorTemMargemDescEmFila(rseCodigo, svcCodigo, svcPrioridade, adeVlr, Short.valueOf(incMargem), responsavel)) {
                                                    // Se o valor for menor, o contrato será lançado integralmente, com prazo 1, e o sistema
                                                    // poderá verificar se o próximo também cabe integralmente no valor máximo restante.
                                                    alterarConsignacaoDescontoEmFila(adeCodigo, rseCodigo, svcCodigo, adeVlr, null, null, Short.valueOf(incMargem), pexPeriodo, pexDataFim, reimplante, responsavel);

                                                    BigDecimal vlrMaxDescontoRestante = vlrMaxDesconto.subtract(adeVlr);
                                                    while (it2.hasNext()) {
                                                        final TransferObject adeProxima = it2.next();
                                                        // Se for da mesma base de cálculo, então verifica se pode ser enviado integralmente
                                                        final String baseCalcProxima = (String) adeProxima.getAttribute("BASE_CALC");
                                                        if (baseCalc.equals(baseCalcProxima)) {
                                                            final BigDecimal adeVlrProxima = (BigDecimal) adeProxima.getAttribute(Columns.ADE_VLR);
                                                            if ((adeVlrProxima.compareTo(vlrMaxDescontoRestante) <= 0) && servidorTemMargemDescEmFila(rseCodigo, svcCodigo, svcPrioridade, adeVlrProxima, Short.valueOf(incMargem), responsavel)) {
                                                                final String adeCodigoProxima = (String) adeProxima.getAttribute(Columns.ADE_CODIGO);
                                                                alterarConsignacaoDescontoEmFila(adeCodigoProxima, rseCodigo, svcCodigo, adeVlrProxima, null, null, Short.valueOf(incMargem), pexPeriodo, pexDataFim, reimplante, responsavel);

                                                                vlrMaxDescontoRestante = vlrMaxDescontoRestante.subtract(adeVlrProxima);
                                                            } else {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // Se não possuir margem, passa ao próximo servidor.
                                                    break;
                                                }

                                            } else {
                                                // Se o valor for maior, a dívida deverá ser parcelada para que o valor da parcela seja
                                                // menor ou igual ao valor máximo de desconto. Deve-se dividir o valor da dívida pelo valor
                                                // máximo de desconto, para obter o prazo, caso fracionário, arredondar para cima e dividir pela
                                                // dívida novamente, obtendo o valor final de parcela.
                                                final Integer novoAdePrazo = adeVlr.divide(vlrMaxDesconto, 0, java.math.RoundingMode.UP).intValueExact();
                                                final BigDecimal novoAdeVlr = adeVlr.divide(new BigDecimal(novoAdePrazo), 2, java.math.RoundingMode.DOWN);

                                                if (servidorTemMargemDescEmFila(rseCodigo, svcCodigo, svcPrioridade, novoAdeVlr, Short.valueOf(incMargem), responsavel)) {
                                                    // O contrato terá status alterado para Deferido, valor de parcela e prazo ajustados de acordo
                                                    // com os cálculos acima, e alterada sua incidência de margem e integração com a folha para Sim.
                                                    alterarConsignacaoDescontoEmFila(adeCodigo, rseCodigo, svcCodigo, adeVlr, novoAdeVlr, novoAdePrazo, Short.valueOf(incMargem), pexPeriodo, pexDataFim, reimplante, responsavel);
                                                } else {
                                                    break;
                                                }
                                            }
                                        } else {
                                            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.base.calculo.desconto.em.fila.nula", responsavel));
                                        }
                                    } else {
                                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.configuracao.servico.desconto.em.fila.ausente", responsavel));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void alterarConsignacaoDescontoEmFila(String adeCodigo, String rseCodigo, String svcCodigo, BigDecimal adeVlr, BigDecimal novoAdeVlr, Integer novoAdePrazo, Short adeIncMargem, java.util.Date pexPeriodo, java.util.Date pexDataFim, boolean reimplante, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String sadCodigo = CodedValues.SAD_AGUARD_MARGEM;
            final String novoSadCodigo = CodedValues.SAD_DEFERIDA;

            final String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
            final String csaCodigo = ConsignatariaHome.findByAdeCodigo(adeCodigo).getCsaCodigo();

            // Altera os dados da consignação para deixá-lo apto a ser exportado
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            // Altera valor e prazo caso passado por parâmetro
            if (novoAdeVlr != null) {
                autdes.setAdeVlr(novoAdeVlr);
            } else {
                novoAdeVlr = adeVlr;
            }
            if (novoAdePrazo != null) {
                autdes.setAdePrazo(novoAdePrazo);
            } else {
                novoAdePrazo = 1;
            }

            // Incide na margem, integra folha e troca o status
            autdes.setAdeIncMargem(adeIncMargem);
            autdes.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);
            autdes.setStatusAutorizacaoDesconto(new StatusAutorizacaoDesconto(novoSadCodigo));

            // Ajusta data inicial e final de acordo com o período
            autdes.setAdeAnoMesIni(pexPeriodo);
            autdes.setAdeAnoMesFim(PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, pexPeriodo, novoAdePrazo, autdes.getAdePeriodicidade(), responsavel));

            // Efetua a atualização
            AbstractEntityHome.update(autdes);

            // Cria ocorrencia de informação
            final String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, sadCodigo, novoSadCodigo), adeVlr, novoAdeVlr, responsavel);

            // Cria ocorrencia de reimplante
            if (reimplante) {
                // Grava a data da ocorrência dentro do período de exportação
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.relancamento.automatico", responsavel), null, null, pexDataFim, null, null, responsavel);
            }

            // Afetar a margem do servidor
            try {
                atualizaMargem(rseCodigo, adeIncMargem, novoAdeVlr, true, false, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                // Recupera o parâmetro de valor para inclusão além da margem negativa
                final CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, new BigDecimal("0"), false, null);
                final BigDecimal valorLimiteSemMargem = (((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) ? (BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR) : new BigDecimal("0.00")));

                atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, adeCodigo, ocaCodigo, novoAdeVlr, valorLimiteSemMargem, adeIncMargem, null, false, true, true, responsavel);
            }

        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private boolean servidorTemMargemDescEmFila(String rseCodigo, String svcCodigo, String svcPrioridade, BigDecimal adeVlr, Short adeIncMargem, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Recupera o parâmetro de valor para inclusão além da margem negativa
        CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, new BigDecimal("0"), false, null);
        final BigDecimal valorLimiteSemMargem = (((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) ? (BigDecimal) paramSvc.getAttribute(Columns.PSE_VLR) : new BigDecimal("0.00")));

        // Verifica se o serviço é um compulsório (e se o sistema tem controle de compulsórios)
        boolean servicoCompulsorio = false;
        final boolean temControleCompulsorios = (ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                                                 ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel));
        try {
            if (temControleCompulsorios) {
                // O serviço só é um compulsório caso o sistema tenha controle de compulsórios
                paramSvc = getParametroSvc(CodedValues.TPS_SERVICO_COMPULSORIO, svcCodigo, "", false, null);
                if ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null)) {
                    servicoCompulsorio = ("1".equals(paramSvc.getAttribute(Columns.PSE_VLR).toString()));
                }
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        final BigDecimal valor = adeVlr.subtract(valorLimiteSemMargem);

        if (!temMargem(rseCodigo, svcCodigo, valor, adeIncMargem, false, responsavel)) {
            if (servicoCompulsorio) {
                try {
                    // Busca a margem disponível para compulsório e subtrai este valor da margem para a validação
                    final boolean controlaMargem = !ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel);
                    final BigDecimal margemDisponivelCompulsorio = consultarMargemController.getMargemDisponivelCompulsorio(rseCodigo, svcCodigo, svcPrioridade, adeIncMargem, controlaMargem, null, responsavel);
                    return (margemDisponivelCompulsorio.compareTo(valor) >= 0);
                } catch (final ServidorControllerException ex) {
                    throw new AutorizacaoControllerException(ex);
                }
            }

            return false;
        }

        return true;
    }

    @Override
    public List<TransferObject> possuiRelacionamentoAlteracaoJudicial(String adeCodigoOrigem, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoRelacionamentoByOrigemQuery query = new ListaConsignacaoRelacionamentoByOrigemQuery();
            query.adeCodigoOrigem = adeCodigoOrigem;
            query.sadCodigoDestino = CodedValues.SAD_SUSPENSA_CSE;
            query.tntCodigo = CodedValues.TNT_ALTERACAO_DECISAO_JUDICIAL;

            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private AlterarConsignacaoParametros validaCamposSistema(AutDesconto autdes, AlterarConsignacaoParametros alternParam, AcessoSistema responsavel) throws ZetraException {
        BigDecimal adeVlr;
        BigDecimal adeVlrLiq;
        BigDecimal vlrSegPrestamista;
        BigDecimal vlrIof;
        BigDecimal vlrTac;
        BigDecimal txjuros;
        BigDecimal vlrMesVinc;
        Integer adePrz;
        String adeIdentificador;
        AlterarConsignacaoParametros alterarParam;

        final int prz = autdes.getAdePrazo() != null ? autdes.getAdePrazo() : -1;
        final int przPg = autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0;
        int przRest = prz - przPg;

        List<ParcelaDescontoPeriodo> parcelasEmProcessamento = null;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_CONSIDERA_PARCELAS_AGUARD_PROCESSAMENTO, responsavel)) {
            parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(autdes.getAdeCodigo(), CodedValues.SPD_EMPROCESSAMENTO, responsavel);
            przRest -= parcelasEmProcessamento != null ? parcelasEmProcessamento.size() : 0;
        }

        try {
            if (!Objects.equals(autdes.getAdeVlr(), alternParam.getAdeVlr()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_NOVO_VALOR, responsavel)) {
                adeVlr = alternParam.getAdeVlr();
            } else {
                adeVlr = autdes.getAdeVlr();
            }

            if (!Objects.equals(przRest, alternParam.getAdePrazo()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_NUM_PRESTACOES_REST, responsavel)) {
                adePrz = alternParam.getAdePrazo();
            } else {
                adePrz = przRest;
            }

            if (!Objects.equals(autdes.getAdeIdentificador(), alternParam.getAdeIdentificador()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_ADE_IDENTIFICADOR, responsavel)) {
                adeIdentificador = alternParam.getAdeIdentificador();
            } else {
                adeIdentificador = autdes.getAdeIdentificador();
            }

            if (!Objects.equals(autdes.getAdeVlrLiquido(), alternParam.getAdeVlrLiquido()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_NOVO_VALOR_LIQ, responsavel)) {
                adeVlrLiq = alternParam.getAdeVlrLiquido();
            } else {
                adeVlrLiq = autdes.getAdeVlrLiquido();
            }

            if (!Objects.equals(autdes.getAdeVlrSegPrestamista(), alternParam.getAdeVlrSegPrestamista()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_SEG_PRESTAMISTA, responsavel)) {
                vlrSegPrestamista = alternParam.getAdeVlrSegPrestamista();
            } else {
                vlrSegPrestamista = autdes.getAdeVlrSegPrestamista();
            }

            if (!Objects.equals(autdes.getAdeVlrIof(), alternParam.getAdeVlrIof()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_IOF, responsavel)) {
                vlrIof = alternParam.getAdeVlrIof();
            } else {
                vlrIof = autdes.getAdeVlrIof();
            }

            if (!Objects.equals(autdes.getAdeVlrTac(), alternParam.getAdeVlrTac()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_TAC, responsavel)) {
                vlrTac = alternParam.getAdeVlrTac();
            } else {
                vlrTac = autdes.getAdeVlrTac();
            }

            if (!Objects.equals(autdes.getAdeVlrMensVinc(), alternParam.getAdeVlrMensVinc()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_MENS_VINC, responsavel)) {
                vlrMesVinc = alternParam.getAdeVlrMensVinc();
            } else {
                vlrMesVinc = autdes.getAdeVlrMensVinc();
            }

            if (!Objects.equals(autdes.getAdeTaxaJuros(), alternParam.getAdeTaxaJuros()) && ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_CONSIGNACAO_VLR_LIQ_TX_JUROS, responsavel)) {
                txjuros = alternParam.getAdeTaxaJuros();
            } else {
                txjuros = autdes.getAdeTaxaJuros();
            }

            alterarParam = new AlterarConsignacaoParametros(alternParam.getAdeCodigo(), adeVlr, adePrz, adeIdentificador, alternParam.isValidar(), alternParam.getAdeIndice(),
                                                            vlrTac, vlrIof, adeVlrLiq, vlrMesVinc, txjuros, alternParam.getAnoMesFim(), alternParam.getParametros(), vlrSegPrestamista, alternParam.isSerAtivo(),
                                                            alternParam.isValidaBloqueado(), alternParam.isCnvAtivo(), alternParam.isSvcAtivo(), alternParam.isCsaAtivo(), alternParam.isOrgAtivo(),
                                                            alternParam.isEstAtivo(), alternParam.isCseAtivo(), alternParam.getAdeCarencia(), alternParam.getSerLogin(), alternParam.getSerSenha(),
                                                            alternParam.isValidaSenhaServidor(), alternParam);

        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        }
        return alterarParam;
    }
}
