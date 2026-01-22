package com.zetra.econsig.job.process;

import java.util.Date;

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
public class ProcessaInterromperProcessamentoFolha extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDesfazProcessamentoFolha.class);

    public static final String CHAVE = "PROCESSO_INTERROMPER_RETORNO_FOLHA";

    private final Date bprPeriodo;
    private final String observacao;
    private final AcessoSistema responsavel;

    public ProcessaInterromperProcessamentoFolha(Date bprPeriodo, String observacao, AcessoSistema responsavel) {
        this.bprPeriodo = bprPeriodo;
        this.observacao = observacao;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        LOG.debug("Interrompe o processamento de margem e retorno folha do período [" + bprPeriodo + "]: " + responsavel.getUsuCodigo());

        try {
            // Interrompe o processamento de margem e retorno da folha em execução
            ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);
            processarFolhaController.interromperProcessamento(bprPeriodo, observacao, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
