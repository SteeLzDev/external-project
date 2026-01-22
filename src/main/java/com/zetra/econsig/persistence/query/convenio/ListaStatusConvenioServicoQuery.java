package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusConvenioServicoQuery</p>
 * <p>Description: Lista o status dos convênios para um serviço e
 * para uma consignatária ou um órgão, de acordo com os parâmetros
 * especificados.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusConvenioServicoQuery extends HQuery {

    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(svcCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

        String corpo = "";

        if (!TextHelper.isNull(csaCodigo)) {
            corpo = "select " +
            "org.orgCodigo, " +
            "org.orgIdentificador, " +
            "org.orgNome, " +
            "est.estIdentificador, " +
            "cnv.cnvCodigo, " +
            "cnv.cnvCodVerba, " +
            "cnv.cnvCodVerbaRef, " +
            "cnv.cnvCodVerbaFerias, " +
            "cnv.cnvCodVerbaDirf, " +
            "cnv.statusConvenio.scvCodigo, " +
            "COALESCE(cnv.statusConvenio.scvCodigo, '2') AS status ";

        } else {
            corpo = "select " +
            "csa.csaCodigo, " +
            "csa.csaIdentificador, " +
            "csa.csaNome, " +
            "cnv.cnvCodigo, " +
            "cnv.cnvCodVerba, " +
            "cnv.cnvCodVerbaRef, " +
            "cnv.cnvCodVerbaFerias, " +
            "cnv.cnvCodVerbaDirf, " +
            "cnv.statusConvenio.scvCodigo, " +
            "COALESCE(cnv.statusConvenio.scvCodigo, '2') AS status ";
        }


        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" from Orgao org ");
            corpoBuilder.append(" inner join org.estabelecimento est ");
            corpoBuilder.append(" left outer join org.convenioSet cnv with ");
            corpoBuilder.append(" cnv.servico.svcCodigo = :svcCodigo and ");
            corpoBuilder.append(" cnv.consignataria.csaCodigo = :csaCodigo ");
            if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" where org.orgCodigo = :orgCodigo ");
            }
            corpoBuilder.append(" order by est.estIdentificador, org.orgNome, org.orgIdentificador ");

        } else {
            corpoBuilder.append(" from Consignataria csa ");
            corpoBuilder.append(" left outer join csa.convenioSet cnv with ");
            corpoBuilder.append(" cnv.servico.svcCodigo = :svcCodigo and ");
            corpoBuilder.append(" cnv.orgao.orgCodigo = :orgCodigo ");
            corpoBuilder.append(" order by csa.csaNome ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (!TextHelper.isNull(csaCodigo)) {
            return new String[] {
                    Columns.ORG_CODIGO,
                    Columns.ORG_IDENTIFICADOR,
                    Columns.ORG_NOME,
                    Columns.EST_IDENTIFICADOR,
                    Columns.CNV_CODIGO,
                    Columns.CNV_COD_VERBA,
                    Columns.CNV_COD_VERBA_REF,
                    Columns.CNV_COD_VERBA_FERIAS,
                    Columns.CNV_COD_VERBA_DIRF,
                    Columns.CNV_SCV_CODIGO,
                    "STATUS"
            };
        } else {
            return new String[] {
                    Columns.CSA_CODIGO,
                    Columns.CSA_IDENTIFICADOR,
                    Columns.CSA_NOME,
                    Columns.CNV_CODIGO,
                    Columns.CNV_COD_VERBA,
                    Columns.CNV_COD_VERBA_REF,
                    Columns.CNV_COD_VERBA_FERIAS,
                    Columns.CNV_COD_VERBA_DIRF,
                    Columns.CNV_SCV_CODIGO,
                    "STATUS"
            };
        }
    }
}
