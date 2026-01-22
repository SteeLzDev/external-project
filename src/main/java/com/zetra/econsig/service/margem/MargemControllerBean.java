package com.zetra.econsig.service.margem;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.importacao.ValidaImportacao;
import com.zetra.econsig.folha.margem.ImportaMargem;
import com.zetra.econsig.folha.margem.ImportaMargemFactory;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.CalculoMargemDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCseHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEst;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEstHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrg;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrgHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.ControleDocumentoMargemHome;
import com.zetra.econsig.persistence.entity.HistoricoMediaMargemHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargem;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemCseHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemEstHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemOrgHome;
import com.zetra.econsig.persistence.entity.Margem;
import com.zetra.econsig.persistence.entity.MargemHome;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoCalculoSalarioQuery;
import com.zetra.econsig.persistence.query.margem.ListaCasamentoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoMediaMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoProcMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemComServicoAtivoQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemIncidenteQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemReservaGapQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargensIncideEmprestimoQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargensIncidentesTransferenciaQuery;
import com.zetra.econsig.persistence.query.margem.ListaPenultimoPeriodoHistoricoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ObtemValidacaoPdfMargemQuery;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: MargemControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class MargemControllerBean implements MargemController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MargemControllerBean.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PontuacaoServidorController pontuacaoServidorController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private ValidaImportacaoController validaImportacaoController;

    // Margem
    @Override
    public MargemTO findMargem(MargemTO margem, AcessoSistema responsavel) throws MargemControllerException {
        return setMargemValues(findMargemBean(margem, responsavel));
    }

    private Margem findMargemBean(MargemTO margem, AcessoSistema responsavel) throws MargemControllerException {
        Margem margemBean = null;
        if (margem.getMarCodigo() != null) {
            try {
                margemBean = MargemHome.findByPrimaryKey(margem.getMarCodigo());
            } catch (FindException ex) {
                throw new MargemControllerException("mensagem.erro.margem.nao.encontrada", responsavel);
            }
        } else {
            throw new MargemControllerException("mensagem.erro.margem.nao.encontrada", responsavel);
        }
        return margemBean;
    }

    private MargemTO setMargemValues(Margem margemBean) {
        MargemTO margem = new MargemTO(margemBean.getMarCodigo());
        margem.setMarCodigoPai(margemBean.getMargemPai() != null ? margemBean.getMargemPai().getMarCodigo() : null);
        margem.setMarDescricao(margemBean.getMarDescricao());
        margem.setMarSequencia(margemBean.getMarSequencia());
        margem.setMarPorcentagem(margemBean.getMarPorcentagem() != null ? margemBean.getMarPorcentagem() : null);
        margem.setMarExibeCse(margemBean.getMarExibeCse() != null ? margemBean.getMarExibeCse().charAt(0) : null);
        margem.setMarExibeOrg(margemBean.getMarExibeOrg() != null ? margemBean.getMarExibeOrg().charAt(0) : null);
        margem.setMarExibeSer(margemBean.getMarExibeSer() != null ? margemBean.getMarExibeSer().charAt(0) : null);
        margem.setMarExibeCsa(margemBean.getMarExibeCsa() != null ? margemBean.getMarExibeCsa().charAt(0) : null);
        margem.setMarExibeCor(margemBean.getMarExibeCor() != null ? margemBean.getMarExibeCor().charAt(0) : null);
        margem.setMarExibeSup(margemBean.getMarExibeSup() != null ? margemBean.getMarExibeSup().charAt(0) : null);

        return margem;
    }

    @Override
    public void updateMargem(MargemTO margem, AcessoSistema responsavel) throws MargemControllerException {
        try {
            Margem margemBean = findMargemBean(margem, responsavel);
            LogDelegate log = new LogDelegate(responsavel, Log.MARGEM, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setMargem(String.valueOf(margemBean.getMarCodigo()));

            /* Compara a versão do cache com a passada por parâmetro */
            MargemTO MargemCache = setMargemValues(margemBean);
            CustomTransferObject merge = log.getUpdatedFields(margem.getAtributos(), MargemCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.MAR_CODIGO_PAI)) {
                margemBean.setMargemPai(MargemHome.findByPrimaryKey((Short) merge.getAttribute(Columns.MAR_CODIGO_PAI)));
            }
            if (merge.getAtributos().containsKey(Columns.MAR_DESCRICAO)) {
                margemBean.setMarDescricao((String) merge.getAttribute(Columns.MAR_DESCRICAO));
            }
            if (merge.getAtributos().containsKey(Columns.MAR_SEQUENCIA)) {
                margemBean.setMarSequencia((Short) merge.getAttribute(Columns.MAR_SEQUENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.MAR_PORCENTAGEM)) {
                margemBean.setMarPorcentagem(merge.getAttribute(Columns.MAR_PORCENTAGEM) != null ? ((BigDecimal) merge.getAttribute(Columns.MAR_PORCENTAGEM)) : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_CSE)) {
                margemBean.setMarExibeCse(merge.getAttribute(Columns.MAR_EXIBE_CSE) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_CSE)).toString() : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_ORG)) {
                margemBean.setMarExibeOrg(merge.getAttribute(Columns.MAR_EXIBE_ORG) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_ORG)).toString() : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_SER)) {
                margemBean.setMarExibeSer(merge.getAttribute(Columns.MAR_EXIBE_SER) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_SER)).toString() : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_CSA)) {
                margemBean.setMarExibeCsa(merge.getAttribute(Columns.MAR_EXIBE_CSA) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_CSA)).toString() : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_COR)) {
                margemBean.setMarExibeCor(merge.getAttribute(Columns.MAR_EXIBE_COR) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_COR)).toString() : null);
            }
            if (merge.getAtributos().containsKey(Columns.MAR_EXIBE_SUP)) {
                margemBean.setMarExibeSup(merge.getAttribute(Columns.MAR_EXIBE_SUP) != null ? ((Character) merge.getAttribute(Columns.MAR_EXIBE_SUP)).toString() : null);
            }
            MargemHome.update(margemBean);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException | UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMargem(AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaMargemQuery margens = new ListaMargemQuery();
            return margens.executarDTO();
        } catch (HQueryException ex) {
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<MargemTO> lstMargemRaiz(AcessoSistema responsavel) throws MargemControllerException {
        return lstMargemRaiz(false, responsavel);
    }

    @Override
    public List<MargemTO> lstMargemRaiz(boolean alteracaoMultiplaAde, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaMargemQuery margens = new ListaMargemQuery();
            margens.isRaiz = true;
            margens.alteracaoMultiplaAde = alteracaoMultiplaAde;
            return margens.executarDTO(MargemTO.class);
        } catch (HQueryException ex) {
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMargemReservaGap(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaMargemReservaGapQuery query = new ListaMargemReservaGapQuery();
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMargensIncidentesTransferencia(String csaCodigo, String orgCodigo, String rseCodigo, String estCodigo, String papCodigo, AcessoSistema responsavel) throws MargemControllerException {
        try {
            List<TransferObject> retorno = null;
            List<Short> margens = new ArrayList<>();
            ListaMargemIncidenteQuery incidentesQuery = new ListaMargemIncidenteQuery();
            incidentesQuery.csaCodigo = csaCodigo;
            incidentesQuery.orgCodigo = orgCodigo;
            incidentesQuery.rseCodigo = rseCodigo;
            incidentesQuery.estCodigo = estCodigo;

            List<MargemTO> incidentes = incidentesQuery.executarDTO(MargemTO.class);

            if (incidentes == null || incidentes.isEmpty()) {
                if (orgCodigo != null) {
                    throw new MargemControllerException("mensagem.erro.orgao.sem.cnv.ativo", responsavel);
                }
            }

            for (MargemTO margem : incidentes) {
                margens.add(margem.getMarCodigo());
            }

            ListaMargensIncidentesTransferenciaQuery margensQuery = new ListaMargensIncidentesTransferenciaQuery();
            margensQuery.margens = margens;
            margensQuery.papCodigo = papCodigo;
            retorno = margensQuery.executarDTO();

            if (retorno == null || retorno.isEmpty()) {
                throw new MargemControllerException("mensagem.erro.transferencia.margem.configuracao", responsavel);
            }

            return retorno;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstCasamentoMargem(AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaCasamentoMargemQuery query = new ListaCasamentoMargemQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Long createHistoricoMargem(TransferObject historicoMargem, Map<Short, Map<String, BigDecimal>> mediaMargem, List<Short> lstMarCodigosExtra, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException {
        try {
            Long hpmCodigo = null;

            Date hpmPeriodo;
            try {
                hpmPeriodo = DateHelper.parse(historicoMargem.getAttribute(Columns.HPM_PERIODO).toString(), "yyyy-MM-dd");
            } catch (ParseException e) {
                LOG.error(e.getMessage(), e);
                throw new MargemControllerException("mensagem.erro.transferencia.margem.periodo.informado", responsavel, e);
            }
            Integer hpmQtdServidoresAntes = (Integer) historicoMargem.getAttribute(Columns.HPM_QTD_SERVIDORES_ANTES);
            Integer hpmQtdServidoresDepois = (Integer) historicoMargem.getAttribute(Columns.HPM_QTD_SERVIDORES_DEPOIS);

            String usuCodigo = responsavel.getUsuCodigo();
            HistoricoProcMargem histProcMargem = HistoricoProcMargemHome.create(usuCodigo, hpmPeriodo, null, hpmQtdServidoresAntes, hpmQtdServidoresDepois);
            hpmCodigo = histProcMargem.getHpmCodigo();

            if (estCodigos != null && !estCodigos.isEmpty()) {
                for (String estCodigo : estCodigos) {
                    HistoricoProcMargemEstHome.create(estCodigo, hpmCodigo);
                }
            } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
                for (String orgCodigo : orgCodigos) {
                    HistoricoProcMargemOrgHome.create(orgCodigo, hpmCodigo);
                }
            } else {
                String cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
                HistoricoProcMargemCseHome.create(cseCodigo, hpmCodigo);
            }

            // Cria histórico da média da margem 1
            createHistoricoMediaMargem(hpmCodigo, CodedValues.INCIDE_MARGEM_SIM, mediaMargem);
            // Cria histórico da média da margem 2
            createHistoricoMediaMargem(hpmCodigo, CodedValues.INCIDE_MARGEM_SIM_2, mediaMargem);
            // Cria histórico da média da margem 3
            createHistoricoMediaMargem(hpmCodigo, CodedValues.INCIDE_MARGEM_SIM_3, mediaMargem);

            if (!lstMarCodigosExtra.isEmpty()) {
                for (Short marCodigo : lstMarCodigosExtra) {
                    // Cria histórico da média da margem extra
                    createHistoricoMediaMargem(hpmCodigo, marCodigo, mediaMargem);
                }
            }

            return hpmCodigo;

        } catch (com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void createHistoricoMediaMargem(Long hpmCodigo, Short incideMargemSim, Map<Short, Map<String, BigDecimal>> mediaMargem) throws com.zetra.econsig.exception.CreateException {
        if (mediaMargem != null && mediaMargem.containsKey(incideMargemSim)) {
            Map<String, BigDecimal> media = mediaMargem.get(incideMargemSim);
            BigDecimal hmmMediaMargemAntes = media.get(Columns.HMM_MEDIA_MARGEM_ANTES);
            BigDecimal hmmMediaMargemDepois = media.get(Columns.HMM_MEDIA_MARGEM_DEPOIS);

            HistoricoMediaMargemHome.create(hpmCodigo, incideMargemSim, hmmMediaMargemAntes, hmmMediaMargemDepois);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoProcMargem(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaHistoricoProcMargemQuery query = new ListaHistoricoProcMargemQuery();
            query.estCodigos = estCodigos;
            query.orgCodigos = orgCodigos;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erro.margem.recuperar.historico.processamento", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoMediaMargem(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaHistoricoMediaMargemQuery query = new ListaHistoricoMediaMargemQuery();
            query.estCodigos = estCodigos;
            query.orgCodigos = orgCodigos;

            List<TransferObject> retorno = null;
            List<TransferObject> lista = query.executarDTO();

            if (lista != null && !lista.isEmpty()) {
                retorno = new ArrayList<>();
                TransferObject mediaMargem = new CustomTransferObject();

                String hpmCodigo = null;
                for (TransferObject to : lista) {
                    // Verificação para recuperar somente o último histórico de margem
                    if (!TextHelper.isNull(hpmCodigo) && !hpmCodigo.equals(to.getAttribute(Columns.HPM_CODIGO).toString())) {
                        break;
                    }
                    // Seta o código do histórico de margem para buscar somente o último histórico
                    hpmCodigo = to.getAttribute(Columns.HPM_CODIGO).toString();

                    Short incideMargem = Short.valueOf(to.getAttribute(Columns.HMM_MAR_CODIGO).toString());
                    BigDecimal hmmMediaMargemDepois = (BigDecimal) to.getAttribute(Columns.HMM_MEDIA_MARGEM_DEPOIS);
                    if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                        mediaMargem.setAttribute("RSE_MARGEM", hmmMediaMargemDepois);
                    }
                    if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                        mediaMargem.setAttribute("RSE_MARGEM_2", hmmMediaMargemDepois);
                    }
                    if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                        mediaMargem.setAttribute("RSE_MARGEM_3", hmmMediaMargemDepois);
                    }
                }

                retorno.add(mediaMargem);
            }

            return retorno;

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erro.margem.recuperar.historico.processamento", responsavel, ex);
        }
    }

    /*********************** CALCULO DE MARGEM ***********************************/

    /**
     * Faz o recalculo das margens gerando histórico de margem para
     * esta alteração. Esse método é utilizado pela página de adm
     * para recalculo de margem.
     * @param tipoEntidade
     * @param entCodigos
     * @param responsavel
     * @throws MargemControllerException
     */
    @Override
    public void recalculaMargemComHistorico(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException {
        try {
            // Transforma a lista de códigos em três listas, orgCodigos / estCodigos / rseCodigos
            // baseado no tipoEntidade (EST/ORG/RSE)
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            List<String> rseCodigos = null;
            if (tipoEntidade != null) {
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    estCodigos = entCodigos;
                } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    orgCodigos = entCodigos;
                } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                    rseCodigos = entCodigos;
                }
            }

            // Inicia gravação de histórico de margem
            HistoricoMargemDAO historicoMargemDAO = DAOFactory.getDAOFactory().getHistoricoMargemDAO();
            LOG.debug("Inicia histórico de margem: " + DateHelper.getSystemDatetime());
            historicoMargemDAO.iniciarHistoricoMargem(orgCodigos, estCodigos, rseCodigos, OperacaoHistoricoMargemEnum.RECALCULO_MARGEM);
            LOG.debug("fim - Inicia histórico de margem: " + DateHelper.getSystemDatetime());

            // Executa o recalculo de margem
            recalculaMargem(tipoEntidade, entCodigos, null, true, true, responsavel);

            // Finaliza o historico de margem
            LOG.debug("Finaliza histórico de margem: " + DateHelper.getSystemDatetime());
            historicoMargemDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, rseCodigos, OperacaoHistoricoMargemEnum.RECALCULO_MARGEM);
            LOG.debug("fim - Finaliza histórico de margem: " + DateHelper.getSystemDatetime());

        } catch (DAOException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (MargemControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Faz o recalculo das margens sem gerar histórico de margem para
     * esta alteração. Esse método é utilizado pelas rotinas de
     * exportação e importação de retorno.
     * @param tipoEntidade
     * @param entCodigos
     * @param responsavel
     * @throws MargemControllerException
     */
    @Override
    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException {
        recalculaMargem(tipoEntidade, entCodigos, null, true, true, responsavel);
    }

    /**
     * Método privado que executa o calculo da margem. Faz o calculo
     * da margem usada e da margem restante.
     * @param tipoEntidade
     * @param entCodigos
     * @param servidor
     * @param responsavel
     * @throws MargemControllerException
     */
    @Override
    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, ServidorDAO servidor, boolean atualizarAdeValor, boolean calcularPeriodo, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ImportaMargem importadorMargem = null;
            String importadorMargemClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_MARGEM, responsavel);
            if (!TextHelper.isNull(importadorMargemClassName)) {
                importadorMargem = ImportaMargemFactory.getImportadorMargem(importadorMargemClassName);
                if (importadorMargem.sobreporRecalculoMargem(tipoEntidade, entCodigos, responsavel)) {
                    importadorMargem.recalculaMargem(tipoEntidade, entCodigos, responsavel);
                    return;
                }
            }

            // Pré-processamento do cálculo de margem usando método da classe específica do gestor.
            if (importadorMargem != null) {
                LOG.debug("pre-processamento calculo de margem: " + DateHelper.getSystemDatetime());
                importadorMargem.preRecalculoMargem(tipoEntidade, entCodigos, responsavel);
                LOG.debug("fim - pre-processamento calculo de margem " + DateHelper.getSystemDatetime());
            }

            // Parâmetros de sistema necessários
            boolean controlaMargem = (ParamSist.getInstance().getParam(CodedValues.TPC_ZERA_MARGEM_USADA, responsavel) == null || ParamSist.getInstance().getParam(CodedValues.TPC_ZERA_MARGEM_USADA, responsavel).equals(CodedValues.TPC_NAO));
            boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            // Realiza o calculo das margens usadas
            if (servidor == null) {
                servidor = DAOFactory.getDAOFactory().getServidorDAO();
            }

            List<String> estCodigos = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase("EST") ? entCodigos : null);
            List<String> orgCodigos = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase("ORG") ? entCodigos : null);

            // Seta o período de exportação para aquele do último retorno
            if (calcularPeriodo) {
                List<TransferObject> periodoExportacao = periodoController.obtemPeriodoCalculoMargem(orgCodigos, estCodigos, true, responsavel);
                ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);
            }

            LOG.debug("Calcula margem usada: " + DateHelper.getSystemDatetime());
            servidor.calculaMargemUsada(tipoEntidade, entCodigos, controlaMargem);
            LOG.debug("fim - Calcula margem usada: " + DateHelper.getSystemDatetime());

            // Realiza o calculo das margens restantes
            LOG.debug("Calcula margem restante: " + DateHelper.getSystemDatetime());
            servidor.calculaMargemRestante(tipoEntidade, entCodigos);
            LOG.debug("fim - Calcula margem restante: " + DateHelper.getSystemDatetime());

            // Faz o calculo das margens casadas
            if (margem1CasadaMargem3) {
                // Faz a associação entre as margens 1 e 3.
                LOG.debug("Calcula margem 1 casada margem 3: " + DateHelper.getSystemDatetime());
                servidor.calculaMargem1CasadaMargem3(tipoEntidade, entCodigos);
                LOG.debug("fim - Calcula margem 1 casada margem 3: " + DateHelper.getSystemDatetime());
            } else if (margem123Casadas) {
                // Faz a associação entre as margens 1, 2 e 3.
                LOG.debug("Calcula margens 1, 2 e 3 casadas: " + DateHelper.getSystemDatetime());
                servidor.calculaMargens123Casadas(tipoEntidade, entCodigos);
                LOG.debug("fim - Calcula margens 1, 2 e 3 casadas: " + DateHelper.getSystemDatetime());
            } else if (margem1CasadaMargem3Esq) {
                // Faz a associação entre as margens 1 e 3 pela Esquerda.
                LOG.debug("Calcula margem 1 casada margem 3 pela Esquerda: " + DateHelper.getSystemDatetime());
                servidor.calculaMargem1CasadaMargem3Esq(tipoEntidade, entCodigos);
                LOG.debug("fim - Calcula margem 1 casada margem 3 pela Esquerda: " + DateHelper.getSystemDatetime());
            } else if (margem123CasadasEsq) {
                // Faz a associação entre as margens 1, 2 e 3 pela Esquerda.
                LOG.debug("Calcula margens 1, 2 e 3 casadas pela Esquerda: " + DateHelper.getSystemDatetime());
                servidor.calculaMargens123CasadasEsq(tipoEntidade, entCodigos);
                LOG.debug("fim - Calcula margens 1, 2 e 3 casadas pela Esquerda: " + DateHelper.getSystemDatetime());
            } else if (margem1CasadaMargem3Lateral) {
                // Faz a associação entre as margens 1 e 3 Lateralmente.
                LOG.debug("Calcula margem 1 casada margem 3 Lateralmente: " + DateHelper.getSystemDatetime());
                servidor.calculaMargem1CasadaMargem3Lateral(tipoEntidade, entCodigos);
                LOG.debug("fim - Calcula margem 1 casada margem 3 Lateralmente: " + DateHelper.getSystemDatetime());
            }

            // Calcula as margens extras
            recalculaMargemExtra(tipoEntidade, entCodigos, responsavel);

            // Recalcula a pontuação dos servidores
            pontuacaoServidorController.calcularPontuacao(tipoEntidade, entCodigos, responsavel);

            // Pós-processamento do cálculo de margem usando método da classe específica do gestor.
            if (importadorMargem != null) {
                LOG.debug("pos-processamento calculo de margem: " + DateHelper.getSystemDatetime());
                importadorMargem.posRecalculoMargem(tipoEntidade, entCodigos, responsavel);
                LOG.debug("fim - pos-processamento calculo de margem " + DateHelper.getSystemDatetime());
            }

            // DESENV-11533 : atualiza o valor da consignação quando a margem restante for alterada
            if (atualizarAdeValor) {
                AutorizacaoDAO adeDAO = DAOFactory.getDAOFactory().getAutorizacaoDAO();
                List<String> rseCodigos = adeDAO.atualizarAdeValorAlteracaoMargem(tipoEntidade, entCodigos, responsavel);
                if (rseCodigos != null && !rseCodigos.isEmpty()) {
                    // Executa o recálculo de margem parcial para os servidores que tiveram consignações alteradas
                    recalculaMargem("RSE", rseCodigos, servidor, false, calcularPeriodo, responsavel);
                }
            }

            // Se é um recálculo de margem parcial (tipo = RSE), então volta o período para o
            // último cadastrado na tb_historico_exportacao para que fique com o perido já
            // exportado caso já tenha passado o corte.
            if (calcularPeriodo && tipoEntidade != null && tipoEntidade.equalsIgnoreCase("RSE")) {
                periodoController.obtemPeriodoImpRetorno(orgCodigos, estCodigos, true, responsavel);
            }

            // log de recalculo de margem
            LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.IMPORTACAO_MARGEM, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.recalculo.margem", responsavel));
            log.write();

            // log de ocorrência de consignante
            if (tipoEntidade == null || !tipoEntidade.equalsIgnoreCase("RSE")) {
                consignanteController.createOcorrenciaCse(CodedValues.TOC_RECALCULO_MARGEM, responsavel);
            }

        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex, ex.getMessage());
        }
    }

    private void recalculaMargemExtra(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException {
        try {
            // Parâmetros de sistema necessários
            boolean controlaMargem = (ParamSist.getInstance().getParam(CodedValues.TPC_ZERA_MARGEM_USADA, responsavel) == null || ParamSist.getInstance().getParam(CodedValues.TPC_ZERA_MARGEM_USADA, responsavel).equals(CodedValues.TPC_NAO));

            // Cálcula margem usada e restante
            CalculoMargemDAO dao = DAOFactory.getDAOFactory().getCalculoMargemDAO();
            dao.calcularMargemExtraUsada(tipoEntidade, entCodigos, controlaMargem);
            dao.calcularMargemExtraRestante(tipoEntidade, entCodigos);

            // Obtém os casamentos de margem
            List<Short> grupos = CasamentoMargem.getInstance().getGrupos();
            for (Short grupo : grupos) {
                String tipo = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                List<Short> marCodigos = CasamentoMargem.getInstance().getMargensCasadas(grupo);
                if (tipo.equals(CasamentoMargem.DIREITA)) {
                    LOG.debug("Calcula grupo " + grupo + " de margem casada pela direita [" + TextHelper.join(marCodigos, ",") + "]: " + DateHelper.getSystemDatetime());
                    dao.calcularMargemExtraCasadaDireita(tipoEntidade, entCodigos, marCodigos);
                } else if (tipo.equals(CasamentoMargem.ESQUERDA)) {
                    LOG.debug("Calcula grupo " + grupo + " de margem casada pela esquerda [" + TextHelper.join(marCodigos, ",") + "]: " + DateHelper.getSystemDatetime());
                    dao.calcularMargemExtraCasadaEsquerda(tipoEntidade, entCodigos, marCodigos);
                } else if (tipo.equals(CasamentoMargem.LATERAL)) {
                    LOG.debug("Calcula grupo " + grupo + " de margem casada lateralmente [" + TextHelper.join(marCodigos, ",") + "]: " + DateHelper.getSystemDatetime());
                    dao.calcularMargemExtraCasadaLateral(tipoEntidade, entCodigos, marCodigos);
                } else if (tipo.equals(CasamentoMargem.MINIMO)) {
                    LOG.debug("Calcula grupo " + grupo + " de margem casada limitada ao mínimo [" + TextHelper.join(marCodigos, ",") + "]: " + DateHelper.getSystemDatetime());
                    dao.calcularMargemExtraCasadaMinimo(tipoEntidade, entCodigos, marCodigos);
                }
            }
        } catch (DAOException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException(ex);
        }
    }

    @Override
    public Date recuperaPenultimoPeriodoHistoricoMargem(AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaPenultimoPeriodoHistoricoMargemQuery query = new ListaPenultimoPeriodoHistoricoMargemQuery();
            query.maxResults = 1;
            query.firstResult = 1;
            List<TransferObject> lstPenultimoPeriodoHistoricoMargem = query.executarDTO();

            Date penultimoPeriodoHistoricoMargem = null;
            if (lstPenultimoPeriodoHistoricoMargem != null && !lstPenultimoPeriodoHistoricoMargem.isEmpty()) {
                penultimoPeriodoHistoricoMargem = (Date) lstPenultimoPeriodoHistoricoMargem.get(0).getAttribute("PENULTIMO_PERIODO");
            }
            return penultimoPeriodoHistoricoMargem;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void criaArquivoMargemOrigemServicoExterno(String urlSistemaExterno, AcessoSistema responsavel) throws MargemControllerException {
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, responsavel)) {
            LOG.debug("Para utilizar o criação de arquivo de margem a partir de um serviço externo, é nessário habilitar o parâmetro de sistema 554");
            return;
        }
        CalendarioFolhaOrg cfo = null;
        CalendarioFolhaEst cfe = null;
        CalendarioFolhaCse cfc = null;
        Date dataAtual = DateHelper.getSystemDate();
        try {
            boolean criaArquivo = false;

            List<TransferObject> periodos = periodoController.obtemPeriodoImpRetorno(null, null, false, responsavel);
            for (TransferObject periodoEntidade : periodos) {
                Date periodo = (Date) periodoEntidade.getAttribute(Columns.PEX_PERIODO);
                Date dataPrevistaRetorno = null;
                try {
                    cfo = CalendarioFolhaOrgHome.findByPrimaryKey((String) periodoEntidade.getAttribute(Columns.ORG_CODIGO), periodo);
                    dataPrevistaRetorno = cfo.getCfoDataPrevistaRetorno();
                    if (dataPrevistaRetorno != null && dataAtual.compareTo(dataPrevistaRetorno) == 0) {
                        criaArquivo = true;
                        break;
                    }
                } catch (FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }

                try {
                    cfe = CalendarioFolhaEstHome.findByPrimaryKey((String) periodoEntidade.getAttribute(Columns.EST_CODIGO), periodo);
                    dataPrevistaRetorno = cfe.getCfeDataPrevistaRetorno();
                    if (dataPrevistaRetorno != null && dataAtual.compareTo(dataPrevistaRetorno) == 0) {
                        criaArquivo = true;
                        break;
                    }
                } catch (FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }

                try {
                    cfc = CalendarioFolhaCseHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA, periodo);
                    dataPrevistaRetorno = cfc.getCfcDataPrevistaRetorno();
                    if (dataPrevistaRetorno != null && dataAtual.compareTo(dataPrevistaRetorno) == 0) {
                        criaArquivo = true;
                        break;
                    }
                } catch (FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }
            }

            if(!criaArquivo) {
                LOG.debug("Arquivo de margem só é criado quando existe alguma data prevista de retorno");
                return;
            }

            Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);

            RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HashMap<String, String> body = new HashMap<>();
            body.put("apiKey", cse.getCseIdentificadorInterno());

            JSONObject jsonObject = new JSONObject(body);

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(urlSistemaExterno+"api/margem/v1/all", HttpMethod.POST, httpEntity, String.class);

            if(response == null || (response.getStatusCode() != HttpStatus.NOT_FOUND && response.getStatusCode() != HttpStatus.OK)) {
                LOG.debug("Não foi possível conectar ao sistema de margem, data prevista será alterada para o dia seguinte");
                atualizadaDataPrevistaProximoDia(cfc, cfo, cfe, dataAtual, responsavel);
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> dados = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
            criacaoArquivoMargem(dados, responsavel);
        } catch (PeriodoException | FindException | JsonProcessingException | MargemControllerException ex) {
            String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
            if (!TextHelper.isNull(emailSuporte)) {
                try {
                    EnviaEmailHelper.notificaSuporteErroCriarArqMargemServicoExterno(emailSuporte, responsavel);
                } catch (ViewHelperException e) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            atualizadaDataPrevistaProximoDia(cfc, cfo, cfe, dataAtual, responsavel);
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void atualizadaDataPrevistaProximoDia(CalendarioFolhaCse cfc, CalendarioFolhaOrg cfo, CalendarioFolhaEst cfe, Date dataAtual, AcessoSistema responsavel) throws MargemControllerException {
        Date novaDataPrevista = DateHelper.addDays(dataAtual, 1);
        try {
            if(cfc != null) {
                cfc.setCfcDataPrevistaRetorno(novaDataPrevista);
                CalendarioFolhaCseHome.update(cfc);
            } else if (cfe != null) {
                cfe.setCfeDataPrevistaRetorno(novaDataPrevista);
                CalendarioFolhaEstHome.update(cfe);
            } else if (cfo != null){
                cfo.setCfoDataPrevistaRetorno(novaDataPrevista);
                CalendarioFolhaOrgHome.update(cfo);
            }
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void criacaoArquivoMargem(List<Map<String, Object>> dados, AcessoSistema responsavel) throws MargemControllerException {
        try {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathArquivoMargem = absolutePath + File.separatorChar + "margem" + File.separatorChar + "cse";
            String nomeArqConfMargemEntrada = absolutePath + File.separatorChar + "conf" + File.separatorChar + "imp_margem_entrada_servico_externo.xml";
            String nomeArqConfMargemTradutor = absolutePath + File.separatorChar + "conf" + File.separatorChar + "imp_margem_tradutor_servico_externo.xml";
            //O arquivo de saída é o arquivo de entrada de Margem para importação
            String nomeArqConfMargemSaida = absolutePath + File.separatorChar + "conf" + File.separatorChar + (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MARGEM, responsavel);

            Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(AcessoSistema.ENTIDADE_SUP, CodedValues.CSE_CODIGO_SISTEMA, null, null, responsavel);
            String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get("margem.padraoNomeArquivoFinal"))) ? paramValidacaoArq.get("margem.padraoNomeArquivoFinal") : null;

            if(TextHelper.isNull(padraoNomeArquivo)) {
                LOG.debug("Necessário o cadastro do padrão final do arquivo de margem");
                throw new MargemControllerException("mensagem.erroInternoSistema", responsavel);
            }

            String nomeArquivoFormatado = ValidaImportacao.substituirPadroesNomeArquivoFinal(padraoNomeArquivo, AcessoSistema.ENTIDADE_SUP, responsavel.getCodigoEntidade(), responsavel);
            String nomeArquivoFinal = pathArquivoMargem + File.separatorChar + nomeArquivoFormatado;

            DAOFactory daoFactory = DAOFactory.getDAOFactory();
            MargemDAO margemDAO = daoFactory.getMargemDAO();
            margemDAO.criaTabelaArquivoMargem(dados, nomeArqConfMargemSaida, nomeArqConfMargemEntrada, nomeArqConfMargemTradutor, nomeArquivoFinal, responsavel);

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.margem.nome", responsavel, nomeArquivoFormatado));
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMargemComServicoAtivo(AcessoSistema responsavel) throws MargemControllerException {
        try {
            ListaMargemComServicoAtivoQuery query = new ListaMargemComServicoAtivoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject lstMargemIncideEmprestimo(String rseCodigo, AcessoSistema responsavel) throws MargemControllerException {
        try {
            final ListaMargensIncideEmprestimoQuery query = new ListaMargensIncideEmprestimoQuery();
            query.rseCodigo = rseCodigo;
            final List<TransferObject> margens = query.executarDTO();
            if (!margens.isEmpty()) {
                return margens.get(0);
            } else {
                return null;
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public BigDecimal obtemVlrTotalConsignacoesCalculoSalario(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException {
        try {
            final ObtemTotalValorConsignacaoCalculoSalarioQuery query = new ObtemTotalValorConsignacaoCalculoSalarioQuery();
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;
            return query.executarSomatorio(BigDecimal.ZERO);
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createControleDocumentoMargem(String rseCodigo, String localArquivo, String chave, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ControleDocumentoMargemHome.create(chave, rseCodigo, localArquivo);
        } catch (CreateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransferObject validaDocumentoMargem(String matricula, String cpf, String chave, AcessoSistema responsavel) throws MargemControllerException {
        try {
            ObtemValidacaoPdfMargemQuery bean = new ObtemValidacaoPdfMargemQuery();
            bean.cpf = cpf;
            bean.chave = chave;
            bean.matricula = matricula;

            List<TransferObject> result = bean.executarDTO();
            if (!result.isEmpty()) {
                return result.get(0);
            } else {
                return null;
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MargemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}
