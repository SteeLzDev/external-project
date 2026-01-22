package com.zetra.econsig.service.seguranca;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SegurancaControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.OperacaoLiberaMargem;
import com.zetra.econsig.persistence.entity.OperacaoLiberaMargemHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.query.seguranca.ListaNivelSegurancaParamSistQuery;
import com.zetra.econsig.persistence.query.seguranca.ListaNivelSegurancaQuery;
import com.zetra.econsig.persistence.query.seguranca.ListarOperacoesLiberacaoMargemConfirmadasQuery;
import com.zetra.econsig.persistence.query.seguranca.ListarOperacoesLiberacaoMargemParaBloqueioQuery;
import com.zetra.econsig.persistence.query.seguranca.ListarOperacoesLiberacaoMargemParaConfirmacaoQuery;
import com.zetra.econsig.persistence.query.seguranca.ListarUsuariosLiberacaoMargemParaBloqueioQuery;
import com.zetra.econsig.persistence.query.seguranca.ObtemTotalOperacoesLiberacaoMargemQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SegurancaControllerBean</p>
 * <p>Description: Session Bean para operações de segurança</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SegurancaControllerBean implements SegurancaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SegurancaControllerBean.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public List<TransferObject> lstNivelSeguranca(AcessoSistema responsavel) throws SegurancaControllerException {
        try {
            ListaNivelSegurancaQuery query = new ListaNivelSegurancaQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> lstNivelSegurancaParamSist(AcessoSistema responsavel) throws SegurancaControllerException {
        try {
            ListaNivelSegurancaParamSistQuery query = new ListaNivelSegurancaParamSistQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String obtemNivelSeguranca(AcessoSistema responsavel) throws SegurancaControllerException {
        String nivelSeguranca = ApplicationResourcesHelper.getMessage("rotulo.nivel.seguranca.personalizado", responsavel);

        // Obtém a listagem de configuração dos níveis de segurança
        List<TransferObject> configuracao = lstNivelSegurancaParamSist(responsavel);
        if (configuracao != null && configuracao.size() > 0) {
            Iterator<TransferObject> it = configuracao.iterator();
            TransferObject to = null;

            // Contadores para controle
            int totalParam = 0;
            int totalCorretos = 0;

            String nsgCodigoAnterior = null;
            String nsgCodigo = null;
            String nsgDescricao = null;
            String tpcCodigo = null;
            String tpcDominio = null;
            String nspVlrEsperado = null;
            String psiVlr = null;

            while (it.hasNext()) {
                to = it.next();
                nsgCodigo = to.getAttribute(Columns.NSG_CODIGO).toString();

                // Se mudou o nível, verifica se o nível anterior foi alcançado
                if (nsgCodigoAnterior != null && !nsgCodigoAnterior.equals(nsgCodigo)) {
                    if (totalParam > 0 && totalParam == totalCorretos) {
                        //LOG.debug("Nível de Segurança " + nsgCodigoAnterior + " OK!");
                        nivelSeguranca = nsgDescricao;
                    }
                    // Zera os contadores
                    totalParam = 0;
                    totalCorretos = 0;
                }
                nsgCodigoAnterior = nsgCodigo;

                // Obtém as informações para validação
                nsgDescricao = to.getAttribute(Columns.NSG_DESCRICAO).toString();
                tpcCodigo = to.getAttribute(Columns.TPC_CODIGO).toString();
                tpcDominio = to.getAttribute(Columns.TPC_DOMINIO).toString();
                nspVlrEsperado = to.getAttribute(Columns.NSP_VLR_ESPERADO).toString();
                psiVlr = (String) to.getAttribute(Columns.PSI_VLR);

                //LOG.debug("NÍVEL[" + nsgCodigo + "],PARAM[" + tpcCodigo + "],ESPERADO[" + nspVlrEsperado + "],ATUAL[" + psiVlr + "]");

                // Verifica se o parâmetro está de acordo com o valor esperado para o nível
                boolean vlrEstaCorreto = vlrParamCorrespondeEsperado(tpcCodigo, tpcDominio, psiVlr, nspVlrEsperado);

                // Incrementa os contadores
                totalParam++;
                totalCorretos += (vlrEstaCorreto ? 1 : 0);
            }

            // Verifica se o último nível foi alcançado
            if (totalParam > 0 && totalParam == totalCorretos) {
                //LOG.debug("Nível de Segurança " + nsgCodigoAnterior + " OK!");
                nivelSeguranca = nsgDescricao;
            }
        }

        return nivelSeguranca;
    }

    @Override
    public List<Map<String, String>> detalharNivelSegurancaParamSist(AcessoSistema responsavel) throws SegurancaControllerException {
        // Lista com as configurações do nível de segurança,
        // ordenada de acordo com a ordem de retorno da consulta.
        List<Map<String, String>> nivelSegurancaList = new ArrayList<>();

        // Mapa onde a chave é o código do parâmetro de sistema
        // e o valor é um mapa com as informações sobre o parâmetro,
        // incluindo a descrição e os valores esperados para os níveis.
        Map<String, Map<String, String>> nivelSegurancaMap = new HashMap<>();

        // Lista as configurações de níveis de segurança pelos parâmetros
        // de sistema para montar a exibição tabular dos níveis.
        List<TransferObject> nivelParamSist = lstNivelSegurancaParamSist(responsavel);
        if (nivelParamSist != null && nivelParamSist.size() > 0) {
            for (TransferObject to : nivelParamSist) {
                String nsgDescricao = to.getAttribute(Columns.NSG_DESCRICAO).toString();
                String tpcCodigo = to.getAttribute(Columns.TPC_CODIGO).toString();
                String tpcDescricao = to.getAttribute(Columns.TPC_DESCRICAO).toString();
                String tpcDominio = to.getAttribute(Columns.TPC_DOMINIO).toString();
                String nspVlrEsperado = to.getAttribute(Columns.NSP_VLR_ESPERADO).toString();
                String psiVlr = (String) to.getAttribute(Columns.PSI_VLR);

                Map<String, String> valoresParam = null;
                if (nivelSegurancaMap.containsKey(tpcCodigo)) {
                    // Se o parâmetro já existe no Mapa, então recupera as informações
                    valoresParam = nivelSegurancaMap.get(tpcCodigo);
                } else {
                    // Se é a primeira ocorrência do parâmetro, cria um Mapa
                    // onde as informações serão armazenadas
                    valoresParam = new HashMap<>();
                    valoresParam.put("CODIGO", tpcCodigo);
                    valoresParam.put("DESCRICAO", tpcDescricao);
                    valoresParam.put("VALOR_ATUAL", psiVlr);

                    // Armazena no Mapa para facilitar a recuperação na próxima iteração
                    nivelSegurancaMap.put(tpcCodigo, valoresParam);
                    // Armazena na Lista para o retorno da operação
                    nivelSegurancaList.add(valoresParam);
                }

                // Armazena o valor esperado para o nível de segurança
                valoresParam.put(nsgDescricao, nspVlrEsperado);
                // Armazena indicador se o valor atual está de acordo com o
                // esperado para o nível de segurança
                valoresParam.put(nsgDescricao + " OK?", vlrParamCorrespondeEsperado(tpcCodigo, tpcDominio, psiVlr, nspVlrEsperado) ? "S" : "N");
            }
        }

        return nivelSegurancaList;
    }

    private boolean vlrParamCorrespondeEsperado(String tpcCodigo, String tpcDominio, String psiVlr, String nspVlrEsperado) {
        boolean vlrEstaCorreto = false;
        if (tpcDominio.equals("INT")) {
            // Para domínios INT, verifica operadores como >, <, >=, <=
            if (!TextHelper.isNull(psiVlr)) {
                try {
                    int valorAtual = Integer.parseInt(psiVlr);
                    if (nspVlrEsperado.indexOf("<=") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace("<=", "").trim());
                        vlrEstaCorreto = valorAtual > 0 && valorAtual <= valorEsperado;
                    } else if (nspVlrEsperado.indexOf("<") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace("<", "").trim());
                        vlrEstaCorreto = valorAtual > 0 && valorAtual < valorEsperado;
                    } else if (nspVlrEsperado.indexOf(">=") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace(">=", "").trim());
                        vlrEstaCorreto = valorAtual >= valorEsperado;
                    } else if (nspVlrEsperado.indexOf(">") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace(">", "").trim());
                        vlrEstaCorreto = valorAtual > valorEsperado;
                    } else if (nspVlrEsperado.indexOf("!=") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace("!=", "").trim());
                        vlrEstaCorreto = valorAtual != valorEsperado;
                    } else if (nspVlrEsperado.indexOf("=") != -1) {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.replace("=", "").trim());
                        vlrEstaCorreto = valorAtual == valorEsperado;
                    } else {
                        int valorEsperado = Integer.parseInt(nspVlrEsperado.trim());
                        vlrEstaCorreto = valorAtual == valorEsperado;
                    }
                } catch (NumberFormatException ex) {
                    LOG.error("Valor incorreto para o parâmetro de sistema '" + tpcCodigo + "'.", ex);
                }
            }

        } else if (nspVlrEsperado.indexOf("|") != -1) {
            // Verifica por um conjunto de valores
            String[] valoresEsperados = nspVlrEsperado.split("|");
            for (String valoresEsperado : valoresEsperados) {
                if (valoresEsperado.equals(psiVlr)) {
                    vlrEstaCorreto = true;
                    break;
                }
            }

        } else {
            // Para os demais domínios, verifica apenas se o valor esperado é igual ao atual
            vlrEstaCorreto = (nspVlrEsperado.equals(psiVlr));
        }

        return vlrEstaCorreto;
    }

    @Override
    public void removerOperacoesLiberacaoMargemPosPrazo(AcessoSistema responsavel) throws SegurancaControllerException {
        int horas = 24;
        try {
            Object paramQtdHoras = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_HORAS_CONTROLE_OPERACOES_LIBERACAO_MARGEM, responsavel);
            horas = (TextHelper.isNum(paramQtdHoras) ? Integer.valueOf(paramQtdHoras.toString()) : 24);
        } catch (NumberFormatException ex) {
            LOG.error("Valor incorreto para o parâmetro de sistema '" + CodedValues.TPC_QTD_HORAS_CONTROLE_OPERACOES_LIBERACAO_MARGEM + "'.", ex);
        }
        try {
            Date olmData = DateHelper.addHours(DateHelper.getSystemDatetime(), -1 * horas);
            OperacaoLiberaMargemHome.removeByDateLessThan(olmData);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void registrarOperacoesLiberacaoMargem(String rseCodigo, String adeCodigo, AcessoSistema responsavel) throws SegurancaControllerException {
        try {
            if (!responsavel.isSistema() && !responsavel.isSer() && !responsavel.isSup()) {
                OperacaoLiberaMargemHome.create(rseCodigo, responsavel.getUsuCodigo(), responsavel.getCsaCodigo(), responsavel.getIpUsuario(), DateHelper.getSystemDatetime(), "N", "N", adeCodigo);
            }
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void avaliarOperacoesLiberacaoMargem(AcessoSistema responsavel) throws SegurancaControllerException {
        // Recupera os parâmetros de sistema referentes a limite de operações que liberam margem
        Object paramQtdOperacoesPorUsuEnviaNotificacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_ENVIA_NOTIFICACAO, responsavel);
        int sisQtdOperacoesPorUsuParaNotificacao = (TextHelper.isNum(paramQtdOperacoesPorUsuEnviaNotificacao) ? Integer.valueOf(paramQtdOperacoesPorUsuEnviaNotificacao.toString()) : 0);

        Object paramQtdOperacoesPorUsuBloqueiaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_BLOQUEIA_USUARIO, responsavel);
        int sisQtdOperacoesPorUsuParaBloqueio = (TextHelper.isNum(paramQtdOperacoesPorUsuBloqueiaUsuario) ? Integer.valueOf(paramQtdOperacoesPorUsuBloqueiaUsuario.toString()) : 0);

        Object paramQtdOperacoesPorCsaEnviaNotificacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_ENVIA_NOTIFICACAO, responsavel);
        int sisQtdOperacoesPorCsaParaNotificacao = (TextHelper.isNum(paramQtdOperacoesPorCsaEnviaNotificacao) ? Integer.valueOf(paramQtdOperacoesPorCsaEnviaNotificacao.toString()) : 0);

        Object paramQtdOperacoesPorCsaBloqueiaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_BLOQUEIA_USUARIO, responsavel);
        int sisQtdOperacoesPorCsaParaBloqueio = (TextHelper.isNum(paramQtdOperacoesPorCsaBloqueiaUsuario) ? Integer.valueOf(paramQtdOperacoesPorCsaBloqueiaUsuario.toString()) : 0);

        try {
            ListarOperacoesLiberacaoMargemConfirmadasQuery query = new ListarOperacoesLiberacaoMargemConfirmadasQuery();
            List<TransferObject> dadosOperacoesConfirmadas = query.executarDTO();
            if (dadosOperacoesConfirmadas != null && !dadosOperacoesConfirmadas.isEmpty()) {

                for (TransferObject to : dadosOperacoesConfirmadas) {
                    String usuCodigo = (String) to.getAttribute(Columns.OLM_USU_CODIGO);
                    String csaCodigo = (String) to.getAttribute(Columns.OLM_CSA_CODIGO);

                    int qtdOperacoesPorUsuParaNotificacao = sisQtdOperacoesPorUsuParaNotificacao;
                    int qtdOperacoesPorUsuParaBloqueio = sisQtdOperacoesPorUsuParaBloqueio;
                    int qtdOperacoesPorCsaParaNotificacao = sisQtdOperacoesPorCsaParaNotificacao;
                    int qtdOperacoesPorCsaParaBloqueio = sisQtdOperacoesPorCsaParaBloqueio;
                    boolean usuarioBloqueado = false;

                    if (!TextHelper.isNull(csaCodigo)) {
                        // Verifica se os parâmetros de consignatária foram configurados para sobrepor os parâmetros de sistema
                        String tpaCodigo = "";
                        try {
                            String paramCsaQtdOperacoesPorUsuEnviaNotificacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_ENVIA_NOTIFICACAO, responsavel);
                            if (TextHelper.isNum(paramCsaQtdOperacoesPorUsuEnviaNotificacao) && Integer.valueOf(paramCsaQtdOperacoesPorUsuEnviaNotificacao) > 0) {
                                qtdOperacoesPorUsuParaNotificacao = Integer.valueOf(paramCsaQtdOperacoesPorUsuEnviaNotificacao);
                            }
                            String paramCsaQtdOperacoesPorUsuBloqueiaUsuario = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_BLOQUEIA_USUARIO, responsavel);
                            if (TextHelper.isNum(paramCsaQtdOperacoesPorUsuBloqueiaUsuario) && Integer.valueOf(paramCsaQtdOperacoesPorUsuBloqueiaUsuario) > 0) {
                                qtdOperacoesPorUsuParaBloqueio = Integer.valueOf(paramCsaQtdOperacoesPorUsuBloqueiaUsuario);
                            }
                            String paramCsaQtdOperacoesPorCsaEnviaNotificacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_ENVIA_NOTIFICACAO, responsavel);
                            if (TextHelper.isNum(paramCsaQtdOperacoesPorCsaEnviaNotificacao) && Integer.valueOf(paramCsaQtdOperacoesPorCsaEnviaNotificacao) > 0) {
                                qtdOperacoesPorCsaParaNotificacao = Integer.valueOf(paramCsaQtdOperacoesPorCsaEnviaNotificacao);
                            }
                            String paramCsaQtdOperacoesPorCsaBloqueiaUsuario = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_BLOQUEIA_USUARIO, responsavel);
                            if (TextHelper.isNum(paramCsaQtdOperacoesPorCsaBloqueiaUsuario) && Integer.valueOf(paramCsaQtdOperacoesPorCsaBloqueiaUsuario) > 0) {
                                qtdOperacoesPorCsaParaBloqueio = Integer.valueOf(paramCsaQtdOperacoesPorCsaBloqueiaUsuario);
                            }
                        } catch (ParametroControllerException | NumberFormatException ex) {
                            LOG.error("Valor incorreto para o parâmetro de consignatária '" + tpaCodigo + "'.", ex);
                        }
                    }

                    // Caso o usuário operador do sistema seja de CSA ou COR, avalia todas as operações de todos os usuários de CSA/COR da mesma CSA do usuário operador
                    if (!TextHelper.isNull(csaCodigo) && (qtdOperacoesPorCsaParaNotificacao > 0 || qtdOperacoesPorCsaParaBloqueio > 0)) {
                        int qtdOperacoesLiberacaoMargemPorCsa = recuperarQtdOperacoesLiberacaoMargemPorCsa(csaCodigo, responsavel);

                        if (qtdOperacoesPorCsaParaBloqueio > 0 && qtdOperacoesLiberacaoMargemPorCsa >= qtdOperacoesPorCsaParaBloqueio) {
                            bloquearUsuariosCsaPorMotivoSeguranca(csaCodigo, responsavel);
                            usuarioBloqueado = true;
                        } else if (qtdOperacoesPorCsaParaNotificacao > 0 && qtdOperacoesLiberacaoMargemPorCsa >= qtdOperacoesPorCsaParaNotificacao) {
                            enviarNotificacaoSegurancaPorCsa(csaCodigo, responsavel);
                        }
                    }

                    // Caso o usuário operador do sistema não tenha sido bloqueado no passo anterior, avalia as operações do próprio usuário
                    if (!usuarioBloqueado && (qtdOperacoesPorUsuParaNotificacao > 0 || qtdOperacoesPorUsuParaBloqueio > 0)) {
                        int qtdOperacoesLiberacaoMargemPorUsu = recuperarQtdOperacoesLiberacaoMargemPorUsu(usuCodigo, responsavel);

                        if (qtdOperacoesPorUsuParaBloqueio > 0 && qtdOperacoesLiberacaoMargemPorUsu >= qtdOperacoesPorUsuParaBloqueio) {
                            bloquearUsuarioPorMotivoSeguranca(usuCodigo, csaCodigo, responsavel);
                        } else if (qtdOperacoesPorUsuParaNotificacao > 0 && qtdOperacoesLiberacaoMargemPorUsu >= qtdOperacoesPorUsuParaNotificacao) {
                            enviarNotificacaoSegurancaPorUsu(usuCodigo, csaCodigo, responsavel);
                        }
                    }
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean exigirCaptchaOperacoesLiberacaoMargem(AcessoSistema responsavel) {
        int qtdOperacoesPorUsuParaExigirCaptcha = 0;
        int qtdOperacoesPorCsaParaExigirCaptcha = 0;
        try {
            if (responsavel.isSup() || TextHelper.isNull(responsavel.getFunCodigo())) {
                return false;
            }
            // Verifica se a operação que está em sendo executada é pode liberar margem
            if (isFuncaoLiberaMargem(responsavel.getFunCodigo())) {
                String tpcCodigo = "";
                try {
                    // Recupera os parâmetros de sistema referentes a limite de operações que liberam margem
                    tpcCodigo = CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_CAPTCHA;
                    Object paramQtdOperacoesPorUsuExigeCaptcha = ParamSist.getInstance().getParam(tpcCodigo, responsavel);
                    qtdOperacoesPorUsuParaExigirCaptcha = (TextHelper.isNum(paramQtdOperacoesPorUsuExigeCaptcha) ? Integer.valueOf(paramQtdOperacoesPorUsuExigeCaptcha.toString()) : 0);

                    tpcCodigo = CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_CAPTCHA;
                    Object paramQtdOperacoesPorCsaExigeCaptcha = ParamSist.getInstance().getParam(tpcCodigo, responsavel);
                    qtdOperacoesPorCsaParaExigirCaptcha = (TextHelper.isNum(paramQtdOperacoesPorCsaExigeCaptcha) ? Integer.valueOf(paramQtdOperacoesPorCsaExigeCaptcha.toString()) : 0);
                } catch (NumberFormatException ex) {
                    LOG.error("Valor incorreto para o parâmetro de sistema '" + tpcCodigo + "'.", ex);
                }
                if (responsavel.isCsaCor()) {
                    // Verifica se os parâmetros de consignatária foram configurados para sobrepor os parâmetros de sistema
                    String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
                    String tpaCodigo = "";
                    try {
                        tpaCodigo = CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_CAPTCHA;
                        String paramCsaQtdOperacoesPorUsuExigeCaptcha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                        if (TextHelper.isNum(paramCsaQtdOperacoesPorUsuExigeCaptcha) && Integer.valueOf(paramCsaQtdOperacoesPorUsuExigeCaptcha) > 0) {
                            qtdOperacoesPorUsuParaExigirCaptcha = Integer.valueOf(paramCsaQtdOperacoesPorUsuExigeCaptcha);
                        }

                        tpaCodigo = CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_CAPTCHA;
                        String paramCsaQtdOperacoesPorCsaExigeCaptcha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                        if (TextHelper.isNum(paramCsaQtdOperacoesPorCsaExigeCaptcha) && Integer.valueOf(paramCsaQtdOperacoesPorCsaExigeCaptcha) > 0) {
                            qtdOperacoesPorCsaParaExigirCaptcha = Integer.valueOf(paramCsaQtdOperacoesPorCsaExigeCaptcha);
                        }
                    } catch (NumberFormatException ex) {
                        LOG.error("Valor incorreto para o parâmetro de consignatária '" + tpaCodigo + "'.", ex);
                    }
                }

                if (responsavel.isCsaCor() && qtdOperacoesPorCsaParaExigirCaptcha > 0) {
                    // Caso o usuário operador do sistema seja de CSA ou COR, avalia todas as operações de todos os usuários de CSA/COR da mesma CSA do usuário operador
                    String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
                    int qtdOperacoesLiberacaoMargemPorCsa = recuperarQtdOperacoesLiberacaoMargemPorCsa(csaCodigo, responsavel);
                    if (qtdOperacoesLiberacaoMargemPorCsa >= qtdOperacoesPorCsaParaExigirCaptcha) {
                        return true;
                    }
                }

                if (qtdOperacoesPorUsuParaExigirCaptcha > 0) {
                    // Avalia as operações apenas do usuário operador do sistema
                    String usuCodigo = responsavel.getUsuCodigo();
                    int qtdOperacoesLiberacaoMargemPorUsu = recuperarQtdOperacoesLiberacaoMargemPorUsu(usuCodigo, responsavel);
                    if (qtdOperacoesLiberacaoMargemPorUsu >= qtdOperacoesPorUsuParaExigirCaptcha) {
                        return true;
                    }
                }
            }
        } catch (ParametroControllerException | SegurancaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public boolean exigirSegundaSenhaOperacoesLiberacaoMargem(AcessoSistema responsavel) {
        int qtdOperacoesPorUsuParaExigirSegundaSenha = 0;
        int qtdOperacoesPorCsaParaExigirSegundaSenha = 0;
        try {
            if (responsavel.isSup() || TextHelper.isNull(responsavel.getFunCodigo())) {
                return false;
            }
            // Verifica se a operação que está em sendo executada é pode liberar margem
            if (isFuncaoLiberaMargem(responsavel.getFunCodigo())) {
                String tpcCodigo = "";
                try {
                    // Recupera os parâmetros de sistema referentes a limite de operações que liberam margem
                    tpcCodigo = CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_OUTRA_SENHA;
                    Object paramQtdOperacoesPorUsuExigeSegundaSenha = ParamSist.getInstance().getParam(tpcCodigo, responsavel);
                    qtdOperacoesPorUsuParaExigirSegundaSenha = (TextHelper.isNum(paramQtdOperacoesPorUsuExigeSegundaSenha) ? Integer.valueOf(paramQtdOperacoesPorUsuExigeSegundaSenha.toString()) : 0);

                    tpcCodigo = CodedValues.TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_OUTRA_SENHA;
                    Object paramQtdOperacoesPorCsaExigeSegundaSenha = ParamSist.getInstance().getParam(tpcCodigo, responsavel);
                    qtdOperacoesPorCsaParaExigirSegundaSenha = (TextHelper.isNum(paramQtdOperacoesPorCsaExigeSegundaSenha) ? Integer.valueOf(paramQtdOperacoesPorCsaExigeSegundaSenha.toString()) : 0);
                } catch (NumberFormatException ex) {
                    LOG.error("Valor incorreto para o parâmetro de sistema '" + tpcCodigo + "'.", ex);
                }
                if (responsavel.isCsaCor()) {
                    // Verifica se os parâmetros de consignatária foram configurados para sobrepor os parâmetros de sistema
                    String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
                    String tpaCodigo = "";
                    try {
                        tpaCodigo = CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_OUTRA_SENHA;
                        String paramCsaQtdOperacoesPorUsuExigeSegundaSenha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                        if (TextHelper.isNum(paramCsaQtdOperacoesPorUsuExigeSegundaSenha) && Integer.valueOf(paramCsaQtdOperacoesPorUsuExigeSegundaSenha) > 0) {
                            qtdOperacoesPorUsuParaExigirSegundaSenha = Integer.valueOf(paramCsaQtdOperacoesPorUsuExigeSegundaSenha);
                        }

                        tpaCodigo = CodedValues.TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_OUTRA_SENHA;
                        String paramCsaQtdOperacoesPorCsaExigeSegundaSenha = parametroController.getParamCsa(csaCodigo, tpaCodigo, responsavel);
                        if (TextHelper.isNum(paramCsaQtdOperacoesPorCsaExigeSegundaSenha) && Integer.valueOf(paramCsaQtdOperacoesPorCsaExigeSegundaSenha) > 0) {
                            qtdOperacoesPorCsaParaExigirSegundaSenha = Integer.valueOf(paramCsaQtdOperacoesPorCsaExigeSegundaSenha);
                        }
                    } catch (NumberFormatException ex) {
                        LOG.error("Valor incorreto para o parâmetro de consignatária '" + tpaCodigo + "'.", ex);
                    }
                }

                if (responsavel.isCsaCor() && qtdOperacoesPorCsaParaExigirSegundaSenha > 0) {
                    // Caso o usuário operador do sistema seja de CSA ou COR, avalia todas as operações de todos os usuários de CSA/COR da mesma CSA do usuário operador
                    String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
                    int qtdOperacoesLiberacaoMargemPorCsa = recuperarQtdOperacoesLiberacaoMargemPorCsa(csaCodigo, responsavel);
                    if (qtdOperacoesLiberacaoMargemPorCsa >= qtdOperacoesPorCsaParaExigirSegundaSenha) {
                        return true;
                    }
                }

                if (qtdOperacoesPorUsuParaExigirSegundaSenha > 0) {
                    // Avalia as operações apenas do usuário operador do sistema
                    String usuCodigo = responsavel.getUsuCodigo();
                    int qtdOperacoesLiberacaoMargemPorUsu = recuperarQtdOperacoesLiberacaoMargemPorUsu(usuCodigo, responsavel);
                    if (qtdOperacoesLiberacaoMargemPorUsu >= qtdOperacoesPorUsuParaExigirSegundaSenha) {
                        return true;
                    }
                }
            }
        } catch (ParametroControllerException | SegurancaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    private int recuperarQtdOperacoesLiberacaoMargemPorCsa(String csaCodigo, AcessoSistema responsavel) throws SegurancaControllerException {
        int qtdOperacoesLiberacaoMargemPorCsa = 0;
        try {
            ObtemTotalOperacoesLiberacaoMargemQuery query = new ObtemTotalOperacoesLiberacaoMargemQuery();
            query.csaCodigo = csaCodigo;
            qtdOperacoesLiberacaoMargemPorCsa = query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);

        }
        return qtdOperacoesLiberacaoMargemPorCsa;
    }

    private int recuperarQtdOperacoesLiberacaoMargemPorUsu(String usuCodigo, AcessoSistema responsavel) throws SegurancaControllerException {
        int qtdOperacoesLiberacaoMargemPorUsu = 0;
        try {
            ObtemTotalOperacoesLiberacaoMargemQuery query = new ObtemTotalOperacoesLiberacaoMargemQuery();
            query.usuCodigo = usuCodigo;
            qtdOperacoesLiberacaoMargemPorUsu = query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);

        }
        return qtdOperacoesLiberacaoMargemPorUsu;
    }

    private void enviarNotificacaoSegurancaPorUsu(String usuCodigo, String csaCodigo, AcessoSistema responsavel) {
        try {
            ListarOperacoesLiberacaoMargemParaBloqueioQuery query = new ListarOperacoesLiberacaoMargemParaBloqueioQuery();
            query.usuCodigo = usuCodigo;
            query.responsavel = responsavel;
            List<TransferObject> servidoresAfetados = query.executarDTO();
            if (servidoresAfetados != null && !servidoresAfetados.isEmpty()) {
                // Envia notificação de limite de operações de liberação de margem excedido para a equipe responsável
                EnviaEmailHelper.enviarEmailNotificacaoLimiteOperacoesLiberacaoMargem(servidoresAfetados, csaCodigo, responsavel);
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void enviarNotificacaoSegurancaPorCsa(String csaCodigo, AcessoSistema responsavel) {
        try {
            ListarOperacoesLiberacaoMargemParaBloqueioQuery query = new ListarOperacoesLiberacaoMargemParaBloqueioQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            List<TransferObject> servidoresAfetados = query.executarDTO();
            if (servidoresAfetados != null && !servidoresAfetados.isEmpty()) {
                // Envia notificação de limite de operações de liberação de margem excedido para a equipe responsável
                EnviaEmailHelper.enviarEmailNotificacaoLimiteOperacoesLiberacaoMargem(servidoresAfetados, csaCodigo, responsavel);
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void bloquearUsuarioPorMotivoSeguranca(String usuCodigo, String csaCodigo, AcessoSistema responsavel) {
        // Bloquear o usuário que ultrapassou os limites estabelecidos para operações que liberam margem.
        // Bloquear também os servidores envolvidos nas operações de liberação de margem que motivaram o bloqueio do usuário.
        try {
            ListarOperacoesLiberacaoMargemParaBloqueioQuery query = new ListarOperacoesLiberacaoMargemParaBloqueioQuery();
            query.usuCodigo = usuCodigo;
            query.responsavel = responsavel;
            List<TransferObject> dadosParaBloqueio = query.executarDTO();

            if (dadosParaBloqueio != null && !dadosParaBloqueio.isEmpty()) {
                List<TransferObject> servidoresAfetados = dadosParaBloqueio;
                List<TransferObject> usuarioBloqueado = new ArrayList<>();
                List<TransferObject> servidoresBloqueados = new ArrayList<>();
                List<String> rseCodigosBloqueados = new ArrayList<>();
                for (TransferObject to : dadosParaBloqueio) {
                    String rseCodigo = (String) to.getAttribute(Columns.RSE_CODIGO);
                    String srsCodigo = (String) to.getAttribute(Columns.SRS_CODIGO);
                    String stuCodigo = (String) to.getAttribute(Columns.STU_CODIGO);
                    String olmCodigo = (String) to.getAttribute(Columns.OLM_CODIGO);

                    // Adiciona o usuário envolvido na operação para bloqueio
                    if ((usuarioBloqueado == null || usuarioBloqueado.isEmpty()) && !CodedValues.STU_CODIGOS_INATIVOS.contains(stuCodigo)) {
                        usuarioBloqueado.add(to);
                    }

                    // Bloqueia registros servidores envolvidos
                    if (CodedValues.SRS_ATIVO.equals(srsCodigo) && !rseCodigosBloqueados.contains(rseCodigo)) {
                        servidorController.bloquearRegistroServidorPorMotivoSeguranca(rseCodigo, responsavel);
                        servidoresBloqueados.add(to);
                        rseCodigosBloqueados.add(rseCodigo);
                    }

                    // Atualiza o registro da operação que liberou margem para informar que gerou bloqueio
                    OperacaoLiberaMargem olm = OperacaoLiberaMargemHome.findByPrimaryKey(olmCodigo);
                    olm.setOlmBloqueio("S");
                    OperacaoLiberaMargemHome.update(olm);

                }

                // Bloqueia o usuário envolvido nas operações
                try {
                    if (usuarioBloqueado != null && !usuarioBloqueado.isEmpty()) {
                        usuarioController.bloquearUsuarioMotivoSeguranca(usuCodigo, null, null, responsavel);
                    }
                } catch (UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Envia notificação de bloqueio por motivo de segurança para a equipe responsável
                EnviaEmailHelper.enviarEmailBloqueioAutomaticoSegurancaLiberacaoMargem(servidoresAfetados, usuarioBloqueado, servidoresBloqueados, csaCodigo, responsavel);
            }
        } catch (HQueryException | ServidorControllerException | FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void bloquearUsuariosCsaPorMotivoSeguranca(String csaCodigo, AcessoSistema responsavel) throws SegurancaControllerException {
        // Bloquear todos os usuários da consignatária e dos seus correspondentes envolvidos nas operações de liberação de margem que ultrapassaram os
        // limites estabelecidos.
        // Bloquear também os servidores envolvidos nas operações de liberação de margem que motivaram o bloqueio.
        try {
            AcessoSistema usuarioSistema = AcessoSistema.getAcessoUsuarioSistema();
            ListarOperacoesLiberacaoMargemParaBloqueioQuery query = new ListarOperacoesLiberacaoMargemParaBloqueioQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            List<TransferObject> dadosBloqueioServidores = query.executarDTO();

            if (dadosBloqueioServidores != null && !dadosBloqueioServidores.isEmpty()) {
                List<TransferObject> servidoresAfetados = dadosBloqueioServidores;
                List<TransferObject> servidoresBloqueados = new ArrayList<>();
                List<String> rseCodigosBloqueados = new ArrayList<>();
                List<TransferObject> usuariosBloqueados = new ArrayList<>();
                for (TransferObject to : dadosBloqueioServidores) {
                    String rseCodigo = (String) to.getAttribute(Columns.RSE_CODIGO);
                    String srsCodigo = (String) to.getAttribute(Columns.SRS_CODIGO);
                    String olmCodigo = (String) to.getAttribute(Columns.OLM_CODIGO);

                    // Bloqueia registros servidores envolvidos
                    if (CodedValues.SRS_ATIVO.equals(srsCodigo) && !rseCodigosBloqueados.contains(rseCodigo)) {
                        servidorController.bloquearRegistroServidorPorMotivoSeguranca(rseCodigo, usuarioSistema);
                        servidoresBloqueados.add(to);
                        rseCodigosBloqueados.add(rseCodigo);
                    }

                    // Atualiza o registro da operação que liberou margem para informar que gerou bloqueio
                    OperacaoLiberaMargem olm = OperacaoLiberaMargemHome.findByPrimaryKey(olmCodigo);
                    olm.setOlmBloqueio("S");
                    OperacaoLiberaMargemHome.update(olm);
                }
                // Bloqueia todos os usuários da CSA e todos os usuários dos Correspondentes da CSA.
                try {
                    ListarUsuariosLiberacaoMargemParaBloqueioQuery queryBloqueio = new ListarUsuariosLiberacaoMargemParaBloqueioQuery();
                    queryBloqueio.csaCodigo = csaCodigo;
                    List<TransferObject> dadosBloqueioUsuarios = queryBloqueio.executarDTO();
                    if (dadosBloqueioUsuarios != null && !dadosBloqueioUsuarios.isEmpty()) {
                        for (TransferObject to : dadosBloqueioUsuarios) {
                            String usuCodigo = (String) to.getAttribute(Columns.USU_CODIGO);
                            usuarioController.bloquearUsuarioMotivoSeguranca(usuCodigo, null, null, usuarioSistema);
                            usuariosBloqueados.add(to);
                        }
                    }
                } catch (UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Envia notificação de bloqueio por motivo de segurança para a equipe responsável
                EnviaEmailHelper.enviarEmailBloqueioAutomaticoSegurancaLiberacaoMargem(servidoresAfetados, usuariosBloqueados, servidoresBloqueados, csaCodigo, usuarioSistema);

            }
        } catch (HQueryException | ServidorControllerException | FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private boolean isFuncaoLiberaMargem(String funCodigo) {
        try {
            if (!TextHelper.isNull(funCodigo)) {
                Funcao funcao = FuncaoHome.findByPrimaryKey(funCodigo);
                if (!TextHelper.isNull(funcao.getFunLiberaMargem()) && funcao.getFunLiberaMargem().equals("S")) {
                    return true;
                }
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public void confirmarOperacoesLiberacaoMargem(AcessoSistema responsavel) throws SegurancaControllerException {
        try {
            // Confirma as operações pedentes de confirmação que não possuem referências com múltiplas autorizações de desconto
            OperacaoLiberaMargemHome.confirmarOperacaoLiberaMargemSemMultiplasAdes();
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        try {
            // Recupera as operações pedentes de confirmação que possuem referências com múltiplas autorizações de desconto
            ListarOperacoesLiberacaoMargemParaConfirmacaoQuery query = new ListarOperacoesLiberacaoMargemParaConfirmacaoQuery();
            List<TransferObject> operacoesComMultiplasAdes = query.executarDTO();
            if (operacoesComMultiplasAdes != null && !operacoesComMultiplasAdes.isEmpty()) {
                // Os registros provisórios de liberação de margem serão removidos e um novo registro será criado caso seja confirmada a liberação de margem pela operação
                for (TransferObject to : operacoesComMultiplasAdes) {
                    try {
                        String adeCodigoDestino = (String) to.getAttribute(Columns.OLM_ADE_CODIGO);
                        BigDecimal totalVlrDestino = (BigDecimal) to.getAttribute(Columns.ADE_VLR);
                        BigDecimal totalVlrOrigem = (BigDecimal) to.getAttribute("TOTAL_VLR_ORIGEM");
                        String rseCodigo = (String) to.getAttribute(Columns.OLM_RSE_CODIGO);
                        String usuCodigo = (String) to.getAttribute(Columns.OLM_USU_CODIGO);
                        String csaCodigo = (String) to.getAttribute(Columns.OLM_CSA_CODIGO);
                        String olmIpAcesso = (String) to.getAttribute(Columns.OLM_IP_ACESSO);
                        Date olmData = new Date();

                        // Recupera as ADEs de origem para remover os registros provisórios de liberação de margem
                        List<String> tntCodigos = new ArrayList<>();
                        tntCodigos.add(CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                        tntCodigos.add(CodedValues.TNT_CONTROLE_COMPRA);
                        List<RelacionamentoAutorizacao> lstRad = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, tntCodigos);
                        for (RelacionamentoAutorizacao rad: lstRad) {
                            // Remove operações registradas para confirmação com múltiplas ADEs
                            OperacaoLiberaMargemHome.removeByAdeCodigo(rad.getAdeCodigoOrigem());
                        }

                        // Se houve liberação de margem, confirma a operação criando registro único para confirmar a operação que envolveu múltiplos contratos
                        if (totalVlrDestino.compareTo(totalVlrOrigem) < 0) {
                            OperacaoLiberaMargemHome.create(rseCodigo, usuCodigo, csaCodigo, olmIpAcesso, olmData, "N", "S", adeCodigoDestino);
                        }
                    } catch (UpdateException | FindException | CreateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new SegurancaControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Após confirmação das operações, faz a avaliação necessária para verificar se deve realizar bloqueios de usuários e servidores
        avaliarOperacoesLiberacaoMargem(responsavel);
    }
}
