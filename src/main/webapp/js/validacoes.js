function CGC_OK(Numero_CGC) {
  switch (locale()) {
    case "pt-BR":
      if (!CNPJ.isValid(Numero_CGC)) {
        alert(mensagem('mensagem.erro.cnpj.invalido'));
        return false;
      }
      return true;

    case "es-MX":
      var valid = '^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))';
      var validRfc=new RegExp(valid);
      var matchArray=Numero_CGC.match(validRfc);
      if (matchArray==null) {
        return false;
      } else {
        return true;
      }
    default:
      return true;
  }
}

function sub_string(s,inicio,fim){
	
	var i = 0;
	var str = String("");
	
  for(i = parseInt(inicio);i<parseInt(fim);i++){  	
  	str.concat(String(String(s).charAt(i)));
  }
    
	return str;
}

function CPF_OK(Numero_CPF) {
	switch (locale()) {
    case "pt-BR":
      var Parcela;
      var Quociente;
      var Resto;
      var Soma;
      var Fator;
      var I;
      var C1;
      var C2;
      var dv1;
      var dv2;

      //Verificação dos dois digitos finais em relação ao número completo
      C1 = parseInt(Numero_CPF.substring(9, 10));  //10º caracter = primeiro dígito verificador
      C2 = parseInt(Numero_CPF.substring(10, 11)); //11º caracter = segundo dígito verificador

      //Verificação do primeiro dígito (C1)
      Soma = 0;
      Parcela = 0;
      Fator = 0;

      for(I=1; I<=9; I++) {
        Fator = I + 1;

        Parcela = Fator * parseInt(Numero_CPF.substring(9 - I, 9 - I + 1));
        Soma = Soma + Parcela;
      }

      dv1 = (Soma % 11);
      dv1 = 11 - dv1;

      if(dv1 > 9) {
        dv1 = 0;
      }

      if(C1 != dv1) {
        alert(mensagem('mensagem.erro.cpf.invalido'));
        return false;
      }

      //Verificação do segundo dígito (C2)
      Soma = 0;
      Parcela = 0;
      Fator = 0;

      for(I=1; I<=10; I++) {
        Fator = I + 1;

        Parcela = Fator * parseInt(Numero_CPF.substring(10 - I, 10 - I + 1));
        Soma = Soma + Parcela;
      }

      dv2 = (Soma % 11);
      dv2 = 11 - dv2;

      if(dv2 > 9) {
        dv2 = 0;
      }

      if(C2 != dv2) {
        alert(mensagem('mensagem.erro.cpf.invalido'));
        return false;
      }

      return true;
      break;
    case "es-MX":
        var valid = '^(([A-Z]|[a-z]){4})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))';
        var validRfc=new RegExp(valid);
        var matchArray=Numero_CPF.match(validRfc);
        if (matchArray==null) {
            return false;
        } else {
            return true;
        }
      break;
    case "en-IN":
        var valid = '^(([A-Z]{5})([0-9]{4})([A-Z]{1}))';
        var validRfc=new RegExp(valid);
        var matchArray=Numero_CPF.match(validRfc);
        if (matchArray==null) {
            alert(mensagem('mensagem.erro.cpf.invalido'));
            return false;
        } else {
            return true;
        }
      break;
    case "en-GB":
        var valid = '^(([A-Z]{2})([0-9]{6})([A-Z]{1}))';
        var validRfc=new RegExp(valid);
        var matchArray=Numero_CPF.match(validRfc);
        if (matchArray==null) {
            alert(mensagem('mensagem.erro.cpf.invalido'));
            return false;
        } else {
            return true;
        }
      break;
    case "pt-PT":
    	var nif = Numero_CPF;
    	if (nif != null && nif.length == 9) {
    	    var total = ((nif[7]*2)+(nif[6]*3)+(nif[5]*4)+(nif[4]*5)+(nif[3]*6)+(nif[2]*7)+(nif[1]*8)+(nif[0]*9));
    	    var mod = total % 11;
    	    var controle = -1;	
    	    if (mod == 0 || mod == 1) {
    	        controle = 0;
    	    } else {
    	        controle = 11 - mod;   
    	    }
    	    if (nif[8] == controle) {
    	        return true;
    	    } else {
    	    	alert(mensagem('mensagem.erro.cpf.invalido'));
    	        return false;  
    	    }
    	} else {
    		alert(mensagem('mensagem.erro.cpf.invalido'));
    	    return false;
    	}
        break;
    case "it-IT":
    	var valid = '^(([A-Z]{6}[0-9]{2}[A-Z]{1}[0-9]{2}[A-Z]{1}[0-9]{3}[A-Z]{1}))';
        var validRfc=new RegExp(valid);
        var matchArray=Numero_CPF.match(validRfc);
        if (matchArray==null) {
            alert(mensagem('mensagem.erro.cpf.invalido'));
            return false;
        } else {
            return true;
        }
        break;
	default:
	  return true;
    }
}

