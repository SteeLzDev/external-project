package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: StatusAgendamentoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade StatusAgendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusAgendamentoHome extends AbstractEntityHome {

    public static StatusAgendamento findByPrimaryKey(String codigo) throws FindException {
        StatusAgendamento statusAgendamento = new StatusAgendamento();
        statusAgendamento.setSagCodigo(codigo);
        return find(statusAgendamento, codigo);
    }

    public static StatusAgendamento create(String descricao) throws CreateException {
        StatusAgendamento bean = new StatusAgendamento();
        try {
            String objectId = DBHelper.getNextId();
            bean.setSagCodigo(objectId);
            bean.setSagDescricao(descricao);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }

        create(bean);
        return bean;
    }
}
