package com.zetra.econsig.helper.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.helper.comunicacao.ControleComunicacaoPermitida;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto;
import com.zetra.econsig.helper.contabilizacao.ContabilizacaoInclusaoContratos;
import com.zetra.econsig.helper.email.ControleEnvioEmail;
import com.zetra.econsig.helper.limiteoperacao.RegraLimiteOperacaoCache;
import com.zetra.econsig.helper.log.ControleTipoEntidade;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.ControleAcessoSeguranca;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.senhaexterna.ParamSenhaExternaHelper;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sistema.RecursoSistemaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.sistema.ViewImageHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.usuario.CertificadoDigital;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.webservice.rest.filter.IpWatchdog;

import lombok.Getter;

/**
 * <p>Title: ExternalCacheConfig</p>
 * <p>Description: Configuração do acesso ao Redis.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
@Configuration
@Service
@Getter
public class ExternalCacheConfig {
    @Value("${external.cache.enabled:false}")
    private boolean enabled;

    @Value("${external.cache.paramsist.enabled:true}")
    private boolean paramSistEnabled;

    @Value("${external.cache.paramsvc.enabled:true}")
    private boolean paramsvcEnabled;

    @Value("${external.cache.controle-consulta.enabled:true}")
    private boolean controleConsultaEnabled;

    @Value("${external.cache.controle-comunicacao-permitida.enabled:true}")
    private boolean controleComunicacaoPermitidaEnabled;

    @Value("${external.cache.controle-tipo-entidade.enabled:true}")
    private boolean controleTipoEntidadeEnabled;

    @Value("${external.cache.acesso-recurso.enabled:true}")
    private boolean acessoRecursoEnabled;

    @Value("${external.cache.margem.enabled:true}")
    private boolean margemEnabled;

    @Value("${external.cache.natureza-rel-svc.enabled:true}")
    private boolean naturezaRelSvcEnabled;

    @Value("${external.cache.certificado-digital.enabled:true}")
    private boolean certificadoDigitalEnabled;

    @Value("${external.cache.funcao-exige-motivo.enabled:true}")
    private boolean funcaoExigeMotivoEnabled;

    @Value("${external.cache.config-relatorio.enabled:true}")
    private boolean configRelatorioEnabled;

    @Value("${external.cache.controle-restricao-acesso.enabled:true}")
    private boolean controleRestricaoAcessoEnabled;

    @Value("${external.cache.casamento-margem.enabled:true}")
    private boolean casamentoMargemEnabled;

    @Value("${external.cache.status-autorizacao-desconto.enabled:true}")
    private boolean statusAutorizacaoDescontoEnabled;

    @Value("${external.cache.periodo.enabled:true}")
    private boolean periodoEnabled;

    @Value("${external.cache.repasse.enabled:true}")
    private boolean repasseEnabled;

    @Value("${external.cache.controle-acesso-seguranca.enabled:true}")
    private boolean controleAcessoSegurancaEnabled;

    @Value("${external.cache.application-resources.enabled:true}")
    private boolean applicationResourcesEnabled;

    @Value("${external.cache.param-senha-externa.enabled:true}")
    private boolean paramSenhaExternaHelperEnabled;

    @Value("${external.cache.controle-envio-email.enabled:true}")
    private boolean controleEnvioEmailEnabled;

    @Value("${external.cache.show-field.enabled:true}")
    private Boolean showFieldEnabled;

    @Value("${external.cache.view-image.enabled:true}")
    private Boolean viewImageEnabled;

    @Value("${external.cache.ip-watchdog.enabled:true}")
    private Boolean ipWatchdogEnabled;

    @Value("${external.cache.recurso-sistema.enabled:true}")
    private Boolean recursoSistemaEnabled;

    @Value("${external.cache.regra-limite-operacao.enabled:true}")
    private Boolean regraLimiteOperacaoEnabled;

    @Value("${external.cache.limite-consignacao-csa-diario.enabled:true}")
    private Boolean limiteIncContratosCsaDiario;

    @Autowired
    private RedisConnectionFactory factory;

    private final Map<String, Boolean> enabledByClass = new HashMap<>();

    private synchronized void load() {
        enabledByClass.put(ParamSist.class.getSimpleName(), paramSistEnabled);
        enabledByClass.put(ParamSvcTO.class.getSimpleName(), paramsvcEnabled);
        enabledByClass.put(ControleConsulta.class.getSimpleName(), controleConsultaEnabled);
        enabledByClass.put(ControleComunicacaoPermitida.class.getSimpleName(), controleComunicacaoPermitidaEnabled);
        enabledByClass.put(ControleTipoEntidade.class.getSimpleName(), controleTipoEntidadeEnabled);
        enabledByClass.put(AcessoRecursoHelper.class.getSimpleName(), acessoRecursoEnabled);
        enabledByClass.put(MargemHelper.class.getSimpleName(), margemEnabled);
        enabledByClass.put(NaturezaRelSvc.class.getSimpleName(), naturezaRelSvcEnabled);
        enabledByClass.put(CertificadoDigital.class.getSimpleName(), certificadoDigitalEnabled);
        enabledByClass.put(FuncaoExigeMotivo.class.getSimpleName(), funcaoExigeMotivoEnabled);
        enabledByClass.put(ConfigRelatorio.class.getSimpleName(), configRelatorioEnabled);
        enabledByClass.put(ControleRestricaoAcesso.class.getSimpleName(), controleRestricaoAcessoEnabled);
        enabledByClass.put(CasamentoMargem.class.getSimpleName(), casamentoMargemEnabled);
        enabledByClass.put(StatusAutorizacaoDesconto.class.getSimpleName(), statusAutorizacaoDescontoEnabled);
        enabledByClass.put(PeriodoHelper.class.getSimpleName(), periodoEnabled);
        enabledByClass.put(RepasseHelper.class.getSimpleName(), repasseEnabled);
        enabledByClass.put(ControleAcessoSeguranca.class.getSimpleName(), controleAcessoSegurancaEnabled);
        enabledByClass.put(ApplicationResourcesHelper.class.getSimpleName(), applicationResourcesEnabled);
        enabledByClass.put(ParamSenhaExternaHelper.class.getSimpleName(), paramSenhaExternaHelperEnabled);
        enabledByClass.put(ControleEnvioEmail.class.getSimpleName(), controleEnvioEmailEnabled);
        enabledByClass.put(ShowFieldHelper.class.getSimpleName(), showFieldEnabled);
        enabledByClass.put(ViewImageHelper.class.getSimpleName(), viewImageEnabled);
        enabledByClass.put(IpWatchdog.class.getSimpleName(), ipWatchdogEnabled);
        enabledByClass.put(RecursoSistemaHelper.class.getSimpleName(), recursoSistemaEnabled);
        enabledByClass.put(RegraLimiteOperacaoCache.class.getSimpleName(), regraLimiteOperacaoEnabled);
        enabledByClass.put(ContabilizacaoInclusaoContratos.class.getSimpleName(), limiteIncContratosCsaDiario);
    }

    /**
     *
     */
    public void disable() {
        enabled = false;
    }

    /**
     *
     * @param name
     * @return
     */
    public Boolean getEnabledByClass(String name) {
        if (enabledByClass.isEmpty()) {
            load();
        }
        return enabledByClass.get(name);
    }

    /**
    *
    * @param factory
    * @return
    */
   @Bean(name = "redisTemplateForCache")
   RedisTemplate<Object, Object> redisTemplate() {
       final RedisTemplate<Object, Object> template = new RedisTemplate<>();
       template.setKeySerializer(new StringRedisSerializer());
       template.setHashKeySerializer(new JdkSerializationRedisSerializer());
       template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
       template.setConnectionFactory(factory);
       return template;
   }
}
