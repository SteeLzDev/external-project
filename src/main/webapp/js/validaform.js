// Verifica formul�rio de pesquisa de servidor
function vf_pesquisa_servidor(requerAmbos, tipo) {
  var Controles = new Array("CSA_CODIGO", "SVC_CODIGO", "PLA_CODIGO", "RSE_MATRICULA", "SER_CPF");
  var Msgs = new Array(mensagem('mensagem.informe.consignataria'),
                       mensagem('mensagem.informe.servico'),
                       mensagem('mensagem.informe.plano'),
                       mensagem('mensagem.informe.matricula'),
                       mensagem('mensagem.informe.cpf'));

  if (f0.ADE_NUMERO != null) {
    if ((tipo == 'renegociar' || tipo == 'comprar') && f0.SVC_CODIGO.value == '') {
        ValidaCampos(Controles, Msgs);
        return false;
    }
    if (f0.ADE_NUMERO.value != '') {
      return true;
    } else if (f0.ADE_NUMERO_LIST != null && f0.ADE_NUMERO_LIST.length > 0) {
      return true;
    } else if ((f0.RSE_MATRICULA == null || f0.RSE_MATRICULA.value == '') && (f0.SER_CPF == null || f0.SER_CPF.value == '')) {
      f0.ADE_NUMERO.focus();
      alert(mensagem('mensagem.informe.campo'));
      return false;
    }
  }

  if (requerAmbos) {
    if (!ValidaCampos(Controles, Msgs)) {
      return false;
    } else if (f0.SER_CPF != null && !CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value))) {
      f0.SER_CPF.focus();
      return false;
    } else {
      return true;
    }
  } else {
    Controles = new Array("CSA_CODIGO", "SVC_CODIGO", "PLA_CODIGO");
    Msgs = new Array(mensagem('mensagem.informe.consignataria'),
                     mensagem('mensagem.informe.servico'),
                     mensagem('mensagem.informe.plano'));
    if (tipo != 'extrato_consolidado' && !ValidaCampos(Controles, Msgs)) {
      return false;
    } else if ((f0.RSE_MATRICULA == null || f0.RSE_MATRICULA.value == '') &&
               (f0.SER_CPF == null || f0.SER_CPF.value == '')) {
      if (f0.RSE_MATRICULA != null) {
        f0.RSE_MATRICULA.focus();
        alert(mensagem('mensagem.informe.campo'));
        return false;
      } else if (f0.SER_CPF != null) {
        f0.SER_CPF.focus();
        alert(mensagem('mensagem.informe.campo'));
        return false;
      } else {
   	    return true;
      }
    } else if ((f0.SER_CPF != null) && (f0.SER_CPF.value != '') &&
               (!CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value)))) {
      f0.SER_CPF.focus();
      return false;
    }
    return true;
  }
}

//
function vf_pesquisa_retorno(requerAmbos) {
    var Controles = new Array("ADE_NUMERO", "RSE_MATRICULA", "SER_CPF");
    var Msgs = new Array (mensagem('mensagem.informe.ade.numero'),
        mensagem('mensagem.informe.matricula'),
        mensagem('mensagem.informe.cpf'));
    if (requerAmbos) {
        if (!ValidaCampos(Controles, Msgs)) {
            return false;
        } else if (!CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value))){
            f0.SER_CPF.focus();
            return false;
        } else {
            return true;
        }
    } else {
        if ((f0.ADE_NUMERO != null) && (f0.ADE_NUMERO.value == '') &&
            (f0.RSE_MATRICULA != null) && (f0.RSE_MATRICULA.value == '') &&
            (f0.SER_CPF != null) && (f0.SER_CPF.value == '')) {

            if (f0.ADE_NUMERO != null) {
                f0.ADE_NUMERO.focus();
            }
        } else if ((f0.SER_CPF != null) && (f0.SER_CPF.value != '') &&
            (!CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value)))) {
            f0.SER_CPF.focus();
            return false;
        }
        return true;
    }
}

