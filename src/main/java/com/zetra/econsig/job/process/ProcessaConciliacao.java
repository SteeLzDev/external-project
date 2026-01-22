package com.zetra.econsig.job.process;

import java.io.File;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.folha.ConciliacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaConciliacao</p>
 * <p>Description: Classe para processamento de arquivos de conciliação</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaConciliacao extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaConciliacao.class);

    private String nomeArquivoEntrada;
    private final String nomeArqXmlEntrada;
    private final String nomeArqXmlTradutor;

    private final String csaCodigo;
    private final String tipoEntidade;
    private final String codigoEntidade;

    private final AcessoSistema responsavel;

    public ProcessaConciliacao(String nomeArquivoEntrada, String nomeArqXmlEntrada, String nomeArqXmlTradutor,
                               String csaCodigo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.nomeArqXmlEntrada = nomeArqXmlEntrada;
        this.nomeArqXmlTradutor = nomeArqXmlTradutor;
        this.csaCodigo = csaCodigo;
        this.tipoEntidade = tipoEntidade;
        this.codigoEntidade = codigoEntidade;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Renomeia o arquivo que será processado para que não ocorra duplicação do processamento
            File arquivo = new File(nomeArquivoEntrada);
            if (!arquivo.exists()) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.em.processamento.aguarde.termino.processamento", responsavel);
                codigoRetorno = ERRO;
                return;
            }
            FileHelper.rename(nomeArquivoEntrada, nomeArquivoEntrada + ".prc");
            nomeArquivoEntrada += ".prc";

            arquivo = new File(nomeArquivoEntrada);

            // Faz a importação do arquivo de conciliação
            ConciliacaoController conciliacaoController = ApplicationContextProvider.getApplicationContext().getBean(ConciliacaoController.class);
            conciliacaoController.conciliar(csaCodigo, nomeArqXmlEntrada, nomeArqXmlTradutor, tipoEntidade, codigoEntidade, arquivo.getName(), responsavel);

            // Renomeia o arquivo de conciliação
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            FileHelper.rename(nomeArquivoEntrada, nomeArquivoEntrada + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.conciliacao", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage())
                          + "<br>";
        }
    }
}
