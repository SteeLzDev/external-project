package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoSemParcelaQuery</p>
 * <p>Description: Listagem de Consignações que não possuam parcelas geradas</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoConciliacaoQuery extends HQuery {

    public String orgIdentificador;
    public Date periodo;
    public List<String> cpf;
    public List<Long> adeNumero;
    public List<String> adeIdentificador;
    public String statusPagamento;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("SELECT  ");
        corpoBuilder.append("ser.serNome, ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("ade.adeNumero, ");
        corpoBuilder.append("ade.adeIdentificador, ");
        corpoBuilder.append("ade.adeData, ");
        corpoBuilder.append("ade.adeAnoMesIni, ");
        corpoBuilder.append("ade.adeAnoMesFim, ");
        corpoBuilder.append("ade.adePrazo, ");
        corpoBuilder.append("ade.adePrdPagas, ");
        corpoBuilder.append("prd.prdVlrPrevisto, ");
        corpoBuilder.append("prd.prdVlrRealizado, ");
        corpoBuilder.append("ocp.ocpObs ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.parcelaDescontoSet prd ");
        corpoBuilder.append("LEFT JOIN prd.ocorrenciaParcelaSet ocp ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("WHERE 1 = 1  ");
        
        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" AND org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }
        
        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" AND prd.prdDataDesconto ").append(criaClausulaNomeada("prdDataDesconto", periodo));
        }
        
        if (cpf != null && !cpf.isEmpty()) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", cpf));
        }
        
        if (adeNumero != null && !adeNumero.isEmpty()) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        
        if (adeIdentificador != null && !adeIdentificador.isEmpty()) {
            corpoBuilder.append(" AND ade.adeIdentificador ").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));
        }
        
        if (!TextHelper.isNull(statusPagamento) && statusPagamento.equals(CodedValues.TODAS_PARCELAS_PAGAS)) {
            corpoBuilder.append(" AND prd.prdVlrRealizado <> 0 ");
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo IN('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
        }
        
        if (!TextHelper.isNull(statusPagamento) && statusPagamento.equals(CodedValues.TODAS_PARCELAS_PAGAS_INTEGRALMENTE)) {
            corpoBuilder.append(" AND prd.prdVlrRealizado <> 0 ");
            corpoBuilder.append(" AND prd.prdVlrPrevisto = prd.prdVlrRealizado ");
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo IN('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
        }
        
        if (!TextHelper.isNull(statusPagamento) && statusPagamento.equals(CodedValues.TODAS_PARCELAS_PAGAS_PARCIALMENTE)) {
            corpoBuilder.append(" AND prd.prdVlrRealizado <> 0 ");
            corpoBuilder.append(" AND prd.prdVlrPrevisto <> prd.prdVlrRealizado ");
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo IN('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
        }
        
        if (!TextHelper.isNull(statusPagamento) && statusPagamento.equals(CodedValues.TODAS_PARCELAS_REJEITADAS)) {
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo IN('").append(CodedValues.SPD_REJEITADAFOLHA).append("')");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }
        
        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("prdDataDesconto", periodo, query);
        }
        
        if (cpf != null && !cpf.isEmpty()) {
            defineValorClausulaNomeada("serCpf", cpf, query);
        }
        
        if (adeNumero != null && !adeNumero.isEmpty()) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        
        if (adeIdentificador != null && !adeIdentificador.isEmpty()) {
            defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.OCP_OBS    
        };
    }
}
