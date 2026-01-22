package com.zetra.econsig.web.listener;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OcorrenciaCseListener</p>
 * <p>Description: Listener que gera ocorrência cse de inicialização de sistema</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class OcorrenciaCseListener {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OcorrenciaCseListener.class);

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        try {
            cseDelegate.createOcorrenciaCse(CodedValues.TOC_INICIALIZANDO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
