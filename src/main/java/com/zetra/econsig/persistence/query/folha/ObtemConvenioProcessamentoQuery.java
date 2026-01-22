package com.zetra.econsig.persistence.query.folha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemServidorProcessamentoQuery</p>
 * <p>Description: Obtém os servidores para criação de banco de dados em memória</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConvenioProcessamentoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    public List<String> cnvCodigos;
    public String rseCodigo;
    public String rseMatricula;
    public String serCpf;
    public Long adeNumero;
    public String adeIndice;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT vco.convenio.cnvCodigo ");

        corpoBuilder.append("FROM ParcelaDescontoPeriodo pdp ");
        corpoBuilder.append("INNER JOIN pdp.autDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");

        if (TextHelper.isNull(rseCodigo) || (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)))) {
            corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
            corpoBuilder.append("INNER JOIN rse.servidor ser ");
            if (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
                corpoBuilder.append("INNER JOIN rse.orgao org ");
            }
        }

        corpoBuilder.append("WHERE vco.convenio.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigos));
        corpoBuilder.append(" AND pdp.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("'");

        if (TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));

            if (!TextHelper.isNull(serCpf)) {
                corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
            }
        } else {
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (adeNumero != null) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if (!TextHelper.isNull(adeIndice)) {
            corpoBuilder.append(" AND ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
        }

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND rse.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        corpoBuilder.append(" GROUP BY vco.convenio.cnvCodigo ");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cnvCodigo", cnvCodigos, query);

        if (TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);

            if (!TextHelper.isNull(serCpf)) {
                defineValorClausulaNomeada("serCpf", serCpf, query);
            }
        } else {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (adeNumero != null) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
