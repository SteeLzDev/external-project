package com.zetra.econsig.persistence.query.historico;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoOcorrenciaParcelaQuery</p>
 * <p>Description: Listagem de ocorrências de parcela para
 * exibição do histórico</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoOcorrenciaParcelaQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;
    public List<String> tocCodigos;
    public boolean arquivado = false;
    public int offset = -1;
    public int maxResults = -1;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "prd.prdNumero," +
                       "prd.prdDataDesconto, " +
                       "prd.prdVlrPrevisto, " +
                       "prd.prdDataRealizado, " +
                       "prd.prdVlrRealizado, " +
                       "spd.spdDescricao, " +
                       "ocp.ocpData, " +
                       "usuarioCsa.csaCodigo, " +
                       "usuarioCse.cseCodigo, " +
                       "usuarioCor.corCodigo, " +
                       "usuarioOrg.orgCodigo, " +
                       "usuarioSer.serCodigo, " +
                       "usuarioSup.cseCodigo, " +
                       "usu.usuCodigo, " +
                       "usu.usuLogin, " +
                       "usu.usuTipoBloq, " +
                       "ocp.ocpObs, " +
                       "ocp.tipoOcorrencia.tocCodigo "
                       ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(arquivado ? "FROM HtParcelaDesconto prd " : "FROM ParcelaDesconto prd ");
        corpoBuilder.append(" INNER JOIN prd.statusParcelaDesconto spd ");
        corpoBuilder.append(" INNER JOIN prd.ocorrenciaParcelaSet ocp ");
        corpoBuilder.append(" INNER JOIN ocp.usuario usu ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (tocCodigos != null && tocCodigos.size() > 0) {
            corpoBuilder.append(" AND ocp.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }
        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        corpoBuilder.append(" ORDER BY prd.prdNumero, ocp.ocpData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (tocCodigos != null && tocCodigos.size() > 0) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }
        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        if (offset != -1) {
            query.setFirstResult(offset);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_NUMERO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.PRD_VLR_REALIZADO,
                Columns.SPD_DESCRICAO,
                Columns.OCP_DATA,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.OCP_OBS,
                Columns.TOC_CODIGO
        };
    }
}
