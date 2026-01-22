package com.zetra.econsig.persistence.query.periodo;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUltimoPeriodoExportadoQuery</p>
 * <p>Description: Retorna a data base do último período de exportação,
 * registrado na tabela de histórico de exportação.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUltimoPeriodoExportadoQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public boolean temRetorno;
    public boolean dataIniFimDistintas;
    public Date periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select max(hie.hiePeriodo) ");
        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.historicoExportacaoSet hie ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(estCodigos)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (!TextHelper.isNull(orgCodigos)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" and hie.hiePeriodo ").append(criaClausulaNomeada("periodo", periodo));
        }

        if (temRetorno) {
            corpoBuilder.append(" and exists (select 1 from org.historicoConclusaoRetornoSet hcr ");
            corpoBuilder.append(" where hcr.hcrPeriodo = hie.hiePeriodo ");
            corpoBuilder.append(" and hcr.hcrDesfeito = 'N' ");
            corpoBuilder.append(" and hcr.hcrDataFim is not null) ");
        }

        if (dataIniFimDistintas) {
            corpoBuilder.append(" and to_date(hie.hieDataIni) <> to_date(hie.hieDataFim) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estCodigos)) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (!TextHelper.isNull(orgCodigos)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", periodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HIE_PERIODO
        };
    }
}