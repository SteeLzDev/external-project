<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.delegate.LogDelegate"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.log.Log" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.CodedNames" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.websocket.client.ValidarDigitalClient"%>
<%@ page import="com.zetra.econsig.values.TipoDispositivoEnum"%>
<%@ include file="../geral/env_navegacao.jsp" %>
<jsp:useBean id="serDelegate" scope="session" class="com.zetra.econsig.delegate.ServidorDelegate" />
<jsp:useBean id="usuDelegate" scope="session" class="com.zetra.econsig.delegate.UsuarioDelegate" />
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
    String tokenLeitor = JspHelper.verificaVarQryStr(request, "TOKEN_LEITOR");
    String rseMatriculaInst = null;

    if (!TextHelper.isNull(rseCodigo) && !TextHelper.isNull(rseCodigo)) {
        // Busca o servidor
        CustomTransferObject servidor = null;
        try {
            servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
        } catch (com.zetra.econsig.exception.ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
            return;
        }
        rseMatriculaInst = (String) servidor.getAttribute(Columns.RSE_MATRICULA_INST);
    } else {
        String tipoEntidade = responsavel.getTipoEntidade();
        String codigo = responsavel.getCodigoEntidade();

        String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
        String estIdentificador = JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR");
        String orgIdentificador = JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR");
        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serCPF = JspHelper.verificaVarQryStr(request, "SER_CPF");
        String[] srsCodigo = (String[]) request.getParameterValues("SRS_CODIGO");
        List rseSrsCodigo = new ArrayList();
        if (srsCodigo != null) {
            for (int i = 0; i < srsCodigo.length; i++) {
                String[] aux = srsCodigo[i].split(";");
                rseSrsCodigo.add(aux[0]);
            }
        }
        boolean validaPermissionario = false;
        List servidores = serDelegate.pesquisaServidor(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, false, rseSrsCodigo, validaPermissionario, orgCodigo);

        if (servidores.size() == 1) {
            // Busca o servidor
            CustomTransferObject servidor = (CustomTransferObject) servidores.get(0);
            rseMatriculaInst = (String) servidor.getAttribute(Columns.RSE_MATRICULA_INST);
            rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
        }
    }

    if (!TextHelper.isNull(rseCodigo) && !TextHelper.isNull(rseMatriculaInst) && !TextHelper.isNull(tokenLeitor)) {
        ValidarDigitalClient vdc = new ValidarDigitalClient(rseMatriculaInst, tokenLeitor);
        if (vdc.validarDigital()) {
            usuDelegate.cadastroDeviceToken(responsavel.getUsuCodigo(), TipoDispositivoEnum.LEITOR_DIGITAIS.getCodigo(), tokenLeitor, responsavel);
            session.setAttribute(CodedNames.ATTR_SESSION_TOKEN_LEITOR, tokenLeitor);
            session.setAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA, rseCodigo);
            // Retorna JSON de sucesso
            out.print("{\"success\":\"0\"}");
        } else {
            try {
                LogDelegate logDelegate = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.VRF_LOGIN, Log.LOG_DIGITAL_INVALIDA);
                logDelegate.setRegistroServidor(rseCodigo);
                logDelegate.write();
            } catch (com.zetra.econsig.exception.LogControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            }

            Integer qtdeTentativas = (Integer) session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_TENTATIVAS);
            if (qtdeTentativas == null) {
                qtdeTentativas = 1;
            } else {
                qtdeTentativas++;
            }
            session.setAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_TENTATIVAS, qtdeTentativas);
            Object paramQtdeMaxTentativas = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_TENTATIVAS_VALIDACAO_DIGITAL, responsavel);
            Integer qtdeMaxTentativas = 2;
            if (!TextHelper.isNull(paramQtdeMaxTentativas)) {
                qtdeMaxTentativas = Integer.valueOf(paramQtdeMaxTentativas.toString());
            }
            if (qtdeTentativas >= qtdeMaxTentativas) {
                out.print("{\"success\":\"2\"}");
            } else {
                out.print("{\"success\":\"1\"}");
            }
        }
    } else {
        out.print("{\"success\":\"-1\"}");
    }
%>