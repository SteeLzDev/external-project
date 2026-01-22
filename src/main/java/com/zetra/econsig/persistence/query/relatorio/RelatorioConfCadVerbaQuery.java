package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioConfCadVerbaQuery</p>
 * <p>Description: Consulta de relatório de conferência de cadastro de verbas</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConfCadVerbaQuery extends ReportHQuery {
    public AcessoSistema responsavel;
    public String csaCodigo;
    public List<String> orgCodigos;
    public List<String> svcCodigos;
    public List<String> scvCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        scvCodigos = (List<String>) criterio.getAttribute("SCV_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String sql = "select distinct cnv.cnvCodVerba AS CNV_COD_VERBA," +
        "svc.svcDescricao AS SVC_DESCRICAO," +
        "csa.csaIdentificador AS CSA_IDENTIFICADOR," +
        "csa.csaNomeAbrev AS CSA_NOME_ABREV," +
        "csa.csaNome AS CSA_NOME," +
        "(csa.csaIdentificador || ' - ' || COALESCE(NULLIF(csa.csaNomeAbrev,''),csa.csaNome)) AS CONSIGNATARIA," +
        "scv.scvDescricao AS SCV_DESCRICAO, " +
        "est.estIdentificador AS EST_IDENTIFICADOR," +
        "est.estNome AS EST_NOME";

        StringBuilder corpoBuilder = new StringBuilder(sql);

        corpoBuilder.append(" from StatusConvenio scv ");
        corpoBuilder.append(" inner join scv.convenioSet cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" where 1 = 1 ");

        if (scvCodigos != null && scvCodigos.size() > 0) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvCodigos", scvCodigos));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (svcCodigos != null && svcCodigos.size() > 0) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        corpoBuilder.append(" order by est.estIdentificador, cnv.cnvCodVerba, csa.csaNomeAbrev, svc.svcDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (scvCodigos != null && !scvCodigos.isEmpty()) {
            defineValorClausulaNomeada("scvCodigos", scvCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return  new String[] {
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.SCV_DESCRICAO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME
        };
    }


}
