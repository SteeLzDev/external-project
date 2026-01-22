package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORGAOS;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarOrgaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar órgão</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarOrgaoCommand extends RequisicaoExternaFolhaCommand {

    public ConsultarOrgaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        CustomTransferObject criterio = new CustomTransferObject();

        criterio.setAttribute(Columns.ORG_IDENTIFICADOR, parametros.get(ORG_IDENTIFICADOR));
        criterio.setAttribute(Columns.EST_IDENTIFICADOR, parametros.get(EST_IDENTIFICADOR));

        List<TransferObject> orgaos = cseDelegate.lstOrgaos(criterio, responsavel);

        if (orgaos == null || orgaos.isEmpty()) {
            throw new ZetraException("mensagem.erro.nenhum.orgao.encontrado", responsavel);
        }

        parametros.put(ORGAOS, orgaos);
    }
}
