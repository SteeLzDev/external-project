package com.zetra.econsig.job.process;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaAtualizacaoBaseDeDados</p>
 * <p> Description: Classe para processamento das rotina batch de atualização da base de dados do Sistema</p>
 * <p> Copyright: Copyright (c) 2002-2018</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaAtualizacaoBaseDeDados extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaAtualizacaoBaseDeDados.class);

    private final List<String> orderedFileNames;
    private final Map<String, String> sqlFiles;
    private final AcessoSistema responsavel;
    private final boolean podeDesbloquearSistema;

    public ProcessaAtualizacaoBaseDeDados(List<String> orderedFileNames, Map<String, String> sqlFiles, boolean podeDesbloquearSistema, AcessoSistema responsavel) {
        this.orderedFileNames = orderedFileNames;
        this.sqlFiles = sqlFiles;
        this.podeDesbloquearSistema = podeDesbloquearSistema;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        LOG.warn("Início: Iniciando atualização de sistema.");

        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        boolean desbloquear = true;

        for (String fileName : orderedFileNames) {
            LOG.info("Executando arquivo SQL \"" + fileName + "\"");
            String content = sqlFiles.get(fileName);
            try {
                sistemaController.executarBatchScript(fileName, content, responsavel);
                LOG.info("Arquivo SQL \"" + fileName + "\" executado com sucesso");

            } catch (ConsignanteControllerException ex) {
                LOG.error("Ocorreram erros ao executar o arquivos SQL \"" + fileName + "\"", ex);
                desbloquear = false;
                break;
            }
        }

        try {
            // Desbloqueia o sistema
            if (podeDesbloquearSistema && desbloquear) {
                sistemaController.alteraStatusSistema(CodedValues.CSE_CODIGO_SISTEMA, CodedValues.STS_ATIVO, null, responsavel);
            }
        } catch (ConsignanteControllerException ex) {
            LOG.error("Ocorreram erros ao desbloquear o sistema pós atualização", ex);
        }

        // Limpa cache de parâmetros
        JspHelper.limparCacheParametros();

        if (desbloquear) {
            LOG.warn("Término com sucesso: Finalizando atualização de sistema.");
        } else {
            LOG.warn("Término com erro: Erro na atualização do sistema.");
        }
    }
}
