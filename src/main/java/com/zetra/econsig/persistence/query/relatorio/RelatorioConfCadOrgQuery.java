package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioConfCadOrgQuery</p>
 * <p>Description: Consulta de relatório de conferência de cadastro de órgãos</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConfCadOrgQuery extends ReportHQuery {

    private String estCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String sql = "select" +
                     "(est.estIdentificador || ' - ' || est.estNome) AS estabelecimento," +
                     "org.orgIdentificador AS ORG_IDENTIFICADOR," +
                     "org.orgNome AS ORG_NOME," +
                     "org.orgEmail AS ORG_EMAIL," +
                     "org.orgResponsavel AS ORG_RESPONSAVEL," +
                     "org.orgResponsavel2 AS ORG_RESPONSAVEL_2," +
                     "org.orgResponsavel3 AS ORG_RESPONSAVEL_3," +
                     "org.orgRespCargo AS ORG_RESP_CARGO," +
                     "org.orgRespCargo2 AS ORG_RESP_CARGO_2," +
                     "org.orgRespCargo3 AS ORG_RESP_CARGO_3," +
                     "org.orgAtivo AS ORG_ATIVO," +
                     "org.orgLogradouro AS ORG_LOGRADOURO," +
                     "org.orgNro AS ORG_NRO," +
                     "org.orgCompl AS ORG_COMPL," +
                     "org.orgBairro AS ORG_BAIRRO," +
                     "org.orgCidade AS ORG_CIDADE," +
                     "org.orgUf AS ORG_UF," +
                     "org.orgCep AS ORG_CEP," +
                     "org.orgTel AS ORG_TEL," +
                     "org.orgFax AS ORG_FAX,";

        StringBuilder corpoBuilder = new StringBuilder(sql);
        corpoBuilder.append("org.orgLogradouro || ',' || str(org.orgNro) || ' ' || org.orgCompl || ' - ' || org.orgBairro || ' - ' ");
        corpoBuilder.append("|| org.orgCidade || ' - ' || org.orgUf || ' - ' || org.orgCep as ENDERECO,");
        corpoBuilder.append("concat(concat(org.orgTel, ' - '), org.orgFax) as TELFAX");

        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" WHERE est.estCodigo").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        corpoBuilder.append(" order by to_numeric(est.estIdentificador), to_numeric(org.orgIdentificador), org.orgNome");

        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        return  new String[] {
                "ESTABELECIMENTO",
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_EMAIL,
                Columns.ORG_RESPONSAVEL,
                Columns.ORG_RESPONSAVEL_2,
                Columns.ORG_RESPONSAVEL_3,
                Columns.ORG_RESP_CARGO,
                Columns.ORG_RESP_CARGO_2,
                Columns.ORG_RESP_CARGO_3,
                Columns.ORG_ATIVO,
                Columns.ORG_LOGRADOURO,
                Columns.ORG_NRO,
                Columns.ORG_COMPL,
                Columns.ORG_BAIRRO,
                Columns.ORG_CIDADE,
                Columns.ORG_UF,
                Columns.ORG_CEP,
                Columns.ORG_TEL,
                Columns.ORG_FAX,
                "ENDERECO",
                "TELFAX"
        };
    }
}