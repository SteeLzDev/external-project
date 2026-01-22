package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: listaTotalConsignacaoNaoEncerradasPorOrgao</p>
 * <p>Description: Listagem de Consignações não encerradas por orgão para exibição do dashboard mobile de CSA</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: Salt</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTotalConsignacaoAtivasPorOrgaoQuery extends HNativeQuery {

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT org.org_nome, count(ade.ade_codigo) AS total, sum(ade.ade_vlr) AS somatorio ");
        corpoBuilder.append(" FROM tb_aut_desconto ade ");
        corpoBuilder.append(" INNER JOIN tb_verba_convenio vco on vco.vco_codigo = ade.vco_codigo  ");
        corpoBuilder.append(" INNER JOIN tb_convenio cnv on cnv.cnv_codigo = vco.cnv_codigo ");
        corpoBuilder.append(" INNER JOIN tb_orgao org on org.org_codigo = cnv.org_codigo ");
        corpoBuilder.append(" WHERE ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_ATIVOS));
        corpoBuilder.append(" AND cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", responsavel.getCsaCodigo()));
        corpoBuilder.append(" GROUP BY org.org_nome ");
        corpoBuilder.append(" ORDER BY total desc, somatorio desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_ATIVOS, query);
        defineValorClausulaNomeada("csaCodigo", responsavel.getCsaCodigo(), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_NOME,
                "total",
                "somatorio"
         };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
