package com.zetra.econsig.service.calendario;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CalendarioTO;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Calendario;
import com.zetra.econsig.persistence.entity.CalendarioBase;
import com.zetra.econsig.persistence.entity.CalendarioBaseHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCseHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEst;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEstHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrg;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrgHome;
import com.zetra.econsig.persistence.entity.CalendarioHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParamOrgao;
import com.zetra.econsig.persistence.query.admin.ListaCalendarioBaseOffsetQuery;
import com.zetra.econsig.persistence.query.admin.ListaCalendarioBaseQuery;
import com.zetra.econsig.persistence.query.admin.ListaCalendarioOffsetQuery;
import com.zetra.econsig.persistence.query.admin.ListaCalendarioQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseDataIniFimAntInvalidaQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstDataIniFimAntInvalidaQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgDataIniFimAntInvalidaQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaCseQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaEstQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaOrgQuery;
import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaQntPeriodosQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemMaxDiaCalendarioQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemMinDiaCalendarioQuery;
import com.zetra.econsig.persistence.query.calendario.ObtemProximoDiaUtilQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CalendarioControllerBean</p>
 * <p>Description: Session Bean para a rotina de manutenção de calendário, período, etc.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CalendarioControllerBean implements CalendarioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalendarioControllerBean.class);

    private static final int QUANTIDADE_PERIODOS = 12;

    @Autowired
    private ParametroController parametroController;

    /** MANUTENÇÃO DE CALENDÁRIO **/

    private CalendarioTO setCalendarioValues(Calendario calendarioBean) {
        CalendarioTO calendario = new CalendarioTO(calendarioBean.getCalData());
        calendario.setCalDescricao(calendarioBean.getCalDescricao());
        calendario.setCalDiaUtil(calendarioBean.getCalDiaUtil());
        return calendario;
    }

    private Calendario findCalendarioBean(CalendarioTO calendarioTO) throws CalendarioControllerException {
        Calendario calendarioBean = null;
        if (calendarioTO.getCalData() != null) {
            try {
                calendarioBean = CalendarioHome.findByPrimaryKey(calendarioTO.getCalData());
            } catch (FindException ex) {
                throw new CalendarioControllerException("mensagem.erro.calendario.nenhum.item.encontrado", (AcessoSistema) null);
            }
        }
        return calendarioBean;
    }

    @Override
    public CalendarioTO findCalendario(java.util.Date calData, AcessoSistema responsavel) throws CalendarioControllerException {
        return setCalendarioValues(findCalendarioBean(new CalendarioTO(calData)));
    }

    @Override
    public TransferObject findCalendarioBase(java.util.Date cabData, AcessoSistema responsavel) throws CalendarioControllerException {
        TransferObject calendarioBean = null;

        try {
            CalendarioBase calendarioBaseBean = CalendarioBaseHome.findByPrimaryKey(cabData);

            calendarioBean = new CustomTransferObject();
            calendarioBean.setAttribute(Columns.CAB_DATA, calendarioBaseBean.getCabData());
            calendarioBean.setAttribute(Columns.CAB_DESCRICAO, calendarioBaseBean.getCabDescricao());
            calendarioBean.setAttribute(Columns.CAB_DIA_UTIL, calendarioBaseBean.getCabDiaUtil());
        } catch (FindException ex) {
            throw new CalendarioControllerException("mensagem.erro.calendario.nenhum.item.encontrado", responsavel);
        }

        return calendarioBean;
    }

    @Override
    public void updateCalendario(CalendarioTO calendario, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            TransferObject calBaseCache = findCalendarioBase(calendario.getCalData(), responsavel);

            TransferObject calendarioBaseUpdate = new CustomTransferObject();
            calendarioBaseUpdate.setAttribute(Columns.CAB_DATA, calendario.getCalData());
            calendarioBaseUpdate.setAttribute(Columns.CAB_DESCRICAO, calendario.getCalDescricao());
            calendarioBaseUpdate.setAttribute(Columns.CAB_DIA_UTIL, calendario.getCalDiaUtil());

            LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_BASE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setCalendarioBase(DateHelper.format((Date) calBaseCache.getAttribute(Columns.CAB_DATA), "yyyy-MM-dd"));

            CustomTransferObject merge = log.getUpdatedFields(calendarioBaseUpdate.getAtributos(), calBaseCache.getAtributos());

            CalendarioBase calendarioBaseBean = new CalendarioBase();
            calendarioBaseBean.setCabData(calendario.getCalData());
            calendarioBaseBean.setCabDiaUtil(calendario.getCalDiaUtil());
            calendarioBaseBean.setCabDescricao(calendario.getCalDescricao());
            if (merge.getAtributos().containsKey(Columns.CAB_DESCRICAO)) {
                calendarioBaseBean.setCabDescricao((String) merge.getAttribute(Columns.CAB_DESCRICAO));
            }
            if (merge.getAtributos().containsKey(Columns.CAB_DIA_UTIL)) {
                calendarioBaseBean.setCabDiaUtil((String) merge.getAttribute(Columns.CAB_DIA_UTIL));
            }
            CalendarioBaseHome.update(calendarioBaseBean);
            log.write();

            try {
                Calendario calendarioBean = findCalendarioBean(calendario);
                log = new LogDelegate(responsavel, Log.CALENDARIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setCalendario(DateHelper.format(calendarioBean.getCalData(), "yyyy-MM-dd"));

                /* Compara a versão do cache com a passada por parâmetro */
                CalendarioTO calendarioCache = setCalendarioValues(calendarioBean);
                merge = log.getUpdatedFields(calendario.getAtributos(), calendarioCache.getAtributos());

                if (merge.getAtributos().containsKey(Columns.CAL_DESCRICAO)) {
                    calendarioBean.setCalDescricao((String) merge.getAttribute(Columns.CAL_DESCRICAO));
                }
                if (merge.getAtributos().containsKey(Columns.CAL_DIA_UTIL)) {
                    calendarioBean.setCalDiaUtil((String) merge.getAttribute(Columns.CAL_DIA_UTIL));
                }
                CalendarioHome.update(calendarioBean);
                log.write();
            } catch (CalendarioControllerException caex) {
                LOG.info("Data não presente na tabela calendário.");
            }
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<CalendarioTO> lstCalendario(TransferObject criterio, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            ListaCalendarioQuery query = new ListaCalendarioQuery();
            if (criterio != null) {
                if (criterio.getAttribute(Columns.CAL_DIA_UTIL) != null) {
                    query.calDiaUtil = criterio.getAttribute(Columns.CAL_DIA_UTIL).toString();
                }
                if (criterio.getAttribute("ANO-MES") != null) {
                    query.anoMes = criterio.getAttribute("ANO-MES").toString();
                }
            }
            return query.executarDTO(CalendarioTO.class);
        } catch (HQueryException ex) {
            throw new CalendarioControllerException(ex);
        }
    }

    /**
     * Lista registros de calendários na base maiores que a data dada por parâmetro
     * @param calData - data a partir da qual se recupera os registros
     * @param numDatasRecuperar - Limite de registros a retornar
     */
    @Override
    public List<Calendario> lstCalendariosAPartirDe(Date calData, boolean diasUteis, Integer numDatasRecuperar) throws CalendarioControllerException {
        try {
            return CalendarioHome.lstDatesFrom(calData, diasUteis, numDatasRecuperar);
        } catch (FindException e) {
            throw new CalendarioControllerException(e);
        }
    }

    @Override
    public List<CalendarioTO> lstCalendarioBase(TransferObject criterio, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            ListaCalendarioBaseQuery query = new ListaCalendarioBaseQuery();
            if (criterio != null) {
                if (criterio.getAttribute(Columns.CAB_DIA_UTIL) != null) {
                    query.cabDiaUtil = criterio.getAttribute(Columns.CAB_DIA_UTIL).toString();
                }
                if (criterio.getAttribute("ANO-MES") != null) {
                    query.anoMes = criterio.getAttribute("ANO-MES").toString();
                }
            }
            return query.executarDTO(CalendarioTO.class);
        } catch (HQueryException ex) {
            throw new CalendarioControllerException(ex);
        }
    }

    @Override
    public void atualizaCalendarioOffset(java.util.Date calData, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            ObtemMaxDiaCalendarioQuery maxCalendario = new ObtemMaxDiaCalendarioQuery();
            ObtemMinDiaCalendarioQuery minCalendario = new ObtemMinDiaCalendarioQuery();

            List<TransferObject> maxCalendarioBaseLst = maxCalendario.executarDTO();
            List<TransferObject> minCalendarioBaseLst = minCalendario.executarDTO();

            Date offsetInferior = DateHelper.addMonths(calData, -6);
            Date offsetSuperior = DateHelper.addMonths(calData, 6);

            HashSet<TransferObject> atualizacaoList = new HashSet<>();

            Date maxCalData = null;
            Date minCalData = null;
            try {
                maxCalData = (!TextHelper.isNull(maxCalendarioBaseLst.get(0).getAttribute(Columns.CAL_DATA))) ? DateHelper.parse(DateHelper.format((java.sql.Date) maxCalendarioBaseLst.get(0).getAttribute(Columns.CAL_DATA), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd") : null;
                minCalData = (!TextHelper.isNull(minCalendarioBaseLst.get(0).getAttribute(Columns.CAL_DATA))) ? DateHelper.parse(DateHelper.format((java.sql.Date) minCalendarioBaseLst.get(0).getAttribute(Columns.CAL_DATA), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd") : null;
            } catch (ParseException ex) {
                LOG.warn(ex.getMessage(), ex);
            }

            if ((maxCalData == null) && (minCalData == null)) {
                // significa que a tabela calendário está vazio, então faz primeira carga
                ListaCalendarioBaseOffsetQuery cabOffset = new ListaCalendarioBaseOffsetQuery();
                cabOffset.dateOffset = calData;
                cabOffset.mesDiff = true;
                cabOffset.diffOffset = 6;

                List<TransferObject> updateSuperior = cabOffset.executarDTO();

                // para definir limite inferior do calendário a partir da data corrente deve-se definir o offset a partir tb_calendario,
                // pois não se sabe como este estará em cada base de sistema.
                cabOffset = new ListaCalendarioBaseOffsetQuery();
                cabOffset.mesDiff = true;
                cabOffset.diffOffset = -6;
                cabOffset.dateOffset = DateHelper.addDays(calData, -1);
                List<TransferObject> updateInferior = cabOffset.executarDTO();
                atualizacaoList.addAll(updateSuperior);
                atualizacaoList.addAll(updateInferior);
            } else {
                int diffOffsetSuperior = DateHelper.dayDiff(offsetSuperior, maxCalData);

                ListaCalendarioBaseOffsetQuery cabOffsetSup = new ListaCalendarioBaseOffsetQuery();
                cabOffsetSup.dateOffset = maxCalData;
                cabOffsetSup.dayDiff = true;
                cabOffsetSup.diffOffset = diffOffsetSuperior;
                List<TransferObject> updateSuperior = cabOffsetSup.executarDTO();

                if (diffOffsetSuperior > 0) {
                    Iterator<TransferObject> cabIt = updateSuperior.iterator();
                    cabIt.next();

                    while (cabIt.hasNext()) {
                        TransferObject atualizacao = cabIt.next();
                        atualizacaoList.add(atualizacao);
                    }
                } else if (diffOffsetSuperior < 0) {
                    // diff superior negativo, significa que registros devem ser removidos da tb_calendario
                    // ignora o primeiro
                    for (TransferObject calendario : updateSuperior) {
                        try {
                            Calendario calRemocao = CalendarioHome.findByPrimaryKey((Date) calendario.getAttribute(Columns.CAB_DATA));
                            CalendarioHome.remove(calRemocao);
                        } catch (FindException e) {
                            continue;
                        }
                    }
                }

                int diffOffsetInferior = DateHelper.dayDiff(offsetInferior, minCalData);

                // para definir limite inferior do calendário a partir da data corrente deve-se definir o offset a partir tb_calendario,
                // pois não se sabe como este estará em cada base de sistema.
                ListaCalendarioOffsetQuery calOffsetInf = new ListaCalendarioOffsetQuery();
                calOffsetInf.dateOffset = minCalData;
                calOffsetInf.dayDiff = true;
                calOffsetInf.diffOffset = diffOffsetInferior;
                List<TransferObject> updateInferior = calOffsetInf.executarDTO();

                if (diffOffsetInferior > 0) {
                    // diff inferior positivo, significa que registros devem ser removidos da tb_calendario
                    // ignora o primeiro
                    for (TransferObject calendario : updateInferior) {
                        try {
                            Calendario calRemocao = CalendarioHome.findByPrimaryKey((Date) calendario.getAttribute(Columns.CAL_DATA));
                            CalendarioHome.remove(calRemocao);
                        } catch (FindException e) {
                            continue;
                        }
                    }
                } else if (diffOffsetInferior < 0) {
                    Iterator<TransferObject> cabIt = updateInferior.iterator();
                    cabIt.next();

                    while (cabIt.hasNext()) {
                        TransferObject atualizacao = cabIt.next();
                        atualizacaoList.add(atualizacao);
                    }
                }
            }

            if (!atualizacaoList.isEmpty()) {
                for (TransferObject calendario : atualizacaoList) {
                    if (!TextHelper.isNull(calendario.getAttribute(Columns.CAB_DATA))) {
                        CalendarioHome.create((Date) calendario.getAttribute(Columns.CAB_DATA), (String) calendario.getAttribute(Columns.CAB_DESCRICAO), (String) calendario.getAttribute(Columns.CAB_DIA_UTIL));
                    } else {
                        CalendarioHome.create((Date) calendario.getAttribute(Columns.CAL_DATA), (String) calendario.getAttribute(Columns.CAL_DESCRICAO), (String) calendario.getAttribute(Columns.CAL_DIA_UTIL));
                    }
                }
            }
        } catch (HQueryException | RemoveException | CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** MANUTENÇÃO DE CALENDÁRIO FOLHA **/

    @Override
    public boolean existeCalendarioFolhaCse(AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
            query.count = true;
            query.cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
            query.cfcDataFimMaiorQue = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd HH:mm:ss");
            return query.executarContador() > 0;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista os registros de calendário folha, de acordo com o tipo de entidade,
     * para o ano especificado por parâmetro.
     * @param ano
     * @param tipoEntidade
     * @param codigoEntidade
     * @param responsavel
     * @return
     * @throws CalendarioControllerException
     */
    @Override
    public Map<Integer, TransferObject> lstCalendarioFolhaAno(Integer ano, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            Map<Integer, TransferObject> mapaMesPeriodo = new HashMap<>();
            List<TransferObject> lstCalendario = null;

            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
                query.cseCodigo = codigoEntidade;
                query.anoPeriodo = ano;
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
                query.estCodigo = codigoEntidade;
                query.anoPeriodo = ano;
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
                query.orgCodigo = codigoEntidade;
                query.anoPeriodo = ano;
                lstCalendario = query.executarDTO();
            }

            for (TransferObject calendario : lstCalendario) {
                Integer numPeriodo = null;

                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                    numPeriodo = calendario.getAttribute(Columns.CFC_NUM_PERIODO) != null ? Integer.valueOf(calendario.getAttribute(Columns.CFC_NUM_PERIODO).toString()) : null;
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                    numPeriodo = calendario.getAttribute(Columns.CFE_NUM_PERIODO) != null ? Integer.valueOf(calendario.getAttribute(Columns.CFE_NUM_PERIODO).toString()) : null;
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                    numPeriodo = calendario.getAttribute(Columns.CFO_NUM_PERIODO) != null ? Integer.valueOf(calendario.getAttribute(Columns.CFO_NUM_PERIODO).toString()) : null;
                }

                if (numPeriodo != null) {
                    mapaMesPeriodo.put(numPeriodo, calendario);
                }
            }

            return mapaMesPeriodo;
        } catch (HQueryException ex) {
            throw new CalendarioControllerException(ex);
        }
    }

    /**
     * Lista os registros de calendário folha, de acordo com o tipo de entidade,
     * para todos os anos a partir da data do corte atual
     * @param tipoEntidade
     * @param codigoEntidade
     * @param alteraPeriodoVigente
     * @param responsavel
     * @return
     * @throws CalendarioControllerException
     */
    @Override
    public void updateTodosCalendarioFolha(Integer novoDiaCorte, String tipoEntidade, String codigoEntidade, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            List<TransferObject> lstCalendario = null;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Busca todos os registros que tem data fim após hoje
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
                query.cseCodigo = codigoEntidade;
                query.cfcDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
                query.estCodigo = codigoEntidade;
                query.cfeDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
                query.orgCodigo = codigoEntidade;
                query.cfoDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            }

            Date periodoAtual = null;
            try {
                periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
            } catch (PeriodoException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            Date novaDataInicio = null;
            ArrayList<TransferObject> calendarioAtualizar = new ArrayList<>();

            for (TransferObject calendario : lstCalendario) {
                Date dataInicio = null;
                Date periodo = null;
                Integer diaCorte = novoDiaCorte;

                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                    dataInicio = (Date) calendario.getAttribute(Columns.CFC_DATA_INI);
                    periodo = (Date) calendario.getAttribute(Columns.CFC_PERIODO);
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                    dataInicio = (Date) calendario.getAttribute(Columns.CFE_DATA_INI);
                    periodo = (Date) calendario.getAttribute(Columns.CFE_PERIODO);
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                    dataInicio = (Date) calendario.getAttribute(Columns.CFO_DATA_INI);
                    periodo = (Date) calendario.getAttribute(Columns.CFO_PERIODO);
                }

                // Se a data inicio é posterior ao dia de hoje atualiza
                if (dataInicio.after(new Date()) || (alteraPeriodoVigente && periodo.equals(periodoAtual))) {
                    // verifica se a data existe naquele mês, caso não coloca a maior data do mês
                    GregorianCalendar dataNova = new GregorianCalendar();
                    dataNova.setTime(periodo);
                    if (diaCorte > dataNova.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        diaCorte = dataNova.getActualMaximum(Calendar.DAY_OF_MONTH);
                    }

                    dataNova.set(Calendar.DAY_OF_MONTH, diaCorte);
                    dataNova.set(Calendar.HOUR_OF_DAY, 23);
                    dataNova.set(Calendar.MINUTE, 59);
                    dataNova.set(Calendar.SECOND, 59);

                    // seta novo dia de corte
                    if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                        calendario.setAttribute(Columns.CFC_DIA_CORTE, diaCorte.shortValue());
                    } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                        calendario.setAttribute(Columns.CFE_DIA_CORTE, diaCorte.shortValue());
                    } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                        calendario.setAttribute(Columns.CFO_DIA_CORTE, diaCorte.shortValue());
                    }

                    // seta nova data fim
                    if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                        calendario.setAttribute(Columns.CFC_DATA_FIM, dataNova.getTime());
                    } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                        calendario.setAttribute(Columns.CFE_DATA_FIM, dataNova.getTime());
                    } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                        calendario.setAttribute(Columns.CFO_DATA_FIM, dataNova.getTime());
                    }

                    // seta nova data fim para ajustes
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                        Date dataFimAjustes = null;
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            dataFimAjustes = (Date) calendario.getAttribute(Columns.CFC_DATA_FIM_AJUSTES);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            dataFimAjustes = (Date) calendario.getAttribute(Columns.CFE_DATA_FIM_AJUSTES);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            dataFimAjustes = (Date) calendario.getAttribute(Columns.CFO_DATA_FIM_AJUSTES);
                        }
                        if (dataFimAjustes == null || dataFimAjustes.before(dataNova.getTime())) {
                            dataFimAjustes = dataNova.getTime();
                        }
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            calendario.setAttribute(Columns.CFC_DATA_FIM_AJUSTES, dataFimAjustes);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            calendario.setAttribute(Columns.CFE_DATA_FIM_AJUSTES, dataFimAjustes);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            calendario.setAttribute(Columns.CFO_DATA_FIM_AJUSTES, dataFimAjustes);
                        }
                    }

                    // seta nova data prevista do retorno
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                        Date dataPrevistaRetorno = null;
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            dataPrevistaRetorno = (Date) calendario.getAttribute(Columns.CFC_DATA_PREVISTA_RETORNO);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            dataPrevistaRetorno = (Date) calendario.getAttribute(Columns.CFE_DATA_PREVISTA_RETORNO);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            dataPrevistaRetorno = (Date) calendario.getAttribute(Columns.CFO_DATA_PREVISTA_RETORNO);
                        }
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            calendario.setAttribute(Columns.CFC_DATA_PREVISTA_RETORNO, dataPrevistaRetorno);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            calendario.setAttribute(Columns.CFE_DATA_PREVISTA_RETORNO, dataPrevistaRetorno);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            calendario.setAttribute(Columns.CFO_DATA_PREVISTA_RETORNO, dataPrevistaRetorno);
                        }
                    }

                    // seta a data inicial que é o proximo dia da data fim anterior
                    if (novaDataInicio != null) {
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            calendario.setAttribute(Columns.CFC_DATA_INI, novaDataInicio);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            calendario.setAttribute(Columns.CFE_DATA_INI, novaDataInicio);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            calendario.setAttribute(Columns.CFO_DATA_INI, novaDataInicio);
                        }
                    }

                    // Seta data inicio fiscal
                    if (ParamSist.paramEquals(CodedValues.TPC_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO, CodedValues.TPC_SIM, responsavel)) {
                        Date dataIniFiscal = null;
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            dataIniFiscal = (Date) calendario.getAttribute(Columns.CFC_DATA_INI_FISCAL);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            dataIniFiscal = (Date) calendario.getAttribute(Columns.CFE_DATA_INI_FISCAL);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            dataIniFiscal = (Date) calendario.getAttribute(Columns.CFO_DATA_INI_FISCAL);
                        }
                        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                            calendario.setAttribute(Columns.CFC_DATA_INI_FISCAL, dataIniFiscal);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                            calendario.setAttribute(Columns.CFE_DATA_INI_FISCAL, dataIniFiscal);
                        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                            calendario.setAttribute(Columns.CFO_DATA_INI_FISCAL, dataIniFiscal);
                        }
                    }

                    // Soma um dia que sera a data inicial do proximo periodo
                    dataNova.add(Calendar.DAY_OF_MONTH, 1);
                    dataNova.set(Calendar.HOUR_OF_DAY, 0);
                    dataNova.set(Calendar.MINUTE, 0);
                    dataNova.set(Calendar.SECOND, 0);
                    novaDataInicio = dataNova.getTime();

                    calendarioAtualizar.add(calendario);
                }
            }

            // atualiza registros
            updateCalendarioFolha(calendarioAtualizar, new ArrayList<TransferObject>(), tipoEntidade, codigoEntidade, alteraPeriodoVigente, responsavel);

        } catch (HQueryException ex) {
            throw new CalendarioControllerException(ex);
        }
    }

    /**
     * Atualiza os registros de calendário folha, de acordo com o tipo de entidade.
     * Faz a alteração/inclusão dos registros presentes em lstCalendarioFolhaAlteracao
     * e a exclusão dos registros presentes em lstCalendarioFolhaExclusao.
     * @param lstCalendarioFolhaAlteracao
     * @param lstCalendarioFolhaExclusao
     * @param tipoEntidade
     * @param codigoEntidade
     * @param alteraPeriodoVigente
     * @param responsavel
     * @throws CalendarioControllerException
     */
    @Override
    public void updateCalendarioFolha(List<TransferObject> lstCalendarioFolhaAlteracao, List<TransferObject> lstCalendarioFolhaExclusao, String tipoEntidade, String codigoEntidade, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            boolean habilitaAmbienteDeTestes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel);

            if (alteraPeriodoVigente && !habilitaAmbienteDeTestes) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.calendario.folha.alteracao.periodo.atual.invalida", responsavel));
                throw new CalendarioControllerException("mensagem.erro.calendario.folha.alteracao.periodo.atual.invalida", responsavel);
            }

            List<LogDelegate> logs = new ArrayList<>();
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                logs.addAll(updateCalendarioFolhaCse(lstCalendarioFolhaAlteracao, codigoEntidade, alteraPeriodoVigente, responsavel));
                logs.addAll(removeCalendarioFolhaCse(lstCalendarioFolhaExclusao, codigoEntidade, responsavel));
                validarCalendarioFolhaCse(codigoEntidade, responsavel);
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                logs.addAll(updateCalendarioFolhaEst(lstCalendarioFolhaAlteracao, codigoEntidade, alteraPeriodoVigente, responsavel));
                logs.addAll(removeCalendarioFolhaEst(lstCalendarioFolhaExclusao, codigoEntidade, responsavel));
                validarCalendarioFolhaEst(codigoEntidade, responsavel);
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                logs.addAll(updateCalendarioFolhaOrg(lstCalendarioFolhaAlteracao, codigoEntidade, alteraPeriodoVigente, responsavel));
                logs.addAll(removeCalendarioFolhaOrg(lstCalendarioFolhaExclusao, codigoEntidade, responsavel));
                validarCalendarioFolhaOrg(codigoEntidade, responsavel);
            }
            // Caso não tenham ocorrido erros, grava os logs gerados
            for (LogDelegate log : logs) {
                log.write();
            }
        } catch (CalendarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (LogControllerException | UpdateException | CreateException | RemoveException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<LogDelegate> updateCalendarioFolhaCse(List<TransferObject> lstCalendarioFolha, String cseCodigo, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, UpdateException, CreateException {
        /**
         * DESENV-15168: Período atual é tratado aqui para que caso não exista período previamente cadastrado, será cadastrado um novo período.
         */
        Date periodoAtual = null;
        try {
            periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfcPeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFC_PERIODO));
            Short cfcDiaCorte = (Short) calendarioFolha.getAttribute(Columns.CFC_DIA_CORTE);
            Date cfcDataIni = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_INI);
            Date cfcDataFim = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_FIM);
            Date cfcDataFimAjustes = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_FIM_AJUSTES);
            String cfcApenasReducoes = (String) calendarioFolha.getAttribute(Columns.CFC_APENAS_REDUCOES);
            Date cfcDataPrevistaRetorno = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_PREVISTA_RETORNO);
            Date cfcDataIniFiscal = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_INI_FISCAL);
            Date cfcDataFimFiscal = (Date) calendarioFolha.getAttribute(Columns.CFC_DATA_FIM_FISCAL);
            Short cfcNumPeriodo = calendarioFolha.getAttribute(Columns.CFC_NUM_PERIODO) != null ? Short.valueOf(calendarioFolha.getAttribute(Columns.CFC_NUM_PERIODO).toString()) : 0;

            boolean alterouDiaCorte = false;

            if (validarCalendarioFolha(periodoAtual, cfcPeriodo, cfcDataIni, cfcDataFim, cfcDataFimAjustes, cfcDiaCorte, alteraPeriodoVigente, responsavel)) {
                try {
                    // Procura o registro e caso exista, atualiza os dados
                    CalendarioFolhaCse cfc = CalendarioFolhaCseHome.findByPrimaryKey(cseCodigo, cfcPeriodo);

                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_CSE, Log.UPDATE, Log.LOG_INFORMACAO);
                    TransferObject cfcCache = setCalendarioFolhaCseValues(cfc);
                    TransferObject merge = log.getUpdatedFields(calendarioFolha.getAtributos(), cfcCache.getAtributos());
                    log.setCfcPeriodo(DateHelper.toPeriodString(cfcPeriodo));
                    log.setConsignante(cseCodigo);

                    if (!merge.getAtributos().isEmpty()) {
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_INI)) {
                            cfc.setCfcDataIni(cfcDataIni);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_FIM)) {
                            cfc.setCfcDataFim(cfcDataFim);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_FIM_AJUSTES)) {
                            cfc.setCfcDataFimAjustes(cfcDataFimAjustes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DIA_CORTE)) {
                            cfc.setCfcDiaCorte(cfcDiaCorte);
                            alterouDiaCorte = true;
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_APENAS_REDUCOES)) {
                            cfc.setCfcApenasReducoes(cfcApenasReducoes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_PREVISTA_RETORNO)) {
                            cfc.setCfcDataPrevistaRetorno(cfcDataPrevistaRetorno);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_INI_FISCAL)) {
                            cfc.setCfcDataIniFiscal(cfcDataIniFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_DATA_FIM_FISCAL)) {
                            cfc.setCfcDataFimFiscal(cfcDataFimFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFC_NUM_PERIODO)) {
                            cfc.setCfcNumPeriodo(cfcNumPeriodo);
                        }
                        // Se algo foi alterado neste registro, salva as alterações
                        // e grava o registro de log da operação.
                        CalendarioFolhaCseHome.update(cfc);
                        logs.add(log);
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_EMAIL_ALERTA_CSA_DATA_CORTE_ALTERADA, responsavel) && cfcPeriodo.equals(periodoAtual) && alterouDiaCorte) {
                        EnviaEmailHelper.enviarEmailDataCorteAlterada(cfc.getCfcDiaCorte().toString(), periodoAtual, responsavel);
                    }

                } catch (FindException ex) {
                    // Caso não exista, cria o novo registro de calendário folha
                    CalendarioFolhaCseHome.create(cseCodigo, cfcPeriodo, cfcDiaCorte, cfcDataIni, cfcDataFim, cfcDataFimAjustes, cfcApenasReducoes, cfcDataPrevistaRetorno, cfcDataIniFiscal, cfcDataFimFiscal, cfcNumPeriodo);

                    // Salva o log de criação do registro
                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_CSE, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setCfcPeriodo(DateHelper.toPeriodString(cfcPeriodo));
                    log.setConsignante(cseCodigo);
                    log.getUpdatedFields(calendarioFolha.getAtributos(), null);
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    private List<LogDelegate> removeCalendarioFolhaCse(List<TransferObject> lstCalendarioFolha, String cseCodigo, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, RemoveException {
        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfcPeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFC_PERIODO));

            try {
                // Procura o registro e caso exista, atualiza os dados
                CalendarioFolhaCse cfc = CalendarioFolhaCseHome.findByPrimaryKey(cseCodigo, cfcPeriodo);
                CalendarioFolhaCseHome.remove(cfc);

                // Salva o log de criação do registro
                LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_CSE, Log.DELETE, Log.LOG_INFORMACAO);
                log.setCfcPeriodo(DateHelper.toPeriodString(cfcPeriodo));
                log.setConsignante(cseCodigo);
                log.getDeletedFields(calendarioFolha.getAtributos());
                logs.add(log);
            } catch (FindException ex) {
                // Caso não exista, não tem problema, pois era para ser removido
            }
        }
        return logs;
    }

    private void validarCalendarioFolhaCse(String cseCodigo, AcessoSistema responsavel) throws CalendarioControllerException, HQueryException {
        ListaCalFolhaCseDataIniFimAntInvalidaQuery query = new ListaCalFolhaCseDataIniFimAntInvalidaQuery();
        query.cseCodigo = cseCodigo;
        gerarExcecaoDataIniFimInvalida(query.executarLista());
    }

    private TransferObject setCalendarioFolhaCseValues(CalendarioFolhaCse cfc) {
        TransferObject cfcTO = new CustomTransferObject();
        cfcTO.setAttribute(Columns.CFC_PERIODO, cfc.getCfcPeriodo());
        cfcTO.setAttribute(Columns.CFC_CSE_CODIGO, cfc.getCseCodigo());
        cfcTO.setAttribute(Columns.CFC_DATA_INI, cfc.getCfcDataIni());
        cfcTO.setAttribute(Columns.CFC_DATA_FIM, cfc.getCfcDataFim());
        cfcTO.setAttribute(Columns.CFC_DATA_FIM_AJUSTES, cfc.getCfcDataFimAjustes());
        cfcTO.setAttribute(Columns.CFC_DIA_CORTE, cfc.getCfcDiaCorte());
        cfcTO.setAttribute(Columns.CFC_APENAS_REDUCOES, cfc.getCfcApenasReducoes());
        cfcTO.setAttribute(Columns.CFC_DATA_PREVISTA_RETORNO, cfc.getCfcDataPrevistaRetorno());
        return cfcTO;
    }

    private List<LogDelegate> updateCalendarioFolhaEst(List<TransferObject> lstCalendarioFolha, String estCodigo, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, UpdateException, CreateException {
        /**
         * DESENV-15168: Período atual é tratado aqui para que caso não exista período previamente cadastrado, será cadastrado um novo período.
         */
        Date periodoAtual = null;
        try {
            periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfePeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFE_PERIODO));
            Short cfeDiaCorte = (Short) calendarioFolha.getAttribute(Columns.CFE_DIA_CORTE);
            Date cfeDataIni = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_INI);
            Date cfeDataFim = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_FIM);
            Date cfeDataFimAjustes = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_FIM_AJUSTES);
            String cfeApenasReducoes = (String) calendarioFolha.getAttribute(Columns.CFE_APENAS_REDUCOES);
            Date cfeDataPrevistaRetorno = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_PREVISTA_RETORNO);
            Date cfeDataIniFiscal = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_INI_FISCAL);
            Date cfeDataFimFiscal = (Date) calendarioFolha.getAttribute(Columns.CFE_DATA_FIM_FISCAL);
            Short cfeNumPeriodo = calendarioFolha.getAttribute(Columns.CFE_NUM_PERIODO) != null ? Short.valueOf(calendarioFolha.getAttribute(Columns.CFE_NUM_PERIODO).toString()) : 0;

            boolean alterouDiaCorte = false;

            if (validarCalendarioFolha(periodoAtual, cfePeriodo, cfeDataIni, cfeDataFim, cfeDataFimAjustes, cfeDiaCorte, alteraPeriodoVigente, responsavel)) {
                try {
                    // Procura o registro e caso exista, atualiza os dados
                    CalendarioFolhaEst cfe = CalendarioFolhaEstHome.findByPrimaryKey(estCodigo, cfePeriodo);

                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_EST, Log.UPDATE, Log.LOG_INFORMACAO);
                    TransferObject cfeCache = setCalendarioFolhaEstValues(cfe);
                    TransferObject merge = log.getUpdatedFields(calendarioFolha.getAtributos(), cfeCache.getAtributos());
                    log.setCfePeriodo(DateHelper.toPeriodString(cfePeriodo));
                    log.setEstabelecimento(estCodigo);

                    if (!merge.getAtributos().isEmpty()) {
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_INI)) {
                            cfe.setCfeDataIni(cfeDataIni);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_FIM)) {
                            cfe.setCfeDataFim(cfeDataFim);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_FIM_AJUSTES)) {
                            cfe.setCfeDataFimAjustes(cfeDataFimAjustes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DIA_CORTE)) {
                            cfe.setCfeDiaCorte(cfeDiaCorte);
                            alterouDiaCorte = true;
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_APENAS_REDUCOES)) {
                            cfe.setCfeApenasReducoes(cfeApenasReducoes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_PREVISTA_RETORNO)) {
                            cfe.setCfeDataPrevistaRetorno(cfeDataPrevistaRetorno);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_INI_FISCAL)) {
                            cfe.setCfeDataIniFiscal(cfeDataIniFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_DATA_FIM_FISCAL)) {
                            cfe.setCfeDataFimFiscal(cfeDataFimFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFE_NUM_PERIODO)) {
                            cfe.setCfeNumPeriodo(cfeNumPeriodo);
                        }
                        // Se algo foi alterado neste registro, salva as alterações
                        // e grava o registro de log da operação.
                        CalendarioFolhaEstHome.update(cfe);
                        logs.add(log);
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_EMAIL_ALERTA_CSA_DATA_CORTE_ALTERADA, responsavel) && cfePeriodo.equals(periodoAtual) && alterouDiaCorte) {
                        EnviaEmailHelper.enviarEmailDataCorteAlterada(cfe.getCfeDiaCorte().toString(), periodoAtual, responsavel);
                    }

                } catch (FindException ex) {
                    // Caso não exista, cria o novo registro de calendário folha
                    CalendarioFolhaEstHome.create(estCodigo, cfePeriodo, cfeDiaCorte, cfeDataIni, cfeDataFim, cfeDataFimAjustes, cfeApenasReducoes, cfeDataPrevistaRetorno, cfeDataIniFiscal, cfeDataFimFiscal, cfeNumPeriodo);

                    // Salva o log de criação do registro
                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_EST, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setCfePeriodo(DateHelper.toPeriodString(cfePeriodo));
                    log.setEstabelecimento(estCodigo);
                    log.getUpdatedFields(calendarioFolha.getAtributos(), null);
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    private List<LogDelegate> removeCalendarioFolhaEst(List<TransferObject> lstCalendarioFolha, String estCodigo, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, RemoveException {
        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfePeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFE_PERIODO));

            try {
                // Procura o registro e caso exista, atualiza os dados
                CalendarioFolhaEst cfe = CalendarioFolhaEstHome.findByPrimaryKey(estCodigo, cfePeriodo);
                CalendarioFolhaEstHome.remove(cfe);

                // Salva o log de criação do registro
                LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_EST, Log.DELETE, Log.LOG_INFORMACAO);
                log.setCfePeriodo(DateHelper.toPeriodString(cfePeriodo));
                log.setEstabelecimento(estCodigo);
                log.getDeletedFields(calendarioFolha.getAtributos());
                logs.add(log);
            } catch (FindException ex) {
                // Caso não exista, não tem problema, pois era para ser removido
            }
        }
        return logs;
    }

    private void validarCalendarioFolhaEst(String estCodigo, AcessoSistema responsavel) throws CalendarioControllerException, HQueryException {
        ListaCalFolhaEstDataIniFimAntInvalidaQuery query = new ListaCalFolhaEstDataIniFimAntInvalidaQuery();
        query.estCodigo = estCodigo;
        gerarExcecaoDataIniFimInvalida(query.executarLista());
    }

    private TransferObject setCalendarioFolhaEstValues(CalendarioFolhaEst cfe) {
        TransferObject cfeTO = new CustomTransferObject();
        cfeTO.setAttribute(Columns.CFE_PERIODO, cfe.getCfePeriodo());
        cfeTO.setAttribute(Columns.CFE_EST_CODIGO, cfe.getEstCodigo());
        cfeTO.setAttribute(Columns.CFE_DATA_INI, cfe.getCfeDataIni());
        cfeTO.setAttribute(Columns.CFE_DATA_FIM, cfe.getCfeDataFim());
        cfeTO.setAttribute(Columns.CFE_DATA_FIM_AJUSTES, cfe.getCfeDataFimAjustes());
        cfeTO.setAttribute(Columns.CFE_DIA_CORTE, cfe.getCfeDiaCorte());
        cfeTO.setAttribute(Columns.CFE_APENAS_REDUCOES, cfe.getCfeApenasReducoes());
        cfeTO.setAttribute(Columns.CFE_DATA_PREVISTA_RETORNO, cfe.getCfeDataPrevistaRetorno());
        return cfeTO;
    }

    private List<LogDelegate> updateCalendarioFolhaOrg(List<TransferObject> lstCalendarioFolha, String orgCodigo, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, UpdateException, CreateException {
        /**
         * DESENV-15168: Período atual é tratado aqui para que caso não exista período previamente cadastrado, será cadastrado um novo período.
         */
        Date periodoAtual = null;
        try {
            periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfoPeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFO_PERIODO));
            Short cfoDiaCorte = (Short) calendarioFolha.getAttribute(Columns.CFO_DIA_CORTE);
            Date cfoDataIni = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_INI);
            Date cfoDataFim = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_FIM);
            Date cfoDataFimAjustes = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_FIM_AJUSTES);
            String cfoApenasReducoes = (String) calendarioFolha.getAttribute(Columns.CFO_APENAS_REDUCOES);
            Date cfoDataPrevistaRetorno = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_PREVISTA_RETORNO);
            Date cfoDataIniFiscal = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_INI_FISCAL);
            Date cfoDataFimFiscal = (Date) calendarioFolha.getAttribute(Columns.CFO_DATA_FIM_FISCAL);
            Short cfoNumPeriodo = calendarioFolha.getAttribute(Columns.CFO_NUM_PERIODO) != null ? Short.valueOf(calendarioFolha.getAttribute(Columns.CFO_NUM_PERIODO).toString()) : 0;

            boolean alterouDiaCorte = false;

            if (validarCalendarioFolha(periodoAtual, cfoPeriodo, cfoDataIni, cfoDataFim, cfoDataFimAjustes, cfoDiaCorte, alteraPeriodoVigente, responsavel)) {
                try {
                    // Procura o registro e caso exista, atualiza os dados
                    CalendarioFolhaOrg cfo = CalendarioFolhaOrgHome.findByPrimaryKey(orgCodigo, cfoPeriodo);

                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_ORG, Log.UPDATE, Log.LOG_INFORMACAO);
                    TransferObject cfoCache = setCalendarioFolhaOrgValues(cfo);
                    TransferObject merge = log.getUpdatedFields(calendarioFolha.getAtributos(), cfoCache.getAtributos());
                    log.setCfoPeriodo(DateHelper.toPeriodString(cfoPeriodo));
                    log.setOrgao(orgCodigo);

                    if (!merge.getAtributos().isEmpty()) {
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_INI)) {
                            cfo.setCfoDataIni(cfoDataIni);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_FIM)) {
                            cfo.setCfoDataFim(cfoDataFim);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_FIM_AJUSTES)) {
                            cfo.setCfoDataFimAjustes(cfoDataFimAjustes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DIA_CORTE)) {
                            cfo.setCfoDiaCorte(cfoDiaCorte);
                            alterouDiaCorte = true;
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_APENAS_REDUCOES)) {
                            cfo.setCfoApenasReducoes(cfoApenasReducoes);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_PREVISTA_RETORNO)) {
                            cfo.setCfoDataPrevistaRetorno(cfoDataPrevistaRetorno);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_INI_FISCAL)) {
                            cfo.setCfoDataIniFiscal(cfoDataIniFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_DATA_FIM_FISCAL)) {
                            cfo.setCfoDataFimFiscal(cfoDataFimFiscal);
                        }
                        if (merge.getAtributos().containsKey(Columns.CFO_NUM_PERIODO)) {
                            cfo.setCfoNumPeriodo(cfoNumPeriodo);
                        }
                        // Se algo foi alterado neste registro, salva as alterações
                        // e grava o registro de log da operação.
                        CalendarioFolhaOrgHome.update(cfo);
                        logs.add(log);
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_EMAIL_ALERTA_CSA_DATA_CORTE_ALTERADA, responsavel) && cfoPeriodo.equals(periodoAtual) && alterouDiaCorte) {
                        EnviaEmailHelper.enviarEmailDataCorteAlterada(cfo.getCfoDiaCorte().toString(), periodoAtual, responsavel);
                    }

                } catch (FindException ex) {
                    // Caso não exista, cria o novo registro de calendário folha
                    CalendarioFolhaOrgHome.create(orgCodigo, cfoPeriodo, cfoDiaCorte, cfoDataIni, cfoDataFim, cfoDataFimAjustes, cfoApenasReducoes, cfoDataPrevistaRetorno, cfoDataIniFiscal, cfoDataFimFiscal, cfoNumPeriodo);

                    // Salva o log de criação do registro
                    LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_ORG, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setCfoPeriodo(DateHelper.toPeriodString(cfoPeriodo));
                    log.setOrgao(orgCodigo);
                    log.getUpdatedFields(calendarioFolha.getAtributos(), null);
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    private List<LogDelegate> removeCalendarioFolhaOrg(List<TransferObject> lstCalendarioFolha, String orgCodigo, AcessoSistema responsavel) throws CalendarioControllerException, LogControllerException, RemoveException {
        List<LogDelegate> logs = new ArrayList<>();
        for (TransferObject calendarioFolha : lstCalendarioFolha) {
            Date cfoPeriodo = DateHelper.clearHourTime((Date) calendarioFolha.getAttribute(Columns.CFO_PERIODO));

            try {
                // Procura o registro e caso exista, atualiza os dados
                CalendarioFolhaOrg cfo = CalendarioFolhaOrgHome.findByPrimaryKey(orgCodigo, cfoPeriodo);
                CalendarioFolhaOrgHome.remove(cfo);

                // Salva o log de deleção do registro
                LogDelegate log = new LogDelegate(responsavel, Log.CALENDARIO_FOLHA_ORG, Log.DELETE, Log.LOG_INFORMACAO);
                log.setCfoPeriodo(DateHelper.toPeriodString(cfoPeriodo));
                log.setOrgao(orgCodigo);
                log.getDeletedFields(calendarioFolha.getAtributos());
                logs.add(log);
            } catch (FindException ex) {
                // Caso não exista, não tem problema, pois era para ser removido
            }
        }
        return logs;
    }

    private void validarCalendarioFolhaOrg(String orgCodigo, AcessoSistema responsavel) throws CalendarioControllerException, HQueryException {
        ListaCalFolhaOrgDataIniFimAntInvalidaQuery query = new ListaCalFolhaOrgDataIniFimAntInvalidaQuery();
        query.orgCodigo = orgCodigo;
        gerarExcecaoDataIniFimInvalida(query.executarLista());
    }

    private TransferObject setCalendarioFolhaOrgValues(CalendarioFolhaOrg cfo) {
        TransferObject cfoTO = new CustomTransferObject();
        cfoTO.setAttribute(Columns.CFO_PERIODO, cfo.getCfoPeriodo());
        cfoTO.setAttribute(Columns.CFO_ORG_CODIGO, cfo.getOrgCodigo());
        cfoTO.setAttribute(Columns.CFO_DATA_INI, cfo.getCfoDataIni());
        cfoTO.setAttribute(Columns.CFO_DATA_FIM, cfo.getCfoDataFim());
        cfoTO.setAttribute(Columns.CFO_DATA_FIM_AJUSTES, cfo.getCfoDataFimAjustes());
        cfoTO.setAttribute(Columns.CFO_DIA_CORTE, cfo.getCfoDiaCorte());
        cfoTO.setAttribute(Columns.CFO_APENAS_REDUCOES, cfo.getCfoApenasReducoes());
        cfoTO.setAttribute(Columns.CFO_DATA_PREVISTA_RETORNO, cfo.getCfoDataPrevistaRetorno());
        return cfoTO;
    }

    private boolean validarCalendarioFolha(Date periodoAtual, Date periodo, Date dataIni, Date dataFim, Date dataFimAjustes, Short diaCorte, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException {
        String mesAnoPeriodo = DateHelper.toPeriodString(periodo);

        // Não permite alteração de calendário com data fim passada exeto se for informado a possibilidade de alterar
        // o periodo vigente
        if (!TextHelper.isNull(periodoAtual) && DateHelper.getSystemDatetime().after(dataFim) && !(alteraPeriodoVigente && periodo.equals(periodoAtual))) {
            throw new CalendarioControllerException("mensagem.erro.calendario.folha.periodo.anterior.data.atual", responsavel, mesAnoPeriodo);
        }

        // Se permite agrupamento de períodos na mesma data de corte, e a data inicial e final forem
        // iguais, então significa que o período está agrupado em outro, portanto não continua as validações
        boolean periodoAgrupado = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && (dataIni.compareTo(dataFim) == 0);

        // Se está habilitado período para ajustes, então a dataFimAjustes deve ser maior ou igual à data fim
        boolean habilitaPeriodoAjustes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel);

        if (!periodoAgrupado) {
            // Valida se a data fim é maior que a data inicio do período
            if (!dataFim.after(dataIni)) {
                throw new CalendarioControllerException("mensagem.erro.calendario.folha.data.fim.deve.ser.maior.data.inicial", responsavel, mesAnoPeriodo);
            }

            if (habilitaPeriodoAjustes && dataFimAjustes.compareTo(dataFim) < 0) {
                throw new CalendarioControllerException("mensagem.erro.calendario.folha.data.fim.ajustes.deve.ser.maior.igual.data.fim", responsavel, mesAnoPeriodo);
            }

            // Valida a quantidade mínima e máxima de dias no período
            int qtdMinimaDiasPeriodo = 0;
            int qtdMaximaDiasPeriodo = 0;

            try {
                qtdMinimaDiasPeriodo = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MIN_DIAS_PERIODO_EXP, responsavel));
            } catch (NumberFormatException ex) {
                qtdMinimaDiasPeriodo = 15;
            }
            try {
                qtdMaximaDiasPeriodo = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_DIAS_PERIODO_EXP, responsavel));
            } catch (NumberFormatException ex) {
                qtdMaximaDiasPeriodo = 45;
            }

            // Devemos adicionar 1 à conta, já que o dia ini é às 00:00:00 e
            // o dia fim é as 23:59:59, e o método não irá considerar o dia
            // fim na conta.
            int qtdDiasPeriodo = DateHelper.dayDiff(dataFim, dataIni) + 1;

            if (qtdDiasPeriodo < qtdMinimaDiasPeriodo) {
                throw new CalendarioControllerException("mensagem.erro.calendario.folha.periodo.invalido.menos.dias", responsavel, mesAnoPeriodo, String.valueOf(qtdMinimaDiasPeriodo));
            } else if (qtdDiasPeriodo > qtdMaximaDiasPeriodo) {
                throw new CalendarioControllerException("mensagem.erro.calendario.folha.periodo.invalido.mais.dias", responsavel, mesAnoPeriodo, String.valueOf(qtdMaximaDiasPeriodo));
            }
        }

        // Verifica o valor numérico do dia de corte
        if (diaCorte < 1 || diaCorte > 31) {
            throw new CalendarioControllerException("mensagem.calendario.folha.dia.corte", responsavel);
        }

        // Verifica se a data fim é no dia de corte
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataFim);
        if (cal.get(Calendar.DAY_OF_MONTH) != diaCorte) {
            throw new CalendarioControllerException("mensagem.erro.calendario.folha.periodo.invalido.data.final.dia.corte", responsavel, mesAnoPeriodo);
        }

        return true;
    }

    private void gerarExcecaoDataIniFimInvalida(List<Date> periodosInvalidos) throws CalendarioControllerException {
        if (periodosInvalidos != null && periodosInvalidos.size() > 0) {
            StringBuilder mensagem = new StringBuilder();
            Iterator<Date> it = periodosInvalidos.iterator();
            while (it.hasNext()) {
                Date periodo = it.next();
                mensagem.append(DateHelper.toPeriodString(periodo));
                if (it.hasNext()) {
                    mensagem.append(", ");
                }
            }
            throw new CalendarioControllerException("mensagem.erro.calendario.folha.seguintes.periodos.invalidos", (AcessoSistema) null, mensagem.toString());
        }
    }

    @Override
    public Date findProximoDiaUtil(Date dataInicio, Integer diaApos) throws CalendarioControllerException {
        return findProximoDiaUtil(dataInicio, null, diaApos);
    }

    @Override
    public Date findProximoDiaUtil(Date dataInicio, Date dataFim, Integer diaApos) throws CalendarioControllerException {
        Date retorno = null;
        try {
            ObtemProximoDiaUtilQuery proximoDiaUtilQuery = new ObtemProximoDiaUtilQuery(dataInicio, dataFim, diaApos);
            List<TransferObject> proximoDiaResult = proximoDiaUtilQuery.executarDTO();

            if (proximoDiaResult != null && !proximoDiaResult.isEmpty()) {
                CustomTransferObject cto = (CustomTransferObject) proximoDiaResult.iterator().next();
                retorno = (Date) cto.getAttribute(Columns.CAL_DATA);
            }

            return retorno;
        } catch (HQueryException e) {
            throw new CalendarioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }
    }

    @Override
    public void atualizaCalendarioFolhaReplica(List<TransferObject> lstCalendarioFolha, List<TransferObject> lstCalendarioFolhaExclusao, Integer anoBase, Integer anoInicioReplica, Integer anoFimReplica, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            boolean habilitaPeriodoAjustes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel);
            boolean permiteApenasReducoes = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel);
            boolean habilitaDataPrevistaRetorno = ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel);
            int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel);

            // Atualiza calendario folha base das outras replicas
            updateCalendarioFolha(lstCalendarioFolha, lstCalendarioFolhaExclusao, tipoEntidade, codigoEntidade, false, responsavel);

            String nomeCampoEntidade = Columns.CFC_CSE_CODIGO;
            String nomeCampoPeriodo = Columns.CFC_PERIODO;
            String nomeCampoNumPeriodo = Columns.CFC_NUM_PERIODO;
            String nomeCampoDataIni = Columns.CFC_DATA_INI;
            String nomeCampoDataFim = Columns.CFC_DATA_FIM;
            String nomeCampoDataFimAjustes = Columns.CFC_DATA_FIM_AJUSTES;
            String nomeCampoDiaCorte = Columns.CFC_DIA_CORTE;
            String nomeCampoApenasReducoes = Columns.CFC_APENAS_REDUCOES;
            String nomeCampoDataPrevistaRetorno = Columns.CFC_DATA_PREVISTA_RETORNO;

            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {
                nomeCampoEntidade = Columns.CFE_EST_CODIGO;
                nomeCampoPeriodo = Columns.CFE_PERIODO;
                nomeCampoNumPeriodo = Columns.CFE_NUM_PERIODO;
                nomeCampoDataIni = Columns.CFE_DATA_INI;
                nomeCampoDataFim = Columns.CFE_DATA_FIM;
                nomeCampoDataFimAjustes = Columns.CFE_DATA_FIM_AJUSTES;
                nomeCampoDiaCorte = Columns.CFE_DIA_CORTE;
                nomeCampoApenasReducoes = Columns.CFE_APENAS_REDUCOES;
                nomeCampoDataPrevistaRetorno = Columns.CFE_DATA_PREVISTA_RETORNO;

            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                nomeCampoEntidade = Columns.CFO_ORG_CODIGO;
                nomeCampoPeriodo = Columns.CFO_PERIODO;
                nomeCampoNumPeriodo = Columns.CFO_NUM_PERIODO;
                nomeCampoDataIni = Columns.CFO_DATA_INI;
                nomeCampoDataFim = Columns.CFO_DATA_FIM;
                nomeCampoDataFimAjustes = Columns.CFO_DATA_FIM_AJUSTES;
                nomeCampoDiaCorte = Columns.CFO_DIA_CORTE;
                nomeCampoApenasReducoes = Columns.CFO_APENAS_REDUCOES;
                nomeCampoDataPrevistaRetorno = Columns.CFO_DATA_PREVISTA_RETORNO;
            }

            Map<Integer, TransferObject> calendarioAnoBase = lstCalendarioFolhaAno(anoBase, tipoEntidade, codigoEntidade, responsavel);

            // Monta lista dos próximos anos
            for (int i = anoInicioReplica; i <= anoFimReplica; i++) {
                Map<Integer, TransferObject> calendarioAno = lstCalendarioFolhaAno(i, tipoEntidade, codigoEntidade, responsavel);
                List<TransferObject> lstCalendarioFolhaReplica = new ArrayList<>();

                for (int j = 1; j <= qtdPeriodos; j++) {
                    // Calendario base que sera replicado
                    TransferObject calendarioBase = calendarioAnoBase.get(j);
                    Date periodoBase = (Date) calendarioBase.getAttribute(nomeCampoPeriodo);
                    int mes = DateHelper.getMonth(periodoBase);
                    int dia = DateHelper.getDay(periodoBase);

                    Calendar calTemp = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

                    String dataIniStr = null;

                    if (j == 1) {
                        // se for o primeiro periodo ele pega o próximo dia da ultima data
                        dataIniStr = calendarioAnoBase.get(qtdPeriodos).getAttribute(nomeCampoDataFim).toString();
                        calTemp.setTime(sdf.parse(dataIniStr));
                        calTemp.add(Calendar.DAY_OF_MONTH, 1);
                        calTemp.set(Calendar.HOUR_OF_DAY, 0);
                        calTemp.set(Calendar.MINUTE, 0);
                        calTemp.set(Calendar.SECOND, 0);
                    } else {
                        dataIniStr = calendarioBase.getAttribute(nomeCampoDataIni).toString();
                        calTemp.setTime(sdf.parse(dataIniStr));
                    }

                    calTemp = DateHelper.setYearOfCalendar(calTemp, i);
                    if (calTemp.get(Calendar.MONTH) == Calendar.DECEMBER && mes == 1) {
                        // Trata data inicial em ano anterior
                        calTemp.set(Calendar.YEAR, i - 1);
                    }
                    Date dataIni = calTemp.getTime();

                    String dataFimStr = calendarioBase.getAttribute(nomeCampoDataFim).toString();
                    calTemp.clear();
                    calTemp.setTime(sdf.parse(dataFimStr));
                    calTemp = DateHelper.setYearOfCalendar(calTemp, i);
                    Date dataFim = calTemp.getTime();

                    String diaCorte = String.valueOf(calTemp.get(Calendar.DAY_OF_MONTH));

                    Date dataFimAjustes = null;
                    if (habilitaPeriodoAjustes) {
                        String dataFimAjustesStr = calendarioBase.getAttribute(nomeCampoDataFimAjustes).toString();
                        calTemp.clear();
                        calTemp.setTime(sdf.parse(dataFimAjustesStr));
                        calTemp = DateHelper.setYearOfCalendar(calTemp, i);
                        dataFimAjustes = calTemp.getTime();
                    }

                    Date dataPrevistaRetorno = null;
                    if (habilitaDataPrevistaRetorno) {
                        String dataPrevistaRetornoStr = calendarioBase.getAttribute(nomeCampoDataPrevistaRetorno).toString();
                        calTemp.clear();
                        calTemp.setTime(sdf.parse(dataPrevistaRetornoStr));
                        calTemp = DateHelper.setYearOfCalendar(calTemp, i);
                        dataPrevistaRetorno = calTemp.getTime();
                    }

                    String apenasReducoes = (String) calendarioBase.getAttribute(nomeCampoApenasReducoes);

                    // Calendario cadastrado
                    TransferObject calendario = calendarioAno.get(j);

                    if (dataIni != null && dataFim != null && !diaCorte.equals("")) {
                        if (calendario == null) {
                            calendario = new CustomTransferObject();
                            calendario.setAttribute(nomeCampoPeriodo, DateHelper.getDate(i, mes, dia));
                            calendario.setAttribute(nomeCampoNumPeriodo, j);
                            calendario.setAttribute(nomeCampoEntidade, codigoEntidade);
                        }

                        String diaCorteOld = calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDiaCorte).toString();
                        String diaCorteNew = diaCorte;
                        String dataIniOld = calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataIni).toString();
                        String dataFimOld = calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataFim).toString();
                        String dataFimAjustesOld = calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataFimAjustes) == null ? "0000-00-00" : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataFimAjustes).toString();
                        String apenasReducoesOld = (String) (calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoApenasReducoes));
                        String dataPrevistaRetornoOld = calendarioAno.isEmpty() ? null : calendarioAno.get(Integer.valueOf(j)) == null ? null : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataPrevistaRetorno) == null ? "0000-00-00" : calendarioAno.get(Integer.valueOf(j)).getAttribute(nomeCampoDataPrevistaRetorno).toString();

                        calendario.setAttribute(nomeCampoDataIni, dataIni);
                        calendario.setAttribute(nomeCampoDataFim, dataFim);
                        calendario.setAttribute(nomeCampoDataFimAjustes, dataFimAjustes);
                        calendario.setAttribute(nomeCampoDiaCorte, Short.valueOf(diaCorte));
                        calendario.setAttribute(nomeCampoApenasReducoes, apenasReducoes);
                        calendario.setAttribute(nomeCampoDataPrevistaRetorno, dataPrevistaRetorno);

                        if (permiteApenasReducoes) {
                            apenasReducoesOld = (TextHelper.isNull(apenasReducoesOld) ? "N" : apenasReducoesOld);
                            apenasReducoes = (TextHelper.isNull(apenasReducoes) ? "N" : apenasReducoes);
                        }

                        if (dataIniOld == null || dataFimOld == null || diaCorteOld == null || dataIni.compareTo(DateHelper.parse(dataIniOld, "yyyy-MM-dd")) != 0 || dataFim.compareTo(DateHelper.parse(dataFimOld, "yyyy-MM-dd")) != 0 || (habilitaPeriodoAjustes && dataFimAjustes.compareTo(DateHelper.parse(dataFimAjustesOld, "yyyy-MM-dd")) != 0) || (habilitaDataPrevistaRetorno && dataPrevistaRetorno.compareTo(DateHelper.parse(dataPrevistaRetornoOld, "yyyy-MM-dd")) != 0) || !diaCorteNew.equals(diaCorteOld) || (permiteApenasReducoes && !apenasReducoesOld.equals(apenasReducoes))) {
                            // Adiciona o calendário para inclusão/alteração
                            lstCalendarioFolhaReplica.add(calendario);
                        }

                    } else {
                        throw new CalendarioControllerException("mensagem.erro.calendario.folha.preencha.todos.periodos", responsavel);
                    }
                }
                // Atualiza ano a ano
                updateCalendarioFolha(lstCalendarioFolhaReplica, new ArrayList<TransferObject>(), tipoEntidade, codigoEntidade, false, responsavel);
            }
        } catch (CalendarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (ParseException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza a data de previsão de retorno
     * @param novoDiaPrevisaoRetorno
     * @param tipoEntidade
     * @param codigoEntidade
     * @param responsavel
     * @return
     * @throws CalendarioControllerException
     */
    @Override
    public void updateCalendarioFolhaRetorno(Integer novoDiaPrevisaoRetorno, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException {
        ArrayList<TransferObject> calendarioAtualizar = new ArrayList<>();

        List<TransferObject> lstCalendario = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // Busca todos os registros que tem data fim após hoje
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
                query.cseCodigo = codigoEntidade;
                query.cfcDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
                query.estCodigo = codigoEntidade;
                query.cfeDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
                query.orgCodigo = codigoEntidade;
                query.cfoDataFimMaiorQue = df.format(new Date());
                lstCalendario = query.executarDTO();
            }
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        for (TransferObject calendario : lstCalendario) {
            Integer DiaPrevisaoRetorno = novoDiaPrevisaoRetorno;
            Date periodo = null;

            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                periodo = (Date) calendario.getAttribute(Columns.CFC_DATA_FIM);
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                periodo = (Date) calendario.getAttribute(Columns.CFE_DATA_FIM);
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                periodo = (Date) calendario.getAttribute(Columns.CFO_DATA_FIM);
            }

            // verifica se a data existe naquele mês, caso não coloca a maior data do mês
            GregorianCalendar dataNova = new GregorianCalendar();
            dataNova.setTime(periodo);
            if (DiaPrevisaoRetorno > dataNova.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                DiaPrevisaoRetorno = dataNova.getActualMaximum(Calendar.DAY_OF_MONTH);
            }

            //inicia variaveis de montagem da data de previsão
            int diaAtual = dataNova.get(Calendar.DAY_OF_MONTH);
            int mesAtual = dataNova.get(Calendar.MONTH);
            int anoAtual = dataNova.get(Calendar.YEAR);

            int mesReferencia = mesAtual;
            int anoReferencia = anoAtual;

            //No sistema gregoriano, os meses começam de 0 e vão até 11, ao somar um mês além do range é feita a conversão automática para o ano seguinte
            //se diaPrevisao < diaAtual
            if (DiaPrevisaoRetorno < diaAtual) {
                mesReferencia = mesAtual + 1;
                anoReferencia = anoAtual;
            }

            dataNova.set(Calendar.DAY_OF_MONTH, DiaPrevisaoRetorno);
            dataNova.set(Calendar.MONTH, mesReferencia);
            dataNova.set(Calendar.YEAR, anoReferencia);
            dataNova.set(Calendar.HOUR_OF_DAY, 23);
            dataNova.set(Calendar.MINUTE, 59);
            dataNova.set(Calendar.SECOND, 59);

            // seta nova data prevista do retorno
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                    calendario.setAttribute(Columns.CFC_DATA_PREVISTA_RETORNO, dataNova.getTime());
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                    calendario.setAttribute(Columns.CFE_DATA_PREVISTA_RETORNO, dataNova.getTime());
                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                    calendario.setAttribute(Columns.CFO_DATA_PREVISTA_RETORNO, dataNova.getTime());
                }
            }
            calendarioAtualizar.add(calendario);
        }
        // atualiza data prevista para o retorno
        updateCalendarioFolha(calendarioAtualizar, new ArrayList<TransferObject>(), tipoEntidade, codigoEntidade, false, responsavel);
    }

    /**
     * Realizar o cadastro de calendario folha de forma automática
     *
     * @param tipoEntidade - Os valores poderão ser ORG, EST ou CSE
     * @param codigoEntidade - O número identificador da entidade
     * @param diaCorte - o dia de corte que será cadastrado
     * @param periodoInicial -  O cadastro ocorrerá a partir do período informado
     * @param quantidadePeriodos -  A quantidade de períodos que será adicionado. Se 0, apenas atualizará os períodos cadastrados adiante
     * @param responsavel
     * @throws CalendarioControllerException
     */
    @Override
    public void atualizarCalendarioFolhaPorTipoEntidade(String tipoEntidade, String codigoEntidade, Integer diaCorte, String periodoInicial, Integer quantidadePeriodos, Integer diaPrevisaoRetorno, AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            List<TransferObject> lstCalendarioFolhaAlteracao = new ArrayList<>();

            periodoInicial = "01/" + periodoInicial;

            LocalDate periodoOriginal = LocalDate.parse(periodoInicial, DateTimeFormatter.ofPattern("d/MM/yyyy"));
            LocalDate periodo = LocalDate.parse(periodoInicial, DateTimeFormatter.ofPattern("d/MM/yyyy"));

            String codigo = null;

            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                Estabelecimento est = EstabelecimentoHome.findByOrgaoIdentificador(codigoEntidade);
                Orgao org = OrgaoHome.findByIdn(codigoEntidade, est.getEstCodigo());
                codigo = org.getOrgCodigo();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                Estabelecimento est = EstabelecimentoHome.findByIdn(codigoEntidade);
                codigo = est.getEstCodigo();
            } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) {
                Consignante cse = ConsignanteHome.findByIdn(codigoEntidade);
                codigo = cse.getCseCodigo();
            }

            Integer periodosCadastrados = buscarQuantidadePeriodosCadastradosAposPeriodoInicial(tipoEntidade, codigo, Date.from(periodoOriginal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            LOG.debug("periodosCadastrados: " + periodosCadastrados + " - quantidadePeriodos: " + quantidadePeriodos);
            // Para alteração dos periodos cadastrados
            quantidadePeriodos = periodosCadastrados + quantidadePeriodos;

            boolean periodoForaPadrao = false;
            Date dataFimParametroCalcularIniFim = null;

            for (int i = 0; i < quantidadePeriodos; i++) {
                LocalDate periodoIni = periodo.plusMonths(-1);
                LocalDate dataIni = null;
                LocalDate dataFim = null;
                if (!periodoForaPadrao) {
                	dataIni = periodoIni.withDayOfMonth(Math.min(diaCorte, periodoIni.lengthOfMonth())).plusDays(1);
                	dataFim = periodo.withDayOfMonth(Math.min(diaCorte, periodo.lengthOfMonth()));
                } else {
                	LocalDate dataFimParam = LocalDate.parse(DateHelper.toDateString(dataFimParametroCalcularIniFim), DateTimeFormatter.ofPattern("d/MM/yyyy"));
                	dataIni = dataFimParam.withDayOfMonth(Math.min(diaCorte, dataFimParam.lengthOfMonth())).plusDays(1);
                	dataFim = periodoIni.withDayOfMonth(Math.min(diaCorte, periodoIni.lengthOfMonth()));
                }

                Integer diaCorteAtual = dataFim.getDayOfMonth();
                Date periodoDate = Date.from(periodo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date dataIniDate = Date.from(dataIni.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date dataFimDate = Date.from(dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

                TransferObject cfTO = new CustomTransferObject();

                if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {

                    cfTO.setAttribute(Columns.CFO_PERIODO, periodoDate);
                    cfTO.setAttribute(Columns.CFO_ORG_CODIGO, codigo);
                    cfTO.setAttribute(Columns.CFO_DATA_INI, dataIniDate);
                    cfTO.setAttribute(Columns.CFO_DATA_FIM, dataFimDate);
                    cfTO.setAttribute(Columns.CFO_DATA_FIM_AJUSTES, dataFimDate);
                    cfTO.setAttribute(Columns.CFO_DIA_CORTE, diaCorteAtual.shortValue());

                    if (!TextHelper.isNull(diaPrevisaoRetorno)) {
                        GregorianCalendar dataPrevistaRetorno = new GregorianCalendar();
                        dataPrevistaRetorno.setTime(periodoDate);

                        // Se o dia de previsão de retorno é maior, que o número de dias do mês, seta o último dia do mês como possível retorno
                        if (diaPrevisaoRetorno > dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH));
                        } else {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, diaPrevisaoRetorno);
                        }

                        cfTO.setAttribute(Columns.CFO_DATA_PREVISTA_RETORNO, dataPrevistaRetorno.getTime());
                    }

                    if (periodo.equals(periodoOriginal)) {
                        // Realizar ajuste período atual
                        ajustarCalendarioFolhaAtualOrg(cfTO);
                    }

                    if (dataFimDate.compareTo((Date) cfTO.getAttribute(Columns.CFO_DATA_FIM)) != 0) {
                    	periodoForaPadrao = true;
                    }

                    if (periodoForaPadrao) {
                    	dataFimParametroCalcularIniFim = (Date) cfTO.getAttribute(Columns.CFO_DATA_FIM);
                        cfTO.setAttribute(Columns.CFO_DATA_FIM_AJUSTES, dataFimParametroCalcularIniFim);
                    }

                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {

                    cfTO.setAttribute(Columns.CFE_PERIODO, periodoDate);
                    cfTO.setAttribute(Columns.CFE_EST_CODIGO, codigo);
                    cfTO.setAttribute(Columns.CFE_DATA_INI, dataIniDate);
                    cfTO.setAttribute(Columns.CFE_DATA_FIM, dataFimDate);
                    cfTO.setAttribute(Columns.CFE_DATA_FIM_AJUSTES, dataFimDate);
                    cfTO.setAttribute(Columns.CFE_DIA_CORTE, diaCorteAtual.shortValue());

                    if (!TextHelper.isNull(diaPrevisaoRetorno)) {
                        GregorianCalendar dataPrevistaRetorno = new GregorianCalendar();
                        dataPrevistaRetorno.setTime(periodoDate);

                        // Se o dia de previsão de retorno é maior, que o número de dias do mês, seta o último dia do mês como possível retorno
                        if (diaPrevisaoRetorno > dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH));
                        } else {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, diaPrevisaoRetorno);
                        }

                        cfTO.setAttribute(Columns.CFE_DATA_PREVISTA_RETORNO, dataPrevistaRetorno.getTime());
                    }

                    if (periodo.equals(periodoOriginal)) {
                        // Realizar ajuste período atual
                        ajustarCalendarioFolhaAtualEst(cfTO);
                    }

                    if (dataFimDate.compareTo((Date) cfTO.getAttribute(Columns.CFE_DATA_FIM)) != 0) {
                    	periodoForaPadrao = true;
                    }

                    if (periodoForaPadrao) {
                    	dataFimParametroCalcularIniFim = (Date) cfTO.getAttribute(Columns.CFE_DATA_FIM);
                        cfTO.setAttribute(Columns.CFE_DATA_FIM_AJUSTES, dataFimParametroCalcularIniFim);
                    }

                } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) {

                    cfTO.setAttribute(Columns.CFC_PERIODO, periodoDate);
                    cfTO.setAttribute(Columns.CFC_CSE_CODIGO, codigo);
                    cfTO.setAttribute(Columns.CFC_DATA_INI, dataIniDate);
                    cfTO.setAttribute(Columns.CFC_DATA_FIM, dataFimDate);
                    cfTO.setAttribute(Columns.CFC_DATA_FIM_AJUSTES, dataFimDate);
                    cfTO.setAttribute(Columns.CFC_DIA_CORTE, diaCorteAtual.shortValue());

                    if (!TextHelper.isNull(diaPrevisaoRetorno)) {
                        GregorianCalendar dataPrevistaRetorno = new GregorianCalendar();
                        dataPrevistaRetorno.setTime(periodoDate);

                        // Se o dia de previsão de retorno é maior, que o número de dias do mês, seta o último dia do mês como possível retorno
                        if (diaPrevisaoRetorno > dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, dataPrevistaRetorno.getActualMaximum(Calendar.DAY_OF_MONTH));
                        } else {
                            dataPrevistaRetorno.set(Calendar.DAY_OF_MONTH, diaPrevisaoRetorno);
                        }

                        cfTO.setAttribute(Columns.CFC_DATA_PREVISTA_RETORNO, dataPrevistaRetorno.getTime());
                    }

                    if (periodo.equals(periodoOriginal)) {
                        // Realizar ajuste período atual
                        ajustarCalendarioFolhaAtualCse(cfTO);
                    }

                    if (dataFimDate.compareTo((Date) cfTO.getAttribute(Columns.CFC_DATA_FIM)) != 0) {
                    	periodoForaPadrao = true;
                    }

                    if (periodoForaPadrao) {
                    	dataFimParametroCalcularIniFim = (Date) cfTO.getAttribute(Columns.CFC_DATA_FIM);
                        cfTO.setAttribute(Columns.CFC_DATA_FIM_AJUSTES, dataFimParametroCalcularIniFim);
                    }

                }

                lstCalendarioFolhaAlteracao.add(cfTO);

                periodo = periodo.plusMonths(1);

            }

            updateCalendarioFolha(lstCalendarioFolhaAlteracao, new ArrayList<>(), tipoEntidade, codigo, false, responsavel);

        } catch (FindException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    private Integer buscarQuantidadePeriodosCadastradosAposPeriodoInicial(String tipoEntidade, String codigo, Date periodoInicial) throws HQueryException {
        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) {
            ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
            query.cseCodigo = codigo;
            query.cfcPeriodoMaiorQueIgual = periodoInicial;
            query.count = true;
            return query.executarContador();
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
            query.estCodigo = codigo;
            query.cfePeriodoMaiorQueIgual = periodoInicial;
            query.count = true;
            return query.executarContador();
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
            query.orgCodigo = codigo;
            query.cfoPeriodoMaiorQueIgual = periodoInicial;
            query.count = true;
            return query.executarContador();
        } else {
            return 0;
        }
    }

    private void ajustarCalendarioFolhaAtualCse(TransferObject cfTO) throws HQueryException {
        // Verificar data fim periodo anterior
        Date periodoDate = (Date) cfTO.getAttribute(Columns.CFC_PERIODO);
        String cseCodigo = (String) cfTO.getAttribute(Columns.CFC_CSE_CODIGO);

        periodoDate = Date.from(periodoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(-1).atStartOfDay().atZone((ZoneId.systemDefault())).toInstant());

        ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
        query.cfcPeriodo = periodoDate;
        query.cseCodigo = cseCodigo;
        List<TransferObject> cfLst = query.executarDTO();

        if (!cfLst.isEmpty()) {
            TransferObject calendarioFolhaCse = cfLst.get(0);
            Date cfcDataFim = (Date) calendarioFolhaCse.getAttribute(Columns.CFC_DATA_FIM);
            Short cfcDiaCorte = (Short)  calendarioFolhaCse.getAttribute(Columns.CFC_DIA_CORTE);

            LocalDate dataFimPeriodoAnterior = cfcDataFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date dataIniPeriodoAtual = Date.from(dataFimPeriodoAnterior.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

            cfTO.setAttribute(Columns.CFC_DATA_INI, dataIniPeriodoAtual);

            LocalDate periodo = LocalDate.parse(DateHelper.toDateString(periodoDate), DateTimeFormatter.ofPattern("d/MM/yyyy"));
            if(periodo.getMonth() != dataFimPeriodoAnterior.getMonth()) {
            	LocalDate dataFim = periodo.withDayOfMonth(Math.min(cfcDiaCorte, periodo.lengthOfMonth()));
            	Date dataFimDate = Date.from(dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            	cfTO.setAttribute(Columns.CFC_DATA_FIM, dataFimDate);
            }
        }
    }

    private void ajustarCalendarioFolhaAtualEst(TransferObject cfTO) throws HQueryException {
        // Verificar data fim periodo anterior
        Date periodoDate = (Date) cfTO.getAttribute(Columns.CFE_PERIODO);
        String estCodigo = (String) cfTO.getAttribute(Columns.CFE_EST_CODIGO);

        periodoDate = Date.from(periodoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(-1).atStartOfDay().atZone((ZoneId.systemDefault())).toInstant());

        ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
        query.cfePeriodo = periodoDate;
        query.estCodigo = estCodigo;
        List<TransferObject> cfLst = query.executarDTO();

        if (!cfLst.isEmpty()) {
            TransferObject calendarioFolhaCse = cfLst.get(0);
            Date cfcDataFim = (Date) calendarioFolhaCse.getAttribute(Columns.CFE_DATA_FIM);
            Short cfcDiaCorte = (Short)  calendarioFolhaCse.getAttribute(Columns.CFE_DIA_CORTE);

            LocalDate dataFimPeriodoAnterior = cfcDataFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date dataIniPeriodoAtual = Date.from(dataFimPeriodoAnterior.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

            cfTO.setAttribute(Columns.CFE_DATA_INI, dataIniPeriodoAtual);

            LocalDate periodo = LocalDate.parse(DateHelper.toDateString(periodoDate), DateTimeFormatter.ofPattern("d/MM/yyyy"));
            if(periodo.getMonth() != dataFimPeriodoAnterior.getMonth()) {
            	LocalDate dataFim = periodo.withDayOfMonth(Math.min(cfcDiaCorte, periodo.lengthOfMonth()));
            	Date dataFimDate = Date.from(dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            	cfTO.setAttribute(Columns.CFE_DATA_FIM, dataFimDate);
            }
        }
    }

    private void ajustarCalendarioFolhaAtualOrg(TransferObject cfTO) throws HQueryException {
        // Verificar data fim periodo anterior
        Date periodoDate = (Date) cfTO.getAttribute(Columns.CFO_PERIODO);
        String orgCodigo = (String) cfTO.getAttribute(Columns.CFO_ORG_CODIGO);

        periodoDate = Date.from(periodoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(-1).atStartOfDay().atZone((ZoneId.systemDefault())).toInstant());

        ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
        query.cfoPeriodo = periodoDate;
        query.orgCodigo = orgCodigo;
        List<TransferObject> cfLst = query.executarDTO();

        if (!cfLst.isEmpty()) {
            TransferObject calendarioFolhaOrg = cfLst.get(0);
            Date cfcDataFim = (Date) calendarioFolhaOrg.getAttribute(Columns.CFO_DATA_FIM);
            Short cfcDiaCorte = (Short) calendarioFolhaOrg.getAttribute(Columns.CFO_DIA_CORTE);

            LocalDate dataFimPeriodoAnterior = cfcDataFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date dataIniPeriodoAtual = Date.from(dataFimPeriodoAnterior.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

            cfTO.setAttribute(Columns.CFO_DATA_INI, dataIniPeriodoAtual);

            LocalDate periodo = LocalDate.parse(DateHelper.toDateString(periodoDate), DateTimeFormatter.ofPattern("d/MM/yyyy"));
            if(periodo.getMonth() != dataFimPeriodoAnterior.getMonth()) {
            	LocalDate dataFim = periodo.withDayOfMonth(Math.min(cfcDiaCorte, periodo.lengthOfMonth()));
            	Date dataFimDate = Date.from(dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            	cfTO.setAttribute(Columns.CFO_DATA_FIM, dataFimDate);
            }
        }
    }

    private boolean isParamOrgaoHabilitado(String orgCodigo, String taoCodigo, AcessoSistema responsavel) {
        ParamOrgao paramOrgao = null;
        try {
            paramOrgao = parametroController.findParamOrgaoByOrgCodigoAndTaoCodigo(orgCodigo, taoCodigo, responsavel);
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return paramOrgao != null && !TextHelper.isNull(paramOrgao.getPaoVlr()) && paramOrgao.getPaoVlr().equals(CodedValues.TPC_SIM);
    }

    @Override
    public boolean exibirCalendarioFiscal(String tipoEntidade, String estCodigo, String orgCodigo, AcessoSistema responsavel) {
        boolean calendarioFiscalParaNomeArquivo = ParamSist.paramEquals(CodedValues.TPC_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO, CodedValues.TPC_SIM, responsavel);

        try {
            // Determina se deve exibir o campo de data fiscal com base na entidade do calendário e dos parâmetros
            if (calendarioFiscalParaNomeArquivo) {
                // Se um dos parâmetros de sistema que utiliza calendário fiscal estiver habilitado, então exibe o campo na tela
                return true;
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {
                // Se não estiver habilitado geral, verifica se deve exibir o campo no calendário de CSE/EST caso algum órgão destes tenha o calendário habilitado
                boolean existeParamOrgHabCampoFiscal = parametroController.buscarQuantidadeParamOrgao(CodedValues.TAO_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO, estCodigo, null, CodedValues.TPC_SIM, responsavel) > 0;

                if (existeParamOrgHabCampoFiscal) {
                    return true;
                }
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                // Senão, verifica no caso de calendário de órgão se o órgão tem o parâmetro habilitado
                boolean permiteParamOrgCampoFiscal = isParamOrgaoHabilitado(orgCodigo, CodedValues.TAO_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO, responsavel);

                if (permiteParamOrgCampoFiscal) {
                    return true;
                }
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public void carregaCalendarioFolhaAutomatico(AcessoSistema responsavel) throws CalendarioControllerException {
        try {
            ListaCalendarioFolhaQntPeriodosQuery query = new ListaCalendarioFolhaQntPeriodosQuery();
            query.dataIni = DateHelper.getSystemDatetime();

            List<TransferObject> calendarioFolha = query.executarDTO();

            for(TransferObject calendario : calendarioFolha) {
                // Necessário utilizar Object, pois o tipo de objeto que retorna do TransferObject está sendo diferente no Oracle(BigDecimal) e para o Mysql(BigInteger)
                Object quantidade = calendario.getAttribute("QUANTIDADE");
                Date periodo = (Date) calendario.getAttribute("PERIODO");
                String tipoEntidade = (String) calendario.getAttribute("ENTIDADE");
                String codigoEntidade = (String) calendario.getAttribute("CODIGO_ENTIDADE");
                // Necessário utilizar Object, pois o tipo de objeto que retorna do TransferObject está sendo diferente no Oracle(BigDecimal) e para o Mysql(Short)
                Object diaCorte = calendario.getAttribute("CORTE");

                if(Integer.valueOf(quantidade.toString()) < QUANTIDADE_PERIODOS) {
                    atualizarCalendarioFolhaPorTipoEntidade(tipoEntidade, codigoEntidade, Integer.valueOf(diaCorte.toString()), DateHelper.toPeriodString(periodo), Integer.valueOf(QUANTIDADE_PERIODOS), null, responsavel);
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CalendarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
