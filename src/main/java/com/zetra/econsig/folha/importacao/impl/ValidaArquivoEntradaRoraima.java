package com.zetra.econsig.folha.importacao.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.importacao.ValidaImportacaoSistemaBase;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ConverteCharset;
import com.zetra.econsig.helper.texto.DateHelper;

public class ValidaArquivoEntradaRoraima extends ValidaImportacaoSistemaBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaArquivoEntradaRoraima.class);

    @Override
    public List<File> aplicarCustomizacoesPosTotalArquivos(List<File> arquivos, String tipoArquivo, String caminhoCompleto) throws ZetraException {
        LOG.debug("INÍCIO - aplicarCustomizacoesPosTotalArquivos " + DateHelper.getSystemDatetime());
        try {
            converte((File[]) arquivos.toArray(), Charset.forName("ISO-8859-1"));
            LOG.debug("FIM - aplicarCustomizacoesPosTotalArquivos " + DateHelper.getSystemDatetime());
            return arquivos;
        } catch (Exception e) {
            LOG.error("ERRO -  " + DateHelper.getSystemDatetime());
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
    }

    // Este método é candidato a subir para a classe base
    // Valida-Pré
    /** valida-pre: 010-converte_charset.sh.txt
     *  Identifica os arquivos de entrada baseado em expressão regular.
     *  Identifica o charset do arquivo de entrada
     *  Se necessário, converte para iso-8859-1
     *  Arquivo original é salvo como .ARQ
     * @throws Exception
    **/

    public void converte(File[] arquivos, Charset charset) throws Exception {
        LOG.debug("INÍCIO - CONVERTE CHARSET " + DateHelper.getSystemDatetime());
        ConverteCharset conversor = new ConverteCharset();

        try {
            conversor.converte(arquivos, Charset.forName("ISO-8859-1"));
        } catch (ZetraException e) {
            LOG.error("ERRO - CONVERTE CHARSET " + DateHelper.getSystemDatetime());
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
        }
        LOG.debug("FIM - CONVERTE CHARSET " + DateHelper.getSystemDatetime());
    }
}
