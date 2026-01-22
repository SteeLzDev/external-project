function validaCampos() {
  field = getElt("menTitulo");
  if (field.value == '') {
    alert(mensagem('mensagem.informe.men.titulo'));
    field.focus();
    return false;
  } 
  if (f0.funCodigoAux != null && !f0.funCodigoAux.disabled && f0.funCodigoAux.options[f0.funCodigoAux.selectedIndex].value.trim() != '' &&
	  ((f0.menExibeCse != null && f0.menExibeCse.checked) || 
	   (f0.menExibeCor != null && f0.menExibeCor.checked) || 
	   (f0.menExibeOrg != null && f0.menExibeOrg.checked) || 
	   (f0.menExibeSup != null && f0.menExibeSup.checked) || 
	   (f0.menExibeSer != null && f0.menExibeSer.checked))) {

    alert(mensagem('mensagem.informe.funcao.mensagem.apenas.csa'));
    f0.funCodigoAux.focus();
    return false;
  }
  return true;
}

function mudaCombo() {
  if (f0.menExibeCsa.checked || f0.menExibeCor.checked) {
	if(f0.NSE_CODIGO_AUX.value == null || f0.NSE_CODIGO_AUX.value == ""){
		f0.CSA_CODIGO_AUX.disabled=false;		
	}  
    f0.NSE_CODIGO_AUX.disabled=false;
    f0.funCodigoAux.disabled=false;
  } else {
    f0.CSA_CODIGO_AUX.disabled=true;
    f0.NSE_CODIGO_AUX.disabled=true;
    f0.funCodigoAux.disabled=true;
  }

  // Habilita/desabilita opção de bloqueio de csa por não leitura de mensagem
  var radios = f0.menBloqCsaSemLeitura;
  for (var i = 0; i < radios.length; i++) {
    radios[i].disabled = !f0.menExibeCsa.checked;
    
    if (radios[i].disabled && radios[i].value == 'N') {
    	radios[i].checked = true;
    }
  } 

  // Habilita/desabilita opção de push notification para servidor
  var radios = f0.menPushNotificationSer;
  for (var i = 0; i < radios.length; i++) {
    radios[i].disabled = !f0.menExibeSer.checked;
    
    if (radios[i].disabled && radios[i].value == 'N') {
    	radios[i].checked = true;
    }
  }
}

function checkTodos() {
  if (f0.menCheckTodos.checked) {
    if (f0.CSA_CODIGO_AUX != null && f0.CSA_CODIGO_AUX.disabled){      
      f0.CSA_CODIGO_AUX.disabled=false;
      f0.funCodigoAux.disabled=false;
    }
    if (f0.menExibeCse != null && !f0.menExibeCse.checked) { 
      f0.menExibeCse.checked=true;
    }
    if (f0.menExibeCor != null && !f0.menExibeCor.checked) { 
      f0.menExibeCor.checked=true;    
    }
    if (f0.menExibeCsa != null && !f0.menExibeCsa.checked) { 
      f0.menExibeCsa.checked=true;      
    }
    if (f0.menExibeSer != null && !f0.menExibeSer.checked) { 
      f0.menExibeSer.checked=true;
      mudaCombo();
    }
    if (f0.menExibeOrg != null && !f0.menExibeOrg.checked) { 
      f0.menExibeOrg.checked=true;
    }
    if (f0.menExibeSup != null && !f0.menExibeSup.checked) { 
      f0.menExibeSup.checked=true;
    }
  } else {
    if (f0.menExibeCse != null && f0.menExibeCse.checked) { 
      f0.menExibeCse.checked=false;
    }
    if (f0.menExibeCor != null && f0.menExibeCor.checked) { 
      f0.menExibeCor.checked=false;
    }
    if (f0.menExibeCsa != null && f0.menExibeCsa.checked) { 
      f0.menExibeCsa.checked=false;
    }
    if (f0.menExibeCsa != null && !f0.menExibeCsa.checked && f0.menExibeCor != null && !f0.menExibeCor.checked && f0.CSA_CODIGO_AUX != null) {
      f0.CSA_CODIGO_AUX.disabled=true;
    }
    if (f0.menExibeCsa != null && !f0.menExibeCsa.checked && f0.menExibeCor != null && !f0.menExibeCor.checked && f0.funCodigoAux != null) {
      f0.funCodigoAux.disabled=true;
    }
    if (f0.menExibeSer != null && f0.menExibeSer.checked) { 
      f0.menExibeSer.checked=false;
      mudaCombo();
    }
    if (f0.menExibeOrg != null && f0.menExibeOrg.checked) { 
      f0.menExibeOrg.checked=false;
    }
    if (f0.menExibeSup != null && f0.menExibeSup.checked) { 
      f0.menExibeSup.checked=false;
    }
  }  
}

function vf_escolha_csa() {
  var csaCodigo = '';
  var complemento = '';

  if (f0.CSA_CODIGO_AUX != null) {
    for (i = 0 ; i < f0.CSA_CODIGO_AUX.length ; i++) {
      if (f0.CSA_CODIGO_AUX.options[i].selected) {
        csaCodigo += complemento;
        csaCodigo += (f0.CSA_CODIGO_AUX.options[i].value);
        complemento = ',';
      }
    }
    f0.CSA_CODIGO.value = csaCodigo;
  }
}

function vf_escolha_fun() {
  var funCodigo = '';

  if (f0.funCodigoAux != null) {
	if (!f0.funCodigoAux.disabled) {
		for (i = 0 ; i < f0.funCodigoAux.length ; i++) {
			if (f0.funCodigoAux.options[i].selected) {
				funCodigo = (f0.funCodigoAux.options[i].value);
			}
		}
	}
    f0.funCodigo.value = funCodigo;
  }
}

function vf_escolha_nse() {
	var nseCodigo = '';
	var complemento = '';
	  
	if (f0.NSE_CODIGO_AUX != null) {
	  for (i = 0 ; i < f0.NSE_CODIGO_AUX.length ; i++) {
	    if (f0.NSE_CODIGO_AUX.options[i].selected) {
	  	nseCodigo += complemento;
	  	nseCodigo += (f0.NSE_CODIGO_AUX.options[i].value);
	      complemento = ',';
	    }
	  }
	  f0.NSE_CODIGO.value = nseCodigo;
	}
}
