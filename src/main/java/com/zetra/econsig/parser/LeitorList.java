package com.zetra.econsig.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: LeitorList</p>
 * <p>Description: Implementação do Leitor para java.util.List.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorList implements Leitor {

    protected int linhaAtual;
    protected int sizeList;
    protected List<Map<String, Object>> listEntrada;
    protected Map<String, Object> linha;
    protected Map<String, Object> hmap;

    public LeitorList(List<Map<String, Object>> conteudo) {
        listEntrada = conteudo;
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        linhaAtual = 0;
        sizeList = listEntrada.size();
    }

    @Override
    public Map<String, Object> le() throws ParserException {
        if (sizeList > linhaAtual) {
            linha = listEntrada.get(linhaAtual++);
            hmap = new HashMap<>();
            hmap.putAll(linha);
            return hmap;

        } else {
            return null;
        }
    }

    @Override
    public void encerraLeitura() throws ParserException {
        if (listEntrada != null) {
            listEntrada.clear();
        }

        listEntrada = null;
    }
}
