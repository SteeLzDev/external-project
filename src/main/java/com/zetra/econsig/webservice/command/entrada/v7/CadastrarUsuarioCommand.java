package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_ID;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.PER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.operacional.v7.PapelUsuario;

/**
 * <p>Title: CadastrarUsuarioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de criar usuário</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarUsuarioCommand extends RequisicaoExternaCommand {

    public CadastrarUsuarioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        final String tipoEntidade = validarTipoEntidade();
        final String funCodigo = retornaFuncaoParaValidacao(tipoEntidade);

        if ((funCodigo == null) || !responsavel.temPermissao(funCodigo)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        // Valida demais dados obrigatórios
        final UsuarioTransferObject usuario = (UsuarioTransferObject) parametros.get(USUARIO_AFETADO);

        // Nome deve ser informado
        if ((usuario == null) || TextHelper.isNull(usuario.getUsuNome())) {
            throw new ZetraException("mensagem.informe.usu.nome", responsavel);
        }

        // Nome deve ser informado
        if (TextHelper.isNull(usuario.getUsuLogin())) {
            throw new ZetraException("mensagem.informe.usu.login", responsavel);
        }

        final String codigoEntidade = validarCodigoEntidade(tipoEntidade);
        final String perCodigo = validarPerfilUsuario(tipoEntidade, codigoEntidade);

        // Seta qual a função está sendo acessada, para gravação de log
        responsavel.setFunCodigo(funCodigo);

        // Redefine tipo e código de entidade, e perfil do usuário após validação
        parametros.put(TIPO_ENTIDADE, tipoEntidade);
        parametros.put(ENTIDADE_CODIGO, codigoEntidade);
        parametros.put(PER_CODIGO, perCodigo);
    }

    protected String validarTipoEntidade() throws ZetraException {
        final PapelUsuario papel = (PapelUsuario) parametros.get(TIPO_ENTIDADE);

        if ((papel == null) || Boolean.TRUE.equals(papel.getCsa())) {
            // Se o tipo não é passado, então é usuário de CSA
            return AcessoSistema.ENTIDADE_CSA;
        } else if (Boolean.TRUE.equals(papel.getCor())) {
            return AcessoSistema.ENTIDADE_COR;
        } else {
            throw new ZetraException("mensagem.erro.tipo.entidade.invalido", responsavel);
        }
    }

    protected String validarCodigoEntidade(String tipoEntidade) throws ZetraException {
        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            return responsavel.getCodigoEntidade();

        } else {
            CorrespondenteTransferObject cor = null;
            if (responsavel.isCsa()) {
                final String idEntidade = (String) parametros.get(ENTIDADE_CODIGO);
                cor = csaDelegate.findCorrespondenteByIdn(idEntidade, responsavel.getCsaCodigo(), responsavel);
            } else if (responsavel.isCor()) {
                cor = csaDelegate.findCorrespondente(responsavel.getCorCodigo(), responsavel);
            }

            return cor.getCorCodigo();
        }
    }

    protected String retornaFuncaoParaValidacao(String tipoEntidade) throws ZetraException {
        String funCodigo = null;

        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            if (!responsavel.isCsa()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_CSA;
        } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            if (!responsavel.isCsaCor()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_COR;
        }

        return funCodigo;
    }

    protected String validarPerfilUsuario(String tipoEntidade, String codigoEntidade) throws ZetraException {
        String perCodigo = null;
        final String perDescricao = (String) parametros.get(PER_DESCRICAO);

        final UsuarioDelegate usuDelegate = new UsuarioDelegate();

        // Critério de busca: perfil ativo
        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) ? Columns.PCA_ATIVO : Columns.PCO_ATIVO, CodedValues.STS_ATIVO);

        // Com a descrição informada
        if (!TextHelper.isNull(perDescricao)) {
            criterio.setAttribute(Columns.PER_DESCRICAO, CodedValues.LIKE_MULTIPLO + perDescricao + CodedValues.LIKE_MULTIPLO);
        }

        final List<TransferObject> lstPerfil = usuDelegate.lstPerfil(tipoEntidade, codigoEntidade, criterio, responsavel);
        if ((lstPerfil == null) || lstPerfil.isEmpty()) {
            // Reporta erro de perfil não encontrado
            throw new ZetraException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        } else if (lstPerfil.size() > 1) {
            // Reporta erro de múltiplos perfis encontrados
            throw new ZetraException("mensagem.erro.multiplos.perfis.encontrados", responsavel);
        } else {
            perCodigo = lstPerfil.get(0).getAttribute(Columns.PER_CODIGO).toString();
        }

        return perCodigo;
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String tipoEntidade = parametros.get(TIPO_ENTIDADE).toString();
        final String codigoEntidade = parametros.get(ENTIDADE_CODIGO).toString();
        final String perCodigo = parametros.get(PER_CODIGO).toString();
        final UsuarioTransferObject usuario = (UsuarioTransferObject) parametros.get(USUARIO_AFETADO);

        // Usuário é criado ativo ...
        usuario.setStuCodigo(CodedValues.STU_ATIVO);

        // com uma senha inicial aleatória para o usuário
        final String usuSenha = SenhaHelper.gerarSenhaAleatoria(tipoEntidade, responsavel);
        usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuario.getUsuLogin(), usuSenha, false, responsavel));

        final boolean enviaEmailInicializacaoSenha = ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR, CodedValues.TPC_SIM, responsavel);
        if (enviaEmailInicializacaoSenha) {
            String linkAcessoSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
            if (TextHelper.isNull(linkAcessoSistema)) {
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
            // Remove última barra, caso assim esteja cadastrado no banco
            linkAcessoSistema = linkAcessoSistema.replaceFirst("/$", "");
            final String linkReinicializacao = linkAcessoSistema
                                       + "/v3/recuperarSenhaUsuario"
                                       + "?acao=iniciarUsuario"
                                       + "&enti=" + tipoEntidade
                                       ;

            usuario.setLinkRecuperarSenha(linkReinicializacao);
        }

        // Executa criação usuário
        final UsuarioDelegate usuDelegate = new UsuarioDelegate();
        final String usuCodigo = usuDelegate.createUsuario(usuario, perCodigo, codigoEntidade, tipoEntidade, null, true, usuSenha, responsavel);

        // Salva os dados necessários para a resposta da operação
        final TransferObject usuCriado = usuDelegate.obtemUsuarioTipo(usuCodigo, null, responsavel);

        parametros.put(ENTIDADE_ID, usuCriado.getAttribute("IDENTIFICADOR"));
        parametros.put(ENTIDADE_NOME, usuCriado.getAttribute("ENTIDADE"));
        parametros.put(USU_NOME, usuCriado.getAttribute(Columns.USU_NOME));
        parametros.put(USU_LOGIN, usuCriado.getAttribute(Columns.USU_LOGIN));

        if (!enviaEmailInicializacaoSenha) {
            parametros.put(USU_SENHA, usuSenha);
        }
    }
}
