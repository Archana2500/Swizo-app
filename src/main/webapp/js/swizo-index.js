document.addEventListener("DOMContentLoaded", function() {

    // Hero video: keep it looping
    var heroVideo = document.getElementById("heroVideo");
    if (heroVideo) {
        heroVideo.muted = true;
        heroVideo.loop = true;

        var startVideo = function() {
            var p = heroVideo.play();
            if (p !== undefined) p.catch(function() {});
        };
        startVideo();

        heroVideo.addEventListener("ended", function() {
            heroVideo.currentTime = 0;
            startVideo();
        });
        heroVideo.addEventListener("pause", function() {
            if (!document.hidden) startVideo();
        });
        document.addEventListener("visibilitychange", function() {
            if (!document.hidden) startVideo();
        });
    }

    // Navbar turns solid after scrolling
    var navbar = document.getElementById("navbar");
    if (navbar) {
        var onScroll = function() {
            navbar.classList.toggle("scrolled", window.scrollY > 60);
        };
        onScroll();
        window.addEventListener("scroll", onScroll, { passive: true });
    }

    // Bouncing "Scroll down" arrow
    var scrollDown = document.querySelector(".scroll-down");
    if (scrollDown) {
        setInterval(function() {
            scrollDown.classList.toggle("down");
        }, 800);
    }

    // Reveal sections as they enter the screen
    var revealElements = document.querySelectorAll(".reveal");
    if ("IntersectionObserver" in window) {
        var observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add("visible");
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.12 });

        revealElements.forEach(function(el) { observer.observe(el); });
    } else {
        revealElements.forEach(function(el) { el.classList.add("visible"); });
    }
});