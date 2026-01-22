package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaBeneficiarioHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaBeneficiario.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class OcorrenciaBeneficiarioHome extends AbstractEntityHome {

    public static OcorrenciaBeneficiario findByPrimaryKey(String obeCodigo) throws FindException {
        OcorrenciaBeneficiario ocorrenciaBeneficiario = new OcorrenciaBeneficiario();
        ocorrenciaBeneficiario.setObeCodigo(obeCodigo);

        return find(ocorrenciaBeneficiario, obeCodigo);
    }

    public static OcorrenciaBeneficiario create(TipoOcorrencia tipoOcorrencia, Usuario usuario, Beneficiario beneficiario,
            TipoMotivoOperacao tipoMotivoOperacao, Date data, String observacao, String ipAcesso) throws CreateException {
        OcorrenciaBeneficiario ocorrenciaBeneficiario = new OcorrenciaBeneficiario();

        try {
            ocorrenciaBeneficiario.setObeCodigo(DBHelper.getNextId());
            ocorrenciaBeneficiario.setTipoOcorrencia(tipoOcorrencia);
            ocorrenciaBeneficiario.setUsuario(usuario);
            ocorrenciaBeneficiario.setBeneficiario(beneficiario);
            ocorrenciaBeneficiario.setTipoMotivoOperacao(tipoMotivoOperacao);

            if (data == null) {
                data = Calendar.getInstance().getTime();
            }
            ocorrenciaBeneficiario.setObeData(data);

            ocorrenciaBeneficiario.setObeObs(observacao);
            ocorrenciaBeneficiario.setObeIpAcesso(ipAcesso);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(ocorrenciaBeneficiario);

        return ocorrenciaBeneficiario;
    }

    public static void removeByBeneficiario(String bfcCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OcorrenciaBeneficiario obe WHERE obe.beneficiario.bfcCodigo = :bfcCodigo ");

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
