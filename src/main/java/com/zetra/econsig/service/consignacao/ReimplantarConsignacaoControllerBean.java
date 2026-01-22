package com.zetra.econsig.service.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_OCA_ADE_RELANCAMENTO_AUTOMATICO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.geradoradenumero.AdeNumeroHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.movimento.ListaConsignacaoReimpContratosPermissaoCseQuery;
import com.zetra.econsig.persistence.query.movimento.ListaConsignacaoReimpInclusaoAnexoQuery;
import com.zetra.econsig.persistence.query.movimento.ListaConsignacaoReimpReducaoValorQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemCapitalDevidoQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemDatasUltimoPeriodoExportadoQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OrigemSolicitacaoEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ReimplantarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de reimplantação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ReimplantarConsignacaoControllerBean extends ReservarMargemControllerBean implements ReimplantarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReimplantarConsignacaoControllerBean.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        reimplantar(adeCodigo, obsOca, tipoMotivoOperacao, false, false, false, responsavel);
    }

    @Override
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        this.reimplantar(adeCodigo, obsOca, tipoMotivoOperacao, alterarNumeroAde, reduzirValorAde, reativacao, null, false, responsavel);
    }

    @Override
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        this.reimplantar(adeCodigo, obsOca, tipoMotivoOperacao, alterarNumeroAde, reduzirValorAde, reativacao, null, adeSuspensaReativaPermissaoGestor, responsavel);
    }

    @Override
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, Date ocaPeriodo, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(autdes.getRegistroServidor().getRseCodigo());

                validarReimplantacao(autdes, registroServidor, adeSuspensaReativaPermissaoGestor, responsavel);

                final String orgCodigo = registroServidor.getOrgao().getOrgCodigo();

                final String msgOca = ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO, responsavel);
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REIMPLANTAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);

                if (((tipoMotivoOperacao != null) && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO))) &&
                        ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                        ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel))) {
                        ocaPeriodo = DateHelper.toSQLDate(DateHelper.parse(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd"));
                }

                // Alterar a data inicial para o periodo atual de lançamentos
                Date prazoIni = (ocaPeriodo != null ? ocaPeriodo : PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel));
                // Valida o período inicial calculado
                prazoIni = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, prazoIni, responsavel);

                // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data inicio de acordo com essa data.
                final Date dataInicioFimAde = calcularDataIniFimMargemExtra(registroServidor.getRseCodigo(), prazoIni, autdes.getAdeIncMargem(), true, false, responsavel);

                boolean ocorrenciaInformacaoDataInicioMargem = false;
                if(dataInicioFimAde.compareTo(prazoIni) > 0) {
                    ocorrenciaInformacaoDataInicioMargem = true;
                    prazoIni = dataInicioFimAde;
                }

                final StringBuilder msgOcaBldr = new StringBuilder(msgOca);
                ajustarDatasPrazoReimplante(autdes, orgCodigo, msgOcaBldr, prazoIni, reativacao, alterarNumeroAde, adeSuspensaReativaPermissaoGestor, responsavel);

                if (!TextHelper.isNull(obsOca) && (tipoMotivoOperacao == null)) {
                    // Se não foi informado motivo, mas informada observação, grava junto da ocorrência,
                    // senão deixa para ser gravada junto com o motivo de operação
                    msgOcaBldr.append("<BR>").append(obsOca);
                }

                // Incluir ocorrência de informação (tipo 3) sobre o reimplante manual
                final String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, msgOcaBldr.toString(), responsavel);

                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                    // Grava motivo da operacao na ocorrência de informação gerada
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_PERIODO, prazoIni);
                    tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, obsOca);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                if (!adeSuspensaReativaPermissaoGestor && !possuiOcaReimplante(autdes, registroServidor)) {
                    // Incluir ocorrência de reimplante (TOC_RELANCAMENTO ou TOC_RELANCAMENTO_COM_REDUCAO_VALOR)
                    final String tocCodigo = (reduzirValorAde ? CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR : CodedValues.TOC_RELANCAMENTO);
                    criaOcorrenciaADE(adeCodigo, tocCodigo, ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO, responsavel), null, null, null, prazoIni, null, responsavel);
                }

                if(ocorrenciaInformacaoDataInicioMargem) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior", responsavel), responsavel);
                }

                if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                    final String origemSolicitacao = OrigemSolicitacaoEnum.ORIGEM_REIMPLANTE.getCodigo();
                    SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
                    		StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, null, null, origemSolicitacao,
                    		DateHelper.clearHourTime(autdes.getAdeAnoMesIni()));
                }

                // Gera o Log de auditoria
                log.write();
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

    /**
     * Ajusta os prazos, datas e parcelas pagas do contrato reimplantado de acordo com as regras:
     * 1.1) Se Prazo (ADE_PRAZO) diferente de NULL, Prazo recebe Prazo subtraído de Pagas (ADE_PRD_PAGAS). Se Prazo igual a NULL, permanece inalterado.
     * 1.2) Pagas (ADE_PRD_PAGAS) = 0.
     * 1.3) Status (SAD_CODIGO) = Deferido (4).
     * 1.4) Se Data inicial de referência (ADE_ANO_MES_INI_REF) igual a NULL, recebe o valor da Data inicial (ADE_ANO_MES_INI). Se Data inicial de referência diferente de NULL, permanece inalterada.
     * 1.5) Data inicial (ADE_ANO_MES_INI) recebe o período atual (variável pexPeriodo já presente no método).
     * 1.6) Se Data final de referência (ADE_ANO_MES_FIM_REF) igual a NULL, recebe o valor da Data final (ADE_ANO_MES_FIM). Se Data final de referência diferente de NULL, permanece inalterada.
     * 1.7) Se Prazo (ADE_PRAZO) diferente de NULL, Data final (ADE_ANO_MES_FIM) recebe o período atual acrescido do prazo do item 1.1.
     * @param autdes
     * @param orgCodigo
     * @param msgOca
     * @param prazoIni
     * @param reativacao
     * @param alterarNumeroAde
     * @param responsavel
     * @throws LogControllerException
     * @throws FindException
     * @throws AutorizacaoControllerException
     * @throws PeriodoException
     * @throws UpdateException
     */
    private void ajustarDatasPrazoReimplante(AutDesconto autdes, String orgCodigo, StringBuilder msgOca, java.sql.Date prazoIni, boolean reativacao, boolean alterarNumeroAde, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws LogControllerException, FindException, AutorizacaoControllerException, PeriodoException, UpdateException, CreateException {
        final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REIMPLANTAR_CONSIGNACAO, Log.LOG_INFORMACAO);
        log.setAutorizacaoDesconto(autdes.getAdeCodigo());

        // Zerar a quantidade de pagas. Consequentemente, status deve ser Deferido
        final int adePrdPagas = autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0;
        autdes.setAdePrdPagas(0);
        log.addChangedField(Columns.ADE_PRD_PAGAS, 0, adePrdPagas);
        msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
        msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.parcelas.pagas.alterado.de.arg0.para.zero", responsavel, String.valueOf(adePrdPagas)));

        if (!adeSuspensaReativaPermissaoGestor) {
            log.addChangedField(Columns.SAD_CODIGO, autdes.getStatusAutorizacaoDesconto().getSadCodigo(), CodedValues.SAD_DEFERIDA);
            autdes.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(CodedValues.SAD_DEFERIDA));
        }

        // Caso seja nula, alterar a data inicial referencia para a data inicial antiga
        final java.util.Date adeAnoMesIni = autdes.getAdeAnoMesIni();
        if (autdes.getAdeAnoMesIniRef() == null) {
            log.addChangedField(Columns.ADE_ANO_MES_INI_REF, adeAnoMesIni, autdes.getAdeAnoMesIniRef());
            msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
            msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inicial.referencia.alterada.para.arg0", responsavel, DateHelper.toPeriodString(adeAnoMesIni)));
            autdes.setAdeAnoMesIniRef(adeAnoMesIni);
        }

        // Procede a alteração da data inicial
        log.addChangedField(Columns.ADE_ANO_MES_INI, prazoIni, adeAnoMesIni);
        msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
        msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inicial.alterada.de.arg0.para.arg1", responsavel, DateHelper.toPeriodString(adeAnoMesIni), DateHelper.toPeriodString(prazoIni)));
        autdes.setAdeAnoMesIni(prazoIni);

        if (autdes.getAdePrazo() != null) {
         // Verifica se deve preservar parcela
            final boolean sisPreserva = sistemaPreservaParcela(autdes, responsavel);

            final Integer prazoNovo = calcularPrazoNovo(autdes, orgCodigo, prazoIni, reativacao, responsavel, adePrdPagas, adeAnoMesIni, sisPreserva);

            if (prazoNovo <= 0) {
                throw new AutorizacaoControllerException("mensagem.erro.prazo.zero", responsavel);
            }

            if (sisPreserva || reativacao) {
                // Caso seja nula, alterar a data final referencia para a data final antiga
                if (autdes.getAdeAnoMesFimRef() == null) {
                    log.addChangedField(Columns.ADE_ANO_MES_FIM_REF, autdes.getAdeAnoMesFim(), autdes.getAdeAnoMesFimRef());
                    msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
                    msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.final.referencia.alterada.para.arg0", responsavel, DateHelper.toPeriodString(autdes.getAdeAnoMesFim())));
                    autdes.setAdeAnoMesFimRef(autdes.getAdeAnoMesFim());
                }

                // Alterar a data final de acordo com a data inicial e o novo prazo
                final Date prazoFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, prazoIni, prazoNovo, autdes.getAdePeriodicidade(), responsavel);
                calcularDataIniFimMargemExtra(autdes.getRseCodigo(), prazoFim, autdes.getAdeIncMargem(), false, true, responsavel);

                log.addChangedField(Columns.ADE_ANO_MES_FIM, prazoFim, autdes.getAdeAnoMesFim());
                msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
                msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.data.final.alterada.de.arg0.para.arg1", responsavel, DateHelper.toPeriodString(autdes.getAdeAnoMesFim()), DateHelper.toPeriodString(prazoFim)));
                autdes.setAdeAnoMesFim(prazoFim);
            }

            log.addChangedField(Columns.ADE_PRAZO, prazoNovo, autdes.getAdePrazo());
            msgOca.append(msgOca.length() > 0 ? "<BR>"  : "");
            msgOca.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.de.arg0.para.arg1", responsavel, autdes.getAdePrazo().toString(), prazoNovo.toString()));
            autdes.setAdePrazo(prazoNovo);
        }

        if (alterarNumeroAde) {
            final Long adeNumeroNovo = AdeNumeroHelper.getNext(autdes.getVerbaConvenio().getVcoCodigo(), prazoIni);
            log.addChangedField(Columns.ADE_NUMERO, adeNumeroNovo, autdes.getAdeNumero());
            final String msgOcr = ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO, responsavel);
            final String msgOc = msgOcr + (msgOcr.length() > 0 ? "<BR>" : "") +
                    ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.ade.alterado.de.arg0.para.arg1", responsavel, autdes.getAdeNumero().toString(), adeNumeroNovo.toString());
            DadosAutorizacaoDescontoHome.create(autdes.getAdeCodigo(), CodedValues.TDA_NRO_ADE_ANTERIOR_REIMPLANTE, autdes.getAdeNumero().toString());
            autdes.setAdeNumero(adeNumeroNovo);
            criaOcorrenciaADE(autdes.getAdeCodigo(), CodedValues.TOC_REIMPLANTE_CONSIGNACAO_NOVA_ADE, msgOc, responsavel);
        }
        AbstractEntityHome.update(autdes);
    }

    protected Integer calcularPrazoNovo(AutDesconto autdes, String orgCodigo, java.sql.Date prazoIni, boolean reativacao, AcessoSistema responsavel, int adePrdPagas, java.util.Date adeAnoMesIni, boolean sisPreserva) throws FindException, PeriodoException {
        Integer prazoNovo = null;

        if (sisPreserva || reativacao) {
            if (sisPreserva) {
                // Alterar o prazo para (prazo - pagas)
                prazoNovo = autdes.getAdePrazo().intValue() - adePrdPagas;
            } else {
                // Se não preserva parcela mas é operação de reativação de contrato suspenso, calcula o prazo o prazo restante
                // como sendo quantidade de meses entre a data da última parcela gerada para o contrato e sua data final, pois
                // o prazo transcorreu com o contrato suspenso e não sendo rejeitado.
                final ParcelaDesconto prd = ParcelaDescontoHome.findLastByAutDesconto(autdes.getAdeCodigo());
                final java.util.Date dataUltParcela = (prd != null ? prd.getPrdDataDesconto() : adeAnoMesIni);

                prazoNovo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, dataUltParcela, autdes.getAdeAnoMesFim(), autdes.getAdePeriodicidade(), responsavel);
                if (prd != null) {
                    // Se o novo prazo foi calculado com base na última parcela, então
                    // subtrai 1 pois é o período da própria parcela
                    prazoNovo--;
                }

                // Verifica se o novo prazo é maior que Prazo Original - Pagas, o que não pode ocorrer
                if (prazoNovo > (autdes.getAdePrazo() - adePrdPagas)) {
                    prazoNovo = autdes.getAdePrazo() - adePrdPagas;
                }
            }
        } else {
            // Se nao preserva parcela, nao muda data final e recalcula prazo
            prazoNovo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, prazoIni, autdes.getAdeAnoMesFim(), autdes.getAdePeriodicidade(), responsavel);
        }

        return prazoNovo;
    }

    private void validarReimplantacao(AutDesconto autdes, RegistroServidor registroServidor, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (!sistemaReimplanta(autdes, responsavel)) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.reimplantar.contrato.consignataria.optou.por.nao.reimplantar", responsavel);
            }

            // Verifica o status da autorização de desconto
            final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
            if ((!CodedValues.SAD_DEFERIDA.equals(sadCodigo) && !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) && !CodedValues.SAD_ESTOQUE.equals(sadCodigo) && !CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) && (!adeSuspensaReativaPermissaoGestor && CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)))) {
                throw new AutorizacaoControllerException("mensagem.erro.situacao.atual.consignacao.nao.permite.esta.operacao.reimplantacao", responsavel);
            }

            final boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);
            if (!exportacaoInicial) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.reimplantar.contrato.movimento.financeiro.nao.inicial", responsavel);
            }
            if ((autdes.getAdeIntFolha() == null) || !autdes.getAdeIntFolha().equals(CodedValues.INTEGRA_FOLHA_SIM)) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.reimplantar.contrato.consignacao.nao.integra.folha", responsavel);
            }

            // Verifica se envia contrato de servidor excluido
            final boolean enviaADEExcluido = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_SIM, responsavel);
            if (CodedValues.SRS_INATIVOS.contains(registroServidor.getStatusRegistroServidor().getSrsCodigo()) && !enviaADEExcluido) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.reimplantar.contrato.servidor.excluido", responsavel);
            }

            if (possuiOcaReimplante(autdes, registroServidor)) {
                throw new AutorizacaoControllerException("mensagem.erro.existe.ocorrencia.reimplantacao.para.este.contrato.neste.periodo", responsavel);
            }

        } catch (final Exception ex) {
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }
        }
    }

    /**
     * faz o somatório dos valores de parcelas parciais devidos de um contrato.
     * Se o sistema não preserva parcela, adiciona ao somatório as parcelas rejeitadas.
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public BigDecimal calcularCapitalDevido(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ObtemCapitalDevidoQuery obtCpDevido = new ObtemCapitalDevidoQuery();
        obtCpDevido.adeCodigo = adeCodigo;
        obtCpDevido.responsavel = responsavel;
        BigDecimal capitalDevido = new BigDecimal(0);

        try {
            final List<TransferObject> capitalDevidoTO = obtCpDevido.executarDTO();

            if ((capitalDevidoTO != null) && !capitalDevidoTO.isEmpty()) {
                capitalDevido = (BigDecimal) capitalDevidoTO.get(0).getAttribute("CAPITAL_DEVIDO");
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        return capitalDevido;
    }

    @Override
    public void reimplantarCapitalDevido(TransferObject ade, BigDecimal adeVlrNovo, int prazoNovo, String tmoCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final BigDecimal adeVlrAntigo = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);
            final String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);

            if (TextHelper.isNull(tmoCodigo)) {
                throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            if (adeVlrNovo.compareTo(adeVlrAntigo) > 0) {
                throw new AutorizacaoControllerException("mensagem.reimp.capital.devido.parcela.maior.atual", responsavel);
            }

            final BigDecimal capitalDevido = adeVlrNovo.multiply(new BigDecimal(prazoNovo));
            final BigDecimal saldoDevedor = calcularCapitalDevido(adeCodigo, responsavel);

            if (capitalDevido.compareTo(saldoDevedor) > 0) {
                throw new AutorizacaoControllerException("mensagem.reimp.capital.devido.digitado.maior.vlr.devido", responsavel);
            }

            if (prazoNovo == 0) {
                throw new AutorizacaoControllerException("mensagem.erro.reimp.capital.devidor.prazo.zerado", responsavel);
            }

            // Conclui contrato original
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            modificaSituacaoADE(autdes, CodedValues.SAD_CONCLUIDO, responsavel);

            // Inclui novo contrato com o capital devido informado na tela
            final ReservarMargemParametros rmParam = new ReservarMargemParametros();

            rmParam.setRseCodigo((String) ade.getAttribute(Columns.RSE_CODIGO));
            rmParam.setAdeVlr(adeVlrNovo);
            rmParam.setAdePrazo(prazoNovo);
            rmParam.setAdeIdentificador("");
            rmParam.setCnvCodigo((String) ade.getAttribute(Columns.CNV_CODIGO));
            if (responsavel.isCor()) {
                rmParam.setCorCodigo(responsavel.getCodigoEntidade());
            }
            rmParam.setComSerSenha(Boolean.FALSE);
            rmParam.setAdeTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR));
            rmParam.setAdeIntFolha((Short) ade.getAttribute(Columns.ADE_INT_FOLHA));
            rmParam.setAdeIncMargem((Short) ade.getAttribute(Columns.ADE_INC_MARGEM));
            rmParam.setValidar(Boolean.FALSE);
            rmParam.setPermitirValidacaoTaxa(Boolean.FALSE);
            rmParam.setSerAtivo(Boolean.FALSE);
            rmParam.setCnvAtivo(Boolean.FALSE);
            rmParam.setSerCnvAtivo(Boolean.FALSE);
            rmParam.setSvcAtivo(Boolean.FALSE);
            rmParam.setCsaAtivo(Boolean.FALSE);
            rmParam.setOrgAtivo(Boolean.FALSE);
            rmParam.setEstAtivo(Boolean.FALSE);
            rmParam.setCseAtivo(Boolean.FALSE);
            rmParam.setAcao("RESERVAR");
            rmParam.setNomeResponsavel(responsavel.getUsuNome());
            rmParam.setValidaMargem(Boolean.FALSE);
            rmParam.setValidaTaxaJuros(Boolean.FALSE);
            rmParam.setValidaPrazo(Boolean.FALSE);
            rmParam.setValidaLimiteAde(Boolean.FALSE);
            rmParam.setValidaDadosBancarios(Boolean.FALSE);
            rmParam.setValidaSenhaServidor(Boolean.FALSE);
            rmParam.setIsReimpCapitalDevido(true);
            rmParam.setTmoCodigo(tmoCodigo);
            rmParam.setOcaObs(ocaObs);
            rmParam.setValidaAdeIdentificador(Boolean.FALSE);
            // DESENV-21185 : Salva a data inicial da consignação de origem na data ini ref para preservar a prioridade de desconto em folha
            rmParam.setAdeAnoMesIniRef((Date) (ade.getAttribute(Columns.ADE_ANO_MES_INI_REF) != null ? ade.getAttribute(Columns.ADE_ANO_MES_INI_REF) : ade.getAttribute(Columns.ADE_ANO_MES_INI)));

            final String adeCodigoNovo = reservarMargem(rmParam, responsavel);

            // cria o relacionamento de capital devido
            RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoNovo, CodedValues.TNT_REIMPLANTE_CAPITAL_DEVIDO, responsavel.getUsuCodigo());

            final Date ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(responsavel.getOrgCodigo(), responsavel);

            // cria ocorrência autorização para reimplantação de capital devido
            OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_PARCELAS_REINSERIDAS, responsavel.getUsuCodigo(), ocaObs, adeVlrAntigo, adeVlrNovo, responsavel.getIpUsuario(), null , ocaPeriodo, null);

        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (CreateException | PeriodoException | FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage());
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public boolean sistemaReimplanta(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKey(adeCodigo);
            return sistemaReimplanta(autdes, responsavel);
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private boolean sistemaReimplanta(AutDesconto autdes, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica se consignataria optou por nao reimplantar contrato
        boolean sisReimplanta = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, responsavel);
        final boolean csaAlteraReimplante = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, CodedValues.TPC_SIM, responsavel);
        if (sisReimplanta && csaAlteraReimplante){
            final Boolean reimplanta = obtemParametroServico(autdes, CodedValues.TPS_REIMPLANTACAO_AUTOMATICA, responsavel);
            final boolean defaultReimplante = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_SIM, responsavel);
            sisReimplanta = (reimplanta == null ? defaultReimplante : reimplanta);
        } // Reimplanta mesmo se consignante optou por nao reimplantar

        return sisReimplanta;
    }

    @Override
    public boolean sistemaPreservaParcela(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return sistemaPreservaParcela(adeCodigo, false, responsavel);
    }

    @Override
    public boolean sistemaPreservaParcela(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            AutDesconto autdes = null;
            if (arquivado) {
                autdes = AutDescontoHome.findArquivadoByPrimaryKey(adeCodigo);
            } else {
                autdes = AutDescontoHome.findByPrimaryKey(adeCodigo);
            }

            return sistemaPreservaParcela(autdes, responsavel);
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private boolean sistemaPreservaParcela(AutDesconto autdes, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica se o sistema preserva parcela e se a consignatária pode optar por preservar parcela
        boolean sisPreserva = ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);
        final boolean csaAlteraPreserva = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
        if (sisPreserva && csaAlteraPreserva) {
            final Boolean preserva = obtemParametroServico(autdes, CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL, responsavel);
            final boolean defaultPreserva = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
            sisPreserva = (preserva == null ? defaultPreserva : preserva);
        }
        return sisPreserva;
    }

    private Boolean obtemParametroServico(AutDesconto autdes, String parametro, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

            final List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(parametro);
            final List<TransferObject> params = parametroController.selectParamSvcCsa(cnvBean.getServico().getSvcCodigo(), cnvBean.getConsignataria().getCsaCodigo(), tpsCodigo, false, responsavel);
            if ((params != null) && (params.size() == 1)) {
                final CustomTransferObject param = (CustomTransferObject) params.get(0);
                if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null) && !param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) {
                    return "S".equalsIgnoreCase(param.getAttribute(Columns.PSC_VLR).toString());
                }
            }
        } catch (final Exception ex) {
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }
        }
        return null;
    }

    private boolean possuiOcaReimplante(AutDesconto autdes, RegistroServidor registroServidor) throws AutorizacaoControllerException {
        try {
            boolean possuiOcaReimplante = false;

            final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(autdes.getAdeCodigo(), new String[]{CodedValues.TOC_RELANCAMENTO, CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR, CodedValues.TOC_RELANCAMENTO_SEM_ANEXO});
            if ((ocorrencias != null) && !ocorrencias.isEmpty()) {

                // Pega as ultimas datas de exportação
                final ObtemDatasUltimoPeriodoExportadoQuery queryPeriodoExportacao = new ObtemDatasUltimoPeriodoExportadoQuery();
                queryPeriodoExportacao.orgCodigo = registroServidor.getOrgao().getOrgCodigo();
                final List<TransferObject> periodoExportacaoList = queryPeriodoExportacao.executarDTO();
                final java.util.Date dataFimExportacao = ((periodoExportacaoList != null) && !periodoExportacaoList.isEmpty()) ? (java.util.Date) periodoExportacaoList.get(0).getAttribute(Columns.HIE_DATA_FIM) : null;

                for (final OcorrenciaAutorizacao ocaBean : ocorrencias) {
                    final Date dataOcorrencia = new Date(ocaBean.getOcaData().getTime());
                    if ((dataFimExportacao != null) && (dataFimExportacao.compareTo(dataOcorrencia) < 0)) {
                        LOG.debug("Já existe a ocorrência de reimplantação para este contrato neste período.");
                        possuiOcaReimplante = true;
                        break;
                    }
                }
            }

            return possuiOcaReimplante;

        } catch (final Exception ex) {
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, (AcessoSistema) null, ex);
            }
        }
    }

    /**
     * Força o reimplante das consignações que foram reimplantadas no período anterior com permissão
     * de redução de parcela, e que foram aceitas e pagas pela folha. O reimplante é feito para restaurar
     * o valor de face do contrato, caso no período atual não possua nova ocorrência de reimplante
     * permitindo a redução da parcela. Na prática, não ocorre um "reimplante", visto que o contrato
     * está pago, e isso ocasionaria o envio de uma inclusão para a folha. É registrado uma ocorrência
     * de alteração para que esta seja enviada para folha, como alteração, ou como exclusão/inclusão.
     * @param orgCodigos  : orgãos a serem exportados
     * @param estCodigos  : estabelecimentos a serem exportados
     * @param responsavel : usuário que realiza a operação
     * @throws AutorizacaoControllerException
     */
    @Override
    public void reimplantarConsignacoesValorReduzidoPago(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoReimpReducaoValorQuery query = new ListaConsignacaoReimpReducaoValorQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            final List<TransferObject> consignacoes = query.executarDTO();
            for (final TransferObject consignacao : consignacoes) {
                final String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                final java.util.Date pexPeriodo = (java.util.Date) consignacao.getAttribute(Columns.PEX_PERIODO);
                final java.util.Date pexDataFim = (java.util.Date) consignacao.getAttribute(Columns.PEX_DATA_FIM);

                // Incluir ocorrência de alteração com data retroativa, visto que a exportação é realizada após o fechamento do período
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO, ApplicationResourcesHelper.getMessage(MENSAGEM_OCA_ADE_RELANCAMENTO_AUTOMATICO, responsavel), null, null, pexDataFim, pexPeriodo, null, responsavel);
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Força o reimplante das consignações que foram excluídas do movimento por não terem anexo de
     * consignação no período, e que agora possuem o anexo. Realiza a inclusão de uma ocorrência
     * de relançamento para que a rotina de exportação faça a inclusão entre as candidatas à exportação.
     * @param orgCodigos
     * @param estCodigos
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void reimplantarConsignacoesInclusaoAnexo(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String msgOca = ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO, responsavel);

            final ListaConsignacaoReimpInclusaoAnexoQuery query = new ListaConsignacaoReimpInclusaoAnexoQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            final List<TransferObject> consignacoes = query.executarDTO();
            for (final TransferObject consignacao : consignacoes) {
                final String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                final String orgCodigo = (String) consignacao.getAttribute(Columns.ORG_CODIGO);

                final java.util.Date pexPeriodo = (java.util.Date) consignacao.getAttribute(Columns.PEX_PERIODO);

                final AutDesconto autdes = AutDescontoHome.findByPrimaryKey(adeCodigo);
                final boolean adePaga = ((autdes.getAdePaga() != null) && "S".equals(autdes.getAdePaga()));

                final StringBuilder msgOcaBldr = new StringBuilder(msgOca);

                // Alterar a data inicial para o periodo atual de exportação
                Date prazoIni = DateHelper.toSQLDate(pexPeriodo);
                // Valida o período inicial calculado
                prazoIni = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, prazoIni, responsavel);

                if (!adePaga) {
                    ajustarDatasPrazoReimplante(autdes, orgCodigo, msgOcaBldr, prazoIni, false, false, false, responsavel);
                }

                // Incluir ocorrência de informação (tipo 3) sobre o reimplante manual
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, msgOcaBldr.toString(), prazoIni, responsavel);

                // Incluir ocorrência de alteração com data retroativa, visto que a exportação é realizada após o fechamento do período
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage(MENSAGEM_OCA_ADE_RELANCAMENTO_AUTOMATICO, responsavel), prazoIni, responsavel);
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (PeriodoException | LogControllerException | FindException | UpdateException | CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Força o reimplante das consignações que foram excluídas do movimento por não terem permissão do gestor no período,
     * e que agora possuem o anexo. Realiza a inclusão de uma ocorrência
     * de relançamento para que a rotina de exportação faça a inclusão entre as candidatas à exportação.
     * @param orgCodigos
     * @param estCodigos
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void reimplantarConsignacoesPermissaoCse(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String msgOca = ApplicationResourcesHelper.getMessage(MENSAGEM_OBS_OCA_REIMPLANTE_CONTRATO, responsavel);

            final ListaConsignacaoReimpContratosPermissaoCseQuery query = new ListaConsignacaoReimpContratosPermissaoCseQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            final List<TransferObject> consignacoes = query.executarDTO();
            for (final TransferObject consignacao : consignacoes) {
                final String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                final String orgCodigo = (String) consignacao.getAttribute(Columns.ORG_CODIGO);

                final java.util.Date pexPeriodo = (java.util.Date) consignacao.getAttribute(Columns.PEX_PERIODO);

                final AutDesconto autdes = AutDescontoHome.findByPrimaryKey(adeCodigo);
                final boolean adePaga = ((autdes.getAdePaga() != null) && "S".equals(autdes.getAdePaga()));

                final StringBuilder msgOcaBldr = new StringBuilder(msgOca);

                // Alterar a data inicial para o periodo atual de exportação
                Date prazoIni = DateHelper.toSQLDate(pexPeriodo);
                // Valida o período inicial calculado
                prazoIni = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, prazoIni, responsavel);

                if (!adePaga) {
                    ajustarDatasPrazoReimplante(autdes, orgCodigo, msgOcaBldr, prazoIni, false, false, false, responsavel);
                }

                // Incluir ocorrência de informação (tipo 3) sobre o reimplante manual
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, msgOcaBldr.toString(), prazoIni, responsavel);

                // Incluir ocorrência de alteração com data retroativa, visto que a exportação é realizada após o fechamento do período
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage(MENSAGEM_OCA_ADE_RELANCAMENTO_AUTOMATICO, responsavel), prazoIni, responsavel);
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (PeriodoException | LogControllerException | FindException | UpdateException | CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }
    }
}