// Verifica os dados do formul�rio de reserva de margem
function vf_reservar_margem(validaMargem, permiteVlrNegativo) {
  if (validaMargem == null || validaMargem == undefined || validaMargem == '') {
    validaMargem = 'true';
  }
  if (permiteVlrNegativo == null || permiteVlrNegativo == undefined || permiteVlrNegativo == '') {
    permiteVlrNegativo = false;
  }

  var Controles;
  var Msgs;
  var comPrazo = ((f0.adeSemPrazo == null) || (!f0.adeSemPrazo.checked));
  var campo = "adePrazo";
  if (f0.adePrz != null) {
    campo = "adePrz";
  }
  if (comPrazo) {
    if (QualNavegador() == 'NE')
      var campoPrazo = "adePrazo";
    else
      var campoPrazo = (f0.adePrazoAux != null) ? "adePrazoAux" : campo;
    Controles = new Array("adeVlr", campoPrazo, "adeCarencia");
    Msgs = new Array(mensagem('mensagem.informe.ade.valor'),
                     mensagem('mensagem.informe.ade.prazo'),
                     mensagem('mensagem.informe.ade.carencia'));
  } else {
    Controles = new Array("adeVlr", "adeCarencia");
    Msgs = new Array(mensagem('mensagem.informe.ade.valor'),
                     mensagem('mensagem.informe.ade.carencia'));
  }
  var adeVlr = parseFloat(parse_num(f0.adeVlr.value));
  var vlrLimite = 0;
  if (f0.vlrLimite != null) {
    vlrLimite = parseFloat(parse_num(f0.vlrLimite.value));
  } else if (window.vlrLimite != null) {
    vlrLimite = parseFloat(vlrLimite);
  }

  var adeIncMargem = '1';
  if (f0.adeIncMargem != null) {
    adeIncMargem  = f0.adeIncMargem.value;
  } else if (window.arIncMargem != null) {
    adeIncMargem = arIncMargem;
  }

  var margemConsignavel = null;
  if (window.arMargemConsignavel != null) {
    margemConsignavel = parseFloat(arMargemConsignavel != null ? arMargemConsignavel : '0');
  } else if (f0.rseMargemRest != null) {
    margemConsignavel = parseFloat(f0.rseMargemRest.value);
  }

  // Verifica se h� limite de teto quando o sistema controla o saldo devedor.
  var valorReservaControleSaldoDevedor = adeVlr;
  if (f0.vlrMaxParcelaSaldoDevedor != null) {
    maxParcelaSaldoDevedor = parseFloat(f0.vlrMaxParcelaSaldoDevedor.value);
    if (maxParcelaSaldoDevedor < valorReservaControleSaldoDevedor) {
      valorReservaControleSaldoDevedor = maxParcelaSaldoDevedor;
    }
  }

  if (margemConsignavel != null &&
      (adeIncMargem != null && adeIncMargem != '' && adeIncMargem != '0')) {

    var vlrTesteMargem;
    if (f0.vlrMaxParcelaSaldoDevedor != null) {
      vlrTesteMargem = valorReservaControleSaldoDevedor - vlrLimite;
    } else {
      vlrTesteMargem = adeVlr - vlrLimite;
    }

    if ((validaMargem == 'true') && (margemConsignavel - vlrTesteMargem < 0)) {
      alert(mensagem('mensagem.erro.valor.parcela.maior.margem'));
      if (f0.adeVlr != null && !f0.adeVlr.disabled) {
        f0.adeVlr.focus();
      }
      return false;
    }
  }

  if (typeof identificadorObrigatorio != 'undefined' && identificadorObrigatorio == true) {
	  // Caso o identificador seja obrigat�rio, adiciona ao array de campos a serem validados
	  Controles.push("adeIdentificador");
	  Msgs.push(mensagem('mensagem.informe.ade.identificador'));
  }

  if (!ValidaCampos(Controles, Msgs)) {
    return false;
  } else {
    if (isNaN(adeVlr)) {
      alert(mensagem('mensagem.erro.valor.parcela.incorreto'));
      if (f0.adeVlr != null && !f0.adeVlr.disabled) {
        f0.adeVlr.focus();
      }
      return false;
    } else if (parseFloat(adeVlr) < 0.0 && !permiteVlrNegativo) {
      alert(mensagem('mensagem.erro.valor.parcela.negativo'));
      if (f0.adeVlr != null && !f0.adeVlr.disabled) {
        f0.adeVlr.focus();
      }
      return false;
    } else {

      if (comPrazo) {
        if (f0.adePrazo != null) {
          if (f0.adePrazo.type=='select-one')
            var adePrazo = parseInt(f0.adePrazo.options[f0.adePrazo.selectedIndex].value);
          else
            var adePrazo = parseInt(f0.adePrazo.value);

          if (isNaN(adePrazo) || (adePrazo <= 0)) {
            alert(mensagem('mensagem.erro.prazo.negativo'));

            if (campoPrazo == 'adePrazoAux')
              f0.adePrazoAux.focus();
            else if (campoPrazo == 'adePrz')
              f0.adePrz.focus();
            else if (campoPrazo == 'adePrazo' && f0.adePrazo.type != 'hidden')
              f0.adePrazo.focus();

            return false;
          }
        }
      }
      if (f0.adeCarencia != null) {
        if (f0.adeCarencia.value == '') {
          f0.adeCarencia.value = '0';
        }
        var adeCarencia = parseInt(f0.adeCarencia.value);
        if (isNaN(adeCarencia) || (adeCarencia < 0)) {
          alert(mensagem('mensagem.erro.carencia.incorreta'));
          if (f0.adeCarencia.type != 'hidden') {
            if (f0.adeCarencia !=null) {
              f0.adeCarencia.focus();
            }
          }
          return false;
        }
      }
      return true;
    }
  }
}

