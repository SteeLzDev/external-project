package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaConvenioQuery</p>
 * <p>Description: lista ocorrências da entidade serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaConvenioQuery extends HQuery {

    public boolean count = false;
    public String cnvCodigo;
    public String svcCodigo;
    public String csaCodigo;
    public String orgCodigo;
    public List<String> tocCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                + "toc.tocDescricao, "
                + "cnv.cnvCodigo, "
                + "cnv.cnvCodVerba, "
                + "svc.svcDescricao, "
                + "case when nullif(trim(org.orgNomeAbrev), '') is null then org.orgNome else org.orgNomeAbrev end, "
                + "case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome else csa.csaNomeAbrev end, "
                + "oco.ocoCodigo, "
                + "oco.ocoObs, "
                + "oco.ocoData, "
                + "oco.ocoIpAcesso, "
                + "usu.usuCodigo, "
                + "usu.usuLogin, "
                + "usu.usuTipoBloq, "
                + "tmo.tmoDescricao ";

        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaConvenio oco ");
        corpoBuilder.append("inner join oco.tipoOcorrencia toc ");
        corpoBuilder.append("inner join oco.usuario usu ");
        corpoBuilder.append("inner join oco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("left join oco.tipoMotivoOperacao tmo ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(cnvCodigo)) {
            corpoBuilder.append(" and cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and oco.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by oco.ocoData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cnvCodigo)) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.ORG_NOME,
                Columns.CSA_NOME,
                Columns.OCO_CODIGO,
                Columns.OCO_OBS,
                Columns.OCO_DATA,
                Columns.OCO_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.TMO_DESCRICAO
        };
    }
}
