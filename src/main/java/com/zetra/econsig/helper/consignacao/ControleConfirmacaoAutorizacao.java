package com.zetra.econsig.helper.consignacao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.exception.LimiteTentativaExcedidaException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ControleConfirmacaoAutorizacao</p>
 * <p>Description: Faz o controle de tentativas de confirmação da solicitação pelo código de autorização,
 *                 caso o número de tentativas defindas tenha sido alcançado a consignação é cancelada.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ControleConfirmacaoAutorizacao {
    //  Sinlgeton, instância única desta classe de controle de login
    private static ControleConfirmacaoAutorizacao singleton;
    //  Intervalo de tempo em horas, onde a contagem das consultas deve ser reiniciado
    private final int intervalo = 1;
    // Faz um mapeamento onde a chave é o usuCodigo e o valor é o tempo da primeira consulta, indicando o inicio do período
    private Map<String, Long> hora;
    // Faz um mapeamento onde a chave é o usuCodigo e o valor é o número de consultas realizadas no período
    private Map<String, Integer> tentativas;
    // Quantidade de consultas máximas em um dado intervalo de tempo
    private static final int LIMITE = 5;
    // Tempo, em milisegundos, da realização da última limpeza
    private long ultimaLimpeza = Calendar.getInstance().getTimeInMillis();
    // Intervalo de tempo, em milisegundos, da realização de limpezas no cache
    private static final long intervaloLimpeza = 1000 * 60 * 60;

    static {
        singleton = new ControleConfirmacaoAutorizacao();
    }

    private ControleConfirmacaoAutorizacao() {
        reset();
    }

    public static ControleConfirmacaoAutorizacao getInstance() {
        return singleton;
    }

    public void reset() {
        hora = new HashMap<String, Long>();
        tentativas = new HashMap<String, Integer>();
    }

    public synchronized void bloqueia(String adeCodigo, AcessoSistema responsavel) throws ZetraException {
        int limiteTentativas = LIMITE;

        if (limiteTentativas != 0) {
            long agora = Calendar.getInstance().getTimeInMillis();

            // Verifica a necessidade de uma limpeza nos hashs
            if (agora > (ultimaLimpeza + intervaloLimpeza)) {
                ultimaLimpeza = agora;
                limpa();
            }

            // Chave dos mapeamentos.
            String chave = adeCodigo;

            Long tempo = hora.get(chave);

            if (tempo == null ) {

                if (limiteTentativas == 1) {
                    // Se o limite foi alcançado, cancela a consignação
                    ConsignacaoDelegate adeDelegate = new ConsignacaoDelegate();
                    adeDelegate.cancelarConsignacao(adeCodigo, responsavel);

                    // Cria ocorrência de cancelamento automático de consignação
                    adeDelegate.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.codigo.autorizacao.invalido", responsavel), responsavel);

                    LimiteTentativaExcedidaException tentativaExcedida = new LimiteTentativaExcedidaException("mensagem.codigoAutorizacaoCanceladaAutomaticamente", responsavel);
                    throw tentativaExcedida;
                } else if (limiteTentativas == 2) {
                    hora.put(chave, Long.valueOf(agora));
                    tentativas.put(chave, Integer.valueOf(1));

                    ZetraException ze = new ZetraException("mensagem.codigoAutorizacaoInvalidoRestaUm", responsavel);
                    throw ze;
                }

                //Se é a primeira consulta, então adiciona os valores aos mapeamentos
                hora.put(chave, Long.valueOf(agora));
                tentativas.put(chave, Integer.valueOf(1));

            } else {
                // Pega a data da última consulta
                Date dataUltConsulta = new Date(tempo.longValue());
                // Pega a data atual
                Date dataAtual = new Date(agora);

                // Se a diferença em dias é maior do que o intervalo, então reinicializa o controle
                if (hourDiff(dataAtual, dataUltConsulta) >= intervalo) {
                    hora.put(chave, Long.valueOf(agora));
                    tentativas.put(chave, Integer.valueOf(1));
                } else {
                    // Se ainda está dentro do intervalo, verifica a quantidade de tentativas já realizadas
                    Integer qtd = tentativas.get(chave);
                    int qtdtentativas = (qtd != null) ? qtd.intValue() : 0;
                    qtdtentativas++;

                    if (qtdtentativas >= limiteTentativas) {
                        // Se o limite foi alcançado, cancela a consignação
                        ConsignacaoDelegate adeDelegate = new ConsignacaoDelegate();
                        adeDelegate.cancelarConsignacao(adeCodigo, responsavel);

                        // Cria ocorrência de cancelamento automático de consignação
                        adeDelegate.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.codigo.autorizacao.invalido", responsavel), responsavel);

                        resetTetantivasLogin(chave);
                        LimiteTentativaExcedidaException tentativaExcedida = new LimiteTentativaExcedidaException("mensagem.codigoAutorizacaoCanceladaAutomaticamente", responsavel);
                        throw tentativaExcedida;
                    } else {
                        // Se o limite ainda não foi alcançado,
                        // atualiza o contador e permite a consulta
                        tentativas.put(chave, Integer.valueOf(qtdtentativas));

                        if ((qtdtentativas) == (limiteTentativas - 1)) {
                            ZetraException ze = new ZetraException("mensagem.codigoAutorizacaoInvalidoRestaUm", responsavel);
                            throw ze;
                        }
                    }
                }
            }
        }
    }

    /**
     * Retorna a diferença em horas de duas datas.
     * @param a
     * @param b
     * @return
     */
    private int hourDiff(Date a, Date b) {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0) {
            earlier.setTime(a);
            later.setTime(b);
        } else {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
            tempDifference = 24 * 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = 24 * (later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR));
            difference += tempDifference;
        }

        if (earlier.get(Calendar.HOUR_OF_DAY) != later.get(Calendar.HOUR_OF_DAY)) {
            tempDifference = later.get(Calendar.HOUR_OF_DAY) - earlier.get(Calendar.HOUR_OF_DAY);
            difference += tempDifference;
        }

        return difference;
    }

    /**
     * Retira da memória a referência a tentativas de login que já estão fora do intervalo de tempo.
     */
    private void limpa() {
        Map<String, Long> result = new HashMap<String, Long>();

        Date dataAtual = DateHelper.getSystemDatetime();

        String chave = null;
        Long tempo = null;
        Date dataUltConsulta = null;

        Iterator<String> it = hora.keySet().iterator();
        while (it.hasNext()) {
            chave = it.next();
            tempo = hora.get(chave);
            dataUltConsulta = new Date(tempo.longValue());

            if (hourDiff(dataAtual, dataUltConsulta) < intervalo) {
                // Se ainda está no intervalo, mantém a chave
                result.put(chave, tempo);
            } else {
                // Se já não está mais no intervalo, então retira a chave
                tentativas.remove(chave);
            }
        }

        // Limpa o map das horas
        hora.clear();
        hora.putAll(result);
        result.clear();
    }

    /**
     * Limpa o cache para uma determinada consignação.
     * @param adeCodigo
     */
    public void resetTetantivasLogin(String adeCodigo) {
        hora.remove(adeCodigo);
        tentativas.remove(adeCodigo);
    }
}
