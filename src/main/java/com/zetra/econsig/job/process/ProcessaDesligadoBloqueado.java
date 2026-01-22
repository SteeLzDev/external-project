package com.zetra.econsig.job.process;

import java.io.File;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaLote</p>
 * <p>Description: Classe para processamento de arquivos de servidores desligados e bloqueados</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaDesligadoBloqueado extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDesligadoBloqueado.class);

    private final String nomeArquivoEntrada;
    private final boolean validar;
    private final AcessoSistema responsavel;

    public ProcessaDesligadoBloqueado(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.validar = validar;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {

            File arquivo = new File(nomeArquivoEntrada);

            // Faz a importação do arquivo de lote
            ServidorDelegate serDelegate = new ServidorDelegate();
            serDelegate.importaDesligadoBloqueado(arquivo.getName(), validar, responsavel);

            // Caso seja validação não renomeia o arquivo
            if (!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;

            if (!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.lote", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage())
                              + "<br>"
                              + ApplicationResourcesHelper.getMessage("mensagem.informacao.nenhuma.operacao.arquivo.lote.foi.realizada", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.arquivo.lote", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
            }
        }
    }
}
