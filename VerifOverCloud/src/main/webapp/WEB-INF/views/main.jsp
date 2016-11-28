<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<!--  javascript Jquery-->
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery/jquery.ui.datepicker-ko.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery/jquery-ui.min-1.11.4.custom.js"></script>
<!-- StyleSheet -->
<link rel="stylesheet" type="text/css" media="all"
	href="${pageContext.request.contextPath}/resources/css/style.css" />
<link rel="stylesheet" type="text/css" media="all"
	href="${pageContext.request.contextPath}/resources/css/jquery-ui/jquery.selectBoxIt.css" />
<link rel="stylesheet" type="text/css" media="all"
	href="${pageContext.request.contextPath}/resources/css/jquery-ui/jquery-ui.min-1.11.4.custom.css">

<script type="text/javascript">
	$(document).ready(function() {
		//$("#vt_title").html("명령");
		$("#targetService").val("system");
		if ($("#systemPower").val() == "on") {
			$("#systemPowerOn").addClass("disabled");
		} else {
			$("#systemPowerOff").addClass("disabled");
		}
		if ($("#servicePower").val() == "on") {
			$("#servicePowerOn").addClass("disabled");
		} else {
			$("#servicePowerOff").addClass("disabled");
		}
		if ($("#webPower").val() == "on") {
			$("#webPowerOn").addClass("disabled");
		} else {
			$("#webPowerOff").addClass("disabled");
		}

		$("#system_tbody").show();
		$("#systemPowerGroup").show();

		$("#service_tbody").hide();
		$("#servicePowerGroup").hide();

		$("#web_tbody").hide();
		$("#webPowerGroup").hide();

	});

	$(function() {
		$('.form_datepicker').datepicker({
			changeMonth : true, //월 셀렉트 박스 유무
			changeYear : true, //년 셀렉트 박스 유무
			showButtonPanel : true, //달력아래 닫기 버튼 오늘가기 버튼 출력
			dateFormat : "yy-mm-dd", //날짜 출력 형식
			showMonthAfterYear : true
		//년/월 으로 할건지 월/년 으로 할건지
		});
	});

	function getToday() {
		var today = new Date();
		var yy = today.getFullYear();
		var mm = today.getMonth() + 1;
		var dd = today.getDate();
		if (mm < 10)
			mm = "0" + mm;
		if (dd < 10)
			dd = "0" + dd;

		var curDate = "";
		curDate += yy + "-" + mm + "-" + dd;
		return curDate;
	}

	var todate = getToday();
	// 입력폼 정규식 검사
	function chk(re, obj, msg) {
		if (re.test(obj.val())) {
			return true;
		}
		alert(msg);
		obj.val("");
		obj.focus();
		return false;
	}

	$(function() {

		$('.period_valid').on('change', function() {
			chk(/[0-9]$/, $(this), "양식에 맞지 않습니다.");
		});

		$('.hour_valid').on('change',function() {
			$(this).val($(this).val().trim());
			var pre = /^[0-9]$/;
			if (pre.test($(this).val())) {
				$(this).val("0" + $(this).val());
			}
			chk(/^0[0-9]$|^1[0-9]$|^2[0-3]$/, $(this),"00~23 범위 사이로 입력 가능합니다.");
		});

		$('.min_valid').on('change', function() {
			$(this).val($(this).val().trim());
			var pre = /^[0-9]$/;
			if (pre.test($(this).val())) {
				$(this).val("0" + $(this).val());
			}
			chk(/^[0-5][0-9]$/, $(this), "00~59 범위 사이로 입력 가능합니다.");
		});

		$('#save').on('click',function() {
			//폼 validation 체크
			var svc = $("#targetService").val();
			
			if (!chk(/^\d+$/, $("#"+svc+"Period"),"숫자만 입력 가능합니다.")) {
				return false;
			}
			if (!chk(/^[0-9.a-zA-Z]+$/, $("#"+svc+"TargetIP"),"입력값이 없습니다.")) {
				return false;
			}
			if(svc=="service"){
				if (!chk(/^[0-9,]+$/, $("#serviceTargetPort"),"Port를 입력하세요.")) {
					return false;
				}
			}
			
			//폼 validation 체크 끝
			var data = $("#pageFrm").serialize();
			$.ajax({
				'url' : "<c:url value='/operation/saveSchedules.json' />",
				'data' : data,
				'dataType' : 'json',
				'type' : 'POST',
				'async' : false,
				'success' : function(data) {
					if (data.result == true) {
						alert('정상적으로 등록을 하였습니다.');
						location.href="<c:url value='main.do' />";
					} else {
						alert('등록을 실패하였습니다. 관리자에게 문의바랍니다.');
					}
				},
				'error' : function(request, status,error) {
					alert("정보 생성 실패:오류 " + "code:" + request.status + "\n" + "error:" + error);
				}
			});
		});

		$('#reset').on('click', function() {
			location.href = "<c:url value='main.do' />";
		});

		$('.btn_calender').on('click', function(e) {
			target = $(this).data('target');

			//alert(target + $('#' + target).val());
			$('#' + target).datepicker('show');
		});

		$('.tabmenu li button').on('click', function() {

			$(this).parent().addClass('selected');
			$(this).parent().siblings().removeClass('selected');

			var menu = $('.tabmenu li.selected button').val();

			// 설정 update 대상 지정
			$("#targetService").val(menu);

			if (menu == "system") {
				$("#system_tbody").show();
				$("#service_tbody").hide();
				$("#web_tbody").hide();

				$("#systemPowerGroup").show();
				$("#servicePowerGroup").hide();
				$("#webPowerGroup").hide();
			} else if (menu == "service") {
				$("#system_tbody").hide();
				$("#service_tbody").show();
				$("#web_tbody").hide();

				$("#systemPowerGroup").hide();
				$("#servicePowerGroup").show();
				$("#webPowerGroup").hide();
			} else { //menu == web
				$("#system_tbody").hide();
				$("#service_tbody").hide();
				$("#web_tbody").show();

				$("#systemPowerGroup").hide();
				$("#servicePowerGroup").hide();
				$("#webPowerGroup").show();
			}

		});

	});

	function doAction(targetService, targetAction) {
		var beforeAction = $("#" + targetService + "Power").val();

		if (beforeAction == targetAction) {
			alert("이미 " + targetAction + "상태 입니다.");
			return;
		}

		$("#targetService").val(targetService);
		$("#targetAction").val(targetAction);

		var data = $("#pageFrm").serialize();

		$.ajax({
			'url' : '<c:url value="/operation/jobsAjaxAction.json"/>',
			'data' : data,
			'dataType' : 'json',
			'type' : 'POST',
			'success' : function(data) {
				if (data.result == true) {
					$("#" + targetService + "Power").val(targetAction);

					if ($("#" + targetService + "Power").val() == "on") {
						$("#" + targetService + "PowerOn").addClass(
								"disabled");
						$("#" + targetService + "PowerOff")
								.removeClass("disabled");
					} else {
						$("#" + targetService + "PowerOff").addClass(
								"disabled");
						$("#" + targetService + "PowerOn").removeClass(
								"disabled");
					}

				} else {
					alert('On/Off mode change fail.');
				}
			},
			'error' : function(request, status, error) {
				alert(request.responseText);
			}
		});
	}
