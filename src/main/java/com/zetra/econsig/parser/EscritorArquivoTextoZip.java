package com.zetra.econsig.parser;

import java.io.File;
import java.io.IOException;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EscritorArquivoTextoZip</p>
 * <p>Description: Implementação de um escritor para arquivo texto compactado em formato ZIP.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EscritorArquivoTextoZip extends EscritorArquivoTexto {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EscritorArquivoTextoZip.class);

    private final String nomeArqZip;

    public EscritorArquivoTextoZip(String nomearqconfig, String nomearqsaida) {
        super(nomearqconfig, nomearqsaida);
        nomeArqZip = nomearqsaida;
    }

    @Override
    public void iniciaEscrita() throws ParserException {
        // Muda o nome do arquivo de saida
        String nome = nomeArqZip.toLowerCase();
        if (!nome.endsWith("zip")) {
            throw new ParserException("mensagem.erro.escritor.arquivo.entrada.formato.extensao.deve.ser.zip", (AcessoSistema) null);
        }
        nomeArquivo = nome.replaceAll("zip", "txt");
        super.iniciaEscrita();
    }

    @Override
    public void encerraEscrita() throws ParserException {
        super.encerraEscrita();

        // Zipa o arquivo, e remove o .txt
        try {
            FileHelper.zip(nomeArquivo, nomeArqZip);
            File txtArq = new File(nomeArquivo);
            if (txtArq.exists()) {
                txtArq.delete();
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.compactacao.arquivo.saida", (AcessoSistema) null, ex);
        }
    }
}