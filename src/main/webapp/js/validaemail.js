// Testa email
//   1. Livre = ReEmail1 aceita nome-local com todos os caracteres permitidos na RFC 2822:
//      [\w!#$%&'*+/=?^`{|}~-]; e o domínio tem definição bem livre, por nome basicamente fixando
//      apenas que o TLD deve ter entre 2 e 6 caracteres: [A-Za-z]{2,6}; ou por número IP entre
//      colchetes: \[\d{1,3}(\.\d{1,3}){3}\].
//   2. Compacto = ReEmail2 limita os caracteres permitidos no nome-local de forma mais compacta
//      e restritiva, porém cobre os casos mais comuns. Aceita como nome-local uma ou mais palavras
//      separadas por ponto ([\w-]+(\.[\w-]+)*), onde cada palavra é definida por [\w-]+ permitindo
//      assim letra, dígito, sublinhado e hífen. Também limita o tamanho de nomes de domínio entre
//      2 e 63 caracteres apenas com letras, dígitos, sublinhado e hífen: [\w-]{2,63}.
//   3. Restrito = ReEmail3 é uma variação da ReEmail2, mas força nomes de domínio entre 2 e 63
//      caracteres, deixa de usar a seqüência \w para não permitir o sublinhado e garante que não
//      há hífen nem na primeira nem na última posição, conforme RFC 1034/1035. O resultado é o
//      seguinte para representar um nome de domínio: [A-Za-z\d][A-Za-z\d-]{0,61}[A-Za-z\d].
//
var reEmail1 = /^[\w!#$%&'*+\/=?^`{|}~-]+(\.[\w!#$%&'*+\/=?^`{|}~-]+)*@(([\w-]+\.)+[A-Za-z]{2,}|\[\d{1,3}(\.\d{1,3}){3}\])$/;
var reEmail2 = /^[\w-]+(\.[\w-]+)*@(([\w-]{2,63}\.)+[A-Za-z]{2,}|\[\d{1,3}(\.\d{1,3}){3}\])$/;
var reEmail3 = /^[\w-]+(\.[\w-]+)*@(([A-Za-z\d][A-Za-z\d-]{0,61}[A-Za-z\d]\.)+[A-Za-z]{2,}|\[\d{1,3}(\.\d{1,3}){3}\])$/;
var reEmail = reEmail1;
function isEmailValid(pStr, pFmt) {
  if(pFmt) {
    eval("reEmail = reEmail" + pFmt);
  }
  if (pStr == null || pStr == "") {
    return true;
  }
  valid = true;
  pStr = pStr.split(',');
  for (i = 0; i < pStr.length; i++) {
    valid = valid && reEmail.test(pStr[i]);
  }
  return valid;
} // testEmail
