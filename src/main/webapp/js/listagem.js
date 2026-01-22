function preencheLista(listaValores,selectId) {
  var list = document.getElementById(listaValores);
  var selectComp = document.getElementById(selectId);
  
  if (list != null && selectComp != null) {
    var array_regs = list.value.split(";");

    for (var i = 0; i < array_regs.length; i++) {
      if (array_regs[i] != "") {
        var opt = new Option(array_regs[i], array_regs[i]);
        selectComp.options[selectComp.length] = opt;
      }
    }
  }
}

function insereItem(nomeCampoValor, nomeCampoLista) {
  var lista = document.getElementById(nomeCampoLista);
  var valor = document.getElementById(nomeCampoValor);

  if (valor.value != null && valor.value != '') {
    for (var i = 0; i < lista.length; i++) {
      if (lista.options[i].value == valor.value) {
        alert(mensagem('mensagem.erro.lista.valor.existe'));
        valor.focus();
        return;
      }
    }

    var opt = new Option(valor.value, valor.value);
    lista.options[lista.length] = opt;
    valor.value = '';
    valor.focus();
  }
}

function insereIp(tipoEndereco, valor, selectId, bloquearIpInterno) {
  var selectComp = document.getElementById(selectId);
  var valorData = document.getElementById(valor);
  var comprimento = (valorData.value ? valorData.value.length : 0);

  for (var i = 0; i < selectComp.length; i++) {
	comprimento += selectComp.options[i].value.length + 1;
    if (selectComp.options[i].value == valorData.value) {
      alert(mensagem('mensagem.erro.lista.endereco.existe'));
      valorData.focus();
      return;
    }
  }

  if (comprimento > 65000) {
  	alert(mensagem('mensagem.erro.lista.qtd.max.endereco'));
    valorData.focus();
    return;
  }

  if (verificaIp(tipoEndereco, valorData.value, bloquearIpInterno)) {
    var opt = new Option(valorData.value, valorData.value);

    selectComp.options[selectComp.length] = opt;
    valorData.value = '';
  }

   valorData.focus();
}

function removeDaLista(selectId) {
  var selectComp = document.getElementById(selectId);
  
  for (var i = selectComp.length - 1; i >= 0; i--) {
    if (selectComp.options[i].selected) {
      selectComp.options[i] = null;
    }
  }
}

function montaListaIps(listaResult, listaValores) {
  var list = document.getElementById(listaValores);
  var result = document.getElementById(listaResult);
  
  if (list != null && result != null) {
    result.value = '';
	
	for (var i = 0; i < list.length; i++) {
	  result.value += list.options[i].value + ';';
	}
	
	if (result.value.length > 0) {
	  result.value = result.value.substring(0, result.value.length - 1);
	}
  }
}

function copiaIp(nomeCampo, ip) {
  var campo = document.getElementById(nomeCampo);
  campo.value = ip;
  campo.focus();
}

function selecionarTodosItens(selectId) {
  var selectComp = document.getElementById(selectId);
    if (selectComp != null) {
    for (var i = selectComp.length - 1; i >= 0; i--) {
      selectComp.options[i].selected = true;
    }
  }
}