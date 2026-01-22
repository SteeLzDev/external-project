package com.zetra.econsig.parser;

/**
 * <p>Title: ITradutor</p>
 * <p>Description: Interface que define os métodos mínimos necessários para tradução.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ITradutor {
    public void iniciaTraducao() throws ParserException;

    public boolean traduzProximo() throws ParserException;

    public void traduz() throws ParserException;

    public void encerraTraducao() throws ParserException;
}
