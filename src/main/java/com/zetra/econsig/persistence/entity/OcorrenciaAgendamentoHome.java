package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaAgendamentoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade OcorrenciaAgendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaAgendamentoHome extends AbstractEntityHome {

    public static String create(String agdCodigo, String tocCodigo, Date dataInicio, Date dataFim,
                                String oagObs, String usuCodigo, String ipAcesso) throws CreateException {

        final Session session = SessionUtil.getSession();
        String oagCodigo = null;

        try {
            oagCodigo = DBHelper.getNextId();
            final OcorrenciaAgendamento bean = new OcorrenciaAgendamento();
            bean.setOagCodigo(oagCodigo);
            bean.setOagDataInicio(dataInicio);
            bean.setOagDataFim(dataFim);
            bean.setOagObs(oagObs);
            bean.setAgendamento(session.getReference(Agendamento.class, agdCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOagIpAcesso(ipAcesso);
            create(bean, session);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return oagCodigo;
    }

    public static OcorrenciaAgendamento findByPrimaryKey(String codigo) throws FindException {
        final OcorrenciaAgendamento ocorrencia = new OcorrenciaAgendamento();
        ocorrencia.setOagCodigo(codigo);

        return find(ocorrencia, codigo);
    }

    public static void deleteOcorrenciaByStatusByTipoExpiradas(List<String> sagCodigos, List<String> tagCodigos, String tocCodigo, int quantidadeDias) throws RemoveException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OcorrenciaAgendamento oag WHERE oag.oagDataInicio < add_day(current_date(), :quantidadeDias) ");
            hql.append("AND oag.agdCodigo in ( ");
            hql.append("SELECT agdCodigo FROM Agendamento agd WHERE agd.sagCodigo in (:sagCodigos) AND agd.tagCodigo in (:tagCodigos) ");
            hql.append(") ");
            hql.append("AND oag.tocCodigo = :tocCodigo ");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("sagCodigos", sagCodigos);
            queryUpdate.setParameter("tagCodigos", tagCodigos);
            queryUpdate.setParameter("tocCodigo", tocCodigo);
            queryUpdate.setParameter("quantidadeDias", quantidadeDias*-1);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
