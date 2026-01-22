/*
 * <p>Title: jquery-nostrum.js</p>
 * <p>Description: Extensões criadas pela Nostrum para o jQuery.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
function nPrompt(text, label, defaultValue, fnSubmit, fnCallback) {
	nPrompt(text, label, defaultValue, fnSubmit, fnCallback, 1);
}

function nPrompt(text, label, defaultValue, fnSubmit, fnCallback, rows) {
  if ((defaultValue == null) || (defaultValue == undefined)) {
    defaultValue = '';
  }
  if ((rows == null) || (rows == undefined)) {
    rows = 1;
  }
  var btn = JSON.parse('{"' + mensagem('rotulo.botao.confirmar') + '":true, "' + mensagem('rotulo.botao.cancelar') + '":false}');
  var txt = text + '<div class="field"><br><label for="editfield">'+label+'</label><textarea id="editfield" name="editfield" rows="'+ rows +'" cols="70">' + defaultValue + '</textarea></div><br>';
  $.prompt(txt, { 
    buttons: btn,
    submit: function(v, m) {
      return fnSubmit(v, m);
    },
    callback: function(v, m) {
      fnCallback(v, m);
    }
  });
}

function nConfirm(text, fnSubmit, fnCallback) {
  nConfirm(text, fnSubmit, fnCallback, mensagem('rotulo.botao.confirmar'), mensagem('rotulo.botao.cancelar'));
}

function nConfirm(text, fnSubmit, fnCallback, labelTrue, labelFalse) {
  var btn = JSON.parse('{"' + labelTrue + '":true, "' + labelFalse + '":false}');
  $.prompt(text, {
    buttons: btn,
    submit: function(v, m) {
      return fnSubmit(v, m);
    },
    callback: function(v, m) {
      fnCallback(v, m);
    }
  });
}

function postData(href, target) {
  if(window.console) window.console.clear
  
  var hrefCodificado;
  var firstQt = href.indexOf('?');
  var p;
  if (firstQt > 0) {
	  // antes de fazer o split por '?' codifica as interroga��es que porventura
	  // fa�am parte do valor de algum par�metro
	  var prefixHref = href.substr(0, (firstQt + 1));
	  var suffixHref = href.substr((firstQt + 1), href.length);
	  var suffixHrefEncoded = encodeURIComponent(suffixHref);
	  hrefCodificado = prefixHref.concat(suffixHrefEncoded);
	  
	  p = hrefCodificado.split('?');
	  
	  for (var i = 0; i < p.length; i++) {
		  p[i] = decodeURIComponent(p[i]);
	  }
  } else {
	  p = href.split('?');
  }  

  var action = p[0];

  var params;
  if (p.length > 1) {
	  params = p[1].split('&');
  }

  var frame;
  try {
	  if (typeof target != 'undefined') {
		  if (typeof this.parent[target] != 'undefined') {
			  frame = this.parent[target];
		  } else if (typeof this.parent.acessoMainFrame != 'undefined') {
			  frame = this.parent.acessoMainFrame[target];
		  }
	  } else {
		  if (typeof this.parent.acessoMainFrame != 'undefined') {
			  frame = this.parent.acessoMainFrame;
		  } else if (typeof this.parent.mainFrame != 'undefined') {
			  frame = this.parent.mainFrame;
		  } 
	  }
  }	catch(err) {
	  frame = window.frames[target];
  }

  if (typeof frame == 'undefined') {
	  frame = this
  }

  var form = frame.document.createElement('form');
  form.setAttribute('action', action);
  form.setAttribute('method', 'post');
  if (typeof target != 'undefined') form.setAttribute('target', target);
  
  try {
	  frame.document.getElementsByTagName("body")[0].appendChild(form);
  }	catch(err) {}
  
  jQuery(frame.document.createElement('input')).attr('type', 'hidden').attr('name', 'dummy').attr('id', 'dummy').attr('value', 'bugie11').appendTo(form);
  for (var i = 0; i < (typeof params != 'undefined' ? params.length : 0); i++) {
    var tmp = params[i].split('=');
    var key = (tmp.length > 0 ? tmp[0] : '');
    var value = (tmp.length > 1 ? tmp[1] : '');
    try {
      value = decodeURIComponent(value);
    } catch(err) {}
    if (key) {
      jQuery(frame.document.createElement('input')).attr('type', 'hidden').attr('name', key).attr('id', key).attr('value', value).appendTo(form);
    }
  }

  form.submit();
  return false;
}

function nDialog(fnConfirmar, fnCancelar) {
	var btnConfirmar = mensagem('rotulo.botao.confirmar');
	var btnCancelar = mensagem('rotulo.botao.cancelar');
	var dialog;
	
	dialog = $("#dialog").dialog({
	    autoOpen: false,
	    modal: true,
	    resizable: false,
        width:'auto',
	    position: ['center', 'top'],
	    buttons: {
	    	"Confirmar": {"text": btnConfirmar, "click": confirmar, "class":"ui-button-spl1"},  
	        "Cancelar": {"text": btnCancelar, "click": cancelar, "class":"ui-button-spl1"}
	    },
	    open:  function() {
	        $('.ui-widget-overlay').addClass('custom-overlay');
	    },
	    close: function() {
	        $('.ui-widget-overlay').removeClass('custom-overlay');
	    }      
	});
	
	dialog.dialog('open');
	
	function cancelar() {
		eval(fnCancelar);
		dialog.dialog('close');
	}

	function confirmar() {
		if (eval(fnConfirmar)) {
			dialog.dialog('close');    	
		}
	}
}

function toogleContrast() {
    if ($("body").hasClass("contraste")) {
        $("*").removeClass("contraste");
        Cookies.remove("contraste");
    } else {
        $("*").addClass("contraste");
        Cookies.set("contraste", true);
    }
}

function toogleBold() {
    if ($("body").hasClass("bold-v4")) {
        $("*").removeClass("bold-v4");
        Cookies.remove("bold-v4");
    } else {
        $("*").addClass("bold-v4");
        $("i").removeClass("bold-v4");
        Cookies.set("bold-v4", true);
    }
}

function changeFontSize(amount) {
    // Transforma tudo em px antes de aumentar, pois as fontes s�o em tamanho relativo, 
    // e aumentar durante o each debaixo ir� gerar aumento inconsistente das fontes
    if (amount != 0) {
        changeFontSize(0);
    }
    $("*").each(function(elt) {
        var fontSize = parseFloat($(this).css("font-size").split('px')[0]) + amount;
        $(this).css({'font-size' : fontSize + "px"});
    });
}

function toggleMenuAcessibilidadeV4Side() {
  let menuAcessibilidade = document.getElementById('menuAcessibilidade');
  let opcoesAcessibilidadeMoverIcon = document.querySelector('#opcaoAcessibilidadeMover i');

  if(menuAcessibilidade != null && opcoesAcessibilidadeMoverIcon != null) {
    if(menuAcessibilidade.classList.contains('left')) {
      menuAcessibilidade.classList.remove('left');
      menuAcessibilidade.classList.add('right');

      opcoesAcessibilidadeMoverIcon.classList.remove('fa-long-arrow-right');
      opcoesAcessibilidadeMoverIcon.classList.add('fa-long-arrow-left');

      localStorage.setItem('menu-acessibilidade-side', 'right');
    }
    else if(menuAcessibilidade.classList.contains('right')) {
      menuAcessibilidade.classList.remove('right');
      menuAcessibilidade.classList.add('left');

      opcoesAcessibilidadeMoverIcon.classList.remove('fa-long-arrow-left');
      opcoesAcessibilidadeMoverIcon.classList.add('fa-long-arrow-right');

      localStorage.setItem('menu-acessibilidade-side', 'left');
    }
  }
}

/* 
  Pega todos os elementos da página exceto aqueles que
  devem ter seu tamanho de fonte relativo mantido
*/
function getResizeableElements() {
  function difference(firstArray, secondArray) {
    return firstArray.filter(function(e) { return secondArray.indexOf(e) === -1 });
  }

  // Converte elementos NodeList para Array
  let pageElements = Array.prototype.slice.call(document.querySelectorAll('*'));
  let iconsAndRelativelySizedFonts = Array.prototype.slice.call(document.querySelectorAll('#menuAcessibilidade i, #opcoesAcessibilidadeV4 a span, #opcoesAcessibilidadeV4 a i, i.fa, svg'));
  
  return difference(pageElements, iconsAndRelativelySizedFonts);
}

