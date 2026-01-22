package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: LimiteContratoGrupoQuery</p>
 * <p>Description: Relatório de limite de contratos por grupo de serviço</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimiteContratoGrupoQuery extends ReportHQuery {

    String rseMatricula;
    String rseNome;
    String order;
    String tipo;
    String crsDescricao;
    String tgsCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();
        String fields = "rse.rseMatricula AS MATRICULA, ser.serNome AS SER_NOME," +
                        "srs.srsDescricao AS SRS_DESCRICAO, rse.rseTipo AS TIPO, " +
                        "crs.crsDescricao AS CARGO, csa.csaIdentificador AS CSA_IDENTIFICADOR, " +
                        "svc.svcDescricao AS SVC_DESCRICAO, (ade.adePrazo * ade.adeVlr) AS VALOR_CONTRATO, " +
                        "ade.adePrazo AS ADE_PRAZO, ade.adeVlr AS ADE_VLR, ade.adeData AS ADE_DATA, " +
                        "(YEAR(ade.adeAnoMesFim) + MONTH(ade.adeAnoMesFim)) AS DATA_FIM" ;

        Map<String, String> orderMap = new HashMap<String, String>();
        orderMap.put("ORD01", "rse.rseMatricula");
        orderMap.put("ORD02", "ser.serNome");
        orderMap.put("ORD03", "rse.rseTipo");
        orderMap.put("ORD04", "crs.crsDescricao");
        orderMap.put("ORD05", "csa.csaIdentificador");
        orderMap.put("ORD06", "svc.svcDescricao");
        orderMap.put("ORD07", "(ade.adePrazo * ade.adeVlr)");
        orderMap.put("ORD08", "ade.adePrazo");
        orderMap.put("ORD09", "ade.adeVlr");
        orderMap.put("ORD10", "ade.adeData");
        orderMap.put("ORD11", "(year(ade.adeAnoMesFim) + month(ade.adeAnoMesFim))");

        if (!TextHelper.isNull(order)) {
            List<String> orderList = new ArrayList<String>();
            String[] orderArray = order.split(",");
            for (String element : orderArray) {
                String[] orderTemp = element.trim().split(" ");
                if (orderTemp.length == 1 && orderMap.containsKey(orderTemp[0].trim())) {
                    orderList.add(orderMap.get(orderTemp[0].trim()));
                } else if ((orderTemp.length == 2 && (orderTemp[1].trim().equalsIgnoreCase("asc") || orderTemp[1].trim().equalsIgnoreCase("desc")))
                        && orderMap.containsKey(orderTemp[0].trim())) {
                    orderList.add(orderMap.get(orderTemp[0].trim()) + " " + orderTemp[1].trim());
                }
            }
            order = TextHelper.join(orderList, ", ");
        }

        // MONTA A QUERY
        sql.append("SELECT ");
        sql.append(fields);
        sql.append(" FROM AutDesconto ade");
        sql.append(" INNER JOIN ade.registroServidor rse");
        sql.append(" INNER JOIN rse.servidor ser");
        sql.append(" INNER JOIN rse.statusRegistroServidor srs");
        sql.append(" INNER JOIN ade.verbaConvenio vco");
        sql.append(" INNER JOIN vco.convenio cnv");
        sql.append(" INNER JOIN cnv.consignataria csa");
        sql.append(" INNER JOIN cnv.servico svc");
        sql.append(" LEFT OUTER JOIN rse.cargoRegistroServidor crs");
        sql.append(" WHERE ");
        sql.append("( ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_DEFERIDA);
        sql.append("' OR ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_EMANDAMENTO).append("')");

        if (!TextHelper.isNull(tipo)) {
            sql.append(" AND rse.rseTipo ").append(criaClausulaNomeada("tipo", tipo));
        }

        // SUB-QUERY
        sql.append(" AND exists(");
        sql.append("SELECT");
        sql.append(" 1");
        sql.append(" FROM RegistroServidor rse1");
        sql.append(" INNER JOIN rse1.servidor ser1");
        sql.append(" INNER JOIN rse1.autDescontoSet ade1");
        sql.append(" INNER JOIN ade1.verbaConvenio vco1");
        sql.append(" INNER JOIN vco1.convenio cnv1");
        sql.append(" INNER JOIN cnv1.servico svc");
        sql.append(" INNER JOIN svc.tipoGrupoSvc tgs");
        sql.append(" LEFT OUTER JOIN rse1.cargoRegistroServidor crs1");
        sql.append(" WHERE");
        sql.append(" rse1.rseCodigo = rse.rseCodigo");
        sql.append(" AND (ade1.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_DEFERIDA).append("' OR ");
        sql.append(" ade1.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_EMANDAMENTO).append("')");

        if (!TextHelper.isNull(rseMatricula)) {
            sql.append(" AND rse1.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(rseNome)) {
            sql.append(" AND ser1.serNome ").append(criaClausulaNomeada("serNome", rseNome));
        }

        if (!TextHelper.isNull(crsDescricao)) {
            sql.append(" AND crs1.crsDescricao ").append(criaClausulaNomeada("crsDescricao", crsDescricao));
        }

        sql.append(" GROUP BY ");
        sql.append(" ade1.registroServidor.rseCodigo,  tgs.tgsQuantidade");
        sql.append(" HAVING COUNT(DISTINCT ade1.adeCodigo) > tgs.tgsQuantidade ");
        sql.append(")");

        sql.append(" ORDER BY ").append(order);

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(rseNome)) {
            defineValorClausulaNomeada("serNome", rseNome, query);
        }

        if (!TextHelper.isNull(tipo)) {
            defineValorClausulaNomeada("tipo", tipo, query);
        }

        if (!TextHelper.isNull(crsDescricao)) {
            defineValorClausulaNomeada("crsDescricao", crsDescricao, query);
        }

        return query;

    }

    @Override
    public void setCriterios(TransferObject criterio) {
        crsDescricao = (String) criterio.getAttribute("CARGO");
        tgsCodigo = (String) criterio.getAttribute("TGS_CODIGO");
        rseMatricula = (String) criterio.getAttribute("MATRICULA");
        rseNome = (String) criterio.getAttribute("NOME");
        tipo = (String) criterio.getAttribute("TIPO");
        order = (String) criterio.getAttribute("ORDER");
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "MATRICULA",
                Columns.SER_NOME,
                Columns.SRS_DESCRICAO,
                "TIPO",
                "CARGO",
                Columns.CSA_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                "VALOR_CONTRATO",
                Columns.ADE_PRAZO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                "DATA_FIM"
                };
    }
}

