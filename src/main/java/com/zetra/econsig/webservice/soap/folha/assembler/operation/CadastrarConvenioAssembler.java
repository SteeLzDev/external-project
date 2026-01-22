package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConvenio;
import com.zetra.econsig.webservice.soap.folha.v1.Convenio;

/**
 * <p>Title: CadastrarConvenioAssembler</p>
 * <p>Description: Assembler para CadastrarConvenio.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarConvenioAssembler extends BaseAssembler {

    private CadastrarConvenioAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarConvenio cadastrarConvenio) {
        final CustomTransferObject cnvTO = new CustomTransferObject();
        final Convenio convenio = cadastrarConvenio.getConvenio();

        cnvTO.setAttribute(Columns.SVC_IDENTIFICADOR, convenio.getCodigoServico());
        cnvTO.setAttribute(Columns.ORG_IDENTIFICADOR, convenio.getCodigoOrgao());
        cnvTO.setAttribute(Columns.CSA_IDENTIFICADOR, convenio.getCodigoConsignataria());
        cnvTO.setAttribute(Columns.EST_IDENTIFICADOR, convenio.getCodigoEstabelecimento());
        cnvTO.setAttribute(Columns.CNV_COD_VERBA, convenio.getVerbaConvenio());
        cnvTO.setAttribute(Columns.CNV_COD_VERBA_REF, getValue(convenio.getVerbaConvenioRef()));
        cnvTO.setAttribute(Columns.CNV_COD_VERBA_FERIAS, getValue(convenio.getVerbaConvenioFerias()));

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(CONVENIO, cnvTO);
        parametros.put(USUARIO, cadastrarConvenio.getUsuario());
        parametros.put(SENHA, cadastrarConvenio.getSenha());

        return parametros;
    }
}