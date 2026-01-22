package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;

/**
 * <p>Title: Camprev</p>
 * <p>Description: Implementações específicas para Camprev.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Camprev extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Camprev.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            // Remove da tabela de exportação as ADE que não cabem na margem
            LOG.debug("Camprev.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());

            StringBuilder query = new StringBuilder();

            // Lista os contratos de servidores com margem negativa pela ordem de exportação
            // OBS: A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
            // contratos removidos à margem restante até que a mesma seja positiva.
            query.append("select tmp.rse_codigo, mrs_margem_rest, tmp.ade_codigo, tmp.ade_vlr, ade.ade_inc_margem ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_margem_registro_servidor mrs on (mrs.rse_codigo = tmp.rse_codigo and ade.ade_inc_margem = mrs.mar_codigo) ");
            query.append("where mrs_margem_rest < 0.00 ");
            query.append("order by tmp.rse_codigo, ade.ade_inc_margem, ");
            query.append("coalesce(svc_prioridade, 9999999) + 0 DESC, coalesce(cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
            LOG.debug(query.toString());

            try {
                String fieldsNames = "rse_codigo,mrs_margem_rest,ade_codigo,ade_vlr,ade_inc_margem";
                List<TransferObject> contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
                List<String> adeImpropria = obterContratosSemMargemMovimentoMensal(contratos);

                // Apaga os contratos que não devem ser lançados do último servidor
                if (adeImpropria.size() > 0) {
                    excluirContratos(adeImpropria);
                    adeImpropria.clear();
                }
            } catch (DAOException ex) {
                LOG.error(ex.getMessage());
                throw new ExportaMovimentoException(ex);
            }

            LOG.debug("fim - Camprev.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
