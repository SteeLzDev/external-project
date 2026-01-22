jQuery(function($) {

	$('#senha').keyboard({ 
		layout: 'portuguese-qwerty', 
		lockInput: true,
		preventPaste : true
	});


	if($('#senha').length) {
		$('.version').html( '(v' + $('#senha').getkeyboard().version + ')' );
	}

	jQuery.keyboard.layouts['portuguese-qwerty'] = {
		'name' : 'portuguese-qwerty',
		'lang' : ['pt'],
		
		'normal' : [
					"1 2 3 4 5 6 7 8 9 0 \u002d \u003d {bksp}",
					"q w e r t y u i o p \u005b \u005d \u00b4",
					"a s d f g h j k l \u00E7 \u0027 \u005c \u007e {accept}",
					"{shift} z x c v b n m \u002C \u002E \u002f \u005f {cancel}"
		],
		
		'shift' : [
					"\u0021 \u0040 \u0023 \u0024 \u0025 \u00A8 \u0026 \u002A \u0028 \u0029 \u007C \u002B {bksp}",
					"Q W E R T Y U I O P \u007B \u007D \u0060",
					"A S D F G H J K L \u00C7 \u0022 \u003C \u005E {accept}",
					"{shift} Z X C V B N M \u003E \u003B \u003F \u005F {cancel}"
		]
	};	
	
	//Keyboard Language
	//please update this section to match this language and email me with corrections!
	//pt = ISO 639-1 code for Portuguese
	//***********************
	jQuery.keyboard.language.pt = {
		language: 'Portuguese',
		display : {
			'a'      : mensagem("rotulo.botao.teclado.virtual.aceitar"),           // ALTERNATE accept button - unicode for check mark symbol
			'accept' : mensagem("rotulo.botao.teclado.virtual.aceitar"),         // Accept button text
			'alt'    : mensagem("rotulo.botao.teclado.virtual.altgr"),    // Alt button text (AltGr is for international key sets)
			'b'      : mensagem("rotulo.botao.teclado.virtual.retroceder"),                      // ALTERNATE backspace button - unicode for left arrow.
			'bksp'   : mensagem("rotulo.botao.teclado.virtual.apagar"),                 // Backspace button text
			'c'      : mensagem("rotulo.botao.teclado.virtual.cancelar"),           // ALTERNATE cancel button - unicode for big X
			'cancel' : mensagem("rotulo.botao.teclado.virtual.cancelar"),            // Cancel button text
			'clear'  : mensagem("rotulo.botao.teclado.virtual.limpar"),                               // Clear window content (used in num pad)
			's'      : mensagem("rotulo.botao.teclado.virtual.shift"),               // ALTERNATE shift button - unicode for a thick up arrow
			'shift'  : mensagem("rotulo.botao.teclado.virtual.shift"),   // Shift button text
			'space'  : mensagem("rotulo.botao.teclado.virtual.space")                         // Space button text
		},
		wheelMessage : mensagem("rotulo.botao.teclado.virtual.scroll"),

		// Update regex for the combos above
		comboRegex : /([`\'~\^\"ao\u00b4])([a-z])/mig,
		// New combos using specific accents
		combos : {
			"\u00b4" : { a:"\u00e1", A:"\u00c1", e:"\u00e9", E:"\u00c9", i:"\u00ed", I:"\u00cd", o:"\u00f3", O:"\u00d3", u:"\u00fa", U:"\u00da", y:"\u00fd", Y:"\u00dd" }, // acute & cedilla
			// remove apostrophe combo
			"'" : {}
		}

	};
});