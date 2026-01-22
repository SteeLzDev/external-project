package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;

import java.sql.Date;
import java.util.EnumMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.CadastrarUsuario;
import com.zetra.econsig.webservice.soap.operacional.v7.Usuario;

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
        usuTO.setUsuEmail(getValue(usuario.getEmail()));
        usuTO.setUsuCPF(getValue(usuario.getCpf()));
        usuTO.setUsuTel(getValue(usuario.getTelefone()));
        usuTO.setUsuIpAcesso(getValue(usuario.getIpAcesso()));
        usuTO.setUsuDDNSAcesso(getValue(usuario.getDnsAcesso()));
        final XMLGregorianCalendar dataFimVigencia = getValue(usuario.getDataFimVigencia());
        if (dataFimVigencia != null) {
            usuTO.setUsuDataFimVig(new Date(dataFimVigencia.toGregorianCalendar().getTimeInMillis()));
        }
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, cadastrarUsuario.getUsuario());
        parametros.put(SENHA, cadastrarUsuario.getSenha());
        parametros.put(USUARIO_AFETADO, usuTO);
        parametros.put(PER_DESCRICAO, usuario.getPerfil());
        parametros.put(TIPO_ENTIDADE, getValue(usuario.getTipoEntidade()));
        parametros.put(ENTIDADE_CODIGO, getValue(usuario.getEntidadeCodigo()));

        return parametros;
    }
}