package com.zetra.econsig.persistence.query.beneficios.faturamento;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoEnderecoEnum;

/**
 * <p>Title: ListarArquivoFaturamentoBeneficioMncResiduoQuery</p>
 * <p>Description: Query para listar arquivo faturamento de beneficios de margem não consignável e resíduo.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ListarArquivoFaturamentoBeneficioMncResiduoQuery extends HNativeQuery {

    public String fatCodigo;
    public BigDecimal pcsVlr;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> tntResiduo = CodedValues.TNT_BENEFICIO_RESIDUO;
        String tntResiduoPlanoSaude = CodedValues.TNT_RESIDUO_PLANO_SAUDE;
        String tntResiduoOdonto = CodedValues.TNT_RESIDUO_ODONTOLOGICO;
        String nsePlanoSaude = CodedValues.NSE_PLANO_DE_SAUDE;
        String nsePlanoOdonto = CodedValues.NSE_PLANO_ODONTOLOGICO;
        List<String> tntMensalidade = CodedValues.TNT_BENEFICIO_MENSALIDADE;

        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT ");
        corpo.append("'' AS TIPO_REGISTRO, ");
        corpo.append("fat.fat_periodo AS COMPETENCIA_FATURAMENTO, ");
        corpo.append("afb.cbe_data_inclusao AS CBE_DATA_INCLUSAO, ");
        corpo.append("cbe.cbe_data_cancelamento AS CBE_DATA_CANCELAMENTO, ");
        corpo.append("afb.cbe_numero AS CBE_NUMERO, ");
        corpo.append("afb.ben_codigo_registro AS BEN_CODIGO_REGISTRO, ");
        corpo.append("afb.rse_matricula AS RSE_MATRICULA, ");
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
        corpo.append("t.MARGEM_NAO_CONSIGNAVEL AS MARGEM_NAO_CONSIGNAVEL, ");
        corpo.append("afb.bfc_nome AS BFC_NOME, ");
        corpo.append("add_month(fat.fat_periodo, 1) AS ADE_ANO_MES_INI_POS, ");
        corpo.append("t.cnv_cod_verba AS CNV_COD_VERBA_RESIDUO, ");
        corpo.append("t.svc_descricao AS SVC_DESCRICAO_RESIDUO, ");
        corpo.append("org.org_identificador_beneficio AS ORG_IDENTIFICADOR_BENEFICIO, ");
        corpo.append("ens.ens_codigo_municipio AS ENS_CODIGO_MUNICIPIO, ");
        corpo.append("ben.ben_codigo_plano AS BEN_CODIGO_PLANO, ");
        corpo.append("bfc.bfc_data_nascimento AS BFC_DATA_NASCIMENTO, ");
        corpo.append("data_corrente() AS DATA_CORRENTE, ");
        corpo.append("bfc.bfc_estado_civil AS BFC_ESTADO_CIVIL, ");
        corpo.append("grp.grp_codigo AS GRP_CODIGO, ");
        corpo.append("grp.grp_descricao AS GRP_DESCRICAO, ");
        corpo.append("bfc.mde_codigo AS MDE_CODIGO, ");
        corpo.append("bfc.bfc_nome_mae AS BFC_NOME_MAE, ");
        corpo.append("afb.bfc_ordem_dependencia AS BFC_ORDEM_DEPENDENCIA, ");
        corpo.append("bfc.bfc_rg AS BFC_RG, ");
        corpo.append("bfc.bfc_sexo AS BFC_SEXO, ");
        corpo.append("bfc.bfc_telefone AS BFC_TELEFONE, ");
        corpo.append("tib.tib_codigo AS TIB_CODIGO, ");
        corpo.append("tib.tib_descricao AS TIB_DESCRICAO ");

        corpo.append("FROM tb_faturamento_beneficio fat ");
        corpo.append("INNER JOIN tb_consignataria csa on (csa.csa_codigo = fat.csa_codigo) ");
        corpo.append("INNER JOIN tb_arquivo_faturamento_ben afb ON (afb.fat_codigo = fat.fat_codigo) ");
        corpo.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = afb.ade_codigo) ");
        corpo.append("INNER JOIN tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        corpo.append("INNER JOIN tb_servidor ser on (ser.ser_codigo = rse.ser_codigo) ");
        corpo.append("INNER JOIN tb_orgao org on (rse.org_codigo = org.org_codigo) ");
        corpo.append("INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
        corpo.append("INNER JOIN tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo) ");
        corpo.append("INNER JOIN tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo) ");
        corpo.append("INNER JOIN tb_tipo_beneficiario tib on (bfc.tib_codigo = tib.tib_codigo) ");
        corpo.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        corpo.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpo.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
        corpo.append("INNER JOIN tb_relacionamento_servico rel ON (rel.svc_codigo_origem = svc.svc_codigo AND rel.tnt_codigo ").append(criaClausulaNomeada("tntResiduo", tntResiduo)).append(") ");
        corpo.append("INNER JOIN tb_convenio cnvResiduo ON (cnvResiduo.org_codigo = cnv.org_codigo AND cnvResiduo.csa_codigo = cnv.csa_codigo AND cnvResiduo.svc_codigo = rel.svc_codigo_destino) ");
        corpo.append("INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
        corpo.append("LEFT OUTER JOIN tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo and ens.tie_codigo = '").append(TipoEnderecoEnum.COBRANCA.getCodigo()).append("') ");

        corpo.append("INNER JOIN (");
        corpo.append("SELECT SUM(coalesce(afb1.AFB_VALOR_NAO_REALIZADO, 0)) AS MARGEM_NAO_CONSIGNAVEL, rse1.ser_codigo, cnvResiduo1.cnv_cod_verba AS CNV_COD_VERBA, svcResiduo1.svc_descricao AS SVC_DESCRICAO, fat1.fat_codigo ");
        corpo.append("FROM tb_faturamento_beneficio fat1 ");
        corpo.append("INNER JOIN tb_arquivo_faturamento_ben afb1 ON (afb1.fat_codigo = fat1.fat_codigo) ");
        corpo.append("INNER JOIN tb_aut_desconto ade1 ON (ade1.ade_codigo = afb1.ade_codigo) ");
        corpo.append("INNER JOIN tb_registro_servidor rse1 ON (ade1.rse_codigo = rse1.rse_codigo) ");
        corpo.append("INNER JOIN tb_verba_convenio vco1 ON (vco1.vco_codigo = ade1.vco_codigo) ");
        corpo.append("INNER JOIN tb_convenio cnv1 ON (cnv1.cnv_codigo = vco1.cnv_codigo) ");
        corpo.append("INNER JOIN tb_servico svc1 ON (svc1.svc_codigo = cnv1.svc_codigo) ");
        corpo.append("INNER JOIN tb_relacionamento_servico rel1 ON (rel1.svc_codigo_origem = svc1.svc_codigo AND rel1.tnt_codigo ").append(criaClausulaNomeada("tntResiduo", tntResiduo)).append(") ");
        corpo.append("INNER JOIN tb_convenio cnvResiduo1 ON (cnvResiduo1.org_codigo = cnv1.org_codigo AND cnvResiduo1.csa_codigo = cnv1.csa_codigo AND cnvResiduo1.svc_codigo = rel1.svc_codigo_destino) ");
        corpo.append("INNER JOIN tb_servico svcResiduo1 ON (cnvResiduo1.svc_codigo = svcResiduo1.svc_codigo) ");
        corpo.append("WHERE 1 = 1 ");
        corpo.append(" AND fat1.fat_codigo ").append(criaClausulaNomeada("fatCodigo", fatCodigo));
        corpo.append(" AND ((svc1.nse_codigo ").append(criaClausulaNomeada("nsePlanoSaude", nsePlanoSaude));
        corpo.append(" AND rel1.tnt_codigo ").append(criaClausulaNomeada("tntResiduoPlanoSaude", tntResiduoPlanoSaude)).append(") ");
        corpo.append(" OR (svc1.nse_codigo ").append(criaClausulaNomeada("nsePlanoOdonto", nsePlanoOdonto));
        corpo.append(" AND rel1.tnt_codigo ").append(criaClausulaNomeada("tntResiduoOdonto", tntResiduoOdonto)).append(")) ");
        corpo.append("GROUP BY rse1.ser_codigo, cnvResiduo1.cnv_cod_verba, svcResiduo1.svc_descricao, fat1.fat_codigo ");
        corpo.append(") t ON (fat.fat_codigo = t.fat_codigo and ser.ser_codigo = t.ser_codigo) ");

        corpo.append("LEFT OUTER JOIN tb_grau_parentesco grp on (grp.grp_codigo = bfc.grp_codigo) ");

        corpo.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(fatCodigo)) {
            corpo.append(" AND fat.fat_codigo ").append(criaClausulaNomeada("fatCodigo", fatCodigo));
        }

        corpo.append(" AND (bfc.ser_codigo = ser.ser_codigo and tib.tib_codigo = '").append(TipoBeneficiarioEnum.TITULAR.tibCodigo).append("') ");
        corpo.append(" AND tla.tnt_codigo ").append(criaClausulaNomeada("tntMensalidade", tntMensalidade));
        corpo.append(" AND t.MARGEM_NAO_CONSIGNAVEL < :pcsVlr ");
        corpo.append(" AND coalesce(t.MARGEM_NAO_CONSIGNAVEL, 0) > 0 ");

        corpo.append(" ORDER BY fat.fat_periodo, csa.csa_identificador, ser.ser_codigo, ade.ade_codigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(fatCodigo)) {
            defineValorClausulaNomeada("fatCodigo", fatCodigo, query);
        }

        defineValorClausulaNomeada("tntMensalidade", tntMensalidade, query);
        defineValorClausulaNomeada("tntResiduo", tntResiduo, query);
        defineValorClausulaNomeada("tntResiduoPlanoSaude", tntResiduoPlanoSaude, query);
        defineValorClausulaNomeada("tntResiduoOdonto", tntResiduoOdonto, query);
        defineValorClausulaNomeada("nsePlanoSaude", nsePlanoSaude, query);
        defineValorClausulaNomeada("nsePlanoOdonto", nsePlanoOdonto, query);
        defineValorClausulaNomeada("pcsVlr", pcsVlr, query);

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
                "BFC_NOME",
                "ADE_ANO_MES_INI_POS",
                "CNV_COD_VERBA_RESIDUO",
                "SVC_DESCRICAO_RESIDUO",
                "ORG_IDENTIFICADOR_BENEFICIO",
                "ENS_CODIGO_MUNICIPIO",
                "BEN_CODIGO_PLANO",
                "BFC_DATA_NASCIMENTO",
                "DATA_CORRENTE",
                "BFC_ESTADO_CIVIL",
                "GRP_CODIGO",
                "GRP_DESCRICAO",
                "MDE_CODIGO",
                "BFC_NOME_MAE",
                "BFC_ORDEM_DEPENDENCIA",
                "BFC_RG",
                "BFC_SEXO",
                "BFC_TELEFONE",
                "TIB_CODIGO",
                "TIB_DESCRICAO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
