package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoPenalidadeHome</p>
 * <p>Description: Classe para encapsular acesso a entidade TipoPenalidade.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoPenalidadeHome extends AbstractEntityHome {

    public static TipoPenalidade findByPrimaryKey(String codigo) throws FindException {
        TipoPenalidade tipoPenalidade = new TipoPenalidade();
        tipoPenalidade.setTpeCodigo(codigo);
        return find(tipoPenalidade, codigo);
    }

    public static String create(String descricao, Short prazo) throws CreateException {
        String codigo = null;
        try {
            TipoPenalidade tipoPenalidade = new TipoPenalidade();
            codigo = DBHelper.getNextId();
            tipoPenalidade.setTpeCodigo(codigo);
            tipoPenalidade.setTpeDescricao(descricao);
            tipoPenalidade.setTpePrazoPenalidade(prazo);

            create(tipoPenalidade);

        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }

        return codigo;
    }
}
