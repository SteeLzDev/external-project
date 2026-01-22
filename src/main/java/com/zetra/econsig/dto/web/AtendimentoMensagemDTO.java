package com.zetra.econsig.dto.web;

/**
 * <p>Title: AtendimentoMensagemId</p>
 * <p>Description: Mapeamento para Entidade Atendimento</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtendimentoMensagemDTO {

    private String texto;
    private boolean bot;

    public AtendimentoMensagemDTO() {
        super();
    }

    public AtendimentoMensagemDTO(String texto, boolean bot) {
        super();
        this.texto = texto;
        this.bot = bot;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

}
