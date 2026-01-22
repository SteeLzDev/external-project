package com.zetra.econsig.helper.sistema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.CampoUsuario;
import com.zetra.econsig.persistence.entity.CampoUsuarioHome;
import com.zetra.econsig.persistence.entity.CampoUsuarioId;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ShowFieldHelper</p>
 * <p>Description: Helper para buscar no arquivo de propriedades para campos da tela
 * se um dado campo é visível ou não.</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class ShowFieldHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ShowFieldHelper.class);

    private static final String FIELDS_PROPERTIES_FILE = "FieldsPermission.properties";

    private Map<String, String> recursos;

    private static class SingletonHelper {
        private static final ShowFieldHelper instance = new ShowFieldHelper();
    }

    public static ShowFieldHelper getInstance() {
        return SingletonHelper.instance;
    }

    private ShowFieldHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            recursos = new ExternalMap<>();
        } else {
            recursos = new HashMap<>();
        }
    }

    public static void load() {
        try {
            if (SingletonHelper.instance.recursos.isEmpty()) {
                SingletonHelper.instance.loadFieldProperties();
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static boolean showField(String fieldProp, AcessoSistema responsavel) throws ZetraException {
        if (SingletonHelper.instance.recursos.isEmpty()) {
            SingletonHelper.instance.loadFieldProperties();
        }

        return getValue(fieldProp, responsavel);
    }

    public static boolean exibeCampoUsuario(String nomeCampo, AcessoSistema responsavel) throws ZetraException {
        boolean exibicaoCampoUsuario = false;
        final CampoUsuarioId pk = new CampoUsuarioId(responsavel.getUsuCodigo(), nomeCampo);
        CampoUsuario campoUsuario = null;

        try {
            campoUsuario = CampoUsuarioHome.findByPrimaryKey(pk);
            if (campoUsuario != null) {
                exibicaoCampoUsuario = campoUsuario.getCauValor().equals(CodedValues.CAU_SIM);
            }
        } catch (final FindException ex) {
            if (campoUsuario == null) {
                exibicaoCampoUsuario = showField(nomeCampo, responsavel);
            }
        }

        return exibicaoCampoUsuario;
    }

    public static boolean isDisabled(String fieldProp, AcessoSistema responsavel) throws ZetraException {
        if (SingletonHelper.instance.recursos.isEmpty()) {
            SingletonHelper.instance.loadFieldProperties();
        }

        return checkDisabled(fieldProp, responsavel);
    }

    private void loadFieldProperties() throws ZetraException {
        final Properties fieldProperties = new Properties();

        try {
            fieldProperties.load(ShowFieldHelper.class.getClassLoader().getResourceAsStream(FIELDS_PROPERTIES_FILE));

            final Map<String, String> propriedadesArq = new HashMap<>();

            for (final Entry<Object, Object> entrada : fieldProperties.entrySet()) {
                propriedadesArq.put(entrada.getKey().toString(), entrada.getValue().toString());
            }

            if (recursos.isEmpty()) {
                synchronized (this) {
                    final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
                    // Carrega os textos do fieldspermission no banco de dados
                    sistemaController.carregarCampoSistema(propriedadesArq, AcessoSistema.getAcessoUsuarioSistema());

                    // Recarrega os textos à partir do banco
                    final List<TransferObject> lstTextos = sistemaController.lstCampoSistema(AcessoSistema.getAcessoUsuarioSistema());
                    final Map<String, String> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursos;
                    for (final TransferObject texto : lstTextos) {
                        final String chave = texto.getAttribute(Columns.CAS_CHAVE).toString();
                        final String valor = texto.getAttribute(Columns.CAS_VALOR).toString();
                        mapForLoad.put(chave, valor);
                    }
                    if (ExternalCacheHelper.hasExternal() && recursos.isEmpty()) {
                        recursos.putAll(mapForLoad);
                    }
                }
            }
        } catch (final Exception ex) {
            throw new ZetraException("mensagem.erro.carregar.fields.permission", (AcessoSistema) null, ex);
        }
    }

    public static boolean canEdit(String fieldProp, AcessoSistema responsavel) throws ZetraException {
        return showField(fieldProp, responsavel) && !isDisabled(fieldProp, responsavel);
    }

    private static boolean getValue(String key, AcessoSistema responsavel) {
        String value = null;

        if (responsavel.getTipoEntidade() != null) {
            final String entKey = responsavel.getTipoEntidade().toLowerCase() + "." + key;
            value = SingletonHelper.instance.recursos.get(entKey);
        }

        if (TextHelper.isNull(value)) {
            value = SingletonHelper.instance.recursos.get(key);
        }

        return (value != null) && !value.equalsIgnoreCase("n");
    }

    private static boolean checkDisabled(String key, AcessoSistema responsavel) {
        String value = null;

        if (responsavel.getTipoEntidade() != null) {
            final String entKey = responsavel.getTipoEntidade().toLowerCase() + "." + key;
            value = SingletonHelper.instance.recursos.get(entKey);
        }

        if (TextHelper.isNull(value)) {
            value = SingletonHelper.instance.recursos.get(key);
        }

        return (value != null) && value.equalsIgnoreCase("b");
    }

    public static boolean isRequired(String key, AcessoSistema responsavel) throws ZetraException {
        String value = null;
        if (SingletonHelper.instance.recursos.isEmpty()) {
            SingletonHelper.instance.loadFieldProperties();
        }
        if (responsavel.getTipoEntidade() != null) {
            final String entKey = responsavel.getTipoEntidade().toLowerCase() + "." + key;
            value = SingletonHelper.instance.recursos.get(entKey);
        }

        if (TextHelper.isNull(value)) {
            value = SingletonHelper.instance.recursos.get(key);
        }

        return  (value != null) && value.equalsIgnoreCase("o");
    }

    public static void reset() {
        SingletonHelper.instance.recursos.clear();
    }
}
