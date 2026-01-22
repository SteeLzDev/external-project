package com.zetra.econsig.job.process.boleto;

import java.io.File;
import java.util.List;

import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.service.boleto.BoletoServidorController;

/**
 * <p>Title: ProcessaBoletoServidor</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBoletoServidor extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBoletoServidor.class);

    private final BoletoServidorController boletoServidorController;

    private final File zipCarregado;
    private final AcessoSistema responsavel;

    public ProcessaBoletoServidor(File zipCarregado, BoletoServidorController boletoServidorController, AcessoSistema responsavel) {
        this.zipCarregado = zipCarregado;
        this.responsavel = responsavel;
        this.boletoServidorController = boletoServidorController;
    }

    @Override
    protected void executar() {
        try {
            List<String> critica = boletoServidorController.uploadBoleto(zipCarregado, responsavel);

            if (critica == null || critica.isEmpty()) {
                setMensagemSucesso();
            } else {
                setMensagemProcessamentoParcial(critica);
            }

        } catch (BoletoServidorControllerException ex) {
            LOG.debug(ex.getMessage(), ex);
            setMensagemErro(ex.getMessage());
        }
    }

    /**
     * Salva uma mensagem de erro para o processo de upload.
     * @param mensagem
     * @return
     */
    private String setMensagemErro(String mensagem) {
        codigoRetorno = ProcessaBoletoServidor.ERRO;
        this.mensagem = mensagem;
        return this.mensagem;
    }

    /**
     * Gera uma mensagem de sucesso para o processo de upload.
     * @param nomeArquivoUpload Nome do arquivo que foi copiado para o servidor.
     * @param gerouRelatorio Indica se o processo de upload gerou relatório.
     * @return Mensagem gerada.
     */
    private String setMensagemSucesso() {
        codigoRetorno = ProcessaBoletoServidor.SUCESSO;
        mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.boleto.lote.sucesso", responsavel) + "<br/>";
        return mensagem;
    }

    /**
     * Gera uma mensagem de sucesso para o processo de upload.
     * @param nomeArquivoUpload Nome do arquivo que foi copiado para o servidor.
     * @param gerouRelatorio Indica se o processo de upload gerou relatório.
     * @return Mensagem gerada.
     */
    private String setMensagemProcessamentoParcial(List<String> critica) {
        codigoRetorno = ProcessaBoletoServidor.AVISO;
        mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.boleto.lote.parcial", responsavel, TextHelper.join(critica, ", ")) + "<br/>";
        return mensagem;
    }

}
