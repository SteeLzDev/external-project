package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ModeloTermoAditivoHome</p>
 * <p>Description: Classe Home para a entidade ModeloTermoAditivo</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModeloTermoAditivoHome extends AbstractEntityHome {

    public static ModeloTermoAditivo findByPrimaryKey(String mtaCodigo) throws FindException {
        final ModeloTermoAditivo modeloTermoAditivo = new ModeloTermoAditivo();
        modeloTermoAditivo.setMtaCodigo(mtaCodigo);
        return find(modeloTermoAditivo, mtaCodigo);
    }

    public static MotivoDependencia create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
