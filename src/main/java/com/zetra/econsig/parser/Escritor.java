package com.zetra.econsig.parser;

import java.util.Map;

/**
 * <p>Title: Escritor</p>
 * <p>Description: Interface que representa um escritor de informações, onde o destino pode variar.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface Escritor {
    public void iniciaEscrita() throws ParserException;

    public void escreve(Map<String, Object> informacao) throws ParserException;

    public void encerraEscrita() throws ParserException;
}