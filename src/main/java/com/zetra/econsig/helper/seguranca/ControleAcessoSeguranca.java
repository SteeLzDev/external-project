package com.zetra.econsig.helper.seguranca;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ControleAcessoSeguranca</p>
 * <p>Description: Controle da quantidade de acessos não autorizados permitidos no sistema.</p>
 * <p>Copyright: Copyright (c) 2006-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public enum ControleAcessoSeguranca {
    CONTROLESEGURANCA;

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleAcessoSeguranca.class);

    // Intervalo de tempo em dias, onde a contagem de acessos errados deve ser reiniciado
    private final int intervalo = 1000 * 60 * 60 * 24;

    // Faz um mapeamento onde a chave é o usuCodigo
    // e o valor é o tempo da primeira tentativa errada de acesso de segurança, indicando o inicio do período
    private Map<String, Long> hora;

    // Faz um mapeamento onde a chave é o usuCodigo
    // e o valor é o número de erros de acessos de segurança permitidos no período
    private Map<String, Integer> acessosErrados;

    // Tempo, em milisegundos, da realização da última limpeza
    private long ultimaLimpeza = Calendar.getInstance().getTimeInMillis();

    // Intervalo de tempo, em milisegundos, da realização de limpezas no cache
    private final long intervaloLimpeza = 1000L * 60 * 60 * 24;

    // Quantidade de consultas máximas em um dado intervalo de tempo
    private int limite;

    ControleAcessoSeguranca() {
        reset();
    }

    public void reset() {
        try {
            final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_ERRO_SEGURANCA_SESSAO, null);
            limite = param != null ? Integer.parseInt(param.toString()) : 0;
        } catch (final Exception ex) {
            limite = 0;
        }

        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            hora = new ExternalMap<>(prefix + "-hora");
            acessosErrados = new ExternalMap<>(prefix + "-acessosErrados");
        } else {
            hora = new HashMap<>();
            acessosErrados = new HashMap<>();
        }
    }

    public boolean bloqueiaLimiteErroSeguranca(AcessoSistema responsavel, String entidadeOperada, String operacao) {
        final String usuCodigo = responsavel.getUsuCodigo();

        // Inicialmente, o limite de consultas é o valor passado atraves do param sistema
        final int limiteConsultas = limite;

        if ((limiteConsultas == 0) || (usuCodigo == null)) {
            return false;
        }

        final long agora = Calendar.getInstance().getTimeInMillis();

        // Verifica a necessidade de uma limpeza nos hashs
        if (agora > (ultimaLimpeza + intervaloLimpeza)) {
            synchronized (this) {
                if (agora > (ultimaLimpeza + intervaloLimpeza)) {
                    // Se a ultima limpeza já foi feita fora do intervalo de limpeza
                    ultimaLimpeza = agora;
                    limpa();
                }
            }
        }

        final Long tempo = hora.get(usuCodigo);

        if (tempo == null ) {
            // Se é a primeira tentativa errada de acesso, então adiciona os valores aos mapeamentos
            hora.put(usuCodigo, Long.valueOf(agora));
            acessosErrados.put(usuCodigo, Integer.valueOf(1));

        } else {
            // Pega a data do último erro de acesso
            final Date dataUltConsulta = new Date(tempo.longValue());
            // Pega a data atual
            final Date dataAtual = new Date(agora);

            synchronized (this) {
                // Se a diferença em dias é maior do que o intervalo,
                // então reinicializa o controle
                if (dateDiff(dataAtual, dataUltConsulta) >= intervalo) {
                    hora.put(usuCodigo, Long.valueOf(agora));
                    acessosErrados.put(usuCodigo, Integer.valueOf(1));
                } else {
                    // Se ainda está dentro do intervalo, verifica a qtd
                    // de erros já realizados
                    final Integer qtd = acessosErrados.get(usuCodigo);
                    final int qtdConsultas = (qtd != null) ? qtd : 0;

                    try {
                        if (qtdConsultas >= limiteConsultas) {
                            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
                            final UsuarioTransferObject usuTransfer = usuDelegate.findUsuario(usuCodigo, responsavel);
                            final String stuCodigo = usuTransfer.getStuCodigo();

                            if (!CodedValues.STU_CODIGOS_INATIVOS.contains(stuCodigo)) {
                                usuDelegate.bloquearUsuarioMotivoSeguranca(usuCodigo, entidadeOperada, operacao, responsavel);
                            }

                            acessosErrados.put(usuCodigo, Integer.valueOf(qtdConsultas + 1));
                            responsavel.setSessaoInvalidaErroSeg(true);
                            return true;

                        } else {
                            // Se o limite ainda não foi alcançado,
                            // atualiza o contador e mantém o acesso
                            acessosErrados.put(usuCodigo, Integer.valueOf(qtdConsultas + 1));
                        }
                    } catch (final UsuarioControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return false;
    }

    public boolean usuarioBloqueadoPorAcessoIlegal(AcessoSistema responsavel) {
        if (limite == 0) {
            return false;
        } else {
            synchronized (this) {
                if (responsavel.getUsuCodigo() != null) {
                    final Integer qtd = acessosErrados.get(responsavel.getUsuCodigo());
                    final int qtdConsultas = (qtd != null) ? qtd : 0;

                    if (qtdConsultas > limite) {
                        // Se o limite foi alcançado, então bloqueia o usuário
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void removeMapUsuario(String usuCodigo) {
        acessosErrados.remove(usuCodigo);
    }

    /**
     * Retira da memória a referência a consultas que já estão fora do
     * intervalo de tempo.
     */
    private void limpa() {
        final Map<String, Long> result = new HashMap<>();

        final Date dataAtual = DateHelper.getSystemDatetime();

        Date dataUltConsulta = null;

        for (final Map.Entry<String, Long> entry : hora.entrySet()) {
            final String chave = entry.getKey();
            final Long tempo = entry.getValue();
            dataUltConsulta = new Date(tempo.longValue());

            if (dateDiff(dataAtual, dataUltConsulta) < intervalo) {
                // Se ainda está no intervalo, mantém a chave
                result.put(chave, tempo);
            } else {
                // Se já não está mais no intervalo, então retira a chave
                acessosErrados.remove(chave);
            }
        }

        // Limpa o map das horas
        hora.clear();
        hora.putAll(result);
        result.clear();
    }

    /**
     * Retorna a diferença em dias de duas datas.
     * @param a
     * @param b
     * @return
     */
    private int dateDiff(Date a, Date b) {
        int tempDifference = 0;
        int difference = 0;
        final Calendar earlier = Calendar.getInstance();
        final Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0) {
            earlier.setTime(a);
            later.setTime(b);
        } else {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
            tempDifference = 365 * 1000 * 60 * 60 * 24 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference * 1000 * 60 * 60 * 24;
        }

        if (earlier.get(Calendar.HOUR_OF_DAY) != later.get(Calendar.HOUR_OF_DAY)) {
            tempDifference = later.get(Calendar.HOUR_OF_DAY) - earlier.get(Calendar.HOUR_OF_DAY);
            difference += tempDifference * 1000 * 60 * 60;
        } else if (earlier.get(Calendar.MINUTE) != later.get(Calendar.MINUTE)) {
            tempDifference = later.get(Calendar.MINUTE) - earlier.get(Calendar.MINUTE);
            difference += tempDifference * 1000 * 60;
        }

        return difference;
    }
}
