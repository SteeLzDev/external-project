package com.zetra.econsig.folha.importacao;

import java.io.File;
import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidaImportacaoSistemaBase</p>
 * <p>Description: Classe base para implementação de customizações na rotina de validação
 * de importação de margem, retorno, transferidos e crítica.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidaImportacaoSistemaBase implements ValidaImportacaoSistema {

    /**
     * Executa rotina específica por sistema para customização na busca de arquivos
     * em áreas externas, como FTP, HTTP, VPN, etc.
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde os arquivos foram localizados
     * @throws ZetraException
     */
    @Override
    public void aplicarCustomizacoesBuscaArquivos(String tipoArquivo, String caminhoCompleto) throws ZetraException {
    }

    /**
     * Executa rotina específica por sistema para customização nos tratamentos dos
     * arquivos de acordo com o tipo.
     * @param arquivos        : Conjunto de arquivos a serem customizados
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde os arquivos foram localizados
     * @return
     * @throws ZetraException
     */
    @Override
    public List<File> aplicarCustomizacoesPosTotalArquivos(List<File> arquivos, String tipoArquivo, String caminhoCompleto) throws ZetraException {
        return arquivos;
    }

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
    @Override
    public List<File> aplicarCustomizacoesPosTotalLinhas(List<File> arquivos, String tipoArquivo, String caminhoCompleto, int totalLinhas) throws ZetraException {
        return arquivos;
    }

    /**
     * Executa rotina específica por sistema para customização nos tratamentos dos
     * arquivos de acordo com o tipo.
     * @param arquivo         : Arquivo final
     * @param tipoArquivo     : Tipo de arquivo que está sendo customizado
     * @param caminhoCompleto : Caminho completo de onde o arquivo foi localizado
     * @return
     * @throws ZetraException
     */
    @Override
    public File aplicarCustomizacoesPosValidacaoLeiaute(File arquivo, String tipoArquivo, String caminhoCompleto) throws ZetraException {
        return arquivo;
    }

    /**
     * Cria a classe customizada para validação de importação
     * @param nomeClasseCustomizada
     * @return
     * @throws ZetraException
     */
    public static ValidaImportacaoSistema getValidaImportacaoSistema(String nomeClasseCustomizada) throws ZetraException {
        try {
            Object validacaoCustomizada = Class.forName(nomeClasseCustomizada).getDeclaredConstructor().newInstance();
            return (ValidaImportacaoSistema) validacaoCustomizada;
        } catch (Exception ex) {
            throw new ZetraException("mensagem.erro.nao.possivel.criar.classe.customizada.validacao.importacao", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }
}
