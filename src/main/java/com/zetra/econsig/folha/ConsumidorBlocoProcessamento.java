package com.zetra.econsig.folha;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaBlocosProcessamentoFolha;
import com.zetra.econsig.persistence.entity.HistoricoProcessamento;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConsumidorBlocoProcessamento</p>
 * <p>Description: Consome um bloco de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsumidorBlocoProcessamento implements Runnable {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsumidorBlocoProcessamento.class);

    private final BlockingQueue<Integer> queue;
    private final CacheDependenciasServidor cacheEntidades;
    private final HistoricoProcessamento processamento;
    private final AcessoSistema responsavel;

    public ConsumidorBlocoProcessamento(BlockingQueue<Integer> queue, CacheDependenciasServidor cacheEntidades, HistoricoProcessamento processamento, AcessoSistema responsavel) {
        this.queue = queue;
        this.cacheEntidades = cacheEntidades;
        this.processamento = processamento;
        this.responsavel = responsavel;
    }

    @Override
    public void run() {
        ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);

        LOG.info(Thread.currentThread().getName() + " : start ");
        try {
            // Recupera o processo principal para verificar se ainda está em execução
            ProcessaBlocosProcessamentoFolha processoFolha = (ProcessaBlocosProcessamentoFolha) ControladorProcessos.getInstance().getProcesso(ProcessaBlocosProcessamentoFolha.CHAVE);
            while (true) {
                // Caso o processo principal não esteja mais em execução, interrompe o processamento dos blocos
                if (!processoFolha.isRunning()) {
                    LOG.info(Thread.currentThread().getName() + " : interrupted ");
                    return;
                }

                Integer number = queue.poll(10, TimeUnit.SECONDS);
                if (number == null) {
                    LOG.info(Thread.currentThread().getName() + " : finish ");
                    return;
                }
                LOG.info(Thread.currentThread().getName() + " : result " + number);
                processarFolhaController.processarBloco(number, cacheEntidades, processamento, responsavel);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
