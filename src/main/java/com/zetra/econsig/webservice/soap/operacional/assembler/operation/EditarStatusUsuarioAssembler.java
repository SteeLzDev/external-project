package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusUsuario;
import com.zetra.econsig.webservice.soap.operacional.v7.SituacaoUsuario;

/**
 * <p>Title: EditarStatusUsuarioAssembler</p>
 * <p>Description: Assembler para EditarStatusUsuario.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class EditarStatusUsuarioAssembler extends BaseAssembler {

    private EditarStatusUsuarioAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(EditarStatusUsuario editarStatusUsuario) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, editarStatusUsuario.getUsuario());
        parametros.put(SENHA, editarStatusUsuario.getSenha());
        parametros.put(CONVENIO, getValue(editarStatusUsuario.getConvenio()));
        parametros.put(CLIENTE, getValue(editarStatusUsuario.getCliente()));

        parametros.put(USUARIO_AFETADO, editarStatusUsuario.getUsuarioAfetado());
        final SituacaoUsuario situacaoUsuario = editarStatusUsuario.getSituacaoUsuario();
        if (situacaoUsuario.getAtivo() == null) {
            situacaoUsuario.setAtivo(false);
        }
        if (situacaoUsuario.getBloqueado() == null) {
            situacaoUsuario.setBloqueado(false);
        }
        if (situacaoUsuario.getExcluido() == null) {
            situacaoUsuario.setExcluido(false);
        }
        parametros.put(SITUACAO_USUARIO, situacaoUsuario);

        parametros.put(TMO_OBS, getValue(editarStatusUsuario.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(editarStatusUsuario.getCodigoMotivoOperacao()));

        return parametros;
    }
}