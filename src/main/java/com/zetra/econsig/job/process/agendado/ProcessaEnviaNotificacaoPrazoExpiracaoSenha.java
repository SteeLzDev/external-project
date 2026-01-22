package com.zetra.econsig.job.process.agendado;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.sso.SSOClient;
import com.zetra.econsig.webclient.sso.response.UserDetailResponse;

public class ProcessaEnviaNotificacaoPrazoExpiracaoSenha extends ProcessoAgendadoPeriodico {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProcessaEnviaNotificacaoPrazoExpiracaoSenha.class);

    private final UsuarioController usuarioController;

    public ProcessaEnviaNotificacaoPrazoExpiracaoSenha(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        usuarioController = getUsuarioController();
    }

    @Override
    public void executa() throws ZetraException {

        final AcessoSistema responsavel = getResponsavel();
        
        boolean permissaoNotificacao = verificarSeRotinaHabilitada(responsavel);

        if (!permissaoNotificacao) {
            LOG.info("Notificação de expiração de senha desabilitada por configuração de parâmetros.");
            return;
        }
        
        List<TransferObject> usuAtivos = usuarioController.listUsuariosAtivosComEmail(responsavel);

        String idenfificadoInternoEconsig = buscarIdentificadorInternoEconsig(responsavel);

        SSOToken token = autenticarSSO(responsavel);

        for(TransferObject usuAtivo : usuAtivos) {
            try {
                executarParaUsuario(usuAtivo, idenfificadoInternoEconsig, token);
            } catch (Exception e) {
                LOG.error("Erro ao enviar email para usuario com senha expirada: usu_codigo: {}", 
                    usuAtivo.getAttribute(Columns.USU_CODIGO), e);
            }
        }

    }

    private void executarParaUsuario(TransferObject usuAtivo, String idenfificadoInternoEconsig, SSOToken token) {

        if (verificarSeUsuarioSSO(usuAtivo)) {
            executarParaUsuarioSSO(usuAtivo, idenfificadoInternoEconsig, token);
        } else {
            executarParaUsuarioEconsig(usuAtivo);
        }

    }

    private boolean verificarSeUsuarioSSO(TransferObject usuAtivo) {
        return usuAtivo.getAttribute(Columns.USU_AUTENTICA_SSO) != null && "S".equals(usuAtivo.getAttribute(Columns.USU_AUTENTICA_SSO));
    }

    private void executarParaUsuarioEconsig(TransferObject usuAtivo) {
        
        final AcessoSistema responsavel = getResponsavel();

        int tpcDiasExpiracaoSenha = buscarTpcDiasExpiracaoSenhaPorTipoUsuario(usuAtivo, responsavel);
        String data = !TextHelper.isNull(usuAtivo.getAttribute(Columns.USU_DATA_EXP_SENHA)) ? usuAtivo.getAttribute(Columns.USU_DATA_EXP_SENHA).toString() : null;
        int qtdeDiasExpiracaoSenha = calcularQtdeDiasExpiracaoSenha(data);

        if(tpcDiasExpiracaoSenha > 0) {
        	if (qtdeDiasExpiracaoSenha >= 0 && qtdeDiasExpiracaoSenha <= tpcDiasExpiracaoSenha) {
        		enviarNotificacaoPrazoExpiracaoSenha(usuAtivo, qtdeDiasExpiracaoSenha, responsavel);
            }
        }        
    }

    private void executarParaUsuarioSSO (TransferObject usuAtivo, String identificadoInternoEconsig, SSOToken token) {

        if (token == null) {
            throw new RuntimeException("Token de autenticação SSO nulo");
        }

        final AcessoSistema responsavel = getResponsavel();

        int tpcDiasExpiracaoSenha = buscarTpcDiasExpiracaoSenhaPorTipoUsuario(usuAtivo, responsavel);

        UserDetailResponse userDetail = buscarDadosUsuarioSSO((String)usuAtivo.getAttribute(Columns.USU_EMAIL), token);
        String dataFormatada = userDetail.getPasswordExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int qtdeDiasExpiracaoSenha = calcularQtdeDiasExpiracaoSenha(dataFormatada);

        if(tpcDiasExpiracaoSenha > 0) {
	        if (qtdeDiasExpiracaoSenha >= 0 && qtdeDiasExpiracaoSenha <= tpcDiasExpiracaoSenha) {
	
	            String identificadoInternoSSO = userDetail.getEmailIdentInternEconsig();
	
	            if (TextHelper.isNull(identificadoInternoSSO)) {
	                identificadoInternoSSO = identificadoInternoEconsig; 
	                marcarEsteEconsigParaEnviarEmail (userDetail, identificadoInternoEconsig, token);
	            }
	
	            if (identificadoInternoSSO.equals(identificadoInternoEconsig)) {
	            	enviarNotificacaoPrazoExpiracaoSenha(usuAtivo, qtdeDiasExpiracaoSenha, responsavel);
	            }
	        }
        }

    }

    protected SSOToken autenticarSSO(AcessoSistema responsavel) {
        try {

            final String loginAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_LOGIN, responsavel);
            final String senhaAdm = (String) ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_USER_ADMIN_SENHA, responsavel);
            final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_SSO, responsavel);

            if (TextHelper.isNull(urlBase)){
                return null;
            }

            if (TextHelper.isNull(loginAdm) || TextHelper.isNull(senhaAdm)) {
                LOG.warn("URL para SSO configurado, mas sem usuário e senha admin");
                return null;
            }

            final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
            return ssoClient.autenticar(loginAdm, senhaAdm);

        } catch (SSOException e) {
            LOG.error("Erro ao tentar autenticar no SSO: ", e);
            return null;
        }
    }

    private int calcularQtdeDiasExpiracaoSenha(String data) {
        return !TextHelper.isNull(data) ? DateHelper.dateDiff(DateHelper.format(DateHelper.getSystemDate(), "yyyy-MM-dd"), 
            data, "yyyy-MM-dd", null, "DIAS") : -1;
    }

    private void enviarNotificacaoPrazoExpiracaoSenha(TransferObject usuAtivo,
            int qtdeDiasExpiracaoSenha, final AcessoSistema responsavel) {

        String usuNome = usuAtivo.getAttribute(Columns.USU_NOME).toString();
        String usuEmail = usuAtivo.getAttribute(Columns.USU_EMAIL).toString();
        LOG.debug("Envia notificação de prazo de expiração de senha");
        usuarioController.enviarNotificacaoPrazoExpiracaoSenha(usuNome, usuEmail, qtdeDiasExpiracaoSenha, responsavel);

    }
    
    protected int buscarTpcDiasExpiracaoSenhaPorTipoUsuario(TransferObject usuAtivo, AcessoSistema responsavel) {
        int tpcDiasExpiracaoSenha = 0;
        if(!TextHelper.isNull(usuAtivo.getAttribute(Columns.PER_PAP_CODIGO))) {
            if (usuAtivo.getAttribute(Columns.PER_PAP_CODIGO).equals(CodedValues.PAP_CONSIGNANTE) || usuAtivo.getAttribute(Columns.PER_PAP_CODIGO).equals(CodedValues.PAP_ORGAO)) {
                tpcDiasExpiracaoSenha = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSE_ORG, 0, responsavel);
            } else if (usuAtivo.getAttribute(Columns.PER_PAP_CODIGO).equals(CodedValues.PAP_CONSIGNATARIA) || usuAtivo.getAttribute(Columns.PER_PAP_CODIGO).equals(CodedValues.PAP_CORRESPONDENTE)) {
                tpcDiasExpiracaoSenha = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSA_COR, 0, responsavel);
            } else if (usuAtivo.getAttribute(Columns.PER_PAP_CODIGO).equals(CodedValues.PAP_SERVIDOR)) {
                tpcDiasExpiracaoSenha = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_SER, 0, responsavel);
            }
        }
        
        return tpcDiasExpiracaoSenha;
    }

    protected UserDetailResponse buscarDadosUsuarioSSO(String usuEmail, SSOToken token) {
        try {
            final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
            return ssoClient.getUserDetailUsingAdmin(usuEmail, token);
        } catch (SSOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String buscarIdentificadorInternoEconsig(AcessoSistema responsavel) throws FindException {
        Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
        return cse.getCseIdentificadorInterno();
    }

    protected void marcarEsteEconsigParaEnviarEmail(UserDetailResponse userDetail, String identificadoInternoEconsig, SSOToken token) {
        try {
            final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
            ssoClient.updateUserDetailUsingAdmin(userDetail.getUsername(), identificadoInternoEconsig, token);
        } catch (SSOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean verificarSeRotinaHabilitada(final AcessoSistema responsavel) {
        return ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSE_ORG, 0, responsavel) > 0 ||
        ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSA_COR, 0, responsavel) > 0 ||
        ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_SER, 0, responsavel) > 0;
    }

    protected UsuarioController getUsuarioController() {
        return ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
    }
}