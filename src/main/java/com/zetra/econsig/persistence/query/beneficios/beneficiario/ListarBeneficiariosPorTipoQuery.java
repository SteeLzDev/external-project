package com.zetra.econsig.persistence.query.beneficios.beneficiario;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;

/**
 * <p>Title: ListarBeneficiariosPorTipoQuery</p>
 * <p>Description: Listar beneficiários por tipo</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficiariosPorTipoQuery extends HQuery {

    public String tipoEntidade;
    public List<String> tibCodigos;
    public List<String> entCodigos;
    public List<String> srsCodigos;
    public List<String> bfcCodigos;
    public String serCodigo;
    public boolean aplicarRegrasDeOrdemDependencia = false;
    public boolean simulacao = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        // Primeira ordenação: Titular, Dependente Cônjuge, Demais Dependentes, Agregados
        StringBuilder ordem1 = new StringBuilder();
        ordem1.append("CASE ");
        ordem1.append("WHEN bfc.tipoBeneficiario.tibCodigo = '").append(CodedValues.TIB_TITULAR).append("' THEN '0' ");
        ordem1.append("WHEN bfc.tipoBeneficiario.tibCodigo = '").append(CodedValues.TIB_AGREGADO).append("' THEN '3' ");
        ordem1.append("ELSE CASE ");
        ordem1.append("WHEN bfc.grauParentesco.grpCodigo = '").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("' THEN '1' ");
        ordem1.append("WHEN bfc.grauParentesco.grpCodigo = '").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("' THEN '1' ");
        ordem1.append("ELSE '2' END ");
        ordem1.append("END ");

        // Ordenação demais dependentes: Filhos, Menor sob guarda, Enteados
        StringBuilder ordem2 = new StringBuilder();
        ordem2.append("CASE ");
        ordem2.append("WHEN bfc.grauParentesco.grpCodigo = '").append(GrauParentescoEnum.FILHO.getCodigo()).append("' THEN '1' ");
        ordem2.append("WHEN bfc.grauParentesco.grpCodigo = '").append(GrauParentescoEnum.MENOR_SOB_GUARDA.getCodigo()).append("' THEN '2' ");
        ordem2.append("WHEN bfc.grauParentesco.grpCodigo = '").append(GrauParentescoEnum.ENTEADO.getCodigo()).append("' THEN '3' ");
        ordem2.append("ELSE '4' END ");

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT DISTINCT ");
        corpoBuilder.append("rse.servidor.serCodigo, ");
        corpoBuilder.append("rse.orgao.orgCodigo, ");
        corpoBuilder.append("rse.statusRegistroServidor.srsCodigo, ");
        corpoBuilder.append("ser.serDataNasc, ");
        corpoBuilder.append("bfc.bfcCodigo ");

        if (simulacao) {
            corpoBuilder.append("FROM RegistroServidor rse ");
            corpoBuilder.append("INNER JOIN rse.servidor ser ");
            corpoBuilder.append("INNER JOIN rse.orgao org ");
            corpoBuilder.append("INNER JOIN ser.beneficiarioSet bfc ");
        } else {
            corpoBuilder.append("FROM AutDesconto ade ");
            corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
            corpoBuilder.append("INNER JOIN vco.convenio cnv ");
            corpoBuilder.append("INNER JOIN cnv.servico svc ");
            corpoBuilder.append("INNER JOIN cnv.orgao org ");
            corpoBuilder.append("INNER JOIN org.periodoBeneficioSet pbe ");
            corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
            corpoBuilder.append("INNER JOIN rse.servidor ser ");
            corpoBuilder.append("INNER JOIN ade.contratoBeneficio cbe ");
            corpoBuilder.append("INNER JOIN cbe.beneficiario bfc ");
        }

        // Beneficiários por tipo
        corpoBuilder.append("WHERE bfc.tipoBeneficiario.tibCodigo ").append(criaClausulaNomeada("tibCodigos", tibCodigos)).append(" ");

        // Consignação de registro servidor ativo
        corpoBuilder.append("AND rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos)).append(" ");

        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            if (tipoEntidade.equals("RSE")) {
                corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if (tipoEntidade.equals("ORG")) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            } else if (tipoEntidade.equals("EST")) {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(" ");
            }
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append("AND ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo)).append(" ");
        }

        // Consignações em andamento, ou excluídas pós corte de benefícios
        if (!simulacao) {
            corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
            corpoBuilder.append("OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_EXCLUIDOS_POS_CORTE, "','")).append("') ");
            corpoBuilder.append("AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
            corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXCLUSAO_POS_CORTE, "','")).append("') ");
            corpoBuilder.append("AND (oca.ocaData > pbe.pbeDataFim OR oca.ocaPeriodo > pbe.pbePeriodo)))) ");
        } else {
            corpoBuilder.append("AND bfc.statusBeneficiario.sbeCodigo = '").append(StatusBeneficiarioEnum.ATIVO.sbeCodigo).append("'");
            if (bfcCodigos != null && !bfcCodigos.isEmpty()) {
                corpoBuilder.append("AND bfc.bfcCodigo ").append(criaClausulaNomeada("bfcCodigos", bfcCodigos)).append(" ");
            }
        }

        /*
         Regras de ordem de dependência:
         a) Cônjuge;
         b) Pela ordem de data de nascimento, os demais dependentes;
         c) Se houver empate, pela regra anterior, levar em consideração a ordem de prioridade dos tipos de relacionamento a seguir:
            1. Filhos (no caso de gêmeos, trigêmeos, etc., levar em consideração a ordem alfabética dos nomes);
            2. Menor sob guarda;
            3. Enteados.
         d) Pela ordem de data de nascimento e depois alfabética, os agregados.
        */
        if (aplicarRegrasDeOrdemDependencia) {
            corpoBuilder.append("ORDER BY ").append(ordem1.toString());
            corpoBuilder.append(", bfc.bfcDataNascimento DESC, ");
            corpoBuilder.append(ordem2.toString());
            corpoBuilder.append(", bfc.bfcNome ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tibCodigos", tibCodigos, query);

        defineValorClausulaNomeada("srsCodigos", srsCodigos, query);

        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            defineValorClausulaNomeada("entCodigos", entCodigos, query);
        }

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (simulacao && bfcCodigos != null && !bfcCodigos.isEmpty()) {
            defineValorClausulaNomeada("bfcCodigos", bfcCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_SER_CODIGO,
                Columns.RSE_ORG_CODIGO,
                Columns.RSE_SRS_CODIGO,
                Columns.SER_DATA_NASC,
                Columns.BFC_CODIGO
        };
    }
}
