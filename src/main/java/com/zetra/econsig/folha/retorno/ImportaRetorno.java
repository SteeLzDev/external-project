package com.zetra.econsig.folha.retorno;

import java.io.Serializable;

import com.zetra.econsig.exception.ImportaRetornoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaRetorno</p>
 * <p>Description: Interface de definição das classes de importação de retorno.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public interface ImportaRetorno extends Serializable {
    /**
     * Indica se o processo padrão completo de importação de retorno deve ser sobreposto pela
     * implementação específica.
     * @return
     */
    public abstract boolean sobreporImportacaoRetorno();

    /**
     * Indica se o processo padrão completo de conclusão de retorno deve ser sobreposto pela
     * implementação específica.
     * @return
     */
    public abstract boolean sobreporConclusaoRetorno();

    /**
     * Processo de importação de retorno para os casos em que o processo padrão deve ser sobreposto.
     * @param nomeArquivo
     * @param orgCodigo
     * @param estCodigo
     * @param tipo
     * @param responsavel
     * @throws ImportaRetornoException
     */
    public void importarRetornoIntegracao(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da importação do arquivo de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preImportacaoArquivoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após da importação do arquivo de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posImportacaoArquivoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da primeira fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preFase1ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a primeira fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posFase1ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da segunda fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preFase2ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a segunda fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posFase2ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da terceira fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preFase3ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a terceira fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posFase3ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da quarta fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preFase4ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a quarta fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posFase4ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da quinta fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preFase5ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a quinta fase de importação de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posFase5ImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo de conclusão de retorno para os casos em que o processo normal deve ser sobreposto.
     * @param tipoEntidade
     * @param codigoEntidade
     * @param responsavel
     * @throws ImportaRetornoException
     */
    public void finalizarIntegracaoFolha(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaRetornoException;

    /**
     * Processo executado imediatamente antes da conclusão de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void preConclusaoImportacaoRetorno() throws ImportaRetornoException;

    /**
     * Processo executado imediatamente após a conclusão de retorno.
     * @throws ImportaRetornoException
     */
    public abstract void posConclusaoImportacaoRetorno() throws ImportaRetornoException;

}
