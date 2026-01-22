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
 * <p>Title: ListaCalFolhaEstDataIniFimAntInvalidaQuery</p>
 * <p>Description: Lista os registros de calendário da folha do estabelecimento
 * em que a data início de um período não é consistente com a data
 * fim do período anterior: devem ter 1 segundo de diferença.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaEstDataIniFimAntInvalidaQuery extends HQuery {

    public String estCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfe.cfePeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaEst cfe ");
        corpoBuilder.append("WHERE cfe.estCodigo = :codigo ");

        if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND NOT EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaEst cfeAnt ");
            corpoBuilder.append("  WHERE cfe.estCodigo = cfeAnt.estCodigo ");
            corpoBuilder.append("    AND (cfe.cfeDataIni = add_second(cfeAnt.cfeDataFim, 1) ");     // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("      OR cfe.cfeDataIni = add_second(cfeAnt.cfeDataFim, 3601)) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append("    AND cfe.cfePeriodo > cfeAnt.cfePeriodo ");
            corpoBuilder.append("    AND NOT EXISTS ( ");
            corpoBuilder.append("      SELECT 1 FROM CalendarioFolhaEst cfeInt ");
            corpoBuilder.append("      WHERE cfe.estCodigo = cfeInt.estCodigo ");
            corpoBuilder.append("        AND cfeInt.cfePeriodo <> cfeAnt.cfePeriodo ");
            corpoBuilder.append("        AND cfeInt.cfePeriodo <> cfe.cfePeriodo ");
            corpoBuilder.append("        AND cfeInt.cfePeriodo between cfeAnt.cfePeriodo and cfe.cfePeriodo ");
            corpoBuilder.append("    ) ");
            corpoBuilder.append(") ");
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaEst cfeAnt ");
            corpoBuilder.append("  WHERE cfe.estCodigo = cfeAnt.estCodigo ");
            corpoBuilder.append("    AND cfe.cfePeriodo > cfeAnt.cfePeriodo ");
            corpoBuilder.append(") ");
        } else {
            corpoBuilder.append("AND EXISTS ( ");
            corpoBuilder.append("  SELECT 1 FROM CalendarioFolhaEst cfeAnt ");
            corpoBuilder.append("  WHERE cfe.estCodigo = cfeAnt.estCodigo ");
            corpoBuilder.append("    AND cfe.cfePeriodo = add_month(cfeAnt.cfePeriodo, 1) ");
            corpoBuilder.append("    AND cfe.cfeDataIni <> add_second(cfeAnt.cfeDataFim, 1) ");    // Adiciona 1 segundo para virada do dia
            corpoBuilder.append("    AND cfe.cfeDataIni <> add_second(cfeAnt.cfeDataFim, 3601) "); // Horário de verão 3600 segundos (1 hora) + 1 segundo para virada do dia
            corpoBuilder.append(") ");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append("AND cfe.cfeDataIni <> cfe.cfeDataFim ");
        }

        corpoBuilder.append("ORDER BY cfe.cfePeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", estCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFE_PERIODO
        };
    }
}
