package com.zetra.econsig.persistence.query.convenio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCnvsAtualizarVerbaByCnvQuery</p>
 * <p>Description: Listagem dos convênios que não possuem verba cadastrada
 * e que devem ser atualizados com um novo código de verba baseado em outros convênios cadastrados
 * e que possui contrato(s) ativo(s).</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCnvsAtualizarVerbaByCnvQuery extends HNativeQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigosAtivos = new ArrayList<String>();
        sadCodigosAtivos.add(CodedValues.NOT_EQUAL_KEY);
        sadCodigosAtivos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ");
        corpoBuilder.append("cnv1.cnv_codigo as cnv_codigo, cnv2.cnv_cod_verba as cnv_cod_verba ");
        corpoBuilder.append("from tb_convenio cnv1 ");
        corpoBuilder.append("inner join tb_convenio cnv2 on (cnv1.svc_codigo = cnv2.svc_codigo and cnv1.csa_codigo = cnv2.csa_codigo) ");
        corpoBuilder.append("inner join tb_verba_convenio vco2 on (vco2.cnv_codigo = cnv2.cnv_codigo) ");
        corpoBuilder.append("inner join tb_aut_desconto ade2 on (vco2.vco_codigo = ade2.vco_codigo) ");
        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append(" and cnv1.cnv_cod_verba ").append(criaClausulaNomeada("", CodedValues.IS_NULL_KEY));
        corpoBuilder.append(" and cnv2.cnv_cod_verba ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
        corpoBuilder.append(" and ade2.sad_codigo ").append(criaClausulaNomeada("sadCodigosAtivos", sadCodigosAtivos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigosAtivos", sadCodigosAtivos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
