package com.zetra.econsig.webservice.command.entrada.v2;

import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;

import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarParametrosCommand</p>
 * <p>Description:classe command que trata requisição externa ao eConsig de consultar parâmetros</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarParametrosCommand extends com.zetra.econsig.webservice.command.entrada.ConsultarParametrosCommand {

    public static final String USA_CET = "USA_CET";

    public ConsultarParametrosCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected CustomTransferObject recuperarParametrosSistema() throws ZetraException {

        CustomTransferObject paramSet = super.recuperarParametrosSistema();

        // recupera param. tem cet
        boolean temCet = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_CET, responsavel);
        paramSet.setAttribute(USA_CET, temCet);

        parametros.put(PARAMETRO_SET, paramSet);

        return paramSet;

    }

}
