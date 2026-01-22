package com.zetra.econsig.job.process.agendado;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.command.EnviarEmailMensalAtualizarTaxasCommand;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: ProcessaEmailComunicadoAtualizarCet</p>
 * <p>Description: Processo períodico que lista todas as consignatárias que possuem o parâmetro de serviço TPS_DIAS_VIGENCIA_CET definido
 *                 e envia um e-mail de comunicado de atualização de suas taxas para cada ma delas.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
/**
 * Processo períodico que lista todas as consignatárias que possuem o parâmetro de serviço TPS_DIAS_VIGENCIA_CET definido
 * e envia um e-mail de comunicado de atualização de suas taxas para cada ma delas.
 * @author fagner
 *
 */
public class ProcessaEmailComunicadoAtualizarCet extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEmailComunicadoAtualizarCet.class);

    public ProcessaEmailComunicadoAtualizarCet(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        final EnviarEmailMensalAtualizarTaxasCommand enviarEmailMensalAtualizarTaxas = new EnviarEmailMensalAtualizarTaxasCommand();
        List<TransferObject> lstCsas = consignatariaController.lstConsignatariaByTpsCodigo(CodedValues.TPS_DIAS_VIGENCIA_CET, AcessoSistema.getAcessoUsuarioSistema());

        if (lstCsas != null && !lstCsas.isEmpty()) {
            lstCsas.stream().forEach((csa -> {
                enviarEmailMensalAtualizarTaxas.setCsaCodigo(csa);
                try {
                    enviarEmailMensalAtualizarTaxas.execute();
                } catch (ViewHelperException e) {
                    LOG.error("mensagem.erro.email.enviar", e);
                }
            }));
        }
    }

}
