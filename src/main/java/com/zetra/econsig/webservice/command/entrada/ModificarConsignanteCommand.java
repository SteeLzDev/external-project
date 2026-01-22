package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.BANCOS;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNANTE;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ModificarConsignanteCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para modificar consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModificarConsignanteCommand extends RequisicaoExternaFolhaCommand {

    public ModificarConsignanteCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        ConsignanteTransferObject cseTO = (ConsignanteTransferObject) parametros.get(CONSIGNANTE);
        ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        Short cseAtivo = cse.getCseAtivo();
        List<String> bcoCodigos = parametros.containsKey(BANCOS) ? Arrays.asList((String[]) parametros.get(BANCOS)) : null;

        // Remove os atributos que vieram na requisição
        cse.removeAll(cseTO);
        // Inclui os atributos que vieram na requisição
        cse.setAtributos(cseTO.getAtributos());

        // Caso não seja informada alteração de status do consignante, mantém o status atual
        if (TextHelper.isNull(cse.getCseAtivo())) {
            cse.setCseAtivo(cseAtivo);
        }

        cseDelegate.updateConsignante(cse, responsavel);

        if(!TextHelper.isNull(bcoCodigos) && !bcoCodigos.get(0).isBlank() && responsavel.isSup()) {
            cseDelegate.updateBancosCse(bcoCodigos, responsavel);
        }else if(TextHelper.isNull(bcoCodigos) && responsavel.isSup()){
            bcoCodigos = new ArrayList<>();
            cseDelegate.updateBancosCse(bcoCodigos, responsavel);
        }

        if(responsavel.isCse() && (!TextHelper.isNull(bcoCodigos) && !bcoCodigos.get(0).isBlank()) || (TextHelper.isNull(bcoCodigos))) {
            parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.sucesso.parcial.campos.codigo.banco", responsavel));
        }

    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }
}
