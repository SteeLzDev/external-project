package com.zetra.econsig.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.usuario.UsuarioController;

@Component
public class ConfiguradorPermissoesUsuario {

	@Autowired
    private UsuarioController usuarioController;

	public void carregarPermissoes(AcessoSistema responsavel, String usuCodigo, String entidade, String codigoEntidade) throws UsuarioControllerException {
		responsavel.setPermissoes(usuarioController.selectFuncoes(usuCodigo, codigoEntidade, entidade, AcessoSistema.getAcessoUsuarioSistema()));
		responsavel.setPermissaoUnidadesEdt(usuarioController.unidadesPermissaoEdtUsuario(usuCodigo, AcessoSistema.getAcessoUsuarioSistema()));
	}

}