function toggleContrastv4() {
  localStorage.getItem('contraste-v4') ? localStorage.removeItem('contraste-v4') : localStorage.setItem('contraste-v4', 'true');

  let allElements = Array.prototype.slice.call(document.querySelectorAll('*'));
  for(let i = 0; i < allElements.length; i++) {
    let elem = allElements[i];

    // classList não é suportada em elementos svg no Internet Explorer
    if(elem.tagName.toLowerCase() === 'svg' && elem.classList === null) {
      if(localStorage.getItem('contraste-v4') !== null)
        elem.className += ' contraste-v4';
      else
        elem.className.replace(' contraste-v4', '');
    } else elem.classList.toggle('contraste-v4');
  }

  // Alguns elementos mantém a classe mesmo depois do toggle, quando muda de página
  // O trecho abaixo remove explicitamente
  let elementsWithContrast = Array.prototype.slice.call(document.getElementsByClassName('contraste-v4'));
  if(!localStorage.getItem('contraste-v4') && elementsWithContrast.length > 0) {
    for(let i = 0; i < elementsWithContrast.length; i++) {
      let ewc = elementsWithContrast[i];
      ewc.classList.remove('contraste-v4');
    }
  }
}

function resizeSvgIcons(amount) {
  let svgIcons = Array.prototype.slice.call(document.getElementsByTagName('svg'));

  for(let i = 0; i < svgIcons.length; i++) {
    let icon = svgIcons[i];
    let x = Number(getComputedStyle(icon)['x'].split('px')[0]);
    let y = Number(getComputedStyle(icon)['y'].split('px')[0]);
    let width = Number(getComputedStyle(icon)['width'].split('px')[0]);
    let height = Number(getComputedStyle(icon)['height'].split('px')[0]);

    if(!isNaN(x) && !isNaN(y) && !isNaN(width) && !isNaN(height)) {
      if(!icon.hasAttribute('viewBox'))
        icon.setAttribute('viewBox', x + " " + y + " " + width + " " + height)
      if(!icon.hasAttribute('preserveAspectRatio'))
        icon.setAttribute('preserveAspectRatio', 'xMidYMid meet')

      let newWidth;
      const containerFavoritos = document.getElementById('containerFavoritos');
      const mainMenu = document.getElementsByClassName('main-menu')[0];

      // Mudança de tamanho para manter a proporção é diferente para os ícones que
      // que estão no Menu de Favoritos
      if(containerFavoritos !== null && containerFavoritos.contains(icon))
        newWidth = width + 4 * amount;
      else newWidth = width + 1.75 * amount;

      if(!mainMenu.contains(icon))
        icon.style.height = 'auto'

      icon.setAttribute('width', newWidth);
    }
  }
}

