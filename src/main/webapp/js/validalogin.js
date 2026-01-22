function ValidaLogin(plainPasswordField, cryptedPasswordField) {
  var Nome, Agencia;
  
  Nome = f0.username.value;

  if ((Nome == "") || (Nome == null)) {
    alert(mensagem('mensagem.informe.login.usuario'));
    f0.username.focus();
    focusSenha = false;
    focusAgencia = false;
    return false;
  }

  if(!CriptografaSenha(plainPasswordField, cryptedPasswordField, true, null)) {
	return false;
  }

  if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
    alert(mensagem('mensagem.informe.login.captcha'));
    f0.captcha.focus();
    return false;
  }

  // Valida captcha avan�ado
  if ((f0.recaptcha_response_field != undefined) && (f0.recaptcha_response_field.value == "")) {
    alert(mensagem('mensagem.informe.login.captcha'));
    f0.recaptcha_response_field.focus();
    return false;
  }

  return true;
}

function CriptografaSenha(plainPasswordField, cryptedPasswordField, senhaObrigatoria, nullPasswordMessage) {
    
  if (plainPasswordField == null) {
    plainPasswordField = f0.senha_aberta;
  }

  if (cryptedPasswordField == null) {
    cryptedPasswordField = f0.senha_criptografada;
  }

  Senha = plainPasswordField.value;
  
  if (senhaObrigatoria) {
	  if (Senha == null || Senha == "") {
		if (focusSenha == true) {
	      alert(nullPasswordMessage != null ? nullPasswordMessage : mensagem('mensagem.informe.login.senha'));
	    }
		plainPasswordField.focus();
	    focusSenha = true;
	    return false;
	  }
  }

  if (Senha != null && Senha != "") {
	  cryptedPasswordField.value = criptografaRSA(plainPasswordField.value);
	  plainPasswordField.value = '';
  }

  return true;
}

function FocusNome() {
  focusSenha = false;
  return true;
}

function FocusSenha() {
  f0.senha.focus();
  focusSenha = true;
  return true;
}

function reloadCaptcha() {
  var randomNumber = Math.floor(Math.random() * 1000);
  document['captcha_img'].src='../captcha.jpg?_=' + randomNumber;
  document.forms[0].captcha.focus();
}

function reloadSimpleCaptcha() {
  var randomNumber = Math.floor(Math.random() * 1000);

  if(QualNavegador() == 'IE'){
		document.getElementById('divCaptchaSound').innerHTML = '<object id="simpleAudio" width="300" height="20" classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" type="audio/wav" >'
			 + '<param name="src" value="../audio.wav?new=1&t='+randomNumber+'"/>'
			 + '<param name="controller" value="true"/>'
			 + '<param name="autostart" value="false"/></object>';  
  } else{
	  document.getElementById('simpleAudio').src='../audio.wav?new=1&t=' + randomNumber;
  }

  focaCaptcha();
}

function montaCaptchaSom(){
	var randomNumber = Math.floor(Math.random() * 1000);
	
	if (QualNavegador() == 'IE') {
		document.getElementById('divCaptchaSound').innerHTML = '<object id="simpleAudio" width="300" height="20" classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" type="audio/wav" >'
			 + '<param name="src" value="../audio.wav?t='+randomNumber+'"/>'
			 + '<param name="controller" value="true"/>'
			 + '<param name="autostart" value="false"/></object>';
	} else{
		document.getElementById('divCaptchaSound').innerHTML = '<audio id="simpleAudio" controls src="../audio.wav" onplay="focaCaptcha();">'
			 + '<source src="../audio.wav?t='+randomNumber+'" type="audio/wav">';
			 + mensagem('mensagem.erro.browser.sem.audio') + '</audio>';		
	}
}

function montaCaptchaSomSer(type){
    var randomNumber = Math.floor(Math.random() * 1000);

    if (QualNavegador() == 'IE') {
        document.getElementById('divCaptchaSound_' + type).innerHTML = '<object id="simpleAudio_'+type+'"  width="300" height="20" classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" type="audio/wav" >'
            + '<param name="src" value="../audio.wav?t='+randomNumber+'"/>'
            + '<param name="controller" value="true"/>'
            + '<param name="autostart" value="false"/></object>';
    } else{
        document.getElementById('divCaptchaSound_' + type).innerHTML = '<audio id="simpleAudio_'+type+'" controls src="../audio.wav">'
            + '<source src="../audio.wav?t='+randomNumber+'" type="audio/wav">';
        + mensagem('mensagem.erro.browser.sem.audio') + '</audio>';
    }
}

function focaCaptcha(){
    document.forms[0].captcha.focus();
}

function helpCaptcha() {
  window.open('../ajuda/captcha.jsp?tipo=1', null, 'height=250,width=400,status=no,toolbar=no,menubar=no,location=no,scrollbars=no,left=50,top=100');
}

function helpCaptcha2() {
  window.open('../ajuda/captcha.jsp?tipo=2', null, 'height=250,width=400,status=no,toolbar=no,menubar=no,location=no,scrollbars=no,left=50,top=100');
}

function helpCaptcha3() {
  window.open('../ajuda/captcha.jsp?tipo=3', null, 'height=250,width=400,status=no,toolbar=no,menubar=no,location=no,scrollbars=no,left=50,top=100');
}

