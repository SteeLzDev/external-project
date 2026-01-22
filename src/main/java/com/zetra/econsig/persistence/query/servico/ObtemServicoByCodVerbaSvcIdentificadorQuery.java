package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServicoByCodVerbaSvcIdentificadorQuery</p>
 * <p>Description: Recupera o serviço de acordo com o código de verba e o identificador deste</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemServicoByCodVerbaSvcIdentificadorQuery extends HQuery {
    public String svcIdentificador;
    public String cnvCodVerba;
    public List<String> orgCodigos;
    public String csaCodigo;
    public String nseCodigo;
    public boolean ativo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =  "select "
                     + "svc.svcCodigo, "
                     + "svc.svcIdentificador, "
                     + "svc.svcDescricao, "
                     + "svc.svcPrioridade, "
                     + "cnv.cnvCodigo, "
                     + "cnv.orgao.orgCodigo, "
                     + "cnv.consignataria.csaCodigo, "
                     + "cnv.cnvCodVerba, "
                     + "vco.vcoCodigo, "
                     + "cnv.statusConvenio.scvCodigo " ;


        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");
        corpoBuilder.append(" inner join cnv.verbaConvenioSet vco");

        corpoBuilder.append(" where 1=1 ");

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigos",orgCodigos));
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba",cnvCodVerba));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo",csaCodigo));
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" AND svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador",svcIdentificador));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo",nseCodigo));
        }

        if (ativo) {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo ").append(" = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" AND svc.svcAtivo").append(" = ").append(CodedValues.STS_ATIVO).append("");
            corpoBuilder.append(" AND vco.vcoAtivo").append(" = ").append(CodedValues.STS_ATIVO).append("");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_PRIORIDADE,
                Columns.CNV_CODIGO,
                Columns.CNV_ORG_CODIGO,
                Columns.CNV_CSA_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.VCO_CODIGO,
                Columns.CNV_SCV_CODIGO
        };
    }
}
