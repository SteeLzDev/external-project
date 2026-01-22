package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.folha.ExportaMovimentoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaExportacaoMovimentoOrgaoAutomaticamente</p>
 * <p>Description:Verifica se o parâmetro que permite exportação por órgão está habilitado, se estiver então inicia a verificação do dia e a exportação</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaExportacaoMovimentoOrgaoAutomaticamente extends ProcessoAgendadoPeriodico {

    public ProcessaExportacaoMovimentoOrgaoAutomaticamente(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        ExportaMovimentoController exportaMovimentoContoller = ApplicationContextProvider.getApplicationContext().getBean(ExportaMovimentoController.class);
        exportaMovimentoContoller.exportaMovimentoFinanceiroAutomaticoOrgao(getResponsavel());
    }
}
