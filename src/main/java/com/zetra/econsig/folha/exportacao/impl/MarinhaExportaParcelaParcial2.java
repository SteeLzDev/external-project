package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

@SuppressWarnings({"java:S1192", "java:S1948"})
public class MarinhaExportaParcelaParcial2 extends MarinhaExportaParcelaParcial {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MarinhaExportaParcelaParcial2.class);

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            if (parametrosExportacao.getAcao().equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) {

                // DESENV-23668: regra de 6 tentativas (enviar inclusão) para contratos que chegaram nos 60 meses (mesmos os de prazo menor que 60, ele vai sendo reimplantado até 60, e então inicia a regra)
                // ou são maior de 60 meses para tentar descontos em 6 tentativas
                // Não podem participar deste fluxo contratos que sejam de decisão judicial.
                // Todos os contratos que estão suspensos há mais de 5 anos, precisam ser encerrados
                // Todos os contratos que tiveram suas 6 tentativas alcançadas deverão ser encerrados
                // Se algum contrato teve pagamento durante as 6 tentativas não deve ser encerrado, porém se faltar pagamento novamente, a regra de tentativas com este
                // contrato se reinicia.
                // Para esta lógica temos uma tabela de controle de tentativas tb_tmp_contr_regra_tentativa e as tabelas temporarias abaixo para encontrar os contratos que chegaram no limite.

                //Tabela temporária com os contratos que serão encerrados
                LOG.info("iniciando regra de 6 tentativas ");
                query.setLength(0);
                query.append("DROP TEMPORARY TABLE IF EXISTS tmp_contratos_encerramento ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tmp_contratos_encerramento( ");
                query.append("ade_codigo varchar(32), ");
                query.append("ade_prazo int, ");
                query.append("periodo_enc_inicial date, ");
                query.append("periodo_ult_parc_paga date, ");
                query.append("periodo_enc_final date, ");
                query.append("PRIMARY KEY (ade_codigo) ");
                query.append(") ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Tabela temporária com o último periodo pago com parcela liquidada
                query.setLength(0);
                query.append("DROP TEMPORARY TABLE IF EXISTS tmp_ult_parcela_paga ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tmp_ult_parcela_paga( ");
                query.append("ade_codigo varchar(32), ");
                query.append("periodo_ult_parc_paga date, ");
                query.append("PRIMARY KEY (ade_codigo) ");
                query.append(") ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Query que lista os contratos que tem prazo menor que 60 e que estão ativos
                // Fazemos o cálculo do periodo maximo até 60 meses, subtraindo 60 do prazo adicionamos a difente em meses a partir da data fim
                LOG.info("Regra 6 tentativas: Mapeando os contratos ativos que o periodo de exportação seja maior que a data fim do contrato prazo MENOR que 60 ");
                query.setLength(0);
                query.append("INSERT INTO tmp_contratos_encerramento (ade_codigo, ade_prazo, periodo_enc_inicial, periodo_enc_final) ");
                query.append("SELECT ade.ade_codigo, ade.ade_prazo, date_add(ade_ano_mes_fim, INTERVAL (60-ade_prazo) month ), date_add(ade_ano_mes_fim, INTERVAL (60-ade_prazo) month ) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE 1=1 ");
                query.append("AND ade.sad_codigo IN ('").append(CodedValues.SAD_DEFERIDA).append("','").append(CodedValues.SAD_EMANDAMENTO).append("') ");
                query.append("AND ade.ade_prazo IS NOT NULL ");
                query.append("AND ade.ade_prazo < 60 ");
                query.append("AND pex.pex_periodo > ade.ade_ano_mes_fim ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Query que insere os contratos ativos com prazo maior que 60
                LOG.info("Regra 6 tentativas: Mapeando os contratos ativos que o periodo de exportação seja maior que a data fim do contrato prazo MAIOR OU IGUAL há 60");
                query.setLength(0);
                query.append("INSERT INTO tmp_contratos_encerramento (ade_codigo, ade_prazo, periodo_enc_inicial, periodo_enc_final) ");
                query.append("SELECT ade.ade_codigo, ade.ade_prazo, ade_ano_mes_fim, ade_ano_mes_fim ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE 1=1 ");
                query.append("AND ade.sad_codigo IN ('").append(CodedValues.SAD_DEFERIDA).append("','").append(CodedValues.SAD_EMANDAMENTO).append("') ");
                query.append("AND ade.ade_prazo IS NOT NULL ");
                query.append("AND ade.ade_prazo >= 60 ");
                query.append("AND pex.pex_periodo > ade.ade_ano_mes_fim ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Deletamos os contratos de servidores excluidos
                LOG.info("Regra 6 tentativas: Deletando contratos de servidores excluidos ");
                query.setLength(0);
                query.append("DELETE tmp.*  ");
                query.append("FROM tmp_contratos_encerramento tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (tmp.ade_codigo = ade.ade_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
                query.append("WHERE rse.srs_codigo IN (").append(TextHelper.sqlJoin(CodedValues.SRS_INATIVOS)).append(") ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Deletamos os contratos que tem decisao judicial
                LOG.info("Regra 6 tentativas: Deletando contratos com decisão judicial ");
                query.setLength(0);
                query.append("DELETE tmp.*  ");
                query.append("FROM tmp_contratos_encerramento tmp ");
                query.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (tmp.ade_codigo = oca.ade_codigo AND oca.tmo_codigo IS NOT NULL) ");
                query.append("INNER JOIN tb_tipo_motivo_operacao tmo ON (tmo.tmo_codigo = oca.tmo_codigo) ");
                query.append("WHERE tmo.tmo_decisao_judicial = 'S'  ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Precisamos verificar qual é o último periodo da parcela se existir , pois se pagou durante a regra de 6 meses, e o periodo da parcela é maior que o limite
                //signfica que pagou durente o perido, então esta data passa a ser o inicial de contagem para a regra de 6 meses, pensando em performance é melhor
                //ter outra tabela preenchida do que colocar subquery, pensando no tamanho do sistema
                LOG.info("Regra 6 tentativas: Verificando a última parcela paga ");
                query.setLength(0);
                query.append("INSERT INTO tmp_ult_parcela_paga ");
                query.append("SELECT prd.ade_codigo, max(prd_data_desconto) ");
                query.append("FROM tmp_contratos_encerramento tmp  ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = tmp.ade_codigo) ");
                query.append("WHERE prd.spd_codigo IN ('").append(CodedValues.SPD_LIQUIDADAMANUAL).append("','").append(CodedValues.SPD_LIQUIDADAFOLHA).append("') ");
                query.append("GROUP BY prd.ade_codigo ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //atualizando a coluna com a data da última parcela paga ou o valor da data limite já calculada
                LOG.info("Regra 6 tentativas: Atualizando última parcela paga ");
                query.setLength(0);
                query.append("UPDATE tmp_contratos_encerramento tmpc ");
                query.append("INNER JOIN tmp_ult_parcela_paga tmp ON (tmpc.ade_codigo = tmp.ade_codigo) ");
                query.append("SET tmpc.periodo_ult_parc_paga = tmp.periodo_ult_parc_paga ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("UPDATE tmp_contratos_encerramento SET periodo_ult_parc_paga = periodo_enc_inicial WHERE periodo_ult_parc_paga IS NULL ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Deletamos os contratos que não tenham chegado ao periodo de encerramento ou que há última parcela paga seja igual a do periodo passado
                //pois significa que o contrato está sendo descontado na folha
                LOG.info("Regra 6 tentativas: Deletando os contratos que não participaram da regra ");
                query.setLength(0);
                query.append("DELETE tmp.* FROM tmp_contratos_encerramento tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE periodo_enc_inicial > pex.pex_periodo OR periodo_ult_parc_paga = pex.pex_periodo_ant ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Os contratos que sobraram temos duas situações:
                //1) Precisamos excluir da tabela de controle que não existem na tabela de contratos para encerramento
                LOG.info("Regra 6 tentativas: Deletando contratos da tabela de contole, pois estão sendo pagos ");
                query.setLength(0);
                query.append("DELETE controle.* ");
                query.append("FROM tb_tmp_contr_regra_tentativa controle ");
                query.append("WHERE NOT EXISTS (SELECT 1 FROM tmp_contratos_encerramento tmp WHERE tmp.ade_codigo = controle.ade_codigo) ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //2) Inserir na tabela de controle todos os contratos que não existem na tabela de contratos para serem encerrados.
                LOG.info("Regra 6 tentativas: Inserindo contratos novos na tabela de controle. ");
                query.setLength(0);
                query.append("INSERT INTO tb_tmp_contr_regra_tentativa ");
                query.append("SELECT tmp.ade_codigo, 0 FROM tmp_contratos_encerramento tmp ");
                query.append("WHERE NOT EXISTS (SELECT 1 FROM tb_tmp_contr_regra_tentativa controle WHERE controle.ade_codigo = tmp.ade_codigo) ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //Atualizamos as tentativas, pois os que tiverem o contato maior que 6 serão excluídos
                LOG.info("Regra 6 tentativas: Atualizando número de tentativas ");
                query.setLength(0);
                query.append("UPDATE tb_tmp_contr_regra_tentativa SET num_tentativa = num_tentativa+1 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //inserir ocorrência de relançamento para os contratos com tentativa menor que 6
                LOG.info("Regra 6 tentativas: Inserindo Ocorrência dos contratos que serão reimplantados, pois ainda não chegaram no limite de tentativas ");
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
                query.append("SELECT DISTINCT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), ");
                query.append("'10', ade.ade_codigo, '1', pex.pex_periodo, pex.pex_periodo, concat('Regra 6 Tentativas: Reimplante Automático da tentativa número ', tmp.num_tentativa) ");
                query.append("FROM tb_tmp_contr_regra_tentativa tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE tmp.num_tentativa < 7 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //inserir ocorrência de encerramento para os contratos com tentativa maior que 6
                LOG.info("Regra 6 tentativas: Inserindo Ocorrência dos contratos que serão encerrados, pois chegaram no limite de tentativas ");
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
                query.append("SELECT DISTINCT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), ");
                query.append("'3', ade.ade_codigo, '1', pex.pex_periodo, pex.pex_periodo, 'Contrato Encerrado por exclusão, alcançou todas as tentativas.' ");
                query.append("FROM tb_tmp_contr_regra_tentativa tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE tmp.num_tentativa > 6 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Atualiza o status dos contratos que serão encerrados
                LOG.info("Regra 6 tentativas: Atualizando o status dos contratos que alcançaram o número de tentativas sad(18)");
                query.setLength(0);
                query.append("UPDATE tb_tmp_contr_regra_tentativa tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("SET ade.sad_codigo='").append(CodedValues.SAD_ENCERRADO).append("' ");
                query.append("WHERE tmp.num_tentativa > 6 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                //deletar os contratos com tentativa maior que 6
                LOG.info("Regra 6 tentativas: Removendo os contratos da tabela de controle que chegou no limite de tentativas ");
                query.setLength(0);
                query.append("DELETE FROM tb_tmp_contr_regra_tentativa WHERE num_tentativa > 6 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
                LOG.info("Regra 6 tentativas: Concluída ");

                // Agora precisamos encerrar todos os contratos suspensos há mais de 5 anos. Para melhorar a performance, iremos criar uma temporária com os contartos suspensos e o oca_data da ocorrência de suspensão
                // para depois fazer o encerramos , pois usar a subquery tratá gargalo na execução da query
                LOG.info("Iniciando exclusão de contratos suspensos há mais de 5 anos ");
                query.setLength(0);
                query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_encerra_suspensos ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tb_tmp_encerra_suspensos( ");
                query.append("ade_codigo varchar(32), ");
                query.append("oca_data date, ");
                query.append("PRIMARY KEY (ade_codigo) ");
                query.append(") ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                LOG.info("Encerra suspensos: Seleciando os contratos suspensos ");
                query.setLength(0);
                query.append("INSERT INTO tb_tmp_encerra_suspensos ");
                query.append("SELECT ade.ade_codigo, max(oca.oca_data) ");
                query.append("FROM tb_aut_desconto ade  ");
                query.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (oca.ade_codigo = ade.ade_codigo AND oca.toc_codigo ='").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("') ");
                query.append("WHERE ade.sad_codigo IN ('").append(CodedValues.SAD_SUSPENSA).append("','").append(CodedValues.SAD_SUSPENSA_CSE).append("') ");
                query.append("GROUP BY ade.ade_codigo ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                LOG.info("Encerra suspensos: Inserindo ocorrẽncias dos contratos suspensos há mais de 5 anos ");
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
                query.append("SELECT DISTINCT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), ");
                query.append("'3', ade.ade_codigo, '1', pex.pex_periodo, pex.pex_periodo, 'Contrato Encerrado por exclusão, suspenso há mais de 5 anos' ");
                query.append("FROM tb_tmp_encerra_suspensos tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE tmp.oca_data <= date_add(CURRENT_TIMESTAMP(), INTERVAL -5 year) ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                LOG.info("Encerra suspensos: Atualizando o status dos contratos para encerrado sad (18)");
                query.setLength(0);
                query.append("UPDATE tb_tmp_encerra_suspensos tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("SET ade.sad_codigo='").append(CodedValues.SAD_ENCERRADO).append("' ");
                query.append("WHERE tmp.oca_data <= date_add(CURRENT_TIMESTAMP(), INTERVAL -5 year) ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
