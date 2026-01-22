package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoCoeficienteCorrecaoHome</p>
 * <p>Description: Classe Home para a entidade TipoCoeficienteCorrecao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoCoeficienteCorrecaoHome extends AbstractEntityHome {

    public static TipoCoeficienteCorrecao findByPrimaryKey(String tccCodigo) throws FindException {
        TipoCoeficienteCorrecao tipoCoeficienteCorrecao = new TipoCoeficienteCorrecao();
        tipoCoeficienteCorrecao.setTccCodigo(tccCodigo);
        return find(tipoCoeficienteCorrecao, tccCodigo);
    }

    public static TipoCoeficienteCorrecao create(String tccDescricao, String tccFormaCalc) throws CreateException {
        TipoCoeficienteCorrecao bean = new TipoCoeficienteCorrecao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();            
            bean.setTccCodigo(objectId);
            bean.setTccDescricao(tccDescricao);
            bean.setTccFormaCalc(tccFormaCalc);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }
}
