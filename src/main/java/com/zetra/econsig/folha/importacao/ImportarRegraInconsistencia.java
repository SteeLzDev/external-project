package com.zetra.econsig.folha.importacao;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.folha.ImportarRegraInconsistenciaHelper;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportarRegraInconsistencia</p>
 * <p>Description: Classe utilitária para importação de regras de
 *                 inconsistência de contratos
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportarRegraInconsistencia implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarRegraInconsistencia.class);
    private static final String NOME_CLASSE = ImportarRegraInconsistencia.class.getName();

    @Override
    public int executar(String args[]) {
        String instrucoesUso = "USE: java " + NOME_CLASSE + " ARQUIVO_ENTRADA \n"
                + " -> ARQUIVO_ENTRADA : Nome do arquivo de entrada \n"
                ;

        if (args.length != 1) {
            LOG.error(instrucoesUso);
            return -1;
        } else {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            ImportarRegraInconsistenciaHelper impRegraHelper = new ImportarRegraInconsistenciaHelper(responsavel);

            try {
                impRegraHelper.importaInconsistencia(args[0]);
                return 0;
            } catch (ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            }
        }
    }
}
