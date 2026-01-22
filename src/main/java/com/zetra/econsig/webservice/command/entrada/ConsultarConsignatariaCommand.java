package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIAS;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarConsignatariaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar consignatária</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarConsignatariaCommand extends RequisicaoExternaFolhaCommand {

    public ConsultarConsignatariaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        String csaIdentificador = (String) parametros.get(CSA_IDENTIFICADOR);
        TransferObject criterio = null;

        if (!TextHelper.isNull(csaIdentificador)) {
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_IDENTIFICADOR, csaIdentificador);
        }

        List<TransferObject> consignatarias = csaDelegate.lstConsignatarias(criterio, responsavel);

        if (consignatarias == null || consignatarias.isEmpty()) {
            throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
        }

        parametros.put(CONSIGNATARIAS, consignatarias);
    }
}
