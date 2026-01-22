package com.zetra.econsig.job.process;

import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaDesfazProcessamentoFolha</p>
 * <p>Description: Processo para reverter as operações realizadas na execução dos blocos de processamento folha.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaDesfazProcessamentoFolha extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDesfazProcessamentoFolha.class);

    public static final String CHAVE = "PROCESSO_DESFAZER_BLOCO_PROCESSAMENTO_FOLHA";

    private final String bprPeriodo;
    private final AcessoSistema responsavel;

    public ProcessaDesfazProcessamentoFolha(String bprPeriodo, AcessoSistema responsavel) {
        this.bprPeriodo = bprPeriodo;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        LOG.debug("Desfaz o processamento dos blocos de margem e retorno do período [" + bprPeriodo + "]: " + responsavel.getUsuCodigo());

        try {
            ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);

            // Recupera os blocos de processamento
            List<Integer> blocos = processarFolhaController.obterBlocosProcessados(null, null, responsavel);
            if (blocos != null && !blocos.isEmpty()) {
                for (Integer bloco:blocos) {
                    // Desfaz o processamento de margem e retorno de cada bloco econtrado
                    processarFolhaController.desfazerProcessamentoBloco(bloco, responsavel);
                }
            }

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
