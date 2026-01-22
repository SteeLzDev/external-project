package com.zetra.econsig.service.ambiente;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.validacaoambiente.RegraValidacaoAmbienteInterface;
import com.zetra.econsig.persistence.query.admin.ListaRegraAmbienteQuery;
import com.zetra.econsig.persistence.query.admin.ObtemValidacaoAmbienteQuery;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.RegraValidacaoEnum;

/**
 * <p>Title: ValidacaoAmbienteControllerBean</p>
 * <p>Description: Fachada dos métodos de negócio de Validação de Ambiente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ValidacaoAmbienteControllerBean  implements ValidacaoAmbienteController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacaoAmbienteControllerBean.class);

    @Autowired
    private SistemaController sistemaController;

    /**
     * Método que verifica se as regras existentes estão válidas no sistema.
     * @param responsavel Responsável pela operação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da classe.
     */
    @Override
    public void verificarRegraValidacaoAmbiente(AcessoSistema responsavel) throws ValidacaoAmbienteControllerException {
        try {
            boolean bloquear = false;
            List<String> regrasInvalidas = new ArrayList<>();

            // Recupera regras ativas de RegraValidacaoAmbiente
            List<TransferObject> regras = lstRegraAmbiente(CodedValues.STS_ATIVO, responsavel);
            Iterator<TransferObject> it = regras.iterator();
            while (it.hasNext()) {
                TransferObject to = it.next();
                String reaDescricao = to.getAttribute(Columns.REA_DESCRICAO).toString();
                String reaJavaClassName = to.getAttribute(Columns.REA_JAVA_CLASS_NAME).toString();
                String reaBloqueiaSistema = to.getAttribute(Columns.REA_BLOQUEIA_SISTEMA).toString();

                // Carrega a classe que executa essa regra
                RegraValidacaoAmbienteInterface classe = ((Class<RegraValidacaoAmbienteInterface>) Class.forName(reaJavaClassName)).getDeclaredConstructor().newInstance();

                // Chama o metodo executar da classe especifica que retorna um Map do resultado da validacao (key) e do valor existente no sistema (value)
                Map<Boolean, String> resultado = classe.executar();
                if (!resultado.isEmpty() && resultado.containsKey(Boolean.FALSE)) {
                    String msg = ApplicationResourcesHelper.getMessage("rotulo.validacao.ambiente.valor.encontrado", responsavel,
                            reaDescricao, resultado.get(Boolean.FALSE).toString());
                    LOG.error(msg);
                    regrasInvalidas.add(msg);
                    // Se a regra foi configurada para bloquear o sistema
                    if (Boolean.parseBoolean(reaBloqueiaSistema)) {
                        bloquear = true;
                    }
                }
            }
            // Se alguma regra nao foi cumprida
            if (!regrasInvalidas.isEmpty()) {
                // Envia email
                try {
                    EnviaEmailHelper.enviarEmailValidacaoAmbiente(regrasInvalidas, bloquear, responsavel);
                } catch (ViewHelperException e) {
                    LOG.error("Erro ao enviar e-mail de validação do ambiente.", e);
                }
                // Bloqueia o sistema
                if (bloquear) {
                    sistemaController.alteraStatusSistema(CodedValues.CSE_CODIGO_SISTEMA, CodedValues.STS_INDISP, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.ambiente.sistema.indisponivel", responsavel), responsavel);
                }
            }
        } catch (ClassNotFoundException | InstantiationException| IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoAmbienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoAmbienteControllerException("mensagem.erro.validacao.ambiente.bloquear.sistema", responsavel, ex);
        }
    }

    /**
     * Método que lista as regras cadastradas no sistema.
     * @param reaAtivo Flag que especifica o status da regra a ser retornada.
     * @param responsavel Responsável pela operação.
     * @return Lista das regras casdastradas.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da classe.
     */
    private List<TransferObject> lstRegraAmbiente(Short reaAtivo, AcessoSistema responsavel) throws ValidacaoAmbienteControllerException {
        try {
            ListaRegraAmbienteQuery query = new ListaRegraAmbienteQuery();
            query.reaAtivo = reaAtivo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoAmbienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Método que realiza a consulta ao banco de uma regra específica.
     * @param regraValidacaoEnum Regra a ser consultada.
     * @return Lista com o valor da regra no banco.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da classe.
     */
    @Override
    public List<TransferObject> obterValorRegraValidacaoAmbiente(RegraValidacaoEnum regraValidacaoEnum) throws ValidacaoAmbienteControllerException {
        try {
            ObtemValidacaoAmbienteQuery query = new ObtemValidacaoAmbienteQuery();
            query.regraValidacaoEnum = regraValidacaoEnum;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidacaoAmbienteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
