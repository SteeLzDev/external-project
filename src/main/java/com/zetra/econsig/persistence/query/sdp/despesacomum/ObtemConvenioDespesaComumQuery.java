package com.zetra.econsig.persistence.query.sdp.despesacomum;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConvenioDespesaComumQuery</p>
 * <p>Description: Retorna o convênio para o qual a despesa comum foi lançada.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConvenioDespesaComumQuery extends HQuery {

    public String decCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(decCodigo)) {
            throw new HQueryException("mensagem.erro.informe.despesa.comum.codigo", (AcessoSistema) null);
        }

        String corpo = "";

        corpo =  " SELECT org.orgCodigo, ";
        corpo += " svc.svcCodigo, ";
        corpo += " cnv.cnvCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM DespesaIndividual dsi ");
        corpoBuilder.append(" INNER JOIN dsi.despesaComum dec ");
        corpoBuilder.append(" INNER JOIN dsi.autDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");

        corpoBuilder.append(" WHERE dec.decCodigo ").append(criaClausulaNomeada("decCodigo", decCodigo));

        corpoBuilder.append(" GROUP BY org.orgCodigo, svc.svcCodigo, cnv.cnvCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("decCodigo", decCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.SVC_CODIGO,
                Columns.CNV_CODIGO
        };
    }

}
