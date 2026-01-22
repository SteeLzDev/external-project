package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: NivelEscolaridadeHome</p>
 * <p>Description: Classe Home para a entidade Nível de Escolaridade</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NivelEscolaridadeHome extends AbstractEntityHome{

    public static NivelEscolaridade findByPrimaryKey(String nesCodigo) throws FindException {
        NivelEscolaridade nivelEscolaridade = new NivelEscolaridade();
        nivelEscolaridade.setNesCodigo(nesCodigo);
        return find(nivelEscolaridade, nesCodigo);
    }

    public static NivelEscolaridade create(String nesIdentificador, String nesDescricao) throws CreateException {
        try {
            NivelEscolaridade bean = new NivelEscolaridade();

            bean.setNesCodigo(DBHelper.getNextId());
            bean.setNesIdentificador(nesIdentificador);
            bean.setNesDescricao(nesDescricao);
            create(bean);
            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }
}
