package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;

import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.operacional.v7.SituacaoUsuario;

/**
 * <p>Title: EditarStatusUsuarioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de editar status de usuário</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EditarStatusUsuarioCommand extends RequisicaoExternaCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarStatusUsuarioCommand.class);

    public EditarStatusUsuarioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // Situação é obrigatório independente da configuração de campos
        final SituacaoUsuario situacao = (SituacaoUsuario) parametros.get(SITUACAO_USUARIO);
        if ((situacao == null) || (!situacao.getAtivo() && !situacao.getBloqueado() && !situacao.getExcluido())) {
            throw new ZetraException("mensagem.informe.usu.situacao", responsavel);
        }

    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

        final String usuarioAfetado = parametros.get(USUARIO_AFETADO).toString();
        final SituacaoUsuario situacao = (SituacaoUsuario) parametros.get(SITUACAO_USUARIO);

        final String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);
        final String obsMotivoOperacao = (String) parametros.get(TMO_OBS);

        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.USU_LOGIN, usuarioAfetado);

        final TransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuarioAfetado, responsavel);

        if (usuario == null) {
        	// Nenhum usuário encontrado
        	throw new ZetraException("mensagem.erro.usuario.nenhum.encontrado", responsavel);
        }

        final String usuCodigo = usuario.getAttribute(Columns.USU_CODIGO).toString();

        String codigoEntidade = null;
        String tipoEntidade = "";

		if (!TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO))) {
			codigoEntidade = (String) usuario.getAttribute(Columns.UCO_COR_CODIGO);
			tipoEntidade = AcessoSistema.ENTIDADE_COR;
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO))) {
        	codigoEntidade = (String) usuario.getAttribute(Columns.UCA_CSA_CODIGO);
        	tipoEntidade = AcessoSistema.ENTIDADE_CSA;
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.UCE_CSE_CODIGO))) {
        	codigoEntidade = (String) usuario.getAttribute(Columns.UCE_CSE_CODIGO);
        	tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.UOR_ORG_CODIGO))) {
        	codigoEntidade = (String) usuario.getAttribute(Columns.UOR_ORG_CODIGO);
        	tipoEntidade = AcessoSistema.ENTIDADE_ORG;
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.USP_CSE_CODIGO))) {
        	codigoEntidade = (String) usuario.getAttribute(Columns.USP_CSE_CODIGO);
        	tipoEntidade = AcessoSistema.ENTIDADE_SUP;
        } else {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        final boolean usuAfetadoIsCsa = AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade);

        final String status = situacao.getBloqueado() ? CodedValues.STU_BLOQUEADO : situacao.getExcluido() ? CodedValues.STU_EXCLUIDO : CodedValues.STU_ATIVO;
        final String funCodigo = situacao.getExcluido() ? (usuAfetadoIsCsa ? CodedValues.FUN_EXCL_USUARIO_CSA : CodedValues.FUN_EXCL_USUARIO_COR) : (usuAfetadoIsCsa ? CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA : CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR);

        // Verifica se responsável tem permissão para realizar a operação.
        // Validar se o usuário que realiza a operação tem permissão para as funções 328/329 - Bloquear/Desbloquear Usuário de CSA/COR, de acordo com o papel do usuário a ser bloqueado.
        // Caso o status seja alterado para excluído, validar as permissões 358/359 - Excluir Usuário de CSA/COR.
        if (!responsavel.temPermissao(funCodigo)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        final String stuCodigoAtual = usuario.getAttribute(Columns.USU_STU_CODIGO).toString();
        if ((CodedValues.STU_BLOQUEADO.equals(status) && CodedValues.STU_BLOQUEADO.equals(stuCodigoAtual)) ||
            (CodedValues.STU_ATIVO.equals(status) && CodedValues.STU_ATIVO.equals(stuCodigoAtual))) {
            throw new ZetraException("mensagem.erro.usuario.ja.esta.nesta.situacao", responsavel);
        }

        if (CodedValues.STU_EXCLUIDO.equals(stuCodigoAtual) ||
        	(CodedValues.STU_BLOQUEADO_POR_CSE.equals(stuCodigoAtual) && !responsavel.isCseSup())) {
            throw new ZetraException("mensagem.erro.situacao.usuario.nao.permite.operacao", responsavel);
        }

        if (!responsavel.isSup() && CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.equals(stuCodigoAtual)) {
            throw new ZetraException("mensagem.erro.nao.possivel.desbloquear.usuario.arg0.pois.foi.bloqueado.por.seguranca", responsavel, usuario.getAttribute(Columns.USU_LOGIN).toString());
        }

        String tmoCodigo = null;

        if (TextHelper.isNull(tmoIdentificador) && isExigeMotivoOperacao(funCodigo, responsavel)) {
            throw new ZetraException("mensagem.erro.informacao.motivo.operacao.ausente", responsavel);
        }

        if (!TextHelper.isNull(tmoIdentificador)) {
            try {
                final TipoMotivoOperacaoController tmoController = ApplicationContextProvider.getApplicationContext().getBean(TipoMotivoOperacaoController.class);
                final TipoMotivoOperacaoTransferObject tipoMotivo = tmoController.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);
                tmoCodigo = tipoMotivo.getTmoCodigo();

            } catch (final TipoMotivoOperacaoControllerException tex) {
                LOG.error(tex.getMessage(), tex);
                throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
            }
        }

        if (situacao.getExcluido()) {
            final UsuarioTransferObject criterioRemocao = new UsuarioTransferObject(usuCodigo);

            if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            	criterioRemocao.setAttribute(Columns.COR_CODIGO, codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            	criterioRemocao.setAttribute(Columns.CSA_CODIGO, codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
            	criterioRemocao.setAttribute(Columns.CSE_CODIGO, codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
            	criterioRemocao.setAttribute(Columns.ORG_CODIGO, codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            	criterioRemocao.setAttribute(Columns.CSE_CODIGO, codigoEntidade);
            }

            CustomTransferObject tmo = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmo.setAttribute(Columns.OUS_OBS, obsMotivoOperacao);
            }

            usuarioController.removeUsuario(criterioRemocao, tipoEntidade, tmo, responsavel);
        } else {
            usuarioController.bloquearDesbloquearUsuario(usuCodigo, status , tipoEntidade, tmoCodigo, obsMotivoOperacao, responsavel);
        }

    }

    private boolean isExigeMotivoOperacao (String funCodigo, AcessoSistema responsavel) {
        // Verifica se a função exige tipo de motivo da operação
        final Boolean exigeTipoMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel);

        // Busca atributos quanto a exigencia de Tipo de motivo da operacao
        if (!ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, CodedValues.TPC_SIM, responsavel) || !exigeTipoMotivoOperacao) {
            return false;
        }
        return true;
    }
}
