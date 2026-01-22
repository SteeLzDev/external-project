var tempoExpiracao = new Number();
var tempoInicialExpiracao = new Number();
var tempoInicial;
var timerHandle = null; 
//Tempo em segundos

function startCountdown(maxInactiveInterval){
  tempoExpiracao = maxInactiveInterval;
  tempoInicialExpiracao = maxInactiveInterval;
  tempoInicial = Date.now();
  if (timerHandle) {
    clearTimeout(timerHandle);
    timerHandle = 0;
  }
}

function continueCountdown(sessaoValida){
  // Se o tempo não for zerado
  if ((tempoExpiracao - 1) >= -1 && sessaoValida) {
    //Pega a parte inteira dos minutos
    var min = parseInt(tempoExpiracao/60);
    // Calcula os segundos restantes
    var seg = tempoExpiracao % 60;
    // Formata o número menor que dez, ex: 08, 07, ...
    if (min < 10) {
       min = "0" + min;
       min = min.substr(0, 2);
    }
    
    if (seg <=9) {
      seg = "0"+seg;
    }

    var clockField = document.getElementById("tempoExpiracao");
    if (!clockField && document.getElementById("topFrame") != undefined && document.getElementById("topFrame") != null) {
      clockField = document.getElementById("topFrame").contentWindow.document.getElementById("tempoExpiracao");
    }
    if (!clockField) {
	  clockField = $("span.tempoExpiracao");
	}
    if (!clockField) {
      return;
    }
    
    //Define que a função será executada novamente em 1000ms = 1 segundo
    timerHandle = setTimeout('continueCountdown('+sessaoValida+')',1000);

    if (clockField.length == undefined) {
    	changeClockField(clockField, min, seg);
    } else {
      clockField.each(function( index ) {
    	changeClockField(this, min, seg);
      });
    }
  }
}

function changeClockField(clockField, min, seg) {
    clockField.innerHTML = min + ':' + seg;
    // diminui o tempo
    var now = Date.now();
    var delta = Math.floor((now - tempoInicial)/1000);
    tempoExpiracao = tempoInicialExpiracao - delta;
}