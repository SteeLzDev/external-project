package com.zetra.econsig.job.process;

import java.io.File;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: ProcessaLote</p>
 * <p>Description: Classe para processamento de arquivos
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaLote extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaLote.class);

    private final String nomeArquivoEntrada;
    private final String nomeArqXmlEntrada;
    private final String nomeArqXmlTradutor;

    private final String csaCodigo;
    private final String corCodigo;

    private final boolean validar;
    private final boolean permiteLoteAtrasado;
    private final java.util.Date periodoConfiguravel;
    private final boolean permiteReducaoLancamentoCartao;
    private final boolean serAtivo;

    private final AcessoSistema responsavel;

    public ProcessaLote(String nomeArquivoEntrada, String nomeArqXmlEntrada,
            String nomeArqXmlTradutor, boolean validar, String csaCodigo,
            String corCodigo, boolean permiteLoteAtrasado,
            boolean permiteReducaoLancamentoCartao, boolean serAtivo,
            java.util.Date periodoConfiguravel, AcessoSistema responsavel) {

        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.nomeArqXmlEntrada = nomeArqXmlEntrada;
        this.nomeArqXmlTradutor = nomeArqXmlTradutor;
        this.validar = validar;
        this.csaCodigo = csaCodigo;
        this.corCodigo = corCodigo;
        this.permiteLoteAtrasado = permiteLoteAtrasado;
        this.permiteReducaoLancamentoCartao = permiteReducaoLancamentoCartao;
        this.serAtivo = serAtivo;
        this.periodoConfiguravel = periodoConfiguravel;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        File arquivo = new File(nomeArquivoEntrada);

        try {
            if (!arquivo.exists()) {
                mensagem =  ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.em.processamento.aguarde.termino.processamento", responsavel);
                codigoRetorno = ERRO;
                return;
            }

            // Renomeia o arquivo que será processado para que não ocorra duplicação do processamento
            if (!validar) {
                final String novoNomeArquivoEntrada = nomeArquivoEntrada + ".prc";
                FileHelper.rename(arquivo.getAbsolutePath(), novoNomeArquivoEntrada);
                arquivo = new File(novoNomeArquivoEntrada);
            }

            // Faz a importação do arquivo de lote
            final LoteHelper lote = new LoteHelper(csaCodigo, corCodigo, validar, serAtivo, true, permiteLoteAtrasado, permiteReducaoLancamentoCartao, periodoConfiguravel, responsavel);
            lote.importarLote(nomeArqXmlEntrada, nomeArqXmlTradutor, arquivo.getName());

            // Define mensagem de sucesso e renomeia o arquivo em caso de processamento
            if (!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
                FileHelper.rename(arquivo.getAbsolutePath(), nomeArquivoEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;

            if (!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.lote", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage()) + "<br>"
                              + ApplicationResourcesHelper.getMessage("mensagem.informacao.nenhuma.operacao.arquivo.lote.foi.realizada", responsavel);

                // Restaura o nome do arquivo de entrada
                FileHelper.rename(arquivo.getAbsolutePath(), nomeArquivoEntrada);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.arquivo.lote", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
            }
        }
    }
}
