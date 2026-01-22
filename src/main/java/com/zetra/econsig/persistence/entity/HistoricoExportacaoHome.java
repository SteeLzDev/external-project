package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoExportacaoHome</p>
 * <p>Description: Classe Home para a entidade HistoricoExportacao</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoExportacaoHome extends AbstractEntityHome {

    public static HistoricoExportacao findByPrimaryKey(String hieCodigo) throws FindException {
        final HistoricoExportacao bean = new HistoricoExportacao();
        bean.setHieCodigo(hieCodigo);
        return find(bean, hieCodigo);
    }

    public static HistoricoExportacao create(String usuCodigo, String orgCodigo, Date hieDataIni, Date hieDataFim, java.sql.Date hiePeriodo, Date hieDataIniExp, Date hieDataFimExp) throws CreateException {

        final Session session = SessionUtil.getSession();
        final HistoricoExportacao bean = new HistoricoExportacao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setHieCodigo(objectId);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
            bean.setHieDataIni(hieDataIni);
            bean.setHieDataFim(hieDataFim);
            bean.setHiePeriodo(hiePeriodo);
            bean.setHieData(DateHelper.getSystemDatetime());
            bean.setHieDataInicioExp(hieDataIniExp);
            bean.setHieDataFimExp(hieDataFimExp);
            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<HistoricoExportacao> findByPeriodoOrgcodigo(List<Date> periodos, List<String> orgCodigos) throws FindException {
        final String query = "FROM HistoricoExportacao hist WHERE hist.hiePeriodo IN (:periodos) AND hist.orgCodigo IN (:orgCodigos) ";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("periodos", periodos);
        parameters.put("orgCodigos", orgCodigos);

        return findByQuery(query, parameters);
    }
}
