package com.zetra.econsig.persistence.dao.oracle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.RelatorioConcessoesDeBeneficiosDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OracleRelatorioConcessoesDeBeneficiosDAO</p>
 * <p>Description: Classe DAO para gereção de relatorio de Concessões Beneficios</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleRelatorioConcessoesDeBeneficiosDAO implements RelatorioConcessoesDeBeneficiosDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleRelatorioConcessoesDeBeneficiosDAO.class);

    @Override
    public List<String> geraRelatorioConcessoesDeBeneficios(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto, String pathRelatorioConcessao,
            String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto, Calendar dataAtual, boolean reenviaConceCadastroReativacao, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {

        List<String> arquivos = new ArrayList<>();

        try {
            // Gera a tabela temporária que contem os dados para o relatório, porem não são ordenadas.
            LOG.info("Início da geração das tabelas temporárias.");
            geraTabelaTemporaria();
            LOG.info("Fim da geração das tabelas temporárias.");

            // Método que vai conter todos os fluxos para a geração das linhas de inclusão.
            LOG.info("Início da geração das linhas de inclusão.");
            geraLinhasInclusaoConcessaoBeneficios(reenviaConceCadastroReativacao);
            LOG.info("Fim da geração das linhas de inclusão.");

            // Método que vai conter todos os fluxos para a geração das linhas de Alteração.
            LOG.info("Início da geração das linhas de alteração.");
            geraLinhasAlteracaoConcessaoBeneficios();
            LOG.info("Fim da geração das linhas de alteração.");

            // Método que vai conter todos os fluxos para a geração das linhas de Exclusão.
            LOG.info("Início da geração das linhas de exclusão.");
            geraLinhasExclusaoConcessaoBeneficios();
            LOG.info("Fim da geração das linhas de exclusão.");

            // Salva o relatório nos arquivos finais.
            LOG.info("Início da geração dos arquivos finais.");
            arquivos = salvaRelatorioConcessaoDeBeneficios(orgaos, periodo, nomeArquivoFinalTexto, pathRelatorioConcessao,
                    nomeArqConfEntradaAbsoluto, nomeArqConfSaidaAbsoluto, nomeArqConfTradutorAbsoluto, dataAtual, responsavel);
            LOG.info("Fim da geração dos arquivos finais.");
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }

        return arquivos;
    }

    /**
     * Método que salvar os relatório com base na tebela final e filtrando o orgão.
     * @param orgaos
     * @param periodo
     * @param nomeArquivoFinalTexto
     * @param pathRelatorioConcessao
     * @param nomeArqConfEntradaAbsoluto
     * @param nomeArqConfSaidaAbsoluto
     * @param nomeArqConfTradutorAbsoluto
     * @param dataAtual
     * @param statement
     * @param resultSet
     * @param responsavel
     * @return
     * @throws DataAccessException
     * @throws ParserException
     * @throws PeriodoException
     */
    private List<String> salvaRelatorioConcessaoDeBeneficios(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto,
            String pathRelatorioConcessao, String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto,
            Calendar dataAtual, AcessoSistema responsavel) throws DataAccessException, ParserException, PeriodoException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final List<String> arquivosGerados = new ArrayList<>();

        for (String orgao : orgaos) {
            LOG.info("Salvando arquivo do órgão: " + orgao);

            String pathRelatorioConcessaoOrg = pathRelatorioConcessao + File.separatorChar + orgao;

            LOG.info("Analisando se o direito : " + pathRelatorioConcessaoOrg + " existe.");
            File filePathRelatorioConcessao = new File(pathRelatorioConcessaoOrg);
            if (!filePathRelatorioConcessao.exists()) {
                // Não existe e vamos tentar criar.
                LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
                filePathRelatorioConcessao.mkdirs();
            }

            String nomeArquivoFinalAbsoluto = pathRelatorioConcessaoOrg + File.separatorChar + nomeArquivoFinalTexto;

            // Calculando variáveis para criar o nome do arquivo
            String sql = "select org_identificador_beneficio from tb_orgao where org_codigo = :orgao";
            queryParams.addValue("orgao", orgao);
            String orgIdentificadorBeneficio = jdbc.queryForObject(sql, queryParams, String.class);
            if (TextHelper.isNull(orgIdentificadorBeneficio)) {
                orgIdentificadorBeneficio = orgao;
            }

            LOG.info("ORG_IDENTIFICADOR_BENEFICIO calculado: " + orgIdentificadorBeneficio);

            String mmaaaa = DateHelper.format(periodo, "MMyyyy");

            // Ajustando o nome do arquivo
            nomeArquivoFinalAbsoluto = nomeArquivoFinalAbsoluto.replaceAll("<<ORG_IDENTIFICADOR_BENEFICIO>>", orgIdentificadorBeneficio);
            nomeArquivoFinalAbsoluto = nomeArquivoFinalAbsoluto.replaceAll("<<MMAAAA>>", mmaaaa);
            nomeArquivoFinalAbsoluto = nomeArquivoFinalAbsoluto.replaceAll("<<NOME_SISTEMA>>", JspHelper.getNomeSistema(responsavel).replaceAll(" ", "_"));

            // Iniciando a escrita
            LOG.info("Início da escrita do arquivo final");
            EscritorArquivoTexto escritorArquivoTexto = new EscritorArquivoTexto(nomeArqConfSaidaAbsoluto, nomeArquivoFinalAbsoluto);
            geraTabelaFinal();
            populaTabelaFinalOrdenada(orgao, dataAtual);
            Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntradaAbsoluto, "select * from tb_tmp_conce_benficios_ord order by contador");
            Tradutor tradutor = new Tradutor(nomeArqConfTradutorAbsoluto, leitor, escritorArquivoTexto);
            tradutor.traduz();
            arquivosGerados.add(nomeArquivoFinalAbsoluto);
            dataAtual.add(Calendar.MINUTE, 1);
            LOG.info("Fim da escrita do arquivo final");
        }

        // Removendo nome de arquivo duplicado, evitando problema ao criar o zip se o sistema estiver configurado de forma errada
        Set<String> arquivosGeradosUnicos = arquivosGerados.stream().collect(Collectors.toSet());
        return arquivosGeradosUnicos.stream().collect(Collectors.toList());
    }

    /**
     * Cria a tabela final ordenada com base na tebela temporária, filtrando o órgão
     * @param orgao
     * @param dataAtual
     * @param statement
     * @throws DataAccessException
     */
    private void populaTabelaFinalOrdenada(String orgao, Calendar dataAtual) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append(" insert into tb_tmp_conce_benficios_ord (identificador_arquivo, hora_geracao, tipo_operacao, org_codigo, org_identificador, org_identificador_beneficio,");
        sql.append(" rse_matricula, bfc_cpf, ben_codigo_registro, tib_codigo, bfc_ordem_dependencia, cbe_data_inicio_vigencia, cbe_data_fim_vigencia, cbe_numero,");
        sql.append(" bfc_excecao_dependencia_fim, mde_codigo, tmo_codigo_inclusao_alteracao, tmo_codigo_exclusao)");
        sql.append(" select '0001',");
        sql.append("        :dataAtual,");
        sql.append("        tipo_operacao,");
        sql.append("        org_codigo,");
        sql.append("        org_identificador,");
        sql.append("        org_identificador_beneficio,");
        sql.append("        rse_matricula,");
        sql.append("        bfc_cpf,");
        sql.append("        ben_codigo_registro,");
        sql.append("        tib_codigo,");
        sql.append("        bfc_ordem_dependencia,");
        sql.append("        cbe_data_inicio_vigencia,");
        sql.append("        cbe_data_fim_vigencia,");
        sql.append("        cbe_numero,");
        sql.append("        bfc_excecao_dependencia_fim,");
        sql.append("        mde_codigo,");
        sql.append("        tmo_codigo_inclusao_alteracao,");
        sql.append("        tmo_codigo_exclusao");
        sql.append(" from tb_tmp_conce_benficios");
        sql.append(" where 1 = 1");
        sql.append(" and org_codigo = :orgao");
        sql.append(" order by rse_matricula,");
        sql.append("          cbe_data_inicio_vigencia desc,");
        sql.append("          ben_codigo_registro,");
        sql.append("          bfc_ordem_dependencia,");
        sql.append("          bfc_cpf");
        LOG.info(sql);
        queryParams.addValue("orgao", orgao);
        queryParams.addValue("dataAtual", new java.sql.Timestamp(dataAtual.getTimeInMillis()));
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        String rownum = "SET @rownum := 0; ";
        LOG.debug(rownum);
        jdbc.update(rownum, queryParams);

        StringBuilder sqlOrdenacao = new StringBuilder();
        sqlOrdenacao.append("update tb_tmp_conce_benficios_ord set contador = @rownum:=@rownum+1 order by field (tipo_operacao, 'E', 'A', 'I')");
        LOG.info(sqlOrdenacao);
        jdbc.update(sqlOrdenacao.toString(), queryParams);
        sqlOrdenacao.setLength(0);
    }

    /**
     * Gera a tabela temporária final
     * @param statement
     * @throws DataAccessException
     */
    private void geraTabelaTemporaria() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_conce_benficios;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table `tb_tmp_conce_benficios` (");
        sql.append("   `tipo_operacao` char(1) default null,");
        sql.append("   `org_codigo` char(32),");
        sql.append("   `org_identificador` varchar(40) not null,");
        sql.append("   `org_identificador_beneficio` varchar(40) not null,");
        sql.append("   `rse_matricula` varchar(20) not null default '',");
        sql.append("   `bfc_cpf` varchar(19) not null,");
        sql.append("   `ben_codigo_registro` varchar(40) not null,");
        sql.append("   `tib_codigo` varchar(32) not null,");
        sql.append("   `bfc_ordem_dependencia` smallint(6) not null,");
        sql.append("   `cbe_data_inicio_vigencia` datetime not null,");
        sql.append("   `cbe_data_fim_vigencia` datetime default null,");
        sql.append("   `cbe_numero` varchar(40) not null,");
        sql.append("   `bfc_excecao_dependencia_fim` date default null,");
        sql.append("   `mde_codigo` varchar(32) default null,");
        sql.append("   `tmo_codigo_inclusao_alteracao` varchar(32) default null,");
        sql.append("   `tmo_codigo_exclusao` varchar(32) default null");
        sql.append(" ) engine=innodb default charset=latin1");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Gera a tabela final.
     * @param statement
     * @throws DataAccessException
     */
    private void geraTabelaFinal() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_conce_benficios_ord;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table `tb_tmp_conce_benficios_ord` (");
        sql.append("   `contador` int(11) not null auto_increment,");
        sql.append("   `identificador_arquivo` varchar(4),");
        sql.append("   `hora_geracao` datetime,");
        sql.append("   `tipo_operacao` char(1) default null,");
        sql.append("   `org_codigo` char(32),");
        sql.append("   `org_identificador` varchar(40) not null,");
        sql.append("   `org_identificador_beneficio` varchar(40) not null,");
        sql.append("   `rse_matricula` varchar(20) not null default '',");
        sql.append("   `bfc_cpf` varchar(19) not null,");
        sql.append("   `ben_codigo_registro` varchar(40) not null,");
        sql.append("   `tib_codigo` varchar(32) not null,");
        sql.append("   `bfc_ordem_dependencia` smallint(6) not null,");
        sql.append("   `cbe_data_inicio_vigencia` datetime not null,");
        sql.append("   `cbe_data_fim_vigencia` datetime default null,");
        sql.append("   `cbe_numero` varchar(40) not null,");
        sql.append("   `bfc_excecao_dependencia_fim` date default null,");
        sql.append("   `mde_codigo` varchar(32) default null,");
        sql.append("   `tmo_codigo_inclusao_alteracao` varchar(32) default null,");
        sql.append("   `tmo_codigo_exclusao` varchar(32) default null,");
        sql.append("   key(`contador`)");
        sql.append(" ) engine=innodb default charset=latin1");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /** Inicio bloco de logica de inclusão **/

    /**
     * Método orquestrador da geração das linhas de inclusão
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasInclusaoConcessaoBeneficios(boolean reenviaConceCadastroReativacao) throws DataAccessException, SQLException {
        LOG.info("Início da seleção dos contrato benefícios e beneficiario com inclusão no período.");
        obtemBeneficiarioEContratoBeneficioInclusaoPeriodo(reenviaConceCadastroReativacao);
        LOG.info("Fim da seleção dos contrato benefícios com inclusão no período.");

        LOG.info("Início da obtenção da maior revisão do beneficiario selecionado.");
        obtemMaiorRevisaoParaContratoBeneficioSelecionadosInclusaoPeriodo(reenviaConceCadastroReativacao);
        LOG.info("Fim da obtenção da maior revisão do beneficiario selecionado.");

        LOG.info("Início da obtenção da maior revisão do contrato benefícios selecionado.");
        obtemMaiorRevisaoParaBeneficiarioSelecionadosInclusaoPeriodo();
        LOG.info("Fim da obtenção da maior revisão do contrato benefícios selecionado.");

        LOG.info("Início da inclusão das linhas na tabela final.");
        incluiBeneficiarioEContratoBeneficioInclusaoNaTabelaFinal(reenviaConceCadastroReativacao);
        LOG.info("Fim da inclusão das linhas na tabela final.");
    }

    /**
     * Seleciona Beneficiario e Contrato Beneficio que foram incluidos no período
     * Com base na ADE
     * @param statement
     * @throws DataAccessException
     */
    private void obtemBeneficiarioEContratoBeneficioInclusaoPeriodo(boolean reenviaConceCadastroReativacao) throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_cbe_inclusao");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        // Recupera os contratos de benefício que tiverem sua inclusão no período de benefício informado
        sql.append(" create temporary table tb_tmp_bfc_cbe_inclusao");
        sql.append(" select bfc.bfc_codigo, cbe.cbe_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        // Considera contratos que foram excluídos depois do período analisado
        sql.append(" left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and (oca.oca_data > pbe.pbe_data_fim or oca.oca_periodo > pbe.pbe_periodo ");
        if (reenviaConceCadastroReativacao) {
            sql.append(" or oca.oca_periodo = pbe.pbe_periodo");
        }
        sql.append(" ) and oca.toc_codigo in (?,?");
        if (reenviaConceCadastroReativacao) {
            sql.append(" ,?");
        }
        sql.append(" ))");
        sql.append(" where 1 = 1");
        sql.append(" and (ade.ade_ano_mes_ini = pbe.pbe_periodo");
        if (reenviaConceCadastroReativacao) {
            sql.append(" or oca.oca_periodo = pbe.pbe_periodo");
        }
        sql.append(" )");
        sql.append(" and ade.ade_int_folha = ?");
        sql.append(" and (ade.sad_codigo in (");
        for (int k = 0; k < CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA.size(); k++) {
            sql.append("?,");
        }
        sql.replace(sql.length()-1, sql.length(), ")");

        sql.append("or (ade.sad_codigo in (");
        for (int k = 0; k < CodedValues.SAD_CODIGOS_INATIVOS.size(); k++) {
            sql.append("?,");
        }
        sql.replace(sql.length()-1, sql.length(), ")");

        sql.append("and oca.oca_codigo is not null))");
        sql.append(" and tla.tla_codigo_pai is null");
        sql.append(" group by bfc.bfc_codigo, cbe.cbe_codigo;");
        LOG.info(sql);
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_TARIF_LIQUIDACAO);
            preparedStatement.setString(i++, CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            if (reenviaConceCadastroReativacao) {
                preparedStatement.setString(i++, CodedValues.TOC_REATIVACAO_CONTRATO);
            }
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA) {
                preparedStatement.setString(i++, sadCodigo);
            }
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }

            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Para cada contrato beneficio seleciona encontro a maior revisão dele e o TMO_CODIGO da inclusão
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaContratoBeneficioSelecionadosInclusaoPeriodo(boolean reenviaConceCadastroReativacao) throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_cbe_inclusao;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("create temporary table tb_tmp_cbe_inclusao (");
            sql.append("    cbe_codigo varchar(32) not null,");
            sql.append("    tmo_codigo varchar(32),");
            sql.append("    rev_data_maior datetime,");
            sql.append("    key(cbe_codigo)");
            sql.append(") engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" insert into tb_tmp_cbe_inclusao");
            sql.append(" select distinct cbe_codigo, null, null from tb_tmp_bfc_cbe_inclusao;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_ajuda_inclusao;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("create temporary table tb_tmp_ajuda_inclusao");
            sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.cbe_codigo");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_revisao_auditoria rev on (cbe.rev_codigo = rev.rev_codigo)");
            sql.append(" inner join tb_tmp_bfc_cbe_inclusao tmp on (cbe.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and rev.rev_data <= pbe.pbe_data_fim");
            sql.append(" group by tmp.cbe_codigo;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" update tb_tmp_cbe_inclusao tmp");
            sql.append(" inner join tb_tmp_ajuda_inclusao as ajuda  on (tmp.cbe_codigo = ajuda.cbe_codigo)");
            sql.append(" set tmp.rev_data_maior = ajuda.rev_data_maior;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_ajuda_inclusao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table tb_tmp_ajuda_inclusao");
            sql.append(" select max(ocb_data) as ocb_data, tmp.cbe_codigo ");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_tmp_bfc_cbe_inclusao tmp on (cbe.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ocb_data <= pbe_data_fim");
            sql.append(" and ocb.toc_codigo in (?");
            if (reenviaConceCadastroReativacao) {
                sql.append(" ,?");
            }
            sql.append(" )");
            sql.append(" group by tmp.cbe_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            int i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_INCLUSAO_CONTRATO_BENEFICIO);
            if (reenviaConceCadastroReativacao) {
                preparedStatement.setString(i++, CodedValues.TOC_REATIVACAO_CONTRATO);
            }
            preparedStatement.executeUpdate();
            sql.setLength(0);

            sql.append(" update tb_tmp_cbe_inclusao tmp ");
            sql.append(" inner join tb_tmp_ajuda_inclusao as ajuda  on (tmp.cbe_codigo = ajuda.cbe_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on  (ocb.cbe_codigo = tmp.cbe_codigo and ajuda.ocb_data = ocb.ocb_data)");
            sql.append(" set tmp.tmo_codigo = ocb.tmo_codigo;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop table if exists tb_tmp_ajuda_inclusao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Para o beneficiario selecionado procuro a maior revisão dele.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaBeneficiarioSelecionadosInclusaoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_inclusao");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("create temporary table tb_tmp_bfc_inclusao ( ");
        sql.append("    bfc_codigo varchar(32) not null,");
        sql.append("    rev_data_maior datetime,");
        sql.append("    key(bfc_codigo)");
        sql.append(") engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_bfc_inclusao");
        sql.append(" select distinct bfc_codigo, null from tb_tmp_bfc_cbe_inclusao;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_inclusao");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_inclusao");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.bfc_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (bfc.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_cbe_inclusao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data <= pbe.pbe_data_fim");
        sql.append(" group by tmp.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_inclusao tmp ");
        sql.append(" inner join tb_tmp_ajuda_inclusao ajuda on (tmp.bfc_codigo = ajuda.bfc_codigo)");
        sql.append(" set tmp.rev_data_maior = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Com os calculos da revisão pegamos os dados e incluimos na tabela temporario
     * @param statement
     * @throws DataAccessException
     */
    private void incluiBeneficiarioEContratoBeneficioInclusaoNaTabelaFinal(boolean reenviaConceCadastroReativacao) throws DataAccessException, SQLException {
        final StringBuilder sql = new StringBuilder();

        sql.append(" insert into tb_tmp_conce_benficios");
        sql.append(" select distinct 'I', org.org_codigo, org.org_identificador, org.org_identificador_beneficio, rse.rse_matricula, bfcAudit.bfc_cpf, ben_codigo_registro, bfcAudit.tib_codigo, bfcAudit.bfc_ordem_dependencia,");
        sql.append(" cbe.cbe_data_inicio_vigencia, cbe.cbe_data_fim_vigencia, cbe.cbe_numero, bfcAudit.bfc_excecao_dependencia_fim, bfcAudit.mde_codigo, tmpcbe.tmo_codigo, null");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        // tabela de auditoria de contrato de benefício
        sql.append(" inner join ta_contrato_beneficio cbeAudit on (ade.cbe_codigo = cbeAudit.cbe_codigo)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        // tabela de auditoria de beneficiário
        sql.append(" inner join ta_beneficiario bfcAudit on (cbeAudit.bfc_codigo = bfcAudit.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (ben.ben_codigo = cbeAudit.ben_codigo) ");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfcAudit.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_revisao_auditoria revbfc on (revbfc.rev_codigo = bfcAudit.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_inclusao tmpbfc on (tmpbfc.bfc_codigo = bfcAudit.bfc_codigo and tmpbfc.rev_data_maior = revbfc.rev_data)");
        sql.append(" inner join tb_revisao_auditoria revcbe on (revcbe.rev_codigo = cbeAudit.rev_codigo)");
        sql.append(" inner join tb_tmp_cbe_inclusao tmpcbe on (tmpcbe.cbe_codigo = cbeAudit.cbe_codigo and tmpcbe.rev_data_maior = revcbe.rev_data)");
        if (reenviaConceCadastroReativacao) {
            sql.append(" left outer join tb_ocorrencia_ctt_beneficio ocb on (ocb.ocb_data between pbe.pbe_data_ini and pbe.pbe_data_fim and ocb.toc_codigo=?)");
        }
        sql.append(" where 1 = 1");
        sql.append(" and (ade.ade_ano_mes_ini = pbe.pbe_periodo");
        if (reenviaConceCadastroReativacao) {
            sql.append(" or ocb.ocb_data between pbe.pbe_data_ini and pbe.pbe_data_fim");
        }
        sql.append(" )");
        sql.append(" and ade.ade_int_folha = ?");
        sql.append(" and ade.sad_codigo in (");
        for (int k = 0; k < CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA.size(); k++) {
            sql.append("?,");
        }
        sql.replace(sql.length()-1, sql.length(), ")");
        sql.append(" and tla.tla_codigo_pai is null;");
        LOG.info(sql);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            if (reenviaConceCadastroReativacao) {
                preparedStatement.setString(i++, CodedValues.TOC_REATIVACAO_CONTRATO);
            }
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA) {
                preparedStatement.setString(i++, sadCodigo);
            }

            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /** Fim bloco de logica de inclusão **/

    /** Inicio bloco de logica de alteração **/

    /**
     * Método orquestrador da geração das linhas de alteração
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasAlteracaoConcessaoBeneficios() throws DataAccessException, SQLException {
        LOG.info("Início da obtenção dos beneficiários que tiveram alteração no período.");
        obtemBeneficiariosAlteracaoPeriodo();
        LOG.info("Fim da obtenção dos beneficiários que tiveram alteração no período.");

        LOG.info("Início da obtenção dos contratos benefícios que tiveram alteração no período.");
        obtemContratoBeneficioAlteracaoPeriodo();
        LOG.info("Fim da obtenção dos contratos benefícios que tiveram alteração no período.");

        LOG.info("Início da obtenção da maior e menor revisão dos beneficiários selecionados.");
        obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado();
        LOG.info("Fim da obtenção da maior e menor revisão dos beneficiários selecionados.");

        LOG.info("Início da obtenção da maior e menor revisão dos contrato beneficio selecionados.");
        obtemMaiorRevisaoParaContratoBeneficioSelecionadosAlteracaoPeriodoAtualEPassado();
        LOG.info("Fim da obtenção da maior e menor revisão dos contrato beneficio selecionados.");

        LOG.info("Início do ajustes das datas das revisões selecionadas");
        ajustaDatasBeneficiariosEContratoBeneficioSemAlteracaoNoPeriodo();
        LOG.info("Fim do ajustes das datas das revisões selecionadas");

        LOG.info("Início da analise se o beneficiario teve alteração nos campos do relatório.");
        analisaSeBeneficiarioTeveAlteracaoCamposRelatorio();
        LOG.info("Fim da analise se o beneficiario teve alteração nos campos do relatório.");

        LOG.info("Início da analise se o contrato beneficio teve alteração nos campos do relatório.");
        analisaSeContratoBeneficioTeveAlteracaoCamposRelatorio();
        LOG.info("Início da analise se o contrato beneficio teve alteração nos campos do relatório.");

        LOG.info("Início do ajustes das datas das revisões selecionadas");
        ajustaDatasBeneficiarioEContratoBeneficio();
        LOG.info("Fim do ajustes das datas das revisões selecionadas");

        LOG.info("Início da inclusão do beneficiario e contrato beneficio na tabela final.");
        incluiBeneficiarioEContratoBeneficioAlteracaoNaTabelaFinal();
        LOG.info("Fim da inclusão do beneficiario e contrato beneficio na tabela final.");
    }

    /**
     * Procuramos todos os benefiarios e seus contratos que sofreram alteração no período.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemBeneficiariosAlteracaoPeriodo() throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_bfc_alteracao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table tb_tmp_bfc_alteracao");
            sql.append(" select bfc.bfc_codigo ");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_beneficiario obe on (obe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and obe.toc_codigo = ?");
            sql.append(" and obe.obe_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?, ?) ");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" group by bfc.bfc_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_BENFICIARIO);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
            DBHelper.closeStatement(preparedStatement);
            sql.setLength(0);

            sql.append(" insert into tb_tmp_bfc_alteracao");
            sql.append(" select bfc.bfc_codigo ");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ocb.toc_codigo = ?");
            sql.append(" and ocb.ocb_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?, ?) ");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" group by bfc.bfc_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Procuramos todos os contratos benefícios e os seus beneficiários que sofreram alteração no período
     * @param statement
     * @throws DataAccessException
     */
    private void obtemContratoBeneficioAlteracaoPeriodo() throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_cbe_alteracao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table  tb_tmp_cbe_alteracao");
            sql.append(" select cbe.cbe_codigo ");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_beneficiario obe on (obe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and obe.toc_codigo = ?");
            sql.append(" and obe.obe_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?, ?) ");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" group by cbe.cbe_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_BENFICIARIO);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
            DBHelper.closeStatement(preparedStatement);
            sql.setLength(0);

            sql.append(" insert into tb_tmp_cbe_alteracao");
            sql.append(" select cbe.cbe_codigo ");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ocb.toc_codigo = ?");
            sql.append(" and ocb.ocb_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?, ?) ");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" group by cbe.cbe_codigo ;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Para cada beneficiario selecionado descobrimos a maior revisão do período e do passado antes do período.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_alteracao_datas");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("create temporary table `tb_tmp_bfc_alteracao_datas` (");
        sql.append("  `bfc_codigo` varchar(32) not null,");
        sql.append("  `rev_data_periodo` datetime default null,");
        sql.append("  `rev_data_passado` datetime default null,");
        sql.append("  `rev_data_escolhido` datetime default null,");
        sql.append("  `processado` varchar(1) character set utf8 not null default 'n'");
        sql.append(") engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_bfc_alteracao_datas ");
        sql.append(" select distinct bfc_codigo, null, null, null, 'n' from tb_tmp_bfc_alteracao;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.bfc_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (bfc.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data between pbe_data_ini and pbe_data_fim");
        sql.append(" group by tmp.bfc_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.bfc_codigo = ajuda.bfc_codigo)");
        sql.append(" set tmp.rev_data_periodo = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.bfc_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (bfc.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data < pbe_data_ini");
        sql.append(" group by tmp.bfc_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.bfc_codigo = ajuda.bfc_codigo)");
        sql.append(" set tmp.rev_data_passado = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Para cada contrato beneficio selecionado descobrimos a maior revisão do período e do passado antes do período.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaContratoBeneficioSelecionadosAlteracaoPeriodoAtualEPassado() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_cbe_alteracao_datas");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table `tb_tmp_cbe_alteracao_datas` (");
        sql.append("   `cbe_codigo` varchar(32) not null,");
        sql.append("   `rev_data_periodo` datetime default null,");
        sql.append("   `rev_data_passado` datetime default null,");
        sql.append("   `rev_data_escolhido` datetime default null,");
        sql.append("   `processado` varchar(1) character set utf8 not null default 'n'");
        sql.append(" ) engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_cbe_alteracao_datas");
        sql.append(" select distinct cbe_codigo, null, null, null, 'n' from tb_tmp_cbe_alteracao;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.cbe_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (cbe.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_cbe_alteracao tmp on (cbe.cbe_codigo = tmp.cbe_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data between pbe_data_ini and pbe_data_fim ");
        sql.append(" group by tmp.cbe_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_cbe_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.cbe_codigo = ajuda.cbe_codigo)");
        sql.append(" set tmp.rev_data_periodo = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.cbe_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (cbe.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_cbe_alteracao tmp on (cbe.cbe_codigo = tmp.cbe_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data < pbe_data_ini");
        sql.append(" group by tmp.cbe_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_cbe_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.cbe_codigo = ajuda.cbe_codigo)");
        sql.append(" set tmp.rev_data_passado = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Caso o beneficiario ou o contrato beneficio não teve alteração no período consideramos que pode ser que
     * o beneficiario ligado ao contrato beneficio e vice-versa tenha sofriado a alteração
     * @param statement
     * @throws DataAccessException
     */
    private void ajustaDatasBeneficiariosEContratoBeneficioSemAlteracaoNoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append(" update tb_tmp_bfc_alteracao_datas");
        sql.append(" set rev_data_escolhido = rev_data_passado , processado = 's'");
        sql.append(" where rev_data_periodo is null;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_cbe_alteracao_datas");
        sql.append(" set rev_data_escolhido = rev_data_passado , processado = 's'");
        sql.append(" where rev_data_periodo is null;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Analisando se o beneficiario teve alteração no campos do relatório
     * @param statement
     * @throws DataAccessException
     */
    private void analisaSeBeneficiarioTeveAlteracaoCamposRelatorio() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_escolhido;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_bfc_escolhido");
        sql.append(" select tmp.bfc_codigo");
        sql.append(" from tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" inner join ta_beneficiario bfcperiodo on (bfcperiodo.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" inner join tb_revisao_auditoria revperiodo on (revperiodo.rev_codigo = bfcperiodo.rev_codigo and revperiodo.rev_data = tmp.rev_data_periodo)");
        sql.append(" inner join ta_beneficiario bfcpassado on (bfcpassado.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" inner join tb_revisao_auditoria revpassado on (revpassado.rev_codigo = bfcpassado.rev_codigo and revpassado.rev_data = tmp.rev_data_passado)");
        sql.append(" where 1 = 1");
        sql.append(" and tmp.processado = 'n'");
        sql.append(" and  (");
        sql.append(" bfcperiodo.tib_codigo <> bfcpassado.tib_codigo");
        sql.append(" or bfcperiodo.mde_codigo <> bfcpassado.mde_codigo");
        sql.append(" or bfcperiodo.bfc_ordem_dependencia <> bfcpassado.bfc_ordem_dependencia");
        sql.append(" or (bfcperiodo.bfc_excecao_dependencia_fim <> bfcpassado.bfc_excecao_dependencia_fim and bfcperiodo.mde_codigo = '1')");
        sql.append(" );");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_bfc_escolhido tmp2 on (tmp.bfc_codigo = tmp2.bfc_codigo)");
        sql.append(" set rev_data_escolhido = rev_data_periodo, processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Analisando se o contrato beneficio teve alteração no campos do relatório
     * @param statement
     * @throws DataAccessException
     */
    private void analisaSeContratoBeneficioTeveAlteracaoCamposRelatorio() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_cbe_escolhido;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_cbe_escolhido");
        sql.append(" select tmp.cbe_codigo");
        sql.append(" from tb_tmp_cbe_alteracao_datas tmp");
        sql.append(" inner join ta_contrato_beneficio cbeperiodo on (cbeperiodo.cbe_codigo = tmp.cbe_codigo)");
        sql.append(" inner join tb_revisao_auditoria revperiodo on (revperiodo.rev_codigo = cbeperiodo.rev_codigo and revperiodo.rev_data = tmp.rev_data_passado)");
        sql.append(" inner join ta_contrato_beneficio cbepassado on (cbepassado.cbe_codigo = tmp.cbe_codigo)");
        sql.append(" inner join tb_revisao_auditoria revpassado on (revpassado.rev_codigo = cbepassado.rev_codigo and revpassado.rev_data = tmp.rev_data_passado)");
        sql.append(" where 1 = 1");
        sql.append(" and tmp.processado = 'n'");
        sql.append(" and cbeperiodo.cbe_numero <> cbepassado.cbe_numero;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_cbe_alteracao_datas tmp");
        sql.append(" inner join tb_tmp_cbe_escolhido tmp2 on (tmp.cbe_codigo = tmp2.cbe_codigo)");
        sql.append(" set rev_data_escolhido = rev_data_periodo, processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Caso o beneficiario ou o contrato beneficio não teve alteração no passado consideramos que pode ser que
     * o beneficiario ligado ao contrato beneficio e vice-versa tenha sofriado a alteração
     * @param statement
     * @throws DataAccessException
     */
    private void ajustaDatasBeneficiarioEContratoBeneficio() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" set rev_data_escolhido = rev_data_periodo , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_cbe_alteracao_datas tmp");
        sql.append(" set rev_data_escolhido = rev_data_periodo , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmpbfc");
        sql.append(" inner join tb_contrato_beneficio cbe on (cbe.bfc_codigo = tmpbfc.bfc_codigo)");
        sql.append(" inner join tb_tmp_cbe_alteracao_datas tmpcbe on (tmpcbe.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" set tmpbfc.rev_data_escolhido = coalesce(tmpbfc.rev_data_periodo, tmpbfc.rev_data_passado), tmpbfc.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpbfc.rev_data_escolhido is not null");
        sql.append(" and tmpbfc.processado <> 's'");
        sql.append(" and tmpcbe.rev_data_periodo is not null");
        sql.append(" and tmpcbe.rev_data_passado is not null");
        sql.append(" and tmpcbe.rev_data_escolhido is not null");
        sql.append(" and tmpcbe.processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmpbfc");
        sql.append(" inner join tb_contrato_beneficio cbe on (cbe.bfc_codigo = tmpbfc.bfc_codigo)");
        sql.append(" inner join tb_tmp_cbe_alteracao_datas tmpcbe on (tmpcbe.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" set tmpcbe.rev_data_escolhido = coalesce(tmpcbe.rev_data_periodo, tmpcbe.rev_data_passado), tmpcbe.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpcbe.rev_data_escolhido is not null");
        sql.append(" and tmpcbe.processado <> 's'");
        sql.append(" and tmpbfc.rev_data_periodo is not null");
        sql.append(" and tmpbfc.rev_data_passado is not null");
        sql.append(" and tmpbfc.rev_data_escolhido is not null");
        sql.append(" and tmpbfc.processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Com os calculos da revisao calculados fazemos os insert na tabela temporária
     * @param statement
     * @throws DataAccessException
     */
    private void incluiBeneficiarioEContratoBeneficioAlteracaoNaTabelaFinal() throws DataAccessException, SQLException {
        final StringBuilder sql = new StringBuilder();

        sql.append(" insert into tb_tmp_conce_benficios");
        sql.append(" select 'A', org.org_codigo, org_identificador, org_identificador_beneficio, rse_matricula, bfc_cpf, ben_codigo_registro, tib_codigo, bfc_ordem_dependencia,");
        sql.append(" cbe_data_inicio_vigencia, cbe_data_fim_vigencia, cbe_numero, bfc_excecao_dependencia_fim, mde_codigo, null as tmo_codigo, null");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_revisao_auditoria revbfc on (revbfc.rev_codigo = bfc.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo and tmpbfc.rev_data_escolhido = revbfc.rev_data)");
        sql.append(" inner join tb_revisao_auditoria revcbe on (revcbe.rev_codigo = cbe.rev_codigo)");
        sql.append(" inner join tb_tmp_cbe_alteracao_datas tmpcbe on (tmpcbe.cbe_codigo = cbe.cbe_codigo and tmpcbe.rev_data_escolhido = revcbe.rev_data)");
        sql.append(" where 1 = 1");
        sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
        sql.append(" and ade.ade_int_folha in (?, ?)");
        sql.append(" and tla.tla_codigo_pai is null");
        sql.append(" group by bfc.bfc_codigo, cbe.cbe_codigo;");
        LOG.info(sql);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int  i = 1;
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /** Fim bloco de logica de alteração **/

    /** Inicio bloco de logica de exclusão **/

    /**
     * Método orquestrador da geração das linhas de exclusão
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasExclusaoConcessaoBeneficios() throws DataAccessException, SQLException {
        LOG.info("Início da obtenção dos Contrato Beneficio que tiveram exclusão no período.");
        obtemContratoBeneficioExclusaoPeriodo();
        LOG.info("Início da obtenção dos Contrato Beneficio que tiveram exclusão no período.");

        LOG.info("Início da obtenção do motivo da exclusão.");
        obtemMotivoExclusaoContratoBeneficio();
        LOG.info("Fim da obtenção do motivo da exclusão.");

        LOG.info("Início da inclusão do contrato beneficio na tabela final.");
        incluiBeneficiarioEContratoBeneficioExclusaoNaTabelaFinal();
        LOG.info("Fim da inclusão do contrato beneficio na tabela final.");
    }

    /**
     * Lista todos os Contrato Beneficio que foram excluidos no período
     * @param statement
     * @throws DataAccessException
     */
    private void obtemContratoBeneficioExclusaoPeriodo() throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_cbe_exclusao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table tb_tmp_cbe_exclusao (");
            sql.append("     cbe_codigo varchar(32) not null,");
            sql.append("     tmo_codigo varchar(32),");
            sql.append("     key(cbe_codigo)");
            sql.append(" ) engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" insert into tb_tmp_cbe_exclusao");
            sql.append(" select cbe.cbe_codigo, null");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ocb.toc_codigo = ?");
            sql.append(" and ocb.ocb_data between pbe_data_ini and pbe_data_fim");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?, ?) ");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_INATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" group by cbe.cbe_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            int  i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Com os contratos selecionados para exclusão procurarmos o TMO_CODIGO da exclusão
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMotivoExclusaoContratoBeneficio() throws DataAccessException, SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_ajuda_exclusao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table tb_tmp_ajuda_exclusao");
            sql.append(" select max(ocb_data) as ocb_data, tmp.cbe_codigo");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_tmp_cbe_exclusao tmp on (cbe.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ocb_data <= pbe_data_fim");
            sql.append(" and ocb.toc_codigo in (?,?)");
            sql.append(" group by tmp.cbe_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());

            int  i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO);
            preparedStatement.setString(i++, CodedValues.TOC_RETIFICACAO_MOTIVO_OPERACAO);
            preparedStatement.executeUpdate();
            DBHelper.closeStatement(preparedStatement);
            sql.setLength(0);

            sql.append(" update tb_tmp_cbe_exclusao tmp");
            sql.append(" inner join tb_tmp_ajuda_exclusao as ajuda  on (tmp.cbe_codigo = ajuda.cbe_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on  (ocb.cbe_codigo = tmp.cbe_codigo and ajuda.ocb_data = ocb.ocb_data)");
            sql.append(" set tmp.tmo_codigo = ocb.tmo_codigo ");
            sql.append(" where ocb.toc_codigo in (?,?)");
            LOG.info(sql);

            preparedStatement = conn.prepareStatement(sql.toString());

            i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO);
            preparedStatement.setString(i++, CodedValues.TOC_RETIFICACAO_MOTIVO_OPERACAO);
            preparedStatement.executeUpdate();
            sql.setLength(0);

            sql.append("drop table if exists tb_tmp_ajuda_exclusao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Fazemos insert na tabela temporária os contratos selecionado para exclusão
     * @param statement
     * @throws DataAccessException
     */
    private void incluiBeneficiarioEContratoBeneficioExclusaoNaTabelaFinal() throws DataAccessException, SQLException {
        final StringBuilder sql = new StringBuilder();

        sql.append(" insert into tb_tmp_conce_benficios");
        sql.append(" select 'E', org.org_codigo, org_identificador, org_identificador_beneficio, rse_matricula, bfc_cpf, ben_codigo_registro, tib_codigo, bfc_ordem_dependencia,");
        sql.append(" cbe_data_inicio_vigencia, cbe_data_fim_vigencia, cbe_numero, bfc_excecao_dependencia_fim, mde_codigo, null, tmo_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_tmp_cbe_exclusao tmpcbe on (tmpcbe.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
        sql.append(" and ade.ade_int_folha in (?, ?)");
        sql.append(" and tla.tla_codigo_pai is null");
        sql.append(" group by bfc.bfc_codigo, cbe.cbe_codigo;");
        LOG.info(sql);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int  i = 1;
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            preparedStatement.executeUpdate();
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /** Fim bloco de logica de exclusão **/
}