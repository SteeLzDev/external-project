package com.zetra.econsig.webservice.soap.assembler;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBElement;

/**
 * <p>Title: BaseAssembler</p>
 * <p>Description: Métodos gerais para os Assemblers.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1118")
public abstract class BaseAssembler {

    /**
     * Convert a java.util.Date to javax.xml.datatype.XMLGregorianCalendar
     * @param date The date to be converted
     * @return The corresponding XMLGregorianCalendar for the date informed or null
     * @throws DatatypeConfigurationException
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date, boolean hasTime) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        final XMLGregorianCalendar xmlGC = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        if (!hasTime) {
            xmlGC.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
        }
        return xmlGC;
    }

    /**
     * Return an object with the type of the value of the element or null.
     * @param <T> Class of the return.
     * @param element Element which value will be return
     * @return The value of the element or null
     */
    public static <T> T getValue(JAXBElement<T> element) {
        if (element == null) {
            return null;
        } else {
            return element.getValue();
        }
    }

    /**
     * Return the java.util.Date equivalent of the element
     * @param element Element to convert to java.util.Date
     * @return The value of the element as java.util.Date.
     */
    public static Date getValueAsDate(JAXBElement<XMLGregorianCalendar> element) {
        if ((element == null) || (element.getValue() == null)) {
            return null;
        } else {
            return element.getValue().toGregorianCalendar().getTime();
        }
    }

    /**
     * Return the java.util.Date equivalent of the value
     * @param value Value to convert to java.util.Date
     * @return The value as java.util.Date.
     */
    public static Date toDate(XMLGregorianCalendar value) {
        if ((value == null)) {
            return null;
        } else {
            return value.toGregorianCalendar().getTime();
        }
    }
}