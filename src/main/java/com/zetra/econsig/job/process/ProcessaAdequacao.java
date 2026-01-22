package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.consignacao.AdequacaoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaAdequacao</p>
 * <p>Description: Classe para processamento de arquivos de adequação à margem</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaAdequacao extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaAdequacao.class);

    private final String nomeArquivoEntrada;
    private final boolean validar;
    private final AcessoSistema responsavel;

    public ProcessaAdequacao(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.validar = validar;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Faz a importação do arquivo de adequação
            AdequacaoHelper.processar(nomeArquivoEntrada, validar, responsavel);

            // Renomeia o arquivo de conciliação
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.processar.arquivo", responsavel, nomeArquivoEntrada) + "<br>"
                     + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage()) + "<br>";
        }
    }
}
