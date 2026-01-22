package com.zetra.econsig.persistence.query.coeficiente;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaCoeficienteQuery</p>
 * <p>Description: Listagem de Ocorrência de Coeficiente</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaCoeficienteQuery extends HQuery {

    public boolean count = false;
    public String svcCodigo;
    public String csaCodigo;
    public List<String> tocCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                    + "ocf.ocfCodigo, "
                    + "svc.svcCodigo, "
                    + "svc.svcDescricao, "
                    + "csa.csaCodigo, "
                    + "csa.csaNome, "
                    + "usu.usuCodigo, "
                    + "usu.usuLogin, "
                    + "usu.usuTipoBloq, "+
                    "usuarioCsa.csaCodigo, " +
                    "usuarioCse.cseCodigo, " +
                    "usuarioCor.corCodigo, " +
                    "usuarioOrg.orgCodigo, " +
                    "usuarioSer.serCodigo, " +
                    "usuarioSup.cseCodigo, " +
                    "toc.tocCodigo, " +
                    "toc.tocDescricao, " +
                    "ocf.ocfData, " +
                    "ocf.ocfDataIniVig, " +
                    "ocf.ocfDataFimVig, " +
                    "ocf.ocfObs, " +
                    "ocf.ocfIpAcesso "
                    ;
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaCoeficiente ocf ");
        corpoBuilder.append("inner join ocf.servico svc ");
        corpoBuilder.append("inner join ocf.consignataria csa ");
        corpoBuilder.append("inner join ocf.tipoOcorrencia toc ");
        corpoBuilder.append("inner join ocf.usuario usu ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append("WHERE 1=1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by ocf.ocfData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCF_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.OCF_DATA,
                Columns.OCF_DATA_INICIO_VIG,
                Columns.OCF_DATA_FIM_VIG,
                Columns.OCF_OBS,
                Columns.OCF_IP_ACESSO
        };
    }
}
