package com.zetra.econsig.job.process.agendado;

import java.util.Date;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBloqueioCsaAdeSemMinAnexos</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias por Contratos realizados por usuários CSA/COR sem um número
 *                 mínimo de anexos definido pelo parâmetro de serviço 284</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 24977 $
 * $Date: 2018-07-12 16:21:49 -0300 (qui, 12 jul 2018) $
 */
public class ProcessaBloqueioCsaAdeSemMinAnexos extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaAdeSemMinAnexos.class);
    private final AcessoSistema responsavel;

    public ProcessaBloqueioCsaAdeSemMinAnexos(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        this.responsavel = responsavel;
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa Bloqueio de Consignatárias que possuir contratos abertos sem o mínimo de anexos definido no param serviço 284
        LOG.debug("Executa Bloqueio de Consignatárias que possuir contratos abertos sem o mínimo de anexos definido no param serviço 284");
        String agdCodigo = getAgdCodigo();
        AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
        TransferObject agendamento = agendamentoController.findAgendamento(agdCodigo, responsavel);
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        csaDelegate.bloqueiaConsignatariasComAdeSemNumAnexosMin((Date) agendamento.getAttribute(Columns.AGD_DATA_CADASTRO), responsavel);
    }

}
