package com.zetra.econsig.service.folha;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConciliacaoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConciliacaoController  {

    public void conciliar(String csaCodigo, String nomeArqXmlEntrada, String nomeArqXmlTradutor, String tipoEntidade, String codigoEntidade,
            String nomeArquivoEntrada, AcessoSistema responsavel) throws ViewHelperException;
}