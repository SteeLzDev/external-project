package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.boleto.ProcessaBoletoServidor;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaInclusaoColaboradorRescisao</p>
 * <p>Description: Processo para incluir colaboradores no processo inicial de rescisão contratual.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaInclusaoColaboradorRescisao extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaInclusaoColaboradorRescisao.class);

    public static final String CHAVE = "PROCESSO_INCLUIR_COLABORADOR_RESCISAO_CONTRATUAL";

    private final AcessoSistema responsavel;

    public ProcessaInclusaoColaboradorRescisao(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        LOG.debug("Inclui colaborador no processo de rescisão contratual: " + responsavel.getUsuCodigo());

        try {
            List<String> critica = new ArrayList<>();
            VerbaRescisoriaController verbaRescisoriaController = ApplicationContextProvider.getApplicationContext().getBean(VerbaRescisoriaController.class);

            // Recupera os registros candidatos ao processo de rescisão contratual
            List<String> svrCodigos = new ArrayList<>();
            svrCodigos.add(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());
            List<TransferObject> listaRseCandidatoRescisao = verbaRescisoriaController.listarVerbaRescisoriaRse(svrCodigos, responsavel);
            
            // Processa a inclusão do colaborador candidato no processo de rescisão contratual
            if (listaRseCandidatoRescisao != null && !listaRseCandidatoRescisao.isEmpty()) {
                for (TransferObject candidato : listaRseCandidatoRescisao) {
                    String retorno = verbaRescisoriaController.processarInclusaoColaborador(candidato, responsavel);
                    if (!TextHelper.isNull(retorno)) {
                        critica.add(retorno);
                    }
                }
            }
            
            // Verifica se houve crítica e adiciona na mensagem de retorno
            if (critica != null && !critica.isEmpty()) {
                setMensagemErro(critica);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.lista.de.colaboradores.para.rescisao.sucesso", responsavel);
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
    
    /**
     * Salva uma mensagem de erro para o processo de inclusão de colaboradores para rescisão contratual.
     * @param critica
     * @return
     */
    private String setMensagemErro(List<String> critica) {
        codigoRetorno = ProcessaBoletoServidor.ERRO;
        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.confirmar.colaborador.rescisao.inclusao", responsavel, "<br/>" + TextHelper.join(critica, "<br/>")) + "<br/>";
        return this.mensagem;
    }

}
