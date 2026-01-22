package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaRetorno</p>
 * <p>Description: Classe para processamento de arquivos de retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRetorno extends Processo {

    protected final String nomeArquivoEntrada;
    protected final String orgCodigo;
    protected final String estCodigo;
    protected final String tipo;
    protected final java.sql.Date periodoRetAtrasado;

    protected final AcessoSistema responsavel;

    public ProcessaRetorno(String nomeArquivoEntrada, String orgCodigo, String estCodigo, String tipo, java.sql.Date periodoRetAtrasado, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.orgCodigo = orgCodigo;
        this.estCodigo = estCodigo;
        this.tipo = tipo;
        this.responsavel = responsavel;
        this.periodoRetAtrasado = periodoRetAtrasado;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria o delegate necessário para o processo
            ImpRetornoDelegate delegate = new ImpRetornoDelegate();
            // Executa rotina de importação de retorno
            delegate.importarRetornoIntegracao(nomeArquivoEntrada, orgCodigo, estCodigo, tipo, periodoRetAtrasado, responsavel);
            // Cria mensagem de sucesso para a importação
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.importacao.arg0.integracao.realizada.sucesso", responsavel, tipo);
        } catch (ImpRetornoControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.arg0", responsavel, tipo) + "<br>"
                + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
