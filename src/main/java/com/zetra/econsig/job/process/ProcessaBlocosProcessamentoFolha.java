package com.zetra.econsig.job.process;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.ConsumidorBlocoProcessamento;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.HistoricoProcessamento;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBlocosProcessamentoFolha</p>
 * <p>Description: Processo que irá disparar a execução dos blocos de processamento folha.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBlocosProcessamentoFolha extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBlocosProcessamentoFolha.class);

    public static final String CHAVE = "PROCESSO_RETORNO_FOLHA";

    private final HistoricoProcessamento processamento;
    private final AcessoSistema responsavel;

    private volatile boolean running = true;

    public ProcessaBlocosProcessamentoFolha(HistoricoProcessamento processamento, AcessoSistema responsavel) {
        this.processamento = processamento;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        String tipoEntidade = (processamento.getEstabelecimento() != null ? AcessoSistema.ENTIDADE_EST : processamento.getOrgao() != null ? AcessoSistema.ENTIDADE_ORG : AcessoSistema.ENTIDADE_CSE);
        String codigoEntidade = (processamento.getEstabelecimento() != null ? processamento.getEstabelecimento().getEstCodigo() : processamento.getOrgao() != null ? processamento.getOrgao().getOrgCodigo() : CodedValues.CSE_CODIGO_SISTEMA);

        LOG.debug("Realiza processamento dos blocos [" + tipoEntidade + ", " + codigoEntidade + "]: " + responsavel.getUsuCodigo());

        int qtdProcessos = 10;

        try {
            qtdProcessos = Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_PROCESSOS_PARALELOS_PROCESSAMENTO_FOLHA, responsavel).toString());
        } catch (NumberFormatException | NullPointerException e) {
            qtdProcessos = 10;
        }

        try {
            ProcessarFolhaController processarFolhaController = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);

            // Tenta por 5 vezes processar todos os blocos, necessário pois alguns blocos
            // podem não ser processados por causa de deadlocks, visto que o sistema estará desbloqueado
            for (int i = 1; i <= 5; i++) {
                if (running) {
                    // Obtém os blocos de processamento agrupados por registro servidor
                    List<Integer> blocos = processarFolhaController.obterBlocosAguardProcessamento(tipoEntidade, codigoEntidade, responsavel);

                    // Se não tem mais blocos, interrompe a execução por 1 minuto, e tenta novamente
                    if (blocos == null || blocos.isEmpty()) {
                        try {
                            Thread.sleep(60 * 1000);
                        } catch (InterruptedException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                        continue;
                    }

                    // Adiciona os blocos à fila
                    BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
                    queue.addAll(blocos);

                    // Carrega o cache de entidades que são as dependências ao servidor
                    CacheDependenciasServidor cacheEntidades = new CacheDependenciasServidor();
                    cacheEntidades.carregarCache(responsavel);

                    // Caso seja a última das 5 tentativas de processamento, deve consumir todos os blocos restantes em apenas 01 processo para evitar deadlocks
                    if (i==5) {
                        qtdProcessos = 1;
                    }
                    // Inicia os consumidores de blocos, de acordo com o parâmetro de quantidade de processos em paralelo
                    Thread[] processos = new Thread[qtdProcessos];
                    for (int j = 0; j < qtdProcessos; j++) {
                        processos[j] = new Thread(new ConsumidorBlocoProcessamento(queue, cacheEntidades, processamento, responsavel));
                        processos[j].start();
                    }

                    // Espera pelo término dos processos
                    for (int j = 0; j < qtdProcessos; j++) {
                        try {
                            processos[j].join();
                        } catch (InterruptedException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }
            }

            // Finaliza o processamento
            if (running) {
                processarFolhaController.finalizarProcessamento(tipoEntidade, codigoEntidade, processamento, responsavel);
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public void interromper() {
        LOG.debug("Interrompe o processo principal de processamento dos blocos: " + responsavel.getUsuCodigo());
        running = false;
        super.interrupt();
    }

    public boolean isRunning() {
        return running;
    }
}
