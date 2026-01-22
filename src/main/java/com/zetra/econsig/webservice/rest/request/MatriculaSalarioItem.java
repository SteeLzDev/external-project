package com.zetra.econsig.webservice.rest.request;
 
public class MatriculaSalarioItem {
    public String matricula;
    public String salario;
 
    public MatriculaSalarioItem(String matricula, String salario){
        this.matricula = matricula;
        this.salario = salario;
    }
}