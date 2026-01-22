package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRelatorioTipoQuery</p>
 * <p>Description: Lista filtro relatório.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRelatorioTipoQuery extends ReportHQuery {

    public String relCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select rel.relCodigo, "
            + " rfi.tfrCodigo, "
            + " rfi.rfiExibeCse, "
            + " rfi.rfiExibeCsa, "
            + " rfi.rfiExibeCor, "
            + " rfi.rfiExibeOrg, "
            + " rfi.rfiExibeSer, "
            + " rfi.rfiExibeSup, "
            + " rfi.rfiSequencia, "
            + " rfi.rfiParametro, "
            + " tfr.tfrDescricao, "
            + " tfr.tfrRecurso ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Relatorio rel ");
        corpoBuilder.append(" left outer join rel.relatorioFiltroSet rfi ");
        corpoBuilder.append(" left outer join rfi.tipoFiltroRelatorio tfr ");
        corpoBuilder.append(" where 1=1  ");

        if (!TextHelper.isNull(relCodigo)) {
            corpoBuilder.append(" and rel.relCodigo ").append(criaClausulaNomeada("relCodigo", relCodigo));
        }

        if (responsavel.isCse()) {
            corpoBuilder.append(" AND (rfi.rfiExibeCse <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeCse is null)");
        } else if (responsavel.isOrg()) {
            corpoBuilder.append(" AND (rfi.rfiExibeOrg <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeOrg is null)");
        } else if (responsavel.isCsa()) {
            corpoBuilder.append(" AND (rfi.rfiExibeCsa <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeCsa is null)");
        } else if (responsavel.isCor()) {
            corpoBuilder.append(" AND (rfi.rfiExibeCor <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeCor is null)");
        } else if (responsavel.isSer()) {
            corpoBuilder.append(" AND (rfi.rfiExibeSer <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeSer is null)");
        } else if (responsavel.isSup()) {
            corpoBuilder.append(" AND (rfi.rfiExibeSup <> '").append(CodedValues.REL_FILTRO_NAO_EXISTENTE).append("' OR rfi.rfiExibeSup is null)");
        }

        corpoBuilder.append(" order by rfi.rfiSequencia ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(relCodigo)) {
            defineValorClausulaNomeada("relCodigo", relCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.REL_CODIGO,
                Columns.RFI_TFR_CODIGO,
                Columns.RFI_EXIBE_CSE,
                Columns.RFI_EXIBE_CSA,
                Columns.RFI_EXIBE_COR,
                Columns.RFI_EXIBE_ORG,
                Columns.RFI_EXIBE_SER,
                Columns.RFI_EXIBE_SUP,
                Columns.RFI_SEQUENCIA,
                Columns.RFI_PARAMETRO,
                Columns.TFR_DESCRICAO,
                Columns.TFR_RECURSO
                };
    }
}
