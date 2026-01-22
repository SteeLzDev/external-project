package com.zetra.econsig.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: LeitorListTO</p>
 * <p>Description: Implementação do Leitor para java.util.List de TransferObjects.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorListTO implements Leitor {

    protected int linhaAtual;
    protected int sizeList;
    protected List<TransferObject> listEntrada;
    protected TransferObject linha;
    protected Map<String, Object> hmap;

    public LeitorListTO(List<TransferObject> conteudo) {
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
            hmap.putAll(linha.getAtributos());
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
