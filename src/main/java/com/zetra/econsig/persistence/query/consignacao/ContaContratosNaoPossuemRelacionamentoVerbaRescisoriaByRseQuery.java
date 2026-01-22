package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;

public class ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery extends HNativeQuery {

    public String rseCodigo;
    
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT COUNT(*) AS total ");
        corpo.append("FROM tb_aut_desconto ade ");
        corpo.append("INNER JOIN tb_verba_convenio vco on vco.vco_codigo = ade.vco_codigo ");
        corpo.append("INNER JOIN tb_convenio cnv on cnv.cnv_codigo = vco.cnv_codigo ");
        corpo.append("INNER JOIN tb_servico svc on svc.svc_codigo = cnv.svc_codigo ");
        corpo.append("WHERE ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "' , '")).append("') ");
        corpo.append("AND svc.nse_codigo IN ('").append(CodedValues.NSE_EMPRESTIMO).append("' , '").append(CodedValues.NSE_CARTAO).append("') ");
        corpo.append("AND ade.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpo.append(" AND NOT EXISTS ( SELECT 1 FROM tb_relacionamento_autorizacao rad ");
        corpo.append("  WHERE (ade.ade_codigo = rad.ade_codigo_origem OR ade.ade_codigo = rad.ade_codigo_destino) ");
        corpo.append("      AND rad.tnt_codigo = '").append(CodedValues.TNT_VERBA_RESCISORIA).append("')");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "total"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {        
    }
}
