package com.zetra.econsig.persistence.query.saldodevedor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoSaldoDevedorSemRespostaQuery</p>
 * <p>Description: Lista contratos que nao atenderam a solicitacao do saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoSaldoDevedorSemRespostaQuery extends HNativeQuery {

    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        // Parâmetro para verificar se tem saldo devedor para exclusão de servidor
        boolean temModuloSaldoDevedorExclusao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        
        int qtdeDiasBloqCsaSolicSaldoDevedor = ParamSist.getIntParamSist(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_SOLIC_SALDO_DEVEDOR_NAO_ATENDIDA, 0, responsavel);

        String campos = Columns.CSA_CODIGO + " ," +
                        Columns.CSA_NOME_ABREV + " ," +
                        Columns.CSA_NOME + " ," +
                        Columns.ADE_CODIGO + " ," +
                        Columns.ADE_NUMERO + " ," +
                        Columns.ADE_VLR + " ," +
                        Columns.ADE_IDENTIFICADOR + " ," +
                        Columns.ADE_INDICE + " ," +
                        Columns.ADE_PRAZO + " ," +
                        Columns.ADE_PRD_PAGAS + " ," +
                        Columns.CNV_COD_VERBA + " ," +
                        Columns.SVC_IDENTIFICADOR + " ," +
                        Columns.SVC_DESCRICAO + " ," +
                        Columns.RSE_MATRICULA + " ," +
                        Columns.SER_NOME + " ," +
                        Columns.SER_CPF + " ," +
                        Columns.COR_EMAIL + "," +
                        "CASE WHEN NULLIF(TRIM(psc140.PSC_VLR), '') IS NULL THEN " + Columns.CSA_EMAIL +
                        "     ELSE psc140.PSC_VLR END AS EMAIL_AVISO_CSA, " +
                        "psc168.PSC_VLR AS DESTINATARIOS_EMAILS";

        String campoVvr = "";
        if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && qtdeDiasBloqCsaSolicSaldoDevedor > 0) {
            campoVvr = ", " + Columns.VRR_CODIGO;        
        }
        campos += campoVvr;
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT DISTINCT ");
        corpoBuilder.append(campos);
        corpoBuilder.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.ADE_RSE_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_SERVIDOR).append(" ON (").append(Columns.SER_CODIGO).append(" = ").append(Columns.RSE_SER_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (").append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.CNV_CSA_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON (").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" pse126 ON (pse126.SVC_CODIGO = ").append(Columns.SVC_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_SOLICITACAO_AUTORIZACAO).append(" ON (").append(Columns.ADE_CODIGO).append(" = ").append(Columns.SOA_ADE_CODIGO).append(")");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" pse161 ON (pse161.SVC_CODIGO = ").append(Columns.SVC_CODIGO).append(" AND pse161.TPS_CODIGO = '").append(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR).append("')");
        if (temModuloSaldoDevedorExclusao) {
            corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_VERBA_RESCISORIA_RSE).append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.VRR_RSE_CODIGO).append(")");
            corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" pse223 ON (pse223.SVC_CODIGO = ").append(Columns.SVC_CODIGO).append(" AND pse223.TPS_CODIGO = '").append(CodedValues.TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO).append("')");
        }
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_CORRESPONDENTE).append(" ON (").append(Columns.COR_CODIGO).append(" = ").append(Columns.ADE_COR_CODIGO).append(")");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" psc140 ON (psc140.SVC_CODIGO = ").append(Columns.SVC_CODIGO).append(" AND psc140.CSA_CODIGO = ").append(Columns.CSA_CODIGO).append(" AND psc140.TPS_CODIGO = '").append(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR).append("')");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" psc168 ON (psc168.SVC_CODIGO = ").append(Columns.SVC_CODIGO).append(" AND psc168.CSA_CODIGO = ").append(Columns.CSA_CODIGO).append(" AND psc168.TPS_CODIGO = '").append(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA).append("')");
        corpoBuilder.append(" WHERE 1=1 ");

        // Serviço permite cadastro de saldo devedor
        corpoBuilder.append(" AND pse126.TPS_CODIGO = '").append(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR).append("'");
        corpoBuilder.append(" AND pse126.PSE_VLR in ('").append(CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR).append("','").append(CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR).append("')");

        // Consignacao ainda aberta
        corpoBuilder.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("')");

        // Solicitação de saldo ainda pendente
        corpoBuilder.append(" AND ").append(Columns.SOA_TIS_CODIGO).append(" IN ('").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO).append("','").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR).append("','").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("')");
        corpoBuilder.append(" AND ").append(Columns.SOA_SSO_CODIGO).append(" = '").append(StatusSolicitacaoEnum.PENDENTE).append("'");

        // Verifica o tipo de solicitação para saber se pega valores do parâmetro 161 ou do 223
        corpoBuilder.append(" AND (");
        // parâmetro 161 - saldo devedor solicitado pelo servidor
        corpoBuilder.append(" (").append(Columns.SOA_TIS_CODIGO).append(" <> '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("'");
        corpoBuilder.append(" AND NULLIF(TRIM(pse161.PSE_VLR), '') IS NOT NULL");
        // Diferenca de dias ultrapassou o prazo maximo cadastrado
        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" AND (SELECT COUNT(*) FROM ").append(Columns.TB_CALENDARIO).append(" WHERE ").append(Columns.CAL_DIA_UTIL).append(" = 'S' AND ").append(Columns.CAL_DATA).append(" BETWEEN to_date(").append(Columns.SOA_DATA).append(") AND data_corrente()) > ");
        } else {
            corpoBuilder.append(" AND (to_days(data_corrente()) - to_days(").append(Columns.SOA_DATA).append(")) >= ");
        }
        corpoBuilder.append(" (CASE isnumeric(pse161.PSE_VLR) WHEN 1 THEN to_numeric(COALESCE(NULLIF(TRIM(pse161.PSE_VLR), ''), '0')) ELSE 99999 END) ");
        corpoBuilder.append(" )");
        // verifica também o parâmetro 223 - saldo devedor solicitado para o gestor para exclusão de servidor
        if (temModuloSaldoDevedorExclusao) {
            // parâmetro 223 - saldo devedor solicitado para o gestor para exclusão de servidor
            corpoBuilder.append(" OR ");
            corpoBuilder.append(" (").append(Columns.SOA_TIS_CODIGO).append(" = '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("'");
            corpoBuilder.append(" AND NULLIF(TRIM(pse223.PSE_VLR), '') IS NOT NULL");
            String soaData = "";
            //verifica também o parâmetro 966 - quantidade de dias para bloqueio de consignatária com solicitação de saldo devedor de rescisão
            if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && qtdeDiasBloqCsaSolicSaldoDevedor > 0) {
                soaData = "add_day(" + Columns.SOA_DATA + "," + qtdeDiasBloqCsaSolicSaldoDevedor + ")";
            } else {
                soaData = Columns.SOA_DATA;
            }
            // Diferenca de dias ultrapassou o prazo maximo cadastrado
            if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM ").append(Columns.TB_CALENDARIO).append(" WHERE ").append(Columns.CAL_DIA_UTIL).append(" = 'S' AND ").append(Columns.CAL_DATA).append(" BETWEEN to_date(").append(soaData).append(") AND data_corrente()) > ");
            } else {
                corpoBuilder.append(" AND (to_days(data_corrente()) - to_days(").append(soaData).append(")) >= ");
            }
            corpoBuilder.append(" (CASE isnumeric(pse223.PSE_VLR) WHEN 1 THEN to_numeric(COALESCE(NULLIF(TRIM(pse223.PSE_VLR), ''), '0')) ELSE 99999 END) ");
            
            if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && qtdeDiasBloqCsaSolicSaldoDevedor > 0) {
                corpoBuilder.append(" AND (");
                corpoBuilder.append(" EXISTS (");
                corpoBuilder.append("  select 1 from ").append(Columns.TB_VERBA_RESCISORIA_RSE).append(" vrr ");
                corpoBuilder.append("      where vrr.rse_codigo = ").append(Columns.RSE_CODIGO).append("))");
            }
            corpoBuilder.append(" )");
        }
        corpoBuilder.append(" )");

        // Define filtro pela consignatária, ou de todas ativas
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else {
            corpoBuilder.append(" AND ").append(Columns.CSA_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");
        }

        corpoBuilder.append(" ORDER BY ").append(Columns.CSA_CODIGO).append(",");
        if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && qtdeDiasBloqCsaSolicSaldoDevedor > 0) {
            corpoBuilder.append(Columns.VRR_CODIGO).append(",");
        }
        corpoBuilder.append(Columns.ADE_CODIGO);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        List<String> fields = new ArrayList<String>();
        fields.addAll(Arrays.asList(
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_INDICE,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.CNV_COD_VERBA,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.COR_EMAIL,
                "EMAIL_AVISO_CSA",
                "DESTINATARIOS_EMAILS"
        ));
        int qtdeDiasBloqCsaSolicSaldoDevedor = ParamSist.getIntParamSist(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_SOLIC_SALDO_DEVEDOR_NAO_ATENDIDA, 0, responsavel);
        if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && qtdeDiasBloqCsaSolicSaldoDevedor > 0) {
            fields.add(Columns.VRR_CODIGO);
        }
        return fields.toArray(new String[0]);
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
    }
}