// Para utilizar a função, basta adicioná-la ao evento onPaste, ex:
// onPaste="Formata_CPF('SER_CPF')". Para o InternetExplorer a formatação
// é feita no mesmo momento, através do window.clipboardData. Para os
// demais navegadores, é necessário disparar uma função após um 
// período de tempo, pois não é possível acessar o conteúdo colado.
function Formata_CPF(nomeCampo) {
  campo = getElt(nomeCampo);
  if (QualNavegador()=="IE") {
    campo.value = Formata_Num_CPF(window.clipboardData.getData("Text"));
    return false; // Deve ser false para não sobrepor o campo
  } else {
    // Para os demais navegadores, dispara a função após um quarto de segundo
    setTimeout('Formata_CPF_FF("' + nomeCampo + '")', 250);
    return true;
  }
}
function Formata_CPF_FF(nomeCampo) {
  campo = getElt(nomeCampo);
  campo.value = Formata_Num_CPF(campo.value);
}
function Formata_Num_CPF(cpf) {
    switch (locale()) {
    case "pt-BR":
        if (isNumber(cpf) && cpf.length == 11) {
           return cpf.substring(0, 3) + '.' + cpf.substring(3, 6) + '.' + cpf.substring(6, 9) + '-' + cpf.substring(9, 11);
        } else {
           return cpf;
        }
        break;
    case "es-MX":
        return cpf;
        break;
    case "en-GB":
        return cpf;
        break;
    default:
        return cpf;
        break;
    }
}

Dias=new Array(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

function IsNulo(Campo) {
  return ((Campo.value== "") || (Campo.value== null));
}

function ValidaData(Dia, Mes, Ano) {
  if (!IsNulo(Dia)) {
    if (IsNulo(Mes)) {
      alert(mensagem('mensagem.informe.data.mes'));
      Mes.focus();
      return false;
    }
    if (IsNulo(Ano)) {
      alert(mensagem('mensagem.informe.data.ano'));
      Ano.focus();
      return false;
    }
  }
  if (!IsNulo(Mes)) {
    if (IsNulo(Dia)) {
      alert(mensagem('mensagem.informe.data.dia'));
      Dia.focus();
      return false;
    }
    if (IsNulo(Ano)) {
      alert(mensagem('mensagem.informe.data.ano'));
      Ano.focus();
      return false;
    }
  }
  if (!IsNulo(Ano)) {
    if (IsNulo(Dia)) {
      alert(mensagem('mensagem.informe.data.dia'));
      Dia.focus();
      return false;
    }
    if (IsNulo(Mes)) {
      alert(mensagem('mensagem.informe.data.mes'));
      Ano.focus();
      return false;
    }
  }

  if (IsNulo(Dia)) {
    return true;
  }

  var d = Dia.value;
  var m = Mes.value;
  var a = Ano.value;

  if ((a<1900) || (a>2050)) {
    alert(mensagem('mensagem.erro.ano.invalido'));
    Ano.focus();
    return false;
  }
  if ((m<1) || (m>12)) {
    alert(mensagem('mensagem.erro.mes.invalido'));
    Mes.focus();
    return false;
  }
  if ((d < 1) || (d > Dias[m-1])) {
    alert(mensagem('mensagem.erro.dia.invalido').replace('{0}', Dias[m-1]));
    Dia.focus();
    return(false);
  }
  return true;
}

function ValidaCampos(Controles, Msgs) {
  //Garante que as mascaras dos campos estarão setadas...
  for ( var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];
    if ((e.type == 'text') && (!e.disabled)) {
      if (e !=null && e.onfocus && e.onblur) {
        e.onfocus();
        e.onblur();
      } else {
        document.forms[0].elements[i].focus();
        document.forms[0].elements[i].blur();
      }
    }
  }
  for (var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];

    if (sub_string(e.name, 0, 5) == "edDia") {
      if (! ValidaData(e, document.forms[0].elements[i+1], document.forms[0].elements[i+2])) {
        return false;
      }
    }
    for (var j=0;j<Controles.length;j++) {
      if (e.name == Controles[j]) {
        if (e.type=='select-one'){
          var v = "";
          if (e.selectedIndex > -1) {
              v = e.options[e.selectedIndex].value;
          }
        } else {
          var v = e.value;
        }
        if ((v == null) || (trim(v) == "") || (v.toUpperCase() == "NULL")) {
          alert(Msgs[j]);
          if (e != null && !e.disabled) { e.focus(); }
          return false;
        }

        if ((e.name.substr(0,2) == 'rb') || (e.name.substr(0,3) == 'chk') ||
            (e.type == 'radio') || (e.type == 'check')) {
          eval('ee=document.forms[0].'+Controles[j]);
          if(ee == null) {
            alert(Msgs[j]);
            if (e !=null){ e.focus(); }
            return false;
          }
          var eetm = false;
          for(var k=0; k<ee.length; k++) {
            eetm = eetm || ee[k].checked;
          }
          if(!eetm) {
            alert(Msgs[j]);
            if (e !=null){ e.focus(); }
            return false;
          }
        }
      }
    }

    if ((e.type == 'text')  && (!e.disabled)) {
      if(!ValidaMascara(e)) {
        alert(mensagem('mensagem.erro.verifique.campos'));
        if (e != null){ e.focus(); }
        return false;
      }
    }
  }
  return true;
}

