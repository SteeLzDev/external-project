package com.zetra.econsig.helper.lote;

import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ControleLote</p>
 * <p>Description: Singleton para o gerenciamento do processamento de lotes.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ControleLote {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleLote.class);

    private final Map<String, AcessoSistema> fila;
    private static ControleLote controle;

    static {
        controle = new ControleLote();
    }

    private ControleLote() {
        fila = new HashMap<>();
    }

    public static ControleLote getInstance() {
        return controle;
    }

    /**
     * Adiciona um arquivo para ser processado via lote.
     * @param nomeArquivo Nome do arquivo a ser procesado.
     * @param responsavel Usuário responsável pelo processamento
     * @throws ViewHelperException
     */
    public synchronized void adicionar(String nomeArquivo, AcessoSistema responsavel) throws ViewHelperException {
        final int max = ParamSist.getIntParamSist(CodedValues.TPC_LIMITE_PROCESSA_LOTE, -1, responsavel);
        if (max == 0) {
            throw new ViewHelperException("mensagem.erro.processar.lote.tente.novamente.mais.tarde", responsavel);
        } else if (max > 0) {
            if (fila.size() >= max) {
                throw new ViewHelperException("mensagem.erro.processar.lote.maximo.processamento", responsavel, String.valueOf(max));
            } else if (fila.containsKey(nomeArquivo)) {
                throw new ViewHelperException("mensagem.erro.processar.lote.mesmo.nome.processando", responsavel);
            }
            LOG.debug("adicionando lote");
            fila.put(nomeArquivo, responsavel);
        }
    }

    /**
     * Remove um lote da fila de processamento.
     * @param nomeArquivo Nome do arquivo a ser removido da fila de processamento.
     */
    public synchronized void remover(String nomeArquivo, AcessoSistema responsavel) {
        fila.remove(nomeArquivo);
    }
}
