package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConveniosIncMargemCartaoReservaLancamentoQuery</p>
 * <p>Description: Listamos os convenios que estão vinculado a cartao reserva ou lançamento
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConveniosIncMargemCartaoReservaLancamentoQuery extends HNativeQuery {

    public Short marCodigo;
    public boolean buscaCnvReservaCartao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cnv.cnv_codigo ");
        corpoBuilder.append("FROM tb_relacionamento_servico rel ");
        if (buscaCnvReservaCartao) {
            corpoBuilder.append("INNER JOIN tb_param_svc_consignante tps2 ON (tps2.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_origem = tps2.svc_codigo) ");
            corpoBuilder.append("INNER JOIN tb_convenio cnv ON (cnv.svc_codigo = tps2.svc_codigo) ");
        } else {
            corpoBuilder.append("INNER JOIN tb_param_svc_consignante tps ON (tps.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_destino = tps.svc_codigo) ");
            corpoBuilder.append("INNER JOIN tb_param_svc_consignante tps2 ON (tps2.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_origem = tps2.svc_codigo) ");
            corpoBuilder.append("INNER JOIN tb_convenio cnv ON (cnv.svc_codigo = tps.svc_codigo) ");
        }

        corpoBuilder.append(" WHERE rel.tnt_codigo = '" + CodedValues.TNT_CARTAO + "' ");
        corpoBuilder.append(" AND tps2.pse_vlr ").append(criaClausulaNomeada("marCodigo",marCodigo));

        if (!buscaCnvReservaCartao) {
            corpoBuilder.append(" AND tps.pse_vlr = '0'");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("marCodigo", marCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}