function ValidaCamposV4(Controles, Msgs) {
	//Garante que as mascaras dos campos estarão setadas...
	  for ( var i=0; i<document.forms[0].elements.length; i++ ) {
	    var e=document.forms[0].elements[i];
	    if ((e.type == 'text') && (!e.disabled)) {
	      if (e !=null && e.onfocus && e.onblur) {
	        e.onfocus();
	        e.onblur();
	      } else {
	        document.forms[0].elements[i].focus();
	        document.forms[0].elements[i].blur();
	      }
	    }
	  }	  
	  for (var i=0; i<document.forms[0].elements.length; i++ ) {
	    var e=document.forms[0].elements[i];

	    if (sub_string(e.name, 0, 5) == "edDia") {
	      if (! ValidaData(e, document.forms[0].elements[i+1], document.forms[0].elements[i+2])) {
	    	  event.preventDefault();
	    	  fouterrv4(e);
	    	  return false;
	      }
	    }
	    for (var j=0;j<Controles.length;j++) {
	      if (e.name == Controles[j]) {
	        if (e.type=='select-one'){
	          var v = "";
	          if (e.selectedIndex > -1) {
	              v = e.options[e.selectedIndex].value;
	          }
	        } else {
	          var v = e.value;
	        }
	        if ((v == null) || (trim(v) == "") || (v.toUpperCase() == "NULL")) {
	          alert(Msgs[j]);
	          if (e != null && !e.disabled) { e.focus(); }
	          event.preventDefault();
	        //document.forms[0].classList.add('was-validated');
	    	  fouterrv4(e);
		      
	          return false;
	        }

	        if ((e.name.substr(0,2) == 'rb') || (e.name.substr(0,3) == 'chk') ||
	            (e.type == 'radio') || (e.type == 'check')) {
	          eval('ee=document.forms[0].'+Controles[j]);
	          if(ee == null) {
	            alert(Msgs[j]);
	            if (e !=null){ e.focus(); }
	            event.preventDefault();
		        	            
	          //document.forms[0].classList.add('was-validated');
		    	  fouterrv4(e);
		    	
	            return false;
	          }
	          var eetm = false;
	          for(var k=0; k<ee.length; k++) {
	            eetm = eetm || ee[k].checked;
	          }
	          if(!eetm) {
	            alert(Msgs[j]);
	            if (e !=null){ e.focus(); }
	            event.preventDefault();
		        	            
	          //document.forms[0].classList.add('was-validated');
		    	  fouterrv4(e);		    	
	            return false;
	          }
	        }
	      }
	    }

	    if ((e.type == 'text')  && (!e.disabled)) {
	      if(!ValidaMascara(e)) {
	        alert(mensagem('mensagem.erro.verifique.campos'));
	        if (e != null){ e.focus(); }
	        event.preventDefault();      
	        
	      //document.forms[0].classList.add('was-validated');
	    	  fouterrv4(e);
	        return false;
	      }
	    }
	  }
	  
	  return true;
}

