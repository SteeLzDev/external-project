package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRelacionamentoSvcCorrecaoQuery</p>
 * <p>Description: Lista os serviços que têm correção a partir de um serviço dado</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRelacionamentoSvcCorrecaoQuery extends HQuery {
    public String svcCodigoOrigem;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select relServico.tipoNatureza.tntCodigo," + 
        "relServico.relSvcCodigo," +        
        "servico.svcCodigo," +
        "servico.svcIdentificador," +
        "servico.svcDescricao," +        
        "case when relServico.tipoNatureza.tntCodigo is null then '' " +
        "else 'SELECTED' end as SELECTED";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        
        corpoBuilder.append(" from Servico servico ");
        corpoBuilder.append(" inner join servico.paramSvcConsignanteSet paramSvcCse ");
        corpoBuilder.append(" left outer join servico.relacionamentoServicoByDestinoSet relServico ");
        corpoBuilder.append(" with (relServico.servicoBySvcCodigoOrigem.svcCodigo = :svcCodigoOrigem");
        corpoBuilder.append(" and relServico.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CORRECAO_SALDO).append("')");
        corpoBuilder.append(" WHERE 1=1 ");
        corpoBuilder.append(" and paramSvcCse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_CONTROLA_SALDO).append("'");
        corpoBuilder.append(" and paramSvcCse.pseVlr = '").append(CodedValues.POSSUI_CONTROLE_SALDO_DEVEDOR).append("'");
        corpoBuilder.append(" and servico.svcCodigo <> :svcCodigoOrigem");        
        
        corpoBuilder.append(" order by servico.svcDescricao");        
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSV_TNT_CODIGO,
                Columns.RSV_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,                
                Columns.SVC_DESCRICAO,
                "SELECTED"
        };
    }

}
