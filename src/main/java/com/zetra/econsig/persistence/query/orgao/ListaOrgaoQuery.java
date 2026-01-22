package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOrgaoQuery</p>
 * <p>Description: Listagem de órgãos</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOrgaoQuery extends HQuery {
    public Object orgAtivo;
    public String orgIdentificador;
    public String orgNome;
    public Object orgCodigo;
    public String estIdentificador;
    public String estNome;
    public String estCodigo;
    public String orgEstCodigo;
    public String csaCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select orgao.orgCodigo," +
            "orgao.orgIdentificador," +
            "orgao.orgCnpj," +
            "orgao.orgNome," +
            "orgao.orgNomeAbrev," +
            "orgao.orgAtivo," +
            "orgao.orgFolha," +
            "orgao.orgEmailFolha," +
            "orgao.orgIdentificadorBeneficio, " +
            "estabelecimento.estCodigo," +
            "estabelecimento.estIdentificador," +
            "estabelecimento.estNome," +
            "estabelecimento.estCnpj, " +
            "orgao.orgEmail ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Estabelecimento estabelecimento INNER JOIN " +
                            "estabelecimento.orgaoSet orgao WHERE 1=1 ");


        if (orgAtivo != null) {
            corpoBuilder.append(" and orgao.orgAtivo ").append(criaClausulaNomeada("orgAtivo", orgAtivo));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("orgao.orgIdentificador ", "orgIdentificador", orgIdentificador));
        }

        if (!TextHelper.isNull(orgNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("orgao.orgNome ", "orgNome", orgNome));
        }

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("estabelecimento.estIdentificador ", "estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(estNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("estabelecimento.estNome ", "estNome", estNome));
        }

        if (orgCodigo != null) {
            corpoBuilder.append(" and orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(orgEstCodigo)) {
            corpoBuilder.append(" and orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("orgEstCodigo", orgEstCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and exists (select 1 from Convenio cnv where orgao.orgCodigo = cnv.orgao.orgCodigo ");
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("' ");
            corpoBuilder.append(") ");
        }

        if (!count) {
            corpoBuilder.append(" order by orgao.orgNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(orgNome)) {
            defineValorClausulaNomeada("orgNome", orgNome, query);
        }

        if (!TextHelper.isNull(estNome)) {
            defineValorClausulaNomeada("estNome", estNome, query);
        }

        if (orgAtivo != null) {
            defineValorClausulaNomeada("orgAtivo", orgAtivo, query);
        }

        if (orgCodigo != null) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(orgEstCodigo)) {
            defineValorClausulaNomeada("orgEstCodigo", orgEstCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_CNPJ,
                Columns.ORG_NOME,
                Columns.ORG_NOME_ABREV,
                Columns.ORG_ATIVO,
                Columns.ORG_FOLHA,
                Columns.ORG_EMAIL_FOLHA,
                Columns.ORG_IDENTIFICADOR_BENEFICIO,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.EST_CNPJ,
                Columns.ORG_EMAIL
        };
    }
}
