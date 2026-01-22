
/**
 * PapelUsuario.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.1
 * Built on : Aug 31, 2011 (12:23:23 CEST)
 */

package com.zetra.econsig.webservice.soap.entidade;

/**
 * <p>Title: PapelUsuario</p>
 * <p>Description: Entidade PapelUsuario para requisição SOAP.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class PapelUsuario /*implements org.apache.axis2.databinding.ADBBean*/ {
    /* This type was generated from the piece of schema that had
       name = PapelUsuario
       Namespace URI = PapelUsuario
       Namespace Prefix = ns4
     */

    /**
     * field for Cse
     */

    protected boolean localCse;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCseTracker = false;

    public boolean isCseSpecified() {
        return localCseTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getCse() {
        return localCse;
    }

    /**
      * Auto generated setter method
      * @param param Cse
      */
    public void setCse(boolean param) {

        // setting primitive attribute tracker to true
        localCseTracker = true;

        localCse = param;

    }

    /**
     * field for Org
     */

    protected boolean localOrg;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localOrgTracker = false;

    public boolean isOrgSpecified() {
        return localOrgTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getOrg() {
        return localOrg;
    }

    /**
      * Auto generated setter method
      * @param param Org
      */
    public void setOrg(boolean param) {

        // setting primitive attribute tracker to true
        localOrgTracker = true;

        localOrg = param;

    }

    /**
     * field for Csa
     */

    protected boolean localCsa;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCsaTracker = false;

    public boolean isCsaSpecified() {
        return localCsaTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getCsa() {
        return localCsa;
    }

    /**
      * Auto generated setter method
      * @param param Csa
      */
    public void setCsa(boolean param) {

        // setting primitive attribute tracker to true
        localCsaTracker = true;

        localCsa = param;

    }

    /**
     * field for Cor
     */

    protected boolean localCor;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localCorTracker = false;

    public boolean isCorSpecified() {
        return localCorTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getCor() {
        return localCor;
    }

    /**
      * Auto generated setter method
      * @param param Cor
      */
    public void setCor(boolean param) {

        // setting primitive attribute tracker to true
        localCorTracker = true;

        localCor = param;
    }

    /**
     * field for Ser
     */
    protected boolean localSer;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localSerTracker = false;

    public boolean isSerSpecified() {
        return localSerTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getSer() {
        return localSer;
    }

    /**
      * Auto generated setter method
      * @param param Ser
      */
    public void setSer(boolean param) {

        // setting primitive attribute tracker to true
        localSerTracker = true;

        localSer = param;

    }
    /**
     * field for Sup
     */
    protected boolean localSup;

    /*  This tracker boolean wil be used to detect whether the user called the set method
    *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localSupTracker = false;

    public boolean isSupSpecified() {
        return localSupTracker;
    }

    /**
     * Auto generated getter method
     * @return boolean
     */
    public boolean getSup() {
        return localSup;
    }

    /**
     * Auto generated setter method
     * @param param Sup
     */
    public void setSup(boolean param) {

        // setting primitive attribute tracker to true
        localSupTracker = true;

        localSup = param;

    }
}