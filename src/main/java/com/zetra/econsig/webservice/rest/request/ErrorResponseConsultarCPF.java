 
package com.zetra.econsig.webservice.rest.request;
 
public class ErrorResponseConsultarCPF {
    public int code;
    public String message;
    public ErrorResponseConsultarCPF(int code, String message) { this.code = code; this.message = message; }
}