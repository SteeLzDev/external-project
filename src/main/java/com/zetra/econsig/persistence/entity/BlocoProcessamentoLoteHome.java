package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;

/**
 * <p>Title: BlocoProcessamentoLoteHome</p>
 * <p>Description: Classe Home para manutenção de blocos de processamento de lote.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BlocoProcessamentoLoteHome extends AbstractEntityHome {

    public static BlocoProcessamentoLote findByPrimaryKey(String cplArquivoEconsig, Integer bplNumLinha) throws FindException {
        final BlocoProcessamentoLoteId id = new BlocoProcessamentoLoteId(cplArquivoEconsig, bplNumLinha);
        final BlocoProcessamentoLote bean = new BlocoProcessamentoLote();
        bean.setId(id);
        return find(bean, id);
    }

    public static BlocoProcessamentoLote create(ControleProcessamentoLote controleProcessamentoLote, StatusBlocoProcessamentoEnum status, String csaCodigo,
            Integer bplNumLinha, Date bplDataInclusao, Date bplPeriodo, String bplLinha, String bplCampos, Session session) throws CreateException {

        final BlocoProcessamentoLote bean = new BlocoProcessamentoLote();

        bean.setStatusBlocoProcessamento(session.getReference(StatusBlocoProcessamento.class, status.getCodigo()));
        bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
        bean.setCplArquivoEconsig(controleProcessamentoLote.getCplArquivoEconsig());

        bean.setBplNumLinha(bplNumLinha);
        bean.setBplPeriodo(bplPeriodo);
        bean.setBplDataInclusao(bplDataInclusao);
        bean.setBplLinha(bplLinha);
        bean.setBplCampos(bplCampos);

        return create(bean, session, false);
    }

    public static void atualizarStatusBlocos(StatusBlocoProcessamentoEnum statusOrigem, StatusBlocoProcessamentoEnum statusDestino, String cplArquivoEconsig, String csaCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("UPDATE BlocoProcessamentoLote bpl ");
            hql.append("SET bpl.statusBlocoProcessamento.sbpCodigo = :sbpCodigoDestino ");
            hql.append("WHERE bpl.cplArquivoEconsig = :cplArquivoEconsig ");
            hql.append("  AND bpl.statusBlocoProcessamento.sbpCodigo = :sbpCodigoOrigem ");

            if (!TextHelper.isNull(csaCodigo)) {
                hql.append(" AND bpl.consignataria.csaCodigo = :csaCodigo ");
            }

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("cplArquivoEconsig", cplArquivoEconsig);
            queryUpdate.setParameter("sbpCodigoDestino", statusDestino.getCodigo());
            queryUpdate.setParameter("sbpCodigoOrigem", statusOrigem.getCodigo());

            if (!TextHelper.isNull(csaCodigo)) {
                queryUpdate.setParameter("csaCodigo", csaCodigo);
            }

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removerBlocos(String cplArquivoEconsig) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BlocoProcessamentoLote bpl ");
            hql.append("WHERE bpl.cplArquivoEconsig = :cplArquivoEconsig ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("cplArquivoEconsig", cplArquivoEconsig);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
