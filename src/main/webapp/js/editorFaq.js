function validaCampos() {
  var Controles = new Array("faqTitulo1", "faqTitulo2");
  var Msgs = new Array(mensagem('mensagem.informe.faq.titulo1'),
                       mensagem('mensagem.informe.faq.titulo2'));
  return ValidaCampos(Controles, Msgs);
}

function validaCampoConteudo () {
	
	var valor = document.getElementById('uedit_textarea').value;
	
	if (valor && valor.trim() != '') {
		return true;
	}
	
	alert(mensagem('mensagem.informe.faq.conteudo'));
	return false;
}

function checkTodos() {
  if (f0.faqCheckTodos.checked) {
    if (!f0.faqExibeCse.checked) { 
      f0.faqExibeCse.checked=true;
    }
    if (!f0.faqExibeCor.checked) { 
      f0.faqExibeCor.checked=true;    
    }
    if (!f0.faqExibeCsa.checked) { 
      f0.faqExibeCsa.checked=true;      
    }
    if (!f0.faqExibeSer.checked) { 
      f0.faqExibeSer.checked=true;
    }
    if (!f0.faqExibeOrg.checked) { 
      f0.faqExibeOrg.checked=true;
    }
    if (!f0.faqExibeSup.checked) { 
      f0.faqExibeSup.checked=true;
    }
  } else {
    if (f0.faqExibeCse.checked) { 
      f0.faqExibeCse.checked=false;
    }
    if (f0.faqExibeCor.checked) { 
      f0.faqExibeCor.checked=false;
    }
    if (f0.faqExibeCsa.checked) { 
      f0.faqExibeCsa.checked=false;
    }
    if (f0.faqExibeSer.checked) { 
      f0.faqExibeSer.checked=false;
    }
    if (f0.faqExibeOrg.checked) { 
      f0.faqExibeOrg.checked=false;
    }
    if (f0.faqExibeSup.checked) { 
      f0.faqExibeSup.checked=false;
    }
  }  
}