package com.zetra.econsig.webservice.soap.util;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: VersionInfo</p>
 * <p>Description: Classe auxiliar para detectar a versão da API.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Alexandre Gonçalves, Fagner Luiz, Igor Lucas, Eduardo Fortes, Leonel Martins
 */
public class VersionInfo {
    private static final List<String> SERVICES = new ArrayList<>();
    private static final String VERSION_PREFIX = "-v";

    static {
        SERVICES.add("CompraService");
        SERVICES.add("FolhaService");
        SERVICES.add("HostaHostService"); // Operacional
        SERVICES.add("LoteService");
        SERVICES.add("ServidorService");
    }

    private final String label;
    private final String service;
    private final Integer major;
    private final Integer minor;

    public VersionInfo(String label) {
        this.label = label;
        String prefix = "";
        String serviceName = null;
        for (final String valor : SERVICES) {
            if (label.startsWith(valor)) {
                serviceName = valor;
                prefix = valor + VERSION_PREFIX;
                break;
            }
        }
        service = serviceName;
        final String[] version = !TextHelper.isNull(prefix) ? label.replaceFirst(prefix, "").split("_") : new String[0];
        if (version.length > 1) {
            major = Integer.valueOf(version[0]);
            minor = Integer.valueOf(version[1]);
        } else {
            // Caso não seja possível detectar a versão, o padrão é utilizar a versão 1.0
            major = 1;
            minor = 0;
        }
    }

    public String getLabel() {
        return label;
    }

    public String getService() {
        return service;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getVersion() {
        return major + "_" + minor;
    }

    public boolean isV1() {
        return (major == 1);
    }

    public boolean isV2() {
        return (major == 2);
    }

    public boolean isV3() {
        return (major == 3);
    }

    public boolean isV4() {
        return (major == 4);
    }

    public boolean isV5() {
        return (major == 5);
    }

    public boolean isV6() {
        return (major == 6);
    }

    public boolean isV7() {
        return (major == 7);
    }

    public boolean isV8() {
        return (major == 8);
    }

    public boolean isV2orGreater() {
        return (major >= 2);
    }

    public boolean isV3orGreater() {
        return (major >= 3);
    }

    public boolean isV4orGreater() {
        return (major >= 4);
    }

    public boolean isV5orGreater() {
        return (major >= 5);
    }

    public boolean isV6orGreater() {
        return (major >= 6);
    }

    public boolean isV7orGreater() {
        return (major >= 7);
    }

    public boolean isV8orGreater() {
        return (major >= 8);
    }

    @Override
    public String toString() {
        return "Label: " + label + " - major: " + major + " - minor: " + minor;
    }

    public static boolean isService(String label) {
        return SERVICES.contains(label);
    }
}
