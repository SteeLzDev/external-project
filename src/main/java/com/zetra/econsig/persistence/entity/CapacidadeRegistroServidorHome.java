package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CapacidadeRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade CapacidadeRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CapacidadeRegistroServidorHome extends AbstractEntityHome {

    public static CapacidadeRegistroSer findByPrimaryKey(String capCodigo) throws FindException {
        CapacidadeRegistroSer capacidadeRegistroServidor = new CapacidadeRegistroSer();
        capacidadeRegistroServidor.setCapCodigo(capCodigo);
        return find(capacidadeRegistroServidor, capCodigo);
    }

    public static CapacidadeRegistroSer create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