//
function verificaPrazo(prazoAVerificar) {
  var comPrazo = ((f0.adeSemPrazo == null) || (!f0.adeSemPrazo.checked));
  var quinzenal = (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null && f0.adePeriodicidade.value == 'Q');
  var quatorzenal = (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null && f0.adePeriodicidade.value == 'G');
  var semanal = (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null && f0.adePeriodicidade.value == 'S');
  var mensal = !quinzenal && !quatorzenal && !semanal;
  var p = 0;
  var prazo = '';
  // se n�o foi informado um valor no par�metro, tenta verificar os campos "padr�es"
  // comumente utilizados para prazo nos formul�rios.
  if (prazoAVerificar == undefined) {
    if (f0.adePrz != null) {
       prazo = f0.adePrz.value;
       p = parseInt(f0.adePrz.value);
    } else if (f0.adePrazo != null) {
       prazo = f0.adePrazo.value;
      p = parseInt(f0.adePrazo.value);
    }
  } else {
	  prazo = prazoAVerificar.value;
      p = parseInt(prazoAVerificar.value);
  }
  if (!isNaN(p) && !mensal) {
    var qtdPeriodos = 12;
    if (quinzenal) {
      qtdPeriodos = 24;
    } else if (quatorzenal) {
      qtdPeriodos = 26;
    } else if (semanal) {
      qtdPeriodos = 52;
    }
    p = Math.round(p * 12 / qtdPeriodos);
  }
  if ((comPrazo) && (maxPrazo > 0) && (prazo != '')) {
    if (isNaN(p)) {
      alert(mensagem('mensagem.erro.prazo.incorreto'));
      if (prazoAVerificar) {
    	  prazoAVerificar.value = '';
      }
      return false;
    } else if ((p > maxPrazo) && !permitePrazoMaiorContSer) {
      alert(mensagem('mensagem.erro.prazo.maior.svc').replace("{0}", maxPrazo));
      if (prazoAVerificar) {
    	  prazoAVerificar.value = '';
      }
      return false;
    }
  }
  var rp;
  if (f0.rsePrazo !=null) {
   rp = parseInt(f0.rsePrazo.value);
  }
  if (!isNaN(rp)) {
    if ((p > rp) && !permitePrazoMaiorContSer) {
      alert(mensagem('mensagem.erro.prazo.maior.ser').replace("{0}", rp));
      if (prazoAVerificar) {
    	  prazoAVerificar.value = '';
      }
      return false;
    }
  }
  return true;
}

// Verifica formul�rios de inser��o e edi��o de estabelecimentos
function vf_cadastro_est() {
  var Controles = new Array("EST_IDENTIFICADOR", "EST_NOME", "EST_CNPJ");
  var Msgs = new Array(mensagem('mensagem.informe.est.identificador'),
                       mensagem('mensagem.informe.est.nome'),
                       mensagem('mensagem.informe.est.cnpj'));
  if (!ValidaCampos(Controles, Msgs)) {
    return false;
  }
  if ( (f0.EST_CNPJ.value != '') && (f0.EST_CNPJ.value != null) ) {
    return CGC_OK(extraiNumCNPJCPF (f0.EST_CNPJ.value));
  } else {
    return true;
  }
}

// Verifica formul�rios de inser��o e edi��o de coeficientes de corre��o
function vf_cadastro_ccr() {
  var Controles = new Array("ccrDescricao", "ccrVlr0", "ccrMes0", "ccrAno0");
  var Msgs = new Array(mensagem('mensagem.informe.ccr.descricao'),
                       mensagem('mensagem.informe.ccr.valor'),
                       mensagem('mensagem.informe.ccr.mes'),
                       mensagem('mensagem.informe.ccr.ano'));

  return ValidaCampos(Controles, Msgs);
}

