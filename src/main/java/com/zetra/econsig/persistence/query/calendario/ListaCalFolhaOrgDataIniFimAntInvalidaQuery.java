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
 * <p>Title: ListaCalFolhaOrgDataIniFimAntInvalidaQuery</p>
 * <p>Description: Lista os registros de calendário da folha do órgão
 * em que a data início de um período não é consistente com a data
 * fim do período anterior: devem ter 1 segundo de diferença.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaOrgDataIniFimAntInvalidaQuery extends HQuery {

    public String orgCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfo.cfoPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaOrg cfo ");
        corpoBuilder.append("WHERE cfo.orgCodigo = :codigo ");

        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND NOT EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaOrg cfoAnt ");
            corpoBuilder.append("  WHERE cfo.orgCodigo = cfoAnt.orgCodigo ");
            corpoBuilder.append("    AND (cfo.cfoDataIni = add_second(cfoAnt.cfoDataFim, 1) ");     // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("      OR cfo.cfoDataIni = add_second(cfoAnt.cfoDataFim, 3601)) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append("    AND cfo.cfoPeriodo > cfoAnt.cfoPeriodo ");
            corpoBuilder.append("    AND NOT EXISTS ( ");
            corpoBuilder.append("      SELECT 1 FROM CalendarioFolhaOrg cfoInt ");
            corpoBuilder.append("      WHERE cfo.orgCodigo = cfoInt.orgCodigo ");
            corpoBuilder.append("        AND cfoInt.cfoPeriodo <> cfoAnt.cfoPeriodo ");
            corpoBuilder.append("        AND cfoInt.cfoPeriodo <> cfo.cfoPeriodo ");
            corpoBuilder.append("        AND cfoInt.cfoPeriodo between cfoAnt.cfoPeriodo and cfo.cfoPeriodo ");
            corpoBuilder.append("    ) ");
            corpoBuilder.append(") ");
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaOrg cfoAnt ");
            corpoBuilder.append("  WHERE cfo.orgCodigo = cfoAnt.orgCodigo ");
            corpoBuilder.append("    AND cfo.cfoPeriodo > cfoAnt.cfoPeriodo ");
            corpoBuilder.append(") ");
        } else {
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaOrg cfoAnt ");
            corpoBuilder.append("  WHERE cfo.orgCodigo = cfoAnt.orgCodigo ");
            corpoBuilder.append("    AND cfo.cfoPeriodo = add_month(cfoAnt.cfoPeriodo, 1) ");
            corpoBuilder.append("    AND cfo.cfoDataIni <> add_second(cfoAnt.cfoDataFim, 1) ");    // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("    AND cfo.cfoDataIni <> add_second(cfoAnt.cfoDataFim, 3601) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append(") ");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND cfo.cfoDataIni <> cfo.cfoDataFim ");
        }

        corpoBuilder.append("ORDER BY cfo.cfoPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", orgCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFO_PERIODO
        };
    }
}
