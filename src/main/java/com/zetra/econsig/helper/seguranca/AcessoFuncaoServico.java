package com.zetra.econsig.helper.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AcessoFuncaoServico</p>
 * <p>Description: Classe utilitária que verifica se usuário tem permissão
 *    de executar a função dada para um Serviço.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcessoFuncaoServico {

    private static UsuarioDelegate usuDelegate;
    private static ConvenioDelegate cnvDelegate;

    public static boolean temAcessoFuncao(HttpServletRequest request, String funcao, String usuario, String servico) {
        HttpSession session = request.getSession();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (usuDelegate == null) {
                usuDelegate = new UsuarioDelegate();
            }

            if (cnvDelegate == null) {
                cnvDelegate = new ConvenioDelegate();
            }

            if (!funcao.equals("") && !usuario.equals("") && !servico.equals("")) {
                // Testa se o usúario tem um bloqueio para a função e o serviço correspondentes.
                if (usuDelegate.usuarioTemBloqueioFuncao(usuario, funcao, servico, responsavel)) {
                    CustomTransferObject objFuncao = usuDelegate.getFuncao(funcao, responsavel);
                    String funDescricao = "";
                    if (objFuncao != null) {
                        funDescricao = objFuncao.getAttribute(Columns.FUN_DESCRICAO).toString();
                    }

                    ServicoTransferObject objServico = cnvDelegate.findServico(servico, responsavel);
                    String svcDescricao = "";
                    if (objServico != null) {
                        svcDescricao = objServico.getSvcDescricao();
                    }

                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.possui.permissao.servico", responsavel, funDescricao, svcDescricao));
                    return false;
                }
            } else {
                return true;
            }
            return true;
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return false;
        }
    }
}
