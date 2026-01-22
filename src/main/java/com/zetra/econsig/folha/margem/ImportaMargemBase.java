package com.zetra.econsig.folha.margem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.folha.retorno.ImportaRetornoBase;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.RegistroServidor;

/**
 * <p>Title: ImportaMargemBase</p>
 * <p>Description: Classe base para as classes de importação de margem de modo a centralizar
 * métodos comuns e evitar a declaração de métodos vazios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public abstract class ImportaMargemBase implements ImportaMargem {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaRetornoBase.class);

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#sobreporImportacaoMargem()
     */
    @Override
    public boolean sobreporImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#sobreporRecalculoMargem()
     */
    @Override
    public boolean sobreporRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#importaCadastroMargens(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.zetra.econsig.helper.seguranca.AcessoSistema)
     */
    @Override
    public String importaCadastroMargens(String nomeArquivo, String tipoEntidade, List<String> entCodigos, boolean margemTotal, boolean geraTransferidos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
        return null;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#recalculaMargem(java.lang.String, java.util.List, com.zetra.econsig.helper.seguranca.AcessoSistema)
     */
    @Override
    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#preRecalculoMargem()
     */
    @Override
    public void preRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#posRecalculoMargem()
     */
    @Override
    public void posRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#preImportacaoMargem()
     */
    @Override
    public void preImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#posImportacaoMargem()
     */
    @Override
    public void posImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#preGeracaoTransferidos()
     */
    @Override
    public void preGeracaoTransferidos(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#preGeracaoArqTransferidos(java.lang.String)
     */
    @Override
    public String preGeracaoArqTransferidos(String query, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
        return query;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#posGeracaoTransferidos()
     */
    @Override
    public void posGeracaoTransferidos(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.margem.ImportaMargem#calcularValorMargemFolha()
     */
    @Override
    public BigDecimal calcularValorMargemFolha(Short marCodigo, RegistroServidor rse, Map<String, Object> entrada, AcessoSistema responsavel) throws ImportaMargemException {
        return null;
    }
}
