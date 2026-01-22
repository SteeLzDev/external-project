package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: LimiteContratoEntidadeQuery</p>
 * <p>Description: Relatório de Limite de Contratos por Entidade</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimiteContratoEntidadeQuery extends ReportHQuery {

    public AcessoSistema responsavel;
    public String rseMatricula;
    public String rseNome;
    public String order;
    public String tipo;
    public String crsDescricao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        Object paramMaxCsa = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, responsavel);
        int paramMaxCsaInt = TextHelper.isNum(paramMaxCsa) ? Integer.valueOf(paramMaxCsa.toString()) : 999;

        String sqlOrder = "1";
        if (!TextHelper.isNull(order)) {
            List<String> camposOrdenacao = new ArrayList<>();
            String[] orderArray = order.split(",");
            for (String element : orderArray) {
                String[] elementArray = element.trim().split(" ");
                String key = elementArray[0].trim();
                String value = mapOrderField(key);
                if (value != null) {
                    if (elementArray.length == 1) {
                        camposOrdenacao.add(value);
                    } else if (elementArray.length == 2 && (elementArray[1].trim().equalsIgnoreCase("asc") || elementArray[1].trim().equalsIgnoreCase("desc"))) {
                        camposOrdenacao.add(value + " " + elementArray[1].trim());
                    }
                }
            }
            sqlOrder = TextHelper.join(camposOrdenacao, ", ");
        }

        StringBuilder sql = new StringBuilder();
        String fields = "rse.rseMatricula AS MATRICULA, ser.serNome AS SER_NOME, " +
                        "srs.srsDescricao AS SRS_DESCRICAO, rse.rseTipo AS TIPO, " +
                        "crs.crsDescricao as CARGO, csa.csaIdentificador AS CSA_IDENTIFICADOR, " +
                        "svc.svcDescricao AS SVC_DESCRICAO, (ade.adePrazo * ade.adeVlr) AS VALOR_CONTRATO, " +
                        "ade.adePrazo AS ADE_PRAZO, ade.adeVlr AS ADE_VLR, ade.adeData AS ADE_DATA, " +
                        "(YEAR(ade.adeAnoMesFim) + MONTH(ade.adeAnoMesFim)) AS DATA_FIM" ;

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
        sql.append(" AND rse.rseCodigo IN (");
        sql.append("SELECT ");
        sql.append(" ade1.registroServidor.rseCodigo ");
        sql.append(" FROM RegistroServidor rse1");
        sql.append(" INNER JOIN rse1.servidor ser1");
        sql.append(" INNER JOIN rse1.autDescontoSet ade1");
        sql.append(" INNER JOIN ade1.verbaConvenio vco1");
        sql.append(" INNER JOIN vco1.convenio cnv1");
        sql.append(" LEFT OUTER JOIN rse1.cargoRegistroServidor crs1");

        sql.append(" WHERE ");
        sql.append(" (ade1.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_DEFERIDA).append("' OR ");
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
        sql.append(" ade1.registroServidor.rseCodigo");
        sql.append(" HAVING COUNT(DISTINCT cnv1.consignataria.csaCodigo) >= ").append(paramMaxCsaInt);
        sql.append(")");

        sql.append(" ORDER BY ").append(sqlOrder);

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

    private String mapOrderField(String key) {
        return switch (key) {
            case "ORD01" -> "rse.rseMatricula";
            case "ORD02" -> "ser.serNome";
            case "ORD03" -> "rse.rseTipo";
            case "ORD04" -> "crs.crsDescricao";
            case "ORD05" -> "csa.csaIdentificador";
            case "ORD06" -> "svc.svcDescricao";
            case "ORD07" -> "(ade.adePrazo * ade.adeVlr)";
            case "ORD08" -> "ade.adePrazo";
            case "ORD09" -> "ade.adeVlr";
            case "ORD10" -> "ade.adeData";
            case "ORD11" -> "(year(ade.adeAnoMesFim) + month(ade.adeAnoMesFim))";
            default -> null;
        };
    }
}
