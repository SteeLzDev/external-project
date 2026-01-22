package com.zetra.econsig.parser;

import java.util.Map;

/**
 * <p>Title: Leitor</p>
 * <p>Description: Interface que representa um leitor de informações,
 *    que podem ser de vários tipos.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface Leitor {
    public void iniciaLeitura() throws ParserException;

    public Map<String, Object> le() throws ParserException;

    public void encerraLeitura() throws ParserException;
}