package com.zetra.econsig.parser;

import java.util.HashMap;

import br.com.nostrum.simpletl.Translator;
import br.com.nostrum.simpletl.data.SimpleData;
import br.com.nostrum.simpletl.exception.InterpreterException;

/**
 * <p>Title: TradutorSimpletl</p>
 * <p>Description: Implementação do ITradutor que encapsula o mecanismo de tradução do SimplETL.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TradutorSimpletl extends Translator implements ITradutor {
    private final EscritorMemoria escritor;

    public TradutorSimpletl(String nomeArqConfig, LeitorArquivoTextoSimpletl leitor, EscritorMemoria escritor) {
        super(leitor.getReader(), nomeArqConfig);
        this.escritor = escritor;
    }

    @Override
    public void iniciaTraducao() throws ParserException {
        try {
            super.start();
        } catch (InterpreterException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public boolean traduzProximo() throws ParserException {
        try {
            SimpleData data = (SimpleData) super.next();
            if (data != null) {
                HashMap<String, Object> informacao = new HashMap<>();
                for (String name : data.getNames()) {
                    informacao.put(name, data.get(name));
                }
                escritor.escreve(informacao);
                return true;
            }
        } catch (InterpreterException e) {
            throw new ParserException(e);
        }
        return false;
    }

    @Override
    public void traduz() throws ParserException {
        try {
            super.translate();
        } catch (InterpreterException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public void encerraTraducao() throws ParserException {
        try {
            super.stop();
        } catch (InterpreterException e) {
            throw new ParserException(e);
        }
    }
}
