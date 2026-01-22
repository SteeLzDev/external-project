package com.zetra.econsig.folha.especifica;


/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Classe abstrata para a classe ProcessaFolhaEspecifica</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public abstract class AbstractProcessarFolhaEspecifica implements ProcessaFolhaEspecifica {

    public void prePrepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posPrepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preFinalizarProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posFinalizarProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preProcessarBloco(Integer bprCodigo, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posProcessarBloco(Integer bprCodigo, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preProcessarBlocoMargem(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posProcessarBlocoMargem(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preProcessarBlocoTransferidos(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posProcessarBlocoTransferidos(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preProcessarBlocoRetorno(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posProcessarBlocoRetorno(Integer bprCodigo, String rseCodigo, String campos, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void preProcessarRecalculoMargem(Integer bprCodigo, String rseCodigo, String responsavel) {
        /* TODO document why this method is empty */
    }

    public void posProcessarRecalculoMargem(Integer bprCodigo, String rseCodigo, String responsavel) {
        /* TODO document why this method is empty */
    }
}
