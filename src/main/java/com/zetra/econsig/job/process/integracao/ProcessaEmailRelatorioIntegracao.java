package com.zetra.econsig.job.process.integracao;

import java.util.ArrayList;

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
public class ProcessaEmailRelatorioIntegracao extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEmailRelatorioIntegracao.class);

    private final String email;
    private final String csaCodigo;
    private final String pathArquivo;
    private final ArrayList<String> arquivos;
    private final AcessoSistema responsavel;
    private final String nome;

    public ProcessaEmailRelatorioIntegracao(String email, String csaCodigo, String pathArquivo, ArrayList<String> arquivos, String nome, AcessoSistema responsavel) {
        this.email = email;
        this.csaCodigo = csaCodigo;
        this.pathArquivo = pathArquivo;
        this.arquivos = arquivos;
        this.responsavel = responsavel;
        this.nome = nome;
    }

    @Override
    protected void executar() {
        try {
            EnviaEmailHelper.enviarEmailRelatorioIntegracao(csaCodigo, email, pathArquivo, arquivos, nome, responsavel);
        } catch (ViewHelperException vex) {
            // esta exceção não deve barrar o processo de geração da comunicação
            LOG.error(vex.getMessage(), vex);
        }
    }

}
