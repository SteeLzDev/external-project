package com.zetra.econsig.helper.email;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.NotificacaoEmailControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.notificacao.NotificacaoEmailController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ControleEnvioEmail</p>
 * <p>Description: Controla tipo de notificação de email que deve ser enviado automaticamente ou agendado.</p>
 * <p>Copyright: Copyright (c) 2003-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class ControleEnvioEmail {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleEnvioEmail.class);

    private static final String DIARIO = "D";
    private static final String IMEDIATO = "I";

    private Map<TipoNotificacaoEnum, String> cache = null;

    private static class SingletonHelper {
        private static final ControleEnvioEmail instance = new ControleEnvioEmail();
    }

    public static ControleEnvioEmail getInstance() {
        return SingletonHelper.instance;
    }

    private ControleEnvioEmail() {
        reset();
    }

    public void reset() {
        cache = null;
    }

    public boolean enviar(TipoNotificacaoEnum tipoNotificacao, TransferObject email, AcessoSistema responsavel) throws NotificacaoEmailControllerException {

        if ((cache == null) || cache.isEmpty()) {
            recuperaTipoNotificacao(responsavel);
        }

        if (tipoNotificacao == null) {
            return true;
        }

        if (responsavel == null) {
            responsavel = AcessoSistema.getAcessoUsuarioSistema();
        }

        // Se for envio imediato, altera data de envio para agora
        if (cache.get(tipoNotificacao).equals(IMEDIATO)) {
            email.setAttribute(Columns.NEM_DATA_ENVIO, DateHelper.getSystemDatetime());
        }

        // Agendar envio de email
        agendarEnvioEmail(tipoNotificacao, email, responsavel);

        // Se for envio diario, retorna que envio não será realizado agora
        return !cache.get(tipoNotificacao).equals(DIARIO);
    }

    private static void agendarEnvioEmail(TipoNotificacaoEnum tipoNotificacao, TransferObject email, AcessoSistema responsavel) throws NotificacaoEmailControllerException {
        final NotificacaoEmailController notificacaoEmailController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoEmailController.class);
        final String nemDestinatario = (String) email.getAttribute(Columns.NEM_DESTINATARIO);
        final String nemTitulo = (String) email.getAttribute(Columns.NEM_TITULO);
        final String nemTexto = (String) email.getAttribute(Columns.NEM_TEXTO);
        final Date nemData = DateHelper.getSystemDatetime();
        final Date nemDataEnvio = !TextHelper.isNull(email.getAttribute(Columns.NEM_DATA_ENVIO)) ? (Date) email.getAttribute(Columns.NEM_DATA_ENVIO) : null;
        notificacaoEmailController.criarNotificacao(tipoNotificacao.getCodigo(), nemDestinatario, nemTitulo, nemTexto, nemData, nemDataEnvio, responsavel);
    }

    private void recuperaTipoNotificacao(AcessoSistema responsavel) {
        try {
            if (ExternalCacheHelper.hasExternal()) {
                cache = new ExternalMap<>();
            } else {
                cache = new EnumMap<>(TipoNotificacaoEnum.class);
            }

            final NotificacaoEmailController notificacaoEmailController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoEmailController.class);
            final List<TransferObject> lstTipoNotificacao = notificacaoEmailController.lstTipoNotificacao(responsavel);

            if ((lstTipoNotificacao != null) && !lstTipoNotificacao.isEmpty()) {
                final Map<TipoNotificacaoEnum, String> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                for (final TransferObject tno : lstTipoNotificacao) {
                    final String tnoCodigo = (String) tno.getAttribute(Columns.TNO_CODIGO);
                    final String tnoEnvio = (String) tno.getAttribute(Columns.TNO_ENVIO);
                    try {
                        final TipoNotificacaoEnum t = TipoNotificacaoEnum.recuperaTipoNotificacao(tnoCodigo);

                        mapForLoad.put(t, tnoEnvio);

                    } catch (final Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
                if (ExternalCacheHelper.hasExternal() && cache.isEmpty()) {
                    cache.putAll(mapForLoad);
                }
            }
        } catch (final NotificacaoEmailControllerException e) {
            // TODO Levantar exceção?
            LOG.error(e.getMessage(), e);
        }
    }

}
