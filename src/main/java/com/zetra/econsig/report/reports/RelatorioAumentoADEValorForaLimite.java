package com.zetra.econsig.report.reports;

import java.sql.Connection;
import java.util.Comparator;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAumentoADEValorForaLimiteQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioAumentoADEValorForaLimite</p>
 * <p> Description: Relatório de aumento de valor de ade acima do limite estipulado
 * pelo parâmetro de serviço 135.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAumentoADEValorForaLimite extends ReportTemplate {

    public RelatorioAumentoADEValorForaLimite() {
        hqueries = new ReportHQuery[] { new RelatorioAumentoADEValorForaLimiteQuery(1), new RelatorioAumentoADEValorForaLimiteQuery(2) };
        resultComp = new Comparator<Object[]>() {
            @Override
            public int compare(Object[] cto1, Object[] cto2) {
                // ORDER BY "consignataria", "verba", "servidor"

                int result = 0;
                int field = 10; // "consignataria"
                if (cto1[field] != null || cto2[field] != null) {
                    if (cto1[field] != null && cto2[field] != null) {
                        result = cto1[field].toString().compareTo(cto2[field].toString());
                    } else if (cto1[field] != null) {
                        result = -1;
                    } else {
                        result = 1;
                    }
                }
                if (result == 0) {
                    field = 12; // "verba"
                    if (cto1[field] != null || cto2[field] != null) {
                        if (cto1[field] != null && cto2[field] != null) {
                            result = cto1[field].toString().compareTo(cto2[field].toString());
                        } else if (cto1[field] != null) {
                            result = -1;
                        } else {
                            result = 1;
                        }
                    }
                }
                if (result == 0) {
                    field = 7; // "servidor";
                    if (cto1[field] != null || cto2[field] != null) {
                        if (cto1[field] != null && cto2[field] != null) {
                            result = cto1[field].toString().compareTo(cto2[field].toString());
                        } else if (cto1[field] != null) {
                            result = -1;
                        } else {
                            result = 1;
                        }
                    }
                }

                return result;
            }
        };
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void preSqlProcess(Connection conn) {
    }

    @Override
    public void postSqlProcess(Connection conn) {
    }
}