</script>
<title>클라우드를 통해 생성되는 3-Tier 소프트웨어의 가용성 검증 도구</title>
</head>
<body>
	<br />
	<br />
	<form:form commandName="param" name="pageFrm" id="pageFrm"
		method="post">

		<form:input type="hidden" id="systemPower" path="systemPower" />
		<form:input type="hidden" id="servicePower" path="servicePower" />
		<form:input type="hidden" id="webPower" path="webPower" />
		<form:input type="hidden" id="targetAction" path="targetAction" />
		<form:input type="hidden" id="targetService" path="targetService" />

		<div class="contents">
			<h1>클라우드를 통해 생성되는 3-Tier 소프트웨어의 가용성 검증 도구</h1>
			<br />
			<br />
			<div class="tab_wrap">
				<ul class="tabmenu clearfix">
					<li class="selected">
						<button type="button" value="system">
							<span>시스템 검증</span>
						</button>
					</li>
					<li>
						<button type="button" value="service">
							<span>서비스 검증</span>
						</button>
					</li>
					<li>
						<button type="button" value="web">
							<span>웹사이트 검증</span>
						</button>
					</li>
				</ul>
			</div>
			<div class="panel tab_content">
				<div class="panel_title clearfix">
					<h4 id="vt_title">시스템 검증</h4>
					<p id="systemPowerGroup" class='btns_action clearfix'>
						<a data-remote="true" href="javascript:doAction('system','on');">
							<button id="systemPowerOn" class='b_start valign_mid'
								type='button'>
								<span>시작</span>
							</button>
						</a> <a data-remote="true" href="javascript:doAction('system','off')">
							<button id="systemPowerOff" class='b_stop valign_mid'
								type='button'>
								<span>중지</span>
							</button>
						</a>
					</p>

					<p id="servicePowerGroup" class='btns_action clearfix'>
						<a data-remote="true" href="javascript:doAction('service','on')">
							<button id="servicePowerOn" class='b_start valign_mid'
								type='button'>
								<span>시작</span>
							</button>
						</a> <a data-remote="true" href="javascript:doAction('service','off')">
							<button id="servicePowerOff" class='b_stop valign_mid'
								type='button'>
								<span>중지</span>
							</button>
						</a>
					</p>

					<p id="webPowerGroup" class='btns_action clearfix'>
						<a data-remote="true" href="javascript:doAction('web','on')">
							<button id="webPowerOn" class='b_start valign_mid' type='button'>
								<span>시작</span>
							</button>
						</a> <a data-remote="true" href="javascript:doAction('web','off')">
							<button id="webPowerOff" class='b_stop valign_mid' type='button'>
								<span>중지</span>
							</button>
						</a>
					</p>

				</div>

				<div class="tab_content">
					<div class="panel_content">
						<table class="verif_table">
							<colgroup>
								<col width="200"></col>
								<col width="600"></col>
							</colgroup>
							<tbody id="system_tbody">
								<tr>
									<th style="padding-left: 10px">대상 IP(or Domain)</th>
									<td><form:input type="text" id="systemTargetIP"
											name="systemTargetIP" path="systemTargetIP" /></td>
								</tr>
								<tr>
									<th style="padding-left: 10px">반복주기(단위:Minute)</th>
									<td><form:input type="text" id="systemPeriod" path="systemPeriod" class="date_input" name="systemPeriod" placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">시작일</th>
									<td><form:input type="text" id="systemStartDate"
											path="systemStartDate" name="systemStartDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="systemStartDate"
												type="button"></button>
									</span> <form:input type="text" id="systemStartHour"
											path="systemStartHour" name="systemStartHour"
											class="date_input hour_valid" placeholder="00" />시 <form:input
											type="text" id="systemStartMin" path="systemStartMin"
											name="systemStartMin" class="date_input min_valid"
											placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">종료일</th>
									<td><form:input type="text" id="systemEndDate"
											path="systemEndDate" name="systemEndDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="systemEndDate"
												type="button"></button>
									</span> <form:input type="text" id="systemEndHour"
											path="systemEndHour" name="systemEndHour"
											class="date_input hour_valid" placeholder="00" />시 <form:input
											type="text" id="systemEndMin" path="systemEndMin"
											name="systemEndMin" class="date_input min_valid"
											placeholder="00" />분</td>
								</tr>
							</tbody>

							<tbody id="service_tbody">
								<tr>
									<th style="padding-left: 10px">대상 IP(or Domain)</th>
									<td><form:input type="text" id="serviceTargetIP"
											path="serviceTargetIP" name="serviceTargetIP" /></td>
								</tr>
								<tr>
									<th style="padding-left: 10px">대상 Port</th>
									<td><form:input type="text" id="serviceTargetPort"
											path="serviceTargetPort" name="serviceTargetPort"
											placeholder="ex) 80,8080,5050" /></td>
								</tr>
								<tr>
									<th style="padding-left: 10px">반복주기(단위:Minute)</th>
									<td><form:input type="text" class="date_input"
											id="servicePeriod" path="servicePeriod" name="servicePeriod"
											placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">시작일</th>
									<td><form:input type="text" id="serviceStartDate"
											path="serviceStartDate" name="serviceStartDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="serviceStartDate"
												type="button"></button>
									</span> <form:input type="text" id="serviceStartHour"
											path="serviceStartHour" name="serviceStartHour"
											class="date_input hour_valid" placeholder="00" />시 <form:input
											type="text" id="serviceStartMin" path="serviceStartMin"
											name="serviceStartMin" class="date_input min_valid"
											placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">종료일</th>
									<td><form:input type="text" id="serviceEndDate"
											path="serviceEndDate" name="serviceEndDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="serviceEndDate"
												type="button"></button>
									</span> <form:input type="text" id="serviceEndHour"
											path="serviceEndHour" name="serviceEndHour"
											class="date_input hour_valid" placeholder="00" />시 <form:input
											type="text" id="serviceEndMin" path="serviceEndMin"
											name="serviceEndMin" class="date_input min_valid"
											placeholder="00" />분</td>
								</tr>
							</tbody>

							<tbody id="web_tbody">
								<tr>
									<th style="padding-left: 10px">대상 IP(or Domain)</th>
									<td><form:input type="text" id="webTargetIP"
											path="webTargetIP" name="webTargetIP" /></td>
								</tr>
								<tr>
									<th style="padding-left: 10px">반복주기(단위:Minute)</th>
									<td><form:input type="text" class="date_input"
											id="webPeriod" path="webPeriod" name="webPeriod"
											placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">시작일</th>
									<td><form:input type="text" id="webStartDate"
											path="webStartDate" name="webStartDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="webStartDate"
												type="button"></button>
									</span> <form:input type="text" id="webStartHour" path="webStartHour"
											name="webStartHour" class="date_input hour_valid"
											placeholder="00" />시 <form:input type="text" id="webStartMin"
											path="webStartMin" name="webStartMin"
											class="date_input min_valid" placeholder="00" />분</td>
								</tr>
								<tr>
									<th style="padding-left: 10px">종료일</th>
									<td><form:input type="text" id="webEndDate"
											path="webEndDate" name="webEndDate"
											class="date_input form_datepicker" readonly="true" /> <span
										class="select_group">
											<button class="btn_calender" data-target="webEndDate"
												type="button"></button>
									</span> <form:input type="text" id="webEndHour" path="webEndHour"
											name="webEndHour" class="date_input hour_valid"
											placeholder="00" />시 <form:input type="text" id="webEndMin"
											path="webEndMin" name="webEndMin"
											class="date_input min_valid" placeholder="00" />분</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class='search_group panel_buttons'>
				<button id="save" class='btn_square'>
					<span>저장</span>
				</button>
				<button id="reset" class='btn_square'>
					<span>새로고침</span>
				</button>
			</div>
		</div>
	</form:form>
</body>
</html>