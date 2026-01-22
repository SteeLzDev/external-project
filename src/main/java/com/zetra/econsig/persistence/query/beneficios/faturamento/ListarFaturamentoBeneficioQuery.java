package com.zetra.econsig.persistence.query.beneficios.faturamento;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FaturamentoBeneficioQuery</p>
 * <p>Description: Query para faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: tadeu.cruz $
 * $Revision: 25571 $
 * $Date: 2018-10-10 13:59:39 -0300 (Qua, 10 out 2018) $
 */

public class ListarFaturamentoBeneficioQuery extends HQuery {

    public String fatCodigo;
    public String csaCodigo;
    public Date fatPeriodo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT distinct fat.fatCodigo, fat.fatPeriodo, csa.csaCodigo, csa.csaIdentificador, csa.csaNome, fat.fatData ");
        corpo.append("FROM FaturamentoBeneficio fat ");
        corpo.append("INNER JOIN fat.consignataria csa ");
        corpo.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(fatCodigo)) {
            corpo.append("AND fat.fatCodigo ").append(criaClausulaNomeada("fatCodigo", fatCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append("AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(fatPeriodo)) {
            corpo.append("AND fat.fatPeriodo ").append(criaClausulaNomeada("fatPeriodo", fatPeriodo));
        }

        corpo.append("ORDER BY fat.fatPeriodo desc, csa.csaIdentificador ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(fatCodigo)) {
            defineValorClausulaNomeada("fatCodigo", fatCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(fatPeriodo)) {
            defineValorClausulaNomeada("fatPeriodo", fatPeriodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FAT_CODIGO,
                Columns.FAT_PERIODO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.FAT_DATA
            };
    }

}
