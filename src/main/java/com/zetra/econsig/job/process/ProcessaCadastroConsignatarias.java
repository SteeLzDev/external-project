package com.zetra.econsig.job.process;

import java.io.File;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaCadastroConsignatarias</p>
 * <p>Description: Classe para processamento de arquivo para cadastro de consignatárias</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaCadastroConsignatarias extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaCadastroConsignatarias.class);

    private final String nomeArquivoEntrada;
    private final boolean validar;
    private final AcessoSistema responsavel;

    public ProcessaCadastroConsignatarias(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.validar = validar;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {

            final File arquivo = new File(nomeArquivoEntrada);

            // Faz a importação do arquivo de lote
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.impCadastroConsignatarias(arquivo.getName(), validar, responsavel);

            // Caso seja validação não renomeia o arquivo
            if (!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
            }

        } catch (final Exception ex) {
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
