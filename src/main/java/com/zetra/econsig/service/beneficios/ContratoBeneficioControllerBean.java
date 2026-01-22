package com.zetra.econsig.service.beneficios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.BeneficiarioHome;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioHome;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.ContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaCttBeneficio;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoServico;
import com.zetra.econsig.persistence.entity.RelacionamentoServicoHome;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficio;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.persistence.entity.TipoLancamentoHome;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacaoHome;
import com.zetra.econsig.persistence.entity.TipoOcorrenciaHome;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaAdesPorCbePorTntPorSadQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaContratosBeneficioPendentesExclusaoQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaContratosBeneficioPendentesInclusaoQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaLancamentosContratosBeneficiosQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListaOcorrenciaContratosBeneficiosQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficioCancelamentoInadimplenciaQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficioPorRegistroServidorQuery;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficiosMensalidadeEdicaoTelaQuery;
import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoControllerBean;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: ContratoBeneficioControllerBean</p>
 * <p>Description: Controller Bean para operações de contrato beneficio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ContratoBeneficioControllerBean extends AutorizacaoControllerBean implements ContratoBeneficioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ContratoBeneficioControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarMargemController;

    @Autowired
    @Qualifier("deferirConsignacaoController")
    private DeferirConsignacaoController deferirConsignacaoController;

    @Autowired
    private ConfirmarConsignacaoController confirmarConsignacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private RelacionamentoBeneficioServicoController relacionamentoBeneficioServicoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Override
    public ContratoBeneficio findByPrimaryKey(String cbeCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            return ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);
        } catch (FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject listarContratosBeneficiosMensalidadeEdicaoTela(String cbeCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListarContratosBeneficiosMensalidadeEdicaoTelaQuery query = new ListarContratosBeneficiosMensalidadeEdicaoTelaQuery();
            query.cbeCodigo = cbeCodigo;
            query.tntCodigo = CodedValues.TNT_BENEFICIO_MENSALIDADE;

            List<TransferObject> result = query.executarDTO();

            if (result.isEmpty()) {
                throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nenhum.contrato.encontrado", responsavel);
            } else if (result.size() > 1) {
                throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.mais.de.um.contrato.encontrado", responsavel);
            }

            return result.get(0);
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    /**
     *
     * @param beneficiario
     * @param beneficio
     * @param numero
     * @param dataInclusao
     * @param dataInicioVigencia
     * @param itemLote
     * @param numeroLote
     * @param valorTotal
     * @param valorSubsidio
     * @param statusContratoBeneficio
     * @param responsavel
     * @return
     * @throws ContratoBeneficioControllerException
     */
    private ContratoBeneficio create(Beneficiario beneficiario, Beneficio beneficio, String numero, Date dataInclusao, Date dataInicioVigencia, String itemLote, String numeroLote, BigDecimal valorTotal, BigDecimal valorSubsidio, StatusContratoBeneficio statusContratoBeneficio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ContratoBeneficio contratoBeneficio = ContratoBeneficioHome.create(beneficiario, beneficio, numero, dataInclusao, dataInicioVigencia, itemLote, numeroLote, valorTotal, valorSubsidio, statusContratoBeneficio, null);

            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();

            criaOcorrenciaContratoBeneficio(contratoBeneficio.getCbeCodigo(), TipoOcorrenciaHome.findByPrimaryKey(CodedValues.TOC_INCLUSAO_CONTRATO_BENEFICIO).getTocCodigo(), ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.inclusao", responsavel), new Date(), "", responsavel);

            return contratoBeneficio;
        } catch (CreateException | LogControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String criaOcorrenciaContratoBeneficio(String cbeCodigo, String tocCodigo, String ocbObs, Date ocbData, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            responsavel = (responsavel == null ? AcessoSistema.getAcessoUsuarioSistema() : responsavel);
            OcorrenciaCttBeneficio OcorrenciaCttBeneficio = OcorrenciaContratoBeneficioHome.create(tocCodigo, cbeCodigo, tmoCodigo, ocbObs, responsavel);
            return OcorrenciaCttBeneficio.getOcbCodigo();
        } catch (CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void update(ContratoBeneficio contratoBeneficio, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        update(contratoBeneficio, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, null, tmoCodigo, responsavel);
    }

    @Override
    public void aprovar(List<TransferObject> beneficiarios, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ArrayList<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmoTO = new CustomTransferObject();
                tmoTO.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmoTO.setAttribute(Columns.OCA_OBS, ocbObs);
            }

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);
                String tibCodigo = (String) bfc.getAttribute(Columns.TIB_CODIGO);
                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Somente contrato benefício solicitado pode ser aprovado
                if (!StatusContratoBeneficioEnum.SOLICITADO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                    cancelaContratosOrigemMigracao(bfc, responsavel);
                }

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                if (ades == null || ades.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nao.pode.aprovado.situacao.atual.nao.permite", responsavel);
                }

                for (TransferObject aut : ades) {
                    if (tmoTO != null) {
                        tmoTO.setAttribute(Columns.ADE_CODIGO, aut.getAttribute(Columns.ADE_CODIGO));
                    }
                    confirmarConsignacaoController.confirmar(aut.getAttribute(Columns.ADE_CODIGO).toString(), tmoTO, responsavel);
                }

                update(cttBeneficio, CodedValues.TOC_APROVACAO_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
            }
        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void cancelaContratosOrigemMigracao(TransferObject dadosContrato, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery listaContratosBeneficiosRelacionamentoMigracaoOrigem = new ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery();

            String cbeCodigo = dadosContrato.getAttribute(Columns.CBE_CODIGO).toString();
            listaContratosBeneficiosRelacionamentoMigracaoOrigem.cbeCodigo = cbeCodigo;
            List<TransferObject> resultado = listaContratosBeneficiosRelacionamentoMigracaoOrigem.executarDTO();

            TipoMotivoOperacao tipoMotivoOperacao = TipoMotivoOperacaoHome.findByAcao(AcaoEnum.CANCELAMENTO_BENEFICIO_POR_MIGRACAO_BENEFICIO.getCodigo(), responsavel);

            cancelar(resultado, tipoMotivoOperacao.getTmoCodigo(), null, null, null,false, responsavel);
        } catch (HQueryException | ContratoBeneficioControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    @Override
    public void rejeitar(List<TransferObject> beneficiarios, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            StatusContratoBeneficio statusContrato = new StatusContratoBeneficio(StatusContratoBeneficioEnum.CANCELADO.getCodigo());

            ArrayList<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmoTO = new CustomTransferObject();
                tmoTO.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmoTO.setAttribute(Columns.OCA_OBS, ocbObs);
            }

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);
                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Somente contrato benefício solicitado pode ser rejeitado
                if (!StatusContratoBeneficioEnum.SOLICITADO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                if (ades == null || ades.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nao.pode.rejeitado.situacao.atual.nao.permite", responsavel);
                }

                for (TransferObject aut : ades) {
                    if (tmoTO != null) {
                        tmoTO.setAttribute(Columns.ADE_CODIGO, aut.getAttribute(Columns.ADE_CODIGO));
                    }
                    cancelarConsignacaoController.cancelar(aut.getAttribute(Columns.ADE_CODIGO).toString(), tmoTO, responsavel);
                }

                update(cttBeneficio, CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
            }

        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void cancelar(List<TransferObject> beneficiarios, String tmoCodigo, String ocbObs, String tdaAdesao, String tdaMesesContruicao, boolean solicitacaoCancelamento, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            StatusContratoBeneficio statusContrato;

            if (solicitacaoCancelamento) {
                statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo());
            } else {
                statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo());
            }

            ArrayList<String> sadCodigos = new ArrayList<>();
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO);

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);
                String tibCodigo = (String) bfc.getAttribute(Columns.TIB_CODIGO);
                String bfcSubsidioConcedido = bfc.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? bfc.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO;

                if (CodedValues.TPC_SIM.equals(bfcSubsidioConcedido)) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.algum.beneficiario.cadastrado.como.excecao", responsavel);
                }

                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Setando a data de cancelamento para o ultimo dia do mes.
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
                cttBeneficio.setCbeDataCancelamento(cal.getTime());

                // Somente contrato benefício ativos ou cancelamentos solicitados podem ser cancelados
                if (!StatusContratoBeneficioEnum.ATIVO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo()) && !StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                if (tibCodigo != null && tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                    TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                    criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                    criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                    List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                    if (ades == null || ades.isEmpty()) {
                        throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nao.pode.cancelado.situacao.atual.nao.permite", responsavel);
                    }

                    for (TransferObject aut : ades) {
                        String dadValor34 = (String) aut.getAttribute("DAD_VALOR_34");
                        String dadValor35 = (String) aut.getAttribute("DAD_VALOR_35");
                        tdaAdesao = TextHelper.isNull(tdaAdesao) && !TextHelper.isNull(dadValor34) ? dadValor34 : tdaAdesao;
                        tdaMesesContruicao = TextHelper.isNull(tdaMesesContruicao) && !TextHelper.isNull(dadValor35) ? dadValor35 : tdaMesesContruicao;

                        autorizacaoController.setDadoAutDesconto(aut.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO, tdaAdesao, responsavel);
                        autorizacaoController.setDadoAutDesconto(aut.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO, tdaMesesContruicao, responsavel);
                    }
                }
                if (solicitacaoCancelamento) {
                    update(cttBeneficio, CodedValues.TOC_SOLICITACAO_CANC_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
                    criaOcorrenciaContratoBeneficio(cbeCodigo, CodedValues.TOC_SOLICITACAO_CANC_CONTRATO_BENEFICIO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.servidor.solicitacao.cancelamento", responsavel), null, tmoCodigo, responsavel);
                } else {
                    update(cttBeneficio, CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
                }
            }

        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void update(ContratoBeneficio contratoBeneficio, String tocCodigo, String ocbObs, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            if (TextHelper.isNull(tocCodigo)) {
                tocCodigo = CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO;
            }

            ContratoBeneficio contratoBeneficioBanco = ContratoBeneficioHome.findByPrimaryKey(contratoBeneficio.getCbeCodigo());
            LogDelegate log = new LogDelegate(responsavel, Log.CONTRATO_BENEFICIO, Log.UPDATE, Log.LOG_INFORMACAO);

            String msgOcorrenciaContratoBeneficio = ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.alteracao", responsavel);

            /**
             * TODO Tipo ocorrência está fixo porque na exportação é validado apenas o tipo de ocorrência de alteração.
             * Será realizado em tarefa futura para que sejam tratados na exportação os tipos corretos informados
             * na aprovação, rejeição e cancelamento de benefício.
             */
            tocCodigo = tocCodigo.equals(CodedValues.TOC_APROVACAO_CONTRATO_BENEFICIO) ? tocCodigo : CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO;

            if (tocCodigo.equals(CodedValues.TOC_APROVACAO_CONTRATO_BENEFICIO)) {
                msgOcorrenciaContratoBeneficio = ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.aprovacao", responsavel);
            }

            if (contratoBeneficio.getStatusContratoBeneficio().getScbCodigo() != contratoBeneficioBanco.getStatusContratoBeneficio().getScbCodigo()) {
                // Se estamos alterando o status do contrato, vamos analisar se o novo status atende a regra.
                analisaRegraDeNegocioStatusContratoBeneficio(contratoBeneficio, tmoCodigo, responsavel);

                StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(contratoBeneficio.getStatusContratoBeneficio().getScbCodigo());

                msgOcorrenciaContratoBeneficio = ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.alteracao.status", responsavel, contratoBeneficioBanco.getStatusContratoBeneficio().getScbDescricao(), statusContrato.getScbDescricao());
                log.addChangedField(Columns.CBE_SCB_CODIGO, contratoBeneficio.getStatusContratoBeneficio().getScbCodigo());
            }

            if ((contratoBeneficioBanco.getCbeNumero() == null && contratoBeneficio.getCbeNumero() != null) || (contratoBeneficioBanco.getCbeNumero() != null && contratoBeneficio.getCbeNumero() == null) || ((contratoBeneficioBanco.getCbeNumero() != null && contratoBeneficio.getCbeNumero() != null) && (!contratoBeneficio.getCbeNumero().equals(contratoBeneficioBanco.getCbeNumero())))) {
                log.addChangedField(Columns.CBE_NUMERO, contratoBeneficio.getCbeNumero());
            }

            if ((contratoBeneficioBanco.getCbeDataInicioVigencia() == null && contratoBeneficio.getCbeDataInicioVigencia() != null) || (contratoBeneficioBanco.getCbeDataInicioVigencia() != null && contratoBeneficio.getCbeDataInicioVigencia() == null) || ((contratoBeneficioBanco.getCbeDataInicioVigencia() != null && contratoBeneficio.getCbeDataInicioVigencia() != null) && (contratoBeneficio.getCbeDataInicioVigencia().compareTo(contratoBeneficioBanco.getCbeDataInicioVigencia())) != 0)) {
                log.addChangedField(Columns.CBE_DATA_INICIO_VIGENCIA, contratoBeneficio.getCbeDataInicioVigencia());
            }

            if ((contratoBeneficioBanco.getCbeDataFimVigencia() == null && contratoBeneficio.getCbeDataFimVigencia() != null) || (contratoBeneficioBanco.getCbeDataFimVigencia() != null && contratoBeneficio.getCbeDataFimVigencia() == null) || ((contratoBeneficioBanco.getCbeDataFimVigencia() != null && contratoBeneficio.getCbeDataFimVigencia() != null) && (contratoBeneficio.getCbeDataFimVigencia().compareTo(contratoBeneficioBanco.getCbeDataFimVigencia())) != 0)) {
                log.addChangedField(Columns.CBE_DATA_FIM_VIGENCIA, contratoBeneficio.getCbeDataFimVigencia());
            }

            if ((contratoBeneficioBanco.getCbeDataCancelamento() == null && contratoBeneficio.getCbeDataCancelamento() != null) || (contratoBeneficioBanco.getCbeDataCancelamento() != null && contratoBeneficio.getCbeDataCancelamento() == null) || ((contratoBeneficioBanco.getCbeDataCancelamento() != null && contratoBeneficio.getCbeDataCancelamento() != null) && (contratoBeneficio.getCbeDataCancelamento().compareTo(contratoBeneficioBanco.getCbeDataCancelamento())) != 0)) {
                log.addChangedField(Columns.CBE_DATA_CANCELAMENTO, contratoBeneficio.getCbeDataCancelamento());
            }

            ContratoBeneficioHome.update(contratoBeneficio);
            criaOcorrenciaContratoBeneficio(contratoBeneficio.getCbeCodigo(), tocCodigo, msgOcorrenciaContratoBeneficio, null, tmoCodigo, responsavel);

            log.write();

        } catch (FindException | UpdateException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void analisaRegraDeNegocioStatusContratoBeneficio(ContratoBeneficio contratoBeneficio, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        // Analisando se o contrato que esta sofrendo alteração atende a regra de negocio.
        if (contratoBeneficio.getStatusContratoBeneficio().getScbCodigo() == CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO && TextHelper.isNull(tmoCodigo)) {
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel);
        } else if (contratoBeneficio.getStatusContratoBeneficio().getScbCodigo() == CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO && TextHelper.isNull(tmoCodigo)) {
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     * Realiza as analise de função e atualiza o contrato beneficio como os dados autorizacao da+ mensalidade
     * @param dados
     * @param responsavel
     * @throws ContratoBeneficioControllerException
     */
    @Override
    public void updateAnalisandoFuncaoEDadosAutorizacao(TransferObject dados, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            String cbeCodigo = dados.getAttribute(Columns.CBE_CODIGO).toString();
            String cbeNumero = dados.getAttribute(Columns.CBE_NUMERO).toString();

            String cbeDataInicioVigenciaString = dados.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA).toString();
            String cbeDataFimVigenciaString = dados.getAttribute(Columns.CBE_DATA_FIM_VIGENCIA).toString();
            String cbeDataCancelamentoString = dados.getAttribute(Columns.CBE_DATA_CANCELAMENTO).toString();

            String dad34 = dados.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO).toString();
            String dad35 = dados.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO).toString();
            String dad36 = dados.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO).toString();
            String dad37 = dados.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO).toString();

            dad34 = TextHelper.isNull(dad34) ? null : dad34;
            dad35 = TextHelper.isNull(dad35) ? null : dad35;
            dad36 = TextHelper.isNull(dad36) ? null : dad36;
            dad37 = TextHelper.isNull(dad37) ? null : dad37;

            // Validação de campos referentes a contribuição para o plano
            if (dad36 != null && dad36.equals(CodedValues.TPC_SIM) && dad37 == null) {
                // Valor da contribuição para o plano deve ser informado
                throw new ContratoBeneficioControllerException("mensagem.contrato.beneficio.valor.contribuicao.informar", responsavel);
            }

            Date cbeDataInicioVigencia = null;
            if (!TextHelper.isNull(cbeDataInicioVigenciaString)) {
                cbeDataInicioVigencia = DateHelper.parse(cbeDataInicioVigenciaString, LocaleHelper.getDatePattern());
            }

            Date cbeDataFimVigencia = null;
            if (!TextHelper.isNull(cbeDataFimVigenciaString)) {
                cbeDataFimVigencia = DateHelper.parse(cbeDataFimVigenciaString, LocaleHelper.getDatePattern());
            }

            Date cbeDataCancelamento = null;
            if (!TextHelper.isNull(cbeDataCancelamentoString)) {
                cbeDataCancelamento = DateHelper.parse(cbeDataCancelamentoString, LocaleHelper.getDatePattern());
            }

            // Validação de datas
            if (cbeDataInicioVigencia != null && cbeDataFimVigencia != null && cbeDataInicioVigencia.after(cbeDataFimVigencia)) {
                throw new ContratoBeneficioControllerException("mensagem.erro.alterar.contrato.beneficio.data.ini.maior.data.fim", responsavel);
            }
            if (cbeDataInicioVigencia != null && cbeDataCancelamento != null && cbeDataInicioVigencia.after(cbeDataCancelamento)) {
                throw new ContratoBeneficioControllerException("mensagem.erro.alterar.contrato.beneficio.data.ini.maior.data.cancelamento", responsavel);
            }
            if (cbeDataCancelamento != null && cbeDataFimVigencia != null && cbeDataCancelamento.after(cbeDataFimVigencia)) {
                throw new ContratoBeneficioControllerException("mensagem.erro.alterar.contrato.beneficio.data.cancelamanto.maior.data.fim", responsavel);
            }

            ContratoBeneficio contratoBeneficio = findByPrimaryKey(cbeCodigo, responsavel);
            TransferObject contratoBeneficioTo = listarContratosBeneficiosMensalidadeEdicaoTela(cbeCodigo, responsavel);
            String adeCodigo = contratoBeneficioTo.getAttribute(Columns.ADE_CODIGO).toString();

            boolean funEditarContratoBeneficio = responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO);
            boolean funEditarContratoBeneficioAvancado = responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO);

            if (funEditarContratoBeneficioAvancado) {
                if (cbeDataInicioVigencia == null) {
                    throw new ContratoBeneficioControllerException("mensagem.contrato.beneficio.data.inicio.vigencia.informar", responsavel);
                }

                contratoBeneficio.setCbeNumero(cbeNumero);
                contratoBeneficio.setCbeDataInicioVigencia(cbeDataInicioVigencia);
                contratoBeneficio.setCbeDataFimVigencia(cbeDataFimVigencia);
                contratoBeneficio.setCbeDataCancelamento(cbeDataCancelamento);

                update(contratoBeneficio, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, null, null, responsavel);
            }

            if (funEditarContratoBeneficio) {
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO, dad34, responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO, dad35, responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO, dad36, responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO, dad37, responsavel);
            }
        } catch (AutorizacaoControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new ContratoBeneficioControllerException(e);
        } catch (ParseException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<StatusContratoBeneficio> listAllStatusContratoBeneficio(AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            return StatusContratoBeneficioHome.listAll();
        } catch (FindException ex) {
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listLancamentosContratosBeneficiosByDataAndCbeCodigo(String cbeCodigo, Date prdDataDesconto, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListaLancamentosContratosBeneficiosQuery query = new ListaLancamentosContratosBeneficiosQuery();
            query.cbeCodigo = cbeCodigo;
            query.prdDataDesconto = prdDataDesconto;
            return query.executarDTO();
        } catch (HQueryException e) {
            throw new ContratoBeneficioControllerException(e);
        }
    }

    @Override
    public List<TransferObject> listOcorrenciaContratosBeneficiosByCbeCodigo(TransferObject criterio, Boolean justInfo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
        	ListaOcorrenciaContratosBeneficiosQuery query = new ListaOcorrenciaContratosBeneficiosQuery();

            if (criterio != null) {
                query.cbeCodigo = (String) criterio.getAttribute(Columns.CBE_CODIGO);
            }

            return query.executarDTO();

        } catch (HQueryException e) {
            throw new ContratoBeneficioControllerException(e);
        }
    }

    @Override
    public List<TransferObject> listarContratosBeneficioPorRegistroServidorQuery(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            if (criterio == null) {
                return null;
            }

            String rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
            List<String> scbCodigos = (List<String>) criterio.getAttribute(Columns.SCB_CODIGO);
            String nseCodigo = (String) criterio.getAttribute(Columns.NSE_CODIGO);

            ListarContratosBeneficioPorRegistroServidorQuery listarContratosBeneficioPorRegistroServidorQuery = new ListarContratosBeneficioPorRegistroServidorQuery();
            listarContratosBeneficioPorRegistroServidorQuery.scbCodigos = scbCodigos;
            listarContratosBeneficioPorRegistroServidorQuery.rseCodigo = rseCodigo;
            listarContratosBeneficioPorRegistroServidorQuery.nseCodigo = nseCodigo;

            List<String> tibCodigo = new ArrayList<>();
            if (criterio.getAttribute(Columns.TIB_CODIGO) == null) {
                tibCodigo.add(CodedValues.TIB_TITULAR);
            } else {
                 tibCodigo.addAll((List<String>) criterio.getAttribute(Columns.TIB_CODIGO));
            }

            listarContratosBeneficioPorRegistroServidorQuery.tibCodigo = tibCodigo;

            if (criterio.getAttribute(Columns.BFC_CODIGO) != null) {
                listarContratosBeneficioPorRegistroServidorQuery.bfcCodigo = criterio.getAttribute(Columns.BFC_CODIGO).toString();
            }

            List<String> tntCodigos = new ArrayList<>();
            if (criterio.getAttribute(Columns.TNT_CODIGO) == null) {
                tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            } else {
                tntCodigos.addAll((List<String>) criterio.getAttribute(Columns.TNT_CODIGO));
            }

            listarContratosBeneficioPorRegistroServidorQuery.tntCodigo = tntCodigos;

            if (criterio.getAttribute("reativar") != null && criterio.getAttribute("reativar").equals("true")) {
                listarContratosBeneficioPorRegistroServidorQuery.reativar = true;
            }

            if (criterio.getAttribute(Columns.CSA_CODIGO) != null) {
                listarContratosBeneficioPorRegistroServidorQuery.csaCodigo = criterio.getAttribute(Columns.CSA_CODIGO).toString();
            }

            if (criterio.getAttribute("reservaSemRegrasModulo") != null && criterio.getAttribute("reservaSemRegrasModulo").equals("true")) {
                listarContratosBeneficioPorRegistroServidorQuery.reservaSemRegrasModulo = true;
            }

            if (criterio.getAttribute(Columns.BEN_CODIGO) != null) {
                listarContratosBeneficioPorRegistroServidorQuery.benCodigo = criterio.getAttribute(Columns.BEN_CODIGO).toString();
            }

            return listarContratosBeneficioPorRegistroServidorQuery.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarContratosBeneficiosPendentesInclusao(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            if (criterio == null) {
                return null;
            }

            String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);

            StatusContratoBeneficio statusContratoInclusao = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo());

            String statusContrato = statusContratoInclusao.getScbDescricao();

            ListaContratosBeneficioPendentesInclusaoQuery listarContratosBeneficiosPendentesInclusao = new ListaContratosBeneficioPendentesInclusaoQuery();
            listarContratosBeneficiosPendentesInclusao.csaCodigo = csaCodigo;
            listarContratosBeneficiosPendentesInclusao.statusContrato = statusContrato;

            if (count != -1) {
                listarContratosBeneficiosPendentesInclusao.maxResults = count;
                listarContratosBeneficiosPendentesInclusao.firstResult = offset;
            }

            return listarContratosBeneficiosPendentesInclusao.executarDTO();
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public long countContratosBeneficiosPendentesInclusao(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);

            ListaContratosBeneficioPendentesInclusaoQuery listarContratosBeneficiosPendentesInclusao = new ListaContratosBeneficioPendentesInclusaoQuery();
            listarContratosBeneficiosPendentesInclusao.csaCodigo = csaCodigo;
            listarContratosBeneficiosPendentesInclusao.count = true;

            return listarContratosBeneficiosPendentesInclusao.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarContratosBeneficiosPendentesExclusao(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            if (criterio == null) {
                return null;
            }

            String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);

            StatusContratoBeneficio statusContratoExclusao = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo());

            String statusContrato = statusContratoExclusao.getScbDescricao();

            ListaContratosBeneficioPendentesExclusaoQuery listarContratosBeneficiosPendentesExclusao = new ListaContratosBeneficioPendentesExclusaoQuery();
            listarContratosBeneficiosPendentesExclusao.csaCodigo = csaCodigo;
            listarContratosBeneficiosPendentesExclusao.statusContrato = statusContrato;

            if (count != -1) {
                listarContratosBeneficiosPendentesExclusao.maxResults = count;
                listarContratosBeneficiosPendentesExclusao.firstResult = offset;
            }

            return listarContratosBeneficiosPendentesExclusao.executarDTO();
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public long countContratosBeneficiosPendentesExclusao(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);

            ListaContratosBeneficioPendentesExclusaoQuery listarContratosBeneficiosPendentesExclusao = new ListaContratosBeneficioPendentesExclusaoQuery();
            listarContratosBeneficiosPendentesExclusao.csaCodigo = csaCodigo;
            listarContratosBeneficiosPendentesExclusao.count = true;

            return listarContratosBeneficiosPendentesExclusao.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }


    @Override
    public List<String> criarReservaDeContratosBeneficios(String rseCodigo, Map<String, List<String>> dadosSimulacao, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        return criarReservaDeContratosBeneficios(rseCodigo, dadosSimulacao, null, null, false, responsavel);
    }

    @Override
    public List<String> criarReservaDeContratosBeneficios(String rseCodigo, Map<String, List<String>> dadosSimulacao, List<String> beneficiariosComContratoSaude, List<String> beneficiariosComContratoOdontologico, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        return criarReservaDeContratosBeneficios(rseCodigo, dadosSimulacao, beneficiariosComContratoSaude, beneficiariosComContratoOdontologico, false, responsavel);
    }

    @Override
    public List<String> criarReservaDeContratosBeneficiosMigracao(String rseCodigo, Map<String, List<String>> dadosSimulacao, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        return criarReservaDeContratosBeneficios(rseCodigo, dadosSimulacao, null, null, true, responsavel);
    }

    /**
     * Realiza a criação do contrato de beneficio junto com as ade de mensalidad e subcidio.
     * @param rseCodigo
     * @param dadosSimulacao
     * @param migracaoBeneficio
     * @param responsavel
     * @throws ContratoBeneficioControllerException
     */
    private List<String> criarReservaDeContratosBeneficios(String rseCodigo, Map<String, List<String>> dadosSimulacao,
            List<String> beneficiariosComContratoSaude, List<String> beneficiariosComContratoOdonto, boolean migracaoBeneficio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            // Criando os delegate necessarios para o caso de uso
            ListaConvenioRegistroServidorQuery listaConvenioRegistroServidorQuery = new ListaConvenioRegistroServidorQuery();

            HashMap<String, Boolean> teveTitularSelecionado = new HashMap<>();

            List<String> cbeCodigoCriados = new ArrayList<>();

            Set<String> nseCodigosSimulacao = new HashSet<>();

            // Analisando se realmente dodos beneficiarios informados são validados para receber plano e subcidio
            for (String key : dadosSimulacao.keySet()) {
                Beneficio beneficio = BeneficioHome.findByPrimaryKey(key);
                nseCodigosSimulacao.add(beneficio.getNaturezaServico().getNseCodigo());

                // Buscando o serviço que o ben pode ter.
                List<BeneficioServico> servicosTitular = relacionamentoBeneficioServicoController.findByBenCodigoTibCodigo(key, CodedValues.TIB_TITULAR);
                if (servicosTitular == null || servicosTitular.size() != 1) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.servico.entrados", responsavel);
                }

                // Buscando, filtrando e ordenando os beneficiario que são validados para receber plano e subsidio
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                criterio.setAttribute(Columns.SVC_CODIGO, servicosTitular.get(0).getServico().getSvcCodigo());
                List<TransferObject> beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

                Set<String> bfcCodigoValidos = new HashSet<>();
                for (TransferObject beneficiario : beneficiariosGrupoFamiliar) {
                    String bfcCodigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
                    bfcCodigoValidos.add(bfcCodigo);
                }

                List<String> beneficiarios = dadosSimulacao.get(key);
                Iterator<String> it = beneficiarios.iterator();
                while (it.hasNext()) {
                    String bfcCodigo = it.next();
                    if (!bfcCodigoValidos.contains(bfcCodigo)) {
                        it.remove();
                    }
                }

                if (beneficiariosComContratoSaude != null && !beneficiariosComContratoSaude.isEmpty()) {
                    it = beneficiariosComContratoSaude.iterator();
                    while (it.hasNext()) {
                        String bfcCodigo = it.next();
                        if (!bfcCodigoValidos.contains(bfcCodigo)) {
                            it.remove();
                        }
                    }
                }

                if (beneficiariosComContratoOdonto != null && !beneficiariosComContratoOdonto.isEmpty()) {
                    it = beneficiariosComContratoOdonto.iterator();
                    while (it.hasNext()) {
                        String bfcCodigo = it.next();
                        if (!bfcCodigoValidos.contains(bfcCodigo)) {
                            it.remove();
                        }
                    }
                }
            }

            // Recebemos os dados vamos calcular novamene os dados para garantir que esta tudo certo.
            List<TransferObject> resultados = calcularSubsidioBeneficioController.simularCalculoSubsidio(dadosSimulacao, rseCodigo, migracaoBeneficio, responsavel);

            if (resultados == null || resultados.size() == 0) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.beneficiario.valido", responsavel);
            }

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.TIB_CODIGO, CodedValues.TIB_TITULAR);
            List<TransferObject> listaBeneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);

            if (listaBeneficiarios == null || listaBeneficiarios.size() == 0) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.beneficiario.valido", responsavel);
            }

            // Lista de contratos ativos, usado para o fluxo de migração
            List<TransferObject> contratosAtivos = null;
            Set<String> nseCodigosAnalisadosMigracao = new HashSet<>();

            Map<String, BigDecimal> totalPlanoPassado = new HashMap<>();
            Map<String, BigDecimal> totalPlanoNovo = new HashMap<>();
            Map<String, String> svcTitular = new HashMap<>();
            Map<String, String> csaTitular = new HashMap<>();
            Map<String, Short> indideMargemTitular = new HashMap<>();
            List<String> adesPlanoAtual = new ArrayList<>();

            // Loop da criação dos contrato, ade mensalidade, ade sub, relacionamento
            for (TransferObject resultado : resultados) {
                boolean contratoVirtual = (boolean) resultado.getAttribute("contratoVirtual");
                BigDecimal valorMensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                BigDecimal valorSubcidio = (BigDecimal) resultado.getAttribute("VALOR_SUBSIDIO");
                String svcCodigo = (String) resultado.getAttribute("SVC_CODIGO");
                String bfcCodigo = (String) resultado.getAttribute(Columns.BFC_CODIGO);
                String nseCodigo = (String) resultado.getAttribute(Columns.NSE_CODIGO);

                String benCodigo = (String) resultado.getAttribute(Columns.BEN_CODIGO);
                Beneficio beneficio = BeneficioHome.findByPrimaryKey(benCodigo);
                Beneficiario beneficiario = BeneficiarioHome.findByPrimaryKey(bfcCodigo);

                BigDecimal somatorioPlanoNovo = totalPlanoNovo.containsKey(nseCodigo) ? totalPlanoNovo.get(nseCodigo) : new BigDecimal("0.00");
                somatorioPlanoNovo = somatorioPlanoNovo.add(valorMensalidade.subtract(valorSubcidio));
                totalPlanoNovo.put(nseCodigo, somatorioPlanoNovo);

                if (!contratoVirtual) {
                    if (TipoBeneficiarioEnum.TITULAR.equals(beneficiario.getTipoBeneficiario().getTibCodigo()) && nseCodigosSimulacao.contains(nseCodigo)) {
                        teveTitularSelecionado.put(benCodigo, true);
                    }
                    continue;
                }

                if ( (beneficio.getNaturezaServico().getNseCodigo().equals(CodedValues.NSE_PLANO_DE_SAUDE) && (beneficiariosComContratoSaude == null || !beneficiariosComContratoSaude.contains(bfcCodigo))) ||
                        (beneficio.getNaturezaServico().getNseCodigo().equals(CodedValues.NSE_PLANO_ODONTOLOGICO) && (beneficiariosComContratoOdonto == null || !beneficiariosComContratoOdonto.contains(bfcCodigo))) ) {

                    // Controle para analisar se teve algum beneficiario no loop.
                    String tibCodigo = (String) resultado.getAttribute(Columns.TIB_CODIGO);

                    RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);

                    // Analisando se conseguimos buscar os dados de forma correta e evitando null pointer
                    if (beneficiario == null || beneficiario == null || registroServidor == null) {
                        throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel);
                    }

                    // Analisando os parametros de sistema para utilizar na reserva de margem
                    ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                    // 1 - Cria o contrato beneficio
                    ContratoBeneficio contratoBeneficio = create(beneficiario, beneficio, "", new Date(), PeriodoHelper.getInstance().getPeriodoBeneficioAtual(registroServidor.getOrgao().getOrgCodigo(), responsavel), "", "", valorMensalidade, valorSubcidio, new StatusContratoBeneficio(StatusContratoBeneficioEnum.SOLICITADO.getCodigo()), responsavel);

                    // 2 - Criar a autorização desconto do tipo mensalidade
                    // Analisar no futuro se usamos o caro de usuo InserirSolicitacaoControllerBean
                    // Não estamos usando ela nesse momento porque ela tem o comportamento de realização de simulação de taxas
                    List<TipoLancamento> tiposLancamento = TipoLancamentoHome.findByTntCodigoAndNseCodigo(CodedValues.TNT_BENEFICIO_MENSALIDADE, beneficio.getNaturezaServico().getNseCodigo());
                    if (tiposLancamento == null || tiposLancamento.isEmpty() || tiposLancamento.size() > 1) {
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.tipo.lancamento.entrados", responsavel);
                    }
                    TipoLancamento tipoLancamentoMensalidade = tiposLancamento.get(0);
                    listaConvenioRegistroServidorQuery.rseCodigo = rseCodigo;
                    listaConvenioRegistroServidorQuery.csaCodigo = beneficio.getConsignataria().getCsaCodigo();
                    listaConvenioRegistroServidorQuery.svcCodigo = svcCodigo;
                    List<TransferObject> convenios = listaConvenioRegistroServidorQuery.executarDTO();
                    if (convenios == null || convenios.isEmpty()) {
                        throw new ContratoBeneficioControllerException("mensagem.erro.convenio.multiplo", responsavel);
                    } else if (convenios.size() > 1) {
                        throw new ContratoBeneficioControllerException("mensagem.nenhumConvenioEncontrado ", responsavel);
                    }
                    String cnvCodigo = (String) convenios.get(0).getAttribute(Columns.CNV_CODIGO);

                    ReservarMargemParametros reservarMargemParametros = new ReservarMargemParametros();
                    reservarMargemParametros.setRseCodigo(rseCodigo);
                    reservarMargemParametros.setAdeVlr(valorMensalidade.subtract(valorSubcidio));
                    reservarMargemParametros.setAdeCarencia(0);
                    reservarMargemParametros.setCnvCodigo(cnvCodigo);
                    reservarMargemParametros.setSadCodigo(CodedValues.SAD_SOLICITADO);
                    reservarMargemParametros.setComSerSenha(false);
                    reservarMargemParametros.setAdeTipoVlr(paramSvcCse.getTpsTipoVlr());
                    reservarMargemParametros.setAdeIntFolha(paramSvcCse.getTpsIntegraFolha());
                    reservarMargemParametros.setAdeIncMargem(paramSvcCse.getTpsIncideMargem());
                    reservarMargemParametros.setValidar(Boolean.FALSE);
                    reservarMargemParametros.setPermitirValidacaoTaxa(Boolean.FALSE);
                    reservarMargemParametros.setSerAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCnvAtivo(Boolean.TRUE);
                    reservarMargemParametros.setSerCnvAtivo(Boolean.TRUE);
                    reservarMargemParametros.setSvcAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCsaAtivo(Boolean.TRUE);
                    reservarMargemParametros.setOrgAtivo(Boolean.TRUE);
                    reservarMargemParametros.setEstAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCseAtivo(Boolean.TRUE);
                    reservarMargemParametros.setAdeIdentificador("");
                    reservarMargemParametros.setCbeCodigo(contratoBeneficio.getCbeCodigo());
                    reservarMargemParametros.setTlaCodigo(tipoLancamentoMensalidade.getTlaCodigo());
                    reservarMargemParametros.setAdeAnoMesIni(PeriodoHelper.getInstance().getPeriodoBeneficioAtual(registroServidor.getOrgao().getOrgCodigo(), responsavel));

                    if (migracaoBeneficio) {
                        reservarMargemParametros.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                        reservarMargemParametros.setAcao("MIGRAR_BENEFICIO");
                    } else {
                        reservarMargemParametros.setAdeIncMargem(CodedValues.INCIDE_MARGEM_SIM);
                        reservarMargemParametros.setAcao("RESERVAR");
                    }

                    reservarMargemParametros.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);

                    String adeCodigo = reservarMargemController.reservarMargem(reservarMargemParametros, responsavel);
                    LOG.info(adeCodigo);
                    AutDesconto autDescontoMensalidade = AutDescontoHome.findByPrimaryKey(adeCodigo);

                    if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        teveTitularSelecionado.put(benCodigo, true);
                        indideMargemTitular.put(nseCodigo, paramSvcCse.getTpsIncideMargem());
                    }

                    // 3 - Criar a autorização desconto do tipo subcidio
                    List<RelacionamentoServico> relacionamentosServido = RelacionamentoServicoHome.findBySvcCodigoOrigem(svcCodigo, CodedValues.TNT_BENEFICIO_SUBSIDIO);
                    if (relacionamentosServido == null || relacionamentosServido.isEmpty() || relacionamentosServido.size() > 1) {
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.servico.entrados", responsavel);
                    }
                    String tntCodigo = relacionamentosServido.get(0).getTipoNatureza().getTntCodigo();

                    tiposLancamento = TipoLancamentoHome.findByTntCodigoAndNseCodigo(CodedValues.TNT_BENEFICIO_SUBSIDIO, beneficio.getNaturezaServico().getNseCodigo());
                    if (tiposLancamento == null || tiposLancamento.isEmpty() || tiposLancamento.size() > 1) {
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.tipo.lancamento.entrados", responsavel);
                    }
                    TipoLancamento tipoLancamentoSubcidio = tiposLancamento.get(0);

                    listaConvenioRegistroServidorQuery.rseCodigo = rseCodigo;
                    listaConvenioRegistroServidorQuery.csaCodigo = beneficio.getConsignataria().getCsaCodigo();
                    listaConvenioRegistroServidorQuery.svcCodigo = relacionamentosServido.get(0).getServicoBySvcCodigoDestino().getSvcCodigo();
                    convenios = listaConvenioRegistroServidorQuery.executarDTO();
                    if (convenios == null || convenios.isEmpty()) {
                        throw new ContratoBeneficioControllerException("mensagem.erro.convenio.multiplo", responsavel);
                    } else if (convenios.size() > 1) {
                        throw new ContratoBeneficioControllerException("mensagem.nenhumConvenioEncontrado ", responsavel);
                    }
                    cnvCodigo = (String) convenios.get(0).getAttribute(Columns.CNV_CODIGO);

                    paramSvcCse = parametroController.getParamSvcCseTO(relacionamentosServido.get(0).getServicoBySvcCodigoDestino().getSvcCodigo(), responsavel);

                    reservarMargemParametros = new ReservarMargemParametros();
                    reservarMargemParametros.setRseCodigo(rseCodigo);
                    reservarMargemParametros.setAdeVlr(valorSubcidio);
                    reservarMargemParametros.setAdeCarencia(0);
                    reservarMargemParametros.setCnvCodigo(cnvCodigo);
                    reservarMargemParametros.setSadCodigo(CodedValues.SAD_SOLICITADO);
                    reservarMargemParametros.setComSerSenha(false);
                    reservarMargemParametros.setAdeTipoVlr(paramSvcCse.getTpsTipoVlr());
                    reservarMargemParametros.setAdeIntFolha(paramSvcCse.getTpsIntegraFolha());
                    reservarMargemParametros.setAdeIncMargem(paramSvcCse.getTpsIncideMargem());
                    reservarMargemParametros.setValidar(Boolean.FALSE);
                    reservarMargemParametros.setPermitirValidacaoTaxa(Boolean.FALSE);
                    reservarMargemParametros.setSerAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCnvAtivo(Boolean.TRUE);
                    reservarMargemParametros.setSerCnvAtivo(Boolean.TRUE);
                    reservarMargemParametros.setSvcAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCsaAtivo(Boolean.TRUE);
                    reservarMargemParametros.setOrgAtivo(Boolean.TRUE);
                    reservarMargemParametros.setEstAtivo(Boolean.TRUE);
                    reservarMargemParametros.setCseAtivo(Boolean.TRUE);
                    reservarMargemParametros.setAdeIdentificador("");
                    reservarMargemParametros.setCbeCodigo(contratoBeneficio.getCbeCodigo());
                    reservarMargemParametros.setTlaCodigo(tipoLancamentoSubcidio.getTlaCodigo());
                    reservarMargemParametros.setAdeAnoMesIni(PeriodoHelper.getInstance().getPeriodoBeneficioAtual(registroServidor.getOrgao().getOrgCodigo(), responsavel));

                    if (migracaoBeneficio) {
                        reservarMargemParametros.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                        reservarMargemParametros.setAcao("MIGRAR_BENEFICIO");
                    } else {
                        reservarMargemParametros.setAcao("RESERVAR");
                    }

                    reservarMargemParametros.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);

                    adeCodigo = reservarMargemController.reservarMargem(reservarMargemParametros, responsavel);
                    LOG.info(adeCodigo);
                    AutDesconto autDescontoSubcidio = AutDescontoHome.findByPrimaryKey(adeCodigo);

                    RelacionamentoAutorizacaoHome.create(autDescontoMensalidade.getAdeCodigo(), autDescontoSubcidio.getAdeCodigo(), tntCodigo, responsavel.getUsuCodigo());

                    cbeCodigoCriados.add(contratoBeneficio.getCbeCodigo());

                    if (migracaoBeneficio && TipoBeneficiarioEnum.TITULAR.equals(tibCodigo) && !nseCodigosAnalisadosMigracao.contains(beneficio.getNaturezaServico().getNseCodigo())) {
                        svcTitular.put(nseCodigo, svcCodigo);
                        csaTitular.put(nseCodigo, beneficio.getConsignataria().getCsaCodigo());


                        criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);

                        List<String> scbCodigos = new ArrayList<>();
                        scbCodigos.add(CodedValues.SCB_CODIGO_ATIVO);

                        List<String> tntCodigos = new ArrayList<>();
                        tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);

                        List<String> tibsCodigo = Arrays.asList(CodedValues.TIB_TITULAR, CodedValues.TIB_AGREGADO, CodedValues.TIB_DEPENDENTE);

                        criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
                        criterio.setAttribute(Columns.NSE_CODIGO, beneficio.getNaturezaServico().getNseCodigo());
                        criterio.setAttribute(Columns.TIB_CODIGO, tibsCodigo);
                        criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);

                        contratosAtivos = listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

                        // Analisando se a carencia do contratos de mensalidade está validado para migração entre mesma CSA
                        for (TransferObject contratoAtivo : contratosAtivos) {
                            try {
                                validaCarenciaMigracaoContratoBeneficio(contratoAtivo, beneficio.getConsignataria().getCsaCodigo(), benCodigo, responsavel);
                            } catch (ContratoBeneficioControllerException e) {
                                LOG.error(e.getMessage(), e);
                                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                                throw new ContratoBeneficioControllerException(e);
                            }

                            String tibCodigoContrato = contratoAtivo.getAttribute(Columns.TIB_CODIGO).toString();
                            String adeCodigoAtivo = contratoAtivo.getAttribute(Columns.ADE_CODIGO).toString();
                            String valorTotal = contratoAtivo.getAttribute("valorTotal").toString();
                            String valorSubsidio = contratoAtivo.getAttribute("valorSubsidio").toString();

                            BigDecimal valorTotalBigDecimal = new BigDecimal(valorTotal);
                            BigDecimal valorSubsidioBigDecimal = new BigDecimal(valorSubsidio);

                            if(TipoBeneficiarioEnum.TITULAR.equals(tibCodigoContrato)) {
                                RelacionamentoAutorizacaoHome.create(adeCodigoAtivo, autDescontoMensalidade.getAdeCodigo(), CodedValues.TNT_CONTROLE_MIGRACAO_BENEFICIO, responsavel.getUsuCodigo());
                            }

                            BigDecimal somatorioPlanoPassado = totalPlanoPassado.containsKey(nseCodigo) ? totalPlanoPassado.get(nseCodigo) : new BigDecimal("0.00");
                            somatorioPlanoPassado = somatorioPlanoPassado.add(valorTotalBigDecimal.subtract(valorSubsidioBigDecimal));
                            totalPlanoPassado.put(nseCodigo, somatorioPlanoPassado);

                            adesPlanoAtual.add(adeCodigoAtivo);
                        }

                        // Analisando se não existe algum contrato em solicitado ou agurdando inclusão para algum do grupo familiar
                        nseCodigosAnalisadosMigracao.add(beneficio.getNaturezaServico().getNseCodigo());

                        scbCodigos = new ArrayList<>();
                        scbCodigos.add(StatusContratoBeneficioEnum.SOLICITADO.getCodigo());
                        scbCodigos.add(StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo());

                        criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                        criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
                        criterio.setAttribute(Columns.NSE_CODIGO, beneficio.getNaturezaServico().getNseCodigo());
                        criterio.setAttribute(Columns.TIB_CODIGO, tibsCodigo);

                        List<TransferObject> listaContrato = listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
                        Iterator<TransferObject> iteratorListaContrato = listaContrato.iterator();
                        while (iteratorListaContrato.hasNext()) {
                            TransferObject contrato = iteratorListaContrato.next();
                            String cbeCodigo = contrato.getAttribute(Columns.CBE_CODIGO).toString();

                            if (cbeCodigoCriados.contains(cbeCodigo)) {
                                iteratorListaContrato.remove();
                            }
                        }

                        if (!listaContrato.isEmpty()) {
                            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                            throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.beneficiario.possui.contrato.simulado", responsavel);
                        }

                        listaContrato.clear();
                        listaContrato = null;
                    }
                } else if (TipoBeneficiarioEnum.TITULAR.equals(beneficiario.getTipoBeneficiario().getTibCodigo())) {
                    teveTitularSelecionado.put(benCodigo, true);
                }
            }

            if (migracaoBeneficio) {
                for (String key : nseCodigosAnalisadosMigracao) {
                    BigDecimal totalNovo = totalPlanoNovo.get(key);
                    BigDecimal totalPassado = totalPlanoPassado.get(key);
                    String csaCodigo = csaTitular.get(key);
                    String svcCodigo = svcTitular.get(key);
                    Short incideMargem = indideMargemTitular.get(key);

                    BigDecimal diff = totalNovo.subtract(totalPassado);

                    if (diff.signum() == 1) {
                        atualizaMargem(rseCodigo, incideMargem, diff, true, false, true, null, csaCodigo, svcCodigo, adesPlanoAtual, responsavel);
                    }

                }
            }

            // Se por algum motivo o beneficiario remover o titular do array da tela e etc aqui vamos barrar isso.
            if (teveTitularSelecionado.size() != dadosSimulacao.size()) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.titular.nao.selecionado", responsavel);
            }

            if (cbeCodigoCriados.size() == 0) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.nenhum.contrato.criado", responsavel);
            }

            return cbeCodigoCriados;
        } catch (BeneficioControllerException | AutorizacaoControllerException | FindException | CreateException | HQueryException | ParametroControllerException | PeriodoException | RelacionamentoBeneficioServicoControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new ContratoBeneficioControllerException(e);
        }

    }

    private List<TransferObject> findByContratoBeneficioAndInTntCodigoAndInSadCodigo(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListaAdesPorCbePorTntPorSadQuery query = new ListaAdesPorCbePorTntPorSadQuery();

            if (criterio != null) {
                query.cbeCodigo = (String) criterio.getAttribute(Columns.CBE_CODIGO);
                query.tntCodigo = (List<String>) criterio.getAttribute(Columns.TNT_CODIGO);
                query.sadCodigo = (List<String>) criterio.getAttribute(Columns.SAD_CODIGO);
            }

            return query.executarDTO();

        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ContratoBeneficioControllerException(e);
        }
    }

    @Override
    public void validaCarenciaMigracaoContratoBeneficio(TransferObject contratoAtivo, String csaCodigo, String beCodigoNovoPlano, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            String tpaPrazoMinimoMigracaoBeneficio = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PRAZO_MINIMO_MIGRACAO_BENEFICIO, responsavel);

            Beneficio beneficioNovoPlano = beneficioController.findBeneficioByCodigo(beCodigoNovoPlano, responsavel);

            // Analisando se existe carencia para migração de plano entre a mesma CSA para todos os contratos ativos
            String csaCodigoPlanoVigente = contratoAtivo.getAttribute(Columns.CSA_CODIGO).toString();
            Date cbeDataInicioVigencia = (Date) contratoAtivo.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA);
            Short benCategoriaVigencia = contratoAtivo.getAttribute(Columns.BEN_CATEGORIA) == null ? 0 : (Short) contratoAtivo.getAttribute(Columns.BEN_CATEGORIA);

            if (!TextHelper.isNull(tpaPrazoMinimoMigracaoBeneficio) && csaCodigoPlanoVigente.equals(csaCodigo)) {
                LocalDate agora = LocalDate.now();
                LocalDate dataInicioContrato = cbeDataInicioVigencia.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                long mesContrato = ChronoUnit.MONTHS.between(dataInicioContrato, agora);
                long prazoMinimo = Long.parseLong(tpaPrazoMinimoMigracaoBeneficio);

                // Verifica se o prazo de carência para troca de benefícios está sendo respeitado.
                if (mesContrato <= prazoMinimo) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.existe.carencia.para.migracao.plano.superiodo", responsavel);
                }
                // Verifica se a categoria do novo benefício é superior à categoria do benefício vigente. Caso seja inferior, exibe erro para o usuário.
                Short benCategoriaNovoPlano = (beneficioNovoPlano.getBenCategoria() == null ? 0 : beneficioNovoPlano.getBenCategoria());
                if (benCategoriaNovoPlano < benCategoriaVigencia) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.simulacao.beneficio.migracao.beneficio.categoria.inferior", responsavel);
                }
            }
        } catch (BeneficioControllerException | ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new ContratoBeneficioControllerException(e);
        }
    }

    @Override
    public void exclusaoManual(List<TransferObject> beneficiarios, Date cbeDataInicioVigencia, Date cbeDataFimVigencia, boolean cancelarInclusao, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.CANCELADO.getCodigo());

            ArrayList<String> sadCodigos = new ArrayList<>();
            if (!cancelarInclusao) {
                sadCodigos.addAll(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO);
            } else {
                sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);
            }

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            if(cancelarInclusao) {
                tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);
            }

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);

                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);
                cttBeneficio.setCbeDataInicioVigencia(cbeDataInicioVigencia);
                cttBeneficio.setCbeDataFimVigencia(cbeDataFimVigencia);

                if (!cancelarInclusao) {
                    // Somente contrato benefício ativo pode ser cancelado
                    if (!StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                        continue;
                    }
                } else if (!StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                if (ades == null || ades.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nao.pode.cancelado.situacao.atual.nao.permite", responsavel);
                }

                for (TransferObject aut : ades) {
                    String adeCodigo = (String) aut.getAttribute(Columns.ADE_CODIGO);
                    if (!cancelarInclusao) {
                        CustomTransferObject motivoOperacao = new CustomTransferObject();
                        String tmoCodigo = recuperaMotivoOperacaoExclusao(cbeCodigo,responsavel);
                        motivoOperacao.setAttribute(Columns.TMO_CODIGO, tmoCodigo);

                        liquidarConsignacaoController.liquidar(adeCodigo, TextHelper.isNull(tmoCodigo) ? null : motivoOperacao, null, responsavel);
                    } else {
                        cancelarConsignacaoController.cancelar(adeCodigo, responsavel);
                    }
                }

                update(cttBeneficio, null, null, null, responsavel);

            }

        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void inclusaoManual(List<TransferObject> beneficiarios, Date cbeDataInicioVigencia, String cbeNumero,  AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.ATIVO.getCodigo());

            ArrayList<String> sadCodigos = new ArrayList<>();
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);

                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);
                cttBeneficio.setCbeDataInicioVigencia(cbeDataInicioVigencia);
                cttBeneficio.setCbeNumero(cbeNumero);

                // Somente contrato benefício ativo pode ser cancelado
                if (!StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                if (ades == null || ades.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.contrato.beneficio.nao.pode.incluido.situacao.atual.nao.permite", responsavel);
                }

                for (TransferObject aut : ades) {
                    String adeCodigo = (String) aut.getAttribute(Columns.ADE_CODIGO);
                    deferirConsignacaoController.deferir(adeCodigo, null, responsavel);
                }

                update(cttBeneficio, null, null, null, responsavel);

            }

        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void desfazerCancelamento(List<TransferObject> beneficiarios, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {

            StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.ATIVO.getCodigo());

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmoTO = new CustomTransferObject();
                tmoTO.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmoTO.setAttribute(Columns.OCA_OBS, ocbObs);
            }

            for (TransferObject bfc : beneficiarios) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);
                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Somente contrato benefício cancelamento solicitado pelo beneficiário pode ser desfeito
                if (!StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);

                update(cttBeneficio, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
            }
        } catch (ContratoBeneficioControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    @Override
    public void desfazerCancelamentoAutomatico(AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {

            StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.ATIVO.getCodigo());

            ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery query = new ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery();
            List<TransferObject> cbeCodigos = query.executarDTO();

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

            for (TransferObject bfc : cbeCodigos) {
                String cbeCodigo = (String) bfc.getAttribute(Columns.CBE_CODIGO);
                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Somente contrato benefício cancelamento solicitado pelo beneficiário pode ser desfeito
                if (!StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    continue;
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);

                update(cttBeneficio, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.cancelado.desfeito.automatico", responsavel), null, responsavel);
            }
        } catch (ContratoBeneficioControllerException | FindException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    @Override
    public List<String> reativarContratoBeneficio(List<String> cbeCodigos,  AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            StatusContratoBeneficio statusContrato = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.ATIVO.getCodigo());

            ArrayList<String> sadCodigos = new ArrayList<>();
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

            ArrayList<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);

            for (String cbeCodigo : cbeCodigos) {

                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                // Somente contrato benefício cancelado pode ser reativado
                if (!StatusContratoBeneficioEnum.CANCELADO.getCodigo().equals(cttBeneficio.getStatusContratoBeneficio().getScbCodigo())) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.reativar.beneficio.status.nao.permitido", responsavel);
                }

                cttBeneficio.setStatusContratoBeneficio(statusContrato);
                cttBeneficio.setCbeDataFimVigencia(null);
                cttBeneficio.setCbeDataCancelamento(null);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);
                criterio.setAttribute(Columns.TNT_CODIGO, tntCodigos);
                criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);

                List<TransferObject> ades = findByContratoBeneficioAndInTntCodigoAndInSadCodigo(criterio, responsavel);

                if (ades == null || ades.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.erro.reativar.beneficio.status.nao.permitido", responsavel);
                }

                for (TransferObject aut : ades) {
                    String adeCodigo = (String) aut.getAttribute(Columns.ADE_CODIGO);

                    liquidarConsignacaoController.desliquidar(adeCodigo, null, responsavel);
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_REATIVACAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.reativar.beneficio", responsavel), responsavel);
                }

                update(cttBeneficio, null, null, null, responsavel);
                criaOcorrenciaContratoBeneficio(cbeCodigo, CodedValues.TOC_REATIVACAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.reativar.beneficio", responsavel), new Date(), null, responsavel);
            }

            return cbeCodigos;

        } catch (ContratoBeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        } catch (AutorizacaoControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void cancelarContratoBeneficioInadimplencia(String arquivoLote, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            if (TextHelper.isNull(arquivoLote)) {
                throw new ContratoBeneficioControllerException("mensagem.erro.cancelar.beneficio.inadimplente.arquivo.nao.encontrado", responsavel);
            }

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathConf = absolutePath + File.separatorChar + "conf" + File.separatorChar;
            String pathFile = absolutePath + File.separatorChar + "cancelamentoporinadimplencia" + File.separatorChar + "cse" + File.separatorChar ;

            String entradaArqConf = "imp_canc_beneficio_inadimplencia_entrada.xml";
            String tradutorArqConf = "imp_canc_beneficio_inadimplencia_tradutor.xml";

            if (TextHelper.isNull(tradutorArqConf) || TextHelper.isNull(entradaArqConf)) {
                throw new ContratoBeneficioControllerException("mensagem.erro.cancelar.beneficio.inadimplente.arquivos.configuracao.ausentes", responsavel);
            }

            File file = new File(pathFile);
            if (!file.exists()) {
                file.mkdirs();
            }

            // Seta diretório padrão arquivos XML
            entradaArqConf = pathConf + entradaArqConf;
            tradutorArqConf = pathConf + tradutorArqConf;

            // Verifica se arquivos de configuração existem
            File arqConfEntrada = new File(entradaArqConf);
            File arqConfTradutor = new File(tradutorArqConf);

            List<String> critica = new ArrayList<>();

            if (!arqConfTradutor.exists() || !arqConfEntrada.exists()) {
                throw new ContratoBeneficioControllerException("mensagem.erro.cancelar.beneficio.inadimplente.arquivos.configuracao.ausentes", responsavel);
            }

            if (!TextHelper.isNull(arquivoLote)) {

                StatusContratoBeneficio statusContratoBeneficio = StatusContratoBeneficioHome.findByPrimaryKey(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo());
                LeitorArquivoTexto l = null;
                Escritor e = null;
                Tradutor t = null;

                String arquivoCancelamento = pathFile + arquivoLote + ".txt";
                String arquivoProcessamento = arquivoCancelamento + ".prc";
                String arquivoProcessado = arquivoProcessamento + ".ok";
                File arqProcessamento = new File(arquivoProcessamento);
                File arqProcessado = new File(arquivoProcessado);
                File arq = new File(arquivoCancelamento);

                if (arq.renameTo(arqProcessamento)) {
                    l = new LeitorArquivoTexto(entradaArqConf, arquivoProcessamento);
                    e = new EscritorMemoria(new HashMap<>());
                    t = new Tradutor(tradutorArqConf, l, e);

                    t.iniciaTraducao(true);
                    int qtdeLinhas = 0;

                    while (t.traduzProximo()) {

                        Map<String, Object> valoresMap = t.getDados();

                        if (valoresMap != null) {
                            String rseMatricula = (String) valoresMap.get("RSE_MATRICULA");
                            String bfcCpf = (String) valoresMap.get("BFC_CPF");
                            String benCodigoPlano = (String) valoresMap.get("BEN_CODIGO_CONTRATO");
                            Date cbeDataFimVigencia = !TextHelper.isNull(valoresMap.get("CBE_DATA_FIM_VIGENCIA")) ? DateHelper.parse((String) valoresMap.get("CBE_DATA_FIM_VIGENCIA"), "yyyy-MM-dd") : null;

                            if(TextHelper.isNull(rseMatricula)) {
                               critica.add(l.getLinha() + ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.matricula.nulo", responsavel));
                               continue;
                            } else if(TextHelper.isNull(bfcCpf)) {
                               critica.add(l.getLinha() + ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.cpf.titular.nulo", responsavel));
                               continue;
                            } else if(TextHelper.isNull(benCodigoPlano)) {
                               critica.add(l.getLinha() + ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.codigo.contrato.beneficio.nulo", responsavel));
                               continue;
                            } else if(TextHelper.isNull(cbeDataFimVigencia)) {
                               critica.add(l.getLinha() + ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.data.fim.nulo", responsavel));
                               continue;
                            }

                            List<TransferObject> contratosBeneficio = listaContratosInadimplenciaBeneficio(rseMatricula, bfcCpf, benCodigoPlano, responsavel);

                            if (contratosBeneficio != null && contratosBeneficio.isEmpty()) {
                                critica.add(l.getLinha() + ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.nenhum.contrato", responsavel));
                                continue;
                            }

                            TipoMotivoOperacao tipoMotivoOperacao = TipoMotivoOperacaoHome.findByAcao(AcaoEnum.CANCELAR_CONTRATO_BENEFICIO_POR_INADIPLENCIA.getCodigo(), responsavel);
                            if (tipoMotivoOperacao == null) {
                                throw new ContratoBeneficioControllerException("mensagem.erro.processamento.cancelar.beneficio.inadimplente.tipo.motivo", responsavel, AcaoEnum.CANCELAR_CONTRATO_BENEFICIO_POR_INADIPLENCIA.getCodigo());
                            }

                            for (TransferObject contratoBeneficio : contratosBeneficio) {
                                String cbeCodigo = (String) contratoBeneficio.getAttribute(Columns.CBE_CODIGO);

                                ContratoBeneficio cttBeneficio = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                                // Setando o status para cancelamento solicitado
                                cttBeneficio.setStatusContratoBeneficio(statusContratoBeneficio);

                                // Setando a data de cancelamento para o ultimo dia do mes.
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
                                cttBeneficio.setCbeDataCancelamento(cal.getTime());

                                // Setando a data de fim da vigência, a data informada no arquivo de lote
                                cttBeneficio.setCbeDataFimVigencia(cbeDataFimVigencia);

                                update(cttBeneficio, CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO, null, tipoMotivoOperacao.getTmoCodigo(), responsavel);
                                criaOcorrenciaContratoBeneficio(cbeCodigo, CodedValues.TOC_SOLICITACAO_CANC_CONTRATO_BENEFICIO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.cancelar.beneficio.inadimplente", responsavel), new Date(), null, responsavel);
                            }
                        }

                        if (++qtdeLinhas % 1000 == 0) {
                            LOG.debug("LINHAS LIDAS = " + qtdeLinhas);
                        }
                    }
                    LOG.debug("TOTAL DE LINHAS LIDAS = " + qtdeLinhas);
                    try {
                        t.encerraTraducao();
                    } catch (ParserException pe) {
                        LOG.error(pe.getMessage(), pe);
                    }

                    if (arqProcessamento.exists()) {
                        arqProcessamento.renameTo(arqProcessado);
                    }
                }
            }

            if (critica.size() > 0) {
                // Grava de crítica com o resultado dos comandos que retornaram algum erro
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

                String nomeArqSaida = pathFile + "critica_" + arquivoLote + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");

                String nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = null;

                try {
                    arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                } catch (IOException ex) {
                    throw new ContratoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }

                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.lineSeparator()));

                arqSaida.close();
                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                try {
                    LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                    String nomeArqSaidaZip = nomeArqSaida + ".zip";
                    FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                    LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                    FileHelper.delete(nomeArqSaidaTxt);
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } catch (ParserException | ParseException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    private List<TransferObject> listaContratosInadimplenciaBeneficio(String rseMatricula, String bfcCpf, String benCodigoPlano, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListarContratosBeneficioCancelamentoInadimplenciaQuery contratosBeneficio = new ListarContratosBeneficioCancelamentoInadimplenciaQuery();
            contratosBeneficio.rseMatricula = rseMatricula;
            contratosBeneficio.bfcCpf = bfcCpf;
            contratosBeneficio.benCodigoContrato = benCodigoPlano;

            List<TransferObject> contratos = contratosBeneficio.executarDTO();

            return contratos;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    private String recuperaMotivoOperacaoExclusao(String cbeCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        try {
            ListaOcorrenciaContratosBeneficiosQuery query = new ListaOcorrenciaContratosBeneficiosQuery();
            query.cbeCodigo = cbeCodigo;
            query.motivoExclusao = true;
            query.maxResults = 1;

            List<TransferObject> ocorrenciaBeneficios = query.executarDTO();

            String tmoCodigo = "";
            if (ocorrenciaBeneficios != null && !ocorrenciaBeneficios.isEmpty()) {
                return (String) ocorrenciaBeneficios.get(0).getAttribute(Columns.OCB_TMO_CODIGO);
            } else {
                return tmoCodigo;
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }
    }

    @Override
    public String criaContratoBeneficioSemRegrasModulos(String rseCodigo, String svcCodigo, String codigoDependente, String csaCodigo, String tibCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws ContratoBeneficioControllerException {

        String cbeCodigo = "";
        try {
            Beneficio beneficio = BeneficioHome.findByConsignatariaServicoTipoBeneficiario(csaCodigo, svcCodigo, tibCodigo);

            if (beneficio == null) {
                throw new ContratoBeneficioControllerException("mensagem.reservar.margem.dependente.beneficio.nao.existe", responsavel);
            }

            RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            String serCodigo = registroServidor.getServidor().getSerCodigo();

            //Caso o contrato seja dependente é preciso verificar se já existe beneficiário titular criado.
            Beneficiario beneficiarioTitular = BeneficiarioHome.findByCpfEServidorERseCodigo(registroServidor.getServidor().getSerCpf(), serCodigo, rseCodigo);
            if(beneficiarioTitular == null) {
                beneficiarioTitular = BeneficiarioHome.findByCpfEServidor(registroServidor.getServidor().getSerCpf(), serCodigo);
                if(beneficiarioTitular == null) {
                    throw new ContratoBeneficioControllerException("mensagem.reservar.margem.dependente.beneficiario.titular.nao.existe", responsavel);
                }
            }

            // Caso o contrato seja dependente é preciso verificar se já existe contrato de titular antes de criar o do depenente, além da obrigatoriedade de existir o beneficiário dependente cadastrado
            Beneficiario beneficiario = null;
            List<String> sadCodigos = new ArrayList<>();
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);
            if(!TextHelper.isNull(codigoDependente)) {
                List<ContratoBeneficio> contratoBeneficioTitular = ContratoBeneficioHome.findByBfcCodigoAndSadCodigo(beneficiarioTitular.getBfcCodigo(), sadCodigos);
                if(contratoBeneficioTitular == null || contratoBeneficioTitular.isEmpty()) {
                    throw new ContratoBeneficioControllerException("mensagem.reservar.margem.dependente.contrato.beneficiario.titular.nao.existe", responsavel);
                }
                beneficiario = BeneficiarioHome.findByPrimaryKey(codigoDependente);

                if (beneficiario == null) {
                    throw new ContratoBeneficioControllerException("mensagem.reservar.margem.dependente.obrigatorio", responsavel);
                }
            }

            //DESENV-19961: Necessário verificar se existe algum contrato para o titular ou dependente já ativo para este benefício, se existir a reserva não pode ser permitida.
            String codigoBeneficiario = beneficiario !=null ? beneficiario.getBfcCodigo() : beneficiarioTitular.getBfcCodigo();
            String mensagemErroContratoExiste = beneficiario !=null ? "mensagem.reservar.margem.dependente.contrato.beneficio.dependente.existe" : "mensagem.reservar.margem.dependente.contrato.beneficio.titular.existe";

            List<ContratoBeneficio> contratoBeneficiarioExiste = ContratoBeneficioHome.findByBfcCodigoAndSadCodigoBenCodigo(codigoBeneficiario, sadCodigos, beneficio.getBenCodigo());
            if(contratoBeneficiarioExiste != null && !contratoBeneficiarioExiste.isEmpty()) {
                throw new ContratoBeneficioControllerException(mensagemErroContratoExiste, responsavel);
            }

            StatusContratoBeneficio statusContrato = new StatusContratoBeneficio(StatusContratoBeneficioEnum.ATIVO.getCodigo());

            ContratoBeneficio contratoBeneficio = ContratoBeneficioHome.create(beneficiario == null ? beneficiarioTitular : beneficiario, beneficio, String.valueOf(""), DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), String.valueOf(""), String.valueOf(""), adeVlr, new BigDecimal("0.00"), statusContrato, null);
            cbeCodigo = contratoBeneficio.getCbeCodigo();
        } catch (FindException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ContratoBeneficioControllerException(ex);
        }

        return cbeCodigo;
    }

}
