package com.zetra.econsig.helper.rede;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * <p>Title: DDNSAddress</p>
 * <p>Description: Classe utilitária que resolve o endereço de um DDNS dado.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DDNSAddress {
    public static String getIpDDNSAddress(String ddnsName) throws UnknownHostException{
        return InetAddress.getByName(ddnsName).toString();
    }
}
