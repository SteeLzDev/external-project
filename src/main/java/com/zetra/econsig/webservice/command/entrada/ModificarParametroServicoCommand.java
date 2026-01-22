package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.PSE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.PSE_VLR_REF;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TPS_CODIGO;

import java.util.Map;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.TipoParamSvc;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

public class ModificarParametroServicoCommand extends RequisicaoExternaFolhaCommand {

    public ModificarParametroServicoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConvenioDelegate cnvDelegate = new ConvenioDelegate();

        String svcIdentificador = parametros.get(SVC_IDENTIFICADOR).toString();
        String tpsCodigo = parametros.get(TPS_CODIGO).toString();
        String pseVlr = (String) parametros.get(PSE_VLR);
        String pseVlrRef = (String) parametros.get(PSE_VLR_REF);

        ServicoTransferObject servico = cnvDelegate.findServicoByIdn(svcIdentificador, responsavel);
        TipoParamSvc tipoParam = parametroController.findTipoParamServico(tpsCodigo, responsavel);

        if (tipoParam == null) {
            throw new ZetraException("mensagem.erro.parametro.servico.incorreto", responsavel);
        }

        ParamSvcCseTO paramSvcCse = new ParamSvcCseTO(tpsCodigo, CodedValues.CSE_CODIGO_SISTEMA, servico.getSvcCodigo());
        paramSvcCse.setPseVlr(pseVlr);
        paramSvcCse.setPseVlrRef(pseVlrRef);
        parametroController.updateParamSvcCse(paramSvcCse, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }
}
