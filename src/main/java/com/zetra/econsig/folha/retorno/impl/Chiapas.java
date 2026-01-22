package com.zetra.econsig.folha.retorno.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ImportaRetornoException;
import com.zetra.econsig.folha.retorno.ImportaRetornoBase;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: Chiapas</p>
 * <p>Description: Implementações específicas para o sistema Chiapas/MX.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Chiapas extends ImportaRetornoBase {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Chiapas.class);

    public Chiapas(int tipoImportacaoRetorno, String orgCodigo, String estCodigo) {
        super(tipoImportacaoRetorno, orgCodigo, estCodigo);
    }

    @Override
    public void preImportacaoRetorno() throws ImportaRetornoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // ATUALIZA O CAMPO "ade_indice_exp" PARA 99999, POIS ESTE SERA O VALOR REPASSADO PELO
            // XML TRADUTOR DO RETORNO PARA QUE A CHAVE DE PAGAMENTO SEJA EXATA
            final StringBuilder query = new StringBuilder();
            query.append("update tb_aut_desconto ");
            query.append("set ade_indice_exp = '99999' ");
            query.append("where rse_codigo in ( ");
            query.append("  select rse_codigo from tb_registro_servidor ");
            query.append("  where org_codigo = '96808080808080808080808080808F83' "); // 002 DOCENTES
            query.append(") ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // REMOVE QUALQUER VALOR DO CAMPO "ade_indice_exp" PARA OS DEMAIS ORGAOS,
            // EVITANDO QUE UMA TRANSFERENCIA DE ORGAO FIQUE COM VALOR DO CAMPO
            // INCORRETAMENTE PREENCHIDO

            query.setLength(0);
            query.append("update tb_aut_desconto ");
            query.append("set ade_indice_exp = NULL ");
            query.append("where rse_codigo not in ( ");
            query.append("  select rse_codigo from tb_registro_servidor ");
            query.append("  where org_codigo = '96808080808080808080808080808F83' "); // 002 DOCENTES
            query.append(") ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImportaRetornoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
