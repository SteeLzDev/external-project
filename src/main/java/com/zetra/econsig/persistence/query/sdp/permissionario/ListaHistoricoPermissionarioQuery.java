package com.zetra.econsig.persistence.query.sdp.permissionario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoPermissionarioQuery</p>
 * <p>Description: Listagem de histórico de permissionários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision:  $
 * $Date: 2012-12-27 18:37:00 $
 */
public class ListaHistoricoPermissionarioQuery extends HNativeQuery {

    public String prmCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> tocCodigos = new ArrayList<>();
        tocCodigos.add(CodedValues.TOC_ALTERACAO_TIPO_REGISTRO_SERVIDOR);
        tocCodigos.add(CodedValues.TOC_ALTERACAO_POSTO_REGISTRO_SERVIDOR);

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select sum(qtde) from ( ");
        }

        // Recupera as ocorrências do permissionário
        if (count) {
            corpoBuilder.append("select count(").append(Columns.OPE_CODIGO).append(") as qtde ");
        } else {
            corpoBuilder.append("select ").append(Columns.OPE_CODIGO).append(" as codigo_ocorrencia, ");
            corpoBuilder.append(Columns.TOC_DESCRICAO).append(", ");
            corpoBuilder.append(Columns.USU_LOGIN).append(", ");

            corpoBuilder.append(Columns.UCA_CSA_CODIGO).append(" as uca_csa_codigo, ");
            corpoBuilder.append(Columns.UCE_CSE_CODIGO).append(" as uce_cse_codigo, ");
            corpoBuilder.append(Columns.UCO_COR_CODIGO).append(" as uco_cor_codigo, ");
            corpoBuilder.append(Columns.UOR_ORG_CODIGO).append(" as uor_org_codigo, ");
            corpoBuilder.append(Columns.USE_SER_CODIGO).append(" as use_ser_codigo, ");
            corpoBuilder.append(Columns.USP_CSE_CODIGO).append(" as usp_cse_codigo, ");

            corpoBuilder.append(Columns.OPE_DATA).append(" as data_ocorrencia, ");
            corpoBuilder.append(Columns.OPE_IP_ACESSO).append(" as ip_acesso, ");
            corpoBuilder.append(Columns.OPE_OBS).append(" as observacao ");
        }
        corpoBuilder.append("from ").append(Columns.TB_OCORRENCIA_PERMISSIONARIO).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" on (").append(Columns.TOC_CODIGO).append(" = ").append(Columns.OPE_TOC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO).append(" on (").append(Columns.USU_CODIGO).append(" = ").append(Columns.OPE_USU_CODIGO).append(") ");

        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_CSA).append(" on (").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_CSE).append(" on (").append(Columns.UCE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_COR).append(" on (").append(Columns.UCO_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_ORG).append(" on (").append(Columns.UOR_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_SER).append(" on (").append(Columns.USE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_SUP).append(" on (").append(Columns.USP_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");

        corpoBuilder.append("where ").append(Columns.OPE_PRM_CODIGO).append(" ").append(criaClausulaNomeada("prmCodigo", prmCodigo));

        corpoBuilder.append(" union all ");

        // Recupera as ocorrências do registro servidor ligado ao permissionário
        if (count) {
            corpoBuilder.append("select count(").append(Columns.ORS_CODIGO).append(") as qtde ");
        } else {
            corpoBuilder.append("select ").append(Columns.ORS_CODIGO).append(" as codigo_ocorrencia, ");
            corpoBuilder.append(Columns.TOC_DESCRICAO).append(", ");
            corpoBuilder.append(Columns.USU_LOGIN).append(", ");

            corpoBuilder.append(Columns.UCA_CSA_CODIGO).append(" as uca_csa_codigo, ");
            corpoBuilder.append(Columns.UCE_CSE_CODIGO).append(" as uce_cse_codigo, ");
            corpoBuilder.append(Columns.UCO_COR_CODIGO).append(" as uco_cor_codigo, ");
            corpoBuilder.append(Columns.UOR_ORG_CODIGO).append(" as uor_org_codigo, ");
            corpoBuilder.append(Columns.USE_SER_CODIGO).append(" as use_ser_codigo, ");
            corpoBuilder.append(Columns.USP_CSE_CODIGO).append(" as usp_cse_codigo, ");

            corpoBuilder.append(Columns.ORS_DATA).append(" as data_ocorrencia, ");
            corpoBuilder.append(Columns.ORS_IP_ACESSO).append(" as ip_acesso, ");
            corpoBuilder.append(Columns.ORS_OBS).append(" as observacao ");
        }
        corpoBuilder.append("from ").append(Columns.TB_OCORRENCIA_REGISTRO_SERVIDOR).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" on (").append(Columns.TOC_CODIGO).append(" = ").append(Columns.ORS_TOC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO).append(" on (").append(Columns.USU_CODIGO).append(" = ").append(Columns.ORS_USU_CODIGO).append(") ");

        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_CSA).append(" on (").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_CSE).append(" on (").append(Columns.UCE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_COR).append(" on (").append(Columns.UCO_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_ORG).append(" on (").append(Columns.UOR_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_SER).append(" on (").append(Columns.USE_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");
        corpoBuilder.append("left join ").append(Columns.TB_USUARIO_SUP).append(" on (").append(Columns.USP_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");

        corpoBuilder.append("inner join ").append(Columns.TB_PERMISSIONARIO).append(" on (").append(Columns.PRM_RSE_CODIGO).append(" = ").append(Columns.ORS_RSE_CODIGO).append(" ");
        corpoBuilder.append("and ").append(Columns.ORS_TOC_CODIGO).append(" in ('").append(TextHelper.join(tocCodigos, "','")).append("')) ");
        corpoBuilder.append("where ").append(Columns.PRM_CODIGO).append(" ").append(criaClausulaNomeada("prmCodigo", prmCodigo));

        if (count) {
            corpoBuilder.append(") tabela ");
        } else {
            corpoBuilder.append(" ORDER BY data_ocorrencia desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("prmCodigo", prmCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "codigo_ocorrencia",
                Columns.TOC_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                "data_ocorrencia",
                "ip_acesso",
                "observacao"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}