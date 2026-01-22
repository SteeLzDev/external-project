package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioConferenciaPermissionarioQuery</p>
 * <p>Description: Relatório de Conferência de Permissionário</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class RelatorioConferenciaPermissionarioQuery extends ReportHNativeQuery {

    public String csaCodigo;
    public String echCodigo;
    public String posCodigo;
    public String trsCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        // Recupera os dados do permissionário
        corpoBuilder.append("select ").append(Columns.RSE_MATRICULA).append(" as rse_matricula, ");
        corpoBuilder.append(Columns.SER_CPF).append(" as ser_cpf, ");
        corpoBuilder.append(Columns.SER_NOME).append(" as ser_nome, ");
        corpoBuilder.append(Columns.ECH_DESCRICAO).append(" as ech_descricao, ");
        corpoBuilder.append(" to_locale_date(").append(Columns.PRM_DATA_OCUPACAO).append(") as prm_data_ocupacao, ");
        corpoBuilder.append(" to_locale_date(").append(Columns.PRM_DATA_DESOCUPACAO).append(") as prm_data_desocupacao, ");
        corpoBuilder.append(Columns.PRM_EMAIL).append(" as prm_email, ");
        corpoBuilder.append(Columns.POS_DESCRICAO).append(" as pos_descricao, ");
        corpoBuilder.append(Columns.TRS_DESCRICAO).append(" as trs_descricao, ");
        corpoBuilder.append(" case when ").append(Columns.PRM_ATIVO).append(" = 1 then 'Ativo' else 'Excluído' end as prm_ativo ");
        corpoBuilder.append("from ").append(Columns.TB_PERMISSIONARIO).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" on (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PRM_CSA_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_REGISTRO_SERVIDOR).append(" on (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.PRM_RSE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVIDOR).append(" on (").append(Columns.SER_CODIGO).append(" = ").append(Columns.RSE_SER_CODIGO).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_ENDERECO_CONJUNTO_HABITACIONAL).append(" on (").append(Columns.ECH_CODIGO).append(" = ").append(Columns.PRM_ECH_CODIGO).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_POSTO_REGISTRO_SERVIDOR).append(" on (").append(Columns.POS_CODIGO).append(" = ").append(Columns.RSE_POS_CODIGO).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_TIPO_REGISTRO_SERVIDOR).append(" on (").append(Columns.TRS_CODIGO).append(" = ").append(Columns.RSE_TRS_CODIGO).append(") ");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" and ").append(Columns.ECH_CODIGO).append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(posCodigo)) {
            corpoBuilder.append(" and ").append(Columns.POS_CODIGO).append(criaClausulaNomeada("posCodigo", posCodigo));
        }

        if (!TextHelper.isNull(trsCodigo)) {
            corpoBuilder.append(" and ").append(Columns.TRS_CODIGO).append(criaClausulaNomeada("trsCodigo", trsCodigo));
        }

        corpoBuilder.append(" order by ").append(Columns.SER_NOME);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(posCodigo)) {
            defineValorClausulaNomeada("posCodigo", posCodigo, query);
        }
        if (!TextHelper.isNull(trsCodigo)) {
            defineValorClausulaNomeada("trsCodigo", trsCodigo, query);
        }

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    	csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
    	echCodigo = (String) criterio.getAttribute("ENDERECO");
        posCodigo = (String) criterio.getAttribute("POSTO");
        trsCodigo = (String) criterio.getAttribute("TIPO");
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.ECH_DESCRICAO,
                Columns.PRM_DATA_OCUPACAO,
                Columns.PRM_DATA_DESOCUPACAO,
                Columns.PRM_EMAIL,
                Columns.POS_DESCRICAO,
                Columns.TRS_DESCRICAO,
                Columns.PRM_ATIVO
        };
    }
}