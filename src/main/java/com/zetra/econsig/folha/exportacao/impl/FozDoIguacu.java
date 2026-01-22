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
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: FozDoIguacu</p>
 * <p>Description: Implementações específicas para a Prefeitura de Foz do Iguaçu.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FozDoIguacu extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FozDoIguacu.class);

    private static final String SVC_CODIGO_SISMUFI = "92808080808080808080808080805A9C";

    /**
     * Atualiza o valor das consignações do serviço "SEGURO DE VIDA SISMUFI %", de código "92808080808080808080808080805A9C"
     * Este serviço utiliza o valor da base de calculo para determinar o valor a ser descontado em folha, sendo este 2,175%
     * sobre o salário do servidor, carregado no campo RSE_BASE_CALCULO. Após calcular o valor, este deve ser limitado ao
     * valor R$ 43,50 por CPF, ou seja, caso o servidor possua duas ou mais matrículas distintas, com consignações neste
     * serviço, o valor deverá ser calculado de forma proporcional de modo que soma não ultrapasse o valor máximo. O valor
     * percentual 2,175% será gravado no parâmetro de serviço 5. O valor limite de R$ 43,50 será gravado no parâmetro de serviço 119.
     */
    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            final StringBuilder query = new StringBuilder();

            LOG.debug("Inicio - FozDoIguacu.preProcessaAutorizacoes: " + DateHelper.getSystemDatetime());

            query.setLength(0);
            query.append("drop temporary table if exists tmp_calc_seguro_vida_sismufi");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tmp_calc_seguro_vida_sismufi (cpf varchar(14) not null, soma_base_calc decimal(13,2) not null, valor_max decimal(13,2) not null, primary key (cpf))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tmp_calc_seguro_vida_sismufi (cpf, soma_base_calc, valor_max) ");
            query.append("select ser_cpf, sum(rse.rse_base_calculo), round(least(coalesce(cast(pse1.pse_vlr as decimal(13,5)), 2.175) / 100.00 * sum(rse.rse_base_calculo), coalesce(cast(pse2.pse_vlr as decimal(13,2)), 43.50)), 2) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) ");
            query.append("inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
            query.append("left outer join tb_param_svc_consignante pse1 on (cnv.svc_codigo = pse1.svc_codigo and pse1.tps_codigo = '").append(CodedValues.TPS_ADE_VLR).append("') ");
            query.append("left outer join tb_param_svc_consignante pse2 on (cnv.svc_codigo = pse2.svc_codigo and pse2.tps_codigo = '").append(CodedValues.TPS_VLR_MAXIMO_CONTRATO).append("') ");
            query.append("where cnv.svc_codigo = '").append(SVC_CODIGO_SISMUFI).append("' ");
            query.append("  and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
            query.append("  and rse.rse_base_calculo > 0 ");
            query.append("group by ser.ser_cpf ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("update tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) ");
            query.append("inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
            query.append("inner join tmp_calc_seguro_vida_sismufi tmp on (ser.ser_cpf = tmp.cpf) ");
            query.append("set ade.ade_vlr = round(tmp.valor_max * (rse.rse_base_calculo / tmp.soma_base_calc), 2) ");
            query.append("where cnv.svc_codigo = '").append(SVC_CODIGO_SISMUFI).append("' ");
            query.append("  and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
            query.append("  and rse.rse_base_calculo > 0 ");
            LOG.debug(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            LOG.debug("Fim - FozDoIguacu.preProcessaAutorizacoes: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
