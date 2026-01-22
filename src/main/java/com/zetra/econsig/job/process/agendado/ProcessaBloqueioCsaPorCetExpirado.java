package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBloqueioCsaPorCetExpirado.java</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias por CET / Taxa de Juros com data de vigência expirada.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioCsaPorCetExpirado extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaPorCetExpirado.class);

    public ProcessaBloqueioCsaPorCetExpirado(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();

        if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CSA_POR_CET_EXPIRADO, CodedValues.TPC_SIM, responsavel)) {
            LOG.debug("Executa Bloqueio de Consignatárias por CET / Taxa de Juros com data de vigência expirada");
            ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.bloqueiaCsaCetExpirado(responsavel);
        }
    }
}
