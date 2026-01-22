package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioConfCadEmpresaCorQuery</p>
 * <p> Description: Gera relatório de conferência de cadastro de empresas correspondentes</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConfCadEmpresaCorQuery extends ReportHQuery {

    public String csaCodigo;
    public String ecoCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        ecoCodigo = (String) criterio.getAttribute(Columns.ECO_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select "
                     + "eco.ecoCodigo as eco_codigo, "
                     + "eco.ecoIdentificador as eco_identificador, "
                     + "eco.ecoNome as eco_nome, "
                     + "eco.ecoCnpj as eco_cnpj, "
                     + "eco.ecoResponsavel as eco_responsavel, "
                     + "eco.ecoRespCargo as eco_resp_cargo, "
                     + "eco.ecoRespTelefone as eco_resp_telefone, "
                     + "cast(eco.ecoAtivo as int) as eco_ativo, "
                     + "cor.corCodigo as cor_codigo, "
                     + "cor.corIdentificador as cor_identificador, "
                     + "cor.corNome as cor_nome, "
                     + "csa.csaCodigo as csa_codigo, "
                     + "csa.csaIdentificador as csa_identificador, "
                     + "csa.csaCnpj as csa_cnpj, "
                     + "csa.csaResponsavel as csa_responsavel, "
                     + "csa.csaRespCargo as csa_resp_cargo, "
                     + "csa.csaRespTelefone as csa_resp_telefone, "
                     + "csa.csaNome as csa_nome ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from EmpresaCorrespondente eco");
        corpoBuilder.append(" inner join eco.correspondenteSet cor");
        corpoBuilder.append(" inner join cor.consignataria csa");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(ecoCodigo)) {
            corpoBuilder.append(" and eco.ecoCodigo ").append(criaClausulaNomeada("ecoCodigo", ecoCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" order by eco_identificador, csa_identificador, cor_identificador");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(ecoCodigo)) {
            defineValorClausulaNomeada("ecoCodigo", ecoCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

}
