package com.zetra.econsig.folha.exportacao.impl;

/**
 * <p>Title: Marinha</p>
 * <p>Description: Implementações específicas para a Marinha.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MarinhaLicitacao extends Marinha2 {
    public MarinhaLicitacao() {
        super();
        sleepBetweenQuerys = false;
        enviaContratosPensionistasSemMargem = false;
        retiraDoEstoquePosExportacao = false;
    }
}
