package com.zetra.econsig.folha.exportacao.impl;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TRERJ</p>
 * <p>Description: Implementações específicas para TRERJ - Tribunal Regional Eleitoral do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TRERJ extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TRERJ.class);

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {

        if(ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, responsavel) || !ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.movimento.parametros.nao.configurados", responsavel));
            throw new ExportaMovimentoException("mensagem.erro.movimento.parametros.nao.configurados",responsavel);
        }

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        try {
            int rows = 0;
            int linhasAlteracao = 0;

            // Quando o contrato é alteração e essa alteração ela é tanto para boleto quando para folha existem algumas regras que precisam ser verificadas
            // 1. Se o contrato foi alterado de Boleto para Folha,na exportação aparece a letra "A", porém deve-se então gerar uma linha de BE para o boleto e uma nova linha "I" a partir da alteração
            // 2. Se o contrato foi alterado de Folha para Boleto, na exportação também aparece a letra "A", porém deve-se então gerar uma linha "E" para a folha e uma nova linha "BI" a partir da alteração
            // Essa verificação deve ser feita pela tb_ocorrencia_dados_ade dentro do período
            // 3. Se o contrato foi alterado o valor e também a forma de pagamento (BOLETO para FOLHA) gerar a linha BE para o boleto com o valor antigo e a linha I com o valor novo a partir da linha de alteração
            // 4. Alterado somente o valor do contrato caso for BOLETO gerar BA, caso for FOLHA, gerar A

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_contratos_saude_alteracao ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_contratos_saude_alteracao ");
            query.append("SELECT tmp.* FROM tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("WHERE svc.nse_codigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
            query.append("AND tmp.situacao='A' ");
            LOG.debug(query.toString());
            linhasAlteracao = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_contratos_saude_exportacao ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            //Neste momento crio uma tabela tabela que contenrá todos as linhas do movimento de saúde para depois irem para a tb_tmp_exportacao_ordenada, faço isso para tratar o contador que é PK da tabela
            query.append("CREATE TEMPORARY TABLE tb_tmp_contratos_saude_exportacao ");
            query.append("SELECT tmp.* FROM tb_tmp_exportacao_ordenada tmp ");
            query.append("WHERE contador='1' ");
            LOG.debug(query.toString());
            linhasAlteracao = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_contratos_boleto_alteracao ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_contratos_boleto_alteracao (");
            query.append("ADE_CODIGO varchar(32) NOT NULL, ");
            query.append("ODA_DATA datetime NOT NULL, ");
            query.append("PERIODO date NOT NULL, ");
            query.append("SITUACAO_INCLUSAO varchar(2) DEFAULT NULL, ");
            query.append("SITUACAO_EXCLUSAO varchar(2) DEFAULT NULL, ");
            query.append("ADE_VLR_ANTIGO decimal(13,2) DEFAULT NULL, ");
            query.append("PRIMARY KEY (ADE_CODIGO)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_contratos_folha_alteracao ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_contratos_folha_alteracao (");
            query.append("ADE_CODIGO varchar(32) NOT NULL, ");
            query.append("ODA_DATA datetime NOT NULL, ");
            query.append("PERIODO date NOT NULL, ");
            query.append("SITUACAO_INCLUSAO varchar(2) DEFAULT NULL, ");
            query.append("SITUACAO_EXCLUSAO varchar(2) DEFAULT NULL, ");
            query.append("ADE_VLR_ANTIGO decimal(13,2) DEFAULT NULL, ");
            query.append("PRIMARY KEY (ADE_CODIGO)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Query para insert nas tabelas de apoio para pagamentos de boleto e folha, precisamos saber qual foi a última alteração realizada dentro do período, por isso a criação e insert nessas tabelas
            StringBuilder complemento = new StringBuilder();
            complemento.append("FROM tb_tmp_contratos_saude_alteracao tmp ");
            complemento.append("INNER JOIN tb_aut_desconto ade ON (tmp.ade_numero = ade.ade_numero) ");
            complemento.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            complemento.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            complemento.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            complemento.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
            complemento.append("INNER JOIN tb_ocorrencia_dados_ade oda ON (oda.ade_codigo = ade.ade_codigo) ");
            complemento.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (oca.ade_codigo = ade.ade_codigo and pex.pex_periodo = oca.oca_periodo) ");
            complemento.append("WHERE oda.tda_codigo = '").append(CodedValues.TDA_FORMA_PAGAMENTO).append("' ");
            complemento.append("AND oda.oda_data BETWEEN pex.pex_data_ini AND pex.pex_data_fim ");
            complemento.append("AND oda.toc_codigo='").append(CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS).append("' ");
            complemento.append("AND oca.toc_codigo='").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");

            // Query para insert nas tabelas de apoio para pagamentos de boleto e folha, precisamos saber quando houve somente alteração de valor sem alteração de forma de pagamento
            StringBuilder complementoAlteracaoSomenteValor = new StringBuilder();
            complementoAlteracaoSomenteValor.append("FROM tb_tmp_contratos_saude_alteracao tmp ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_aut_desconto ade ON (tmp.ade_numero = ade.ade_numero) ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) ");
            complementoAlteracaoSomenteValor.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (oca.ade_codigo = ade.ade_codigo and pex.pex_periodo = oca.oca_periodo) ");
            complementoAlteracaoSomenteValor.append("WHERE NOT EXISTS (SELECT oda.ade_codigo FROM tb_ocorrencia_dados_ade oda WHERE oda.tda_codigo = '").append(CodedValues.TDA_FORMA_PAGAMENTO).append("' ");
            complementoAlteracaoSomenteValor.append("AND oda.oda_data BETWEEN pex.pex_data_ini AND pex.pex_data_fim AND ade.ade_codigo = oda.ade_codigo ");
            complementoAlteracaoSomenteValor.append("AND oda.toc_codigo='").append(CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS).append("') ");
            complementoAlteracaoSomenteValor.append("AND oca.toc_codigo='").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");

            // Quando o contrato é de inclusão e do tipo boleto, deve virar BI
            // BI - exportação de linha de inclusão de contrato (adesão de serviço de plano de saúde) VIA BOLETO. NÃO CONSOME MARGEM
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("SET tmp.situacao= 'BI' ");
            query.append("WHERE tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
            query.append("AND svc.nse_codigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
            query.append("AND tmp.situacao='I' ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            // Quando o contrato é de exclusão e do tipo boleto, deve virar BE
            // BE - exportação de linha de exclusão de contrato de plano de saúde VIA BOLETO.
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("SET tmp.situacao= 'BE' ");
            query.append("WHERE tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
            query.append("AND svc.nse_codigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
            query.append("AND tmp.situacao='E' ");
            // Não existe alteração na forma de pagamento para garantir o estado original da situação.
            query.append("AND NOT EXISTS (SELECT oda.ade_codigo FROM tb_ocorrencia_dados_ade oda WHERE oda.tda_codigo = '").append(CodedValues.TDA_FORMA_PAGAMENTO).append("' ");
            query.append("AND oda.oda_data BETWEEN pex.pex_data_ini AND pex.pex_data_fim AND ade.ade_codigo = oda.ade_codigo ");
            query.append("AND oda.toc_codigo='").append(CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS).append("') ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            // Quando é exclusão precisamos verificar o último estado do contrato alterado e colocar nele o valor de alteração da forma de pagamento anterior
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("SET tmp.situacao= case when  ");
            query.append(" tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' then 'E' else 'BE' end, ");
            query.append("tmp.dad_valor_55 = case when ");
            query.append("tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' then '").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' else '").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' end ");
            query.append("WHERE tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
            query.append("AND svc.nse_codigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
            query.append("AND tmp.situacao='E' ");
            query.append("AND EXISTS (SELECT oda.ade_codigo FROM tb_ocorrencia_dados_ade oda WHERE oda.tda_codigo = '").append(CodedValues.TDA_FORMA_PAGAMENTO).append("' ");
            query.append("AND oda.oda_data BETWEEN pex.pex_data_ini AND pex.pex_data_fim AND ade.ade_codigo = oda.ade_codigo ");
            query.append("AND oda.toc_codigo='").append(CodedValues.TOC_ALTERACAO_DADOS_ADICIONAIS).append("') ");

            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            if (linhasAlteracao > 0) {
                // Limpa a tabela tb_tmp_contratos_saude_exportacao pois ela foi criada apenas para conter os arquivos finais de saúde.
                query.append("DELETE tmp.* FROM tb_tmp_contratos_saude_exportacao tmp ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                // Todos os contratos de alteração que são de plano de saúde devem ser deletados, pois irão ser tratados de acordo com a alteração da forma de pagamento.
                query.append("DELETE tmp.* FROM tb_tmp_exportacao_ordenada tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
                query.append("WHERE svc.nse_codigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("') ");
                query.append("AND tmp.situacao='A' ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //Preenchendo tabelas de apoio para alterações de forma de pagamento sem alteração de valor
                query.append("INSERT INTO tb_tmp_contratos_boleto_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao) ");
                query.append("SELECT ade.ADE_CODIGO, max(oda.oda_data), pex.pex_periodo, 'E', 'BI' ");
                query.append(complemento.toString());
                query.append("AND oda.oda_valor_novo = '").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_folha_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao) ");
                query.append("SELECT ade.ADE_CODIGO, max(oda.oda_data), pex.pex_periodo, 'BE', 'I' ");
                query.append(complemento.toString());
                query.append("AND oda.oda_valor_novo = '").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                //Tratando as tabelas para não terem contratos duplicados em cada tabela, ou seja, tiveram alteração de forma de pagamento mais de uma vez dentro do mesmo período, assim, é preciso deixar
                //a última alteração em cada tabela.
                query.append("DELETE tmpb.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data < tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("DELETE tmpf.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data > tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                // 1. Tratando linhas de exclusão e inclusão de contratos que modificação de Boleto para folha, criando linhas de BE e I no movimento.
                query.append("UPDATE tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_exclusao ");
                query.append(", tmpSaude.dad_valor_55 ='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("UPDATE tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_inclusao ");
                query.append(", tmpSaude.dad_valor_55 ='").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                // 2. Tratando linhas de exclusão e inclusão de contratos que modificação de Folha para BOleto, criando linhas de E e BI no movimento.
                query.append("UPDATE tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_exclusao ");
                query.append(", tmpSaude.dad_valor_55 ='").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("UPDATE tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_inclusao ");
                query.append(", tmpSaude.dad_valor_55 ='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //Reinicio aqui as tabelas de apoio, pois agora são contratos que tiveram o valor e a forma de pagamento alterados.
                query.append("DELETE FROM tb_tmp_contratos_boleto_alteracao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("DELETE FROM tb_tmp_contratos_folha_alteracao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_boleto_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao,ade_vlr_antigo) ");
                query.append("SELECT ade.ADE_CODIGO, max(oda.oda_data), pex.pex_periodo, 'E', 'BI', oca.oca_ade_vlr_ant ");
                query.append(complemento.toString());
                query.append("AND oda.oda_valor_novo = '").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NOT NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_folha_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao,ade_vlr_antigo) ");
                query.append("SELECT ade.ADE_CODIGO, max(oda.oda_data), pex.pex_periodo, 'BE', 'I', oca.oca_ade_vlr_ant ");
                query.append(complemento.toString());
                query.append("AND oda.oda_valor_novo = '").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NOT NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                //Tratando as tabelas para não terem contratos duplicados em cada tabela, ou seja, tiveram alteração de forma de pagamento mais de uma vez dentro do mesmo período, assim, é preciso deixar
                //a última alteração em cada tabela.
                query.append("DELETE tmpb.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data < tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("DELETE tmpf.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data > tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //DESENV-18037 Não é para enviar mais linhas de exclusão e inclusão para boleto ou folha quando é somente alteração de valor, assim é para enviar
                // Pagamento Boleto BA e pagamento folha A quando for somente alteração de valor.
                query.append("DELETE FROM tb_tmp_contratos_boleto_alteracao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("DELETE FROM tb_tmp_contratos_folha_alteracao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_boleto_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao,ade_vlr_antigo) ");
                query.append("SELECT ade.ADE_CODIGO, max(oca.oca_data), pex.pex_periodo, 'BA', 'BA', oca.oca_ade_vlr_ant ");
                query.append(complementoAlteracaoSomenteValor.toString());
                query.append("AND tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_BOLETO).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NOT NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_folha_alteracao (ade_codigo,oda_data,periodo,situacao_exclusao,situacao_inclusao,ade_vlr_antigo) ");
                query.append("SELECT ade.ADE_CODIGO, max(oca.oca_data), pex.pex_periodo, 'A', 'A', oca.oca_ade_vlr_ant ");
                query.append(complementoAlteracaoSomenteValor.toString());
                query.append("AND tmp.dad_valor_55='").append(CodedValues.FORMA_PAGAMENTO_FOLHA).append("' ");
                query.append("AND oca.oca_ade_vlr_ant IS NOT NULL ");
                query.append("GROUP BY ade.ade_codigo, periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                //Tratando as tabelas para não terem contratos duplicados em cada tabela, ou seja, tiveram alteração de forma de pagamento mais de uma vez dentro do mesmo período, assim, é preciso deixar
                //a última alteração em cada tabela.
                query.append("DELETE tmpb.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data < tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("DELETE tmpf.* FROM tb_tmp_contratos_boleto_alteracao tmpb ");
                query.append("INNER JOIN tb_tmp_contratos_folha_alteracao tmpf ON (tmpb.ade_codigo = tmpf.ade_codigo) ");
                query.append("WHERE tmpb.oda_data > tmpf.oda_data ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //5. Criando linhas de Alteração BA
                query.append("UPDATE tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_inclusao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_boleto_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //6. Criando linhas de Alteração para Folha, A
                query.append("UPDATE tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                query.append("SET tmpSaude.situacao = tmp.situacao_inclusao ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_contratos_saude_exportacao ");
                query.append("SELECT tmpSaude.* FROM tb_tmp_contratos_folha_alteracao tmp ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = tmp.ade_codigo) ");
                query.append("INNER JOIN tb_tmp_contratos_saude_alteracao tmpSaude ON (ade.ade_numero = tmpSaude.ade_numero) ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                //Coloco todos os contadores da tb_tmp_exportacao_ordenada para frente para tratar os contadores das linhas de saúde, depois junta-los a ordenada e reordenar os contadores
                ajustarContador(false);
                ajustarContador(true, true);

                //Insiro todas as linhas da exportação de saúde na tabela final para exportação
                query.append("INSERT INTO tb_tmp_exportacao_ordenada ");
                query.append("SELECT * FROM tb_tmp_contratos_saude_exportacao tmp ");
                LOG.debug(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.debug("Linhas afetadas: " + rows);
                query.setLength(0);

                ajustarContador(true);

            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private void ajustarContador(boolean inicio) throws ExportaMovimentoException {
        ajustarContador(inicio, false);
    }

    private void ajustarContador(boolean inicio, boolean tmpExportacaoOrdenada) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            int rows = 1;

            if (!inicio && !tmpExportacaoOrdenada) {
                query.append("SELECT max(ade_numero) as count FROM tb_tmp_exportacao_ordenada");
                rows = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);

            } else if (inicio && tmpExportacaoOrdenada) {
                query.append("SELECT max(contador) as count FROM tb_tmp_exportacao_ordenada");
                rows = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);
            }

            query.setLength(0);
            query.append("SET @rownum :=").append(rows).append(";");
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            if (!tmpExportacaoOrdenada) {
                query.append("UPDATE tb_tmp_exportacao_ordenada ");
            } else {
                query.append("UPDATE tb_tmp_contratos_saude_exportacao ");
            }
            query.append("SET contador = @rownum := @rownum + 1 ");
            query.append("ORDER BY field (situacao, 'E', 'BE', 'A', 'BI', 'I') ");
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
