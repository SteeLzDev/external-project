package com.zetra.econsig.persistence.dao.oracle;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.ArquivoRescisaoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;

/**
 * <p>Title: OracleArquivoRescisaoDAO</p>
 * <p>Description: Rotina de exportação do arquivo de rescisão</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleArquivoRescisaoDAO implements ArquivoRescisaoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleArquivoRescisaoDAO.class);

    @Override
    public void gerarArquivoRescisao(String nomeArqSaida, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida, AcessoSistema responsavel) throws DAOException {
        try {
            // Cria tabela temporária para armazenar os dados necessários para geração do arquivo
            criarTabelaArquivorescisao();

            // Grava os dados na tabela temporária
            if (gravarDadosRescisao() != 0) {
             // Salva os dados da tabela temporária em arquivo
                EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
                salvarArquivoRescisao(nomeArqConfEntrada, nomeArqConfTradutor, escritor);
                
                // Marca os registros de verba rescisória exportados como processados
                atualizarDadosProcessados();
            }else {
                LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.agendamento.movimento.rescisao", responsavel));
            }
        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }   
    }

    private void criarTabelaArquivorescisao() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // Criar tabela temporária para armazenar os dados para o arquivo de rescisão. 
            // A tabela deve ser compatível com a tabela de exportação de movimento financeiro, pois os arquivos XML
            // de configuração podem mapear os mesmos campos do movimento financeiro para envio no arquivo de rescisão.  
            StringBuilder query = new StringBuilder();
            query.append("drop table if exists tb_tmp_exportacao_arquivo_rescisao");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exportacao_arquivo_rescisao (");
            query.append("ade_codigo varchar(32), ");
            query.append("ade_numero bigint(20), ");
            query.append("ade_identificador varchar(40), ");
            query.append("ade_cod_reg char(1), ");
            query.append("ade_inc_margem smallint(6), ");
            query.append("ade_tipo_vlr char(1), ");
            query.append("ade_indice varchar(32), ");
            query.append("ade_data datetime, ");
            query.append("ade_data_ref datetime, ");
            query.append("ade_data_exclusao datetime, ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_ini_folha date, ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_ano_mes_fim_folha date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("ade_prazo_exc int(11), ");
            query.append("ade_prazo_folha int(11), ");
            query.append("ade_prazo int(11), ");
            query.append("ade_prd_pagas_exc int(11), ");
            query.append("ade_prd_pagas int(11), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("ade_vlr_folha decimal(13,2), ");
            query.append("prd_data_desconto date, ");
            query.append("prd_numero varchar(32), ");
            query.append("sad_codigo varchar(32), ");
            query.append("cnv_codigo varchar(32), ");
            query.append("cnv_cod_verba_ref varchar(40), ");
            query.append("cnv_cod_verba varchar(32), ");
            query.append("cnv_prioridade int(11), ");
            query.append("scv_codigo varchar(32), ");
            query.append("svc_codigo varchar(32), ");
            query.append("svc_descricao varchar(100), ");
            query.append("svc_identificador varchar(40), ");
            query.append("svc_prioridade varchar(4), ");
            query.append("csa_codigo varchar(32), ");
            query.append("csa_identificador varchar(40), ");
            query.append("csa_cnpj varchar(19), ");
            query.append("est_codigo varchar(32), ");
            query.append("est_identificador varchar(40), ");
            query.append("est_cnpj varchar(19), ");
            query.append("org_codigo varchar(32), ");
            query.append("org_identificador varchar(40), ");
            query.append("org_cnpj varchar(19), ");
            query.append("ser_codigo varchar(32), ");
            query.append("ser_nome varchar(100), ");
            query.append("ser_primeiro_nome varchar(40), ");
            query.append("ser_ultimo_nome varchar(100), ");
            query.append("ser_nome_meio varchar(100), ");
            query.append("ser_nome_pai varchar(100), ");
            query.append("ser_nome_mae varchar(100), ");
            query.append("ser_cpf varchar(19), ");
            query.append("ser_nacionalidade varchar(40), ");
            query.append("rse_codigo varchar(32), ");
            query.append("rse_matricula varchar(20), ");
            query.append("rse_matricula_inst varchar(20), ");
            query.append("rse_tipo varchar(255), ");
            query.append("rse_associado char(1), ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("pos_codigo varchar(32), ");
            query.append("srs_codigo varchar(32), ");
            query.append("trs_codigo varchar(32), ");
            query.append("oca_periodo date, ");
            query.append("pex_periodo date, ");
            query.append("pex_periodo_ant date, ");
            query.append("pex_periodo_pos date, ");
            query.append("periodo char(6), ");
            query.append("competencia char(6), ");
            query.append("data date, ");
            query.append("autoriza_pgt_parcial char(1), ");
            query.append("capital_devido decimal(13,2), ");
            query.append("saldo_devedor decimal(13,2), ");
            query.append("codigo_folha varchar(40), ");
            query.append("percentual_padrao varchar(255), ");
            query.append("consolida char(1), ");
            query.append("situacao varchar(2), ");
            query.append("key tb_tmp_exportacao_arquivo_rescisao_IDX0 (consolida), ");
            query.append("key tb_tmp_exportacao_arquivo_rescisao_IDX1 (cnv_codigo, rse_codigo, ade_indice, sad_codigo), ");
            query.append("key tb_tmp_exportacao_arquivo_rescisao_IDX2 (sad_codigo), ");
            query.append("key tb_tmp_exportacao_arquivo_rescisao_IDX3 (ade_codigo)");

            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
    
    private int gravarDadosRescisao() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            int linhasAfetadas = 0;
            String query = "";

            query = "insert into tb_tmp_exportacao_arquivo_rescisao (ser_codigo, ser_nome, ser_primeiro_nome , ser_nome_meio , ser_ultimo_nome , ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_associado, pos_codigo, " +
                    "trs_codigo, org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, periodo, " +
                    "competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, rse_margem, rse_margem_rest, " +
                    "rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, ade_numero, ade_identificador, ade_prazo, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc, ade_vlr, " +
                    "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, svc_prioridade, cnv_prioridade, " +
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, " +
                    "situacao, ade_indice, percentual_padrao, capital_devido, saldo_devedor, rse_codigo, org_codigo, est_codigo, svc_codigo, scv_codigo, csa_codigo, cnv_codigo, " +
                    "ade_codigo, sad_codigo, consolida, autoriza_pgt_parcial, oca_periodo, codigo_folha) " +
                    // dados do servidor
                    "select ser.ser_codigo, ser_nome, ser_primeiro_nome , ser_nome_meio , ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    // dados do convenio
                    "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, " +
                    "coalesce(cnv_cod_verba_ref, cnv_cod_verba) as cnv_cod_verba_ref, " +
                    "null as periodo, " +
                    "null as competencia, " +
                    "curdate() as data, " +
                    "null as PEX_PERIODO, " +
                    "null as PEX_PERIODO_ANT, " +
                    "null as PEX_PERIODO_POS, " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados da operação
                    "ade_numero, ade_identificador, ade_prazo, ade_prazo as ade_prazo_exc, ade_prd_pagas, ade_prd_pagas as ade_prd_pagas_exc, ade.ade_vlr, " +
                    "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, " +
                    // dados da folha
                    "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                    // dados de prioridade
                    "svc_prioridade, cnv_prioridade, " +
                    // dados da data de referencia
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, " +
                    "ade_ano_mes_fim, ade_data, null as prd_data_desconto, null as prd_numero, null as situacao, " +
                    // dados indice
                    "ade_indice, " +
                    // dados de percentual limite
                    "'' AS percentual_padrao, " +
                    // Valores financeiros
                    "(case when ade.ade_prazo is null then 1 else ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0) end) * coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) as capital_devido, NULL as saldo_devedor, " +
                    // dados internos
                    "rse.rse_codigo, org.org_codigo, est.est_codigo, svc.svc_codigo, scv_codigo, " +
                    "csa.csa_codigo, cnv.cnv_codigo, ade.ade_codigo, " +
                    "ade.sad_codigo, " +
                    "null as consolida, " +
                    "null as autoriza_pgt_parcial, " +
                    "null as oca_periodo, " +
                    "COALESCE(NULLIF(ORG_FOLHA, ''), COALESCE(NULLIF(EST_FOLHA, ''), NULLIF(CSE_FOLHA, ''))) as codigo_folha " +
                    "from tb_verba_rescisoria_rse vrr " +
                    "inner join tb_registro_servidor rse on (vrr.rse_codigo = rse.rse_codigo) " +
                    "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) " +
                    "inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) " +
                    "inner join tb_relacionamento_autorizacao rad on (rad.ade_codigo_destino = ade.ade_codigo " + 
                                                                    " and rad.tnt_codigo = '" + CodedValues.TNT_VERBA_RESCISORIA + "') " +
                    "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                    "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                    "inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                    "inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) " +
                    "inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo) " +
                    "inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) " +
                    "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) " +
                    "where vrr.svr_codigo = '" + StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo() + "' " + 
                    "and vrr.vrr_processado = 'N' " + 
                    "and ade.sad_codigo = '" + CodedValues.SAD_DEFERIDA + "' "
                    ;

            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            return linhasAfetadas;

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
    
    private void salvarArquivoRescisao(String nomeArqConfEntrada, String nomeArqConfTradutor, Escritor escritor) throws DAOException {
        String queryArquivo = "SELECT * FROM tb_tmp_exportacao_arquivo_rescisao ";
        try {
            Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntrada, queryArquivo);
            Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
    
    private void atualizarDadosProcessados() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            int linhasAfetadas = 0;
            String query = "";

            query = "update tb_verba_rescisoria_rse " +
                    "set vrr_processado = 'S', " +
                    "vrr_data_fim = now() " +
                    "where svr_codigo = '" + StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo() + "' " + 
                    "and vrr_processado = 'N' "
                    ;
            
            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
