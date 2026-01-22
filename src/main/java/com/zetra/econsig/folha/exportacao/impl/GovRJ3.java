package com.zetra.econsig.folha.exportacao.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorListTO;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: GovRJ3</p>
 * <p>Description: Implementações específicas para GovRJ - Governo do Estado do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GovRJ3 extends ExportaMovimentoBase {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GovRJ3.class);

    // DESENV-16229 : Contratos anteriores a 14/02/2016 devem respeitar as margens de 40/50
    protected static final String DATA_INI_MUDANCA_MARGEM = "2016-02-14 00:00:00";

    // DESENV-17172 : Alterar a classe de exportação do movimento (margem 30%-35%)
    protected static final String DATA_INI_MARGEM_ADICIONAL = "2021-12-15 00:00:00";
    protected static final String DATA_FIM_MARGEM_ADICIONAL = "2021-12-31 23:59:59";

    // As margens devem estar casadas à esquerda na ordem da maior para menor: 100, 104, 102
    protected static final Short MARGEM_CARTAO_50 = Short.valueOf("100"); // Margem antiga, contratos feitos até 14/02/2016
    protected static final Short MARGEM_CARTAO_35 = Short.valueOf("102"); // Margem atual, as ADEs são lançadas nesta margem
    protected static final Short MARGEM_CARTAO_40 = Short.valueOf("104"); // Margem adicional, para contratos feitos entre 15/12/2021 e 31/12/2021

    // As margens devem estar casadas à esquerda na ordem da maior para menor: 101, 105, 103
    protected static final Short MARGEM_CONSIGNACAO_40 = Short.valueOf("101"); // Margem antiga, contratos feitos até 14/02/2016
    protected static final Short MARGEM_CONSIGNACAO_30 = Short.valueOf("103"); // Margem atual, as ADEs são lançadas nesta margem
    protected static final Short MARGEM_CONSIGNACAO_35 = Short.valueOf("105"); // Margem adicional, para contratos feitos entre 15/12/2021 e 31/12/2021

    protected Date inicioPeriodoMargemAdicional;
    protected Date fimPeriodoMargemAdicional;

    protected static final String SUFIXO_ARQUIVO_DIFERENCAS = ".dif";
    protected static final String SUFIXO_ARQUIVO_DIFERENCAS_VAZIO = ".vazio";

    public GovRJ3() {
        try {
            inicioPeriodoMargemAdicional = DateHelper.parse(DATA_INI_MARGEM_ADICIONAL, "yyyy-MM-dd HH:mm:ss");
            fimPeriodoMargemAdicional = DateHelper.parse(DATA_FIM_MARGEM_ADICIONAL, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            LOG.error(e);
        }
    }

    /**
     * Lista os contratos de servidores com margem negativa, deconsiderando as mudanças após o corte, pela ordem de exportação
     * A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
     * contratos removidos à margem restante até que a mesma seja positiva.
     * @param stat
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_data, tmp.ade_vlr, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial  ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where tmp.ade_inc_margem not in ('0','1','2','3') ");
        query.append("and (");
        query.append("select mrs_margem_rest + coalesce((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) ");
        query.append("from tb_margem_registro_servidor mrs ");
        query.append("where mrs.rse_codigo = tmp.rse_codigo ");
        query.append("and mrs.mar_codigo = (");
        query.append("case ");

        // DESENV-16229 : Contratos anteriores a 14/02/2016 devem respeitar as margens de 40/50
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CARTAO_35 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CARTAO_50 + " ");
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CONSIGNACAO_30 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CONSIGNACAO_40 + " ");

        // DESENV-17172 : Alterar a classe de exportação do movimento (margem 30%-35%)
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CARTAO_35 + " and (tmp.ade_data between '" + DATA_INI_MARGEM_ADICIONAL + "' and '" + DATA_FIM_MARGEM_ADICIONAL + "') then " + MARGEM_CARTAO_40 + " ");
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CONSIGNACAO_30 + " and (tmp.ade_data between '" + DATA_INI_MARGEM_ADICIONAL + "' and '" + DATA_FIM_MARGEM_ADICIONAL + "') then " + MARGEM_CONSIGNACAO_35 + " ");

        // Demais casos, valida contra a margem ao qual incide
        query.append("  else tmp.ade_inc_margem end) ");
        query.append(") < 0.00 ");
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, tmp.ade_data DESC, tmp.ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_data,ade_vlr,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }


    /**
     * Navega na lista de contratos passada e verifica se este pode
     * ser exportado de acordo com a margem restante do servidor
     * @param contratos
     * @param permiteDescontoParcial
     * @param verificaParamCsaPgParcial - determina se verifica o parâmetro de CSA que permite ou não pagamento parcial
     * @return
     * @throws ExportaMovimentoException
     */
    @Override
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            ContratosSemMargem adeImpropria = new ContratosSemMargem();
            ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

            if (contratos != null && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                Short adeIncMargemValidacao = null;
                Date adeData = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                List<String> adeRemovidas = new ArrayList<>();
                Map<Short, BigDecimal> margemFolha = new HashMap<>();
                Map<Short, BigDecimal> margemRestante = new HashMap<>();
                Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                // Transforma as datas em objetos Date para comparação
                Date dataInicioMudancaMargem = DateHelper.parse(DATA_INI_MUDANCA_MARGEM,   "yyyy-MM-dd HH:mm:ss");
                Date dataIniMargemAdicional  = DateHelper.parse(DATA_INI_MARGEM_ADICIONAL, "yyyy-MM-dd HH:mm:ss");
                Date dataFimMargemAdicional  = DateHelper.parse(DATA_FIM_MARGEM_ADICIONAL, "yyyy-MM-dd HH:mm:ss");

                Iterator<TransferObject> it = contratos.iterator();
                while (it.hasNext()) {
                    TransferObject contrato = it.next();
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());
                    final Object adeDataObj = contrato.getAttribute("ade_data");
                    adeData = DateHelper.objectToDate(adeDataObj);

                    // Inicialmente a margem usada para validação será a margem ao qual a ADE incide
                    adeIncMargemValidacao = adeIncMargem;

                    // DESENV-16229 : Contratos anteriores a 14/02/2016 devem respeitar as margens de 40/50
                    adeIncMargemValidacao = (adeIncMargem == MARGEM_CONSIGNACAO_30.shortValue() && adeData.compareTo(dataInicioMudancaMargem) < 0) ? MARGEM_CONSIGNACAO_40 : adeIncMargemValidacao;
                    adeIncMargemValidacao = (adeIncMargem == MARGEM_CARTAO_35.shortValue() && adeData.compareTo(dataInicioMudancaMargem) < 0) ? MARGEM_CARTAO_50 : adeIncMargemValidacao;

                    // DESENV-17172 : Alterar a classe de exportação do movimento (margem 30%-35%)
                    adeIncMargemValidacao = (adeIncMargem == MARGEM_CONSIGNACAO_30.shortValue() && adeData.compareTo(dataIniMargemAdicional) >= 0 && adeData.compareTo(dataFimMargemAdicional) <= 0) ? MARGEM_CONSIGNACAO_35 : adeIncMargemValidacao;
                    adeIncMargemValidacao = (adeIncMargem == MARGEM_CARTAO_35.shortValue() && adeData.compareTo(dataIniMargemAdicional) >= 0 && adeData.compareTo(dataFimMargemAdicional) <= 0) ? MARGEM_CARTAO_40 : adeIncMargemValidacao;

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();
                        adeRemovidas.clear();

                        List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (MargemTO margemTO : margens) {
                            Short marCodigo = margemTO.getMarCodigo();
                            margemFolha.put(marCodigo, margemTO.getMrsMargem());
                            margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                            margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            BigDecimal margemUsadaAposCorte = calcularMargemUsadaPosCorte(rseCodigo, marCodigo, responsavel);
                            margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaAposCorte));
                            margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaAposCorte));
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    // Margem restante para validação usa o adeIncMargemValidacao
                    BigDecimal margemRestExportacao = margemRestante.get(adeIncMargemValidacao);

                    // Se o contrato incide na margem sem adicional (Cartão 35% e Empréstimo 30%) mas está fora do período
                    // em que foi concedido margem adicional (15/12/2021 a 31/12/2021), verifica se ela está negativa por
                    // existir lançamento que deve abater da margem adicional.
                    if ((adeIncMargemValidacao.equals(MARGEM_CARTAO_35) || adeIncMargemValidacao.equals(MARGEM_CONSIGNACAO_30)) &&
                            (adeData.compareTo(inicioPeriodoMargemAdicional) < 0 || adeData.compareTo(fimPeriodoMargemAdicional) > 0) && margemRestExportacao !=null &&
                            margemRestExportacao.signum() < 0) {
                        BigDecimal margemRestComAdicional = margemRestante.get(adeIncMargemValidacao.equals(MARGEM_CARTAO_35) ? MARGEM_CARTAO_40 : MARGEM_CONSIGNACAO_35);
                        // Se a margem sem adicional está negativa, mas a margem com adicional está positiva
                        // verifica se existem consignações incidindo na margem adicional que tornaram a margem
                        // sem adicional negativa. Caso tenha, reverte estes valores para a margem sem adiconal
                        if (margemRestComAdicional != null && margemRestComAdicional.signum() >= 0) {
                            BigDecimal contratosIncMargemAdicional = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, inicioPeriodoMargemAdicional, fimPeriodoMargemAdicional, adeIncMargemValidacao, adeRemovidas, responsavel);
                            margemRestExportacao = margemRestExportacao.add(contratosIncMargemAdicional);
                        }
                    }

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestExportacao != null && margemRestExportacao.signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        // Passa o valor negativo, pois está sendo somado à margem
                        atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);

                        // Obtém novamente a margem restante do qual a ADE deve validar
                        margemRestExportacao = margemRestante.get(adeIncMargemValidacao);

                        if (!permiteDescontoParcial || margemRestExportacao.signum() <= 0) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                            adeRemovidas.add(adeCodigo);
                        } else if (verificaParamCsaPgParcial) {
                            if (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && contrato.getAttribute("autoriza_pgt_parcial").equals(CodedValues.TPA_SIM)) {
                                // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                                // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                                adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestExportacao);
                            } else {
                                // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
                                // lista dos contratos a serem removidos do movimento
                                adeImpropria.addContratoSemMargem(adeCodigo);
                                adeRemovidas.add(adeCodigo);
                            }
                        } else {
                            // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                            // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                            adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestExportacao);
                        }
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    /**
     * Atualiza o valor dos contratos para pagamento parcial daqueles que não cabem integralmente na
     * margem, em sistemas que permite esta rotina, somente na tabela de exportação
     * @param parcialmenteSemMargem
     * @param stat
     * @throws DataAccessException
     */
    private void atualizarAdeDataContratosProvisionamentoMargem() throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            /**
             * DESENV-15666:
             * 1) Para a apresentação da licitação do Governo do RJ foi criada uma classe de exportação específica para eles (GovRJ.java) na DESENV-15333.
             * 1.1) Excluir todas as regras implementadas nessa classe.
             * 1.2) Criar nova regra na classe de exportação GovRJ.java:
             *    Caso o registro da tb_tmp_exportacao_ordenada corresponda a um serviço relacionado a outro que não integra folha (tps_codigo = '2', "TPS_INTEGRA_FOLHA")
             *    e que tem relacionamento de provisionamento de margem (tb_tipo_natureza = '3', "Relacionamento para Provisionamento de Margem"),
             *    então a ade_data do registro da tb_tmp_exportacao_ordenada deve ser atualizada com a ade_data do serviço que não integra folha
             *    e que é do tipo natureza relacionamento para provisionamento de margem.
             * 1.3) Se houver mais de um lançamento vinculados a um mesmo serviço, utilizar a mesma data pra todos.
            */
            StringBuilder query = new StringBuilder();
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("inner join tb_relacionamento_servico rel on (svc.svc_codigo = rel.svc_codigo_destino and rel.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("')  ");
            query.append("inner join tb_relacionamento_autorizacao rad on (rad.ade_codigo_destino = ade.ade_codigo and rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("') ");
            query.append("inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
            query.append("set tmp.ade_data = adeOrigem.ade_data ");

            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }


    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem

        LOG.debug("GovRJ.atualizarAdeDataContratosProvisionamentoMargem: " + DateHelper.getSystemDatetime());
        atualizarAdeDataContratosProvisionamentoMargem();
        LOG.debug("fim - GovRJ.atualizarAdeDataContratosProvisionamentoMargem: " + DateHelper.getSystemDatetime());
        LOG.debug("GovRJ.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - GovRJ.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    /**
    *
    * @param diferencas
    * @param situacao
    * @param stat
    * @param sql
    * @throws SQLException
    * @throws ParseException
    */
   private void adicionarDiferenca(List<TransferObject> diferencas, String situacao, String sql) throws SQLException, ParseException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
            int nrCampos = rs.getMetaData().getColumnCount();
            Date dataFormatada = null;
            while (rs.next()) {
                CustomTransferObject diferenca = new CustomTransferObject();
                for (int i = 1; i <= nrCampos; i++) {
                    diferenca.setAttribute(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                if (!TextHelper.isNull(diferenca.getAttribute("ade_data"))) {
                    dataFormatada = DateHelper.objectToDate(diferenca.getAttribute("ade_data"));
                    diferenca.setAttribute("ade_data", DateHelper.format(dataFormatada, "yyyy-MM-dd HH:mm:ss"));
                }
                if (!TextHelper.isNull(diferenca.getAttribute("ade_data_exclusao"))) {
                    dataFormatada = DateHelper.objectToDate(diferenca.getAttribute("ade_data_exclusao"));
                    diferenca.setAttribute("ade_data_exclusao", DateHelper.format(dataFormatada, "yyyy-MM-dd HH:mm:ss"));
                }
                if (!TextHelper.isNull(diferenca.getAttribute("ade_data_ref"))) {
                    dataFormatada = DateHelper.objectToDate(diferenca.getAttribute("ade_data_ref"));
                    diferenca.setAttribute("ade_data_ref", DateHelper.format(dataFormatada, "yyyy-MM-dd HH:mm:ss"));
                }

                diferenca.setAttribute("SITUACAO", situacao);
                diferencas.add(diferenca);
            }
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
   }

    /**
    *
    */
   @Override
   public void gravaArquivoDiferencas(String nomeArqSaidaMov, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
       // Arquivos de configuração para processamento do retorno
       ParamSist ps = ParamSist.getInstance();
       //String nomeArqConfEntrada = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN_DIFERENCAS, parametrosExportacao.responsavel);
       String nomeArqConfSaidaDiferencas = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN, responsavel);
       String nomeArqConfTradutorDiferencas = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN, responsavel);

       if (!TextHelper.isNull(nomeArqConfSaidaDiferencas) && !TextHelper.isNull(nomeArqConfTradutorDiferencas)) {
           // Diretório raiz de arquivos eConsig
           String absolutePath = ParamSist.getDiretorioRaizArquivos();
           String pathConf = absolutePath + File.separatorChar + "conf";
           // Arquivos de configuração utilizados na exportação
           //String nomeArqConfEntradaDefault = pathConf + File.separatorChar + nomeArqConfEntrada;
           String nomeArqConfSaida = pathConf + File.separatorChar + nomeArqConfSaidaDiferencas;
           String nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfTradutorDiferencas;

           String nomeArqSaida = nomeArqSaidaMov + SUFIXO_ARQUIVO_DIFERENCAS;
           // Apaga arquivos antigos
           new File(nomeArqSaida).delete();
           new File(nomeArqSaida + SUFIXO_ARQUIVO_DIFERENCAS_VAZIO).delete();

           try {
               String campos = "teo.contador, teo.ade_numero, teo.ade_identificador, teo.ade_cod_reg, teo.ade_inc_margem, teo.ade_tipo_vlr, teo.ade_indice, teo.ade_data, teo.ade_data_ref, teo.ade_data_exclusao, "
                       + "teo.ade_ano_mes_ini, teo.ade_ano_mes_ini_folha, teo.ade_ano_mes_ini_ref, teo.ade_ano_mes_fim, teo.ade_ano_mes_fim_folha, teo.ade_ano_mes_fim_ref, teo.nro_parcelas, teo.prazo_restante, "
                       + "teo.ade_prazo, teo.ade_prazo_exc, teo.ade_prazo_folha, teo.ade_prd_pagas_exc, teo.ade_prd_pagas, teo.ade_vlr, teo.ade_vlr_folha, teo.valor_desconto, teo.valor_desconto_exc, "
                       + "teo.valor_desconto_folha, teo.prd_numero, teo.data_desconto, teo.data_ini_contrato, teo.data_fim_contrato, teo.cnv_cod_verba, teo.cnv_cod_verba_ref, teo.cnv_prioridade, "
                       + "teo.svc_descricao, teo.svc_identificador, teo.svc_prioridade, teo.csa_identificador, teo.csa_cnpj, teo.est_identificador, teo.est_cnpj, teo.org_identificador, teo.org_cnpj, "
                       + "teo.ser_nome, teo.ser_primeiro_nome, teo.ser_ultimo_nome, teo.ser_nome_meio, teo.ser_nome_pai, teo.ser_nome_mae, teo.ser_cpf, teo.ser_nacionalidade, teo.rse_matricula, "
                       + "teo.rse_matricula_inst, teo.rse_tipo, teo.rse_associado, teo.rse_margem, teo.rse_margem_rest, teo.rse_margem_2, teo.rse_margem_rest_2, teo.rse_margem_3, teo.rse_margem_rest_3, "
                       + "teo.pos_codigo, teo.srs_codigo, teo.trs_codigo, teo.oca_periodo, teo.pex_periodo, teo.pex_periodo_ant, teo.pex_periodo_pos, teo.periodo, teo.competencia, teo.data, teo.autoriza_pgt_parcial, "
                       + "teo.capital_devido, teo.saldo_devedor, teo.codigo_folha";

               if (parametrosExportacao.getAcao().equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
                   StringBuilder query = new StringBuilder();
                   List<TransferObject> diferencas = new ArrayList<>();

                   // 1) Contratos não enviados no primeiro movimento, mas enviados no segundo, devem compor o arquivo de diferenças.
                   query.setLength(0);
                   query.append("SELECT ");
                   query.append(campos);
                   query.append(" FROM tb_tmp_exportacao_ordenada teo ");
                   query.append("WHERE NOT EXISTS ( ");
                   query.append("  SELECT * FROM tb_arquivo_movimento arm ");
                   query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                   query.append("  AND teo.RSE_MATRICULA = arm.RSE_MATRICULA ");
                   query.append("  AND teo.SER_CPF = arm.SER_CPF ");
                   query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                   query.append(") ");
                   LOG.debug(query.toString());
                   adicionarDiferenca(diferencas, "I", query.toString());

                   // 2) Contratos em que os valores estão divergentes, seja para maior ou para menor (nesse caso, enviar o valor da parcela do segundo movimento).
                   query.setLength(0);
                   query.append("SELECT ");
                   query.append(campos);
                   query.append(" FROM tb_tmp_exportacao_ordenada teo ");
                   query.append("WHERE EXISTS ( ");
                   query.append("  SELECT * FROM tb_arquivo_movimento arm ");
                   query.append("  WHERE teo.PEX_PERIODO = arm.PEX_PERIODO ");
                   query.append("  AND teo.RSE_MATRICULA = arm.RSE_MATRICULA ");
                   query.append("  AND teo.CNV_COD_VERBA = arm.CNV_COD_VERBA ");
                   query.append("  AND teo.SER_CPF = arm.SER_CPF ");
                   query.append("  AND teo.ADE_VLR != arm.ADE_VLR ");
                   query.append(") ");
                   LOG.debug(query.toString());
                   adicionarDiferenca(diferencas, "I", query.toString());

                   File arqDiferencas = new File(nomeArqSaida);
                   if (arqDiferencas.exists()) {
                       FileHelper.delete(nomeArqSaida);
                   }
                   if (diferencas.isEmpty()) {
                       LOG.debug("Nenhuma diferença encontrada.");
                       new File(nomeArqConfSaida + SUFIXO_ARQUIVO_DIFERENCAS_VAZIO).createNewFile();
                   } else {
                       EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
                       Leitor leitor = new LeitorListTO(diferencas);
                       Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
                       tradutor.traduz();

                       LOG.debug("Arquivo de diferenças: " + nomeArqSaida);
                   }
               }
           } catch (final SQLException | ParserException | IOException | ParseException ex) {
               LOG.error(ex.getMessage(), ex);
               throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
           }
       }
   }

   @Override
   public String posProcessaArqLote(String nomeArqLote, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
       // Diretório raiz de arquivos eConsig
       String absolutePath = ParamSist.getDiretorioRaizArquivos();
       String pathLote = absolutePath + File.separatorChar + "movimento" + File.separatorChar + "cse";
       String dia = DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy");
       String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".txt";
       String nomeArqZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + dia + ".zip";

       File arquivoDiferencas = new File(pathLote, nomeArqSaida + SUFIXO_ARQUIVO_DIFERENCAS);
       if (arquivoDiferencas.length() > 0) {
           nomeArqLote = arquivoDiferencas.getName();
           new File(pathLote, nomeArqSaida).delete();
           new File(pathLote, nomeArqZip).delete();
       }
       return nomeArqLote;
   }
}
