package com.zetra.econsig.helper.periodo;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
/**
 * <p>Title: RepasseHelper</p>
 * <p>Description: Classe auxiliar para tratamento das datas de repasse.</p>
 * <p>Copyright: Copyright (c) 2003-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class RepasseHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RepasseHelper.class);

    private Map<String, Integer> diaRepasseOrgaos;
    private final Map<String, Map<Date, Integer>> diaUtilRepasseOrgaos;

    private static class SingletonHelper {
        private static final RepasseHelper instance = new RepasseHelper();
    }

    public static RepasseHelper getInstance() {
        return SingletonHelper.instance;
    }

    private RepasseHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            diaRepasseOrgaos = new ExternalMap<>(prefix + "-diaRepasseOrgaos");
            diaUtilRepasseOrgaos = new ExternalMap<>(prefix + "-diaUtilRepasseOrgaos");
        } else {
            diaUtilRepasseOrgaos = new ConcurrentHashMap<>();
        }
    }

    private synchronized void carregarCacheRepasse() {
        if (diaRepasseOrgaos == null) {
            try {
                final ConsignanteDelegate delegate = new ConsignanteDelegate();
                if (ExternalCacheHelper.hasExternal()) {
                    final String prefix = getClass().getSimpleName();
                    diaRepasseOrgaos = new ExternalMap<>(prefix + "-diaRepasseOrgaos");
                    final Map<String, Integer> diaRepasseOrgaosTmp = delegate.getOrgDiaRepasse(null, AcessoSistema.getAcessoUsuarioSistema());
                    if (diaRepasseOrgaosTmp != null ) {
                        diaRepasseOrgaosTmp.putAll(diaRepasseOrgaosTmp);
                    }
                } else {
                    diaRepasseOrgaos = delegate.getOrgDiaRepasse(null, AcessoSistema.getAcessoUsuarioSistema());
                    if (diaRepasseOrgaos == null) {
                        diaRepasseOrgaos = new HashMap<>();
                    }
                }
            } catch (final ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    public void reset() {
        diaRepasseOrgaos = null;
        diaUtilRepasseOrgaos.clear();
    }

    /**
     * Retorna o dia de repasse de um determinado órgão, se nulo retorna o dia de
     * de repasse dos parâmetros de sistema
     * @param orgCodigo : código do órgão
     * @param mesAnoProximoDesconto : mês/ano (dia é ignorado) do período de desconto
     * @param responsavel : usuário que realiza a operação
     * @return : o dia de repasse
     * @throws ViewHelperException
     */
    public static int getDiaRepasse(String orgCodigo, Date mesAnoProximoDesconto, AcessoSistema responsavel) throws ViewHelperException {
        SingletonHelper.instance.carregarCacheRepasse();

        int diaRepasse = 0;
        if (TextHelper.isNull(orgCodigo) || !SingletonHelper.instance.diaRepasseOrgaos.containsKey(orgCodigo)) {
            // Se o dia de repasse do órgão é nulo, retorna o dia de repasse dos parâmetros de
            // sistema da consignante
            final Object tpcDiaRepasse = ParamSist.getInstance().getParam(CodedValues.TPC_DIA_PAGTO_PRIMEIRA_PARCELA, responsavel);
            if (TextHelper.isNull(tpcDiaRepasse)) {
                throw new ViewHelperException("mensagem.informe.repasse.dia", responsavel);
            }
            try {
                diaRepasse = Integer.parseInt(tpcDiaRepasse.toString());
            } catch (final NumberFormatException ex) {
                throw new ViewHelperException("mensagem.erro.repasse.numero.inteiro.intervalo", responsavel);
            }
        } else {
            diaRepasse = SingletonHelper.instance.diaRepasseOrgaos.get(orgCodigo);
        }
        if ((diaRepasse < 1) || (diaRepasse > 31)) {
            throw new ViewHelperException("mensagem.erro.repasse.numero.inteiro.intervalo", responsavel);
        }

        // Se utiliza dias úteis na data de repasse, obtém do calendário do sistema
        // o próximo dia útil à partir do mês/ano de lançamento dos descontos
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_DIA_PAGTO_PRIMEIRA_PARCELA, CodedValues.TPC_SIM, responsavel)) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mesAnoProximoDesconto);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal = DateHelper.clearHourTime(cal);
                final Date dataInicio = cal.getTime();

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                final Date dataFim = cal.getTime();

                final String chave = TextHelper.isNull(orgCodigo) ? "__GERAL__" : orgCodigo;

                if (!SingletonHelper.instance.diaUtilRepasseOrgaos.containsKey(chave)) {
                    SingletonHelper.instance.diaUtilRepasseOrgaos.put(chave, new ConcurrentHashMap<>());
                }
                if (!SingletonHelper.instance.diaUtilRepasseOrgaos.get(chave).containsKey(dataInicio)) {
                    final CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
                    Date proximoDiaUtil = calendarioController.findProximoDiaUtil(dataInicio, dataFim, --diaRepasse);
                    while ((proximoDiaUtil == null) && (diaRepasse > 0)) {
                        proximoDiaUtil = calendarioController.findProximoDiaUtil(dataInicio, dataFim, --diaRepasse);
                    }
                    if (proximoDiaUtil == null) {
                        throw new ViewHelperException("mensagem.erro.repasse.dia.util.nao.cadastrado", responsavel);
                    } else {
                        diaRepasse = DateHelper.getDay(proximoDiaUtil);
                    }
                    SingletonHelper.instance.diaUtilRepasseOrgaos.get(chave).put(dataInicio, diaRepasse);
                } else {
                    diaRepasse = SingletonHelper.instance.diaUtilRepasseOrgaos.get(chave).get(dataInicio);
                }
            } catch (final CalendarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(ex);
            }
        }

        return diaRepasse;
    }
}
