package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ControladorProcessos</p>
 * <p>Description: Controlador de processos</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ControladorProcessos {

    private static ControladorProcessos controladorProcessos;

    static {
        controladorProcessos = new ControladorProcessos();
    }

    public static ControladorProcessos getInstance() {
        return controladorProcessos;
    }

    private final Map<String, Processo> cache;
    private boolean processosSuspensos;

    private ControladorProcessos() {
        cache = new Hashtable<>();
    }

    public synchronized void incluir(String chave, Processo processo) {
        cache.put(chave, processo);
    }

    private synchronized void remover(String id) {
        if (cache.containsKey(id)) {
            Processo processo = cache.get(id);
            if (!processo.isAlive()) {
                // Remove do cache caso o processo tenha
                // terminado de processar
                cache.remove(id);
            }
        }
    }

    public Processo getProcesso(String chave) {
        return cache.get(chave);
    }

    public synchronized boolean verificar(String chave, HttpSession session) {
        if (processosSuspensos) {
            // Retorna TRUE evitando que novos processos sejam iniciados
            return true;
        }

        Processo processo = getProcesso(chave);
        if (processo != null) {
            if (processo.isAlive()) {
                String msg = ApplicationResourcesHelper.getMessage("mensagem.informacao.processo.arg0.em.execucao.por.favor.aguarde", (AcessoSistema) null, "");
                if (processo.descricao != null) {
                    msg = ApplicationResourcesHelper.getMessage("mensagem.informacao.processo.arg0.em.execucao.por.favor.aguarde", (AcessoSistema) null, "(" + processo.descricao + ") ");
                }
                if (session != null) {
                    session.setAttribute(CodedValues.MSG_ALERT, msg);
                }
                return true;
            } else {
                // Remove o processo do controlador
                remover(chave);

                // Seta mensagem na sessão do usuário
                if (session != null) {
                    if (processo.getCodigoRetorno() == Processo.SUCESSO) {
                        session.setAttribute(CodedValues.MSG_INFO, processo.getMensagem());
                    } else if (processo.getCodigoRetorno() == Processo.AVISO) {
                        session.setAttribute(CodedValues.MSG_ALERT, processo.getMensagem());
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, processo.getMensagem());
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verifica se existe algum processo ativo.
     *
     * @return Retorna <code>true</code> caso algum exista algum processo ativo.
     */
    public synchronized boolean existeProcessoAtivo() {
        if (processosSuspensos) {
            // Retorna TRUE evitando que novos processos sejam iniciados
            return true;
        }

        List<String> lista = new ArrayList<>(cache.keySet());
        for (String chave : lista) {
            if(verificar(chave, null)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se existe o processo está ativo.
     *
     * @return Retorna <code>true</code> caso processo esteja ativo.
     */
    public synchronized boolean processoAtivo(String chave) {
        return verificar(chave, null);
    }

    /**
     * Suspende a execução de novos processos
     */
    public synchronized void suspenderProcessos() {
        processosSuspensos = true;
    }

    /**
     * Reativa a execução de novos processos
     */
    public synchronized void reativarProcessos() {
        processosSuspensos = false;
    }
}
