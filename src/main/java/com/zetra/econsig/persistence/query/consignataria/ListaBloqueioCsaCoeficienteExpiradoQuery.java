package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioCsaCoeficienteExpiradoQuery</p>
 * <p>Description: Listagem de consignatárias que possuem coeficientes expirados
 * e que não possuem um coeficiente ativo para o mesmo prazo e serviço.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioCsaCoeficienteExpiradoQuery extends HQuery {

    private final String csaCodigo;

    public ListaBloqueioCsaCoeficienteExpiradoQuery(String csaCodigo) {
        super();
        this.csaCodigo = csaCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String campos = "csa.csaCodigo, csa.csaAtivo, csa.ncaCodigo";

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT ").append(campos);
        corpoBuilder.append(" FROM Coeficiente cft");
        corpoBuilder.append(" INNER JOIN cft.prazoConsignataria pzc");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN prz.servico svc");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse");

        // Possui um convênio ativo entre o SVC e CSA
        corpoBuilder.append(" WHERE EXISTS (SELECT 1");
        corpoBuilder.append(" FROM Convenio cnv");
        corpoBuilder.append(" WHERE cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" AND cnv.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(")");

        // Que o prazo esteja ativo para a consignatária
        corpoBuilder.append(" AND coalesce(pzc.przCsaAtivo, ").append(CodedValues.STS_ATIVO).append(") = ").append(CodedValues.STS_ATIVO);
        // Que o prazo esteja ativo para o serviço
        corpoBuilder.append(" AND coalesce(prz.przAtivo, ").append(CodedValues.STS_ATIVO).append(") = ").append(CodedValues.STS_ATIVO);
        // Que o parâmetro de serviço de dias de vigênca do CET esteja preenchido
        corpoBuilder.append(" AND pse.tpsCodigo = '").append(CodedValues.TPS_DIAS_VIGENCIA_CET).append("'");
        corpoBuilder.append(" AND nullif(trim(pse.pseVlr), '') IS NOT NULL");
        // Que a vigência das taxas tenha expirado
        corpoBuilder.append(" AND cft.cftDataFimVig < current_timestamp()");

        // Que não tenha uma outra taxa ativa para o mesmo serviço/consignataria, independente do prazo
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1");
        corpoBuilder.append(" FROM CoeficienteAtivo cft2");
        corpoBuilder.append(" INNER JOIN cft2.prazoConsignataria pzc2");
        corpoBuilder.append(" INNER JOIN pzc2.prazo prz2");
        corpoBuilder.append(" WHERE prz2.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND pzc2.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" AND (cft2.cftDataFimVig IS NULL OR cft2.cftDataFimVig > current_timestamp())");
        corpoBuilder.append(")");

        // Que não tenha  regra de taxa ativa para o mesmo consignatária/serviço
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1");
        corpoBuilder.append(" FROM DefinicaoTaxaJuros dtj2");
        corpoBuilder.append(" WHERE dtj2.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" AND dtj2.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND (dtj2.dtjDataVigenciaFim IS NULL OR dtj2.dtjDataVigenciaFim > current_timestamp())");
        corpoBuilder.append(")");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" GROUP BY ").append(campos);

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_ATIVO,
                Columns.NCA_CODIGO
        };
    }
}
