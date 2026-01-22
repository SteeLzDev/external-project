package com.zetra.econsig.persistence.query.anexo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAnexoComunicacaoQuery</p>
 * <p>Description: Query para listagem dos anexos de uma consignação.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ListaAnexoComunicacaoQuery extends HQuery {

    public String cmnCodigo;
    public String acmNome;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select "
                  + "cmn.cmnCodigo, "
                  + "acm.acmNome, "
                  + "usu.usuLogin, "
                  + "usuarioCsa.csaCodigo, "
                  + "usuarioCse.cseCodigo, "
                  + "usuarioCor.corCodigo, "
                  + "usuarioOrg.orgCodigo, "
                  + "usuarioSer.serCodigo, "
                  + "usuarioSup.cseCodigo, "
                  + "acm.acmAtivo, "
                  + "acm.acmData, "
                  + "tar.tarCodigo, "
                  + "acm.acmDescricao, "
                  + "cmn.cmnData "
                  ;
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from AnexoComunicacao acm ");
        corpoBuilder.append(" inner join acm.usuario usu ");
        corpoBuilder.append(" inner join acm.tipoArquivo tar ");
        corpoBuilder.append(" inner join acm.comunicacao cmn ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" left join cmn.comunicacaoPai cmnPai ");
        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(cmnCodigo)) {
            corpoBuilder.append(" and ( ");
            corpoBuilder.append(" cmn.cmnCodigo ").append(criaClausulaNomeada("cmnCodigo", cmnCodigo));
            corpoBuilder.append(" or cmnPai.cmnCodigo ").append(criaClausulaNomeada("cmnPaiCodigo", cmnCodigo));
            corpoBuilder.append(" ) ");

        }
        if (!TextHelper.isNull(acmNome)) {
            corpoBuilder.append(" and acm.acmNome ").append(criaClausulaNomeada("acmNome", acmNome));
        }
        if (!count) {
            corpoBuilder.append(" ORDER BY acm.acmData ASC, acm.acmNome ASC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cmnCodigo)) {
            defineValorClausulaNomeada("cmnCodigo", cmnCodigo, query);
            defineValorClausulaNomeada("cmnPaiCodigo", cmnCodigo, query);
        }
        if (acmNome != null) {
            defineValorClausulaNomeada("acmNome", acmNome, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ACM_CMN_CODIGO,
                Columns.ACM_NOME,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.ACM_ATIVO,
                Columns.ACM_DATA,
                Columns.ACM_TAR_CODIGO,
                Columns.ACM_DESCRICAO,
                Columns.CMN_DATA
        };
    }

}
