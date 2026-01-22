package com.zetra.econsig.helper.sistema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: ViewImageHelper</p>
 * <p>Description: Helper para controle de imagens para views</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Fagner Fernandes, Leonel Martins
 */
public class ViewImageHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ViewImageHelper.class);

    private Map<String, byte []> carouselImagens;

    private static class SingletonHelper {
        private static final ViewImageHelper instance = new ViewImageHelper();
    }

    public static ViewImageHelper getInstance() {
        return SingletonHelper.instance;
    }

    private ViewImageHelper() {
        carregaImagensCarousel();
    }

    public void reset() {
        carouselImagens = null;
        carregaImagensCarousel();

    }

    private void carregaImagensCarousel() {
        if (carouselImagens == null) {
            if (ExternalCacheHelper.hasExternal()) {
                carouselImagens = new ExternalMap<>();
            } else {
                carouselImagens = new HashMap<>();
            }

            final ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();

            if (appContext != null) {
                final NaturezaServicoController naturezaServicoController = ApplicationContextProvider.getApplicationContext().getBean(NaturezaServicoController .class);

                try {
                    final List<NaturezaServico> lstNses = naturezaServicoController.listaNaturezas(null);
                    final Map<String, byte[]> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : carouselImagens;
                    for (final NaturezaServico nse: lstNses) {
                        mapForLoad.put(nse.getNseCodigo(), nse.getNseImagem());
                    }
                    if (ExternalCacheHelper.hasExternal() && carouselImagens.isEmpty()) {
                        carouselImagens.putAll(mapForLoad);
                    }
                } catch (final NaturezaServicoControllerException e) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null), e);
                }
            }
        }
    }

    public String buscarImagemEmpresa(String nseCodigo) {
        if (carouselImagens != null) {
            if (carouselImagens.get(nseCodigo) != null) {
                return TextHelper.encode64Binary(carouselImagens.get(nseCodigo));
            }
        } else {
            carregaImagensCarousel();
            if (carouselImagens.get(nseCodigo) != null) {
                return TextHelper.encode64Binary(carouselImagens.get(nseCodigo));
            }
        }

        return "";
    }
}
