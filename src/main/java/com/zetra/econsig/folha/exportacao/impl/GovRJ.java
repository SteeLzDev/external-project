package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: GovRJ</p>
 * <p>Description: Implementações específicas para GovRJ - Governo do Estado do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GovRJ extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GovRJ.class);

    /**
     * Atualiza o valor dos contratos para pagamento parcial daqueles que não cabem integralmente na
     * margem, em sistemas que permite esta rotina, somente na tabela de exportação
     * @param parcialmenteSemMargem
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
        // Atualiza data dos contratos da tabela de exportação as ADE que são de provisionamento de margem
        LOG.debug("GovRJ.atualizarAdeDataContratosProvisionamentoMargem: " + DateHelper.getSystemDatetime());
        atualizarAdeDataContratosProvisionamentoMargem();
        LOG.debug("fim - GovRJ.atualizarAdeDataContratosProvisionamentoMargem: " + DateHelper.getSystemDatetime());
    }
}
