package com.zetra.econsig.persistence.dao.mysql;

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
import com.zetra.econsig.persistence.dao.RelatorioBeneficiariosDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MySqlRelatorioBeneficiariosDAO</p>
 * <p>Description: Classe DAO para gereção de relatorio de Beneficiarioss</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlRelatorioBeneficiariosDAO implements RelatorioBeneficiariosDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlRelatorioBeneficiariosDAO.class);

    @Override
    public List<String> geraRelatorioBeneficiarios(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto, String pathRelatorioConcessao,
            String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto, Calendar dataAtual, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {

        List<String> arquivos = new ArrayList<>();

        try {
            // Gera a tabela temporaria que contem os dados para o relatorio, porem não são ordenadas.
            LOG.info("Inicio da geração das tabelas temporaria.");
            geraTabelaTemporaria();
            LOG.info("Fim da geração das tabelas temporaria.");

            // Metodo que vai conter todos os fluxo para a geração das linhas de inclusão.
            LOG.info("Inicio da geração das linhas de inclusão.");
            geraLinhasInclusaoBeneficiariosEEnderecoServidor();
            LOG.info("Fim da geração das linhas de inclusão.");

            // Metodo que vai conter todos os fluxo para a geração das linhas de alteração.
            LOG.info("Inicio da geração das linhas de alteração.");
            geraLinhasAlteracaoBeneficiariosEEnderecoServidor();
            LOG.info("Fim da geração das linhas de alteração.");

            // Salva o relatorio nos arquivos finais.
            LOG.info("Inicio da geração dos arquivos finais.");
            arquivos = salvaRelatorioBeneficiario(orgaos, periodo, nomeArquivoFinalTexto, pathRelatorioConcessao,
                    nomeArqConfEntradaAbsoluto, nomeArqConfSaidaAbsoluto, nomeArqConfTradutorAbsoluto, dataAtual, responsavel);
            LOG.info("Fim da geração dos arquivos finais.");
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }

        return arquivos;
    }

    /**
     * Metodo que salvar os relatorio com base na tebela final e filtrando o orgão.
     * @param orgaos
     * @param periodo
     * @param pathRelatorioConcessao
     * @param nomeArqConfEntradaAbsoluto
     * @param nomeArqConfSaidaAbsoluto
     * @param nomeArqConfTradutorAbsoluto
     * @param dataAtual
     * @param resultSet
     * @param statement
     * @param responsavel
     * @return
     * @throws DataAccessException
     * @throws ParserException
     * @throws PeriodoException
     */
    private List<String> salvaRelatorioBeneficiario(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto,
            String pathRelatorioConcessao, String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto,
            Calendar dataAtual, AcessoSistema responsavel) throws DataAccessException, ParserException, PeriodoException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final List<String> arquivosGerados = new ArrayList<>();

        for (String orgao : orgaos) {
            LOG.info("Salvando arquivo do orgão: " + orgao);

            String pathRelatorioConcessaoOrg = pathRelatorioConcessao + File.separatorChar + orgao;

            LOG.info("Analisando se o direito : " + pathRelatorioConcessaoOrg + " existe.");
            File filePathRelatorioConcessao = new File(pathRelatorioConcessaoOrg);
            if (!filePathRelatorioConcessao.exists()) {
                // Não existe e vamos tentar criar.
                LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
                filePathRelatorioConcessao.mkdirs();
            }

            String nomeArquivoFinalAbsoluto = pathRelatorioConcessaoOrg + File.separatorChar + nomeArquivoFinalTexto;

            // Calculando variaveis para criar o nome do arquivo
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

            LOG.info("Inicio da escrita do arquivo final");
            EscritorArquivoTexto escritorArquivoTexto = new EscritorArquivoTexto(nomeArqConfSaidaAbsoluto, nomeArquivoFinalAbsoluto);
            geraTabelaFinal();
            populaTabelaFinalOrdenada(orgao, dataAtual);
            Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntradaAbsoluto, "select * from tb_tmp_arq_beneficiario_ord order by field (tipo_operacao, 'E', 'A', 'I'), rse_matricula, cbe_data_inicio_vigencia asc, bfc_cpf");
            Tradutor tradutor = new Tradutor(nomeArqConfTradutorAbsoluto, leitor, escritorArquivoTexto);
            tradutor.traduz();
            arquivosGerados.add(nomeArquivoFinalAbsoluto);
            dataAtual.add(Calendar.MINUTE, 1);
            LOG.info("Fim da escrita do arquivo final");
        }

        // Removendo nome de arquivo duplicado, evitando assim se o sistema estiver configurado de forma errada dar problema ao criar o zip
        Set<String> arquivosGeradosUnicos = arquivosGerados.stream().collect(Collectors.toSet());
        return arquivosGeradosUnicos.stream().collect(Collectors.toList());
    }

    /**
     * Cria a tabela final com base na tebela temporaria filtrando o orgão
     * @param orgao
     * @param dataAtual
     * @param statement
     * @throws DataAccessException
     */
    private void populaTabelaFinalOrdenada(String orgao, Calendar dataAtual) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("insert into tb_tmp_arq_beneficiario_ord (identificador_arquivo, hora_geracao, tipo_operacao, org_codigo, org_identificador,");
        sql.append(" org_identificador_beneficio, rse_matricula, bfc_cpf, bfc_nome, bfc_data_nascimento, bfc_sexo, grp_codigo, bfc_estado_civil,");
        sql.append(" bfc_rg, bfc_nome_mae, ens_logradouro, ens_numero, ens_complemento, ens_bairro, ens_codigo_municipio, ens_uf,");
        sql.append(" ens_cep, bfc_telefone, bfc_celular, cbe_data_inicio_vigencia)");
        sql.append("  select");
        sql.append("    '0002',");
        sql.append("    :dataAtual,");
        sql.append("    tipo_operacao,");
        sql.append("    org_codigo,");
        sql.append("    org_identificador,");
        sql.append("    org_identificador_beneficio,");
        sql.append("    rse_matricula,");
        sql.append("    bfc_cpf,");
        sql.append("    bfc_nome,");
        sql.append("    bfc_data_nascimento,");
        sql.append("    bfc_sexo,");
        sql.append("    grp_codigo,");
        sql.append("    bfc_estado_civil,");
        sql.append("    bfc_rg,");
        sql.append("    bfc_nome_mae,");
        sql.append("    ens_logradouro,");
        sql.append("    ens_numero,");
        sql.append("    ens_complemento,");
        sql.append("    ens_bairro,");
        sql.append("    ens_codigo_municipio,");
        sql.append("    ens_uf,");
        sql.append("    ens_cep,");
        sql.append("    bfc_telefone,");
        sql.append("    bfc_celular,");
        sql.append("    cbe_data_inicio_vigencia");
        sql.append("  from tb_tmp_arquivo_beneficiario");
        sql.append("  where org_codigo  = :orgao");
        sql.append("  order by field (tipo_operacao, 'E', 'A', 'I'), rse_matricula, cbe_data_inicio_vigencia asc, bfc_cpf;");
        LOG.info(sql);
        queryParams.addValue("orgao", orgao);
        queryParams.addValue("dataAtual", new java.sql.Timestamp(dataAtual.getTimeInMillis()));
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Gera a tabela final
     * @param statement
     * @throws DataAccessException
     */
    private void geraTabelaFinal() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_arq_beneficiario_ord;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("create temporary table `tb_tmp_arq_beneficiario_ord` (");
        sql.append("  `contador` int(11) not null auto_increment,");
        sql.append("  `identificador_arquivo` varchar(4),");
        sql.append("  `hora_geracao` datetime,");
        sql.append("  `tipo_operacao` char(1),");
        sql.append("  `org_codigo` char(32),");
        sql.append("  `org_identificador` varchar(40) not null,");
        sql.append("  `org_identificador_beneficio` varchar(40) not null,");
        sql.append("  `rse_matricula` varchar(20),");
        sql.append("  `bfc_cpf` varchar(19),");
        sql.append("  `bfc_nome` varchar(255),");
        sql.append("  `bfc_data_nascimento` date,");
        sql.append("  `bfc_sexo` char(1),");
        sql.append("  `grp_codigo` varchar(20),");
        sql.append("  `bfc_estado_civil` char(1),");
        sql.append("  `bfc_rg` varchar(40),");
        sql.append("  `bfc_nome_mae` varchar(100),");
        sql.append("  `ens_logradouro` varchar(100),");
        sql.append("  `ens_numero` varchar(15),");
        sql.append("  `ens_complemento` varchar(40),");
        sql.append("  `ens_bairro` varchar(40),");
        sql.append("  `ens_codigo_municipio` varchar(7),");
        sql.append("  `ens_uf` char(2),");
        sql.append("  `ens_cep` varchar(10),");
        sql.append("  `bfc_telefone` varchar(40),");
        sql.append("  `bfc_celular` varchar(40),");
        sql.append("  `cbe_data_inicio_vigencia` date,");
        sql.append("  primary key (`contador`)");
        sql.append(") engine=innodb default charset=latin1");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Gera a tabela temporaria que contem as linhas de inclusão e alteração.
     * Base para gerar a tabela final ordenada
     * @param statement
     * @throws DataAccessException
     */
    private void geraTabelaTemporaria() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("drop temporary table if exists tb_tmp_arquivo_beneficiario;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("create temporary table `tb_tmp_arquivo_beneficiario` (");
        sql.append("  `tipo_operacao` char(1) default null,");
        sql.append("  `org_codigo` char(32),");
        sql.append("  `org_identificador` varchar(40) not null,");
        sql.append("  `org_identificador_beneficio` varchar(40) not null,");
        sql.append("  `rse_matricula` varchar(20) default null,");
        sql.append("  `bfc_cpf` varchar(19) default null,");
        sql.append("  `bfc_nome` varchar(255) default null,");
        sql.append("  `bfc_data_nascimento` date default null,");
        sql.append("  `bfc_sexo` char(1) default null,");
        sql.append("  `grp_codigo` varchar(20) default null,");
        sql.append("  `bfc_estado_civil` char(1) default null,");
        sql.append("  `bfc_rg` varchar(40) default null,");
        sql.append("  `bfc_nome_mae` varchar(100) default null,");
        sql.append("  `ens_logradouro` varchar(100) default null,");
        sql.append("  `ens_numero` varchar(15) default null,");
        sql.append("  `ens_complemento` varchar(40) default null,");
        sql.append("  `ens_bairro` varchar(40) default null,");
        sql.append("  `ens_codigo_municipio` varchar(7) default null,");
        sql.append("  `ens_uf` char(2) default null,");
        sql.append("  `ens_cep` varchar(10) default null,");
        sql.append("  `bfc_telefone` varchar(40) default null,");
        sql.append("  `bfc_celular` varchar(40) default null,");
        sql.append("  `cbe_data_inicio_vigencia` date");
        sql.append(") engine=innodb default charset=latin1");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /** Inicio bloco de logica de inclusão **/

    /**
     * Metodo orquestadro da geração das linhas de inclusão.
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasInclusaoBeneficiariosEEnderecoServidor() throws DataAccessException, SQLException {
        LOG.info("Inicio da seleção dos beneficiario com inclusão no perido.");
        obtemBeneficiariosEEnderecoServidorInclusaoPeriodo();
        LOG.info("Fim da seleção dos beneficiario com inclusão no perido.");

        LOG.info("Inicio da obteção da maior revisão do beneficiario selecionado.");
        obtemMaiorRevisaoParaBeneficiariosSelecionadosInclusaoPeriodo();
        LOG.info("Fim da obteção da maior revisão do beneficiario selecionado.");

        LOG.info("Inicio da obteção da maior revisão do endereço servidor selecionado.");
        obtemMaiorRevisaoParaEnderecoServidorSelecionadosInclusaoPeriodo();
        LOG.info("Fim da obteção da maior revisão do endereço servidor selecionado.");

        LOG.info("Inicio da inclusão do beneficiario e endereço servidor na tabela final.");
        incluiuBeneficiariosEEnderecoServidorInclusaoPeriodoNaTabelaFinal();
        LOG.info("Fim da inclusão do beneficiario e endereço servidor na tabela final.");
    }

    /**
     * Com base no contrato (ade) que será enviado no movimento pegamos os beneficiario e endereço servidor
     * Associado ao contrato (ade).
     * @param statement
     * @throws DataAccessException
     */
    private void obtemBeneficiariosEEnderecoServidorInclusaoPeriodo() throws SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_ens_inclusao");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_bfc_ens_inclusao");
        sql.append(" select bfc.bfc_codigo, ens.ens_codigo");
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
        //sql.append(" inner join tb_ocorrencia_beneficiario obe on (obe.bfc_codigo = bfc.bfc_codigo and obe.obe_data between pbe.pbe_data_ini and pbe.pbe_data_fim and obe.toc_codigo = ?)");
        sql.append(" left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and (oca.oca_data > pbe.pbe_data_fim OR oca.oca_periodo > pbe.pbe_periodo)");
        sql.append(" and oca.toc_codigo in (?,?))");
        sql.append(" where 1 = 1");
        sql.append(" and ade_ano_mes_ini = pbe_periodo");
        sql.append(" and bfc.tib_codigo <> ?");
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
        sql.append(" group by bfc.bfc_codigo, ens.ens_codigo;");
        LOG.info(sql);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            //        preparedStatement.setString(i++, CodedValues.TOC_INCLUSAO_BENFICIARIO);
            preparedStatement.setString(i++, CodedValues.TOC_TARIF_LIQUIDACAO);
            preparedStatement.setString(i++, CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            preparedStatement.setString(i++, CodedValues.TIB_TITULAR);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA) {
                preparedStatement.setString(i++, sadCodigo);
            }
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }

            preparedStatement.executeUpdate();
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Usando a tabela de auditoria pegamos a maior data de revisão do Beneficiario que foi encontrado para inclusão.
     * A ideia é que tenhamos o ultimo dado ajustado, ou não, que foi feito dentro a data limite do periodo.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaBeneficiariosSelecionadosInclusaoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_inclusao_data");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_bfc_inclusao_data");
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
        sql.append(" inner join tb_tmp_bfc_ens_inclusao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data <= pbe.pbe_data_fim");
        sql.append(" group by tmp.bfc_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
    }

    /**
     * Usando a tabela de auditoria pegamos a maior data de revisão do Endereço do servidor que foi encontrado para inclusão.
     * A ideia é que tenhamos o ultimo dado ajustado, ou não, que foi feito dentro a data limite do periodo.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaEnderecoServidorSelecionadosInclusaoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_ens_inclusao_data");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ens_inclusao_data");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.ens_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join ta_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (ens.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_ens_inclusao tmp on (ens.ens_codigo = tmp.ens_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data <= pbe.pbe_data_fim");
        sql.append(" group by tmp.bfc_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
    }

    /**
     * Inclui na tabela temporaria os Beneficiario e Endereço Servidor que foram selecionados
     * Com base na maior data de revisão do dados que foi encontrado.
     * Assim se o arquivo for reexportado sempre vai considerar os dados alterados dentro do periodo.
     * @param statement
     * @throws DataAccessException
     */
    private void incluiuBeneficiariosEEnderecoServidorInclusaoPeriodoNaTabelaFinal() throws SQLException {
        final StringBuilder sql = new StringBuilder();
        sql.append(" insert into tb_tmp_arquivo_beneficiario");
        sql.append(" select 'I', org.org_codigo, org_identificador, org_identificador_beneficio, rse_matricula, bfc_cpf, bfc_nome, bfc_data_nascimento, bfc_sexo,");
        sql.append(" grp_codigo, bfc_estado_civil, bfc_rg, bfc_nome_mae, ens_logradouro, ens_numero, ens_complemento, ens_bairro, ens_codigo_municipio,");
        sql.append(" ens_uf, ens_cep, bfc_telefone, bfc_celular, cbe_data_inicio_vigencia");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_revisao_auditoria revbfc on (revbfc.rev_codigo = bfc.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_inclusao_data tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo and tmpbfc.rev_data_maior = revbfc.rev_data)");
        sql.append(" inner join tb_revisao_auditoria revens on (revens.rev_codigo = ens.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_inclusao_data tmpens on (tmpens.ens_codigo = ens.ens_codigo and tmpens.rev_data_maior = revens.rev_data)");
        sql.append(" where 1 = 1");
        sql.append(" and ade_ano_mes_ini = pbe_periodo");
        sql.append(" and ade.ade_int_folha = ?");
        sql.append(" and ade.sad_codigo in (");
        for (int k = 0; k < CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA.size(); k++) {
            sql.append("?,");
        }
        sql.replace(sql.length()-1, sql.length(), ")");
        sql.append(" and tla.tla_codigo_pai is null");
        sql.append(" group by bfc.bfc_codigo, ens.ens_codigo;");
        LOG.info(sql);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();
            preparedStatement = conn.prepareStatement(sql.toString());

            int i = 1;
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA) {
                preparedStatement.setString(i++, sadCodigo);
            }

            preparedStatement.executeUpdate();
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /** Fim bloco de logica de inclusão **/

    /** Inicio bloco de logica de alteração **/

    /**
     * Metodo orquestadro da geração das linhas de alteração.
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasAlteracaoBeneficiariosEEnderecoServidor() throws DataAccessException, SQLException {
        LOG.info("Inicio da obtenção dos beneficiarios que tiveram alteração no periodo.");
        obtemBeneficiariosAlteracaoPeriodo();
        LOG.info("Fim da obtenção dos beneficiarios que tiveram alteração no periodo.");

        LOG.info("Inicio da obtenção dos endereços servidor que tiveram alteração no periodo.");
        obtemEnderecoServidorAlteracaoPeriodo();
        LOG.info("Inicio da obtenção dos  endereços servidor que tiveram alteração no periodo.");

        LOG.info("Inicio da criação e populando tabelas temporarias para o fluxo·");
        criaEPopulaTabelaAuxiliaresFluxoAlteracao();
        LOG.info("Fim da criação e populando tabelas temporarias para o fluxo·");

        LOG.info("Inicio da obtenção da maior e menor revisão dos beneficiarios selecionados.");
        obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado();
        LOG.info("Fim da obtenção da maior e menor revisão dos beneficiarios selecionados.");

        LOG.info("Inicio da obtenção da maior e menor revisão dos endereços servidor selecionados.");
        obtemMaiorRevisaoParaEnderecoServidorSelecionadosAlteracaoPeriodoAtualEPassado();
        LOG.info("Fim da obtenção da maior e menor revisão dos endereços servidor selecionados.");

        LOG.info("Inicio do ajustes das datas das revisões selecionadas");
        ajustaDatasBeneficiariosEEnderecoServidorSemAlteracaoNoPeriodo();
        LOG.info("Fim do ajustes das datas das revisões selecionadas");

        LOG.info("Inicio da analise se o beneficiario teve alteração nos campos do relatorio.");
        analisaSeBeneficiarioTeveAlteracaoCamposRelatorio();
        LOG.info("Fim da analise se o beneficiario teve alteração nos campos do relatorio.");

        LOG.info("Inicio da analise se o endereço servidor teve alteração nos campos do relatorio.");
        analisaSeEnderecoServidorTeveAlteracaoCamposRelatorio();
        LOG.info("Fim da analise se o endereço servidor teve alteração nos campos do relatorio.");

        LOG.info("Inicio do ajustes das datas das revisões selecionadas");
        ajustaDatasBeneficiarioEEnderecoServidorGeral();
        LOG.info("Fim do ajustes das datas das revisões selecionadas");

        LOG.info("Inicio da inclusão do beneficiario e endereço servidor na tabela final.");
        incluiuBeneficiariosEEnderecoServidorAlteracaoPeriodoNaTabelaFinal();
        LOG.info("Fim da inclusão do beneficiario e endereço servidor na tabela final.");
    }

    /**
     * Obtem os beneficiarios que tiveram alteração dos dadados cadastrais e o endereço alterado no periodo.
     * @param statement
     * @throws DataAccessException
     */
    private void obtemBeneficiariosAlteracaoPeriodo() throws SQLException {
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
            sql.append(" group by bfc.bfc_codigo");
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
            sql.append(" inner join tb_ocorrencia_endereco_ser oes on (oes.ens_codigo = ens.ens_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and oes.toc_codigo = ?");
            sql.append(" and oes.oes_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?,?) ");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" and tib_codigo = ?");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" group by bfc.bfc_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_ENDERECO_SERVIDOR);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            preparedStatement.setString(i++, CodedValues.TIB_TITULAR);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Obtem os endereço servidor que tiveram alteração dos dadados cadastrais
     * e que o beneficiario tenha sido alterado no periodo
     * @param statement
     * @throws DataAccessException
     */
    private void obtemEnderecoServidorAlteracaoPeriodo() throws SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_ens_alteracao");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append(" create temporary table tb_tmp_ens_alteracao");
            sql.append(" select ens.ens_codigo ");
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
            sql.append(" group by ens.ens_codigo");
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

            sql.append(" insert into tb_tmp_ens_alteracao");
            sql.append(" select ens.ens_codigo ");
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
            sql.append(" inner join tb_ocorrencia_endereco_ser oes on (oes.ens_codigo = ens.ens_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and oes.toc_codigo = ?");
            sql.append(" and oes.oes_data between pbe_data_ini and pbe_data_fim ");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?,?) ");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" and tib_codigo = ?");
            sql.append(" and ade.sad_codigo in (");
            for (int k = 0; k < CodedValues.SAD_CODIGOS_ATIVOS.size(); k++) {
                sql.append("?,");
            }
            sql.replace(sql.length()-1, sql.length(), ")");
            sql.append(" group by ens.ens_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            i = 1;
            preparedStatement.setString(i++, CodedValues.TOC_ALTERACAO_ENDERECO_SERVIDOR);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SIM);
            preparedStatement.setInt(i++, CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
            preparedStatement.setString(i++, CodedValues.TIB_TITULAR);
            for (String sadCodigo : CodedValues.SAD_CODIGOS_ATIVOS) {
                preparedStatement.setString(i++, sadCodigo);
            }
            preparedStatement.executeUpdate();
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Populando as tabelas auxiliares para conter beneficiarios e endereços servidor não repetidos
     * Essas tabelas que contem as datas de maior alteração no periodo e no passado
     * @param statement
     * @throws DataAccessException
     */
    private void criaEPopulaTabelaAuxiliaresFluxoAlteracao() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_alteracao_datas");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table `tb_tmp_bfc_alteracao_datas` (");
        sql.append("   `bfc_codigo` varchar(32) not null,");
        sql.append("   `rev_data_periodo` datetime default null,");
        sql.append("   `rev_data_passado` datetime default null,");
        sql.append("   `rev_data_escolhido` datetime default null,");
        sql.append("   `processado` varchar(1) character set utf8 not null default 'n'");
        sql.append(" ) engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_bfc_alteracao_datas");
        sql.append(" select distinct bfc_codigo, null, null, null, 'n' from tb_tmp_bfc_alteracao;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ens_alteracao_datas");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table `tb_tmp_ens_alteracao_datas` (");
        sql.append("   `ens_codigo` varchar(32) not null,");
        sql.append("   `rev_data_periodo` datetime default null,");
        sql.append("   `rev_data_passado` datetime default null,");
        sql.append("   `rev_data_escolhido` datetime default null,");
        sql.append("   `processado` varchar(1) character set utf8 not null default 'n'");
        sql.append(" ) engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_ens_alteracao_datas");
        sql.append(" select distinct ens_codigo, null, null, null, 'n' from tb_tmp_ens_alteracao;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Para os beneficarios selecionados busco a maior data de alteração do periodo atual e do passad
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

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
        sql.append(" and rev.rev_data between pbe_data_ini and pbe_data_fim ");
        sql.append(" group by tmp.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
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
        sql.append(" group by tmp.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.bfc_codigo = ajuda.bfc_codigo)");
        sql.append(" set tmp.rev_data_passado = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Para os endereços servidor selecionados busco a maior data de alteração do periodo atual e do passad
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaEnderecoServidorSelecionadosAlteracaoPeriodoAtualEPassado() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.ens_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (ens.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_alteracao tmp on (ens.ens_codigo = tmp.ens_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data between pbe_data_ini and pbe_data_fim ");
        sql.append(" group by tmp.ens_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.ens_codigo = ajuda.ens_codigo)");
        sql.append(" set tmp.rev_data_periodo = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ajuda_update");
        sql.append(" select max(rev.rev_data) as rev_data_maior, tmp.ens_codigo");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (ens.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_alteracao tmp on (ens.ens_codigo = tmp.ens_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data < pbe_data_ini");
        sql.append(" group by tmp.ens_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.ens_codigo = ajuda.ens_codigo)");
        sql.append(" set tmp.rev_data_passado = ajuda.rev_data_maior;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("drop temporary table if exists tb_tmp_ajuda_update;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Para Beneficiario e endereço servidor pode acontecer de somente uma das duplas teve alteração no periodo
     * Então a data escolhida vai ser a mais data de revisão do passado.
     * @param statement
     * @throws DataAccessException
     */
    private void ajustaDatasBeneficiariosEEnderecoServidorSemAlteracaoNoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append(" update tb_tmp_bfc_alteracao_datas");
        sql.append(" set rev_data_escolhido = rev_data_passado , processado = 's'");
        sql.append(" where rev_data_periodo is null; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas");
        sql.append(" set rev_data_escolhido = rev_data_passado , processado = 's'");
        sql.append(" where rev_data_periodo is null; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Com as data de revisao calculadas, analisamos se o Beneficiario teve alteração nos campos de relatorios
     * alterados no periodo e na versão passada dele.
     * @param statement
     * @throws DataAccessException
     */
    private void analisaSeBeneficiarioTeveAlteracaoCamposRelatorio() throws SQLException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_bfc_escolhido");
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
            sql.append(" bfcperiodo.bfc_cpf <> bfcpassado.bfc_cpf");
            sql.append(" or bfcperiodo.bfc_nome <> bfcpassado.bfc_nome");
            sql.append(" or bfcperiodo.bfc_data_nascimento <> bfcpassado.bfc_data_nascimento");
            sql.append(" or bfcperiodo.bfc_sexo <> bfcpassado.bfc_sexo");
            sql.append(" or ( if (bfcperiodo.tib_codigo = ?, false ,bfcperiodo.grp_codigo <> bfcpassado.grp_codigo) )");
            sql.append(" or bfcperiodo.bfc_estado_civil <> bfcpassado.bfc_estado_civil");
            sql.append(" or bfcperiodo.bfc_rg <> bfcpassado.bfc_rg");
            sql.append(" or bfcperiodo.bfc_nome_mae <> bfcpassado.bfc_nome_mae");
            sql.append(" or bfcperiodo.bfc_telefone <> bfcpassado.bfc_telefone");
            sql.append(" or bfcperiodo.bfc_celular <> bfcpassado.bfc_celular");
            sql.append(" );");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            int i = 1;
            preparedStatement.setString(i++, CodedValues.TIB_TITULAR);
            preparedStatement.executeUpdate();
            sql.setLength(0);

            sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
            sql.append(" inner join tb_tmp_bfc_escolhido tmp2 on (tmp.bfc_codigo = tmp2.bfc_codigo)");
            sql.append(" set rev_data_escolhido = rev_data_periodo, processado = 's'; ");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Com as data de revisao calculadas, analisamos se o endereço servidor teve alteração nos campos de relatorios
     * alterados no periodo e na versão passada dele.
     * @param statement
     * @throws DataAccessException
     */
    private void analisaSeEnderecoServidorTeveAlteracaoCamposRelatorio() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_ens_escolhido");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_ens_escolhido");
        sql.append(" select tmp.ens_codigo");
        sql.append(" from tb_tmp_ens_alteracao_datas tmp");
        sql.append(" inner join ta_endereco_servidor ensperiodo on (ensperiodo.ens_codigo = tmp.ens_codigo)");
        sql.append(" inner join tb_revisao_auditoria revperiodo on (revperiodo.rev_codigo = ensperiodo.rev_codigo and revperiodo.rev_data = tmp.rev_data_periodo)");
        sql.append(" inner join ta_endereco_servidor enspassado on (enspassado.ens_codigo = tmp.ens_codigo)");
        sql.append(" inner join tb_revisao_auditoria revpassado on (revpassado.rev_codigo = enspassado.rev_codigo and revpassado.rev_data = tmp.rev_data_passado)");
        sql.append(" where 1 = 1");
        sql.append(" and tmp.processado = 'n'");
        sql.append(" and  (");
        sql.append(" ensperiodo.ens_logradouro <> enspassado.ens_logradouro");
        sql.append(" or ensperiodo.ens_numero <> enspassado.ens_numero");
        sql.append(" or ensperiodo.ens_complemento <> enspassado.ens_complemento");
        sql.append(" or ensperiodo.ens_bairro <> enspassado.ens_bairro");
        sql.append(" or ensperiodo.ens_codigo_municipio <> enspassado.ens_codigo_municipio");
        sql.append(" or ensperiodo.ens_uf <> enspassado.ens_uf");
        sql.append(" or ensperiodo.ens_cep <> enspassado.ens_cep");
        sql.append(" );");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ens_escolhido tmp2 on (tmp.ens_codigo = tmp2.ens_codigo)");
        sql.append(" set rev_data_escolhido = rev_data_periodo, processado = 's'; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Ajustes para não deixarmos qualquer registro de fora.
     * @param statement
     * @throws DataAccessException
     */
    private void ajustaDatasBeneficiarioEEnderecoServidorGeral() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
        sql.append(" set rev_data_escolhido = rev_data_periodo , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" set rev_data_escolhido = rev_data_periodo , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's'; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmpens");
        sql.append(" inner join tb_endereco_servidor ens on (tmpens.ens_codigo = ens.ens_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (bfc.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" set tmpbfc.rev_data_escolhido = coalesce(tmpbfc.rev_data_periodo, tmpbfc.rev_data_passado), tmpbfc.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpbfc.rev_data_escolhido is null");
        sql.append(" and tmpbfc.processado <> 's'");
        sql.append(" and tmpens.rev_data_periodo is not null");
        sql.append(" and tmpens.rev_data_passado is not null");
        sql.append(" and tmpens.rev_data_escolhido is not null");
        sql.append(" and tmpens.processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmpens");
        sql.append(" inner join tb_endereco_servidor ens on (tmpens.ens_codigo = ens.ens_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (bfc.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" set tmpens.rev_data_escolhido = coalesce(tmpens.rev_data_periodo, tmpens.rev_data_passado), tmpens.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpens.rev_data_escolhido is null");
        sql.append(" and tmpens.processado <> 's'");
        sql.append(" and tmpbfc.rev_data_periodo is not null");
        sql.append(" and tmpbfc.rev_data_passado is not null");
        sql.append(" and tmpbfc.rev_data_escolhido is not null");
        sql.append(" and tmpbfc.processado = 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Apos os calculos selecionamos os dados e salvamos na tebela temporaria
     * @param statement
     * @throws DataAccessException
     */
    private void incluiuBeneficiariosEEnderecoServidorAlteracaoPeriodoNaTabelaFinal() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append(" insert into tb_tmp_arquivo_beneficiario");
            sql.append(" select 'A', org.org_codigo, org_identificador, org_identificador_beneficio, rse_matricula, bfc_cpf, bfc_nome, bfc_data_nascimento, bfc_sexo,");
            sql.append(" grp_codigo, bfc_estado_civil, bfc_rg, bfc_nome_mae, ens_logradouro, ens_numero, ens_complemento, ens_bairro, ens_codigo_municipio, ens_uf,");
            sql.append(" ens_cep, bfc_telefone, bfc_celular, cbe_data_inicio_vigencia");
            sql.append(" from tb_aut_desconto ade ");
            sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo and pbe.pbe_sequencia = 0)");
            sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
            sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
            sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_revisao_auditoria revbfc on (revbfc.rev_codigo = bfc.rev_codigo)");
            sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo and tmpbfc.rev_data_escolhido = revbfc.rev_data)");
            sql.append(" inner join tb_revisao_auditoria revens on (revens.rev_codigo = ens.rev_codigo)");
            sql.append(" inner join tb_tmp_ens_alteracao_datas tmpens on (tmpens.ens_codigo = ens.ens_codigo and tmpens.rev_data_escolhido = revens.rev_data)");
            sql.append(" where 1 = 1");
            sql.append(" and (ade_ano_mes_ini < pbe_periodo or ade_ano_mes_ini_ref < pbe_periodo)");
            sql.append(" and ade.ade_int_folha in (?,?) ");
            sql.append(" and tla.tla_codigo_pai is null");
            sql.append(" group by bfc.bfc_codigo, ens.ens_codigo;");
            LOG.info(sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            int i = 1;
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
}