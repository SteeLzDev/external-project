package com.zetra.econsig.helper.solicitacaosuporte;

import com.zetra.econsig.helper.solicitacaosuporte.jira.SolicitacaoSuporteAPIJira;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.web.ApplicationContextProvider;

public class SolicitacaoSuporteAPIFactory {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoSuporteAPIFactory.class);

    private static final String SISTEMA_JIRA = "JIRA";

    public SolicitacaoSuporteAPI getSolicitacaoSuporteAPI() {
        final SolicitacaoSuporteConfig ssc;
        try {
            ssc = ApplicationContextProvider.getApplicationContext().getBean(SolicitacaoSuporteConfig.class);
            final String sistemaName = ssc.getSistemaSolicitacao();

            if (!TextHelper.isNull(sistemaName) && sistemaName.equals(SISTEMA_JIRA)) {
                return new SolicitacaoSuporteAPIJira();
            }

        } catch (final Exception ex) {
            LOG.error("Erro interno de sistema ao ler arquivo de propriedades interface jira.", ex);
        }

        return null;
    }
}
