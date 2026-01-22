package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

public class ListaRelatorioQuery extends ReportHQuery {

    public String relCodigo;
    public String relTitulo;
    public Short relAtivo;
    public String relCustomizado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select rel.relCodigo, "
            + " rel.relTitulo, "
            + " rel.relAtivo, "
            + " rel.relAgendado, "
            + " rel.relClasseRelatorio, "
            + " rel.relClasseProcesso, "
            + " rel.relClasseAgendamento, "
            + " rel.relTemplateJasper, "
            + " rel.relTemplateDinamico, "
            + " rel.relTemplateSubrelatorio, "
            + " rel.funcao.funCodigo, "
            + " rel.tipoAgendamento.tagCodigo, "
            + " rel.relTemplateSql, "
            + " rel.relQtdDiasLimpeza,"
            + " rel.relCustomizado,"
            + " rel.relAgrupamento";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Relatorio rel ");
        corpoBuilder.append(" where 1=1  ");

        if (!TextHelper.isNull(relCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("rel.relCodigo", "relCodigo", relCodigo));
        }
        if (!TextHelper.isNull(relTitulo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("rel.relTitulo", "relTitulo", relTitulo));
        }
        if (!TextHelper.isNull(relAtivo)) {
            corpoBuilder.append(" and rel.relAtivo ").append(criaClausulaNomeada("relAtivo", relAtivo));
        }
        if (!TextHelper.isNull(relCustomizado)) {
            corpoBuilder.append(" and rel.relCustomizado ").append(criaClausulaNomeada("relCustomizado", relCustomizado));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(relCodigo)) {
            defineValorClausulaNomeada("relCodigo", relCodigo, query);
        }
        if (!TextHelper.isNull(relTitulo)) {
            defineValorClausulaNomeada("relTitulo", relTitulo, query);
        }
        if (!TextHelper.isNull(relAtivo)) {
            defineValorClausulaNomeada("relAtivo", relAtivo, query);
        }
        if (!TextHelper.isNull(relCustomizado)) {
            defineValorClausulaNomeada("relCustomizado", relCustomizado, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.REL_CODIGO,
                Columns.REL_TITULO,
                Columns.REL_ATIVO,
                Columns.REL_AGENDADO,
                Columns.REL_CLASSE_RELATORIO,
                Columns.REL_CLASSE_PROCESSO,
                Columns.REL_CLASSE_AGENDAMENTO,
                Columns.REL_TEMPLATE_JASPER,
                Columns.REL_TEMPLATE_DINAMICO,
                Columns.REL_TEMPLATE_SUBRELATORIO,
                Columns.REL_FUN_CODIGO,
                Columns.REL_TAG_CODIGO,
                Columns.REL_TEMPLATE_SQL,
                Columns.REL_QTD_DIAS_LIMPEZA,
                Columns.REL_CUSTOMIZADO,
                Columns.REL_AGRUPAMENTO
                };
    }
}
