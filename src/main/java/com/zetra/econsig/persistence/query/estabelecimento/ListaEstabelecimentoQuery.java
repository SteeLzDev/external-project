package com.zetra.econsig.persistence.query.estabelecimento;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaEstabelecimentoQuery</p>
 * <p>Description: Query para listagem de Estabelecimentos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEstabelecimentoQuery extends HQuery {

    public boolean count = false;

    // A lista dos possíveis filtros para a query
    public String estIdentificador;
    public String estNome;
    public Short estAtivo;
    public Object estCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo +=
                "select count(*) as total ";
        } else {
            corpo +=
                "select trim(estabelecimento.estCodigo), " +
                "   estabelecimento.estIdentificador, " +
                "   estabelecimento.estNome, " +
                "   estabelecimento.estCnpj, " +
                "   estabelecimento.estNomeAbrev, " +
                "   estabelecimento.estAtivo, " + 
            	"   estabelecimento.estFolha ";
        }

        corpo += "from Estabelecimento estabelecimento where 1=1 ";

        if (!TextHelper.isNull(estIdentificador)) {
            corpo += " and " + criaClausulaNomeada("estabelecimento.estIdentificador", "estIdentificador", estIdentificador);
        }

        if (!TextHelper.isNull(estNome)) {
            corpo += " and " + criaClausulaNomeada("estabelecimento.estNome", "estNome", estNome);
        }

        if (estAtivo != null) {
            corpo += " and estabelecimento.estAtivo " + criaClausulaNomeada("estAtivo", estAtivo);
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpo += " and estabelecimento.estCodigo " + criaClausulaNomeada("estCodigo", estCodigo);
        }

        if (!count) {
            corpo += " order by estabelecimento.estNome";
        }

        Query<Object[]> query = instanciarQuery(session, corpo);

        // Seta os parâmetros na query
        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(estNome)) {
            defineValorClausulaNomeada("estNome", estNome, query);
        }

        if (estAtivo != null) {
            defineValorClausulaNomeada("estAtivo", estAtivo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.EST_CNPJ,
                Columns.EST_NOME_ABREV,
                Columns.EST_ATIVO,
                Columns.EST_FOLHA
        };
    }
}
