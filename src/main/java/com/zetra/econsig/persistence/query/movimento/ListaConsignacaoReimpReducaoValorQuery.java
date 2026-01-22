package com.zetra.econsig.persistence.query.movimento;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoReimpReducaoValorQuery</p>
 * <p>Description: Lista os contratos que possuem ocorrência de relançamento (107) com redução de parcela no período anterior,
 * que não possuem ocorrência de relançamento (10 ou 107) para este período, de status Deferido (4), Em andamento (5), Estoque (12, 16),
 * e que foi pago com valor distinto ao valor do contrato no período anterior.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoReimpReducaoValorQuery extends HNativeQuery {

    public List<String> estCodigos;
    public List<String> orgCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigosPermiteReimplante = new ArrayList<>();
        sadCodigosPermiteReimplante.add(CodedValues.SAD_DEFERIDA);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_ESTOQUE);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_ESTOQUE_MENSAL);

        List<String> tocCodigosNaoPermiteReimplante = new ArrayList<>();
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_RELANCAMENTO);
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR);
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_ALTERACAO_CONTRATO);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ade.ade_codigo, pex.pex_periodo, pex.pex_data_fim ");
        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
        corpoBuilder.append("inner join tb_historico_exportacao hie on (org.org_codigo = hie.org_codigo and pex.pex_periodo_ant = hie.hie_periodo) ");

        // Contratos que permitem reimplante e que foram pagos pela folha com valor distinto ao valor da parcela
        corpoBuilder.append("where ade.sad_codigo in ('").append(TextHelper.join(sadCodigosPermiteReimplante, "','")).append("') ");
        corpoBuilder.append("and coalesce(ade.ade_paga, 'N') = 'S' ");
        corpoBuilder.append("and ade.ade_vlr_folha <> ade.ade_vlr ");

        // Garante que utilize o último histórico de exportação, já que podem haver vários para o mesmo período
        corpoBuilder.append("and not exists ( ");
        corpoBuilder.append("  select 1 from tb_historico_exportacao hia ");
        corpoBuilder.append("  where hia.org_codigo = hie.org_codigo ");
        corpoBuilder.append("    and hia.hie_periodo = hie.hie_periodo ");
        corpoBuilder.append("    and hia.hie_data > hie.hie_data ");
        corpoBuilder.append(") ");

        // Contratos que possuem ocorrência de relançamento (107) com redução de parcela no período anterior
        corpoBuilder.append("and exists ( ");
        corpoBuilder.append("  select 1 from tb_ocorrencia_autorizacao oca");
        corpoBuilder.append("  where oca.ade_codigo = ade.ade_codigo");
        corpoBuilder.append("    and oca.toc_codigo = '").append(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR).append("'");
        corpoBuilder.append("    and oca.oca_data between hie.hie_data_ini and add_day(hie.hie_data_fim, 1)");
        corpoBuilder.append(") ");

        // Contratos que não possuem ocorrência de relançamento (10 ou 107), ou de alteração (14) para este período
        corpoBuilder.append("and not exists ( ");
        corpoBuilder.append("  select 1 from tb_ocorrencia_autorizacao oca ");
        corpoBuilder.append("  where oca.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("    and oca.toc_codigo in ('").append(TextHelper.join(tocCodigosNaoPermiteReimplante, "','")).append("') ");
        corpoBuilder.append("    and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim ");
        corpoBuilder.append(") ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.est_codigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_FIM
         };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigos = (List<String>) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
    }
}
