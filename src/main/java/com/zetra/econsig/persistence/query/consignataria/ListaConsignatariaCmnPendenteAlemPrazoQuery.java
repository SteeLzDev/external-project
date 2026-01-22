package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaCmnPendenteAlemPrazoQuery</p>
 * <p>Description: Lista consignatárias com comunicações pendentes além do prazo em dias corridos,
 * definido pelo parâmetro de sistema 314, para bloqueio das consignatárias.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaCmnPendenteAlemPrazoQuery extends HNativeQuery {

    public int diasParaBloqueioCmnSer;
    public int diasParaBloqueioCmnCseOrg;
    public String csaCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CMN_PENDENTE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final String campos = Columns.CSA_CODIGO + "," +
                        Columns.CSA_NOME_ABREV + "," +
                        Columns.CSA_NOME
                        ;

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ").append(campos);

        corpoBuilder.append(", to_numeric(SUM(CASE WHEN ").append(Columns.CMS_SER_CODIGO).append(" IS NOT NULL THEN 1 ELSE 0 END)) AS QTD_PENDENCIA_SER");
        corpoBuilder.append(", to_numeric(SUM(CASE WHEN ").append(Columns.CME_CSE_CODIGO).append(" IS NOT NULL THEN 1 ELSE 0 END)) AS QTD_PENDENCIA_CSE");
        corpoBuilder.append(", to_numeric(SUM(CASE WHEN ").append(Columns.CMO_ORG_CODIGO).append(" IS NOT NULL THEN 1 ELSE 0 END)) AS QTD_PENDENCIA_ORG");

        corpoBuilder.append(" FROM ").append(Columns.TB_CONSIGNATARIA);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_COMUNICACAO_CSA).append(" ON (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.CMC_CSA_CODIGO).append(" AND ").append(Columns.CMC_DESTINATARIO).append(" = 'S')");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_COMUNICACAO).append(" pai ON (").append(Columns.CMC_CMN_CODIGO).append(" = pai.cmn_codigo)");

        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_COMUNICACAO_SER).append(" ON (").append(Columns.CMS_CMN_CODIGO).append(" = ").append(Columns.CMC_CMN_CODIGO).append(" AND ").append(Columns.CMS_DESTINATARIO).append(" = 'N')");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_COMUNICACAO_CSE).append(" ON (").append(Columns.CME_CMN_CODIGO).append(" = ").append(Columns.CMC_CMN_CODIGO).append(" AND ").append(Columns.CME_DESTINATARIO).append(" = 'N')");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_COMUNICACAO_ORG).append(" ON (").append(Columns.CMO_CMN_CODIGO).append(" = ").append(Columns.CMC_CMN_CODIGO).append(" AND ").append(Columns.CMO_DESTINATARIO).append(" = 'N')");

        corpoBuilder.append(" WHERE pai.cmn_pendencia = ").append(Boolean.TRUE);

        corpoBuilder.append(" AND (").append(Columns.CMS_SER_CODIGO).append(" IS NOT NULL ");
        corpoBuilder.append("   OR ").append(Columns.CME_CSE_CODIGO).append(" IS NOT NULL ");
        corpoBuilder.append("   OR ").append(Columns.CMO_ORG_CODIGO).append(" IS NOT NULL ");
        corpoBuilder.append(")");

        // ATENÇÃO: É A MESMA CLÁUSULA DA ListarComunicacoesQuery QUANDO  listarBloqueioCsa = TRUE
        // Comunicações originais geradas pelo usuário que ainda estão pendentes sem nenhuma resposta com prazo maior
        // que a data limite de bloqueio da CSA, ou aqueles que tem resposta pendente do usuário que criou a comunicação
        // original com prazo maior que a data limite de bloqueio da CSA
        if (!usaDiasUteis) {
            corpoBuilder.append(" AND to_days(data_corrente()) - to_days(COALESCE((");
            corpoBuilder.append(" SELECT MAX(cmnFilho.cmn_data) FROM ").append(Columns.TB_COMUNICACAO).append(" cmnFilho ");
            corpoBuilder.append(" WHERE cmnFilho.cmn_codigo_pai = pai.cmn_codigo and cmnFilho.usu_codigo = pai.usu_codigo ");
            corpoBuilder.append("), pai.cmn_data)) >= ");
        } else {
            // se usa dias úteis, conta quantos tem entre a data da última comunicação pendente do usuário e a data corrente
            corpoBuilder.append(" AND (SELECT COUNT(*) FROM ").append(Columns.TB_CALENDARIO);
            corpoBuilder.append(" WHERE ").append(Columns.CAL_DIA_UTIL).append(" = '").append(CodedValues.TPC_SIM).append("'");
            corpoBuilder.append(" AND ").append(Columns.CAL_DATA).append(" BETWEEN to_date(COALESCE((");
            corpoBuilder.append(" SELECT MAX(cmnFilho.cmn_data) FROM ").append(Columns.TB_COMUNICACAO).append(" cmnFilho ");
            corpoBuilder.append(" WHERE cmnFilho.cmn_codigo_pai = pai.cmn_codigo and cmnFilho.usu_codigo = pai.usu_codigo ");
            corpoBuilder.append("), pai.cmn_data)) and data_corrente()) > ");
        }

        // Dias para bloqueio depende de qual parâmetro está habilitado
        corpoBuilder.append(" (CASE");
        corpoBuilder.append(" WHEN ").append(Columns.CMS_SER_CODIGO).append(" IS NOT NULL THEN ").append(diasParaBloqueioCmnSer);
        corpoBuilder.append(" WHEN ").append(Columns.CME_CSE_CODIGO).append(" IS NOT NULL THEN ").append(diasParaBloqueioCmnCseOrg);
        corpoBuilder.append(" WHEN ").append(Columns.CMO_ORG_CODIGO).append(" IS NOT NULL THEN ").append(diasParaBloqueioCmnCseOrg);
        corpoBuilder.append(" ELSE 99999 END)");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else {
            corpoBuilder.append(" AND ").append(Columns.CSA_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");
        }

        corpoBuilder.append(" GROUP BY ").append(campos);

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                "QTD_PENDENCIA_SER",
                "QTD_PENDENCIA_CSE",
                "QTD_PENDENCIA_ORG",
        };
    }
}