function changeFontSizev4(amount) {
  if(typeof amount != 'number') return;

  if(amount != 0) changeFontSizev4(0); // converte a medida de font-size para pixels

  let elementsToResize = getResizeableElements();

  for(let i = 0; i < elementsToResize.length; i++) {
    let elem = elementsToResize[i];
    const currentFontSize = parseFloat(getComputedStyle(elem)['font-size'].split('px')[0]);
    const newFontSize = currentFontSize + amount;
    elem.style.fontSize = newFontSize + "px";
  }

  if(amount != 0) resizeSvgIcons(amount);
  
  // A quantidade de pixels que o tamanho das fontes mudou em relação ao default
  const previousTotalChange = parseFloat(localStorage.getItem('total-font-size-change')) || 0;
  const newTotalChange = amount + previousTotalChange;

  if(newTotalChange === 0) localStorage.removeItem('total-font-size-change');
  else localStorage.setItem('total-font-size-change', newTotalChange.toString());

  let cardHeadersWithIcon = Array.prototype.slice.call(document.querySelectorAll('.card-header.hasIcon'));

  for(let i = 0; i < cardHeadersWithIcon.length; i++) {
    let header = cardHeadersWithIcon[i];
    if(newTotalChange === 0) {
      header.style.paddingLeft = null;
    } else {
      let currentPaddingLeft = parseFloat(getComputedStyle(header)['padding-left'].split('px')[0]);
      let newPaddingLeft = currentPaddingLeft;
      
      if(amount < 0)
        newPaddingLeft -= amount * 1.85;
      else if(amount > 0)
        newPaddingLeft += amount * 1.85;

      header.style.paddingLeft = newPaddingLeft + "px";
    }
  }
}

