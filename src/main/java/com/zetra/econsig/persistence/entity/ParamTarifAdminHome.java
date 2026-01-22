package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParamTarifAdminHome</p>
 * <p>Description: Classe Home para a entidade ParamTarifAdmin</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamTarifAdminHome extends AbstractEntityHome {

    public static ParamTarifAdmin findByPrimaryKey(String ptfCodigo) throws FindException {
        throw new FindException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static ParamTarifAdmin create(String ptfCodigo) throws CreateException {
        ParamTarifAdmin bean = new ParamTarifAdmin();

        bean.setPtfCodigo(ptfCodigo);
        create(bean);
        return bean;
    }


}
