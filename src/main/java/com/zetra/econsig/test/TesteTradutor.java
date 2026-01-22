package com.zetra.econsig.test;

import java.io.File;

import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;

public class TesteTradutor {
    public static void main(String[] args) {
        try {
            String path = "C:/Temp/eConsig/arquivos/";
            
            String nomeArqEntrada = path + File.separatorChar + "entrada.txt";
            String nomeArqSaida   = path + File.separatorChar + "saida.txt";

            String nomeArqConfEntrada  = path + File.separatorChar + "entrada.xml";
            String nomeArqConfTradutor = path + File.separatorChar + "tradutor.xml";
            String nomeArqConfSaida    = path + File.separatorChar + "saida.xml";

            LeitorArquivoTexto leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArqEntrada);
            EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
            Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
