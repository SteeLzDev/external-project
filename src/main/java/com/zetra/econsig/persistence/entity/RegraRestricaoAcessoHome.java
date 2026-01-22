package com.zetra.econsig.persistence.entity;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RegraRestricaoAcessoHome</p>
 * <p>Description: Classe Home para a entidade RegraRestricaoAcesso.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraRestricaoAcessoHome extends AbstractEntityHome {

	public static RegraRestricaoAcesso findByPrimaryKey(String rraCodigo) throws FindException {
		RegraRestricaoAcesso regraRestricaoAcesso = new RegraRestricaoAcesso();
        regraRestricaoAcesso.setRraCodigo(rraCodigo);
        return find(regraRestricaoAcesso, rraCodigo);
    }

	public static RegraRestricaoAcessoCsa findRestricaoAcessoCsaByEntPai(String rraCodigo) throws FindException {
		String query = "FROM RegraRestricaoAcessoCsa rac WHERE rac.id.rraCodigo = :rraCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rraCodigo", rraCodigo);

        List<RegraRestricaoAcessoCsa> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
	}

	public static RegraRestricaoAcesso create (Time rraHoraInicio, Time rraHoraFim, String rraDescricao, Date rraData, Short rraDiaSemana, String rraDiasUteis,
			String funCodigo, String csaCodigo, String papCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
		RegraRestricaoAcesso bean = new RegraRestricaoAcesso();

		String objectId = null;
		try {
			objectId = DBHelper.getNextId();
			bean.setRraCodigo(objectId);
			bean.setRraHoraInicio(rraHoraInicio);
			bean.setRraHoraFim(rraHoraFim);
			bean.setRraDescricao(rraDescricao);
			bean.setRraData(rraData);
			bean.setRraDiaSemana(rraDiaSemana);
			bean.setRraDiasUteis(rraDiasUteis);
			if (!TextHelper.isNull(funCodigo)) {
				bean.setFuncao(session.getReference(Funcao.class, funCodigo));
			}
			if (!TextHelper.isNull(papCodigo)) {
				bean.setPapel(session.getReference(Papel.class, papCodigo));
			}

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

		if (!TextHelper.isNull(csaCodigo)) {
			RegraRestricaoAcessoCsaHome.create(objectId, csaCodigo);
		}

		return bean;
	}
}
