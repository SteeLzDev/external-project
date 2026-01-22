package com.zetra.econsig.persistence.query.consignataria;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaComAdeSerQuery</p>
 * <p>Description: Lista consignatárias para as quais o servidor possui contratos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaComAdeSerQuery extends HQuery {
    public boolean count = false;
    public String serCodigo;
    public String rseCodigo;
    public String csaCodigo;
    public boolean somenteAtivos = false;
    public boolean sadAtivosLimite = false;
    public List<String> adeCodigosExcecao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo, csa.csaEmail";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria csa ");
        corpoBuilder.append(" inner join csa.convenioSet cnv ");
        corpoBuilder.append(" inner join cnv.verbaConvenioSet vco ");
        corpoBuilder.append(" inner join vco.autDescontoSet ade ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" and rse.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (adeCodigosExcecao != null && adeCodigosExcecao.size() > 0) {
            List<String> codigos = new ArrayList<>(adeCodigosExcecao);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        if (somenteAtivos) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SAD_CODIGOS_ATIVOS, "' , '")).append("')");
        } else  if (sadAtivosLimite) {
            // Conta com todos os status de contratos que não estão cancelados/liquidados/concluidos ou indeferidos.
            // Enquanto uma negociação de compra não estiver finalizada, ambas as pontas (Aguard. Liquidação e Aguard.
            // Confirmação) serão incluidas no limite de consignatárias.
            List<String> sadCodigos = new ArrayList<>();
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE);
            sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);

            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.joinWithEscapeSql(sadCodigos, "' , '")).append("')");
        }


        if (!count) {
            corpoBuilder.append(" group by csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo, csa.csaEmail ");
            corpoBuilder.append(" order by csa.csaNome ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (adeCodigosExcecao != null && adeCodigosExcecao.size() > 0) {
            defineValorClausulaNomeada("adeCodigos", adeCodigosExcecao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL
        };
    }

}
