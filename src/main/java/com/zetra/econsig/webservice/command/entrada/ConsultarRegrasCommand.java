package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.REGRAS;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.RegraConvenioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarOrgaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar regras</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarRegrasCommand extends RequisicaoExternaCommand {

    public ConsultarRegrasCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
    	String csaCodigo = (String) parametros.get(CSA_CODIGO);
    	RegraConvenioDelegate regraDelegate = new RegraConvenioDelegate();
        List<TransferObject> regras = regraDelegate.listaRegrasConvenioByCsa(csaCodigo, responsavel);

        if (regras == null || regras.isEmpty()) {
            parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.regra.convenio.encontrada", responsavel));
		    parametros.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.regra.convenio.encontrada" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
        }

        parametros.put(REGRAS, regras);
    }
}
