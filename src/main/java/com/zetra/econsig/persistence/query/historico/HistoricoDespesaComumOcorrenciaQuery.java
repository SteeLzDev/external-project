package com.zetra.econsig.persistence.query.historico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoDespesaComumOcorrenciaQuery</p>
 * <p>Description: Listagem de ocorrências de despesa comum para
 * exibição do histórico</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Nostrum</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoDespesaComumOcorrenciaQuery extends HQuery {

    public String decCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                       "odc.odcData," +
                       "odc.odcIpAcesso," +
                       "usu.usuLogin, " +
                       "usu.usuTipoBloq, " +
                       "usu.usuCodigo, " +
                       "usuarioCsa.csaCodigo, " +
                       "usuarioCse.cseCodigo, " +
                       "usuarioCor.corCodigo, " +
                       "usuarioOrg.orgCodigo, " +
                       "usuarioSer.serCodigo, " +
                       "usuarioSup.cseCodigo, " +
                       "toc.tocCodigo, " +
                       "toc.tocDescricao, " +
                       "odc.odcObs ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM OcorrenciaDespesaComum odc ");
        corpoBuilder.append(" INNER JOIN odc.usuario usu ");
        corpoBuilder.append(" INNER JOIN odc.tipoOcorrencia toc ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE odc.despesaComum.decCodigo ").append(criaClausulaNomeada("decCodigo", decCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("decCodigo", decCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ODC_DATA,
                Columns.ODC_IP_ACESSO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.USU_CODIGO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.ODC_OBS,
        };
    }
}