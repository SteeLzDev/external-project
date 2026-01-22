function BloquearUsuario(url, status, usuario, codigoEntidade, desc, redireciona, token_url) {  
  var msg = "";
  
  if (!redireciona) {
    if (status == "1") {
      msg = mensagem('mensagem.confirmacao.bloqueio.usuario').replace('{0}', desc);
    } else {
      msg = mensagem('mensagem.confirmacao.desbloqueio.usuario').replace('{0}', desc);
    }
    url += "?acao=bloquear";
  } else {
    url += "?acao=efetivarAcao";
  }

  if (msg == "" || confirm(msg)) {
	  url += "&USU_CODIGO=" + usuario + "&codigo=" + codigoEntidade + "&STATUS=" + status + "&eConsig.page.token=" + token_url;
	  postData(url);
  }
}

function ExcluirUsuario(url, usuario, desc, codigoEntidade, redireciona, token_url) {
  var msg = mensagem('mensagem.confirmacao.exclusao.usuario').replace('{0}', desc);
  if (redireciona) {
    msg = "";
    url += "?acao=efetivarAcao";
  } else {
    url += "?acao=excluir";
  }

  if (msg == "" || confirm(msg)) {
	  url += "&USU_CODIGO=" + usuario + "&codigo=" + codigoEntidade + "&eConsig.page.token=" + token_url;
	  postData(url);
  }
}

function ReiniciarSenha(url, usuario, codigoEntidade, login, desc, redireciona, token_url) {
  var msg = mensagem('mensagem.confirmacao.reinicializar.senha.usuario').replace('{0}', desc);
  if (redireciona) {
    msg = "";
    url += "?acao=efetivarAcao";
  } else {
    url += "?acao=reinicializar";
  }

  if (msg == "" || confirm(msg)) {
	  url += "&USU_CODIGO=" + usuario + "&codigo=" + codigoEntidade + "&USU_LOGIN=" + login + "&eConsig.page.token=" + token_url;
	  postData(url);
  }
}

function detalheUsuario(url, codigo, token_url) {
  url += "?acao=iniciar&USU_CODIGO=" + codigo + "&eConsig.page.token=" + token_url;
  postData(url);
}
