package com.zetra.econsig.folha.dirf;

import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.exception.ArquivoDirfControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.dirf.ArquivoDirfController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImportaArquivoDirf</p>
 * <p>Description: Classe utilitária para execução da rotina de carga de arquivos DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaArquivoDirf implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaArquivoDirf.class);
    private static final String NOME_CLASSE = ImportaArquivoDirf.class.getName();

    @Override
    public int executar(String[] args) {
    	ImportaArquivoDirfDTO dto = buscarValores(args);
        if (dto == null) {
            LOG.error("USE: java " + NOME_CLASSE + " I [ARQUIVO] C [ARQUIVO]");
            LOG.error("Opcoes:");
            LOG.error("		I - Arquivo principal para importacao. (Obrigatorio)");
            LOG.error("		C - Arquivo secundario para complemento. (Opcional)");
            return -1;
        } else {
            try {
                LOG.info("INÍCIO - IMPORTA ARQUIVO DIRF: " + DateHelper.getSystemDatetime());
                ArquivoDirfController arquivoDirfController = ApplicationContextProvider.getApplicationContext().getBean(ArquivoDirfController.class);
                arquivoDirfController.importarArquivoDirf(dto, AcessoSistema.getAcessoUsuarioSistema());
                LOG.info("FIM - IMPORTA ARQUIVO DIRF: " + DateHelper.getSystemDatetime());
                return 0;
            } catch (ArquivoDirfControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            }
        }
    }

    /**
     * Recupera os valores informados por argumento e retorna um map com as chaves
     */
    private ImportaArquivoDirfDTO buscarValores(String [] args) {
    	try {

    		String nomeArquivo = null;
    		String nomeArquivoComplemento = null;

    		List<String> argsList = Arrays.asList(args);

    		int index = 0;

    		if (argsList.contains("I")) {
    			index = argsList.indexOf("I") + 1;
    			nomeArquivo = argsList.get(index);
    		} else {
    			// "I" é obrigatório
    			return null;
    		}

    		if (argsList.contains("C")) {
    			index = argsList.indexOf("C") + 1;
    			nomeArquivoComplemento = argsList.get(index);
    		}

    		return new ImportaArquivoDirfDTO(nomeArquivo, nomeArquivoComplemento);

    	} catch (Exception ex) {
    	    LOG.error(ex.getMessage(), ex);
    	}

    	return null;
    }
}
