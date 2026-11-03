<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}%>
<%
String flashOk = (String) session.getAttribute("adminFlash");
String flashErr = (String) session.getAttribute("adminErr");
session.removeAttribute("adminFlash");
session.removeAttribute("adminErr");

if (flashOk != null) {
%>
<div class="banner ok"><%=esc(flashOk)%></div>
<%
}
if (flashErr != null) {
%>
<div class="banner err"><%=esc(flashErr)%></div>
<%
}
%>