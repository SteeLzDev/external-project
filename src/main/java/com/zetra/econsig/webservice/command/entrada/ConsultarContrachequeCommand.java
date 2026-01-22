package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ImpArqContrachequeController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarContrachequeCommand extends RequisicaoExternaCommand {

    public ConsultarContrachequeCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        //        responsavel.getRseMatricula();
        //        responsavel.getUsuNome();

        ImpArqContrachequeController impArqContrachequeController = ApplicationContextProvider.getApplicationContext().getBean(ImpArqContrachequeController.class);

        List<TransferObject> contracheques = null;
        Date periodo = (Date) parametros.get(PERIODO);

        try {
            if (!TextHelper.isNull(periodo)) {
                Date ccqPeriodo = DateHelper.toPeriodDate(periodo);
                contracheques = impArqContrachequeController.listarContrachequeRse(rseCodigo, ccqPeriodo, false, responsavel);
            } else {
                // Lista o último contracheque cadastrado caso não seja informado o período
                contracheques = impArqContrachequeController.listarContrachequeRse(rseCodigo, null, true, responsavel);
            }
        } catch (Exception ex) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        if (contracheques == null || contracheques.isEmpty()) {
            throw new ZetraException("mensagem.erro.servidor.contracheque.indisponivel", responsavel);
        }

        // Sempre retornará somente um contracheque
        parametros.put(CONTRACHEQUE, contracheques.get(0));
    }
}
