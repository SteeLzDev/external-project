package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoHabitacaoHome</p>
 * <p>Description: Classe Home para a entidade Tipo de Habitação</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoHabitacaoHome extends AbstractEntityHome {

    public static TipoHabitacao findByPrimaryKey(String thaCodigo ) throws FindException {
        TipoHabitacao tipoHabitacao = new TipoHabitacao();
        tipoHabitacao.setThaCodigo(thaCodigo);
        return find(tipoHabitacao, thaCodigo);
    }

    public static TipoHabitacao create(String thaIdentificador, String thaDescricao) throws CreateException {
        try {
            TipoHabitacao bean = new TipoHabitacao();

            bean.setThaCodigo(DBHelper.getNextId());
            bean.setThaIdentificador(thaIdentificador);
            bean.setThaDescricao(thaDescricao);
            create(bean);
            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }
}
