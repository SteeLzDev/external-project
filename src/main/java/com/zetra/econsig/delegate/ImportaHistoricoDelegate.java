package com.zetra.econsig.delegate;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.folha.ImportaHistoricoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImportaHistoricoDelegate</p>
 * <p>Description: Delegate de processos de importação de histórico</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaHistoricoDelegate extends AbstractDelegate {

    private ImportaHistoricoController importaHistoricoController = null;

    private ImportaHistoricoController getImportaHistoricoController() throws ConsignanteControllerException {
        try {
            if (importaHistoricoController == null) {
                importaHistoricoController = ApplicationContextProvider.getApplicationContext().getBean(ImportaHistoricoController.class);
            }
            return importaHistoricoController;
        } catch (Exception ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public void gerarHistoricoTeste(int qtdRse, int qtdAde, int matriculaInicial, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        getImportaHistoricoController().gerarHistoricoTeste(qtdRse, qtdAde, matriculaInicial, criarParcelas, nseCodigo, responsavel);
    }

    public void gerarHistoricoTesteOrientado(int pctRse, int qtdAdePorRse, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        getImportaHistoricoController().gerarHistoricoTesteOrientado(pctRse, qtdAdePorRse, criarParcelas, nseCodigo, responsavel);
    }

    public void apagarHistoricoTesteOrientado(AcessoSistema responsavel) throws ConsignanteControllerException {
        getImportaHistoricoController().apagarHistoricoTesteOrientado(responsavel);
    }
}
