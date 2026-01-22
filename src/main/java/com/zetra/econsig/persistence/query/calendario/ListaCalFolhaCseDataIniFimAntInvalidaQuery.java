package com.zetra.econsig.persistence.query.calendario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaCseDataIniFimAntInvalidaQuery</p>
 * <p>Description: Lista os registros de calendário da folha geral
 * em que a data início de um período não é consistente com a data
 * fim do período anterior: devem ter 1 segundo de diferença.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaCseDataIniFimAntInvalidaQuery extends HQuery {

    public String cseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfc.cfcPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaCse cfc ");
        corpoBuilder.append("WHERE cfc.cseCodigo = :codigo ");

        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND NOT EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaCse cfcAnt ");
            corpoBuilder.append("  WHERE cfc.cseCodigo = cfcAnt.cseCodigo ");
            corpoBuilder.append("    AND (cfc.cfcDataIni = add_second(cfcAnt.cfcDataFim, 1) ");     // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("      OR cfc.cfcDataIni = add_second(cfcAnt.cfcDataFim, 3601)) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append("    AND cfc.cfcPeriodo > cfcAnt.cfcPeriodo ");
            corpoBuilder.append("    AND NOT EXISTS ( ");
            corpoBuilder.append("      SELECT 1 FROM CalendarioFolhaCse cfcInt ");
            corpoBuilder.append("      WHERE cfc.cseCodigo = cfcInt.cseCodigo ");
            corpoBuilder.append("        AND cfcInt.cfcPeriodo <> cfcAnt.cfcPeriodo ");
            corpoBuilder.append("        AND cfcInt.cfcPeriodo <> cfc.cfcPeriodo ");
            corpoBuilder.append("        AND cfcInt.cfcPeriodo between cfcAnt.cfcPeriodo and cfc.cfcPeriodo ");
            corpoBuilder.append("    ) ");
            corpoBuilder.append(") ");
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaCse cfcAnt ");
            corpoBuilder.append("  WHERE cfc.cseCodigo = cfcAnt.cseCodigo ");
            corpoBuilder.append("    AND cfc.cfcPeriodo > cfcAnt.cfcPeriodo ");
            corpoBuilder.append(") ");
        } else {
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaCse cfcAnt ");
            corpoBuilder.append("  WHERE cfc.cseCodigo = cfcAnt.cseCodigo ");
            corpoBuilder.append("    AND cfc.cfcPeriodo = add_month(cfcAnt.cfcPeriodo, 1) ");
            corpoBuilder.append("    AND cfc.cfcDataIni <> add_second(cfcAnt.cfcDataFim, 1) ");    // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("    AND cfc.cfcDataIni <> add_second(cfcAnt.cfcDataFim, 3601) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append(") ");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND cfc.cfcDataIni <> cfc.cfcDataFim ");
        }

        corpoBuilder.append("ORDER BY cfc.cfcPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", cseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFC_PERIODO
        };
    }
}
