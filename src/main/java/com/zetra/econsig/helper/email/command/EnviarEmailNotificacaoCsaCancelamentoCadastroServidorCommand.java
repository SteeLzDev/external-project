package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCsaCancelamentoCadastroServidorCommand</p>
 * <p>Description: Command para envio de email de notificação do cancelamento de cadastro de servidor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailNotificacaoCsaCancelamentoCadastroServidorCommand extends AbstractEnviarEmailCommand {

    private TransferObject ade;

    public TransferObject getAde() {
        return ade;
    }

    public void setAde(TransferObject ade) {
        this.ade = ade;
    }

    @Override
    public void execute() throws ViewHelperException {
        try {
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            String csaEmail = csaDelegate.findConsignataria((String) ade.getAttribute(Columns.CSA_CODIGO), responsavel).getCsaEmail();

            if (TextHelper.isNull(csaEmail)) {
                throw new ViewHelperException("mensagem.email.csa.nao.cadastrado", responsavel);
            }

            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_NOTIFICACAO_CSA_CANCELAMENTO_CADASTRO_SERVIDOR, responsavel);

            // 2. Preenche dos dados disponíveis para uso no template
            CustomTransferObject dados = new CustomTransferObject();
            AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            /*
             * Comentário copiado do método gerarTextoDetalheContratoParaEmail():
             * Como não vinham dados suficientes em nenhuma das situações que este método é chamado, foi escolhido realizar uma
             * busca pelos dados do contrato.
             */
            TransferObject infoContrato = adeDelegate.buscaAutorizacao((String) ade.getAttribute(Columns.ADE_CODIGO), responsavel);
            for (Object chave : infoContrato.getAtributos().keySet()) {
                addValue(infoContrato, dados, chave.toString(), null);
            }
            // Consignatária
            String csaIdentificador = infoContrato.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? infoContrato.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "";
            String csaNome = infoContrato.getAttribute(Columns.CSA_NOME_ABREV) != null ? infoContrato.getAttribute(Columns.CSA_NOME_ABREV).toString() : infoContrato.getAttribute(Columns.CSA_NOME).toString();
            dados.setAttribute("consignataria", csaIdentificador + " - " + csaNome);
            dados.setAttribute("correspondente_label_html_noescape",
                               "<div class=\"item\"><span class=\"rotulo\"><b>${rotulo.consignataria.singular}<span class=\"colon\"></span>:</b></span> <span class=\"valor\">" +
                               csaIdentificador + " - " + csaNome + "</span></div>\n");
            // Serviço
            String servico = (infoContrato.getAttribute(Columns.CNV_COD_VERBA) != null && !infoContrato.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ?
                             infoContrato.getAttribute(Columns.CNV_COD_VERBA).toString() :
                             infoContrato.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
            servico += (infoContrato.getAttribute(Columns.ADE_INDICE) != null && !infoContrato.getAttribute(Columns.ADE_INDICE).toString().equals("")) ?
                       infoContrato.getAttribute(Columns.ADE_INDICE).toString() :
                       "";
            servico += " - " + infoContrato.getAttribute(Columns.SVC_DESCRICAO).toString();
            dados.setAttribute("servico", servico);
            servico = "<div class=\"item\"><span class=\"rotulo\"><b>${rotulo.servico.singular}<span class=\"colon\"></span>:</b></span> <span class=\"valor\">"
                    + servico
                    + "</span></div>\n";
            dados.setAttribute("servico_label_html_noescape", servico);
            // Correspondente
            String corIdentificador = infoContrato.getAttribute(Columns.COR_IDENTIFICADOR) != null ? infoContrato.getAttribute(Columns.COR_IDENTIFICADOR).toString() : "";
            String corNome = infoContrato.getAttribute(Columns.COR_NOME) != null ? infoContrato.getAttribute(Columns.COR_NOME).toString() : "";
            if (!TextHelper.isNull(corIdentificador) && !TextHelper.isNull(corNome)) {
                dados.setAttribute("correspondente", corIdentificador + " - " + corNome);
                dados.setAttribute("correspondente_label_html_noescape",
                                   "<div class=\"item\"><span class=\"rotulo\"><b>${rotulo.correspondente.singular}<span class=\"colon\"></span>:</b></span> <span class=\"valor\">" +
                                   corIdentificador + " - " + corNome + "</span></div>\n");
            }
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_CANCELAMENTO_CADASTRO_SERVIDOR, csaEmail.replaceAll(";", ","), null, null, titulo, corpo, null, null, responsavel);

        } catch (Exception ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
