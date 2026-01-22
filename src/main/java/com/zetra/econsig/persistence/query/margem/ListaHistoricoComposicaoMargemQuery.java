package com.zetra.econsig.persistence.query.margem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoComposicaoMargem</p>
 * <p>Description: Consulta usada na Composição de Margem, acessada via Consulta de Margem ou Reserva de Margem.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoComposicaoMargemQuery extends HNativeQuery {

    public String strRseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> sadCodigos = new ArrayList<>();
        // Acrescenta todos os status presentes em CodedValues.SAD_CODIGOS_ATIVOS
        // de menos os SAD_AGUARD_CONF e SAD_AGUARD_DEFER com acréscimo dos
        // SAD_AGUARD_LIQUIDACAO e SAD_AGUARD_LIQUI_COMPRA
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        // Só serão contabilizados, caso não sejam fruto de uma renegociação
        List<String> sadCodigosAguardConf = new ArrayList<>();
        sadCodigosAguardConf.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigosAguardConf.add(CodedValues.SAD_AGUARD_DEFER);

        VinculoQuery queryVinculo = new VinculoQuery();
        queryVinculo.strRseCodigo = strRseCodigo;
        List<TransferObject> lista = queryVinculo.executarDTO();

        String vinculo = "NULL";
        Iterator<TransferObject> it = lista.iterator();
        if (it.hasNext()) {
            TransferObject vincTmpTO = it.next();
            String vincTmp = (String) vincTmpTO.getAttribute(Columns.CMA_VINCULO) ;
            if (vincTmp != null) {
                vinculo = "'" + vincTmp + "'";
            }
        }

        StringBuilder sql = new StringBuilder();
        // Itens de composição de margem
        sql.append("SELECT ");
        sql.append("1 AS TIPO, ");
        sql.append(Columns.CMA_VINCULO).append(" AS VINCULO, ");
        sql.append(Columns.VCT_IDENTIFICADOR).append(" AS CODIGO, ");
        sql.append(Columns.VCT_DESCRICAO).append(" AS DESCRICAO, ");
        sql.append("NULL AS VERBA, ");
        sql.append(Columns.CMA_QUANTIDADE).append(" AS QUANTIDADE, ");
        sql.append(" (CASE WHEN ").append(Columns.CMA_VLR).append(" >= 0 THEN ").append(Columns.CMA_VLR).append(" ELSE 0 END) AS VENCIMENTO, ");
        sql.append(" (CASE WHEN ").append(Columns.CMA_VLR).append(" < 0  THEN ").append(Columns.CMA_VLR).append(" ELSE 0 END) AS DESCONTO, ");
        sql.append(Columns.VRS_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.VRS_DESCRICAO).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.CRS_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.CRS_DESCRICAO).append(", ");
        sql.append(" '0' AS SVC_DESCRICAO");
        sql.append(" FROM ").append(Columns.TB_COMP_MARGEM);
        sql.append(" INNER JOIN ").append(Columns.TB_VENCIMENTO);
        sql.append(" ON (").append(Columns.VCT_CODIGO).append(" = ").append(Columns.CMA_VCT_CODIGO).append(")");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_VINCULO_REGISTRO_SERVIDOR);
        sql.append(" ON (").append(Columns.CMA_VRS_CODIGO).append(" = ").append(Columns.VRS_CODIGO).append(")");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CARGO_REGISTRO_SERVIDOR);
        sql.append(" ON (").append(Columns.CMA_CRS_CODIGO).append(" = ").append(Columns.CRS_CODIGO).append(")");
        sql.append(" WHERE (").append(Columns.CMA_RSE_CODIGO).append(criaClausulaNomeada("strRseCodigo", strRseCodigo)).append(")");

        sql.append(" UNION");

        // Contratos abertos
        sql.append(" SELECT ");
        sql.append("2 AS TIPO, ");
        sql.append(vinculo).append(" AS VINCULO, ");
        sql.append(Columns.CSA_IDENTIFICADOR).append(" AS CODIGO, ");
        sql.append("COALESCE(").append(Columns.CSA_NOME_ABREV).append(",");
        sql.append(Columns.CSA_NOME).append(") AS DESCRICAO, ");
        sql.append(Columns.CNV_COD_VERBA).append(" AS VERBA, ");
        sql.append("0 AS QUANTIDADE, ");
        sql.append("0 AS VENCIMENTO, ");
        sql.append(Columns.ADE_VLR).append(" AS DESCONTO, ");
        sql.append(Columns.VRS_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.VRS_DESCRICAO).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.CRS_IDENTIFICADOR).append(MySqlDAOFactory.SEPARADOR);
        sql.append(Columns.CRS_DESCRICAO).append(", ");
        sql.append(Columns.SVC_DESCRICAO).append(" AS SVC_DESCRICAO ");
        sql.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        sql.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO);
        sql.append(" ON (").append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_CONVENIO);
        sql.append(" ON (").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_SERVICO);
        sql.append(" ON (").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO);
        sql.append(" AND ").append(Columns.ADE_INC_MARGEM).append(" <> 0 ").append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
        sql.append(" ON (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.CNV_CSA_CODIGO).append(")");
        sql.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR);
        sql.append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.ADE_RSE_CODIGO).append(")");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_VINCULO_REGISTRO_SERVIDOR);
        sql.append(" ON (").append(Columns.RSE_VRS_CODIGO).append(" = ").append(Columns.VRS_CODIGO).append(")");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_CARGO_REGISTRO_SERVIDOR);
        sql.append(" ON (").append(Columns.RSE_CRS_CODIGO).append(" = ").append(Columns.CRS_CODIGO).append(")");
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO).append(" ON (");
        sql.append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO).append(" AND ");
        sql.append(Columns.RAD_TNT_CODIGO).append(" IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','");
        sql.append(CodedValues.TNT_CONTROLE_COMPRA).append("'))");
        sql.append(" WHERE (").append(Columns.ADE_RSE_CODIGO).append(criaClausulaNomeada("strRseCodigo", strRseCodigo)).append(")");
        // Todos os contratos que prendem margem, exceto os aguard. conf. provenientes de renegociação/compra
        sql.append(" AND ((").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(sadCodigos, "','")).append("'))");
        sql.append("   OR (").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(sadCodigosAguardConf, "','")).append("')");
        sql.append("   AND ").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" IS NULL))");

        sql.append(" ORDER BY VINCULO, TIPO, CODIGO, VERBA, SVC_DESCRICAO ");
        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(strRseCodigo)) {
            defineValorClausulaNomeada("strRseCodigo", strRseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TIPO",
                "VINCULO",
                "CODIGO",
                "DESCRICAO",
                "VERBA",
                "QUANTIDADE",
                "VENCIMENTO",
                "DESCONTO",
                Columns.VRS_IDENTIFICADOR,
                Columns.VRS_DESCRICAO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_DESCRICAO,
                "SVC_DESCRICAO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}

class VinculoQuery extends HNativeQuery {
    public String strRseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT ");
        sql.append(Columns.CMA_VINCULO);
        sql.append(" FROM ").append(Columns.TB_COMP_MARGEM);
        sql.append(" WHERE (").append(Columns.CMA_RSE_CODIGO).append(criaClausulaNomeada("strRseCodigo", strRseCodigo));
        sql.append(") ORDER BY ").append(Columns.CMA_VINCULO);

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(strRseCodigo)) {
            defineValorClausulaNomeada("strRseCodigo", strRseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {Columns.CMA_VINCULO};
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}