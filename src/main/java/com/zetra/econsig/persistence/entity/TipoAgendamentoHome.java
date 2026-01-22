package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoAgendamentoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade TipoAgendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoAgendamentoHome extends AbstractEntityHome {

    public static TipoAgendamento findByPrimaryKey(String codigo) throws FindException {
        TipoAgendamento tipoAgendamento = new TipoAgendamento();
        tipoAgendamento.setTagCodigo(codigo);
        return find(tipoAgendamento, codigo);
    }

    public static TipoAgendamento create(String descricao) throws CreateException {
        TipoAgendamento bean = new TipoAgendamento();
        try {
            String objectId = DBHelper.getNextId();
            bean.setTagCodigo(objectId);
            bean.setTagDescricao(descricao);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }

        create(bean);
        return bean;
    }
}
