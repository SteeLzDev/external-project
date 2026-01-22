package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
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
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TermoAdesaoHome</p>
 * <p>Description: Classe Home para a entidade TermoAdesao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoHome extends AbstractEntityHome {

    public static TermoAdesao findByPrimaryKey(String pk) throws FindException {
        final TermoAdesao termoAdesao = new TermoAdesao();
        termoAdesao.setTadCodigo(pk);
        return find(termoAdesao, pk);
    }

    public static Collection<TermoAdesao> findByFunCodigo(String funCodigo) throws FindException {
        String query = "FROM TermoAdesao tad WHERE tad.funcao.funCodigo = :funCodigo";

        if (TextHelper.isNull(funCodigo)) {
        	query += "WHERE tad.funcao.funCodigo IS NULL";
        } else {
        	query += "WHERE tad.funcao.funCodigo = :funCodigo";
        }

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }


    public static Collection<TermoAdesao> findUnreadByUsuCodigoAndFunCodigo(String usuCodigo, String funCodigo, List<String> termoAdesaoLerDepois, AcessoSistema responsavel) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append(" SELECT tad ");
        query.append(" FROM TermoAdesao tad ");
        query.append(" LEFT OUTER JOIN tad.leituraTermoUsuarioSet ltu ");
        query.append(" WITH (tad.tadData <= ltu.ltuData AND ltu.usuCodigo = :usuCodigo AND ltu.ltuTermoAceito IN ('").append(CodedValues.TPC_SIM).append("', '").append(CodedValues.TPC_NAO).append("')) ");
        query.append(" WHERE 1 = 1 ");
		query.append(" AND ltu.ltuCodigo IS NULL ");

        if (responsavel.isCse()) {
            query.append(" AND tad.tadExibeCse = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isOrg()) {
            query.append(" AND tad.tadExibeOrg = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isCsa()) {
            query.append(" AND tad.tadExibeCsa = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isCor()) {
            query.append(" AND tad.tadExibeCor = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isSer()) {
            query.append(" AND tad.tadExibeSer = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isSup()) {
            query.append(" AND tad.tadExibeSup = '").append(CodedValues.TPC_SIM).append("'");
        }

        if (TextHelper.isNull(funCodigo)) {
        	query.append(" AND tad.funcao.funCodigo IS NULL");
        } else {
        	query.append(" AND tad.funcao.funCodigo = :funCodigo");
        }

        if (termoAdesaoLerDepois != null && !termoAdesaoLerDepois.isEmpty()) {
        	query.append(" AND tad.tadCodigo NOT IN (:termoAdesaoLerDepois)");
        }

    	query.append(" ORDER BY tad.tadSequencia ASC ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        if (!TextHelper.isNull(funCodigo)) {
        	parameters.put("funCodigo", funCodigo);
        }

        if (termoAdesaoLerDepois != null && !termoAdesaoLerDepois.isEmpty()) {
        	parameters.put("termoAdesaoLerDepois", termoAdesaoLerDepois);
        }

        return findByQuery(query.toString(), parameters);
    }

    public static List<TermoAdesao> listWithoutFunCodigoAndShowToServer() throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append(" SELECT tad ");
        query.append(" FROM TermoAdesao tad ");
        query.append(" WHERE tad.funCodigo IS NULL ");
        query.append(" AND tad.tadExibeSer = '").append(CodedValues.TPC_SIM).append("'");

        return findByQuery(query.toString(), null);
    }

    public static TermoAdesao create(String usuCodigo, String funCodigo, String tadTitulo, String tadTexto, Integer tadSequencia,
            String tadExibeCse, String tadExibeOrg, String tadExibeCsa, String tadExibeCor, String tadExibeSer,
            String tadExibeSup, String tadHtml, String tadPermiteRecusar, String tadPermiteLerDepois) throws CreateException {

        final Session session = SessionUtil.getSession();

    	final TermoAdesao bean = new TermoAdesao();

        final Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String objectId = null;
        try {
			objectId = DBHelper.getNextId();

			bean.setTadCodigo(objectId);
			bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
			bean.setFuncao(session.getReference(Funcao.class, funCodigo));
			bean.setTadTitulo(tadTitulo);
			bean.setTadTexto(tadTexto);
			bean.setTadData(agora);
			bean.setTadSequencia(tadSequencia);
			bean.setTadExibeCse(tadExibeCse);
			bean.setTadExibeOrg(tadExibeOrg);
			bean.setTadExibeCsa(tadExibeCsa);
			bean.setTadExibeCor(tadExibeCor);
			bean.setTadExibeSer(tadExibeSer);
			bean.setTadExibeSup(tadExibeSup);
			bean.setTadHtml(tadHtml);
			bean.setTadPermiteRecusar(tadPermiteRecusar);
			bean.setTadPermiteLerDepois(tadPermiteLerDepois);

			create(bean);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
