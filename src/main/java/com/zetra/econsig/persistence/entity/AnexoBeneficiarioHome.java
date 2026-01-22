package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AnexoBeneficiarioHome</p>
 * <p>Description: Classe Home para a entidade AnexoBeneficiario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AnexoBeneficiarioHome extends AbstractEntityHome {

    public static AnexoBeneficiario findByPrimaryKey(AnexoBeneficiarioId id) throws FindException {
        AnexoBeneficiario anexoBeneficiario = new AnexoBeneficiario();
        anexoBeneficiario.setId(id);
        return find(anexoBeneficiario, id);
    }

    public static AnexoBeneficiario create(Beneficiario beneficiario, Usuario usuario, TipoArquivo tipoArquivo, String abfNome, String abfDescricao,
            Short abfAtivo, Date abfData, Date abfDataValidade, String abfIpAcess) throws CreateException {
        AnexoBeneficiario anexoBeneficiario = new AnexoBeneficiario();

            AnexoBeneficiarioId anexoId = new AnexoBeneficiarioId();
            anexoId.setAbfNome(abfNome);
            anexoId.setBfcCodigo(beneficiario.getBfcCodigo());
            anexoBeneficiario.setId(anexoId);
            anexoBeneficiario.setBeneficiario(beneficiario);
            anexoBeneficiario.setUsuario(usuario);
            anexoBeneficiario.setTipoArquivo(tipoArquivo);
            anexoBeneficiario.setAbfNome(abfNome);
            anexoBeneficiario.setAbfDescricao(abfDescricao);
            anexoBeneficiario.setAbfAtivo(abfAtivo);
            anexoBeneficiario.setAbfData(abfData);
            anexoBeneficiario.setAbfDataValidade(abfDataValidade);
            anexoBeneficiario.setAbfIpAcesso(abfIpAcess);

        create(anexoBeneficiario);

        return anexoBeneficiario;
    }

    public static AnexoBeneficiario findByAnexoBeneficiarioId(AnexoBeneficiarioId anexoBeneficiarioId) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT abf ");
        query.append("FROM AnexoBeneficiario abf ");
        query.append("JOIN FETCH abf.tipoArquivo tar ");
        query.append("JOIN FETCH abf.beneficiario bfc ");
        query.append("WHERE abf.id = :abfAnexoBeneficiarioId ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("abfAnexoBeneficiarioId", anexoBeneficiarioId);

        List<Object> anexoBeneficiario = findByQuery(query.toString(), parameters);

        if (anexoBeneficiario.size() < 1) {
            return null;
        } else {
            return (AnexoBeneficiario) anexoBeneficiario.get(0);
        }
    }

    public static void removeByBeneficiario(String bfcCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM AnexoBeneficiario abf WHERE abf.beneficiario.bfcCodigo = :bfcCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("bfcCodigo", bfcCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
