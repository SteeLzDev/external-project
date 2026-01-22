package com.zetra.econsig.job.process.comunicacao;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.Processo;

/**
 * <p>Title: ProcessaEmailAlertaNovaComunicacao</p>
 * <p>Description: Dispara thread para envio de e-mail de alerta de nova comunicação gerada no sistema.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEmailAlertaNovaComunicacao extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEmailAlertaNovaComunicacao.class);
    
    private String cmnUsuCodigo;
    private String email;
    private TransferObject comunicacao;
    private AcessoSistema responsavel;

    public ProcessaEmailAlertaNovaComunicacao(String cmnUsuCodigo, String email, TransferObject comunicacao, AcessoSistema responsavel) {
        this.cmnUsuCodigo = cmnUsuCodigo;
        this.email = email;
        this.comunicacao = comunicacao;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            EnviaEmailHelper.enviarEmailComunicacao(cmnUsuCodigo, email, comunicacao, responsavel);
        } catch (ViewHelperException vex) {
            // esta exceção não deve barrar o processo de geração da comunicação
            LOG.error(vex.getMessage(), vex);
        }
    }

}
