package com.zetra.econsig.helper.contabilizacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

public enum ContabilizacaoInclusaoContratos {
    CONTABILIZACAOCONTRATOS;

    private static final Log LOG = LogFactory.getLog(ContabilizacaoInclusaoContratos.class);

    private Map<String, Integer> contadorContratos;

    private Map<String, Long> ultimaInclusao;

    private long ultimaLimpeza;

    private final long intervaloLimpeza = 1000L * 60 * 60 * 24;

    private final long intervalo = 1000L * 60 * 60 * 24;

    private int limiteContratos;

    private List<String> notificacoesEnviadas;

    ContabilizacaoInclusaoContratos() {
        reset();
    }

    public void reset() {
        try {
            final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_QUANTIDADE_MINIMA_CONTRATOS_CSA_NO_DIA_NOTIFICAR_GESTOR, null);
            limiteContratos = param != null ? Integer.parseInt(param.toString()) : 0;
        } catch (final Exception ex) {
            limiteContratos = 0;
            LOG.error("Erro ao obter o limite de inclusões de contratos", ex);
        }

        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            contadorContratos = new ExternalMap<>(prefix + "-contadorContratos");
            ultimaInclusao = new ExternalMap<>(prefix + "-ultimaInclusao");
        } else {
            contadorContratos = new HashMap<>();
            ultimaInclusao = new HashMap<>();
        }

        notificacoesEnviadas = new ArrayList<>();
        ultimaLimpeza = Calendar.getInstance().getTimeInMillis();
    }

    public synchronized void contabilizarInclusao(String csaCodigo) {
        final long agora = Calendar.getInstance().getTimeInMillis();

        if (agora > (ultimaLimpeza + intervaloLimpeza)) {
            ultimaLimpeza = agora;
            limpa();
        }

        final Long ultimaData = ultimaInclusao.get(csaCodigo);
        if ((ultimaData == null) || ((agora - ultimaData) > intervalo)) {
            contadorContratos.put(csaCodigo, 1);
            ultimaInclusao.put(csaCodigo, agora);
        } else {
            final int contadorAtual = contadorContratos.getOrDefault(csaCodigo, 0) + 1;
            contadorContratos.put(csaCodigo, contadorAtual);

            if (contadorAtual >= limiteContratos) {
                notificarResponsavel(csaCodigo, contadorAtual);
            }
        }
    }

    private void notificarResponsavel(String csaCodigo, int contadorAtual) {

        if (notificacoesEnviadas.contains(csaCodigo)) {
            return;
        }

        try {
            EnviaEmailHelper.notificaCseLimiteConsignacaoCsa(csaCodigo, contadorAtual, AcessoSistema.getAcessoUsuarioSistema());
            notificacoesEnviadas.add(csaCodigo);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void limpa() {
        final long agora = Calendar.getInstance().getTimeInMillis();
        ultimaInclusao.entrySet().removeIf(entry -> (agora - entry.getValue()) > intervalo);
        contadorContratos.keySet().retainAll(ultimaInclusao.keySet());
        notificacoesEnviadas.clear();
    }

    public int obterContadorAtual(String csaCodigo) {
        return contadorContratos.getOrDefault(csaCodigo, 0);
    }
}
