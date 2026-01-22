package com.zetra.econsig.folha.importacao;

import java.io.File;
import java.util.List;

import com.zetra.econsig.exception.ZetraException;

/**
 * <p>Title: ValidaImportacaoSistema</p>
 * <p>Description: Interface para implementação de customizações na rotina de validação
 * de importação de margem, retorno, transferidos e crítica.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidaImportacaoSistema {

    /**
     * Executa rotina específica por sistema para customização na busca de arquivos
     * em áreas externas, como FTP, HTTP, VPN, etc.
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde os arquivos foram localizados
     * @throws ZetraException
     */
    public void aplicarCustomizacoesBuscaArquivos(String tipoArquivo, String caminhoCompleto) throws ZetraException;
    
    /**
     * Executa rotina específica por sistema para customização nos tratamentos dos 
     * arquivos de acordo com o tipo.
     * @param arquivos        : Conjunto de arquivos a serem customizados
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde os arquivos foram localizados
     * @return
     * @throws ZetraException
     */
    public List<File> aplicarCustomizacoesPosTotalArquivos(List<File> arquivos, String tipoArquivo, String caminhoCompleto) throws ZetraException;
    
    /**
     * Executa rotina específica por sistema para customização nos tratamentos dos 
     * arquivos de acordo com o tipo.
     * @param arquivos        : Conjunto de arquivos a serem customizados
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde os arquivos foram localizados
     * @param totalLinhas     : Total de linhas encontradas
     * @return
     * @throws ZetraException
     */
    public List<File> aplicarCustomizacoesPosTotalLinhas(List<File> arquivos, String tipoArquivo, String caminhoCompleto, int totalLinhas) throws ZetraException;
    
    /**
     * Executa rotina específica por sistema para customização nos tratamentos dos 
     * arquivos de acordo com o tipo.
     * @param arquivo         : Arquivo final
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde o arquivo foi localizado
     * @return
     * @throws ZetraException
     */
    public File aplicarCustomizacoesPosValidacaoLeiaute(File arquivo, String tipoArquivo, String caminhoCompleto) throws ZetraException;
}
