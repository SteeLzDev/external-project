package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: Processo</p>
 * <p>Description: Classe Pai para criação de processos a
 * serem executados, em background.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class Processo extends Thread {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Processo.class);

    public static final int ERRO    = -1;
    public static final int SUCESSO = 0;
    public static final int AVISO   = 1;

    protected String owner;
    protected String descricao;
    protected String mensagem;
    protected int codigoRetorno;

    public Processo() {
        codigoRetorno = SUCESSO;
    }

    protected abstract void executar();

    @Override
    public final void run() {
        executar();
        LOG.debug("FINALIZANDO PROCESSAMENTO: " + DateHelper.getSystemDatetime());
    }

    @Override
    public final void start() {
        LOG.debug("INICIANDO PROCESSAMENTO: " + DateHelper.getSystemDatetime());
        super.start();
    }

    public int getCodigoRetorno() {
        return codigoRetorno;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOwner() {
        return owner;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isSucesso() {
        return codigoRetorno == Processo.SUCESSO;
    }
}
