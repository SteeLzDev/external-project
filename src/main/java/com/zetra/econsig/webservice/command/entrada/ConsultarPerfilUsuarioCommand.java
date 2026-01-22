package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_PERFIL_USUARIO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarUsuarioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de criar usuário</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarPerfilUsuarioCommand extends RequisicaoExternaFolhaCommand {

    public ConsultarPerfilUsuarioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        String funCodigo = retornaFuncaoParaValidacao();
        if (funCodigo == null || !responsavel.temPermissao(funCodigo)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        // Seta qual a função está sendo acessada, para gravação de log
        responsavel.setFunCodigo(funCodigo);
    }

    protected String retornaFuncaoParaValidacao() throws ZetraException {
        String funCodigo = null;

        if (responsavel.isCse()) {
            funCodigo = CodedValues.FUN_CONS_PERFIL_CSE;

        } else if (responsavel.isCsa()) {
            funCodigo = CodedValues.FUN_CONS_PERFIL_CSA;

        } else if (responsavel.isOrg()) {
            funCodigo = CodedValues.FUN_CONS_PERFIL_ORG;

        } else if (responsavel.isCor()) {
            funCodigo = CodedValues.FUN_CONS_PERFIL_COR;

        } else if (responsavel.isSup()) {
            funCodigo = CodedValues.FUN_CONS_PERFIL_SUP;
        }

        return funCodigo;
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        // Executa consulta de peril
        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        CustomTransferObject criterio = new CustomTransferObject();
        List<TransferObject> lstPerfil = usuDelegate.lstPerfil(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), criterio, responsavel);

        if (lstPerfil != null && !lstPerfil.isEmpty()) {
            parametros.put(CONSULTA_PERFIL_USUARIO, lstPerfil);
        } else {
            throw new ZetraException("mensagem.erro.nenhum.registro.encontrado", responsavel);
        }
    }
}
