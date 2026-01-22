package com.zetra.econsig.dto.web;


/**
 * <p>Title: ServicoSolicitacaoServidor</p>
 * <p>Description: POJO contendo configuração da listagem dos serviços que o servidor pode realizar solicitações,
 *                 tanto para simulação quanto para reserva.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoSolicitacaoServidor {

    private String link;
    private String label;


    public ServicoSolicitacaoServidor(String link, String label) {
        super();
        this.link = link;
        this.label = label;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }



}
