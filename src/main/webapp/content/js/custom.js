jQuery(document).ready(function($){




	$('.navigation').singlePageNav({
        currentClass : 'active'
    });



    $('.flexslider').flexslider({
	    animation: "fade",
	    directionNav: false
	});


 

	$('.menu-toggle-btn').click(function(){
        $('.responsive-menu').stop(true,true).slideToggle();
    });




});
