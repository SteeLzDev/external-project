package com.zetra.econsig.job.process;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.saldodevedor.ImportarSaldoDevedorHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaInfoSaldoDevedor</p>
 * <p>Description: Classe para processamento de Informação de Saldo Devedor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaInfoSaldoDevedor extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaInfoSaldoDevedor.class);

    private final String nomeArquivoEntrada;
    private final String csaCodigo;
    private final boolean validar;
    private final AcessoSistema responsavel;

    public ProcessaInfoSaldoDevedor(String nomeArquivoEntrada, String csaCodigo, boolean validar, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.csaCodigo = csaCodigo;
        this.validar = validar;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        ImportarSaldoDevedorHelper impSaldoDevedor = new ImportarSaldoDevedorHelper(responsavel);
        try {
            impSaldoDevedor.importar(nomeArquivoEntrada, csaCodigo,validar, responsavel);
            if(!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
            }

        } catch (ViewHelperException e) {
            LOG.error(e.getMessage(), e);
            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.saldo.devedor.processamento.lote", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, e.getMessage())
                          + "<br>";
        }

    }
}
