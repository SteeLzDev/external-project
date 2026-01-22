package com.zetra.econsig.persistence.query.admin;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioOffsetQuery</p>
 * <p>Description: Registros a serem manipulados no processo diário de atualização da tb_calendario</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioOffsetQuery extends HQuery {

    public boolean mesDiff = false;
    public boolean dayDiff = false;
    public Date dateOffset;
    public Integer diffOffset;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (dateOffset == null || diffOffset == null) {
            throw new HQueryException("mensagem.usoIncorretoSistema", (AcessoSistema) null);
        }

        String corpo = "select " +
                "cal.calData, " +
                "cal.calDescricao, " +
                "cal.calDiaUtil";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Calendario cal where ");

        if (mesDiff) {
            if (diffOffset < 0) {
                corpoBuilder.append(" cal.calData between add_month(:dateOffset, :diffOffset) and :dateOffset");
                corpoBuilder.append(" order by cal.calData desc");
            } else {
                corpoBuilder.append(" cal.calData between :dateOffset and add_month(:dateOffset, :diffOffset)");
                corpoBuilder.append(" order by cal.calData");
            }
        } else if (dayDiff) {
            if (diffOffset < 0) {
                corpoBuilder.append(" cal.calData between add_day(:dateOffset, :diffOffset) and :dateOffset");
                corpoBuilder.append(" order by cal.calData desc");
            } else {
                corpoBuilder.append(" cal.calData between :dateOffset and add_day(:dateOffset, :diffOffset)");
                corpoBuilder.append(" order by cal.calData");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (dateOffset != null) {
            defineValorClausulaNomeada("dateOffset", dateOffset, query);
        }
        if (diffOffset != null) {
            defineValorClausulaNomeada("diffOffset", diffOffset, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAL_DATA,
                Columns.CAL_DESCRICAO,
                Columns.CAL_DIA_UTIL
        };
    }
}
