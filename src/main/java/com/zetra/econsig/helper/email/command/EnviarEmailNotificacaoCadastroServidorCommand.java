package com.zetra.econsig.helper.email.command;

import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCadastroServidorCommand</p>
 * <p>Description: Command para envio de email de notificação para CSE/ORG no cadastro de servidor realizado por CSA/COR.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailNotificacaoCadastroServidorCommand extends AbstractEnviarEmailCommand {

    private String rseCodigo;

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public void execute() throws ViewHelperException {
        try {
            ServidorDelegate serDelegate = new ServidorDelegate();
            CustomTransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);

            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ConsignanteTransferObject cseTO = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            String cseMail = cseTO.getCseEmailValidarServidor();

            OrgaoTransferObject orgTO = getOrgao((String) servidor.getAttribute(Columns.ORG_CODIGO), responsavel);
            String orgEmail = (String) orgTO.getAttribute(Columns.ORG_EMAIL_VALIDAR_SERVIDOR);

            if (TextHelper.isNull(cseMail) && TextHelper.isNull(orgEmail)) {
                throw new ViewHelperException("mensagem.email.validar.servidor.cse.nao.cadastrado", responsavel);
            }

            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CADASTRO_SERVIDOR, responsavel);

            // 2. Preenche dos dados disponíveis para uso no template
            CustomTransferObject dados = new CustomTransferObject();
            adicionarDadosNotificacaoCadServidorParaEmail(dados, servidor, orgTO, responsavel);
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_CADASTRO_SERVIDOR,
                            !TextHelper.isNull(orgEmail) ? orgEmail.replaceAll(";", ",") : cseMail.replaceAll(";", ","),
                            null, null, titulo, corpo, null, null, responsavel);
       } catch (ViewHelperException ex) {
            throw new ViewHelperException(ex);
       } catch (Exception ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
       }
    }

    /**
    *
    * @param dados
    * @param servidor
    * @param orgTO
    * @param responsavel
    * @throws ZetraException
    */
    private void adicionarDadosNotificacaoCadServidorParaEmail(CustomTransferObject dados, CustomTransferObject servidor,
                                                               OrgaoTransferObject orgTO, AcessoSistema responsavel) throws ZetraException {
        // Servidor
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME, responsavel)) {
            addValue(servidor, dados, Columns.SER_NOME, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CPF, responsavel)) {
            addValue(servidor, dados, Columns.SER_CPF, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_PAI, responsavel)) {
            addValue(servidor, dados, Columns.SER_NOME_PAI, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_MAE, responsavel)) {
            addValue(servidor, dados, Columns.SER_NOME_MAE, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_NASCIMENTO, responsavel)) {
            addValue(servidor, dados, Columns.SER_DATA_NASC, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SEXO, responsavel)) {
            Map<Object, String> mapping = new HashMap<Object, String>();
            mapping.put("M", ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel));
            mapping.put("F", ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel));
            addValue(servidor, dados, Columns.SER_SEXO, mapping);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTADO_CIVIL, responsavel)) {
            Map<Object, String> mapping = new HashMap<Object, String>();
            ServidorDelegate serDelegate = new ServidorDelegate();
            String serEstCivil = (String) servidor.getAttribute(Columns.SER_EST_CIVIL);
            if (!TextHelper.isNull(serEstCivil)) {
                mapping.put(serEstCivil, serDelegate.getEstCivil(serEstCivil, responsavel));
            }
            addValue(servidor, dados, Columns.SER_EST_CIVIL, mapping);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NACIONALIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_NACIONALIDADE, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_IDENTIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_NRO_IDT, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMISSOR_IDENTIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_EMISSOR_IDT, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_IDENTIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_UF_IDT, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_IDENTIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_DATA_IDT, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_CARTEIRA_TRABALHO, responsavel)) {
            addValue(servidor, dados, Columns.SER_CART_PROF, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NUM_PIS, responsavel)) {
            addValue(servidor, dados, Columns.SER_PIS, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_LOGRADOURO, responsavel)) {
            addValue(servidor, dados, Columns.SER_END, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NRO, responsavel)) {
            addValue(servidor, dados, Columns.SER_NRO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_COMPLEMENTO, responsavel)) {
            addValue(servidor, dados, Columns.SER_COMPL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BAIRRO, responsavel)) {
            addValue(servidor, dados, Columns.SER_BAIRRO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE, responsavel)) {
            addValue(servidor, dados, Columns.SER_CIDADE, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF, responsavel)) {
            addValue(servidor, dados, Columns.SER_UF, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CEP, responsavel)) {
            addValue(servidor, dados, Columns.SER_CEP, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TELEFONE, responsavel)) {
            addValue(servidor, dados, Columns.SER_TEL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CELULAR, responsavel)) {
            addValue(servidor, dados, Columns.SER_CELULAR, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_EMAIL, responsavel)) {
            addValue(servidor, dados, Columns.SER_EMAIL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CIDADE_NASC, responsavel)) {
            addValue(servidor, dados, Columns.SER_CID_NASC, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_UF_NASC, responsavel)) {
            addValue(servidor, dados, Columns.SER_UF_NASC, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_NOME_CONJUGE, responsavel)) {
            addValue(servidor, dados, Columns.SER_NOME_CONJUGE, null);
        }
        // Registro Servidor
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_MATRICULA, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ORGAO, responsavel)) {
            String chave = Columns.getColumnName(Columns.ORG_NOME);
            String valor = orgTO.getOrgNome();
            dados.setAttribute(chave, valor);
            valor = "<div class=\"item\"><span class=\"rotulo\"><b>" + Columns.getColumnLabel(Columns.ORG_NOME) + "<span class=\"colon\"></span>:</b></span> <span class=\"valor\">" + valor + "</span></div>\n";
            chave += "_label_html_noescape";
            dados.setAttribute(chave, valor);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MUNICIPIO_LOTACAO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_MUNICIPIO_LOTACAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_COMPULSORIOS, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DESCONTOS_COMP, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DESCONTOS_FACULTATIVOS, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DESCONTOS_FACU, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OUTROS_DESCONTOS, responsavel)) {
            addValue(servidor, dados, Columns.RSE_OUTROS_DESCONTOS, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ASSOCIADO, responsavel)) {
            Map<Object, String> mapping = new HashMap<Object, String>();
            mapping.put("S", ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel));
            mapping.put("N", ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel));
            addValue(servidor, dados, Columns.RSE_ASSOCIADO, mapping);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BASE_CALCULO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_BASE_CALCULO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_MATRICULA_INSTITUCIONAL, responsavel)) {
            addValue(servidor, dados, Columns.RSE_MATRICULA_INST, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_CONTRACHEQUE, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DATA_CTC, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SITUACAO, responsavel)) {
            addValue(servidor, dados, Columns.SRS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CATEGORIA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_TIPO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CARGO, responsavel)) {
            addValue(servidor, dados, Columns.CRS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_TIPO_REG_SERVIDOR, responsavel)) {
            addValue(servidor, dados, Columns.TRS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_POSTO, responsavel)) {
            addValue(servidor, dados, Columns.POS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_ESTABILIZADO, responsavel)) {
            Map<Object, String> mapping = new HashMap<Object, String>();
            mapping.put("S", ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel));
            mapping.put("N", ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel));
            addValue(servidor, dados, Columns.RSE_ESTABILIZADO, mapping);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_FIM_ENGAJAMENTO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DATA_FIM_ENGAJAMENTO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_LIMITE_PERMANENCIA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DATA_LIMITE_PERMANENCIA, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CAPACIDADE_CIVIL, responsavel)) {
            addValue(servidor, dados, Columns.CAP_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_VINCULO, responsavel)) {
            addValue(servidor, dados, Columns.VRS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PADRAO, responsavel)) {
            addValue(servidor, dados, Columns.PRS_DESCRICAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CLT, responsavel)) {
            Map<Object, String> mapping = new HashMap<Object, String>();
            mapping.put("S", ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel));
            mapping.put("N", ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel));
            addValue(servidor, dados, Columns.RSE_CLT, mapping);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_DATA_ADMISSAO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_DATA_ADMISSAO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRAZO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_PRAZO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_BANCO_SAL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_AGENCIA_SAL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_CONTA_SAL, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_BANCO_ALTERNATIVO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_BANCO_SAL_2, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_AGENCIA_ALTERNATIVA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_AGENCIA_SAL_2, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_CONTA_ALTERNATIVA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_CONTA_SAL_2, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_OBSERVACAO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_OBS, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PRACA, responsavel)) {
            addValue(servidor, dados, Columns.RSE_PRACA, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_SALARIO, responsavel)) {
            addValue(servidor, dados, Columns.RSE_SALARIO, null);
        }
        if (ShowFieldHelper.canEdit(FieldKeysConstants.INS_SERVIDOR_PARA_RESERVA_PROVENTOS, responsavel)) {
            addValue(servidor, dados, Columns.RSE_PROVENTOS, null);
        }
    }
}
