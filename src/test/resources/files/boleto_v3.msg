<TABLE WIDTH="82%" BORDER="0" ALIGN="CENTER" CELLPADDING="0" CELLSPACING="0">
<TR>
  <TD><TABLE WIDTH="100%" BORDER="1" CELLPADDING="0" CELLSPACING="0" >
      <TR>
        <TD colspan="5" ALIGN="CENTER"><FONT SIZE="4"><I>Autoriza&ccedil;&atilde;o de Desconto</I></FONT></TD>
      </TR>
      <TR>
        <TD colspan="5" class="TituloColuna"><I>I - Dados Pessoais</I></TD>
      </TR>
      <TR>
        <TD colspan="4" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Nome</span><BR>
          <@ser_nome>
          &nbsp;</TD>
        <TD width="18%" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">CPF</span><BR>
          <@ser_cpf>
          &nbsp;</TD>
        <!--
        <TD width="18%" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Sexo</span><BR>
          <@ser_sexo>
          &nbsp;</TD>
          -->
      </TR>
      <TR>
        <TD valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Data
          de Nascimento</span><br>
          <@ser_data_nasc>
          &nbsp;</TD>
        <TD valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Estado
          Civil</span><br>
          <@ser_est_civil>
          &nbsp;</TD>
        <TD colspan="3" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Doc.
          de Identidade - UF - &Oacute;rg. Emissor - Dt. Emiss&atilde;o</span><br>
          <@ser_nro_idt>
          &nbsp; </TD>
      </TR>
      <!--<TR>
            <TD colspan="2" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Nome
              do Pai</span><BR>
              <@ser_nome_pai>
              &nbsp;</TD>
            <TD colspan="3" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Nome
              da M&atilde;e</span><BR>
              <@ser_nome_mae>
              &nbsp;</TD>
          </TR> -->
      <TR>
        <TD colspan="2" CLASS="FonteReduzida"><span class="RotuloReduzido">Rua,
          Av., Pra&ccedil;a</span><BR>
          <@ser_end>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">N&uacute;mero</span><BR>
          <@ser_nro>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Complemento</span><BR>
          <@ser_compl>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Bairro</span><BR>
          <@ser_bairro>
          &nbsp;</TD>
      </TR>
      <!--
      <TR>
        <TD valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Cidade</span><BR>
          <@ser_cidade>
          &nbsp;</TD>
        <TD valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">UF</span><BR>
          <@ser_uf>
          &nbsp;</TD>
        <TD valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">CEP</span><BR>
          <@ser_cep>
          &nbsp;</TD>
        <TD colspan="2" valign="top" CLASS="FonteReduzida"><span class="RotuloReduzido">Telefone</span><BR>
          <@ser_tel>
          &nbsp; </TD>
      </TR> -->
      <TR>
        <TD colspan="5" class="TituloColuna"><I>II - Dados Funcionais</I></TD>
      </TR>
      <TR valign="top">
        <TD width="26%" CLASS="FonteReduzida"><span class="RotuloReduzido">Prec-CP</span><br>
          <@rse_matricula>
          &nbsp;</TD>
        <TD width="17%" CLASS="FonteReduzida"><span class="RotuloReduzido">Categoria</span><br>
          <@rse_tipo>
          &nbsp;</TD>
        <TD width="21%" CLASS="FonteReduzida"><span class="RotuloReduzido">Data
          de Admiss&atilde;o</span><br>
          <@rse_data_admissao>
          &nbsp;</TD>
        <TD colspan="2" CLASS="FonteReduzida"><span class="RotuloReduzido">&Oacute;rg&atilde;o</span><br>
          <@org_identificador>
          -
          <@org_nome>
          &nbsp;</TD>
      </TR>
      <!--
      <TR valign="top">
        <TD colspan="2" CLASS="FonteReduzida"><span class="RotuloReduzido">Endereço
          Comercial</span><br>
          <@org_logradouro>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">N&uacute;mero</span><br>
          <@org_nro>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Complemento</span><br>
          <@org_compl>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Bairro</span><br>
          <@org_bairro>
          &nbsp;</TD>
      </TR>
      <TR valign="top">
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Cidade</span><br>
          <@org_cidade>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">UF</span><BR>
          <@org_uf>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">CEP</span><br>
          <@org_cep>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Telefone/Ramal</span><br>
          <@org_tel>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">End. Corresp.</span><br>
          <@org_uf></TD>
      </TR>
       -->
      <TR>
        <TD colspan="5" class="TituloColuna"><I>III - Caracter&iacute;sticas
          da Opera&ccedil;&atilde;o</I></TD>
      </TR>
      <TR valign="top">
        <TD colspan="3" CLASS="FonteReduzida"><span class="RotuloReduzido">Consignat&aacute;ria</span><BR>
          <@csa_nome>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Respons&aacute;vel</span><br>
          <@usu_login>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Ranking</span><br>
          <@cde_ranking>
          &ordf;</TD>
      </TR>
      <TR valign="top">
        <TD colspan="2" CLASS="FonteReduzida"><span class="RotuloReduzido">Natureza da
          Opera&ccedil;&atilde;o</span><br>
          <@svc_identificador>
          -
          <@svc_descricao>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Data da Opera&ccedil;&atilde;o</span><br>
          <@ade_data>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">CET Mensal real</span><br>
          <@cft_vlr_real>
          a.m.&nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">CET Anual real</span><br>
          <@cet_anual_real>
          a.a.&nbsp; </TD>
      </TR>
      <TR valign="top">
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">N&ordm; ADE</span><br>
          <@ade_numero>
          &nbsp;</TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Valor da Presta&ccedil;&atilde;o</span><br>
          R$
          <@ade_vlr>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">N&ordm; Presta&ccedil;&otilde;es</span><br>
          <@ade_prazo>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Data Inicial</span><br>
          <@ade_ano_mes_ini>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Data Final Prevista</span><br>
          <@ade_ano_mes_fim>
          &nbsp; </TD>
      </TR>
      <TR valign="top">
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">C&oacute;digo
          Desconto</span><br>
          <@cnv_cod_verba>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Valor Liberado</span><br>
          R$
          <@ade_vlr_liquido>
          &nbsp; </TD>
        <TD CLASS="FonteReduzida"><span class="RotuloReduzido">Total dos encargos (CET)</span><br>
          R$
          <@ade_vlr_total_encargos>
          &nbsp; </TD>
        <TD colspan="2" CLASS="FonteReduzida"><span class="RotuloReduzido">Valor Total
          a ser Pago</span><br>
          R$
          <@ade_vlr_total_pago>
          &nbsp; </TD>
      </TR>
    </TABLE></TD>
