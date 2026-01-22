package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: BH</p>
 * <p>Description: Implementações específicas para a Prefeitura de Belo Horizonte.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BH extends ModuloBeneficioSaude {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BH.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // DESENV-9900: Um contrato é inserido e enviado à folha (I) inclusão. Porém no decorrer de alguns períodos ele não sofre desconto na folha,
            // assim o contrato permanece com situação Deferido sem parcela paga.
            // Num determinado momento, esse contrato sofre uma alteração automática de valor (ex.: alteração de faixa etária ou salário que altera valor).
            // Nesse momento o eConsig exporta arquivo de Movimento com (I) Inclusão novamente, pois acredito que o sistema 'entende'
            // que a folha não reconheceu essa inclusão em outro momento.
            // Conclusão: O sistema de folha do cliente retorna uma crítica, pois informa que esse contrato já existe. Dessa forma,
            // já foi incluído e precisamos enviar (A) Alteração para atualizar o valor na folha.
            StringBuilder query = new StringBuilder();
            query.append("update tb_tmp_exportacao tmp ");
            query.append("set tmp.situacao = 'A' ");
            query.append("where tmp.sad_codigo in ('").append(TextHelper.joinWithEscapeSql(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "' , '")).append("') ");
            query.append("and (select count(*) from tb_parcela_desconto prd where tmp.ade_codigo = prd.ade_codigo > 0) ");
            LOG.debug(query.toString());
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
