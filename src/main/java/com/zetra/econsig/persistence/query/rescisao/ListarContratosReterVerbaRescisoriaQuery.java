package com.zetra.econsig.persistence.query.rescisao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosReterVerbaRescisoriaQuery</p>
 * <p>Description: Listar os contratos de um colaborador que são elegíveis para retenção de verba rescisória</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Rodrigo Viana, Leonardo Angoti, Leonel Martins
 */
public class ListarContratosReterVerbaRescisoriaQuery extends HQuery  {

    public String rseCodigo;

    public ListarContratosReterVerbaRescisoriaQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Recupera parâmetro de validade em dias do saldo devedor para rescisão contratual
        String paramQtdeDiasValidadeSdv = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_EXPIRACAO_INF_SALDO_DEVEDOR_RESCISAO, AcessoSistema.getAcessoUsuarioSistema());
        if (TextHelper.isNull(paramQtdeDiasValidadeSdv)) {
            paramQtdeDiasValidadeSdv = "1";
        }
        // Recupera parâmetros de priorização de pagamento
        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean permitePriorizarVerba = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_VERBA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // lista de status de autorização que permitem solicitação de saldo
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo,");
        corpoBuilder.append(" ade.adeNumero, ");
        corpoBuilder.append(" ade.adeVlr, ");
        corpoBuilder.append(" ade.adeData, ");
        corpoBuilder.append(" ade.adeTipoVlr, ");
        corpoBuilder.append(" ade.correspondente.corCodigo, ");
        corpoBuilder.append(" rse.rseCodigo, ");
        corpoBuilder.append(" rse.rseMatricula, ");
        corpoBuilder.append(" ser.serNome, ");
        corpoBuilder.append(" ser.serEmail, ");
        corpoBuilder.append(" csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" vco.vcoCodigo, ");
        corpoBuilder.append(" cnv.cnvCodigo, ");
        corpoBuilder.append(" sdv.sdvValor ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("LEFT JOIN ade.saldoDevedorSet sdv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE svc.naturezaServico.nseRetemVerba = '").append(CodedValues.CAS_SIM).append("' ");
        corpoBuilder.append("AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append("AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigos, "','")).append("') ");

        // A ordenação da lista de contratos deve seguir os critérios de prioridade conforme parâmetros de sistema 100 e 130:
        corpoBuilder.append("ORDER BY ");
        if (permitePriorizarServico) {
            corpoBuilder.append("coalesce(to_numeric(svc.svcPrioridade), 9999999), ");
        }
        if (permitePriorizarVerba) {
            corpoBuilder.append("coalesce(to_numeric(cnv.cnvPrioridade), 9999999), ");
        }
        corpoBuilder.append("coalesce(ade.adeAnoMesIniRef, ade.adeAnoMesIni), ");
        corpoBuilder.append("coalesce(ade.adeDataRef, ade.adeData), ");
        corpoBuilder.append("ade.adeNumero, ");
        corpoBuilder.append("coalesce(to_numeric(sdv.sdvValor), 9999999) ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.ADE_TIPO_VLR,
                Columns.COR_CODIGO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_EMAIL,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.VCO_CODIGO,
                Columns.CNV_CODIGO,
                Columns.SDV_VALOR
        };
    }
}
