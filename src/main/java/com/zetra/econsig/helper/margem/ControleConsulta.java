package com.zetra.econsig.helper.margem;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ControleConsulta</p>
 * <p>Description: Controla a consulta de margem, limitando o número de operações
 * realizadas. O controle de consultas é configurado através dos parâmetros de
 * sistema TPC_LIMITE_CONSULTAS_MARGEM, TPC_LIMITE_CONSULTA_MARGEM_POR_USUARIO e
 * TPC_QTD_DIAS_LIMITE_CONSULTA_MARGEM.</p>
 * <p>Copyright: Copyright (c) 2003-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor Lucas, Leonel Martins
 */
public class ControleConsulta {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleConsulta.class);

    // Flag que indica se o controle é por consignatária ou por usuário
    private boolean controlePorUsuario;
    // Flag que indica se o limite do numero de consultas de margem é global
    // (independe do servidor) ou é por servidor
    private boolean limiteGlobal;
    // Quantidade de consultas máximas em um dado intervalo de tempo
    private int limite;
    // Intervalo de tempo em dias, onde a contagem das consultas deve ser reiniciado
    private int intervalo;
    // Faz um mapeamento onde a chave é o csaCodigo|rseCodigo ou usuCodigo|rseCodigo
    // e o valor é o tempo da primeira consulta, indicando o inicio do período
    private Map<String, Long> hora;
    // Faz um mapeamento onde a chave é o csaCodigo|rseCodigo ou usuCodigo|rseCodigo
    // e o valor é o número de consultas realizadas no período
    private Map<String, Integer> consultas;
    // Quantidade de consultas máximas, em um dado intervalo de tempo,
    // para cada uma das consignatárias
    private Map<String, Integer> limiteCsa;
    // Tempo, em milisegundos, da realização da última limpeza
    private long ultimaLimpeza = Calendar.getInstance().getTimeInMillis();
    // Intervalo de tempo, em milisegundos, da realização de limpezas no cache
    private final long intervaloLimpeza = 1000 * 60 * 60 * 24;
    // Quantidade de vezes em que se pode consultar margem sem captcha
    private Integer quantidadeSemCaptcha;
    // Quantidade de vezes que pode consultar margem SER
    private Integer quantidadeSemCaptchaSer;
    // Quantidade de vezes em que se pode consultar consignação sem captcha
    private Integer quantidadeSemCaptchaConsultaConsignacao;
    // Mapeamento de consulta de margens por dia
    private Map<String, Integer> consultaDiaria;
    // Ultima limpeza captcha
    private long ultimaLimpezaCaptcha = DateHelper.getSystemDate().getTime();
    // Ultima limpeza captcha SER
    private long ultimaLimpezaCaptchaSer = DateHelper.getSystemDate().getTime();

    // Sinlgeton, instância única desta classe de controle de acesso
    private static class SingletonHelper {
        private static final ControleConsulta instance = new ControleConsulta();
    }

    public static ControleConsulta getInstance() {
        return SingletonHelper.instance;
    }

    /**
     * Construtor privado. Só é criado apenas uma instância da
     * classe para o controle de consultas de margem.
     */
    private ControleConsulta() {
        reset();
    }

    public void reset() {
        try {
            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_CONSULTAS_MARGEM, null);
            limite = param != null ? Integer.parseInt(param.toString()) : 0;

            param = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_CONSULTA_MARGEM_POR_USUARIO, null);
            controlePorUsuario = (param != null) && CodedValues.TPC_SIM.equals(param.toString());

            param = ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_CONSULTAS_MARGEM_GLOBAL, null);
            limiteGlobal = (param != null) && CodedValues.TPC_SIM.equals(param.toString());

            param = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_DIAS_LIMITE_CONSULTA_MARGEM, null);
            if ((param != null) && !TextHelper.isNull(param)) {
                intervalo = Integer.parseInt(param.toString());
            } else {
                intervalo = 0;
            }

            param = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_MARGEM, null);
            if ((param != null) && !TextHelper.isNull(param)) {
                quantidadeSemCaptcha = Integer.parseInt(param.toString());
            } else {
                quantidadeSemCaptcha = null;
            }

            param = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_CONSIGNACAO, null);
            if ((param != null) && !TextHelper.isNull(param)) {
                quantidadeSemCaptchaConsultaConsignacao = Integer.parseInt(param.toString());
            } else {
                quantidadeSemCaptchaConsultaConsignacao = null;
            }


            param = ParamSist.getInstance().getParam(CodedValues.TPC_MAX_CONSULTA_MARGEM_SER, null);
            if ((param != null) && !TextHelper.isNull(param)) {
                quantidadeSemCaptchaSer = Integer.parseInt(param.toString());
            } else {
                quantidadeSemCaptchaSer = null;
            }

        } catch (final Exception ex) {
            limite = 0;
            intervalo = 0;
            controlePorUsuario = false;
        }

        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            hora = new ExternalMap<>(prefix + "-hora");
            consultas = new ExternalMap<>(prefix + "-consultas");
            limiteCsa = new ExternalMap<>(prefix + "-limiteCsa");
            consultaDiaria = new ExternalMap<>(prefix + "-consultaDiaria");
        } else {
            hora = new HashMap<>();
            consultas = new HashMap<>();
            limiteCsa = new HashMap<>();
            consultaDiaria = new HashMap<>();
        }
    }

    /**
     * Verifica se a consignatária pode consultar a margem do servidor.
     * @param csaCodigo : código da consignatária que está consultando a margem
     * @param usuCodigo : código do usuário que está consultando a margem
     * @param rseCodigo : codigo do registro do servidor
     * @return : true se a consulta pode ser feita
     */
    public boolean podeConsultar(String csaCodigo, String usuCodigo, String rseCodigo, AcessoSistema responsavel) {
        // Inicialmente, o limite de consultas é o valor passado atraves do param sistema
        int limiteConsultas = responsavel.getQtdConsultasMargem() != null ? responsavel.getQtdConsultasMargem() : limite;

        if (limiteConsultas == 0) {
            return true;
        } else {
            /**
             * Se TPC_LIMITE_CONSULTAS_MARGEM != 0, obtem o valor de
             * TPA_LIMITE_CONSULTAS_MARGEM pois este sobrepõe o valor do anterior
             */
            try {
                if (!limiteCsa.containsKey(csaCodigo)) {
                    // Se não existe o parâmetro para a consignatária então busca da base de dados
                    final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                    final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_LIMITE_CONSULTAS_MARGEM, responsavel);
                    int limiteGeralCsa = !TextHelper.isNull(pcsVlr) ? Integer.parseInt(pcsVlr.toString()) : 0;

                    // DESENV-9262: limite geral da CSA só sobrepõe o limite de consultas a verificar se o limite específico do usuário for nulo
                    if (!TextHelper.isNull(pcsVlr) && (responsavel.getQtdConsultasMargem() == null)) {
                        limiteConsultas = limiteGeralCsa;
                    }
                    // cache da CSA continuará armazenando ou o limite geral desta ou o limite geral do sistema
                    limiteCsa.put(csaCodigo, limiteGeralCsa > 0 ? limiteGeralCsa : limite);
                } else if (responsavel.getQtdConsultasMargem() == null) {
                    // Usa o parâmetro já selecionado para a consignatária
                    limiteConsultas = limiteCsa.get(csaCodigo);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
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

        // Chave dos mapeamentos.
        String chave = null;
        if (limiteGlobal) {
            // Se controle por usuário então a chave é usuCodigo, senão csaCodigo
            // DESENV-9262: se limite de consulta exclusiva do usuário responsável estiver setado (USU_QTD_CONSULTAS_MARGEM),
            //              se for controle por CSA, será criada também uma chave exclusiva no cache para este. A chave será composta
            //              por "csaCodigo + | + usuCodigo"
            chave = controlePorUsuario ? usuCodigo : responsavel.getQtdConsultasMargem() == null ? csaCodigo : csaCodigo + "|" + usuCodigo;
        } else {
            // Se controle por usuário então a chave é usuCodigo|rseCodigo, senão csaCodigo|rseCodigo
            chave = (controlePorUsuario ? usuCodigo : responsavel.getQtdConsultasMargem() == null ? csaCodigo : csaCodigo + "|" + usuCodigo)
                  + "|" + rseCodigo;
        }

        final Long tempo = hora.get(chave);

        if (tempo == null ) {
            // Se é a primeira consulta, então adiciona os valores aos mapeamentos
            hora.put(chave, agora);
            consultas.put(chave, 1);

        } else {
            // Pega a data da última consulta
            final Date dataUltConsulta = new Date(tempo);
            // Pega a data atual
            final Date dataAtual = new Date(agora);

            // Se a diferença em dias é maior do que o intervalo,
            // então reinicializa o controle
            if (dayDiff(dataAtual, dataUltConsulta) >= intervalo) {
                hora.put(chave, agora);
                consultas.put(chave, 1);
            } else {
                // Se ainda está dentro do intervalo, verifica a qtd
                // de consultas já realizadas
                final Integer qtd = consultas.get(chave);
                final int qtdConsultas = qtd != null ? qtd : 0;

                if (qtdConsultas >= limiteConsultas) {
                    // Se o limite foi alcançado, então bloqueia a consulta
                    return false;
                } else {
                    // Se o limite ainda não foi alcançado,
                    // atualiza o contador e permite a consulta
                    consultas.put(chave, qtdConsultas + 1);
                }
            }
        }
        return true;
    }

    /**
     * Verifica se pode consultar margem sem captcha.
     */
    public boolean podeConsultarMargemSemCaptcha(String usuCodigo) {
        if (quantidadeSemCaptcha == null) {
            return true;
        }

        final long agora = DateHelper.getSystemDate().getTime();

        // Verifica a necessidade de uma limpeza nos hashs
        if (agora > ultimaLimpezaCaptcha) {
            synchronized (this) {
                if (agora > ultimaLimpezaCaptcha) {
                    // Se a ultima limpeza já foi feita fora do intervalo de limpeza
                    ultimaLimpezaCaptcha = agora;
                    limparCaptcha();
                }
            }
        }

        final String chave = gerarChave(usuCodigo);

        final Integer quantidade = consultaDiaria.computeIfAbsent(chave, k -> 0);

        return (quantidade.compareTo(quantidadeSemCaptcha) < 0);
    }

    public boolean podeConsultarMargemSemCaptchaSer(String usuCodigo) {
        if (quantidadeSemCaptchaSer == null) {
            return true;
        }

        final long agora = DateHelper.getSystemDate().getTime();

        // Verifica a necessidade de uma limpeza nos hashs
        if (agora > ultimaLimpezaCaptchaSer) {
            synchronized (this) {
                if (agora > ultimaLimpezaCaptchaSer) {
                    // Se a ultima limpeza já foi feita fora do intervalo de limpeza
                    ultimaLimpezaCaptchaSer = agora;
                    limparCaptcha();
                }
            }
        }

        final String chave = gerarChaveSer(usuCodigo);

        final Integer quantidade = consultaDiaria.computeIfAbsent(chave, k -> 0);

        return (quantidade.compareTo(quantidadeSemCaptchaSer) < 0);
    }

    /**
     * Verifica se pode consultar consignacao sem captcha.
     */
    public boolean podeConsultarConsignacaoSemCaptcha(String usuCodigo) {
        if (quantidadeSemCaptchaConsultaConsignacao == null) {
            return true;
        }

        final long agora = DateHelper.getSystemDate().getTime();

        // Verifica a necessidade de uma limpeza nos hashs
        if (agora > ultimaLimpezaCaptcha) {
            synchronized (this) {
                if (agora > ultimaLimpezaCaptcha) {
                    // Se a ultima limpeza já foi feita fora do intervalo de limpeza
                    ultimaLimpezaCaptcha = agora;
                    limparCaptcha();
                }
            }
        }

        final String chave = gerarChave(usuCodigo);

        final Integer quantidade = consultaDiaria.computeIfAbsent(chave, k -> 0);

        return (quantidade.compareTo(quantidadeSemCaptchaConsultaConsignacao) < 0);
    }

    public void somarValorCaptcha (String usuCodigo) {
        if ((quantidadeSemCaptcha == null) && (quantidadeSemCaptchaConsultaConsignacao == null)) {
            return;
        }

        final String chave = gerarChave(usuCodigo);

        final Integer quantidade = consultaDiaria.computeIfAbsent(chave, k -> 0);

        consultaDiaria.put(chave, quantidade + 1);
    }

    public void somarValorCaptchaSer(String usuCodigo) {
        if ((quantidadeSemCaptcha == null) && (quantidadeSemCaptchaConsultaConsignacao == null)) {
            return;
        }

        final String chave = gerarChaveSer(usuCodigo);

        if (!consultaDiaria.containsKey(chave)) {
            consultaDiaria.put(chave, 0);
        }

        final Integer quantidade = consultaDiaria.get(chave);
        consultaDiaria.put(chave, quantidade + 1);
    }

    private void limparCaptcha() {
        try {
            final Set<Entry<String, Integer>> entries = consultaDiaria.entrySet();

            final Date hoje = DateHelper.getSystemDate();

            final Iterator<Entry<String, Integer>> itEntries= entries.iterator();
            while (itEntries.hasNext()) {
                final Entry<String, Integer> entry = itEntries.next();

                final String key = entry.getKey();
                final Date data = DateHelper.parse(key.substring(0, key.indexOf("|")), "yyyy-MM-dd");

                if (data.before(hoje)){
                    itEntries.remove();
                }
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String gerarChave (String usuCodigo) {
        final Date dataAtual = DateHelper.getSystemDatetime();
        final StringBuilder result = new StringBuilder();
        result.append(DateHelper.format(dataAtual, "yyyy-MM-dd")).append("|").append(usuCodigo);
        return result.toString();
    }

    public String gerarChaveSer(String usuCodigo) {
        final Date dataAtual = DateHelper.getSystemDatetime();
        final StringBuilder result = new StringBuilder();
        result.append(DateHelper.format(dataAtual, "yyyy-MM-dd")).append("|").append(usuCodigo).append("SER");
        return result.toString();
    }

    /**
     * Retira da memória a referência a consultas que já estão fora do
     * intervalo de tempo.
     */
    private void limpa() {
        final Map<String, Long> result = new HashMap<>();

        final Date dataAtual = DateHelper.getSystemDatetime();

        String chave = null;
        Long tempo = null;
        Date dataUltConsulta = null;

        for (final String element : hora.keySet()) {
            chave = element;
            tempo = hora.get(chave);
            dataUltConsulta = new Date(tempo);

            if (dayDiff(dataAtual, dataUltConsulta) < intervalo) {
                // Se ainda está no intervalo, mantém a chave
                result.put(chave, tempo);
            } else {
                // Se já não está mais no intervalo, então retira a chave
                consultas.remove(chave);
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
    private int dayDiff(Date a, Date b) {
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
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;
        }

        return difference;
    }
}