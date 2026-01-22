package com.zetra.econsig.persistence.query.servidor;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorPorCnvQuery</p>
 * <p>Description: Recupera servidor de acordo com dados do convênio ao qual está ligado</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorPorCnvQuery extends HNativeQuery {

    public String codVerba;
    public String csaCodigo;
    public String tipo;
    public String tipoCodigo;
    public String estIdentificador;
    public String orgIdentificador;
    public String rseMatricula;
    public String serCpf;
    public String svcIdentificador;
    public String nseCodigo;
    public boolean cnvAtivo;
    public boolean serAtivo;
    public boolean inclusao;
    public boolean renegociacao;
    public List<String> ignorarRseCodigo;
    public boolean matriculaExata = false;

    public String numeroContratoBeneficio;
    public boolean buscaBeneficiario = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
                "select " +
                        "rse.rse_codigo, " +
                        "rse.rse_margem_rest, " +
                        "rse.rse_margem_rest_2, " +
                        "rse.rse_margem_rest_3, " +
                        "rse.rse_matricula, " +
                        "rse.rse_prazo, " +
                        "rse.rse_banco_sal, " +
                        "rse.rse_agencia_sal, " +
                        "rse.rse_conta_sal, " +
                        "rse.srs_codigo, " +
                        "rse.vrs_codigo, " +
                        "rse.rse_tipo, " +
                        "ser.ser_codigo, " +
                        "ser.ser_cpf, " +
                        "ser.ser_data_nasc, " +
                        "org.org_codigo, " +
                        "cnv.cnv_codigo, " +
                        "cnv.csa_codigo, " +
                        "cnv.cnv_cod_verba, " +
                        "svc.svc_codigo, " +
                        "svc.svc_identificador, " +
                        "mrs.mrs_margem_rest ";

        if (buscaBeneficiario) {
            corpo += ", bfc.bfc_cpf ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from tb_servidor ser");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (ser.ser_codigo = rse.ser_codigo)");
        corpoBuilder.append(" inner join tb_orgao org on (rse.org_codigo = org.org_codigo)");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (cnv.org_codigo = org.org_codigo)");

        if (buscaBeneficiario) {
            corpoBuilder.append(" inner join tb_beneficiario bfc on (bfc.ser_codigo = ser.ser_codigo) ");
            corpoBuilder.append(" inner join tb_contrato_beneficio cbe on (cbe.bfc_codigo = bfc.bfc_codigo) ");
        }

        if (tipo.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" inner join tb_correspondente_convenio crc on (crc.cnv_codigo = cnv.cnv_codigo)");
        }

        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
        corpoBuilder.append(" left outer join tb_param_svc_consignante pse on (pse.svc_codigo = svc.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");
        corpoBuilder.append(" left outer join tb_margem_registro_servidor mrs on (mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = to_numeric(COALESCE(NULLIF(TRIM(pse.pse_vlr), ''), '0')))");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(codVerba)) {
            corpoBuilder.append(" AND cnv.cnv_cod_verba ").append(criaClausulaNomeada("codVerba", codVerba));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" AND est.est_identificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" AND org.org_identificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" AND svc.svc_identificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (tipo.equalsIgnoreCase("CSA")) {
            if (cnvAtivo) {
                corpoBuilder.append(" AND cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
            }
        } else if (tipo.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" AND crc.cor_codigo ").append(criaClausulaNomeada("tipoCodigo", tipoCodigo));
            if(cnvAtivo) {
                corpoBuilder.append(" AND cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" AND crc.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
            }
        } else if (tipo.equalsIgnoreCase("ORG")) {
            corpoBuilder.append(" AND org.org_codigo ").append(criaClausulaNomeada("tipoCodigo", tipoCodigo));
        } else if (tipo.equalsIgnoreCase("EST")) {
            corpoBuilder.append(" AND est.est_codigo ").append(criaClausulaNomeada("tipoCodigo", tipoCodigo));
        }

        if (serAtivo) {
            corpoBuilder.append(" AND (rse.srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("'").append(",'").append(CodedValues.SRS_PENDENTE).append("')");
        } else {
            corpoBuilder.append(" AND (rse.srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("'").append(",'").append(CodedValues.SRS_PENDENTE).append("'").append(",'").append(CodedValues.SRS_EXCLUIDO).append("')");
        }
        if (inclusao) {
            corpoBuilder.append("  OR (rse.srs_codigo IN ('").append(TextHelper.join(CodedValues.SRS_BLOQUEADOS, "','")).append("')");
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_param_svc_consignante pse254");
            corpoBuilder.append("             WHERE pse254.svc_codigo = svc.svc_codigo");
            corpoBuilder.append("               AND pse254.tps_codigo = '").append(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO).append("'");
            corpoBuilder.append("               AND pse254.pse_vlr = '1'");
            corpoBuilder.append("     )");
            corpoBuilder.append("   )");
        }
        corpoBuilder.append(" )");

        if (renegociacao) {
            // Se é operação de renegociação via lote, pesquisa serviços que são origem
            // de relacionamento de renegociação
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_relacionamento_servico rsv");
            corpoBuilder.append(" WHERE rsv.svc_codigo_origem = svc.svc_codigo");
            corpoBuilder.append(" AND rsv.tnt_codigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("')");
        }

        // Gera cláusula de Matricula e CPF
        if (buscaBeneficiario) {
            corpoBuilder.append(ListaServidorQuery.gerarClausulaNativaMatriculaCpf(rseMatricula, null, !matriculaExata));
        } else {
            corpoBuilder.append(ListaServidorQuery.gerarClausulaNativaMatriculaCpf(rseMatricula, serCpf, !matriculaExata));
        }

        // Gera cláusula para ignorar conjunto de rseCodigo
        if (ignorarRseCodigo != null && ignorarRseCodigo.size() > 0) {
            ignorarRseCodigo.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND rse.rse_codigo ").append(criaClausulaNomeada("ignorarRseCodigo", ignorarRseCodigo));
        }

        if (buscaBeneficiario) {
            if (!TextHelper.isNull(numeroContratoBeneficio)) {
                corpoBuilder.append(" AND cbe.cbe_numero = :numeroContratoBeneficio ");
            }
            if (!TextHelper.isNull(serCpf)) {
                corpoBuilder.append(" AND bfc.bfc_cpf = :serCpf ");
            }
        }

        // Cláusula de ordenação
        corpoBuilder.append(" ORDER BY rse.rse_codigo, svc.svc_identificador");

        if (buscaBeneficiario) {
            corpoBuilder.append(" , bfc.tib_codigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (buscaBeneficiario) {
            ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, null, !matriculaExata, query);
        } else {
            ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata, query);
        }

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(codVerba)) {
            defineValorClausulaNomeada("codVerba", codVerba, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (tipo.equalsIgnoreCase("COR") || tipo.equalsIgnoreCase("ORG") || tipo.equalsIgnoreCase("EST")) {
            defineValorClausulaNomeada("tipoCodigo", tipoCodigo, query);
        }

        if (ignorarRseCodigo != null && ignorarRseCodigo.size() > 0) {
            defineValorClausulaNomeada("ignorarRseCodigo", ignorarRseCodigo, query);
        }

        if (buscaBeneficiario) {
            if (!TextHelper.isNull(numeroContratoBeneficio)) {
                defineValorClausulaNomeada("numeroContratoBeneficio", numeroContratoBeneficio, query);
            }

            if (!TextHelper.isNull(serCpf)) {
                defineValorClausulaNomeada("serCpf", serCpf, query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        String[] fields = {
                Columns.RSE_CODIGO,
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MARGEM_REST_2,
                Columns.RSE_MARGEM_REST_3,
                Columns.RSE_MATRICULA,
                Columns.RSE_PRAZO,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.SRS_CODIGO,
                Columns.VRS_CODIGO,
                Columns.RSE_TIPO,
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.ORG_CODIGO,
                Columns.CNV_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.MRS_MARGEM_REST
        };

        if (buscaBeneficiario) {
            // Se tem modolo beneficio configurado
            String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.BFC_CPF;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        return fields;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null));
    }
}
