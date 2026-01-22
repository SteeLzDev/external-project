package com.zetra.econsig.persistence.query.comunicacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaComunicacaoQuery</p>
 * <p>Description: Lista Comunicacao de acordo com filtros.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaComunicacaoQuery extends HQuery {

    public String cmnCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append(" cmn.cmnCodigo, ");
        corpoBuilder.append(" cmn.cmnNumero, ");
        corpoBuilder.append(" cmn.cmnData, ");
        corpoBuilder.append(" cmn.cmnTexto, ");
        corpoBuilder.append(" cmn.cmnPendencia, ");
        corpoBuilder.append(" cmn.comunicacaoPai.cmnCodigo, ");
        corpoBuilder.append(" cmn.cmnIpAcesso, ");
        corpoBuilder.append(" cmn.usuario.usuLogin, ");
        corpoBuilder.append(" cmn.usuario.usuNome, ");
        corpoBuilder.append(" cmn.usuario.usuCodigo, ");
        corpoBuilder.append(" consignante.cseCodigo, ");
        corpoBuilder.append(" orgao.orgCodigo, ");
        corpoBuilder.append(" consignataria.csaCodigo, ");
        corpoBuilder.append(" servidor.serCodigo, ");
        corpoBuilder.append(" registroServidor.rseCodigo, ");

        corpoBuilder.append(" CASE ");
        corpoBuilder.append(" WHEN consignante.cseCodigo IS NOT NULL AND cmnCse.cmeDestinatario = 'N' THEN '").append(CodedValues.PAP_CONSIGNANTE).append("'");
        corpoBuilder.append(" WHEN orgao.orgCodigo IS NOT NULL AND cmnOrg.cmoDestinatario = 'N' THEN '").append(CodedValues.PAP_ORGAO).append("'");
        corpoBuilder.append(" WHEN consignataria.csaCodigo IS NOT NULL AND cmnCsa.cmcDestinatario = 'N' THEN '").append(CodedValues.PAP_CONSIGNATARIA).append("'");
        corpoBuilder.append(" WHEN servidor.serCodigo IS NOT NULL AND cmnSer.cmsDestinatario = 'N' THEN '").append(CodedValues.PAP_SERVIDOR).append("'");
        corpoBuilder.append(" ELSE '' END AS papCodigoRemetente, ");

        corpoBuilder.append(" CASE ");
        corpoBuilder.append(" WHEN consignante.cseCodigo IS NOT NULL AND cmnCse.cmeDestinatario = 'S' THEN '").append(CodedValues.PAP_CONSIGNANTE).append("'");
        corpoBuilder.append(" WHEN orgao.orgCodigo IS NOT NULL AND cmnOrg.cmoDestinatario = 'S' THEN '").append(CodedValues.PAP_ORGAO).append("'");
        corpoBuilder.append(" WHEN consignataria.csaCodigo IS NOT NULL AND cmnCsa.cmcDestinatario = 'S' THEN '").append(CodedValues.PAP_CONSIGNATARIA).append("'");
        corpoBuilder.append(" WHEN servidor.serCodigo IS NOT NULL AND cmnSer.cmsDestinatario = 'S' THEN '").append(CodedValues.PAP_SERVIDOR).append("'");
        corpoBuilder.append(" ELSE '' END AS papCodigoDestinatario, ");
        corpoBuilder.append(" cmn.adeCodigo ");

        corpoBuilder.append(" FROM Comunicacao cmn");
        corpoBuilder.append(" INNER JOIN cmn.usuario usu");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCseSet cmnCse");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCse.consignante consignante");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoOrgSet cmnOrg");
        corpoBuilder.append(" LEFT OUTER JOIN cmnOrg.orgao orgao");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCsaSet cmnCsa");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCsa.consignataria consignataria");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoSerSet cmnSer");
        corpoBuilder.append(" LEFT OUTER JOIN cmnSer.servidor servidor");
        corpoBuilder.append(" LEFT OUTER JOIN cmnSer.registroServidor registroServidor");

        corpoBuilder.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(cmnCodigo)) {
            corpoBuilder.append(" AND cmn.cmnCodigo ").append(criaClausulaNomeada("cmnCodigo", cmnCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(cmnCodigo)) {
            defineValorClausulaNomeada("cmnCodigo", cmnCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CMN_CODIGO,
                Columns.CMN_NUMERO,
                Columns.CMN_DATA,
                Columns.CMN_TEXTO,
                Columns.CMN_PENDENCIA,
                Columns.CMN_CODIGO_PAI,
                Columns.CMN_IP_ACESSO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_CODIGO,
                Columns.CSE_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SER_CODIGO,
                Columns.RSE_CODIGO,
                "PAP_CODIGO_REMETENTE",
                "PAP_CODIGO_DESTINATARIO",
                Columns.CMN_ADE_CODIGO
        };
    }
}
