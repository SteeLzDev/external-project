package com.zetra.econsig.parser;

import java.io.File;
import java.io.IOException;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LeitorArquivoTextoZip</p>
 * <p>Description: Implementação do LeitorArquivoTexto que faz o tratamento de arquivos compactados.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorArquivoTextoZip extends LeitorArquivoTexto {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeitorArquivoTextoZip.class);

    public LeitorArquivoTextoZip(String nomearqconfig, String nomearqentrada) {
        super(nomearqconfig, nomearqentrada);
    }

    public LeitorArquivoTextoZip(String nomearqconfig, String nomearqentrada, boolean ignoraHeaderFooter) {
        super(nomearqconfig, nomearqentrada, ignoraHeaderFooter);
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        try {
            String path = nomeArquivo.substring(0, nomeArquivo.lastIndexOf(File.separatorChar));
            nomeArquivo = FileHelper.unZip(nomeArquivo, path);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.leitor.arquivo.falha.descompactacao.arquivo", (AcessoSistema) null);
        }

        super.iniciaLeitura();
    }

    @Override
    public void encerraLeitura() throws ParserException {
        super.encerraLeitura();

        File tmpFile = new File(nomeArquivo);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }
}