package com.zetra.econsig.folha.retorno;

import com.zetra.econsig.exception.ImportaRetornoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;


/**
 * <p>Title: ImportaRetornoBase</p>
 * <p>Description: Classe base para as classes de importação de retorno de modo a centralizar
 * métodos comuns e evitar a declaração de métodos vazios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public abstract class ImportaRetornoBase implements ImportaRetorno {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaRetornoBase.class);

    protected int tipoImportacaoRetorno;
    protected String orgCodigo;
    protected String estCodigo;

    /**
     * @param tipoImportacaoRetorno Tipo de importação de retorno (normal, atrasado, crítica, etc)
     * @param orgCodigo Código do órgão
     * @param estCodigo Código do estabelecimento
     */
    public ImportaRetornoBase(int tipoImportacaoRetorno, String orgCodigo, String estCodigo) {
        this.tipoImportacaoRetorno = tipoImportacaoRetorno;
        this.orgCodigo = orgCodigo;
        this.estCodigo = estCodigo;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#sobreporImportacaoRetorno()
     */
    @Override
    public boolean sobreporImportacaoRetorno() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#sobreporConclusaoRetorno()
     */
    @Override
    public boolean sobreporConclusaoRetorno() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#importarRetornoIntegracao(java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.zetra.econsig.helper.seguranca.AcessoSistema)
     */
    @Override
    public void importarRetornoIntegracao(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preImportacaoRetorno()
     */
    @Override
    public void preImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posImportacaoRetorno()
     */
    @Override
    public void posImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preImportacaoArquivoRetorno()
     */
    @Override
    public void preImportacaoArquivoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posImportacaoArquivoRetorno()
     */
    @Override
    public void posImportacaoArquivoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preFase1ImportacaoRetorno()
     */
    @Override
    public void preFase1ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posFase1ImportacaoRetorno()
     */
    @Override
    public void posFase1ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preFase2ImportacaoRetorno()
     */
    @Override
    public void preFase2ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posFase2ImportacaoRetorno()
     */
    @Override
    public void posFase2ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preFase3ImportacaoRetorno()
     */
    @Override
    public void preFase3ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posFase3ImportacaoRetorno()
     */
    @Override
    public void posFase3ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preFase4ImportacaoRetorno()
     */
    @Override
    public void preFase4ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posFase4ImportacaoRetorno()
     */
    @Override
    public void posFase4ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preFase4ImportacaoRetorno()
     */
    @Override
    public void preFase5ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posFase4ImportacaoRetorno()
     */
    @Override
    public void posFase5ImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#finalizarIntegracaoFolha(java.lang.String, java.lang.String, com.zetra.econsig.helper.seguranca.AcessoSistema)
     */
    @Override
    public void finalizarIntegracaoFolha(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#preConclusaoImportacaoRetorno()
     */
    @Override
    public void preConclusaoImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.folha.retorno.ImportaRetorno#posConclusaoImportacaoRetorno()
     */
    @Override
    public void posConclusaoImportacaoRetorno() throws ImportaRetornoException {
        LOG.debug("Método não implementado.");
    }
}
