package com.zetra.econsig.helper.parametro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

public class ParamCsa {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParamCsa.class);

    private static final Map<String, String> cache;

    static {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
    }

    public ParamCsa() {
    }

    public static synchronized void reset() {
        cache.clear();
    }

    private static String getKey(String csaCodigo, String tpaCodigo) {
        return String.format("%s;%s", csaCodigo, tpaCodigo);
    }

    public static synchronized String getParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) {
        if (cache.isEmpty()) {
            loadParamCsa(responsavel);
        }
        return cache.get(getKey(csaCodigo, tpaCodigo));
    }

    public static synchronized void setParamCsa(String csaCodigo, String tpaCodigo, String pcsVlr, AcessoSistema responsavel) {
        cache.put(getKey(csaCodigo, tpaCodigo), pcsVlr);
    }

    private static synchronized void loadParamCsa(AcessoSistema responsavel) {
        try {
            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            final List<TransferObject> parametros = parametroController.selectParamCsaNaoNulo(responsavel);
            for (TransferObject parametro : parametros) {
                final String csaCodigo = parametro.getAttribute(Columns.PCS_CSA_CODIGO).toString();
                final String tpaCodigo = parametro.getAttribute(Columns.TPA_CODIGO).toString();
                final String pcsVlr = (String) parametro.getAttribute(Columns.PCS_VLR);
                setParamCsa(csaCodigo, tpaCodigo, pcsVlr, responsavel);
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
