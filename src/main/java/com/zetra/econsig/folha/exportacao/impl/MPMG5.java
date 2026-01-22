package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

public class MPMG5 extends MPMG4 {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MPMG5.class);

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        List<TransferObject> contratos = new ArrayList<>();
        final StringBuilder query = new StringBuilder();
        try {
            listarContratosCandidatos(marCodigos);

            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_ano_mes_ini, tmp.ade_inc_margem, tmp.autoriza_pgt_parcial, desconta_margem_70, existe_desc_margem_70, total_contratos_margem_70, csa.csa_identificador ");
            query.append("from tmp_contratos_nao_cabem_margem tmp ");
            query.append("inner join tb_aut_desconto tad ON (tad.ADE_CODIGO = tmp.ade_codigo) ");
            query.append("inner join tb_verba_convenio tvc ON (tvc.vco_codigo = tad.VCO_CODIGO) ");
            query.append("inner join tb_convenio tc ON (tc.cnv_codigo = tvc.cnv_codigo) ");
            query.append("inner join tb_consignataria csa ON (csa.csa_codigo = tc.csa_codigo) ");
            query.append("order by tmp.rse_codigo, ");
            query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, tmp.ade_ano_mes_ini DESC, tmp.ade_vlr DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC ");
            LOG.debug(query.toString());

            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_ano_mes_ini,ade_inc_margem,autoriza_pgt_parcial,desconta_margem_70,existe_desc_margem_70,total_contratos_margem_70,csa_identificador";
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
                            if (marCodigo.equals(MARGEM_70) && existeDescMargem70) {
                                final BigDecimal totalMargem70 = new BigDecimal(contrato.getAttribute("total_contratos_margem_70").toString());
                                BigDecimal contratosIncMargem70 = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, null, DateHelper.addMonths(periodoMudancaMargem, -1), MARGEM_40, null, false, responsavel);
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
                    if ((margemRestante.get(adeIncMargem).signum() < 0) && (margemRestante.get(adeIncMargem).compareTo(LIMITE_MARGEM) < 0)) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            // Passa o valor negativo, pois está sendo somado à margem
                            atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
                        }
                        if (!"033".equals(contrato.getAttribute("csa_identificador").toString())) {
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
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }
}
