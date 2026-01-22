package com.zetra.econsig.webservice.command.entrada.v3;

import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_LIMITE;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarMargemCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar margem</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarMargemCommand extends com.zetra.econsig.webservice.command.entrada.ConsultarMargemCommand {

    public ConsultarMargemCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void setParametros(Map<CamposAPI, Object> servidor, Map<CamposAPI, Object> parametros, boolean multipla) {
        super.setParametros(servidor, parametros, multipla);

        List<Map<CamposAPI, Object>> lstInfoMargem = (List<Map<CamposAPI, Object>>) parametros.get(INFO_MARGEM);
        Map<CamposAPI, Object> infoMargem = multipla ? lstInfoMargem.get(lstInfoMargem.size() - 1) : null;

        // Paramêtro para margem limite por consignatária
        Short codMargemLimite = (ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel) != null && !ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).equals("")) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : 0;
        // Verifica se pode mostrar margem limite por csa
        if (multipla && (codMargemLimite != null && !codMargemLimite.equals(CodedValues.INCIDE_MARGEM_NAO))) {
            infoMargem.put(VALOR_MARGEM_LIMITE, servidor.get(VALOR_MARGEM_LIMITE));
        } else {
            parametros.put(VALOR_MARGEM_LIMITE, servidor.get(VALOR_MARGEM_LIMITE));
        }

        if (multipla) {
            lstInfoMargem.remove(lstInfoMargem.size() - 1);
            lstInfoMargem.add(infoMargem);
        }
    }
}
