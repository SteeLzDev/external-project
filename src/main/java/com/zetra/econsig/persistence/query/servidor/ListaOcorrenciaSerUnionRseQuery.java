package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaSerUnionRseQuery</p>
 * <p>Description: Listagem de Ocorrências de Servidores consolidadas com as ocorrência de suas matrículas</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaSerUnionRseQuery extends HNativeQuery {

    public boolean count = false;
    public String serCodigo;
    public String tocCodigo;
    public String tocCodigoRse;
    public String rseCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {


    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo =  "select "
                + Columns.TOC_DESCRICAO + " as TOC_DESCRICAO, "
                + Columns.OCS_CODIGO + " as OCS_CODIGO, text_to_string("
                + Columns.OCS_OBS + ") as  OCS_OBS, "
                + Columns.OCS_DATA + " as  OCS_DATA, "
                + Columns.OCS_IP_ACESSO + " as  OCS_IP_ACESSO, "
                + Columns.USU_CODIGO + " as  USU_CODIGO, "
                + Columns.USU_LOGIN + " as  USU_LOGIN, "
                + Columns.UCA_CSA_CODIGO + " as  UCA_CSA_CODIGO, "
                + Columns.UCE_CSE_CODIGO + " as  UCE_CSE_CODIGO, "
                + Columns.UCO_COR_CODIGO + " as  UCO_COR_CODIGO, "
                + Columns.UOR_ORG_CODIGO + " as  UOR_ORG_CODIGO, "
                + Columns.USE_SER_CODIGO + " as  USE_SER_CODIGO, "
                + Columns.USU_TIPO_BLOQ + " as  USU_TIPO_BLOQ";

        StringBuilder corpoBuilder = null;

        if (count) {
            corpoBuilder = new StringBuilder("SELECT SUM(qtd) FROM (");
        }

        if (!TextHelper.isNull(serCodigo)) {
            if (count) {
                corpoBuilder.append("select count(*) as qtd ");
            } else {
                corpoBuilder = new StringBuilder(corpo);
            }

            corpoBuilder.append(" from ").append(Columns.TB_OCORRENCIA_SERVIDOR);
            corpoBuilder.append(" inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" ON (").append(Columns.OCS_TOC_CODIGO).append(" = ").append(Columns.TOC_CODIGO).append(")");
            corpoBuilder.append(" inner join ").append(Columns.TB_USUARIO).append(" ON (").append(Columns.OCS_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(")");;
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_CSA).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCA_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_CSE).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCE_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_COR).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCO_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_ORG).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UOR_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_SER).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.USE_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_SUP).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.USP_USU_CODIGO).append(")");
            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(serCodigo)) {
                corpoBuilder.append(" and ").append(Columns.OCS_SER_CODIGO).append(criaClausulaNomeada("serCodigo", serCodigo));
            }

            if (!TextHelper.isNull(tocCodigo)) {
                corpoBuilder.append(" and ").append(Columns.OCS_TOC_CODIGO).append(" ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
            }
        }

        // só fará união com ocorrências de RSE se rseCodigo tiver sido informado
        if (!TextHelper.isNull(serCodigo) && !TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" UNION ALL ");
        }

        if (!TextHelper.isNull(rseCodigo)) {
            if (count) {
                corpoBuilder.append(" select count(*) as qtd ");
            } else {
                corpoBuilder.append("select "
                        + Columns.TOC_DESCRICAO + " as  TOC_DESCRICAO, "
                        + Columns.ORS_CODIGO + " as  OCS_CODIGO, text_to_string("
                        + Columns.ORS_OBS + ") as  OCS_OBS, "
                        + Columns.ORS_DATA + " as  OCS_DATA, "
                        + Columns.ORS_IP_ACESSO + " as  OCS_IP_ACESSO, "
                        + Columns.USU_CODIGO + " as  USU_CODIGO, "
                        + Columns.USU_LOGIN + " as  USU_LOGIN, "
                        + Columns.UCA_CSA_CODIGO + " as  UCA_CSA_CODIGO, "
                        + Columns.UCE_CSE_CODIGO + " as  UCE_CSE_CODIGO, "
                        + Columns.UCO_COR_CODIGO + " as  UCO_COR_CODIGO, "
                        + Columns.UOR_ORG_CODIGO + " as  UOR_ORG_CODIGO, "
                        + Columns.USE_SER_CODIGO + " as  USE_SER_CODIGO, "
                        + Columns.USU_TIPO_BLOQ + " as  USU_TIPO_BLOQ");


            }

            corpoBuilder.append(" from ").append(Columns.TB_OCORRENCIA_REGISTRO_SERVIDOR);
            corpoBuilder.append(" inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" ON (").append(Columns.ORS_TOC_CODIGO).append(" = ").append(Columns.TOC_CODIGO).append(")");
            corpoBuilder.append(" inner join ").append(Columns.TB_USUARIO).append(" ON (").append(Columns.ORS_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_CSA).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCA_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_CSE).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCE_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_COR).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UCO_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_ORG).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.UOR_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_SER).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.USE_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_USUARIO_SUP).append(" ON (").append(Columns.USU_CODIGO).append(" = ").append(Columns.USP_USU_CODIGO).append(")");
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_TIPO_MOTIVO_OPERACAO).append(" ON (").append(Columns.ORS_TMO_CODIGO).append(" = ").append(Columns.TMO_CODIGO).append(")");
            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(rseCodigo)) {
                corpoBuilder.append(" and ").append(Columns.ORS_RSE_CODIGO).append(" ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            }

            if (!TextHelper.isNull(tocCodigoRse)) {
                corpoBuilder.append(" and ").append(Columns.ORS_TOC_CODIGO).append(" ").append(criaClausulaNomeada("tocCodigoRse", tocCodigoRse));
            }

            // RELACIONAMENTO REGISTRO SERVIDOR
            corpoBuilder.append(" UNION ALL ");
            if (count) {
                corpoBuilder.append(" select count(*) as qtd ");
            } else {
                corpoBuilder.append("SELECT ");
                corpoBuilder.append("tnt.tnt_descricao as TOC_DESCRICAO, ");
                corpoBuilder.append("'' as  OCS_CODIGO, ");
                corpoBuilder.append("concatenar('").append(ApplicationResourcesHelper.getMessage("mensagem.informacao.controle.relacionamento.servidor", AcessoSistema.getAcessoUsuarioSistema())).append("', rseOrigem.rse_matricula) as  OCS_OBS, ");
                corpoBuilder.append("rre.rre_data as  OCS_DATA, ");
                corpoBuilder.append("'' as  OCS_IP_ACESSO, ");
                corpoBuilder.append("usu.usu_codigo as  USU_CODIGO, ");
                corpoBuilder.append("usu.usu_login as  USU_LOGIN, ");
                corpoBuilder.append("uca.USU_CODIGO as  UCA_CSA_CODIGO, ");
                corpoBuilder.append("uce.USU_CODIGO as  UCE_CSE_CODIGO, ");
                corpoBuilder.append("uco.USU_CODIGO as  UCO_COR_CODIGO, ");
                corpoBuilder.append("uor.USU_CODIGO as  UOR_ORG_CODIGO, ");
                corpoBuilder.append("usr.USU_CODIGO as  USE_SER_CODIGO, ");
                corpoBuilder.append("usu.usu_tipo_bloq as  USU_TIPO_BLOQ ");
            }

            corpoBuilder.append("FROM tb_relacionamento_registro_ser rre ");
            corpoBuilder.append("INNER JOIN tb_tipo_natureza tnt ON (rre.tnt_codigo = tnt.tnt_codigo) ");
            corpoBuilder.append("INNER JOIN tb_registro_servidor rseOrigem ON (rre.rse_codigo_origem = rseOrigem.rse_codigo) ");
    		corpoBuilder.append("INNER JOIN tb_registro_servidor rseDestino ON (rre.rse_codigo_destino = rseDestino.rse_codigo) ");
    		corpoBuilder.append("INNER JOIN tb_usuario usu ON (rre.usu_codigo = usu.usu_codigo) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_csa uca ON (uca.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_cse uce ON (uce.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_cor uco ON (uco.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_org uor ON (uor.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_ser usr ON (usr.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("LEFT JOIN tb_usuario_sup usp ON (usp.USU_CODIGO = usu.USU_CODIGO) ");
            corpoBuilder.append("WHERE rseOrigem.rse_codigo = :rseCodigo or rseDestino.rse_codigo = :rseCodigo ");
        }

        if (!count) {
            corpoBuilder.append(" order by OCS_DATA desc");
        }

        if (count) {
            corpoBuilder.append(" ) x");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigoRse)) {
            defineValorClausulaNomeada("tocCodigoRse", tocCodigoRse, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.OCS_CODIGO,
                Columns.OCS_OBS,
                Columns.OCS_DATA,
                Columns.OCS_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USU_TIPO_BLOQ
        };
    }

}
