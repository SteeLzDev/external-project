package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParametrosConvenioQuery</p>
 * <p>Description: Lista os parâmetros de um determinado convênio.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParametrosConvenioQuery extends HNativeQuery {

    public String cnvCodigo;
    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;
    public boolean cnvAtivo;
    public boolean svcAtivo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(cnvCodigo) && (TextHelper.isNull(orgCodigo) || TextHelper.isNull(csaCodigo)) &&
            (TextHelper.isNull(orgCodigo) || TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo))) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

        String fields = Columns.CNV_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.CNV_COD_VERBA + MySqlDAOFactory.SEPARADOR
                        + Columns.CNV_IDENTIFICADOR + MySqlDAOFactory.SEPARADOR
                        + Columns.CNV_PRIORIDADE + MySqlDAOFactory.SEPARADOR
                        + Columns.CNV_ORG_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.CNV_CSA_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.SVC_IDENTIFICADOR + MySqlDAOFactory.SEPARADOR
                        + Columns.SVC_DESCRICAO + MySqlDAOFactory.SEPARADOR
                        + Columns.SVC_PRIORIDADE + MySqlDAOFactory.SEPARADOR
                        + Columns.SVC_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.CSA_IDENTIFICADOR + MySqlDAOFactory.SEPARADOR
                        + Columns.CSA_NOME;

        // Pega o nome dos campos sem o prefixo com o nome da tabela
        String campoPscVlr = Columns.getColumnName(Columns.PSC_VLR);
        String campoTpsCodigo = Columns.getColumnName(Columns.PSE_TPS_CODIGO);
        String campoSvcCodigo = Columns.getColumnName(Columns.PSC_SVC_CODIGO);
        String campoCsaCodigo = Columns.getColumnName(Columns.PSC_CSA_CODIGO);

        String nomes[] = {"CARENCIA_MINIMA", "CARENCIA_MAXIMA", "VLR_INDICE", "PERMITE_PRAZO_MAIOR_RSE_PRAZO", "IDENTIFICADOR_ADE_OBRIGATORIO"};
        String tpsCodigos[] = {CodedValues.TPS_CARENCIA_MINIMA, CodedValues.TPS_CARENCIA_MAXIMA, CodedValues.TPS_INDICE, CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA, CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO};
        String apelidos[] = {"PSCCMI", "PSCCMA", "INDICE", "PPMCS", "PIDAO"};

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ").append(fields);
        for (int i = 0; i < nomes.length; i++) {
            // Adiciona os parâmetros de convênio
            corpoBuilder.append(MySqlDAOFactory.SEPARADOR).append(apelidos[i]).append(".").append(campoPscVlr);
            corpoBuilder.append(" AS ").append(nomes[i]);
        }
        corpoBuilder.append(" FROM ").append(Columns.TB_CONVENIO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (");
        corpoBuilder.append(Columns.CSA_CODIGO).append(" = ").append(Columns.CNV_CSA_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON (");
        corpoBuilder.append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(")");

        for (int i = 0; i < nomes.length; i++) {
            corpoBuilder.append(" LEFT JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" ").append(apelidos[i]);
            corpoBuilder.append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(apelidos[i]).append(".").append(campoSvcCodigo);
            corpoBuilder.append(" AND ").append(Columns.CNV_CSA_CODIGO).append(" = ").append(apelidos[i]).append(".").append(campoCsaCodigo);
            corpoBuilder.append(" AND ").append(apelidos[i]).append(".").append(campoTpsCodigo).append(" = '").append(tpsCodigos[i]).append("')");
        }

        if (TextHelper.isNull(cnvCodigo)) {
            if (cnvAtivo) {
                corpoBuilder.append(" WHERE ").append(Columns.CNV_SCV_CODIGO).append(" = '").append(CodedValues.SCV_ATIVO).append("'");
            } else {
                corpoBuilder.append(" WHERE 1 = 1");
            }
            if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(criaClausulaNomeada("orgCodigo", orgCodigo));
            }
            if (!TextHelper.isNull(svcCodigo)) {
                corpoBuilder.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(criaClausulaNomeada("svcCodigo", svcCodigo));
            }
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND ").append(Columns.CNV_CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (svcAtivo) {
                corpoBuilder.append(" AND (").append(Columns.SVC_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("' OR ");
                corpoBuilder.append(Columns.SVC_ATIVO).append(" IS NULL) ");
            }
            corpoBuilder.append(" ORDER BY ").append(Columns.SVC_DESCRICAO);
        } else {
            corpoBuilder.append(" WHERE ").append(Columns.CNV_CODIGO).append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (TextHelper.isNull(cnvCodigo)) {
            if (!TextHelper.isNull(orgCodigo)) {
                defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
            }
            if (!TextHelper.isNull(svcCodigo)) {
                defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
            }
            if (!TextHelper.isNull(csaCodigo)) {
                defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
            }
        } else {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_IDENTIFICADOR,
                Columns.CNV_PRIORIDADE,
                Columns.CNV_ORG_CODIGO,
                Columns.CNV_CSA_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_PRIORIDADE,
                Columns.SVC_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                "CARENCIA_MINIMA",
                "CARENCIA_MAXIMA",
                "VLR_INDICE",
                "PERMITE_PRAZO_MAIOR_RSE_PRAZO",
                "IDENTIFICADOR_ADE_OBRIGATORIO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {}
}
