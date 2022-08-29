<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Errore</title>
<link rel="stylesheet" type="text/css"  href="css/common.css">
<style>
	#error-message {
		background-color: #FB3640;
		padding: 12px 10px;
		border-radius: 2px;
	}
</style>
</head>
<body>
	<h1>Ops... qualcosa è andato storto</h1>
	<p>Il trasferimento non è stato effettuato.</p>
	<p>Messaggio:</p>
	<div id="error-message"><b><c:out value="${message}" /></b></div>
	<c:url var="url" value="/AccountPage?id=${accountId}"/>
	<p><a class="button" href="${url}">Indietro</a></p>
</body>
</html>