package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaComposicaoMargemRseQuery</p>
 * <p>Description: Lista composição de margem de um registro servidor.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaComposicaoMargemRseQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select rse.rseCodigo, rse.rseMatricula, ser.serCpf, ser.serNome, "
                        + "cma.cmaCodigo, "
                        + "cma.cmaVlr, "
                        + "cma.cmaVinculo, "
                        + "cma.cmaQuantidade, "
                        + "vct.vctCodigo, "
                        + "vct.vctIdentificador, "
                        + "vct.vctDescricao, "
                        + "vrs.vrsCodigo, "
                        + "vrs.vrsIdentificador, "
                        + "vrs.vrsDescricao, "
                        + "crs.crsCodigo, "
                        + "crs.crsIdentificador, "
                        + "crs.crsDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");
        corpoBuilder.append(" inner join rse.compMargemSet cma");
        corpoBuilder.append(" inner join cma.vencimento vct");
        corpoBuilder.append(" left join cma.vinculoRegistroServidor vrs");
        corpoBuilder.append(" left join cma.cargoRegistroServidor crs");

        corpoBuilder.append(" where rse.rseCodigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.CMA_CODIGO,
                Columns.CMA_VLR,
                Columns.CMA_VINCULO,
                Columns.CMA_QUANTIDADE,
                Columns.VCT_CODIGO,
                Columns.VCT_IDENTIFICADOR,
                Columns.VCT_DESCRICAO,
                Columns.VRS_CODIGO,
                Columns.VRS_IDENTIFICADOR,
                Columns.VRS_DESCRICAO,
                Columns.CRS_CODIGO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_DESCRICAO
        };
    }

}
