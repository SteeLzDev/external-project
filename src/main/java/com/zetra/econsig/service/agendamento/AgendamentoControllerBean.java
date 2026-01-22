package com.zetra.econsig.service.agendamento;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.Agendador;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Agendamento;
import com.zetra.econsig.persistence.entity.AgendamentoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAgendamentoHome;
import com.zetra.econsig.persistence.entity.ParametroAgendamentoHome;
import com.zetra.econsig.persistence.entity.StatusAgendamento;
import com.zetra.econsig.persistence.entity.StatusAgendamentoHome;
import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosInstantaneosParaExecucaoQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosParaExecucaoQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaOcorrenciaAgendamentoComErroQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaOcorrenciaAgendamentoPorPeriodoQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaParametrosAgendamentoQuery;
import com.zetra.econsig.persistence.query.agendamento.ListaTipoAgendamentoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: AgendamentoControllerBean</p>
 * <p>Description: Fachada dos métodos de negócio de Agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AgendamentoControllerBean implements AgendamentoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AgendamentoControllerBean.class);

    /**
     * @see AgendamentoController#insereAgendamento(TransferObject, Map, AcessoSistema)
     */
    @Override
    public void insereAgendamento(TransferObject transferObject, Map<String, List<String>> parametros,
            int periodicidade, AcessoSistema responsavel) throws AgendamentoControllerException {

        if ((transferObject == null) ||
                TextHelper.isNull(transferObject.getAttribute(Columns.AGD_DESCRICAO)) ||
                TextHelper.isNull(transferObject.getAttribute(Columns.AGD_TAG_CODIGO)) ||
                TextHelper.isNull(transferObject.getAttribute(Columns.AGD_JAVA_CLASS_NAME))) {

            throw new AgendamentoControllerException("mensagem.erro.agendamento.campos.nao.informados.inclusao", responsavel);
        }

        final boolean permiteAgendaParaMesmoDia = ((responsavel.isCse() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSE, CodedValues.TPC_SIM, responsavel)) ||
                                             (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_SUP, CodedValues.TPC_SIM, responsavel)) ||
                                             (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSA, CodedValues.TPC_SIM, responsavel)));

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        final String descricao = transferObject.getAttribute(Columns.AGD_DESCRICAO).toString();
        final StatusAgendamentoEnum status = StatusAgendamentoEnum.AGUARDANDO_EXECUCAO;
        final Date dataCadastro = new Date();
        Date dataPrevista = calendar.getTime();
        if (transferObject.getAttribute(Columns.AGD_DATA_PREVISTA) != null) {
            try {
                dataPrevista = DateHelper.parse(transferObject.getAttribute(Columns.AGD_DATA_PREVISTA).toString(), LocaleHelper.getDatePattern());
                if (!permiteAgendaParaMesmoDia && (dataPrevista.compareTo(DateHelper.getSystemDate()) == 0)) {
                    throw new AgendamentoControllerException("mensagem.erro.agendamento.data.execucao.passada", responsavel);
                } else if (dataPrevista.compareTo(DateHelper.getSystemDate()) < 0) {
                    throw new AgendamentoControllerException(permiteAgendaParaMesmoDia ? "mensagem.erro.agendamento.data.execucao.passada.hoje" : "mensagem.erro.agendamento.data.execucao.passada", responsavel);
                }
            } catch (final ParseException e) {
                LOG.error("Não foi possível realizar o parser da data prevista de agendamento, processo agendado para o próximo dia.");
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                dataPrevista = cal.getTime();
            }
        }
        final String classe = transferObject.getAttribute(Columns.AGD_JAVA_CLASS_NAME).toString();

        try {
            final String tagCodigo = transferObject.getAttribute(Columns.AGD_TAG_CODIGO).toString();
            final TipoAgendamentoEnum tipo = TipoAgendamentoEnum.recuperaTipoAgendamento(tagCodigo);
            int count = 0;
            do {
                if (count > 0) {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(dataPrevista);
                    if (TipoAgendamentoEnum.PERIODICO_DIARIO.equals(tipo)) {
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                    } else if (TipoAgendamentoEnum.PERIODICO_SEMANAL.equals(tipo)) {
                        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
                    } else if (TipoAgendamentoEnum.PERIODICO_MENSAL.equals(tipo)) {
                        cal.add(Calendar.MONTH, 1);
                    } else if (TipoAgendamentoEnum.PERIODICO_ANUAL.equals(tipo)) {
                        cal.add(Calendar.YEAR, 1);
                    }
                    dataPrevista = cal.getTime();
                }

                final String relCodigo = (String) transferObject.getAttribute(Columns.AGD_REL_CODIGO);
                final String agdCodigo = AgendamentoHome.create(descricao, dataCadastro, dataPrevista, classe, tagCodigo, status.getCodigo(), responsavel.getUsuCodigo(), relCodigo).getAgdCodigo();

                if ((parametros != null) && !parametros.isEmpty()) {
                    insereParametrosAgendamento(agdCodigo, parametros, responsavel);
                }

                insereOcorrencia(agdCodigo, CodedValues.TOC_INCLUSAO_AGENDAMENTO, null, null,
                        ApplicationResourcesHelper.getMessage("rotulo.inclusao.agendamento", responsavel), responsavel);

                final LogDelegate log = new LogDelegate(responsavel, Log.AGENDAMENTO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setAgendamento(agdCodigo);
                log.setStatusAgendamento(status.getCodigo());
                log.setTipoAgendamento(tipo.getCodigo());
                log.addChangedField(Columns.AGD_DESCRICAO, descricao);
                log.addChangedField(Columns.AGD_DATA_CADASTRO, dataCadastro);
                log.addChangedField(Columns.AGD_DATA_PREVISTA, dataPrevista);
                log.addChangedField(Columns.AGD_JAVA_CLASS_NAME, classe);
                log.write();

                // Se permite agendamento para o mesmo dia, e a data prevista passada é a data corrente, então dispara a execução do agendamento
                if (permiteAgendaParaMesmoDia && (dataPrevista.compareTo(DateHelper.getSystemDate()) == 0)) {
                    LOG.info("Agenda relatório para execução");
                    final Class<AbstractJob> clazz = (Class<AbstractJob>) Class.forName(classe);
                    Agendador.agendaTrabalho(agdCodigo, responsavel.getUsuCodigo(), clazz, TipoAgendamentoEnum.recuperaTipoAgendamento(tagCodigo));
                }
            } while (++count < periodicidade);

        } catch (AgendamentoControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException(ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AgendamentoControllerException("mensagem.erro.agendamento.incluir.agendamento", responsavel, ex);
        } catch (final ClassNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#cancelaAgendamento(String, AcessoSistema)
     */
    @Override
    public void cancelaAgendamento(String agdCodigo, AcessoSistema responsavel)
            throws AgendamentoControllerException {

        if (TextHelper.isNull(agdCodigo)) {
            throw new AgendamentoControllerException("mensagem.erro.agendamento.campos.nao.informados.cancelamento", responsavel);
        }

        try {
            final Agendamento agendamento = AgendamentoHome.findByPrimaryKey(agdCodigo);

            final StatusAgendamentoEnum statusAtual = StatusAgendamentoEnum.recuperaStatusAgendamento(agendamento.getStatusAgendamento().getSagCodigo());
            if (!StatusAgendamentoEnum.AGUARDANDO_EXECUCAO.equals(statusAtual) &&
                    !StatusAgendamentoEnum.EXECUCAO_DIARIA.equals(statusAtual)) {

                throw new AgendamentoControllerException("mensagem.erro.agendamento.situacao.nao.permite.cancelamento", responsavel);
            }

            final StatusAgendamentoEnum status = StatusAgendamentoEnum.CANCELADO;
            final StatusAgendamento statusAgendamento = StatusAgendamentoHome.findByPrimaryKey(status.getCodigo());
            agendamento.setStatusAgendamento(statusAgendamento);

            AbstractEntityHome.update(agendamento);

            insereOcorrencia(agdCodigo, CodedValues.TOC_CANCELAMENTO_AGENDAMENTO, null, null,
                    ApplicationResourcesHelper.getMessage("rotulo.cancelamento.agendamento", responsavel), responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.AGENDAMENTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setAgendamento(agdCodigo);
            log.setStatusAgendamento(status.getCodigo());
            log.write();

        } catch (final AgendamentoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException(ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.foi.possivel.cancelar", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#concluiAgendamento(String, Date, Date, AcessoSistema)
     */
    @Override
    public void concluiAgendamento(String agdCodigo, Date dataInicio, Date dataFim, AcessoSistema responsavel)
            throws AgendamentoControllerException {

        if (TextHelper.isNull(agdCodigo)) {
            throw new AgendamentoControllerException("mensagem.erro.agendamento.campos.nao.informados.conclusao", responsavel);
        }

        try {
            if (dataInicio == null) {
                dataInicio = new Date();
            }
            if (dataFim == null) {
                dataFim = new Date();
            }
            final Agendamento agendamento = AgendamentoHome.findByPrimaryKey(agdCodigo);

            final StatusAgendamentoEnum status = StatusAgendamentoEnum.CONCLUIDO;
            final StatusAgendamento statusAgendamento = StatusAgendamentoHome.findByPrimaryKey(status.getCodigo());
            agendamento.setStatusAgendamento(statusAgendamento);

            AbstractEntityHome.update(agendamento);

            insereOcorrencia(agdCodigo, CodedValues.TOC_CONCLUSAO_AGENDAMENTO, dataInicio, dataFim,
                    ApplicationResourcesHelper.getMessage("rotulo.conclusao.agendamento", responsavel), responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.AGENDAMENTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setAgendamento(agdCodigo);
            log.setStatusAgendamento(status.getCodigo());
            log.write();

        } catch (final AgendamentoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.encontrado", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.concluido", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstAgendamentos(List, List, List, String, String, String, String, AcessoSistema)
     */
    @Override
    public List<TransferObject> lstAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe,
            String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, AcessoSistema responsavel)
                    throws AgendamentoControllerException {

        return lstAgendamentos(agdCodigos, sagCodigos, tagCodigos, classe, tipoEntidade, codigoEntidade, usuCodigo, relCodigo, -1, -1, responsavel);
    }

    /**
     * @see AgendamentoController#lstAgendamentos(List, List, List, String, String, String, String, int, int, AcessoSistema)
     */
    @Override
    public List<TransferObject> lstAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe,
            String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, int offset, int count,
            AcessoSistema responsavel) throws AgendamentoControllerException {
        try {
            final ListaAgendamentosQuery query = new ListaAgendamentosQuery(agdCodigos, sagCodigos, tagCodigos, classe, tipoEntidade, codigoEntidade, usuCodigo, relCodigo);

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.listagem.agendamentos", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#countAgendamentos(List, List, List, String, String, String, String, AcessoSistema)
     */
    @Override
    public int countAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe, String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, AcessoSistema responsavel)
            throws AgendamentoControllerException {

        try {
            final ListaAgendamentosQuery query = new ListaAgendamentosQuery(agdCodigos, sagCodigos, tagCodigos, classe, tipoEntidade, codigoEntidade, usuCodigo, relCodigo);
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.count.agendamentos", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstAgendamentosParaExecucao(AcessoSistema)
     */
    @Override
    public List<TransferObject> lstAgendamentosParaExecucao(AcessoSistema responsavel) throws AgendamentoControllerException {
        try {
            final ListaAgendamentosParaExecucaoQuery query = new ListaAgendamentosParaExecucaoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.lista.agendamentos.execucao", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstAgendamentosInstantaneosParaExecucao(AcessoSistema)
     */
    @Override
    public List<TransferObject> lstAgendamentosInstantaneosParaExecucao(AcessoSistema responsavel) throws AgendamentoControllerException {
        try {
            final ListaAgendamentosInstantaneosParaExecucaoQuery query = new ListaAgendamentosInstantaneosParaExecucaoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.lista.agendamentos.execucao", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstParametrosAgendamento(String, AcessoSistema)
     */
    @Override
    public Map<String, List<String>> lstParametrosAgendamento(String agdCodigo, AcessoSistema responsavel) throws AgendamentoControllerException {
        try {
            final ListaParametrosAgendamentoQuery query = new ListaParametrosAgendamentoQuery(agdCodigo);
            final List<TransferObject> parametros = query.executarDTO();
            final Map<String, List<String>> retorno = new HashMap<>();

            for (final TransferObject to : parametros) {
                final String chave = to.getAttribute(Columns.PAG_NOME).toString();
                final String valor = to.getAttribute(Columns.PAG_VALOR).toString();
                List<String> valores = retorno.get(chave);
                if (valores == null) {
                    valores = new ArrayList<>();
                    retorno.put(chave, valores);
                }
                valores.add(valor);
            }

            return retorno;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.lista.param.agendamentos", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#findAgendamento(String, AcessoSistema)
     */
    @Override
    public TransferObject findAgendamento(String agdCodigo, AcessoSistema responsavel)
            throws AgendamentoControllerException {

        TransferObject to = null;

        if (TextHelper.isNull(agdCodigo)) {
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.encontrado", responsavel);
        }

        try {
            final Agendamento agendamento = AgendamentoHome.findByPrimaryKey(agdCodigo);

            if (agendamento != null) {
                to = new CustomTransferObject();
                to.setAttribute(Columns.AGD_CODIGO, agdCodigo);
                to.setAttribute(Columns.AGD_DESCRICAO, agendamento.getAgdDescricao());
                to.setAttribute(Columns.AGD_JAVA_CLASS_NAME, agendamento.getAgdJavaClassName());
                to.setAttribute(Columns.SAG_CODIGO, agendamento.getStatusAgendamento().getSagCodigo());
                to.setAttribute(Columns.TAG_CODIGO, agendamento.getTipoAgendamento().getTagCodigo());
                to.setAttribute(Columns.AGD_USU_CODIGO, agendamento.getUsuario().getUsuCodigo());
                to.setAttribute(Columns.AGD_DATA_CADASTRO, agendamento.getAgdDataCadastro());
                to.setAttribute(Columns.AGD_DATA_PREVISTA, agendamento.getAgdDataPrevista());
                to.setAttribute(Columns.AGD_REL_CODIGO, agendamento.getRelatorio() != null ? agendamento.getRelatorio().getRelCodigo() : null);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.AGENDAMENTO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setAgendamento(agdCodigo);
            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.encontrado", responsavel, ex);
        }

        return to;
    }

    /**
     * @see AgendamentoController#insereOcorrencia(String, String, Date, Date, String, AcessoSistema)
     */
    @Override
    public String insereOcorrencia(String agdCodigo, String tocCodigo, Date dataInicio, Date dataFim,
            String oagObs, AcessoSistema responsavel)
                    throws AgendamentoControllerException {
        String oagCodigo = null;

        if (TextHelper.isNull(agdCodigo) ||
                TextHelper.isNull(tocCodigo) ||
                TextHelper.isNull(oagObs)) {
            throw new AgendamentoControllerException("mensagem.erro.agendamento.campos.nao.informados.ocorrencia", responsavel);
        }

        try {
            if (dataInicio == null) {
                dataInicio = new Date();
            }
            if (dataFim == null) {
                dataFim = new Date();
            }

            oagCodigo = OcorrenciaAgendamentoHome.create(agdCodigo, tocCodigo, dataInicio, dataFim, oagObs, responsavel.getUsuCodigo(), responsavel.getIpUsuario());

        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AgendamentoControllerException("mensagem.erro.agendamento.nao.foi.possivel.incluir.ocorrencia", responsavel, ex);
        }

        return oagCodigo;
    }

    /**
     * Insere os parâmetros de um agendamento.
     *
     * @param agdCodigo Código do Agendamento
     * @param parametros Mapeamento de parâmetros e valores do Agendamento
     * @param responsavel Responsável pela operação
     * @throws AgendamentoControllerException
     */
    private void insereParametrosAgendamento(String agdCodigo, Map<String, List<String>> parametros,
            AcessoSistema responsavel) throws AgendamentoControllerException {

        if (TextHelper.isNull(agdCodigo)) {
            throw new AgendamentoControllerException("mensagem.erro.agendamento.campos.nao.informados.inclusao", responsavel);
        }

        try {
            final StringBuilder logObs = new StringBuilder();
            for (final Entry<String, List<String>> entry : parametros.entrySet()) {
                final List<String> valores = entry.getValue();
                for (final String valor : valores) {
                    final String nome = entry.getKey();
                    ParametroAgendamentoHome.create(agdCodigo, nome, valor);
                    logObs.append("[").append(nome).append("]=[").append(valor).append("] ");
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.PARAMETRO_AGENDAMENTO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setAgendamento(agdCodigo);
            log.add(logObs.toString());
            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AgendamentoControllerException("mensagem.erro.agendamento.inserir.parametros", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstTipoAgendamento(List, AcessoSistema)
     */
    @Override
    public List<TransferObject> lstTipoAgendamento(List<String> tagCodigos, AcessoSistema responsavel) throws AgendamentoControllerException {
        try {
            final ListaTipoAgendamentoQuery query = new ListaTipoAgendamentoQuery(tagCodigos);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.listagem.agendamentos", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#countOcorrenciaAgendamentoComErro(List, List, List, String, String, String, String, AcessoSistema)
     */
    @Override
    public int countOcorrenciaAgendamentoComErro(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, int horasLimite, AcessoSistema responsavel)
            throws AgendamentoControllerException {

        try {
            final ListaOcorrenciaAgendamentoComErroQuery query = new ListaOcorrenciaAgendamentoComErroQuery(agdCodigos, sagCodigos, tagCodigos, horasLimite);
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erro.agendamento.count.agendamentos", responsavel, ex);
        }
    }

    /**
     * @see AgendamentoController#lstOcorrenciaPorIntervalo(String, Date, Date, List, AcessoSistema)
     */
    @Override
    public List<String> lstOcorrenciaPorIntervalo(String agdCodigo, Date dataInicio, Date dataFim, List<String> tocCodigos, AcessoSistema responsavel) throws AgendamentoControllerException{
        try {
            final ListaOcorrenciaAgendamentoPorPeriodoQuery query = new ListaOcorrenciaAgendamentoPorPeriodoQuery(agdCodigo, tocCodigos, dataInicio, dataFim);
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> lstOcorrenciaSucessoPorIntervalo(String agdCodigo, Date dataInicio, Date dataFim, AcessoSistema responsavel) throws AgendamentoControllerException {
        final List<String> tocSucesso = new ArrayList<>();
        tocSucesso.add(CodedValues.TOC_PROCESSAMENTO_AGENDAMENTO);
        return lstOcorrenciaPorIntervalo(agdCodigo, dataInicio, dataFim, tocSucesso, responsavel);
    }

    @Override
    public void excluiHistoricoOcorrenciaAgendamentoExpiradaBySagCodigoByTagCodigo (List<String> sagCodigos, List<String> tagCodigos, String tocCodigo, int quantidadeDias, AcessoSistema responsavel) throws AgendamentoControllerException{
        try {
            OcorrenciaAgendamentoHome.deleteOcorrenciaByStatusByTipoExpiradas(sagCodigos, tagCodigos, tocCodigo, quantidadeDias);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AgendamentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
