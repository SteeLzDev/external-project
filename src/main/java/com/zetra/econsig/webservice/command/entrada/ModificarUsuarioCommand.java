package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;
import static com.zetra.econsig.webservice.CamposAPI.USU_DATA_EXP_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ModificarUsuarioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de modificar usuário</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModificarUsuarioCommand extends CadastrarUsuarioCommand {

    public ModificarUsuarioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected Map<CamposAPI, Object> saveOrUpdateUsuario(UsuarioDelegate usuDelegate, UsuarioTransferObject usuarioTO, String perCodigo, List<String> lstFuncoes) throws UsuarioControllerException {
        String login = usuarioTO.getUsuLogin();
        UsuarioTransferObject usuario = usuDelegate.findUsuarioByLogin(login, responsavel);

        String usuCodigo = usuario.getUsuCodigo();
        String stuCodigoOld = usuario.getStuCodigo();
        String stuCodigoNew = usuarioTO.getStuCodigo();
        Date usuDataExpSenhaOld = usuario.getUsuDataExpSenha();

        if (!TextHelper.isNull(stuCodigoNew) && stuCodigoNew.equals(CodedValues.STU_EXCLUIDO)) {
            removeUsuario(usuDelegate, usuario);
            return null;
        }

        // Remove os atributos que vieram na requisição
        usuario.removeAll(usuarioTO);
        // Inclui os atributos que vieram na requisição
        usuario.setAtributos(usuarioTO.getAtributos());

        /**
         * Caso o login seja alerado, o sistema deverá gerar uma nova senha aleatória, expirada,
         * que deve ser alterada no primeiro acesso ao sistema
         */
        String usuSenha = null;
        String novoLogin = (String) parametros.get(NOVO_LOGIN);
        if (!TextHelper.isNull(novoLogin) && !novoLogin.equals(usuario.getUsuLogin())) {
            usuario.setUsuLogin(novoLogin);
            usuario.setUsuDataExpSenha(null);

            // Se não informou uma nova senha ao modificar o login, gera uma nova senha que será criptografada e retornada
            if (TextHelper.isNull(usuarioTO.getUsuSenha())) {
                usuSenha = SenhaHelper.gerarSenhaAleatoria(UsuarioHelper.obterTipoEntidade(usuarioTO), responsavel);
                usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuarioTO.getUsuLogin(), usuSenha, false, responsavel));
            }
        }

        // Caso seja informada uma nova senha, criptografa antes de salvar
        if (!TextHelper.isNull(usuarioTO.getUsuSenha())) {
            // Criptografa senha enviada pelo usuário
            usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuarioTO.getUsuLogin(), usuarioTO.getUsuSenha(), false, responsavel));
            // Se está alterando a senha, mantém a data de expiração
            usuario.setUsuDataExpSenha(usuDataExpSenhaOld);
            // Invalida possível senha que possa ter sido gerada anteriormente, senha que será salva é a informada na requisição
            usuSenha = null;
        }

        usuDelegate.updateUsuario(usuario, null, lstFuncoes, perCodigo, tipoEntidade, codigoEntidade, null, responsavel);

        // Verifica se o usuário está sendo bloqueado por um usuário da consignante
        if (!TextHelper.isNull(stuCodigoNew) && stuCodigoNew.equals(CodedValues.STU_BLOQUEADO) && responsavel.isCseSup()) {
            stuCodigoNew = CodedValues.STU_BLOQUEADO_POR_CSE;
        }

        // Se houve alteração de status cria ocorrência para o usuário
        if (!TextHelper.isNull(stuCodigoNew) && !stuCodigoOld.equals(stuCodigoNew)) {
            OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo((stuCodigoNew.equals(CodedValues.STU_ATIVO) ? CodedValues.TOC_DESBLOQUEIO_USUARIO : responsavel.isCseSup() ? CodedValues.TOC_BLOQUEIO_USUARIO_POR_CSE : CodedValues.TOC_BLOQUEIO_USUARIO));
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage((stuCodigoNew.equals(CodedValues.STU_ATIVO) ? "mensagem.ocorrencia.ous.obs.desbloqueio.usuario" : responsavel.isSup() ? "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.sup" : responsavel.isCse() ? "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.cse" : "mensagem.ocorrencia.ous.obs.bloqueio.usuario"), responsavel));
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

            usuDelegate.createOcorrenciaUsuario(ocorrencia, responsavel);
        }

        /*
         * O WebService deverá retornar, além do código de retorno e mensagem, as seguintes informações:
         * Identificador da entidade do usuário criado;
         * Nome da entidade do usuário criado;
         * Nome do usuário criado;
         * Login do usuário criado;
         * Senha Plana criada, caso não seja informada, nulo caso informado;
         * Data de expiração da senha do usuário criado.
        */
        TransferObject usuCriado = usuDelegate.obtemUsuarioTipo(usuario.getUsuCodigo(), null, responsavel);
        Map<CamposAPI, Object> usuCadastrado = new HashMap<>();
        usuCadastrado.put(ENTIDADE_CODIGO, usuCriado.getAttribute("IDENTIFICADOR"));
        usuCadastrado.put(ENTIDADE_NOME, usuCriado.getAttribute("ENTIDADE"));
        usuCadastrado.put(USU_NOME, usuCriado.getAttribute(Columns.USU_NOME));
        usuCadastrado.put(USU_LOGIN, usuCriado.getAttribute(Columns.USU_LOGIN));
        usuCadastrado.put(USU_SENHA, usuSenha);
        usuCadastrado.put(USU_DATA_EXP_SENHA, !TextHelper.isNull(usuCriado.getAttribute(Columns.USU_DATA_EXP_SENHA)) ? usuCriado.getAttribute(Columns.USU_DATA_EXP_SENHA) : DateHelper.getSystemDate());

        return usuCadastrado;
    }

    private void removeUsuario(UsuarioDelegate usuDelegate, UsuarioTransferObject usuario) throws UsuarioControllerException {
        String usuCodigo = usuario.getUsuCodigo();
        UsuarioTransferObject criterio = new UsuarioTransferObject(usuCodigo);

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
            criterio.setAttribute(Columns.COR_CODIGO, codigoEntidade);
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            criterio.setAttribute(Columns.CSA_CODIGO, codigoEntidade);
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
            criterio.setAttribute(Columns.CSE_CODIGO, codigoEntidade);
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            criterio.setAttribute(Columns.ORG_CODIGO, codigoEntidade);
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
            criterio.setAttribute(Columns.CSE_CODIGO, codigoEntidade);
        }

        /**
         *  Usuário centralizador só poderá ser removido por usuário de suporte
         */
        CustomTransferObject tmo = null;
        Boolean usuCentralizador = !TextHelper.isNull(usuario.getUsuCentralizador()) && usuario.getUsuCentralizador().equals(CodedValues.TPC_SIM);
        if (usuCentralizador && !responsavel.isSup()) {
            throw new UsuarioControllerException("mensagem.erro.usuario.centralizador.exclusao.somente.gestor", responsavel);
        } else {
            usuDelegate.removeUsuario(criterio, tipoEntidade, tmo, responsavel);
        }
    }

    @Override
    protected void validarTipoEntidade() throws ZetraException {
        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        UsuarioTransferObject usuario = (UsuarioTransferObject) parametros.get(USUARIO_AFETADO);
        TransferObject usuCriado = usuDelegate.obtemUsuarioTipo(null, usuario.getUsuLogin(), responsavel);

        tipoEntidade = (String) usuCriado.getAttribute("TIPO");
        codigoEntidade = (String) usuCriado.getAttribute("CODIGO");
    }

    @Override
    protected void validarCodigoEntidade() throws ZetraException {
        // Código é retornado no método validarTipoEntidade(), sobreescrita deste método é realizada para que não seja executado da classe mãe
    }

    @Override
    protected String retornaFuncaoParaValidacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String funCodigo = null;

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
            if (!responsavel.isCseSup()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_EDT_USUARIOS_CSE;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            if (!responsavel.isCseSupOrg()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_EDT_USUARIOS_ORG;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            if (responsavel.isCor()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_EDT_USUARIOS_CSA;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
            funCodigo = CodedValues.FUN_EDT_USUARIOS_COR;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
            if (!responsavel.isSup()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_EDT_USUARIOS_SUP;
        }

        return funCodigo;
    }
}
