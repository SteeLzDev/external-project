package com.zetra.econsig.service.rescisao;

import com.zetra.econsig.exception.ExportarArquivoRescisaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportarArquivoRescisaoController</p>
 * <p>Description: Interface para exportação de arquivos do modulo de rescisão</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ExportarArquivoRescisaoController {

    public void exportarArquivoRescisao(AcessoSistema responsavel) throws ExportarArquivoRescisaoControllerException;

}