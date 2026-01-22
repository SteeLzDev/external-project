package com.zetra.econsig.job.process.comunicacao;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ProcessaEnvioEmailCmnMsgPendente</p>
 * <p>Description: Dispara thread para envio de e-mail de alerta de mensagens pendentes do servidor </p>
 * <p>             para uma comunicação.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailCmnMsgPendente extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailCmnMsgPendente.class);
    
    private String cmnTexto;
    private Date cmnData;
    private Long cmnNumero;
    private String csaCodigo;
    private String usuCodigoSer;
    private AcessoSistema responsavel;
    
    public ProcessaEnvioEmailCmnMsgPendente (String cmnTexto, Date cmnData, Long cmnNumero, String usuCodigoSer, String csaCodigo, AcessoSistema responsavel) {
        this.cmnTexto = cmnTexto;
        this.cmnData = cmnData;
        this.cmnNumero = cmnNumero;
        this.csaCodigo = csaCodigo;
        this.usuCodigoSer = usuCodigoSer;
        this.responsavel = responsavel;
    }
    
    @Override
    protected void executar() {
        CustomTransferObject cmnTO = new CustomTransferObject();
        cmnTO.setAttribute(Columns.CMN_TEXTO, cmnTexto);
        cmnTO.setAttribute(Columns.CMN_DATA, cmnData);
        cmnTO.setAttribute(Columns.CMN_NUMERO, cmnNumero);
        cmnTO.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        cmnTO.setAttribute(Columns.CMN_USU_CODIGO, usuCodigoSer);
        try {
            EnviaEmailHelper.enviarEmailAlertaCmnMsgPendentes(cmnTO, responsavel);
        } catch (ViewHelperException vex) {
            // esta exceção não deve barrar o processo de geração da comunicação
            LOG.error(vex.getMessage(), vex);
        }

    }

}