//Valida se pelo menos um dos campos passados estão preenchidos.
function ValidaCamposPeloMenosUmPreenchido(Controles, MsgPeloMenosUm) {
  limparErros("mensagens");
  //Garante que as mascaras dos campos estarão setadas...
  for ( var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];
    if ((e.type == 'text') && (!e.disabled)) {
      if (e !=null) {
        e.onfocus();
        e.onblur();
      } else {
        document.forms[0].elements[i].focus();
        document.forms[0].elements[i].blur();
      }
    }
  }
  
  var umPreenchido = false;
  var alerta = "";
  
  for (var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];

    for(var j=0;j<Controles.length;j++) {
      if (e.name == Controles[j]) {
        
        if (e.type=='select-one'){
          var v = "";
          if (e.selectedIndex > -1) {
              v = e.options[e.selectedIndex].value;
          }
        } else if (e.type=='select-multiple' && e.options.length.toString() != "0") {
            var v = e.options.length.toString();
        } else {
          var v = e.value;
        }
        
        if ((v == null) || (trim(v) == "") || (v.toUpperCase() == "NULL")) {
        	if (e != null && !e.disabled) { e.className += " is-invalid"; }
        	umPreenchido = false;
        	jQuery("#"+e.id).addClass("is-invalid");
        }
        if (v != null && trim(v) != "" && v.toUpperCase() != "NULL"){
        	umPreenchido = true;
        	break;
        }

        if ((e.name.substr(0,2) == 'rb') || (e.name.substr(0,3) == 'chk') ||
            (e.type == 'radio') || (e.type == 'check')) {
            
          eval('ee=document.forms[0].'+Controles[j]);
          
          if (ee != null) {
            umPreenchido = true;
            break;
          }
          
          var eetm = false;
          for (var k=0; k<ee.length; k++) {
            eetm = eetm || ee[k].checked;
          }
          if (eetm) {
            umPreenchido = true;
            break;
          }
        }
      }
    }

    if ((e.type == 'text' || e.type=='select-multiple') && (!e.disabled)) {
      if (!ValidaMascara(e)) {
        alerta += mensagem('mensagem.erro.verifique.campos');
        if (e != null){ e.focus(); }
      }else if(umPreenchido){
    	  break;
      } 
    }
  }

  if (!umPreenchido) {
    if (alerta == "") {
      mostrarMensagem("mensagens", 'danger', MsgPeloMenosUm);
    } else {
      mostrarMensagem("mensagens", 'danger', MsgPeloMenosUm + "<br>" + alerta);
    }
    return false;
  } else {
    return true;  
  }
}

// Retira os caracteres de pontuação do CNPJ ou do CPF retornando apenas os digitos
function extraiNumCNPJCPF(cnpj_cpf) {
    switch (locale()) {
    case "pt-BR":
        // CNPJ
        if (cnpj_cpf.length == 18) {
          return cnpj_cpf.substring(0,2) + cnpj_cpf.substring(3,6) + cnpj_cpf.substring(7,10) + cnpj_cpf.substring(11,15) + cnpj_cpf.substring(16,18);
        }
        // CPF
        else if (cnpj_cpf.length == 14) {
          return cnpj_cpf.substring(0,3) + cnpj_cpf.substring(4,7) + cnpj_cpf.substring(8,11) + cnpj_cpf.substring(12,14);
        } else {
          return "";
        }
        break;
    case "es-MX":
        return cnpj_cpf;
        break;
    default :
        return cnpj_cpf;
        break;
    }
}

