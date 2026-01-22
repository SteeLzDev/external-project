package com.zetra.econsig.service.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_IMP_HISTORICO_OCORRENCIA_ADE_CONSIGNACAO_SUSPENSA_REJEITO_PARCELA_FOLHA;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReativarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.SuspenderConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.job.process.ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DecisaoJudicialHome;
import com.zetra.econsig.persistence.entity.FuncaoAlteraMargemAde;
import com.zetra.econsig.persistence.entity.FuncaoAlteraMargemAdeHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.consignacao.ObtemContratoPrdRejeitadaReativadoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasPeriodoQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: SuspenderConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Suspensão/Reativação de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SuspenderConsignacaoControllerBean extends AutorizacaoControllerBean implements SuspenderConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SuspenderConsignacaoControllerBean.class);

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    /**
     * Suspende um contrato ativo: Deferido, Em Andamento ou Estoque
     * @param adeCodigo - código do contrato a reativar
     * @param tipoMotivoOperacao
     * @param parametros
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void suspender(String adeCodigo, CustomTransferObject tipoMotivoOperacao, SuspenderConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final boolean usuPossuiSuspensaoAvancada = (responsavel.isCseSupOrg() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) && responsavel.temPermissao(CodedValues.FUN_SUSP_AVANCADA_CONSIGNACAO);

                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();

                final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
                final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
                final String csaCodigo = convenio.getConsignataria().getCsaCodigo();
                final String svcCodigo = convenio.getServico().getSvcCodigo();

                // Contrato em estoque pode ser suspenso por consignante ou suporte
                final boolean suspendeEstoque = (responsavel.isCseSup() || responsavel.isCsa()) && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo));

                if (!CodedValues.SAD_DEFERIDA.equals(sadCodigo) && !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) && !suspendeEstoque) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.suspensa.situacao.atual.dela.nao.permite.esta.operacao", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
                }

                // Indica se o registro de aut desconto foi alterado e precisa ser persistido
                boolean alterou = false;

                java.util.Date ocaPeriodo = null;
                if (((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                        ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) &&
                        (tipoMotivoOperacao != null) && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO))) {
                    ocaPeriodo = DateHelper.parse(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd");
                }

                //DESENV-17375: para usuários CSE/SUP/ORG pode-se optar por registrar uma suspensão comum ou suspensão de administração (CSE). Default é true para suspensão de CSE.
                final String sadSuspensaoPeloPapelUsuario = responsavel.isCseSupOrg() ? CodedValues.SAD_SUSPENSA_CSE : CodedValues.SAD_SUSPENSA;
                sadCodigo = (parametros != null) && !parametros.isSuspendeCse() ? CodedValues.SAD_SUSPENSA : sadSuspensaoPeloPapelUsuario;
                final String ocaCodigo = modificaSituacaoADE(autdes, sadCodigo, responsavel);
                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                    final boolean obsSuspensaoObrigatorio = ParamSist.getBoolParamSist(CodedValues.TPC_OBS_OBRIGATORIO_SUSPENSAO_ADE, responsavel);
                    //DESENV-17375: suspensão por processos automáticos executado por usuário de sistema, então não deve exigir preenchimento de observação.
                    if (!responsavel.isSistema() && obsSuspensaoObrigatorio && TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_OBS))) {
                        throw new AutorizacaoControllerException("mensagem.erro.preencher.observacao.para.operacao.suspender.autorizacao", responsavel);
                    }

                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                final String obs = ApplicationResourcesHelper.getMessage(responsavel.isCseSupOrg() ? "mensagem.informacao.consignacao.suspensa.pela.cse" : "mensagem.informacao.consignacao.suspensa", responsavel);

                // Se exportação somente inicial, inclui ocorrência de liquidação para ser enviada para a folha
                final boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);
                if (exportacaoInicial) {
                    // Remove qualquer ocorrencia de relançamento
                    removeOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, responsavel);

                    // Inclui ocorrencia de liquidação
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, obs, null, null, null, ocaPeriodo, null, responsavel);
                }

                // Inclui ocorrencia de suspensão de contrato
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO, obs, null, null, null, ocaPeriodo, null, responsavel);

                // Gera o Log de auditoria
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SUSPENDER_CONSIGNACAO, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.SUSPENDER_CONSIGNACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();

                if ((parametros != null) && (parametros.getAnexos() != null) && !parametros.getAnexos().isEmpty()) {
                    final String aadDescricao = ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.suspensao.consignacao", responsavel);
                    final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);
                    final String[] visibilidadeAnexos = parametros.getVisibilidadeAnexos();
                    if (visibilidadeAnexos != null) {
                        Arrays.sort(visibilidadeAnexos);
                    }
                    final String aadExibeSup = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SUPORTE) >= 0) ? "S" : "N";
                    final String aadExibeCse = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNANTE) >= 0) ? "S" : "N";
                    final String aadExibeOrg = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_ORGAO) >= 0) ? "S" : "N";
                    final String aadExibeCsa = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNATARIA) >= 0) ? "S" : "N";
                    final String aadExibeCor = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CORRESPONDENTE) >= 0) ? "S" : "N";
                    final String aadExibeSer = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SERVIDOR) >= 0) ? "S" : "N";
                    for (final File anexo : parametros.getAnexos()) {
                        if (anexo.exists()){
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, new java.sql.Date(periodoAtual.getTime()),
                                    TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_SUSPENSAO, aadExibeSup, aadExibeCse, aadExibeOrg, aadExibeCsa, aadExibeCor, aadExibeSer, responsavel);
                        }
                    }
                }

                // se for alteração avançada, remover incidência da margem conforme escolha do usuário
                if ((parametros != null) && parametros.isRemoveIncidenciaMargem()) {
                    if (!usuPossuiSuspensaoAvancada) {
                        throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.para.alteracao.avancada.contratos", responsavel);
                    }
                    if (autdes.getAdeIncMargem().compareTo(CodedValues.INCIDE_MARGEM_NAO) != 0) {
                        // recupera incidência de margem antiga
                        final Short adeIncMargemOld = autdes.getAdeIncMargem();
                        // altera o incidência de margem para zero
                        autdes.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                        // Armazena o valor da autorização que já foi modificado para poder liberar o valor na margem antiga
                        // e prender na margem nova para onde o contrato foi migrado.
                        // Seta o ADE_VLR para zero para ser realizada as validações na migração da margem
                        final BigDecimal adeVlr = autdes.getAdeVlr();
                        autdes.setAdeVlr(BigDecimal.ZERO);
                        // Libera margem de acordo com a incidência antiga
                        atualizaMargem(autdes.getRegistroServidor().getRseCodigo(), adeIncMargemOld, adeVlr.negate(), false, false, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                        // Volta com o ADE_VLR do contrato que já foi alterado anteriormente
                        autdes.setAdeVlr(adeVlr);
                        // Adiciona a alteração da incidência de margem no log
                        log.addChangedField(Columns.ADE_INC_MARGEM, CodedValues.INCIDE_MARGEM_NAO, adeIncMargemOld);
                        alterou = true;
                    }
                }

                if ((parametros != null) && parametros.isAlteraIncidenciaMargem() ) {
                    final FuncaoAlteraMargemAde funcaoAlteraMargemAde = buscarFuncaoAlteraMargemAde(CodedValues.FUN_SUSP_CONSIGNACAO, responsavel);
                    final boolean possuiFuncaoAlteraMargemAde = (funcaoAlteraMargemAde != null);
                    if(possuiFuncaoAlteraMargemAde && (autdes.getAdeIncMargem() != null) && (autdes.getAdeIncMargem() != 0) && autdes.getAdeIncMargem().equals(funcaoAlteraMargemAde.getMarCodigoOrigem())) {
                        // recupera incidência de margem antiga
                        final Short adeIncMargemOld = autdes.getAdeIncMargem();
                        // altera o incidência de margem para zero
                        autdes.setAdeIncMargem(parametros.getMarCodigo());
                        // Armazena o valor da autorização que já foi modificado para poder liberar o valor na margem antiga
                        // e prender na margem nova para onde o contrato foi migrado.
                        // Seta o ADE_VLR para zero para ser realizada as validações na migração da margem
                        final BigDecimal adeVlr = autdes.getAdeVlr();
                        autdes.setAdeVlr(BigDecimal.ZERO);
                        // Libera margem de acordo com a incidência antiga
                        atualizaMargem(autdes.getRegistroServidor().getRseCodigo(), adeIncMargemOld, adeVlr.negate(), false, false, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                        // Volta com o ADE_VLR do contrato que já foi alterado anteriormente
                        autdes.setAdeVlr(adeVlr);
                        // Adiciona a alteração da incidência de margem no log
                        log.addChangedField(Columns.ADE_INC_MARGEM, parametros.getMarCodigo(), adeIncMargemOld);
                        alterou = true;
                    }
                }

                if ((parametros != null) && (parametros.getDataReativacaoAutomatica() != null)) {
                    // Atualiza a data de reativação automática
                    autdes.setAdeDataReativacaoAutomatica(parametros.getDataReativacaoAutomatica());
                    // Adiciona no log a alteração da data de reativação automática
                    log.addChangedField(Columns.ADE_DATA_REATIVACAO_AUTOMATICA, parametros.getDataReativacaoAutomatica());
                    alterou = true;
                } else if (autdes.getAdeDataReativacaoAutomatica() != null) {
                    // Caso não tenha sido informada uma data de reativação automática, mas ela já esteja preenchida, remove a informação
                    log.addChangedField(Columns.ADE_DATA_REATIVACAO_AUTOMATICA, null, autdes.getAdeDataReativacaoAutomatica());
                    autdes.setAdeDataReativacaoAutomatica(null);
                    alterou = true;
                }

                // Persiste as alterações realizadas
                if (alterou) {
                    AbstractEntityHome.update(autdes);
                }

                // Persiste as alterações no log
                log.write();

                // Cria o registro de decisão judicial, caso informado e o sistema permita
                if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel) &&
                        (parametros != null) && !TextHelper.isNull(parametros.getTjuCodigo()) && !TextHelper.isNull(parametros.getDjuTexto()) && !TextHelper.isNull(parametros.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                    DecisaoJudicialHome.create(ocaCodigo, parametros.getTjuCodigo(), parametros.getCidCodigo(), parametros.getDjuNumProcesso(), parametros.getDjuData(), parametros.getDjuTexto(), null);
                }

                if (CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo())) {
                    setDadoAutDesconto(adeCodigo, CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel);
                }

            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                    throw (AutorizacaoControllerException) ex;
                } else {
                    throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                }
            }
        }
    }

    @Override
    public FuncaoAlteraMargemAde buscarFuncaoAlteraMargemAde(String funCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            return FuncaoAlteraMargemAdeHome.findByFunCodigoAndPapCodigo(funCodigo, responsavel.getPapCodigo());
        } catch (final Exception ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Reativa um contrato suspenso
     * @param adeCodigo - código do contrato a reativar
     * @param tipoMotivoOperacao
     * @param parametros - parâmetros extras para a operação
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void reativar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, ReativarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                // Realizando validações de limites
                verificaLimiteAoReativarContrato(adeCodigo, responsavel);

                // A reativação de contrato que contenha decisão judicial só podem acontecer pelo módulo de decisão judicial
                verificaAlteracaoReativacaoDecisaoJudicial(adeCodigo, responsavel);

                // True caso o usuário esteja fazendo reativação via função de decisão judicial
                final boolean decisaoJudicial = CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo());

                final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                if (!CodedValues.SAD_SUSPENSA.equals(sadCodigo) && !CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo)) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.reativada.situacao.atual.dela.nao.permite.esta.operacao", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
                }
                if (CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && !responsavel.isCseSupOrg() && !responsavel.isSistema() && !decisaoJudicial) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.suspensa.pelo.consignante.nao.pode.ser.reativada.por.consignataria", responsavel);
                }
                final boolean suspendeAdePrdRejeitadaRetorno = ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel);
                final boolean contratoSuspensoPrdRejeitada = suspendeAdePrdRejeitadaRetorno && contratoSuspensoPrdRejeitadaNaoReativado(adeCodigo, responsavel);
                if (responsavel.isSer()) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.reativada.pelo.servidor", responsavel);
                }

                final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
                final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
                final String csaCodigo = convenio.getConsignataria().getCsaCodigo();
                final String svcCodigo = convenio.getServico().getSvcCodigo();

                // Verifica se a margem disponível é zerada ou positiva para permitir reativação de contrato pelo servidor
                final BigDecimal margemConsignavel = new MargemDisponivel(autdes.getRegistroServidor().getRseCodigo(), csaCodigo, svcCodigo, autdes.getAdeIncMargem(), responsavel).getMargemRestante();
                if (contratoSuspensoPrdRejeitada && (margemConsignavel.compareTo(BigDecimal.ZERO) < 0)) {
                    throw new AutorizacaoControllerException("mensagem.margemInsuficiente", responsavel, NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang(), true));
                }

                java.util.Date ocaPeriodo = null;
                if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                        ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                    if ((tipoMotivoOperacao != null) && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO))) {
                        final String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
                        ocaPeriodo = DateHelper.parse(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd");
                        ocaPeriodo = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), responsavel);
                    }
                }

                if (ocaPeriodo == null) {
                    ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(convenio.getOrgao().getOrgCodigo(), responsavel);
                }

                final ParamSvcTO paramTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                String sad = null;
                boolean adeSuspensaReativaPermissaoGestor = false;
                final Integer prdPagas = autdes.getAdePrdPagas();
                if (contratoSuspensoPrdRejeitada && ParamSist.getBoolParamSist(CodedValues.TPC_REATIVAR_CONTRATO_SUSP_PRD_REJEITADA_EXIGE_CONF_GESTOR, responsavel) && paramTo.isTpsRequerDeferimentoReservas()) {
                    adeSuspensaReativaPermissaoGestor = true;
                    sad = CodedValues.SAD_AGUARD_DEFER;
                } else if ((prdPagas == null) || (prdPagas.intValue() == 0)) {
                    sad = CodedValues.SAD_DEFERIDA;
                } else {
                    sad = CodedValues.SAD_EMANDAMENTO;
                }

                // confere se o contrato não incide em margem e se o serviço incide em alguma. Caso ocorra, atualiza o contrato
                // para incidir na margem do serviço corrente e atualiza a margem do servidor
                final Short adeIncMargem = autdes.getAdeIncMargem();

                // Gera o Log de auditoria
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REATIVAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);

                // se for uma correção de inconsistência entre incidência de margem do contrato e de serviço, seta para true
                // para verificação de obrigatoriedade de motivo de operação.
                final Short svcIncMargem = paramTo.getTpsIncideMargem();
                boolean correcaoIncMargem = false;

                try {
                    if (((adeIncMargem == null) || (adeIncMargem.shortValue() == 0)) && CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) &&
                         (svcIncMargem != null) && (svcIncMargem != 0)) {
                        autdes.setAdeIncMargem(svcIncMargem);
                        AbstractEntityHome.update(autdes);
                        log.addChangedField(Columns.ADE_INC_MARGEM, svcIncMargem, CodedValues.INCIDE_MARGEM_NAO);

                        final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKeyForUpdate(autdes.getRegistroServidor().getRseCodigo());

                        try {
                            // Tenta atualizar na margem o valor total da autorização
                            atualizaMargem(registroServidor.getRseCodigo(), svcIncMargem, autdes.getAdeVlr(), false, true, true, null, csaCodigo, svcCodigo, null, responsavel);
                        } catch (final AutorizacaoControllerException ex) {
                            BigDecimal limite = new BigDecimal("0.00");
                            final CustomTransferObject param = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, new BigDecimal("0"), false, null);
                            if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
                                try {
                                    limite = new BigDecimal(param.getAttribute(Columns.PSE_VLR).toString());
                                } catch (final NumberFormatException nfex) {
                                    LOG.debug("Valor do parametro de limite de ade sem margem invalido: " + param.getAttribute(Columns.PSE_VLR));
                                }
                            }

                            /*
                             * Se não foi possível reservar todo o valor, verifica se o serviço tem
                             * limite para autorizações sem margem. Verifica também se não é um compulsório,
                             * e caso positivo, pesquisa o vlr total dos contratos que podem ser estocados
                             * liberando assim vlr de margem para a inclusão
                             */
                            atualizaMargemCompulsorios(registroServidor.getRseCodigo(), svcCodigo, csaCodigo, autdes.getAdeCodigo(), null, autdes.getAdeVlr(), limite, svcIncMargem, null, true, true, false, responsavel);
                        }

                        correcaoIncMargem = true;
                    } else if ((parametros != null) && parametros.isAlteraIncidenciaMargem() ) {
                        final FuncaoAlteraMargemAde funcaoAlteraMargemAde = buscarFuncaoAlteraMargemAde(CodedValues.FUN_REAT_CONSIGNACAO, responsavel);
                        final boolean possuiFuncaoAlteraMargemAde = (funcaoAlteraMargemAde != null);
                        if(possuiFuncaoAlteraMargemAde && (autdes.getAdeIncMargem() != null) && (autdes.getAdeIncMargem() != 0) && autdes.getAdeIncMargem().equals(funcaoAlteraMargemAde.getMarCodigoOrigem())) {
                            // recupera incidência de margem antiga
                            final Short adeIncMargemOld = autdes.getAdeIncMargem();
                            // altera o incidência de margem para zero
                            autdes.setAdeIncMargem(parametros.getMarCodigo());
                            // Armazena o valor da autorização que já foi modificado para poder liberar o valor na margem antiga
                            // e prender na margem nova para onde o contrato foi migrado.
                            // Seta o ADE_VLR para zero para ser realizada as validações na migração da margem
                            final BigDecimal adeVlr = autdes.getAdeVlr();
                            autdes.setAdeVlr(BigDecimal.ZERO);
                            // Libera margem de acordo com a incidência antiga
                            atualizaMargem(autdes.getRegistroServidor().getRseCodigo(), adeIncMargemOld, adeVlr.negate(), false, false, false, null, csaCodigo, svcCodigo, null, responsavel);
                            // Volta com o ADE_VLR do contrato que já foi alterado anteriormente
                            autdes.setAdeVlr(adeVlr);
                            // Adiciona a alteração da incidência de margem no log
                            log.addChangedField(Columns.ADE_INC_MARGEM, parametros.getMarCodigo(), adeIncMargemOld);
                            AbstractEntityHome.update(autdes);
                            removeOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_INCIDENCIA_MARGEM_SUSPENSAO, responsavel);
                        }
                    }

                } catch (final NumberFormatException nex) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.margem.incidente.servico.invalido", responsavel);
                }

                // Não cria ocorrência na modificação da situação do contrato.
                // Cria ocorrência no final do método, para garantir a ocorrência com o último status atualizado.
                modificaSituacaoADE(autdes, sad, responsavel, false, true);

                final boolean exigeTMO = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REAT_CONSIGNACAO, responsavel);
                if ((exigeTMO || correcaoIncMargem) && ((tipoMotivoOperacao == null) || TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO))) && !responsavel.isSistema()) {
                    throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
                }

                // Se exportação somente inicial, inclui ocorrência de relançamento para ser enviada para a folha,
                // apenas se a liquidação do contrato já foi enviada para a folha. Faz também a exclusão da ocorrência
                // de liquidação, evitando que um contrato aberto tenha ocorrência de liquidação.
                final boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);
                if (exportacaoInicial && CodedValues.INTEGRA_FOLHA_SIM.equals(autdes.getAdeIntFolha())) {
                    // Obtém as ocorrências de liquidação do contrato que está sendo reativado
                    final Collection<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
                    if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                        // Obtém a data da última ocorrência de liquidação
                        final OcorrenciaAutorizacao ocaLiq = ocorrencias.iterator().next();
                        final java.util.Date ocaLiqData = ocaLiq.getOcaData();
                        final java.util.Date ocaLiqPeriodo = ocaLiq.getOcaPeriodo();
                        // Obtém a data inicial do período atual de lançamento
                        final java.util.Date dataIniPeriodo = PeriodoHelper.getInstance().getDataIniPeriodoAtual(convenio.getOrgao().getOrgCodigo(), responsavel);

                        // Se o período de lançamento atual for depois do período de liquidação
                        // OU a data de liquidação do contrato é menor que a data inicial do período de lançamento atual,
                        // então significa que a exclusão do contrato já foi para a folha. Desta forma a ocorrência
                        // de relançamento deve ser incluída para que o contrato seja reexportado no próximo movimento.
                        if (((ocaLiqPeriodo != null) && (ocaPeriodo.compareTo(ocaLiqPeriodo) == 1)) || (dataIniPeriodo.compareTo(ocaLiqData) == 1)) {
                            // Zera os campos folha, para o sistema identificar este contrato para ser enviado à folha.
                            // OBS: DEVE SER EXECUTADO ANTES DO REIMPLANTE, PARA NÃO SOBREPOR AS ATUALIZAÇÕES LÁ REALIZADAS.
                            autdes.setAdeVlrFolha(null);
                            autdes.setAdePaga("N");
                            AbstractEntityHome.update(autdes);
                            log.addChangedField(Columns.ADE_VLR_FOLHA, null);
                            log.addChangedField(Columns.ADE_PAGA, "N");

                            // Reimplanta contrato para a próxima exportação
                            // Ou se suspende contrato com parcela rejeitada,
                            // tenta reimplantar, em caso de não reimplante, operação não poderá ser concluída
                            if (reimplantarConsignacaoController.sistemaReimplanta(adeCodigo, responsavel) ||
                                (contratoSuspensoPrdRejeitada)) {
                                reimplantarConsignacaoController.reimplantar(adeCodigo, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.reativada", responsavel), tipoMotivoOperacao, false, false, true, adeSuspensaReativaPermissaoGestor, responsavel);
                            }
                        }

                        if (ocaPeriodo == null) {
                            // Remove todas as ocorrencias de liquidação, existentes, e provavelmente incluidas na suspensão
                            removeOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, responsavel);
                        } else {
                            // Se a reativação se refere a um período informado, remove apenas as ocorrências
                            // de liquidação que sejam iguais ou posteriores ao período da reativação, para
                            // evitar conflito de comando na exportação. As de períodos passados permanecerão.
                            for (final OcorrenciaAutorizacao oca : ocorrencias) {
                                if ((oca.getOcaPeriodo() != null) && (oca.getOcaPeriodo().compareTo(ocaPeriodo) >= 0)) {
                                    AbstractEntityHome.remove(oca);
                                }
                            }
                        }
                    } else // Se não tem ocorrência de liquidação, provavelmente é um contrato antigo suspenso,
                    // ou contrato alterado judicialmente, ou histórico importado como suspenso. Como é
                    // movimento inicial, reimplanta o contrato.
                    // Ou se suspende contrato com parcela rejeitada e a reativação está sendo realizada pelo servidor,
                    // tenta reimplantar, em caso de não reimplante, operação não poderá ser concluída
                    if (reimplantarConsignacaoController.sistemaReimplanta(adeCodigo, responsavel) ||
                        (contratoSuspensoPrdRejeitada)) {
                        reimplantarConsignacaoController.reimplantar(adeCodigo, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.reativada", responsavel), tipoMotivoOperacao, false, false, true, adeSuspensaReativaPermissaoGestor, responsavel);
                    }
                }

                if (autdes.getAdeDataReativacaoAutomatica() != null) {
                    // Caso não tenha sido informada uma data de reativação automática, mas ela já esteja preenchida, remove a informação
                    log.addChangedField(Columns.ADE_DATA_REATIVACAO_AUTOMATICA, null, autdes.getAdeDataReativacaoAutomatica());
                    autdes.setAdeDataReativacaoAutomatica(null);
                    AbstractEntityHome.update(autdes);
                }

                // Cria ocorrência de alteração de situação do contrato com o último status atualizado.
                final AutDesconto autdesAtual = AutDescontoHome.findByPrimaryKey(adeCodigo);
                final String sadAtual = autdesAtual.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                final String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, sadCodigo, sadAtual), null, null, null, ocaPeriodo, null, responsavel);

                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_PERIODO, ocaPeriodo);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                // Inclui ocorrencia específica de reativação do contrato
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_REATIVACAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.reativada", responsavel), null, null, null, ocaPeriodo, null, responsavel);

                // Inclui ocorrencia específica de reativação do contrato com parcela rejeitada
                if (contratoSuspensoPrdRejeitada) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_REATIVACAO_CONTRATO_PARCELA_REJEITADA, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.reativada", responsavel), null, null, null, ocaPeriodo, null, responsavel);
                }

                // Persiste alterações no log
                log.write();

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.REATIVAR_CONSIGNACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();

                // DESENV-17377: envia email de notificação para o consignante da reativação do contrato com parcela rejeitada.
                final ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada enviarEmailReativacao = new ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada(adeCodigo, responsavel);
                enviarEmailReativacao.start();

                if ((parametros != null) && (parametros.getAnexos() != null) && !parametros.getAnexos().isEmpty()) {
                    final String aadDescricao = ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.reativacao.consignacao", responsavel);
                    final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);
                    final String[] visibilidadeAnexos = parametros.getVisibilidadeAnexos();
                    if (visibilidadeAnexos != null) {
                        Arrays.sort(visibilidadeAnexos);
                    }
                    final String aadExibeSup = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SUPORTE) >= 0) ? "S" : "N";
                    final String aadExibeCse = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNANTE) >= 0) ? "S" : "N";
                    final String aadExibeOrg = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_ORGAO) >= 0) ? "S" : "N";
                    final String aadExibeCsa = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNATARIA) >= 0) ? "S" : "N";
                    final String aadExibeCor = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CORRESPONDENTE) >= 0) ? "S" : "N";
                    final String aadExibeSer = (visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SERVIDOR) >= 0) ? "S" : "N";
                    for (final File anexo : parametros.getAnexos()) {
                        if (anexo.exists()){
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, new java.sql.Date(periodoAtual.getTime()),
                                    TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_REATIVACAO, aadExibeSup, aadExibeCse, aadExibeOrg, aadExibeCsa, aadExibeCor, aadExibeSer, responsavel);
                        }
                    }
                }

                // Cria o registro de decisão judicial, caso informado e o sistema permita
                if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                    if ((parametros != null) && !TextHelper.isNull(parametros.getTjuCodigo()) && !TextHelper.isNull(parametros.getDjuTexto()) && !TextHelper.isNull(parametros.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                        DecisaoJudicialHome.create(ocaCodigo, parametros.getTjuCodigo(), parametros.getCidCodigo(), parametros.getDjuNumProcesso(), parametros.getDjuData(), parametros.getDjuTexto(), null);
                    }
                }

                if (CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo())) {
                    setDadoAutDesconto(adeCodigo, CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel);
                }

            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                if (ex.getClass().equals(AutorizacaoControllerException.class) ||
                        ex.getClass().equals(PeriodoException.class)) {
                    throw new AutorizacaoControllerException(ex);
                } else {
                    throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                }
            }
        }
    }

    @Override
    public void removerDataReativacaoAutomatica(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                if (autdes.getAdeDataReativacaoAutomatica() != null) {
                    // Gera o Log de auditoria
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REATIVAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.addChangedField(Columns.ADE_DATA_REATIVACAO_AUTOMATICA, null, autdes.getAdeDataReativacaoAutomatica());
                    log.write();

                    autdes.setAdeDataReativacaoAutomatica(null);
                    AbstractEntityHome.update(autdes);
                }
            } catch (FindException | UpdateException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            } catch (final LogControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(ex);
            }
        }
    }

    /**
     * Suspensão de contratos cuja parcela foi rejeitada ou não houve retorno no processamento de retorna da folha
     */
    @Override
    public void suspenderContratosParcelaRejeitada(AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ListaParcelasPeriodoQuery lstPrdRejeitadas = new ListaParcelasPeriodoQuery();
        lstPrdRejeitadas.spdCodigos = Arrays.asList(CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_SEM_RETORNO);
        List<String> sadCodigosQuePermitemSuspensao = null;

        if (responsavel.isCseSup() || responsavel.isCsa()) {
            sadCodigosQuePermitemSuspensao = Arrays.asList(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_ESTOQUE,
                    CodedValues.SAD_ESTOQUE_MENSAL, CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        } else {
            sadCodigosQuePermitemSuspensao = Arrays.asList(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO);
        }
        lstPrdRejeitadas.sadCodigos = sadCodigosQuePermitemSuspensao;

        try {
        final List<TransferObject> prdReijtadas = lstPrdRejeitadas.executarDTO();

        for (final TransferObject prd: prdReijtadas) {

                final String adeCodigo = (String) prd.getAttribute(Columns.PRD_ADE_CODIGO);

                // DESENV-17375: suspensões vindas de retorno executam como usuário de sistema. Porém, o contrato
                // deve estar com status de suspensão normal (sad_codigo = 6)
                final SuspenderConsignacaoParametros suspParam = new SuspenderConsignacaoParametros();
                suspParam.setSuspendeCse(false);
                suspender(adeCodigo, null, suspParam, responsavel);

                final RegistroServidor rse = RegistroServidorHome.findByAutDesconto(adeCodigo);

                OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA, responsavel.getUsuCodigo(),
                        ApplicationResourcesHelper.getMessage(MENSAGEM_IMP_HISTORICO_OCORRENCIA_ADE_CONSIGNACAO_SUSPENSA_REJEITO_PARCELA_FOLHA, responsavel) , null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(),
                        PeriodoHelper.getInstance().getPeriodoAtual(rse.getOrgCodigo(), responsavel), null);

        }

        } catch (HQueryException | FindException | CreateException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex.getMessageKey(), responsavel);
        }

    }

    @Override
    public boolean contratoSuspensoPrdRejeitadaNaoReativado(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery query = new ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery();
            query.adeCodigos = Arrays.asList(adeCodigo);
            final List<TransferObject> lista = query.executarDTO();
            return (lista != null) && !lista.isEmpty();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex.getMessageKey(), responsavel);
        }
    }

    @Override
    public List<TransferObject> verificaContratosForamSuspensosPrdRejeitada(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery query = new ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery();
            query.adeCodigos = adeCodigos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex.getMessageKey(), responsavel);
        }
    }

    @Override
    public boolean contratoSuspensoPrdRejeitadaReativado(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemContratoPrdRejeitadaReativadoQuery query = new ObtemContratoPrdRejeitadaReativadoQuery();
            query.adeCodigos = Arrays.asList(adeCodigo);
            final List<TransferObject> lista = query.executarDTO();
            return (lista != null) && !lista.isEmpty();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex.getMessageKey(), responsavel);
        }
    }
}

