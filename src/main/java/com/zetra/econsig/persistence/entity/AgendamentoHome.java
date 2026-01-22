package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: AgendamentoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade Agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AgendamentoHome extends AbstractEntityHome {

    public static Agendamento findByPrimaryKey(String codigo) throws FindException {
        Agendamento agendamento = new Agendamento();
        agendamento.setAgdCodigo(codigo);
        return find(agendamento, codigo);
    }

    public static Agendamento create(String descricao, Date dataCadastro, Date dataPrevista, String classe,
            String tagCodigo, String sagCodigo, String usuCodigo, String relCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        Agendamento bean = new Agendamento();

        try {
            String objectId = DBHelper.getNextId();
            bean.setAgdCodigo(objectId);
            bean.setAgdDescricao(descricao);
            bean.setAgdDataCadastro(dataCadastro);
            bean.setAgdDataPrevista(dataPrevista);
            bean.setAgdJavaClassName(classe);

            StatusAgendamentoEnum status = StatusAgendamentoEnum.recuperaStatusAgendamento(sagCodigo);
            bean.setStatusAgendamento((StatusAgendamento) session.getReference(StatusAgendamento.class, status.getCodigo()));

            TipoAgendamentoEnum tipo = TipoAgendamentoEnum.recuperaTipoAgendamento(tagCodigo);
            bean.setTipoAgendamento((TipoAgendamento) session.getReference(TipoAgendamento.class, tipo.getCodigo()));

            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));

            if (!TextHelper.isNull(relCodigo)) {
                bean.setRelatorio((Relatorio) session.getReference(Relatorio.class, relCodigo));
            }

            create(bean, session);

        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
