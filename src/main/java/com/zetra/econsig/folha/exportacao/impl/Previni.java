package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: Previni</p>
 * <p>Description: Implementações específicas para Previni - Instituto de Previdência dos Servidores Municipais de Nova Iguaçu.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Previni extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Previni.class);

//    private static final String DATA_INI_MARGEM_ADICIONAL = "2021-04-20 00:00:00";
//    private static final String DATA_FIM_MARGEM_ADICIONAL = "2021-12-31 23:59:59";

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {

        // Só analisa contratos que incidem na margem 1, margem Empréstimo
        List<Short> marCodigos = new ArrayList<>();
        marCodigos.add(CodedValues.INCIDE_MARGEM_SIM);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("PREVINI.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalPrevini(true, marCodigos);
        LOG.debug("fim - PREVINI.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    protected void removerContratosSemMargemMovimentoMensalPrevini(boolean permiteDescontoParcial, List<Short> marCodigos) throws ExportaMovimentoException {
        try {
            // Lista contratos candidatos a serem removidos de servidores com margem negativa
            List<TransferObject> contratos = listaContratosSemMargemCandidatosPrevini(marCodigos);
            ContratosSemMargem adeImpropria = obterContratosSemMargemMovimentoMensalPrevini(contratos, permiteDescontoParcial);

            // Apaga os contratos que não devem ser lançados do último servidor
            if (adeImpropria.getIntegralmenteSemMargem().size() > 0) {
                gravaMotivoNaoExportacao(adeImpropria.getIntegralmenteSemMargem(), TipoMotivoNaoExportacaoEnum.SERVIDOR_SEM_MARGEM_SUFICIENTE);
                excluirContratos(adeImpropria.getIntegralmenteSemMargem());
            }
            if (permiteDescontoParcial && !adeImpropria.getParcialmenteSemMargem().isEmpty()) {
                atualizarParcelaPgtParcial(adeImpropria.getParcialmenteSemMargem());
            }
        } catch (final DataAccessException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private static ContratosSemMargem obterContratosSemMargemMovimentoMensalPrevini(List<TransferObject> contratos, boolean permiteDescontoParcial) throws ExportaMovimentoException {
        try {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            ContratosSemMargem adeImpropria = new ContratosSemMargem();

            if (contratos != null && contratos.size() > 0) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr = BigDecimal.ZERO;

                Map<Short, BigDecimal> margemFolha = new HashMap<>();

                Iterator<TransferObject> it = contratos.iterator();
                while (it.hasNext()) {
                    TransferObject contrato = it.next();
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();

                        List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (MargemTO margemTO : margens) {
                            margemFolha.put(margemTO.getMarCodigo(), margemTO.getMrsMargem());
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    if (margemFolha.get(adeIncMargem).compareTo(adeVlr) >= 0) {
                        // Se a margem folha é maior ou igual ao ade_vlr, então subtrai o valor da margem e continua
                        margemFolha.put(adeIncMargem, margemFolha.get(adeIncMargem).subtract(adeVlr));
                    } else if (!permiteDescontoParcial || margemFolha.get(adeIncMargem).signum() <= 0 ) {
                        // Se o valor da ade é maior que a margem, remove do movimento e continua
                        adeImpropria.addContratoSemMargem(adeCodigo);
                    } else {
                        // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                        // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                        adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemFolha.get(adeIncMargem));
                        margemFolha.put(adeIncMargem, margemFolha.get(adeIncMargem).subtract(adeVlr));
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    private static List<TransferObject> listaContratosSemMargemCandidatosPrevini(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_inc_margem ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("where ((tmp.ade_inc_margem = '1' and tmp.rse_margem_rest   < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = '2' and tmp.rse_margem_rest_2 < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = '3' and tmp.rse_margem_rest_3 < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in ('0','1','2','3') and (select mrs_margem_rest from tb_margem_registro_servidor mrs where mrs.rse_codigo = tmp.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        if (marCodigos != null && !marCodigos.isEmpty()) {
            query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 , coalesce(tmp.cnv_prioridade, 9999999) + 0 , coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) , coalesce(tmp.ade_data_ref, tmp.ade_data), tmp.ade_numero");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }
}
