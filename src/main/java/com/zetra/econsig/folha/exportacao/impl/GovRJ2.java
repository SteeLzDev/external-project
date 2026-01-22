package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
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
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: GovRJ2</p>
 * <p>Description: Implementações específicas para GovRJ - Governo do Estado do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GovRJ2 extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GovRJ2.class);

    private static final String DATA_INI_MUDANCA_MARGEM = "2016-02-14 00:00:00";

    private static final Short MARGEM_CARTAO_50 = Short.valueOf("100");
    private static final Short MARGEM_CONSIGNACAO_40 = Short.valueOf("101");
    private static final Short MARGEM_CARTAO_35 = Short.valueOf("102");
    private static final Short MARGEM_CONSIGNACAO_30 = Short.valueOf("103");

    private Date dataInicioMudancaMargem;

    public GovRJ2() {
        try {
            dataInicioMudancaMargem = DateHelper.parse(DATA_INI_MUDANCA_MARGEM, "yyyy-MM-dd HH:mm:ss");
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
        /*
         * DESENV-16229
         * Contratos anteriores a 14/02/2016 devem respeitar as margens de 40/50
        */

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
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CARTAO_35 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CARTAO_50 + " ");
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CONSIGNACAO_30 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CONSIGNACAO_40 + " ");
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

            if (contratos != null && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                Date adeData = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                Map<Short, BigDecimal> margemFolha = new HashMap<>();
                Map<Short, BigDecimal> margemRestante = new HashMap<>();
                Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                Iterator<TransferObject> it = contratos.iterator();
                while (it.hasNext()) {
                    TransferObject contrato = it.next();
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());
                    try {
                        adeData = DateHelper.parse(contrato.getAttribute("ade_data").toString(), "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        LOG.error(e);
                    }

                    adeIncMargem = (adeIncMargem == MARGEM_CONSIGNACAO_30.shortValue()) && (adeData.compareTo(dataInicioMudancaMargem) < 0) ? MARGEM_CONSIGNACAO_40 : adeIncMargem;
                    adeIncMargem = (adeIncMargem == MARGEM_CARTAO_35.shortValue()) && (adeData.compareTo(dataInicioMudancaMargem) < 0) ? MARGEM_CARTAO_50 : adeIncMargem;

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

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

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestante.get(adeIncMargem).signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                       if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            // Passa o valor negativo, pois está sendo somado à margem
                            atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
                        }

                        if (!permiteDescontoParcial || margemRestante.get(adeIncMargem).signum() <= 0) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else if (verificaParamCsaPgParcial) {
                            if (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && contrato.getAttribute("autoriza_pgt_parcial").equals(CodedValues.TPA_SIM)) {
                                // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                                // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                                adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
                            } else {
                                // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
                                // lista dos contratos a serem removidos do movimento
                                adeImpropria.addContratoSemMargem(adeCodigo);
                            }
                        } else {
                            // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                            // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                            adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
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
}