// Faz parse de uma data em String e retorna um array com os componentes
// na ordem [Ano, Mês, Dia]
function obtemPartesData(dateString) {
  var dia, mes, ano;
  switch (locale()) {
    case "pt-BR":  /* dd/MM/yyyy ou MM/yyyy com separadores de barra, ponto, traço ou espaço */
    case "pt-PT":
    case "es-MX":
    case "en-IN":
    case "en-GB":
    case "it-IT":
      var partesData = dateString.split(/[ \/\.-]/g);
      dia = (partesData.length == 3 ? partesData[0] : 1);
      mes = (partesData.length == 3 ? partesData[1] : partesData[0]);
      ano = (partesData.length == 3 ? partesData[2] : partesData[1]);
      break;
    case "en-US":  /* MM/dd/yyyy ou MM/yyyy com separadores de barra, ponto, traço ou espaço */
      var partesData = dateString.split(/[ \/\.-]/g);
      dia = (partesData.length == 3 ? partesData[1] : 1);
      mes = partesData[0];
      ano = (partesData.length == 3 ? partesData[2] : partesData[1]);
      break;
    default: /* yyyy-MM-dd ou yyyy-MM */
      var partesData = dateString.split('-');
      dia = (partesData.length == 3 ? partesData[2] : 1);
      mes = partesData[1];
      ano = partesData[0];
      break;
  }
  return [dia, mes, ano];
}

function verificaData(data) {
  var partesData = obtemPartesData(data);
  var d = partesData[0];
  var m = partesData[1];
  var a = partesData[2];

  if (isNaN(d) || isNaN(m) || isNaN(a)) {
    alert(mensagem('mensagem.erro.data.invalida'));
    return false;
  }
  if ((a<1900) || (a>2050)) {
    alert(mensagem('mensagem.erro.ano.invalido'));
    return false;
  }
  if ((m<1) || (m>12)) {
    alert(mensagem('mensagem.erro.mes.invalido'));
    return false;
  }
  if ((d < 1) || (d > Dias[m-1])) {
    alert(mensagem('mensagem.erro.dia.invalido').replace('{0}', Dias[m-1]));
    return false;
  }
  return true;
}

function verificaPeriodo(data, periodicidade) {
  var partesData = obtemPartesData(data);
  var p = partesData[1];
  var a = partesData[2];

  periodicidade = (periodicidade === undefined) ? 'M' : periodicidade;
  var limitePeriodo = { 'M': 12, 'Q': 24, 'G': 26, 'S': 52 };
  var mensagemErro = { 'M': 'mensagem.erro.mes.invalido', 'Q': 'mensagem.erro.quinzena.invalida', 'G': 'mensagem.erro.quatorzena.invalida', 'S': 'mensagem.erro.semana.invalida' };
  
  if (isNaN(p) || isNaN(a)) {
    alert(mensagem('mensagem.erro.data.invalida.mes.ano'));
    return false;
  }
  if (a<1900 || a>2050) {
    alert(mensagem('mensagem.erro.ano.invalido'));
    return false;
  }
  if (p<1 || p>limitePeriodo[periodicidade]) {
    alert(mensagem(mensagemErro[periodicidade]));
    return false;
  }
  return true;
}

function verificaHora(Hora) {
  var PartesHora = new Array();
  PartesHora = Hora.split(':');
  var h = PartesHora[0];
  var m = PartesHora[1];
  var s = PartesHora[2];

  if (isNaN(h) || isNaN(m) || isNaN(s)) {
    alert(mensagem('mensagem.erro.horario.invalido'));
    return false;
  }
  if (h > 23) {
    alert(mensagem('mensagem.erro.hora.invalida'));
    return false;
  }
  if (m > 59) {
    alert(mensagem('mensagem.erro.minuto.invalido'));
    return false;
  }
  if (s > 59) {
    alert(mensagem('mensagem.erro.segundo.invalido'));
    return false;
  }
  return true;
}

