package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusServidorEnum;

/**
 * <p>Title: ListarServidoresCalculoSubsidioQuery</p>
 * <p>Description: Lista servidores para cálculo de subsídio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarServidoresCalculoSubsidioQuery extends HQuery {

    public String tipoEntidade;
    public List<String> entCodigos;
    public List<String> srsCodigos;
    public boolean servidoresForaFolha;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT ");
        corpoBuilder.append("ser.serCodigo, ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("rse.orgao.orgCodigo, ");
        corpoBuilder.append("rse.statusRegistroServidor.srsCodigo, ");
        corpoBuilder.append("rse.rseSalario ");

        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN org.periodoBeneficioSet pbe ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN svc.paramSvcConsignanteSet pse256 ");
        corpoBuilder.append("WITH pse256.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TEM_SUBSIDIO).append("' ");

        // Consignação que possui contrato benefício de serviço que possui subsídio
        corpoBuilder.append("WHERE ade.contratoBeneficio.cbeCodigo IS NOT NULL ");
        corpoBuilder.append("AND pse256.pseVlr = '1' ");

        if(!servidoresForaFolha) {
            // Consignação de registro servidor ativo
            corpoBuilder.append("AND rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos)).append(" ");
        } else {
            corpoBuilder.append("AND rse.statusRegistroServidor.srsCodigo='").append(CodedValues.SRS_EXCLUIDO).append("' ");
        }

        // Consignações em andamento, ou excluídas pós corte
        corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
        corpoBuilder.append("OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_EXCLUIDOS_POS_CORTE, "','")).append("') ");
        corpoBuilder.append("AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXCLUSAO_POS_CORTE, "','")).append("') ");
        corpoBuilder.append("AND (oca.ocaData > pbe.pbeDataFim OR oca.ocaPeriodo > pbe.pbePeriodo)))) ");

        if ((tipoEntidade != null) && (entCodigos != null) && !entCodigos.isEmpty()) {
            if ("RSE".equals(tipoEntidade)) {
                corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if ("ORG".equals(tipoEntidade)) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if ("EST".equals(tipoEntidade)) {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            }
        }

        if(servidoresForaFolha) {
            corpoBuilder.append(" AND ser.statusServidor.sseCodigo='").append(StatusServidorEnum.FORA_FOLHA_DIREITO_SUBSIDIO.getCodigo()).append("'");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!servidoresForaFolha) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if ((tipoEntidade != null) && (entCodigos != null) && !entCodigos.isEmpty()) {
            defineValorClausulaNomeada("entCodigos", entCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.ORG_CODIGO,
                Columns.SRS_CODIGO,
                Columns.RSE_SALARIO
        };
    }
}