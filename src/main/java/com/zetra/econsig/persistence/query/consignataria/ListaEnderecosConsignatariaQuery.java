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
public class ListaEnderecosConsignatariaQuery extends HQuery {

    public String csaCodigo;

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "SELECT enc.encCodigo, " +
                    " enc.tipoEndereco.tieCodigo, " +
                    " enc.tipoEndereco.tieDescricao, " +
                    " enc.encLogradouro, " +
                    " enc.encNumero, " +
                    " enc.encComplemento, " +
                    " enc.encBairro, " +
                    " enc.encMunicipio, " +
                    " enc.encUf, " +
                    " enc.encCep, " +
                    " enc.encLatitude, " +
                    " enc.encLongitude ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM EnderecoConsignataria as enc");

        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada(" enc.consignataria.csaCodigo", "csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" ORDER BY enc.tipoEndereco.tieDescricao, enc.encCep, enc.encUf, enc.encMunicipio, enc.encLogradouro, enc.encNumero, enc.encComplemento ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		Columns.ENC_CODIGO,
        		Columns.ENC_TIE_CODIGO,
        		Columns.TIE_DESCRICAO,
        		Columns.ENC_LOGRADOURO,
        		Columns.ENC_NUMERO,
        		Columns.ENC_COMPLEMENTO,
        		Columns.ENC_BAIRRO,
        		Columns.ENC_MUNICIPIO,
        		Columns.ENC_UF,
        		Columns.ENC_CEP,
        		Columns.ENC_LATITUDE,
        		Columns.ENC_LONGITUDE
        };
    }

}
