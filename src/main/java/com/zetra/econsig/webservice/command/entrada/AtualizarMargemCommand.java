package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;

import java.math.BigDecimal;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AtualizarMargemCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de atualizar margem</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizarMargemCommand extends RequisicaoExternaFolhaCommand {

    public AtualizarMargemCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        RegistroServidorTO rseTO = new RegistroServidorTO(rseCodigo);

        Double rseMargem1 = (Double) parametros.get(MARGEM_1);
        if (rseMargem1 != null) {
            rseTO.setRseMargem(BigDecimal.valueOf(rseMargem1));
        }
        Double rseMargem2 = (Double) parametros.get(MARGEM_2);
        if (rseMargem2 != null) {
            rseTO.setRseMargem2(BigDecimal.valueOf(rseMargem2));
        }
        Double rseMargem3 = (Double) parametros.get(MARGEM_3);
        if (rseMargem3 != null) {
            rseTO.setRseMargem3(BigDecimal.valueOf(rseMargem3));
        }

        ServidorDelegate serDelegate = new ServidorDelegate();
        serDelegate.updateRegistroServidor(rseTO, !responsavel.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL), true, false, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if(TextHelper.isNull(parametros.get(MARGEM_1)) && TextHelper.isNull(parametros.get(MARGEM_2)) && TextHelper.isNull(parametros.get(MARGEM_3))) {
            throw new ZetraException("mensagem.informe.rse.margem.ao.menos.um", responsavel);
        }
    }
}
