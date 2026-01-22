package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

public class ExercitoLicitacaoSemParcial extends ExercitoLicitacao {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExercitoLicitacaoSemParcial.class);

    /**
     * Lista os contratos de servidores com margem negativa pela ordem de exportação
     * A ordenação é CRESCENTE, pois a verificação é feita na ordem de exportação, 
     * subtraindo o valor dos contratos da margem folha até que a mesma seja negativa ou zero.
     * @param stat
     * @param marCodigos
     * @throws ExportaMovimentoException
     */
    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Cria tabela temporária com a margem usada pós corte
        salvarMargemUsadaPosCorte(null);

        StringBuilder query = new StringBuilder();
        // DESENV-13946 : Fixa a margem 1 como sendo a margem limite para exportação
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, '1' as ade_inc_margem, tmp.autoriza_pgt_parcial ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("left outer join tb_tmp_margem_usada_pos_corte mpc on (tmp.rse_codigo = mpc.rse_codigo and mpc.mar_codigo = 1) ");
        query.append("where tmp.ade_inc_margem <> '0' ");
        query.append("and tmp.rse_margem_rest + COALESCE(mpc.margem_usada_pos_corte, 0.00) < 0.00 ");

        // DESENV-14158: Consignações que não tenham sido afetadas por decisão judicial e sejam nas naturezas de empréstimo, financiamento ou auxílio de financiamento.
        query.append("and not exists ( ");
        query.append("select 1 ");
        query.append("from tb_aut_desconto ade ");
        query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        query.append("inner join tb_dados_autorizacao_desconto dad on (ade.ade_codigo = dad.ade_codigo and dad.tda_codigo = '").append(CodedValues.TDA_AFETADA_DECISAO_JUDICIAL).append("') ");
        query.append("inner join tb_ocorrencia_dados_ade oda on (dad.ade_codigo = oda.ade_codigo and dad.tda_codigo = oda.tda_codigo and oda.toc_codigo = '").append(CodedValues.TOC_CRIACAO_DADOS_ADICIONAIS).append("') ");
        query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) ");
        query.append("where ade.ade_codigo = tmp.ade_codigo ");
        query.append("and coalesce(dad.dad_valor, 'N') = 'S' ");
        query.append("and svc.nse_codigo in ('").append(CodedValues.NSE_EMPRESTIMO).append("', '").append(CodedValues.NSE_FINANCIAMENTO).append("', '").append(CodedValues.NSE_AUXILIO_FINANCEIRO).append("') ");
        query.append("and oda_data < pex_data_fim ");
        query.append(") ");

        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 ASC, coalesce(tmp.cnv_prioridade, 9999999) + 0 ASC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) ASC, coalesce(tmp.ade_data_ref, tmp.ade_data) ASC, tmp.ade_numero ASC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (DAOException ex) {
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

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                for (final TransferObject contrato : contratos) {
                    final String rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    final Short adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
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
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    final String adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    final BigDecimal adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    if ((margemFolha.get(adeIncMargem).signum() > 0) && (margemFolha.get(adeIncMargem).subtract(adeVlr).signum() >= 0)) {
                        // Se a margem folha é positiva, e o valor do contrato cabe na margem folha, então abate da margem folha o contrato
                    	// para continuar avaliando os próximos contratos
                        margemFolha.put(adeIncMargem, margemFolha.get(adeIncMargem).subtract(adeVlr));
                    } else {
                    	// Caso o contrato não caiba de forma integral, não abate da margem e segue para o próximo
                        adeImpropria.addContratoSemMargem(adeCodigo);
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }
}
