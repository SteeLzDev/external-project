package com.zetra.econsig.persistence.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.values.TipoEnderecoEnum;

/**
 * <p>Title: MySqlArquivoFaturamentoBeneficioDAO</p>
 * <p>Description: Implementação para MySQL do DAO de ArquivoFaturamentoBeneficio</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlArquivoFaturamentoBeneficioDAO extends GenericArquivoFaturamentoBeneficioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlArquivoFaturamentoBeneficioDAO.class);

    @Override
    public void inserirArquivoFaturamentoBeneficio(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;

            final List<String> tntCodigosSubsidio = new ArrayList<>();
            tntCodigosSubsidio.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);
            tntCodigosSubsidio.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO_PRO_RATA);

            final List<String> tntCodigosSemFatura = new ArrayList<>();
            tntCodigosSemFatura.addAll(CodedValues.TNT_BENEFICIO_VERBAS_SEM_FATURA);

            final List<String> tntCodigos = new ArrayList<>();
            tntCodigos.addAll(CodedValues.TNT_RELACIONAMENTO_MODULO_BENEFICIO);
            tntCodigos.removeAll(tntCodigosSubsidio);
            tntCodigos.removeAll(tntCodigosSemFatura);

            final StringBuilder query = new StringBuilder();
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // INSERE FATURAMENTO
            query.setLength(0);
            query.append("INSERT INTO tb_faturamento_beneficio (FAT_CODIGO, CSA_CODIGO, FAT_PERIODO, FAT_DATA) ");
            query.append("SELECT CONCAT('H', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(csa.csa_codigo, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("csa.csa_codigo, :periodo, NOW() ");
            query.append("FROM tb_consignataria csa ");
            query.append("WHERE csa.nca_codigo = '" + NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo() + "' ");
            query.append("AND NOT EXISTS (SELECT 1 FROM tb_faturamento_beneficio fat WHERE fat.csa_codigo = csa.csa_codigo AND fat.fat_periodo = :periodo) ");
            query.append("GROUP BY csa.csa_codigo");
            LOG.trace(query.toString());
            queryParams.addValue("periodo", periodo);
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // INSERE ARQUIVO FATURAMENTO
            query.setLength(0);
            query.append("INSERT INTO tb_arquivo_faturamento_ben ");
            query.append("(FAT_CODIGO, ADE_CODIGO, TLA_CODIGO, RSE_MATRICULA, CBE_NUMERO, CBE_VALOR_TOTAL, CBE_DATA_INCLUSAO, ");
            query.append("BEN_CODIGO_REGISTRO, BEN_CODIGO_CONTRATO, BFC_CPF, BFC_CELULAR, BFC_ORDEM_DEPENDENCIA, BFC_NOME, ");
            query.append("ENS_CEP, ENS_LOGRADOURO, ENS_NUMERO, ENS_COMPLEMENTO, ENS_BAIRRO, ENS_MUNICIPIO, ENS_UF, ENS_CODIGO_MUNICIPIO, ");
            query.append("PRD_VLR_PREVISTO, ADE_ANO_MES_INI, CNV_COD_VERBA, RSE_MATRICULA_INST, ");
            query.append("AFB_NUMERO_LOTE, AFB_ITEM_LOTE, AFB_VALOR_SUBSIDIO, AFB_VALOR_REALIZADO, AFB_VALOR_NAO_REALIZADO, ");
            query.append("AFB_VALOR_TOTAL, AFB_CODIGO_FUNDO_REPASSE, AFB_DESCRICAO_FUNDO_REPASSE) ");

            query.append("SELECT fat.fat_codigo, ade.ade_codigo, ");
            query.append("tla.tla_codigo, ");
            query.append("rse.rse_matricula, ");
            query.append("cbe.cbe_numero, ");
            query.append("cbe.cbe_valor_total, ");
            query.append("cbe.cbe_data_inclusao, ");
            query.append("ben.ben_codigo_registro, ");
            query.append("ben.ben_codigo_contrato, ");
            query.append("bfc.bfc_cpf, ");
            query.append("bfc.bfc_celular, ");
            query.append("bfc.bfc_ordem_dependencia, ");
            query.append("bfc.bfc_nome, ");
            query.append("ens.ens_cep, ");
            query.append("ens.ens_logradouro, ");
            query.append("ens.ens_numero, ");
            query.append("ens.ens_complemento, ");
            query.append("ens.ens_bairro, ");
            query.append("ens.ens_municipio, ");
            query.append("ens.ens_uf, ");
            query.append("ens.ens_codigo_municipio, ");
            query.append("prd.prd_vlr_previsto, ");
            query.append("ade.ade_ano_mes_ini, ");
            query.append("cnv.cnv_cod_verba, ");
            query.append("rse.rse_matricula_inst, ");
            query.append("cbe.cbe_numero_lote, ");
            query.append("cbe.cbe_item_lote, ");
            query.append("prdSub.prd_vlr_previsto, ");
            query.append("coalesce(prd.prd_vlr_realizado, 0), ");
            query.append("coalesce(prd.prd_vlr_previsto, 0) - coalesce(prd.prd_vlr_realizado, 0), "); // valorNaoRealizado
            query.append("coalesce(prd.prd_vlr_previsto, 0) + coalesce(prdSub.prd_vlr_previsto, 0), "); // totalValor
            //TODO Será incluído em atividade futura: AFB_CODIGO_FUNDO_REPASSE
            query.append("NULL, ");
            //TODO Será incluído em atividade futura: AFB_DESCRICAO_FUNDO_REPASSE
            query.append("NULL ");

            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_faturamento_beneficio fat on (fat.csa_codigo = csa.csa_codigo AND fat.fat_periodo = :periodo) ");
            query.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("INNER JOIN tb_parcela_desconto_periodo prd ON (prd.ade_codigo = ade.ade_codigo AND prd.prd_data_desconto = :periodo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
            query.append("INNER JOIN tb_tipo_lancamento tla ON (tla.tla_codigo = ade.tla_codigo) ");
            query.append("INNER JOIN tb_contrato_beneficio cbe ON (cbe.cbe_codigo = ade.cbe_codigo) ");
            query.append("INNER JOIN tb_beneficio ben ON (ben.ben_codigo = cbe.ben_codigo) ");
            query.append("INNER JOIN tb_beneficiario bfc ON (bfc.bfc_codigo = cbe.bfc_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = bfc.ser_codigo) ");

            // -- Recupera endereço de cobrança
            query.append("LEFT OUTER JOIN tb_endereco_servidor ens ON (ens.ser_codigo = ser.ser_codigo ");
            query.append(" AND ens.tie_codigo = '").append(TipoEnderecoEnum.COBRANCA.getCodigo()).append("' ");
            query.append(") ");

            query.append("LEFT OUTER JOIN tb_relacionamento_autorizacao rel ON (ade.ade_codigo = rel.ade_codigo_origem ");
            query.append("AND rel.tnt_codigo IN ('" + TextHelper.join(tntCodigosSubsidio, "','") + "')) ");

            query.append("LEFT OUTER JOIN tb_aut_desconto adeSub ON (rel.ade_codigo_destino = adeSub.ade_codigo) ");
            query.append("LEFT OUTER JOIN tb_parcela_desconto_periodo prdSub ON (prdSub.ade_codigo = adeSub.ade_codigo ");
            query.append("AND prdSub.prd_data_desconto = :periodo) ");

            query.append("WHERE 1 = 1 ");

            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND (org.org_codigo IN (:orgCodigos)) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND (org.est_codigo IN (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            }

            //-- exceto lançamentos de subsídio
            query.append("AND tla.tnt_codigo IN ('" + TextHelper.join(tntCodigos, "','") + "') ");

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
