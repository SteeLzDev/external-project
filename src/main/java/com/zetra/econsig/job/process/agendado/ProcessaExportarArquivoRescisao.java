package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ExportarArquivoRescisaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.rescisao.ExportarArquivoRescisaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaExportarArquivoRescisao</p>
 * <p>Description: Classe de processamento para gerar arquivo de movimento de rescisão.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaExportarArquivoRescisao extends ProcessoAgendadoPeriodico {

    /**
     * Log object for this class.
     */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaExportarArquivoRescisao.class);

    private boolean executaRescisao = false;
    private static final String MENSAGEM_ERRO_RESCISAO_AUSENTE = "mensagem.erro.sistema.parametros.exportacao.rescisao.ausentes";
    private static final String MENSAGEM_ERRO_RESCISAO_VAZIO = "mensagem.aviso.processo.movimento.rescisao.arquivo.vazio";

    public ProcessaExportarArquivoRescisao(String agdCodigo, boolean executaRecisao, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        executaRescisao = executaRecisao;
    }

    @Override
    protected void executa() throws ZetraException {
        try {
            if (!ParamSist.getBoolParamSist(CodedValues.TPC_GERA_ARQUIVO_RESCISAO, AcessoSistema.getAcessoUsuarioSistema()) || executaRescisao) {
                LOG.debug("Gera arquivo de movimento de rescisão");
                final ExportarArquivoRescisaoController exportarArquivoRescisaoController = ApplicationContextProvider.getApplicationContext().getBean(ExportarArquivoRescisaoController.class);
                exportarArquivoRescisaoController.exportarArquivoRescisao(getResponsavel());
            }
        } catch (final ExportarArquivoRescisaoControllerException ex) {
            codigoRetorno = AVISO;
            if ((ex.getMessageKey() != null) && MENSAGEM_ERRO_RESCISAO_AUSENTE.equals(ex.getMessageKey())) {
                mensagem = ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_RESCISAO_AUSENTE, AcessoSistema.getAcessoUsuarioSistema());
            } else {
                mensagem = ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_RESCISAO_VAZIO, AcessoSistema.getAcessoUsuarioSistema());
            }
        }
    }
}
