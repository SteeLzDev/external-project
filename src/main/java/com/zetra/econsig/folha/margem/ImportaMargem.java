package com.zetra.econsig.folha.margem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.RegistroServidor;

/**
 * <p>Title: ImportaMargem</p>
 * <p>Description: Interface de definição das classes de importação de margem.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaMargem extends Serializable {

    /**
     * Indica se o processo padrão completo de importação de margem deve ser sobreposto pela
     * implementação específica.
     * @return
     */
    boolean sobreporImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel);

    /**
     * Indica se o processo padrão completo de recálculo de margem deve ser sobreposto pela
     * implementação específica.
     * @return
     */
    boolean sobreporRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel);

    /**
     * Processo de importação de margem para os casos em que o processo padrão deve ser sobreposto.
     * @param nomeArquivo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param margemTotal
     * @param geraTransferidos
     * @param responsavel
     * @return
     * @throws ImportaMargemException
     */
    String importaCadastroMargens(String nomeArquivo, String tipoEntidade, List<String> entCodigos, boolean margemTotal, boolean geraTransferidos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo de recálculo de margem para os casos em que o processo padrão deve ser sobreposto.
     * @param tipoEntidade
     * @param entCodigos
     * @param responsavel
     * @throws ImportaMargemException
     */
    void recalculaMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente antes do recálculo de margem.
     * @throws ImportaMargemException
     */
    void preRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente após o recálculo de margem.
     * @throws ImportaMargemException
     */
    void posRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente antes da importação de margem.
     * @throws ImportaMargemException
     */
    void preImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente após a importação de margem.
     * @throws ImportaMargemException
     */
    void posImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente antes da geração de transferidos.
     * @throws ImportaMargemException
     */
    void preGeracaoTransferidos(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente antes da geração do arquivo de transferidos,
     * logo após já ter selecionado os registros de transferência
     * @param query
     * @return
     * @throws ImportaMargemException
     */
    String preGeracaoArqTransferidos(String query, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Processo executado imediatamente após a geração de transferidos.
     * @throws ImportaMargemException
     */
    void posGeracaoTransferidos(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException;

    /**
     * Executado antes de gravar o valor de margem, para que rotinas customizadas possam calcular o valor
     * de margem folha com base em critérios específicos de cada sistema.
     * @param marCodigo
     * @param rse
     * @param responsavel
     * @return
     * @throws ImportaMargemException
     */
    BigDecimal calcularValorMargemFolha(Short marCodigo, RegistroServidor rse, Map<String, Object> entrada, AcessoSistema responsavel) throws ImportaMargemException;
}
