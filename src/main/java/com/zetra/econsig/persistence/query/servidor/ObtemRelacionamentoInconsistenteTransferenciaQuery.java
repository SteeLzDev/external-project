package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemRelacionamentoByRseCodigoQuery</p>
 * <p>Description: Retorna os relacionamentos de um determinado registro servidor sendo origem ou destino</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemRelacionamentoInconsistenteTransferenciaQuery extends HNativeQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select rad.ade_codigo_origem as origem_atual, ");
        corpoBuilder.append(" rad.ade_codigo_destino as destino_atual, ");
        corpoBuilder.append(" rad.tnt_codigo as tnt_codigo, ");
        corpoBuilder.append(" case when radOrigem24.ade_codigo_destino is not null then radOrigem24.ade_codigo_destino else null end as nova_origem, ");
        corpoBuilder.append(" case when radDestino24.ade_codigo_destino is not null then radDestino24.ade_codigo_destino else null end as novo_destino ");
        corpoBuilder.append(" from tb_relacionamento_autorizacao rad ");
        corpoBuilder.append(" inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
        corpoBuilder.append(" inner join tb_aut_desconto adeDestino on (rad.ade_codigo_destino = adeDestino.ade_codigo) ");
        corpoBuilder.append(" left outer join tb_relacionamento_autorizacao radOrigem24 on (radOrigem24.ade_codigo_origem = rad.ade_codigo_origem and radOrigem24.tnt_codigo = '").append(CodedValues.TNT_TRANSFERENCIA_CONTRATO).append("') ");
        corpoBuilder.append(" left outer join tb_relacionamento_autorizacao radDestino24 on (radDestino24.ade_codigo_origem = rad.ade_codigo_destino and radDestino24.tnt_codigo = '").append(CodedValues.TNT_TRANSFERENCIA_CONTRATO).append("') ");
        corpoBuilder.append(" where rad.tnt_codigo <> '").append(CodedValues.TNT_TRANSFERENCIA_CONTRATO).append("' ");
        corpoBuilder.append(" and adeOrigem.rse_codigo <> adeDestino.rse_codigo ");
        corpoBuilder.append(" and (radOrigem24.ade_codigo_destino is not null or radDestino24.ade_codigo_destino is not null) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "origem_atual",
                "destino_atual",
                "tnt_codigo",
                "nova_origem",
                "novo_destino"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