// Verifica formul�rios de inser��o e edi��o de consignatarias
function vf_cadastro_csa() {
  var Controles = new Array("CSA_IDENTIFICADOR", "CSA_NOME", "NCA_CODIGO");
  var Msgs = new Array(mensagem('mensagem.informe.csa.identificador'),
                       mensagem('mensagem.informe.csa.nome'),
                       mensagem('mensagem.informe.csa.natureza'));
  if (!ValidaCampos(Controles, Msgs)) {
    return false;
  }
  if ((f0.CSA_CNPJ != undefined) && (f0.CSA_CNPJ.value != '') && (f0.CSA_CNPJ.value != null) &&
      (!CGC_OK(extraiNumCNPJCPF(f0.CSA_CNPJ.value)))) {
    f0.CSA_CNPJ.focus();
    return false;
  }
  if ((f0.CSA_CNPJ_CTA != undefined) && (f0.CSA_CNPJ_CTA.value != '') && (f0.CSA_CNPJ_CTA.value != null) &&
      (!CGC_OK(extraiNumCNPJCPF(f0.CSA_CNPJ_CTA.value)))) {
    f0.CSA_CNPJ_CTA.focus();
    return false;
  }
  if ((f0.CSA_DATA_EXPIRACAO != undefined) && ((f0.CSA_DATA_EXPIRACAO.value != '') && (f0.CSA_DATA_EXPIRACAO.value != null) &&
      (!verificaData(f0.CSA_DATA_EXPIRACAO.value)))) {
    f0.CSA_DATA_EXPIRACAO.focus();
    return false;
  }
  if ((f0.CSA_EMAIL != undefined) && (f0.CSA_EMAIL.value != null && f0.CSA_EMAIL.value != '' && !isEmailValid(f0.CSA_EMAIL.value))) {
    alert(mensagem('mensagem.erro.email.csa.invalido'));
    f0.CSA_EMAIL.focus();
    return false;
  }

  return true;
}

function vf_cadastro_csa_v4() {
	 var Controles = new Array("CSA_IDENTIFICADOR", "CSA_NOME", "NCA_CODIGO");
	  var Msgs = new Array(mensagem('mensagem.informe.csa.identificador'),
	                       mensagem('mensagem.informe.csa.nome'),
	                       mensagem('mensagem.informe.csa.natureza'));
	  if (!ValidaCamposV4(Controles, Msgs)) {
	    return false;
	  }
	  if ((f0.CSA_CNPJ != undefined) && (f0.CSA_CNPJ.value != '') && (f0.CSA_CNPJ.value != null) &&
	      (!CGC_OK(extraiNumCNPJCPF(f0.CSA_CNPJ.value)))) {
	    f0.CSA_CNPJ.focus();
	    return false;
	  }
	  if ((f0.CSA_CNPJ_CTA != undefined) && (f0.CSA_CNPJ_CTA.value != '') && (f0.CSA_CNPJ_CTA.value != null) &&
	      (!CGC_OK(extraiNumCNPJCPF(f0.CSA_CNPJ_CTA.value)))) {
	    f0.CSA_CNPJ_CTA.focus();
	    return false;
	  }
	  if ((f0.CSA_DATA_EXPIRACAO != undefined) && (f0.CSA_DATA_EXPIRACAO.value != '') && (f0.CSA_DATA_EXPIRACAO.value != null) &&
	      (!verificaData(f0.CSA_DATA_EXPIRACAO.value))) {
	    f0.CSA_DATA_EXPIRACAO.focus();
	    return false;
	  }
	  if ((f0.CSA_EMAIL != undefined) && f0.CSA_EMAIL.value != null && f0.CSA_EMAIL.value != '' && !isEmailValid(f0.CSA_EMAIL.value)) {
	    alert(mensagem('mensagem.erro.email.csa.invalido'));
	    f0.CSA_EMAIL.focus();
	    return false;
	  }

	  return true;
}

// Verifica formul�rios de inser��o e edi��o de consignantes
function vf_cadastro_cse() {
  var Controles = new Array("CSE_IDENTIFICADOR", "CSE_NOME");
  var Msgs = new Array(mensagem('mensagem.informe.cse.identificador'),
                       mensagem('mensagem.informe.cse.nome'));
  if (!ValidaCampos(Controles, Msgs)) {
    return false;
  }
  if ( (f0.CSE_CNPJ.value != '') && (f0.CSE_CNPJ.value != null) ) {
    return CGC_OK(extraiNumCNPJCPF (f0.CSE_CNPJ.value));
  } else {
    return true;
  }
}

