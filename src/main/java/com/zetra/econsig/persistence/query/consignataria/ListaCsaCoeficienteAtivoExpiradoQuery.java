package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaCoeficienteAtivoExpiradoQuery</p>
 * <p>Description: Listagem de consignatárias que possuem coeficientes ativos com data de expiração próxima.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaCoeficienteAtivoExpiradoQuery extends HQuery {

    private final Integer diasParaExpiracao;

    public ListaCsaCoeficienteAtivoExpiradoQuery(Integer diasParaExpiracao) {
        super();
        this.diasParaExpiracao = diasParaExpiracao;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaIdentificador, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" csa.csaAtivo, ");
        corpoBuilder.append(" csa.csaEmail, ");
        corpoBuilder.append(" csa.csaResponsavel, ");
        corpoBuilder.append(" csa.csaResponsavel2, ");
        corpoBuilder.append(" csa.csaResponsavel3, ");
        corpoBuilder.append(" csa.ncaCodigo ");

        corpoBuilder.append(" FROM Consignataria csa ");

        corpoBuilder.append(" WHERE EXISTS (SELECT 1");
        corpoBuilder.append(" FROM csa.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cfa");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN prz.servico svc");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse");
        corpoBuilder.append(" INNER JOIN svc.convenioSet cnv");

        corpoBuilder.append(" WHERE cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" AND coalesce(pzc.przCsaAtivo, ").append(CodedValues.STS_ATIVO).append(") = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND coalesce(prz.przAtivo, ").append(CodedValues.STS_ATIVO).append(") = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND pse.tpsCodigo = '").append(CodedValues.TPS_DIAS_VIGENCIA_CET).append("'");
        corpoBuilder.append(" AND nullif(trim(pse.pseVlr), '') IS NOT NULL");
        corpoBuilder.append(" AND date_diff(cfa.cftDataFimVig, data_corrente()) = :diasParaExpiracao");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("diasParaExpiracao", diasParaExpiracao, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL,
                Columns.CSA_RESPONSAVEL,
                Columns.CSA_RESPONSAVEL_2,
                Columns.CSA_RESPONSAVEL_3,
                Columns.NCA_CODIGO
        };
    }
}
