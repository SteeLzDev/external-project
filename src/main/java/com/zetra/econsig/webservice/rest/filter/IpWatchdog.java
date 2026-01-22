package com.zetra.econsig.webservice.rest.filter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: IpWatchdog</p>
 * <p>Description: Classe auxiliar para verificação de segurança.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class IpWatchdog {

	// Lista das sessões por dia (para facilitar a limpeza de 24 em 24h
	private static Map<String, SessionData> sessionByIp;

	// Lista das sessões por IP (O objeto é o mesmo da lista anterior, só que indexado por IP)
	private static Map<Integer, Set<SessionData>> sessionByDay;

	static {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = IpWatchdog.class.getSimpleName();
            sessionByIp = new ExternalMap<>(prefix + "-sessionByIp");
            sessionByDay = new ExternalMap<>(prefix + "-sessionByDay");
        } else {
            sessionByIp = new HashMap<>();
            sessionByDay = new HashMap<>();
        }
	}

    // DESENV-17859 : sugestão do SonarLint
	private IpWatchdog() {

	}

	/**
	 * Verifica se determinado ip está bloqueado. Se sim, retorna o quando se
	 * deve esperar, no caso contrário, retorna zero.
	 *
	 * @param ip
	 * @return
	 */
	public static int verificaIp(String ip) {
        if (sessionByDay.size() > 1) {
            cleanCache();
        }
        final Calendar now = Calendar.getInstance();
        if (sessionByIp.containsKey(ip)) {
            final SessionData data = sessionByIp.get(ip);
            if ((data.tries > 5) && now.before(data.date)) {
                // Se ainda não passou o tempo necessário, informa novamente
                return data.delay;
            }
        }
		return 0;
	}

	/**
	 * Retira o ip da lista de bloqueados
	 * @param ip
	 */
	public static void desbloqueiaIp(String ip) {
		if (sessionByIp.containsKey(ip)) {
			final SessionData data = sessionByIp.get(ip);
			final Set<SessionData> list = sessionByDay.get(data.day);
			list.remove(data);
			sessionByIp.remove(ip);
		}
	}

	/**
	 * Inicia o procedimento de bloqueio
     * IMPORTANTE: O IP do Centralizador não será bloqueado.
	 * @param ip
	 * @return
	 */
	public static int bloqueiaIp(String ip) {
	    int delay = 0;
        final Object paramUrlCentralizador = ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR_MOBILE, AcessoSistema.getAcessoUsuarioSistema());
        final String urlCentralizador = (paramUrlCentralizador != null) ? (String) paramUrlCentralizador : null;
        if ((urlCentralizador != null) && !urlCentralizador.equals("")) {
            final List<String> urls = Arrays.asList(urlCentralizador.split(";"));
            if (!JspHelper.validaUrl(ip, urls)) {
                final Integer hoje = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                if (sessionByIp.containsKey(ip)) {
                    final SessionData data = sessionByIp.get(ip);
                    data.tries++;
                    if (data.tries > 5) {
                        data.date = Calendar.getInstance();
                        if (data.delay < 86400) {
                            data.delay = data.delay * 2;
                        }
                        data.date.add(Calendar.SECOND, data.delay);
                        delay = data.delay;
                    }
                    sessionByIp.put(data.ip, data);
                } else {
                    final SessionData data = new SessionData();
                    data.ip = ip;
                    data.day = hoje;
                    data.tries = 1;
                    data.date = null;
                    data.delay = 15;
                    final Set<SessionData> dayList = sessionByDay.computeIfAbsent(hoje, k -> new HashSet<>());
                    dayList.add(data);
                    sessionByDay.put(hoje, dayList);
                    sessionByIp.put(data.ip, data);
                }
            }
        }
		return delay;
	}

	/**
	 * Limpa os que foram do dia anterior
	 */
	public static void cleanCache() {
        // pega o dia de hoje
        final Integer hoje = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        // mata tudo que não for do dia de hoje
        final Iterator<Entry<Integer, Set<SessionData>>> it = sessionByDay.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Integer, Set<SessionData>> pair = it.next();
            if (!pair.getKey().equals(hoje)) {
                final Set<SessionData> killList = pair.getValue();
                // Remove as sessões da lista de ips
                for (final SessionData sessionData : killList) {
                    sessionByIp.remove(sessionData.ip);
                }
                // remove o dia
                it.remove();
            }
        }
	}

	/**
	 * Limpa completamente o cache.
	 */
    public static void reset() {
        sessionByDay.clear();
        sessionByIp.clear();
    }
}
