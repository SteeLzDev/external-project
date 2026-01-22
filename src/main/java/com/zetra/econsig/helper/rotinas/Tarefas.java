package com.zetra.econsig.helper.rotinas;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorBaseDeDados;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;

/**
 * <p>Title: Tarefas</p>
 * <p>Description: Tarefas</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Tarefas implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Tarefas.class);
    private static final String NOME_CLASSE = Tarefas.class.getName();

    @Override
    public int executar(String args[]) {
        String acao = (args.length == 0) ? "" : args[0];

        if (acao.equals("IMPORTA_ARQ")) {
            if (args.length == 6) {
                LOG.debug("Importando arquivo...");
                importaArquivo(args[1], args[2], args[3], args[4], args[5]);
            } else {
                LOG.error("Uso: " + NOME_CLASSE + " IMPORTA_ARQ PATH ARQUIVO_ENTRADA XML_ENTRADA XML_TRADUTOR XML_SAIDA");
                LOG.error("Importacao cancelada...");
                return -1;
            }

        } else if (acao.equals("EXPORTA_ARQ")) {
            if (args.length == 6) {
                LOG.debug("Exportando arquivo...");
                exportaArquivo(args[1], args[2], args[3], args[4], args[5]);
            } else {
                LOG.error("Uso: " + NOME_CLASSE + " EXPORTA_ARQ PATH ARQUIVO_SAIDA XML_ENTRADA XML_TRADUTOR XML_SAIDA");
                LOG.error("Exportacao cancelada...");
                return -1;
            }

        } else if (acao.equals("XML")) {
            if (args.length == 4) {
                LOG.debug("Validando XML...");
                validaXML(args[1], args[2], args[3]);
            } else {
                LOG.error("Uso: " + NOME_CLASSE + " XML ARQUIVO_ENTRADA XML_ENTRADA XML_TRADUTOR");
                LOG.error("Verificacao cancelada...");
                return -1;
            }

        } else {
            LOG.error("Uso incorreto. Ações disponíveis: IMPORTA_ARQ, EXPORTA_ARQ, XML");
            return -1;
        }

        return 0;
    }

    private void validaXML(String nomeArq, String xmlEntrada, String xmlSaida) {
        try {
            Leitor l = new LeitorArquivoTexto(xmlEntrada, nomeArq);
            Map<String, Object> e = new HashMap<>();
            Escritor es = new EscritorMemoria(e);
            Tradutor t = new Tradutor(xmlSaida, l, es);
            t.iniciaTraducao();
            while (t.traduzProximo()) {
                LOG.debug("Conteudo: " + e);
            }
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void importaArquivo(String path, String arqEntrada, String xmlEntrada, String xmlTradutor, String xmlSaida) {
        LeitorArquivoTexto leitor = null;
        EscritorBaseDeDados escritor = null;
        Tradutor tradutor = null;
        LOG.debug("INICIANDO Tarefas.importaArquivo()");
        try {
            String nomeArqEntrada = path + File.separatorChar + arqEntrada;
            String nomeArqConfEntrada = path + File.separatorChar + xmlEntrada;
            String nomeArqConfSaida = path + File.separatorChar + xmlSaida;
            String nomeArqConfTradutor = path + File.separatorChar + xmlTradutor;

            leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArqEntrada);
            escritor = new EscritorBaseDeDados(nomeArqConfSaida);
            tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                LOG.debug("FINALIZANDO tarefas.importaArquivo()");
                leitor.encerraLeitura();
                tradutor.encerraTraducao();
                escritor.encerraEscrita();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }
    }

    private void exportaArquivo(String path, String arqSaida, String xmlEntrada, String xmlTradutor, String xmlSaida) {
        LeitorBaseDeDados leitor = null;
        EscritorArquivoTexto escritor = null;
        Tradutor tradutor = null;
        LOG.debug("INICIANDO Tarefas.exportaArquivo()");
        try {
            String nomeArqSaida = path + File.separatorChar + arqSaida;
            String nomeArqConfEntrada = path + File.separatorChar + xmlEntrada;
            String nomeArqConfSaida = path + File.separatorChar + xmlSaida;
            String nomeArqConfTradutor = path + File.separatorChar + xmlTradutor;

            leitor = new LeitorBaseDeDados(nomeArqConfEntrada);
            escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
            tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                LOG.debug("FINALIZANDO tarefas.exportaArquivo()");
                leitor.encerraLeitura();
                tradutor.encerraTraducao();
                escritor.encerraEscrita();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }
   }
}