
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
String ctx = request.getContextPath();

Object orderId = session.getAttribute("lastOrderId");

// Prevent opening this page without a valid order
if (orderId == null) {
	response.sendRedirect(ctx + "/cart");
	return;
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Order Placed - Swizo</title>

<link rel="stylesheet" href="<%=ctx%>/css/common.css">

<style>
* {
	box-sizing: border-box;
}

.success-body {
	min-height: 100vh;
	margin: 0;
	background: linear-gradient(135deg, #fff8f1, #fff0df);
	font-family: "Segoe UI", Arial, sans-serif;
	color: #24212a;
}

.os-wrap {
	min-height: 85vh;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 35px 15px;
}

.os-card {
	width: 100%;
	max-width: 700px;
	min-height: 430px;
	padding: 35px 25px;
	background: white;
	border-radius: 24px;
	text-align: center;
	box-shadow: 0 15px 45px rgba(70, 40, 15, 0.10);
}

/* VIDEO */
.os-video-wrap {
	width: 100%;
	max-width: 560px;
	height: 300px;
	margin: 10px auto 0;
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
	border-radius: 16px;
	background: #fff8f1;
}

.os-video {
	display: block;
	width: 100%;
	height: 100%;
	object-fit: contain;
	border-radius: 16px;
}

/* VIDEO STATUS */
.os-video-message {
	margin-top: 15px;
	color: #fc8019;
	font-size: 14px;
	font-weight: 600;
}

/* SUCCESS RESULT */
.os-result {
	display: none;
	opacity: 0;
	transform: scale(0.92);
	transition: opacity 0.7s ease, transform 0.7s ease;
}

.os-result.show {
	display: block;
}

.os-result.visible {
	opacity: 1;
	transform: scale(1);
}

.os-icon {
	width: 95px;
	height: 95px;
	margin: 15px auto 20px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #e7f8ed;
	color: #20a45b;
	border-radius: 50%;
	font-size: 55px;
	font-weight: bold;
	box-shadow: 0 5px 20px rgba(32, 164, 91, 0.12);
	animation: popIn 0.6s ease;
}

@
keyframes popIn {from { transform:scale(0.3);
	
}

to {
	transform: scale(1);
}

}
.os-result h1 {
	color: #24212a;
	font-size: 28px;
	margin-bottom: 15px;
}

.os-id {
	color: #777582;
	font-size: 17px;
	margin: 12px 0;
}

.os-id b {
	color: #fc8019;
}

.os-msg {
	color: #777582;
	font-size: 15px;
	line-height: 1.6;
}

.os-actions {
	display: flex;
	justify-content: center;
	flex-wrap: wrap;
	gap: 13px;
	margin-top: 25px;
}

.os-btn {
	display: inline-block;
	padding: 13px 23px;
	background: #fc8019;
	color: white;
	text-decoration: none;
	border-radius: 9px;
	font-weight: 700;
	transition: 0.3s;
}

.os-btn:hover {
	background: #e96d0b;
	transform: translateY(-2px);
}

.os-btn-outline {
	background: white;
	color: #fc8019;
	border: 2px solid #fc8019;
}

.os-btn-outline:hover {
	background: #fff3e8;
	color: #e96d0b;
}

@media ( max-width : 480px) {
	.os-card {
		padding: 25px 15px;
	}
	.os-video-wrap {
		height: 230px;
	}
	.os-result h1 {
		font-size: 23px;
	}
	.os-actions {
		flex-direction: column;
	}
	.os-btn {
		width: 100%;
	}
}
</style>

</head>

<body class="success-body">

	<jsp:include page="includes/navbar.jsp" />

	<main class="os-wrap">

		<div class="os-card">

			<!-- STEP 1: PLAY YOUR ACTUAL VIDEO -->

			<div class="os-video-wrap" id="osVideoWrap">

				<video class="os-video" id="osVideo" autoplay muted playsinline
					preload="auto">

					<source src="<%=ctx%>/videos/orderpagesuccesssful.mp4"
						type="video/mp4">

					Your browser does not support HTML video.
				</video>

			</div>

			<p class="os-video-message" id="videoMessage">Your order is being
				prepared...</p>

			<!-- STEP 2: SHOW SUCCESS AFTER VIDEO -->

			<div class="os-result" id="osResult">

				<div class="os-icon">&#10003;</div>

				<h1>Order Placed Successfully!</h1>

				<p class="os-id">
					Your Order ID: <b>#<%=orderId%></b>
				</p>

				<p class="os-msg">Thank you for ordering with Swizo. Your food
					is on its way!</p>

				<div class="os-actions">

					<a class="os-btn" href="<%=ctx%>/restaurant"> Continue Ordering
					</a> <a class="os-btn os-btn-outline" href="<%=ctx%>/orders"> View
						My Orders </a>

				</div>

			</div>

		</div>

	</main>

	<script>

    (function() {

        const video = document.getElementById("osVideo");
        const videoWrap = document.getElementById("osVideoWrap");
        const result = document.getElementById("osResult");
        const message = document.getElementById("videoMessage");

        let completed = false;

        // Show success only after the video finishes
        function showResult() {

            if (completed) {
                return;
            }

            completed = true;

            video.pause();

            videoWrap.style.display = "none";
            message.style.display = "none";

            result.classList.add("show");

            setTimeout(function() {
                result.classList.add("visible");
            }, 50);
        }

        // Video completed successfully
        video.addEventListener("ended", showResult);

        // Video file could not load
        video.addEventListener("error", function() {

            message.textContent =
                "Video could not be loaded. Your order is still confirmed.";

            showResult();

        });

        // Try autoplay
        const playPromise = video.play();

        if (playPromise !== undefined) {

            playPromise.catch(function() {

                // If autoplay is blocked, allow manual playback
                video.controls = true;

                message.textContent =
                    "Tap the play button to watch your order animation.";

            });

        }

    })();

    </script>

</body>
</html>
