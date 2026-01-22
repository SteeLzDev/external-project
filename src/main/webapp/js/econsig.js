$(function(){
	$.get('../img/sprite.svg', function(data) {
		var div = document.createElement('div');
		div.innerHTML = new XMLSerializer().serializeToString(data.documentElement);
		document.body.insertBefore(div, document.body.childNodes[0]);
	});
	
	$(".ico-menu").click(function(){
		$(this).toggleClass("close-menu");
		$('body').toggleClass("hideScroll");
		$(".nav-bar").toggleClass("open");
	});
	
	$(window).resize(function(){
		if(window.innerWidth > 768) {
			$(".nav-bar nav").show();
		}
	});
			
	$('[data-bs-toggle="tooltip"]').tooltip();
	$('[data-bs-toggle="popover"]').popover();
	
	$('.question-head').click(function() {
		if($(this).next('.collapse').hasClass('show')) {
			$(this).removeClass('open');
		} else {
			$(this).addClass('open');
		}
	});
	
	$('.scroll-pane').jScrollPane({
		autoReinitialise: true,
		verticalGutter: 0
	});
	
// Hide Header on on scroll down
var didScroll;
var lastScrollTop = 0;
var delta = 5;
var navbarHeight = $('.header-logo').outerHeight();

$(window).scroll(function(event){
	didScroll = true;
});

setInterval(function() {
	if (didScroll) {
		hasScrolled();
		didScroll = false;
	}
}, 250);

function hasScrolled() {
	var st = $(this).scrollTop();
    
	// Make sure they scroll more than delta
	if(Math.abs(lastScrollTop - st) <= delta)
		return;
    
	// If they scrolled down and are past the navbar, add class .nav-up.
	// This is necessary so you never see what is "behind" the navbar.
	if (st > lastScrollTop && st > navbarHeight && window.innerWidth < 767 ){
		// Scroll Down
		$('.header-logo').addClass('nav-up');
		$('.ico-menu').removeClass("close-menu");
	} else {
		// Scroll Up
		if(st + $(window).height() < $(document).height()) {
			$('.header-logo').removeClass('nav-up');
		}
	}
	lastScrollTop = st;
}

});
