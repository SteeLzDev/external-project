package com.zetra.econsig.helper.email.command;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.service.notificacao.NotificacaoEmailController;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

public class EnviarEmailCsaNotificacaoRegraCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailCsaNotificacaoRegraCommand.class);

    private String csaNome;
    private String csaNotificacaoRegra;
    private List<RegrasConvenioParametrosBean> dadosAlterados;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            // 2. Preenche dos dados disponíveis para uso no template
            final CustomTransferObject dados = new CustomTransferObject();
            setDadosTemplateEmail(dados);
            dados.setAttribute("csa_nome", csaNome);
            
            StringBuilder listaRegrasAlteradas = new StringBuilder();  
            
            for(RegrasConvenioParametrosBean regra : dadosAlterados) {
                StringBuilder dadosRegra = new StringBuilder();  
                appendString(dadosRegra, regra.getChave());                
				appendString(dadosRegra, regra.getValor());              
                listaRegrasAlteradas.append(dadosRegra).append("<br/>");
            }
            dados.setAttribute("dados_regras_noescape", listaRegrasAlteradas.toString());
            
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            final NotificacaoEmailController notificacaoEmailController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoEmailController.class);
            String[] csaEmails = csaNotificacaoRegra.split(",");
            for (String csaEmail : csaEmails) {
                mailHelper.send(TipoNotificacaoEnum.EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, csaEmail.trim(), null, null, titulo, corpo, null, null, responsavel);
                notificacaoEmailController.criarNotificacao(TipoNotificacaoEnum.EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO.getCodigo(), csaEmail.trim(), titulo, corpo, DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }
    
    private void appendString(StringBuilder dadosRegra, String dado) {
        if (dadosRegra.length() > 0) {
        	dadosRegra.append(" - ");
        }
        dadosRegra.append(dado);
    }

	public String getCsaNome() {
		return csaNome;
	}

	public void setCsaNome(String csaNome) {
		this.csaNome = csaNome;
	}

	public String getCsaNotificacaoRegra() {
		return csaNotificacaoRegra;
	}

	public void setCsaNotificacaoRegra(String csaNotificacaoRegra) {
		this.csaNotificacaoRegra = csaNotificacaoRegra;
	}

	public List<RegrasConvenioParametrosBean> getDadosAlterados() {
		return dadosAlterados;
	}

	public void setDadosAlterados(List<RegrasConvenioParametrosBean> dadosAlterados) {
		this.dadosAlterados = dadosAlterados;
	}       
}
