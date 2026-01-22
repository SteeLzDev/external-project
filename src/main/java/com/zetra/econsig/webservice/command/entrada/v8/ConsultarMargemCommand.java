package com.zetra.econsig.webservice.command.entrada.v8;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_ADICIONAIS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.VALORES_MARGEM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
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
public class ConsultarMargemCommand extends com.zetra.econsig.webservice.command.entrada.v3.ConsultarMargemCommand {

    public ConsultarMargemCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected Map<CamposAPI, Object> consultaMargemUnica(Map<CamposAPI, Object> parametros) throws ZetraException {
        Map<CamposAPI, Object> retorno = super.consultaMargemUnica(parametros);
        adicionarDadosServidor(retorno, responsavel);
        return retorno;
    }

    @Override
    protected Map<CamposAPI, Object> consultaMargemMultipla(String rseCodigo, Map<CamposAPI, Object> parametros) throws ParametroControllerException, ZetraException, ServidorControllerException {
        Map<CamposAPI, Object> retorno = super.consultaMargemMultipla(rseCodigo, parametros);
        adicionarDadosServidor(retorno, responsavel);
        return retorno;
    }

    @Override
    protected void setParametros(Map<CamposAPI, Object> servidor, Map<CamposAPI, Object> parametros, boolean multipla) {
        super.setParametros(servidor, parametros, multipla);

        Map<String, Object> valoresMargem = (Map<String, Object>) parametros.get(VALORES_MARGEM);
        if (valoresMargem == null || valoresMargem.isEmpty()) {
            valoresMargem = new HashMap<>();
            parametros.put(VALORES_MARGEM, valoresMargem);
        }

        Map<String, List<TransferObject>> mapDadosAdicionaisServidor = (Map<String, List<TransferObject>>) parametros.get(DADOS_ADICIONAIS);
        if (mapDadosAdicionaisServidor == null) {
            mapDadosAdicionaisServidor = new HashMap<>();
            parametros.put(DADOS_ADICIONAIS, mapDadosAdicionaisServidor);
        }
        mapDadosAdicionaisServidor.put(servidor.get(RSE_CODIGO).toString(), (List<TransferObject>) servidor.get(DADOS_ADICIONAIS));
    }

    private void adicionarDadosServidor(Map<CamposAPI, Object> retorno, AcessoSistema responsavel) throws ServidorControllerException {
        String serCodigo = (String) retorno.get(SER_CODIGO);
        if (!TextHelper.isNull(serCodigo)) {
            ServidorDelegate serDelegate = new ServidorDelegate();
            List<TransferObject> listaDadosAdicionaisServidor = serDelegate.lstDadosServidor(AcaoTipoDadoAdicionalEnum.CONSULTA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST, serCodigo, responsavel);
            retorno.put(DADOS_ADICIONAIS, listaDadosAdicionaisServidor);
        }
    }
}