function helpCaptcha4() {
  window.open('../ajuda/captcha.jsp?tipo=4', null, 'height=250,width=400,status=no,toolbar=no,menubar=no,location=no,scrollbars=no,left=50,top=100');
}

function helpCaptcha5() {
  window.open('../ajuda/captcha.jsp?tipo=5', null, 'height=250,width=400,status=no,toolbar=no,menubar=no,location=no,scrollbars=no,left=50,top=100');
}

function criptografaRSA(texto) {
  var rsa = new RSAKey();
  rsa.setPublic(chave_publica_modulo, chave_publica_expoente);
  var res = rsa.encrypt(texto);

  if(res) {
    return hex2b64(res);
  }
  
  return '';
}

function visualizarMargemSer(act, token, avancado, refresh){
    let vlr = "";
    if(avancado === 'false'){
       vlr = document.getElementById('captcha_' + act).value;
    } else {
        var gCaptchaResponse = null;
        if(act === 'topo'){
            gCaptchaResponse = document.getElementById('g-recaptcha-response').value;
            vlr = '""&g-recaptcha-response_' + act + '=' + encodeURIComponent(gCaptchaResponse);
        } else {
            gCaptchaResponse = document.getElementById('g-recaptcha-response-1').value;
            if (gCaptchaResponse === ''){
                gCaptchaResponse = document.getElementById('g-recaptcha-response').value;
            }
            vlr = '""&g-recaptcha-response_' + act + '=' + encodeURIComponent(gCaptchaResponse);
        }
    }
    var refreshh = atob(refresh).replace("codigoCapTopo", '').replace("codigoCapRenegociacao", '').replace("&validaCaptchaTopo=S", '').replace("validaCaptchaRenegociacao", '').replace("g-recaptcha-response_" + act, '').replace("codigoCapHistorico", '');
    if(act === 'principal') {
        postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true&validaCaptcha=S&codigoCap=' + vlr + '&' + atob(token));
    } else if (act === 'topo') {
        postData(refreshh + '&validaCaptchaTopo=S&codigoCapTopo=' + vlr );
    } else if (act === 'leilaoR') {
        postData('../v3/solicitarLeilao?acao=iniciarSimulacao&validaCaptchaSimular=S&codigoCapSimular=' + vlr + '&SVC_CODIGO='+ document.getElementById('SVC_CODIGO').value +'&RSE_CODIGO=' +  document.getElementById('RSE_CODIGO').value  + '&titulo=' +  document.getElementById('titulo').value  + '&' + atob(token));
    } else if (act === 'renegociacao') {
        postData(refreshh + '&validaCaptchaRenegociacao=S&codigoCapRenegociacao=' + vlr)
    } else if (act === 'reservar') {
        postData('../v3/reservarMargem?acao=reservarMargem&PORTAL_BENEFICIO=true&validaCaptchaReservar=S&codigoCapReservar=' + vlr +'&_skip_history_=true&RSE_CODIGO=' +  document.getElementById('RSE_CODIGO').value + '&CSA_CODIGO=' + document.getElementById('CSA_CODIGO').value + '&SVC_CODIGO=' + document.getElementById('SVC_CODIGO').value + '&' + atob(token));
    } else if (act === 'consultar') {
        postData('../v3/consultarConsignacao?acao=pesquisarConsignacao&validaCaptchaConsultar=S&codigoCapConsultar=' + vlr + '&_skip_history_=true&RSE_CODIGO=' +  document.getElementById('RSE_CODIGO').value + '&' + atob(token))
    } else if (act === 'historico') {
        postData(refreshh + '&validaCaptchaHistorico=S&codigoCapHistorico=' + vlr)
    } else if (act === 'simular') {
        postData('../v3/simularConsignacao?acao=iniciarSimulacao&validaCaptchaSimular=S&codigoCapSimular=' + vlr + '&SVC_CODIGO='+ document.getElementById('SVC_CODIGO').value + '&titulo=' +  document.getElementById('titulo').value + '&' + atob(token))
    } else if (act === 'portabilidadeCartao') {
        postData('../v3/solicitarPortabilidade?acao=solicitarPortabilidadeCartao&validaCaptchaPortabilidadeCartao=S&codigoCapPortabilidadeCartao=' + vlr + '&_skip_history_=true&RSE_CODIGO=' +  document.getElementById('RSE_CODIGO').value + '&CSA_CODIGO=' + document.getElementById('CSA_CODIGO').value + '&SVC_CODIGO=' + document.getElementById('SVC_CODIGO').value + '&' + atob(token));
    }
}
function reloadCaptchaSer(act) {
    var randomNumber = Math.floor(Math.random() * 1000);
    document['captcha_img_' + act].src='../captcha.jpg?_=' + randomNumber;
    document.getElementById('captcha_img_' + act).focus();
}

function reloadSimpleCaptchaSer(type) {
    var randomNumber = Math.floor(Math.random() * 1000);

    if (QualNavegador() == 'IE') {
        document.getElementById('divCaptchaSound_' + type).innerHTML = '<object id="simpleAudio_' + type + '" width="300" height="20" classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" type="audio/wav" >'
            + '<param name="src" value="../audio.wav?new=1&t=' + randomNumber + '"/>'
            + '<param name="controller" value="true"/>'
            + '<param name="autostart" value="false"/></object>';
    } else {
        document.getElementById('simpleAudio_' + type).src = '../audio.wav?new=1&t=' + randomNumber;
    }
}



