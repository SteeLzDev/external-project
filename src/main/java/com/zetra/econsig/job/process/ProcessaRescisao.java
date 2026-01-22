package com.zetra.econsig.job.process;

import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.rescisao.RescisaoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

public final class ProcessaRescisao extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRescisao.class);
    private final boolean validar;
    private final String nomeArquivoEntrada;
    private final AcessoSistema responsavel;



    public ProcessaRescisao(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) {
        this.nomeArquivoEntrada = nomeArquivoEntrada;
        this.responsavel = responsavel;
        this.validar = validar;

        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        RescisaoHelper rescisao = new RescisaoHelper(responsavel);
        try{
            rescisao.importar(nomeArquivoEntrada, validar, responsavel);
            if(!validar) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
            }
            LOG.debug(mensagem);
        } catch (ViewHelperException e) {
            LOG.error(e.getMessage(), e);
            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rescisao.processamento.lote", responsavel) + "<br>"
                    + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, e.getMessage())
                    + "<br>";
        } catch (VerbaRescisoriaControllerException e) {
            throw new RuntimeException(e);
        }
    }
}