// Verifica formul�rios de inser��o e edi��o de org�os
function vf_cadastro_org() {
  var Controles = new Array("ORG_IDENTIFICADOR", "ORG_NOME","EST_CODIGO");
  var Msgs = new Array(mensagem('mensagem.informe.org.identificador'),
                       mensagem('mensagem.informe.org.nome'),
                       mensagem('mensagem.informe.org.estabelecimento'));
  return ValidaCampos(Controles, Msgs);
}

// Verifica formul�rios de inser��o e edi��o de correspondentes
function vf_cadastro_cor() {
  var Controles = new Array("COR_IDENTIFICADOR", "COR_NOME");
  var Msgs = new Array(mensagem('mensagem.informe.cor.identificador'),
                       mensagem('mensagem.informe.cor.nome'));

  if ((f0.COR_EMAIL.value != '') && (!isEmailValid(f0.COR_EMAIL.value, 3))) {
    alert(mensagem('mensagem.erro.email.cor.invalido'));
    f0.COR_EMAIL.focus();
    return false;
  } else {
    return ValidaCampos(Controles, Msgs);
  }
}

// Verifica formul�rios de inser��o e edi��o de servi�os
function vf_cadastro_svc() {
  var Controles = new Array("SVC_IDENTIFICADOR", "SVC_DESCRICAO", "NSE_CODIGO");
  var Msgs = new Array(mensagem('mensagem.informe.svc.identificador'),
                       mensagem('mensagem.informe.svc.descricao'),
                       mensagem('mensagem.informe.svc.natureza'));
  return ValidaCampos(Controles, Msgs);
}

// Verifica formul�rios de edi��o de registro servidor
function vf_edt_registro_servidor (campoStatus, campoMargem1, campoMargem2, campoMargem3) {

  var Controles = new Array(campoStatus, campoMargem1, campoMargem2, campoMargem3);
  var Msgs = new Array(mensagem('mensagem.informe.rse.status'),
                       mensagem('mensagem.informe.rse.margem'),
                       mensagem('mensagem.informe.rse.margem'),
                       mensagem('mensagem.informe.rse.margem'));

  if(!ValidaCampos(Controles, Msgs)) {
    return false;
  }

  if (f0[campoMargem1] != null) {
    var margem_1 = parseFloat(parse_num(f0[campoMargem1].value));

    if (isNaN(margem_1)) {
      alert(mensagem('mensagem.erro.valor.margem.incorreto'));
      f0[campoMargem1].focus();
      return false;
    }

    if (typeof margem_antiga_1 != "undefined" && margem_antiga_1 != null) {
      if (margem_1 > margem_antiga_1) {
        alert(mensagem('mensagem.erro.valor.margem.maior.atual'));
        f0[campoMargem1].focus();
        return false;
      }
    }
  }

  if (f0[campoMargem2] != null) {
    var margem_2 = parseFloat(parse_num(f0[campoMargem2].value));

    if (isNaN(margem_2)) {
      alert(mensagem('mensagem.erro.valor.margem.incorreto'));
      f0[campoMargem2].focus();
      return false;
    }

    if (typeof margem_antiga_2 != "undefined" && margem_antiga_2 != null) {
      if (margem_2 > margem_antiga_2) {
        alert(mensagem('mensagem.erro.valor.margem.maior.atual'));
        f0[campoMargem2].focus();
        return false;
      }
    }
  }

  if (f0[campoMargem3] != null) {
    var margem_3 = parseFloat(parse_num(f0[campoMargem3].value));

    if (isNaN(margem_3)) {
      alert(mensagem('mensagem.erro.valor.margem.incorreto'));
      f0[campoMargem3].focus();
      return false;
    }

    if (typeof margem_antiga_3 != "undefined" && margem_antiga_3 != null) {
      if (margem_3 > margem_antiga_3) {
        alert(mensagem('mensagem.erro.valor.margem.maior.atual'));
        f0[campoMargem3].focus();
        return false;
      }
    }
  }

  return true;
}

// Verifica formul�rios de inser��o e edi��o de org�os
function vf_cadastro_end() {
  var Controles = new Array("ECH_IDENTIFICADOR", "ECH_DESCRICAO","ECH_QTD_UNIDADES");
  var Msgs = new Array(mensagem('mensagem.informe.ech.identificador'),
                       mensagem('mensagem.informe.ech.descricao'),
                       mensagem('mensagem.informe.ech.qtd.unidade'));
  return ValidaCampos(Controles, Msgs);
}