function verificaIp(tipoEndereco, ip, bloquearIpInterno) {
  var charInvalidos = ';';
  var charFinaisValidos = '0123456789*';
  var qtdePontos = 0;
  var retorno = true;

  for (var i = 0; i < ip.length; i++) {
    if(ip.charAt(i) == ".") {
      qtdePontos++;
    }

    if(charInvalidos.indexOf(ip.charAt(i)) >= 0) {
        retorno = false;
        break;
    }
  }
  
  if (tipoEndereco == "numero_ip") {
      // Devem existir exatamente 3 pontos e o ip deve terminar em algarismo ou asterisco.
      if (qtdePontos != 3 || ip == null || charFinaisValidos.indexOf(ip.charAt(ip.length - 1)) < 0) {
        retorno = false;
      } 
    
      // Verifica se o IP é interno
      if ((ip.substring(0,8) == '192.168.') || (ip.substring(0,7) == '172.16.') || (ip.substring(0,4) == '127.') || (ip.substring(0,3) == '10.')) {
        if (bloquearIpInterno == 'true') {
          retorno = false;
        } else if (!confirm(mensagem('mensagem.erro.ip.interno'))) {
          return false;
        }
      }

      if (!retorno) {
        alert(mensagem('mensagem.erro.ip.invalido'));
      }
  }

  return retorno;
}

/*
 * Métodos para validação no leiaute v4.
 */

function mostrarMensagem(containerId, tipo, mensagem) {
  var html = "<div class=\"alert alert-" + tipo + "\" role=\"alert\"><p class=\"mb-0\">" + mensagem + "</p></div>";
  jQuery("#" + containerId).append(html);
} 

function validarCampos(mensagensId, controles, msgs) {
  limparErros(mensagensId);
  // Garante que as mascaras dos campos estarão setadas...
  for ( var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];
    if ((e.type == 'text') && (!e.disabled)) {
      if (e !=null && e.onfocus && e.onblur) {
        e.onfocus();
        e.onblur();
      } else {
        document.forms[0].elements[i].focus();
        document.forms[0].elements[i].blur();
      }
    }
  }
  var validacaoChk = [];
  var ok = true;
  for (var i=0; i<document.forms[0].elements.length; i++ ) {
    var e=document.forms[0].elements[i];
    if (sub_string(e.name, 0, 5) == "edDia") {
      if (! ValidaData(e, document.forms[0].elements[i+1], document.forms[0].elements[i+2])) {
        ok = false;
        jQuery("#"+e.id).addClass("is-invalid");
      }
    }
    for (var j=0; j < controles.length; j++) {
      if (e.name == controles[j] && validacaoChk.indexOf(e.name) == -1) {
        if (e.type=='select-one'){
          var v = "";
          if (e.selectedIndex > -1) {
              v = e.options[e.selectedIndex].value;
          }
        } else {
          var v = e.value;
        }
        if ((v == null) || (trim(v) == "") || (v.toUpperCase() == "NULL")) {
          mostrarMensagem(mensagensId, 'danger', msgs[j]);
          if (e != null && !e.disabled) { e.className += " is-invalid"; }
          ok = false;
          jQuery("#"+e.id).addClass("is-invalid");
        }

        if ((e.name.substr(0,2) == 'rb') || (e.name.substr(0,3) == 'chk') ||
            (e.type == 'radio') || (e.type == 'check')) {
          eval('ee=document.forms[0].' + controles[j]);
          if(ee == null) {
            mostrarMensagem(mensagensId, 'danger', msgs[j]);
            if (e !=null) { e.className += " is-invalid"; }
            ok = false;
            jQuery("#"+e.id).addClass("is-invalid");
          }
          var eetm = false;
          for(var k=0; k<ee.length; k++) {
            eetm = eetm || ee[k].checked;
          }
          if(!eetm) {
            mostrarMensagem(mensagensId, 'danger', msgs[j]);
            if (e !=null) { e.className += " is-invalid"; }
            ok = false;
          }
        }
        validacaoChk.push(e.name);
      }
    }

    if ((e.type == 'text')  && (!e.disabled)) {
      if(!ValidaMascara(e)) {
        mostrarMensagem(mensagensId, 'danger', mensagem('mensagem.erro.verifique.campos'));
        if (e != null){ e.className += " is-invalid"; }
        ok = false;
        jQuery("#"+e.id).addClass("is-invalid");
      }
    }
  }
  return ok;
}

function limparErros(mensagensId) {
  // limpa as mensagens antigas
  jQuery("#" + mensagensId).html("");
  // remove a classe de erro dos elentos
  jQuery(".is-invalid").removeClass("is-invalid");
}