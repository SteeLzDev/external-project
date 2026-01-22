package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioCorrespondentesQuery</p>
 * <p> Description: gera relatório de cadastro de correspondentes</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioCorrespondentesQuery extends ReportHQuery {
    public String csaCodigo;
    public String ecoCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select csa.csaCodigo as csa_codigo,"
                     + "csa.csaIdentificador as csa_identificador,"
                     + "csa.csaNome as csa_nome,"
                     + "cor.corCodigo as cor_codigo,"
                     + "cor.corIdentificador as cor_identificador,"
                     + "cor.corNome as cor_nome,"
                     + "cor.corEmail as cor_email,"
                     + "cor.corResponsavel as cor_responsavel,"
                     + "cor.corResponsavel2 as cor_responsavel_2,"
                     + "cor.corResponsavel3 as cor_responsavel_3,"
                     + "cor.corRespCargo as cor_resp_cargo,"
                     + "cor.corRespCargo2 as cor_resp_cargo_2,"
                     + "cor.corRespCargo3 as cor_resp_cargo_3,"
                     + "cor.corRespTelefone as cor_resp_telefone,"
                     + "cor.corRespTelefone2 as cor_resp_telefone_2,"
                     + "cor.corRespTelefone3 as cor_resp_telefone_3,"
                     + "cast(cor.corAtivo as int) as cor_ativo";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Correspondente cor");
        corpoBuilder.append(" inner join cor.consignataria csa");
        if (!TextHelper.isNull(ecoCodigo)) {
            if (ecoCodigo.equals("NENHUM")) {
                corpoBuilder.append(" left outer join cor.empresaCorrespondente eco");

            } else {
                corpoBuilder.append(" inner join cor.empresaCorrespondente eco");
            }
        }
        corpoBuilder.append(" where cor.corAtivo <> ").append(CodedValues.STS_INDISP);

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cor.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(ecoCodigo)) {
            if (ecoCodigo.equals("NENHUM")) {
                corpoBuilder.append(" and eco.ecoCodigo is null ");

            } else if (!ecoCodigo.equals("QUALQUER_UM")) {
                corpoBuilder.append(" and eco.ecoCodigo ").append(criaClausulaNomeada("ecoCodigo", ecoCodigo));
            }
        }

        corpoBuilder.append(" order by csa_identificador, cor_identificador");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(ecoCodigo) && !ecoCodigo.equals("NENHUM") && !ecoCodigo.equals("QUALQUER_UM")) {
            defineValorClausulaNomeada("ecoCodigo", ecoCodigo, query);
        }

        return query;
    }

}
