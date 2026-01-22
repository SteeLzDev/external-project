package com.zetra.econsig.folha.exportacao.impl;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Infraero</p>
 * <p>Description: Implementações específicas para a INFRAERO.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 30469 $
 * $Date: 2020-09-25 18:35:51 -0300 (sex, 25 set 2020) $
 */
public class Infraero extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Infraero.class);

    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (parametrosExportacao.getAcao().equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
            ajustarNumeroParcelas(parametrosExportacao.getAcao(), false, responsavel);
        }
    }

    @Override
    public void posProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        ajustarNumeroParcelas(parametrosExportacao.getAcao(), true, responsavel);
    }

    private void ajustarNumeroParcelas (String acao, boolean minPrdNumero, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            final StringBuilder query = new StringBuilder();

            // DESENV-14752 - Necessário setar na prd_numero criada o prd_numero mais antiga rejeitada ou maior rejeitado, de acordo com a ação
            // (exportar ou reexportar) e na tb_parcela_desconto setar o valor da prd_numero criada.

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_prd_numero");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_prd_numero (ID int, ADE_CODIGO varchar(32), PRD_NUMERO smallint, primary key (ID), unique key idx_ade (ADE_CODIGO))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            String rownum = "SET @rownum := 0; ";
            LOG.debug(rownum);
            jdbc.update(rownum, queryParams);

            // Crio uma tabela temporária com os ade_codigos e prd_numero minimo que estão na exportação, pois no update não pode ter agrupamento, e como
            // utilizo a function min() do mysql, não seria possível.
            query.setLength(0);
            query.append("INSERT INTO tb_prd_numero (ID, ADE_CODIGO, PRD_NUMERO) ");
            query.append("SELECT @rownum:=@rownum+1 as ID, ade.ade_codigo AS ADE_CODIGO, ").append(minPrdNumero ? "min(prd.prd_numero)" : "max(prd.prd_numero)").append(" AS PRD_NUMERO ");
            query.append("FROM tb_parcela_desconto_periodo pdp ");
            query.append("INNER JOIN tb_aut_desconto ade on (ade.ade_codigo = pdp.ade_codigo) ");
            query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo AND prd.spd_codigo NOT IN ( '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') AND prd.prd_vlr_realizado != prd.prd_vlr_previsto) " );
            query.append("WHERE ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "' , '")).append("') ");
            query.append("AND ade.ade_prd_pagas != ade.ade_prazo ");
            query.append("AND ade.ade_ano_mes_fim < curdate() ");
            query.append("GROUP BY ade.ade_codigo");
            LOG.debug(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            // Aqui acontece todas as modificações dos prd_numero
            // prd_numero do contrato que vai para a folha recebe o que o prd_numero mínimo rejeitado da parcela_desconto
            // prd_numero mínimo rejeitado recebe o prd_numero que iria para a folha
            // prd_numero da ocorrencia da parcela recebe o prd_numero que iria para a folha
            // prd_numero da ocorrencia da parcela periodo recebe o prd_numero que mínimo rejeito da parcela_desconto
            query.setLength(0);
            query.append("UPDATE tb_parcela_desconto_periodo pdp ");
            query.append("INNER JOIN tb_prd_numero mpn ON (mpn.ade_codigo = pdp.ade_codigo) ");
            query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = mpn.ade_codigo AND prd.prd_numero = mpn.prd_numero) ");
            query.append("SET prd.prd_numero = pdp.prd_numero, pdp.prd_numero = mpn.prd_numero, mpn.prd_numero = pdp.prd_numero");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            if (!acao.equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
                query.setLength(0);
                query.append("SELECT COUNT(*) as count FROM tb_prd_numero");
                LOG.debug(query.toString());
                int count = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);

                for (int i = 1; i <= count; i++) {
                    // Inserir as ocorrências das parcelas novas criadas, informando que elas foram modificadas
                    query.setLength(0);
                    query.append("INSERT INTO tb_ocorrencia_parcela_periodo (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, PRD_CODIGO, OCP_DATA, OCP_OBS) ");
                    query.append("SELECT :ocpCodigo, '").append(CodedValues.TOC_AVISO).append("', :usuCodigo, prd.prd_codigo, current_timestamp(), ");
                    query.append("concat(:obs, ' ', mpn.prd_numero) ");
                    query.append("FROM tb_parcela_desconto_periodo prd ");
                    query.append("INNER JOIN tb_prd_numero mpn ON (prd.ade_codigo = mpn.ade_codigo) ");
                    query.append("WHERE mpn.id = :id");
                    queryParams.addValue("id", i);
                    queryParams.addValue("ocpCodigo", DBHelper.getNextId());
                    queryParams.addValue("usuCodigo", responsavel.getUsuCodigo());
                    queryParams.addValue("obs", ApplicationResourcesHelper.getMessage("mensagem.informacao.ocorrencia.parcela.refenrente.outro.prd", responsavel));
                    jdbc.update(query.toString(), queryParams);
                }
            }
        } catch (final DataAccessException | MissingPrimaryKeyException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
