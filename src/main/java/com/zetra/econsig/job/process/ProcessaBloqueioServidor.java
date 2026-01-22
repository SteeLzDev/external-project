package com.zetra.econsig.job.process;

import java.io.File;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: ProcessaBloqueioServidor</p>
 * <p>Description: Classe para processamento de arquivo de bloqueio de servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioServidor extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioServidor.class);

    private final String nomeArquivo;
    private final AcessoSistema responsavel;

    public ProcessaBloqueioServidor(String nomeArquivo, AcessoSistema responsavel) {
        this.nomeArquivo = nomeArquivo;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        String fileName = ParamSist.getDiretorioRaizArquivos();
        if(responsavel.isCsa()) {
            fileName += File.separatorChar + "bloqueio_ser" + File.separatorChar + "csa" + File.separatorChar + responsavel.getCsaCodigo() + File.separatorChar + nomeArquivo;
        } else {
            fileName += File.separatorChar + "bloqueio_ser" + File.separatorChar + "cse" + File.separatorChar + nomeArquivo;
        }

        try {
            File arquivo = new File(fileName);
            if (!arquivo.exists()) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.arquivo.nao.encontrado", responsavel);
                codigoRetorno = ERRO;
                return;
            }

            ServidorDelegate servidorDelegate = new ServidorDelegate();
            servidorDelegate.importarBloqueioServidor(nomeArquivo, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.bloqueio.servidor", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage()) + "<br>"
                          + ApplicationResourcesHelper.getMessage("mensagem.informacao.nenhuma.operacao.arquivo.lote.foi.realizada", responsavel);
        }
    }

}
