package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_MAE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.FUNCOES;
import static com.zetra.econsig.webservice.CamposAPI.PER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarUsuario;
import com.zetra.econsig.webservice.soap.folha.v1.SituacaoUsuario;
import com.zetra.econsig.webservice.soap.folha.v1.Usuario;

/**
 * <p>Title: CadastrarUsuarioAssembler</p>
 * <p>Description: Assembler para CadastrarUsuario.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarUsuarioAssembler extends BaseAssembler {

    private CadastrarUsuarioAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarUsuario cadastrarUsuario) {
        final UsuarioTransferObject usuTO = new UsuarioTransferObject();
        final Usuario usuario = cadastrarUsuario.getUsuarioAfetado();

        usuTO.setUsuLogin(usuario.getLogin());
        usuTO.setUsuNome(usuario.getNome());
        usuTO.setUsuSenha(getValue(usuario.getSenha()));
        usuTO.setUsuEmail(getValue(usuario.getEmail()));
        usuTO.setUsuCPF(getValue(usuario.getCpf()));
        usuTO.setUsuTel(getValue(usuario.getTelefone()));
        usuTO.setUsuCentralizador(getValue(usuario.getCentralizador()));

        final SituacaoUsuario situacaoUsuario = usuario.getSituacaoUsuario();
        if (situacaoUsuario != null) {
            if (Boolean.TRUE.equals(situacaoUsuario.getAtivo())) {
                usuTO.setStuCodigo(CodedValues.STU_ATIVO);
            }
            if (Boolean.TRUE.equals(situacaoUsuario.getBloqueado())) {
                usuTO.setStuCodigo(CodedValues.STU_BLOQUEADO);
            }
            if (Boolean.TRUE.equals(situacaoUsuario.getExcluido())) {
                usuTO.setStuCodigo(CodedValues.STU_EXCLUIDO);
            }
        }

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, cadastrarUsuario.getUsuario());
        parametros.put(SENHA, cadastrarUsuario.getSenha());
        parametros.put(USUARIO_AFETADO, usuTO);
        parametros.put(PER_CODIGO, getValue(usuario.getPerfilCodigo()));
        parametros.put(FUNCOES, usuario.getFuncaoCodigo());
        parametros.put(TIPO_ENTIDADE, usuario.getTipoEntidade());
        parametros.put(ENTIDADE_CODIGO, getValue(usuario.getEntidadeCodigo()));
        parametros.put(ENTIDADE_MAE_CODIGO, getValue(usuario.getEntidadeMaeCodigo()));

        return parametros;
    }
}
