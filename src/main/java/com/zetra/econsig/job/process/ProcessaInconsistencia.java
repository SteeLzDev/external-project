package com.zetra.econsig.job.process;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.folha.ImportarRegraInconsistenciaHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaInconsistencia</p>
 * <p>Description: Classe para processamento de arquivos de inconsistência</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaInconsistencia extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaConciliacao.class);

    private final String nomeArquivoEntrada;
    private final AcessoSistema responsavel;

    public ProcessaInconsistencia(String nomeArquivoEntrada, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        ImportarRegraInconsistenciaHelper impRegraHelper = new ImportarRegraInconsistenciaHelper(responsavel);
        try {
            impRegraHelper.importaInconsistencia(nomeArquivoEntrada);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);

        } catch (ViewHelperException e) {
            LOG.error(e.getMessage(), e);
            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.inconsistencia", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, e.getMessage())
                          + "<br>";
        }

    }
}
