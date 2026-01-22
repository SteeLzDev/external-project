package com.zetra.econsig.parser;

import java.util.Map;

/**
 * <p>Title: EscritorMemoria</p>
 * <p>Description: Escritor no qual os dados são gravados na memoria</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EscritorMemoria implements Escritor {

    private Map<String, Object> memoria;

    public EscritorMemoria(Map<String, Object> mem) {
        memoria = mem;
    }

    @Override
    public void iniciaEscrita() throws ParserException {
        memoria.clear();
    }

    @Override
    public void escreve(Map<String, Object> informacao) {
        memoria.clear();
        memoria.putAll(informacao);
    }

    @Override
    public void encerraEscrita() throws ParserException {
        memoria = null;
    }
}