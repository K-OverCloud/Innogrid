$(function(){	
	$('nav > ul > li ul').each(function(){
		$(this).css({marginLeft: -( $(this).width() /2)});
	});

	$('nav > ul > li').hover (
		function(){
			$(this).children('ul').show();
			$(this).children('ul').stop().animate({
				opacity: 1,
				top: 50
			}, 300, 'easeOutExpo');
		},
		function(){
			$(this).children('ul').stop().animate({
				opacity: 0,
				top: 80
			}, 300, 'easeOutExpo',function(){
				$(this).hide();
			});
		}
		
	);
});

//by ken 20140926 
//delete popup
//POPUP
$(function(){
	$('.btn_pop_close').each(function(){
		$(this).on('click', function(){
			$(this).parent().parent().hide();
		});
	});
	$('.popup_buttons .btn_close').each(function(){
		$(this).on('click', function(){
			$(this).parent().parent().parent().parent().hide();
		});
	});	
});


function doPopup(actionType) 
{
	id = $('#id').val();
	
	if(actionType == 'd'){
		var a = "javascript:doAction("+id+",'d')";
		$('#table_del_url').attr('href', a);
		$('#popup_del').show();
	}
}


$('.btn_calender').on('click', function(e) {
    target = $(this).data('target');

    //alert(target + $('#' + target).val());
    $('#' + target).datepicker('show');
});
