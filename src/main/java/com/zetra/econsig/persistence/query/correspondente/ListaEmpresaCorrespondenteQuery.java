package com.zetra.econsig.persistence.query.correspondente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaEmpresaCorrespondenteQuery</p>
 * <p>Description: Listagem de empresas correspondentes</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEmpresaCorrespondenteQuery extends HQuery {

    public boolean count = false;
    public String csaCodigo;
    public Short ecoAtivo;
    public String ecoIdentificador;
    public String ecoNome;
    public String ecoCodigo;
    public String ecoCnpj;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = " select empresa.ecoCodigo, " +
            " empresa.ecoIdentificador, " +
            " empresa.ecoNome, " +
            " empresa.ecoAtivo, " +
            " empresa.ecoBairro, " +
            " empresa.ecoCep, " +
            " empresa.ecoCidade, " +
            " empresa.ecoCnpj, " +
            " empresa.ecoCompl, " +
            " empresa.ecoEmail, " +
            " empresa.ecoFax, " +
            " empresa.ecoLogradouro, " +
            " empresa.ecoNro, " +
            " empresa.ecoRespCargo, " +
            " empresa.ecoRespCargo2, " +
            " empresa.ecoRespCargo3, " +
            " empresa.ecoResponsavel, " +
            " empresa.ecoResponsavel2, " +
            " empresa.ecoResponsavel3, " +
            " empresa.ecoRespTelefone, " +
            " empresa.ecoRespTelefone2, " +
            " empresa.ecoRespTelefone3, " +
            " empresa.ecoTel, " +
            " empresa.ecoUf ";
            ;
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from EmpresaCorrespondente empresa WHERE 1=1 ");

        if (ecoAtivo != null) {
            corpoBuilder.append(" and empresa.ecoAtivo ").append(criaClausulaNomeada("ecoAtivo", ecoAtivo));
        }

        if (!TextHelper.isNull(ecoIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("empresa.ecoIdentificador", "ecoIdentificador", ecoIdentificador));
        }

        if (!TextHelper.isNull(ecoNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("empresa.ecoNome", "ecoNome", ecoNome));
        }

        if (!TextHelper.isNull(ecoCodigo)) {
            corpoBuilder.append(" and empresa.ecoCodigo ").append(criaClausulaNomeada("ecoCodigo", ecoCodigo));
        }

        if (!TextHelper.isNull(ecoCnpj)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("substituir(substituir(substituir(empresa.ecoCnpj,'-',''),'.',''),'/','')", "ecoCnpj", ecoCnpj));
        }

        if (!count) {
            corpoBuilder.append(" order by empresa.ecoNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (ecoAtivo != null) {
            defineValorClausulaNomeada("ecoAtivo", ecoAtivo, query);
        }

        if (!TextHelper.isNull(ecoIdentificador)) {
            defineValorClausulaNomeada("ecoIdentificador", ecoIdentificador, query);
        }

        if (!TextHelper.isNull(ecoNome)) {
            defineValorClausulaNomeada("ecoNome", ecoNome, query);
        }

        if (!TextHelper.isNull(ecoCodigo)) {
            defineValorClausulaNomeada("ecoCodigo", ecoCodigo, query);
        }

        if (!TextHelper.isNull(ecoCnpj)) {
            // Remove a máscara do CNPJ já que na cláusula acima, são removidos dos valores do banco de dados.
            defineValorClausulaNomeada("ecoCnpj", TextHelper.dropSeparator(ecoCnpj), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ECO_CODIGO,
                Columns.ECO_IDENTIFICADOR,
                Columns.ECO_NOME,
                Columns.ECO_ATIVO,
                Columns.ECO_BAIRRO,
                Columns.ECO_CEP,
                Columns.ECO_CIDADE,
                Columns.ECO_CNPJ,
                Columns.ECO_COMPL,
                Columns.ECO_EMAIL,
                Columns.ECO_FAX,
                Columns.ECO_LOGRADOURO,
                Columns.ECO_NRO,
                Columns.ECO_RESP_CARGO,
                Columns.ECO_RESP_CARGO_2,
                Columns.ECO_RESP_CARGO_3,
                Columns.ECO_RESPONSAVEL,
                Columns.ECO_RESPONSAVEL_2,
                Columns.ECO_RESPONSAVEL_3,
                Columns.ECO_RESP_TELEFONE,
                Columns.ECO_RESP_TELEFONE_2,
                Columns.ECO_RESP_TELEFONE_3,
                Columns.ECO_TEL,
                Columns.ECO_UF
        };
    }

}
