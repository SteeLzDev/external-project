package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.validacaoambiente.RegraValidacaoTriggerStatusAutDesconto;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.RegraValidacaoEnum;

/**
 * <p>Title: ObtemValidacaoAmbienteQuery</p>
 * <p>Description: Obtem resultado da validação do ambiente do eConsig</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemValidacaoAmbienteQuery extends HNativeQuery {

    public RegraValidacaoEnum regraValidacaoEnum;

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String consulta = "";
        if (regraValidacaoEnum.equals(RegraValidacaoEnum.VALIDAR_INNODB_MYSQL)) {
            consulta = "SHOW VARIABLES LIKE 'have_innodb'";
        } else if (regraValidacaoEnum.equals(RegraValidacaoEnum.VALIDAR_TRIGGER_STATUS_ADE)) {
            consulta = "SELECT 'exists' as variable, 'true' as value FROM INFORMATION_SCHEMA.TRIGGERS WHERE TRIGGER_SCHEMA = DATABASE() AND TRIGGER_NAME = '" + RegraValidacaoTriggerStatusAutDesconto.TRIGGER_HISTORICO_STATUS_ADE + "'";
        } else if (regraValidacaoEnum.equals(RegraValidacaoEnum.VALIDAR_PERMISSOES_USUARIOS_SALARYPAY)) {
            consulta = "select 'exists' as variable, 'true' as value where exists (select 1 from tb_funcao_perfil p26 where p26.per_codigo = '" + CodedValues.PER_CODIGO_SERVIDOR + "' and p26.fun_codigo = '" + CodedValues.FUN_CONS_CONSIGNACAO + "')"
                    + " and exists (select 1 from tb_funcao_perfil p80 where p80.per_codigo = '" + CodedValues.PER_CODIGO_SERVIDOR + "' and p80.fun_codigo = '" + CodedValues.FUN_CANC_SOLICITACAO + "')"
                    + " and exists (select 1 from tb_funcao_perfil p57 where p57.per_codigo = '" + CodedValues.PER_CODIGO_SERVIDOR + "' and p57.fun_codigo = '" + CodedValues.FUN_RES_MARGEM + "')"
                    + " and exists (select 1 from tb_funcao_perfil p63 where p63.per_codigo = '" + CodedValues.PER_CODIGO_SERVIDOR + "' and p63.fun_codigo = '" + CodedValues.FUN_SOL_EMPRESTIMO + "')"
                    + " and exists (select 1 from tb_funcao_perfil p79 where p79.per_codigo = '" + CodedValues.PER_CODIGO_SERVIDOR + "' and p79.fun_codigo = '" + CodedValues.FUN_SIM_CONSIGNACAO + "')";
        }

        return instanciarQuery(session, consulta);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "variable",
                "value"
        };
    }
}
