package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: MPMG</p>
 * <p>Description: Implementações específicas para MPMG - Ministério Público de Minas Gerais.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco$
 * $Revision$
 * $Date$
 */
public class MPMG extends ExportaMovimentoBase {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MPMG.class);

    protected static final String PERIODO_INI_MUDANCA_MARGEM = "2015-04-01";

    protected static final Short MARGEM_70 = Short.valueOf("1");
    protected static final Short MARGEM_40 = Short.valueOf("3");

    protected static final BigDecimal LIMITE_MARGEM = new BigDecimal("-10.00");

    protected Date periodoMudancaMargem;

    public MPMG() {
        try {
            periodoMudancaMargem = DateHelper.parse(PERIODO_INI_MUDANCA_MARGEM, "yyyy-MM-dd");
        } catch (final ParseException e) {
            LOG.error(e);
        }
    }

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);


        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            query.append("DROP TABLE IF EXISTS tmp_contratos_nao_cabem_margem");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //Criamos tabela temporaria para
            query.setLength(0);
            query.append("CREATE TABLE tmp_contratos_nao_cabem_margem ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("ade_codigo varchar(32), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_inc_margem smallint(6), ");
            query.append("autoriza_pgt_parcial char(1), ");
            query.append("desconta_margem_70 char(1), ");
            query.append("svc_prioridade varchar(4), ");
            query.append("cnv_prioridade int(11), ");
            query.append("ade_data_ref datetime, ");
            query.append("ade_data datetime, ");
            query.append("ade_numero bigint(20), ");
            query.append("existe_desc_margem_70 char(1), ");
            query.append("total_contratos_margem_70 decimal(13,2) default 0.00, ");
            query.append("key tmp_contratos_nao_cabem_margem_IDX1 (rse_codigo), ");
            query.append("key tmp_contratos_nao_cabem_margem_IDX2 (ade_codigo), ");
            query.append("key tmp_contratos_nao_cabem_margem_IDX3 (rse_codigo, ade_inc_margem)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_exportacao_MPMG ON tb_tmp_exportacao (rse_matricula, org_identificador, est_identificador, csa_identificador, cnv_cod_verba)");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_exportacao_ordenada_MPMG ON tb_tmp_exportacao_ordenada (rse_matricula, org_identificador, est_identificador, csa_identificador, cnv_cod_verba)");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        List<TransferObject> contratos = new ArrayList<>();
        final StringBuilder query = new StringBuilder();
        try {
            listarContratosCandidatos(marCodigos);

            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_ano_mes_ini, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, desconta_margem_70, existe_desc_margem_70, total_contratos_margem_70 ");
            query.append("from tmp_contratos_nao_cabem_margem tmp ");
            query.append("order by tmp.rse_codigo, ");
            query.append("tmp.ade_ano_mes_ini DESC, coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
            LOG.debug(query.toString());

            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_ano_mes_ini,ade_inc_margem,autoriza_pgt_parcial,desconta_margem_70,existe_desc_margem_70,total_contratos_margem_70";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    @Override
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            final ContratosSemMargem adeImpropria = new ContratosSemMargem();
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                Date adeAnoMesIni = null;
                String adeCodigo = null;
                String descontaMargem70 = null;
                BigDecimal adeVlr;
                boolean existeDescMargem70 = false;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());
                    descontaMargem70 = contrato.getAttribute("desconta_margem_70").toString();
                    try {
                        adeAnoMesIni = DateHelper.parse(contrato.getAttribute("ade_ano_mes_ini").toString(), "yyyy-MM-dd");
                    } catch (final ParseException e) {
                        LOG.error(e);
                    }

                    adeIncMargem = ((adeIncMargem.equals(MARGEM_40)) && (adeAnoMesIni.compareTo(periodoMudancaMargem) < 0)) || CodedValues.TPC_SIM.equals(descontaMargem70) ? MARGEM_70 : adeIncMargem;

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        existeDescMargem70 = CodedValues.TPC_SIM.equals(contrato.getAttribute("existe_desc_margem_70").toString());
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            final Short marCodigo = margemTO.getMarCodigo();
                            margemFolha.put(marCodigo, margemTO.getMrsMargem());
                            margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                            margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            final BigDecimal margemUsadaAposCorte = calcularMargemUsadaPosCorte(rseCodigo, marCodigo, responsavel);
                            margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaAposCorte));
                            margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaAposCorte));

                            //Nenhum contrato incide nesta MARGEM_70, porém contratos anteriores a 2015-04-01 devem incidir nela, por isso é necessário fazer o subtract para verificar corretamente o que cabe na margem.
                            if(marCodigo.equals(MARGEM_70) && existeDescMargem70) {
                                final BigDecimal totalMargem70 = new BigDecimal(contrato.getAttribute("total_contratos_margem_70").toString());
                                BigDecimal contratosIncMargem70 = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, null, DateHelper.addMonths(periodoMudancaMargem, -1), MARGEM_40, null, true, responsavel);
                                contratosIncMargem70 = contratosIncMargem70.add(totalMargem70);
                                margemUsada.put(marCodigo, contratosIncMargem70);
                                margemRestante.put(marCodigo, margemTO.getMrsMargem().subtract(contratosIncMargem70));
                            }
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    // DESENV-20753: A margem que está sendo descontada, se estiver negativa até -10.00, irá descontar tudo, não desconta parcial, caso contratário parcial.
                    if ((margemRestante.get(adeIncMargem).signum() < 0) && (margemRestante.get(adeIncMargem).compareTo(LIMITE_MARGEM) < 0) ) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                       if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            // Passa o valor negativo, pois está sendo somado à margem
                            atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
                        }

                        if (!permiteDescontoParcial || (margemRestante.get(adeIncMargem).signum() <= 0)) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else if (!verificaParamCsaPgParcial || (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && CodedValues.TPA_SIM.equals(contrato.getAttribute("autoriza_pgt_parcial")))) {
                            // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                            // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                            adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
                        } else {
                            // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
                            // lista dos contratos a serem removidos do movimento
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        }
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("MPMG.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null);
        LOG.debug("fim - MPMG.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    /**
     * A rotina atual usa como referência o campo ADE_ANO_MES_INI. Este método serve para que
     * as rotinas filhas possam sobrepor o campo. Não é necessário chamar este método quando
     * o campo ADE_ANO_MES_INI for usado/consultado da tb_tmp_exportacao pois basta que as
     * rotinas filhas sobrescrevam o valor do campo nesta tabela, como é feita na MPMG2.java
     * @return
     */
    protected String getCampoReferencia() {
        return "ade.ade_ano_mes_ini";
    }

    protected BigDecimal pesquisarTotalContratosDataIniMudanca (String rseCodigo, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);

        try {
            final StringBuilder query = new StringBuilder();
            query.append("select sum(ade_vlr) AS TOTAL ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("where rse_codigo = :rseCodigo ");
            query.append("and ade_ano_mes_ini < '").append(PERIODO_INI_MUDANCA_MARGEM).append("' ");
            LOG.trace(query.toString());
            final BigDecimal vlrTotal = jdbc.queryForObject(query.toString(), queryParams, BigDecimal.class);
            return (vlrTotal == null) ? BigDecimal.ZERO : vlrTotal;
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void listarContratosCandidatos(List<Short> marCodigos) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // os servidores que tiverem posto com codigo = 1 (tb_registro_servidor.pos_codigo = 1), sendo que para eles deverá ser exportado todos os contratos,
            // mesmo se não couber na margem ou se a margem estiver negativa
            // Porém, caso na exportação estiver sendo incluído um novo contrato para um servidor com tb_posto_registro_servidor.pos_codigo = 1, ou seja,
            // houver algum contrato desse servidor sendo exportado com data inicial do contrato igual a data do período exportado, então o sistema deve
            // realizar a exportação parcial dos contratos desse servidor, caso o valor não caiba na margem
            // Além disso os contratos que são fruto de renegociação ou portabilidade de contratos do anteriores à 2015-04-01 devem descontar na margem 70

            final StringBuilder query = new StringBuilder();

            //Inserimos na tabela todos os contratos que devem ser exportados olhando desconto parcial
            //1. todos que não são pos_codigo 1 que não são do periodo atual de exportação devem ser considerados com desconto parcial
            query.append("INSERT INTO tmp_contratos_nao_cabem_margem ");
            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_ano_mes_ini, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, 'N', tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_data_ref, tmp.ade_data, tmp.ade_numero, 'N', '0.00' ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
            if ((marCodigos != null) && !marCodigos.isEmpty()) {
                query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
                queryParams.addValue("marCodigos", marCodigos);
            }
            query.append("and (tmp.pos_codigo != 1 or tmp.pos_codigo is NULL) ");
            query.append("and tmp.ade_ano_mes_ini != pex.pex_periodo ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //2. Todos os contratos que são do periodo atual devem ser observados para desconto parcial
            query.setLength(0);
            query.append("INSERT INTO tmp_contratos_nao_cabem_margem ");
            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_ano_mes_ini, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, 'N', tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_data_ref, tmp.ade_data, tmp.ade_numero, 'N', '0.00' ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
            if ((marCodigos != null) && !marCodigos.isEmpty()) {
                query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
                queryParams.addValue("marCodigos", marCodigos);
            }
            query.append("and tmp.ade_ano_mes_ini = pex.pex_periodo ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //2.1 Se houver algum contrato novo que é do pos_codigo 1, inserir os contratos que estão indo no movimento deste mesmo servidor que não seja do periodo atual
            // pois ele perdeu o privilégio de enviar tudo, mesmo que não caiba na margem
            query.setLength(0);
            query.append("INSERT INTO tmp_contratos_nao_cabem_margem ");
            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_ano_mes_ini, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, 'N', tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_data_ref, tmp.ade_data, tmp.ade_numero, 'N', '0.00' ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
            if ((marCodigos != null) && !marCodigos.isEmpty()) {
                query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
                queryParams.addValue("marCodigos", marCodigos);
            }
            query.append("and tmp.ade_ano_mes_ini != pex.pex_periodo ");
            query.append("and tmp.pos_codigo ='1' ");
            query.append("and exists (select 1 from tmp_contratos_nao_cabem_margem tmp1 where tmp.rse_codigo = tmp1.rse_codigo and tmp1.ade_ano_mes_ini = pex.pex_periodo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //Lógica contratos que devem descontar da margem 70
            //3. Todos os contratos anteriores à 2015-04-01 devem exportar olhando a margem 70
            query.setLength(0);
            query.append("update tmp_contratos_nao_cabem_margem tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("set desconta_margem_70 = 'S' ");
            query.append("where tmp.ade_ano_mes_ini < '").append(PERIODO_INI_MUDANCA_MARGEM).append("' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //4. Todos os contratos do periodo atual devem exportar olhando a margem 70 quando o contrato de origem é anterior '2015-04-01' e é fruto de renegociação ou compra
            query.setLength(0);
            query.append("update tmp_contratos_nao_cabem_margem tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("set desconta_margem_70 = 'S' ");
            query.append("where tmp.ade_ano_mes_ini = pex.pex_periodo ");
            query.append("and exists (");
            query.append("select 1 from tb_relacionamento_autorizacao rad ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = rad.ade_codigo_origem) ");
            query.append("where rad.ade_codigo_destino = tmp.ade_codigo ");
            query.append("and rad.tnt_codigo in ('6','7') ");
            query.append("and ").append(getCampoReferencia()).append(" < '").append(PERIODO_INI_MUDANCA_MARGEM).append("'");
            query.append(") ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-20753: Quando a margem 3 (MARGEM_40) for maior que a margem 1 (MARGEM_70), então deve olhar a margem 3 (MARGEM_70), pois ela é menor.
            query.setLength(0);
            query.append("update tmp_contratos_nao_cabem_margem tmp ");
            query.append("inner join tb_registro_servidor rse on (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("set tmp.desconta_margem_70 = 'S' ");
            query.append("where rse.rse_margem_3 > rse.rse_margem ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //5. Para melhorar a performance setamos para sim, todos os registros servidores quem desconto na margem 70, para buscar os contratos que estão nesta regra de inicio de contrato anterior a '2015-04-01'
            query.setLength(0);
            query.append("drop temporary table if exists tmp_contrato_desconto_margem_70 ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tmp_contrato_desconto_margem_70 ");
            query.append("select * from tmp_contratos_nao_cabem_margem where desconta_margem_70='S' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Precisamos somar todos os contratos que são do período atual e que são frutos de renegocição ou portabilidae para subtrair da margem 70 para fazer corretamente o calculo do que cabe ou não.
            query.setLength(0);
            query.append("update tmp_contrato_desconto_margem_70 tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("set tmp.total_contratos_margem_70 = (select sum(tmp1.ade_vlr) from tmp_contratos_nao_cabem_margem tmp1 where tmp1.desconta_margem_70='S' and tmp1.rse_codigo = tmp.rse_codigo and (tmp1.ade_ano_mes_ini = pex.pex_periodo OR rse.rse_margem_3 > rse.rse_margem and tmp1.ade_ano_mes_ini > '2015-03-01')) ");
            query.append("where exists (select 1 from tmp_contratos_nao_cabem_margem tmp2 where tmp2.desconta_margem_70='S' and tmp2.rse_codigo = tmp.rse_codigo and (tmp2.ade_ano_mes_ini = pex.pex_periodo OR rse.rse_margem_3 > rse.rse_margem and tmp2.ade_ano_mes_ini > '2015-03-01')) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("update tmp_contrato_desconto_margem_70 tmp ");
            query.append("inner join tmp_contratos_nao_cabem_margem tmp1 ON (tmp.rse_codigo = tmp1.rse_codigo) ");
            query.append("set tmp1.total_contratos_margem_70 = tmp.total_contratos_margem_70 ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("update tmp_contratos_nao_cabem_margem tmp ");
            query.append("set tmp.existe_desc_margem_70 = 'S' ");
            query.append("where exists (select 1 from tmp_contrato_desconto_margem_70 tmp1 where tmp1.rse_codigo=tmp.rse_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("update tmp_contratos_nao_cabem_margem tmp ");
            query.append("set tmp.existe_desc_margem_70 = 'S' ");
            query.append("where exists (select 1 from tmp_contrato_desconto_margem_70 tmp1 where tmp1.rse_codigo=tmp.rse_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            //Os servidores de pos_codigo 1, perdem o posto ao terem contratos incluídos no periodo atual
            //a alteração precisa ser neste ponto, pois pode acontecer do contrato não caber na margem e não ser enviado no movimento.
            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
            query.append("set rse.pos_codigo = NULL ");
            query.append("where tmp.ade_ano_mes_ini = pex.pex_periodo ");
            query.append("and tmp.pos_codigo = '1' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
