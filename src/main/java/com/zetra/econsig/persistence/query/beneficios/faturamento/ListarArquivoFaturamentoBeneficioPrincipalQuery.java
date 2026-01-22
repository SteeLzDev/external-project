package com.zetra.econsig.persistence.query.beneficios.faturamento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListarArquivoFaturamentoBeneficioPrincipalQuery</p>
 * <p>Description: Query para listar arquivo faturamento de beneficios principal.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ListarArquivoFaturamentoBeneficioPrincipalQuery extends HNativeQuery {

    public String fatCodigo;
    public Boolean creditos = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String tpaCodigo = CodedValues.TPA_VLR_MIN_ENVIO_BOLETO_FATURAMENTO_BENEFICIO;

        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT ");
        corpo.append("'' AS TIPO_REGISTRO, ");
        corpo.append("fat.fat_periodo AS COMPETENCIA_FATURAMENTO, ");
        corpo.append("afb.cbe_data_inclusao AS CBE_DATA_INCLUSAO, ");
        corpo.append("cbe.cbe_data_cancelamento AS CBE_DATA_CANCELAMENTO, ");
        corpo.append("afb.cbe_numero AS CBE_NUMERO, ");
        corpo.append("afb.ben_codigo_registro AS BEN_CODIGO_REGISTRO, ");
        corpo.append("afb.rse_matricula AS RSE_MATRICULA, ");
        corpo.append("afb.bfc_ordem_dependencia AS BFC_ORDEM_DEPENDENCIA, ");
        corpo.append("afb.ben_codigo_contrato AS BEN_CODIGO_CONTRATO, ");
        corpo.append("ade.ade_numero AS ADE_NUMERO, ");
        corpo.append("'' AS CODIGO_PROCEDIMENTO, ");
        corpo.append("'' AS DESCRICAO_PROCEDIMENTO, ");
        corpo.append("afb.afb_valor_total AS VALOR_DEBITO, ");
        corpo.append("'' AS QUANTIDADE, ");
        corpo.append("'' AS DATA_PROCEDIMENTO, ");
        corpo.append("'' AS TIPO_PRESTADOR, ");
        corpo.append("'' AS CNPJ_PRESTADOR, ");
        corpo.append("'' AS NOME_PRESTADOR, ");
        corpo.append("tla.tla_codigo AS TLA_CODIGO, ");
        corpo.append("fat.fat_periodo AS COMPETENCIA_PRORATA, ");
        corpo.append("'' AS REAJUSTE_FAIXA_ETARIA, ");
        corpo.append("'' AS REAJUSTE_ANUAL, ");
        corpo.append("'' AS MENSAGEM_REAJUSTE_FAIXA_ETARIA, ");
        corpo.append("'' AS MENSAGEM_REAJUSTE_ANUAL, ");
        corpo.append("ade.ade_numero AS SEQUENCIAL, ");
        corpo.append("afb.afb_numero_lote AS AFB_NUMERO_LOTE, ");
        corpo.append("afb.afb_item_lote AS AFB_ITEM_LOTE, ");
        corpo.append("afb.afb_valor_subsidio AS AFB_VALOR_SUBSIDIO, ");
        corpo.append("afb.afb_valor_realizado AS AFB_VALOR_REALIZADO, ");
        corpo.append("afb.afb_valor_nao_realizado AS AFB_VALOR_NAO_REALIZADO, ");
        corpo.append("afb.afb_valor_total AS AFB_VALOR_TOTAL, ");
        corpo.append("afb.ens_cep AS ENS_CEP, ");
        corpo.append("afb.ens_logradouro AS ENS_LOGRADOURO, ");
        corpo.append("afb.ens_numero AS ENS_NUMERO, ");
        corpo.append("afb.ens_complemento AS ENS_COMPLEMENTO, ");
        corpo.append("afb.ens_bairro AS ENS_BAIRRO, ");
        corpo.append("afb.ens_municipio AS ENS_MUNICIPIO, ");
        corpo.append("afb.ens_uf AS ENS_UF, ");
        corpo.append("afb.bfc_celular AS BFC_CELULAR, ");
        corpo.append("ser.ser_email AS SER_EMAIL, ");
        corpo.append("fat.fat_periodo AS COMPETENCIA_COBRANCA, ");
        corpo.append("org.org_identificador AS ORG_IDENTIFICADOR, ");
        corpo.append("afb.cnv_cod_verba AS CNV_COD_VERBA, ");
        corpo.append("afb.bfc_cpf AS BFC_CPF, ");

        corpo.append("(SELECT SUM(coalesce(afb1.afb_valor_nao_realizado, 0)) < pcs1.pcs_vlr ");
        corpo.append("FROM tb_arquivo_faturamento_ben afb1 ");
        corpo.append("INNER JOIN tb_aut_desconto ade1 on (afb1.ade_codigo = ade1.ade_codigo) ");
        corpo.append("INNER JOIN tb_verba_convenio vco1 on (ade1.vco_codigo = vco1.vco_codigo) ");
        corpo.append("INNER JOIN tb_convenio cnv1 on (cnv1.cnv_codigo = vco1.cnv_codigo) ");
        corpo.append("INNER JOIN tb_consignataria csa1 on (csa1.csa_codigo = cnv1.csa_codigo) ");
        corpo.append("INNER JOIN tb_contrato_beneficio cbe1 on (ade1.cbe_codigo = cbe1.cbe_codigo) ");
        corpo.append("INNER JOIN tb_registro_servidor rse1 on (ade1.rse_codigo = rse1.rse_codigo) ");
        corpo.append("INNER JOIN tb_servidor ser1 on (rse1.ser_codigo = ser1.ser_codigo) ");
        corpo.append("LEFT OUTER JOIN tb_param_consignataria pcs1 on (csa1.csa_codigo = pcs1.csa_codigo ");
        corpo.append("AND pcs1.tpa_codigo ").append(criaClausulaNomeada("tpaCodigo", tpaCodigo)).append(") ");
        corpo.append("WHERE coalesce(afb1.afb_valor_nao_realizado, 0) > 0 ");
        corpo.append("AND csa.csa_codigo = csa1.csa_codigo ");
        corpo.append("AND ser.ser_codigo = ser1.ser_codigo) AS MARGEM_NAO_CONSIGNAVEL, ");
        corpo.append("afb.bfc_nome AS BFC_NOME ");

        corpo.append("FROM tb_arquivo_faturamento_ben afb ");
        corpo.append("INNER JOIN tb_faturamento_beneficio fat on (afb.fat_codigo = fat.fat_codigo) ");
        corpo.append("INNER JOIN tb_consignataria csa on (csa.csa_codigo = fat.csa_codigo) ");
        corpo.append("INNER JOIN tb_aut_desconto ade on (ade.ade_codigo = afb.ade_codigo) ");
        corpo.append("INNER JOIN tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        corpo.append("INNER JOIN tb_orgao org on (rse.org_codigo = org.org_codigo) ");
        corpo.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpo.append("INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");//and csa.csa_codigo = cnv.csa_codigo and cnv.org_codigo = org.org_codigo
        corpo.append("INNER JOIN tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpo.append("INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
        corpo.append("INNER JOIN tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo) ");
        corpo.append("INNER JOIN tb_tipo_beneficiario tib on (bfc.tib_codigo = tib.tib_codigo) ");
        corpo.append("INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
        corpo.append("INNER JOIN tb_servidor ser on (ser.ser_codigo = rse.ser_codigo) ");
        corpo.append("LEFT OUTER JOIN tb_grau_parentesco grp on (grp.grp_codigo = bfc.grp_codigo) ");

        corpo.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(fatCodigo)) {
            corpo.append(" AND fat.fat_codigo ").append(criaClausulaNomeada("fatCodigo", fatCodigo));
        }

        if (creditos) {
            corpo.append(" AND (coalesce(afb.afb_valor_realizado, 0) > afb.prd_vlr_previsto) ");
        }

        corpo.append(" ORDER BY fat.fat_periodo, csa.csa_identificador, ser.ser_codigo, ade.ade_codigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("tpaCodigo", tpaCodigo, query);

        if (!TextHelper.isNull(fatCodigo)) {
            defineValorClausulaNomeada("fatCodigo", fatCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TIPO_REGISTRO",
                "COMPETENCIA_FATURAMENTO",
                "CBE_DATA_INCLUSAO",
                "CBE_DATA_CANCELAMENTO",
                "CBE_NUMERO",
                "BEN_CODIGO_REGISTRO",
                "RSE_MATRICULA",
                "BFC_ORDEM_DEPENDENCIA",
                "BEN_CODIGO_CONTRATO",
                "ADE_NUMERO",
                "CODIGO_PROCEDIMENTO",
                "DESCRICAO_PROCEDIMENTO",
                "VALOR_DEBITO",
                "QUANTIDADE",
                "DATA_PROCEDIMENTO",
                "TIPO_PRESTADOR",
                "CNPJ_PRESTADOR",
                "NOME_PRESTADOR",
                "TLA_CODIGO",
                "COMPETENCIA_PRORATA",
                "REAJUSTE_FAIXA_ETARIA",
                "REAJUSTE_ANUAL",
                "MENSAGEM_REAJUSTE_FAIXA_ETARIA",
                "MENSAGEM_REAJUSTE_ANUAL",
                "SEQUENCIAL",
                "AFB_NUMERO_LOTE",
                "AFB_ITEM_LOTE",
                "AFB_VALOR_SUBSIDIO",
                "AFB_VALOR_REALIZADO",
                "AFB_VALOR_NAO_REALIZADO",
                "AFB_VALOR_TOTAL",
                "ENS_CEP",
                "ENS_LOGRADOURO",
                "ENS_NUMERO",
                "ENS_COMPLEMENTO",
                "ENS_BAIRRO",
                "ENS_MUNICIPIO",
                "ENS_UF",
                "BFC_CELULAR",
                "SER_EMAIL",
                "COMPETENCIA_COBRANCA",
                "ORG_IDENTIFICADOR",
                "CNV_COD_VERBA",
                "BFC_CPF",
                "MARGEM_NAO_CONSIGNAVEL",
                "BFC_NOME"
            };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
