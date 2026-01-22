package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.web.ApplicationContextProvider;

public class ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio.class);

    public ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
    	final AcessoSistema responsavel = getResponsavel();
    	final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
    	try {
    		LOG.debug("Envia Notificação para CSAs sobre alteração das regras de convênio");
			relatorioController.enviarNotificacaoCsaAlteracaoRegrasConvenio(responsavel);
		} catch (RelatorioControllerException e) {
			LOG.error(e.getMessage(), e);
		}
    }	
}
