package com.zetra.econsig.folha.especifica;


/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Interface para as novas classes especificas que serão procecssadas junto do processamento sem bloqueio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface ProcessaFolhaEspecifica {
    public abstract void prePrepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel);
    public abstract void posPrepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel);
    public abstract void preFinalizarProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel);
    public abstract void posFinalizarProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoCodigoEntidade, String responsavel);
    public abstract void preProcessarBloco(Integer bprCodigo, String responsavel);
    public abstract void posProcessarBloco(Integer bprCodigo, String responsavel);
    public abstract void preProcessarBlocoMargem(Integer bprCodigo, String rseCodigo, String campos, String responsavel);
    public abstract void posProcessarBlocoMargem(Integer bprCodigo, String rseCodigo, String campos, String responsavel);
    public abstract void preProcessarBlocoTransferidos(Integer bprCodigo, String rseCodigo, String campos, String responsavel);
    public abstract void posProcessarBlocoTransferidos(Integer bprCodigo, String rseCodigo,String campos, String responsavel);
    public abstract void preProcessarBlocoRetorno(Integer bprCodigo, String rseCodigo, String campos, String responsavel);
    public abstract void posProcessarBlocoRetorno(Integer bprCodigo, String rseCodigo, String campos, String responsavel);
    public abstract void preProcessarRecalculoMargem(Integer bprCodigo, String rseCodigo, String responsavel);
    public abstract void posProcessarRecalculoMargem(Integer bprCodigo, String rseCodigo, String responsavel);

}
