package com.zetra.econsig.helper.comunicacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ComunicacaoPermitida;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ControleComunicacaoPermitida</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class ControleComunicacaoPermitida {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleComunicacaoPermitida.class);

    private Map<String, Map<String, Boolean>> cache;

    private static class SingletonHelper {
        private static final ControleComunicacaoPermitida instance = new ControleComunicacaoPermitida();
    }

    public static ControleComunicacaoPermitida getInstance() {
        return SingletonHelper.instance;
    }

    private ControleComunicacaoPermitida() {
        reset();
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }

        try {
            final ComunicacaoController comunicacaoController = ApplicationContextProvider.getApplicationContext().getBean(ComunicacaoController.class);
            final List<ComunicacaoPermitida> cmnPermitida = comunicacaoController.listaComunicacaoPermitida(AcessoSistema.getAcessoUsuarioSistema());

            if ((cmnPermitida != null) && !cmnPermitida.isEmpty()) {
                final Map<String, Map<String, Boolean>> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                for (final ComunicacaoPermitida comunicacaoPermitida : cmnPermitida) {
                    final String papCodigoRemetente = comunicacaoPermitida.getPapelRemetente().getPapCodigo();
                    final Map<String, Boolean> destinatario = mapForLoad.computeIfAbsent(papCodigoRemetente, k -> new HashMap<>());
                    destinatario.put(comunicacaoPermitida.getPapelDestinatario().getPapCodigo(), Boolean.TRUE);
                }
                if (ExternalCacheHelper.hasExternal() && cache.isEmpty()) {
                    cache.putAll(mapForLoad);
                }
            }

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public synchronized boolean permite(String papCodigoRemetente, String papCodigoDestinatario) {
        if (TextHelper.isNull(papCodigoRemetente) || TextHelper.isNull(papCodigoDestinatario)) {
            return false;
        }

        final Map<String, Boolean> destinatarios = cache.get(papCodigoRemetente);
        return (destinatarios != null) && !TextHelper.isNull(destinatarios.get(papCodigoDestinatario));
    }

    public Map<String, Map<String, Boolean>> lstComunicacaoPermitida() {
        final Map<String, Map<String, Boolean>> retorno = new HashMap<>();

        for (final Map.Entry<String, Map<String, Boolean>> cacheEntry : cache.entrySet()) {
            final String tenCodigo = cacheEntry.getKey();
            for (final Map.Entry<String, Boolean> valoresEntry : cacheEntry.getValue().entrySet()) {
                final Boolean tenCampo = valoresEntry.getValue();

                final Map<String, Boolean> x = retorno.computeIfAbsent(tenCodigo, k -> new HashMap<>());
                x.put(tenCodigo, tenCampo);
            }
        }

        return retorno;
    }
}
