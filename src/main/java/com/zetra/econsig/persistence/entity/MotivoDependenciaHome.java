package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: MotivoDependenciaHome</p>
 * <p>Description: Classe Home para a entidade MotivoDependencia</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MotivoDependenciaHome extends AbstractEntityHome {

    public static MotivoDependencia findByPrimaryKey(String mdeCodigo) throws FindException {
        MotivoDependencia motivoDependencia = new MotivoDependencia();
        motivoDependencia.setMdeCodigo(mdeCodigo);
        return find(motivoDependencia, mdeCodigo);
    }

    public static MotivoDependencia create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
