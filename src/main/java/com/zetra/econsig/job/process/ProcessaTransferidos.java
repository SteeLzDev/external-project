package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaTransferidos</p>
 * <p>Description: Classe para processamento de arquivos de transferidos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaTransferidos extends Processo {

    private final String nomeArquivoEntrada;
    private final String tipoEntidade;
    private final String codigoEntidade;

    private final AcessoSistema responsavel;

    public ProcessaTransferidos(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.tipoEntidade = tipoEntidade;
        this.codigoEntidade = codigoEntidade;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria os delegates necessários
            ServidorDelegate serDelegate = new ServidorDelegate();

            // Executa rotina de importação de transferidos
            serDelegate.importaServidoresTransferidos(nomeArquivoEntrada, tipoEntidade, codigoEntidade, responsavel);

            // Seta mensagem de sucesso na sessão do usuário
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.importacao.servidores.transferidos.sucesso", responsavel);
        } catch (ServidorControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.transferidos", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }}
