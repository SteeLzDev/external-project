package com.zetra.econsig.service.servidor;

import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImpBloqueioServidorController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImpBloqueioServidorController {

    public void importarBloqueioServidor(String nomeArquivo, AcessoSistema responsavel) throws ServidorControllerException;

}
