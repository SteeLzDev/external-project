package com.zetra.econsig.dto;

import java.util.Map;

/**
 * <p>Title: TransferObject</p>
 * <p>Description: Interface para todos os TransferObject</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TransferObject {

    public Map<String, Object> getAtributos();

    public void setAtributos(Map<String, Object> atributos);

    public Object getAttribute(String name);

    public void setAttribute(String name, Object value);
}