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
 * <p>Title: STJ</p>
 * <p>Description: Implementações específicas para STJ - Superior Tribunal de Justiça.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class STJRegraGeral extends STJ {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(STJRegraGeral.class);

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, '1' as ade_inc_margem ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("where ((tmp.ade_inc_margem = '1' and tmp.rse_margem_rest   < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = '2' and tmp.rse_margem_rest_2 < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = '3' and tmp.rse_margem_rest_3 < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in ('0','1','2','3') and (select mrs_margem_rest from tb_margem_registro_servidor mrs where mrs.rse_codigo = tmp.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
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
