<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}%>
<%
String ctx = request.getContextPath();
String action = ctx + "/forgot-password";
Integer stepObj = (Integer) request.getAttribute("step");
int step = (stepObj == null) ? 1 : stepObj;
String email = (String) request.getAttribute("email");
String error = (String) request.getAttribute("error");
String info = (String) request.getAttribute("info");
Integer waitObj = (Integer) request.getAttribute("resendWait");
int wait = (waitObj == null) ? 0 : waitObj;

String title = step == 1 ? "Forgot Password" : (step == 2 ? "Verify Your Email" : "Create New Password");
String backHref = step == 1 ? ctx + "/login" : action + "?restart=1";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=title%> | Swizo</title>
<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--ink: #1f2430;
	--muted: #7a8090;
	--line: #e3e5ea;
	--bg: #fffbf7;
}

* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: "Segoe UI", system-ui, sans-serif;
	background: var(--bg);
	color: var(--ink);
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20px;
}

.card {
	position: relative;
	background: #fff;
	width: 100%;
	max-width: 400px;
	border-radius: 32px;
	padding: 28px 32px 36px;
	box-shadow: 0 14px 40px rgba(31, 36, 48, .09);
	text-align: center;
}

.back {
	position: absolute;
	left: 20px;
	top: 24px;
	width: 34px;
	height: 34px;
	border-radius: 10px;
	background: #fff;
	box-shadow: 0 2px 8px rgba(0, 0, 0, .1);
	display: flex;
	align-items: center;
	justify-content: center;
	color: var(--ink);
	text-decoration: none;
	font-size: 1.1rem;
}

h1 {
	font-size: 1.35rem;
	font-weight: 600;
	margin: 4px 0 28px;
}

.badge {
	width: 150px;
	height: 150px;
	border-radius: 50%;
	background: var(--orange-soft);
	margin: 0 auto 26px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 4.2rem;
}

.lead {
	font-size: .98rem;
	line-height: 1.7;
	margin-bottom: 26px;
	overflow-wrap: anywhere;
}

.banner {
	border-radius: 12px;
	padding: 10px 14px;
	margin-bottom: 18px;
	font-size: .88rem;
	font-weight: 600;
}

.banner.err {
	background: #fde8e8;
	border: 1px solid #f5c6cb;
	color: #c53030;
}

.banner.ok {
	background: #e8f8ee;
	border: 1px solid #bfe8cd;
	color: #168a45;
}

.field {
	text-align: left;
	margin-bottom: 22px;
}

.field label {
	display: block;
	font-size: .8rem;
	color: var(--muted);
	margin-bottom: 2px;
}

.field input {
	width: 100%;
	border: none;
	border-bottom: 1.5px solid var(--line);
	padding: 9px 0;
	font-size: 1rem;
	background: transparent;
	outline: none;
}

.field input:focus {
	border-bottom-color: var(--orange);
}

.codes {
	display: flex;
	justify-content: center;
	gap: 16px;
	margin: 6px 0 26px;
}

.code-box {
	width: 52px;
	height: 60px;
	border: none;
	border-bottom: 3px solid #ffd5a8;
	background: #fff8ef;
	text-align: center;
	font-size: 1.7rem;
	font-weight: 600;
	outline: none;
	border-radius: 6px 6px 0 0;
}

.code-box:focus {
	border-bottom-color: var(--orange);
}

.link {
	display: inline-block;
	margin: 4px 0 22px;
	color: var(--orange-dark);
	font-size: .92rem;
	text-decoration: underline;
	background: none;
	border: none;
	cursor: pointer;
	font-family: inherit;
}

.link:disabled {
	color: var(--muted);
	text-decoration: none;
	cursor: default;
}

.btn {
	display: block;
	width: 100%;
	background: var(--orange);
	color: #fff;
	border: none;
	padding: 14px;
	border-radius: 999px;
	font-weight: 700;
	font-size: 1.05rem;
	cursor: pointer;
	box-shadow: 0 6px 14px rgba(252, 128, 25, .3);
	transition: background .15s;
}

