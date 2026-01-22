package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
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
 * <p>Title: ListarConsignacaoReimplanteManualQuery</p>
 * <p>Description: Lista contratos que podem ter parcelas para serem reimplantadas manualmente</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarConsignacaoReimplanteManualQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public List<Long> adeNumeros;
    public boolean count = false;
    public Date periodoAtual;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> spdCodigo = new ArrayList<>();
        spdCodigo.add(CodedValues.SPD_REJEITADAFOLHA);
        spdCodigo.add(CodedValues.SPD_SEM_RETORNO);

        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);

        String corpo ="";

        if(count) {
            corpo = "select count(*) ";
        } else {
            corpo = "select " +
                    "ade.adeCodigo, " +
                    "csa.csaIdentificador, " +
                    "csa.csaNomeAbrev, " +
                    "usu.usuLogin, " +
                    "ade.adeNumero, " +
                    "ade.adeIdentificador," +
                    "cnv.cnvCodVerba," +
                    "svc.svcDescricao," +
                    "ade.adeData," +
                    "ade.adeVlr, " +
                    "ade.adePrdPagas, " +
                    "ade.adePrazo," +
                    "ade.statusAutorizacaoDesconto.sadDescricao," +
                    "prd.prdNumero, " +
                    "case " +
                    "when padraoPreserva.psiVlr IS NOT NULL AND padraoPreserva.psiVlr='N' AND paramCsaPreserva.pscVlr IS NULL then 'true' " +
                    "when padraoPreserva.psiVlr IS NOT NULL AND padraoPreserva.psiVlr='N' AND paramCsaPreserva.pscVlr='N' then 'true' " +
                    "when padraoPreserva.psiVlr IS NOT NULL AND padraoPreserva.psiVlr='S' AND paramCsaPreserva.pscVlr='N' then 'true' " +
                    " else 'false' end as reimplanta_manual";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN est.consignante cse");
        corpoBuilder.append(" INNER JOIN ade.parcelaDescontoSet prd");
        corpoBuilder.append(" INNER JOIN ade.usuario usu");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" LEFT JOIN cse.paramSistConsignanteSet sistPreserva WITH sistPreserva.tipoParamSistConsignante.tpcCodigo = '").append(CodedValues.TPC_PRESERVA_PRD_REJEITADA).append("'");
        corpoBuilder.append(" LEFT JOIN cse.paramSistConsignanteSet csaEscolhePreserva WITH csaEscolhePreserva.tipoParamSistConsignante.tpcCodigo = '").append(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD).append("'");
        corpoBuilder.append(" LEFT JOIN cse.paramSistConsignanteSet padraoPreserva WITH padraoPreserva.tipoParamSistConsignante.tpcCodigo = '").append(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD).append("'");
        corpoBuilder.append(" LEFT JOIN svc.paramSvcConsignatariaSet paramCsaPreserva WITH paramCsaPreserva.consignataria.csaCodigo = csa.csaCodigo AND paramCsaPreserva.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL).append("'");
        corpoBuilder.append(" WHERE prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        corpoBuilder.append(" AND ade.adePrazo IS NOT NULL");
        corpoBuilder.append(" AND sistPreserva.psiVlr IS NOT NULL");
        corpoBuilder.append(" AND csaEscolhePreserva.psiVlr IS NOT NULL");
        corpoBuilder.append(" AND sistPreserva.psiVlr='S'");
        corpoBuilder.append(" AND csaEscolhePreserva.psiVlr='S'");

        if(!TextHelper.isNull(periodoAtual)) {
            corpoBuilder.append(" AND ade.adeAnoMesFim >= :periodoAtual");
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (adeNumeros != null && !adeNumeros.isEmpty()) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumeros", adeNumeros));
        }

        corpoBuilder.append(" ORDER BY ade.adeNumero ASC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("spdCodigo", spdCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!TextHelper.isNull(periodoAtual)) {
            defineValorClausulaNomeada("periodoAtual", periodoAtual, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (adeNumeros != null && !adeNumeros.isEmpty()) {
            defineValorClausulaNomeada("adeNumeros", adeNumeros, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME_ABREV,
                Columns.USU_LOGIN,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_PRAZO,
                Columns.SAD_DESCRICAO,
                Columns.PRD_NUMERO,
                "REIMPLANTA_MANUAL"
        };
    }
}
