package com.zetra.econsig.persistence.query.distribuirconsignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorVerbaComConsignacaoParaDistribuicaoQuery</p>
 * <p>Description: Listagem de registros servidores e convênios que possuem
 * consignações para distribuição entre serviços</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorVerbaComConsignacaoParaDistribuicaoQuery extends HQuery {

    public String svcCodigoOrigem;
    public List<String> csaCodigos;

    public String rseMatricula;
    public String serCPF;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT min(ade.adeNumero), ade.registroServidor.rseCodigo, ade.verbaConvenio.vcoCodigo, cnv.orgao.orgCodigo, cnv.consignataria.csaCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");

        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo NOT IN (:sadCodigos) ");
        corpoBuilder.append("AND cnv.servico.svcCodigo = :svcCodigoOrigem ");

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            corpoBuilder.append("AND cnv.consignataria.csaCodigo IN (:csaCodigos) ");
        }

        // Adiciona cláusula de matrícula e CPF: o método já valida se são diferente de nulo
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCPF, true));

        // Agrupa o resultado por registro servidor e verba convênio pois só queremos os casos
        // onde esta combinação existe em quantidade maior que 1
        corpoBuilder.append(" GROUP BY ade.registroServidor.rseCodigo, ade.verbaConvenio.vcoCodigo, cnv.orgao.orgCodigo, cnv.consignataria.csaCodigo");
        corpoBuilder.append(" HAVING COUNT(*) > 1");

        // Ordena pelos ADE Números
        corpoBuilder.append(" ORDER BY 1");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_INATIVOS, query);
        defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        // Define os valores de matrícula e CPF caso informados
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCPF, true, query);

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.ADE_NUMERO,
        		Columns.RSE_CODIGO,
                Columns.VCO_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CSA_CODIGO
         };
    }
}