.btn:hover {
	background: var(--orange-dark);
}
</style>
</head>
<body>

	<div class="card">
		<a class="back" href="<%=backHref%>" aria-label="Back">&lsaquo;</a>
		<h1><%=title%></h1>

		<%
		if (error != null && !error.isEmpty()) {
		%>
		<div class="banner err"><%=esc(error)%></div>
		<%
		}
		if (info != null && !info.isEmpty()) {
		%>
		<div class="banner ok"><%=esc(info)%></div>
		<%
		}
		%>

		<%
		if (step == 1) {
		%>
		<!-- CARD 1: email -->
		<div class="badge">&#128274;</div>
		<p class="lead">
			Please enter your email address to<br>receive a verification
			code.
		</p>
		<form method="post" action="<%=action%>">
			<input type="hidden" name="action" value="send">
			<div class="field">
				<label for="email">Email Address</label> <input id="email"
					type="email" name="email" required maxlength="120"
					autocomplete="email" value="<%=esc(email)%>">
			</div>
			<a class="link" href="<%=ctx%>/login">Back to login</a>
			<button type="submit" class="btn">Send</button>
		</form>

		<%
		} else if (step == 2) {
		%>
		<!-- CARD 2: 4-digit code -->
		<div class="badge">&#9993;&#65039;</div>
		<p class="lead">
			Please enter the 4 digit code sent to<br> <strong><%=esc(email)%></strong>
		</p>
		<form id="verifyForm" method="post" action="<%=action%>">
			<input type="hidden" name="action" value="verify"> <input
				type="hidden" name="code" id="codeField">
			<div class="codes">
				<input class="code-box" inputmode="numeric" maxlength="1"
					autocomplete="one-time-code" aria-label="Digit 1"> <input
					class="code-box" inputmode="numeric" maxlength="1"
					aria-label="Digit 2"> <input class="code-box"
					inputmode="numeric" maxlength="1" aria-label="Digit 3"> <input
					class="code-box" inputmode="numeric" maxlength="1"
					aria-label="Digit 4">
			</div>
			<button type="submit" class="btn">Verify</button>
		</form>
		<form method="post" action="<%=action%>" style="margin-top: 18px">
			<input type="hidden" name="action" value="resend">
			<button type="submit" class="link" id="resendBtn">Resend
				Code</button>
		</form>

		<%
		} else {
		%>
		<!-- CARD 3: new password -->
		<div class="badge">&#128272;</div>
		<p class="lead">
			Your new password must be different<br>from your previous
			password.
		</p>
		<form id="resetForm" method="post" action="<%=action%>">
			<input type="hidden" name="action" value="reset">
			<div class="field">
				<label for="pw">New Password</label> <input id="pw" type="password"
					name="password" required minlength="6" maxlength="72"
					autocomplete="new-password">
			</div>
			<div class="field">
				<label for="cf">Confirm Password</label> <input id="cf"
					type="password" name="confirm" required minlength="6"
					maxlength="72" autocomplete="new-password">
			</div>
			<button type="submit" class="btn">Save</button>
		</form>
		<%
		}
		%>
	</div>

	<script>
		// no dollar-brace and no backtick strings in here: a JSP would treat them as server-side EL
		(function() {
			// ---- card 2: four code boxes ----
			var boxes = document.querySelectorAll('.code-box');
			if (boxes.length) {
				var hidden = document.getElementById('codeField');
				var sync = function() {
					var v = '';
					boxes.forEach(function(b) {
						v += b.value;
					});
					hidden.value = v;
				};
				boxes.forEach(function(b, i) {
					b.addEventListener('input', function() {
						b.value = b.value.replace(/\D/g, '').slice(0, 1);
						sync();
						if (b.value && i < boxes.length - 1)
							boxes[i + 1].focus();
					});
					b.addEventListener('keydown', function(e) {
						if (e.key === 'Backspace' && !b.value && i > 0)
							boxes[i - 1].focus();
					});
					b.addEventListener('paste', function(e) {
						var t = (e.clipboardData || window.clipboardData)
								.getData('text').replace(/\D/g, '').slice(0,
										boxes.length);
						if (!t)
							return;
						e.preventDefault();
						for (var k = 0; k < boxes.length; k++)
							boxes[k].value = t.charAt(k) || '';
						sync();
						boxes[Math.min(t.length, boxes.length) - 1].focus();
					});
				});
				boxes[0].focus();
				document.getElementById('verifyForm').addEventListener(
						'submit', function(e) {
							sync();
							if (hidden.value.length !== 4) {
								e.preventDefault();
								alert('Please enter the 4-digit code.');
							}
						});

				// "Resend Code" countdown
				var left =
	<%=wait%>
		;
				var btn = document.getElementById('resendBtn');
				if (btn && left > 0) {
					btn.disabled = true;
					btn.textContent = 'Resend code in ' + left + 's';
					var timer = setInterval(function() {
						left--;
						if (left <= 0) {
							clearInterval(timer);
							btn.disabled = false;
							btn.textContent = 'Resend Code';
						} else {
							btn.textContent = 'Resend code in ' + left + 's';
						}
					}, 1000);
				}
			}

			// ---- card 3: passwords must match ----
			var rf = document.getElementById('resetForm');
			if (rf) {
				rf.addEventListener('submit', function(e) {
					if (document.getElementById('pw').value !== document
							.getElementById('cf').value) {
						e.preventDefault();
						alert('The two passwords do not match.');
					}
				});
			}
		})();
	</script>
</body>
</html>