package com.zetra.econsig.helper.validacaoambiente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.ambiente.ValidacaoAmbienteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.RegraValidacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RegraValidacaoPermissoesUsuarioSalaryPay</p>
 * <p>Description: Regra que verifica se usuários servidores possuem as permissões necessárias para sistemas SalaryPay.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoPermissoesUsuarioSalaryPay implements RegraValidacaoAmbienteInterface {

    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        Map<Boolean, String> resultado = new HashMap<>();
        if (!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_INTEGRACAO_SALARYPAY, AcessoSistema.getAcessoUsuarioSistema())) {
            return resultado;
        }

        ValidacaoAmbienteController validacaoAmbienteController = ApplicationContextProvider.getApplicationContext().getBean(ValidacaoAmbienteController.class);
        List<TransferObject> listResult = validacaoAmbienteController.obterValorRegraValidacaoAmbiente(RegraValidacaoEnum.VALIDAR_PERMISSOES_USUARIOS_SALARYPAY);

        if (listResult == null || listResult.isEmpty()) {
            resultado.put(false, "false");
            return resultado;
        }

        for (TransferObject cto : listResult) {
            String permissoesSalaryPay = (String) cto.getAttribute("value");
            resultado.put(Boolean.valueOf(permissoesSalaryPay), permissoesSalaryPay);
        }
        return resultado;
    }

}