</TR>
<TR>
  <TD><p align="justify"><br>
    <P><B>FORMA DE CONTATO</B>:
      <@csa_txt_contato>
    </P>
    <span class="FonteReduzida"><b>AUTORIZA&Ccedil;&Atilde;O DE DESCONTO</b>: Esta  Autoriza&ccedil;&atilde;o de Desconto constitui parte integrante do</span><strong> </strong><b>CONTRATO DE
    <@svc_descricao>
    </b>. A assinatura do <b>MILITAR</b> acarreta a imediata
    assun&ccedil;&atilde;o dos direitos e obriga&ccedil;&otilde;es estipulados
    no referido instrumento e constitui comprova&ccedil;&atilde;o, neste ato,
    do recebimento de uma c&oacute;pia do citado contrato. O <b>MILITAR</b> reconhece que o cancelamento dos descontos n&atilde;o poder&aacute; ocorrer
    antes de liquidado o seu d&eacute;bito supra.
    </p>
    <p align="justify"><b>AUTORIZA&Ccedil;&Atilde;O</b>:
      Autorizo expressamente o <b>
      <@csa_nome>
      </b>, em car&aacute;ter irrevog&aacute;vel
      e irretrat&aacute;vel, a fornecer &agrave; Central de Risco do Banco Central
      do Brasil, informa&ccedil;&otilde;es sobre o montante dos d&eacute;bitos
      e responsabilidade por garantias de minha titularidade, por for&ccedil;a
      das normas aplicav&eacute;is, bem como a acessar as referidas informa&ccedil;&otilde;es
      junto &agrave;quela Central, prestadas por outras institui&ccedil;&otilde;es
      financeiras. <br>
      <br>
      <b>DECLARA&Ccedil;&Atilde;O</b>: Declaro que o valor da presta&ccedil;&atilde;o
      a ser averbado, constante no Quadro III supra, est&aacute; de conformidade
      com o pactuado, compreendendo os encargos ali previstos, e que n&atilde;o
      me fora exigido qualquer outro encargo e/ou aquisi&ccedil;&atilde;o de
      outro(s) produto(s).<br>
      <br>
      Fica o <b> <@est_nome> </b> autorizado a consignar as parcelas acordadas em minha folha
      de pagamento. <br>
      <br>
    </p></TD>
</TR>
<TR>
  <TD><TABLE WIDTH="100%" BORDER="0">
      <TR>
        <TD colspan="2" VALIGN="TOP" CLASS="FonteReduzida">_________________________,
          _____ de ________________ de 20____</TD>
      </TR>
      <TR>
        <TD colspan="2" height="30" VALIGN="TOP" CLASS="FonteReduzida">&nbsp;</TD>
      </TR>
      <TR>
        <TD align="center" VALIGN="TOP" CLASS="FonteReduzida"><p>_______________________________________________________<BR>
            MILITAR</p></TD>
        <TD align="center" valign="top" CLASS="FonteReduzida"><p>__________________________________________________<BR>
            CONSIGNAT&Aacute;RIA</p></TD>
      </TR>
    </TABLE></TD>
</TR>
<TR>
  <TD><HR>
  </TD>
</TR>
<TR>
  <TD CLASS="FonteReduzida"><TABLE WIDTH="100%" BORDER="0" cellpadding="0" cellspacing="0">
      <TR>
        <TD CLASS="FonteReduzida" WIDTH="40%">&nbsp;</TD>
        <TD WIDTH="20%" align="center" CLASS="FonteReduzida">&nbsp;</TD>
        <TD CLASS="FonteReduzida" WIDTH="40%" ALIGN="RIGHT"><@usu_login>
          -
          <@agora></TD>
      </TR>
    </TABLE></TD>
</TR>
<BR>
