package com.zetra.econsig.folha.exportacao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;

/**
 * <p>Title: MPMG3</p>
 * <p>Description: Implementações específicas para MPMG - Ministério Público de Minas Gerais.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco$
 * $Revision$
 * $Date$
 */
public class MPMG4 extends MPMG3 {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MPMG4.class);

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
            query.append("tmp.ade_ano_mes_ini DESC, tmp.ade_vlr DESC, coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC ");
            LOG.debug(query.toString());

            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_ano_mes_ini,ade_inc_margem,autoriza_pgt_parcial,desconta_margem_70,existe_desc_margem_70,total_contratos_margem_70";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }
}
