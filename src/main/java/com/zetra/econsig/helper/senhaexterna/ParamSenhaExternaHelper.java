package com.zetra.econsig.helper.senhaexterna;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ParamSenhaExternaHelper</p>
 * <p>Description: Singleton repositório dos parâmetros de senha externa (substitui senhaexterna.properties)</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ParamSenhaExternaHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParamSenhaExternaHelper.class);

    private final Map<String, String> parametros;

    private static class SingletonHelper {
        private static final ParamSenhaExternaHelper instance = new ParamSenhaExternaHelper();
    }

    public static ParamSenhaExternaHelper getInstance() {
        return SingletonHelper.instance;
    }

    private ParamSenhaExternaHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            parametros = new ExternalMap<>();
        } else {
            parametros = new HashMap<>();
        }
    }

    private void carregarRecursosBanco() {
        try {
            if (parametros.isEmpty()) {
                synchronized (this) {
                    if (parametros.isEmpty()) {
                        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);

                        // Recarrega os textos à partir do banco
                        final List<TransferObject> lstParametros = sistemaController.lstParamSenhaExterna(AcessoSistema.getAcessoUsuarioSistema());
                        final Map<String, String> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : parametros;
                        for (final TransferObject texto : lstParametros) {
                            final String chave = texto.getAttribute(Columns.PSX_CHAVE).toString();
                            final String valor = texto.getAttribute(Columns.PSX_VALOR).toString();
                            mapForLoad.put(chave, valor);
                        }
                        if (ExternalCacheHelper.hasExternal() && parametros.isEmpty()) {
                            parametros.putAll(mapForLoad);
                        }
                    }
                }
            }
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

     public void reset() {
        parametros.clear();
    }

    /**
     * Retorna uma mensagem do senhaexterna.properties
     * @param chave : a chave da mensagem no senhaexterna
     * @return : valor correspondente a chave
     */
    public static String getValor(String chave) {
        SingletonHelper.instance.carregarRecursosBanco();

         // Busca mensagem padrão
        return SingletonHelper.instance.parametros.get(chave);
    }
}