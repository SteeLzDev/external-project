package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.AtualizarMargem;

/**
 * <p>Title: AtualizarMargemAssembler</p>
 * <p>Description: Assembler para AtualizarMargem.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AtualizarMargemAssembler extends BaseAssembler {

    private AtualizarMargemAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(AtualizarMargem atualizarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SER_CPF, getValue(atualizarMargem.getCpf()));
        parametros.put(RSE_MATRICULA, atualizarMargem.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(atualizarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(atualizarMargem.getEstabelecimentoCodigo()));
        parametros.put(USUARIO, atualizarMargem.getUsuario());
        parametros.put(SENHA, atualizarMargem.getSenha());
        final Double margem1 = getValue(atualizarMargem.getMargem1());
        if ((margem1 == null) || margem1.equals(Double.NaN)) {
            parametros.put(MARGEM_1, null);
        } else {
            parametros.put(MARGEM_1, margem1);
        }
        final Double margem2 = getValue(atualizarMargem.getMargem2());
        if ((margem2 == null) || margem2.equals(Double.NaN)) {
            parametros.put(MARGEM_2, null);
        } else {
            parametros.put(MARGEM_2, margem2);
        }
        final Double margem3 = getValue(atualizarMargem.getMargem3());
        if ((margem3 == null) || margem3.equals(Double.NaN)) {
            parametros.put(MARGEM_3, null);
        } else {
            parametros.put(MARGEM_3, margem3);
        }
        return parametros;
    }
}