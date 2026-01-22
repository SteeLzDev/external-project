package com.zetra.econsig.folha.exportacao.impl;

import java.text.ParseException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

public class Diviprev extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Diviprev.class);

    private static final String DATA_PADRAO_MYSQL = "yyyy-MM-dd";
    private static final String DIVINOPOLIS_PREVIDENCIA = "F080808080808080808080808080C780";

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        String dataNovosAposentados = null;

        try {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            dataNovosAposentados = cseDelegate.findDadoAdicionalConsignante(CodedValues.CSE_CODIGO_SISTEMA, CodedValues.TDA_DATA_NOVOS_SERVIDORES_APOSENTADOS, responsavel);

            if (TextHelper.isNull(dataNovosAposentados)) {
                return;
            }
            if (DateHelper.verifyPattern(dataNovosAposentados, LocaleHelper.getDatePattern())) {
                dataNovosAposentados = DateHelper.reformat(dataNovosAposentados, LocaleHelper.getDatePattern(), DATA_PADRAO_MYSQL);
            }
            if (!DateHelper.verifyPattern(dataNovosAposentados, DATA_PADRAO_MYSQL)) {
                LOG.warn("A 'Data para novos servidores aposentados' deve estar no padrão '" + DATA_PADRAO_MYSQL + "' ou '"+ LocaleHelper.getDatePattern() + "'.");
                return;
            }
        } catch (ConsignanteControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_servidores_aposentados");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_servidores_aposentados (rse_codigo varchar(32), rse_data_admissao datetime, primary key (rse_codigo))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_servidores_aposentados (rse_codigo, rse_data_admissao) ");
            query.append("select rse.rse_codigo, rse.rse_data_admissao ");
            query.append("from tb_registro_servidor rse ");
            query.append("where rse.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
            query.append("and rse.org_codigo = '").append(DIVINOPOLIS_PREVIDENCIA).append("' ");
            query.append("and rse.rse_data_admissao >= '").append(dataNovosAposentados).append("' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_contratos_aposentados");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_contratos_aposentados (ade_codigo varchar(32), ade_prazo int, ade_prd_pagas int, primary key (ade_codigo))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Prazo = prazo do contrato - número de parcelas pagas com data realizada (PRD_DATA_REALIZADO) menor ou igual á data da admissão do registro servidor
            // Pagas = quantidade de parcelas pagas com data realizada (PRD_DATA_REALIZADO) maior que a data da admissão do registro servidor
            query.setLength(0);
            query.append("insert into tb_tmp_contratos_aposentados (ade_codigo, ade_prazo, ade_prd_pagas) ");
            query.append("select ade.ade_codigo, ade.ade_prazo - ");
            query.append("   sum(case when prd.prd_data_realizado <= tmp.rse_data_admissao then 1 else 0 end), ");
            query.append("   sum(case when prd.prd_data_realizado > tmp.rse_data_admissao then 1 else 0 end) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_tmp_servidores_aposentados tmp on (ade.rse_codigo = tmp.rse_codigo) ");
            query.append("where prd.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') ");
            query.append("and exists (select 1 from tb_tmp_exportacao exp where ade.ade_codigo = exp.ade_codigo) ");
            query.append("group by ade.ade_codigo, ade.ade_prazo");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("update tb_tmp_exportacao exp ");
            query.append("inner join tb_tmp_contratos_aposentados tmp on (exp.ade_codigo = tmp.ade_codigo) ");
            query.append("set exp.ade_prazo = tmp.ade_prazo, exp.ade_prd_pagas = tmp.ade_prd_pagas ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
