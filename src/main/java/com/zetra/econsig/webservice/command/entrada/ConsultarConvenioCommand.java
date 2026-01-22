package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIOS;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarConvenioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar convênio</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarConvenioCommand extends RequisicaoExternaFolhaCommand {

    public ConsultarConvenioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String csaIdentificador = (String) parametros.get(CSA_IDENTIFICADOR);

        ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        String svcIdentificador = (String) parametros.get(SVC_IDENTIFICADOR);

        String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);

        String cnvCodVerba = (String) parametros.get(CNV_COD_VERBA);

        List<TransferObject> lstCnv = cnvDelegate.getCnvByIdentificadores(csaIdentificador, estIdentificador, orgIdentificador, svcIdentificador, cnvCodVerba, responsavel);

        parametros.put(CONVENIOS, lstCnv);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if (TextHelper.isNull(parametros.get(SVC_IDENTIFICADOR)) && TextHelper.isNull(parametros.get(CSA_IDENTIFICADOR))
                && TextHelper.isNull(parametros.get(ORG_IDENTIFICADOR)) && TextHelper.isNull(parametros.get(CNV_COD_VERBA))) {
            throw new ZetraException("mensagem.informe.filtros.consulta", responsavel);
        }
    }
}
