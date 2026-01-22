package com.zetra.econsig.webservice.command.entrada.v8;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_SISTEMA;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;

import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

import br.com.nostrum.simpletl.util.TextHelper;

/**
 * <p>Title: ConsultarParametrosCommand</p>
 * <p>Description:classe command que trata requisição externa ao eConsig de consultar parâmetros</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarParametrosCommand extends com.zetra.econsig.webservice.command.entrada.v2.ConsultarParametrosCommand {

    public ConsultarParametrosCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        
        if (verificarSeExecutarOperacao(parametros)) {
            super.preProcessa(parametros);
        }
        
    }

   
    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        if (verificarSeExecutarOperacao(parametros)) {
            super.executaOperacao(parametros);
        } else {
            this.recuperarParametrosSistema();
        }
        
    }

    private boolean verificarSeExecutarOperacao(Map<CamposAPI, Object> parametros) {

        if (parametros.containsKey(SERVICO_CODIGO) && !TextHelper.isNull(parametros.get(SERVICO_CODIGO))){
            return Boolean.TRUE;
        }
         
        boolean dadosSistema = recuperarValorDadosSistema(parametros);

        return !dadosSistema;
    }

    private boolean recuperarValorDadosSistema(Map<CamposAPI, Object> parametros) {
        
        if (parametros.containsKey(DADOS_SISTEMA) && parametros.get(DADOS_SISTEMA) != null) {
            return Boolean.TRUE.equals(parametros.get(DADOS_SISTEMA));
        }
        
        return Boolean.FALSE;
    }

}
