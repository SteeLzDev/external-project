package com.zetra.econsig.persistence.dao.generic;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: GenericRelatorioDAO</p>
 * <p>Description: Implementacao Genérica do DAO de Relatórios. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericRelatorioDAO implements RelatorioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericRelatorioDAO.class);

    /**
     * Monta query de geração do relatório de integração reformatado à partir das tabelas
     * de retorno, previamente carregadas na importação.
     * @param csaCodigo
     * @param chaveSeparador
     * @param separarRelIntegracao
     * @param sequencial
     * @param nomeArqEntrada
     * @param queryParams
     * @return
     */
    @Override
    public String montaQueryRelatorioIntegracao(String csaCodigo, String chaveSeparador, String separarRelIntegracao, int sequencial, String nomeArqEntrada, MapSqlParameterSource queryParams) {
        // Define os parâmetros da consulta
        queryParams.addValue("csaCodigo", csaCodigo);
        queryParams.addValue("nomeArqEntrada", nomeArqEntrada);
        queryParams.addValue("sequencial", sequencial);

        // Verifica se o relatório de integração é separado
        final String tipoSeparador = (!CodedValues.SEPARA_REL_INTEGRACAO_NAO.equals(separarRelIntegracao) ? separarRelIntegracao : null);
        if (!TextHelper.isNull(tipoSeparador)) {
            queryParams.addValue("chaveSeparador", chaveSeparador);
        }

        final StringBuilder query = new StringBuilder();

        // Linhas que não foram utilizadas para pagamento de nenhuma parcela
        query.append("select :sequencial as sequencial, art.nome_arquivo, 2 as ordem, art.id_linha, art.linha, ");
        query.append("null as cse_identificador, art.est_identificador, art.org_identificador, art.csa_identificador, art.svc_identificador, art.cnv_cod_verba, ");
        query.append("null as cse_nome, null as est_nome, null as org_nome, csa.csa_nome, null as svc_descricao, null as nse_codigo, null as nse_descricao, ");
        query.append("null as cse_cnpj, null as est_cnpj, null as org_cnpj, csa.csa_cnpj, ");
        query.append("csa.csa_nro_contrato, csa.csa_nro_bco, csa.csa_nro_age, csa.csa_nro_cta, csa.csa_dig_cta, ");
        query.append("art.rse_matricula, null as rse_matricula_inst, art.ser_nome, art.ser_cpf, null as ser_data_nasc, null as srs_codigo, null as rse_tipo, null as rse_data_admissao, ");
        query.append("pos.pos_identificador, pos.pos_descricao, ");
        query.append("art.ade_numero, null as ade_identificador, art.ade_indice, art.ade_cod_reg, art.ade_prazo, art.ade_prd_pagas, art.prd_vlr_realizado as ade_vlr, null as ade_vlr_liquido, art.ade_data, ");
        query.append("art.ade_ano_mes_ini, art.ade_ano_mes_fim, art.ade_ano_mes_ini as ade_ano_mes_ini_ref, art.ade_ano_mes_fim as ade_ano_mes_fim_ref, art.ade_ano_mes_ini as ade_ano_mes_ini_folha, art.ade_ano_mes_fim as ade_ano_mes_fim_folha, ");
        query.append("null as prd_numero, art.prd_vlr_realizado as prd_vlr_previsto, art.prd_vlr_realizado, art.ano_mes_desconto as prd_data_desconto, art.prd_data_realizado, art.ocp_obs, art.spd_codigo, art.tde_codigo ");
        query.append("from tb_arquivo_retorno art ");
        query.append("inner join tb_consignataria csa on (art.csa_codigo = csa.csa_codigo) ");
        query.append("left outer join tb_posto_registro_servidor pos on (art.pos_identificador = pos.pos_identificador) ");
        query.append("where art.nome_arquivo = :nomeArqEntrada ");
        query.append("and art.csa_codigo = :csaCodigo ");
        if (!TextHelper.isNull(tipoSeparador)) {
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_VERBA)) {
                query.append("and (art.cnv_cod_verba is null or art.cnv_cod_verba = :chaveSeparador) ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ORGAO)) {
                query.append("and (art.org_identificador is null or art.org_identificador = :chaveSeparador) ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO)) {
                query.append("and (art.est_identificador is null or art.est_identificador = :chaveSeparador) ");
            }
        }
        // Não foi usada para pagar nenhuma parcela
        query.append("and not exists (select 1 from tb_arquivo_retorno_parcela arp where art.nome_arquivo = arp.nome_arquivo and art.id_linha = arp.id_linha) ");
        if (!TextHelper.isNull(tipoSeparador)) {
            // E contém os requisitos para o filtro de separação passado
            query.append("and exists (select 1 from tb_convenio cnv ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
            query.append("where art.cnv_cod_verba = cnv.cnv_cod_verba ");
            query.append("and art.csa_codigo = cnv.csa_codigo ");
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_VERBA)) {
                query.append("and cnv.cnv_cod_verba = :chaveSeparador ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ORGAO)) {
                query.append("and org.org_identificador = :chaveSeparador ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO)) {
                query.append("and est.est_identificador = :chaveSeparador ");
            }
            query.append(") ");
        }

        query.append("union all ");

        // Linhas que foram utilizadas para pagamento de uma ou mais parcelas
        query.append("select :sequencial as sequencial, art.nome_arquivo, 1 as ordem, art.id_linha, art.linha, ");
        query.append("cse.cse_identificador, est.est_identificador, org.org_identificador, csa.csa_identificador, svc.svc_identificador, cnv.cnv_cod_verba, ");
        query.append("cse.cse_nome, est.est_nome, org.org_nome, csa.csa_nome, svc.svc_descricao, svc.nse_codigo, nse.nse_descricao, ");
        query.append("cse.cse_cnpj, est.est_cnpj, org.org_cnpj, csa.csa_cnpj, ");
        query.append("csa.csa_nro_contrato, csa.csa_nro_bco, csa.csa_nro_age, csa.csa_nro_cta, csa.csa_dig_cta, ");
        query.append("rse.rse_matricula, rse.rse_matricula_inst, ser.ser_nome, ser.ser_cpf, ser.ser_data_nasc, rse.srs_codigo, rse.rse_tipo, rse.rse_data_admissao, ");
        query.append("pos.pos_identificador, pos.pos_descricao, ");
        query.append("ade.ade_numero, ade.ade_identificador, ade.ade_indice, ade.ade_cod_reg, ade.ade_prazo, ade.ade_prd_pagas, ade.ade_vlr, ade.ade_vlr_liquido, ade.ade_data, ");
        query.append("ade.ade_ano_mes_ini, ade.ade_ano_mes_fim, ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_fim_folha, ");
        query.append("prd.prd_numero, prd.prd_vlr_previsto, prd.prd_vlr_realizado, prd.prd_data_desconto, prd.prd_data_realizado, art.ocp_obs, prd.spd_codigo, prd.tde_codigo ");
        query.append("from tb_arquivo_retorno art ");
        query.append("inner join tb_arquivo_retorno_parcela arp on (art.nome_arquivo = arp.nome_arquivo and art.id_linha = arp.id_linha) ");
        query.append("inner join tb_parcela_desconto prd on (arp.ade_codigo = prd.ade_codigo and arp.prd_numero = prd.prd_numero) ");
        query.append("inner join tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) ");
        query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        query.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        query.append("inner join tb_natureza_servico nse on (svc.nse_codigo = nse.nse_codigo) ");
        query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        query.append("inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        query.append("inner join tb_consignante cse on (est.cse_codigo = cse.cse_codigo) ");
        query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
        query.append("left outer join tb_posto_registro_servidor pos on (rse.pos_codigo = pos.pos_codigo) ");
        query.append("where art.nome_arquivo = :nomeArqEntrada ");
        query.append("and art.csa_codigo = :csaCodigo ");
        if (!TextHelper.isNull(tipoSeparador)) {
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_VERBA)) {
                query.append("and cnv.cnv_cod_verba = :chaveSeparador ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ORGAO)) {
                query.append("and org.org_identificador = :chaveSeparador ");
            }
            if (tipoSeparador.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO)) {
                query.append("and est.est_identificador = :chaveSeparador ");
            }
        }

        // ORDER BY NOME_ARQUIVO, ORDEM, ID_LINHA
        query.append("order by 2, 3, 4 ");

        LOG.trace(query.toString());
        return query.toString();
    }

    @Override
	public String montaQueryRelatorioEstatistico(TransferObject criterio, Map<String, Object> parameters) {
        final StringBuilder query = new StringBuilder();

        final String[] referencias = new String[6];
        final List<String> refList = (List<String>) parameters.get("REFERENCIAS");
        for (int i = 0; i < refList.size(); i++) {
            referencias[i] = refList.get(i);
        }

        final String tableName = (String) parameters.get("TABLE_NAME");

        query.append("SELECT DISTINCT csa_identificador, csa_nome, cnv_cod_verba, svc_descricao, ord.ore_sequencia, trim(cast(ord.ore_codigo as char(2))) as ore_codigo, ord.ore_codigo as ordem, ord.ore_descricao, ");
        query.append("coalesce(tmp01.ree_valor, case when tmp01.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor1, ");
        query.append("coalesce(tmp02.ree_valor, case when tmp02.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor2, ");
        query.append("coalesce(tmp03.ree_valor, case when tmp03.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor3, ");
        query.append("coalesce(tmp04.ree_valor, case when tmp04.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor4, ");
        query.append("coalesce(tmp05.ree_valor, case when tmp05.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor5, ");
        query.append("coalesce(tmp06.ree_valor, case when tmp06.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor6, ");
        query.append("cast(coalesce(tmp01.ree_qtd, 0) as decimal(13, 2)) AS valor7, ");
        query.append("cast(coalesce(tmp02.ree_qtd, 0) as decimal(13, 2)) AS valor8, ");
        query.append("cast(coalesce(tmp03.ree_qtd, 0) as decimal(13, 2)) AS valor9, ");
        query.append("cast(coalesce(tmp04.ree_qtd, 0) as decimal(13, 2)) AS valor10, ");
        query.append("cast(coalesce(tmp05.ree_qtd, 0) as decimal(13, 2)) AS valor11, ");
        query.append("cast(coalesce(tmp06.ree_qtd, 0) as decimal(13, 2)) AS valor12 ");
        query.append("FROM tb_consignataria csa ");
        query.append("INNER JOIN tb_convenio cnv ON (csa.csa_codigo = cnv.csa_codigo) ");
        query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
        query.append("CROSS JOIN tb_ordem_relatorio_estatistico ord ");

        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp01a ON (tmp01a.ree_nome = '").append(tableName).append("' AND tmp01a.ree_referencia = '").append(referencias[0]).append("' AND tmp01a.ree_ordem = 1 AND tmp01a.ree_csa = csa.csa_identificador AND tmp01a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp02a ON (tmp02a.ree_nome = '").append(tableName).append("' AND tmp02a.ree_referencia = '").append(referencias[1]).append("' AND tmp02a.ree_ordem = 1 AND tmp02a.ree_csa = csa.csa_identificador AND tmp02a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp03a ON (tmp03a.ree_nome = '").append(tableName).append("' AND tmp03a.ree_referencia = '").append(referencias[2]).append("' AND tmp03a.ree_ordem = 1 AND tmp03a.ree_csa = csa.csa_identificador AND tmp03a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp04a ON (tmp04a.ree_nome = '").append(tableName).append("' AND tmp04a.ree_referencia = '").append(referencias[3]).append("' AND tmp04a.ree_ordem = 1 AND tmp04a.ree_csa = csa.csa_identificador AND tmp04a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp05a ON (tmp05a.ree_nome = '").append(tableName).append("' AND tmp05a.ree_referencia = '").append(referencias[4]).append("' AND tmp05a.ree_ordem = 1 AND tmp05a.ree_csa = csa.csa_identificador AND tmp05a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp06a ON (tmp06a.ree_nome = '").append(tableName).append("' AND tmp06a.ree_referencia = '").append(referencias[5]).append("' AND tmp06a.ree_ordem = 1 AND tmp06a.ree_csa = csa.csa_identificador AND tmp06a.ree_VERBA = cnv_cod_verba) ");

        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp01 ON (tmp01.ree_nome = '").append(tableName).append("' AND tmp01.ree_referencia = '").append(referencias[0]).append("' AND tmp01.ree_ordem = ord.ore_codigo AND tmp01.ree_csa = csa.csa_identificador AND tmp01.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp02 ON (tmp02.ree_nome = '").append(tableName).append("' AND tmp02.ree_referencia = '").append(referencias[1]).append("' AND tmp02.ree_ordem = ord.ore_codigo AND tmp02.ree_csa = csa.csa_identificador AND tmp02.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp03 ON (tmp03.ree_nome = '").append(tableName).append("' AND tmp03.ree_referencia = '").append(referencias[2]).append("' AND tmp03.ree_ordem = ord.ore_codigo AND tmp03.ree_csa = csa.csa_identificador AND tmp03.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp04 ON (tmp04.ree_nome = '").append(tableName).append("' AND tmp04.ree_referencia = '").append(referencias[3]).append("' AND tmp04.ree_ordem = ord.ore_codigo AND tmp04.ree_csa = csa.csa_identificador AND tmp04.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp05 ON (tmp05.ree_nome = '").append(tableName).append("' AND tmp05.ree_referencia = '").append(referencias[4]).append("' AND tmp05.ree_ordem = ord.ore_codigo AND tmp05.ree_csa = csa.csa_identificador AND tmp05.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp06 ON (tmp06.ree_nome = '").append(tableName).append("' AND tmp06.ree_referencia = '").append(referencias[5]).append("' AND tmp06.ree_ordem = ord.ore_codigo AND tmp06.ree_csa = csa.csa_identificador AND tmp06.ree_VERBA = cnv_cod_verba) ");
        query.append("WHERE ord.ore_codigo <> '2' AND (tmp01a.ree_CSA IS NOT NULL OR tmp02a.ree_CSA IS NOT NULL OR tmp03a.ree_CSA IS NOT NULL OR tmp04a.ree_CSA IS NOT NULL OR tmp05a.ree_CSA IS NOT NULL OR tmp06a.ree_CSA IS NOT NULL) ");
        query.append("AND ord.ore_ativo = 1 ");
        query.append("ORDER BY csa_identificador, csa_nome, cnv_cod_verba, ord.ore_sequencia, ord.ore_codigo");
        LOG.debug(query.toString());

        return query.toString();
    }

}
