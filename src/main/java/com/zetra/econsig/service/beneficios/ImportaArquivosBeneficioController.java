package com.zetra.econsig.service.beneficios;

import com.zetra.econsig.exception.ImportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaArquivosBeneficioController</p>
 * <p>Description: Interface para importação de arquivos do modulo Beneficio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaArquivosBeneficioController {

    public void importaArquivoRetornoOperadora(String csaCodigo, String nomeArquivo, AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException;

}
