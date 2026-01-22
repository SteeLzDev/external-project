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
public class STJRegraEspecial extends STJ {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(STJRegraEspecial.class);

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Em um sistema de "margem 3 casada com a margem 1", parâmetro de sistema 91 = S, a margem 1 será sempre maior que a margem 3.
        // Desta forma, não há como a margem 3 ser positiva com margem 1 negativa pois todos os valores incidentes na margem 3 também
        // consomem da margem 1. A regra geral então deve remover consignações até que a margem 1 esteja positiva, sejam consignações
        // incidentes na margem 1 ou 3, e para a regra especial, devemos remover consignações que incidem na margem 1 ou 3 até que a
        // margem 3 seja positiva, o que consequentemente tornará a margem 1 positiva.

        StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, case when coalesce(rse.mar_codigo, '') = '3' then '3' else '1' end as ade_inc_margem ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = tmp.rse_codigo) ");
        query.append("where tmp.ade_inc_margem in ('1', '2', '3') ");
        query.append("and ((coalesce(rse.mar_codigo, '')  = '3' and tmp.rse_margem_rest_3 < 0.00) ");
        query.append("  or (coalesce(rse.mar_codigo, '') != '3' and tmp.rse_margem_rest   < 0.00) ");
        query.append(") ");
        query.append("order by rse_codigo, ");
        query.append("coalesce(svc_prioridade, 9999999) + 0 DESC, coalesce(cnv_prioridade, 9999999) + 0 DESC, coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini) DESC, coalesce(ade_data_ref, ade_data) DESC, ade_numero DESC");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem";
            contratos.addAll(MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR));
        } catch (DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

        return contratos;
    }
}
