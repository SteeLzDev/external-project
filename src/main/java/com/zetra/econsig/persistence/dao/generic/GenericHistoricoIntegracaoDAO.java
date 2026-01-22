package com.zetra.econsig.persistence.dao.generic;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.folha.exportacao.ExportaMovimento;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoIntegracaoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericHistoricoIntegracaoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de histórico de integração. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericHistoricoIntegracaoDAO implements HistoricoIntegracaoDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericHistoricoIntegracaoDAO.class);

    /**
     * Gera o arquivo de exportação de movimento financeiro
     * @param opcaoExportacao : define se é arquivo único, ou arquivo separado por órgão e verba
     * @param orgCodigo : Código do órgão do usuário, caso seja usuário de órgão
     * @param responsavel : usuário responsável pela operação
     * @param exportaMensal : se exporta mensalmente ou não
     * @param exportaPorOrgao : se exporta por órgão ou por estabelecimento
     * @param pathLote : diretório onde são gravados os arquivos de exportação
     * @param pathConf : diretório onde estão os arquivos de configuração da exportação
     * @param nomeArqConfEntrada : nome do arquivo de configuração de entrada da exportação
     * @param nomeArqConfTradutor : nome do arquivo de configuração do tradutor da exportação
     * @param nomeArqConfSaida : nome do arquivo de configuração de saída da exportação
     * @param nomeArqConfEntradaDefault : arquivo default de configuração de entrada da exportação
     * @param nomeArqConfTradutorDefault : arquivo default de configuração do tradutor da exportação
     * @param nomeArqConfSaidaDefault : arquivo default de configuração da saída da exportação
     * @param tdaList : Códigos dos dados de autorização que serão incluídos na tabela
     * @param adeNumeros : Números de ADE para filtrar a geração do arquivo
     * @param exportador : Classe específica de exportação para o sistema
     * @param parametrosExportacao : Configuração da exportação atual
     * @return Uma lista com os nomes dos arquivos de exportação gerados
     * @throws DAOException
     */
    @Override
    public List<String> geraArqExportacao(String opcaoExportacao, String orgCodigo, AcessoSistema responsavel, boolean exportaMensal, boolean exportaPorOrgao,
            String pathLote, String pathConf, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida,
            String nomeArqConfEntradaDefault, String nomeArqConfTradutorDefault, String nomeArqConfSaidaDefault, List<TransferObject> tdaList, List<String> adeNumeros, ExportaMovimento exportador,
            ParametrosExportacao parametrosExportacao) throws DAOException {

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Lista com os nomes dos arquivos de movimento
        final List<String> nomesArquivosSaida = new ArrayList<>();

        final ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();
        final String dia = DateHelper.format(new java.util.Date(), "dd-MM-yyyy");

        try {
            // Monta a query de exportação
            String query = geraQueryExportacao(exportaMensal, tdaList, responsavel);

            if (opcaoExportacao.equals("1")) {
                // Arquivo único com todo o movimento
                String sql = query;

                // Faz a query sem restrição e gera o arquivo de saida
                sql = sql.replaceAll("<<CNV_COD_VERBA>>", "");
                sql = sql.replaceAll("<<EST_CODIGO>>", "");
                sql = sql.replaceAll("<<ORG_CODIGO>>", "");

                // Define o nome do arquivo de saida de exportação de movimento
                String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".txt";
                String nomeArqSaidaAbsoluto = pathLote + File.separatorChar + nomeArqSaida;

                nomesArquivosSaida.add(nomeArqSaidaAbsoluto);
                gravaArquivoExportacao(sql, nomeArqSaidaAbsoluto,
                                       nomeArqConfEntradaDefault,
                                       nomeArqConfTradutorDefault,
                                       nomeArqConfSaidaDefault, tdaList, adeNumeros,
                                       exportador, parametrosExportacao, queryParams);

                // Se param TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN estiver como S, gera arquivo compactado com todos anexos de contrato do período.
                if (ParamSist.getBoolParamSist(CodedValues.TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN, responsavel)) {
                    try {
                        String nomeZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".zip";
                        expDelegate.compactarAnexosAdePeriodo(null, null, null, nomeZip, responsavel);
                    } catch (ConsignanteControllerException csex) {
                        LOG.error(csex.getMessage(), csex);
                    }
                }

            } else {
                // Vários Arquivos Separados por Estabelecimento/Orgao/Verba
                String sql = null;

                if (opcaoExportacao.equals("2")) {
                    // Separação por Estabelecimento/Orgao
                    if (exportaPorOrgao) {
                        sql = "SELECT DISTINCT EST_IDENTIFICADOR, ORG_IDENTIFICADOR, ORG_CODIGO FROM tb_tmp_exportacao";
                    } else {
                        sql = "SELECT DISTINCT EST_IDENTIFICADOR FROM tb_tmp_exportacao";
                    }
                } else if (opcaoExportacao.equals("3")) {
                    // Separação por Verba
                    sql = "SELECT DISTINCT CNV_COD_VERBA FROM tb_tmp_exportacao";
                } else {
                    // Separação Estabelecimento/Orgao/Verba
                    if (exportaPorOrgao) {
                        sql = "SELECT DISTINCT EST_IDENTIFICADOR, ORG_IDENTIFICADOR, ORG_CODIGO, CNV_COD_VERBA FROM tb_tmp_exportacao";
                    } else {
                        sql = "SELECT DISTINCT EST_IDENTIFICADOR, CNV_COD_VERBA FROM tb_tmp_exportacao";
                    }
                }

                String filtro = null;
                String codigo = null;
                String verba = null;
                String xmlEntrada, xmlSaida, xmlTradutor;

                LOG.trace(sql);
                final List<Map<String, Object>> rset = jdbc.queryForList(sql, queryParams);
                for (Map<String, Object> row : rset) {
                    List<String> orgaos = new ArrayList<>();
                    List<String> estabelecimentos = new ArrayList<>();
                    List<String> verbas = new ArrayList<>();

                    // Pega a query original
                    sql = query;

                    // Pega os xml´s default
                    xmlEntrada = nomeArqConfEntradaDefault;
                    xmlSaida = nomeArqConfSaidaDefault;
                    xmlTradutor = nomeArqConfTradutorDefault;

                    // Seta restrição de código de verba
                    if (opcaoExportacao.equals("3") || opcaoExportacao.equals("4")) {
                        verba = (String) row.get("CNV_COD_VERBA");
                        sql = sql.replaceAll("<<CNV_COD_VERBA>>", " AND CNV_COD_VERBA = :verba ");
                        queryParams.addValue("verba", verba);
                        verbas.add(verba);
                    } else {
                        verba = null;
                        sql = sql.replaceAll("<<CNV_COD_VERBA>>", "");
                    }

                    // Seta a restrição sobre órgão/estabelecimento
                    if (opcaoExportacao.equals("2") || opcaoExportacao.equals("4")) {
                        if (!exportaPorOrgao) {
                            codigo = (String) row.get("EST_IDENTIFICADOR");
                            filtro = codigo + (verba != null ? "_" + verba : "");
                            sql = sql.replaceAll("<<EST_CODIGO>>", " AND EST_IDENTIFICADOR = :estIdentificador ");
                            sql = sql.replaceAll("<<ORG_CODIGO>>", "");
                            queryParams.addValue("estIdentificador", codigo);
                            estabelecimentos.add(codigo);

                        } else {
                            codigo = (String) row.get("ORG_CODIGO");
                            filtro = (String) row.get("EST_IDENTIFICADOR") + "_" + (String) row.get("ORG_IDENTIFICADOR") + (verba != null ? "_" + verba : "");
                            sql = sql.replaceAll("<<EST_CODIGO>>", "");
                            sql = sql.replaceAll("<<ORG_CODIGO>>", " AND ORG_CODIGO = :orgIdentificador ");
                            queryParams.addValue("orgIdentificador", codigo);
                            orgaos.add(codigo);

                            // Verifica se o órgão possui layout específico
                            String dirPathConf = pathConf + File.separatorChar + "cse"
                                               + File.separatorChar + codigo + File.separatorChar;

                            if (new File(dirPathConf + nomeArqConfEntrada).exists() &&
                                    new File(dirPathConf + nomeArqConfSaida).exists() &&
                                    new File(dirPathConf + nomeArqConfTradutor).exists()) {

                                // Muda os arquivos de configuração
                                xmlEntrada = dirPathConf + nomeArqConfEntrada;
                                xmlSaida = dirPathConf + nomeArqConfSaida;
                                xmlTradutor = dirPathConf + nomeArqConfTradutor;
                            }
                        }
                    } else {
                        filtro = verba;
                        sql = sql.replaceAll("<<EST_CODIGO>>", "");
                        sql = sql.replaceAll("<<ORG_CODIGO>>", "");
                    }

                    // Define o nome do arquivo de saida de exportação de movimento
                    String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + filtro + "_" + dia + ".txt";
                    String nomeArqSaidaAbsoluto = pathLote + File.separatorChar + nomeArqSaida;

                    nomesArquivosSaida.add(nomeArqSaidaAbsoluto);
                    gravaArquivoExportacao(sql, nomeArqSaidaAbsoluto, xmlEntrada,
                                           xmlTradutor, xmlSaida, tdaList, adeNumeros,
                                           exportador, parametrosExportacao, queryParams);

                    // Se param TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN estiver como S, gera arquivo compactado com anexos da entidade escolhida
                    if (ParamSist.getBoolParamSist(CodedValues.TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN, responsavel)) {
                        try {
                            String nomeZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + filtro + "_" + dia + ".zip";
                            expDelegate.compactarAnexosAdePeriodo(orgaos, estabelecimentos, verbas, nomeZip, responsavel);
                        } catch (ConsignanteControllerException csex) {
                            LOG.error(csex.getMessage(), csex);
                        }
                    }
                }
            }
            return nomesArquivosSaida;

        } catch (DAOException ex) {
            removerArquivosExportacao(nomesArquivosSaida);
            throw ex;
        } catch (final DataAccessException ex) {
            removerArquivosExportacao(nomesArquivosSaida);
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected abstract String geraQueryExportacao(boolean exportaMensal, List<TransferObject> tdaList, AcessoSistema responsavel);
    protected abstract void gravaArquivoExportacao(String sql, String nomeArqSaida, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida, List<TransferObject> tdaList, List<String> adeNumeros, ExportaMovimento exportador, ParametrosExportacao parametrosExportacao, MapSqlParameterSource queryParams) throws DAOException;

    /**
     * Gera cláusula para junção da tabela tb_tmp_exportacao com a tabela
     * tb_dados_autorizacao_desconto para inclusão dos valores na tabela
     * final de exportação.
     * @param tdaList : Tipos de dados que serão gerados
     * @param apelidos : TRUE para retornar apenas os apelidos das colunas
     * @param definicaoCampos : TRUE para retornar a definição da criação das colunas
     * @param campos : TRUE para retornar os campos para o select
     * @param camposApelidos : TRUE para retornar os campos e apelidos (campo as apelido)
     * @param tabelas : TRUE para retornar o join com as tabelas
     * @param tipoCampoValor : tipo do campo a ser criado, quando definicaoCampos for TRUE, default: varchar(255)
     * @return
     */
    protected String gerarClausulaDadosAutorizacao(List<TransferObject> tdaList, boolean apelidos, boolean definicaoCampos, boolean campos, boolean camposApelidos, boolean tabelas, String tipoCampoValor) {
        StringBuilder clausula = new StringBuilder();
        if (tdaList != null && !tdaList.isEmpty()) {
            for (TransferObject tda : tdaList) {
                String tdaCodigo = tda.getAttribute(Columns.TDA_CODIGO).toString();
                String tenCodigo = tda.getAttribute(Columns.TDA_TEN_CODIGO).toString();

                String nomeColunaValor = Columns.getColumnName(Columns.DAD_VALOR);
                String nomeColunaTdaCodigo = Columns.getColumnName(Columns.DAD_TDA_CODIGO);
                String nomeColunaAdeCodigo = Columns.getColumnName(Columns.DAD_ADE_CODIGO);
                String nomeTabela = Columns.TB_DADOS_AUTORIZACAO_DESCONTO;
                String prefixo = "dad";
                String nomeColunaTmp = "tb_tmp_exportacao.ade_codigo";

                if (tenCodigo.equals(Log.AUTORIZACAO) || tenCodigo.equals(Log.BENEFICIARIO)) {
                    nomeColunaValor = Columns.getColumnName(Columns.DAD_VALOR);
                    nomeColunaTdaCodigo = Columns.getColumnName(Columns.DAD_TDA_CODIGO);
                    nomeColunaAdeCodigo = Columns.getColumnName(Columns.DAD_ADE_CODIGO);
                    nomeTabela = Columns.TB_DADOS_AUTORIZACAO_DESCONTO;
                    prefixo = "dad";
                    nomeColunaTmp = "tb_tmp_exportacao.ade_codigo";

                } else if (tenCodigo.equals(Log.SERVIDOR)) {
                    nomeColunaValor = Columns.getColumnName(Columns.DAS_VALOR);
                    nomeColunaTdaCodigo = Columns.getColumnName(Columns.DAS_TDA_CODIGO);
                    nomeColunaAdeCodigo = Columns.getColumnName(Columns.DAS_SER_CODIGO);
                    nomeTabela = Columns.TB_DADOS_SERVIDOR;
                    prefixo = "das";
                    nomeColunaTmp = "tb_tmp_exportacao.ser_codigo";

                } else {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.tipo.entidade.invalido.para.tipo.dado.adicional", AcessoSistema.getAcessoUsuarioSistema(), tenCodigo, tdaCodigo));
                    continue;
                }

                if (apelidos) {
                    clausula.append(nomeColunaValor + "_" + tdaCodigo);
                } else if (definicaoCampos) {
                    clausula.append(nomeColunaValor + "_" + tdaCodigo + " " + (tipoCampoValor != null ? tipoCampoValor : "varchar(255)"));
                } else if (campos) {
                    clausula.append(prefixo + tdaCodigo + "." + nomeColunaValor);
                } else if (camposApelidos) {
                    clausula.append(prefixo + tdaCodigo + "." + nomeColunaValor + " as " + nomeColunaValor + "_" + tdaCodigo);
                } else if (tabelas) {
                    clausula.append("left outer join " + nomeTabela + " " + prefixo + tdaCodigo);
                    clausula.append(" on (" + prefixo + tdaCodigo + "." + nomeColunaAdeCodigo + " = " + nomeColunaTmp);
                    clausula.append(" and " + prefixo + tdaCodigo + "." + nomeColunaTdaCodigo + " = '" + tdaCodigo + "')");
                }

                clausula.append(!tabelas ? ", " : " ");
            }
        }
        return clausula.toString();
    }

    /**
     * Caso tenha ocorrido erro na exportação, renomeia os arquivos gerados
     * colocando extensão indicando erro na geração.
     * @param nomesArquivosSaida
     */
    protected void removerArquivosExportacao(List<String> nomesArquivosSaida) {
        // Renomeia os arquivos TXT
        if (nomesArquivosSaida != null && nomesArquivosSaida.size() > 0) {
            Iterator<String> it = nomesArquivosSaida.iterator();
            while (it.hasNext()) {
                String fileName = it.next();
                if (!TextHelper.isNull(fileName) && new File(fileName).exists()) {
                    FileHelper.rename(fileName, fileName + ".err");
                }
            }
        }
    }

    /**
     * Insere na tabela de contratos exportados (tb_arquivo_movimento) o resultado da rotina de exportação, contido
     * na "tb_tmp_exportacao_ordenada", independente da forma de exportação, se arquivo geral ou por órgãos/estabelecimentos.
     */
    @Override
    public void gravarTabelaExportacao() throws DAOException {
        Object paramQtdPeriodosTbExportacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_PERIODOS_MANTIDOS_NA_TABELA_MOVIMENTO, AcessoSistema.getAcessoUsuarioSistema());
        int qtdPeriodos = (!TextHelper.isNull(paramQtdPeriodosTbExportacao) ? Integer.parseInt(paramQtdPeriodosTbExportacao.toString()) : 0);
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        AutorizacaoDAO adeDAO = daoFactory.getAutorizacaoDAO();
        if (qtdPeriodos > 0) {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {
                String query = "INSERT INTO tb_arquivo_movimento (ARM_SITUACAO, PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, SER_CPF, RSE_MATRICULA, ORG_IDENTIFICADOR, EST_IDENTIFICADOR, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, CNV_COD_VERBA, ADE_NUMERO, ADE_INDICE, ADE_DATA, ADE_ANO_MES_INI, ADE_ANO_MES_FIM, ADE_PRAZO, ADE_VLR) "
                             + "SELECT SITUACAO, PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, SER_CPF, RSE_MATRICULA, ORG_IDENTIFICADOR, EST_IDENTIFICADOR, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, CNV_COD_VERBA, ADE_NUMERO, ADE_INDICE, ADE_DATA, DATA_INI_CONTRATO, DATA_FIM_CONTRATO, NRO_PARCELAS, VALOR_DESCONTO "
                             + "FROM tb_tmp_exportacao_ordenada";
                LOG.trace(query);
                jdbc.update(query, queryParams);
                adeDAO.atualizaAdeUltPeriodoExportado();

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
    }

    protected void traduzirArquivoExportacao(String nomeArqConfEntrada, String nomeArqConfTradutor, Escritor escritor, List<String> adeNumeros) throws DAOException {
        if (adeNumeros != null && !adeNumeros.isEmpty()) {
            // Se é exportação complementar, e foi passada uma lista de ADEs, então remove da tabela
            // de exportação o que não se refere a estas ADEs a serem enviadas

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {
                final String query = "DELETE FROM tb_tmp_exportacao_ordenada WHERE ADE_NUMERO NOT IN (:adeNumeros) ";
                queryParams.addValue("adeNumeros", adeNumeros);
                LOG.trace(query);
                jdbc.update(query, queryParams);
            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }

        try {
            String queryArquivo = "SELECT * FROM tb_tmp_exportacao_ordenada ORDER BY contador";
            Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntrada, queryArquivo);
            Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    protected void atualizaAdeIndiceQuandoFolhaNaoAceitaAlteracao() throws DAOException {
        // Se permite cadastrar e alterar índices, e a folha de movimento inicial não aceita alteração
        // altera o comando de "A" que será mapeado para "E" de modo que o campo índice tenha o valor
        // anterior, gravado no TDA 44, e não o valor atual da ADE
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_INDICE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                ParamSist.paramEquals(CodedValues.TPC_FOLHA_ACEITA_ALTERACAO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {


            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {
                StringBuilder query = new StringBuilder();
                query.append("update tb_tmp_exportacao_ordenada tmp ");

                query.append("set tmp.ade_indice = coalesce((");
                query.append("  select dad.dad_valor ");
                query.append("  from tb_aut_desconto ade ");
                query.append("  inner join tb_dados_autorizacao_desconto dad on (dad.ade_codigo = ade.ade_codigo and dad.tda_codigo = '").append(CodedValues.TDA_INDICE_ANTERIOR).append("') ");
                query.append("  where tmp.ade_numero = ade.ade_numero ");
                query.append("), tmp.ade_indice) ");

                query.append("where tmp.situacao = 'A' ");
                query.append("and exists ( ");
                query.append("  select 1 ");
                query.append("  from tb_aut_desconto ade ");
                query.append("  inner join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo) ");
                query.append("  where tmp.ade_numero = ade.ade_numero ");
                query.append("    and oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_INDICE).append("' ");
                query.append("    and oca.oca_periodo = tmp.pex_periodo ");
                query.append(") ");

                LOG.trace(query);
                int linhasAfetadas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhasAfetadas);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
    }

    protected List<String> validarCamposTabelaExportacao(String camposOrdem) {
        List<String> camposList = null;
        if (!TextHelper.isNull(camposOrdem)) {
            camposList = new ArrayList<>();
            String[] campos = camposOrdem.split(",");
            for (String campo : campos) {
                campo = campo.trim().toLowerCase();
                boolean validado = switch (campo) {
                    case "ade_ano_mes_fim" -> true;
                    case "ade_ano_mes_fim_folha" -> true;
                    case "ade_ano_mes_fim_folha_q" -> true;
                    case "ade_ano_mes_fim_q" -> true;
                    case "ade_ano_mes_fim_ref" -> true;
                    case "ade_ano_mes_fim_ref_q" -> true;
                    case "ade_ano_mes_ini" -> true;
                    case "ade_ano_mes_ini_folha" -> true;
                    case "ade_ano_mes_ini_folha_q" -> true;
                    case "ade_ano_mes_ini_q" -> true;
                    case "ade_ano_mes_ini_ref" -> true;
                    case "ade_ano_mes_ini_ref_q" -> true;
                    case "ade_cod_reg" -> true;
                    case "ade_data" -> true;
                    case "ade_data_exclusao" -> true;
                    case "ade_data_ref" -> true;
                    case "ade_identificador" -> true;
                    case "ade_inc_margem" -> true;
                    case "ade_indice" -> true;
                    case "ade_numero" -> true;
                    case "ade_prazo" -> true;
                    case "ade_prazo_exc" -> true;
                    case "ade_prazo_folha" -> true;
                    case "ade_prd_pagas" -> true;
                    case "ade_prd_pagas_exc" -> true;
                    case "ade_tipo_vlr" -> true;
                    case "ade_vlr" -> true;
                    case "ade_vlr_folha" -> true;
                    case "autoriza_pgt_parcial" -> true;
                    case "capital_devido" -> true;
                    case "capital_pago" -> true;
                    case "cnv_cod_verba" -> true;
                    case "cnv_cod_verba_ref" -> true;
                    case "cnv_prioridade" -> true;
                    case "codigo_folha" -> true;
                    case "competencia" -> true;
                    case "contador" -> true;
                    case "csa_cnpj" -> true;
                    case "csa_identificador" -> true;
                    case "data" -> true;
                    case "data_desconto" -> true;
                    case "data_desconto_q" -> true;
                    case "data_fim_contrato" -> true;
                    case "data_fim_contrato_q" -> true;
                    case "data_ini_contrato" -> true;
                    case "data_ini_contrato_q" -> true;
                    case "est_cnpj" -> true;
                    case "est_identificador" -> true;
                    case "nro_parcelas" -> true;
                    case "oca_periodo" -> true;
                    case "oca_periodo_q" -> true;
                    case "org_cnpj" -> true;
                    case "org_identificador" -> true;
                    case "percentual_padrao" -> true;
                    case "periodo" -> true;
                    case "pex_periodo" -> true;
                    case "pex_periodo_ant" -> true;
                    case "pex_periodo_ant_q" -> true;
                    case "pex_periodo_pos" -> true;
                    case "pex_periodo_pos_q" -> true;
                    case "pex_periodo_q" -> true;
                    case "pos_codigo" -> true;
                    case "prazo_restante" -> true;
                    case "prd_numero" -> true;
                    case "rse_associado" -> true;
                    case "rse_margem" -> true;
                    case "rse_margem_2" -> true;
                    case "rse_margem_3" -> true;
                    case "rse_margem_rest" -> true;
                    case "rse_margem_rest_2" -> true;
                    case "rse_margem_rest_3" -> true;
                    case "rse_matricula" -> true;
                    case "rse_matricula_inst" -> true;
                    case "rse_obs" -> true;
                    case "rse_tipo" -> true;
                    case "saldo_devedor" -> true;
                    case "ser_cpf" -> true;
                    case "ser_nacionalidade" -> true;
                    case "ser_nome" -> true;
                    case "ser_nome_mae" -> true;
                    case "ser_nome_meio" -> true;
                    case "ser_nome_pai" -> true;
                    case "ser_primeiro_nome" -> true;
                    case "ser_ultimo_nome" -> true;
                    case "situacao" -> true;
                    case "srs_codigo" -> true;
                    case "svc_descricao" -> true;
                    case "svc_identificador" -> true;
                    case "svc_prioridade" -> true;
                    case "trs_codigo" -> true;
                    case "valor_desconto" -> true;
                    case "valor_desconto_exc" -> true;
                    case "valor_desconto_folha" -> true;
                    case String s -> s.matches("dad_valor_[a-zA-Z0-9]+");
                };
                if (validado) {
                    camposList.add(campo);
                }
            }
        }
        return camposList;
    }
}
