package com.zetra.econsig.persistence.dao.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.ExportaArquivoOperadoraDAO;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficio;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;

/**
 * <p>Title: MysqlExportaArquivoOperadoraDAO</p>
 * <p>Description: Rotinha de exportação do arquivo de operado para o MYSQL</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MysqlExportaArquivoOperadoraDAO implements ExportaArquivoOperadoraDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MysqlExportaArquivoOperadoraDAO.class);

    @Override
    public void exportaArquivoOperadora(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, List<String> tipoOperacaoArquivoOperadora,
        String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, Map<String, String> configuracao, ContratoBeneficioController contratoBeneficioController, boolean permiteCancelarBeneficioSemAprovacao, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {

        try {
            // Cria a tabela final onde vai conters os dados necessarios.
            LOG.info("Inicio da criação da tabela final.");
            criaTabelaFinal();
            LOG.info("Fim da criação da tabela final.");

            // Se for informado a necessidade de gerar linhas de Inclusão chamas os metodos necessarios para realizar o mesmo.
            if (tipoOperacaoArquivoOperadora.contains("I")) {
                LOG.info("Inicio da geração das linhas de inclusão.");
                controlarExportacaoArquivosOperadoraInclusaoContratoBeneficio(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo, contratoBeneficioController, responsavel);
                LOG.info("Fim da geração das linhas de inclusão.");
            }

            // Se for informado a necessidade de gerar linhas de Exclusão chamas os metodos necessarios para realizar o mesmo.
            if (tipoOperacaoArquivoOperadora.contains("E")) {
                LOG.info("Inicio da geração das linhas de exclusão.");
                controlarExportacaoArquivosOperadoraCancelamentoContratoBeneficio(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo, contratoBeneficioController, permiteCancelarBeneficioSemAprovacao, responsavel);
                LOG.info("Fim da geração das linhas de exclusão.");
            }

            // Se for informado a necessidade de gerar linhas de Alteração chamas os metodos necessarios para realizar o mesmo.
            if (tipoOperacaoArquivoOperadora.contains("A")) {
                LOG.info("Inicio da geração das linhas de alteração.");
                controlarExportacaoArquivosOperadoraAlteracao(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo,responsavel);
                LOG.info("Fim da geração das linhas de alteração.");
            }

            // Apos realizar todos os calculos, vamos ajustar os endereços seperando eles por tipo.
            LOG.info("Inicio da rotina que separa o logradouro do tipo logradouro.");
            separaEnderecoLogradouro();
            LOG.info("Fim da rotina que separa o logradouro do tipo logradouro.");

            // Apos realizar todos os calculos vamos salvar o arquivo conforme XML informado.
            LOG.info("Inicio da rotina de salvar os dados no arquivo final.");
            salvaArquivoFinal(configuracao);
            LOG.info("Fim da rotina de salvar os dados no arquivo final.");
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    /**
     * Metodo responsavel por salvar o arquivo no XML informado.
     * @param configuracao
     * @throws ParserException
     */
    private void salvaArquivoFinal(Map<String, String> configuracao) throws ParserException {
        String nomeArqConfSaidaAbsoluto = configuracao.get("nomeArqConfSaida");
        String nomeArqConfEntradaAbsoluto = configuracao.get("nomeArqConfEntrada");
        String nomeArqConfTradutorAbsoluto = configuracao.get("nomeArqConfTradutor");

        String nomeArquivoFinalAbsoluto = configuracao.get("nomeArquivoFinal");

        LOG.info("Iniciando escrita no arquivo: " + nomeArquivoFinalAbsoluto);

        EscritorArquivoTexto escritorArquivoTexto = new EscritorArquivoTexto(nomeArqConfSaidaAbsoluto, nomeArquivoFinalAbsoluto);
        Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntradaAbsoluto, "select * from tb_tmp_integracao_operadora order by field (tipo_operacao, 'E', 'A', 'I'), ben_codigo_contrato, rse_matricula, bfc_ordem_dependencia");
        Tradutor tradutor = new Tradutor(nomeArqConfTradutorAbsoluto, leitor, escritorArquivoTexto);
        tradutor.traduz();
    }

    /**
     * Cria a estrutura da tabela final
     * @param statement
     * @throws DataAccessException
     */
    private void criaTabelaFinal() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("drop temporary table if exists tb_tmp_integracao_operadora");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("create temporary table tb_tmp_integracao_operadora (");
        sql.append("    contador int(11) not null auto_increment,");
        sql.append("    tipo_operacao char(1),");
        sql.append("    ben_codigo_contrato varchar(40),");
        sql.append("    tib_codigo varchar(32),");
        sql.append("    cbe_numero varchar(40),");
        sql.append("    cbe_numero_titular varchar(40),");
        sql.append("    cbe_data_inicio_vigencia datetime,");
        sql.append("    bfc_nome varchar(255),");
        sql.append("    bfc_sexo char(1),");
        sql.append("    grp_codigo varchar(32),");
        sql.append("    bfc_estado_civil char(1),");
        sql.append("    bfc_data_nascimento date,");
        sql.append("    bfc_cpf varchar(19),");
        sql.append("    bfc_nome_mae varchar(100),");
        sql.append("    rse_matricula varchar(20) default '',");
        sql.append("    ens_cep varchar(10),");
        sql.append("    ens_tipo_logradouro varchar(30),");
        sql.append("    ens_nome_logradouro varchar(100),");
        sql.append("    ens_numero varchar(15),");
        sql.append("    ens_complemento varchar(40),");
        sql.append("    ens_bairro varchar(40),");
        sql.append("    ens_municipio varchar(40),");
        sql.append("    ens_uf char(2),");
        sql.append("    bfc_telefone varchar(40),");
        sql.append("    bfc_celular varchar(40),");
        sql.append("    ser_email varchar(100),");
        sql.append("    ben_codigo_plano varchar(40),");
        sql.append("    dad_valor_34 varchar(255),");
        sql.append("    dad_valor_35 varchar(255),");
        sql.append("    tmo_codigo varchar(32),");
        sql.append("    rse_data_admissao datetime,");
        sql.append("    nac_codigo varchar(32),");
        sql.append("    cbe_data_cancelamento date,");
        sql.append("    cse_cnpj varchar(19),");
        sql.append("    bfc_ordem_dependencia varchar(6),");
        sql.append("    ser_cpf varchar(19),");
        sql.append("    ser_pis varchar(40),");
        sql.append("    bfc_rg varchar(40),");
        sql.append("    dad_valor_36 varchar(255),");
        sql.append("    dad_valor_37 varchar(255),");
        sql.append("    bfc_data_obito date,");
        sql.append("    primary key (contador)");
        sql.append(") engine=innodb default charset=latin1;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    // Fluxo de inclusão

    /**
     * Metodo que realizar o controle do fluxo de exportação do arquivo.
     * @param reexporta
     * @param dataArquivoOperadoraMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param resultSet
     * @param responsavel
     * @throws DataAccessException
     * @throws ContratoBeneficioControllerException
     */
    private void controlarExportacaoArquivosOperadoraInclusaoContratoBeneficio(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, ContratoBeneficioController contratoBeneficioController, AcessoSistema responsavel) throws DataAccessException, ContratoBeneficioControllerException {

        LOG.info("Inicio da gravação das linhas de inclusão na tabela final.");
        geraLinhasOperadoraInclusaoContratoBeneficio(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo);
        LOG.info("Fim da gravação das linhas de inclusão na tabela final.");

        if (!reexporta) {
            LOG.info("Inicio da alteração dos status das linhas de inclusão geradas.");
            alteraContratoBeneficioInclusaoLinhasGeradas(csaCodigo, dataFiltroOperacaoMin, dataFiltroOperacaoMax, rseCodigo, orgCodigo, estCodigo, contratoBeneficioController, responsavel);
            LOG.info("Fim da alteração dos status das linhas de inclusão geradas.");
        }
    }

    /**
     * Gera a linha de inclusão com base nas datas calculadas/informadas.
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasOperadoraInclusaoContratoBeneficio(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("insert into tb_tmp_integracao_operadora ");
        sql.append(" (tipo_operacao, ben_codigo_contrato, tib_codigo, cbe_numero, cbe_numero_titular, cbe_data_inicio_vigencia, bfc_nome, bfc_sexo, ");
        sql.append(" grp_codigo, bfc_estado_civil, bfc_data_nascimento, bfc_cpf, bfc_nome_mae, rse_matricula, ens_cep, ens_nome_logradouro, ens_numero, ens_complemento, ");
        sql.append(" ens_bairro, ens_municipio, ens_uf, bfc_telefone, bfc_celular, ser_email, ben_codigo_plano, dad_valor_34, dad_valor_35, tmo_codigo, rse_data_admissao, ");
        sql.append(" nac_codigo, cbe_data_cancelamento, cse_cnpj, bfc_ordem_dependencia, ser_cpf, bfc_rg ,ser_pis, dad_valor_36, dad_valor_37, bfc_data_obito)");
        sql.append(" select");
        sql.append(" 'I',");
        sql.append(" ben.ben_codigo_contrato,");
        sql.append(" bfc.tib_codigo,");
        sql.append(" '' as cbe_numero,");
        sql.append(" if (cbeTitular.cbe_numero is not null, cbeTitular.cbe_numero, '')  as cbe_numero_titular,");
        sql.append(" cbe.cbe_data_inicio_vigencia, ");
        sql.append(" bfc.bfc_nome,");
        sql.append(" bfc.bfc_sexo,");
        sql.append(" bfc.grp_codigo,");
        sql.append(" bfc.bfc_estado_civil,");
        sql.append(" bfc.bfc_data_nascimento,");
        sql.append(" bfc.bfc_cpf,");
        sql.append(" bfc.bfc_nome_mae,");
        sql.append(" rse_matricula,");
        sql.append(" ens_cep,");
        sql.append(" ens_logradouro,");
        sql.append(" ens_numero,");
        sql.append(" ens_complemento,");
        sql.append(" ens_bairro,");
        sql.append(" ens_municipio,");
        sql.append(" ens_uf,");
        sql.append(" bfc.bfc_telefone,");
        sql.append(" bfc.bfc_celular,");
        sql.append(" ser_email,");
        sql.append(" ben.ben_codigo_plano,");
        sql.append(" dad34.dad_valor as periodo_contribuicao,");
        sql.append(" dad35.dad_valor as adesao_plano_ex_funcionario,");
        sql.append(" '' as tmo_codigo,");
        sql.append(" case");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("' then rse.rse_data_admissao");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_AGREGADO).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    else null");
        sql.append(" end as rse_data_admissao,");
        sql.append(" bfc.nac_codigo as nac_codigo,");
        sql.append(" null as cbe_data_cancelamento,");
        sql.append(" cse_cnpj as cse_cnpj,");
        sql.append(" bfc.bfc_ordem_dependencia as bfc_ordem_dependencia,");
        sql.append(" ser.ser_cpf,");
        sql.append(" bfc.bfc_rg,");
        sql.append(" ser.ser_pis,");
        sql.append(" dad36.dad_valor as contribiu_plano,");
        sql.append(" dad37.dad_valor as valor_contribuicao,");
        sql.append(" bfc.bfc_data_obito");
        sql.append(" from tb_contrato_beneficio cbe");
        sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo) ");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo  = bfc.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (tla.tla_codigo = ade.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");

        // Tenta descobrir a carteiria do titular caso ele já tenha um contrato ativo.
        sql.append(" left join tb_beneficiario bfcTitular on (bfcTitular.ser_codigo = ser.ser_codigo and bfcTitular.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("')");
        sql.append(" left join tb_contrato_beneficio cbeTitular on (bfcTitular.bfc_codigo = cbeTitular.bfc_codigo and cbeTitular.ben_codigo = cbe.ben_codigo and cbeTitular.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("')");
        sql.append(" left join tb_beneficio benTitular on (cbeTitular.ben_codigo = benTitular.ben_codigo)");

        sql.append(" left join tb_dados_autorizacao_desconto dad34 on (ade.ade_codigo = dad34.ade_codigo and dad34.tda_codigo = '").append(CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad35 on (ade.ade_codigo = dad35.ade_codigo and dad35.tda_codigo = '").append(CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad36 on (ade.ade_codigo = dad36.ade_codigo and dad36.tda_codigo = '").append(CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad37 on (ade.ade_codigo = dad37.ade_codigo and dad37.tda_codigo = '").append(CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO).append("')");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");

        if (reexporta) {
            sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA).append("'");
        } else {
            sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_SOLICITADO).append("'");
        }

        sql.append(" and ade.sad_codigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        if (reexporta) {
            sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");
            sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
            sql.append(" group by cbe.cbe_codigo;");
        } else {
            sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_APROVACAO_CONTRATO_BENEFICIO).append("'");
            sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
            sql.append(" group by cbe.cbe_codigo;");
        }

        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Lista quais são os contratos e realizamos updates do status do mesmo.
     * @param csaCodigo
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param resultSet
     * @param responsavel
     * @throws DataAccessException
     * @throws ContratoBeneficioControllerException
     */
    private void alteraContratoBeneficioInclusaoLinhasGeradas(String csaCodigo, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, List<String> rseCodigo,
            List<String> orgCodigo, List<String> estCodigo, ContratoBeneficioController contratoBeneficioController, AcessoSistema responsavel) throws DataAccessException, ContratoBeneficioControllerException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append(" select cbe.cbe_codigo from tb_contrato_beneficio cbe");
        sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo) ");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo  = bfc.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (tla.tla_codigo = ade.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_SOLICITADO).append("'");
        sql.append(" and ade.sad_codigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_APROVACAO_CONTRATO_BENEFICIO).append("'");
        sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" group by cbe.cbe_codigo;");

        LOG.info(sql);
        final List<Map<String, Object>> resultSet = jdbc.queryForList(sql.toString(), queryParams);
        sql.setLength(0);

        for (Map<String, Object> row : resultSet) {
            String cbeCodigo = (String) row.get("cbe_codigo");
            ContratoBeneficio contratoBeneficio = contratoBeneficioController.findByPrimaryKey(cbeCodigo, responsavel);
            contratoBeneficio.setStatusContratoBeneficio(new StatusContratoBeneficio(StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo()));
            contratoBeneficioController.update(contratoBeneficio, null, responsavel);
        }
    }

    // Fluxo exclusão

    /**
     * Metodo que controla o fluxo de exportação de linhas de cancelamentos.
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param resultSet
     * @param responsavel
     * @throws DataAccessException
     * @throws ContratoBeneficioControllerException
     */
    private void controlarExportacaoArquivosOperadoraCancelamentoContratoBeneficio(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, ContratoBeneficioController contratoBeneficioController, boolean permiteCancelarBeneficioSemAprovacao, AcessoSistema responsavel) throws DataAccessException, ContratoBeneficioControllerException {

        LOG.info("Inicio do calculo da maior data de alteração de um contrato beneficio");
        obtemMaiorDataOcorrenciaAlteracaoStatusContratoBeneficio(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo, permiteCancelarBeneficioSemAprovacao);
        LOG.info("Fim do calculo da maior data de alteração de um contrato beneficio");

        LOG.info("Inicio da gravação das linhas de exclusão na tabela final.");
        geraLinhasOperadoraCancelamentoContratoBeneficio(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo, permiteCancelarBeneficioSemAprovacao);
        LOG.info("Fim da gravação das linhas de exclusão na tabela final.");


        if (!reexporta) {
            alteraContratoBeneficioCancelamentoLinhasGeradas(csaCodigo, dataFiltroOperacaoMin, dataFiltroOperacaoMax, rseCodigo, orgCodigo, estCodigo, contratoBeneficioController, permiteCancelarBeneficioSemAprovacao, responsavel);
        }
    }

    /**
     * Metodo que pegar a maior ocorencia que tenha o motivo de operação informado.
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @throws DataAccessException
     */
    private void obtemMaiorDataOcorrenciaAlteracaoStatusContratoBeneficio(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, boolean permiteCancelarBeneficioSemAprovacao) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("drop temporary table if exists tb_tmp_max_data_solita_exc_cbe");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_max_data_solita_exc_cbe");
        sql.append(" select max(ocb.ocb_data) as ocb_data, cbe.cbe_codigo");
        sql.append(" from tb_contrato_beneficio cbe");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo  = bfc.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (tla.tla_codigo = ade.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");
        sql.append(" and ocb.tmo_codigo is not null");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");

        if (reexporta) {
            sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA).append("'");
        } else {
            sql.append(" and (cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO).append("'");
            if (permiteCancelarBeneficioSemAprovacao) {
                sql.append(" or cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO).append("'");
            }
            sql.append(")");
        }

        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");

        sql.append(" group by cbe.cbe_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Metodo que grava uma linha de cancelamento na tabela final.
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @throws DataAccessException
     */
    private void geraLinhasOperadoraCancelamentoContratoBeneficio(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, boolean permiteCancelarBeneficioSemAprovacao) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("insert into tb_tmp_integracao_operadora ");
        sql.append(" (tipo_operacao, ben_codigo_contrato, tib_codigo, cbe_numero, cbe_numero_titular, cbe_data_inicio_vigencia, bfc_nome, bfc_sexo, ");
        sql.append(" grp_codigo, bfc_estado_civil, bfc_data_nascimento, bfc_cpf, bfc_nome_mae, rse_matricula, ens_cep, ens_nome_logradouro, ens_numero, ens_complemento, ");
        sql.append(" ens_bairro, ens_municipio, ens_uf, bfc_telefone, bfc_celular, ser_email, ben_codigo_plano, dad_valor_34, dad_valor_35, tmo_codigo, rse_data_admissao, ");
        sql.append(" nac_codigo, cbe_data_cancelamento, cse_cnpj, bfc_ordem_dependencia, ser_cpf, bfc_rg ,ser_pis, dad_valor_36, dad_valor_37, bfc_data_obito)");
        sql.append(" select");
        sql.append(" 'E',");
        sql.append(" ben.ben_codigo_contrato,");
        sql.append(" bfc.tib_codigo,");
        sql.append(" cbe.cbe_numero,");
        sql.append(" '' as cbe_numero_titular,");
        sql.append(" cbe.cbe_data_inicio_vigencia,");
        sql.append(" bfc.bfc_nome,");
        sql.append(" bfc.bfc_sexo,");
        sql.append(" bfc.grp_codigo,");
        sql.append(" bfc.bfc_estado_civil,");
        sql.append(" bfc.bfc_data_nascimento,");
        sql.append(" bfc.bfc_cpf,");
        sql.append(" bfc.bfc_nome_mae,");
        sql.append(" rse_matricula,");
        sql.append(" ens_cep,");
        sql.append(" ens_logradouro,");
        sql.append(" ens_numero,");
        sql.append(" ens_complemento,");
        sql.append(" ens_bairro,");
        sql.append(" ens_municipio,");
        sql.append(" ens_uf,");
        sql.append(" bfc.bfc_telefone,");
        sql.append(" bfc.bfc_celular,");
        sql.append(" ser_email,");
        sql.append(" ben.ben_codigo_plano,");
        sql.append(" dad34.dad_valor as periodo_contribuicao,");
        sql.append(" dad35.dad_valor as adesao_plano_ex_funcionario,");
        sql.append(" ocbTmo.tmo_codigo,");
        sql.append(" case");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("' then rse.rse_data_admissao");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_AGREGADO).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    else null");
        sql.append(" end as rse_data_admissao,");
        sql.append(" bfc.nac_codigo as nac_codigo,");
        sql.append(" cbe.cbe_data_cancelamento as cbe_data_cancelamento,");
        sql.append(" cse_cnpj as cse_cnpj,");
        sql.append(" bfc.bfc_ordem_dependencia as bfc_ordem_dependencia,");
        sql.append(" ser.ser_cpf,");
        sql.append(" bfc.bfc_rg,");
        sql.append(" ser.ser_pis,");
        sql.append(" dad36.dad_valor as contribiu_plano,");
        sql.append(" dad37.dad_valor as valor_contribuicao,");
        sql.append(" bfc.bfc_data_obito");
        sql.append(" from tb_contrato_beneficio cbe");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo  = bfc.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (tla.tla_codigo = ade.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo) ");
        sql.append(" left join tb_tmp_max_data_solita_exc_cbe tmp on (tmp.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" left join tb_ocorrencia_ctt_beneficio ocbTmo on (ocbTmo.cbe_codigo = tmp.cbe_codigo and ocbTmo.ocb_data = tmp.ocb_data)");
        sql.append(" left join tb_dados_autorizacao_desconto dad34 on (ade.ade_codigo = dad34.ade_codigo and dad34.tda_codigo = '").append(CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad35 on (ade.ade_codigo = dad35.ade_codigo and dad35.tda_codigo = '").append(CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad36 on (ade.ade_codigo = dad36.ade_codigo and dad36.tda_codigo = '").append(CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO).append("')");
        sql.append(" left join tb_dados_autorizacao_desconto dad37 on (ade.ade_codigo = dad37.ade_codigo and dad37.tda_codigo = '").append(CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO).append("')");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");

        if (reexporta) {
            sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA).append("'");
        } else {
            sql.append(" and (cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO).append("'");
            if (permiteCancelarBeneficioSemAprovacao) {
                sql.append(" or cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO).append("'");
            }
            sql.append(")");
        }

        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");
        sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" group by cbe.cbe_codigo;");

        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Apos gerar as linhas vamos alterar os status do contrato
     * @param csaCodigo
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param resultSet
     * @param responsavel
     * @throws DataAccessException
     * @throws ContratoBeneficioControllerException
     */
    private void alteraContratoBeneficioCancelamentoLinhasGeradas(String csaCodigo, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, List<String> rseCodigo,
            List<String> orgCodigo, List<String> estCodigo, ContratoBeneficioController contratoBeneficioController, boolean permiteCancelarBeneficioSemAprovacao, AcessoSistema responsavel) throws DataAccessException, ContratoBeneficioControllerException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" cbe.cbe_codigo, ocb.tmo_codigo ");
        sql.append(" from tb_contrato_beneficio cbe");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo  = bfc.bfc_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ser.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (tla.tla_codigo = ade.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo) ");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and (cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO).append("'");
        if (permiteCancelarBeneficioSemAprovacao) {
            sql.append(" or cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO).append("'");
        }
        sql.append(")");
        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");
        sql.append(" and ocb.ocb_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" and ocb.tmo_codigo is not null");
        sql.append(" group by cbe.cbe_codigo;");

        LOG.info(sql);
        final List<Map<String, Object>> resultSet = jdbc.queryForList(sql.toString(), queryParams);
        sql.setLength(0);

        for (Map<String, Object> row : resultSet) {
            String cbeCodigo = (String) row.get("cbe_codigo");
            String tmoCodigo = (String) row.get("tmo_codigo");
            ContratoBeneficio contratoBeneficio = contratoBeneficioController.findByPrimaryKey(cbeCodigo, responsavel);
            contratoBeneficio.setStatusContratoBeneficio(new StatusContratoBeneficio(StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo()));
            contratoBeneficioController.update(contratoBeneficio, tmoCodigo, responsavel);
        }
    }

    /** Inicio bloco de logica de alteração **/

    /**
     * Metodo orquestadro da geração das linhas de alteração.
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param responsavel
     * @throws DataAccessException
     */
    private void controlarExportacaoArquivosOperadoraAlteracao(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo,AcessoSistema responsavel) throws DataAccessException {
        LOG.info("Inicio do calculo dos beneficiarios que tiveram alteração nas datas informada.");
        obtemBeneficiariosAlteracaoPeriodo(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo,responsavel);
        LOG.info("Fim do calculo dos beneficiarios que tiveram alteração nas datas informada.");

        LOG.info("Inicio do calculo dos endereços servidores que tiveram alteração nas datas informada.");
        obtemEnderecoServidorAlteracaoPeriodo(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax, csaCodigo, rseCodigo, orgCodigo, estCodigo,responsavel);
        LOG.info("Fim do calculo dos endereços servidores que tiveram alteração nas datas informada.");

        LOG.info("Inicio da criação das tabelas auxiliares do fluxo de alteração.");
        criaEPopulaTabelaAuxiliaresFluxoAlteracao();
        LOG.info("Fim da criação das tabelas auxiliares do fluxo de alteração.");

        LOG.info("Inicio da obteção da maior revisão do beneficiario selecionado.");
        obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax,responsavel);
        LOG.info("Fim da obteção da maior revisão do beneficiario selecionado.");

        LOG.info("Inicio da obteção da maior revisão do endereço servidor selecionado.");
        obtemMaiorRevisaoParaEnderecoServidorSelecionadosAlteracaoPeriodoAtualEPassado(reexporta, dataFiltroOperacaoMin, dataFiltroOperacaoMax,responsavel);
        LOG.info("Fim da obteção da maior revisão do endereço servidor selecionado.");

        LOG.info("Inicio dos ajustes das datas das revisões escolhidas.");
        ajustaDatasBeneficiariosEEnderecoServidorSemAlteracaoNoPeriodo();
        LOG.info("Fim dos ajustes das datas das revisões escolhidas.");

        LOG.info("Inicio das analise se o beneficio teve alteração nos campos do relatorio.");
        analisaSeBeneficiarioTeveAlteracaoCamposRelatorio();
        LOG.info("Fim das analise se o beneficio teve alteração nos campos do relatorio.");

        LOG.info("Inicio das analise se o endereço servidor teve alteração nos campos do relatorio.");
        analisaSeEnderecoServidorTeveAlteracaoCamposRelatorio();
        LOG.info("Fim das analise se o endereço servidor teve alteração nos campos do relatorio.");

        LOG.info("Inicio dos ajustes das datas das revisões escolhidas.");
        ajustaDatasBeneficiarioEEnderecoServidorGeral();
        LOG.info("Fim das analise se o beneficio teve alteração nos campos do relatorio.");

        LOG.info("Inicio da gravação das linhas de alteração na tabela final.");
        incluiuBeneficiariosEEnderecoServidorAlteracaoPeriodoNaTabelaFinal();
        LOG.info("Fim da gravação das linhas de alteração na tabela final.");
    }

    /**
     * Obtem os beneficiarios que tiveram alteração dos dadados cadastrais e o endereço alterado no periodo.
     * @param reexporta
     * @param dataArquivoOperadora
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param responsavel
     * @throws DataAccessException
     */
    private void obtemBeneficiariosAlteracaoPeriodo(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo,AcessoSistema responsavel) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_ocorrencia_beneficiario obe on (obe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
        sql.append(" and obe.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_BENFICIARIO).append("'");
        sql.append(" and obe.obe_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" group by bfc.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_bfc_alteracao");
        sql.append(" select bfc.bfc_codigo ");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_ocorrencia_endereco_ser oes on (oes.ens_codigo = ens.ens_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
        sql.append(" and oes.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_ENDERECO_SERVIDOR).append("'");
        sql.append(" and oes.oes_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");
        sql.append(" and bfc.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("'");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" group by bfc.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /**
     * Obtem os endereço servidor que tiveram alteração dos dadados cadastrais
     * e que o beneficiario tenha sido alterado no periodo
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param statement
     * @param responsavel
     * @throws DataAccessException
     */
    private void obtemEnderecoServidorAlteracaoPeriodo(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo,AcessoSistema responsavel) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_ocorrencia_beneficiario obe on (obe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
        sql.append(" and obe.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_BENFICIARIO).append("'");
        sql.append(" and obe.obe_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" group by ens.ens_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" insert into tb_tmp_ens_alteracao");
        sql.append(" select ens.ens_codigo ");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_ocorrencia_endereco_ser oes on (oes.ens_codigo = ens.ens_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and cnv.csa_codigo = '").append(csaCodigo).append("'");
        sql.append(" and cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
        sql.append(" and oes.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_ENDERECO_SERVIDOR).append("'");
        sql.append(" and oes.oes_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("')");
        sql.append(" and ens.tie_codigo = '").append(CodedValues.TIE_COBRANCA).append("'");
        sql.append(" and tnt.tnt_codigo in ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')");

        if (!TextHelper.isNull(rseCodigo) && !rseCodigo.isEmpty()) {
            sql.append(" and rse.rse_codigo in (:rseCodigos)");
            queryParams.addValue("rseCodigos", rseCodigo);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.isEmpty()) {
            sql.append(" and org.org_codigo in (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigo);
        }

        if (!TextHelper.isNull(estCodigo) && !estCodigo.isEmpty()) {
            sql.append(" and est.est_codigo in (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigo);
        }

        sql.append(" group by ens.ens_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
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

        sql.append(" create temporary table tb_tmp_bfc_alteracao_datas (");
        sql.append("   bfc_codigo varchar(32) not null,");
        sql.append("   rev_data_atual datetime default null,");
        sql.append("   rev_data_passado datetime default null,");
        sql.append("   rev_data_escolhido datetime default null,");
        sql.append("   processado varchar(1) character set utf8 not null default 'n'");
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

        sql.append(" create temporary table tb_tmp_ens_alteracao_datas (");
        sql.append("   ens_codigo varchar(32) not null,");
        sql.append("   rev_data_atual datetime default null,");
        sql.append("   rev_data_passado datetime default null,");
        sql.append("   rev_data_escolhido datetime default null,");
        sql.append("   processado varchar(1) character set utf8 not null default 'n'");
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
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param statement
     * @param responsavel
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaBeneficiariosSelecionadosAlteracaoPeriodoAtualEPassado(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax,AcessoSistema responsavel) throws DataAccessException {
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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (bfc.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" group by tmp.bfc_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.bfc_codigo = ajuda.bfc_codigo)");
        sql.append(" set tmp.rev_data_atual = ajuda.rev_data_maior;");
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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (bfc.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao tmp on (bfc.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data < '").append(dataFiltroOperacaoMin).append("'");
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
     * @param reexporta
     * @param dataFiltroOperacaoMin
     * @param dataFiltroOperacaoMax
     * @param statement
     * @param responsavel
     * @throws DataAccessException
     */
    private void obtemMaiorRevisaoParaEnderecoServidorSelecionadosAlteracaoPeriodoAtualEPassado(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax,AcessoSistema responsavel) throws DataAccessException {
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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (ens.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_alteracao tmp on (ens.ens_codigo = tmp.ens_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data between '").append(dataFiltroOperacaoMin).append("' and '").append(dataFiltroOperacaoMax).append("'");
        sql.append(" group by tmp.ens_codigo");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ajuda_update ajuda on (tmp.ens_codigo = ajuda.ens_codigo)");
        sql.append(" set tmp.rev_data_atual = ajuda.rev_data_maior;");
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
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_revisao_auditoria rev on (ens.rev_codigo = rev.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_alteracao tmp on (ens.ens_codigo = tmp.ens_codigo)");
        sql.append(" where 1 = 1");
        sql.append(" and rev.rev_data < '").append(dataFiltroOperacaoMin).append("'");
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
        sql.append(" where rev_data_atual is null; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas");
        sql.append(" set rev_data_escolhido = rev_data_passado , processado = 's'");
        sql.append(" where rev_data_atual is null; ");
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
    private void analisaSeBeneficiarioTeveAlteracaoCamposRelatorio() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("drop temporary table if exists tb_tmp_bfc_escolhido");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" create temporary table tb_tmp_bfc_escolhido");
        sql.append(" select tmp.bfc_codigo");
        sql.append(" from tb_tmp_bfc_alteracao_datas tmp");
        sql.append(" inner join ta_beneficiario bfcatual on (bfcatual.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" inner join tb_revisao_auditoria revperiodo on (revperiodo.rev_codigo = bfcatual.rev_codigo and revperiodo.rev_data = tmp.rev_data_atual)");
        sql.append(" inner join ta_beneficiario bfcpassado on (bfcpassado.bfc_codigo = tmp.bfc_codigo)");
        sql.append(" inner join tb_revisao_auditoria revpassado on (revpassado.rev_codigo = bfcpassado.rev_codigo and revpassado.rev_data = tmp.rev_data_passado)");
        sql.append(" where 1 = 1");
        sql.append(" and tmp.processado = 'n'");
        sql.append(" and  (");
        sql.append(" bfcatual.bfc_nome <> bfcpassado.bfc_nome");
        sql.append(" or bfcatual.bfc_sexo <> bfcpassado.bfc_sexo");
        sql.append(" or bfcatual.tib_codigo <> bfcpassado.tib_codigo");
        sql.append(" or bfcatual.bfc_estado_civil <> bfcpassado.bfc_estado_civil");
        sql.append(" or bfcatual.bfc_data_nascimento <> bfcpassado.bfc_data_nascimento");
        sql.append(" or bfcatual.bfc_cpf <> bfcpassado.bfc_cpf");
        sql.append(" or bfcatual.bfc_nome_mae <> bfcpassado.bfc_nome_mae");
        sql.append(" );");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_bfc_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_bfc_escolhido tmp2 on (tmp.bfc_codigo = tmp2.bfc_codigo)");
        sql.append(" set rev_data_escolhido = rev_data_atual, processado = 's'; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
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
        sql.append(" inner join ta_endereco_servidor ensatual on (ensatual.ens_codigo = tmp.ens_codigo)");
        sql.append(" inner join tb_revisao_auditoria revperiodo on (revperiodo.rev_codigo = ensatual.rev_codigo and revperiodo.rev_data = tmp.rev_data_atual)");
        sql.append(" inner join ta_endereco_servidor enspassado on (enspassado.ens_codigo = tmp.ens_codigo)");
        sql.append(" inner join tb_revisao_auditoria revpassado on (revpassado.rev_codigo = enspassado.rev_codigo and revpassado.rev_data = tmp.rev_data_passado)");
        sql.append(" where 1 = 1");
        sql.append(" and tmp.processado = 'n'");
        sql.append(" and  (");
        sql.append(" ensatual.ens_cep <> enspassado.ens_cep");
        sql.append(" or ensatual.ens_logradouro <> enspassado.ens_logradouro");
        sql.append(" or ensatual.ens_numero <> enspassado.ens_numero");
        sql.append(" or ensatual.ens_complemento <> enspassado.ens_complemento");
        sql.append(" or ensatual.ens_bairro <> enspassado.ens_bairro");
        sql.append(" or ensatual.ens_municipio <> enspassado.ens_municipio");
        sql.append(" or ensatual.ens_uf <> enspassado.ens_uf");
        sql.append(" );");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" inner join tb_tmp_ens_escolhido tmp2 on (tmp.ens_codigo = tmp2.ens_codigo)");
        sql.append(" set rev_data_escolhido = rev_data_atual, processado = 's'; ");
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
        sql.append(" set rev_data_escolhido = rev_data_atual , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's';");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmp ");
        sql.append(" set rev_data_escolhido = rev_data_atual , processado = 's'");
        sql.append(" where rev_data_passado is null and processado <> 's'; ");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append(" update tb_tmp_ens_alteracao_datas tmpens");
        sql.append(" inner join tb_endereco_servidor ens on (tmpens.ens_codigo = ens.ens_codigo)");
        sql.append(" inner join tb_beneficiario bfc on (bfc.ser_codigo = ens.ser_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" set tmpbfc.rev_data_escolhido = coalesce(tmpbfc.rev_data_atual, tmpbfc.rev_data_passado), tmpbfc.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpbfc.rev_data_escolhido is null");
        sql.append(" and tmpbfc.processado <> 's'");
        sql.append(" and tmpens.rev_data_atual is not null");
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
        sql.append(" set tmpens.rev_data_escolhido = coalesce(tmpens.rev_data_atual, tmpens.rev_data_passado), tmpens.processado = 's'");
        sql.append(" where 1 = 1");
        sql.append(" and tmpens.rev_data_escolhido is null");
        sql.append(" and tmpens.processado <> 's'");
        sql.append(" and tmpbfc.rev_data_atual is not null");
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
    private void incluiuBeneficiariosEEnderecoServidorAlteracaoPeriodoNaTabelaFinal() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();
        sql.append(" insert into tb_tmp_integracao_operadora");
        sql.append(" (tipo_operacao, ben_codigo_contrato, tib_codigo, cbe_numero, cbe_numero_titular, cbe_data_inicio_vigencia, bfc_nome, bfc_sexo, ");
        sql.append(" grp_codigo, bfc_estado_civil, bfc_data_nascimento, bfc_cpf, bfc_nome_mae, rse_matricula, ens_cep, ens_nome_logradouro, ens_numero, ens_complemento, ");
        sql.append(" ens_bairro, ens_municipio, ens_uf, bfc_telefone, bfc_celular, ser_email, ben_codigo_plano, dad_valor_34, dad_valor_35, tmo_codigo, rse_data_admissao, ");
        sql.append(" nac_codigo, cbe_data_cancelamento, cse_cnpj, bfc_ordem_dependencia, ser_cpf, bfc_rg ,ser_pis, dad_valor_36, dad_valor_37, bfc_data_obito)");
        sql.append(" select");
        sql.append(" 'A',");
        sql.append(" ben.ben_codigo_contrato,");
        sql.append(" bfc.tib_codigo,");
        sql.append(" cbe.cbe_numero,");
        sql.append(" '' as cbe_numero_titular,");
        sql.append(" cbe.cbe_data_inicio_vigencia,");
        sql.append(" bfc.bfc_nome,");
        sql.append(" bfc.bfc_sexo,");
        sql.append(" bfc.grp_codigo,");
        sql.append(" bfc.bfc_estado_civil,");
        sql.append(" bfc.bfc_data_nascimento,");
        sql.append(" bfc.bfc_cpf,");
        sql.append(" bfc.bfc_nome_mae,");
        sql.append(" rse_matricula,");
        sql.append(" ens_cep,");
        sql.append(" ens_logradouro,");
        sql.append(" ens_numero,");
        sql.append(" ens_complemento,");
        sql.append(" ens_bairro,");
        sql.append(" ens_municipio,");
        sql.append(" ens_uf,");
        sql.append(" bfc.bfc_telefone,");
        sql.append(" bfc.bfc_celular,");
        sql.append(" ser_email,");
        sql.append(" ben.ben_codigo_plano,");
        sql.append(" '' as periodo_contribuicao,");
        sql.append(" '' as adesao_plano_ex_funcionario,");
        sql.append(" '' as tmo_codigo,");
        sql.append(" case");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("' then rse.rse_data_admissao");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo = '").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("' then bfc.bfc_data_casamento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_DEPENDENTE).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    when bfc.tib_codigo = '").append(CodedValues.TIB_AGREGADO).append("' and cbe.cbe_numero='' and bfc.grp_codigo not in ('").append(GrauParentescoEnum.COMPANHEIRO.getCodigo()).append("','").append(GrauParentescoEnum.CONJUGE.getCodigo()).append("') then bfc.bfc_data_nascimento");
        sql.append("    else null");
        sql.append(" end as rse_data_admissao,");
        sql.append(" bfc.nac_codigo as nac_codigo,");
        sql.append(" null as cbe_data_cancelamento,");
        sql.append(" cse_cnpj as cse_cnpj,");
        sql.append(" bfc.bfc_ordem_dependencia as bfc_ordem_dependencia,");
        sql.append(" ser.ser_cpf,");
        sql.append(" bfc.bfc_rg,");
        sql.append(" ser.ser_pis,");
        sql.append(" '' as contribiu_plano,");
        sql.append(" '' as valor_contribuicao,");
        sql.append(" '' bfc_data_obito");
        sql.append(" from tb_aut_desconto ade");
        sql.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
        sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
        sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        sql.append(" inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo)");
        sql.append(" inner join tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo)");
        sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo)");
        sql.append(" inner join ta_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        sql.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        sql.append(" inner join ta_endereco_servidor ens on (ens.ser_codigo = ser.ser_codigo)");
        sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
        sql.append(" inner join tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo)");
        sql.append(" inner join tb_revisao_auditoria revbfc on (revbfc.rev_codigo = bfc.rev_codigo)");
        sql.append(" inner join tb_tmp_bfc_alteracao_datas tmpbfc on (tmpbfc.bfc_codigo = bfc.bfc_codigo and tmpbfc.rev_data_escolhido = revbfc.rev_data)");
        sql.append(" inner join tb_revisao_auditoria revens on (revens.rev_codigo = ens.rev_codigo)");
        sql.append(" inner join tb_tmp_ens_alteracao_datas tmpens on (tmpens.ens_codigo = ens.ens_codigo and tmpens.rev_data_escolhido = revens.rev_data)");
        sql.append(" where 1 = 1");
        sql.append(" group by cbe.cbe_codigo, bfc.bfc_codigo, ens.ens_codigo;");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    public void separaEnderecoLogradouro() throws DataAccessException {

        // A lista é adicionada manualmente, pois foi estraida da tabela de cep baseada nos correios, essas tabela,
        // não tem o tipo logradouro separado, foi decidio não criar tabela por ser o único momento que será utilizado essa separação.
        List<String> tiposLogradouro = new ArrayList<>();
        tiposLogradouro.add("Praça");
        tiposLogradouro.add("Rua");
        tiposLogradouro.add("Viaduto");
        tiposLogradouro.add("Largo");
        tiposLogradouro.add("Galeria");
        tiposLogradouro.add("Parque");
        tiposLogradouro.add("Passarela");
        tiposLogradouro.add("Avenida");
        tiposLogradouro.add("Ladeira");
        tiposLogradouro.add("Vila");
        tiposLogradouro.add("Viela");
        tiposLogradouro.add("Jardim");
        tiposLogradouro.add("Beco");
        tiposLogradouro.add("Passagem");
        tiposLogradouro.add("Ponte");
        tiposLogradouro.add("Travessa");
        tiposLogradouro.add("Via");
        tiposLogradouro.add("Alameda");
        tiposLogradouro.add("Vereda");
        tiposLogradouro.add("Rodovia");
        tiposLogradouro.add("Estrada");
        tiposLogradouro.add("Caminho");
        tiposLogradouro.add("Complexo Viário");
        tiposLogradouro.add("Túnel");
        tiposLogradouro.add("Acesso");
        tiposLogradouro.add("Loteamento");
        tiposLogradouro.add("Passeio");
        tiposLogradouro.add("Terminal");
        tiposLogradouro.add("Boulevard");
        tiposLogradouro.add("Corredor");
        tiposLogradouro.add("Calçada");
        tiposLogradouro.add("Trevo");
        tiposLogradouro.add("Servidão");
        tiposLogradouro.add("Rodo Anel");
        tiposLogradouro.add("Trecho");
        tiposLogradouro.add("Quadra");
        tiposLogradouro.add("Ramal");
        tiposLogradouro.add("Praia");
        tiposLogradouro.add("Lago");
        tiposLogradouro.add("Esplanada");
        tiposLogradouro.add("Entrada");
        tiposLogradouro.add("Escadaria");
        tiposLogradouro.add("Cais");
        tiposLogradouro.add("Marginal");
        tiposLogradouro.add("Rotatória");
        tiposLogradouro.add("Pátio");
        tiposLogradouro.add("Chácara");
        tiposLogradouro.add("Ruela");
        tiposLogradouro.add("Ciclovia");
        tiposLogradouro.add("Residencial");
        tiposLogradouro.add("Lagoa");
        tiposLogradouro.add("Campo");
        tiposLogradouro.add("Bosque");
        tiposLogradouro.add("Anel Viário");
        tiposLogradouro.add("Conjunto");
        tiposLogradouro.add("Colônia");
        tiposLogradouro.add("Favela");
        tiposLogradouro.add("Morro");
        tiposLogradouro.add("Estação");
        tiposLogradouro.add("Setor");
        tiposLogradouro.add("Sítio");
        tiposLogradouro.add("Feira");
        tiposLogradouro.add("Distrito");
        tiposLogradouro.add("Aeroporto");

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        for (String tipoLogradouro : tiposLogradouro) {
            sql.append(" UPDATE tb_tmp_integracao_operadora SET ens_nome_logradouro=replace(ens_nome_logradouro,'").append(tipoLogradouro).append("',''), ens_tipo_logradouro='").append(tipoLogradouro).append("'");
            sql.append(" WHERE ens_nome_logradouro regexp '^").append(tipoLogradouro).append("'");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        }

        // Depois de percorrer a lista e mesmo assim ficar algum tipo logradouro vazio, colocamos por padrão o tipo Rua para não sair null no xml.
        sql.append(" UPDATE tb_tmp_integracao_operadora SET ens_tipo_logradouro='Rua' WHERE ens_tipo_logradouro is null or ens_tipo_logradouro=''");
        LOG.info(sql);
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /** Fim bloco de logica de alteração **/
}