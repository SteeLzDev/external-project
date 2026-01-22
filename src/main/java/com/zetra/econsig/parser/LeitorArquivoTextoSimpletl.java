package com.zetra.econsig.parser;

import java.io.File;

import br.com.nostrum.simpletl.exception.InterpreterException;
import br.com.nostrum.simpletl.reader.Reader;
import br.com.nostrum.simpletl.reader.TextFileReader;

/**
 * <p>Title: LeitorArquivoTextoSimpletl</p>
 * <p>Description: Implementação do LeitorArquivoTexto que encapsula o mecanismo de leitura de arquivos do SimplETL.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorArquivoTextoSimpletl extends LeitorArquivoTexto {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeitorArquivoTextoSimpletl.class);

    private Reader<File> reader;

    public LeitorArquivoTextoSimpletl(String nomeArqConfig, String nomeArqEntrada) {
        super(nomeArqEntrada);
        try {
            reader = new TextFileReader(nomeArqConfig).setSource(new File(nomeArqEntrada));
        } catch (InterpreterException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        try {
            reader.open();
        } catch (InterpreterException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException(ex);
        }
    }

    @Override
    public void encerraLeitura() throws ParserException {
        try {
            reader.close();
        } catch (InterpreterException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException(ex);
        }
    }

    @Override
    public String getLinha() {
        return ((TextFileReader)reader).getCurrentLine();
    }

    public Reader<File> getReader() {
        return reader;
    }
}
