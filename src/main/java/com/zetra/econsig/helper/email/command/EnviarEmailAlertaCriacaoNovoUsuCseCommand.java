package com.zetra.econsig.helper.email.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailAlertaCriacaoNovoUsuCseCommand</p>
 * <p>Description: Command para envio de email de notificação após a criação de novos usuários
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: 2020-07-21 14:28:22 -0300 (Ter, 21 jul 2020) $
 */
public class EnviarEmailAlertaCriacaoNovoUsuCseCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlertaCriacaoNovoUsuCseCommand.class);

    
    private String usuCodigo;
    private String convenio;
    private String nome;
    private String email;
    private String telefone;
    private String usuLogin;
    private String emailUsuario;
    private String dataCriacao;
    private String horaCriacao;


    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(
					ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_CRIACAO_NOVO_USU_CSE, responsavel);
			
			String destinatario = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALERTA_CRIACAO_NOVO_USUARIO_CSE_ORG, responsavel);
	        
			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.criacao.usuario.cse.org", responsavel);
            }
			
			if (TextHelper.isNull(destinatario)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }
			
			UsuarioDelegate usuDelegate = new UsuarioDelegate();
			UsuarioTransferObject usuario = usuDelegate.findUsuario(usuCodigo, responsavel);

			usuLogin = usuario.getUsuLogin();
			nome = usuario.getUsuNome();
			telefone = usuario.getUsuTel();
			emailUsuario = usuario.getUsuEmail();
			dataCriacao = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
			horaCriacao = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			
			CustomTransferObject dados = new CustomTransferObject();
			dados.setAttribute("convenio", convenio);
			dados.setAttribute(Columns.USU_NOME, nome);
			dados.setAttribute(Columns.USU_LOGIN, usuLogin);
			dados.setAttribute(Columns.USU_TEL, telefone);
			dados.setAttribute(Columns.USU_EMAIL, emailUsuario);
			dados.setAttribute("date", dataCriacao);
			dados.setAttribute("hora", horaCriacao);
			
			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_ALERTA_CRIACAO_NOVO_USU_CSE_ORG, destinatario, null, null, titulo,
			        corpo, null, null, responsavel);

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("rotulo.email.criacao.novo.usuario.cse.erro", responsavel, ex);
		}

    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }
    
}
