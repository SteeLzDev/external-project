package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioIncBeneficiariosPorPeriodoQuery</p>
 * <p>Description: Query usada para gerar relatório de beneficiários por período</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioIncBeneficiariosPorPeriodoQuery extends ReportHQuery {

    public String codigoOperadora;

    public String benCodigo;

    public String dataInicio;

    public String dataFim;

    public RelatorioIncBeneficiariosPorPeriodoQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT DISTINCT beneficiario.bfcCodigo as bfc_codigo, "
                + "beneficiario.bfcNome as bfc_nome, "
                + "beneficiario.bfcDataNascimento as bfc_data_nascimento, "
                + "contrato.cbeDataInclusao as cbe_data_inclusao, "
                + "contrato.cbeNumero as cbe_numero, "
                + "registro.rseMatricula as rse_matricula, "
                + "servidor.serNome as ser_nome, "
                + "consignataria.csaNome as csa_nome, "
                + "beneficio.benDescricao as ben_descricao, "
                + "consignataria.csaCodigo as csa_codigo, "
                + "contrato.cbeDataInicioVigencia as cbe_data_inicio_vigencia, "
                + "scb.scbDescricao as scb_descricao, "
                + "desconto.adeCodigo as ade_codigo, "
                + "contrato.cbeValorTotal as cbe_valor_total ");

        corpo.append("from AutDesconto desconto ");
        corpo.append("inner join desconto.tipoLancamento tla ");
        corpo.append("inner join tla.tipoNatureza tnt ");
        corpo.append("inner join desconto.contratoBeneficio contrato ");
        corpo.append("inner join contrato.statusContratoBeneficio scb ");
        corpo.append("inner join desconto.registroServidor registro ");
        corpo.append("inner join contrato.beneficiario beneficiario ");
        corpo.append("inner join contrato.beneficio beneficio ");
        corpo.append("inner join beneficiario.servidor servidor ");
        corpo.append("inner join beneficio.consignataria consignataria ");
        corpo.append("where ");
        corpo.append("contrato.cbeDataInclusao between :dataInicio and :dataFim ");
        corpo.append("AND tnt.tntCodigo in (:tntCodigos) ");

        if (!TextHelper.isNull(codigoOperadora)) {
            corpo.append("AND consignataria.csaCodigo ").append(criaClausulaNomeada("operadoraCodigo", codigoOperadora)).append(" ");
        }

        if (!TextHelper.isNull(benCodigo)) {
            corpo.append("AND beneficio.benCodigo ").append(criaClausulaNomeada("benCodigo", benCodigo));
        }

        corpo.append("ORDER BY contrato.cbeDataInclusao, registro.rseMatricula, beneficio.benDescricao, consignataria.csaNome ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(codigoOperadora)) {
            defineValorClausulaNomeada("operadoraCodigo", codigoOperadora, query);
        }

        if (!TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        defineValorClausulaNomeada("tntCodigos", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);

        defineValorClausulaNomeada("dataInicio", parseDateTimeString(dataInicio), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        codigoOperadora = (String) criterio.getAttribute("CSA_CODIGO");
        benCodigo = (String) criterio.getAttribute("BEN_CODIGO");
        dataInicio = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BFC_CODIGO,
                Columns.BFC_NOME,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.CBE_DATA_INCLUSAO,
                Columns.CBE_NUMERO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.CSA_NOME,
                Columns.BEN_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.SCB_DESCRICAO,
                Columns.ADE_CODIGO,
                Columns.CBE_VALOR_TOTAL
        };
    }
}
