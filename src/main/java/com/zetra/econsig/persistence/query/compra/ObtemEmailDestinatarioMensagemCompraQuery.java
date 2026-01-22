package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemEmailDestinatarioMensagemCompraQuery</p>
 * <p>Description: Retorna o e-mail destinatário para envio de mensagem pelo módulo de portabilidade</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class ObtemEmailDestinatarioMensagemCompraQuery extends HNativeQuery {

    public String adeCodigoOrigem;
    public String csaCodigoRemetente;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select case when csaOrigem.csa_codigo ").append(criaClausulaNomeada("csaCodigoRemetente", csaCodigoRemetente)).append(" then ");
        corpoBuilder.append("coalesce(CASE WHEN NULLIF(TRIM(usuDestino.usu_email), '') IS NOT NULL THEN usuDestino.usu_email ELSE NULL END, ");
        corpoBuilder.append("CASE WHEN NULLIF(TRIM(corDestino.cor_email), '') IS NOT NULL THEN corDestino.cor_email ELSE NULL END, ");
        corpoBuilder.append("CASE WHEN NULLIF(TRIM(csaDestino.csa_email), '') IS NOT NULL THEN csaDestino.csa_email ELSE NULL END) ");
        corpoBuilder.append("else ");
        corpoBuilder.append("coalesce(CASE WHEN NULLIF(TRIM(usuOrigem.usu_email), '') IS NOT NULL THEN usuOrigem.usu_email ELSE NULL END, ");
        corpoBuilder.append("CASE WHEN NULLIF(TRIM(corOrigem.cor_email), '') IS NOT NULL THEN corOrigem.cor_email ELSE NULL END, ");
        corpoBuilder.append("CASE WHEN NULLIF(TRIM(csaOrigem.csa_email), '') IS NOT NULL THEN csaOrigem.csa_email ELSE NULL END) ");
        corpoBuilder.append("end as email_destinatario ");
        corpoBuilder.append("from tb_relacionamento_autorizacao rad ");
        corpoBuilder.append("inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
        corpoBuilder.append("inner join tb_aut_desconto adeDestino on (rad.ade_codigo_destino = adeDestino.ade_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csaOrigem on (rad.csa_codigo_origem = csaOrigem.csa_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csaDestino on (rad.csa_codigo_destino = csaDestino.csa_codigo) ");
        corpoBuilder.append("inner join tb_usuario usuOrigem on (usuOrigem.usu_codigo = adeOrigem.usu_codigo) ");
        corpoBuilder.append("inner join tb_usuario usuDestino on (usuDestino.usu_codigo = adeDestino.usu_codigo) ");
        corpoBuilder.append("left join tb_correspondente corOrigem on (corOrigem.cor_codigo = adeOrigem.cor_codigo) ");
        corpoBuilder.append("left join tb_correspondente corDestino on (corDestino.cor_codigo = adeDestino.cor_codigo) ");
        corpoBuilder.append("where rad.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("and rad.ade_codigo_origem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        defineValorClausulaNomeada("csaCodigoRemetente", csaCodigoRemetente, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_EMAIL
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}