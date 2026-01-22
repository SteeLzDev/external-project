package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaEnderecosConsignatariaQuery</p>
 * <p>Description: Listagem de Endereços de Consignatária</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEnderecosCorrespondenteQuery extends HQuery {

    public String corCodigo;
    public String csaCodigo;

    public boolean count = false;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "SELECT ecr.ecrCodigo, " +
                    " ecr.tipoEndereco.tieCodigo, " +
                    " ecr.tipoEndereco.tieDescricao, " +
                    " ecr.ecrLogradouro, " +
                    " ecr.ecrNumero, " +
                    " ecr.ecrComplemento, " +
                    " ecr.ecrBairro, " +
                    " ecr.ecrMunicipio, " +
                    " ecr.ecrUf, " +
                    " ecr.ecrCep, " +
                    " ecr.ecrLatitude, " +
                    " ecr.ecrLongitude ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM EnderecoCorrespondente as ecr");

        corpoBuilder.append(" WHERE 1=1 ");
        
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada(" ecr.correspondente.consignataria.csaCodigo", "csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada(" ecr.correspondente.corCodigo", "corCodigo", corCodigo));
        }

        corpoBuilder.append(" ORDER BY ecr.tipoEndereco.tieDescricao, ecr.ecrCep, ecr.ecrUf, ecr.ecrMunicipio, ecr.ecrLogradouro, ecr.ecrNumero, ecr.ecrComplemento ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		Columns.ECR_CODIGO,
        		Columns.ECR_TIE_CODIGO,
        		Columns.TIE_DESCRICAO,
        		Columns.ECR_LOGRADOURO,
        		Columns.ECR_NUMERO,
        		Columns.ECR_COMPLEMENTO,
        		Columns.ECR_BAIRRO,
        		Columns.ECR_MUNICIPIO,
        		Columns.ECR_UF,
        		Columns.ECR_CEP,
        		Columns.ECR_LATITUDE,
        		Columns.ECR_LONGITUDE
        };
    }

}