function checaAcessibilidadev4() {

  function contrastColors() { 
    let allElements = Array.prototype.slice.call(document.querySelectorAll('*'));
    for(let i = 0; i < allElements.length; i++) {
      let elem = allElements[i];
      elem.classList.add('contraste-v4');

      // classList não é suportada em elementos svg no Internet Explorer
      if(elem.tagName.toLowerCase() === 'svg' && elem.classList === null) {
          elem.className += ' contraste-v4';
      }
    }
  }

  function adjustFontSize() {
    changeFontSize(0); // converte a medida de font-size para pixels

    // Se possui alteração no tamanho (em relação ao padrão), será o valor da alteração, se não será NaN
    let totalChangeFontSize = parseFloat(localStorage.getItem('total-font-size-change'));

    let elementsToResize = getResizeableElements();

    if(totalChangeFontSize) {
      for(let i = 0; i < elementsToResize.length; i++) {
        let elem = elementsToResize[i];
        let defaultSizeOfElement = parseFloat(getComputedStyle(elem)['font-size'].split('px')[0]);
        elem.style.fontSize = (defaultSizeOfElement + totalChangeFontSize) + "px";
      }
    }
  }

  function setMenuSide() {
    let menuAcessibilidade = document.getElementById('menuAcessibilidade');
    let opcoesAcessibilidadeMoverIcon = document.querySelector('#opcaoAcessibilidadeMover i');

    if(menuAcessibilidade != null && opcoesAcessibilidadeMoverIcon != null) {
      if(localStorage.getItem('menu-acessibilidade-side') === 'left') {
        menuAcessibilidade.classList.remove('right');
        menuAcessibilidade.classList.add('left');
  
        opcoesAcessibilidadeMoverIcon.classList.remove('fa-long-arrow-left');
        opcoesAcessibilidadeMoverIcon.classList.add('fa-long-arrow-right');
      } else if(localStorage.getItem('menu-acessibilidade-side') === 'right') {
        menuAcessibilidade.classList.remove('left');
        menuAcessibilidade.classList.add('right');
  
        opcoesAcessibilidadeMoverIcon.classList.remove('fa-long-arrow-right');
        opcoesAcessibilidadeMoverIcon.classList.add('fa-long-arrow-left');
      }
    }
  }
  
  // Cores
  if(localStorage.getItem('contraste-v4') !== null)
    contrastColors();

  // Tamanho da fonte
  if(localStorage.getItem('total-font-size-change') !== null) {
    adjustFontSize();
    resizeSvgIcons(Number(localStorage.getItem('total-font-size-change')));
  }

  // Por padrão, o lado da tela que o menu fica é o direito
  if(localStorage.getItem('menu-acessibilidade-side') === null)
    localStorage.setItem('menu-acessibilidade-side', 'right');

  // Lado da tela
  setMenuSide();
}

// V4 - Ao mudar de página, continua no mesmo modo 
// Não sobreescreve o window.load da(s) página(s)
window.addEventListener('load', checaAcessibilidadev4, false);