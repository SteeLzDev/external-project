package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosBeneficiosMensalidadeEdicaoTelaQuery</p>
 * <p>Description: Lista os contratos beneficio com os dados necessarios para a tela.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarContratosBeneficiosMensalidadeEdicaoTelaQuery extends HQuery {
    public String cbeCodigo;
    public List<String> tntCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cbe.cbeCodigo,");
        corpoBuilder.append(" ade.adeCodigo,");
        corpoBuilder.append(" cnv.orgao.orgCodigo,");
        corpoBuilder.append(" cbe.cbeNumero,");
        corpoBuilder.append(" cbe.cbeDataInclusao,");
        corpoBuilder.append(" cbe.cbeDataInicioVigencia,");
        corpoBuilder.append(" cbe.cbeValorTotal,");
        corpoBuilder.append(" cbe.cbeValorSubsidio,");
        corpoBuilder.append(" cbe.cbeDataFimVigencia,");
        corpoBuilder.append(" cbe.cbeDataCancelamento,");
        corpoBuilder.append(" scb.scbDescricao,");
        corpoBuilder.append(" dad34.dadValor,");
        corpoBuilder.append(" dad35.dadValor,");
        corpoBuilder.append(" dad36.dadValor,");
        corpoBuilder.append(" dad37.dadValor,");
        corpoBuilder.append(" bfc.servidor.serCodigo,");
        corpoBuilder.append(" bfc.bfcCodigo,");
        corpoBuilder.append(" bfc.bfcNome,");
        corpoBuilder.append(" bfc.tipoBeneficiario.tibCodigo,");
        corpoBuilder.append(" ben.benCodigo,");
        corpoBuilder.append(" ben.benCodigoPlano,");
        corpoBuilder.append(" ben.benDescricao,");
        corpoBuilder.append(" ben.benCodigoRegistro,");
        corpoBuilder.append(" ben.benCodigoContrato,");
        corpoBuilder.append(" csa.csaNome,");
        corpoBuilder.append(" nse.nseDescricao,");
        corpoBuilder.append(" rse.rseCodigo,");
        corpoBuilder.append(" rse.rseMatricula,");
        corpoBuilder.append(" csa.csaCodigo,");
        corpoBuilder.append(" svc.svcCodigo");

        corpoBuilder.append(" from ContratoBeneficio cbe");
        corpoBuilder.append(" inner join cbe.beneficiario bfc");
        corpoBuilder.append(" inner join cbe.beneficio ben");
        corpoBuilder.append(" inner join ben.consignataria csa");
        corpoBuilder.append(" inner join ben.naturezaServico nse");
        corpoBuilder.append(" inner join cbe.statusContratoBeneficio scb");
        corpoBuilder.append(" inner join cbe.autDescontoSet ade");
        corpoBuilder.append(" inner join ade.tipoLancamento tla");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" inner join tla.tipoNatureza tnt");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");

        // Buscando o DAD para exibir na tela.
        corpoBuilder.append(" left join ade.dadosAutorizacaoDescontoSet dad34 WITH dad34.tdaCodigo = '").append(CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO).append("'");
        corpoBuilder.append(" left join ade.dadosAutorizacaoDescontoSet dad35 WITH dad35.tdaCodigo = '").append(CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO).append("'");
        corpoBuilder.append(" left join ade.dadosAutorizacaoDescontoSet dad36 WITH dad36.tdaCodigo = '").append(CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO).append("'");
        corpoBuilder.append(" left join ade.dadosAutorizacaoDescontoSet dad37 WITH dad37.tdaCodigo = '").append(CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO).append("'");

        corpoBuilder.append(" where 1 = 1");

        if (!TextHelper.isNull(cbeCodigo)) {
            corpoBuilder.append(" and cbe.cbeCodigo = :cbeCodigo");
        }

        if (!TextHelper.isNull(tntCodigo)) {
            corpoBuilder.append(" and tnt.tntCodigo in (:tntCodigo)");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cbeCodigo)) {
            defineValorClausulaNomeada("cbeCodigo", cbeCodigo, query);
        }

        if (!TextHelper.isNull(tntCodigo)) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO,
                Columns.ADE_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CBE_NUMERO,
                Columns.CBE_DATA_INCLUSAO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.CBE_VALOR_TOTAL,
                Columns.CBE_VALOR_SUBSIDIO,
                Columns.CBE_DATA_FIM_VIGENCIA,
                Columns.CBE_DATA_CANCELAMENTO,
                Columns.SCB_DESCRICAO,
                Columns.DAD_VALOR + CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO,
                Columns.DAD_VALOR + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO,
                Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO,
                Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO,
                Columns.SER_CODIGO,
                Columns.BFC_CODIGO,
                Columns.BFC_NOME,
                Columns.TIB_CODIGO,
                Columns.BEN_CODIGO,
                Columns.BEN_CODIGO_PLANO,
                Columns.BEN_DESCRICAO,
                Columns.BEN_CODIGO_REGISTRO,
                Columns.BEN_CODIGO_CONTRATO,
                Columns.CSA_NOME,
                Columns.NSE_DESCRICAO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.CSA_CODIGO,
                Columns.SVC_CODIGO
        };
    }

}
