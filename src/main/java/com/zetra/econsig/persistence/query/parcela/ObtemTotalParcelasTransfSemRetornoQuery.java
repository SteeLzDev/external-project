package com.zetra.econsig.persistence.query.parcela;

import java.text.ParseException;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalParcelasTransfSemRetornoQuery</p>
 * <p>Description: Obtém o total de parcelas ligadas a contratos que foram transferidos
 * para servidores ligados a órgãos que já tiverem o processamento do retorno do período
 * da parcela, de forma que estas ficarão sem retorno.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasTransfSemRetornoQuery extends HQuery {

    public List<String> adeCodigos;
    public boolean agrupaPorOrgao = false;
    public String periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        if (agrupaPorOrgao) {
            corpoBuilder.append("select rse.orgao.orgCodigo, count(*) ");
        } else {
            corpoBuilder.append("select count(*) ");
        }
        corpoBuilder.append("from ParcelaDescontoPeriodo prd ");
        corpoBuilder.append("inner join prd.autDesconto ade ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join rse.orgao.historicoConclusaoRetornoSet hcr ");

        // Histórico de conclusão para o período base das parcelas,
        // que não foram desfeitos e com data de finalização, ou seja
        // retorno iniciado e finalizado, e não desfeito.
        corpoBuilder.append("where hcr.hcrPeriodo = prd.prdDataDesconto ");
        corpoBuilder.append("  and hcr.hcrDesfeito = 'N' ");
        corpoBuilder.append("  and hcr.hcrDataFim is not null ");

        if (adeCodigos != null && adeCodigos.size() > 0) {
            corpoBuilder.append(" and ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));
        }

        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" and prd.prdDataDesconto = to_date(:periodo) ");
        }

        if (agrupaPorOrgao) {
            corpoBuilder.append(" group by rse.orgao.orgCodigo");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }

        if (!TextHelper.isNull(periodo)) {
            try {
                defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodo);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (agrupaPorOrgao) {
            return new String[] {
                    "ORGAO",
                    "QTD"
            };
        } else {
            return new String[] {
                    "QTD"
            };
        }
    }
}
