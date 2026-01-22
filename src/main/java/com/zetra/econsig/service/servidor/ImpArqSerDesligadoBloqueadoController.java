package com.zetra.econsig.service.servidor;

import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImpArqSerDesligadoBloqueadoController</p>
 * <p>Description: Session Bean para a operação de importação de arquivos de servidores desligados e bloqueados.</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImpArqSerDesligadoBloqueadoController {

    public String importaDesligadoBloqueado(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ServidorControllerException;

}
